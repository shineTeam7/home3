using System;
using ShineEngine;
using UnityEngine;

/// <summary>
/// 单位移动逻辑
/// </summary>
public class UnitPosLogic:UnitLogicBase
{
	protected UnitPosData _d;
	/** 位置数据 */
	protected PosData _pos;
	/** 朝向数据 */
	protected DirData _dir;

	/** 位置朝向数据 */
	private PosDirData _posDir=new PosDirData();

	/** 移动绑定组 */
	private SList<Unit> _moveBindUnits=new SList<Unit>();
	/** 被移动绑定单位 */
	private Unit _beMoveBindUnit=null;

	protected ScenePosLogic _scenePosLogic;

	/** 临时排序key */
	public float tempSortKey=0f;

	public UnitPosLogic()
	{

	}

	public override void init()
	{
		base.init();

		_scenePosLogic=_scene.pos;

		_d=_data.pos;
		_posDir.pos=_pos=_d.pos;
		_posDir.dir=_dir=_d.dir;

	}

	public override void dispose()
	{
		base.dispose();

		_scenePosLogic=null;
		_d=null;
		_pos=null;
		_dir=null;
		_posDir.pos=null;
		_posDir.dir=null;

		tempSortKey=0f;

		_moveBindUnits.clear();
		_beMoveBindUnit=null;
	}

	/** 获取位置 */
	public PosData getPos()
	{
		return _pos;
	}

	/** 获取朝向 */
	public DirData getDir()
	{
		return _dir;
	}

	/** 获取位置朝向数据 */
	public PosDirData getPosDir()
	{
		return _posDir;
	}

	/** 设置坐标和朝向 */
	public void setByPosDir(PosDirData posDir)
	{
		_pos.copyPos(posDir.pos);
		_dir.copyDir(posDir.dir);

		onSetPos();
		onSetDir();
	}

	//元方法

	/** 只设置朝向 */
	public void setDir(DirData dir)
	{
		_dir.copyDir(dir);
		onSetDir();
	}

	public virtual void onSetDir()
	{
		_unit.show.setDir(_dir);

		if(!_moveBindUnits.isEmpty())
		{
			Unit[] values=_moveBindUnits.getValues();
			Unit v;

			for(int i=0,len=_moveBindUnits.size();i<len;++i)
			{
				v=values[i];
				v.pos.setDir(_dir);
			}
		}
	}

	/** 设置位置(不推送) */
	public void setPos(PosData pos)
	{
		_pos.copyPos(pos);

		onSetPos();
	}

	public virtual void onSetPos()
	{
		_unit.show.setPos(_pos);
		_unit.head?.setPos(_pos);

		if(!_moveBindUnits.isEmpty())
		{
			Unit[] values=_moveBindUnits.getValues();
			Unit v;

			for(int i=0,len=_moveBindUnits.size();i<len;++i)
			{
				v=values[i];
				v.pos.setPos(_pos);
			}
		}
	}

	/** 设置位置(不推送) */
	public void setPos(Vector3 vec)
	{
		_pos.setByVector(vec);
		onSetPos();
	}

	/** 计算与目标位置距离平方 */
	public float calculateDistanceSq(PosData pos)
	{
		return _scenePosLogic.calculatePosDistanceSq(_pos,pos);
	}

	/** 添加移动绑定单位 */
	public void addMoveBindUnit(Unit unit)
	{
		_moveBindUnits.add(unit);
		unit.pos._beMoveBindUnit=_unit;
	}

	/** 移除移动绑定单位 */
	public void removeMoveBinUnit(Unit unit)
	{
		_moveBindUnits.removeObj(unit);
		unit.pos._beMoveBindUnit=null;
	}

	public override void preRemove()
	{
		base.preRemove();

		if(_beMoveBindUnit!=null)
		{
			_beMoveBindUnit.pos.removeMoveBinUnit(_unit);
		}

		if(!_moveBindUnits.isEmpty())
		{
			for(int i=_moveBindUnits.size()-1;i>=0;--i)
			{
				Unit unit=_moveBindUnits.remove(i);
				unit.pos._beMoveBindUnit=null;
			}
		}
	}
}