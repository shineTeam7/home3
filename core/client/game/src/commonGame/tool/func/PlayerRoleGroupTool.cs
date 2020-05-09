using System;
using ShineEngine;

/// <summary>
/// 角色身上玩家群工具
/// </summary>
public class PlayerRoleGroupTool:PlayerFuncTool
{
	protected PlayerRoleGroupClientToolData _d;

	protected RoleGroupConfig _config;

	/** 玩家群字典 */
	private LongObjectMap<PlayerRoleGroup> _roleGroupDic=new LongObjectMap<PlayerRoleGroup>();
	/** 唯一群 */
	private PlayerRoleGroup _onlyOne;
	/** 邀请信息组 */
	private SList<InviteRoleGroupReceiveData> _inviteList;

	/** 通用事件 */
	public RoleGroupEvt evt;

	public PlayerRoleGroupTool(int funcID,int groupID):base(FuncToolType.RoleGroup,funcID)
	{
		_config=RoleGroupConfig.get(groupID);

		evt=new RoleGroupEvt();
		evt.funcID=funcID;
	}

	protected override void toSetData(FuncToolData data)
	{
		base.toSetData(data);

		_d=(PlayerRoleGroupClientToolData)data;
	}

	public override void afterReadData()
	{
		base.afterReadData();

		if(_d.inviteList==null)
			_d.inviteList=new SList<InviteRoleGroupReceiveData>();

		_inviteList=_d.inviteList;

		PlayerRoleGroup playerRoleGroup;

		PlayerRoleGroupData[] values;
		PlayerRoleGroupData v;

		for(int i=(values=_d.groups.getValues()).Length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				playerRoleGroup=toCreatePlayerRoleGroup();
				playerRoleGroup.setGroupTool(this);
				playerRoleGroup.construct();
				playerRoleGroup.setData(v);

				_roleGroupDic.put(playerRoleGroup.groupID,playerRoleGroup);
				if(_config.isSingleJoin())
					_onlyOne=playerRoleGroup;

				playerRoleGroup.init();
				playerRoleGroup.afterReadData();
			}
		}
	}

	public override void dispose()
	{
		base.dispose();

		foreach(PlayerRoleGroup v in _roleGroupDic)
		{
			v.dispose();
			_roleGroupDic.remove(v.groupID);
		}

		_onlyOne=null;
	}

	public RoleGroupConfig getConfig()
	{
		return _config;
	}

	/** 创建角色自身玩家群 */
	protected virtual PlayerRoleGroup toCreatePlayerRoleGroup()
	{
		return new PlayerRoleGroup();
	}

	public override void onSecond(int delay)
	{
		base.onSecond(delay);

		LongObjectMap<PlayerApplyRoleGroupSelfData> applyDic=_d.applyDic;
		if(applyDic!=null && !applyDic.isEmpty())
		{
			long now=me.getTimeMillis();

			foreach(PlayerApplyRoleGroupSelfData v in applyDic)
			{
				if(v.disableTime>0 && now>v.disableTime)
				{
					applyDic.remove(v.data.groupID);
				}
			}
		}

		if(!_roleGroupDic.isEmpty())
		{
			foreach(PlayerRoleGroup v in _roleGroupDic)
			{
				v.onSecond(delay);
			}
		}
	}

	/** 获取角色自身玩家群逻辑 */
    public PlayerRoleGroup getRoleGroup(long groupID)
	{
		return _roleGroupDic.get(groupID);
	}

    /** 获取玩家群字典 */
    public LongObjectMap<PlayerRoleGroup> getRoleGroupDic()
    {
	    return _roleGroupDic;
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
    public bool isRoleGroupFull()
    {
	    return _config.eachPlayerGroupNum>0 && getRoleGroupNum()>=_config.eachPlayerGroupNum;
    }

    /** 是否可创建玩家群额外判定 */
    protected virtual bool canCreateRoleGroupEx()
    {
	    return true;
    }

    private PlayerRoleGroup toAddRoleGroup(PlayerRoleGroupData data)
    {
	    PlayerRoleGroup roleGroup=toCreatePlayerRoleGroup();
	    roleGroup.setGroupTool(this);
	    roleGroup.construct();
	    roleGroup.setData(data);

	    _d.groups.put(data.groupID,data);
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

		    _d.groups.remove(roleGroup.groupID);
	    }

	    roleGroup=toAddRoleGroup(data);

	    //移除申请
	    _d.applyDic.remove(data.groupID);

	    evt.groupID=data.groupID;
	    evt.type=type;
	    me.dispatch(GameEventType.JoinRoleGroup,evt);
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

	    roleGroup.dispose();

	    _d.groups.remove(roleGroup.groupID);

	    if(_config.isSingleJoin())
		    _onlyOne=null;

	    evt.groupID=groupID;
	    evt.type=type;
	    me.dispatch(GameEventType.LeaveRoleGroup,evt);
    }

    /** 添加申请玩家群数据到自身 */
    public void onAddApplySelf(PlayerApplyRoleGroupSelfData selfData)
    {
	    _d.applyDic.put(selfData.data.groupID,selfData);

	    evt.groupID=selfData.data.groupID;

	    me.dispatch(GameEventType.RoleGroupSelfApplyChange,evt);
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
		    _d.applyDic.remove(groupID);
	    }

	    me.dispatch(GameEventType.RoleGroupSelfApplyChange,evt);

	    if(BaseC.constlist.roleGroupHandleResult_needReback(result))
	    {
		    evt.groupID=groupID;

		    me.dispatch(GameEventType.RoleGroupSelfApplyResult,evt);
	    }
    }

    /** 邀请结果 */
    public void onInviteResult(RoleShowData data,int result)
    {
	    evt.roleShowData=data;
	    evt.result=result;

	    me.dispatch(GameEventType.RoleGroupInviteResult,evt);
    }

    //--行为--//

    /** 创建玩家群 */
    public void createRoleGroup(CreateRoleGroupData data)
    {
	    if(isRoleGroupFull())
	    {
		    me.warnLog("创建玩家群时,已达到数目限制");
		    return;
	    }

	    if(_config.createCostID>0 && !me.bag.hasCost(_config.createCostID))
	    {
		    me.warnLog("创建玩家群时,cost不满足");
		    return;
	    }

	    if(!me.role.checkRoleConditions(_config.createConditions,true))
	    {
		    me.warnLog("创建玩家群时,condition不满足");
		    return;
	    }

	    if(_config.nameCharLimit>0 && StringUtils.getCharMachineNum(data.name)>_config.nameCharLimit)
	    {
		    me.warnLog("创建玩家群时,名字过长");
		    return;
	    }

	    if(_config.noticeCharLimit>0 && StringUtils.getCharMachineNum(data.notice)>_config.noticeCharLimit)
	    {
		    me.warnLog("创建玩家群时,公告过长");
		    return;
	    }

	    //敏感字
	    if(BaseGameUtils.hasSensitiveWord(data.name))
	    {
		    Ctrl.warnLog("创建玩家群时,名字敏感");
		    GameC.info.showInfoCode(InfoCodeType.CreateRoleGroupFailed_nameIsSensitive);
		    return;
	    }

	    //敏感字
	    if(BaseGameUtils.hasSensitiveWord(data.notice))
	    {
		    Ctrl.warnLog("创建玩家群时,公告敏感");
		    GameC.info.showInfoCode(InfoCodeType.CreateRoleGroupFailed_noticeIsSensitive);
		    return;
	    }

	    if(!canCreateRoleGroupEx())
	    {
		    me.warnLog("创建玩家群时,ex不满足");
		    return;
	    }

	    //发送
	    me.send(FuncCreateRoleGroupRequest.create(_funcID,data));
    }

    /** 邀请入群 */
    public void inviteRoleGroup(long groupID,long targetID)
    {
	    if(!me.role.checkRoleConditions(_config.joinConditions,true))
	    {
		    me.warnLog("邀请入群时,condition不满足");
		    return;
	    }

	    if(groupID>0)
	    {
		    PlayerRoleGroup roleGroup=getRoleGroup(groupID);

		    if(roleGroup==null)
		    {
			    me.warnLog("邀请入群时,找不到群",groupID);
			    return;
		    }

		    if(roleGroup.isFull())
		    {
			    me.warnLog("邀请入群时,人数已满",groupID);
			    return;
		    }

		    if(!RoleGroupTitleType.canOperateApply(roleGroup.getSelfData().title))
		    {
			    Ctrl.warnLog("邀请入群时,邀请者没有权限",groupID,roleGroup.getSelfData().title);
			    return;
		    }

		    if(roleGroup.getMember(targetID)!=null)
		    {
			    me.warnLog("邀请入群时,已在群中",groupID);
			    return;
		    }
	    }
	    else
	    {
		    if(!_config.canInviteCreate)
		    {
			    me.warnLog("邀请入群直接创建时,不支持直接邀请创建");
			    return;
		    }

		    if(_config.eachPlayerGroupNum!=1)
		    {
			    me.warnLog("邀请入群直接创建时,必须为单一群");
			    return;
		    }

		    if(isRoleGroupFull())
		    {
			    me.warnLog("邀请入群直接创建时,已达到数目限制");
			    return;
		    }
	    }

	    me.send(FuncInviteRoleGroupRequest.create(_funcID,groupID,targetID));
    }

    /** 收到邀请入群 */
    public void receiveInviteRoleGroup(InviteRoleGroupReceiveData data)
    {
	    _inviteList.add(data);

	    if(_config.inviteKeepMax>0 && _inviteList.size()>_config.inviteKeepMax)
	    {
		    _inviteList.shift();
	    }

	    me.dispatch(GameEventType.ReceiveInviteRoleGroup,data);
    }

    /** 处理邀请信息 */
    public void handleInvite(InviteRoleGroupReceiveData tData,int result)
    {
	    int index=_inviteList.indexOf(tData);

	    if(index==-1)
	    {
		    me.warnLog("未找到邀请信息");
		    return;
	    }

	    _inviteList.remove(index);


	    long invitorID=tData.inviter.playerID;

	    if(isRoleGroupFull())
	    {
		    me.warnLog("处理邀请时,群已满",invitorID);
		    return;
	    }

	    if(!me.role.checkRoleConditions(_config.joinConditions,false))
	    {
		    me.warnLog("处理邀请时,目标condition不满足");
		    return;
	    }

	    //有群
	    if(tData.simpleData!=null)
	    {
		    PlayerRoleGroup roleGroup=getRoleGroup(tData.simpleData.groupID);

		    if(roleGroup!=null)
		    {
			    me.warnLog("处理邀请时,已在群中",invitorID);
			    return;
		    }
	    }

		me.send(FuncHandleInviteRoleGroupRequest.create(_funcID,tData.inviter.playerID,tData.getRoleGroupID(),result));
    }

    /** 申请加入玩家群 */
    public void applyRoleGroup(long groupID)
    {
	    if(!_config.canApply)
	    {
		    me.warnLog("申请加入玩家群时,配置不可申请");
		    return;
	    }

	    if(isRoleGroupFull())
	    {
		    me.warnLog("申请加入玩家群时,群数已满");
		    return;
	    }

	    PlayerRoleGroup roleGroup=getRoleGroup(groupID);

	    if(roleGroup!=null)
	    {
		    me.warnLog("申请加入玩家群时,已在该群");
		    return;
	    }

	    if(!me.role.checkRoleConditions(_config.joinConditions,true))
	    {
		    me.warnLog("申请加入玩家群时,自身条件不满足");
		    return;
	    }

	    PlayerApplyRoleGroupSelfData aData=_d.applyDic.get(groupID);

	    if(aData!=null)
	    {
		    me.warnLog("申请加入玩家群时,已申请过");
		    return;
	    }

	    if(_config.applyKeepMax>0 && _d.applyDic.size()>=_config.applyKeepMax)
	    {
		    me.warnLog("申请加入玩家群时,已达到申请上限");
		    return;
	    }


	    me.send(FuncApplyRoleGroupRequest.create(_funcID,groupID));
    }

    /** 更改收到邀请时是否直接同意 */
    public void changeCanInviteInAbs(bool value)
    {
	    _d.canInviteInAbs=value;
    }

}