package com.home.commonBase.scene.unit;

import com.home.commonBase.config.game.FightUnitConfig;
import com.home.commonBase.constlist.generate.UnitAICommandType;
import com.home.commonBase.constlist.generate.UnitAIFightStateType;
import com.home.commonBase.constlist.generate.UnitMoveType;
import com.home.commonBase.constlist.generate.UnitType;
import com.home.commonBase.data.item.ItemData;
import com.home.commonBase.data.scene.base.DirData;
import com.home.commonBase.data.scene.base.PosData;
import com.home.commonBase.data.scene.base.SkillData;
import com.home.commonBase.data.scene.fight.SkillTargetData;
import com.home.commonBase.data.scene.unit.identity.FieldItemIdentityData;
import com.home.commonBase.dataEx.scene.UnitReference;
import com.home.commonBase.global.Global;
import com.home.commonBase.scene.base.Unit;
import com.home.commonBase.scene.base.UnitLogicBase;
import com.home.shine.ctrl.Ctrl;

/** unitAI指令逻辑 */
public class UnitAICommandLogic extends UnitLogicBase
{
	/** 战斗单位配置 */
	protected FightUnitConfig _fightUnitConfig;
	/** ai逻辑 */
	protected UnitAILogic _aiLogic;
	
	/** 默认指令 */
	private int _defaultCommand=UnitAICommandType.None;
	
	//指令部分
	/** 当前指令 */
	protected int _commandType=UnitAICommandType.None;
	/** 指令序号 */
	private int _commandIndex;
	/** 指令完成回调 */
	private Runnable _commandOver;
	/** 目标位置 */
	private PosData _commandPos=new PosData();
	/** 目标朝向 */
	private DirData _commandDir=new DirData();
	/** 目标距离 */
	private float _commandDistance=0f;
	/** 指令移动类型 */
	private int _commandMoveType=UnitMoveType.Run;
	/** 技能数据 */
	private SkillData _commandSkillData;
	/** 技能目标数据 */
	private SkillTargetData _commandSkillTargetData;
	/** 指令技能序号 */
	private int _commandSkillIndex;
	/** 指令目标单位 */
	private UnitReference _commandUnit=new UnitReference();
	
	@Override
	public void init()
	{
		super.init();
		
		_aiLogic=_unit.ai;
		_fightUnitConfig=_unit.fight.getFightUnitConfig();
		
		setDefaultCommand(_fightUnitConfig.defaultAICommand);
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		clearCurrentCommand();
		_fightUnitConfig=null;
	}
	
	@Override
	public void afterInit()
	{
		super.afterInit();
		
		initAI();
	}
	
	/** 初始化AI */
	protected void initAI()
	{
		if(_aiLogic.isAIRunning())
		{
			doLoopCommand(_defaultCommand);
		}
	}
	
	/** 设置默认指令 */
	public void setDefaultCommand(int type)
	{
		_defaultCommand=type;
	}
	
	/** 指令为空 */
	public boolean isCommandEmpty()
	{
		return _commandType==UnitAICommandType.None;
	}
	
	/** 当前指令是否可战斗 */
	public boolean isCommandCanFight()
	{
		switch(_commandType)
		{
			case UnitAICommandType.Protect:
			case UnitAICommandType.SkillTo:
			case UnitAICommandType.AttackMoveTo:
			case UnitAICommandType.FollowAttack:
				return true;
		}
		
		return false;
	}
	
	/** 当前指令是否可搜索敌人 */
	public boolean isCommandCanSearchTarget()
	{
		switch(_commandType)
		{
			case UnitAICommandType.Protect:
			case UnitAICommandType.Escape:
			case UnitAICommandType.AttackMoveTo:
			case UnitAICommandType.FollowAttack:
				return true;
		}
		
		return false;
	}
	
	public boolean isCommandNeedWander()
	{
		switch(_commandType)
		{
			case UnitAICommandType.Protect:
			case UnitAICommandType.Escape:
				return true;
		}
		
		return false;
	}
	
	/** 当前指令是否可更换追击技能 */
	public boolean isCommandCanChangePursueSkill()
	{
		return _commandType!=UnitAICommandType.SkillTo;
	}
	
	/** 当前指令是否可更换追击技能目标 */
	public boolean isCommandCanChangePursueUnit()
	{
		return _commandType!=UnitAICommandType.SkillTo;
	}
	
	/** 当前指令是否需要检查退回 */
	public boolean isCommandNeedCheckBack()
	{
		switch(_commandType)
		{
			case UnitAICommandType.Protect:
			case UnitAICommandType.AttackMoveTo:
			case UnitAICommandType.FollowAttack:
			{
				return true;
			}
		}
		
		return false;
	}
	
	/** 清空当前数据 */
	private void clearCurrentCommand()
	{
		_commandType=UnitAICommandType.None;
		_commandOver=null;
		
		_commandSkillData=null;
		_commandSkillTargetData=null;
		_commandMoveType=UnitMoveType.Run;
		_commandUnit.clear();
	}
	
	/** 更新指令(1秒10次) */
	public void updateCommand(int delay)
	{
		switch(_commandType)
		{
			case UnitAICommandType.Protect:
			{
			
			}
				break;
			case UnitAICommandType.Escape:
			{
				_aiLogic.updateEscape();
			}
				break;
			case UnitAICommandType.MoveTo:
			case UnitAICommandType.PickUpItem:
			{
				//没有移动中
				if(!_unit.move.isMoving())
				{
					float dis=_commandType==UnitAICommandType.PickUpItem ? Global.pickUpRadiusSq : 0f;
					//向目标移动
					_unit.move.moveTo(_commandPos,dis);
				}
			}
				break;
			case UnitAICommandType.MoveToUnit:
			{
				//没有移动中
				if(!_unit.move.isMoving())
				{
					Unit unit=_commandUnit.getUnit();
					
					if(unit!=null)
					{
						//向目标移动
						_unit.move.moveToUnit(unit,_commandDistance);
					}
					else
					{
						commandFailed();
					}
				}
			}
				break;
			case UnitAICommandType.AttackMoveTo:
			{
				//没有移动中
				if(_unit.ai.isIdle() && !_unit.move.isMoving())
				{
					//向目标移动
					_unit.move.moveTo(_commandPos);
				}
			}
				break;
			case UnitAICommandType.SpecialMoveTo:
			{
			
			}
				break;
			case UnitAICommandType.Follow:
			case UnitAICommandType.FollowAttack:
			{
				if(_unit.ai.isIdle() && _unit.move.canMoveNow())
				{
					Unit unit=_commandUnit.getUnit();
					
					if(unit==null)
					{
						commandOver();
					}
					else
					{
						if(_unit.pos.calculateDistanceSq(unit.pos.getPos())>_fightUnitConfig.followRadiusT)
						{
							_unit.move.moveToUnit(unit,_fightUnitConfig.followRadius);
						}
					}
				}
			}
				break;
		}
	}
	
	/** 执行循环command */
	public void doLoopCommand(int command)
	{
		if(_commandType==command)
			return;
		
		clearCurrentCommand();
		
		_commandType=command;
		
		runCommand();
	}
	
	/** 当前指令完成 */
	public void commandOver()
	{
		Runnable func=_commandOver;
		
		doLoopCommand(_defaultCommand);
		
		//TODO:是否下帧调用
		
		if(func!=null)
		{
			func.run();
		}
	}
	
	/** 当前指令失败 */
	protected void commandFailed()
	{
		doLoopCommand(_defaultCommand);
	}
	
	/** 获取当前指令 */
	public int getCurrentCommand()
	{
		return _commandType;
	}
	
	/** 指令移动结束在 Move状态下 */
	public void onCommandMoveToOverOnIdle(boolean isSuccess)
	{
		switch(_commandType)
		{
			case UnitAICommandType.AttackMoveTo:
			{
				if(isSuccess)
					commandOver();
				else
					commandFailed();
			}
				break;
		}
	}
	
	/** 指令移动结束在 Move状态下 */
	public void onCommandMoveToOver(boolean isSuccess)
	{
		switch(_commandType)
		{
			case UnitAICommandType.MoveTo:
			case UnitAICommandType.MoveToUnit:
			{
				if(isSuccess)
					commandOver();
				else
					commandFailed();
			}
				break;
			case UnitAICommandType.PickUpItem:
			{
				if(isSuccess)
					pickUpMoveOver();
				else
					commandFailed();
			}
				break;
			case UnitAICommandType.Escape:
			{
				if(isSuccess)
				{
					_aiLogic.setCurrentAnchorPos();
				}
				
				_aiLogic.reIdle();
			}
				break;
		}
	}
	
	/** 指令技能完毕 */
	public boolean onCommandSkillOver()
	{
		//是相同序号的技能
		if(_commandType==UnitAICommandType.SkillTo && _commandIndex==_commandSkillIndex)
		{
			commandOver();
			return true;
		}
		
		return false;
	}
	
	/** 追击结束 */
	public void onCommandPursueOver()
	{
		switch(_commandType)
		{
			case UnitAICommandType.SkillTo:
			{
				_aiLogic.stopMoveAndReIdle();
				commandOver();
			}
				break;
			case UnitAICommandType.Protect:
			case UnitAICommandType.FollowAttack:
			{
				_aiLogic.reBack();
			}
				break;
			case UnitAICommandType.AttackMoveTo:
			{
				_aiLogic.reIdle();
				//已改为继续移动到目标点
				_unit.move.moveTo(_commandPos);
			}
				break;
			default:
			{
			
			}
				break;
		}
	}
	
	public void setCurrentCommandSkillIndex()
	{
		_commandSkillIndex=_commandIndex;
	}
	
	/** 拾取移动结束 */
	private boolean checkCanPickUp(Unit unit)
	{
		if(unit.getType()!=UnitType.FieldItem)
			return false;
		
		FieldItemIdentityData iData=(FieldItemIdentityData)unit.getUnitData().identity;
		
		ItemData itemData=iData.item;
		
		if(!doCanPickUpItem(itemData))
			return false;
		
		return true;
	}
	
	/** 是否可拾取物品 */
	protected boolean doCanPickUpItem(ItemData item)
	{
		return true;
	}
	
	/** 发送拾取 */
	protected void sendPickUp(int targetInstanceID)
	{
	
	}
	
	/** 拾取移动结束 */
	private void pickUpMoveOver()
	{
		Unit unit=_commandUnit.getUnit();
		
		if(unit!=null && checkCanPickUp(unit))
		{
			sendPickUp(unit.instanceID);
			commandOver();
		}
		else
		{
			commandFailed();
		}
	}
	
	/** 执行指令 */
	protected void runCommand()
	{
		if(!_aiLogic.isAIRunning())
		{
			Ctrl.throwError("AI未启动");
			return;
		}
		
		_aiLogic.activeAI();
		
		_commandIndex++;
		
		//不可战斗的指令
		if(!isCommandCanFight())
		{
			_aiLogic.clearFightAI();
		}
		
		doRunCommand();
		
		_aiLogic.updateAI(0);
	}
	
	protected void doRunCommand()
	{
		switch(_commandType)
		{
			case UnitAICommandType.None:
			case UnitAICommandType.Protect:
			case UnitAICommandType.Escape:
			{
				//设置锚点
				_aiLogic.setCurrentAnchorPos();
				
				//先停止移动
				if(_unit.move.canMoveNow())
				{
					_unit.move.stopMove();
				}
				
				_aiLogic.setFightState(UnitAIFightStateType.Idle);
			}
				break;
			case UnitAICommandType.MoveTo:
			case UnitAICommandType.PickUpItem:
			{
				_aiLogic.setFightState(UnitAIFightStateType.Move);
				//向目标移动
				_unit.move.moveTo(_commandMoveType,_commandPos);
			}
				break;
			case UnitAICommandType.MoveToUnit:
			{
				_aiLogic.setFightState(UnitAIFightStateType.Move);
				
				Unit unit=_commandUnit.getUnit();
				
				if(unit!=null)
				{
					//向目标移动
					_unit.move.moveToUnit(unit,_commandDistance);
				}
			}
				break;
			case UnitAICommandType.SkillTo:
			{
				_aiLogic.clearFightAI();
				_aiLogic.setFightState(UnitAIFightStateType.Pursue);
				_aiLogic.setPursueSkill(_commandSkillData);
				_aiLogic.setPursueCommandTarget(_commandSkillTargetData);
			}
				break;
			case UnitAICommandType.AttackMoveTo:
			{
				//设置锚点
				_aiLogic.setCurrentAnchorPos();
				
				//先停止移动
				if(_unit.move.canMoveNow())
				{
					_unit.move.moveTo(_commandPos);
				}
				
				_aiLogic.setFightState(UnitAIFightStateType.Idle);
			}
				break;
			case UnitAICommandType.Follow:
			case UnitAICommandType.FollowAttack:
			{
				Unit unit=_commandUnit.getUnit();
				
				if(unit!=null)
				{
					//设置原点
					_aiLogic.setAnchorUnit(unit,_fightUnitConfig.followRadius);
					
					//先停止移动
					if(_unit.move.canMoveNow())
					{
						_aiLogic.setFightState(UnitAIFightStateType.Idle);
						_unit.move.moveToUnit(unit,_fightUnitConfig.followRadius);
					}
				}
				else
				{
					//主没了
					commandOver();
				}
			}
				break;
		}
	}
	
	//--指令组--//
	
	public void commandToNone()
	{
		doLoopCommand(UnitAICommandType.None);
	}
	
	public void commandToProtect()
	{
		doLoopCommand(UnitAICommandType.Protect);
	}
	
	/** 移动到 */
	public void moveTo(PosData pos,Runnable func)
	{
		moveTo(pos,UnitMoveType.Run,func);
	}
	
	/** 移动到 */
	public void moveTo(PosData pos,int moveType,Runnable func)
	{
		clearCurrentCommand();
		
		_commandType=UnitAICommandType.MoveTo;
		_commandPos.copyPos(pos);
		_commandOver=func;
		_commandMoveType=moveType;
		
		runCommand();
	}
	
	/** 移动到 */
	public void moveTo(Unit unit,float radius,Runnable func)
	{
		moveTo(unit,UnitMoveType.Run,radius,func);
	}
	
	/** 移动到 */
	public void moveTo(Unit unit,int moveType,float radius,Runnable func)
	{
		clearCurrentCommand();
		
		_commandType=UnitAICommandType.MoveToUnit;
		_commandUnit.setUnit(unit);
		_commandDistance=radius;
		_commandOver=func;
		_commandMoveType=moveType;
		
		runCommand();
	}
	
	/** 技能到 */
	public void skillTo(int skillID,SkillTargetData tData)
	{
		SkillData sData=_aiLogic.getUnitSkills().get(skillID);
		
		//没有技能
		if(sData==null)
			return;
		
		if(tData==null)
			tData=SkillTargetData.createByNone();
		
		clearCurrentCommand();
		
		_commandType=UnitAICommandType.SkillTo;
		_commandSkillData=sData;
		_commandSkillTargetData=tData;
		
		runCommand();
	}
	
	/** 攻击移动到 */
	public void attackMoveTo(PosData pos)
	{
		clearCurrentCommand();
		
		_commandType=UnitAICommandType.AttackMoveTo;
		_commandPos.copyPos(pos);
		
		runCommand();
	}
	
	/** 拾取物品 */
	public void pickUpItem(int targetInstanceID)
	{
		Unit targetUnit=_scene.getUnit(targetInstanceID);
		
		if(targetUnit==null)
			return;
		
		clearCurrentCommand();
		
		_commandType=UnitAICommandType.PickUpItem;
		_commandUnit.setUnit(targetUnit);
		_commandPos.copyPos(targetUnit.pos.getPos());
		
		runCommand();
	}
	
	/** 跟随 */
	public void follow(Unit unit)
	{
		clearCurrentCommand();
		
		_commandType=UnitAICommandType.Follow;
		_commandUnit.setUnit(unit);
		
		runCommand();
	}
	
	/** 跟随 */
	public void followAttack(Unit unit)
	{
		clearCurrentCommand();
		
		_commandType=UnitAICommandType.FollowAttack;
		_commandUnit.setUnit(unit);
		
		runCommand();
	}
}
