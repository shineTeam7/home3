package com.home.commonManager.control;

import com.home.commonBase.constlist.system.ManagerCommandType;
import com.home.commonBase.data.login.CenterInitServerData;
import com.home.commonBase.data.login.GameInitServerData;
import com.home.commonBase.data.login.LoginInitServerData;
import com.home.commonBase.data.login.SceneInitServerData;
import com.home.commonBase.data.system.GameServerClientSimpleData;
import com.home.commonBase.data.system.GameServerInfoData;
import com.home.commonBase.data.system.GameServerSimpleInfoData;
import com.home.commonBase.data.system.ServerInfoData;
import com.home.commonBase.data.system.ServerSimpleInfoData;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.table.table.ServerTable;
import com.home.commonManager.dataEx.GMClientUser;
import com.home.commonManager.global.ManagerC;
import com.home.commonManager.net.serverRequest.center.CenterExitServerRequest;
import com.home.commonManager.net.serverRequest.center.ManagerToCenterCommandServerRequest;
import com.home.commonManager.net.serverRequest.center.ReloadServerConfigToCenterServerRequest;
import com.home.commonManager.net.serverRequest.game.HotfixToGameServerRequest;
import com.home.commonManager.net.serverRequest.game.ReloadServerConfigToGameServerRequest;
import com.home.commonManager.net.serverRequest.login.HotfixToLoginServerRequest;
import com.home.commonManager.net.serverRequest.login.LoginExitServerRequest;
import com.home.commonManager.net.serverRequest.login.ManagerToLoginCommandServerRequest;
import com.home.commonManager.net.serverRequest.login.ReloadServerConfigToLoginServerRequest;
import com.home.commonManager.net.serverRequest.login.SendServerOpenToLoginServerRequest;
import com.home.commonManager.net.serverRequest.scene.SceneExitServerRequest;
import com.home.shine.ShineSetup;
import com.home.shine.constlist.SocketType;
import com.home.shine.control.ThreadControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.net.socket.BaseSocket;
import com.home.shine.serverConfig.ServerConfig;
import com.home.shine.support.collection.IntIntMap;
import com.home.shine.support.collection.IntList;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.IntSet;
import com.home.shine.support.collection.SList;
import com.home.shine.support.collection.SMap;
import com.home.shine.table.BaseTable;
import com.home.shine.utils.MathUtils;

import java.util.Arrays;

public class ManagerMainControl
{
	/** 当前是否开放 */
	private boolean _isOpen;
	
	//server相关
	private ServerSimpleInfoData _centerInfo;
	/** 登录服列表 */
	private IntObjectMap<ServerInfoData> _loginInfoDic=new IntObjectMap<>(ServerInfoData[]::new);
	/** 登陆服简版信息列表 */
	private IntObjectMap<ServerSimpleInfoData> _loginSimpleInfoDic=new IntObjectMap<>(ServerSimpleInfoData[]::new);
	/** 登陆服id列表 */
	private int[] _loginList;
	
	/** 场景服列表 */
	private IntObjectMap<ServerInfoData> _sceneInfoDic=new IntObjectMap<>(ServerInfoData[]::new);
	/** 场景服简版信息列表 */
	private IntObjectMap<ServerSimpleInfoData> _sceneSimpleInfoDic=new IntObjectMap<>(ServerSimpleInfoData[]::new);
	
	/** 游戏服列表 */
	private IntObjectMap<GameServerInfoData> _gameInfoDic=new IntObjectMap<>(GameServerInfoData[]::new);
	/** 游戏服简版信息列表 */
	private IntObjectMap<GameServerSimpleInfoData> _gameSimpleInfoDic=new IntObjectMap<>(GameServerSimpleInfoData[]::new);
	/** 游戏服客户端简版列表 */
	private IntObjectMap<GameServerClientSimpleData> _gameClientSimpleDic=new IntObjectMap<>(GameServerClientSimpleData[]::new);
	
	/** 区服老新对照表(areaID:gameID) */
	private IntIntMap _areaDic=new IntIntMap();
	/** 区服list(value:areaID) */
	private int[] _areaList;
	
	///** 游戏服运行组 */
	//private IntObjectMap<GameServerRunData> _gameRunDic=new IntObjectMap<>(GameServerRunData[]::new);
	/** 游戏服list(key:gameID) */
	private IntList _gameList=new IntList();
	
	//gm相关
	
	private SMap<String,GMClientUser> _userDic=new SMap<>();
	
	private IntObjectMap<GMClientUser> _userDicByToken=new IntObjectMap<>(GMClientUser[]::new);
	
	public void init()
	{
		initConfigs();
		
		_isOpen=ManagerC.setting.isOpenOnStart;
	}
	
	public void dispose()
	{
	
	}
	
	private void initConfigs()
	{
		_centerInfo=new ServerSimpleInfoData();
		_centerInfo.readByConfig(ServerConfig.getCenterConfig());
		
		_loginInfoDic.clear();
		_loginSimpleInfoDic.clear();
		
		IntList loginList=new IntList();
		//login
		ServerConfig.getLoginConfigDic().forEachValue(v->
		{
			ServerInfoData data=new ServerInfoData();
			data.readByConfig(v);
			_loginInfoDic.put(data.id,data);
			
			ServerSimpleInfoData sData=new ServerSimpleInfoData();
			sData.readByConfig(v);
			_loginSimpleInfoDic.put(sData.id,sData);
			
			loginList.add(v.id);
		});
		
		_loginList=loginList.toArray();
		
		_gameInfoDic.clear();
		_areaDic.clear();
		_gameSimpleInfoDic.clear();
		_gameList.clear();
		
		IntSet tempAreaSet=new IntSet();
		
		//game
		ServerConfig.getGameConfigDic().forEachValue(v->
		{
			GameServerInfoData data=new GameServerInfoData();
			data.initDefault();
			data.readByConfig(v);
			
			_gameInfoDic.put(data.id,data);
			
			//先补充自身
			_areaDic.put(data.id,data.id);
			
			GameServerSimpleInfoData simpleData=new GameServerSimpleInfoData();
			simpleData.initDefault();
			simpleData.readByConfig(v);
			
			_gameSimpleInfoDic.put(simpleData.id,simpleData);
			
			//TODO:未完待续，补充areaList
			
			GameServerClientSimpleData sData=new GameServerClientSimpleData();
			sData.id=data.id;
			sData.readByConfig(v);
			_gameClientSimpleDic.put(sData.id,sData);
			
			_gameList.add(data.id);
			
			//GameServerRunData rData=new GameServerRunData();
			//rData.id=data.id;
			//rData.onlineNum=0;
			//
			//_gameRunDic.put(rData.id,rData);
			
			tempAreaSet.add(data.id);
		});
		
		_gameList.sort();
		
		//gameInfo
		SList<BaseTable> list=new ServerTable().loadAllSync(ManagerC.db.getConnect());
		
		list.forEach(v->
		{
			ServerTable st=(ServerTable)v;
			
			GameServerInfoData data=_gameInfoDic.get(st.nowAreaID);
			
			if(data==null)
			{
				Ctrl.throwError("未找到区服ID为:" + st.nowAreaID + "的区服配置");
				return;
			}
			
			data.areaIDList.add(st.areaID);
			//simple也补
			_gameSimpleInfoDic.get(st.nowAreaID).areaIDList.add(st.areaID);
			
			_areaDic.put(st.areaID,st.nowAreaID);
			
			tempAreaSet.add(st.areaID);
		});
		
		_areaList=tempAreaSet.toArray();
		//排序
		Arrays.sort(_areaList);
		
		if(_areaList.length==0)
		{
			Ctrl.throwError("不能没有game服");
			ShineSetup.exit();
			return;
		}
		
		//scene
		if(CommonSetting.useSceneServer)
		{
			_sceneSimpleInfoDic.clear();
			
			ServerConfig.getSceneConfigDic().forEachValue(v->
			{
				ServerInfoData data=new ServerInfoData();
				data.readByConfig(v);
				_sceneInfoDic.put(data.id,data);
				
				ServerSimpleInfoData sData=new ServerSimpleInfoData();
				sData.readByConfig(v);
				_sceneSimpleInfoDic.put(sData.id,sData);
			});
		}
	}
	
	/** 更改服务器开放状态 */
	public void changeOpen(boolean value)
	{
		if(_isOpen==value)
			return;
		
		_isOpen=value;
		
		ManagerC.server.radioLogins(SendServerOpenToLoginServerRequest.create(_isOpen));
	}
	
	public boolean isOpen()
	{
		return _isOpen;
	}
	
	/** 获取区服 */
	public IntIntMap getAreaDic()
	{
		return _areaDic;
	}
	
	//configs
	
	public ServerSimpleInfoData getCenterInfo()
	{
		return _centerInfo;
	}
	
	/** 是否有登录服 */
	public boolean hasLoginServer(int id)
	{
		return _loginInfoDic.contains(id);
	}
	
	/** 登录服信息组 */
	public ServerInfoData getLoginInfo(int id)
	{
		return _loginInfoDic.get(id);
	}
	
	/** 是否有game服 */
	public boolean hasGameServer(int id)
	{
		return _gameInfoDic.contains(id);
	}
	
	/** 游戏服信息组 */
	public GameServerInfoData getGameInfo(int id)
	{
		return _gameInfoDic.get(id);
	}
	
	/** 是否有场景服 */
	public boolean hasSceneServer(int id)
	{
		return _sceneInfoDic.contains(id);
	}
	
	/** 场景服信息组 */
	public ServerInfoData getSceneInfo(int id)
	{
		return _sceneInfoDic.get(id);
	}
	
	/** 重新加载配置(主线程) */
	public void reloadConfig(Runnable func)
	{
		Ctrl.log("reloadManagerConfig");
		
		BaseC.config.reload(this::onReloadConfig,()->
		{
			Ctrl.log("reloadManagerConfigComplete");
			
			ManagerC.server.radioLogins(ManagerToLoginCommandServerRequest.create(ManagerCommandType.ReloadConfig));
			ManagerC.server.getCenterSocket().send(ManagerToCenterCommandServerRequest.create(ManagerCommandType.ReloadConfig));
			
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
			//CenterC.global.onReloadConfig();
		}
	}
	
	/** 重新加载服务器配置(主线程) */
	public void reloadServerConfig()
	{
		Ctrl.log("reloadManagerServerConfig");
		
		ServerConfig.load();
		initConfigs();
		
		ManagerC.server.getCenterSocket().send(ReloadServerConfigToCenterServerRequest.create(createCenterInitData()));
		
		ManagerC.server.getSocketInfo(SocketType.Login).socketDic.forEach((k,v)->
		{
			v.send(ReloadServerConfigToLoginServerRequest.create(createLoginInitData(k)));
		});
		
		ManagerC.server.getSocketInfo(SocketType.Game).socketDic.forEach((k,v)->
		{
			v.send(ReloadServerConfigToGameServerRequest.create(createGameInitData(k)));
		});
	}
	
	/** 客户端版本热更 */
	public void clientHotfix(boolean hasConfig)
	{
		if(hasConfig)
		{
			reloadConfig(()->
			{
				toClientHotFix();
			});
		}
		else
		{
			toClientHotFix();
		}
	}
	
	private void toClientHotFix()
	{
		//读取一下
		ManagerC.setting.load();
		
		ManagerC.server.radioLogins(HotfixToLoginServerRequest.create(ManagerC.setting.clientVersionDic,ManagerC.setting.redirectURLDic));
		ManagerC.server.radioGames(HotfixToGameServerRequest.create(ManagerC.setting.clientVersionDic));
	}
	
	//--gm--//
	
	/** 用户登录 */
	public GMClientUser userLogin(String user,String password)
	{
		//TODO:验证
		
		GMClientUser obj=_userDic.get(user);
		
		if(obj==null)
		{
			obj=new GMClientUser();
			obj.user=user;
			obj.password=password;
			
			_userDic.put(user,obj);
			
			int token;
			while(_userDicByToken.contains(token=MathUtils.getToken()));
			
			obj.token=token;
			_userDicByToken.put(token,obj);
		}
		
		return obj;
	}
	
	public GMClientUser getUserByToken(int token)
	{
		return _userDicByToken.get(token);
	}
	
	/** 关闭服务器 */
	public void exitServer()
	{
		ManagerC.server.getCenterSocket().send(CenterExitServerRequest.create());
		
		ManagerC.server.radioLogins(LoginExitServerRequest.create());
		
		ManagerC.server.radioScenes(SceneExitServerRequest.create());
	}
	
	public LoginInitServerData createLoginInitData(int id)
	{
		ServerInfoData loginInfo=getLoginInfo(id);
		
		if(loginInfo==null)
			return null;
		
		LoginInitServerData initData=new LoginInitServerData();
		initData.info=loginInfo;
		initData.loginServerDic=_loginSimpleInfoDic;
		initData.gameServerDic=_gameSimpleInfoDic;
		initData.games=_gameClientSimpleDic;
		initData.clientVersion=ManagerC.setting.clientVersionDic;
		initData.redirectURLDic=ManagerC.setting.redirectURLDic;
		initData.isOpen=ManagerC.main.isOpen();
		
		return initData;
	}
	
	public GameInitServerData createGameInitData(int id)
	{
		GameServerInfoData gameInfo=getGameInfo(id);
		
		if(gameInfo==null)
			return null;
		
		GameInitServerData initData=new GameInitServerData();
		initData.info=gameInfo;
		initData.centerInfo=getCenterInfo();
		initData.gameServerDic=_gameSimpleInfoDic;
		initData.sceneServerDic=_sceneSimpleInfoDic;
		initData.loginList=_loginList;
		initData.clientVersion=ManagerC.setting.clientVersionDic;
		initData.isOfficial=ManagerC.setting.isOfficial;
		
		return initData;
	}
	
	public CenterInitServerData createCenterInitData()
	{
		CenterInitServerData initData=new CenterInitServerData();
		initData.info=new ServerInfoData();
		initData.info.readByConfig(ServerConfig.getCenterConfig());
		
		initData.areaDic=_areaDic;
		
		return initData;
	}
	
	public SceneInitServerData createSceneInitData(int id)
	{
		ServerInfoData sceneInfo=getSceneInfo(id);
		
		if(sceneInfo==null)
			return null;
		
		SceneInitServerData initData=new SceneInitServerData();
		initData.info=sceneInfo;
		initData.areaDic=_areaDic;
		
		return initData;
	}
}
