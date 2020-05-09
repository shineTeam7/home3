package com.home.commonGame.logic.func;

import com.home.commonBase.config.game.RoleGroupConfig;
import com.home.commonBase.config.game.RoleGroupLevelConfig;
import com.home.commonBase.config.game.RoleGroupTitleConfig;
import com.home.commonBase.config.game.enumT.RoleGroupChangeTypeConfig;
import com.home.commonBase.config.game.enumT.RoleGroupMemberChangeTypeConfig;
import com.home.commonBase.config.game.enumT.RoleShowDataPartTypeConfig;
import com.home.commonBase.constlist.generate.FuncToolType;
import com.home.commonBase.constlist.generate.InfoCodeType;
import com.home.commonBase.constlist.generate.RoleGroupChangeType;
import com.home.commonBase.constlist.generate.RoleGroupMemberChangeType;
import com.home.commonBase.constlist.generate.RoleGroupTitleType;
import com.home.commonBase.data.func.FuncToolData;
import com.home.commonBase.data.role.RoleShowChangeData;
import com.home.commonBase.data.scene.scene.SceneLocationData;
import com.home.commonBase.data.social.chat.RoleChatData;
import com.home.commonBase.data.social.chat.SendPlayerChatOWData;
import com.home.commonBase.data.social.rank.RankSimpleData;
import com.home.commonBase.data.social.rank.RoleGroupRankData;
import com.home.commonBase.data.social.roleGroup.PlayerRoleGroupData;
import com.home.commonBase.data.social.roleGroup.PlayerRoleGroupExData;
import com.home.commonBase.data.social.roleGroup.PlayerRoleGroupMemberData;
import com.home.commonBase.data.social.roleGroup.PlayerRoleGroupSaveData;
import com.home.commonBase.data.social.roleGroup.RoleGroupChangeData;
import com.home.commonBase.data.social.roleGroup.RoleGroupMemberChangeData;
import com.home.commonBase.data.social.roleGroup.RoleGroupSimpleData;
import com.home.commonBase.data.social.roleGroup.work.PlayerToRoleGroupTCCWData;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.utils.BaseGameUtils;
import com.home.commonGame.global.GameC;
import com.home.commonGame.net.request.func.rank.FuncRefreshRankRequest;
import com.home.commonGame.net.request.func.rank.FuncRefreshRoleGroupRankRequest;
import com.home.commonGame.net.request.func.rank.FuncResetRoleGroupRankRequest;
import com.home.commonGame.net.request.func.roleGroup.FuncRefeshTitleRoleGroupRequest;
import com.home.commonGame.net.request.func.roleGroup.FuncSendChangeLeaderRoleGroupRequest;
import com.home.commonGame.net.request.func.roleGroup.FuncSendRoleGroupAddMemberRequest;
import com.home.commonGame.net.request.func.roleGroup.FuncSendRoleGroupChangeRequest;
import com.home.commonGame.net.request.func.roleGroup.FuncSendRoleGroupMemberChangeRequest;
import com.home.commonGame.net.request.func.roleGroup.FuncSendRoleGroupMemberRoleShowChangeRequest;
import com.home.commonGame.net.request.func.roleGroup.FuncSendRoleGroupRemoveMemberRequest;
import com.home.commonGame.net.serverRequest.game.base.PlayerGameToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.base.FuncRoleGroupToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncChangeLeaderRoleGroupToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncChangeRoleGroupApplyTypeToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncChangeRoleGroupNameToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncChangeRoleGroupNoticeToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncDisbandRoleGroupToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncHandleApplyRoleGroupToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncKickMemberRoleGroupToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncLeaveRoleGroupToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncRoleGroupEnterOwnSceneToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncRoleGroupRefreshRoleShowDataToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.roleGroup.FuncSetTitleRoleGroupToGameServerRequest;
import com.home.commonGame.net.serverRequest.game.social.SendPlayerChatToPlayerServerRequest;
import com.home.commonGame.part.player.Player;
import com.home.commonGame.tool.func.PlayerRoleGroupTool;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.IntIntMap;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.LongObjectMap;
import com.home.shine.utils.StringUtils;

/** 角色身上玩家群逻辑 */
public class PlayerRoleGroup
{
	/** 角色 */
	public Player me;
	
	/** 群ID */
	public long groupID;
	/** 创建区id */
	public int createAreaID;
	
	protected int _funcID;
	
	protected RoleGroupConfig _config;
	/** 工具 */
	protected PlayerRoleGroupTool _tool;
	
	/** 客户端数据 */
	protected PlayerRoleGroupData _d;
	/** 保存数据 */
	private PlayerRoleGroupSaveData _saveData;
	
	private RoleGroupLevelConfig _levelConfig;
	
	/** 职位数目统计 */
	private IntIntMap _memberTitleCount=new IntIntMap();
	
	/** 群主数据 */
	protected PlayerRoleGroupMemberData _leader;
	
	/** 自己的数据 */
	protected PlayerRoleGroupMemberData _selfData;
	
	public PlayerRoleGroup()
	{
	
	}
	
	/** 创建角色存库数据 */
	public PlayerRoleGroupSaveData toCreatePlayerRoleGroupSaveData()
	{
		return new PlayerRoleGroupSaveData();
	}
	
	/** 创建公会简版数据 */
	protected RoleGroupSimpleData toCreateRoleGroupSimpleData()
	{
		return new RoleGroupSimpleData();
	}
	
	/** 创建公会额外数据 */
	protected PlayerRoleGroupExData toCreatePlayerRoleGroupExData()
	{
		return new PlayerRoleGroupExData();
	}
	
	/** 构造 */
	public void construct()
	{
	
	}
	
	public void init()
	{
	
	}
	
	public void dispose()
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
		setPlayer(tool.me);
	}
	
	/** 设置数据 */
	public void setSaveData(PlayerRoleGroupSaveData data)
	{
		_saveData=data;
		groupID=data.groupID;
		createAreaID= BaseC.logic.getAreaIDByLogicID(groupID);
	}
	
	/** 设置数据 */
	public void setData(PlayerRoleGroupData data)
	{
		_d=data;
		groupID=data.groupID;
		createAreaID=BaseC.logic.getAreaIDByLogicID(groupID);
		
		_levelConfig=RoleGroupLevelConfig.get(_config.id,data.level);
		
		makePlayerRoleGroupSaveData(_saveData,data);
		
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
		_d.members.forEachValue(v->
		{
			if(v.title==RoleGroupTitleType.Leader)
			{
				_leader=v;
			}
		});
	}
	
	/** 构造 */
	protected void makePlayerRoleGroupSaveData(PlayerRoleGroupSaveData re,PlayerRoleGroupData data)
	{
	
	}
	
	/** 构造简版数据 */
	protected void makeSimpleData(RoleGroupSimpleData data)
	{
		data.key=groupID;
		data.groupID=groupID;
		data.level=_d.level;
		data.name=_d.name;
		data.notice=_d.notice;
		data.memberNum=_d.members.size();
	}
	
	/** 构造额外数据 */
	protected void makeExData(PlayerRoleGroupExData data)
	{
		data.groupID=groupID;
		data.exp=_d.exp;
		data.members=_d.members;
	}
	
	/** 创建存库数据 */
	public PlayerRoleGroupSaveData createSaveData(PlayerRoleGroupData data)
	{
		PlayerRoleGroupSaveData re=toCreatePlayerRoleGroupSaveData();
		makePlayerRoleGroupSaveData(re,data);
		return re;
	}
	
	/** 创建额外数据 */
	public PlayerRoleGroupExData createRoleGroupExData()
	{
		PlayerRoleGroupExData re=toCreatePlayerRoleGroupExData();
		makeExData(re);
		return re;
	}
	
	/** 获取自己职位 */
	public int getTitle()
	{
		return _selfData.title;
	}
	
	/** 此时是否可操作进出 */
	protected boolean canHandleInOut()
	{
		return true;
	}
	
	/** 获取成员 */
	public PlayerRoleGroupMemberData getMember(long playerID)
	{
		return _d.members.get(playerID);
	}
	
	/** 获取成员组 */
	public LongObjectMap<PlayerRoleGroupMemberData> getMemberDic()
	{
		return _d.members;
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
	public boolean isFull()
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
	
	/** 是否在当前逻辑服 */
	public boolean isCurrentGame()
	{
		return GameC.main.isCurrentGame(groupID);
	}
	
	/** 发送消息到RoleGroup */
	public void sendToRoleGroup(FuncRoleGroupToGameServerRequest request)
	{
		request.sendToSourceGameByLogicID(groupID);
	}
	
	/** 广播消息 */
	public void radioMembers(PlayerGameToGameServerRequest request)
	{
		request.write();
		
		_d.members.forEachValue(v->
		{
			//在线
			if(v.socialData.isOnline)
			{
				request.sendToPlayer(v.playerID);
			}
		});
	}
	
	/** 添加成员 */
	public void onAddMember(PlayerRoleGroupMemberData mData,int type)
	{
		_d.members.put(mData.playerID,mData);
		
		if(mData.title==RoleGroupTitleType.Leader)
		{
			_leader=mData;
		}
		
		me.send(FuncSendRoleGroupAddMemberRequest.create(_funcID,groupID,mData,type));
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
			
			onRemoveMember(mData);
		}
		
		me.send(FuncSendRoleGroupRemoveMemberRequest.create(_funcID,groupID,playerID,type));
	}
	
	protected void onRemoveMember(PlayerRoleGroupMemberData mData)
	{
	
	}
	
	/** 设置职位 */
	protected void doSetTitle(PlayerRoleGroupMemberData member,int title,boolean needSend)
	{
		_memberTitleCount.addValue(member.title,-1);
		if(member.title==RoleGroupTitleType.Leader)
			_leader=null;
		
		member.title=title;
		
		_memberTitleCount.addValue(member.title,1);
		if(member.title==RoleGroupTitleType.Leader)
			_leader=member;
		
		if(needSend)
		{
			me.send(FuncRefeshTitleRoleGroupRequest.create(_funcID,groupID,member.playerID,title));
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
		
		me.send(FuncSendChangeLeaderRoleGroupRequest.create(_funcID,groupID,lastLeaderID,leaderID));
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
		
		if(RoleGroupChangeTypeConfig.get(data.type).needSendClientAbs)
		{
			me.send(FuncSendRoleGroupChangeRequest.create(_funcID,groupID,data));
		}
	}
	
	/** 执行改变成员信息 */
	protected void doRoleGroupMemberChange(PlayerRoleGroupMemberData member,RoleGroupMemberChangeData data)
	{
		switch(data.type)
		{
			case RoleGroupMemberChangeType.Title:
			{
				doSetTitle(member,data.arg0,true);
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
			doRoleGroupMemberChange(member,data);
			
			if(RoleGroupMemberChangeTypeConfig.get(data.type).needSendClientAbs)
			{
				me.send(FuncSendRoleGroupMemberChangeRequest.create(_funcID,groupID,memberID,data));
			}
		}
	}
	
	//行为
	
	/** 解散 */
	public void disband()
	{
		if(!canDisband())
		{
			me.warningInfoCode(InfoCodeType.PlayerGroup_player_disband_canNotDisband);
			return;
		}
		
		if(getTitle()!=RoleGroupTitleType.Leader)
		{
			me.warningInfoCode(InfoCodeType.PlayerGroup_player_disband_isNotLeader);
			return;
		}
		
		if(!canHandleInOut())
		{
			me.warningInfoCode(InfoCodeType.PlayerGroup_player_disband_canNotHandle);
			return;
		}
		
		if(isCurrentGame())
		{
			long groupID=this.groupID;
			
			me.addMainFunc(()->
			{
				RoleGroup roleGroup;
				if((roleGroup=GameC.main.getRoleGroup(_funcID,groupID))!=null)
				{
					roleGroup.disband();
				}
			});
		}
		else
		{
			sendToRoleGroup(FuncDisbandRoleGroupToGameServerRequest.create(_funcID,groupID));
		}
	}
	
	/** 判断可离开条件 */
	protected boolean canDisband()
	{
		return true;
	}
	
	/** 主动退出 */
	public void leave()
	{
		if(!canLeave())
		{
			me.warningInfoCode(InfoCodeType.PlayerGroup_player_leave_canNotLeave);
			return;
		}
		
		if(!canHandleInOut())
		{
			me.warningInfoCode(InfoCodeType.PlayerGroup_player_leave_canNotHandle);
			return;
		}
		
		if(isCurrentGame())
		{
			long groupID=this.groupID;
			long playerID=me.role.playerID;
			
			me.addMainFunc(()->
			{
				RoleGroup roleGroup;
				if((roleGroup=GameC.main.getRoleGroup(_funcID,groupID))!=null)
				{
					roleGroup.playerLeave(playerID);
				}
			});
		}
		else
		{
			sendToRoleGroup(FuncLeaveRoleGroupToGameServerRequest.create(_funcID,groupID,me.role.playerID));
		}
	}
	
	/** 判断可离开条件 */
	protected boolean canLeave()
	{
		return true;
	}
	
	/** 禅让群主 */
	public void changeLeader(long playerID)
	{
		if((getMember(playerID))==null)
		{
			me.warningInfoCode(InfoCodeType.PlayerGroup_player_changeLeader_canNotFindTarget,playerID);
			return;
		}
		
		if(playerID==me.role.playerID)
		{
			me.warningInfoCode(InfoCodeType.PlayerGroup_player_changeLeader_targetIsSelf);
			return;
		}
		
		if(isCurrentGame())
		{
			long groupID=this.groupID;
			long selfID=me.role.playerID;
			
			me.addMainFunc(()->
			{
				RoleGroup roleGroup;
				if((roleGroup=GameC.main.getRoleGroup(_funcID,groupID))!=null)
				{
					roleGroup.changeLeader(selfID,playerID);
				}
			});
		}
		else
		{
			sendToRoleGroup(FuncChangeLeaderRoleGroupToGameServerRequest.create(_funcID,groupID,me.role.playerID,playerID));
		}
	}
	
	/** 设置职位 */
	public void setTitle(long memberID,int title)
	{
		int canSetTitle=RoleGroupTitleConfig.get(_selfData.title).canSetTitle;
		
		if(canSetTitle<=0 || canSetTitle>title)
		{
			me.warningInfoCode(InfoCodeType.PlayerGroup_player_setTitle_notAllowed,_selfData.title,title);
			return;
		}
		
		PlayerRoleGroupMemberData member=getMember(memberID);
		
		if(member==null)
		{
			me.warningInfoCode(InfoCodeType.PlayerGroup_player_setTitle_canNotFindTarget,memberID);
			return;
		}
		
		if(member.title==RoleGroupTitleType.Leader || title==RoleGroupTitleType.Leader)
		{
			me.warningInfoCode(InfoCodeType.PlayerGroup_player_setTitle_canNotSetLeader,memberID,title);
			return;
		}
		
		if(member.title<canSetTitle)
		{
			me.warningInfoCode(InfoCodeType.PlayerGroup_player_setTitle_targetLevelHigher,_selfData.title,member.title);
			return;
		}
		
		if(member.title==title)
		{
			me.warningInfoCode(InfoCodeType.PlayerGroup_player_setTitle_titleIsSame,_selfData.title,member.title);
			return;
		}
		
		int num=RoleGroupTitleConfig.get(title).num;
		
		//超过数目
		if(num>0 && _memberTitleCount.get(title)>=num)
		{
			me.warningInfoCode(InfoCodeType.PlayerGroup_player_setTitle_titleNumFull,title);
			return;
		}
		
		if(isCurrentGame())
		{
			long groupID=this.groupID;
			long selfID=me.role.playerID;
			
			me.addMainFunc(()->
			{
				RoleGroup roleGroup;
				if((roleGroup=GameC.main.getRoleGroup(_funcID,groupID))!=null)
				{
					roleGroup.setTitle(selfID,memberID,title);
				}
			});
		}
		else
		{
			sendToRoleGroup(FuncSetTitleRoleGroupToGameServerRequest.create(_funcID,groupID,me.role.playerID,memberID,title));
		}
	}
	
	/** 踢出成员 */
	public void kickMember(long memberID)
	{
		PlayerRoleGroupMemberData member=getMember(memberID);
		
		if(member==null)
		{
			me.warningInfoCode(InfoCodeType.PlayerGroup_player_kickMember_memberNotExist,memberID);
			return;
		}
		
		if(me.role.playerID==memberID)
		{
			me.warningInfoCode(InfoCodeType.PlayerGroup_player_kickMember_canNotKickSelf,memberID);
			return;
		}
		
		int canKickTitle=RoleGroupTitleConfig.get(_selfData.title).canKickTitle;
		
		if(canKickTitle<=0 || member.title<canKickTitle)
		{
			me.warningInfoCode(InfoCodeType.PlayerGroup_player_kickMember_notAllowed,_selfData.title,member.title);
			return;
		}
		
		if(member.title==RoleGroupTitleType.Leader)
		{
			me.warningInfoCode(InfoCodeType.PlayerGroup_player_kickMember_canNotKickLeader);
			return;
		}
		
		if(isCurrentGame())
		{
			long groupID=this.groupID;
			long selfID=me.role.playerID;
			
			me.addMainFunc(()->
			{
				RoleGroup roleGroup;
				if((roleGroup=GameC.main.getRoleGroup(_funcID,groupID))!=null)
				{
					roleGroup.kickMember(selfID,memberID);
				}
			});
		}
		else
		{
			sendToRoleGroup(FuncKickMemberRoleGroupToGameServerRequest.create(_funcID,groupID,me.role.playerID,memberID));
		}
	}
	
	/** 处理申请 */
	public void handleApply(long targetID,int result)
	{
		if(isFull())
		{
			me.warningInfoCode(InfoCodeType.PlayerGroup_player_handleApply_playerNumFull);
			return;
		}
		
		if((getMember(targetID))!=null)
		{
			me.warningInfoCode(InfoCodeType.PlayerGroup_player_handleApply_memberExist);
			return;
		}
		
		//不可处理申请
		if(!RoleGroupTitleType.canOperateApply(_selfData.title))
		{
			me.warningInfoCode(InfoCodeType.PlayerGroup_player_handleApply_canNotHandle);
			return;
		}
		
		if(isCurrentGame())
		{
			long groupID=this.groupID;
			long selfID=me.role.playerID;
			
			me.addMainFunc(()->
			{
				RoleGroup roleGroup;
				if((roleGroup=GameC.main.getRoleGroup(_funcID,groupID))!=null)
				{
					roleGroup.handleApply(selfID,targetID,result);
				}
			});
		}
		else
		{
			sendToRoleGroup(FuncHandleApplyRoleGroupToGameServerRequest.create(_funcID,groupID,me.role.playerID,targetID,result));
		}
	}
	
	/** 改群名 */
	public void changeName(String name)
	{
		if(!RoleGroupTitleConfig.get(_selfData.title).canChangeName)
		{
			me.warningInfoCode(InfoCodeType.PlayerGroup_player_changeName_notAllowed);
			return;
		}
		
		if(_config.nameCharLimit>0 && StringUtils.getCharMachineNum(name)>_config.nameCharLimit)
		{
			me.warningInfoCode(InfoCodeType.PlayerGroup_player_changeName_nameTooLong);
			return;
		}
		
		//敏感字
		if(BaseGameUtils.hasSensitiveWord(name))
		{
			me.warningInfoCode(InfoCodeType.PlayerGroup_player_changeName_nameSensitive);
			return;
		}
		
		if(isCurrentGame())
		{
			long groupID=this.groupID;
			long selfID=me.role.playerID;
			
			me.addMainFunc(()->
			{
				RoleGroup roleGroup;
				if((roleGroup=GameC.main.getRoleGroup(_funcID,groupID))!=null)
				{
					roleGroup.changeName(selfID,name);
				}
			});
		}
		else
		{
			sendToRoleGroup(FuncChangeRoleGroupNameToGameServerRequest.create(_funcID,groupID,me.role.playerID,name));
		}
	}
	
	/** 改公告 */
	public void changeNotice(String notice)
	{
		if(!RoleGroupTitleConfig.get(_selfData.title).canChangeNotice)
		{
			me.warningInfoCode(InfoCodeType.PlayerGroup_player_changeNotice_notAllowed);
			return;
		}
		
		if(_config.noticeCharLimit>0 && StringUtils.getCharMachineNum(notice)>_config.noticeCharLimit)
		{
			me.warningInfoCode(InfoCodeType.PlayerGroup_player_changeNotice_nameTooLong);
			return;
		}
		
		//敏感字
		if(BaseGameUtils.hasSensitiveWord(notice))
		{
			me.warningInfoCode(InfoCodeType.PlayerGroup_player_changeNotice_nameSensitive);
			return;
		}
		
		if(isCurrentGame())
		{
			long groupID=this.groupID;
			long selfID=me.role.playerID;
			
			me.addMainFunc(()->
			{
				RoleGroup roleGroup;
				if((roleGroup=GameC.main.getRoleGroup(_funcID,groupID))!=null)
				{
					roleGroup.changeNotice(selfID,notice);
				}
			});
		}
		else
		{
			sendToRoleGroup(FuncChangeRoleGroupNoticeToGameServerRequest.create(_funcID,groupID,me.role.playerID,notice));
		}
	}
	
	/** 修改是否开启自动批准申请 */
	public void changeCanApplyInAbs(boolean canApplyInAbs)
	{
		if(!RoleGroupTitleConfig.get(_selfData.title).canChangeApplyInAbs)
		{
			me.warningInfoCode(InfoCodeType.PlayerGroup_player_changeAutoApply_notAllowed);
			return;
		}
		
		if(isCurrentGame())
		{
			long groupID=this.groupID;
			long selfID=me.role.playerID;
			
			me.addMainFunc(()->
			{
				RoleGroup roleGroup;
				if((roleGroup=GameC.main.getRoleGroup(_funcID,groupID))!=null)
				{
					roleGroup.changeCanApplyInAbs(selfID,canApplyInAbs);
				}
			});
		}
		else
		{
			sendToRoleGroup(FuncChangeRoleGroupApplyTypeToGameServerRequest.create(_funcID,groupID,me.role.playerID,canApplyInAbs));
		}
	}
	
	/** 添加玩家群tcc事务 */
	public void addRoleGroupTCCWork(PlayerToRoleGroupTCCWData data)
	{
		data.funcID=_funcID;
		data.groupID=groupID;
		data.sendPlayerID=me.role.playerID;
		data.receiveAreaID=createAreaID;
		
		me.addAreaGlobalWork(data);
	}
	
	/** 进入专属场景 */
	public void enterOwnScene()
	{
		if(_config.ownSceneID<=0)
		{
			me.warnLog("该玩家群没有专属场景",_config.ownSceneID);
			return;
		}
		
		if(!me.scene.checkCanEnterScene(_config.ownSceneID))
		{
			me.warnLog("进入玩家群专属场景时,条件未达成",_config.ownSceneID);
			return;
		}
		
		if(isCurrentGame())
		{
			long groupID=this.groupID;
			long selfID=me.role.playerID;
			
			me.addMainFunc(()->
			{
				RoleGroup roleGroup;
				if((roleGroup=GameC.main.getRoleGroup(_funcID,groupID))!=null)
				{
					roleGroup.enterOwnScene(selfID);
				}
			});
		}
		else
		{
			sendToRoleGroup(FuncRoleGroupEnterOwnSceneToGameServerRequest.create(_funcID,groupID,me.role.playerID));
		}
	}
	
	/** 回复进入专属场景 */
	public void onReEnterOwnScene(SceneLocationData data)
	{
		if(!me.scene.checkCanEnterScene(_config.ownSceneID))
		{
			me.warnLog("进入玩家群专属场景时,条件未达成",_config.ownSceneID);
			return;
		}
		
		me.scene.playerEnterSignedSceneAndCheck(data);
	}
	
	/** 更新社交部分角色显示数据(自身)(逻辑线程) */
	public void refreshRoleSocialData(RoleShowChangeData data)
	{
		if(isCurrentGame())
		{
			long playerID=me.role.playerID;
			
			me.addMainFunc(()->
			{
				RoleGroup roleGroup=GameC.main.getRoleGroup(_funcID,groupID);
				
				if(roleGroup!=null)
				{
					roleGroup.onRefreshRoleSocialData(playerID,data);
				}
			});
		}
		else
		{
			sendToRoleGroup(FuncRoleGroupRefreshRoleShowDataToGameServerRequest.create(_funcID,groupID,me.role.playerID,data));
		}
	}
	
	/** 成员外显数据变化 */
	public void onMemberRoleShowChange(long memberID,RoleShowChangeData data)
	{
		PlayerRoleGroupMemberData member=getMember(memberID);
		
		if(member!=null)
		{
			member.socialData.onChange(data);
			
			onMemberRoleShowChange(member,data);
			
			//不推送
			if(_config.isUpdateRoleSocialAbs && RoleShowDataPartTypeConfig.get(data.type).needSendAbs)
			{
				me.send(FuncSendRoleGroupMemberRoleShowChangeRequest.create(_funcID,groupID,memberID,data));
			}
		}
	}
	
	/** 成员外显数据变化 */
	protected void onMemberRoleShowChange(PlayerRoleGroupMemberData mData,RoleShowChangeData data)
	{
	
	}
	
	/** 玩家群聊天 */
	public void playerChat(RoleChatData cData,int channel,boolean needOffline)
	{
		if(needOffline && _config.needSave)
		{
			_d.members.forEachValue(v->
			{
				SendPlayerChatOWData wData=new SendPlayerChatOWData();
				wData.chatData=cData;
				wData.channel=channel;
				wData.key=groupID;
				
				_tool.addPlayerWork(v.playerID,wData);
			});
		}
		else
		{
			radioMembers(SendPlayerChatToPlayerServerRequest.create(cData,channel,groupID));
		}
	}
	
	/** 获取Rank简版数据 */
	public RankSimpleData getRankSimpleData(int funcID)
	{
		IntObjectMap<FuncToolData> dic=_d.funcTools.get(FuncToolType.Rank);
		
		if(dic==null)
			return null;
	
		return (RankSimpleData)dic.get(funcID);
	}
	
	/** 更新排行 */
	public void onRefreshRank(int rankFuncID,int rank,long value)
	{
		RankSimpleData data=getRankSimpleData(rankFuncID);
		
		if(data!=null)
		{
			data.rank=rank;
			data.value=value;
			
			//推送
			me.send(FuncRefreshRoleGroupRankRequest.create(rankFuncID,_funcID,groupID,rank,value));
		}
	}
	
	public void onRefreshRank(int rankFuncID,RoleGroupRankData rData)
	{
		RankSimpleData data=getRankSimpleData(rankFuncID);
		
		if(data!=null)
		{
			if(rData!=null)
			{
				data.rank=rData.rank;
				data.value=rData.value;
			}
			else
			{
				data.rank=-1;
				data.value=0;
			}
			
			//推送
			me.send(FuncRefreshRoleGroupRankRequest.create(rankFuncID,_funcID,groupID,data.rank,data.value));
		}
	}
	
	/** 排行榜重置 */
	public void onResetRank(int rankFuncID)
	{
		RankSimpleData data=getRankSimpleData(rankFuncID);
		
		if(data!=null)
		{
			data.rank=-1;
			data.value=0;
			
			//推送
			me.send(FuncResetRoleGroupRankRequest.create(rankFuncID,_funcID,groupID));
		}
	}
}
