package com.home.commonScene.control;

import com.home.commonBase.config.game.SceneConfig;
import com.home.commonBase.constlist.generate.SceneInstanceType;
import com.home.commonBase.constlist.generate.SceneType;
import com.home.commonBase.control.LogicExecutorBase;
import com.home.commonBase.data.mail.MailData;
import com.home.commonBase.data.quest.AchievementData;
import com.home.commonBase.data.quest.TaskData;
import com.home.commonBase.data.scene.scene.CreateSceneData;
import com.home.commonBase.data.scene.scene.SceneLocationData;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.global.Global;
import com.home.commonScene.global.SceneC;
import com.home.commonScene.part.ScenePlayer;
import com.home.commonScene.scene.base.SScene;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.IndexMaker;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.LongObjectMap;
import com.home.shine.support.pool.ObjectPool;

public class SceneLogicExecutor extends LogicExecutorBase
{
	//场景部分
	/** 场景对象池 */
	private ObjectPool<SScene>[] _scenePoolDic;
	
	/** 角色字典(id为key)(在线) */
	private LongObjectMap<ScenePlayer> _players=new LongObjectMap<>(ScenePlayer[]::new);
	
	/** 场景组(key:instanceID) */
	private IntObjectMap<SScene> _scenes=new IntObjectMap<>(SScene[]::new);
	/** 场景流水ID生成 */
	private IndexMaker _sceneInstanceIDMaker=new IndexMaker(0,ShineSetting.indexMax,true);
	
	//逻辑部分
	/** 临时创建场景数据 */
	private CreateSceneData _createSceneData;
	
	public SceneLogicExecutor(int index)
	{
		super(index);
	}
	
	@Override
	public void init()
	{
		super.init();
		
		_scenePoolDic=new ObjectPool[SceneType.size];
		
		for(int i=0;i<SceneType.size;++i)
		{
			(_scenePoolDic[i]=createScenePool(i)).setEnable(CommonSetting.sceneUsePool);
		}
		
		_createSceneData=BaseC.factory.createCreateSceneData();
	}
	
	private ObjectPool<SScene> createScenePool(int type)
	{
		ObjectPool<SScene> re=new ObjectPool<SScene>(()->
		{
			SScene scene=SceneC.factory.createScene();
			scene.setType(type);
			scene.construct();
			return scene;
		},CommonSetting.scenePoolSize);
		
		return re;
	}
	
	@Override
	protected void onFrame(int delay)
	{
		super.onFrame(delay);
		
		if(delay<=0)
			return;
		
		if(!_scenes.isEmpty())
		{
			SScene[] values;
			SScene v;
			
			for(int i=(values=_scenes.getValues()).length-1;i>=0;--i)
			{
				if((v=values[i])!=null)
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
	
	@Override
	protected void onSecond(int delay)
	{
		super.onSecond(delay);
		
		int index=_index;
		
		_players.forEachValueS(player->
		{
			//当前执行器才执行
			if(player.isCurrentExecutor(index))
			{
				try
				{
					player.onSecond(delay);
				}
				catch(Exception e)
				{
					Ctrl.errorLog(e);
				}
			}
		});
	}
	
	/** 获取当前承载场景数 */
	public int getSceneNum()
	{
		return _scenes.size();
	}
	
	/** 取一个单位实例ID */
	private int getSceneInstanceID()
	{
		int re;
		
		while(_scenes.contains(re=_sceneInstanceIDMaker.get()));
		
		return re;
	}
	
	/** 获取场景 */
	public SScene getScene(int instanceID)
	{
		return _scenes.get(instanceID);
	}
	
	/** 创建场景(实际创建)(已init) */
	public SScene createScene(int sceneID)
	{
		_createSceneData.sceneID=sceneID;
		return createScene(_createSceneData);
	}
	
	/** 创建场景(实际创建)(已init) */
	public SScene createScene(CreateSceneData data)
	{
		SScene scene=_scenePoolDic[SceneConfig.get(data.sceneID).type].getOne();
		
		//绑定执行器
		scene.setExecutor(this);
		scene.instanceID=getSceneInstanceID();
		Ctrl.print("添加场景",scene.instanceID);
		_scenes.put(scene.instanceID,scene);
		
		//初始化ID
		scene.initCreate(data);
		scene.init();
		
		return scene;
	}
	
	/** 删除场景 */
	public void removeScene(int instanceID)
	{
		SScene scene=_scenes.remove(instanceID);
		
		if(scene==null)
		{
			Ctrl.throwError("未找到场景",instanceID);
			return;
		}
		
		//析构
		scene.preDispose();
		
		_scenePoolDic[scene.getType()].back(scene);
	}
	
	/** 获取活创建场景 */
	public SScene getOrCreateScene(SceneLocationData location)
	{
		if(location.instanceID!=-1)
		{
			return getScene(location.instanceID);
		}
		
		return createScene(location.sceneID);
	}
	
	public void playerSwitchIn(ScenePlayer player)
	{
		_players.put(player.playerID,player);
		player.setExecutor(this);
	}
	
	public void playerSwitchOut(ScenePlayer player)
	{
		toPlayerLeave(player);
	}
	
	/** 角色离开(与角色退出不同)(逻辑线程) */
	public void playerExit(ScenePlayer player)
	{
		toPlayerLeave(player);
	}
	
	protected void toPlayerLeave(ScenePlayer player)
	{
		if(ShineSetting.openCheck)
		{
			if(!_players.contains(player.playerID))
			{
				player.throwError("此时executor不该没有角色");
				return;
			}
		}
		
		try
		{
			SScene scene=player.getScene();
			
			if(scene!=null)
			{
				scene.sInOut.playerLeave(player);
			}
			
			//离开
			player.onLeave();
		}
		catch(Exception e)
		{
			player.errorLog(e);
		}
		
		_players.remove(player.playerID);
		
		player.setExecutor(null);
		player.setSocketReady(false);
	}
}
