package com.home.commonGame.tool.func;

import com.home.commonBase.constlist.generate.FuncToolType;
import com.home.commonBase.data.social.RoleSocialData;
import com.home.commonBase.tool.func.FuncTool;
import com.home.commonGame.part.player.Player;

/** game到center的角色社交数据池工具 */
public abstract class GameToCenterRoleSocialPoolTool extends FuncTool
{
	/** 是否启用自定义社交数据 */
	private boolean _useCustom;
	
	public GameToCenterRoleSocialPoolTool(int funcID)
	{
		this(funcID,false);
	}
	
	public GameToCenterRoleSocialPoolTool(int funcID,boolean useCustom)
	{
		super(FuncToolType.RoleSocialPool,funcID);
		
		_useCustom=useCustom;
	}
	
	/** 是否需要提交(主线程)(自定义数据的话,data为null) */
	public abstract boolean needCommit(RoleSocialData data,Player player);
	
	/** 是否启用自定义社交数据 */
	public boolean isUseCustom()
	{
		return _useCustom;
	}
	
	/** 创建自定义社交数据 */
	public RoleSocialData createCustomRoleSocialData(Player player)
	{
		return null;
	}
}
