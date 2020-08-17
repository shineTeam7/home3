using System;
using ShineEngine;

/// <summary>
/// 属性数据逻辑
/// </summary>
public class AttributeDataLogic:AttributeTool
{
	private UnitFightDataLogic _parent;

	public void setParent(UnitFightDataLogic parent)
	{
		_parent=parent;
		setInfo(AttributeControl.attribute);
	}

	protected override void toDispatchAttribute(int[] changeList,int num,bool[] changeSet,int[] lastAttributes)
	{
		//先buff
		_parent.buff.onAttributeChange(changeList,num,changeSet,lastAttributes);
		_parent.onAttributeChange(changeList,num,changeSet,lastAttributes);
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

	/** 获取当前血量百分比 */
	public float getHpPercent()
	{
		int hpMax=getHpMax();

		if(hpMax<=0)
			return 0;

		float re=(float)getHp() / hpMax;

		if(re>=1)
			re=1;

		return re;
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

	/** 获取当前蓝量百分比 */
	public float getMpPercent()
	{
		int mpMax=getMpMax();

		if(mpMax<=0)
			return 0;

		float re=(float)getMp() / mpMax;

		if(re>=1)
			re=1;

		return re;
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