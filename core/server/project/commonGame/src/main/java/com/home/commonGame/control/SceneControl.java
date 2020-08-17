package com.home.commonGame.control;

import com.home.commonBase.config.game.SceneConfig;
import com.home.commonBase.config.other.MapInfoConfig;
import com.home.commonBase.constlist.generate.InfoCodeType;
import com.home.commonBase.constlist.generate.SceneInstanceType;
import com.home.commonBase.data.role.RoleShowData;
import com.home.commonBase.data.scene.match.MatchSceneData;
import com.home.commonBase.data.scene.match.PlayerMatchData;
import com.home.commonBase.data.scene.match.PlayerMatchSuccessWData;
import com.home.commonBase.data.scene.scene.CreateSceneData;
import com.home.commonBase.data.scene.scene.SceneEnterArgData;
import com.home.commonBase.data.scene.scene.SceneLocationData;
import com.home.commonBase.extern.ExternMethodNative;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.global.Global;
import com.home.commonGame.dataEx.scene.SceneAutoLineCountData;
import com.home.commonGame.global.GameC;
import com.home.commonGame.net.request.func.match.FuncCancelMatchRequest;
import com.home.commonGame.net.request.func.match.FuncMatchOverRequest;
import com.home.commonGame.net.serverRequest.center.scene.ReCreateSignedSceneToCenterServerRequest;
import com.home.commonGame.part.player.Player;
import com.home.commonGame.scene.base.GameScene;
import com.home.commonGame.tool.func.IGameMatchTool;
import com.home.shine.control.DateControl;
import com.home.shine.control.ThreadControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.collection.IntIntMap;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.LongIntMap;
import com.home.shine.support.func.ObjectCall;
import com.home.shine.utils.MathUtils;

/** 场景控制 */
public class SceneControl
{
	/** 离开当前场景的next信息 */
	private SceneEnterArgData _leaveSceneEnterArg;
	
	/** 角色分线字典 */
	private LongIntMap _playerLineDic=new LongIntMap();
	/** 分线人数字典 */
	private IntIntMap _lineNumDic=new IntIntMap();
	
	/** 场景自动分线字典 */
	private final IntObjectMap<SceneAutoLineCountData> _sceneAutoLineDic=new IntObjectMap<>(SceneAutoLineCountData[]::new);
	
	//temp
	
	private int[] _tempCount;
	
	public SceneControl()
	{
		_tempCount=new int[ShineSetting.poolThreadNum];
	}
	
	/** 初始化 */
	public void init()
	{
		ThreadControl.getMainTimeDriver().setInterval(this::onSecond,1000);
		
		if(CommonSetting.serverMapNeedRecast)
		{
			MapInfoConfig.getDic().forEachValue(v->
			{
				if(v.recast.bytes!=null && v.recast.bytes.length>0)
				{
					//注册地图
					ExternMethodNative.registMap(v.id,v.recast.bytes);
				}
			});
		}
		
		_leaveSceneEnterArg=BaseC.factory.createSceneEnterArgData();
		SceneLocationData sceneLocationData=new SceneLocationData();
		sceneLocationData.sceneID=-1;
		sceneLocationData.instanceID=-1;
		sceneLocationData.serverID=-1;
		_leaveSceneEnterArg.location=sceneLocationData;
	}
	
	/** 析构 */
	public void dispose()
	{
	
	}
	
	public SceneEnterArgData getLeaveSceneEnterArgData()
	{
		return _leaveSceneEnterArg;
	}
	
	/** 是否是离开场景的标记数据 */
	public boolean isLeaveSceneEnterArg(SceneEnterArgData data)
	{
		SceneLocationData location=data.location;
		return location.instanceID==-1 && location.sceneID==-1 && location.serverID==-1;
	}
	
	protected void onSecond(int delay)
	{
		_sceneAutoLineDic.forEachValue(v->
		{
			v.onSecond();
		});
	}
	
	/** 获取匹配插件 */
	private IGameMatchTool getMatchToolBase(int funcID)
	{
		return GameC.global.func.getMatchToolBase(funcID);
	}
	
	/** 获取自动分享场景数据 */
	private SceneAutoLineCountData getAutoLineData(int sceneID)
	{
		SceneAutoLineCountData data;
		
		if((data=_sceneAutoLineDic.get(sceneID))==null)
		{
			synchronized(_sceneAutoLineDic)
			{
				if((data=_sceneAutoLineDic.get(sceneID))==null)
				{
					_sceneAutoLineDic.put(sceneID,data=new SceneAutoLineCountData());
					data.lineMax=Global.autoLinedSceneCapacity;
				}
			}
		}
		
		return data;
	}
	
	/** 获取单例场景执行器号 */
	public void makeSingleSceneExecutorIndex(Player player,SceneLocationData data)
	{
		SceneConfig config=SceneConfig.get(data.sceneID);
		
		switch(config.instanceType)
		{
			case SceneInstanceType.SingleInstance:
			{
				data.executorIndex=GameC.scene.getSingleInstanceSceneExecutorIndex(data.sceneID);
			}
				break;
			case SceneInstanceType.LinedSingleInstance:
			{
				data.executorIndex=GameC.scene.getLinedSingleInstanceSceneExecutorIndex(data.sceneID,data.lineID);
			}
				break;
			case SceneInstanceType.AutoLinedScene:
			{
				GameC.scene.makeAutoLinedSingleInstanceSceneLineID(player,data);
				if(data.executorIndex==-1)
				{
					data.executorIndex=GameC.scene.getAutoLinedSingleInstanceSceneExecutorIndexByLineID(data.sceneID,data.lineID);
				}
			}
				break;
			default:
			{
				data.executorIndex=MathUtils.randomInt(ShineSetting.poolThreadNum);
			}
		}
	}
	
	/** 获取单例场景执行器号(主线程/逻辑线程) */
	public int getSingleInstanceSceneExecutorIndex(int sceneID)
	{
		return sceneID & ThreadControl.poolThreadNumMark;
	}
	
	/** 获取单例场景执行器号(主线程/逻辑线程) */
	public int getLinedSingleInstanceSceneExecutorIndex(int sceneID,int lineID)
	{
		//没有都按0算
		if(lineID<0)
			lineID=0;
		
		return ((sceneID * Global.sceneLineNum)+lineID) & ThreadControl.poolThreadNumMark;
	}
	
	/** 获取单例场景执行器号(主线程/逻辑线程) */
	public void makeAutoLinedSingleInstanceSceneLineID(Player player,SceneLocationData data)
	{
		data.lineID=-1;
		//角色自身判定
		player.scene.makeAutoLinedSceneLocation(data);
		//依旧没有
		if(data.lineID==-1)
		{
			data.lineID=getAutoLineData(data.sceneID).nowIndex;
		}
	}
	
	/** 获取单例场景执行器号(主线程/逻辑线程) */
	public int getAutoLinedSingleInstanceSceneExecutorIndexByLineID(int sceneID,int lineID)
	{
		return ((sceneID * CommonSetting.sceneAutoLineMax)+lineID) & ThreadControl.poolThreadNumMark;
	}
	
	/** 记录自动分线场景(主线程) */
	public void addAutoLinedScenePreOne(long playerID,int sceneID,int lineID)
	{
		SceneAutoLineCountData aData=getAutoLineData(sceneID);
		aData.addOne(playerID,lineID,true);
	}
	
	/** 记录自动分线场景(主线程) */
	public void addAutoLinedSceneOne(long playerID,int sceneID,int lineID)
	{
		SceneAutoLineCountData aData=getAutoLineData(sceneID);
		aData.addOne(playerID,lineID,false);
	}
	
	/** 移除自动分线场景(主线程) */
	public void removeAutoLinedSceneOne(long playerID,int sceneID,int lineID)
	{
		SceneAutoLineCountData aData=getAutoLineData(sceneID);
		aData.removeOne(lineID);
	}
	
	/** 获取多实例场景执行器号(主线程) */
	public int getMultiInstanceSceneExecutorIndex()
	{
		//TODO:这里人数可以做缓存
		
		//找到人数最少的执行器
		
		int index=0;
		int num=Integer.MAX_VALUE;
		int temp;
		
		for(int i=ShineSetting.poolThreadNum-1;i>=0;--i)
		{
			if((temp=GameC.main.getExecutor(i).getPlayerNum())<num)
			{
				num=temp;
				index=i;
			}
		}
		
		return index;
	}
	
	/** 获取最多的执行器号(没有返回-1) */
	private int getMostExecutorIndex(RoleShowData[] signedPlayers)
	{
		int[] tempCount=_tempCount;
		
		Player player;
		LogicExecutor executor;
		
		for(RoleShowData v:signedPlayers)
		{
			if((player=GameC.main.getPlayerByID(v.playerID))!=null)
			{
				if((executor=player.getExecutor())!=null)
				{
					tempCount[executor.getIndex()]++;
				}
			}
		}
		
		int min=signedPlayers.length>>1;//一半
		
		int re=-1;
		
		for(int i=tempCount.length-1;i>=0;--i)
		{
			if(tempCount[i]>=min)
			{
				re=i;
			}
			
			//归零
			tempCount[i]=0;
		}
		
		return re;
	}
	
	/** 创建多实例场景 */
	public void createMultiInstanceScene(CreateSceneData data,ObjectCall<SceneLocationData> overFunc)
	{
		int eIndex=getMultiInstanceSceneExecutorIndex();
		
		LogicExecutor executor=GameC.main.getExecutor(eIndex);
		
		//到对应场景线程
		executor.addFunc(()->
		{
			GameScene scene=executor.createScene(data);
			SceneLocationData eData=scene.createLocationData();
			
			ThreadControl.addMainFunc(()->
			{
				overFunc.apply(eData);
			});
		});
	}
	
	/** 创建指定场景(主线程) */
	private void createSignedScene(CreateSceneData createData,RoleShowData[] signedPlayers,ObjectCall<SceneLocationData> overFunc)
	{
		int eIndex;
		
		//找执行器ID
		if((eIndex=getMostExecutorIndex(signedPlayers))==-1)
		{
			eIndex=getMultiInstanceSceneExecutorIndex();
		}
		
		LogicExecutor executor=GameC.main.getExecutor(eIndex);
		
		//到对应场景线程
		executor.addFunc(()->
		{
			GameScene scene=executor.createScene(createData);
			SceneLocationData eData=scene.createLocationData();
			
			scene.gameInOut.setSignedPlayers(signedPlayers);
			
			ThreadControl.addMainFunc(()->
			{
				overFunc.apply(eData);
			});
		});
	}
	
	/** 申请匹配(逻辑线程) */
	public void applyMatch(Player player,int funcID)
	{
		//正匹配中
		if(player.scene.isMatching())
		{
			player.warningInfoCode(InfoCodeType.ApplyMatch_playerInMatching);
			return;
		}
		
		//正切换中
		if(player.scene.isSwitching())
		{
			player.warningInfoCode(InfoCodeType.ApplyMatch_playerIsChangingScene);
			return;
		}
		
		IGameMatchTool tool=getMatchToolBase(funcID);
		
		//不可匹配
		if(!tool.checkCanMatch(player))
		{
			player.warningInfoCode(InfoCodeType.ApplyMatch_playerCanNotMatch);
			return;
		}
		
		//同时设置匹配中
		player.scene.setMatchingFuncID(funcID);
		
		player.addMainFunc(()->
		{
			tool.addPlayer(player);
		});
	}
	
	/** 取消匹配(逻辑线程) */
	public void cancelMatch(Player player,int funcID)
	{
		if(!player.scene.isMatching())
		{
			player.warningInfoCode(InfoCodeType.CancelMatch_playerNotInMatching);
			return;
		}
		
		if(player.scene.getMatchingFuncID()!=funcID)
		{
			player.warningInfoCode(InfoCodeType.CancelMatch_playerMatchFuncError,funcID);
			return;
		}
		
		player.addMainFunc(()->
		{
			IGameMatchTool tool=getMatchToolBase(funcID);
			
			//没有该功能的
			if(tool==null)
			{
				player.warningInfoCode(InfoCodeType.CancelMatch_playerMatchFuncNotExist);
				return;
			}
			
			boolean re=tool.cancelMatch(player.role.playerID);
			
			//还在线
			if(player.system.isStateOnline())
			{
				player.addFunc(()->
				{
					player.scene.setMatchingFuncID(-1);
					
					if(re)
					{
						player.send(FuncCancelMatchRequest.create(funcID));
					}
				});
			}
		});
	}
	
	/** 接受匹配(逻辑线程) */
	public void acceptMatch(Player player,int funcID,int index)
	{
		if(!player.scene.isMatching())
		{
			player.warningInfoCode(InfoCodeType.AcceptMatch_playerNotInMatching);
			return;
		}
		
		if(player.scene.getMatchingFuncID()!=funcID)
		{
			player.warningInfoCode(InfoCodeType.AcceptMatch_playerMatchFuncError,funcID);
			return;
		}
		
		player.addMainFunc(()->
		{
			IGameMatchTool tool=getMatchToolBase(funcID);
			
			//没有该功能的
			if(tool==null)
			{
				player.warningInfoCode(InfoCodeType.AcceptMatch_playerMatchFuncNotExist);
				return;
			}
			
			if(!player.scene.isMatching())
			{
				player.warningInfoCode(InfoCodeType.AcceptMatch_playerNotInMatching);
				return;
			}
			
			tool.acceptMatch(index,player.role.playerID);
		});
	}
	
	/** 匹配一组(主线程) */
	public void onMatchOnce(int funcID,PlayerMatchData[] matches)
	{
		int sceneID=GameC.global.func.getMatchTool(funcID).getMatchSceneID();
		
		SceneConfig config=SceneConfig.get(sceneID);
		
		//不是多人副本
		if(!BaseC.constlist.sceneInstance_isMultiBattle(config.instanceType))
		{
			Ctrl.throwError("不支持的场景类型(不是多人副本):" + sceneID);
			return;
		}
		
		RoleShowData[] signedPlayers=new RoleShowData[matches.length];
		
		for(int i=matches.length-1;i>=0;--i)
		{
			signedPlayers[i]=matches[i].showData;
		}
		
		CreateSceneData createSceneData=BaseC.factory.createCreateSceneData();
		createSceneData.sceneID=sceneID;
		
		//创建指定场景
		createSignedScene(createSceneData,signedPlayers,eData->
		{
			MatchSceneData data=BaseC.factory.createMatchSceneData();
			data.funcID=funcID;
			data.matchTime=DateControl.getTimeMillis();
			data.location=eData;
			
			for(RoleShowData v:signedPlayers)
			{
				PlayerMatchSuccessWData wData=new PlayerMatchSuccessWData();
				wData.data=data;
				
				GameC.main.addPlayerOfflineWork(v.playerID,wData);
			}
		});
	}
	
	/** 创建指定场景for中心服 */
	public void createSignedSceneByCenter(int index,CreateSceneData createData,RoleShowData[] signedPlayers)
	{
		createSignedScene(createData,signedPlayers,eData->
		{
			ReCreateSignedSceneToCenterServerRequest.create(index,eData).send();
		});
	}
	
	/** 查询某场景ID是否是匹配场景(默认全是） */
	protected boolean isMatchingSceneByCenter(int sceneID)
	{
		//TODO:补充匹配场景判定
		return true;
	}
}
