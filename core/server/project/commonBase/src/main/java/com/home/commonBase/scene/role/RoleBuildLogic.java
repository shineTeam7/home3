package com.home.commonBase.scene.role;

import com.home.commonBase.config.game.BuildingLevelConfig;
import com.home.commonBase.data.scene.base.PosData;
import com.home.commonBase.data.scene.base.PosDirData;
import com.home.commonBase.scene.base.RoleLogicBase;
import com.home.commonBase.scene.base.Unit;

/** 角色建造逻辑 */
public class RoleBuildLogic extends RoleLogicBase
{
	@Override
	public void init()
	{
		super.init();
	}
	
	/** 获取某建筑当前的可建造等级 */
	public int getCanBuildLevel(int id)
	{
		return 1;
	}
	
	/** 是否有某消耗 */
	protected boolean hasCost(int costID)
	{
		return false;
	}
	
	/** 执行消耗 */
	protected void doCost(int costID,int way)
	{
	
	}
	
	/** 检查是否可建造某建筑 */
	public boolean checkCanBuild(int id)
	{
		int level=getCanBuildLevel(id);
		
		if(level<=0)
		{
			_role.warnLog("无可用等级",id);
			return false;
		}
		
		return checkCanBuild(id,level);
	}
	
	/** 检查是否可建造某建筑 */
	public boolean checkCanBuild(int id,int level)
	{
		BuildingLevelConfig levelConfig=BuildingLevelConfig.get(id,level);
		
		if(levelConfig.costID>0 && !hasCost(levelConfig.costID))
		{
			_role.warnLog("建筑消耗不足",id);
			return false;
		}
		
		//TODO:角色属性
		
		return true;
	}
	
	/** 创建建筑(带消耗的) */
	public Unit createBuilding(int id,boolean isReady,PosDirData pos)
	{
		int level=getCanBuildLevel(id);
		
		BuildingLevelConfig levelConfig=BuildingLevelConfig.get(id,level);
		
		if(levelConfig.costID>0)
		{
			doCost(levelConfig.costID,0);
		}
		
		return _scene.unitFactory.createAddBuilding(id,level,_role,isReady,pos);
	}
}
