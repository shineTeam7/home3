package com.home.commonBase.logic.unit;

import com.home.commonBase.constlist.generate.AttributeType;
import com.home.commonBase.control.AttributeControl;
import com.home.commonBase.global.Global;
import com.home.commonBase.tool.AttributeTool;
import com.home.shine.support.collection.IntIntMap;

/** 属性数据逻辑 */
public class AttributeDataLogic extends AttributeTool
{
	private UnitFightDataLogic _parent;
	
	public void setParent(UnitFightDataLogic parent)
	{
		_parent=parent;
		setInfo(AttributeControl.attribute);
	}
	
	@Override
	protected void toSendSelf(IntIntMap dic)
	{
		_parent.sendSelfAttribute(dic);
	}
	
	@Override
	protected void toSendOther(IntIntMap dic)
	{
		_parent.sendOtherAttribute(dic);
	}
	
	@Override
	protected void toDispatchAttribute(int[] changeList,int num,boolean[] changeSet)
	{
		//先buff
		_parent.buff.onAttributeChange(changeList,num,changeSet);
		_parent.dispatchAttribute(changeList,num,changeSet);
	}
	
	//快捷方式
	
	/** 当前血 */
	public int getHp()
	{
		return getAttribute(AttributeType.Hp);
	}
	
	/** 当前血 */
	public int getHpMax()
	{
		return getAttribute(AttributeType.HpMax);
	}
	
	/** 当前蓝 */
	public int getMp()
	{
		return getAttribute(AttributeType.Mp);
	}
	
	/** 当前蓝上限 */
	public int getMpMax()
	{
		return getAttribute(AttributeType.MpMax);
	}
	
	/** 获取实际移速 */
	public int getRealMoveSpeed()
	{
		int re=getAttribute(AttributeType.MoveSpeed);
		
		if(re<Global.moveSpeedMin)
		{
			re=Global.moveSpeedMin;
		}
		
		if(re>Global.moveSpeedMax)
		{
			re=Global.moveSpeedMax;
		}
		
		return re;
	}
	
	/** 获取实际攻速 */
	public int getRealAttackSpeed()
	{
		int re=getAttribute(AttributeType.AttackSpeed);
		
		if(re<Global.attackSpeedMin)
		{
			re=Global.attackSpeedMin;
		}
		
		if(re>Global.attackSpeedMax)
		{
			re=Global.attackSpeedMax;
		}
		
		return re;
	}
	
	/** 获取实际施法速度 */
	public int getRealCastSpeed()
	{
		int re=getAttribute(AttributeType.CastSpeed);
		
		if(re<Global.castSpeedMin)
		{
			re=Global.castSpeedMin;
		}
		
		if(re>Global.castSpeedMax)
		{
			re=Global.castSpeedMax;
		}
		
		return re;
	}
	
	/** 补满血蓝 */
	public void fillHpMp()
	{
		if(getHp()<getHpMax())
			setOneAttribute(AttributeType.Hp,getHpMax());
		
		if(getMp()<getMpMax())
			setOneAttribute(AttributeType.Mp,getMpMax());
	}
	
	/** 添加生命百分比 */
	public void addHPPercent(int value)
	{
		addCurrentPercent(AttributeType.Hp,value);
	}
	
	/** 添加魔法百分比 */
	public void addMPPercent(int value)
	{
		addCurrentPercent(AttributeType.Mp,value);
	}
}
