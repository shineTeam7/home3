package com.home.commonGame.logic.team;

import com.home.commonBase.constlist.generate.FunctionType;
import com.home.commonBase.constlist.generate.TeamTargetType;
import com.home.commonBase.data.social.roleGroup.CreateRoleGroupData;
import com.home.commonBase.data.social.roleGroup.RoleGroupData;
import com.home.commonBase.data.social.roleGroup.RoleGroupSimpleData;
import com.home.commonBase.data.social.team.CreateTeamData;
import com.home.commonBase.data.social.team.TeamData;
import com.home.commonBase.data.social.team.TeamSimpleData;
import com.home.commonBase.global.Global;
import com.home.commonGame.logic.func.RoleGroup;
import com.home.commonGame.tool.func.GameRoleGroupTool;
import com.home.shine.support.collection.LongObjectMap;
import com.home.shine.support.collection.SList;

import java.util.Comparator;

/** 玩家队伍工具 */
public class TeamTool extends GameRoleGroupTool
{
	private LongObjectMap<TeamSimpleData>[] _teamTargetDic=new LongObjectMap[TeamTargetType.size];
	
	private SList<TeamSimpleData>[] _teamTargetList=new SList[TeamTargetType.size];
	
	private boolean[] _teamTargetDirty=new boolean[TeamTargetType.size];
	
	private Comparator<TeamSimpleData> _compareTeamList;
	
	public TeamTool()
	{
		super(FunctionType.Team,Global.teamRoleGroupID);
	}
	
	/** 创建玩家群(只创建) */
	@Override
	protected RoleGroup toCreateRoleGroup()
	{
		return new Team();
	}
	
	/** 创建玩家群数据(只创建) */
	@Override
	protected RoleGroupData toCreateRoleGroupData()
	{
		return new TeamData();
	}
	
	/** 创建群创建数据 */
	@Override
	protected CreateRoleGroupData toCreateCreateRoleGroupData()
	{
		return new CreateTeamData();
	}
	
	@Override
	public void construct()
	{
		super.construct();
		
		_compareTeamList=this::compareTeamSimpleList;
		
		for(int i=0;i<_teamTargetDirty.length;i++)
		{
			_teamTargetDirty[i]=true;
		}
	}
	
	private LongObjectMap<TeamSimpleData> getDicByTarget(int type)
	{
		LongObjectMap<TeamSimpleData> dic=_teamTargetDic[type];
		
		if(dic==null)
		{
			_teamTargetDic[type]=dic=new LongObjectMap<>(TeamSimpleData[]::new);
		}
		
		return dic;
	}
	
	private SList<TeamSimpleData> getListByTarget(int type)
	{
		SList<TeamSimpleData> list=_teamTargetList[type];
		
		if(list==null)
		{
			_teamTargetList[type]=list=new SList<>(TeamSimpleData[]::new);
		}
		
		return list;
	}
	
	private void setTargetDirty(int targetID)
	{
		if(!_teamTargetDirty[targetID])
		{
			_teamTargetDirty[targetID]=true;
			getListByTarget(targetID).clear();
		}
	}
	
	/** 比较组队简版信息 */
	protected int compareTeamSimpleList(TeamSimpleData arg0,TeamSimpleData arg1)
	{
		return Long.compare(arg0.groupID,arg1.groupID);
	}
	
	/** 获取队伍目标数据列表 */
	public SList<TeamSimpleData> getTargetList(int targetID)
	{
		SList<TeamSimpleData> list=getListByTarget(targetID);
		
		if(_teamTargetDirty[targetID])
		{
			_teamTargetDirty[targetID]=false;
			
			list.clear();
			
			LongObjectMap<TeamSimpleData> dic=getDicByTarget(targetID);
			
			TeamSimpleData[] values;
			TeamSimpleData v;
			
			for(int i=(values=dic.getValues()).length-1;i>=0;--i)
			{
				if((v=values[i])!=null)
				{
					list.add(v);
				}
			}
			
			list.sort(_compareTeamList);
		}
		
		return list;
	}
	
	@Override
	protected void makeDefaultCreateData(CreateRoleGroupData data)
	{
		super.makeDefaultCreateData(data);
		
		((CreateTeamData)data).targetID=0;
	}
	
	@Override
	protected void onAddGameRoleGroupSimpleData(RoleGroupSimpleData data)
	{
		super.onAddGameRoleGroupSimpleData(data);
		
		TeamSimpleData tData=(TeamSimpleData)data;
		
		getDicByTarget(tData.targetID).put(tData.groupID,tData);
		setTargetDirty(tData.targetID);
	}
	
	@Override
	protected void onRemoveGameRoleGroupSimpleData(RoleGroupSimpleData data)
	{
		super.onRemoveGameRoleGroupSimpleData(data);
		
		TeamSimpleData tData=(TeamSimpleData)data;
		
		getDicByTarget(tData.targetID).remove(tData.groupID);
		setTargetDirty(tData.targetID);
	}
	
	@Override
	protected void setSimpleListDirty(RoleGroupSimpleData data)
	{
		super.setSimpleListDirty(data);
		
		TeamSimpleData tData=(TeamSimpleData)data;
		
		setTargetDirty(tData.targetID);
	}
}
