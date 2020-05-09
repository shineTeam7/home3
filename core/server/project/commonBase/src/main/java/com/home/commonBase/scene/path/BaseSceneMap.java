package com.home.commonBase.scene.path;

import com.home.commonBase.config.other.MapInfoConfig;
import com.home.commonBase.data.scene.base.PosData;
import com.home.commonBase.scene.base.Scene;
import com.home.shine.support.collection.SList;

/** scene地图 */
public abstract class BaseSceneMap
{
	protected Scene _scene;
	
	protected PosData _originPos;
	
	protected PosData _endPos;
	
	public void setScene(Scene scene)
	{
		_scene=scene;
	}
	
	/** 初始化地图信息 */
	public void init()
	{
		_originPos=_scene.originPos;
		_endPos=_scene.endPos;
	}
	
	/** 析构 */
	public void dispose()
	{
	
	}
	
	/** 格子是否可走 */
	public abstract boolean isPosEnabled(int moveType,PosData pos,boolean isClient);
	/** 从某点开始,寻找射线点 */
	public abstract void findRayPos(int moveType,PosData re,PosData from,float direction,float length);
	/** 寻路(不包含当前点) */
	public abstract void findPath(SList<PosData> list,int moveType,boolean needCrowed,PosData from,PosData target);
	/** 获取坐标阻挡类型 */
	public abstract int getBlockType(PosData pos);
}
