package com.home.commonScene.part;

import com.home.commonBase.constlist.generate.FunctionType;
import com.home.commonBase.constlist.generate.RoleShowDataPartType;
import com.home.commonBase.constlist.generate.SceneInstanceType;
import com.home.commonBase.data.role.CharacterSaveData;
import com.home.commonBase.data.role.CharacterUseData;
import com.home.commonBase.data.role.MUnitSaveData;
import com.home.commonBase.data.role.MUnitUseData;
import com.home.commonBase.data.role.RoleShowData;
import com.home.commonBase.data.scene.scene.SceneEnterArgData;
import com.home.commonBase.data.scene.scene.SceneLocationRoleShowChangeData;
import com.home.commonBase.data.scene.scene.SceneServerEnterData;
import com.home.commonBase.data.scene.scene.SceneServerExitData;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.logic.LogicEntity;
import com.home.commonBase.scene.base.Role;
import com.home.commonBase.scene.base.Scene;
import com.home.commonBase.scene.base.Unit;
import com.home.commonScene.constlist.system.ScenePlayerLoginStateType;
import com.home.commonScene.control.SceneLogicExecutor;
import com.home.commonScene.global.SceneC;
import com.home.commonScene.logic.unit.SCharacterUseLogic;
import com.home.commonScene.logic.unit.SMUnitUseLogic;
import com.home.commonScene.scene.base.SScene;
import com.home.commonScene.server.SceneReceiveSocket;
import com.home.commonSceneBase.part.IScenePlayer;
import com.home.commonSceneBase.scene.unit.CharacterIdentityLogic;
import com.home.shine.control.ThreadControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.dataEx.AffairTimeLock;
import com.home.shine.dataEx.AffairTimeOut;
import com.home.shine.net.base.BaseRequest;
import com.home.shine.net.socket.BaseSocket;
import com.home.shine.support.concurrent.collection.NatureConcurrentQueue;
import com.home.shine.support.func.ObjectCall;
import com.home.shine.support.pool.IPoolObject;
import com.home.shine.support.pool.PoolObject;

/** 场景服角色 */
public class ScenePlayer extends LogicEntity implements IPoolObject, IScenePlayer
{
	/** 角色ID */
	public long playerID;
	/** 名字 */
	public String name;
	/** 所在gameID */
	public int gameID;
	/** 连接 */
	public SceneReceiveSocket socket;
	/** 连接是否准备好(必须加volatile，不然顶号测试，会在InitClient后发上次登陆消息) */
	private volatile boolean _socketReady=false;
	/** 登录状态 */
	public volatile int loginState=ScenePlayerLoginStateType.None;
	/** 登录令牌 */
	public int token;
	/** 外显数据 */
	public RoleShowData showData;
	/** 进入场景数据 */
	public SceneEnterArgData enterArg;
	/** 进入数据 */
	public SceneServerEnterData enterData;
	/** 角色使用逻辑 */
	public SCharacterUseLogic useLogic;
	/** 所在线程 */
	private int _executorIndex=-1;
	/** 当前执行器 */
	private volatile SceneLogicExecutor _executor;
	/** 预备进入场景id */
	public int preSceneInstanceID=-1;
	/** 是否切换场景中 */
	private AffairTimeLock _switchingLock=new AffairTimeLock();
	
	private SScene _preScene;
	
	/** 进入场景倒计时 */
	private AffairTimeOut _enterSceneTimeOut=new AffairTimeOut(this::enterSceneTimeOut,CommonSetting.enterSceneMaxWaitTime);
	
	/** 是否需要补发预备进入场景 */
	private boolean _needSupplyPreEnterScene;
	
	/** 是否有断线重连进入 */
	private boolean _hasReconnectEnter=false;
	
	/** 当前场景对象 */
	private SScene _scene;
	
	/** 场景角色 */
	private Role _role;
	
	/** 角色单位 */
	private Unit _unit;
	
	@Override
	public void clear()
	{
		dispose();
	}
	
	public void construct()
	{
		useLogic=SceneC.factory.createSCharacterUseLogic();
		useLogic.setPlayer(this);
	}
	
	public void dispose()
	{
		onLeave();
		
		socket=null;
		loginState=ScenePlayerLoginStateType.None;
		_socketReady=false;
	}
	
	public void onLeave()
	{
		//切换false
		setSwitching(false);
		clearPreEnterScene();
		
		_scene=null;
		_unit=null;
	}
	
	/** 设置执行器(切换过程中会置空) */
	public void setExecutor(SceneLogicExecutor executor)
	{
		_executor=executor;
		_executorIndex=(executor!=null ? executor.getIndex() : -1);
	}
	
	/** 获取场景执行器 */
	public SceneLogicExecutor getExecutor()
	{
		return _executor;
	}
	
	/** 是否是当前执行器 */
	public boolean isCurrentExecutor(int index)
	{
		return _executorIndex==index;
	}
	
	public void setSocketReady(boolean value)
	{
		_socketReady=value;
	}
	
	/** 当前状态为空 */
	public boolean isStateNone()
	{
		return loginState==ScenePlayerLoginStateType.None;
	}
	
	/** 添加主线程执行(逻辑线程) */
	public void addMainFunc(Runnable func)
	{
		if(isStateNone())
			return;
		
		ThreadControl.addMainFunc(func);
	}
	
	/** 添加待办事务(主线程)(如是在主线程通过GetPlayer系列取出的Player然后addFunc,则一定会被执行) */
	public void addFunc(Runnable func)
	{
		if(isStateNone())
			return;
		
		SceneLogicExecutor executor=getExecutor();
		
		if(executor!=null)
		{
			ScenePlayerFunc pFunc=new ScenePlayerFunc();
			pFunc.func=func;
			pFunc.index=executor.getIndex();
			
			executor.addFunc(pFunc);
		}
	}
	
	@Override
	public RoleShowData createRoleShowData()
	{
		if(showData!=null)
			return (RoleShowData)showData.clone();
		
		return null;
	}
	
	@Override
	public boolean isSocketReady()
	{
		return !_hasReconnectEnter && _socketReady;
	}
	
	@Override
	public BaseSocket getSocket()
	{
		return socket;
	}
	
	@Override
	public long getPlayerID()
	{
		return playerID;
	}
	
	private class ScenePlayerFunc implements Runnable
	{
		public Runnable func;
		
		public int index;
		
		@Override
		public void run()
		{
			SceneLogicExecutor executor=getExecutor();
			
			if(executor!=null && executor.getIndex()==index)
			{
				func.run();
			}
		}
	}
	
	/** 推送消息 */
	public void send(BaseRequest request)
	{
		if(socket==null)
			return;
		
		if(!_socketReady)
			return;
		
		socket.send(request);
	}
	
	@Override
	public void writeInfo(StringBuilder sb)
	{
		sb.append("playerID:");
		sb.append(playerID);
		//sb.append(" playerID:");
		//sb.append(role.playerID);
		//sb.append(" 名字:");
		//sb.append(role.name);
		//sb.append(" 源服:");
		//sb.append(role.sourceGameID);
		//sb.append(" socketID:");
		//sb.append(system.socket!=null ? system.socket.id : -1);
	}
	
	@Override
	protected void sendWarnLog(String str)
	{
		if(CommonSetting.needSendWarningLog)
		{
			//send(SendWarningLogRequest.create(str));
		}
	}
	
	public void onSecond(int delay)
	{
	
	}
	
	/** 场景丢失接口 */
	public void sceneMiss()
	{
		warnLog("场景miss");
		
		clearNowScene();
		
		//关了切换标记
		setSwitching(false);
		
		//TODO:实现
		//backToTown();
	}
	
	/** 进入准备好的预备场景 */
	public void enterPreScene(SceneServerEnterData data)
	{
		setSocketReady(true);
		
		if(preSceneInstanceID<=0)
		{
			sceneMiss();
			return;
		}
		
		SceneLogicExecutor executor=getExecutor();
		
		if(executor==null)
		{
			sceneMiss();
			return;
		}
		
		SScene scene=executor.getScene(preSceneInstanceID);
		
		if(scene==null)
		{
			sceneMiss();
			return;
		}
		
		enterData=data;
		
		initUseLogic();
		
		scene.sInOut.playerPreEnter(this);
	}
	
	protected void initUseLogic()
	{
		useLogic.init(enterData.hero);
	}
	
	/** 离开当前场景(逻辑线程) */
	public void leaveNowScene()
	{
		if(loginState==ScenePlayerLoginStateType.Exiting)
			return;
		
		loginState=ScenePlayerLoginStateType.Exiting;
		
		getExecutor().playerExit(this);
		
		addMainFunc(()->
		{
			SceneC.main.playerExitLast(this);
		});
	}
	
	public void setPreScene(SScene scene)
	{
		_preScene=scene;
		
		//计时
		_enterSceneTimeOut.start();
	}
	
	/** 进入场景超时 */
	private void enterSceneTimeOut()
	{
		//直接ready
		sceneReady();
	}
	
	/** 清除预进入场景信息 */
	public void clearPreEnterScene()
	{
		_preScene=null;
		_enterSceneTimeOut.stop();
		_needSupplyPreEnterScene=false;
	}
	
	public boolean needSupplyPreEnterScene()
	{
		return _needSupplyPreEnterScene;
	}
	
	public void setNeedSupplyPreEnterScene(boolean value)
	{
		_needSupplyPreEnterScene=value;
	}
	
	/** 标记此次重连进入 */
	public void setReconnectEnter(boolean value)
	{
		_hasReconnectEnter=value;
	}
	
	/** 是否有预备场景 */
	public SScene getPreScene()
	{
		return _preScene;
	}
	
	/** 客户端场景ready */
	public void sceneReady()
	{
		//不在切换中
		if(_preScene==null)
		{
			warnLog("没有预备场景");
			return;
		}
		
		//场景已被移除
		SScene scene=getExecutor().getScene(_preScene.instanceID);
		
		if(scene==null)
		{
			clearPreEnterScene();
			sceneMiss();
			return;
		}
		
		scene=_preScene;
		
		//重连进入
		if(_hasReconnectEnter)
		{
			_hasReconnectEnter=false;
			
			clearPreEnterScene();
			setSwitching(false);
			
			//重连进入
			scene.sInOut.playerEnterForReconnect(this);
		}
		else
		{
			clearPreEnterScene();
			setSwitching(false);
			
			scene.sInOut.playerEnter(this);
		}
	}
	
	/** 清空当前场景的索引 */
	public void clearNowScene()
	{
		////没有下一个了
		//if(!hasNext)
		//{
		//	clearCurrentSceneInfo();
		//
		//	//清空
		//	//推送消息
		//	if(CommonSetting.needSyncRoleSocialSceneLocation)
		//	{
		//		SceneLocationRoleShowChangeData data=new SceneLocationRoleShowChangeData();
		//		data.type=RoleShowDataPartType.Location;
		//		me.social.refreshRoleSocialData(data);
		//	}
		//}
		//
		//if(!CommonSetting.useSceneServer)
		//{
		//	if(_scene!=null)
		//	{
		//		//自动分线场景
		//		if(_scene.getConfig().instanceType==SceneInstanceType.AutoLinedScene)
		//		{
		//			long playerID=me.role.playerID;
		//			int sceneID=_scene.getSceneID();
		//			int lineID=_scene.getLineID();
		//
		//			me.addMainFunc(()->
		//			{
		//				GameC.scene.removeAutoLinedSceneOne(playerID,sceneID,lineID);
		//			});
		//		}
		//	}
		//
		//	_scene=null;
		//	_sceneLocation=null;
		//	_role=null;
		//	_unit=null;
		//}
		//
		//_hasNextScene=hasNext;
		
		_hasReconnectEnter=false;
	}
	
	/** 检查是否立即进入场景 */
	public void checkPreEnter()
	{
		if(socket!=null && !socket.isConnectForLogic())
		{
			sceneReady();
		}
	}
	
	/** 获取场景 */
	public SScene getScene()
	{
		return _scene;
	}
	
	/** 获取场景角色 */
	public Role getRole()
	{
		return _role;
	}
	
	/** 获取当前的主角(池线程) */
	public Unit getUnit()
	{
		return _unit;
	}
	
	/** 重连后(socket处理) */
	public void onReconnect()
	{
		if(_scene!=null)
		{
			Unit unit=_scene.getCharacterByPlayerID(playerID);
			//连接重置
			((CharacterIdentityLogic)unit.identity).onSocketReplace();
		}
	}
	
	/** 绑定场景 */
	public void setScene(SScene scene)
	{
		if(_scene!=null)
		{
			errorLog("绑定场景时，有旧值");
		}
		
		_scene=scene;
	}
	
	/** 绑定场景角色 */
	public void setRole(Role role)
	{
		_role=role;
	}
	
	/** 绑定单位 */
	public void setUnit(Unit unit)
	{
		_unit=unit;
	}
	
	/** 当前是否切换中(逻辑线程) */
	public boolean isSwitching()
	{
		return _switchingLock.isLocking();
	}
	
	/** 设置切换状态(逻辑线程) */
	public void setSwitching(boolean bool)
	{
		if(bool)
		{
			_switchingLock.lockOn();
		}
		else
		{
			_switchingLock.unlock();
		}
	}
	
	public SceneServerExitData createSceneServerExitData()
	{
		SceneServerExitData re=SceneC.factory.createSceneServerExitData();
		re.hero=null;//TODO:目前不做cache
		
		return re;
	}
	
	
	/** 通过保存数据，创建使用数据 */
	public CharacterUseData createCharacterUseDataBySaveData(int id,int level)
	{
		CharacterUseData data=BaseC.factory.createCharacterUseData();
		data.initDefault();
		
		//1级
		data.mIndex=1;
		data.id=id;
		data.level=level;
		
		return data;
	}
	
	/** 通过保存数据初始化 */
	public void initUseLogicByUseData(SMUnitUseLogic logic,MUnitUseData useData,boolean needBindPlayer)
	{
		//基础部分
		logic.init(useData);
		
		//补满血蓝
		logic.getAttributeLogic().fillHpMp();
		//刷属性
		logic.getAttributeLogic().refreshAttributes();
		
		if(needBindPlayer)
		{
			logic.setPlayer(this);
		}
	}
}
