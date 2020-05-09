package com.home.commonBase.trigger;

import com.home.commonBase.config.game.ScenePlaceElementConfig;
import com.home.commonBase.constlist.generate.MapMoveType;
import com.home.commonBase.constlist.generate.TriggerEventType;
import com.home.commonBase.constlist.generate.TriggerFunctionType;
import com.home.commonBase.constlist.generate.TriggerObjectType;
import com.home.commonBase.constlist.generate.UnitMoveType;
import com.home.commonBase.data.scene.base.DirData;
import com.home.commonBase.data.scene.base.PosData;
import com.home.commonBase.scene.base.Scene;
import com.home.commonBase.scene.base.Unit;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.data.trigger.TriggerFuncData;
import com.home.shine.data.trigger.TriggerObjData;
import com.home.shine.utils.MathUtils;

/** 场景触发 */
public class SceneTriggerExecutor extends TriggerExecutor
{
	/** 场景 */
	protected Scene _scene;
	
	public void setScene(Scene scene)
	{
		_scene=scene;
	}
	
	/** 获取单位快捷方式 */
	protected Unit getObjUnit(TriggerObjData obj,TriggerArg arg)
	{
		return (Unit)getObj(obj,arg);
	}
	
	/** 获取点快捷方式 */
	protected PosData getObjPos(TriggerObjData obj,TriggerArg arg)
	{
		return (PosData)getObj(obj,arg);
	}
	
	/** 获取朝向快捷方式 */
	protected DirData getObjDir(TriggerObjData obj,TriggerArg arg)
	{
		return (DirData)getObj(obj,arg);
	}
	
	/** 创建事件(只创建) */
	protected TriggerEvent toCreateEvent()
	{
		return new SceneTriggerEvent();
	}
	
	@Override
	protected void initEvent(TriggerEvent event)
	{
		super.initEvent(event);
		
		switch(event.type)
		{
			case TriggerEventType.OnUnitMove:
			case TriggerEventType.OnUnitBeDamage:
			{
				((SceneTriggerEvent)event).triggerUnit=(Unit)event.args[0];
			}
				break;
		}
	}
	
	@Override
	protected boolean toGetBooleanFuncValue(TriggerFuncData func,TriggerArg arg)
	{
		switch(func.id)
		{
			case TriggerFunctionType.UnitIsAlive:
				return getObjUnit(func.args[0],arg).fight.isAlive();
			case TriggerFunctionType.IsPosEnabled:
			{
				Unit unit=getObjUnit(func.args[0],arg);
				PosData pos=getObjPos(func.args[1],arg);
				
				return _scene.pos.isPosEnabled(unit!=null ? unit.move.getMoveType(): MapMoveType.Land,pos,false);
			}
			default:
			{
				return super.toGetBooleanFuncValue(func,arg);
			}
		}
	}
	
	@Override
	protected float toGetFloatFuncValue(TriggerFuncData func,TriggerArg arg)
	{
		switch(func.id)
		{
			case TriggerFunctionType.PosDistance:
			{
				return _scene.pos.calculatePosDistance(getObjPos(func.args[0],arg),getObjPos(func.args[1],arg));
			}
			default:
			{
				return super.toGetFloatFuncValue(func,arg);
			}
		}
	}
	
	@Override
	protected int toGetIntFuncValue(TriggerFuncData func,TriggerArg arg)
	{
		switch(func.id)
		{
			case TriggerFunctionType.GetUnitFightUnitID:
			{
				return getObjUnit(func.args[0],arg).fight.getFightUnitConfig().id;
			}
			default:
			{
				return super.toGetIntFuncValue(func,arg);
			}
		}
		
	}
	
	@Override
	protected Object toGetObjectFuncValue(TriggerFuncData func,TriggerArg arg)
	{
		switch(func.id)
		{
			case TriggerFunctionType.AsUnit:
			case TriggerFunctionType.AsPos:
			case TriggerFunctionType.AsDir:
				return getObj(func.args[0],arg);
				
				//unit
			case TriggerFunctionType.TriggerUnit:
				return ((SceneTriggerEvent)arg.evt).triggerUnit;
			case TriggerFunctionType.GetUnit:
				return _scene.getUnit(getInt(func.args[0],arg));
			case TriggerFunctionType.CreateAddPuppet:
				return _scene.unitFactory.createAddPuppet(getInt(func.args[0],arg),getInt(func.args[1],arg),getObjPos(func.args[2],arg),getObjUnit(func.args[3],arg),getInt(func.args[4],arg));
				//pos
			case TriggerFunctionType.GetUnitPos:
				return getObjUnit(func.args[0],arg).pos.getPos();
			case TriggerFunctionType.GetScenePlacePos:
			{
				int eID=getInt(func.args[0],arg);
				ScenePlaceElementConfig eConfig=_scene.getPlaceConfig().getElement(eID);
				
				if(eConfig==null)
				{
					Ctrl.errorLog("未找到场景摆放配置",eID);
					return null;
				}
				PosData re=new PosData();
				re.setByFArr(eConfig.pos);
				
				return re;
			}
			case TriggerFunctionType.PosPolar:
			{
				PosData re=new PosData();
				_scene.pos.polar2D(re,getFloat(func.args[1],arg),getObjDir(func.args[2],arg));
				_scene.pos.addPos(re,getObjPos(func.args[0],arg));
				return re;
			}
			case TriggerFunctionType.AddPos:
			{
				PosData re=new PosData();
				re.copyPos(getObjPos(func.args[0],arg));
				_scene.pos.addPos(re,getObjPos(func.args[1],arg));
				return re;
			}
			
				//dir
			case TriggerFunctionType.GetUnitDir:
				return getObjUnit(func.args[0],arg).pos.getDir();
			case TriggerFunctionType.AddDir:
			{
				DirData re=new DirData();
				re.copyDir(getObjDir(func.args[0],arg));
				DirData dir2=getObjDir(func.args[1],arg);
				re.direction=MathUtils.directionCut(re.direction+dir2.direction);
				//re.directionX+=dir2.directionX;
				return re;
			}
			case TriggerFunctionType.AddDirFloat:
			{
				DirData re=new DirData();
				re.copyDir(getObjDir(func.args[0],arg));
				float value=getFloat(func.args[1],arg);
				re.direction=MathUtils.directionCut(re.direction+value);
				return re;
			}
			default:
			{
				return super.toGetObjectFuncValue(func,arg);
			}
		}
	}
	
	@Override
	protected void toDoActionFunc(TriggerFuncData func,TriggerArg arg)
	{
		switch(func.id)
		{
			case TriggerFunctionType.KillUnit:
			{
				_scene.fight.killUnit(getObjUnit(func.args[0],arg));
			}
				break;
			case TriggerFunctionType.RemoveUnit:
			{
				getObjUnit(func.args[0],arg).removeLater();
			}
				break;
			case TriggerFunctionType.MoveToUnit:
			{
				getObjUnit(func.args[0],arg).aiCommand.moveTo(getObjUnit(func.args[1],arg),getFloat(func.args[2],arg),null);
			}
				break;
			case TriggerFunctionType.UnitAddAttribute:
			{
				getObjUnit(func.args[0],arg).fight.getAttributeLogic().addOneAttribute(getInt(func.args[1],arg),getInt(func.args[2],arg));
			}
				break;
			case TriggerFunctionType.UnitAddHpPercent:
			{
				getObjUnit(func.args[0],arg).fight.getAttributeLogic().addHPPercent(getInt(func.args[1],arg));
			}
				break;
			default:
			{
				super.toDoActionFunc(func,arg);
			}
		}
	}
}
