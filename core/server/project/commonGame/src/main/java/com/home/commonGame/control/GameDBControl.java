package com.home.commonGame.control;

import com.home.commonBase.constlist.system.GameAreaDivideType;
import com.home.commonBase.control.BaseGameDBControl;
import com.home.commonBase.data.login.PlayerLoginData;
import com.home.commonBase.data.social.roleGroup.RoleGroupData;
import com.home.commonBase.data.system.PlayerWorkListData;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.global.Global;
import com.home.commonBase.part.gameGlobal.list.GameGlobalListData;
import com.home.commonBase.part.player.list.PlayerListData;
import com.home.commonBase.table.table.PlayerTable;
import com.home.commonBase.table.table.RoleGroupTable;
import com.home.commonGame.global.GameC;
import com.home.commonGame.logic.func.RoleGroup;
import com.home.commonGame.part.gameGlobal.part.GameFuncPart;
import com.home.commonGame.part.player.Player;
import com.home.commonGame.tool.func.GameRoleGroupTool;
import com.home.shine.bytes.BytesReadStream;
import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.control.ThreadControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.data.DateData;
import com.home.shine.dataEx.TableBatchData;
import com.home.shine.global.ShineGlobal;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.LongObjectMap;
import com.home.shine.support.collection.SList;
import com.home.shine.support.collection.SMap;
import com.home.shine.support.func.ObjectCall;
import com.home.shine.table.BaseTable;
import com.home.shine.table.DBConnect;
import com.home.shine.utils.BytesUtils;

import java.sql.Connection;

public class GameDBControl extends BaseGameDBControl
{
	/** 中心服db连接 */
	private String _centerURL;
	/** 中心服数据库连接 */
	private DBConnect _centerConnect;
	//playerTables
	/** 角色表(key:playerID) */
	private LongObjectMap<PlayerTable> _playerTables=new LongObjectMap<>(PlayerTable[]::new);
	/** 角色表(key:name) */
	private SMap<String,PlayerTable> _playerTablesByName=new SMap<>();
	/** 角色表(key:userID:<createAreaID:playerID>) */
	private LongObjectMap<IntObjectMap<LongObjectMap<PlayerTable>>> _playerTablesByUIDAndCreateAreaID=new LongObjectMap<>();
	/** 获取角色表字典(key:playerID) */
	private LongObjectMap<SList<ObjectCall<PlayerTable>>> _getPlayerTableTaskDic=new LongObjectMap<>();
	
	//roleGroupTables
	private LongObjectMap<RoleGroupTable> _roleGroupTables=new LongObjectMap<>(RoleGroupTable[]::new);
	
	//batch
	/** 玩家批量 */
	private TableBatchData _playerBatch=new TableBatchData();
	
	/** 玩家群批量 */
	private TableBatchData _roleGroupBatch=new TableBatchData();
	
	//write
	
	private DBWriteTempData _tempData;
	
	//temp
	
	private PlayerTable _tempPlayerTable;
	
	public void setCenterURL(String str)
	{
		_centerURL=str;
	}
	
	@Override
	protected void makeDBFilePath()
	{
		_dbFilePath=ShineGlobal.dbFilePath +"/game_"+GameC.app.id+".bin";
	}
	
	@Override
	protected void toInit()
	{
		//辅助服不需要gameDB
		if(!GameC.main.isAssist())
		{
			_tempPlayerTable=BaseC.factory.createPlayerTable();
			_tempData=toCreateDBWriteTempData();
			
			super.toInit();
		}
		
		_centerConnect=new DBConnect(_centerURL);
	}
	
	/** 获取中心服数据库连接 */
	public DBConnect getCenterConnect()
	{
		return _centerConnect;
	}
	
	protected DBWriteTempData toCreateDBWriteTempData()
	{
		return new DBWriteTempData();
	}
	
	protected PlayerWriteTempData toCreatePlayerWriteTempData()
	{
		return new PlayerWriteTempData();
	}

	public DBConnect getPlayerNameConnect()
	{
		//不是分服
		if(!CommonSetting.isAreaSplit())
		{
			return _centerConnect;
		}
		//分服模式
		else
		{
			return _connect;
		}
	}
	
	//playerTables
	
	/** 添加角色表(返回添加好的表,如存在就是原来的表) */
	private PlayerTable addPlayerTable(PlayerTable table)
	{
		ThreadControl.checkCurrentIsMainThread();
		
		PlayerTable pt=_playerTables.get(table.playerID);
		
		//已存在
		if(pt!=null)
		{
			pt.refreshKeepTime();
			
			return pt;
		}
		
		_playerTables.put(table.playerID,table);
		
		_playerTablesByName.put(table.name,table);
		
		_playerTablesByUIDAndCreateAreaID.computeIfAbsent(table.userID,k0->new IntObjectMap<>()).computeIfAbsent(table.createAreaID,k->new LongObjectMap<>(PlayerTable[]::new)).put(table.playerID,table);
		
		table.refreshKeepTime();
		
		return table;
	}
	
	/** 移除角色表 */
	protected void removePlayerTable(PlayerTable table)
	{
		ThreadControl.checkCurrentIsMainThread();
		
		_playerTables.remove(table.playerID);

		_playerTablesByName.remove(table.name);
		
		IntObjectMap<LongObjectMap<PlayerTable>> dic=_playerTablesByUIDAndCreateAreaID.get(table.userID);
		
		if(dic!=null)
		{
			LongObjectMap<PlayerTable> dic2=dic.get(table.createAreaID);
			
			if(dic2!=null)
			{
				dic2.remove(table.playerID);
				
				if(dic2.isEmpty())
				{
					dic.remove(table.createAreaID);
					
					if(dic.isEmpty())
					{
						_playerTablesByUIDAndCreateAreaID.remove(table.userID);
					}
				}
			}
		}
	}
	
	/** 更改表名 */
	public void changePlayerTableName(long playerID,String name)
	{
		PlayerTable table=getPlayerTable(playerID);
		
		if(table!=null)
		{
			_playerTablesByName.remove(table.name);
			table.name=name;
			_playerTablesByName.put(name,table);
		}
	}
	
	/** 获取角色表(主线程) */
	public PlayerTable getPlayerTable(long playerID)
	{
		ThreadControl.checkCurrentIsMainThread();
		
		PlayerTable table=_playerTables.get(playerID);
		
		if(table!=null)
		{
			table.refreshKeepTime();
		}
		
		return table;
	}
	
	/** 获取角色表通过名字 */
	public PlayerTable getPlayerTableByName(String name)
	{
		ThreadControl.checkCurrentIsMainThread();
		
		PlayerTable table=_playerTablesByName.get(name);
		
		if(table!=null)
		{
			table.refreshKeepTime();
		}
		
		return table;
	}
	
	private LongObjectMap<PlayerTable> getPlayerTablesByUserIDAndCreateAreaID(long userID,int createAreaID)
	{
		IntObjectMap<LongObjectMap<PlayerTable>> dic=_playerTablesByUIDAndCreateAreaID.get(userID);
		
		if(dic!=null)
		{
			return dic.get(createAreaID);
		}
		
		return null;
	}
	
	/** 获取角色表(主线程)(如有就直接同步调用,没有就异步加载)(如异步返回null,就是加载失败) */
	public void getPlayerTableAbsByID(long playerID,ObjectCall<PlayerTable> overCall)
	{
		ThreadControl.checkCurrentIsMainThread();
		
		PlayerTable pt=getPlayerTable(playerID);
		
		if(pt!=null)
		{
			overCall.apply(pt);
		}
		else
		{
			SList<ObjectCall<PlayerTable>> taskList=_getPlayerTableTaskDic.get(playerID);
			
			if(taskList!=null)
			{
				taskList.add(overCall);
			}
			else
			{
				taskList=new SList<>(ObjectCall[]::new);
				taskList.add(overCall);
				_getPlayerTableTaskDic.put(playerID,taskList);
				
				PlayerTable pt2=BaseC.factory.createPlayerTable();
				pt2.playerID=playerID;
				
				ObjectCall<Boolean> func=k->
				{
					PlayerTable pt3;
					if(k)
					{
						pt3=addPlayerTable(pt2);
					}
					else
					{
						pt3=null;
					}
					
					SList<ObjectCall<PlayerTable>> taskList2=_getPlayerTableTaskDic.remove(playerID);
					ObjectCall<PlayerTable>[] values=taskList2.getValues();
					for(int i=0,len=taskList2.size();i<len;i++)
					{
						values[i].apply(pt3);
					}
				};
				
				pt2.load(_connect,func,ThreadControl.getMainThread());
			}
		}
	}
	
	/** 获取角色表通过名字(主线程)(如有就直接同步调用,没有就异步加载)(如异步返回null,就是加载失败)(底层有做合并,本层就不必做了) */
	public void getPlayerTableAbsByName(String name,ObjectCall<PlayerTable> overCall)
	{
		ThreadControl.checkCurrentIsMainThread();
		
		//这个不做合并了，因为用的少
		
		PlayerTable pt=getPlayerTableByName(name);
		
		if(pt!=null)
		{
			overCall.apply(pt);
		}
		else
		{
			PlayerTable pt2=BaseC.factory.createPlayerTable();
			pt2.name=name;
			
			ObjectCall<Boolean> func=k->
			{
				if(k)
				{
					overCall.apply(addPlayerTable(pt2));
				}
				else
				{
					overCall.apply(null);
				}
			};
			
			pt2.load2(_connect,func,ThreadControl.getMainThread());
		}
	}
	
	/** 通过uid和createAreaID查询角色表(需要读一次库)(主线程) */
	public void loadPlayerTableByUserIDAndCreateAreaID(long userID,int createAreaID,ObjectCall<SList<PlayerTable>> overCall)
	{
		ThreadControl.checkCurrentIsMainThread();
		
		LongObjectMap<PlayerTable> dic=getPlayerTablesByUserIDAndCreateAreaID(userID,createAreaID);
		
		//数目够
		if(dic!=null && dic.size() >= Global.ownPlayerNum)
		{
			SList<PlayerTable> list=new SList<>(PlayerTable[]::new);
			
			dic.forEachValue(v->
			{
				if(!v.needRemove())
				{
					list.add(v);
				}
			});
			
			overCall.apply(list);
			
			return;
		}
		
		ObjectCall<SList<BaseTable>> func=k->
		{
			int len=k.size();
			Object[] values=k.getValues();
			
			for(int i=0;i<len;++i)
			{
				addPlayerTable((PlayerTable)values[i]);
			}
			
			SList<PlayerTable> list=new SList<>();
			
			LongObjectMap<PlayerTable> dic2=getPlayerTablesByUserIDAndCreateAreaID(userID,createAreaID);
			
			if(dic2!=null)
			{
				dic2.forEachValue(v->
				{
					if(!v.needRemove())
					{
						list.add(v);
					}
				});
			}
			
			overCall.apply(list);
		};
		
		_tempPlayerTable.loadByUserIDAndCreateAreaID(_connect,userID,createAreaID,func,ThreadControl.getMainThread());
	}
	
	
	//操作
	
	/** 插入新角色表(主线程) */
	public void insertNewPlayer(PlayerTable table)
	{
		//下次插入
		table.setNeedInsert();
		
		table.loginData=BytesUtils.EmptyByteArr;
		table.data=BytesUtils.EmptyByteArr;
		table.offlineData=BytesUtils.EmptyByteArr;
		
		//添加角色表
		addPlayerTable(table);
	}
	
	/** 写离线角色数据到表(主线程) */
	public void writeOfflinePlayer(Player player)
	{
		PlayerTable table=getPlayerTable(player.role.playerID);
		
		if(table==null)
		{
			Ctrl.throwError("不应该找不到角色表");
			return;
		}
		
		boolean hasError=false;
		PlayerListData listData=player.createListData();
		
		try
		{
			player.writeListData(listData);
		}
		catch(Exception e)
		{
			player.errorLog(e);
			hasError=true;
		}
		
		//登录数据
		PlayerLoginData loginData=player.role.createLoginData();
		
		try
		{
			player.writePlayerTableForOther(table);
		}
		catch(Exception e)
		{
			player.errorLog(e);
			hasError=true;
		}
		
		//清掉离线事务
		table.offlineWorkDataListMain.clear();
		
		if(!hasError)
		{
			//db写线程写入
			ThreadControl.addDBWriteFunc(()->
			{
				_writeStream.clear();
				listData.writeBytesFull(_writeStream);
				table.data=_writeStream.getByteArray();
				
				//loginData
				table.loginDataT=loginData;
			});
		}
	}
	
	/** 删除角色表 */
	public void deletePlayerTable(long playerID)
	{
		PlayerTable table=getPlayerTable(playerID);
		
		if(table==null)
		{
			Ctrl.warnLog("deletePlayerTable时，未找到playerTable");
			return;
		}
		
		table.setNeedDelete();
	}
	
	//DB写
	
	/** 构造DB写入对象(主线程) */
	@Override
	protected void makeDBWrites(int index,boolean lastFinish)
	{
		if(!lastFinish)
		{
			_tempData.clear();
			//创建新的
			_tempData=toCreateDBWriteTempData();
		}
		
		DBWriteTempData tempData=_tempData;
		tempData.index=index;
		
		//global
		makeDBWriteGlobal(tempData);
		//player
		makeDBWritePlayers(tempData);
		//roleSocial
		makeDBWriteRoleSocials(tempData,GameC.global.social.getRoleSocialDataDic());
		//roleGroup
		makeDBWriteRoleGroups(tempData);
		
		checkMakeOver(tempData,index);
	}
	
	/** 构造global的写入 */
	protected void makeDBWriteGlobal(DBWriteTempData tempData)
	{
		//全局表
		tempData.globalListData=GameC.global.createListData();
		GameC.global.writeListData((GameGlobalListData)tempData.globalListData);
	}
	
	/** 构造玩家部分的写入 */
	protected void makeDBWritePlayers(DBWriteTempData tempData)
	{
		int index=tempData.index;
		
		//角色表组
		tempData.players.ensureCapacity(_playerTables.size());
		
		_playerTables.forEachValue(table->
		{
			PlayerWriteTempData tData;
			Player player;
			long playerID;
			playerID=table.playerID;
			
			//存在角色
			if((player=GameC.main.getExistPlayerByID(playerID))!=null)
			{
				table.refreshKeepTime();
				
				//在线状态的
				if(player.system.isStateOnline())
				{
					tData=toCreatePlayerWriteTempData();
					tempData.players.add(tData);
					
					tData.addTable(table);
					tData.player=player;
					++tempData.needPlayerNum;
					
					table.clearOfflineWork();
					
					writeOnePlayer(tempData,tData,index,player);
				}
				//离线状态的
				else if(player.system.isStateOffline())
				{
					tData=toCreatePlayerWriteTempData();
					tData.addTable(table);
					tempData.players.add(tData);
					
					toWriteOnePlayer(player,tData);
				}
				else
				{
					//其他过程维持旧值
					tData=toCreatePlayerWriteTempData();
					tData.addTable(table);
					tempData.players.add(tData);
				}
			}
			//其他
			else
			{
				//切换中的角色
				if(GameC.gameSwitch.getSwitchToPlayerByID(playerID)!=null)
				{
					table.refreshKeepTime();
					table.needWrite=true;
				}
				
				//数据保留
				if(table.needWrite)
				{
					table.needWrite=false;
					
					tData=toCreatePlayerWriteTempData();
					tData.addTable(table);
					tempData.players.add(tData);
					
					if(CommonSetting.offlineWorkUseTable)
					{
						tData.needOfflineWork=table.refreshOfflineWork();
					}
				}
				else
				{
					tempData.keepPlayerTables.add(table);
				}
			}
		});
		
	}
	
	/** 构造玩家群的写入 */
	protected void makeDBWriteRoleGroups(DBWriteTempData tempData)
	{
		GameFuncPart gameFunc=GameC.global.func;
		
		tempData.roleGroups.ensureCapacity(_roleGroupTables.size());
		
		_roleGroupTables.forEachValue(v->
		{
			GameRoleGroupTool roleGroupTool;
			RoleGroup roleGroup;
			RoleGroupWriteTempData rtData;
			
			rtData=new RoleGroupWriteTempData();
			rtData.table=v;
			tempData.roleGroups.add(rtData);
			
			roleGroupTool=gameFunc.getRoleGroupTool(v.funcID);
			
			if((roleGroup=roleGroupTool.getRoleGroup(v.groupID))!=null)
			{
				rtData.data=(RoleGroupData)roleGroup.getData().clone();
			}
		});
	}
	
	/** 写一个角色的基础部分 */
	protected void toWriteOnePlayer(Player player,PlayerWriteTempData tData)
	{
		PlayerTable table=tData.table;
		long playerID=table.playerID;
		
		player.writePlayerTableForOther(table);
		
		if(table.playerID!=playerID)
		{
			Ctrl.throwError("严重错误,PlayerTable写飞");
		}
		
		try
		{
			player.writeListData(tData.listData=player.createListData());
			tData.loginData=player.role.createLoginData();
		}
		catch(Exception e)
		{
			player.errorLog(e);
		}
	}
	
	private void writeOnePlayer(DBWriteTempData tempWriteData,PlayerWriteTempData tData,int index,Player player)
	{
		PlayerWriteDBFunc func=new PlayerWriteDBFunc();
		func.player=player;
		func.tempWriteData=tempWriteData;
		func.tData=tData;
		func.index=index;
		tData.func=func;
		
		//切逻辑线程
		player.addFunc(func);
	}
	
	//roleGroups
	
	private void addRoleGroupTable(RoleGroupTable table)
	{
		_roleGroupTables.put(table.groupID,table);
	}
	
	private void removeRoleGroupTable(RoleGroupTable table)
	{
		_roleGroupTables.remove(table.groupID);
	}
	
	public RoleGroupTable getRoleGroupTable(long groupID)
	{
		return _roleGroupTables.get(groupID);
	}
	
	/** 读取玩家群表 */
	public void loadRoleGroupTable(long groupID,ObjectCall<RoleGroupTable> func)
	{
		RoleGroupTable t=getRoleGroupTable(groupID);
		
		if(t!=null)
		{
			//已删除
			if(t.needRemove())
			{
				t=null;
			}
			
			func.apply(t);
			return;
		}
		
		RoleGroupTable table=BaseC.factory.createRoleGroupTable();
		table.groupID=groupID;
		table.load(_connect,v->
		{
			if(v)
			{
				addRoleGroupTable(table);
				func.apply(table);
			}
			else
			{
				func.apply(null);
			}
		});
	}
	
	/** 插入新玩家群表(主线程) */
	public void insertNewRoleGroup(RoleGroupTable table)
	{
		//下次插入
		table.setNeedInsert();
		
		table.data=BytesUtils.EmptyByteArr;
		
		//添加角色表
		addRoleGroupTable(table);
	}
	
	/** 删除玩家群表 */
	public void deleteRoleGroupTable(long groupID)
	{
		RoleGroupTable table=getRoleGroupTable(groupID);
		
		if(table==null)
		{
			Ctrl.warnLog("deleteRoleGroupTable时，未找到RoleGroupTable");
			return;
		}
		
		table.setNeedDelete();
	}
	
	//write
	
	/** 检查角色表是否构造完毕(检验过index了)(主线程) */
	private void checkMakeOver(DBWriteTempData tempWriteData,int index)
	{
		if(tempWriteData.isOver)
			return;
		
		//完了
		if(tempWriteData.needPlayerNum==0)
		{
			tempWriteData.isOver=true;
			
			makeDBWriteOver(index,tempWriteData);
		}
	}
	
	@Override
	protected void makeDBTimeOut()
	{
		//Player[] values=_tempWriteData.playerDic.getValues();
		//
		//Player player;
		//
		//for(int i=values.length - 1;i >= 0;--i)
		//{
		//	if((player=values[i])!=null)
		//	{
		//		player.errorLog("角色写库超时");
		//	}
		//}
		
		super.makeDBTimeOut();
	}
	
	@Override
	protected void toWriteDB(BaseDBWriteTempData tData,Connection con)
	{
		super.toWriteDB(tData,con);
		
		DBWriteTempData data=(DBWriteTempData)tData;
		
		writeDBPlayers(data,con);
		writeDBLog(_tempResult,"players");
		
		writeDBRoleGroups(data,con);
		writeDBLog(_tempResult,"roleGroups");
	}
	
	@Override
	protected void toWriteEndDB(BaseDBWriteTempData tData)
	{
		super.toWriteEndDB(tData);
		
		DBWriteTempData data=(DBWriteTempData)tData;
		
		writeEndPlayers(data);
		writeEndRoleGroups(data);
	}
	
	
	protected void writeDBPlayers(DBWriteTempData tData,Connection con)
	{
		boolean hasError=tData.hasError;
		BytesWriteStream tempStream=_writeStream;
		
		int playerWriteNum=0;
		DateData nowDate=DateData.getNow();
		
		long t=Ctrl.getTimer();
		
		//players
		SList<PlayerWriteTempData> players=tData.players;
		
		//写player
		if(!players.isEmpty())
		{
			_playerBatch.clear();
			_playerBatch.setConnection(con);
			_playerBatch.hasError=hasError;
			
			try
			{
				PlayerWriteTempData[] values=players.getValues();
				PlayerWriteTempData ptData;
				
				PlayerTable table;
				
				PlayerWorkListData offlineWorkListData=CommonSetting.offlineWorkUseTable ? new PlayerWorkListData() : null;
				
				for(int i=players.size() - 1;i >= 0;--i)
				{
					if((ptData=values[i])!=null)
					{
						table=ptData.table;
						
						try
						{
							//有list数据
							if(ptData.listData!=null)
							{
								tempStream.clear();
								ptData.listData.writeBytesFull(tempStream);
								table.data=tempStream.getByteArray();
							}
							
							if(ptData.loginData!=null)
							{
								table.loginDataT=ptData.loginData;
							}
							
							//loginData
							tempStream.clear();
							table.loginDataT.writeBytesFull(tempStream);
							table.loginData=tempStream.getByteArray();
							
							if(ptData.needOfflineWork)
							{
								if(CommonSetting.offlineWorkUseTable)
								{
									offlineWorkListData.list=table.offlineWorkDataListT;
									tempStream.clear();
									offlineWorkListData.writeBytesFull(tempStream);
									table.offlineData=tempStream.getByteArray();
								}
								else
								{
									table.offlineData=BytesUtils.EmptyByteArr;
								}
							}
							
							//当前时间
							table.saveDate=nowDate;
							
							_playerBatch.addTable(table);//默认100
						}
						catch(Exception e)
						{
							Ctrl.errorLog(e);
							_playerBatch.hasError=true;
						}
						
						playerWriteNum++;
					}
				}
				
				if(offlineWorkListData!=null)
				{
					offlineWorkListData.list=null;
				}
				
				_playerBatch.executeBatch();
			}
			catch(Exception e)
			{
				_playerBatch.hasError=true;
				Ctrl.errorLog(e);
			}
			
			_playerBatch.closePs();
			
			if(_playerBatch.hasError)
			{
				hasError=true;
			}
		}
		
		if(hasError)
			tData.hasError=true;
		
		if(ShineSetting.needDetailLog)
		{
			Ctrl.runningLog("game服 writePlayer totalSize:"+_playerBatch.getTotalWriteSize());
		}
		
		_tempResult[0]=_playerBatch.getTotalWriteSize();
		_tempResult[1]=(int)(Ctrl.getTimer()-t);
		_tempResult[2]=playerWriteNum;
	}
	
	protected void writeDBRoleGroups(DBWriteTempData tData,Connection con)
	{
		boolean hasError=tData.hasError;
		
		BytesWriteStream tempStream=_writeStream;
		
		long t=Ctrl.getTimer();
		int roleGroupWriteNum=0;
		
		SList<RoleGroupWriteTempData> roleGroups=tData.roleGroups;
		
		//写roleGroup
		if(!roleGroups.isEmpty())
		{
			_roleGroupBatch.clear();
			_roleGroupBatch.setConnection(con);
			_roleGroupBatch.hasError=hasError;
			
			try
			{
				RoleGroupWriteTempData[] values=roleGroups.getValues();
				RoleGroupWriteTempData v;
				
				for(int i=roleGroups.size()-1;i>=0;--i)
				{
					if((v=values[i])!=null)
					{
						try
						{
							if(v.data!=null)
							{
								tempStream.clear();
								v.data.writeBytesFull(tempStream);
								v.table.data=tempStream.getByteArray();
							}
							
							_roleGroupBatch.addTable(v.table);
						}
						catch(Exception e)
						{
							Ctrl.errorLog(e);
						}
						
						roleGroupWriteNum++;
					}
				}
				
				_roleGroupBatch.executeBatch();
			}
			catch(Exception e)
			{
				_roleGroupBatch.hasError=true;
				Ctrl.errorLog(e);
			}
			
			_roleGroupBatch.closePs();
			if(_roleGroupBatch.hasError)
			{
				hasError=true;
			}
		}
		
		if(hasError)
			tData.hasError=true;
		
		if(ShineSetting.needDetailLog)
		{
			Ctrl.runningLog("game服 writeRoleGroup totalSize:"+_roleGroupBatch.getTotalWriteSize());
		}
		
		_tempResult[0]=_roleGroupBatch.getTotalWriteSize();
		_tempResult[1]=(int)(Ctrl.getTimer()-t);
		_tempResult[2]=roleGroupWriteNum;
	}
	
	
	protected void writeEndPlayers(DBWriteTempData tData)
	{
		if(tData.hasError)
		{
			Ctrl.errorLog("gameDB写入出错");
			
			SList<PlayerWriteTempData> players=tData.players;
			
			players.forEach(v->
			{
				v.table.needWrite=true;
			});
		}
		
		_playerBatch.end(tData.hasError);
	}
	
	protected void writeEndRoleGroups(DBWriteTempData tData)
	{
		_roleGroupBatch.end(tData.hasError);
	}
	
	@Override
	protected void toWriteFile(BytesWriteStream stream,Object obj)
	{
		super.toWriteFile(stream,obj);
		
		DBWriteTempData data=(DBWriteTempData)obj;
		
		//players
		stream.writeLen(data.players.size());
		
		data.players.forEach(v->
		{
			v.table.writeTableStream(stream);
		});
		
		//roleGroups
		stream.writeLen(data.roleGroups.size());
		
		data.roleGroups.forEach(v->
		{
			v.table.writeTableStream(stream);
		});
	}
	
	@Override
	protected void toReadFile(BytesReadStream stream)
	{
		super.toReadFile(stream);
		
		int len=stream.readLen();
		
		PlayerTable playerTable;
		
		for(int i=0;i<len;i++)
		{
			playerTable=BaseC.factory.createPlayerTable();
			playerTable.readTableStream(stream);
			
			addPlayerTable(playerTable);
		}
		
		len=stream.readLen();
		
		RoleGroupTable roleGroupTable;
		
		for(int i=0;i<len;i++)
		{
			roleGroupTable=BaseC.factory.createRoleGroupTable();
			roleGroupTable.readTableStream(stream);
			
			addRoleGroupTable(roleGroupTable);
		}
	}
	
	@Override
	protected void afterWriteDB(BaseDBWriteTempData data)
	{
		super.afterWriteDB(data);
		
		DBWriteTempData tData=(DBWriteTempData)data;
		
		PlayerWriteTempData[] values=tData.players.getValues();
		
		for(int i=0,len=tData.players.size();i<len;++i)
		{
			afterWriteOnePlayerTable(values[i].table);
		}
		
		PlayerTable[] values2=tData.keepPlayerTables.getValues();
		for(int i=0,len=tData.keepPlayerTables.size();i<len;++i)
		{
			afterWriteOnePlayerTable(values2[i]);
		}
		
		RoleGroupWriteTempData[] values1=tData.roleGroups.getValues();
		RoleGroupTable rTable;
		
		for(int i=0,len=tData.roleGroups.size();i<len;++i)
		{
			rTable=values1[i].table;
			
			if(rTable.needRemove())
			{
				removeRoleGroupTable(rTable);
			}
		}
	}
	
	protected void afterWriteOnePlayerTable(PlayerTable table)
	{
		if(table.needRemove())
		{
			removePlayerTable(table);
		}
		else
		{
			table.keepTime-=CommonSetting.dbWriteDelay;
			
			if(table.keepTime<=0)
			{
				table.keepTime=0;
				
				if(table.needWrite)
				{
					//出现这种情况是因为断电或卡了
					Ctrl.warnLog("重置角色表刷新时间");
					table.refreshKeepTime();
				}
				else
				{
					removePlayerTable(table);
				}
			}
		}
	}
	
	/** db写入临时数据 */
	protected class DBWriteTempData extends BaseDBWriteTempData
	{
		/** 分派执行是否结束 */
		public boolean isOver=false;
		//player
		
		/** 角色列表数据组 */
		public SList<PlayerWriteTempData> players=new SList<>(PlayerWriteTempData[]::new);
		/** 需要处理的角色数 */
		public int needPlayerNum=0;
		
		public SList<PlayerTable> keepPlayerTables=new SList<>(PlayerTable[]::new);
		
		
		//roleGroup
		/** 玩家群数据 */
		public SList<RoleGroupWriteTempData> roleGroups=new SList<>(RoleGroupWriteTempData[]::new);
		
		@Override
		public void clear()
		{
			super.clear();
			
			players.forEachAndClear(v->
			{
				v.clear();
			});
			
			roleGroups.clear();
			keepPlayerTables.clear();
			needPlayerNum=0;
			isOver=false;
		}
	}
	
	protected class PlayerWriteTempData
	{
		public PlayerTable table;
		
		public Player player;
		/** 列表数据 */
		public PlayerListData listData;
		/** 登录数据 */
		public PlayerLoginData loginData;
		
		public boolean writeOver;
		
		public boolean needOfflineWork;
		
		/** 写入回调(跨线程才有) */
		public PlayerWriteDBFunc func;
		
		public void addTable(PlayerTable table)
		{
			this.table=table;
		}
		
		/** 清空 */
		public void clear()
		{
			if(func!=null)
			{
				func.clear();
				func=null;
			}
		}
	}
	
	protected class RoleGroupWriteTempData
	{
		public RoleGroupTable table;
		/** 数据 */
		public RoleGroupData data;
	}
	
	/** 玩家写DBFunc */
	protected class PlayerWriteDBFunc implements Runnable
	{
		public Player player;
		public DBWriteTempData tempWriteData;
		public PlayerWriteTempData tData;
		public int index;
		
		@Override
		public void run()
		{
			//已失效
			if(player==null)
				return;
			
			toWriteOnePlayer(player,tData);
			
			//切主线程
			ThreadControl.addMainFunc(()->
			{
				//未过期
				if(index==_writeIndex)
				{
					//记录
					if(!tempWriteData.isOver && !tData.writeOver)
					{
						tData.writeOver=true;
						
						--tempWriteData.needPlayerNum;
						
						checkMakeOver(tempWriteData,index);
					}
				}
				else
				{
					Ctrl.errorLog("角色makeTable过期",tData.table.playerID);
				}
			});
		}
		
		/** 失效 */
		public void clear()
		{
			player=null;
			tempWriteData=null;
			tData=null;
		}
	}
}
