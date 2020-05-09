package com.home.commonData.trigger.base;

import com.home.commonData.trigger.type.DirT;
import com.home.commonData.trigger.type.PosT;
import com.home.commonData.trigger.type.UnitT;

public abstract class SceneT extends BaseT
{
	//--condition--//
	
	/** c单位是否存活 */
	protected boolean unitIsAlive(UnitT unit){return false;}
	/** 对于某单位来说，点是否可走 */
	protected boolean isPosEnabled(UnitT unit,PosT pos){return false;}
	
	//--trigger--//
	
	/** 当前触发单位 */
	protected UnitT triggerUnit(){return null;}
	
	//--event--//
	
	/** e场景开始时 */
	protected void onSceneStart(){}
	/** 单位发生移动 */
	protected void onUnitMove(){}
	/** 单位受到伤害时 */
	protected void onUnitBeDamage(){}
	
	
	//--func--//
	/** 计算点距离 */
	protected float posDistance(PosT pos0,PosT pos1){return 0f;}
	/** 获取单位的战斗单位id */
	protected int getUnitFightUnitID(UnitT unit){return 0;}
	
	/** 通过实例id获取单位 */
	protected UnitT getUnit(int instanceID){return null;}
	/** 强制类型转化为单位 */
	protected UnitT asUnit(Object obj){return null;}
	
	/** 创建并添加傀儡 */
	protected UnitT createAddPuppet(int id,int level,PosT pos,UnitT master,int lastTime){return null;}
	
	/** 强制类型转化为点 */
	protected PosT asPos(Object obj){return null;}
	/** 获取单位位置 */
	protected PosT getUnitPos(UnitT unit){return null;}
	/** 获取场景排放配置位置 */
	protected PosT getScenePlacePos(int instanceID){return null;}
	/** 极坐标取点 */
	protected PosT posPolar(PosT pos,float distance,DirT dir){return null;}
	/** 点相加 */
	protected PosT addPos(PosT pos0,PosT pos1){return null;}
	
	/** 强制类型转化为朝向 */
	protected DirT asDir(Object obj){return null;}
	/** 获取单位朝向 */
	protected DirT getUnitDir(UnitT unit){return null;}
	
	/** 朝向 相加 */
	protected DirT addDir(DirT dir,DirT value){return null;}
	/** 朝向 相加 */
	protected DirT addDirFloat(DirT dir,float value){return null;}
	
	//--action--//
	
	/** 强制击杀单位 */
	protected void killUnit(UnitT unit){}
	/** 移除单位 */
	protected void removeUnit(UnitT unit){}
	/** 移动到目标单位 */
	protected void moveToUnit(UnitT unit,UnitT target,float radius){}
	/** 单位添加属性 */
	protected void unitAddAttribute(UnitT unit,int type,int value){}
	/** 单位治疗生命千分比 */
	protected void unitAddHpPercent(UnitT unit,int percent){}
}
