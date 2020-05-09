using System;
using ShineEngine;

/// <summary>
/// 场景基类
/// </summary>
[Hotfix]
public class Scene:ILogic,ITimeEntity
{
	/** 场景实例ID */
	public int instanceID=-1;
	/** 驱动类型 */
	public int driveType=CommonSetting.sceneDriveType;

	/** 场景类型 */
	protected int _type;

	/** 场景配置 */
	private SceneConfig _config;
	/** 场景地图配置 */
	private SceneMapConfig _mapConfig;
	/** 布置配置 */
	private ScenePlaceConfig _placeConfig;
	/** 预备信息 */
	private ScenePreInfoData _preInfo;
	/** 地图信息配置 */
	private MapInfoConfig _mapInfoConfig;

	private long _fixedTimer;

	/** 逻辑体组 */
	private SList<SceneLogicBase> _logics=new SList<SceneLogicBase>();

	/** 是否已初始化 */
	protected bool _inited=false;
	/** 是否预备删除 */
	protected bool _preRemoved=false;
	/** 是否暂停 */
	private bool _pause=false;
	/** 是否处于帧同步当中 */
	private bool _isFrameSync=false;

	/** 每秒计时计数 */
	private int _secondTimeTick=0;

	/** 单位组 */
	private IntObjectMap<Unit> _units=new IntObjectMap<Unit>();
	/** 战斗单位组 */
	private IntObjectMap<Unit> _fightUnits=new IntObjectMap<Unit>();
	/** 角色字典组(playerID为key) */
	private LongObjectMap<Unit> _characters=new LongObjectMap<Unit>();
	/** 角色字典组(index为key) */
	private LongObjectMap<Unit> _charactersByIndex=new LongObjectMap<Unit>();
	/** 流水ID构造器(客户端单位用) */
	private IndexMaker _unitInstanceIDMaker=new IndexMaker(ShineSetting.indexMaxHalf,ShineSetting.indexMax,true);

	/** 视野绑定单位数据组 */
	private IntObjectMap<UnitSimpleData> _bindVisionUnits=new IntObjectMap<UnitSimpleData>();

	/** 主角 */
	protected Unit _hero;

	/** 自己角色 */
	protected Role _selfRole;

	//logics

	/** 单位工厂逻辑 */
	public SceneUnitFactoryLogic unitFactory;
	/** 进出逻辑 */
	public SceneInOutLogic inout;
	/** 角色逻辑 */
	public SceneRoleLogic role;
	/** 位置逻辑 */
	public ScenePosLogic pos;

	/** 场景显示逻辑 */
	public SceneShowLogic show;

	/** 场景加载逻辑 */
	public SceneLoadLogic load;

	/** 场景战斗逻辑 */
	public SceneFightLogic fight;

	/** 摄像机逻辑 */
	public SceneCameraLogic camera;

	/** 玩法逻辑 */
	public ScenePlayLogic play;

	//地图部分

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

	/** 是否客户端驱动主要 */
	public bool isDriveAll()
	{
		return driveType==SceneDriveType.ClientDriveAll;
	}

	/** 设置配置 */
	public virtual void initSceneID(int id)
	{
		_config=SceneConfig.get(id);
		_mapConfig=SceneMapConfig.get(_config.mapID);
		_placeConfig=ScenePlaceConfig.getSync(id);

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

	/** 预备信息 */
	public void setPreInfo(ScenePreInfoData preInfo)
	{
		_preInfo=preInfo;
	}

	/** 获取预备信息 */
	public ScenePreInfoData getPreInfo()
	{
		return _preInfo;
	}

	/** 场景配置 */
	public SceneConfig getConfig()
	{
		return _config;
	}

	/** 场景地图配置 */
	public SceneMapConfig getMapConfig()
	{
		return _mapConfig;
	}

	/** 布置配置 */
	public ScenePlaceConfig getPlaceConfig()
	{
		return _placeConfig;
	}

	public MapInfoConfig getMapInfoConfig()
	{
		return _mapInfoConfig;
	}

	/** 是否简版场景 */
	public bool isSimple()
	{
		return BaseC.constlist.sceneInstance_isSimple(_config.instanceType);
	}

	/** 是否限定场景 */
	public bool isFinite()
	{
		return BaseC.constlist.sceneInstance_isFinite(_config.instanceType);
	}

	/** 重新加载配置 */
	public void onReloadConfig()
	{
		int id=_config.id;

		_config=SceneConfig.get(id);
		_placeConfig=ScenePlaceConfig.get(id);

		Unit[] values;
		Unit v;

		for(int i=(values=_units.getValues()).Length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				v.onReloadConfig();
			}
		}
	}

	/** 是否已经初始化过 */
	public bool isInit
	{
		get {return _inited;}
	}

	/** 是否预备删除 */
	public bool isPreRemove
	{
		get {return _preRemoved;}
	}

	public Unit hero
	{
		get {return _hero;}
	}

	public virtual Role selfRole
	{
		get {return _selfRole;}
	}

	public void setSelfRole(Role value)
	{
		_selfRole=value;
	}

	/** 添加logic */
	public void addLogic(SceneLogicBase logic)
	{
		logic.setScene(this);
		_logics.add(logic);
	}

	/** 移除logic */
	public void removeLogic(SceneLogicBase logic)
	{
		_logics.removeObj(logic);
	}

	/** 注册逻辑体 */
	protected virtual void registLogics()
	{
		if((unitFactory=createUnitFactoryLogic())!=null)
			addLogic(unitFactory);

		if((inout=createInOutLogic())!=null)
			addLogic(inout);

		if((role=createRoleLogic())!=null)
			addLogic(role);

		if((pos=createPosLogic())!=null)
			addLogic(pos);

		if((show=createShowLogic())!=null)
			addLogic(show);

		//必须存在
		addLogic(load=createLoadLogic());

		if((fight=createFightLogic())!=null)
			addLogic(fight);

		//必须存在
		addLogic(camera=createCameraLogic());

		//添加play逻辑
		if((play=createPlayLogic())!=null)
			addLogic(play);
		else
			Ctrl.throwError("不能没有play");
	}

	/** 构造 */
	public void construct()
	{
		registLogics();

		for(int i=0,len=_logics.Count;i<len;++i)
		{
			_logics[i].construct();
		}
	}

	/** 场景资源刚加载好 */
	public void onSceneLoad()
	{
		preInit();

		if(CommonSetting.clientMapNeedGrid)
		{
			_mapInfoConfig=MapInfoConfig.getSync(_config.mapID);
		}

		SList<SceneLogicBase> logics=_logics;
		SceneLogicBase logic;

		for(int i=0,len=logics.Count;i<len;++i)
		{
			if((logic=logics[i]).enabled)
			{
				logic.onSceneLoad();
			}
		}
	}

	/** 初始化 */
	public void init()
	{
		_fixedTimer=Ctrl.getFixedTimer();

		_inited=true;

		SList<SceneLogicBase> logics=_logics;
		SceneLogicBase logic;

		for(int i=0,len=logics.Count;i<len;++i)
		{
			if((logic=logics[i]).enabled)
			{
				logic.init();
			}
		}

		// for(int i=0,len=logics.Count;i<len;++i)
		// {
		// 	if((logic=logics[i]).enabled)
		// 	{
		// 		logic.afterInit();
		// 	}
		// }
	}

	/** 预备初始化 */
	protected virtual void preInit()
	{
		bool simple=isSimple();

		if(pos!=null)
			pos.enabled=!simple;

		if(fight!=null)
			fight.enabled=!simple;

		if(show!=null)
			show.enabled=!simple;

		if(camera!=null)
			camera.enabled=!simple;
	}

	/** 析构 */
	public void dispose()
	{
		//移除所有单位
		foreach(Unit v in _units)
		{
			v.removeAbs();
		}

		SList<SceneLogicBase> logics=_logics;
		SceneLogicBase logic;

		for(int i=0,len=logics.Count;i<len;++i)
		{
			if((logic=logics[i]).enabled)
			{
				logic.dispose();
			}
			else
			{
				logic.enabled=true;
			}
		}

		_inited=false;
		_hero=null;
	}


	/** 是否暂停 */
	public bool isPause()
	{
		return _pause;
	}

	/** 暂停/取消 */
	public void pause(bool value)
	{
		_pause=value;
	}

	/** 当前是否在帧同步中 */
	public bool isInFrameSync()
	{
		return _isFrameSync;
	}

	/** 设置帧同步状态 */
	public void setFrameSync(bool value)
	{
		_isFrameSync=value;
	}

	/** 刷帧 */
	public void onFrame(int delay)
	{
		//暂停不走帧
		if(_pause)
			return;

		if(_isFrameSync)
		{
			((BattleSceneSyncPlayLogic)play).preFrame(delay);
		}
		else
		{
			doFrame(delay);
		}
	}

	/** 执行帧 */
	public void doFrame(int delay)
	{
		_fixedTimer=Ctrl.getFixedTimer();

		_secondTimeTick+=delay;

		if(_secondTimeTick>1000)
		{
			_secondTimeTick=0; //归零

			onSecond();
		}

		SList<SceneLogicBase> logics=_logics;
		SceneLogicBase logic;

		for(int i=0,len=logics.Count;i<len;++i)
		{
			try
			{
				if((logic=logics[i]).enabled)
				{
					logic.onFrame(delay);
				}
			}
			catch(Exception e)
			{
				Ctrl.errorLog(e);
			}
		}

		//FIXME:利用SMap特性
		//为了性能,服务器的自定义Map系列不存在ConcurrentModified问题

		tickUnit(delay);
	}

	private void tickUnit(int delay)
	{
		IntObjectMap<Unit> fDic;
		if(!(fDic=_units).isEmpty())
		{
			Unit[] values=fDic.getValues();
			int safeIndex=fDic.getLastFreeIndex();
			Unit v;

			for(int i=safeIndex - 1;i!=safeIndex;--i)
			{
				if(i<0)
				{
					i=values.Length;
				}
				else if((v=values[i])!=null)
				{
					try
					{
						v.onFrame(delay);
					}
					catch(Exception e)
					{
						Ctrl.errorLog(e);
					}

					if(v!=values[i])
					{
						++i;
					}
				}
			}
		}

	}


	private void onSecond()
	{
		SList<SceneLogicBase> logics=_logics;
		SceneLogicBase logic;

		for(int i=0,len=logics.Count;i<len;++i)
		{
			try
			{
				if((logic=logics[i]).enabled)
				{
					logic.onSecond();
				}
			}
			catch(Exception e)
			{
				Ctrl.errorLog(e);
			}
		}

		Unit[] unitValues=_units.getValues();
		Unit unit;

		for(int i=unitValues.Length - 1;i>=0;--i)
		{
			if((unit=unitValues[i])!=null)
			{
				if(unit.enabled)
				{
					try
					{
						unit.onSecond();
					}
					catch(Exception e)
					{
						Ctrl.errorLog(e);
					}
				}
			}
		}
	}

	/** 固定更新 */
	public void onFixedUpdate()
	{
		//暂停不走帧
		if(_pause)
			return;

		SList<SceneLogicBase> logics=_logics;
		SceneLogicBase logic;

		for(int i=0,len=logics.Count;i<len;++i)
		{
			try
			{
				if((logic=logics[i]).enabled)
				{
					logic.onFixedUpdate();
				}
			}
			catch(Exception e)
			{
				Ctrl.errorLog(e);
			}
		}

		Unit[] unitValues=_units.getValues();

		Unit unit;

		for(int i=unitValues.Length - 1;i>=0;--i)
		{
			if((unit=unitValues[i])!=null)
			{
				if(unit.enabled)
				{
					try
					{
						unit.onFixedUpdate();
					}
					catch(Exception e)
					{
						Ctrl.errorLog(e);
					}
				}
			}
		}
	}

	//logics

	protected virtual SceneUnitFactoryLogic createUnitFactoryLogic()
	{
		return new SceneUnitFactoryLogic();
	}

	protected virtual SceneInOutLogic createInOutLogic()
	{
		return new SceneInOutLogic();
	}

	protected virtual SceneRoleLogic createRoleLogic()
	{
		return new SceneRoleLogic();
	}

	protected virtual ScenePosLogic createPosLogic()
	{
		return new ScenePosLogic();
	}

	protected virtual SceneShowLogic createShowLogic()
	{
		return null;
	}

	protected virtual SceneLoadLogic createLoadLogic()
	{
		return new SceneLoadLogic();
	}

	protected virtual SceneFightLogic createFightLogic()
	{
		return new SceneFightLogic();
	}

	protected virtual SceneCameraLogic createCameraLogic()
	{
		return new SceneCameraLogic();
	}

	/** 创建玩法逻辑 */
	protected virtual ScenePlayLogic createPlayLogic()
	{
		return new ScenePlayLogic();
	}

	//methods

	/** 获取固定系统时间 */
	public long getFixedTimer()
	{
		return _fixedTimer;
	}

	/** 初始化进入数据 */
	public void initEnterData(SceneEnterData enterData)
	{
		//有玩家角色
		if(enterData.roles!=null)
		{
			foreach(SceneRoleData roleData in enterData.roles)
			{
				role.addRole(roleData);
			}
		}

		if(enterData.bindVisionUnits!=null)
		{
			_bindVisionUnits=enterData.bindVisionUnits;
		}

		//有主角
		if(enterData.hero!=null)
		{
			//主角
			addHero(enterData.hero);
		}

		SList<SceneLogicBase> logics=_logics;
		SceneLogicBase logic;

		for(int i=0,len=logics.Count;i<len;++i)
		{
			if((logic=logics[i]).enabled)
			{
				logic.afterHero();
			}
		}

		//先处理掉落包数据
		if(enterData.selfBindFieldItemBags!=null)
		{
			role.initFieldItemBagBindDic(enterData.selfBindFieldItemBags);
		}

		//有单位组
		if(enterData.units!=null)
		{
			foreach(UnitData v in enterData.units)
			{
				addUnit(v);
			}
		}

		if(unitFactory!=null)
		{
			//初始化起始单位
			unitFactory.initFirst();
		}

		if(camera!=null)
		{
			//默认镜头
			camera.cameraToDefault();
		}

		//后调用play的
		play.initEnterData(enterData);

		onStart();
	}

	/** 开始 */
	protected virtual void onStart()
	{
		Ctrl.print("添加完毕");
	}

	//
	/** 准备删除 */
	public void preRemove()
	{
		_preRemoved=true;

		SList<SceneLogicBase> logics=_logics;

		for(int i=0,len=logics.Count;i<len;++i)
		{
			logics[i].preRemove();
		}
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

	/** 获取单位组 */
	public IntObjectMap<Unit> getUnitDic()
	{
		return _units;
	}

	/** 获取战斗单位 */
	public Unit getFightUnit(int instanceID)
	{
		return _fightUnits.get(instanceID);
	}

	/** 获取战斗单位 */
	public Unit getFightUnitAbs(int instanceID)
	{
		Unit re=_fightUnits.get(instanceID);

		if(re==null)
			Ctrl.throwError("找不到单位");

		return re;
	}

	/** 获取战斗单位组 */
	public IntObjectMap<Unit> getFightUnitDic()
	{
		return _fightUnits;
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

	/** 是否主角数据 */
	private bool isHeroData(UnitData data)
	{
		return data.identity.type==UnitType.Character && data.identity.playerID==GameC.player.role.playerID;
	}
	
	/** 添加主角 */
	private void addHero(UnitData data)
	{
		//直接添加，内部处理
		addUnit(data);
	}

	/** 添加单位 */
	public Unit addUnit(UnitData data)
	{
		//预处理
		//服务器驱动场景启用
		if(_config.instanceType!=SceneInstanceType.ClientDriveSinglePlayerBattle)
		{
			//是自己的M单位
			if(data.identity is MUnitIdentityData && data.identity.playerID==GameC.player.role.playerID)
			{
				MUnitUseLogic useLogic=GameC.player.character.getMUnitUseLogic(data.getMUnitIdentity().mIndex);

				if(useLogic==null)
				{
					Ctrl.throwError("不能找不到主单位的使用逻辑",data.getMUnitIdentity().mIndex);
				}

				//取主角的数据逻辑
				UnitFightDataLogic dataLogic=useLogic.getFightLogic();

				//先清空
				dataLogic.clear();
				//再重设数据
				dataLogic.setData(data.fight,data.avatar);

				data.fightDataLogic=dataLogic;
			}
		}


		//

		Unit unit=toAddUnit(data);

		if(unit!=null)
		{
			toActiveUnit(unit);

			UnitSimpleData sData=_bindVisionUnits.get(data.instanceID);

			if(sData!=null)
			{
				unit.makeSimpleUnitData(sData);
			}
		}

		return unit;
	}

	private Unit toAddUnit(UnitData data)
	{
		if(data.instanceID<=0)
		{
			Ctrl.throwError("单位流水ID未赋值",data.identity.type);
			return null;
		}

		if(ShineSetting.openCheck)
		{
			if(_units.contains(data.instanceID))
			{
				Ctrl.throwError("单位已存在");
				return null;
			}
		}

		bool canFight=BaseC.constlist.unit_canFight(data.identity.type) && !isSimple();

		if(canFight)
		{
			//没绑数据逻辑
			if(data.fightDataLogic==null)
			{
				UnitFightDataLogic dataLogic=GameC.pool.createUnitFightDataLogic();
				//主控标记
				dataLogic.isSelfControl=data.getFightIdentity().controlPlayerID==GameC.player.role.playerID;

				dataLogic.setData(data.fight,data.avatar);
				data.fightDataLogic=dataLogic;
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

		//是角色
		if(unit.isCharacter())
		{
			CharacterIdentityData iData=(CharacterIdentityData)data.identity;

			if(_config.instanceType==SceneInstanceType.FiniteBattleWithFrameSync)
			{
				_charactersByIndex.put(iData.syncIndex,unit);
			}

			_characters.put(iData.playerID,unit);
		}

		return unit;
	}

	private void toActiveUnit(Unit unit)
	{
		//active部分
		unit.enabled=true;

		unit.init();

		onAddUnit(unit);
	}

	/** 删除单位 */
	public void removeUnit(int instanceID)
	{
		Unit unit=_units.get(instanceID);

		if(unit==null)
		{
			if(ShineSetting.openCheck)
			{
				Ctrl.throwError("单位不存在:" + instanceID);
			}

			return;
		}

		UnitSimpleData sData=_bindVisionUnits.get(instanceID);

		if(sData!=null)
		{
			unit.makeSimpleUnitData(sData);
		}

		//场景移除单位
		onRemoveUnit(unit);
		//预移除
		unit.preRemove();
		//没有aoi

		//标记
		unit.enabled=false;
		//析构
		unit.dispose();
		//字典移除
		_units.remove(instanceID);

		bool canFight=unit.canFight() && !isSimple();
		
		if(canFight)
		{
			_fightUnits.remove(instanceID);
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

		if(canFight && isNeedReleaseFightDataLogic(unit))
		{
			//清空数据
			data.fightDataLogic.clear();

			//析构数据逻辑
			data.fightDataLogic.setData(null,null);
			GameC.pool.releaseUnitFightDataLogic(data.fightDataLogic);
			data.fightDataLogic=null;
		}

		if(isNeedReleaseUnit(unit))
		{
			GameC.pool.releaseUnit(unit);
		}
	}
	
	protected Unit toCreateUnitByData(UnitData data)
	{
		if(isHeroData(data))
		{
			//主角不走池
			Unit unit=GameC.factory.createUnit();
			unit.type=data.identity.type;

			//自己的单位
			unit.isHero=true;
			unit.construct();

			if(ShineSetting.openCheck)
			{
				if(_hero!=null)
				{
					Ctrl.throwError("已存在主角了");
				}
			}
			
			_hero=unit;

			return unit;
		}
		else
		{
			return GameC.pool.createUnit(data.identity.type);
		}
	}

	/** 是否回收单位数据 */
	protected bool isNeedReleaseFightDataLogic(Unit unit)
	{
		return !unit.isHero;
	}
	
	/** 是否回收单位 */
	protected bool isNeedReleaseUnit(Unit unit)
	{
		return !unit.isHero;
	}

	private void onAddUnit(Unit unit)
	{
		SList<SceneLogicBase> logics=_logics;

		for(int i=0,len=logics.Count;i<len;++i)
		{
			logics[i].onAddUnit(unit);
		}
	}

	private void onRemoveUnit(Unit unit)
	{
		SList<SceneLogicBase> logics=_logics;

		for(int i=0,len=logics.Count;i<len;++i)
		{
			logics[i].onRemoveUnit(unit);
		}
	}

	/** 移除该场景(客户端驱动用) */
	public void removeScene()
	{
		//不是客户端驱动场景
		if(driveType!=SceneDriveType.ClientDriveAll)
		{
			Ctrl.throwError("不是客户端驱动场景");
			return;
		}

		//离开场景
		GameC.scene.leaveScene(false);
	}

	/** 添加简版单位数据 */
	public void addBindVision(UnitSimpleData data)
	{
		_bindVisionUnits.put(data.instanceID,data);
	}

	public void removeBindVision(int instanceID)
	{
		_bindVisionUnits.remove(instanceID);
	}

	public IntObjectMap<UnitSimpleData> getBindVisionDic()
	{
		return _bindVisionUnits;
	}

	public UnitSimpleData getBindVisionUnit(int instanceID)
	{
		return _bindVisionUnits.get(instanceID);
	}

	//--快捷方式--//

	/** 获取副本玩法逻辑 */
	public BattleScenePlayLogic getBattleScenePlayLogic()
	{
		return (BattleScenePlayLogic)play;
	}

	public long getTimeMillis()
	{
		return DateControl.getTimeMillis();
	}
}