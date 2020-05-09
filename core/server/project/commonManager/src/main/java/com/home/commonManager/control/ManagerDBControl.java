package com.home.commonManager.control;

import com.home.commonBase.control.BaseDBControl;
import com.home.commonBase.data.system.PlayerWorkData;
import com.home.commonBase.data.system.PlayerWorkListData;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.part.player.list.PlayerListData;
import com.home.commonBase.table.table.PlayerTable;
import com.home.shine.bytes.BytesReadStream;
import com.home.shine.control.ThreadControl;
import com.home.shine.serverConfig.ServerConfig;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.SList;
import com.home.shine.support.func.ObjectCall3;
import com.home.shine.table.DBConnect;

public class ManagerDBControl extends BaseDBControl
{
	private IntObjectMap<DBConnect> _gameConnectDic=new IntObjectMap<>(DBConnect[]::new);
	
	public ManagerDBControl()
	{
	
	}
	
	@Override
	public void init()
	{
		setURL(ServerConfig.getCenterConfig().mysql);
		super.init();
	}
	
	@Override
	protected void toDispose()
	{
		_gameConnectDic.forEachValue(v->
		{
			v.close();
		});
		
		super.toDispose();
	}
	
	protected void onKeep(int delay)
	{
		super.onKeep(delay);
		
		_gameConnectDic.forEachValue(v->
		{
			v.keep();
		});
	}
	
	/** 获取game服连接 */
	public synchronized DBConnect getGameConnect(int gameID)
	{
		DBConnect connect=_gameConnectDic.get(gameID);
		
		if(connect==null)
		{
			connect=new DBConnect(ServerConfig.getGameConfig(gameID).mysql);
			_gameConnectDic.put(gameID,connect);
		}
		
		return connect;
	}
	
	/** 查询一个玩家的全部数据(从库里) */
	public void selectPlayer(int gameID,long playerID,ObjectCall3<PlayerTable,PlayerListData,PlayerWorkListData> func)
	{
		DBConnect connect=getGameConnect(gameID);

		PlayerTable table=BaseC.factory.createPlayerTable();
		table.playerID=playerID;

		table.load(connect,k->
		{
			if(k)
			{
				PlayerListData listData=BaseC.factory.createPlayerListData();
				BytesReadStream stream=BytesReadStream.create(table.data);
				listData.readBytesFull(stream);

				SList<PlayerWorkData> offlineWorkList=CommonSetting.offlineWorkUseTable ? table.offlineWorkDataListMain : null;
				PlayerWorkListData wList=new PlayerWorkListData();
				wList.list=offlineWorkList;
				
				func.apply(table,listData,wList);
			}
			else
			{
				func.apply(null,null,null);
			}
		});
	}
	
	
}
