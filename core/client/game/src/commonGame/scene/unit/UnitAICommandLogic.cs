using System;
using ShineEngine;

/// <summary>
///
/// </summary>
public class UnitAICommandLogic:UnitLogicBase
{
	/** 战斗单位配置 */
	protected FightUnitConfig _fightUnitConfig;
	/** ai逻辑 */
	protected UnitAILogic _aiLogic;

	/** 默认指令 */
	private int _defaultCommand=UnitAICommandType.None;

	//指令部分
	/** 当前指令 */
	private int _commandType=UnitAICommandType.None;
	/** 指令序号 */
	private int _commandIndex;
	/** 指令经过时间(部分指令用) */
	private int _commandPassTime;
	/** 指令完成回调 */
	private Action _commandOver;
	/** 目标位置 */
	private PosData _commandPos=new PosData();
	/** 目标朝向 */
	private DirData _commandDir=new DirData();
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

	private PosData _tempPos=new PosData();

	public override void init()
	{
		base.init();

		_aiLogic=_unit.ai;
		_fightUnitConfig=_unit.fight.getFightUnitConfig();

		setDefaultCommand(_fightUnitConfig.defaultAICommand);
	}

	public override void dispose()
	{
		base.dispose();

		clearCurrentCommand();
		_fightUnitConfig=null;
	}

	public override void afterInit()
	{
		base.afterInit();

		initAI();

		checkPetFollow();
	}

	public override void onSecond()
	{
		base.onSecond();

		checkPetFollow();
	}

	private void checkPetFollow()
	{
		if(_unit.isSelfControl())
		{
			if(_unit.identity.isPet())
			{
				if(getCurrentCommand()!=UnitAICommandType.FollowAttack)
				{
					Unit master=_scene.getCharacterByPlayerID(_unit.identity.playerID);

					if(master!=null)
					{
						followAttack(master);
					}
				}
			}
		}
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
	public bool isCommandEmpty()
	{
		return _commandType==UnitAICommandType.None;
	}

	/** 当前指令是否可战斗 */
	public bool isCommandCanFight()
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
	public bool isCommandCanSearchTarget()
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

	public bool isCommandNeedWander()
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
	public bool isCommandCanChangePursueSkill()
	{
		return _commandType!=UnitAICommandType.SkillTo;
	}

	/** 当前指令是否可更换追击技能目标 */
	public bool isCommandCanChangePursueUnit()
	{
		return _commandType!=UnitAICommandType.SkillTo;
	}

	/** 当前指令是否需要检查退回 */
	public bool isCommandNeedCheckBack()
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
					//向目标移动
					_unit.move.moveTo(_commandPos);
				}
				else
				{
					if(_commandType==UnitAICommandType.PickUpItem)
					{
						if(_unit.pos.calculateDistanceSq(_commandPos)<=Global.pickUpRadiusSq)
						{
							pickUpMoveOver();
						}
					}
				}
			}
				break;
			case UnitAICommandType.MoveDir:
			{
				UnitMoveLogic unitMoveLogic=_unit.move;

				_commandPassTime+=delay;

				//没有移动中
				if(unitMoveLogic.canMoveNow() && (!unitMoveLogic.isMoving() || _commandPassTime>=Global.moveDirSendDelay))
				{
					_commandPassTime=0;
					doMoveDirOnce();
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

	/** 恢复单位本身默认行为 */
	public void reDefaultCommand()
	{
		doLoopCommand(_defaultCommand);
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
		Action func=_commandOver;

		doLoopCommand(_defaultCommand);

		//TODO:是否下帧调用

		if(func!=null)
		{
			func();
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
	public void onCommandMoveToOverOnIdle(bool isSuccess)
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
	public void onCommandMoveToOver(bool isSuccess)
	{
		switch(_commandType)
		{
			case UnitAICommandType.MoveTo:
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

	public bool onCommandSkillOver()
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
		}
	}

	public void setCurrentCommandSkillIndex()
	{
		_commandSkillIndex=_commandIndex;
	}

	/** 执行指令 */
	protected void runCommand()
	{
		if(!_aiLogic.isAIRunning())
		{
			Ctrl.throwError("AI未启动");
			return;
		}

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
			case UnitAICommandType.MoveDir:
			{
				_aiLogic.setFightState(UnitAIFightStateType.Move);

				//可移动再计算
				if(_unit.move.canMoveNow())
				{
					doMoveDirOnce();
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

	/** 执行一次移动朝向 */
	private void doMoveDirOnce()
	{
		_unit.move.moveDir(_commandDir);
		
		// if(CommonSetting.clientMoveDirUseForecast)
		// {
		// 	float dis=_unit.move.getUseMoveSpeedM() * Global.moveDirForecastTime;
		//
		// 	if(dis<Global.moveDirForecastMinDistance)
		// 		dis=Global.moveDirForecastMinDistance;
		//
		// 	_scene.pos.findRayPos(_unit.move.getMoveType(),_tempPos,_unit.pos.getPos(),_commandDir.direction,dis);
		// 	_unit.move.moveTo(_tempPos);
		// }
		// else
		// {
		// 	_unit.move.moveDir(_commandDir);
		// }
	}

	//--指令辅助--//

	/** 拾取移动结束 */
	private void pickUpMoveOver()
	{
		Unit unit=_commandUnit.getUnit();

		if(unit!=null && checkCanPickUp(unit))
		{
			doPickUp(unit);
			commandOver();
		}
		else
		{
			commandFailed();
		}

	}

	protected virtual void doPickUp(Unit unit)
	{
		if(unit.getType()==UnitType.FieldItem)
		{
			if(checkCanPickUp(unit))
			{
				CUnitPickUpItemRequest.create(_unit.instanceID,unit.instanceID).send();
			}
		}
		else
		{
			FieldItemBagBindData bData=_scene.role.getFieldItemBagBind(unit.instanceID);

			if(bData!=null)
			{
				doPickUpFieldItemBagBind(bData);
			}
		}
	}

	/** 执行拾取绑定物品掉落包 */
	protected virtual void doPickUpFieldItemBagBind(FieldItemBagBindData data)
	{
		//目前直接拾取
		if(GameC.player.bag.hasItemPlace(data.items))
		{
			CUnitPickUpItemBagAllRequest.create(_unit.instanceID,data.instanceID).send();
		}
	}

	/** 检查是否可拾取 */
	public bool checkCanPickUp(Unit unit)
	{
		if(unit.getType()!=UnitType.FieldItem)
			return false;

		return checkCanPickUpFieldItem(unit);
	}

	/** 检查是否可拾取 */
	public bool checkCanPickUpFieldItem(Unit fieldItem)
	{
		FieldItemIdentityData iData=(FieldItemIdentityData)fieldItem.getUnitData().identity;

		ItemData itemData=iData.item;

		if(!GameC.player.bag.hasItemPlace(itemData))
			return false;

		return true;
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
	public void moveTo(PosData pos,Action func=null)
	{
		moveTo(pos,UnitMoveType.Run,func);
	}

	/** 移动到 */
	public void moveTo(PosData pos,int moveType,Action func=null)
	{
		clearCurrentCommand();

		_commandType=UnitAICommandType.MoveTo;
		_commandPos.copyPos(pos);
		_commandOver=func;
		_commandMoveType=moveType;

		runCommand();
	}

	/** 技能到 */
	public void skillTo(int skillID,SkillTargetData tData,Action func=null)
	{
		SkillData sData=_aiLogic.getUnitSkills().get(skillID);

		//没有技能
		if(sData==null)
			return;

		if(tData==null)
			tData=SkillTargetData.createByNone();

		clearCurrentCommand();

		_commandType=UnitAICommandType.SkillTo;
		_commandOver=func;
		_commandSkillData=sData;
		_commandSkillTargetData=tData;

		runCommand();
	}

	/** 攻击移动到 */
	public void attackMoveTo(PosData pos,Action func=null)
	{
		clearCurrentCommand();

		_commandType=UnitAICommandType.AttackMoveTo;
		_commandOver=func;
		_commandPos.copyPos(pos);

		runCommand();
	}

	/** 拾取物品 */
	public void pickUpItem(int targetInstanceID,Action func=null)
	{
		Unit targetUnit=_scene.getUnit(targetInstanceID);

		if(targetUnit==null)
			return;

		clearCurrentCommand();

		_commandType=UnitAICommandType.PickUpItem;
		_commandOver=func;
		_commandUnit.setUnit(targetUnit);
		_commandPos.copyPos(targetUnit.pos.getPos());

		runCommand();
	}

	/** 移动朝向 */
	public void moveDir(DirData dir)
	{
		clearCurrentCommand();

		_commandType=UnitAICommandType.MoveDir;
		_commandDir.copyDir(dir);

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