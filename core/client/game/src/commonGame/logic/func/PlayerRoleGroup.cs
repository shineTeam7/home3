using System;
using ShineEngine;

/// <summary>
/// 玩家群逻辑
/// </summary>
public class PlayerRoleGroup
{
	/** 角色 */
	public Player me;

	/** 群ID */
	public long groupID;

	protected int _funcID;

	protected RoleGroupConfig _config;
	/** 工具 */
	protected PlayerRoleGroupTool _tool;

	protected RoleGroupEvt evt;

	/** 客户端数据 */
	protected PlayerRoleGroupData _d;
	/** 保存数据 */
	private PlayerRoleGroupSaveData _saveData;

	private RoleGroupLevelConfig _levelConfig;

	/** 职位数目统计 */
	private IntIntMap _memberTitleCount=new IntIntMap();
	/** 群主数据 */
	private PlayerRoleGroupMemberData _leader;

	private PlayerRoleGroupMemberData _selfData;

	private Comparison<PlayerApplyRoleGroupData> _applyComparator;


	/** 创建公会简版数据 */
	protected virtual RoleGroupSimpleData toCreateRoleGroupSimpleData()
	{
		return new RoleGroupSimpleData();
	}

	/** 构造 */
	public virtual void construct()
	{

	}

	public virtual void init()
	{
		_applyComparator=this.compareApply;
	}

	public virtual void dispose()
	{
		if(_d.funcTools!=null)
		{
			IntObjectMap<FuncToolData> dic=_d.funcTools.get(FuncToolType.Rank);

			if(dic!=null)
			{
				FuncToolData[] values2;
				FuncToolData v2;

				for(int i2=(values2=dic.getValues()).Length-1;i2>=0;--i2)
				{
					if((v2=values2[i2])!=null)
					{
						//设置数据
						me.func.getRoleGroupRankTool(v2.funcID).setData(v2);
					}
				}
			}
		}
	}

	/** 每秒间隔 */
	public virtual void onSecond(int delay)
	{
		if(!_d.applyDic.isEmpty())
		{
			long tt=DateControl.getTimeMillis()-_config.applyEnableTime*1000;

			foreach(PlayerApplyRoleGroupData v in _d.applyDic)
			{
				if(tt>v.time)
				{
					_d.applyDic.remove(v.data.showData.playerID);
				}
			}
		}
	}

	/** 每日 */
	public virtual void onDaily()
	{

	}

	public void setPlayer(Player player)
	{
		this.me=player;
	}

	public void setGroupTool(PlayerRoleGroupTool tool)
	{
		_tool=tool;
		_funcID=tool.getFuncID();
		_config=tool.getConfig();
		evt=tool.evt;
		setPlayer(tool.me);
	}

	/** 设置数据 */
	public void setData(PlayerRoleGroupData data)
	{
		_d=data;
		groupID=data.groupID;
		_levelConfig=RoleGroupLevelConfig.get(_config.id,data.level);

		_selfData=_d.members.get(me.role.playerID);
	}

	/** 获取客户端数据 */
	public PlayerRoleGroupData getData()
	{
		return _d;
	}

	/** 读数据后 */
	public void afterReadData()
	{
		PlayerRoleGroupMemberData[] values;
		PlayerRoleGroupMemberData v;

		for(int i=(values=_d.members.getValues()).Length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				if(v.title==RoleGroupTitleType.Leader)
				{
					_leader=v;
				}
			}
		}

		if(_d.funcTools!=null)
		{
			IntObjectMap<FuncToolData> dic=_d.funcTools.get(FuncToolType.Rank);

			if(dic!=null)
			{
				FuncToolData[] values2;
				FuncToolData v2;

				for(int i2=(values2=dic.getValues()).Length-1;i2>=0;--i2)
				{
					if((v2=values2[i2])!=null)
					{
						//设置数据
						me.func.getRoleGroupRankTool(v2.funcID).setData(v2);
					}
				}
			}
		}
	}

	/** 构造简版数据 */
	protected void makeSimpleData(RoleGroupSimpleData data)
	{
		data.groupID=groupID;
		data.level=_d.level;
		data.name=_d.name;
		data.notice=_d.notice;
	}


	/** 获取自己职位 */
	public int getTitle()
	{
		return _selfData.title;
	}

	/** 此时是否可操作进出 */
	protected bool canHandleInOut()
	{
		return true;
	}

	/** 获取成员 */
	public PlayerRoleGroupMemberData getMember(long playerID)
	{
		return _d.members.get(playerID);
	}

	/** 获取自身 */
	public PlayerRoleGroupMemberData getSelfData()
	{
		return _selfData;
	}

	/** 成员数目 */
	public int getMemberNum()
	{
		return _d.members.size();
	}

	/** 是否人数已满 */
	public bool isFull()
	{
		return _levelConfig.maxNum>0 && getMemberNum()>=_levelConfig.maxNum;
	}

	/** 创建简版数据 */
	public RoleGroupSimpleData createSimpleData()
	{
		RoleGroupSimpleData re=toCreateRoleGroupSimpleData();
		makeSimpleData(re);
		return re;
	}

	/** 添加成员 */
	public void onAddMember(PlayerRoleGroupMemberData mData,int type)
	{
		_d.members.put(mData.playerID,mData);

		if(mData.title==RoleGroupTitleType.Leader)
		{
			_leader=mData;
		}

		evt.type=type;
		evt.groupID=groupID;
		evt.targetID=mData.playerID;
		me.dispatch(GameEventType.AddRoleGroupMember,evt);
	}

	/** 移除队员 */
	public void onRemoveMember(long playerID,int type)
	{
		PlayerRoleGroupMemberData mData=_d.members.remove(playerID);

		if(mData!=null)
		{
			if(mData.title==RoleGroupTitleType.Leader)
			{
				_leader=null;
			}
		}

		evt.type=type;
		evt.groupID=groupID;
		evt.targetID=playerID;
		me.dispatch(GameEventType.RemoveRoleGroupMember,evt);
	}

	/** 设置职位 */
	protected void doSetTitle(PlayerRoleGroupMemberData member,int title,bool needDispatch)
	{
		if(needDispatch)
		{
			evt.oldTitle=member.title;
		}

		_memberTitleCount.addValue(member.title,-1);
		if(member.title==RoleGroupTitleType.Leader)
			_leader=null;

		member.title=title;

		_memberTitleCount.addValue(member.title,1);
		if(member.title==RoleGroupTitleType.Leader)
			_leader=member;

		if(needDispatch)
		{
			evt.newTitle=title;
			evt.groupID=groupID;
			evt.targetID=member.playerID;

			me.dispatch(GameEventType.RemoveRoleGroupMember,evt);
		}
	}

	/** 职位变更 */
	public void onSetTitle(long memberID,int title)
	{
		PlayerRoleGroupMemberData member=getMember(memberID);

		if(member!=null)
		{
			doSetTitle(member,title,true);
		}
	}

	/** 更换队长 */
	public void onChangeLeader(long lastLeaderID,long leaderID)
	{
		PlayerRoleGroupMemberData lastLeader=getMember(lastLeaderID);

		if(lastLeader!=null)
		{
			doSetTitle(lastLeader,_config.joinTitle,false);
		}

		PlayerRoleGroupMemberData leader=getMember(leaderID);

		if(leader!=null)
		{
			doSetTitle(leader,RoleGroupTitleType.Leader,false);
		}

		evt.targetID=lastLeaderID;
		evt.groupID=groupID;
		me.dispatch(GameEventType.ChangeRoleGroupLeader,evt);
	}

	/** 执行改变 */
	protected void doRoleGroupChange(RoleGroupChangeData data)
	{
		switch(data.type)
		{
			case RoleGroupChangeType.Name:
			{
				_d.name=data.arg1;
			}
				break;
			case RoleGroupChangeType.Notice:
			{
				_d.notice=data.arg1;
			}
				break;
			case RoleGroupChangeType.Level:
			{
				_d.level=data.arg0;
			}
				break;
			case RoleGroupChangeType.CanApplyInAbs:
			{
				_d.canApplyInAbs=data.arg0==1;
			}
				break;
		}
	}

	/** 玩家群变更 */
	public void onRoleGroupChange(RoleGroupChangeData data)
	{
		doRoleGroupChange(data);

		evt.groupID=groupID;
		evt.changeData=data;
		me.dispatch(GameEventType.ChangeRoleGroupData,evt);
	}

	/** 执行改变 */
	protected void doRoleGroupChange(PlayerRoleGroupMemberData mData,RoleGroupMemberChangeData data)
	{
		switch(data.type)
		{
			case RoleGroupMemberChangeType.Title:
			{
				doSetTitle(mData,data.arg0,false);
			}
				break;
		}
	}

	/** 玩家群变更 */
	public void onRoleGroupMemberChange(long memberID,RoleGroupMemberChangeData data)
	{
		PlayerRoleGroupMemberData member=getMember(memberID);

		if(member!=null)
		{
			doRoleGroupChange(member,data);
		}

		evt.groupID=groupID;
		evt.targetID=memberID;
		evt.memberChangeData=data;
		me.dispatch(GameEventType.ChangeRoleGroupMemberData,evt);
	}

	/** 成员外显数据变化 */
	public void onMemberRoleShowChange(long memberID,RoleShowChangeData data)
	{
		PlayerRoleGroupMemberData member=getMember(memberID);

		if(member!=null)
		{
			member.socialData.onChange(data);

			evt.groupID=groupID;
			evt.targetID=memberID;
			evt.type=data.type;
			me.dispatch(GameEventType.ChangeRoleGroupMemberRoleShowData,evt);
		}
	}

	/** 添加申请 */
	public void onAddApply(PlayerApplyRoleGroupData data)
	{
		//放入邀请组
		OtherUtils.putObjInDicWithMax(data.data.showData.playerID,data,_d.applyDic,_config.applyKeepMax,_applyComparator);

		evt.groupID=groupID;
		evt.targetID=data.data.showData.playerID;
		evt.applyData=data;
		me.dispatch(GameEventType.RoleGroupReceiveApply,evt);

		evt.groupID=groupID;
		evt.targetID=data.data.showData.playerID;
		evt.applyData=data;
		me.dispatch(GameEventType.RoleGroupApplyChange,evt);
	}

	/** 申请被处理 */
	public void onApplyHandleResult(long targetID,int result)
	{
		PlayerApplyRoleGroupData aData=_d.applyDic.remove(targetID);

		if(aData==null)
		{
			me.warnLog("收到处理结果时,找不到申请数据");
			return;
		}

		evt.groupID=groupID;
		evt.targetID=targetID;
		evt.result=result;
		evt.applyData=aData;

		me.dispatch(GameEventType.RoleGroupApplyChange,evt);
	}

	protected int compareApply(PlayerApplyRoleGroupData arg0,PlayerApplyRoleGroupData arg1)
	{
		return MathUtils.longCompare(arg0.time,arg1.time);
	}

	//--行为部分--//

	/** 解散 */
	public void disband()
	{
		if(!canDisband())
		{
			me.warnLog("不可离开");
			return;
		}

		if(getTitle()!=RoleGroupTitleType.Leader)
		{
			me.warnLog("解散玩家群时，不是群主");
			return;
		}

		if(!canHandleInOut())
		{
			me.warnLog("解散玩家群时，此时不可操作");
			return;
		}

		me.send(FuncDisbandRoleGroupRequest.create(_funcID,groupID));
	}

	/** 判断可离开条件 */
	protected bool canDisband()
	{
		return true;
	}

	/** 离开 */
	public void leave()
	{
		if(!canLeave())
		{
			me.warnLog("不可离开");
			return;
		}

		if(!canHandleInOut())
		{
			me.warnLog("主动退出玩家群时，此时不可操作");
			return;
		}

		me.send(FuncLeaveRoleGroupRequest.create(_funcID,groupID));
	}

	/** 判断可离开条件 */
	protected bool canLeave()
	{
		return true;
	}

	/** 禅让群主 */
	public void changeLeader(long playerID)
	{
		if((getMember(playerID))==null)
		{
			me.warnLog("changeLeader时,找不到目标",playerID);
			return;
		}

		if(playerID==me.role.playerID)
		{
			me.warnLog("changeLeader时,目标是自己");
			return;
		}

		me.send(FuncChangeLeaderRoleGroupRequest.create(_funcID,groupID,playerID));
	}

	/** 设置职位 */
	public void setTitle(long memberID,int title)
	{
		int canSetTitle=RoleGroupTitleConfig.get(_selfData.title).canSetTitle;

		if(canSetTitle<=0 || canSetTitle>title)
		{
			me.warnLog("设置职位时,没有权限",_selfData.title,title);
			return;
		}

		PlayerRoleGroupMemberData member=getMember(memberID);

		if(member==null)
		{
			me.warnLog("设置职位时,找不到目标成员",memberID);
			return;
		}

		if(member.title==RoleGroupTitleType.Leader || title==RoleGroupTitleType.Leader)
		{
			me.warnLog("设置职位时,不能设置队长",memberID,title);
			return;
		}

		if(member.title<canSetTitle)
		{
			me.warnLog("设置职位时,没有权限2",_selfData.title,member.title);
			return;
		}

		if(member.title==title)
		{
			me.warnLog("设置职位时,当前已是该职位",_selfData.title,member.title);
			return;
		}

		int num=RoleGroupTitleConfig.get(title).num;

		//超过数目
		if(num>0 && _memberTitleCount.get(title)>=num)
		{
			me.warnLog("设置职位时,超过容纳数目",title);
			return;
		}

		me.send(FuncSetTitleRoleGroupRequest.create(_funcID,groupID,memberID,title));
	}

	/** 踢出成员 */
	public void kickMember(long memberID)
	{
		PlayerRoleGroupMemberData member=getMember(memberID);

		if(member==null)
		{
			me.warnLog("踢出成员时,不存在",member);
			return;
		}

		if(me.role.playerID==memberID)
		{
			me.warnLog("踢出成员时,不可踢出自己",memberID);
			return;
		}

		int canKickTitle=RoleGroupTitleConfig.get(_selfData.title).canKickTitle;

		if(canKickTitle<=0 || member.title<canKickTitle)
		{
			me.warnLog("踢出成员时,权限不足",_selfData.title,member.title);
			return;
		}

		if(member.title==RoleGroupTitleType.Leader)
		{
			me.warnLog("踢出成员时,不可提出队长");
			return;
		}

		me.send(FuncKickMemberRoleGroupRequest.create(_funcID,groupID,memberID));
	}

	/** 处理申请 */
	public void handleApply(long targetID,int result)
	{
		if(isFull())
		{
			me.warnLog("处理申请时,人数已满");
			return;
		}

		if((getMember(targetID))!=null)
		{
			me.warnLog("处理申请时,成员已存在");
			return;
		}

		//不可处理申请
		if(!RoleGroupTitleType.canOperateApply(_selfData.title))
		{
			me.warnLog("处理申请时,不可处理申请");
			return;
		}

		if(!_d.applyDic.contains(targetID))
		{
			me.warnLog("处理申请时,不在申请组");
			return;
		}

		me.send(FuncHandleApplyRoleGroupRequest.create(_funcID,groupID,targetID,result));
	}

	/** 改群名 */
	public void changeName(String name)
	{
		if(!RoleGroupTitleConfig.get(_selfData.title).canChangeName)
		{
			me.warnLog("修改群名时,权限不够");
			return;
		}

		if(_config.nameCharLimit>0 && StringUtils.getCharMachineNum(name)>_config.nameCharLimit)
		{
			me.warnLog("修改群名时,名字过长");
			return;
		}

		//敏感字
		if(BaseGameUtils.hasSensitiveWord(name))
		{
			Ctrl.warnLog("修改群名时,名字敏感");
			GameC.info.showInfoCode(InfoCodeType.CreateRoleGroupFailed_nameIsSensitive);
			return;
		}

		me.send(FuncChangeRoleGroupNameRequest.create(_funcID,groupID,name));
	}

	/** 改公告 */
	public void changeNotice(String notice)
	{
		if(!RoleGroupTitleConfig.get(_selfData.title).canChangeNotice)
		{
			me.warnLog("修改群公告时,权限不够");
			return;
		}

		if(_config.noticeCharLimit>0 && StringUtils.getCharMachineNum(notice)>_config.noticeCharLimit)
		{
			me.warnLog("修改群公告时,名字过长");
			return;
		}

		//敏感字
		if(BaseGameUtils.hasSensitiveWord(notice))
		{
			Ctrl.warnLog("修改群公告时,名字敏感");
			GameC.info.showInfoCode(InfoCodeType.CreateRoleGroupFailed_nameIsSensitive);
			return;
		}

		me.send(FuncChangeRoleGroupNoticeRequest.create(_funcID,groupID,notice));
	}

	/** 更改权限 */
	public void changeApplyType(bool canApplyInAbs)
	{
		if(!RoleGroupTitleConfig.get(_selfData.title).canChangeApplyInAbs)
		{
			me.warnLog("修改群公告时,权限不够");
			return;
		}

		me.send(FuncChangeRoleGroupCanApplyInAbsRequest.create(_funcID,groupID,canApplyInAbs));
	}

	public void onAddInfoLog(InfoLogData data)
	{
		SQueue<InfoLogData> queue;

		(queue=_d.logQueue).offer(data);

		if(queue.size()>_config.infoLogKeepNum)
		{
			queue.poll();
		}

		evt.groupID=groupID;
		me.dispatch(GameEventType.RoleGroupRefreshInfoLog,evt);
	}

	/** 从附加数据读取 */
	protected void readFromExData(PlayerRoleGroupExData data)
	{
		_d.exp=data.exp;
		_d.members=data.members;

		_selfData=_d.members.get(me.role.playerID);

		PlayerRoleGroupMemberData[] values;
		PlayerRoleGroupMemberData v;

		for(int i=(values=_d.members.getValues()).Length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				if(v.title==RoleGroupTitleType.Leader)
				{
					_leader=v;
				}
			}
		}
	}

	/** 玩家群额外 */
	public void onGetRoleGroupEx(PlayerRoleGroupExData data)
	{
		readFromExData(data);

		evt.groupID=groupID;
		me.dispatch(GameEventType.RefreshRoleGroupEx,evt);
	}

	/** 获取Rank简版数据 */
	public RankSimpleData getRankSimpleData(int funcID)
	{
		IntObjectMap<FuncToolData> dic=_d.funcTools.get(FuncToolType.Rank);

		if(dic==null)
			return null;

		return (RankSimpleData)dic.get(funcID);
	}
}