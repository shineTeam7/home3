package com.home.commonClient.control;

import com.home.commonBase.config.game.SceneConfig;
import com.home.commonBase.config.game.enumT.TaskTypeConfig;
import com.home.commonBase.constlist.generate.QuestType;
import com.home.commonBase.constlist.generate.SceneType;
import com.home.commonBase.control.LogicExecutorBase;
import com.home.commonBase.data.quest.TaskData;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.global.CommonSetting;
import com.home.commonClient.global.ClientC;
import com.home.commonClient.part.player.Player;
import com.home.commonClient.scene.base.GameScene;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.collection.SMap;
import com.home.shine.support.pool.ObjectPool;

/** 逻辑执行器 */
public class LogicExecutor extends LogicExecutorBase
{
	/** 角色字典(uid为key) */
	private SMap<String,Player> _players=new SMap<>();
	
	/** 场景对象池 */
	private ObjectPool<GameScene>[] _scenePoolDic;
	
	/** 任务目标数据池 */
	private ObjectPool<TaskData>[] _taskDataPool;
	
	public LogicExecutor(int index)
	{
		super(index);
	}
	
	/** 初始化(池线程) */
	@Override
	public void init()
	{
		super.init();
		
		_scenePoolDic=new ObjectPool[SceneType.size];
		
		for(int i=0;i<SceneType.size;++i)
		{
			_scenePoolDic[i]=createScenePool(i);
		}
		
		//逻辑部分
		
		TaskTypeConfig typeConfig;
		
		_taskDataPool=new ObjectPool[QuestType.size];
		_taskDataPool[0]=createTaskDataPool(0);
		
		for(int i=0;i<_taskDataPool.length;++i)
		{
			if((typeConfig=TaskTypeConfig.get(i))!=null && typeConfig.needCustomTask)
			{
				_taskDataPool[i]=createTaskDataPool(i);
			}
		}
	}
	
	private ObjectPool<GameScene> createScenePool(int type)
	{
		ObjectPool<GameScene> re=new ObjectPool<GameScene>(()->
		{
			GameScene scene=ClientC.factory.createScene();
			scene.setType(type);
			scene.construct();
			
			return scene;
		},CommonSetting.scenePoolSize);
		
		return re;
	}
	
	private ObjectPool<TaskData> createTaskDataPool(int type)
	{
		ObjectPool<TaskData> re=new ObjectPool<TaskData>(()->
		{
			return BaseC.logic.createTaskData(type);
		});
		
		return re;
	}
	
	@Override
	protected void onFrame(int delay)
	{
		super.onFrame(delay);
		
		//玩家部分
		
		Object[] table=_players.getTable();
		
		Player player;
		
		for(int i=table.length - 2;i >= 0;i-=2)
		{
			if(table[i]!=null)
			{
				player=(Player)table[i + 1];
				
				try
				{
					player.onFrame(delay);
				}
				catch(Exception e)
				{
					Ctrl.errorLog(e);
				}
				
				if(player!=table[i + 1])
				{
					i+=2;
				}
			}
		}
	}
	
	/** 获取角色数目 */
	public int getPlayerNum()
	{
		return _players.size();
	}
	
	/** 创建场景(实际创建)(未init) */
	public GameScene createScene(int sceneID)
	{
		GameScene scene=_scenePoolDic[SceneConfig.get(sceneID).type].getOne();
		
		scene.initSceneID(sceneID);
		
		//绑定执行器
		scene.setExecutor(this);
		
		return scene;
	}
	
	/** 释放场景(dispose过) */
	public void releaseScene(GameScene scene)
	{
		_scenePoolDic[scene.getType()].back(scene);
	}
	
	/** 角色登录(与角色进入不同)(逻辑线程) */
	public void playerLogin(Player player)
	{
		if(ShineSetting.openCheck)
		{
			if(_players.contains(player.role.uid))
			{
				Ctrl.throwError("此时executor不该有角色");
			}
		}
		
		_players.put(player.role.uid,player);
		player.system.executorIndex=_index;
		
		//开始登陆
		player.system.startLogin();
	}
	
	/** 角色下线(与角色退出不同)(逻辑线程) */
	public void playerExit(Player player)
	{
		if(ShineSetting.openCheck)
		{
			if(!_players.contains(player.role.uid))
			{
				Ctrl.throwError("此时executor不该没有角色");
				return;
			}
		}
		
		_players.remove(player.role.uid);
		
		//不置空
		//player.system.executorIndex=-1;
	}
	
	/** 创建任务目标数据 */
	public TaskData createTaskData(int type)
	{
		if(TaskTypeConfig.get(type).needCustomTask)
		{
			return _taskDataPool[type].getOne();
		}
		else
		{
			return _taskDataPool[0].getOne();
		}
	}
	
	/** 回收任务目标数据 */
	public void releaseTaskData(int type,TaskData data)
	{
		if(TaskTypeConfig.get(type).needCustomTask)
		{
			_taskDataPool[type].back(data);
		}
		else
		{
			_taskDataPool[0].back(data);
		}
	}
}
