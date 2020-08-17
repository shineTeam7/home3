package com.home.commonCenter.control;

import com.home.commonBase.config.game.SceneConfig;
import com.home.commonBase.constlist.generate.SceneInstanceType;
import com.home.commonBase.data.role.RoleShowData;
import com.home.commonBase.data.scene.match.MatchSceneData;
import com.home.commonBase.data.scene.match.PlayerMatchSuccessWData;
import com.home.commonBase.data.scene.scene.CreateSceneData;
import com.home.commonBase.data.scene.scene.SceneEnterArgData;
import com.home.commonBase.data.scene.match.PlayerMatchData;
import com.home.commonBase.data.scene.scene.SceneLocationData;
import com.home.commonBase.data.system.GameServerRunData;
import com.home.commonBase.global.BaseC;
import com.home.commonCenter.global.CenterC;
import com.home.commonCenter.net.serverRequest.game.scene.CreateSignedSceneToGameServerRequest;
import com.home.commonCenter.net.serverRequest.game.scene.EnterSignedSceneToGameServerRequest;
import com.home.commonCenter.tool.func.CenterMatchTool;
import com.home.shine.control.DateControl;
import com.home.shine.control.ThreadControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.collection.IntIntMap;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.func.IntCall2;
import com.home.shine.support.func.ObjectCall;

/** 中心服场景控制 */
public class CenterSceneControl
{
	/** 指定场景创建组 */
	private IntObjectMap<SignedSceneCreateData> _signedSceneCreateDic=new IntObjectMap<>(SignedSceneCreateData[]::new);
	/** 指定场景创建序号 */
	private int _signedSceneCreateIndex=0;
	
	private IntIntMap _tempCount=new IntIntMap();
	
	public void init()
	{
		ThreadControl.getMainTimeDriver().setInterval(this::onSecond,1000);
	}
	
	public void dispose()
	{
	
	}
	
	private void onSecond(int delay)
	{
		IntObjectMap<SignedSceneCreateData> signedSceneCreateDic=_signedSceneCreateDic;
		
		signedSceneCreateDic.forEachValueS(v->
		{
			if((--v.waitTime)<=0)
			{
				signedSceneCreateDic.remove(v.index);
			}
		});
	}
	
	/** 匹配一组(主线程) */
	public void onMatchOnce(CenterMatchTool tool,PlayerMatchData[] matches)
	{
		int sceneID=tool.getMatchSceneID();
		
		SceneConfig config=SceneConfig.get(sceneID);
		
		//不是多人副本
		if(!BaseC.constlist.sceneInstance_isMultiBattle(config.instanceType))
		{
			Ctrl.throwError("不支持的场景类型:" + sceneID);
			return;
		}
		
		RoleShowData[] signedPlayers=new RoleShowData[matches.length];
		
		for(int i=matches.length-1;i>=0;--i)
		{
		    signedPlayers[i]=matches[i].showData;
		}
		
		int temp;
		
		//找到gameID
		if((temp=getMostGameID(signedPlayers))==-1)
		{
			temp=getMultiInstanceSceneGameID();
		}
		
		//if(CommonSetting.isTest)
		//{
		//	temp=MathUtils.randomInt(ServerConfig.getGameConfigDic().size())+1;
		//}
		
		int gameID=temp;
		
		CreateSceneData createData=BaseC.factory.createCreateSceneData();
		createData.sceneID=sceneID;
		
		//创建场景
		createSignedScene(gameID,createData,signedPlayers,eData->
		{
			MatchSceneData data=BaseC.factory.createMatchSceneData();
			data.funcID=tool.getFuncID();
			data.matchTime=DateControl.getTimeMillis();
			data.location=eData;
			
			for(RoleShowData v:signedPlayers)
			{
				PlayerMatchSuccessWData wData=new PlayerMatchSuccessWData();
				wData.data=data;
				
				CenterC.main.addPlayerOfflineWork(v.playerID,wData);
			}
		});
	}
	
	/** 获取最多的gameID(主线程)(场景用) */
	public int getMostGameID(RoleShowData[] signedPlayers)
	{
		IntIntMap tempCount=_tempCount;
		tempCount.clear();
		
		//一半
		int min=signedPlayers.length>>1;
		
		int gameID;
		
		for(RoleShowData v:signedPlayers)
		{
			//过半
			if(tempCount.addValue(gameID=CenterC.main.getNowGameID(v.createAreaID),1)>=min)
			{
				return gameID;
			}
		}
		
		return -1;
	}
	
	/** 获取多实例场景分配的gameID(主线程) */
	public int getMultiInstanceSceneGameID()
	{
		int gameID=-1;
		int num=Integer.MAX_VALUE;
		int temp;
		
		GameServerRunData[] values;
		GameServerRunData v;
		
		for(int i=(values=CenterC.main.getGameRunDic().getValues()).length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				if((temp=v.onlineNum)<num)
				{
					num=temp;
					gameID=v.id;
				}
			}
		}
		
		if(gameID==-1)
		{
			Ctrl.throwError("不能没有game服");
		}
		
		return gameID;
	}
	
	/** 创建指定场景 */
	public void createSignedScene(int gameID,CreateSceneData createData,RoleShowData[] signedPlayers,ObjectCall<SceneLocationData> overFunc)
	{
		SignedSceneCreateData data=new SignedSceneCreateData();
		
		int index=data.index=++_signedSceneCreateIndex;
		data.gameID=gameID;
		data.waitTime=ShineSetting.affairDefaultExecuteTime;
		data.signedPlayers=signedPlayers;
		data.overFunc=overFunc;
		
		_signedSceneCreateDic.put(index,data);
		
		CreateSignedSceneToGameServerRequest.create(index,createData,signedPlayers).send(gameID);
	}
	
	/** 回复创建指定场景 */
	public void reCreateSignedScene(int index,SceneLocationData eData)
	{
		SignedSceneCreateData data;
		
		if((data=_signedSceneCreateDic.get(index))==null)
		{
			Ctrl.warnLog("创建指定场景数据丢失");
			return;
		}
		
		_signedSceneCreateDic.remove(index);
		
		data.overFunc.apply(eData);
	}
	
	/** 指定场景创建数据 */
	private class SignedSceneCreateData
	{
		/** 序号 */
		public int index;
		/** 等待时间(s) */
		public int waitTime;
		/** 游戏服ID */
		public int gameID;
		/** 单位数据组 */
		public RoleShowData[] signedPlayers;
		/** 完成回调 */
		public ObjectCall<SceneLocationData> overFunc;
	}
}
