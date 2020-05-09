package com.home.commonBase.control;

import com.home.commonBase.data.system.PlayerWorkListData;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.table.table.GlobalTable;
import com.home.commonBase.table.table.PlayerTable;
import com.home.shine.ShineSetup;
import com.home.shine.bytes.BytesReadStream;
import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.control.DBControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.data.BaseData;
import com.home.shine.data.DateData;
import com.home.shine.global.ShineSetting;
import com.home.shine.serverConfig.ServerConfig;
import com.home.shine.support.collection.SList;
import com.home.shine.table.BaseTable;
import com.home.shine.table.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;

/** 数据库升级控制 */
public class DBUpdateControl
{
	/** 读流 */
	private BytesReadStream _rStream=BytesReadStream.create();
	/** 写流 */
	private BytesWriteStream _wStream=BytesWriteStream.create();
	
	/** 上次db版本号 */
	protected int getLastDataVersion()
	{
		return 0;
	}
	
	/** 当前db版本号 */
	protected int getNowDataVersion()
	{
		return 0;
	}
	
	/** 更新某根数据 */
	protected void updateDataOne(BaseData data)
	{
	
	}
	
	/** 执行单个数据 */
	protected byte[] doDataOne(BaseData data,byte[] bytes)
	{
		_rStream.clear();
		_rStream.setBuf(bytes);
		data.readBytesFull(_rStream);
		
		updateDataOne(data);
		
		_wStream.clear();
		data.writeBytesFull(_wStream);
		return _wStream.getByteArray();
	}
	
	/** 执行角色组 */
	protected void doPlayers(DBConnect connect,BaseData listData,BaseData loginData,BaseData offlineListData)
	{
		PlayerTable table=BaseC.factory.createPlayerTable();
		
		SList<BaseTable> list=table.loadAllSync(connect);
		
		//没有角色
		if(list.isEmpty())
			return;
		
		Connection con=connect.getMainConnection();
		
		try
		{
			PreparedStatement updatePs=con.prepareStatement(table.getUpdateStr());
			
			BaseTable[] values=list.getValues();
			PlayerTable v;
			
			for(int i=0,len=list.size();i<len;++i)
			{
				v=(PlayerTable)values[i];
				
				v.data=doDataOne(listData,v.data);
				v.loginData=doDataOne(loginData,v.loginData);
				v.offlineData=doDataOne(offlineListData,v.offlineData);
				
				v.updateToPs(updatePs,true);
				updatePs.addBatch();
			}
			
			updatePs.executeBatch();
			DBControl.closeStatement(updatePs);
			con.commit();
			connect.reMainConnection(con);
		}
		catch(Exception e)
		{
			Ctrl.errorLog(e);
			ShineSetup.exit("执行Player出错");
		}
	}
	
	protected void doGlobal(DBConnect connect,GlobalTable globalTable,BaseData listData,String mark)
	{
		boolean re=globalTable.loadSync(connect);
		
		if(!re)
		{
			Ctrl.print("找不到global表:"+mark+" 跳过");
			return;
		}
		
		int lastDataVersion=getLastDataVersion();
		
		if(globalTable.dataVersion!=lastDataVersion)
		{
			ShineSetup.exit("update数据库时,结构版本不匹配,last:" + globalTable.dataVersion + ",期望:" + lastDataVersion+" 是否已执行过？");
			return;
		}
		
		globalTable.data=doDataOne(listData,globalTable.data);
		
		globalTable.dataVersion=getNowDataVersion();
		globalTable.saveDate=DateData.getNow();
		
		//更新回库
		globalTable.updateSync(connect);
	}
	
	/** 执行升级 */
	public void execute()
	{
		//标记
		ShineSetting.isDBUpdate=true;
		
		DBConnect centerConnect=new DBConnect(ServerConfig.getCenterConfig().mysql);
		doGlobal(centerConnect,BaseC.factory.createCenterGlobalTable(),BaseC.factory.createCenterGlobalListData(),"center");
		
		ServerConfig.getGameConfigDic().forEachValue(gConfig->
		{
			DBConnect gameConnect=null;
			
			try
			{
				gameConnect=new DBConnect(gConfig.mysql);
			}
			catch(Exception e)
			{
				Ctrl.print("game"+gConfig.id+"的mysql:"+gConfig.mysql+"无法连接，已跳过");
				return;
			}
			
			doGlobal(gameConnect,BaseC.factory.createGameGlobalTable(),BaseC.factory.createGameGlobalListData(),"game"+gConfig.id);
			doPlayers(gameConnect,BaseC.factory.createPlayerListData(),BaseC.factory.createPlayerLoginData(),new PlayerWorkListData());
		});
		
		Ctrl.print("升级完毕!");
		
		ShineSetup.exit();
	}
}
