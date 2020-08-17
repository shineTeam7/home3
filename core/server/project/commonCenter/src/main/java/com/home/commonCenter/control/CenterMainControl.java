package com.home.commonCenter.control;


import com.home.commonBase.constlist.generate.InfoCodeType;
import com.home.commonBase.constlist.system.WorkSenderType;
import com.home.commonBase.constlist.system.WorkType;
import com.home.commonBase.data.login.CenterInitServerData;
import com.home.commonBase.data.login.GameLoginData;
import com.home.commonBase.data.system.AreaGlobalWorkData;
import com.home.commonBase.data.system.CenterGlobalWorkData;
import com.home.commonBase.data.system.GameServerRunData;
import com.home.commonBase.data.system.PlayerWorkData;
import com.home.commonBase.data.system.WorkData;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.utils.BaseGameUtils;
import com.home.commonCenter.global.CenterC;
import com.home.commonCenter.net.centerRequest.system.SendInfoCodeFromCenterRequest;
import com.home.commonCenter.net.serverRequest.game.system.GameReloadConfigServerRequest;
import com.home.commonCenter.net.serverRequest.game.system.SendAreaWorkToGameFromCenterServerRequest;
import com.home.commonCenter.net.serverRequest.game.system.SendPlayerWorkCompleteServerRequest;
import com.home.commonCenter.net.serverRequest.game.system.SendPlayerWorkServerRequest;
import com.home.commonCenter.net.serverRequest.game.system.SendWorkFailedServerRequest;
import com.home.commonCenter.net.serverRequest.manager.ReMAddPlayerWorkToCenterServerRequest;
import com.home.commonCenter.net.serverRequest.manager.ReMQueryPlayerWorkToCenterServerRequest;
import com.home.shine.control.DateControl;
import com.home.shine.control.ThreadControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.collection.IntIntMap;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.LongIntMap;
import com.home.shine.thread.PoolThread;

/** 中心服主控制 */
public class CenterMainControl
{
	//pool
	/** 执行器组(分线程) */
	private CenterLogicExecutor[] _executors;
	
	//configs
	
	/** 区服老新对照表(areaID:gameID)(如需修改，必须copyOnWrite) */
	private IntIntMap _areaDic=new IntIntMap();
	
	//--logic--//
	/** 游戏服运行组(key:gameID) */
	private IntObjectMap<GameServerRunData> _gameRunDic=new IntObjectMap<>(GameServerRunData[]::new);
	
	
	/** 角色在线数目 */
	private int _playerOnlineNum=0;
	
	//login
	
	//gm操作
	/** gm执行的角色离线事务表 */
	private LongIntMap _gmPlayerOfflineWorkDic=new LongIntMap();
	
	
	public void init()
	{
		_executors=new CenterLogicExecutor[ShineSetting.poolThreadNum];
		
		for(int i=0;i<_executors.length;++i)
		{
			CenterLogicExecutor executor=createExecutor(i);
			_executors[i]=executor;
			
			ThreadControl.addPoolFunc(i,()->
			{
				executor.init();
			});
		}
		
		ThreadControl.getMainTimeDriver().setInterval(this::onSecond,1000);
	}
	
	public void dispose()
	{
		
	}
	
	/** 创建执行器 */
	protected CenterLogicExecutor createExecutor(int index)
	{
		return new CenterLogicExecutor(index);
	}
	
	protected void onSecond(int delay)
	{
		CenterC.global.onSecond(delay);
	}
	
	/** 获取对应执行器 */
	public CenterLogicExecutor getExecutor(int index)
	{
		if(index==-1)
		{
			return null;
		}
		
		return _executors[index];
	}
	
	//方法
	
	/** 设置初始化数据 */
	public void setInitData(CenterInitServerData initData)
	{
		_areaDic=initData.areaDic;
		CenterC.server.setSelfInfo(initData.info);
	}
	
	/** 热更服务器配置后 */
	public void onReloadServerConfig()
	{
	
	}
	
	/** 获取当前区服ID */
	public int getNowGameID(int areaID)
	{
		return _areaDic.getOrDefault(areaID,areaID);
	}
	
	/** 通过角色ID获取当前所在源区服 */
	public int getNowGameIDByLogicID(long logicID)
	{
		return getNowGameID(BaseC.logic.getAreaIDByLogicID(logicID));
	}
	
	/** 区服id是否存在 */
	public boolean isAreaAvailable(int areaID)
	{
		return _areaDic.get(areaID)>0;
	}
	
	//config
	
	/** 重新加载配置(主线程) */
	public void reloadConfig(Runnable func)
	{
		Ctrl.log("reloadCenterConfig");

		BaseC.config.reload(this::onReloadConfig,()->
		{
			Ctrl.log("reloadCenterConfigComplete");
			
			//广播game
			CenterC.server.radioGames(GameReloadConfigServerRequest.create());
			
			if(func!=null)
				func.run();
		});
	}
	
	private void onReloadConfig()
	{
		//当前是主线程
		if(ThreadControl.isMainThread())
		{
			//global
			CenterC.global.onReloadConfig();
		}
		else
		{
			PoolThread poolThread=(PoolThread)ThreadControl.getCurrentShineThread();
			
			_executors[poolThread.index].onReloadConfig();
		}
	}
	
	/** 游戏服登录 */
	public GameLoginData makeGameLoginData(int gameID)
	{
		GameLoginData data=BaseC.factory.createGameLoginData();
		data.initDefault();
		CenterC.global.makeGameLoginData(gameID,data);
		return data;
	}
	
	/** 游戏运行中字典 */
	public IntObjectMap<GameServerRunData> getGameRunDic()
	{
		return _gameRunDic;
	}
	
	/** 获取某game服的在线角色数目 */
	public int getGamePlayerNum(int gameID)
	{
		return _gameRunDic.get(gameID).onlineNum;
	}
	
	/** 获取当前总在线角色数 */
	public int getPlayerOnlineNum()
	{
		return _playerOnlineNum;
	}
	
	/** 重放事务 */
	public void resendWork(WorkData data)
	{
		if(data instanceof PlayerWorkData)
		{
			PlayerWorkData pData=(PlayerWorkData)data;
			
			doAddPlayerWork(pData);
		}
		else
		{
			Ctrl.throwError("不支持的事务类型",data.getDataClassName());
		}
	}
	
	/** 完成事务 */
	public void completeWork(WorkData data)
	{
		if(data instanceof PlayerWorkData)
		{
			PlayerWorkData pData=(PlayerWorkData)data;
			
			doCompletePlayerWork(pData);
		}
		else
		{
			Ctrl.throwError("不支持的事务类型2",data.getDataClassName());
		}
	}
	
	/** 角色服添加离线事务 */
	public void onAddPlayerWorkForGame(PlayerWorkData data)
	{
		doAddPlayerWork(data);
	}
	
	private void toAddPlayerWork(PlayerWorkData data)
	{
		data.timestamp=DateControl.getTimeMillis();
		data.senderIndex=BaseGameUtils.getWorkSenderIndex(WorkSenderType.Center,0);
		
		int areaID=BaseC.logic.getAreaIDByLogicID(data.receivePlayerID);
		
		//不存在的区
		if(!isAreaAvailable(areaID))
		{
			onWorkResult(data,InfoCodeType.WorkError);
			return;
		}
		
		if(ThreadControl.isMainThread())
		{
			toAddPlayerWorkOnMain(data);
		}
		else
		{
			ThreadControl.addMainFunc(()->
			{
				toAddPlayerWorkOnMain(data);
			});
		}
	}
	
	private void toAddPlayerWorkOnMain(PlayerWorkData data)
	{
		//不是在线事务
		if(data.workType!=WorkType.PlayerOnline)
		{
			//记录事务
			CenterC.global.system.getWorkSendTool().recordWork(data);
		}
		
		doAddPlayerWork(data);
	}
	
	private void doAddPlayerWork(PlayerWorkData data)
	{
		int areaID=BaseC.logic.getAreaIDByLogicID(data.receivePlayerID);
		
		//存在的区服
		if(_areaDic.contains(areaID))
		{
			SendPlayerWorkServerRequest.create(data).send(getNowGameID(areaID));
		}
		else
		{
			//不是在线事务
			if(data.workType!=WorkType.PlayerOnline)
			{
				//不存在的情况
				
				if(data.workInstanceID>0)
				{
					int senderType=BaseGameUtils.getWorkSenderType(data.senderIndex);
					
					if(senderType==WorkSenderType.Center)
					{
						onReceiptWork(data.workInstanceID,InfoCodeType.WorkError);
					}
					else if(senderType==WorkSenderType.Game)
					{
						int senderID=BaseGameUtils.getWorkSenderID(data.senderIndex);
						//事务失败
						SendWorkFailedServerRequest.create(data.senderIndex,data.workInstanceID).send(getNowGameID(senderID));
					}
				}
			}
		}
	}
	
	private void doCompletePlayerWork(PlayerWorkData data)
	{
		SendPlayerWorkCompleteServerRequest.create(data.createCompleteData()).sendToSourceGameByPlayerID(data.receivePlayerID);
	}
	
	/** 添加角色事务 */
	public void addPlayerWork(int type,long playerID,PlayerWorkData data)
	{
		data.receivePlayerID=playerID;
		data.workType=type;
		
		toAddPlayerWork(data);
	}
	
	/** 添加角色离线事务 */
	public void addPlayerOfflineWork(long playerID,PlayerWorkData data)
	{
		addPlayerWork(WorkType.PlayerOffline,playerID,data);
	}
	
	/** 添加角色立即执行事务 */
	public void addPlayerAbsWork(long playerID,PlayerWorkData data)
	{
		addPlayerWork(WorkType.PlayerAbs,playerID,data);
	}
	
	/** 添加角色在线事务 */
	public void addPlayerOnlineWork(long playerID,PlayerWorkData data)
	{
		addPlayerWork(WorkType.PlayerOnline,playerID,data);
	}
	
	/** gm添加离线事务(主线程) */
	public void addPlayerOfflineWorkByGM(int httpID,PlayerWorkData data)
	{
		data.workType=WorkType.PlayerOffline;
		_gmPlayerOfflineWorkDic.put(data.workInstanceID,httpID);
		
		toAddPlayerWork(data);
	}
	
	/** gm添加查询事务(主线程) */
	public void addPlayerQueryWorkByGM(int httpID,PlayerWorkData data)
	{
		data.workType=WorkType.PlayerAbs;
		data.senderCarryID=httpID;
		_gmPlayerOfflineWorkDic.put(data.workInstanceID,httpID);
		
		toAddPlayerWork(data);
	}
	
	/** 添加区服全局事务(业务线程) */
	public void addAreaWork(AreaGlobalWorkData data)
	{
		data.timestamp=DateControl.getTimeMillis();
		data.senderIndex=BaseGameUtils.getWorkSenderIndex(WorkSenderType.Center,0);
		data.workType=WorkType.Game;
		
		ThreadControl.checkCurrentIsMainThread();
		
		if(isAreaAvailable(data.receiveAreaID))
		{
			if(ThreadControl.isMainThread())
			{
				doAddAreaWorkOnMain(data);
			}
			else
			{
				ThreadControl.addMainFunc(()->
				{
					doAddAreaWorkOnMain(data);
				});
			}
		}
		else
		{
			onWorkResult(data,InfoCodeType.WorkError);
		}
	}
	
	/** 执行添加区服事务(主线程) */
	private void doAddAreaWorkOnMain(AreaGlobalWorkData data)
	{
		//记录事务
		CenterC.global.system.getWorkSendTool().recordWork(data);
		
		doAddAreaWork(data);
	}
	
	/** 执行添加区服事务 */
	private void doAddAreaWork(AreaGlobalWorkData data)
	{
		int nowGameID=getNowGameID(data.receiveAreaID);
		
		SendAreaWorkToGameFromCenterServerRequest.create(data).send(nowGameID);
	}
	
	/** 中心服收到回执 */
	public void onReceiptWork(long instanceID,int result)
	{
		WorkData workData=CenterC.global.system.getWorkSendTool().onReceiptWork(instanceID);
		
		if(workData!=null)
		{
			onWorkResult(workData,result);
		}
		
		int httpID=_gmPlayerOfflineWorkDic.remove(instanceID);
		
		if(httpID>0)
		{
			if(workData!=null && workData.senderCarryID>0)
			{
				if(result!=InfoCodeType.Success)
				{
					ReMQueryPlayerWorkToCenterServerRequest.create(httpID,null).send();
					return;
				}
			}
			
			//回复
			ReMAddPlayerWorkToCenterServerRequest.create(httpID,result).send();
		}
	}
	
	/** 事务执行结果(是否成功找到目标并执行) */
	protected void onWorkResult(WorkData data,int result)
	{
	
	}
	
	/** 推送角色信息码 */
	public void sendInfoCodeToPlayer(long playerID,int code)
	{
		SendInfoCodeFromCenterRequest.create(code).sendToPlayer(playerID);
	}
	
	/** 更新逻辑服在线数目 */
	public void onRefreshGameOnlineNum(int gameID,int num)
	{
		GameServerRunData data=_gameRunDic.get(gameID);
		
		if(data==null)
		{
			data=new GameServerRunData();
			data.id=gameID;
			data.onlineNum=0;
			_gameRunDic.put(gameID,data);
		}
		
		_playerOnlineNum+=(num-data.onlineNum);
		data.onlineNum=num;
	}
	
	/** 收到添加区服事务(主线程) */
	public void onAddCenterWork(CenterGlobalWorkData data)
	{
		CenterC.global.system.executeCenterWork(data);
	}
	
	
}
