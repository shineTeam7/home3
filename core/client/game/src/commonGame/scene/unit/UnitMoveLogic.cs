using System;
using ShineEngine;
using UnityEngine;
using UnityEngine.AI;

/// <summary>
/// 单位移动逻辑
/// </summary>
public class UnitMoveLogic:UnitLogicBase
{
	protected UnitMoveData _d;
	/** 位置数据 */
	protected PosData _pos;
	/** 朝向数据 */
	protected DirData _dir;

	/** 基元使用移速(每毫秒移动值) */
	protected float _useMoveSpeedM;
	/** 基元使用移速(每毫秒移动值)(加上动作因素,走跑) */
    protected float _useMoveSpeedMForMove;
	/** 服务器移动缩放率(首点) */
	protected float _serverMoveRatio=1f;

	protected ScenePosLogic _scenePosLogic;

	protected int _moveType=MapMoveType.Land;

	protected float _walkSpeedRatio=1f;


	//baseMove

	/** 基元移动朝向 */
	protected DirData _baseMoveDir=new DirData();
	/** 基元移动目标(缓存目标) */
	protected PosData _baseMovePos=new PosData();
	/** 基元移动方向分量(每毫秒) */
	protected PosData _baseMoveSpeedVector=new PosData();
	/** 当前移动是否本端主动发起 */
	private bool _currentMoveIsInitiative=false;
	/** 移动剩余毫秒数 */
	protected float _moveLastTimeMillis=0;
	/** 实际移动速度比率 */
	protected float _realMoveSpeedRatio=-1f;

	/** (side)实际移动朝向 */
	private DirData _realMoveDir=new DirData();


	//specialMove

	/** 特殊移动配置 */
	protected SpecialMoveConfig _specialMoveConfig;
	/** 特殊移动类型 */
	protected int _specialMoveType;
	/** 特殊移动速度 */
	protected float _specialMoveSpeed;

	//moveList

	/** 基元移动点组 */
	private SList<PosData> _moveList=new SList<PosData>();
	/** 基元移动点组序号(-1为没有点组移动) */
	private int _moveListIndex=-1;
	/** 移动目标点 */
	private PosData _moveTargetPos=new PosData();
	/** 与目标点停止距离 */
	private float _targetDisQ;
	/** 是否移动到单位 */
	private bool _isMoveToUnit=false;
	/** 移动单位 */
	private UnitReference _moveTargetUnit=new UnitReference();

	//TODO:缓存上一条寻路路径

	private int _sendLastTime=0;

	//drive

	/** 驾驶状态当前移速(每毫秒移动值) */
	protected float _driveCurrentMoveSpeedM;
	/** 是否需要以驾驶方式移动 */
	protected bool _needDrive;

	protected float _driveTurnRadius;
	/** 加速加速度(每毫秒使用速度) */
	protected float _driveAccelerateSpeedM;
	/** 驾驶地面减速度 */
	protected float _driveGroundFrictionM;

	//--ACT部分--//

	/** 地面状态 */
	protected int _groundState=UnitActGroundStateType.Ground;
	/** 是否僵直 */
	protected bool _isSpasticity=false;
	/** 是否浮空受击 */
	protected bool _isBlowHurt=false;
	/** 是否下落(否则就是上飞) */
	protected bool _isBlowDown=true;

	//vehicle
	/** 载具实例 */
	protected Unit _vehicle;
	/** 载具序号 */
	protected int _vehicleIndex;

	protected PosData _tempPos=new PosData();

	protected DirData _tempDir=new DirData();

	public UnitMoveLogic()
	{

	}

	public override void init()
	{
		base.init();

		_scenePosLogic=_scene.pos;

		_d=_data.move;
		_pos=_data.pos.pos;
		_dir=_data.pos.dir;

		//战斗数据
		if(_data.fight!=null)
		{
			FightUnitConfig fightUnitConfig=_data.getFightIdentity().getFightUnitConfig();

			_moveType=fightUnitConfig.mapMoveType;

			_walkSpeedRatio=fightUnitConfig.walkSpeedRatio/1000f;
			_needDrive=fightUnitConfig.needDrive;
			_driveTurnRadius=fightUnitConfig.driveTurnRadius;

			if(fightUnitConfig.driveAccelerateSpeed==0)
			{
				_driveAccelerateSpeedM=0f;
				_driveGroundFrictionM=0f;
			}
			else
			{
				_driveAccelerateSpeedM=fightUnitConfig.driveAccelerateSpeed*Global.useMoveSpeedRatio/1000000f;
				//先暂时取陆地的
				_driveGroundFrictionM=MapBlockTypeConfig.get(MapBlockType.Land).groundFriction*Global.useMoveSpeedRatio/1000000f;
			}

			calculateUseMoveSpeed();
		}
		else
		{
			_walkSpeedRatio=1;
		}
	}

	public override void afterInit()
	{
		base.afterInit();

		if(_d.vehicleInstanceID>0)
		{
			Unit unit=_scene.getUnit(_d.vehicleInstanceID);

			if(unit!=null)
			{
				unit.pos.addMoveBindUnit(_unit);
			}
		}

		//当前有移动状态
		if(_d.baseMoveState!=UnitBaseMoveState.None)
		{
			switch(_d.baseMoveState)
			{
				case UnitBaseMoveState.MoveToDir:
				{
					baseMoveToDir(_d.moveType,_dir,_d.realMoveDir,_d.realMoveSpeedRatio,false);
				}
					break;
				case UnitBaseMoveState.MoveToPos:
				{
					_moveList.clear();

					if(_d.moveList!=null)
					{
						_moveList.addAll(_d.moveList);
						_moveListIndex=0;
					}
					else
					{
						_moveListIndex=-1;
					}

					baseMoveToPos(_d.moveType,_d.baseMovePos,false);
				}
					break;
				case UnitBaseMoveState.SpecialMove:
				{
					toSpecialMove(_d.specialMoveID,_d.specialMoveArgs,false,false);
				}
					break;
			}
		}
	}

	public override void preRemove()
	{
		toStopMove(false,false);

		base.preRemove();
	}

	public override void dispose()
	{
		base.dispose();

		_scenePosLogic=null;
		_d=null;
		_pos=null;
		_dir=null;

		_specialMoveConfig=null;

		_groundState=UnitActGroundStateType.Ground;
		_isSpasticity=false;
		_isBlowHurt=false;
		_isBlowDown=true;
		_needDrive=false;

		_moveTargetPos.clear();

		_sendLastTime=0;

		_moveType=MapMoveType.Land;
		_walkSpeedRatio=1f;
	}

	public override void onFrame(int delay)
	{
		base.onFrame(delay);

		moveFrame(delay);

		if(_sendLastTime>0)
		{
			if((_sendLastTime-=delay)<0)
			{
				_sendLastTime=0;
				reSendMove();
			}
		}
	}

	public override void onPiece(int delay)
	{
		base.onPiece(delay);

		//移动
		switch(_d.baseMoveState)
		{
			case UnitBaseMoveState.MoveToPos:
			{
				if(_isMoveToUnit)
				{
					Unit unit=_moveTargetUnit.getUnit();

					if(unit!=null)
					{
						PosData pos=unit.pos.getPos();

						//目标位置变更
						if(!_moveTargetPos.isEquals(pos))
						{
							toMoveToList(_d.moveType,pos,_targetDisQ);
							return;
						}
					}
					else
					{
						stopMoveList(false);
						return;
					}
				}

				if(_targetDisQ>0f && _scenePosLogic.calculatePosDistanceSq2D(_pos,_moveTargetPos)<=_targetDisQ)
				{
					stopMoveList(true);
					return;
				}
			}
				break;
		}
	}

	/** 计算使用移速 */
	private void calculateUseMoveSpeed()
	{
		_useMoveSpeedM=_data.fightDataLogic.attribute.getRealMoveSpeed() * Global.useMoveSpeedRatio / 1000f;
	}

	/** 计算移速分量 */
	private void calculateMoveSpeedVector()
	{
		if(_d.moveType==UnitMoveType.Walk)
		{
			_useMoveSpeedMForMove=_useMoveSpeedM*_walkSpeedRatio;
		}
		else
		{
			_useMoveSpeedMForMove=_useMoveSpeedM;
		}

		_scenePosLogic.calculateVectorByDir(_baseMoveSpeedVector,_baseMoveDir,_realMoveSpeedRatio>=0f ? _useMoveSpeedMForMove*_realMoveSpeedRatio : _useMoveSpeedMForMove);
	}

	/** 获取实际移速值 */
	public float getUseMoveSpeedM()
	{
		return _useMoveSpeedM;
	}

	/** 当前状态是否可移动(所有状态) */
	public bool canMoveNow()
	{
		return _unit.fight.isSkillCanMove() && _d.baseMoveState!=UnitBaseMoveState.SpecialMove;
	}

	/** 当前是否待机中(没有任何技能和移动) */
	public bool isAllFreeNow()
	{
		return _d.baseMoveState==UnitBaseMoveState.None && !_unit.fight.isSkilling() && isStateFree();
	}

	/** 正在移动中(任意移动) */
	public bool isMoving()
	{
		return _d.baseMoveState!=UnitBaseMoveState.None;
	}

	/** 获取移动类型 */
	public int getMoveType()
	{
		return _moveType;
	}

	//基元移动
	protected virtual void moveFrame(float delay)
	{
		if(_d.baseMoveState==UnitBaseMoveState.None)
			return;

		//移动
		switch(_d.baseMoveState)
		{
			case UnitBaseMoveState.MoveToDir:
			{

				CharacterController controller;
				if(_currentMoveIsInitiative && (controller=_unit.control.getCharacterController())!=null)
				{
					_tempPos.clear();
					_scenePosLogic.addPosByVector(_tempPos,_baseMoveSpeedVector,delay);

					CollisionFlags collisionFlags=controller.Move(_tempPos.getVector());

					//贴边
					if(collisionFlags==CollisionFlags.Sides)
					{
						_tempPos.setByVector(controller.transform.position);

						_scenePosLogic.calculateDirByPos(_realMoveDir,_pos,_tempPos);

						float dis0=_useMoveSpeedMForMove * delay;
						float dis1=_scenePosLogic.calculatePosDistance(_tempPos,_pos);

						if(dis0<=0f)
						{
							Ctrl.errorLog("moveDir速度小于0");
							_realMoveSpeedRatio=-1;
						}
						else
						{
							_realMoveSpeedRatio=dis1 / dis0;
						}

					}
					else
					{
						_realMoveSpeedRatio=-1;
						_realMoveDir.copyDir(_baseMoveDir);

						_scenePosLogic.addPos(_tempPos,_pos);

					}

					if(_scenePosLogic.isPosEnabled(_moveType,_tempPos))
					{
						_unit.pos.setPos(_tempPos);
					}
					else
					{
						Ctrl.print("出现一次moveDir不可走",_tempPos);
						//恢复
						_unit.pos.onSetPos();
					}
				}
				else
				{
					_tempPos.copyPos(_pos);
					_scenePosLogic.addPosByVector(_tempPos,_baseMoveSpeedVector,delay);

					//可走
					if(_scenePosLogic.isPosEnabled(_moveType,_tempPos))
					{
						_unit.pos.setPos(_tempPos);
					}
				}

			}
				break;
			case UnitBaseMoveState.MoveToPos:
			{
				if(CommonSetting.needMoveLerp)
				{
					delay*=_serverMoveRatio;
				}

				//不乘系数
				if(_moveLastTimeMillis>0)
				{
					delay+=_moveLastTimeMillis;
					_moveLastTimeMillis=0f;
				}

				float sq=_unit.pos.calculateDistanceSq(_baseMovePos);
				float spd=_useMoveSpeedMForMove*delay;
				//到达
				if(spd*spd >= sq)
				{
					//设置位置
					_unit.pos.setPos(_baseMovePos);

					_moveLastTimeMillis=(float)((spd-Math.Sqrt(sq))/_useMoveSpeedMForMove);

					baseMovePosOver();
				}
				else
				{
					_scenePosLogic.addPosByVector(_pos,_baseMoveSpeedVector,delay);

					_unit.pos.onSetPos();
				}
			}
				break;
			case UnitBaseMoveState.SpecialMove:
			{
				specialMoveFrame(delay);
			}
				break;
			case UnitBaseMoveState.Drive:
			{
				driveMoveFrame(delay);
			}
				break;
		}
	}

	/** 移速变化 */
	public void onMoveSpeedChange()
	{
		calculateUseMoveSpeed();

		switch(_d.baseMoveState)
		{
			case UnitBaseMoveState.MoveToDir:
			case UnitBaseMoveState.MoveToPos:
			{
				calculateMoveSpeedVector();
			}
				break;
		}
	}

	//基元移动

	/** 获取当前基元移动状态 */
	public int getBaseMoveState()
	{
		return _d.baseMoveState;
	}

	/** 基元移动清回 */
	private void clearBaseMove()
	{
		if(_d.baseMoveState==UnitBaseMoveState.None)
			return;

		switch(_d.baseMoveState)
		{
			case UnitBaseMoveState.MoveToDir:
			{
				_d.realMoveDir=null;
				_d.realMoveSpeedRatio=-1;
			}
				break;
			case UnitBaseMoveState.MoveToPos:
			{
				_d.baseMovePos=null;
				_moveLastTimeMillis=0f;
			}
				break;
			case UnitBaseMoveState.SpecialMove:
			{
				if(_unit.isDriveAll() && UnitSpecialMoveTypeConfig.get(_specialMoveType).withVertigo)
				{
					_unit.fight.getStatusLogic().subStatus(StatusType.Vertigo);
				}

				_d.specialMoveID=-1;
				_d.specialMoveArgs=null;
				_d.specialMoveLastTime=0;
				_specialMoveConfig=null;
				_specialMoveType=-1;
			}
				break;
			case UnitBaseMoveState.Drive:
			{
				_d.driveData=null;
			}
				break;
		}

		_d.baseMoveState=UnitBaseMoveState.None;
	}

	/** 移除移动结束 */
	private void baseMovePosOver()
	{
		if(CommonSetting.needMoveLerp)
		{
			_serverMoveRatio=1f;
		}

		//移动组
		if(_moveListIndex!=-1)
		{
			if(_moveListIndex<(_moveList.size()-1))
			{
				toMoveOnePos(_moveList.get(++_moveListIndex));
			}
			else
			{
				_moveListIndex=-1;

				clearBaseMove();
				reIdle();
				moveListOver(true);
			}
		}
		else
		{
			clearBaseMove();
			reIdle();
		}
	}

	/** 特殊移动结束 */
	protected void specialMoveOver()
	{
		clearBaseMove();
		reIdle();
	}

	/** 定向移动 */
	private void baseMoveToDir(int type,DirData dir,DirData realDir,float realSpeedRatio,bool isInitiative)
	{
		if(isInitiative)
		{
			if(_unit.control==null || _unit.control.getCharacterController()==null)
			{
				Ctrl.errorLog("主动朝向移动，必须要有CharacterController");
				return;
			}
		}


		clearBaseMove();

		_d.baseMoveState=UnitBaseMoveState.MoveToDir;
		_d.moveType=type;
		_currentMoveIsInitiative=isInitiative;

		toMoveDir(dir,realDir,realSpeedRatio);

		actStartMove(type);

		if(isInitiative)
		{
			sendMoveDir();
		}
	}

	/** 定点移动 */
	private void baseMoveToPos(int type,PosData pos,bool isInitiative)
	{
		clearBaseMove();

		_d.baseMoveState=UnitBaseMoveState.MoveToPos;
		_d.moveType=type;
		_currentMoveIsInitiative=isInitiative;

		toMoveOnePos(pos);

		actStartMove(type);
	}

	private void toMoveOnePos(PosData pos)
	{
		(_d.baseMovePos=_baseMovePos).copyPos(pos);
		_scenePosLogic.calculateDirByPos(_baseMoveDir,_pos,pos);

		//设置朝向
		_unit.pos.setDir(_baseMoveDir);

		calculateMoveSpeedVector();
	}

	private void toMoveDir(DirData dir,DirData realDir,float realSpeedRatio)
	{
		//设置朝向
		_unit.pos.setDir(dir);
		//有
		if(realSpeedRatio>=0f)
		{
			_realMoveSpeedRatio=realSpeedRatio;
			_baseMoveDir.copyDir(realDir);
		}
		else
		{
			_realMoveSpeedRatio=-1;
			_baseMoveDir.copyDir(dir);
		}

		calculateMoveSpeedVector();
	}

	private void clearMoveList()
	{
		_d.moveList=null;

		_moveList.clear();
		_moveListIndex=-1;
		_moveTargetPos.clear();
		_serverMoveRatio=1f;
	}

	/** 移动点组完成 */
	protected void moveListOver(bool isSuccess)
	{
		UnitAILogic ai;
		if((ai=_unit.ai)!=null)
			ai.onMoveToOver(isSuccess);
	}

	/** 停止移动组 */
	protected void stopMoveList(bool isSuccess)
	{
		toStopMove(true,true);
		moveListOver(isSuccess);
	}

	/** 向某朝向移动 */
	public void moveDir(DirData dir)
	{
		moveDir(UnitMoveType.Run,dir);
	}

	/** 向某朝向移动 */
	public void moveDir(int type,DirData dir)
	{
		if(!canMoveNow())
			return;

		//TODO:朝向合法性验证

		baseMoveToDir(type,dir,null,-1f,true);
	}

	protected void toMoveToList(int unitMoveType,PosData pos,float disQ)
	{
		if(disQ>0)
		{
			if(_scenePosLogic.calculatePosDistanceSq2D(_pos,pos)<=disQ)
			{
				stopMoveList(true);
				return;
			}
		}

		_targetDisQ=disQ;

		//目标点
		_moveTargetPos.copyPos(pos);

		_scene.pos.findPath(_moveType,_moveList,_pos,pos);

		//无法到达
		if(_moveList.isEmpty())
		{
			Ctrl.print("无法到达");
			stopMoveList(false);
			return;
		}

		clearBaseMove();

		_d.baseMoveState=UnitBaseMoveState.MoveToPos;
		_d.moveType=unitMoveType;
		_currentMoveIsInitiative=true;
		_moveListIndex=0;

		_sendLastTime=0;
		sendMoveList();
		doMoveList();
	}

	/** 寻路移动到 */
	public void moveTo(PosData pos)
	{
		//默认跑
		moveTo(UnitMoveType.Run,pos,0f);
	}

	/** 寻路移动到 */
	public void moveTo(PosData pos,float dis)
	{
		//默认跑
		moveTo(UnitMoveType.Run,pos,dis);
	}

	/** 寻路移动到(主动) */
	public void moveTo(int unitMoveType,PosData pos)
	{
		moveTo(unitMoveType,pos,0f);
	}

	/** 寻路移动到(主动) */
	public void moveTo(int unitMoveType,PosData pos,float dis)
	{
		if(!canMoveNow())
			return;

		if(dis<0f)
			return;

		//位置纠正
		BaseGameUtils.makeTerrainPos(pos);

		//相同点
		if(_d.baseMoveState==UnitBaseMoveState.MoveToPos && _moveTargetPos.isEquals(pos))
			return;

		_isMoveToUnit=false;
		_moveTargetUnit.clear();
		toMoveToList(unitMoveType,pos,MathUtils.floatEquals(dis,0f) ? 0f:dis*dis);
	}

	/** 寻路移动到单位(主动) */
	public void moveToUnit(Unit unit,float dis)
	{
		//默认跑
		moveToUnit(UnitMoveType.Run,unit,dis);
	}

	/** 寻路移动到单位(主动) */
	public void moveToUnit(int unitMoveType,Unit unit,float dis)
	{
		if(!canMoveNow())
			return;

		if(dis<0f)
			return;

		PosData pos=unit.pos.getPos();

		//相同点
		if(_d.baseMoveState==UnitBaseMoveState.MoveToPos && _moveTargetPos.isEquals(pos))
			return;

		_isMoveToUnit=true;
		_moveTargetUnit.setUnit(unit);

		toMoveToList(unitMoveType,pos,dis*dis);
	}

	private void sendMoveDir()
	{
		if(_sendLastTime==0)
		{
			_sendLastTime=Global.clientMoveSendMinDelay;

			if(_realMoveSpeedRatio>=0f)
			{
				CUnitMoveDirRequest.create(_unit.instanceID,_d.moveType,_pos,_baseMoveDir,_realMoveDir,(int)Math.Round(_realMoveSpeedRatio*100f)).send();
			}
			else
			{
				CUnitMoveDirRequest.create(_unit.instanceID,_d.moveType,_pos,_baseMoveDir,null,-1).send();
			}
		}
	}

	/** 推送移动组 */
	private void sendMoveList()
	{
		if(_sendLastTime==0)
		{
			_sendLastTime=CommonSetting.moveListSendDelay;

			//需要裁剪
			if(_moveListIndex>0)
			{
				_moveList=_moveList.subList(_moveListIndex,_moveList.size());
				_moveListIndex=0;
			}

			Ctrl.print("sendMoveList");

			if(_moveList.size()==1)
			{
				CUnitMovePosRequest.create(_unit.instanceID,_d.moveType,_pos,_moveList.get(0)).send();
			}
			else
			{
				CUnitMovePosListRequest.create(_unit.instanceID,_d.moveType,_pos,_moveList).send();
			}
		}
	}

	private void sendDrive()
	{
		if(_sendLastTime==0)
		{
			_sendLastTime=Global.clientMoveSendMinDelay;

			CUnitDriveRequest.create(_unit.instanceID,_unit.pos.getPosDir(),_d.driveData).send();
		}
	}

	/** 继续发送移动 */
	private void reSendMove()
	{
		switch(_d.baseMoveState)
		{
			case UnitBaseMoveState.MoveToDir:
			{
				sendMoveDir();
			}
				break;
			case UnitBaseMoveState.MoveToPos:
			{
				sendMoveList();
			}
				break;
			case UnitBaseMoveState.Drive:
			{
				sendDrive();
			}
				break;
		}

	}

	private void doMoveList()
	{
		actStartMove(_d.moveType);

		toMoveOnePos(_moveList.get(_moveListIndex));
	}


	/** 特殊移动 */
	public void specialMove(int id,int[] args)
	{
		toSpecialMove(id,args,true,true);
	}

	/** 执行特殊移动 */
	private void toSpecialMove(int id,int[] args,bool isInitiative,bool isNewOne)
	{
		clearBaseMove();

		_d.baseMoveState=UnitBaseMoveState.SpecialMove;
		_currentMoveIsInitiative=isInitiative;
		_specialMoveConfig=SpecialMoveConfig.get(_d.specialMoveID=id);
		_specialMoveType=_specialMoveConfig.type;
		_d.specialMoveArgs=args;

		//TODO:补充特殊移动

		if(!_unit.isDriveAll() && isInitiative)
		{
			CUnitSpecialMoveRequest.create(_unit.instanceID,id,_unit.pos.getPosDir(),args).send();
		}

		//全自己驱动
		if(_unit.isDriveAll() && UnitSpecialMoveTypeConfig.get(_specialMoveType).withVertigo)
		{
			_unit.fight.getStatusLogic().addStatus(StatusType.Vertigo);
		}

		_unit.show.playMotion(_specialMoveConfig.motionID,false);

		toExecuteSpecialMove(isNewOne);
	}

	/** 执行特殊移动 */
	protected virtual void toExecuteSpecialMove(bool isNewOne)
	{
		//移动完了以后调用specialMoveOver();

		switch(_specialMoveType)
		{
			case UnitSpecialMoveType.Blink:
			{
				if(isNewOne)
				{
					_unit.fight.getCurrentSkillTargetDataPos(_d.baseMovePos=_baseMovePos);
					_d.specialMoveLastTime=1;//1ms
				}
				else
				{
					_baseMovePos=_d.baseMovePos;
				}
			}
				break;
			case UnitSpecialMoveType.HitBack:
			{
				float dis=_specialMoveConfig.args[0];
				int specialMoveTimeMax=(int)_specialMoveConfig.args[1];

				if(isNewOne && _unit.isDriveAll())
				{
					Unit attacker=_scene.getFightUnit(_d.specialMoveArgs[0]);

					if(attacker!=null)
					{
						//取攻击者朝向
						_baseMoveDir.copyDir(attacker.pos.getDir());
						//_scene.pos.calculateDirByPos(_baseMoveDir,attacker.pos.getPos(),_pos);
					}
					else
					{
						_baseMoveDir.copyDir(_dir);
						_scene.pos.inverseDir(_baseMoveDir);
					}

					_scene.pos.findRayPos(_moveType,_d.baseMovePos=_baseMovePos,_pos,_baseMoveDir.direction,dis);

					_d.specialMoveLastTime=specialMoveTimeMax;
				}
				else
				{
					_scene.pos.calculateDirByPos(_baseMoveDir,_pos,_baseMovePos=_d.baseMovePos);
				}

				_tempDir.copyDir(_baseMoveDir);
				_scene.pos.inverseDir(_tempDir);
				_unit.pos.setDir(_tempDir);

				_specialMoveSpeed=dis/specialMoveTimeMax;
				_scenePosLogic.calculateVectorByDir(_baseMoveSpeedVector,_baseMoveDir,_specialMoveSpeed);
			}
				break;
		}
	}

	/** 特殊移动每桢 */
	protected virtual void specialMoveFrame(float delay)
	{
		switch(_specialMoveType)
		{
			//击退
			case UnitSpecialMoveType.Blink:
			case UnitSpecialMoveType.HitBack:
			{
				normalSpecialMoveFrame(delay,true);
			}
				break;
		}
	}

	/** 通用的匀速特殊移动 */
	protected void normalSpecialMoveFrame(float delay,bool withSetPos)
	{
		//到达时间
		if((_d.specialMoveLastTime-=(int)delay)<=0)
		{
			if(withSetPos)
			{
				_unit.pos.setPos(_baseMovePos);
			}

			specialMoveOver();
		}
		else
		{
			float sq=_unit.pos.calculateDistanceSq(_baseMovePos);

			//到达
			if(_specialMoveSpeed*delay >= sq)
			{
				_unit.pos.setPos(_baseMovePos);
			}
			else
			{
				_scenePosLogic.addPosByVector(_pos,_baseMoveSpeedVector,delay);
				_unit.pos.onSetPos();
			}
		}
	}

	/** 停止移动(中止) */
	public void stopMove()
	{
		toStopMove(true,true);
	}

	/** 停止移动(定身) */
	public void stopMoveByStatus()
	{
		if(_d.baseMoveState!=UnitBaseMoveState.SpecialMove)// || UnitSpecialMoveTypeConfig.get(_specialMoveConfig.type).underCantMove
		{
			toStopMove(true,true);
		}
	}

	/** 停止移动(isAll:停止所有) */
	public void toStopMove(bool needReIdle,bool needSend)
	{
		if(_d.baseMoveState==UnitBaseMoveState.None)
			return;

		clearMoveList();
		clearBaseMove();

		_sendLastTime=0;
		if(needSend && _unit.isSelfControl())
		{
			sendStopMove();
		}

		if(needReIdle)
		{
			reIdle();
		}
	}

	protected virtual void onStopMove()
	{

	}

	/** 停止移动(不处理姿态) */
	public void stopMoveWithOutPose()
	{
		clearBaseMove();
	}

	/** 恢复待机动作 */
	public virtual void reIdle()
	{
		if(_unit.canFight())
		{
			actReIdle();
		}
		else
		{
			_unit.show.playMotion(MotionType.Idle);
		}
	}

	/** 推送停止 */
	protected void sendStopMove()
	{
		CUnitStopMoveRequest.create(_unit.instanceID,_unit.pos.getPosDir()).send();
	}

	//--服务器部分--//

	/** 同步指令 */
	public virtual void onServerSyncCommand(PosDirData posDir,int type,int[] ints,float[] floats)
	{
		_unit.pos.setByPosDir(posDir);
	}

	/** 服务器拉回*/
	public void onServerPullBack(PosDirData posDir)
	{
		_unit.pos.setByPosDir(posDir);
		toStopMove(true,false);

		Ctrl.print("被拉回");
	}

	/** 服务器设置位置 */
	public void onServerSetPosDir(PosDirData posDir)
	{
		_unit.pos.setByPosDir(posDir);
	}

	/** 服务器移动点 */
	public void onServerMovePos(int type,PosData pos,int moveTime)
	{
		BaseGameUtils.makeTerrainPos(pos);
		clearMoveList();

		if(CommonSetting.needMoveLerp)
		{
			setServerMoveRatio(pos,moveTime);
		}

		baseMoveToPos(type,pos,false);
	}

	/** 服务器移动点 */
	public void onServerMoveDir(int type,DirData dir,DirData realDir,int moveSpeedRatio)
	{
		clearMoveList();
		baseMoveToDir(type,dir,realDir,moveSpeedRatio<0 ? -1f : moveSpeedRatio/100f,false);
	}

	/** 服务器移动点组 */
	public void onServerMovePosList(int type,SList<PosData> list,int moveTime)
	{
		PosData[] values=list.getValues();

		for(int i=0,len=list.size();i<len;++i)
		{
			BaseGameUtils.makeTerrainPos(values[i]);
		}

		clearMoveList();

		_moveList.addAll(list);

		if(CommonSetting.needMoveLerp)
		{
			setServerMoveRatio(list.get(0),moveTime);
		}

		clearBaseMove();

		_d.baseMoveState=UnitBaseMoveState.MoveToPos;
		_d.moveType=type;
		_currentMoveIsInitiative=false;
		_moveListIndex=0;

		doMoveList();
	}

	/** 设置服务器移动缩放比率 */
	private void setServerMoveRatio(PosData pos,int time)
	{
		_serverMoveRatio=(_scenePosLogic.calculatePosDistance(_pos,pos) / _useMoveSpeedM) / time;
	}

	/** 服务器特殊移动 */
	public void onServerSpecialMove(PosDirData posDir,int id,int[] args,int lastTime,PosData baseMovePos)
	{
		if(_scene.driveType!=SceneDriveType.ServerDriveDamage)
		{
			BaseGameUtils.makeTerrainPos(posDir.pos);
			BaseGameUtils.makeTerrainPos(baseMovePos);

			_unit.pos.setByPosDir(posDir);

		}

		clearBaseMove();

		_d.specialMoveLastTime=lastTime;
		_d.baseMovePos=baseMovePos;

		//视为非新执行
		toSpecialMove(id,args,false,false);
	}

	/** 服务器停止移动 */
	public void onServerStopMove(PosDirData posDir)
	{
		clearMoveList();
		_unit.pos.setByPosDir(posDir);
		toStopMove(true,false);
	}

	//--ACT部分--//

	/** 是否在地面上 */
	public bool isGround()
	{
		return _groundState==UnitActGroundStateType.Ground;
	}

	/** 是否空中自由落体 */
	public bool isBlow()
	{
		return _groundState==UnitActGroundStateType.Blow;
	}

	/** 当前是否自由态 */
	public bool isStateFree()
	{
		return _groundState!=UnitActGroundStateType.Lie && !_isSpasticity && !_isBlowHurt;
	}

	/** 回归待机 */
	private void actReIdle()
	{
		switch(_groundState)
		{
			case UnitActGroundStateType.Ground:
			{
				_unit.show.playMotion(MotionType.Idle);
			}
				break;
			case UnitActGroundStateType.Lie:
			{
				if(_unit.fight.isAlive())
				{
					_unit.show.playMotion(MotionType.Lie);
				}
				else
				{
					_unit.show.playMotion(MotionType.Dead,false);
				}
			}
				break;
			case UnitActGroundStateType.Blow:
			{
				_unit.show.playMotion(_isBlowDown ? MotionType.JumpDown : MotionType.JumpUp);
			}
				break;
			case UnitActGroundStateType.Fly:
			{
				_unit.show.playMotion(MotionType.FlyIdle);
			}
				break;
		}
	}

	/** act开始移动 */
	private void actStartMove(int type)
	{
		switch(_groundState)
		{
			case UnitActGroundStateType.Ground:
			{
				if(type==UnitMoveType.Walk)
				{
					_unit.show.playMotion(MotionType.Walk);
				}
				else if(type==UnitMoveType.Run)
				{
					_unit.show.playMotion(MotionType.Run);
				}
			}
				break;
			case UnitActGroundStateType.Blow:
			{

			}
				break;
			case UnitActGroundStateType.Fly:
			{
				//TODO:看方向决定动作
				//				_unit.show.playAction(MotionType.FlyIdle);
			}
				break;
		}
	}

	/** 跳跃 */
	public void actStartJump()
	{
		setGroundState(UnitActGroundStateType.Blow);
		_isBlowDown=false;
	}

	/** 死亡时刻 */
	public void onDead()
	{
		_groundState=UnitActGroundStateType.Lie;
		_unit.show.playMotion(MotionType.Dead,false);
		//		_unit.show.playMotion(MotionType.Lie,false);
	}

	public void onRevive()
	{
		_groundState=UnitActGroundStateType.Ground;
		reIdle();
	}

	/** 设置地面状态 */
	public void setGroundState(int type)
	{
		_groundState=type;
	}

	/** 上载具 */
	public void onGetOnVehicle(int vehicleInstanceID,int index)
	{
		Unit unit=_scene.getUnit(vehicleInstanceID);

		if(unit==null)
		{
			Ctrl.warnLog("上载具时，未找到载具");
			return;
		}

		VehicleIdentityLogic logic=unit.getVehicleIdentityLogic();

		logic.addDrive(index,_unit.instanceID);

		stopMove();

		_vehicle=unit;
		_vehicleIndex=index;
		_d.vehicleInstanceID=unit.instanceID;

		//可控的话，赋值控制
		if(logic.canDriveIndex(index))
		{
			logic.getData().controlPlayerID=unit.identity.controlPlayerID;
		}

		//设置位置朝向
		_unit.pos.setByPosDir(_vehicle.pos.getPosDir());

		//添加影响
		// logic.addDriveInfluence(_unit);

		onDriveChanged();
	}

	/** 下载具 */
	public void onGetOffVehicle(PosData pos)
	{
		if(_vehicle==null)
		{
			Ctrl.warnLog("下载具时,载具为空");
			return;
		}

		doGetOffVehicle(pos);
	}

	/** 强制取消载具骑乘 */
	public void cancelVehicle()
	{
		if(isOnVehicle())
		{
			doGetOffVehicle(_pos);
		}
	}

	protected void doGetOffVehicle(PosData pos)
	{
		VehicleIdentityLogic logic=_vehicle.getVehicleIdentityLogic();

		// logic.removeDriveInfluence(_unit);

		logic.removeDrive(_vehicleIndex);

		_d.vehicleInstanceID=-1;
		_vehicle=null;
		_vehicleIndex=-1;

		_unit.pos.setPos(pos);

		onDriveChanged();
	}

	/** 骑乘状态改变 */
	protected virtual void onDriveChanged()
	{

	}

	/** 是否在载具上 */
	public bool isOnVehicle()
	{
		return _d.vehicleInstanceID>0;
	}

	/** 获取当前使用的载具 */
	public Unit getVehicle()
	{
		return _vehicle;
	}

	/** 驾驶 */
	public void drive(int forward,int turn)
	{
		if(getBaseMoveState()!=UnitBaseMoveState.Drive)
		{
			clearBaseMove();

			_d.baseMoveState=UnitBaseMoveState.Drive;
			_d.moveType=UnitMoveType.Drive;
		}

		_currentMoveIsInitiative=true;

		DriveData dData;

		if((dData=_d.driveData)==null)
		{
			dData=_d.driveData=new DriveData();
		}

		dData.forward=forward;
		dData.turn=turn;

		if(_driveAccelerateSpeedM==0 && forward==0)
		{
			stopMove();
		}
		else
		{
			sendDrive();
		}
	}

	/** 客户端发起驾驶 */
	public void onServerDrive(PosDirData nowPos,DriveData data)
	{
		_unit.pos.setByPosDir(nowPos);

		if(getBaseMoveState()!=UnitBaseMoveState.Drive)
		{
			clearBaseMove();

			_d.baseMoveState=UnitBaseMoveState.Drive;
			_d.moveType=UnitMoveType.Drive;
		}

		_d.driveData=data;

		if(data.forward==0 && data.turn==0)
		{
			clearBaseMove();
			reIdle();
		}
	}

	protected void driveMoveFrame(float delay)
	{
		_tempPos.clear();

		DriveData dData=_d.driveData;

		float speedAbs=Math.Abs(_driveCurrentMoveSpeedM);

		if(speedAbs==0 && dData.forward==0)
			return;

		//本次移动距离
		float dis;

		//不启用加速度
		if(_driveAccelerateSpeedM==0)
		{
			dis=_useMoveSpeedMForMove*delay*dData.forward;
		}
		else
		{
			//滑行
			if(dData.forward==0)
			{
				//需要的减速时间
				float nTime=speedAbs / _driveGroundFrictionM;
				float useA=_driveCurrentMoveSpeedM>0 ? -_driveGroundFrictionM : _driveGroundFrictionM;

				if(delay<=nTime)
				{
					float d=useA*delay;
					dis=_driveCurrentMoveSpeedM*delay+ d*delay/2;
					_driveCurrentMoveSpeedM+=d;
				}
				//减到0
				else
				{
					dis=_driveCurrentMoveSpeedM*nTime/2;//vt/2
					_driveCurrentMoveSpeedM=0;
				}
			}
			else
			{
				float useA=_driveAccelerateSpeedM*dData.forward;
				bool sameSymbol=MathUtils.sameSymbol(useA,_driveCurrentMoveSpeedM);

				//符号相同，并且已经是最高速度
				if(sameSymbol && speedAbs>=_useMoveSpeedMForMove)
				{
					dis=_driveCurrentMoveSpeedM*delay;
				}
				else
				{
					//需要加速的时间
					float nTime=(_useMoveSpeedMForMove - (sameSymbol ? speedAbs : -speedAbs)) / _driveAccelerateSpeedM;

					//匀加速
					if(delay<=nTime)
					{
						float d=useA*delay;
						dis=_driveCurrentMoveSpeedM*delay+ d*delay/2;
						_driveCurrentMoveSpeedM+=d;
					}
					//到max
					else
					{
						dis=_driveCurrentMoveSpeedM*nTime+ useA*nTime*nTime/2;

						//到达最大速度
						_driveCurrentMoveSpeedM=useA>0 ? _useMoveSpeedMForMove : -_useMoveSpeedMForMove;
						//剩余时间用新速度
						dis+=(_driveCurrentMoveSpeedM*(delay-nTime));
					}
				}
			}
		}

		bool hasDir=false;

		if(dData.turn==0)
		{
			if(dis!=0)
			{
				_scenePosLogic.calculateVectorByDir(_tempPos,_dir,dis);
				_scenePosLogic.addPos(_tempPos,_pos);
			}
			else
			{
				return;
			}
		}
		else
		{
			if(dis!=0)
			{
				float angle=dis / _driveTurnRadius;
				//正方向
				float forward=(float)(_driveTurnRadius*Math.Sin(angle));
				//两侧
				float side=(float)(_driveTurnRadius*(1-Math.Cos(angle)));

				_tempPos.x=forward;
				_tempPos.z=dData.turn*side;
				_tempPos.y=0;

				//朝向向量
				_scenePosLogic.rotatePosByDir2D(_tempPos,_dir);
				_scenePosLogic.addPos(_tempPos,_pos);

				//朝向修改
				_dir.direction=MathUtils.directionCut(_dir.direction - (angle * dData.turn));
				hasDir=true;
			}
			else
			{
				return;
			}
		}

		BaseGameUtils.makeTerrainPos(_tempPos);

		if(hasDir)
		{
			_unit.pos.onSetDir();
		}

		if(_scenePosLogic.isPosEnabled(_moveType,_tempPos))
		{
			_pos.copyPos(_tempPos);
			_unit.pos.onSetPos();
		}
		else
		{
			// Ctrl.print("撞墙",_tempPos.toDataString());
		}

		if(_driveAccelerateSpeedM!=0 && _driveCurrentMoveSpeedM==0f && dData.forward==0 && _currentMoveIsInitiative)
		{
			stopMove();
		}
	}
}