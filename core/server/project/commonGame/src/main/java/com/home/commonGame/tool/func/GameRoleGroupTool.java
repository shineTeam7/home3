package com.home.commonGame.tool.func;

import com.home.commonBase.config.game.RoleGroupConfig;
import com.home.commonBase.config.game.enumT.RoleGroupChangeTypeConfig;
import com.home.commonBase.constlist.generate.FuncToolType;
import com.home.commonBase.constlist.generate.RoleGroupChangeType;
import com.home.commonBase.constlist.generate.RoleGroupHandleResultType;
import com.home.commonBase.constlist.generate.RoleGroupMemberInOutType;
import com.home.commonBase.constlist.generate.RoleGroupTitleType;
import com.home.commonBase.constlist.system.GameAreaDivideType;
import com.home.commonBase.data.func.FuncToolData;
import com.home.commonBase.data.func.PlayerFuncWorkData;
import com.home.commonBase.data.social.RoleSocialData;
import com.home.commonBase.data.social.roleGroup.CreateRoleGroupData;
import com.home.commonBase.data.social.roleGroup.RoleGroupChangeData;
import com.home.commonBase.data.social.roleGroup.RoleGroupData;
import com.home.commonBase.data.social.roleGroup.RoleGroupMemberData;
import com.home.commonBase.data.social.roleGroup.RoleGroupSimpleData;
import com.home.commonBase.data.social.roleGroup.RoleGroupToolData;
import com.home.commonBase.data.social.roleGroup.work.CreateRoleGroupResultOWData;
import com.home.commonBase.data.social.roleGroup.work.CreateRoleGroupToCenterWData;
import com.home.commonBase.data.social.roleGroup.work.RemoveRoleGroupToCenterWData;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.table.table.RoleGroupTable;
import com.home.commonBase.table.table.UnionNameTable;
import com.home.commonBase.tool.func.FuncTool;
import com.home.commonBase.utils.BaseGameUtils;
import com.home.commonGame.global.GameC;
import com.home.commonGame.logic.func.RoleGroup;
import com.home.commonGame.net.request.func.roleGroup.FuncSendHandleInviteResultRoleGroupRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncAddRoleGroupSimpleToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncRemoveRoleGroupSimpleToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncSendRoleGroupJoinResultServerRequest;
import com.home.commonGame.part.player.Player;
import com.home.shine.bytes.BytesReadStream;
import com.home.shine.control.ThreadControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.data.DateData;
import com.home.shine.support.collection.IntIntMap;
import com.home.shine.support.collection.LongObjectMap;
import com.home.shine.support.collection.SList;
import com.home.shine.support.collection.StringIntMap;
import com.home.shine.support.func.ObjectCall;
import com.home.shine.support.pool.ObjectPool;

import java.util.Comparator;

/** 玩家群工具 */
public class GameRoleGroupTool extends FuncTool
{
	/** 数据 */
	protected RoleGroupToolData _d;
	/** 配置 */
	protected RoleGroupConfig _config;
	
	protected ObjectPool<RoleGroup> _roleGroupPool;
	
	/** 玩家群字典 */
	protected LongObjectMap<RoleGroup> _roleGroupDic=new LongObjectMap<>(RoleGroup[]::new);
	
	/** 玩家群字典(单个) */
	private LongObjectMap<RoleGroup> _roleGroupSingleByPlayerID=new LongObjectMap<>(RoleGroup[]::new);
	/** 玩家群字典(多个) */
	private LongObjectMap<LongObjectMap<RoleGroup>> _roleGroupMultiByPlayerID=new LongObjectMap<>(LongObjectMap[]::new);
	
	/** 获取玩家群列表 */
	private LongObjectMap<SList<ObjectCall<RoleGroup>>> _getRoleGroupFuncList=new LongObjectMap<>(SList[]::new);
	
	/** 无需存库的index组 */
	private IntIntMap _indexDic=new IntIntMap();
	
	/** 本服简版数据列表 */
	protected LongObjectMap<RoleGroupSimpleData> _localSimpleDic;
	/** 玩家群名字字典(name->数目)(暂时先这么用，回头换字典树) */
	private StringIntMap _nameDic=new StringIntMap();
	
	/** 全服列表脏标记 */
	private boolean _simpleListDirty=true;
	/** 全服缓存简版数据列表(存全服的) */
	protected LongObjectMap<RoleGroupSimpleData> _simpleDic=new LongObjectMap<>(RoleGroupSimpleData[]::new);
	/** 全服缓存简版数据列表(存全服的) */
	protected SList<RoleGroupSimpleData> _simpleList=new SList<>(RoleGroupSimpleData[]::new);
	
	private Comparator<RoleGroupSimpleData> _compareList;
	
	public GameRoleGroupTool(int funcID,int groupID)
	{
		super(FuncToolType.RoleGroup,funcID);
		
		_config=RoleGroupConfig.get(groupID);
		
		_compareList=this::compareSimpleList;
		
		_roleGroupPool=new ObjectPool<>(()->
		{
			RoleGroup roleGroup=toCreateRoleGroup();
			roleGroup.setGroupTool(this);
			roleGroup.construct();
			return roleGroup;
		});
		_roleGroupPool.setEnable(CommonSetting.playerUsePool);
	}
	
	@Override
	protected void toSetData(FuncToolData data)
	{
		super.toSetData(data);
		
		_d=(RoleGroupToolData)data;
	}
	
	@Override
	public void afterReadData()
	{
		super.afterReadData();
		
		_localSimpleDic=_config.needSave ? _d.simpleDatas : new LongObjectMap<>(RoleGroupSimpleData[]::new);
		
		if(_config.needUniqueName)
		{
			RoleGroupSimpleData[] values;
			RoleGroupSimpleData v;
			
			for(int i=(values=_localSimpleDic.getValues()).length-1;i>=0;--i)
			{
				if((v=values[i])!=null)
				{
					_nameDic.addValue(v.name,1);
				}
			}
		}
		
		//先添加了自己
		addGameSimpleInfo(_localSimpleDic,false);
	}
	
	public RoleGroupToolData getData()
	{
		return _d;
	}
	
	@Override
	protected FuncToolData createToolData()
	{
		return new RoleGroupToolData();
	}
	
	@Override
	public void onNewCreate()
	{
		super.onNewCreate();
	}
	
	@Override
	public void onSecond(int delay)
	{
		super.onSecond(delay);
		
		RoleGroup[] values;
		RoleGroup v;
		
		for(int i=(values=_roleGroupDic.getValues()).length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				v.onSecond(delay);
			}
		}
	}
	
	/** 创建玩家群(只创建) */
	protected RoleGroup toCreateRoleGroup()
	{
		return new RoleGroup();
	}
	
	/** 创建玩家群数据(只创建) */
	protected RoleGroupData toCreateRoleGroupData()
	{
		return new RoleGroupData();
	}
	
	/** 创建群创建数据 */
	protected CreateRoleGroupData toCreateCreateRoleGroupData()
	{
		return new CreateRoleGroupData();
	}
	
	/** 获取配置 */
	public RoleGroupConfig getConfig()
	{
		return _config;
	}
	
	/** 是否只本服 */
	public boolean isOnlyLocalGame()
	{
		return CommonSetting.isAreaSplit() && _config.isOnlyLocalGame;
	}
	
	/** 获取一个新群的ID(带了areaID前缀,areaID:原区ID)(如返回-1,则是注册已满) */
	public long getNewGroupID(int areaID)
	{
		if(_config.needSave)
		{
			return GameC.global.func.getNewGroupID(areaID);
		}
		else
		{
			ThreadControl.checkCurrentIsMainThread();
			
			int index=_indexDic.addValue(areaID,1);
			
			if(index>=CommonSetting.areaRegistMax)
			{
				Ctrl.throwError("没有新角色ID了");
				
				_indexDic.put(areaID,index-1);
				
				return -1L;
			}
			
			return BaseGameUtils.makeLogicID(areaID,index);
		}
	}
	
	public RoleGroup addRoleGroupByData(RoleGroupData data)
	{
		RoleGroup roleGroup=_roleGroupPool.getOne();
		
		roleGroup.setGroupTool(this);
		
		//table.data
		roleGroup.setData(data);
		roleGroup.init();
		
		_roleGroupDic.put(roleGroup.groupID,roleGroup);
		
		roleGroup.afterReadData();
		
		roleGroup.beforeLogin();
		
		roleGroup.onLoaded();
		
		return roleGroup;
	}
	
	/** 获取本服简版数据列表 */
	public LongObjectMap<RoleGroupSimpleData> getLocalSimpleDataDic()
	{
		return _d.simpleDatas;
	}
	
	/** 获取玩家群 */
	public RoleGroup getRoleGroup(long groupID)
	{
		return _roleGroupDic.get(groupID);
	}
	
	/** 获取本服玩家群简版数据 */
	public RoleGroupSimpleData getLocalRoleGroupSimpleData(long groupID)
	{
		return _localSimpleDic.get(groupID);
	}
	
	/** 获取玩家群简版信息 */
	public RoleGroupSimpleData getRoleGroupSimpleData(long groupID)
	{
		return _simpleDic.get(groupID);
	}
	
	/** 直接获取玩家群(如未就绪就从数据库加载出来) */
	public void getRoleGroupAbs(long groupID,ObjectCall<RoleGroup> func)
	{
		RoleGroup re;
		if((re=_roleGroupDic.get(groupID))!=null)
		{
			func.apply(re);
			return;
		}
		
		SList<ObjectCall<RoleGroup>> list=_getRoleGroupFuncList.get(groupID);
		
		if(list!=null)
		{
			list.add(func);
			return;
		}
		
		_getRoleGroupFuncList.put(groupID,list=new SList<>(ObjectCall[]::new));
		list.add(func);
		
		GameC.db.loadRoleGroupTable(groupID,table ->
		{
			if(table==null)
			{
				reGetRoleGroupAbs(groupID,null);
				return;
			}
			
			//切IO线程反序列化
			ThreadControl.addIOFunc((int)(table.groupID & ThreadControl.ioThreadNumMark),()->
			{
				RoleGroupData data;
				
				if(table.data==null || table.data.length==0)
				{
					Ctrl.errorLog("玩家群表数据为空,序列化时出现未write就写入的情况",table.groupID);
					data=null;
				}
				else
				{
					data=toCreateRoleGroupData();
					BytesReadStream stream=BytesReadStream.create(table.data);
					data.readBytesFull(stream);
				}
				
				//回主线程
				ThreadControl.addMainFunc(()->
				{
					RoleGroup roleGroup;
					if(data==null)
					{
						roleGroup=null;
					}
					else
					{
						roleGroup=addRoleGroupByData(data);
					}
					
					reGetRoleGroupAbs(groupID,roleGroup);
				});
			});
		});
	}
	
	private void reGetRoleGroupAbs(long groupID,RoleGroup roleGroup)
	{
		SList<ObjectCall<RoleGroup>> list2=_getRoleGroupFuncList.remove(groupID);
		
		if(list2!=null)
		{
			ObjectCall<RoleGroup>[] values=list2.getValues();
			ObjectCall<RoleGroup> v;
			
			for(int i=0,len=list2.size();i<len;++i)
			{
				v=values[i];
				v.apply(roleGroup);
			}
		}
	}
	
	/** 设置本地简版列表改变 */
	protected void setSimpleListDirty(RoleGroupSimpleData data)
	{
		if(!_simpleListDirty)
		{
			_simpleListDirty=true;
			_simpleList.clear();;
		}
	}
	
	/** 简版数据变化 */
	public void onRoleGroupSimpleChange(long groupID,RoleGroupChangeData data)
	{
		RoleGroupSimpleData sData;
		
		if((sData=_simpleDic.get(groupID))!=null)
		{
			sData.onRoleGroupChange(data);
			
			if(RoleGroupChangeTypeConfig.get(data.type).needResort)
			{
				setSimpleListDirty(sData);
			}
			
			GameC.global.func.onRoleGroupSimpleChange(groupID,data);
		}
	}
	
	/** 添加其他服的简版信息 */
	public void addGameSimpleInfo(LongObjectMap<RoleGroupSimpleData> dic,boolean needRemake)
	{
		RoleGroupSimpleData[] values;
		RoleGroupSimpleData v;
		
		for(int i=(values=dic.getValues()).length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				addGameRoleGroupSimpleData(v);
			}
		}
		
		if(needRemake)
		{
			GameC.global.func.remakeRoleGroupRank();
		}
	}
	
	/** 添加玩家群简版数据(全服) */
	public void addGameRoleGroupSimpleData(RoleGroupSimpleData data)
	{
		_simpleDic.put(data.groupID,data);
		setSimpleListDirty(data);
		onAddGameRoleGroupSimpleData(data);
	}
	
	protected void onAddGameRoleGroupSimpleData(RoleGroupSimpleData data)
	{
	
	}
	
	/** 移除玩家群简版数据(全服) */
	public void removeGameRoleGroupSimpleData(long groupID)
	{
		RoleGroupSimpleData data=_simpleDic.remove(groupID);
		
		if(data!=null)
		{
			setSimpleListDirty(data);
			onRemoveGameRoleGroupSimpleData(data);
		}
	}
	
	protected void onRemoveGameRoleGroupSimpleData(RoleGroupSimpleData data)
	{
	
	}
	
	/** 获取本服列表 */
	public SList<RoleGroupSimpleData> getSimpleList()
	{
		if(_simpleListDirty)
		{
			_simpleListDirty=false;
			
			_simpleList.clear();
			
			RoleGroupSimpleData[] values;
			RoleGroupSimpleData v;
			
			for(int i=(values=_simpleDic.getValues()).length-1;i>=0;--i)
			{
				if((v=values[i])!=null)
				{
					_simpleList.add(v);
				}
			}
			
			_simpleList.sort(_compareList);
		}
		
		return _simpleList;
	}
	
	/** 比较简版信息 */
	protected int compareSimpleList(RoleGroupSimpleData arg0,RoleGroupSimpleData arg1)
	{
		return Long.compare(arg0.groupID,arg1.groupID);
	}
	
	//--行为部分--//
	
	/** 某玩家在当前服是否群已满 */
	protected boolean isPlayerRoleGroupFull(long playerID)
	{
		if(_config.isSingleJoin())
		{
			return _roleGroupMultiByPlayerID.get(playerID)!=null;
		}
		else
		{
			LongObjectMap<RoleGroup> dic=_roleGroupMultiByPlayerID.get(playerID);
			
			if(dic==null)
				return false;
			
			return dic.size()>=_config.eachPlayerGroupNum;
		}
	}
	
	/** 添加角色事务 */
	public void addPlayerWork(long playerID,PlayerFuncWorkData data)
	{
		data.funcID=_funcID;
		
		if(_config.needSave)
		{
			GameC.main.addPlayerOfflineWork(playerID,data);
		}
		else
		{
			GameC.main.addPlayerOnlineWork(playerID,data);
		}
	}
	
	/** 检查名字是否可用(不重复)(主线程)(回调也在主线程) */
	public void checkNameAvailable(String name,ObjectCall<Boolean> func)
	{
		boolean suc=_nameDic.get(name)<=0;
		
		if(suc && !_config.isOnlyLocalGame)
		{
			//限定Union
			toCheckNameRepeatDeep(name,func);
			return;
		}
		
		func.apply(suc);
	}
	
	protected void toCheckNameRepeatDeep(String name,ObjectCall<Boolean> func)
	{
	
	}
	
	/** 创建玩家群 */
	public void createRoleGroup(RoleSocialData data,CreateRoleGroupData createData)
	{
		if(!checkCanCreate(data,createData))
		{
			CreateRoleGroupResultOWData wData=new CreateRoleGroupResultOWData();
			wData.success=false;
			addPlayerWork(data.showData.playerID,wData);
			return;
		}
		
		long newGroupID=getNewGroupID(data.showData.createAreaID);
		
		toCreateRoleGroupByLeader(newGroupID,data,createData,roleGroup->
		{
			CreateRoleGroupResultOWData wData=new CreateRoleGroupResultOWData();
			
			if(roleGroup==null)
			{
				wData.success=false;
			}
			else
			{
				wData.success=true;
				
				//需要中心服保存玩家群数据
				if(_config.needCenterSaveRoleGroup)
				{
					RoleGroupSimpleData simpleData=roleGroup.createSimpleData();
					
					CreateRoleGroupToCenterWData wData2=new CreateRoleGroupToCenterWData();
					wData2.funcID=_funcID;
					wData2.simpleData=simpleData;
					
					GameC.main.addCenterWork(wData2);
				}
			}
			
			addPlayerWork(data.showData.playerID,wData);
		});
	}
	
	/** 删除使用名字 */
	public void deleteName(String name)
	{
		if(_config.needUniqueName)
		{
			int re=_nameDic.addValue(name,-1);
			
			if(re!=0)
			{
				Ctrl.errorLog("玩家群名字计数出错");
			}
			
			if(!_config.isOnlyLocalGame)
			{
				UnionNameTable ut=new UnionNameTable();
				ut.name=name;
				ut.delete(GameC.db.getCenterConnect(),null);
			}
		}
	}
	
	/** 使用并插入名字 */
	public void useAndInsertName(String name,long groupID,ObjectCall<Boolean> func)
	{
		boolean suc=true;
		
		if(_config.needUniqueName)
		{
			suc=_nameDic.addValue(name,1)==1;
			
			if(suc && !_config.isOnlyLocalGame)
			{
				UnionNameTable ut=new UnionNameTable();
				ut.name=name;
				ut.groupID=groupID;
				
				ut.insert(GameC.db.getCenterConnect(),b->
				{
					if(b)
					{
						func.apply(true);
					}
					else
					{
						func.apply(false);
					}
				});
				
				return;
			}
		}
		
		func.apply(suc);
	}
	
	/** 通过leader创建一个群 */
	protected void toCreateRoleGroupByLeader(long newGroupID,RoleSocialData data,CreateRoleGroupData createData,ObjectCall<RoleGroup> func)
	{
		useAndInsertName(createData.name,newGroupID,b->
		{
			if(b)
			{
				func.apply(doCreateRoleGroupByLeader(newGroupID,data,createData));
			}
			else
			{
				func.apply(null);
			}
		});
	}
	
	/** 通过leader创建一个群 */
	protected RoleGroup doCreateRoleGroupByLeader(long newGroupID,RoleSocialData data,CreateRoleGroupData createData)
	{
		RoleGroup roleGroup=_roleGroupPool.getOne();
		
		RoleGroupData gData=toCreateRoleGroupData();
		gData.initDefault();
		gData.groupID=newGroupID;
		
		roleGroup.setGroupTool(this);
		roleGroup.setData(gData);
		
		if(_config.needSave)
		{
			//数据
			RoleGroupTable table=BaseC.factory.createRoleGroupTable();
			table.groupID=roleGroup.groupID;
			table.createDate=DateData.getNow();
			table.createAreaID=GameC.app.id;
			table.funcID=_funcID;
			
			GameC.db.insertNewRoleGroup(table);
		}
		
		_roleGroupDic.put(roleGroup.groupID,roleGroup);
		
		roleGroup.init();
		
		roleGroup.onNewCreate(createData);
		roleGroup.afterReadData();
		
		Ctrl.log("创建玩家群",data.showData.playerID,data.showData.name,"groupID:",roleGroup.groupID);
		
		roleGroup.addMember(data,RoleGroupTitleType.Leader,RoleGroupMemberInOutType.Create);
		
		if(_config.needSimpleList)
		{
			RoleGroupSimpleData simpleData=roleGroup.createSimpleData();
			
			_localSimpleDic.put(simpleData.groupID,simpleData);
			
			addGameRoleGroupSimpleData(simpleData);
			
			//把自己的加了
			
			if(!isOnlyLocalGame())
			{
				GameC.server.radioGames(FuncAddRoleGroupSimpleToGameServerRequest.create(_funcID,simpleData));
			}
		}
		
		roleGroup.beforeLogin();
		
		return roleGroup;
	}
	
	/** 检查是否可创建 */
	protected boolean checkCanCreate(RoleSocialData data,CreateRoleGroupData createData)
	{
		return true;
	}
	
	/** 构造默认建群数据 */
	protected void makeDefaultCreateData(CreateRoleGroupData data)
	{
		data.name="";
		data.notice="";
	}
	
	/** 构造默认建群数据 */
	protected CreateRoleGroupData createDefaultCreateData()
	{
		CreateRoleGroupData re=toCreateCreateRoleGroupData();
		makeDefaultCreateData(re);
		return re;
	}
	
	/** 添加到玩家群(只数据) */
	public void addMemberToRoleGroup(RoleGroup group,RoleGroupMemberData mData)
	{
		//单个的
		if(_config.isSingleJoin())
		{
			_roleGroupSingleByPlayerID.put(mData.playerID,group);
		}
		else
		{
			_roleGroupMultiByPlayerID.computeIfAbsent(mData.playerID,k->new LongObjectMap<>(RoleGroup[]::new)).put(group.groupID,group);
		}
	}
	
	/** 从玩家群移除(只数据) */
	public void removeMemberFromRoleGroup(RoleGroup group,RoleGroupMemberData mData)
	{
		//单个的
		if(_config.isSingleJoin())
		{
			_roleGroupSingleByPlayerID.remove(mData.playerID);
		}
		else
		{
			LongObjectMap<RoleGroup> dic=_roleGroupMultiByPlayerID.get(mData.playerID);
			
			if(dic!=null)
			{
				dic.remove(group.groupID);
				
				if(dic.isEmpty())
				{
					_roleGroupMultiByPlayerID.remove(mData.playerID);
				}
			}
		}
	}
	
	/** 移除玩家群 */
	public void removeRoleGroup(long groupID)
	{
		RoleGroup roleGroup=_roleGroupDic.get(groupID);
		
		if(roleGroup==null)
		{
			Ctrl.errorLog("严重错误，删除玩家群时，找不到",groupID);
			return;
		}
		
		if(_config.needSave)
		{
			GameC.db.deleteRoleGroupTable(groupID);
		}
		
		if(_config.needSimpleList)
		{
			_localSimpleDic.remove(groupID);
			
			deleteName(roleGroup.getData().name);
			
			removeGameRoleGroupSimpleData(groupID);
			
			//把自己的加了
			
			if(!isOnlyLocalGame())
			{
				GameC.server.radioGames(FuncRemoveRoleGroupSimpleToGameServerRequest.create(_funcID,groupID));
			}
		}
		
		//其他
		GameC.global.onRoleGroupDelete(roleGroup);
		
		//再删除
		_roleGroupDic.remove(groupID);
		
		roleGroup.dispose();
		
		//需要从中心服移除保存的玩家群数据
		if(_config.needCenterSaveRoleGroup)
		{
			RemoveRoleGroupToCenterWData wData=new RemoveRoleGroupToCenterWData();
			wData.funcID=_funcID;
			wData.groupID=groupID;
			
			GameC.main.addCenterWork(wData);
		}
		
		_roleGroupPool.back(roleGroup);
	}
	
	/** 被邀请成群 */
	public void beInviteCreateRoleGroup(RoleSocialData invitor,RoleSocialData data)
	{
		if(isPlayerRoleGroupFull(data.showData.playerID))
		{
			Ctrl.warnLog("被邀请成群时,自身群已满");
			return;
		}
		
		if(isPlayerRoleGroupFull(invitor.showData.playerID))
		{
			Ctrl.warnLog("被邀请成群时,邀请群已满");
			return;
		}
		
		if(_config.needUniqueName)
		{
			Ctrl.warnLog("玩家群唯一名字配置,被邀请成群时");
			return;
		}
		
		CreateRoleGroupData createData=createDefaultCreateData();
		
		if(!checkCanCreate(data,createData))
		{
			Ctrl.warnLog("被邀请成群时,不满足建群条件");
			return;
		}
		
		//还是先创建群，再依次拉人
		
		long newGroupID=getNewGroupID(data.showData.createAreaID);
		RoleGroup roleGroup=doCreateRoleGroupByLeader(newGroupID,invitor,createData);
		
		roleGroup.addNewMember(data,RoleGroupMemberInOutType.Invite);
		
		FuncSendHandleInviteResultRoleGroupRequest.create(_funcID,data.showData,RoleGroupHandleResultType.Agree).sendToPlayer(invitor.showData.playerID);
	}
	
	/** 绑定翻页显示插件 */
	public void bindPageShowTool(int showMaxNum,int eachPageShowNum)
	{
		GamePageShowTool pageShowTool=new GamePageShowTool(_funcID,showMaxNum,eachPageShowNum);
		pageShowTool.setDataList(_simpleList);
		GameC.global.func.registFuncTool(pageShowTool);
	}
	
	/** 申请加入玩家群结果组 */
	public void beApplyJoin(long groupID,RoleSocialData data)
	{
		getRoleGroupAbs(groupID,roleGroup->
		{
			if(roleGroup!=null)
			{
				roleGroup.beApplyJoin(data);
			}
			else
			{
				FuncSendRoleGroupJoinResultServerRequest.create(_funcID,groupID,false).sendToPlayer(data.showData.playerID);
			}
		});
	}
	
	/** 被邀请进入 */
	public void beInviteJoin(long groupID,long invitorID,RoleSocialData data)
	{
		getRoleGroupAbs(groupID,roleGroup->
		{
			if(roleGroup!=null)
			{
				roleGroup.beInviteJoin(invitorID,data);
			}
			else
			{
				FuncSendRoleGroupJoinResultServerRequest.create(_funcID,groupID,false).sendToPlayer(data.showData.playerID);
			}
		});
	}
}
