package com.home.commonBase.dataEx.scene;

import com.home.commonBase.data.scene.base.BuffData;
import com.home.commonBase.scene.base.Unit;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.pool.PoolObject;

/** 光环buff统计数据 */
public class RingLightBuffCountData extends PoolObject
{
	/** buff数据 */
	public BuffData data;
	/** 半径 */
	public float distance=0f;
	
	public int addBuffID;
	
	public int addBuffLevel;
	/** 影响单位组 */
	public IntObjectMap<Unit> influenceDic=new IntObjectMap<>(Unit[]::new);
	
	/** 清空 */
	@Override
	public void clear()
	{
		data=null;
		distance=0f;
		addBuffID=0;
		addBuffLevel=0;
		influenceDic.clear();
	}
}
