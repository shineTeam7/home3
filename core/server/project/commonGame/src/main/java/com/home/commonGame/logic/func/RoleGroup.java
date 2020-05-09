package com.home.commonGame.logic.func;

import com.home.commonBase.config.game.InfoLogConfig;
import com.home.commonBase.config.game.RoleGroupConfig;
import com.home.commonBase.config.game.RoleGroupLevelConfig;
import com.home.commonBase.config.game.RoleGroupTitleConfig;
import com.home.commonBase.config.game.enumT.RoleGroupChangeTypeConfig;
import com.home.commonBase.constlist.generate.FuncToolType;
import com.home.commonBase.constlist.generate.InfoCodeType;
import com.home.commonBase.constlist.generate.InfoLogType;
import com.home.commonBase.constlist.generate.RoleGroupChangeType;
import com.home.commonBase.constlist.generate.RoleGroupHandleResultType;
import com.home.commonBase.constlist.generate.RoleGroupMemberInOutType;
import com.home.commonBase.constlist.generate.RoleGroupTitleType;
import com.home.commonBase.data.func.FuncToolData;
import com.home.commonBase.data.role.RoleShowChangeData;
import com.home.commonBase.data.role.RoleShowData;
import com.home.commonBase.data.scene.scene.SceneLocationData;
import com.home.commonBase.data.social.RoleSocialData;
import com.home.commonBase.data.social.base.RoleShowLogData;
import com.home.commonBase.data.social.rank.RankSimpleData;
import com.home.commonBase.data.social.roleGroup.CreateRoleGroupData;
import com.home.commonBase.data.social.roleGroup.PlayerApplyRoleGroupData;
import com.home.commonBase.data.social.roleGroup.PlayerApplyRoleGroupSelfData;
import com.home.commonBase.data.social.roleGroup.PlayerRoleGroupData;
import com.home.commonBase.data.social.roleGroup.PlayerRoleGroupMemberData;
import com.home.commonBase.data.social.roleGroup.PlayerRoleGroupSaveData;
import com.home.commonBase.data.social.roleGroup.RoleGroupChangeData;
import com.home.commonBase.data.social.roleGroup.RoleGroupCreateSceneData;
import com.home.commonBase.data.social.roleGroup.RoleGroupData;
import com.home.commonBase.data.social.roleGroup.RoleGroupMemberChangeData;
import com.home.commonBase.data.social.roleGroup.RoleGroupMemberData;
import com.home.commonBase.data.social.roleGroup.RoleGroupSimpleData;
import com.home.commonBase.data.social.roleGroup.work.AddApplyRoleGroupSelfWData;
import com.home.commonBase.data.social.roleGroup.work.HandleApplyRoleGroupWData;
import com.home.commonBase.data.social.roleGroup.work.JoinRoleGroupWData;
import com.home.commonBase.data.social.roleGroup.work.LeaveRoleGroupWData;
import com.home.commonBase.data.system.InfoLogData;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.tool.func.FuncTool;
import com.home.commonBase.tool.func.RankTool;
import com.home.commonBase.utils.BaseGameUtils;
import com.home.commonGame.global.GameC;
import com.home.commonGame.net.base.GameRequest;
import com.home.commonGame.net.request.func.roleGroup.FuncSendAddApplyRoleGroupRequest;
import com.home.commonGame.net.request.func.roleGroup.FuncSendHandleApplyResultToMemberRequest;
import com.home.commonGame.net.request.func.roleGroup.FuncSendHandleInviteResultRoleGroupRequest;
import com.home.commonGame.net.request.func.roleGroup.FuncSendRoleGroupInfoLogRequest;
import com.home.commonGame.net.serverRequest.center.social.CommitRoleGroupToCenterServerRequest;
import com.home.commonGame.net.serverRequest.center.social.FuncRoleGroupChangeSimpleToCenterServerRequest;
import com.home.commonGame.net.serverRequest.game.base.PlayerGameToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.base.SignedSceneGameToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncRefreshTitleRoleGroupToPlayerServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncRoleGroupChangeSimpleToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncRoleGroupChangeToPlayerServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncRoleGroupMemberChangeToPlayerServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncRoleGroupReEnterOwnSceneArgToPlayerServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncRoleGroupRefreshRoleShowDataToPlayerServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncSendChangeLeaderRoleGroupToPlayerServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncSendRoleGroupAddMemberToPlayerServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncSendRoleGroupJoinResultServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncSendRoleGroupRemoveMemberToPlayerServerRequest;
import com.home.commonGame.tool.func.GameRoleGroupTool;
import com.home.commonGame.tool.func.IRoleGroupFuncTool;
import com.home.commonGame.tool.func.RoleGroupRankTool;
import com.home.shine.control.DateControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.collection.IntIntMap;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.LongObjectMap;
import com.home.shine.support.collection.SList;
import com.home.shine.support.collection.SQueue;
import com.home.shine.support.pool.StringBuilderPool;
import com.home.shine.tool.AsyncRunnableTool;
import com.home.shine.utils.OtherUtils;
import com.home.shine.utils.StringUtils;
import com.home.shine.utils.TimeUtils;

import java.util.Comparator;

/** 玩家群逻辑体 */
public class RoleGroup
{
	/** 群ID */
	public long groupID;
	/** 创建区id */
	public int createAreaID;
	
	protected int _funcID;
	
	/** 配置 */
	protected RoleGroupConfig _config;
	/** 工具 */
	protected GameRoleGroupTool _tool;
	
	/** 玩家群数据(单个群) */
	protected RoleGroupData _d;
	
	/** 成员组 */
	protected LongObjectMap<RoleGroupMemberData> _members;
	/** 可处理申请的成员组 */
	private LongObjectMap<RoleGroupMemberData> _handleApplyMemberDic=new LongObjectMap<>(RoleGroupMemberData[]::new);
	
	protected RoleGroupLevelConfig _levelConfig;
	
	/** 职位数目统计 */
	protected IntIntMap _memberTitleCount=new IntIntMap();
	/** 群主 */
	protected RoleGroupMemberData _leader;
	
	/** 场景进入参数数据(如有，则代表场景已创建好) */
	protected SceneLocationData _ownSceneLocation;
	
	/** 创建场景工具 */
	private AsyncRunnableTool _createSceneATool;
	
	private Comparator<PlayerApplyRoleGroupData> _applyComparator;
	
	//funcTools
	
	/** 功能插件字典 */
	private IntObjectMap<FuncTool>[] _funcToolDic=new IntObjectMap[FuncToolType.size];
	/** 功能插件列表 */
	private SList<FuncTool> _funcToolList=new SList<>(FuncTool[]::new);
	/** 功能插件列表 */
	private SList<IRoleGroupFuncTool> _roleGroupFuncToolList=new SList<>(IRoleGroupFuncTool[]::new);
	
	public RoleGroup()
	{
		_applyComparator=this::compareApply;
		
		_createSceneATool=new AsyncRunnableTool()
		{
			@Override
			protected void asyncDo()
			{
				RoleGroupCreateSceneData data=toCreateCreateSceneData();
				data.sceneID=_config.ownSceneID;
				makeCreateSceneData(data);
				
				//执行创建
				GameC.scene.createMultiInstanceScene(data,eData->
				{
					_ownSceneLocation=eData;
					
					_createSceneATool.complete();
				});
			}
		};
	}
	
	/** 获取描述信息 */
	public String getInfo()
	{
		StringBuilder sb=StringBuilderPool.create();
		writeInfo(sb);
		return StringBuilderPool.releaseStr(sb);
	}
	
	/** 写描述信息 */
	public void writeInfo(StringBuilder sb)
	{
		sb.append("groupID:");
		sb.append(groupID);
		sb.append(" name:");
		sb.append(_d.name);
	}
	
	/** 普通日志 */
	public void log(String str)
	{
		StringBuilder sb=StringBuilderPool.create();
		sb.append(str);
		sb.append(' ');
		writeInfo(sb);
		
		Ctrl.log(StringBuilderPool.releaseStr(sb));
	}
	
	/** 普通日志 */
	public void log(Object...args)
	{
		log(StringUtils.objectsToString(args));
	}
	
	/** 调试日志 */
	public void debugLog(String str)
	{
		if(!ShineSetting.needDebugLog)
			return;
		
		StringBuilder sb=StringBuilderPool.create();
		sb.append(str);
		sb.append(' ');
		writeInfo(sb);
		
		Ctrl.debugLog(StringBuilderPool.releaseStr(sb));
	}
	
	/** 调试日志 */
	public void debugLog(Object...args)
	{
		if(!ShineSetting.needDebugLog)
			return;
		
		debugLog(StringUtils.objectsToString(args));
	}
	
	/** 警告日志 */
	public void warnLog(String str)
	{
		StringBuilder sb=StringBuilderPool.create();
		sb.append(str);
		sb.append(' ');
		writeInfo(sb);
		
		Ctrl.warnLog(StringBuilderPool.releaseStr(sb));
	}
	
	/** 警告日志 */
	public void warnLog(Object...args)
	{
		warnLog(StringUtils.objectsToString(args));
	}
	
	/** 错误日志 */
	public void errorLog(String str)
	{
		StringBuilder sb=StringBuilderPool.create();
		sb.append(str);
		sb.append(' ');
		writeInfo(sb);
		
		Ctrl.errorLog(StringBuilderPool.releaseStr(sb));
	}
	
	/** 错误日志输出错误 */
	public void errorLog(Exception e)
	{
		errorLog(Ctrl.exceptionToString(e));
	}
	
	/** 错误日志 */
	public void errorLog(Object...args)
	{
		errorLog(StringUtils.objectsToString(args));
	}
	
	/** 创建成员数据(只创建) */
	protected RoleGroupMemberData toCreateRoleGroupMemberData()
	{
		return new RoleGroupMemberData();
	}
	
	/** 创建客户端成员数据(只创建) */
	protected PlayerRoleGroupMemberData toCreatePlayerRoleGroupMemberData()
	{
		return new PlayerRoleGroupMemberData();
	}
	
	/** 创建客户端玩家群数据(只创建) */
	protected PlayerRoleGroupData toCreatePlayerRoleGroupData()
	{
		return new PlayerRoleGroupData();
	}
	
	/** 创建公会简版数据 */
	protected RoleGroupSimpleData toCreateRoleGroupSimpleData()
	{
		return new RoleGroupSimpleData();
	}
	
	/** 创建构建场景数据 */
	protected RoleGroupCreateSceneData toCreateCreateSceneData()
	{
		return new RoleGroupCreateSceneData();
	}
	
	/** 构造 */
	public void construct()
	{
	
	}
	
	/** 初始化 */
	public void init()
	{
		SList<FuncTool> list;
		FuncTool[] values=(list=_funcToolList).getValues();
		
		for(int i=list.size()-1;i>=0;--i)
		{
			values[i].init();
		}
	}
	
	/** 析构 */
	public void dispose()
	{
		_createSceneATool.clear();
		_handleApplyMemberDic.clear();
		_memberTitleCount.clear();
		_leader=null;
		
		SList<FuncTool> list;
		FuncTool[] values=(list=_funcToolList).getValues();
		FuncTool v;
		
		//倒序+删除
		for(int i=list.size()-1;i>=0;--i)
		{
			(v=values[i]).dispose();
			
			if(v.isAdded)
			{
				//TODO:remove
				//removeFuncTool(v);
			}
		}
	}
	
	public int getFuncID()
	{
		return _funcID;
	}
	
	public void setGroupTool(GameRoleGroupTool tool)
	{
		_tool=tool;
		_funcID=tool.getFuncID();
		_config=tool.getConfig();
	}
	
	/** 设置数据 */
	public void setData(RoleGroupData data)
	{
		_d=data;
		_members=data.members;
		groupID=data.groupID;
		createAreaID= BaseC.logic.getAreaIDByLogicID(groupID);
	}
	
	/** 获取玩家群数据 */
	public RoleGroupData getData()
	{
		return _d;
	}
	
	/** 读数据后 */
	public void afterReadData()
	{
		_levelConfig=RoleGroupLevelConfig.get(_config.id,_d.level);
		
		_members.forEachValue(v->
		{
			if(v.title==RoleGroupTitleType.Leader)
			{
				_leader=v;
			}
			
			if(RoleGroupTitleType.canOperateApply(v.title))
			{
				_handleApplyMemberDic.put(v.playerID,v);
			}
		});
		
		//funcTools
		
		//fix
		if(_d.funcTools==null)
			_d.funcTools=new IntObjectMap<>(IntObjectMap[]::new);
		
		SList<FuncTool> list;
		FuncTool[] values2=(list=_funcToolList).getValues();
		FuncTool v2;
		
		IntObjectMap<FuncToolData> dic;
		FuncToolData data;
		FuncToolData data2;
		
		for(int i=list.size()-1;i>=0;--i)
		{
			v2=values2[i];
			
			if(v2.isAdded)
			{
				Ctrl.throwError("不该有临时添加的FuncTool");
			}
			
			data=(dic=_d.funcTools.get(v2.getType()))!=null ? dic.get(v2.getFuncID()) : null;
			
			v2.setData(data);
			
			//之前没有数据,后来有了数据(说明是新增的)
			if(data==null && (data2=v2.getData())!=null)
			{
				_d.funcTools.computeIfAbsent(v2.getType(),k->new IntObjectMap<>(FuncToolData[]::new)).put(v2.getFuncID(),data2);
			}
		}
		
		for(int i=list.size()-1;i>=0;--i)
		{
			values2[i].afterReadData();
		}
		
		for(int i=list.size()-1;i>=0;--i)
		{
			values2[i].afterReadDataSecond();
		}
		
		//TODO:没完
		
		afterReadDataSecond();
	}
	
	private void afterReadDataSecond()
	{
		SList<IRoleGroupFuncTool> list;
		IRoleGroupFuncTool[] values=(list=_roleGroupFuncToolList).getValues();
		
		FuncTool[] values2=(_funcToolList).getValues();
		
		for(int i=list.size()-1;i>=0;--i)
		{
			//自行添加的不再执行
			if(!values2[i].isAdded)
			{
				values[i].afterReadDataSecond();
			}
		}
	}
	
	/** 读数据后,登录前 */
	public void beforeLogin()
	{
		SList<IRoleGroupFuncTool> list;
		IRoleGroupFuncTool[] values=(list=_roleGroupFuncToolList).getValues();
		
		for(int i=list.size()-1;i>=0;--i)
		{
			values[i].beforeLogin();
		}
	}
	
	/** 读数据后(创建没有) */
	public void onLoaded()
	{
		if(_config.needCenterSaveRoleGroup)
		{
			CommitRoleGroupToCenterServerRequest.create(_funcID,createSimpleData()).send();
		}
	}
	
	public long getTimeMillis()
	{
		return DateControl.getTimeMillis();
	}
	
	/** 每秒 */
	public void onSecond(int delay)
	{
		if(!_d.applyDic.isEmpty())
		{
			long tt=DateControl.getTimeMillis()-_config.applyEnableTime*1000;
			
			_d.applyDic.forEachValueS(v->
			{
				if(tt>v.time)
				{
					_d.applyDic.remove(v.data.showData.playerID);
				}
			});
		}
		
		FuncTool[] values=_funcToolList.getValues();
		
		for(int i=0,len=_funcToolList.size();i<len;++i)
		{
			values[i].onSecond(delay);
		}
		
		checkDaily();
	}
	
	/** 检查是否到了每天间隔 */
	public void checkDaily()
	{
		if(getTimeMillis()>=_d.nextDailyTime)
		{
			long nextDailyTimeT=DateControl.getNextDailyTime();
			
			if(_d.nextDailyTime!=nextDailyTimeT)
			{
				_d.nextDailyTime=nextDailyTimeT;
				
				//每天调用
				onDaily();
			}
		}
	}
	
	/** 每日 */
	protected void onDaily()
	{
		SQueue<InfoLogData> queue;
		if(!(queue=_d.logQueue).isEmpty())
		{
			long removeTime=DateControl.getTimeMillis()-(_config.infoLogKeepTime* TimeUtils.dayTime);
			
			while(true)
			{
				InfoLogData head=queue.peek();
				
				if(head==null)
					break;
				
				if(head.logTime>removeTime)
					break;
				
				queue.poll();
			}
		}
	}
	
	/** 成员数目 */
	public int getMemberNum()
	{
		return _members.size();
	}
	
	/** 是否人满 */
	public boolean isFull()
	{
		return _levelConfig.maxNum>0 && getMemberNum()>=getAllowAddMemberNum();
	}
	
	/** 获取允许添加成员数 */
	protected int getAllowAddMemberNum()
	{
		return _levelConfig.maxNum;
	}
	
	/** 获取群主 */
	public RoleGroupMemberData getLeader()
	{
		return _leader;
	}
	
	/** 新创建时 */
	public void onNewCreate(CreateRoleGroupData createData)
	{
		_d.level=1;//1级
		_d.name=createData.name;
		_d.notice=createData.notice;
		_d.canApplyInAbs=_config.defaultCanApplyInAbs;
		
		_levelConfig=RoleGroupLevelConfig.get(_config.id,_d.level);
	}
	
	/** 获取成员数据 */
	public RoleGroupMemberData getMember(long playerID)
	{
		return _d.members.get(playerID);
	}
	
	/** 是否有成员 */
	public boolean hasMember(long playerID)
	{
		return getMember(playerID)!=null;
	}
	
	/** 构造简版数据 */
	protected void makeSimpleData(RoleGroupSimpleData data)
	{
		data.key=groupID;
		data.groupID=groupID;
		data.level=_d.level;
		data.name=_d.name;
		data.notice=_d.notice;
		data.memberNum=_members.size();
	}
	
	/** 创建简版数据 */
	public RoleGroupSimpleData createSimpleData()
	{
		RoleGroupSimpleData re=toCreateRoleGroupSimpleData();
		makeSimpleData(re);
		return re;
	}
	
	/** 广播成员组 */
	public void radioMembers(PlayerGameToGameServerRequest request)
	{
		if(_members.isEmpty())
			return;
		
		_members.forEachValue(v->
		{
			//在线才广播
			if(v.socialData.isOnline)
			{
				request.sendToPlayer(v.playerID);
			}
		});
	}
	
	/** 广播成员组 */
	protected void radioMembers(GameRequest request)
	{
		if(_members.isEmpty())
			return;
		
		_members.forEachValue(v->
		{
			//在线才广播
			if(v.socialData.isOnline)
			{
				request.sendToPlayer(v.playerID);
			}
		});
	}
	
	/** 广播可操作申请成员组 */
	protected void radioHandleApplyMembersRequest(GameRequest request)
	{
		if(_handleApplyMemberDic.isEmpty())
			return;
		
		_handleApplyMemberDic.forEachValue(v->
		{
			if(v.socialData.isOnline)
			{
				request.sendToPlayer(v.playerID);
			}
		});
	}
	
	/** 广播可操作申请成员组 */
	protected void radioHandleApplyMembersServerRequest(PlayerGameToGameServerRequest request)
	{
		if(_handleApplyMemberDic.isEmpty())
			return;
		
		_handleApplyMemberDic.forEachValue(v->
		{
			request.sendToPlayer(v.playerID);
		});
	}
	
	/** 添加新成员 */
	public void addNewMember(RoleSocialData data,int type)
	{
		addMember(data,_config.joinTitle,type);
		
		//玩家群日志
		RoleShowLogData logData=new RoleShowLogData();
		logData.id=InfoLogType.RoleGroupAddMember;
		logData.showData=(RoleShowData)data.showData.clone();
		addInfoLog(logData);
	}
	
	/** 成员加入 */
	protected void onAddMember(RoleGroupMemberData mData)
	{
	
	}
	
	/** 成员移除 */
	protected void onRemoveMember(RoleGroupMemberData mData)
	{
	
	}
	
	/** 添加成员 */
	public void addMember(RoleSocialData data,int title,int type)
	{
		RoleGroupMemberData mData=createMember(data,title);
		//添加
		
		if(!_members.isEmpty())
		{
			radioMembers(FuncSendRoleGroupAddMemberToPlayerServerRequest.create(_funcID,groupID,createPlayerMemberData(mData),type));
		}
		
		_members.put(mData.playerID,mData);
		_memberTitleCount.addValue(mData.title,1);
		
		if(mData.title==RoleGroupTitleType.Leader)
		{
			_leader=mData;
		}
		
		if(RoleGroupTitleType.canOperateApply(mData.title))
		{
			_handleApplyMemberDic.put(mData.playerID,mData);
		}
		
		_tool.addMemberToRoleGroup(this,mData);
		
		//成员数变更
		doChange(RoleGroupChangeType.MemberNum,_members.size());
		
		PlayerRoleGroupData roleGroupData=createPlayerRoleGroupData(data.showData.playerID);
		
		JoinRoleGroupWData wData=new JoinRoleGroupWData();
		wData.data=roleGroupData;
		wData.type=type;
		
		_tool.addPlayerWork(mData.playerID,wData);
	}
	
	/** 移除成员 */
	public void removeMember(RoleGroupMemberData mData,int type)
	{
		boolean needSend=type!=RoleGroupMemberInOutType.Disband;
		
		long playerID=mData.playerID;
		
		onRemoveMember(mData);
		
		_members.remove(playerID);
		_memberTitleCount.addValue(mData.title,-1);
		
		if(mData.title==RoleGroupTitleType.Leader)
		{
			_leader=null;
		}
		
		if(RoleGroupTitleType.canOperateApply(mData.title))
		{
			_handleApplyMemberDic.remove(mData.playerID);
		}
		
		//成员数变更
		doChange(RoleGroupChangeType.MemberNum,_members.size());
		
		_tool.removeMemberFromRoleGroup(this,mData);
		
		LeaveRoleGroupWData wData=new LeaveRoleGroupWData();
		wData.groupID=groupID;
		wData.type=type;
		_tool.addPlayerWork(playerID,wData);
		
		if(needSend)
		{
			if(!_members.isEmpty())
			{
				radioMembers(FuncSendRoleGroupRemoveMemberToPlayerServerRequest.create(_funcID,groupID,playerID,type));
			}
		}
	}
	
	/** 创建成员数据 */
	protected RoleGroupMemberData createMember(RoleSocialData data,int title)
	{
		RoleGroupMemberData mData=toCreateRoleGroupMemberData();
		mData.initDefault();
		mData.playerID=data.showData.playerID;
		mData.socialData=data;
		mData.title=title;
		mData.joinTime=GameC.global.getTimeMillis();
		
		return mData;
	}
	
	/** 更新玩家存库数据 */
	public void updateRoleSaveData(RoleSocialData sData,PlayerRoleGroupSaveData data)
	{
		RoleGroupMemberData member=getMember(sData.showData.playerID);
		
		if(member!=null)
		{
			member.socialData=(RoleSocialData)sData.clone();
		}
	}
	
	/** 构造客户端玩家群数据 */
	protected void makePlayerRoleGroupData(PlayerRoleGroupData re,long playerID)
	{
		RoleGroupMemberData selfData=getMember(playerID);
		
		re.groupID=groupID;
		re.level=_d.level;
		re.name=_d.name;
		re.notice=_d.notice;
		
		re.members.ensureCapacity(_d.members.size());
		
		_d.members.forEachValue(v->
		{
			re.members.put(v.playerID,createPlayerMemberData(v));
		});
		
		re.memberNum=_d.members.size();
		re.canApplyInAbs=_d.canApplyInAbs;
		re.exp=_d.exp;
		re.logQueue=_d.logQueue;
		
		if(RoleGroupTitleType.canOperateApply(selfData.title))
		{
			re.applyDic=_d.applyDic;
		}
		
		//funcTools
		
		//先clone一下
		re.funcTools=_d.funcTools.clone();
		
		IntObjectMap<FuncTool> rankDic=getFuncToolDic(FuncToolType.Rank);
		
		IntObjectMap<FuncToolData> dic;
		
		if(!rankDic.isEmpty())
		{
			dic=new IntObjectMap<>();
			
			getFuncToolDic(FuncToolType.Rank).forEachValue(v3->
			{
				RoleGroupRankTool v2=(RoleGroupRankTool)v3;
				RankSimpleData tempData=new RankSimpleData();
				tempData.funcID=v2.getFuncID();
				tempData.rank=v2.getRank();
				tempData.value=v2.getData().value;
				dic.put(v2.getFuncID(),tempData);
			});
			
			re.funcTools.put(FuncToolType.Rank,dic);
		}
	}
	
	protected PlayerRoleGroupMemberData createPlayerMemberData(RoleGroupMemberData data)
	{
		PlayerRoleGroupMemberData re=toCreatePlayerRoleGroupMemberData();
		makePlayerMemberData(data,re);
		return re;
	}
	
	/** 构造客户端成员数据(data->mData) */
	protected void makePlayerMemberData(RoleGroupMemberData data,PlayerRoleGroupMemberData re)
	{
		re.playerID=data.playerID;
		re.socialData=data.socialData;
		re.title=data.title;
	}
	
	/** 创建客户端玩家群数据 */
	public PlayerRoleGroupData createPlayerRoleGroupData(long playerID)
	{
		PlayerRoleGroupData re=toCreatePlayerRoleGroupData();
		re.initDefault();
		makePlayerRoleGroupData(re,playerID);
		return re;
	}
	
	/** 设置职位 */
	protected void doSetTitle(RoleGroupMemberData member,int title,boolean needSend)
	{
		_memberTitleCount.addValue(member.title,-1);
		if(member.title==RoleGroupTitleType.Leader)
			_leader=null;
		if(RoleGroupTitleType.canOperateApply(member.title))
			_handleApplyMemberDic.remove(member.playerID);
		
		member.title=title;
		
		_memberTitleCount.addValue(member.title,1);
		if(member.title==RoleGroupTitleType.Leader)
			_leader=member;
		if(RoleGroupTitleType.canOperateApply(member.title))
			_handleApplyMemberDic.put(member.playerID,member);
		
		if(needSend)
		{
			radioMembers(FuncRefreshTitleRoleGroupToPlayerServerRequest.create(_funcID,groupID,member.playerID,title));
		}
	}
	
	/** 自动寻找下一个leader */
	protected RoleGroupMemberData findNextLeader()
	{
		RoleGroupMemberData re=null;
		
		RoleGroupMemberData[] values;
		RoleGroupMemberData v;
		
		for(int i=(values=_members.getValues()).length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				if(v.title!=RoleGroupTitleType.Leader)
				{
					if(re==null || compareLeader(v,re)<0)
					{
						re=v;
					}
				}
			}
		}
		
		return re;
	}
	
	/** 比较leader */
	protected int compareLeader(RoleGroupMemberData arg0,RoleGroupMemberData arg1)
	{
		int re;
		if((re=Integer.compare(arg0.title,arg1.title))!=0)
			return re;
		
		return Long.compare(arg0.joinTime,arg1.joinTime);
	}
	
	protected void doChangeLeader(RoleGroupMemberData leader,RoleGroupMemberData nextLeader)
	{
		doSetTitle(leader,_config.joinTitle,false);
		doSetTitle(nextLeader,RoleGroupTitleType.Leader,false);
		
		radioMembers(FuncSendChangeLeaderRoleGroupToPlayerServerRequest.create(_funcID,groupID,leader.playerID,nextLeader.playerID));
	}
	
	protected void onChangeData(RoleGroupChangeData data)
	{
	
	}
	
	/** 推送改变数据 */
	protected void doChangeData(RoleGroupChangeData data)
	{
		RoleGroupChangeTypeConfig config=RoleGroupChangeTypeConfig.get(data.type);
		
		if(config.isSimpleNeed && _config.needSimpleList)
		{
			_tool.onRoleGroupSimpleChange(groupID,data);
			
			//不止本服
			if(!_tool.isOnlyLocalGame())
			{
				GameC.server.radioGames(FuncRoleGroupChangeSimpleToGameServerRequest.create(_funcID,groupID,data));
			}
			
			//中心服需要
			if(_config.needCenterSaveRoleGroup)
			{
				FuncRoleGroupChangeSimpleToCenterServerRequest.create(_funcID,groupID,data).send();
			}
		}
		
		if(!config.onlySimpleNeed)
		{
			radioMembers(FuncRoleGroupChangeToPlayerServerRequest.create(_funcID,groupID,data));
		}
		
		onChangeData(data);
	}
	
	/** 执行群更改 */
	protected void doChange(int type,String arg)
	{
		RoleGroupChangeData cData=new RoleGroupChangeData();
		cData.type=type;
		cData.arg1=arg;
		
		doChangeData(cData);
	}
	
	/** 执行群更改 */
	protected void doChange(int type,int arg)
	{
		RoleGroupChangeData cData=new RoleGroupChangeData();
		cData.type=type;
		cData.arg0=arg;
		
		doChangeData(cData);
	}
	
	/** 执行群更改 */
	protected void doChange(int type,long arg)
	{
		RoleGroupChangeData cData=new RoleGroupChangeData();
		cData.type=type;
		cData.arg2=arg;
		
		doChangeData(cData);
	}
	
	
	protected int compareApply(PlayerApplyRoleGroupData arg0,PlayerApplyRoleGroupData arg1)
	{
		return Long.compare(arg0.time,arg1.time);
	}
	
	/** 更新社交数据 */
	public void onRefreshRoleSocialData(long playerID,RoleShowChangeData data)
	{
		RoleGroupMemberData member=getMember(playerID);
		
		if(member!=null)
		{
			member.socialData.onChange(data);
			
			radioMembers(FuncRoleGroupRefreshRoleShowDataToPlayerServerRequest.create(_funcID,groupID,playerID,data));
		}
	}
	
	/** 更改成员数据 */
	public void doChangeMember(long memberID,int type,int arg)
	{
		RoleGroupMemberChangeData cData=new RoleGroupMemberChangeData();
		cData.type=type;
		cData.arg0=arg;
		
		doChangeMemberData(memberID,cData);
	}
	
	protected void doChangeMemberData(long memberID,RoleGroupMemberChangeData data)
	{
		radioMembers(FuncRoleGroupMemberChangeToPlayerServerRequest.create(_funcID,groupID,memberID,data));
	}
	
	//行为
	
	/** 解散 */
	public void disband()
	{
		_members.forEachValueS(v->
		{
			removeMember(v,RoleGroupMemberInOutType.Disband);
		});
		
		_tool.removeRoleGroup(groupID);
	}
	
	/** 玩家主动离开 */
	public void playerLeave(long playerID)
	{
		RoleGroupMemberData member=getMember(playerID);
		
		if(member==null)
		{
			warnLog("玩家主动离开玩家群时,找不到玩家",playerID);
			return;
		}
		
		//是队长,并且还有人选
		if(member.title==RoleGroupTitleType.Leader && getMemberNum()>1)
		{
			RoleGroupMemberData nextLeader=findNextLeader();
			
			if(nextLeader==null)
			{
				errorLog("不该找不到下个队长");
			}
			else
			{
				doChangeLeader(member,nextLeader);
			}
		}
		
		removeMember(member,RoleGroupMemberInOutType.Leave);
		
		//玩家群日志
		RoleShowLogData logData=new RoleShowLogData();
		logData.id=InfoLogType.RoleGroupMemberLeave;
		logData.showData=(RoleShowData)member.socialData.showData.clone();
		addInfoLog(logData);
		
		//没人了
		if(getMemberNum()==0)
		{
			_tool.removeRoleGroup(groupID);
			return;
		}
		
		if(_config.removeWithOnlyOne && getMemberNum()==1)
		{
			_members.forEachValueS(v->
			{
				removeMember(v,RoleGroupMemberInOutType.Disband);
			});
			
			_tool.removeRoleGroup(groupID);
			return;
		}
	}
	
	/** 禅让群主 */
	public void changeLeader(long selfID,long targetID)
	{
		RoleGroupMemberData leader=getMember(selfID);
		
		if(leader==null)
		{
			warnLog("禅让群主时,找不到角色",selfID);
			return;
		}
		
		//不是群主
		if(leader.title!=RoleGroupTitleType.Leader)
		{
			warnLog("禅让群主时,不是群主",selfID);
			return;
		}
		
		RoleGroupMemberData target=getMember(targetID);
		
		if(target==null)
		{
			warnLog("禅让群主时,找不到目标",selfID);
			return;
		}
		
		doChangeLeader(leader,target);
	}
	
	/** 设置职位 */
	public void setTitle(long operatorID,long targetID,int title)
	{
		RoleGroupMemberData operator=getMember(operatorID);
		
		if(operator==null)
		{
			warnLog("设置职位时,操作者不存在",operatorID);
			return;
		}
		
		RoleGroupMemberData target=getMember(targetID);
		
		if(target==null)
		{
			warnLog("设置职位时,目标不存在",targetID);
			return;
		}
		
		int canSetTitle=RoleGroupTitleConfig.get(operator.title).canSetTitle;
		
		if(canSetTitle<=0 || canSetTitle>title)
		{
			warnLog("设置职位时,没有权限",operator.title,title);
			return;
		}
		
		if(target.title==RoleGroupTitleType.Leader || title==RoleGroupTitleType.Leader)
		{
			warnLog("设置职位时,不能设置队长",target.title,title);
			return;
		}
		
		if(target.title<canSetTitle)
		{
			warnLog("设置职位时,没有权限2",operator.title,target.title);
			return;
		}
		
		if(target.title==title)
		{
			warnLog("设置职位时,当前已是该职位",operator.title,target.title);
			return;
		}
		
		int num=RoleGroupTitleConfig.get(title).num;
		
		//超过数目
		if(num>0 && _memberTitleCount.get(title)>=num)
		{
			warnLog("设置职位时,超过容纳数目",title);
			return;
		}
		
		doSetTitle(target,title,true);
		
		//玩家群日志
		InfoLogData data=new InfoLogData();
		data.id=InfoLogType.RoleGroupSetTitle;
		data.args=new String[]{operator.socialData.showData.name,target.socialData.showData.name,title+""};
		addInfoLog(data);
	}
	
	/** 踢出成员 */
	public void kickMember(long operatorID,long targetID)
	{
		RoleGroupMemberData operator=getMember(operatorID);
		
		if(operator==null)
		{
			warnLog("踢出成员时,操作者不存在",operatorID);
			return;
		}
		
		RoleGroupMemberData target=getMember(targetID);
		
		if(target==null)
		{
			warnLog("踢出成员时,目标不存在",targetID);
			return;
		}
		
		int canKickTitle=RoleGroupTitleConfig.get(operator.title).canKickTitle;
		
		if(canKickTitle<=0 || canKickTitle>target.title)
		{
			warnLog("踢出成员时,没有权限",operator.title,target.title);
			return;
		}
		
		if(target.title==RoleGroupTitleType.Leader)
		{
			warnLog("踢出成员时,不可提出队长");
			return;
		}
		
		removeMember(target,RoleGroupMemberInOutType.Kick);
		
		//玩家群日志
		RoleShowLogData logData=new RoleShowLogData();
		logData.id=InfoLogType.RoleGroupKickMember;
		logData.showData=(RoleShowData)target.socialData.showData.clone();
		addInfoLog(logData);
	}
	
	private void sendJoinResult(long playerID,boolean success)
	{
		FuncSendRoleGroupJoinResultServerRequest.create(_funcID,groupID,success).sendToPlayer(playerID);
	}
	
	/** 被邀请进入 */
	public void beInviteJoin(long invitorID,RoleSocialData data)
	{
		if(isFull())
		{
			warnLog("被邀请进入时,群已满");
			sendJoinResult(data.showData.playerID,false);
			return;
		}
		
		long playerID=data.showData.playerID;
		if(getMember(playerID)!=null)
		{
			warnLog("被邀请进入时,已在群中");
			return;
		}
		
		addNewMember(data,RoleGroupMemberInOutType.Invite);
		sendJoinResult(data.showData.playerID,true);
		
		RoleGroupMemberData invitor=getMember(invitorID);
		
		//邀请者在
		if(invitor!=null)
		{
			FuncSendHandleInviteResultRoleGroupRequest.create(_funcID,data.showData,RoleGroupHandleResultType.Agree).sendToPlayer(invitor.playerID);
		}
	}
	
	/** 申请加入玩家群 */
	public void applyRoleGroup(RoleSocialData data)
	{
		if(isFull())
		{
			warnLog("申请加入玩家群时,群已满");
			return;
		}
		
		long playerID=data.showData.playerID;
		if(getMember(playerID)!=null)
		{
			warnLog("申请加入玩家群时,已在群中");
			return;
		}
		
		PlayerApplyRoleGroupData aData=_d.applyDic.get(playerID);
		
		if(aData!=null)
		{
			warnLog("申请加入玩家群时,已申请过");
			return;
		}
		
		//无需同意
		if(_d.canApplyInAbs)
		{
			addNewMember(data,RoleGroupMemberInOutType.AgreeApply);
		}
		else
		{
			aData=new PlayerApplyRoleGroupData();
			aData.data=data;
			aData.time=DateControl.getTimeMillis();
			
			//放入申请组
			OtherUtils.putObjInDicWithMax(playerID,aData,_d.applyDic,_config.applyKeepMax,_applyComparator);
			
			RoleGroupSimpleData simpleData=createSimpleData();
			
			PlayerApplyRoleGroupSelfData selfData=new PlayerApplyRoleGroupSelfData();
			selfData.data=simpleData;
			selfData.time=DateControl.getTimeMillis();
			selfData.result=RoleGroupHandleResultType.None;
			
			AddApplyRoleGroupSelfWData wData=new AddApplyRoleGroupSelfWData();
			wData.data=selfData;
			
			_tool.addPlayerWork(playerID,wData);
			
			radioHandleApplyMembersRequest(FuncSendAddApplyRoleGroupRequest.create(_funcID,groupID,aData));
		}
	}
	
	/** 处理申请 */
	public void handleApply(long operatorID,long targetID,int result)
	{
		RoleGroupMemberData operator=getMember(operatorID);
		
		if(operator==null)
		{
			warnLog("处理申请时,操作者不存在",operatorID);
			return;
		}
		
		RoleGroupMemberData target=getMember(targetID);
		
		if(target!=null)
		{
			warnLog("处理申请时,目标已在群",targetID);
			return;
		}
		
		if(!RoleGroupTitleType.canOperateApply(operator.title))
		{
			warnLog("处理申请时,权限不足",operator.title);
			return;
		}
		
		PlayerApplyRoleGroupData aData=_d.applyDic.get(targetID);
		
		if(aData==null)
		{
			warnLog("处理申请时,未找到申请数据");
			return;
		}
		
		//移除
		_d.applyDic.remove(targetID);
		radioHandleApplyMembersRequest(FuncSendHandleApplyResultToMemberRequest.create(_funcID,groupID,targetID,result));
		
		HandleApplyRoleGroupWData wData=new HandleApplyRoleGroupWData();
		wData.groupID=groupID;
		wData.result=result;
		wData.funcID=_funcID;
		
		GameC.main.addPlayerAbsWork(targetID,wData);
	}
	
	/** 被申请进入 */
	public void beApplyJoin(RoleSocialData data)
	{
		if(isFull())
		{
			warnLog("被邀请进入时,群已满");
			sendJoinResult(data.showData.playerID,false);
			return;
		}
		
		long playerID=data.showData.playerID;
		if(getMember(playerID)!=null)
		{
			warnLog("被邀请进入时,已在群中");
			return;
		}
		
		addNewMember(data,RoleGroupMemberInOutType.AgreeApply);
		sendJoinResult(data.showData.playerID,true);
	}
	
	/** 更改群名 */
	public void changeName(long operatorID,String name)
	{
		RoleGroupMemberData operator=getMember(operatorID);
		
		if(operator==null)
		{
			warnLog("更改群名时,操作者不存在",operatorID);
			return;
		}
		
		if(!RoleGroupTitleConfig.get(operator.title).canChangeName)
		{
			warnLog("更改群名时,操作者权限不足",operatorID);
			return;
		}
		
		_tool.checkNameAvailable(name,b->
		{
			if(b)
			{
				String oldName=_d.name;
				_tool.useAndInsertName(name,_d.groupID,b2->
				{
					if(b2)
					{
						_tool.deleteName(oldName);
						doChangeNameNext(name);
					}
					else
					{
						doChangeNameFailed(operatorID,name);
					}
				});
			}
			else
			{
				doChangeNameFailed(operatorID,name);
			}
		});
	}
	
	private void doChangeNameNext(String name)
	{
		_d.name=name;
		doChange(RoleGroupChangeType.Name,name);
		
		//玩家群日志
		InfoLogData logData=new InfoLogData();
		logData.id=InfoLogType.ChangeRoleGroupName;
		logData.args=new String[]{name};
		addInfoLog(logData);
	}
	
	/** 更改群名失败 */
	private void doChangeNameFailed(long operatorID,String name)
	{
		RoleGroupMemberData operator=getMember(operatorID);
		
		if(operator==null)
		{
			warnLog("更改群名Next时,操作者不存在",operatorID);
			return;
		}
		
		GameC.main.sendInfoCodeToPlayer(operatorID,InfoCodeType.PlayerGroup_player_changeName_nameIsRepeat);
	}
	
	/** 更改群公告 */
	public void changeNotice(long operatorID,String notice)
	{
		RoleGroupMemberData operator=getMember(operatorID);
		
		if(operator==null)
		{
			warnLog("更改群公告时,操作者不存在",operatorID);
			return;
		}
		
		if(!RoleGroupTitleConfig.get(operator.title).canChangeNotice)
		{
			warnLog("更改群公告时,操作者权限不足",operatorID);
			return;
		}
		
		_d.notice=notice;
		doChange(RoleGroupChangeType.Notice,notice);
		
		//玩家群日志
		InfoLogData logData=new InfoLogData();
		logData.id=InfoLogType.ChangeRoleGroupNotice;
		logData.args=new String[]{operator.socialData.showData.name};
		addInfoLog(logData);
	}
	
	/** 更改申请方式 */
	public void changeCanApplyInAbs(long operatorID,boolean canApplyInAbs)
	{
		RoleGroupMemberData operator=getMember(operatorID);
		
		if(operator==null)
		{
			warnLog("更改申请方式时,操作者不存在",operatorID);
			return;
		}
		
		if(!RoleGroupTitleConfig.get(operator.title).canChangeApplyInAbs)
		{
			warnLog("更改申请方式时,操作者权限不足",operatorID);
			return;
		}
		
		_d.canApplyInAbs=canApplyInAbs;
		doChange(RoleGroupChangeType.CanApplyInAbs,canApplyInAbs?1:0);
		
		//玩家群日志
		InfoLogData logData=new InfoLogData();
		logData.id=InfoLogType.ChangeRoleGroupCanApplyInAbs;
		logData.args=new String[]{operator.socialData.showData.name};
		addInfoLog(logData);
	}
	
	/** 增加玩家群经验 */
	public void addExp(long value,int way)
	{
		if(value<=0)
			return;
		
		int level;
		//已达最大等级
		if((level=_d.level) >= _config.levelMax)
		{
			return;
		}
		
		long v=(_d.exp+=value);
		
		long expMax;
		
		if(v >= (expMax=_levelConfig.expMax))
		{
			int oldLevel=level;
			
			while(true)
			{
				v-=expMax;
				
				if(++level == _config.levelMax)
				{
					v=0;
					
					break;
				}
				
				expMax=RoleGroupLevelConfig.get(_config.id,level).expMax;
				
				if(v<expMax)
					break;
			}
			
			_d.exp=v;
			
			doChange(RoleGroupChangeType.Exp,v);
			
			_d.level=level;
			_levelConfig=RoleGroupLevelConfig.get(_config.id,level);
			
			doChange(RoleGroupChangeType.Level,level);
			
			onLevelUp(oldLevel);
		}
		else
		{
			doChange(RoleGroupChangeType.Exp,v);
		}
	}
	
	/** 升级 */
	protected void onLevelUp(int oldLevel)
	{
	
	}
	
	/** 记录日志数据 */
	protected void recordInfoLog(InfoLogData data)
	{
		SQueue<InfoLogData> queue;
		
		(queue=_d.logQueue).offer(data);
		
		if(queue.size()>_config.infoLogKeepNum)
			queue.poll();
		
	}
	
	/** 发送日志数据 */
	public void addInfoLog(InfoLogData data)
	{
		if(InfoLogConfig.get(data.id).needSave)
		{
			//存库
			recordInfoLog(data);
		}
		
		radioMembers(FuncSendRoleGroupInfoLogRequest.create(_funcID,groupID,data));
	}
	
	/** 添加简单日志 */
	public void addSimpleInfoLog(int id)
	{
		InfoLogData data=new InfoLogData();
		data.id=id;
		addInfoLog(data);
	}
	
	/** 构造创建场景数据 */
	protected void makeCreateSceneData(RoleGroupCreateSceneData data)
	{
		data.funcID=_funcID;
		data.groupID=groupID;
		data.configID=_config.id;
		data.level=_d.level;
		data.name=_d.name;
	}
	
	/** 进入专属场景 */
	public void enterOwnScene(long playerID)
	{
		_createSceneATool.add(playerID,()->
		{
			FuncRoleGroupReEnterOwnSceneArgToPlayerServerRequest.create(_funcID,groupID,_ownSceneLocation).sendToPlayer(playerID);
		});
	}
	
	/** 执行场景方法(如场景不存在，则返回，否则会在场景创建后调用) */
	protected void doFuncForScene(Runnable func)
	{
		if(_ownSceneLocation!=null)
		{
			func.run();
			return;
		}
		
		//不在执行中就跳过
		if(_createSceneATool.isDoing())
		{
			_createSceneATool.add(func);
		}
	}
	
	/** 发消息到场景服 */
	protected void sendToOwnScene(SignedSceneGameToGameServerRequest request)
	{
		if(_ownSceneLocation==null)
			return;
		
		request.executorIndex=_ownSceneLocation.executorIndex;
		request.instanceID=_ownSceneLocation.instanceID;
		request.send(_ownSceneLocation.gameID);
	}
	
	//funcTools
	
	/** 获取功能插件组(没有就创建) */
	public IntObjectMap<FuncTool> getFuncToolDic(int type)
	{
		IntObjectMap<FuncTool> dic=_funcToolDic[type];
		
		if(dic==null)
		{
			return _funcToolDic[type]=new IntObjectMap<>(FuncTool[]::new);
		}
		else
		{
			return dic;
		}
	}
	
	/** 注册功能插件(construct) */
	public FuncTool registFuncTool(IRoleGroupFuncTool tool)
	{
		toRegistFuncTool(tool);
		
		return (FuncTool)tool;
	}
	
	private void toRegistFuncTool(IRoleGroupFuncTool tool)
	{
		FuncTool fTool=(FuncTool)tool;
		
		IntObjectMap<FuncTool> dic=getFuncToolDic(fTool.getType());
		
		int funcID=fTool.getFuncID();
		
		if(ShineSetting.openCheck)
		{
			if(dic.contains(funcID))
			{
				Ctrl.throwError("已经存在功能插件:",fTool.getType(),funcID);
			}
		}
		
		//设置主角
		tool.setMe(this);
		dic.put(funcID,fTool);
		_funcToolList.add(fTool);
		_roleGroupFuncToolList.add(tool);
		fTool.construct();
	}
	
	/** 获取功能拆件 */
	public FuncTool getFuncTool(int type,int funcID)
	{
		return getFuncToolDic(type).get(funcID);
	}
	
	/** 获取排行工具 */
	public RoleGroupRankTool getRankTool(int funcID)
	{
		return (RoleGroupRankTool)getFuncTool(FuncToolType.Rank,funcID);
	}
}
