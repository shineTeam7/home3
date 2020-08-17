using System;
using ShineEngine;
using UnityEngine;
using UnityEngine.SceneManagement;

/// <summary>
/// common场景控制
/// </summary>
[Hotfix]
public class SceneControl
{
	/** 首包场景资源是否加载了 */
	public bool firstSceneResourceLoaded=false;

	/** 场景 */
	protected Scene _scene;

	/** 是否切换场景中 */
	private bool _isSwitching=false;

	/** 是否还有下一个场景 */
	private bool _hasNextScene;

	/** 预进入场景ID(也做是否有下个场景的标记) */
	private int _preSceneID=-1;
	/** 预进入场景线ID */
	private int _preLineID=-1;
	/** 预进入信息 */
	private ScenePreInfoData _preInfo;
	/** 是否已创建了下个场景(切换阶段用) */
	private bool _createdNextScene=false;

	/** 进入数据 */
	protected SceneEnterData _enterData;

	/** 此次切换场景方式 */
	private int _switchType=SceneSwitchType.Abs;
	/** 是否可不加载资源直接切换场景 */
	private bool _isSwitchCross=false;

	/** 帧事件控制索引 */
	private int _frameIndex;
	/** 固定帧事件控制索引 */
	private int _fixedFrameIndex;

	public SceneControl()
	{

	}

	public virtual void init()
	{
		_frameIndex=TimeDriver.instance.setFrame(onFrame);
		_fixedFrameIndex=TimeDriver.instance.setFixedUpdate(onFixedUpdate);

		//pool添加
		AssetPoolControl.registPool(AssetPoolType.UnitMain);
		AssetPoolControl.registPool(AssetPoolType.UnitModel);
		AssetPoolControl.registPool(AssetPoolType.UnitPart);
		AssetPoolControl.registPool(AssetPoolType.UnitHead);
		AssetPoolControl.registPool(AssetPoolType.SceneFrontUI);
		AssetPoolControl.registPool(AssetPoolType.SceneEffect);

		AssetPoolControl.registCustomCreate(AssetCustomType.UnitMainObj,()=>
		{
			return new GameObject();
		});

		//默认直接去login场景
//		returnToLoginScene();
	}

	public void dispose()
	{
		TimeDriver.instance.clearFrame(_frameIndex);
		TimeDriver.instance.clearFixedUpdate(_fixedFrameIndex);
	}

	public void onReloadConfig()
	{
		if(_scene!=null)
		{
			_scene.onReloadConfig();
		}
	}

	protected ScenePreInfoData toCreateScenePreInfoData()
	{
		return new ScenePreInfoData();
	}

	/** 返回root场景 */
	public virtual void returnToRootScene()
	{
		//没有主城
		if(!CommonSetting.hasTown)
		{
			SceneManager.LoadSceneAsync(ShineSetting.rootSceneName);
//			SceneManager.LoadSceneAsync(1);
		}
	}
	
	/** 返回登录场景 */
	protected virtual void returnToLoginScene()
	{
		//to override
	}

	public Scene getScene()
	{
		return _scene;
	}

	public Unit getHero()
	{
		if(_scene!=null)
			return _scene.hero;

		return null;
	}

	/** 清空当前场景(needClearSource:是否需要清资源) */
	private void clearNowScene(bool needClearSource)
	{
		if(_scene!=null)
		{
			try
			{
				_scene.dispose();
			}
			catch(Exception e)
			{
				Ctrl.errorLog(e);
			}

			if(needClearSource)
			{
				_scene.load.disposeSource();
			}

			_scene=null;
		}
	}

	/** 清除预进入场景标记 */
	private void clearSceneMark()
	{
		_createdNextScene=false;
		_preSceneID=-1;
		_preLineID=-1;
	}

	/** 请求进入场景 */
	public void applyEnterScene(int id)
	{
		applyEnterScene(id,-1,-1);
	}

	/** 请求进入场景 */
	public void applyEnterScene(int id,int lineID,int posID)
	{
		if(_isSwitching)
			return;

		SceneConfig config=SceneConfig.get(id);

		if(!GameC.player.role.checkRoleConditions(config.enterConditions,true))
		{
			Ctrl.warnLog("场景进入条件未达成");
			return;
		}

		//客户端单人副本
		if(config.instanceType==SceneInstanceType.ClientDriveSinglePlayerBattle)
		{
			preEnterScene(id,-1);
		}
		else
		{
			if(!BaseC.constlist.sceneInstance_canClientApplyEnter(config.instanceType))
			{
				Ctrl.warnLog("申请进入场景时,不可进入的场景");
				return;
			}

			ApplyEnterSceneRequest.create(id,lineID,posID).send();
		}
	}

	/** 申请离开当前场景 */
	public void applyLeaveNowScene()
	{
		if(_scene==null)
			return;

		//客户端场景
		if(_scene.isDriveAll())
		{
			//直接移除
			_scene.removeScene();
		}
		else
		{
			ApplyLeaveSceneRequest.create().send();
		}
	}

	/** 离开当前场景 */
	public void leaveScene(bool hasNext)
	{
		Ctrl.print("离开当前场景",hasNext);

		onLeaveScene(hasNext);
	}

	/** 离开当前场景绝对(不包含UI部分)(回登录口用) */
	public void leaveSceneAbs()
	{
		clearSceneMark();
		_isSwitching=true;

		if(_scene!=null)
		{
			_isSwitchCross = false;
			_switchType = SceneSwitchType.Abs;
			
			removeNowScene();
		}
	}

	//中断切换场景
	private void cancelSwitch()
	{

	}

	/** 预备进入场景(来自服务器推送) */
	public void preEnterScene(int sceneID,int lineID)
	{
		_preSceneID=sceneID;
		_preLineID=lineID;
		_createdNextScene=false;
		_preInfo=null;
		_isSwitching=true;
		_isSwitchCross=false;

		SceneConfig nextConfig=SceneConfig.get(sceneID);

		_switchType=nextConfig.switchType;

		if(_scene!=null)
		{
			//是否跳过加载资源
			if(isSkipLoadSource(nextConfig))
			{
				_isSwitchCross=true;
			}
		}

		removeNowScene();
	}

	/** 是否可跳过加载 */
	protected virtual bool isSkipLoadSource(SceneConfig nextConfig)
	{
		//地图id相同
		return _scene.getConfig().mapID == nextConfig.mapID;
	}

	/** 预进入场景第二阶段 */
	public void preEnterSceneNext(ScenePreInfoData info)
	{
		if(_createdNextScene)
		{
			_scene.setPreInfo(info);

			//不是无切换过程
			if(_isSwitchCross)
			{
				loadSceneResourceOver();
			}
			else
			{
				_scene.load.loadNext(info);
			}
		}
		else
		{
			_preInfo=info;
		}
	}

	/** 移除当前场景 */
	protected void removeNowScene()
	{
		if(_scene!=null)
		{
			_scene.preRemove();

			GameC.player.dispatch(GameEventType.LeaveScene,_scene.getConfig().id);
		}

		onRemoveNowScene();

		doRemoveSwitch();
	}

	/** 移除当前场景接口 */
	protected virtual void onRemoveNowScene()
	{
		GameC.ui.hideUIByRemoveScene();
	}

	/** 执行remove切换 */
	protected virtual void doRemoveSwitch()
	{
		switch(_switchType)
		{
			case SceneSwitchType.Abs:
			{
				removeNowSceneOver();
			}
				break;
			case SceneSwitchType.Fade:
			{
				//TODO:实现淡入

				TimeDriver.instance.setTimeOut(removeNowSceneOver,1000);
			}
				break;
			default:
				break;
		}
	}

	protected void removeNowSceneOver()
	{
		onRemoveNowSceneOver();

		clearNowScene(!_isSwitchCross);

		//有下个场景
		if(_preSceneID>0)
		{
			//创建场景
			//客户端场景不做池了

			SceneConfig config=SceneConfig.get(_preSceneID);

			_scene=GameC.factory.createScene();
			_scene.setType(config.type);
			_scene.construct();

			//初始化场景ID
			_scene.initSceneID(_preSceneID);

			//设置预进入数据
			if(_preInfo!=null)
			{
				_scene.setPreInfo(_preInfo);
			}

			//标记创建了下个场景
			_createdNextScene=true;

			if(_isSwitchCross)
			{
				// loadSceneResourceOver();
			}
			else
			{
				_scene.load.startLoad(loadSceneResourceOver);
			}

			//客户端单人副本
			if(config.instanceType==SceneInstanceType.ClientDriveSinglePlayerBattle)
			{
				//有进出逻辑
				if(_scene.inout!=null)
				{
					ScenePreInfoData preInfo=toCreateScenePreInfoData();
					_scene.inout.beforePreEnter();
					_scene.inout.makePreInfo(preInfo);
					preEnterSceneNext(preInfo);
				}
				//没有进出逻辑
				else
				{
					preEnterSceneNext(null);
				}
			}
		}
		else
		{
			leaveSceneOver();
		}
	}

	protected virtual void onRemoveNowSceneOver()
	{
		GameC.ui.hideUIByRemoveSceneOver();
	}

	/** 离开场景结束(返回无场景状态) */
	protected virtual void leaveSceneOver()
	{
		returnToRootScene();

		_isSwitching=false;

		onLeaveSceneOver();

		GameC.player.dispatch(GameEventType.LeaveLastSceneOver);
	}

	protected virtual void onLeaveSceneOver()
	{
		GameC.ui.showUIByLeaveSceneOver();
	}

	protected void loadSceneResourceOver()
	{
		//客户端单人副本
		if(SceneConfig.get(_preSceneID).instanceType==SceneInstanceType.ClientDriveSinglePlayerBattle)
		{
			toEnterClientScene();
		}
		else
		{
			if(CommonSetting.useSceneServer)
			{
				Ctrl.print("A3");

				//推送到服务器
				PreEnterSceneReadyForSceneRequest.create().send();
			}
			else
			{
				//推送到服务器
				PreEnterSceneReadyRequest.create().send();
			}

		}
	}

	private void makeClientHeroData(UnitData uData)
	{
		//给予一个流水
		uData.instanceID=_scene.getUnitInstanceID();

		if(!_scene.isSimple())
		{
			_scene.method.makeScenePosData(uData);
		}

		_scene.battle.makeCharacterData(uData);
		_scene.method.makeCharacterData(uData);
	}

	/** 执行进入空场景 */
	public void doEnterNoneScene()
	{
		//离线游戏中
		if(CommonSetting.useOfflineGame && GameC.main.isEnteredFirstScene())
		{
			//不处理
		}
		else
		{
			onEnterNoneScene();
		}
	}

	/** 进入空场景(初始化阶段用,显示) */
	public virtual void onEnterNoneScene()
	{
		leaveSceneOver();

		GameC.main.checkEnterFirstScene();
	}

	/** 进入客户端场景 */
	private void toEnterClientScene()
	{
		SceneEnterData enterData=_scene.method.createSceneEnterData();

		//有进出逻辑
		if(_scene.inout!=null)
		{
			UnitData heroData=_scene.inout.createCharacterDataSelf();

			enterData.hero=heroData;
			enterData.units=new SList<UnitData>();
		}

		_scene.battle.makeSceneEnterData(enterData);
		_scene.method.makeSceneEnterData(enterData);

		enterScene(enterData);
	}

	/** 服务器推送进入场景 */
	public void enterScene(SceneEnterData enterData)
	{
		Ctrl.print("进入场景");

		_enterData=enterData;

		//TODO:这里可以再补一次资源存在性校验

		//初始化
		_scene.init();
		//初始化进入数据
		_scene.initEnterData(_enterData);

		GameC.player.dispatch(GameEventType.EnterScene,_scene.getConfig().id);

		onEnterScene();

		doAddSwitch();
	}

	protected virtual void onEnterScene()
	{
		GameC.ui.showUIByEnterScene();
		GameC.main.checkEnterFirstScene();
	}

	/** 执行add切换 */
	protected virtual void doAddSwitch()
	{
		switch(_switchType)
		{
			case SceneSwitchType.Abs:
			{
				addSceneOver();
			}
				break;
			case SceneSwitchType.Fade:
			{
				//TODO:实现淡出
				TimeDriver.instance.setTimeOut(addSceneOver,1000);
			}
				break;
			default:
				break;
		}
	}

	/** add场景完毕 */
	protected virtual void addSceneOver()
	{
		_isSwitching=false;
		_isSwitchCross=false;
		_switchType=SceneSwitchType.Abs;

		GameC.ui.showUIByEnterSceneOver();
	}

	/** 进入场景失败 */
	public void onEnterSceneFailed()
	{
		//TODO:进入场景失败的处理,等几秒再进

	}

	/** 离开场景消息 */
	public void onLeaveScene(bool hasNextScene)
	{
		//没有了才做处理
		if(!hasNextScene)
		{
			clearSceneMark();

			if(_scene!=null)
			{
				_isSwitching=true;

				_switchType=_scene.getConfig().switchType;

				removeNowScene();
			}
		}
	}

	private void onFrame(int delay)
	{
		if(_scene!=null && _scene.isInit)
		{
			_scene.onFrame(delay);
		}
	}

	private void onFixedUpdate()
	{
		if(_scene!=null && _scene.isInit)
		{
			_scene.onFixedUpdate();
		}
	}

	//match

	/** 匹配成功 */
	public virtual void onMatchSuccess(int funcID,int index,PlayerMatchData[] matcheDatas)
	{
		//TODO:匹配成功的显示

		//TODO:等上1秒

		TimeDriver.instance.setTimeOut(()=>
		{
			//直接接受
			FuncAcceptMatchRequest.create(funcID,index).send();
		},1000);
	}

	/** 重新加入匹配 */
	public virtual void onReAddMatch(int funcID)
	{

	}

	/** 匹配超时 */
	public virtual void onMatchTimeOut(int funcID)
	{
		//处理超时逻辑
	}

	/** 主角移动朝向 */
	public void heroStartMoveDir()
	{
		Unit unit;
		if(_scene!=null && (unit=_scene.hero)!=null)
			unit.control.startMoveDir();
	}

	/** 主角移动朝向 */
	public void heroMoveDir(float dir)
	{
		Unit unit;
		if(_scene!=null && (unit=_scene.hero)!=null)
			unit.control.moveDir(dir);
	}

	/** 主角取消移动朝向 */
	public void heroCancelMoveDir()
	{
		Unit unit;
		if(_scene!=null && (unit=_scene.hero)!=null)
			unit.control.cancelMoveDir();
	}

	public void heroDrive(int forward,int turn)
	{
		Unit unit;
		if(_scene!=null && (unit=_scene.hero)!=null)
		{
			Unit vehicle=unit.move.getVehicle();

			if(vehicle!=null)
			{
				vehicle.move.drive(forward,turn);
			}
		}
	}
}