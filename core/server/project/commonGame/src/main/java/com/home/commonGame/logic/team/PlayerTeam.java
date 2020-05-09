package com.home.commonGame.logic.team;

import com.home.commonBase.constlist.generate.RoleShowDataPartType;
import com.home.commonBase.data.role.RoleShowChangeData;
import com.home.commonBase.data.scene.scene.SceneLocationData;
import com.home.commonBase.data.social.roleGroup.PlayerRoleGroupMemberData;
import com.home.commonGame.logic.func.PlayerRoleGroup;
import com.home.shine.support.collection.IntIntMap;

/** 角色身上队伍逻辑 */
public class PlayerTeam extends PlayerRoleGroup
{
	//private boolean _sceneLineDirty=false;
	//
	//private IntIntMap _sceneLineDic=new IntIntMap();
	//
	//@Override
	//public void onAddMember(PlayerRoleGroupMemberData mData,int type)
	//{
	//	super.onAddMember(mData,type);
	//
	//	if(mData.socialData.location!=null)
	//	{
	//		_sceneLineDirty=true;
	//	}
	//}
	//
	//@Override
	//protected void onRemoveMember(PlayerRoleGroupMemberData mData)
	//{
	//	super.onRemoveMember(mData);
	//
	//	if(mData.socialData.location!=null)
	//	{
	//		_sceneLineDirty=true;
	//	}
	//}
	//
	//@Override
	//protected void onMemberRoleShowChange(PlayerRoleGroupMemberData mData,RoleShowChangeData data)
	//{
	//	super.onMemberRoleShowChange(mData,data);
	//
	//	if(data.type==RoleShowDataPartType.Location)
	//	{
	//		_sceneLineDirty=true;
	//	}
	//}
	//
	///** 获取场景所在线(找不到返回-1) */
	//public int getSceneLine(int sceneID)
	//{
	//	if(_sceneLineDirty)
	//	{
	//		_sceneLineDirty=false;
	//		_sceneLineDic.clear();
	//
	//		SceneLocationData location;
	//
	//		PlayerRoleGroupMemberData[] values;
	//		PlayerRoleGroupMemberData v;
	//
	//		for(int i=(values=getMemberDic().getValues()).length-1;i>=0;--i)
	//		{
	//			if((v=values[i])!=null)
	//			{
	//				if((location=v.socialData.location)!=null)
	//				{
	//					_sceneLineDic.put(location.sceneID,location.lineID);
	//				}
	//			}
	//		}
	//	}
	//
	//	return _sceneLineDic.getOrDefault(sceneID,-1);
	//}
}
