package com.home.commonBase.scene.unit;

import com.home.commonBase.config.game.FightUnitConfig;
import com.home.commonBase.config.game.SpecialMoveConfig;
import com.home.commonBase.config.game.VehicleConfig;
import com.home.commonBase.config.game.enumT.MapBlockTypeConfig;
import com.home.commonBase.config.game.enumT.UnitSpecialMoveTypeConfig;
import com.home.commonBase.constlist.generate.InfoCodeType;
import com.home.commonBase.constlist.generate.MapBlockType;
import com.home.commonBase.constlist.generate.MapMoveType;
import com.home.commonBase.constlist.generate.StatusType;
import com.home.commonBase.constlist.generate.TriggerEventType;
import com.home.commonBase.constlist.generate.UnitBaseMoveState;
import com.home.commonBase.constlist.generate.UnitMoveType;
import com.home.commonBase.constlist.generate.UnitSpecialMoveType;
import com.home.commonBase.constlist.generate.UnitType;
import com.home.commonBase.constlist.scene.PathFindingType;
import com.home.commonBase.constlist.system.SceneDriveType;
import com.home.commonBase.data.scene.base.DirData;
import com.home.commonBase.data.scene.base.DriveData;
import com.home.commonBase.data.scene.base.PosData;
import com.home.commonBase.data.scene.base.PosDirData;
import com.home.commonBase.data.scene.unit.UnitMoveData;
import com.home.commonBase.dataEx.scene.UnitReference;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.global.Global;
import com.home.commonBase.scene.base.Unit;
import com.home.commonBase.scene.base.UnitLogicBase;
import com.home.commonBase.scene.path.GridSceneMap;
import com.home.commonBase.scene.scene.ScenePosLogic;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.SList;
import com.home.shine.utils.MathUtils;

public class UnitMoveLogic extends UnitLogicBase
{
	/** 数据 */
	protected UnitMoveData _d;
	
	/** 位置数据 */
	protected PosData _pos;
	/** 朝向数据 */
	protected DirData _dir;
	
	/** 基元使用移速(每毫秒移动值) */
	protected float _useMoveSpeedM;
	/** 基元使用移速(每毫秒移动值)(加上动作因素,走跑) */
	protected float _useMoveSpeedMForMove;
	
	protected ScenePosLogic _scenePosLogic;
	
	/** 地图移动方式 */
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
	protected boolean _currentMoveIsInitiative=false;
	/** 移动剩余毫秒数 */
	protected float _moveLastTimeMillis=0;
	/** 实际移速比率 */
	protected float _realMoveSpeedRatio=-1f;
	/** 客户端移动朝向持续时间 */
	private int _clientMoveDirLastTime=0;
	
	//specialMove
	
	/** 当前特殊移动配置 */
	protected SpecialMoveConfig _specialMoveConfig;
	/** 特殊移动类型 */
	protected int _specialMoveType;
	/** 特殊移动速度 */
	protected float _specialMoveSpeed;
	
	//moveList
	/** 移动组是否变更 */
	private boolean _moveListDirty=false;
	/** 基元移动点组 */
	protected SList<PosData> _moveList=new SList<>(PosData[]::new);
	/** 基元移动点组序号(-1为没有点组移动) */
	private int _moveListIndex=-1;
	/** 移动目标点 */
	private PosData _moveTargetPos=new PosData();
	/** 与目标点停止距离 */
	private float _targetDisQ;
	/** 是否移动到单位 */
	private boolean _isMoveToUnit=false;
	/** 移动单位 */
	private UnitReference _moveTargetUnit=new UnitReference();
	
	//vehicle
	/** 当前骑乘载具 */
	private Unit _vehicle;
	
	private int _vehicleIndex;
	
	//drive
	/** 驾驶状态当前移速(每毫秒移动值) */
	protected float _driveCurrentMoveSpeedM;
	/** 是否需要以驾驶方式移动 */
	protected boolean _needDrive;
	
	protected float _driveTurnRadius;
	/** 加速加速度(每毫秒使用速度)(正值) */
	protected float _driveAccelerateSpeedM;
	/** 驾驶地面减速度 */
	protected float _driveGroundFrictionM;
	/** 是否需要格子标记 */
	private boolean _needCrowedGrid=false;
	/** 是否需要重置到可用格子点 */
	private boolean _needResetBlockGrid=false;
	
	private GridSceneMap _gMap;
	
	private int[] _tempArr=new int[2];
	protected PosData _tempPos=new PosData();
	protected DirData _tempDir=new DirData();
	
	@Override
	public void init()
	{
		super.init();
		
		_scenePosLogic=_scene.pos;
		
		_d=_data.move;
		_pos=_data.pos.pos;
		_dir=_data.pos.dir;
		
		//有战斗数据
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
			_walkSpeedRatio=1f;
		}
		
		//TODO:补充移动逻辑中的位置校验
	}
	
	@Override
	public void afterInit()
	{
		super.afterInit();
		
		_needCrowedGrid=_unit.pos.needCrowedGrid();
		
		if(_needCrowedGrid)
		{
			_gMap=(GridSceneMap)_scenePosLogic.getBaseSceneMap();
		}
		
		_needResetBlockGrid=false;
		
		if(CommonSetting.needResetBlockGrid && CommonSetting.pathFindingType==PathFindingType.JPS && !BaseC.constlist.unit_isMUnit(_data.identity.type))
		{
			_gMap=(GridSceneMap)_scenePosLogic.getBaseSceneMap();
			_needResetBlockGrid=true;
		}
	}
	
	@Override
	public void preRemove()
	{
		toStopMove(false,false);
		
		cancelVehicle();
		
		super.preRemove();
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		_scenePosLogic=null;
		_d=null;
		_pos=null;
		_dir=null;
		
		_specialMoveConfig=null;
		_moveType=MapMoveType.Land;
		_needDrive=false;
		
		_moveTargetPos.clear();
		_walkSpeedRatio=1f;
		
		_vehicle=null;
		_vehicleIndex=-1;
		
		_needCrowedGrid=false;
		_needResetBlockGrid=false;
	}
	
	@Override
	public void onFrame(int delay)
	{
		super.onFrame(delay);
		
		moveFrame(delay);
	}
	
	@Override
	public void onPiece(int delay)
	{
		super.onPiece(delay);
		
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
							if(!toMoveToList(_d.moveType,pos,_targetDisQ))
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
					if(checkCanMoveTargetStopEx())
					{
						stopMoveList(true);
					}
					
					return;
				}
			}
				break;
		}
		
		if(isMoving())
		{
			_scene.trigger.triggerEvent(TriggerEventType.OnUnitMove,_unit);
		}
	}
	
	protected boolean checkCanMoveTargetStopEx()
	{
		return true;
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
	
	/** 当前状态是否可移动 */
	public boolean canMoveNow()
	{
		return !isOnVehicle() && _unit.fight.isSkillCanMove() && _d.baseMoveState!=UnitBaseMoveState.SpecialMove;
	}
	
	/** 当前是否待机中(没有任何技能和移动) */
	public boolean isAllFreeNow()
	{
		return _d.baseMoveState==UnitBaseMoveState.None && !_unit.fight.isSkilling();
	}
	
	/** 正在移动中(任意移动) */
	public boolean isMoving()
	{
		return _d.baseMoveState!=UnitBaseMoveState.None;
	}
	
	/** 获取移动类型 */
	public int getMoveType()
	{
		return _moveType;
	}
	
	protected void moveFrame(float delay)
	{
		if(_d.baseMoveState==UnitBaseMoveState.None)
			return;
		
		//移动
		switch(_d.baseMoveState)
		{
			case UnitBaseMoveState.MoveToDir:
			{
				//服务器直接移动，不判定先
				_tempPos.copyPos(_pos);
				_scenePosLogic.addPosByVector(_tempPos,_baseMoveSpeedVector,delay);
				
				//可走
				if(_scenePosLogic.isPosEnabled(_moveType,_tempPos,!CommonSetting.isClient && !_currentMoveIsInitiative))
				{
					_unit.pos.setPos(_tempPos);
				}
				
				if(_clientMoveDirLastTime>0)
				{
					if((_clientMoveDirLastTime-=delay)<=0)
					{
						_clientMoveDirLastTime=0;
						stopMove();
					}
				}
			}
				break;
			case UnitBaseMoveState.MoveToPos:
			{
				if(_moveLastTimeMillis>0)
				{
					delay+=_moveLastTimeMillis;
					_moveLastTimeMillis=0f;
				}
				
				float sq=calculateDistanceSq(_baseMovePos);
				float spd=_useMoveSpeedMForMove*delay;
				//到达
				if(spd*spd >= sq)
				{
					_unit.pos.setPos(_baseMovePos);
					
					_moveLastTimeMillis=(float)((spd-Math.sqrt(sq))/_useMoveSpeedMForMove);
					
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
		//不在移动中,return
		
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
	
	/** 即将写入 */
	public void beforeWrite()
	{
		switch(_d.baseMoveState)
		{
			case UnitBaseMoveState.MoveToDir:
			{
			
			}
				break;
			case UnitBaseMoveState.MoveToPos:
			{
				if(_moveListDirty)
				{
					_moveListDirty=false;
					
					if(_moveListIndex!=-1)
					{
						SList<PosData> mList;
						if(_d.moveList==null)
						{
							mList=_d.moveList=new SList<>();
						}
						else
						{
							(mList=_d.moveList).clear();
						}
						
						PosData[] values=_moveList.getValues();
						
						for(int i=_moveListIndex,len=_moveList.size();i<len;i++)
						{
							mList.add(values[i]);
						}
					}
					else
					{
						_d.moveList=null;
					}
				}
			}
				break;
			case UnitBaseMoveState.Drive:
			{
				if(_driveAccelerateSpeedM!=0)
				{
					int speed=(int)(_driveCurrentMoveSpeedM*1000f/Global.useMoveSpeedRatio);
					
					if(speed>Global.moveSpeedMax)
					{
						speed=Global.moveSpeedMax;
					}
					
					_d.driveData.currentSpeed=speed;
				}
			}
				break;
		}
	}
	
	//基元移动
	
	/** 基元移动清回 */
	private void clearBaseMove()
	{
		if(_d.baseMoveState==UnitBaseMoveState.None)
		{
			return;
		}
		
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
				if(!CommonSetting.isClient && UnitSpecialMoveTypeConfig.get(_specialMoveType).withVertigo)
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
		
		if(_needCrowedGrid)
		{
			_unit.pos.setGridPos(_pos);
		}
	}
	
	/** 获取当前基元移动状态 */
	public int getBaseMoveState()
	{
		return _d.baseMoveState;
	}
	
	/** 移除移动结束 */
	private void baseMovePosOver()
	{
		//置空
		_d.baseMovePos=null;
		
		//移动组
		if(_moveListIndex!=-1)
		{
			if(_moveListIndex<(_moveList.size()-1))
			{
				_moveListDirty=true;
				toMoveOnePos(_moveList.get(++_moveListIndex));
			}
			else
			{
				stopMoveList(true);
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
		
		if(_unit.canFight())
		{
			_unit.fight.refreshRingLightBuffByMove();
		}
	}
	
	/** 定向移动 */
	private void baseMoveToDir(int type,DirData dir,DirData realDir,float realSpeedRatio,boolean isInitiative)
	{
		clearBaseMove();
		
		_d.baseMoveState=UnitBaseMoveState.MoveToDir;
		_d.moveType=type;
		_currentMoveIsInitiative=isInitiative;
		
		if(!CommonSetting.isClient && !isInitiative)
		{
			_clientMoveDirLastTime=CommonSetting.clientMoveDirTimeMax;
		}
		else
		{
			_clientMoveDirLastTime=0;
		}
		
		toMoveDir(dir,realDir,realSpeedRatio);
		
		//服务器或者主动
		if(!CommonSetting.isClient || isInitiative)
		{
			sendMoveDir(dir,realDir,realSpeedRatio);
		}
	}
	
	/** 定点移动 */
	private void baseMoveToPos(int type,PosData pos,boolean isInitiative)
	{
		clearBaseMove();
		
		_d.baseMoveState=UnitBaseMoveState.MoveToPos;
		_d.moveType=type;
		_currentMoveIsInitiative=isInitiative;
		
		toMoveOnePos(pos);
		
		//服务器或者主动
		if(!CommonSetting.isClient || isInitiative)
		{
			sendMovePos();
		}
	}
	
	private void toMoveOnePos(PosData pos)
	{
		(_d.baseMovePos=_baseMovePos).copyPos(pos);
		
		_scenePosLogic.calculateDirByPos(_baseMoveDir,_pos,pos);
		
		//设置朝向
		_unit.pos.setDir(_baseMoveDir);
		
		if(_needCrowedGrid)
		{
			_unit.pos.setGridPos(pos);
		}
		
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
	
	//protected void
	
	private void clearMoveList()
	{
		if(_moveListIndex==-1)
			return;
		
		_d.moveList=null;
		
		_moveList.clear();
		_moveListIndex=-1;
		_moveListDirty=true;
		_moveTargetPos.clear();
		_targetDisQ=0f;
		_isMoveToUnit=false;
		_moveTargetUnit.clear();
	}
	
	/** 移动点组完成 */
	protected void moveListOver(boolean isSuccess)
	{
		UnitAILogic ai;
		if((ai=_unit.ai)!=null)
			ai.onMoveToOver(isSuccess);
	}
	
	/** 尝试重置当前位置(返回是否开始重置) */
	public boolean tryResetPos(PosData pos,float dis)
	{
		//重置移动
		if(_needCrowedGrid && !_unit.pos.isGridSingle())
		{
			if(dis<=0f)
				return false;
			
			int fx=_unit.pos.getGridX();
			int fz=_unit.pos.getGridZ();
			
			boolean re=_gMap.resetPos(_tempArr,_moveType,true,_unit.pos.getGridX(),_unit.pos.getGridZ(),pos,dis);
			
			if(!re)
				return false;
			
			_gMap.findPathByGrid(_moveList,_moveType,false,_pos.y,fx,fz,_tempArr[0],_tempArr[1]);
			
			//无法到达,视为
			if(_moveList.isEmpty())
			{
				stopMoveList(false);
				return false;
			}
			
			toMoveToListNext();
			
			return true;
		}
		
		return false;
	}
	
	/** 停止移动组 */
	protected void stopMoveList(boolean isSuccess)
	{
		toStopMove(true,true);
		moveListOver(isSuccess);
	}
	
	//
	
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
	
	protected boolean toMoveToList(int unitMoveType,PosData pos,float disQ)
	{
		_targetDisQ=disQ;
		//目标点
		_moveTargetPos.copyPos(pos);
		
		_d.moveType=unitMoveType;
		
		if(disQ>0)
		{
			if(_scenePosLogic.calculatePosDistanceSq2D(_pos,pos)<=disQ)
			{
				stopMoveList(true);
				return false;
			}
		}
		
		_scenePosLogic.findPath(_moveList,_moveType,false,_pos,pos);
		
		//无法到达
		if(_moveList.isEmpty())
		{
			stopMoveList(false);
			return false;
		}
		
		toMoveToListNext();
		
		return true;
	}
	
	private void toMoveToListNext()
	{
		clearBaseMove();
		
		_d.baseMoveState=UnitBaseMoveState.MoveToPos;
		
		_currentMoveIsInitiative=true;
		_moveListIndex=0;
		
		doMoveList();
		sendMoveList();
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
		moveToUnit(UnitMoveType.Run,unit,dis*dis);
	}
	
	/** 寻路移动到单位(主动) */
	public void moveToUnitSq(Unit unit,float disSq)
	{
		//默认跑
		moveToUnit(UnitMoveType.Run,unit,disSq);
	}
	
	/** 寻路移动到单位(主动) */
	public void moveToUnit(int unitMoveType,Unit unit,float disSq)
	{
		if(!canMoveNow())
			return;
		
		if(disSq<0f)
			return;
		
		//完全相同
		if(_isMoveToUnit && _moveTargetUnit.getUnit()==unit && disSq==_targetDisQ)
			return;
		
		PosData pos=unit.pos.getPos();
		
		////相同点
		//if(_d.baseMoveState==UnitBaseMoveState.MoveToPos && _moveTargetPos.isEquals(pos))
		//	return;
		
		_isMoveToUnit=true;
		_moveTargetUnit.setUnit(unit);
		
		toMoveToList(unitMoveType,pos,disSq);
	}
	
	protected void sendMoveDir(DirData dir,DirData realDir,float realSpeedRatio)
	{
	
	}
	
	protected void sendMovePos()
	{
	
	}
	
	protected void sendMoveList()
	{
	
	}
	
	protected void sendDrive()
	{
	
	}
	
	private void doMoveList()
	{
		_moveListDirty=true;
		toMoveOnePos(_moveList.get(_moveListIndex));
	}
	
	/** 特殊移动(主动) */
	public void specialMove(int id,int[] args)
	{
		toSpecialMove(id,args,true,true);
	}
	
	/** 执行特殊移动 */
	private void toSpecialMove(int id,int[] args,boolean isInitiative,boolean isNewOne)
	{
		clearBaseMove();
		
		_d.baseMoveState=UnitBaseMoveState.SpecialMove;
		_currentMoveIsInitiative=isInitiative;
		_specialMoveConfig=SpecialMoveConfig.get(_d.specialMoveID=id);
		_specialMoveType=_specialMoveConfig.type;
		_d.specialMoveArgs=args;
		
		if(!CommonSetting.isClient && UnitSpecialMoveTypeConfig.get(_specialMoveType).withVertigo)
		{
			_unit.fight.getStatusLogic().addStatus(StatusType.Vertigo);
		}
		
		toExecuteSpecialMove(isNewOne);
		
		if(isNewOne)
		{
			if(!CommonSetting.isClient || (!_unit.isDriveAll() && isInitiative))
			{
				sendSpecialMove(id,args,isInitiative);
			}
		}
	}
	
	/** 执行特殊移动 */
	protected void toExecuteSpecialMove(boolean isNewOne)
	{
		switch(_specialMoveType)
		{
			case UnitSpecialMoveType.Blink:
			{
				if(isNewOne && !CommonSetting.isClient)
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
				
				if(isNewOne && !CommonSetting.isClient)
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
						_baseMoveDir.copyDir(_unit.pos.getDir());
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
			default:
			{
				Ctrl.throwError("不支持的特殊移动类型",_specialMoveType);
			}
			break;
		}
	}
	
	/** 特殊移动每桢 */
	protected void specialMoveFrame(float delay)
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
	protected void normalSpecialMoveFrame(float delay,boolean withSetPos)
	{
		//到达时间
		if((_d.specialMoveLastTime-=delay)<=0)
		{
			if(withSetPos)
			{
				_unit.pos.setPos(_baseMovePos);
			}
			
			specialMoveOver();
		}
		else
		{
			float sq=calculateDistanceSq(_baseMovePos);
			
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
	
	/** 检查当前的位置朝向是否合法(不合法就拉回,合法就设置) */
	public boolean checkPosDir(PosDirData posDir)
	{
		if(posDir==null)
			return true;
		
		if(checkPos(posDir.pos))
		{
			_unit.pos.setDir(posDir.dir);
			
			return true;
		}
		
		return false;
	}
	
	/** 检查当前的位置朝向是否合法(不合法就拉回,合法就设置) */
	public boolean checkPos(PosData pos)
	{
		if(pos==null)
			return true;
		
		//只有most需要检查
		if(_scene.driveType!=SceneDriveType.ServerDriveMost)
		{
			_unit.pos.setPos(pos);
			return true;
		}
		
		//不在载具上才检查
		if(!isOnVehicle())
		{
			if(!_scenePosLogic.isPosEnabled(_moveType,pos,true))
			{
				Ctrl.warnLog("客户端点为阻挡",pos);
				return false;
			}
		}
		
		if(_scenePosLogic.calculatePosSum(_pos,pos) >= Global.unitMovePosMaxDeviation)
		{
			Ctrl.warnLog("拉回by距离",_unit.identity.playerID);
			pullBack(InfoCodeType.PullBack_distance);
			return false;
		}
		
		//TODO:补充矢量累计校验
		
		_unit.pos.setPos(pos);
		return true;
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
	public void toStopMove(boolean needReIdle,boolean needSend)
	{
		toStopMove(needReIdle,needSend,true);
	}
	
	/** 停止移动(isAll:停止所有) */
	private void toStopMove(boolean needReIdle,boolean needSend,boolean needSelf)
	{
		if(_d.baseMoveState==UnitBaseMoveState.None)
			return;
		
		clearMoveList();
		clearBaseMove();
		
		if(needSend && _unit.isSelfControl())
		{
			sendStopMove(needSelf);
		}
		
		if(_needResetBlockGrid)
		{
			if(!_gMap.isPosEnabled(_moveType,_pos,false))
			{
				if(_gMap.resetPos(_tempArr,_moveType,false,_unit.pos.getGridX(),_unit.pos.getGridZ(),null,0))
				{
					_gMap.setPosByGrid(_tempPos,_tempArr[0],_tempArr[1]);
					
					_unit.pos.setPosAbs(_tempPos);
				}
			}
		}
		
		if(needReIdle)
		{
			reIdle();
		}
	}
	
	/** 拉回(位置不同步时) */
	public void pullBack(int code)
	{
		_unit.sendInfoCode(code);
		sendPullBack();
	}
	
	/** 停止移动(不处理姿态) */
	public void stopMoveWithOutPose()
	{
		clearBaseMove();
	}
	
	/** 返回站立状态 */
	public void reIdle()
	{
		//TODO:站立状态
	}
	
	/** 死亡时刻 */
	public void onDead()
	{
		cancelVehicle();
	}
	
	//client方法组
	
	/** 客户端移动朝向 */
	public void onClientMoveDir(PosData nowPos,int type,DirData dir,DirData realDir,int realMoveSpeedRatio)
	{
		if(_needDrive)
		{
			_unit.warnLog("驾驶类型不可移动");
			return;
		}
		
		if(!canMoveNow())
		{
			pullBack(InfoCodeType.PullBack_cantMove);
			return;
		}
		
		if(!checkPos(nowPos))
		{
			return;
		}
		
		clearMoveList();
		
		_clientMoveDirLastTime=CommonSetting.clientMoveDirTimeMax;
		baseMoveToDir(type,dir,realDir,realMoveSpeedRatio<0 ? -1f : realMoveSpeedRatio/100f,false);
	}
	
	/** 客户端移动点 */
	public void onClientMovePos(PosData nowPos,int type,PosData targetPos)
	{
		if(_needDrive)
		{
			_unit.warnLog("驾驶类型不可移动");
			return;
		}
		
		if(!canMoveNow())
		{
			pullBack(InfoCodeType.PullBack_cantMove);
			return;
		}
		
		if(!checkPos(nowPos))
		{
			return;
		}
		
		if(!_scenePosLogic.isPosEnabled(_moveType,targetPos,true))
		{
			Ctrl.warnLog("客户端点为阻挡",targetPos);
			return;
		}
		
		clearMoveList();
		baseMoveToPos(type,targetPos,false);
	}
	
	/** 客户端移动点组 */
	public void onClientMovePosList(PosData nowPos,int type,SList<PosData> targets)
	{
		if(_needDrive)
		{
			_unit.warnLog("驾驶类型不可移动");
			return;
		}
		
		if(!canMoveNow())
		{
			pullBack(InfoCodeType.PullBack_cantMove);
			return;
		}
		
		if(!checkPos(nowPos))
		{
			return;
		}
		
		ScenePosLogic scenePosLogic=_scenePosLogic;
		
		PosData[] values=targets.getValues();
		PosData v;
		
		for(int i=0,len=targets.size();i<len;++i)
		{
			v=values[i];
			
			if(!scenePosLogic.isPosEnabled(_moveType,v,true))
			{
				Ctrl.warnLog("客户端点为阻挡",v);
				return;
			}
		}
		
		
		_moveList.clear();
		_moveList.addAll(targets);
		
		clearBaseMove();
		
		_d.baseMoveState=UnitBaseMoveState.MoveToPos;
		_d.moveType=type;
		_currentMoveIsInitiative=false;
		_moveListIndex=0;
		
		doMoveList();
		sendMoveList();
	}
	
	/** 客户端主动特殊移动 */
	public void onClientSpecialMove(int id,int[] args,PosDirData posDir)
	{
		if(!canMoveNow())
		{
			pullBack(InfoCodeType.PullBack_cantMove);
			return;
		}
		
		if(!checkPosDir(posDir))
		{
			return;
		}
		
		toSpecialMove(id,args,false,true);
	}
	
	/** 客户端停止移动 */
	public void onClientStopMove(PosDirData posDir)
	{
		if(!canMoveNow())
		{
			pullBack(InfoCodeType.PullBack_cantMove);
			return;
		}
		
		if(!checkPosDir(posDir))
		{
			return;
		}
		
		toStopMove(true,true,false);
		//结束
	}
	
	//推送部分
	
	/** 推送停止 */
	protected void sendStopMove(boolean needSelf)
	{
	
	}
	
	/** 推送特殊移动 */
	protected void sendSpecialMove(int id,int[] args,boolean needSelf)
	{
	
	}
	
	/** 推送拉回 */
	protected void sendPullBack()
	{
	
	}
	
	/** 攻击类的特殊移动 */
	public void specialMoveTarget(int id,Unit target)
	{
		toSpecialMove(id,getSpecialMoveArgs(id,target),true,true);
	}
	
	/** 获取特殊移动参数 */
	protected int[] getSpecialMoveArgs(int id,Unit target)
	{
		return new int[]{target.instanceID};
	}
	
	//--接收服务器--//
	
	/** 同步指令 */
	public void onServerSyncCommand(PosDirData posDir,int type,int[] ints,float[] floats)
	{
		_unit.pos.setByPosDir(posDir);
	}
	
	/** 服务器拉回*/
	public void onServerPullBack(PosDirData posDir)
	{
		_unit.pos.setByPosDir(posDir);
		toStopMove(true,false);
	}
	
	/** 服务器移动点 */
	public void onServerMovePos(int type,PosData pos)
	{
		clearMoveList();
		baseMoveToPos(type,pos,false);
	}
	
	/** 服务器移动点 */
	public void onServerMoveDir(int type,DirData dir,DirData realDir,int realSpeedRatio)
	{
		clearMoveList();
		baseMoveToDir(type,dir,realDir,realSpeedRatio<0 ? -1f: realSpeedRatio/100f,false);
	}
	
	/** 服务器移动点组 */
	public void onServerMovePosList(int type,SList<PosData> list)
	{
		clearMoveList();
		
		_moveList.addAll(list);
		
		clearBaseMove();
		
		_d.baseMoveState=UnitBaseMoveState.MoveToPos;
		_d.moveType=type;
		_currentMoveIsInitiative=false;
		_moveListIndex=0;
		
		doMoveList();
	}
	
	/** 服务器特殊移动 */
	public void onServerSpecialMove(PosDirData posDir,int id,int[] args,int lastTime,PosData baseMovePos)
	{
		if(_scene.driveType!=SceneDriveType.ServerDriveDamage)
		{
			_unit.pos.setByPosDir(posDir);
		}
		
		clearBaseMove();
		
		_d.specialMoveLastTime=lastTime;
		_d.baseMovePos=baseMovePos;
		
		toSpecialMove(id,args,false,false);//视为不是新的
	}
	
	/** 服务器停止移动 */
	public void onServerStopMove(PosDirData posDir)
	{
		clearMoveList();
		_unit.pos.setByPosDir(posDir);
		toStopMove(true,false);
	}
	
	/** 计算与目标位置距离平方 */
	public float calculateDistanceSq(PosData pos)
	{
		return _scenePosLogic.calculatePosDistanceSq(_pos,pos);
	}
	
	/** 是否在载具上 */
	public boolean isDriving()
	{
		return _d.vehicleInstanceID>0;
	}
	
	/** 骑乘载具 */
	public void getOnVehicle(int instanceID,int index)
	{
		Unit unit=_scene.getUnit(instanceID);
		
		if(unit==null)
		{
			_unit.warnLog("骑乘载具时,找不到单位");
			return;
		}
		
		//不是载具
		if(unit.getType()!=UnitType.Vehicle)
		{
			_unit.warnLog("骑乘载具时,不是载具");
			return;
		}
		
		VehicleIdentityLogic logic=unit.getVehicleIdentityLogic();
		
		if(logic.isFull())
		{
			_unit.warnLog("骑乘载具时，载具已满员");
			return;
		}
		
		VehicleConfig config=logic.getVehicleConfig();
		
		if(index>=config.driverNum)
		{
			_unit.warnLog("骑乘载具时,位置异常");
			return;
		}
		
		if(_scenePosLogic.calculatePosDistanceSq2D(_pos,unit.pos.getPos())>=config.driveOnRadiusT)
		{
			_unit.warnLog("骑乘载具时,距离不够");
			return;
		}
		
		int dInstanceID=logic.getDriverByIndex(index);
		
		if(dInstanceID>0)
		{
			_unit.warnLog("骑乘载具时,位置已有人");
			return;
		}
		
		if(!_scene.role.checkCanDrive(_unit,unit,config))
		{
			_unit.warnLog("骑乘载具时,条件不满足");
			return;
		}
		
		stopMove();
		
		logic.addDrive(index,_unit.instanceID);
		
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
		//立即刷新
		_unit.aoi.refreshAOI();
		
		sendGetOnVehicle(unit.instanceID,index);
		
		//添加影响
		logic.addDriveInfluence(_unit);
		
	}
	
	/** 下载具 */
	public void getOffVehicle(PosData pos)
	{
		if(_vehicle==null)
		{
			_unit.warnLog("下载具时,载具为空");
			return;
		}
		
		doGetOffVehicle(pos,true);
	}
	
	/** 强制取消载具骑乘 */
	public void cancelVehicle()
	{
		if(isOnVehicle())
		{
			doGetOffVehicle(_pos,false);
		}
	}
	
	protected void doGetOffVehicle(PosData pos,boolean needCheck)
	{
		VehicleIdentityLogic logic=_vehicle.getVehicleIdentityLogic();
		
		VehicleConfig config=logic.getVehicleConfig();
		
		if(needCheck && (_scenePosLogic.calculatePosDistanceSq2D(_pos,pos)>=config.driveOnRadiusT))
		{
			_unit.warnLog("下载具时,位置不在范围");
			return;
		}
		
		logic.removeDriveInfluence(_unit);
		
		logic.removeDrive(_vehicleIndex);
		
		_d.vehicleInstanceID=-1;
		_vehicle=null;
		_vehicleIndex=-1;
		
		_unit.pos.setPos(pos);
		sendGetOffVehicle(pos);
	}
	
	/** 是否在载具上 */
	public boolean isOnVehicle()
	{
		return _d.vehicleInstanceID>0;
	}
	
	/** 推送上载具 */
	protected void sendGetOnVehicle(int vehicleInstanceID,int index)
	{
	
	}
	
	/** 推送下载具 */
	protected void sendGetOffVehicle(PosData pos)
	{
	
	}
	
	//drive
	
	/** 客户端发起驾驶 */
	public void onClientDrive(PosDirData nowPos,DriveData data)
	{
		if(!_needDrive)
		{
			_unit.warnLog("非驾驶类型不可驾驶");
			return;
		}
		
		if(!canMoveNow())
		{
			pullBack(InfoCodeType.PullBack_cantMove);
			return;
		}
		
		if(!checkPosDir(nowPos))
		{
			return;
		}
		
		if(getBaseMoveState()!=UnitBaseMoveState.Drive)
		{
			clearBaseMove();
			
			_d.baseMoveState=UnitBaseMoveState.Drive;
			_d.moveType=UnitMoveType.Drive;
			_driveCurrentMoveSpeedM=0;
		}
		
		_currentMoveIsInitiative=false;
		
		_d.driveData=data;
		
		sendDrive();
	}
	
	protected void driveMoveFrame(float delay)
	{
		_tempPos.clear();
		
		DriveData dData=_d.driveData;
		
		float speedAbs=Math.abs(_driveCurrentMoveSpeedM);
		
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
				boolean sameSymbol=MathUtils.sameSymbol(useA,_driveCurrentMoveSpeedM);
				
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
		
		
		
		
		boolean hasDir=false;
		
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
				float forward=(float)(_driveTurnRadius*Math.sin(angle));
				//两侧
				float side=(float)(_driveTurnRadius*(1-Math.cos(angle)));
				
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
		
		if(hasDir)
		{
			_unit.pos.onSetDir();
		}
		
		if(_scenePosLogic.isPosEnabled(_moveType,_tempPos,false))
		{
			_pos.copyPos(_tempPos);
			_unit.pos.onSetPos();
		}
		else
		{
			//撞墙
			//Ctrl.print("撞墙",_tempPos.toDataString());
		}
		
		if(_driveAccelerateSpeedM!=0 && _driveCurrentMoveSpeedM==0f && dData.forward==0 && _currentMoveIsInitiative)
		{
			stopMove();
		}
	}
}
