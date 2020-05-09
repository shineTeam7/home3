package com.home.commonGame.control;

import com.home.commonBase.constlist.generate.FuncToolType;
import com.home.commonBase.data.social.RoleSocialData;
import com.home.commonBase.data.social.rank.RankToolData;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.part.gameGlobal.data.GameSystemPartData;
import com.home.commonBase.part.gameGlobal.list.GameGlobalListData;
import com.home.commonBase.table.table.GlobalTable;
import com.home.commonBase.table.table.PlayerTable;
import com.home.commonBase.table.table.RoleGroupTable;
import com.home.commonBase.table.table.RoleSocialTable;
import com.home.commonBase.table.table.ServerTable;
import com.home.commonBase.tool.func.FuncTool;
import com.home.commonGame.global.GameC;
import com.home.commonGame.part.gameGlobal.GameGlobal;
import com.home.commonGame.tool.func.GamePlayerRankTool;
import com.home.shine.ShineSetup;
import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.control.DBControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.data.DateData;
import com.home.shine.global.ShineSetting;
import com.home.shine.serverConfig.GameServerConfig;
import com.home.shine.serverConfig.ServerConfig;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.LongLongMap;
import com.home.shine.support.collection.LongObjectMap;
import com.home.shine.support.collection.SList;
import com.home.shine.table.BaseTable;
import com.home.shine.table.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;

/** 合服控制 */
public class GameJoinControl
{
	/** 执行合服 */
	public void execute(int joinGameID)
	{
		GameC.version.init();
		
		//标记
		ShineSetting.isDBUpdate=true;
		
		
		GameServerConfig baseConfig=ServerConfig.getGameConfig(GameC.app.id);
		
		if(baseConfig==null)
		{
			Ctrl.throwError("未找到baseGameID为",GameC.app.id,"的服务器配置");
			return;
		}
		
		DBConnect baseConnect=null;
		
		try
		{
			baseConnect=new DBConnect(baseConfig.mysql);
		}
		catch(Exception e)
		{
			Ctrl.throwError("game"+baseConfig.id+"的mysql:"+baseConfig.mysql+"无法连接");
			return;
		}
		
		GameServerConfig joinConfig=ServerConfig.getGameConfig(joinGameID);
		
		if(joinConfig==null)
		{
			Ctrl.throwError("未找到joinGameID为",joinGameID,"的服务器配置");
			return;
		}
		
		DBConnect joinConnect=null;
		
		try
		{
			joinConnect=new DBConnect(joinConfig.mysql);
		}
		catch(Exception e)
		{
			Ctrl.throwError("game"+joinConfig.id+"的mysql:"+joinConfig.mysql+"无法连接");
			return;
		}
		
		DBConnect centerConnect;
		
		try
		{
			centerConnect=new DBConnect(ServerConfig.getCenterConfig().mysql);
		}
		catch(Exception e)
		{
			Ctrl.throwError("center的mysql:"+ServerConfig.getCenterConfig().mysql+"无法连接");
			return;
		}
		
		ServerTable table=BaseC.factory.createServerTable();
		table.areaID=joinGameID;
		
		if(table.loadSync(centerConnect) && table.nowAreaID!=table.areaID)
		{
			Ctrl.warnLog("已合服过",joinGameID,"->",table.nowAreaID);
			ShineSetup.exit();
			return;
		}
		
		doGlobals(baseConnect,joinConnect);
		
		//三张单独表
		doOneTable(BaseC.factory.createPlayerTable(),baseConnect,joinConnect);
		doOneTable(BaseC.factory.createRoleSocialTable(),baseConnect,joinConnect);
		doOneTable(BaseC.factory.createRoleGroupTable(),baseConnect,joinConnect);
		
		int newAreaID=GameC.app.id;
		
		//新表
		table.areaID=joinGameID;
		table.nowAreaID=newAreaID;
		table.name="";
		
		if(!table.insertSync(centerConnect))
		{
			Ctrl.throwError("插入server表失败");
			return;
		}
		
		SList<BaseTable> serverTables=table.loadAllSync(centerConnect);
		
		serverTables.forEach(v->
		{
			ServerTable st=(ServerTable)v;
			
			if(st.nowAreaID==joinGameID)
			{
				st.nowAreaID=newAreaID;
				
				if(!st.updateSync(centerConnect))
				{
					Ctrl.throwError("更新server表失败",st.areaID);
					return;
				}
			}
		});
		
		Ctrl.print("合服完毕!");
		ShineSetup.exit();
	}
	
	protected void doOneTable(BaseTable table,DBConnect baseConnect,DBConnect joinConnect)
	{
		SList<BaseTable> list=table.loadAllSync(joinConnect);
		
		//没有角色
		if(list.isEmpty())
			return;
		
		Connection con=baseConnect.getMainConnection();
		
		try
		{
			PreparedStatement insertPs=con.prepareStatement(table.getInsertStr());
			
			BaseTable[] values=list.getValues();
			BaseTable v;
			
			for(int i=0,len=list.size();i<len;++i)
			{
				v=values[i];
				
				v.insertToPs(insertPs);
				insertPs.addBatch();
			}
			
			insertPs.executeBatch();
			DBControl.closeStatement(insertPs);
			con.commit();
			baseConnect.reMainConnection(con);
		}
		catch(Exception e)
		{
			Ctrl.errorLog(e);
			ShineSetup.exit("执行OneTable出错"+table.getTableID());
		}
	}
	
	/** 全局数据 */
	protected void doGlobals(DBConnect baseConnect,DBConnect joinConnect)
	{
		GlobalTable baseTable=BaseC.factory.createGameGlobalTable();
		
		int nowDataVersion=BaseC.config.getDBDataVersion();
		
		boolean re=baseTable.loadSync(baseConnect);
		
		if(!re)
		{
			Ctrl.throwError("global表不存在");
			return;
		}
		
		//版本不匹配
		if(baseTable.dataVersion!=nowDataVersion)
		{
			ShineSetup.exit("base数据库结构版本不匹配,old:" + baseTable.dataVersion + ",new:" + nowDataVersion);
			return;
		}
		
		GlobalTable joinTable=BaseC.factory.createGameGlobalTable();
		
		re=joinTable.loadSync(joinConnect);
		
		if(!re)
		{
			Ctrl.throwError("global表不存在");
			return;
		}
		
		//版本不匹配
		if(joinTable.dataVersion!=nowDataVersion)
		{
			ShineSetup.exit("join数据库结构版本不匹配,old:" + joinTable.dataVersion + ",new:" + nowDataVersion);
			return;
		}
		
		GameGlobal baseGlobal=GameC.factory.createGlobal();
		GameC.global=baseGlobal;
		baseGlobal.construct();
		baseGlobal.init();
		baseGlobal.loadCustom(baseTable);
		
		GameGlobal joinGlobal=GameC.factory.createGlobal();
		GameC.global=joinGlobal;
		joinGlobal.construct();
		joinGlobal.init();
		joinGlobal.loadCustom(joinTable);
		
		doGlobalsNext(baseGlobal,joinGlobal);
		
		GameGlobalListData listData=baseGlobal.createListData();
		baseGlobal.writeListData(listData);
		
		BytesWriteStream tempStream=BytesWriteStream.create();
		listData.writeBytesFull(tempStream);
		
		DateData nowDate=DateData.getNow();
		
		//数据结构版本
		baseTable.dataVersion=nowDataVersion;
		baseTable.data=tempStream.getByteArray();
		baseTable.saveDate=nowDate;
		
		if(!baseTable.updateSync(baseConnect))
		{
			Ctrl.throwError("更新global失败");
		}
	}
	
	/** 执行global下一步 */
	protected void doGlobalsNext(GameGlobal baseGlobal,GameGlobal joinGlobal)
	{
		//更换排行榜
		IntObjectMap<FuncTool> funcToolDic=joinGlobal.func.getFuncToolDic(FuncToolType.Rank);
		
		funcToolDic.forEachValue(v->
		{
			if(v instanceof GamePlayerRankTool)
			{
				GamePlayerRankTool joinTool=(GamePlayerRankTool)v;
				
				GamePlayerRankTool baseTool=baseGlobal.func.getPlayerRankTool(v.getFuncID());
				
				RankToolData data=joinTool.getData();
				
				data.list.forEach(v2->
				{
					baseTool.commitRank(baseTool.getVersion(),v2);
				});
			}
		});
		
		GameSystemPartData bSystemPartData=baseGlobal.system.getPartData();
		GameSystemPartData jSystemPartData=joinGlobal.system.getPartData();
		
		//角色序号合并
		jSystemPartData.playerIndexDic.forEach((k1,v1)->
		{
			if(v1>0)
			{
				//合并
				bSystemPartData.playerIndexDic.put(k1,v1);
			}
		});
		
		//有未完成的事务
		if(!jSystemPartData.workSenderData.workRecordDic.isEmpty())
		{
			Ctrl.print("目标服有未完成的事务",jSystemPartData.workSenderData.workRecordDic.size());
			
			jSystemPartData.workSenderData.workRecordDic.forEachValue(v1->
			{
				v1.workInstanceID=++bSystemPartData.workSenderData.workInstanceID;
				bSystemPartData.workSenderData.workRecordDic.put(v1.workInstanceID,v1);
			});
		}
		
		//接收事务
		jSystemPartData.workReceiverData.workExecuteRecordDic.forEach((k1,v1)->
		{
			LongLongMap dic=bSystemPartData.workReceiverData.workExecuteRecordDic.computeIfAbsent(k1,k2->new LongLongMap());
			
			//补充上去
			v1.forEach((k2,v2)->
			{
				dic.put(k2,v2);
			});
		});
	}
}
