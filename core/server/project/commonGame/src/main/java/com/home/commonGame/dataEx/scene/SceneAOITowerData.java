package com.home.commonGame.dataEx.scene;

import com.home.commonBase.scene.base.Region;
import com.home.commonBase.scene.base.Unit;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.pool.IPoolObject;

/** 场景aoi数据 */
public class SceneAOITowerData implements IPoolObject
{
	/** 单位组 */
	public IntObjectMap<Unit> dic=new IntObjectMap<>(Unit[]::new);
	
	/** 角色单位组(也做拥有视野单位组) */
	public IntObjectMap<Unit> characterDic=new IntObjectMap<>(Unit[]::new);
	
	/** 所涉及区域组 */
	public IntObjectMap<Region> regionDic=new IntObjectMap<>(Region[]::new);
	
	/** 遍历标记 */
	public boolean foreachFlag=false;
	
	@Override
	public void clear()
	{
		dic.clear();
		characterDic.clear();
		foreachFlag=false;
	}
	
	public void addUnit(Unit unit)
	{
		dic.put(unit.instanceID,unit);
		
		if(unit.aoiTower.needVision)
		{
			characterDic.put(unit.instanceID,unit);
		}
	}
	
	public void removeUnit(Unit unit)
	{
		dic.remove(unit.instanceID);
		
		if(unit.aoiTower.needVision)
		{
			characterDic.remove(unit.instanceID);
		}
	}
}
