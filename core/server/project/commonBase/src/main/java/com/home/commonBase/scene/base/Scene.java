package com.home.commonBase.scene.base;

import com.home.commonBase.config.game.SceneConfig;
import com.home.commonBase.config.game.SceneMapConfig;
import com.home.commonBase.config.other.ScenePlaceConfig;
import com.home.commonBase.constlist.generate.SceneInstanceType;
import com.home.commonBase.constlist.generate.TriggerEventType;
import com.home.commonBase.constlist.system.SceneDriveType;
import com.home.commonBase.constlist.system.WorkType;
import com.home.commonBase.control.LogicExecutorBase;
import com.home.commonBase.data.scene.base.PosData;
import com.home.commonBase.data.scene.base.RegionData;
import com.home.commonBase.data.scene.scene.CreateSceneData;
import com.home.commonBase.data.scene.unit.UnitData;
import com.home.commonBase.data.scene.unit.identity.CharacterIdentityData;
import com.home.commonBase.data.system.PlayerWorkData;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.logic.LogicEntity;
import com.home.commonBase.logic.unit.UnitFightDataLogic;
import com.home.commonBase.scene.scene.SceneAOILogic;
import com.home.commonBase.scene.scene.SceneFightLogic;
import com.home.commonBase.scene.scene.SceneInOutLogic;
import com.home.commonBase.scene.scene.ScenePlayLogic;
import com.home.commonBase.scene.scene.ScenePosLogic;
import com.home.commonBase.scene.scene.SceneRoleLogic;
import com.home.commonBase.scene.scene.SceneTriggerLogic;
import com.home.commonBase.scene.scene.SceneUnitFactoryLogic;
import com.home.shine.control.ThreadControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.IndexMaker;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.LongObjectMap;
import com.home.shine.support.collection.SList;

/** 场景基类(主mmo) */
public class Scene extends LogicEntity implements ILogic
{
	/** 场景实例ID */
	public int instanceID=-1;
	
	/** 场景驱动类型 */
	public int driveType=CommonSetting.sceneDriveType;
	
	/** 执行器 */
	protected LogicExecutorBase _executor;
	
	/** 场景类型 */
	protected int _type;
	
	/** 场景配置 */
	private SceneConfig _config;
	/** 地图配置 */
	private SceneMapConfig _mapConfig;
	/** 场景布置配置 */
	private ScenePlaceConfig _placeConfig;
	/** 创建场景数据 */
	protected CreateSceneData _createData;
	
	/** 线ID(默认-1) */
	protected int _lineID=-1;
	
	/** 逻辑体组 */
	private SList<SceneLogicBase> _logics=new SList<>(SceneLogicBase[]::new);
	
	/** 是否初始化过 */
	protected boolean _inited=false;
	/** 是否暂停 */
	private boolean _pause=false;
	/** 是否处于帧同步状态 */
	private boolean _isFrameSync=false;
	
	/** 每秒计时计数 */
	private int _secondTimeTick=0;
	
	/** 固定系统时间 */
	private long _fixedTimer;
	
	private boolean _logicForEaching=false;
	
	/** 单位组(遍历删除问题依靠标记和自定义Map解决) */
	private IntObjectMap<Unit> _units=new IntObjectMap<>(Unit[]::new);
	/** 需要tick单位组 */
	private IntObjectMap<Unit> _tickUnits=new IntObjectMap<>(Unit[]::new);
	/** 战斗单位组 */
	private IntObjectMap<Unit> _fightUnits=new IntObjectMap<>(Unit[]::new);
	/** 角色字典组(playerID为key)(限定了单一Character) */
	private LongObjectMap<Unit> _characters=new LongObjectMap<>(Unit[]::new);
	/** 角色字典组(index为key)(帧同步用) */
	private LongObjectMap<Unit> _charactersByIndex=new LongObjectMap<>(Unit[]::new);
	/** 区域组 */
	private IntObjectMap<Region> _reginos=new IntObjectMap<>(Region[]::new);
	/** 流水ID构造器 */
	private IndexMaker _unitInstanceIDMaker=new IndexMaker(CommonSetting.sceneEditorIndexMax,ShineSetting.indexMaxHalf,true);
	/** 流水ID构造器 */
	private IndexMaker _playerIndexIDMaker=new IndexMaker(0,ShineSetting.indexMax,true);
	
	//logics
	
	/** 单位工厂逻辑 */
	public SceneUnitFactoryLogic unitFactory;
	/** 进出逻辑 */
	public SceneInOutLogic inout;
	/** 场景角色逻辑 */
	public SceneRoleLogic role;
	/** aoi逻辑 */
	public SceneAOILogic aoi;
	/** 位置逻辑 */
	public ScenePosLogic pos;
	/** 战斗逻辑 */
	public SceneFightLogic fight;
	/** 场景玩法逻辑 */
	public ScenePlayLogic play;
	/** 场景触发器逻辑 */
	public SceneTriggerLogic trigger;
	
	//策略参数
	
	private boolean _isDriveAll;
	private boolean _isServerDriveAttackHappen;
	
	//地图相关
	/** 原点 */
	public PosData originPos=new PosData();
	/** 尺寸 */
	public PosData sizePos=new PosData();
	/** 终点 */
	public PosData endPos=new PosData();
	
	public Scene()
	{
	
	}
	
	/** 设置类型 */
	public void setType(int type)
	{
		_type=type;
	}
	
	/** 场景类型 */
	public int getType()
	{
		return _type;
	}
	
	/** 设置执行器 */
	public void setExecutor(LogicExecutorBase executor)
	{
		_executor=executor;
	}
	
	/** 获取执行器 */
	public LogicExecutorBase getExecutor()
	{
		return _executor;
	}
	
	/** 初始化场景创建数据 */
	public void initCreate(CreateSceneData data)
	{
		_createData=data;
		initSceneID(data.sceneID);
	}
	
	/** 初始化场景ID */
	public void initSceneID(int sceneID)
	{
		_config=SceneConfig.get(sceneID);
		_mapConfig=SceneMapConfig.get(_config.mapID);
		_placeConfig=ScenePlaceConfig.get(sceneID);
		
		//绑定驱动类型
		if(_config.instanceType==SceneInstanceType.ClientDriveSinglePlayerBattle)
		{
			driveType=SceneDriveType.ClientDriveAll;
		}
		else
		{
			driveType=CommonSetting.sceneDriveType;
		}
		
		originPos.setByIArr(_mapConfig.origin);
		sizePos.setByIArr(_mapConfig.size);
		endPos.x=originPos.x+sizePos.x;
		endPos.y=originPos.y+sizePos.y;
		endPos.z=originPos.z+sizePos.z;
		
		play.onSetConfig();
	}
	
	/** 获取场景配置 */
	public SceneConfig getConfig()
	{
		return _config;
	}
	
	/** 地图配置 */
	public SceneMapConfig getMapConfig()
	{
		return _mapConfig;
	}
	
	/** 获取创建场景数据 */
	public CreateSceneData getCreateData()
	{
		return _createData;
	}
	
	/** 获取场景ID */
	public int getSceneID()
	{
		return _config.id;
	}
	
	public void setLineID(int value)
	{
		_lineID=value;
	}
	
	/** 获取线ID */
	public int getLineID()
	{
		return _lineID;
	}
	
	/** 获取布置配置 */
	public ScenePlaceConfig getPlaceConfig()
	{
		return _placeConfig;
	}
	
	/** 是否简版场景 */
	public boolean isSimple()
	{
		return BaseC.constlist.sceneInstance_isSimple(_config.instanceType);
	}
	
	/** 是否限定场景 */
	public boolean isFinite()
	{
		return BaseC.constlist.sceneInstance_isFinite(_config.instanceType);
	}
	
	/** 是否服务器驱动攻击发生 */
	public boolean isServerDriveAttackHappen()
	{
		return _isServerDriveAttackHappen;
	}
	
	/** 是否驱动主要 */
	public boolean isDriveAll()
	{
		return _isDriveAll;
	}
	
	public void reloadConfig()
	{
		int id=_config.id;
		
		_config=SceneConfig.get(id);
		_placeConfig=ScenePlaceConfig.get(id);
		
		_units.forEachValue(v->
		{
			v.onReloadConfig();
		});
	}
	
	/** 添加logic */
	public void addLogic(SceneLogicBase logic)
	{
		if(logic==null)
		{
			throwError("logic不能为空");
		}
		
		if(ShineSetting.openCheck)
		{
			if(_logicForEaching)
			{
				throwError("遍历中操作集合");
			}
		}
		
		_logics.add(logic);
		logic.setScene(this);
		
		if(_inited)
		{
			logic.init();
		}
	}
	
	/** 移除logic */
	public void removeLogic(SceneLogicBase logic)
	{
		if(ShineSetting.openCheck)
		{
			if(_logicForEaching)
			{
				throwError("遍历中操作集合");
			}
		}
		
		int index=_logics.indexOf(logic);
		
		if(index!=-1)
		{
			_logics.remove(index);
		}
	}
	
	/** 初始化场景驱动方式 */
	protected void initDriveType()
	{
		driveType=CommonSetting.sceneDriveType;
		_isDriveAll=!CommonSetting.isClient || driveType==SceneDriveType.ClientDriveAll;
		_isServerDriveAttackHappen=SceneDriveType.isServerDriveAttackHappen(driveType);
	}
	
	/** 注册逻辑体 */
	protected void registLogics()
	{
		addLogic(unitFactory=createUnitFactoryLogic());
		
		addLogic(inout=createInOutLogic());
		
		addLogic(role=createRoleLogic());
		
		addLogic(aoi=createAOILogic());
		
		addLogic(pos=createPosLogic());
		
		if((fight=createFightLogic())!=null)
			addLogic(fight);
		
		addLogic(trigger=createTriggerLogic());
		//play最后
		addLogic(play=createPlayLogic());
	}
	
	/** 构造 */
	@Override
	public void construct()
	{
		initDriveType();
		
		registLogics();
		
		SceneLogicBase[] values=_logics.getValues();
		
		for(int i=0, len=_logics.size();i<len;++i)
		{
			values[i].construct();
		}
	}
	
	/** 初始化 */
	@Override
	public void init()
	{
		_inited=true;
		
		_fixedTimer=_executor.getFixedTimer();
		
		preInit();
		
		SceneLogicBase[] values=_logics.getValues();
		SceneLogicBase logic;
		
		for(int i=0, len=_logics.size();i<len;++i)
		{
			if((logic=values[i]).enabled)
			{
				logic.init();
			}
		}
		
		for(int i=0, len=_logics.size();i<len;++i)
		{
			if((logic=values[i]).enabled)
			{
   				logic.afterInit();
			}
		}
		
		start();
	}
	
	/** 预备初始化 */
	protected void preInit()
	{
		boolean simple=isSimple();
		
		pos.enabled=!simple;
		
		if(fight!=null)
			fight.enabled=!simple;
	}
	
	/** 场景开始 */
	protected void start()
	{
		play.onSceneStart();
		trigger.triggerEvent(TriggerEventType.OnSceneStart);
	}
	
	public void preDispose()
	{
		_inited=false;
		dispose();
	}
	
	/** 析构 */
	@Override
	public void dispose()
	{
		instanceID=-1;
		
		_units.forEachValueS(v->
		{
			//立即删除
			v.removeAbs();
		});
		
		_reginos.forEachValueS(v->
		{
			removeRegion(v.instanceID);
		});
		
		_logics.forEach(logic->
		{
			if(logic.enabled)
			{
				logic.dispose();
			}
			else
			{
				logic.enabled=true;
			}
		});
		
		_secondTimeTick=0;
		_unitInstanceIDMaker.reset();
	}
	
	/** 是否暂停 */
	public boolean isPause()
	{
		return _pause;
	}
	
	/** 暂停/取消 */
	public void pause(boolean value)
	{
		_pause=value;
	}
	
	/** 是否是帧同步 */
	public boolean isFrameSync()
	{
		return _isFrameSync;
	}
	
	/** 设置是否处于帧同步 */
	public void setFrameSync(boolean value)
	{
		_isFrameSync=value;
	}
	
	/** 刷帧 */
	//关注点 场景类中的tick处理
	@Override
	public void onFrame(int delay)
	{
		_fixedTimer=_executor.getFixedTimer();
		
		_secondTimeTick+=delay;
		
		if(_secondTimeTick>1000)
		{
			int d=_secondTimeTick;
			_secondTimeTick=0;//归零
			
			onSecondNoMatterPause();
			
			if(!_pause)
			{
				onSecond(d);
			}
		}
		
		//暂停不走帧
		if(_pause)
		{
			return;
		}
		
		if(_isFrameSync)
		{
			play.onFrame(delay);
		}
		else
		{
			SceneLogicBase[] values=_logics.getValues();
			SceneLogicBase logic;
			
			for(int i=0, len=_logics.size();i<len;++i)
			{
				try
				{
					if((logic=values[i]).enabled)
					{
						logic.onFrame(delay);
					}
				}
				catch(Exception e)
				{
					errorLog(e);
				}
			}
			
			tickUnits(delay);
		}
	}
	
	private void tickUnits(int delay)
	{
		IntObjectMap<Unit> fDic;
		if(!(fDic=_tickUnits).isEmpty())
		{
			Unit[] values=fDic.getValues();
			int safeIndex=fDic.getLastFreeIndex();
			Unit v;
			
			for(int i=safeIndex-1;i!=safeIndex;--i)
			{
				if(i<0)
				{
					i=values.length;
				}
				else if((v=values[i])!=null)
				{
					try
					{
						v.onFrame(delay);
					}
					catch(Exception e)
					{
						errorLog(e);
					}
					
					if(v!=values[i])
					{
						++i;
					}
				}
			}
		}
		
	}
	
	/** 每秒 */
	private void onSecond(int delay)
	{
		SceneLogicBase[] values=_logics.getValues();
		SceneLogicBase logic;
		
		for(int i=0, len=_logics.size();i<len;++i)
		{
			try
			{
				if((logic=values[i]).enabled)
				{
					logic.onSecond(delay);
				}
			}
			catch(Exception e)
			{
				errorLog(e);
			}
		}
		
		Unit[] values1;
		Unit v1;
		
		for(int i1=(values1=_tickUnits.getValues()).length-1;i1>=0;--i1)
		{
			if((v1=values1[i1])!=null)
			{
				v1.onSecond(delay);
			}
		}
	}
	
	/** 每秒无视暂停 */
	private void onSecondNoMatterPause()
	{
		SceneLogicBase[] values=_logics.getValues();
		SceneLogicBase logic;
		for(int i=0, len=_logics.size();i<len;++i)
		{
			try
			{
				if((logic=values[i]).enabled)
				{
					logic.onSecondNoMatterPause();
				}
			}
			catch(Exception e)
			{
				errorLog(e);
			}
		}
	}
	
	//logics
	
	/** 创建单位工厂逻辑 */
	protected SceneUnitFactoryLogic createUnitFactoryLogic()
	{
		return new SceneUnitFactoryLogic();
	}
	
	/** 创建进出逻辑 */
	protected SceneInOutLogic createInOutLogic()
	{
		return new SceneInOutLogic();
	}
	
	/** 创建角色逻辑 */
	protected SceneRoleLogic createRoleLogic()
	{
		return new SceneRoleLogic();
	}
	
	/** 创建aoi逻辑 */
	protected SceneAOILogic createAOILogic()
	{
		return null;
	}
	
	/** 创建位置逻辑 */
	protected ScenePosLogic createPosLogic()
	{
		return new ScenePosLogic();
	}
	
	/** 创建战斗逻辑 */
	protected SceneFightLogic createFightLogic()
	{
		return new SceneFightLogic();
	}
	
	/** 创建玩法逻辑 */
	protected ScenePlayLogic createPlayLogic()
	{
		return new ScenePlayLogic();
	}
	
	/** 创建触发器逻辑 */
	protected SceneTriggerLogic createTriggerLogic()
	{
		return new SceneTriggerLogic();
	}
	
	//methods
	
	/** 固定系统时间 */
	public long getFixedTimer()
	{
		return _fixedTimer;
	}
	
	/** 获取时间戳 */
	public long getTimeMillis()
	{
		return _executor.getTimeMillis();
	}
	
	/** 取一个单位实例ID */
	public int getUnitInstanceID()
	{
		int re;
		
		while(_units.contains(re=_unitInstanceIDMaker.get()));
		
		return re;
	}
	
	/** 获取单位 */
	public Unit getUnit(int instanceID)
	{
		return _units.get(instanceID);
	}
	
	/** 获取战斗单位 */
	public Unit getFightUnit(int instanceID)
	{
		return _fightUnits.get(instanceID);
	}
	
	/** 获取战斗单位组 */
	public IntObjectMap<Unit> getFightUnitDic()
	{
		return _fightUnits;
	}
	
	/** 获取单位组 */
	public IntObjectMap<Unit> getUnitDic()
	{
		return _units;
	}
	
	/** 获取角色组(playerID为key) */
	public LongObjectMap<Unit> getCharacterDic()
	{
		return _characters;
	}
	
	/** 通过plyaerID获取角色 */
	public Unit getCharacterByPlayerID(long playerID)
	{
		return _characters.get(playerID);
	}
	
	/** 通过序号获取角色 */
	public Unit getCharacterByIndex(int index)
	{
		return _charactersByIndex.get(index);
	}
	
	/** 添加单位 */
	public Unit addUnit(UnitData data)
	{
		Unit unit=toAddUnit(data);
		
		if(unit!=null)
		{
			toActiveUnit(unit,true);
		}
		
		return unit;
	}
	
	/** 添加单位第一部分 */
	public Unit toAddUnit(UnitData data)
	{
		if(data.instanceID<=0)
		{
			throwError("单位流水ID未赋值",data.identity.type);
			return null;
		}
		
		if(ShineSetting.openCheck)
		{
			if(_units.contains(data.instanceID))
			{
				throwError("单位已存在",data.instanceID,data.identity.type);
				return null;
			}
		}
		
		//是否可战斗
		boolean canFight=BaseC.constlist.unit_canFight(data.identity.type) && !isSimple();
		
		if(canFight)
		{
			//没绑数据逻辑
			if(data.fightDataLogic==null)
			{
				if(!CommonSetting.isClient)
				{
					throwError("不能没有战斗数据逻辑");
					return null;
				}
				else
				{
					//没有就创建
					UnitFightDataLogic dataLogic=getExecutor().createUnitFightDataLogic();
					dataLogic.setData(data.fight,data.avatar);
					data.fightDataLogic=dataLogic;
				}
			}
		}
		
		Unit unit=toCreateUnitByData(data);
		
		//双绑
		unit.setUnitData(data);
		unit.setScene(this);
		
		if(canFight)
		{
			data.fightDataLogic.setUnit(unit);
		}
		
		_units.put(data.instanceID,unit);
		
		//战斗单位
		if(canFight)
		{
			_fightUnits.put(data.instanceID,unit);
		}
		
		if(unit.needTick())
		{
			_tickUnits.put(data.instanceID,unit);
		}
		
		//是角色
		if(data.identity instanceof CharacterIdentityData)
		{
			CharacterIdentityData iData=(CharacterIdentityData)data.identity;
			
			if(_config.instanceType==SceneInstanceType.FiniteBattleWithFrameSync)
			{
				iData.syncIndex=_playerIndexIDMaker.get();
				_charactersByIndex.put(_playerIndexIDMaker.get(),unit);
			}
			
			_characters.put(iData.playerID,unit);
		}
		
		return unit;
	}
	
	/** 激活单位(initDebug 与 aoi) */
	public void toActiveUnit(Unit unit,boolean needSendSelf)
	{
		unit.enabled=true;
		
		unit.init();
		
		aoi.unitAdd(unit,needSendSelf);
		
		unit.afterAOIAdd();
		
		try
		{
			onAddUnit(unit);
		}
		catch(Exception e)
		{
			errorLog("onAddUnit出错",e);
		}
	}
	
	/** 删除单位 */
	public final void removeUnit(int instanceID)
	{
		Unit unit=_units.get(instanceID);
		
		if(unit==null)
		{
			if(ShineSetting.openCheck)
			{
				throwError("单位不存在",instanceID);
			}
			
			return;
		}
		
		if(ShineSetting.openCheck)
		{
			if(ThreadControl.getCurrentShineThread()!=ThreadControl.getPoolThread(_executor.getIndex()))
			{
				throwError("移除单位时,线程不正确",Ctrl.getStackTrace());
			}
		}
		
		if(unit.isCharacter() && !CommonSetting.isClient)
		{
			throwError("不能直接移除角色");
		}
		
		toRemoveUnit(unit,true);
	}
	
	/** 删除单位 */
	public void toRemoveUnit(Unit unit,boolean needSendSelf)
	{
		int instanceID=unit.instanceID;
		
		try
		{
			//场景预删除
			onRemoveUnit(unit);
		}
		catch(Exception e)
		{
			errorLog("onRemoveUnit出错",e);
		}
		
		try
		{
			//预移除
			unit.preRemove();
		}
		catch(Exception e)
		{
			errorLog("unit.preRemove出错",e);
		}
		
		//aoi移除
		aoi.unitRemove(unit,needSendSelf);
		//先标记
		unit.enabled=false;
		
		boolean canFight=unit.canFight();
		//析构
		unit.dispose();
		//从字典移除
		_units.remove(instanceID);
		
		if(canFight)
		{
			_fightUnits.remove(instanceID);
		}
		
		if(unit.needTick())
		{
			_tickUnits.remove(instanceID);
		}
		
		UnitData data=unit.getUnitData();
		
		if(unit.isCharacter())
		{
			CharacterIdentityData iData=(CharacterIdentityData)data.identity;
			
			if(_config.instanceType==SceneInstanceType.FiniteBattleWithFrameSync)
			{
				_charactersByIndex.remove(iData.syncIndex);
			}
			
			_characters.remove(iData.playerID);
		}
		
		unit.setScene(null);
		
		//双解绑
		if(canFight)
		{
			data.fightDataLogic.setUnit(null);
		}
		
		unit.setUnitData(null);
		
		boolean unitDataNeedRelease=isUnitDataNeedRelease(unit);
		boolean needReleaseFightDataLogic=isNeedReleaseFightDataLogic(unit);
		
		if(canFight && (unitDataNeedRelease || needReleaseFightDataLogic))
		{
			//清空数据
			data.fightDataLogic.clear();
		}
		
		if(canFight && needReleaseFightDataLogic)
		{
			//析构数据逻辑
			data.fightDataLogic.setData(null,null);
			_executor.releaseUnitFightDataLogic(data.fightDataLogic);
			data.fightDataLogic=null;
		}
		
		//回收单位数据
		if(unitDataNeedRelease)
		{
			//归零
			data.instanceID=0;
			//回池
			_executor.releaseUnitData(data);
		}
		
		//回收单位
		if(isUnitNeedRelease(unit))
		{
			_executor.releaseUnit(unit);
		}
	}
	
	
	protected Unit toCreateUnitByData(UnitData data)
	{
		return _executor.createUnit(data.identity.type);
	}
	
	/** 单位数据是否需要析构 */
	protected boolean isUnitDataNeedRelease(Unit unit)
	{
		return !unit.isMUnit();
	}
	
	/** 限定的角色逻辑 */
	protected boolean isNeedReleaseFightDataLogic(Unit unit)
	{
		return unit.isMUnit() && isFinite();
	}
	
	/** 单位是否需要析构 */
	protected boolean isUnitNeedRelease(Unit unit)
	{
		return true;
	}
	
	/** 添加区域 */
	public Region addRegion(RegionData data)
	{
		if(data.instanceID<=0)
		{
			throwError("区域流水ID未赋值",data.id);
			return null;
		}
		
		if(ShineSetting.openCheck)
		{
			if(_reginos.contains(data.instanceID))
			{
				throwError("区域已存在",data.instanceID,data.id);
				return null;
			}
		}
		
		Region region=getExecutor().regionPool.getOne();
		
		region.setData(data);
		region.setScene(this);
		
		_reginos.put(data.instanceID,region);
		
		region.init();
		
		return region;
	}
	
	/** 移除区域 */
	public void removeRegion(int instanceID)
	{
		Region region=_reginos.get(instanceID);
		
		if(region==null)
		{
			if(ShineSetting.openCheck)
			{
				throwError("区域不存在",instanceID);
			}
			
			return;
		}
		
		region.dispose();
		
		_reginos.remove(instanceID);
		
		region.setData(null);
		
		getExecutor().regionPool.back(region);
	}
	
	/** 获取region */
	public Region getRegion(int instanceID)
	{
		return _reginos.get(instanceID);
	}
	
	/** 获取全部区域组 */
	public IntObjectMap<Region> getRegions()
	{
		return _reginos;
	}
	
	//行为组
	
	/** 添加单位 */
	protected void onAddUnit(Unit unit)
	{
		SceneLogicBase[] values=_logics.getValues();
		
		for(int i=0, len=_logics.size();i<len;++i)
		{
			values[i].onAddUnit(unit);
		}
	}
	
	/** 删除单位 */
	protected void onRemoveUnit(Unit unit)
	{
		SceneLogicBase[] values=_logics.getValues();
		
		for(int i=0, len=_logics.size();i<len;++i)
		{
			values[i].onRemoveUnit(unit);
		}
	}
	
	/** 添加角色离线事务数据(池线程) */
	public void addPlayerOfflineWork(long playerID,PlayerWorkData data)
	{
		addPlayerWork(WorkType.PlayerOffline,playerID,data);
	}
	
	/** 添加一个角色的在线事务数据(池线程)(只对在线角色有效,如目标离线则收不到) */
	public void addPlayerOnlineWork(long playerID,PlayerWorkData data)
	{
		addPlayerWork(WorkType.PlayerOnline,playerID,data);
	}
	
	/** 添加一个角色的立即事务数据(池线程) */
	public void addPlayerAbsWork(long playerID,PlayerWorkData data)
	{
		addPlayerWork(WorkType.PlayerAbs,playerID,data);
	}
	
	/** 添加角色事务 */
	public void addPlayerWork(int type,long playerID,PlayerWorkData data)
	{
		getExecutor().addPlayerWork(type,playerID,data);
	}
	
	@Override
	public void writeInfo(StringBuilder sb)
	{
		sb.append("sceneID:");
		sb.append(_config.id);
		sb.append(" instanceID:");
		sb.append(instanceID);
	}
	
	@Override
	protected void sendWarnLog(String str)
	{
	
	}
}
