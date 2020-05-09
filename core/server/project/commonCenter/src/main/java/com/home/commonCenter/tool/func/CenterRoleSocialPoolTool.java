package com.home.commonCenter.tool.func;

import com.home.commonBase.data.social.RoleSocialPoolData;
import com.home.commonBase.tool.func.RoleSocialPoolTool;
import com.home.commonCenter.global.CenterC;
import com.home.commonCenter.part.centerGlobal.part.CenterSocialPart;

public abstract class CenterRoleSocialPoolTool extends RoleSocialPoolTool
{
	public CenterRoleSocialPoolTool(int funcID,int maxNum)
	{
		this(funcID,maxNum,3600);
	}
	
	public CenterRoleSocialPoolTool(int funcID,int maxNum,int cutDelay)
	{
		this(funcID,maxNum,cutDelay,false);
	}
	
	public CenterRoleSocialPoolTool(int funcID,int maxNum,int cutDelay,boolean useCustom)
	{
		super(funcID,maxNum,cutDelay,useCustom);
	}
	
	@Override
	protected void onRemoveOne(long playerID)
	{
		super.onRemoveOne(playerID);
		
		//非自定义
		if(!_useCustom)
		{
			CenterC.global.social.tryRemovePlayerRoleSocial(playerID);
		}
	}
}
