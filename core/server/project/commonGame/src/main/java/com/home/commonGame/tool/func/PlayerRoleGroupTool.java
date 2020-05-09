package com.home.commonGame.tool.func;

import com.home.commonBase.config.game.RoleGroupConfig;
import com.home.commonBase.constlist.generate.CallWayType;
import com.home.commonBase.constlist.generate.FuncToolType;
import com.home.commonBase.constlist.generate.InfoCodeType;
import com.home.commonBase.constlist.generate.InfoLogType;
import com.home.commonBase.constlist.generate.RoleGroupHandleResultType;
import com.home.commonBase.constlist.generate.RoleGroupMemberInOutType;
import com.home.commonBase.constlist.generate.RoleGroupTitleType;
import com.home.commonBase.constlist.system.GameAreaDivideType;
import com.home.commonBase.data.func.FuncToolData;
import com.home.commonBase.data.func.PlayerFuncWorkData;
import com.home.commonBase.data.role.RoleShowData;
import com.home.commonBase.data.social.RoleSocialData;
import com.home.commonBase.data.social.roleGroup.CreateRoleGroupData;
import com.home.commonBase.data.social.roleGroup.InviteRoleGroupReceiveData;
import com.home.commonBase.data.social.roleGroup.PlayerApplyRoleGroupSelfData;
import com.home.commonBase.data.social.roleGroup.PlayerRoleGroupClientToolData;
import com.home.commonBase.data.social.roleGroup.PlayerRoleGroupData;
import com.home.commonBase.data.social.roleGroup.PlayerRoleGroupSaveData;
import com.home.commonBase.data.social.roleGroup.PlayerRoleGroupToolData;
import com.home.commonBase.data.social.roleGroup.RoleGroupSimpleData;
import com.home.commonBase.data.social.roleGroup.work.CreateRoleGroupWData;
import com.home.commonBase.data.social.roleGroup.work.InviteRoleGroupWData;
import com.home.commonBase.data.system.PlayerWorkData;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.utils.BaseGameUtils;
import com.home.commonGame.global.GameC;
import com.home.commonGame.logic.func.PlayerRoleGroup;
import com.home.commonGame.net.request.func.roleGroup.FuncSendAddApplyRoleGroupSelfRequest;
import com.home.commonGame.net.request.func.roleGroup.FuncSendChangeCanInviteInAbsRoleGroupRequest;
import com.home.commonGame.net.request.func.roleGroup.FuncSendHandleApplyResultRoleGroupRequest;
import com.home.commonGame.net.request.func.roleGroup.FuncSendHandleInviteResultRoleGroupRequest;
import com.home.commonGame.net.request.func.roleGroup.FuncSendInviteRoleGroupRequest;
import com.home.commonGame.net.request.func.roleGroup.FuncSendPlayerJoinRoleGroupRequest;
import com.home.commonGame.net.request.func.roleGroup.FuncSendPlayerLeaveRoleGroupRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncAgreeApplyNextRoleGroupToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncAgreeInviteCreateRoleGroupToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncAgreeInviteRoleGroupToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncApplyRoleGroupToGameServerRequest;
import com.home.commonGame.part.player.Player;
import com.home.shine.control.ThreadControl;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.collection.IntSet;
import com.home.shine.support.collection.LongObjectMap;
import com.home.shine.support.collection.LongQueue;
import com.home.shine.support.collection.SList;
import com.home.shine.support.pool.ObjectPool;
import com.home.shine.tool.OperateNumTool;
import com.home.shine.utils.StringUtils;

/** 角色身上玩家群工具 */
public class PlayerRoleGroupTool extends PlayerFuncTool
{
	protected PlayerRoleGroupToolData _d;
	
	protected RoleGroupConfig _config;
	
	private ObjectPool<PlayerRoleGroup> _roleGroupPool;
	
	/** 玩家群字典 */
	private LongObjectMap<PlayerRoleGroup> _roleGroupDic=new LongObjectMap<>(PlayerRoleGroup[]::new);
	/** 唯一群 */
	private PlayerRoleGroup _onlyOne;
	/** 接受的邀请信息组 */
	private SList<InviteRoleGroupReceiveData> _inviteList;
	
	private int _tempIndex=-1;
	
	/** 创建玩家群计数工具 */
	private OperateNumTool _createRoleGroupOTool;
	private OperateNumTool _inviteOTool;
	
	public PlayerRoleGroupTool(int funcID,int groupID)
	{
		super(FuncToolType.RoleGroup,funcID);
		
		_config=RoleGroupConfig.get(groupID);
		
		_roleGroupPool=new ObjectPool<>(()->
		{
			PlayerRoleGroup playerRoleGroup=toCreatePlayerRoleGroup();
			playerRoleGroup.setGroupTool(this);//此时为赋值me
			playerRoleGroup.construct();
			return playerRoleGroup;
		});
		_roleGroupPool.setEnable(CommonSetting.playerUsePool);
	}
	
	@Override
	public void construct()
	{
		if(!_config.needKeepOfflineInvite)
		{
			_inviteList=new SList<>(InviteRoleGroupReceiveData[]::new);
		}
		
		_createRoleGroupOTool=new OperateNumTool(me);
		(_inviteOTool=new OperateNumTool(me,ShineSetting.affairDefaultExecuteTime*1000)).init();
	}
	
	@Override
	protected void toSetData(FuncToolData data)
	{
		super.toSetData(data);
		
		_d=(PlayerRoleGroupToolData)data;
	}
	
	@Override
	public void afterReadData()
	{
		super.afterReadData();
		
		if(_config.needKeepOfflineInvite)
		{
			if(_d.inviteList==null)
				_d.inviteList=new SList<>(InviteRoleGroupReceiveData[]::new);
			
			_inviteList=_d.inviteList;
		}
		
		_d.groups.forEachValue(v->
		{
			PlayerRoleGroup playerRoleGroup=_roleGroupPool.getOne();
			playerRoleGroup.setGroupTool(this);
			playerRoleGroup.setSaveData(v);
			_roleGroupDic.put(playerRoleGroup.groupID,playerRoleGroup);
			if(_config.isSingleJoin())
				_onlyOne=playerRoleGroup;
		});
		
		if(_d.createOperateNums==null)
			_d.createOperateNums=new LongQueue();
		
		_createRoleGroupOTool.setData(_d.createOperateNums);
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		_roleGroupDic.forEachValueS(v->
		{
			v.dispose();
			_roleGroupDic.remove(v.groupID);
			_roleGroupPool.back(v);
		});
		
		_tempIndex=-1;
		
		if(_inviteList!=null)
			_inviteList.clear();
		
		_onlyOne=null;
		
		_createRoleGroupOTool.clear();
		_inviteOTool.clear();
	}
	
	public PlayerRoleGroupToolData getData()
	{
		return _d;
	}
	
	@Override
	protected PlayerRoleGroupToolData createToolData()
	{
		return new PlayerRoleGroupToolData();
	}
	
	public RoleGroupConfig getConfig()
	{
		return _config;
	}
	
	/** 创建角色自身玩家群 */
	protected PlayerRoleGroup toCreatePlayerRoleGroup()
	{
		return new PlayerRoleGroup();
	}
	
	@Override
	public void onNewCreate()
	{
		super.onNewCreate();
		
		_d.canInviteInAbs=_config.defaultCanInviteInAbs;
	}
	
	@Override
	public void onSecond(int delay)
	{
		super.onSecond(delay);
		
		_createRoleGroupOTool.check();
		_inviteOTool.check();
		
		if(!_inviteList.isEmpty())
		{
			long tt=me.getTimeMillis()-_config.inviteEnableTime*1000;
			
			InviteRoleGroupReceiveData[] values;
			InviteRoleGroupReceiveData v;
			
			for(int i=(values=_inviteList.getValues()).length-1;i>=0;--i)
			{
				if((v=values[i])!=null)
				{
					if(v.time>0 && tt>v.time)
					{
						//移除
						_inviteList.remove(i);
					}
				}
			}
		}
		
		LongObjectMap<PlayerApplyRoleGroupSelfData> applyDic;
		if(!(applyDic=_d.applyDic).isEmpty())
		{
			long now=me.getTimeMillis();
			
			applyDic.forEachValue(v->
			{
				if(v.disableTime>0 && now>v.disableTime)
				{
					applyDic.remove(v.data.groupID);
				}
			});
		}
	}
	
	private void removeOneTempOperate()
	{
		_inviteOTool.sub();
	}
	
	/** 是否只本服 */
	public boolean isOnlyLocalGame()
	{
		return CommonSetting.isAreaSplit() && _config.isOnlyLocalGame;
	}
	
	/** 创建客户端数据 */
	public PlayerRoleGroupClientToolData createClientData()
	{
		PlayerRoleGroupClientToolData data=new PlayerRoleGroupClientToolData();
		data.applyDic=_d.applyDic;
		data.groups=new LongObjectMap<>(PlayerRoleGroupData[]::new);
		data.canInviteInAbs=_d.canInviteInAbs;
		
		_roleGroupDic.forEachValue(v->
		{
			data.groups.put(v.groupID,v.getData());
		});
		
		data.inviteList=_inviteList;
		
		return data;
	}
	
	/** 获取角色自身玩家群逻辑 */
	public PlayerRoleGroup getRoleGroup(long groupID)
	{
		return _roleGroupDic.get(groupID);
	}
	
	public LongObjectMap<PlayerRoleGroup> getRoleGroupDic()
	{
		return _roleGroupDic;
	}
	
	/** 设置各个逻辑服的玩家群基础数据(主线程) */
	public void setGameData(LongObjectMap<PlayerRoleGroupData> dic)
	{
		if(dic!=null)
		{
			dic.forEachValue(v->
			{
				PlayerRoleGroup roleGroup=getRoleGroup(v.groupID);
				
				if(roleGroup==null)
				{
					roleGroup=toAddRoleGroup(v);
				}
				else
				{
					roleGroup.setData(v);
					roleGroup.init();
					roleGroup.afterReadData();
				}
			});
		}
		
		//PlayerRoleGroup[] values;
		//PlayerRoleGroup v;
		//
		//for(int i=(values=_roleGroupDic.getValues()).length-1;i>=0;--i)
		//{
		//	if((v=values[i])!=null)
		//	{
		//		if(v.getData()==null)
		//		{
		//			//确认删除
		//			if(timeOutGames==null || !timeOutGames.contains(GameC.main.getNowGameIDByLogicID(v.groupID)))
		//			{
		//				v.dispose();
		//				_roleGroupDic.remove(v.groupID);
		//
		//				if(_config.needSave)
		//				{
		//					_d.groups.remove(v.groupID);
		//				}
		//
		//				++i;
		//			}
		//			else
		//			{
		//				//TODO:补充初始化
		//			}
		//		}
		//	}
		//}
	}
	
	/** 添加角色事务 */
	public void addPlayerFuncWork(long playerID,PlayerFuncWorkData data)
	{
		data.funcID=_funcID;
		
		addPlayerWork(playerID,data);
	}
	
	/** 添加角色事务 */
	public void addPlayerWork(long playerID,PlayerWorkData data)
	{
		if(_config.needSave)
		{
			GameC.main.addPlayerOfflineWork(playerID,data);
		}
		else
		{
			GameC.main.addPlayerOnlineWork(playerID,data);
		}
	}
	
	/** 获取当前拥有的群数 */
	public int getRoleGroupNum()
	{
		return _d.groups.size();
	}
	
	/** 获取唯一群 */
	public PlayerRoleGroup getOnlyOne()
	{
		return _onlyOne;
	}
	
	/** 是否拥有的群已满 */
	public boolean isRoleGroupFull()
	{
		return _config.eachPlayerGroupNum>0 && getRoleGroupNum()>=_config.eachPlayerGroupNum;
	}
	
	/** 是否拥有的群已满(包括临时) */
	protected boolean isRoleGroupFullWithTemp()
	{
		return _config.eachPlayerGroupNum>0 && (getRoleGroupNum()+ _createRoleGroupOTool.getNum() + _inviteOTool.getNum())>=_config.eachPlayerGroupNum;
	}
	
	/** 创建玩家群失败 */
	public void onCreateRoleGroupResult(boolean success)
	{
		_createRoleGroupOTool.sub();
		
		if(!success && _config.createCostID>0)
		{
			me.bag.rollbackCost(_config.createCostID,CallWayType.CreateRoleGroup);
		}
	}
	
	/** 是否可创建玩家群额外判定 */
	protected boolean canCreateRoleGroupEx()
	{
		return true;
	}
	
	private PlayerRoleGroup toAddRoleGroup(PlayerRoleGroupData data)
	{
		PlayerRoleGroup roleGroup=_roleGroupPool.getOne();
		PlayerRoleGroupSaveData sData=roleGroup.toCreatePlayerRoleGroupSaveData();
		sData.initDefault();
		sData.groupID=data.groupID;
		roleGroup.setGroupTool(this);
		roleGroup.setSaveData(sData);
		
		if(_config.needSave)
		{
			_d.groups.put(sData.groupID,sData);
		}
		
		roleGroup.setData(data);
		
		_roleGroupDic.put(roleGroup.groupID,roleGroup);
		if(_config.isSingleJoin())
			_onlyOne=roleGroup;
		
		roleGroup.init();
		roleGroup.afterReadData();
		
		return roleGroup;
	}
	
	/** 加入玩家群 */
	public void onJoinRoleGroup(PlayerRoleGroupData data,int type)
	{
		PlayerRoleGroup roleGroup=getRoleGroup(data.groupID);
		
		if(roleGroup!=null)
		{
			me.warnLog("不该有残留的玩家群");
			roleGroup.dispose();
			
			if(_config.needSave)
			{
				_d.groups.remove(roleGroup.groupID);
			}
			
			_roleGroupPool.back(roleGroup);
		}
		
		roleGroup=toAddRoleGroup(data);
		
		//移除申请
		_d.applyDic.remove(data.groupID);
		
		//推送
		me.send(FuncSendPlayerJoinRoleGroupRequest.create(_funcID,data,type));
		
		//创建
		if(type==RoleGroupMemberInOutType.Create)
		{
			me.addSimpleInfoLog(InfoLogType.CreateRoleGroupSuccess);
		}
		
		onJoinRoleGroup(roleGroup);
	}
	
	/** 加入玩家群(可做更新数据到roleGroup) */
	protected void onJoinRoleGroup(PlayerRoleGroup roleGroup)
	{
	
	}
	
	/** 离开玩家群 */
	public void onLeaveRoleGroup(long groupID,int type)
	{
		PlayerRoleGroup roleGroup=getRoleGroup(groupID);
		
		if(roleGroup==null)
		{
			me.warnLog("离开玩家群时,找不到群",groupID);
			return;
		}
		
		onLeaveRoleGroup(roleGroup);
		
		roleGroup.dispose();
		
		if(_config.needSave)
		{
			_d.groups.remove(roleGroup.groupID);
		}
		
		_roleGroupDic.remove(roleGroup.groupID);
		
		if(_config.isSingleJoin())
			_onlyOne=null;
		
		me.send(FuncSendPlayerLeaveRoleGroupRequest.create(_funcID,groupID,type));
		
		_roleGroupPool.back(roleGroup);
	}
	
	/** 离开玩家群(可做更新数据到roleGroup) */
	protected void onLeaveRoleGroup(PlayerRoleGroup roleGroup)
	{
	
	}
	
	/** 获取邀请信息邀请信息 */
	protected InviteRoleGroupReceiveData getInviteInfo(long playerID,long groupID)
	{
		_tempIndex=-1;
		if(_inviteList.isEmpty())
			return null;
		
		InviteRoleGroupReceiveData[] values=_inviteList.getValues();
		InviteRoleGroupReceiveData v;
		
		for(int i=0,len=_inviteList.size();i<len;++i)
		{
			v=values[i];
			
			if(v.inviter.playerID==playerID && v.getGroupID()==groupID)
			{
				_tempIndex=i;
				return v;
			}
		}
		
		return null;
	}
	
	//行为部分
	
	/** 创建玩家群 */
	public void createRoleGroup(CreateRoleGroupData data)
	{
		if(!checkCanCreateRoleGroupSame(data))
			return;
		
		if(_config.nameCharLimit>0 && StringUtils.getCharMachineNum(data.name)>_config.nameCharLimit)
		{
			me.warningInfoCode(InfoCodeType.PlayerGroup_create_nameTooLong);
			return;
		}
		
		if(_config.noticeCharLimit>0 && StringUtils.getCharMachineNum(data.notice)>_config.noticeCharLimit)
		{
			me.warningInfoCode(InfoCodeType.PlayerGroup_create_noticeTooLong);
			return;
		}
		
		//敏感字
		if(BaseGameUtils.hasSensitiveWord(data.name))
		{
			me.warnLog("创建玩家群时,名字敏感");
			me.sendInfoCode(InfoCodeType.CreateRoleGroupFailed_nameIsSensitive);
			return;
		}
		
		//敏感字
		if(BaseGameUtils.hasSensitiveWord(data.notice))
		{
			me.warnLog("创建玩家群时,公告敏感");
			me.sendInfoCode(InfoCodeType.CreateRoleGroupFailed_noticeIsSensitive);
			return;
		}
		
		//使用名
		//String nn=(Global.isNameUseAreaIDAsFront && CommonSetting.areaDivideType==GameAreaDivideType.Split) ? "s" + createAreaID + "." + name : name;
		
		if(_config.needUniqueName)
		{
			long playerID=me.role.playerID;
			int funcID=_funcID;
			String name=data.name;
			me.addMainFunc(()->
			{
				GameC.global.func.getRoleGroupTool(funcID).checkNameAvailable(name,b->
				{
					Player player=GameC.main.getPlayerByID(playerID);
					
					if(player!=null)
					{
						player.addFunc(()->
						{
							player.func.getRoleGroupTool(funcID).doCreateRoleGroupNext(data,b);
						});
					}
				});
			});
		}
		else
		{
			doCreateRoleGroupNext(data,true);
		}
	}
	
	/** 检查创建玩家群名字条件(相同部分) */
	private boolean checkCanCreateRoleGroupSame(CreateRoleGroupData data)
	{
		if(isRoleGroupFullWithTemp())
		{
			me.warnLog("创建玩家群时,已达到数目限制");
			return false;
		}
		
		if(_config.createCostID>0 && !me.bag.hasCost(_config.createCostID))
		{
			me.warnLog("创建玩家群时,cost不满足");
			return false;
		}
		
		if(!me.role.checkRoleConditions(_config.createConditions,true))
		{
			me.warnLog("创建玩家群时,condition不满足");
			return false;
		}
		
		if(!canCreateRoleGroupEx())
		{
			me.warnLog("创建玩家群时,ex不满足");
			return false;
		}
		
		return true;
	}
	
	/** 执行创建玩家群下一步(成功以后) */
	public void doCreateRoleGroupNext(CreateRoleGroupData data,boolean suc)
	{
		if(!suc)
		{
			me.warnLog("创建玩家群时,名字重名");
			me.sendInfoCode(InfoCodeType.CreateRoleGroupFailed_nameIsRepeat);
			return;
		}
		
		if(!checkCanCreateRoleGroupSame(data))
			return;
		
		if(_config.createCostID>0)
		{
			me.bag.doCost(_config.createCostID,CallWayType.CreateRoleGroup);
		}
		
		_createRoleGroupOTool.add();
		
		CreateRoleGroupWData wData=new CreateRoleGroupWData();
		wData.funcID=_funcID;
		wData.data=me.social.createRoleSocialData();
		wData.createData=data;
		wData.receiveAreaID=me.role.createAreaID;
		
		me.addAreaGlobalWork(wData);
	}
	
	/** 邀请入群 */
	public void inviteRoleGroup(long groupID,long targetID)
	{
		if(!me.role.checkRoleConditions(_config.joinConditions,true))
		{
			me.warningInfoCode(InfoCodeType.PlayerGroup_invite_conditionNotMet);
			return;
		}
		
		if(isOnlyLocalGame() && !me.isSameGame(targetID))
		{
			me.warningInfoCode(InfoCodeType.PlayerGroup_wrongServer);
			return;
		}
		
		RoleGroupSimpleData simpleData=null;
		
		if(groupID>0)
		{
			PlayerRoleGroup roleGroup=getRoleGroup(groupID);
			
			if(roleGroup==null)
			{
				me.warningInfoCode(InfoCodeType.PlayerGroup_invite_canNotFindGroup,groupID);
				return;
			}
			
			if(roleGroup.isFull())
			{
				me.warningInfoCode(InfoCodeType.PlayerGroup_invite_memberFull,groupID);
				return;
			}
			
			if(!RoleGroupTitleType.canOperateApply(roleGroup.getSelfData().title))
			{
				me.warningInfoCode(InfoCodeType.PlayerGroup_invite_notAllowed,groupID,roleGroup.getSelfData().title);
				return;
			}
			
			if(roleGroup.getMember(targetID)!=null)
			{
				me.warningInfoCode(InfoCodeType.PlayerGroup_invite_alreadyInGroup,groupID);
				return;
			}
			
			simpleData=roleGroup.createSimpleData();
		}
		else
		{
			if(!_config.canInviteCreate)
			{
				me.warningInfoCode(InfoCodeType.PlayerGroup_inviteAndCreate_canNotCreate);
				return;
			}
			
			if(_config.eachPlayerGroupNum!=1)
			{
				me.warningInfoCode(InfoCodeType.PlayerGroup_inviteAndCreate_mustOnly);
				return;
			}
			
			if(isRoleGroupFullWithTemp())
			{
				me.warningInfoCode(InfoCodeType.PlayerGroup_inviteAndCreate_numLimit);
				return;
			}
		}
		
		InviteRoleGroupWData wData=new InviteRoleGroupWData();
		wData.funcID=_funcID;
		wData.inviter=me.role.createRoleShowData();
		wData.simpleData=simpleData;
		
		//if(_d.canInviteInAbs)
		//{
		//	//需要选择
		//	me.addPlayerAbsWork(targetID,wData);
		//}
		//else
		//{
			//需要选择
			me.addPlayerOnlineWork(targetID,wData);
		//}
	}
	
	/** 收到邀请入群 */
	public void receiveInviteRoleGroup(RoleShowData inviter,RoleGroupSimpleData simpleData)
	{
		long invitorID=inviter.playerID;
		
		if(isRoleGroupFullWithTemp())
		{
			me.log("收到邀请入群时,群已满",invitorID);
			me.sendInfoCodeToPlayer(invitorID,InfoCodeType.InviteRoleGroupFailed_targetRoleGroupFull);
			return;
		}
		
		if(!me.role.checkRoleConditions(_config.joinConditions,false))
		{
			me.warnLog("邀请入群时,目标condition不满足");
			me.sendInfoCodeToPlayer(invitorID,InfoCodeType.InviteRoleGroupFailed_targetConditionNotMatch);
			return;
		}
		
		//有群
		if(simpleData!=null)
		{
			PlayerRoleGroup roleGroup=getRoleGroup(simpleData.groupID);
			
			if(roleGroup!=null)
			{
				me.log("收到邀请入群时,已在群中",invitorID);
				me.sendInfoCodeToPlayer(invitorID,InfoCodeType.InviteRoleGroupFailed_targetInRoleGroup);
				return;
			}
		}
		
		//立即进入
		if(_d.canInviteInAbs)
		{
			doAgreeInvite(inviter,simpleData);
		}
		else
		{
			InviteRoleGroupReceiveData inviteInfo=getInviteInfo(inviter.playerID,simpleData!=null ? simpleData.groupID : -1);
			
			if(inviteInfo==null)
			{
				inviteInfo=new InviteRoleGroupReceiveData();
				_inviteList.add(inviteInfo);
				
				if(_config.inviteKeepMax>0 && _inviteList.size()>_config.inviteKeepMax)
				{
					_inviteList.shift();
				}
			}
			
			//更新邀请时间
			inviteInfo.time=me.getTimeMillis();
			inviteInfo.inviter=inviter;
			inviteInfo.simpleData=simpleData;
			
			me.send(FuncSendInviteRoleGroupRequest.create(_funcID,inviteInfo));
		}
	}
	
	/** 处理邀请 */
	public void handleInvite(long invitorID,long groupID,int result)
	{
		InviteRoleGroupReceiveData inviteInfo=getInviteInfo(invitorID,groupID);
		
		if(inviteInfo==null)
		{
			me.warningInfoCode(InfoCodeType.PlayerGroup_handleInvite_invalid);
			return;
		}
		
		_inviteList.remove(_tempIndex);
		_tempIndex=-1;
		
		//拒绝回复
		if(result==RoleGroupHandleResultType.Refuse)
		{
			FuncSendHandleInviteResultRoleGroupRequest.create(_funcID,me.role.createRoleShowData(),result).sendToPlayer(invitorID);
		}
		//同意
		else if(result==RoleGroupHandleResultType.Agree)
		{
			if(isRoleGroupFullWithTemp())
			{
				me.warningInfoCode(InfoCodeType.PlayerGroup_acceptInvite_groupNumFull);
				return;
			}
			
			if(!me.role.checkRoleConditions(_config.joinConditions,true))
			{
				me.warningInfoCode(InfoCodeType.PlayerGroup_acceptInvite_conditionNotMet);
				return;
			}
			
			doAgreeInvite(inviteInfo.inviter,inviteInfo.simpleData);
		}
	}
	
	/** 申请加入玩家群 */
	public void applyRoleGroup(long groupID)
	{
		if(isOnlyLocalGame() && !me.isSameGame(groupID))
		{
			me.warningInfoCode(InfoCodeType.PlayerGroup_canNotApplyOtherServer);
			return;
		}
		
		if(!_config.canApply)
		{
			me.warningInfoCode(InfoCodeType.PlayerGroup_apply_configCanNotApply);
			return;
		}
		
		if(isRoleGroupFullWithTemp())
		{
			me.warningInfoCode(InfoCodeType.PlayerGroup_apply_groupNumFull);
			return;
		}
		
		PlayerRoleGroup roleGroup=getRoleGroup(groupID);
		
		if(roleGroup!=null)
		{
			me.warningInfoCode(InfoCodeType.PlayerGroup_apply_alreadyInGroup);
			return;
		}
		
		if(!me.role.checkRoleConditions(_config.joinConditions,true))
		{
			me.warningInfoCode(InfoCodeType.PlayerGroup_apply_conditionNotMet);
			return;
		}
		
		PlayerApplyRoleGroupSelfData aData=_d.applyDic.get(groupID);
		
		if(aData!=null)
		{
			me.warningInfoCode(InfoCodeType.PlayerGroup_apply_alreadyApply);
			return;
		}
		
		if(_config.applyKeepMax>0 && _d.applyDic.size()>=_config.applyKeepMax)
		{
			me.warningInfoCode(InfoCodeType.PlayerGroup_apply_applyLimit);
			return;
		}
		
		RoleSocialData sData=me.social.createRoleSocialData();
		
		if(GameC.main.isCurrentGame(groupID))
		{
			me.addMainFunc(()->
			{
				GameC.main.getRoleGroupAbs(_funcID,groupID,v->
				{
					if(v!=null)
					{
						v.applyRoleGroup(sData);
					}
				});
			});
		}
		else
		{
			FuncApplyRoleGroupToGameServerRequest.create(_funcID,groupID,sData).sendToSourceGameByLogicID(groupID);
		}
	}
	
	private void doAgreeInvite(RoleShowData invitor,RoleGroupSimpleData simpleData)
	{
		RoleSocialData sData=me.social.createRoleSocialData();
		
		long groupID=simpleData!=null ? simpleData.groupID : -1;
		long invitorID=invitor.playerID;
		
		_inviteOTool.add();
		
		//有群
		if(groupID>0)
		{
			if(GameC.main.isCurrentGame(groupID))
			{
				me.addMainFunc(()->
				{
					GameC.global.func.getRoleGroupTool(_funcID).beInviteJoin(groupID,invitorID,sData);
				});
			}
			else
			{
				FuncAgreeInviteRoleGroupToGameServerRequest.create(_funcID,groupID,invitorID,sData).sendToSourceGameByLogicID(groupID);
			}
		}
		else
		{
			//TODO:如有需要，可以走一趟 邀请者 取数据
			
			RoleSocialData invitorSocialData=RoleSocialData.createSocialDataByShowDataOnline(invitor);
			
			if(GameC.main.isCurrentGame(invitorID))
			{
				me.addMainFunc(()->
				{
					GameRoleGroupTool tool=GameC.global.func.getRoleGroupTool(_funcID);
					
					tool.beInviteCreateRoleGroup(invitorSocialData,sData);
				});
			}
			else
			{
				FuncAgreeInviteCreateRoleGroupToGameServerRequest.create(_funcID,invitorSocialData,sData).sendToSourceGameByLogicID(invitorID);
			}
		}
	}
	
	/** 添加申请玩家群数据到自身 */
	public void onAddApplySelf(PlayerApplyRoleGroupSelfData selfData)
	{
		selfData.disableTime=selfData.time+_config.applyEnableTime*1000;
		_d.applyDic.put(selfData.data.groupID,selfData);
		
		//推送自己
		me.send(FuncSendAddApplyRoleGroupSelfRequest.create(_funcID,selfData));
	}
	
	/** 申请处理结果 */
	public void onApplyResult(long groupID,int result)
	{
		PlayerApplyRoleGroupSelfData aData=_d.applyDic.get(groupID);
		
		if(aData==null)
		{
			me.warnLog("收到申请处理结果时,未找到数据",groupID);
			return;
		}
		
		//标记结果
		aData.result=result;
		
		if(result==RoleGroupHandleResultType.Refuse)
		{
			aData.disableTime=aData.time+_config.refuseApplyTime*1000;
		}
		else
		{
			//其他类型移除
			_d.applyDic.remove(groupID);
		}
		
		if(BaseC.constlist.roleGroupHandleResult_needReback(result))
		{
			me.send(FuncSendHandleApplyResultRoleGroupRequest.create(_funcID,groupID,result));
		}
		
		//同意
		if(result==RoleGroupHandleResultType.Agree)
		{
			if(isRoleGroupFullWithTemp())
			{
				me.warnLog("被添加到玩家群时,已达到数目限制");
				return;
			}
			
			if(!me.role.checkRoleConditions(_config.joinConditions,true))
			{
				me.warnLog("被添加到玩家群时,自身条件不满足");
				return;
			}
			
			_inviteOTool.add();
			
			RoleSocialData sData=me.social.createRoleSocialData();
			
			if(GameC.main.isCurrentGame(groupID))
			{
				ThreadControl.addMainFunc(()->
				{
					GameC.global.func.getRoleGroupTool(_funcID).beApplyJoin(groupID,sData);
				});
			}
			else
			{
				FuncAgreeApplyNextRoleGroupToGameServerRequest.create(_funcID,groupID,sData).sendToSourceGameByLogicID(groupID);
			}
		}
	}
	
	/** 更改收到邀请时是否直接同意 */
	public void changeCanInviteInAbs(boolean value)
	{
		if(_d.canInviteInAbs==value)
			return;
		
		_d.canInviteInAbs=value;
		
		me.send(FuncSendChangeCanInviteInAbsRoleGroupRequest.create(_funcID,value));
		
		//TODO:推送本端
	}
	
	/** 收回一个添加记录 */
	public void onJoinResult(boolean success)
	{
		//删一个记录
		removeOneTempOperate();
	}
}
