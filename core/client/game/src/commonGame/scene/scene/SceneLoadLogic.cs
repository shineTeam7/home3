using System;
using ShineEngine;
using UnityEngine;
using UnityEngine.SceneManagement;

/// <summary>
/// 场景加载逻辑
/// </summary>
public class SceneLoadLogic:SceneLogicBase
{
	/** 加载全部完成回调 */
	private Action _overFunc;
	/** 场景使用名字 */
	private string _sceneUseName;

	private int _index=0;
	/** 第一部分是否加载完毕 */
	protected bool _partOneComplete=false;
	/** 第一部分是否加载完毕 */
	private bool _partTwoComplete=false;

	protected IntSet _firstSet;
	
	/** 异步加载 */
	private AsyncOperation _async;
	/** 检测是否加载完成 */
	private int _checkIndex = -1;

	public SceneLoadLogic()
	{

	}

	public override void construct()
	{

	}

	public override void init()
	{

	}

	public override void dispose()
	{
		_partOneComplete=false;
		_partTwoComplete=false;
		_async = null;
		_overFunc = null;
		if (_checkIndex != -1)
		{
			TimeDriver.instance.clearFrame(_checkIndex);
			_checkIndex = -1;
		}
	}

	public override void onFrame(int delay)
	{

	}

	/** 释放资源 */
	public void disposeSource()
	{
		LoadControl.unloadSet(_firstSet);
	}

	/** 开始载入场景(第一阶段) */
	public void startLoad(Action overFunc)
	{
		_overFunc=overFunc;
		_partOneComplete=false;

		_firstSet=new IntSet();

		_firstSet.addAll(MarkResourceConfig.firstSceneLoadList);

		SceneMapConfig config=_scene.getMapConfig();
		_firstSet.add(config.sourceT);
		_firstSet.add(config.musicT);

		if(CommonSetting.clientNeedScenePlaceEditor)
		{
			string scenePlaceEditorConfigPath=BaseC.config.getSplitConfigPath(CommonSetting.scenePlaceEditor,_scene.getConfig().id);

			_firstSet.add(LoadControl.getResourceIDByName(scenePlaceEditorConfigPath));
		}

		if(CommonSetting.clientMapNeedGrid)
		{
			string splitConfigPath=BaseC.config.getSplitConfigPath(CommonSetting.mapInfo,config.id);

			_firstSet.add(LoadControl.getResourceIDByName(splitConfigPath));
		}

		foreach(int v in config.sourceExListT)
		{
			_firstSet.add(v);
		}

		makeListEx();

		doLoadOne();
	}

	/** 执行加载场景，需要复写 */
	protected virtual void doLoadOne()
	{
		if(!ShineSetting.isWholeClient)
		{
			onLoadOneOver();
			return;
		}

		int index=++_index;

		LoadControl.loadSet(_firstSet,()=>
		{
			if(_scene.isPreRemove)
				return;

			if(_index==index)
			{
				onLoadOneOver();
			}
		});
	}

	/** 指定单位组 */
	public void loadNext(ScenePreInfoData infoData)
	{
		_partTwoComplete=false;

		if(infoData==null || infoData.signedPlayers==null)
		{
			onLoadTwoOver();
		}
		else
		{
//			IntSet loadList=new IntSet();
//
//			foreach(UnitInfoData v in infoData.signedPlayers)
//			{
//				makeUnitLoadList(loadList,v);
//			}
//
//			int index=_index;
//
//			LoadControl.loadSet(loadList,()=>
//			{
//				if(_index==index)
//				{
//					onLoadTwoOver();
//				}
//			});

			//TODO:资源预加载

			onLoadTwoOver();
		}
	}

	/** 额外加载内容 */
	protected virtual void makeListEx()
	{

	}

	/** 场景资源一部分加载完毕 */
	protected virtual void onLoadOneOver()
	{
		if(!ShineSetting.isWholeClient)
		{
			sceneLoadOver0();
			return;
		}

		SceneManager.sceneLoaded+=onSceneLoaded;

		_sceneUseName=getSceneUseName();

		_async=SceneManager.LoadSceneAsync(_sceneUseName);
	}

	protected virtual string getSceneUseName()
	{
		//有实体场景
		if(_scene.getMapConfig().sourceT>0)
		{
			// if(ShineSetting.localLoadWithOutBundle)
			// {
			// 	return ShineGlobal.sourceHeadU + _scene.getConfig().source;
			// }
			// else
			// {
			// 	return FileUtils.getFileNameWithoutEx(_scene.getConfig().source);
			// }
			
			return FileUtils.getFileNameWithoutEx(_scene.getMapConfig().source);
		}
		else
		{
			return ShineSetting.rootSceneName;
		}
	}

	private void onSceneLoaded(UnityEngine.SceneManagement.Scene scene,LoadSceneMode mod)
	{
		SceneManager.sceneLoaded-=onSceneLoaded;

		//不是同一场景
		if(scene.name!=_sceneUseName)
			return;

		sceneLoadOver0();
	}

	protected void sceneLoadOver0()
	{
		_scene.onSceneLoad();
		_partOneComplete=true;
		checkParts();
	}

	private void onLoadTwoOver()
	{
		if(_scene.isPreRemove)
			return;

		_partTwoComplete=true;
		checkParts();
	}

	private void checkParts()
	{
		if(_partOneComplete && _partTwoComplete)
		{
			_checkIndex=TimeDriver.instance.setFrame(checkLoadSceneCompleteFrame);
		}
	}

	/** 检测加载场景是否完成 */
	private void checkLoadSceneCompleteFrame(int delay)
	{
		if(_partOneComplete && _partTwoComplete)
		{
			if(!ShineSetting.isWholeClient || (_async!=null && _async.isDone))
			{
				TimeDriver.instance.clearFrame(_checkIndex);

				_checkIndex = -1;

				Action func=_overFunc;
				_overFunc=null;
				_async = null;

				if(func!=null)
					func();
			}
		}
	}

	/** 构造单位加载列表 */
	protected virtual void makeUnitLoadList(IntSet loadList,UnitInfoData data)
	{
		if(BaseC.constlist.unit_canFight(data.identity.type))
		{
			int fightUnitID;

			if((fightUnitID=((FightUnitIdentityData)data.identity).getFightUnitID())>0)
			{
				int modelID;

				if((modelID=FightUnitConfig.get(fightUnitID).modelID)>0)
				{
					loadList.add(ModelConfig.get(modelID).sourceT);
				}
			}
		}


		switch(data.identity.type)
		{
			case UnitType.Character:
			{

			}
				break;
		}
	}
}