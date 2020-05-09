package com.home.commonLogin.control;

import com.home.commonBase.constlist.generate.ClientLoginHttpResultType;
import com.home.commonBase.constlist.generate.ClientPlatformType;
import com.home.commonBase.constlist.generate.FlowStepType;
import com.home.commonBase.constlist.generate.InfoCodeType;
import com.home.commonBase.constlist.generate.PlatformType;
import com.home.commonBase.constlist.system.GameAreaDivideType;
import com.home.commonBase.control.CodeCheckRecord;
import com.home.commonBase.data.login.ClientLoginData;
import com.home.commonBase.data.login.ClientLoginExData;
import com.home.commonBase.data.login.ClientLoginResultData;
import com.home.commonBase.data.login.ClientLoginServerInfoData;
import com.home.commonBase.data.login.ClientVersionData;
import com.home.commonBase.data.system.AreaClientData;
import com.home.commonBase.data.system.AreaServerData;
import com.home.commonBase.data.system.GameServerClientSimpleData;
import com.home.commonBase.data.system.GameServerSimpleInfoData;
import com.home.commonBase.data.system.ServerInfoData;
import com.home.commonBase.data.system.UserWorkData;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.table.table.UserTable;
import com.home.commonBase.table.table.WhiteListTable;
import com.home.commonBase.utils.BaseGameUtils;
import com.home.commonLogin.constlist.login.WhiteListType;
import com.home.commonLogin.global.LoginC;
import com.home.commonLogin.net.httpResponse.ClientLoginHttpResponse;
import com.home.commonLogin.net.httpResponse.ClientLoginSelectHttpResponse;
import com.home.commonLogin.net.serverRequest.game.login.PlayerBindPlatformToGameServerRequest;
import com.home.commonLogin.net.serverRequest.game.login.UserLoginToGameServerRequest;
import com.home.commonLogin.net.serverRequest.game.system.ReceiptUserWorkToGameServerRequest;
import com.home.commonLogin.net.serverRequest.game.system.SendInfoCodeFromLoginServerRequest;
import com.home.commonLogin.net.serverRequest.login.login.ClientLoginTransferServerRequest;
import com.home.commonLogin.net.serverRequest.login.login.ReClientLoginErrorServerRequest;
import com.home.commonLogin.net.serverRequest.login.login.ReClientLoginTransferServerRequest;
import com.home.shine.constlist.SLogType;
import com.home.shine.control.LogControl;
import com.home.shine.control.ThreadControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.dataEx.AffairTimeLock;
import com.home.shine.dataEx.LogInfo;
import com.home.shine.dataEx.VBoolean;
import com.home.shine.global.ShineSetting;
import com.home.shine.net.httpResponse.BytesHttpResponse;
import com.home.shine.net.socket.BaseSocket;
import com.home.shine.support.SnowFlaker;
import com.home.shine.support.collection.IntIntMap;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.LongObjectMap;
import com.home.shine.support.collection.LongSet;
import com.home.shine.support.collection.SList;
import com.home.shine.support.collection.SMap;
import com.home.shine.support.collection.SSet;
import com.home.shine.support.func.ObjectCall;
import com.home.shine.table.BaseTable;
import com.home.shine.tool.TableOperateTool;
import com.home.shine.utils.MathUtils;

/** 登录主控制 */
public class LoginMainControl
{
	/** 雪花id */
	private SnowFlaker _snowFalker;
	
	/** 游戏服组 */
	private IntObjectMap<GameServerClientSimpleData> _gameInfos;
	/** 客户端版本 */
	private IntObjectMap<ClientVersionData> _clientVersion;
	/** url重定向组 */
	private IntObjectMap<IntObjectMap<String>> _redirectURLDic;
	
	//areas
	
	/** 区服数据组 */
	private IntObjectMap<AreaServerData> _areaServerDic=new IntObjectMap<>(AreaServerData[]::new);
	/** 区服list(value:areaID) */
	private int[] _areaList;
	/** 可注册的区服ID */
	private int _canRegistArea=-1;
	
	/** 国家信息组 */
	private IntObjectMap<CountryInfo> _countryInfos=new IntObjectMap<>(CountryInfo[]::new);
	
	
	//login
	
	/** 正在登录的玩家字典(key:uid) */
	private SMap<String,LoginTempData> _userLoginDic=new SMap<>();
	/** 正在登录的玩家字典(key:userID) */
	private LongObjectMap<LoginTempData> _userLoginDicByUserID=new LongObjectMap<>();
	/** 正在登录的玩家字典(key:loginToken) */
	private IntObjectMap<LoginTempData> _userLoginDicByToken=new IntObjectMap<>();
	
	//白名单
	
	/** ip白名单 */
	private SSet<String> _whiteListForIP=new SSet<>();
	/** uid白名单 */
	private SSet<String> _whiteListForUID=new SSet<>();
	
	/** 是否开放 */
	private boolean _isOpen;
	
	/** 流程日志 */
	private LogInfo _flowLog=new LogInfo();
	
	//user操作
	/** user表操作队列 */
	private TableOperateTool<Long,UserTable> _userTableTool;
	
	public void init()
	{
		//id
		_snowFalker=new SnowFlaker(LoginC.app.id);
		
		ThreadControl.getMainTimeDriver().setInterval(this::onSecond,1000);
	}
	
	public void initNext()
	{
		_userTableTool=new TableOperateTool<>(LoginC.db.getConnect())
		{
			@Override
			protected UserTable makeTable(Long key)
			{
				UserTable table=BaseC.factory.createUserTable();
				table.userID=key;
				return table;
			}
		};
		
		reloadWriteList();
	}
	
	private void reloadWriteList()
	{
		_whiteListForIP.clear();
		_whiteListForUID.clear();
		
		SList<BaseTable> list=BaseC.factory.createWhiteListTable().loadAllSync(LoginC.db.getConnect());
		
		
		if(!list.isEmpty())
		{
			BaseTable[] values=list.getValues();
			WhiteListTable v;
			
			for(int i=0,len=list.size();i<len;++i)
			{
				v=(WhiteListTable)values[i];
				
				if(v.type==WhiteListType.IP)
				{
					_whiteListForIP.add(v.value);
				}
				else if(v.type==WhiteListType.UID)
				{
					_whiteListForUID.add(v.value);
				}
				else
				{
					Ctrl.warnLog("不识别的白名单类型",v.type);
				}
			}
		}
		
	}
	
	public void dispose()
	{
		
	}
	
	private void onSecond(int delay)
	{
		_userLoginDic.forEachValueS(v->
		{
			//到时间了
			if((--v.timeOut)<=0)
			{
				removeUserLogin(v);
			}
		});
	}
	
	/** 设置是否开放 */
	public void setIsOpen(boolean value)
	{
		_isOpen=value;
	}
	
	/** 设置信息组 */
	public void setInfos(IntObjectMap<GameServerClientSimpleData> games)
	{
		_gameInfos=games;
	}
	
	/** 设置客户端版本 */
	public void setClientInfo(IntObjectMap<ClientVersionData> versionDic,IntObjectMap<IntObjectMap<String>> redirectURLDic)
	{
		_clientVersion=versionDic;
		_redirectURLDic=redirectURLDic;
	}
	
	/** 添加区服组数据 */
	public void addAreaServerDic(IntObjectMap<AreaServerData> dic,int gameID)
	{
		Ctrl.log("添加game",gameID,"服信息到login",LoginC.app.id);
		
		dic.forEachValue(v->
		{
			v.gameID=gameID;
			
			GameServerSimpleInfoData gameSimpleInfo=LoginC.server.getGameSimpleInfo(v.gameID);
			
			if(!gameSimpleInfo.isAssist)
			{
				_areaServerDic.put(v.areaID,v);
			}
		});
		
		_areaList=_areaServerDic.getSortedKeyList().toArray();
		findNextRegistArea();
		
		GameServerSimpleInfoData gameSimpleInfo=LoginC.server.getGameSimpleInfo(gameID);
		
		if(CommonSetting.useCountryArea && gameSimpleInfo.countryID>0)
		{
			//添加到国家信息组
			_countryInfos.computeIfAbsent(gameSimpleInfo.countryID,k->new CountryInfo(k)).addAreaServerDic(dic);
		}
	}
	
	/** 获取可用区服 */
	private void findNextRegistArea()
	{
		_canRegistArea=-1;
		
		int lastArea=-1;
		
		for(int v:_areaList)
		{
			lastArea=v;
			
			if(!_areaServerDic.get(v).isLimitRegist)
			{
				_canRegistArea=v;
				break;
			}
		}
		
		if(_canRegistArea==-1 && lastArea!=-1)
		{
			_canRegistArea=lastArea;
			
			Ctrl.errorLog("严重警告，所有服务器已经达到额定注册上限。");
		}
		
	}
	
	/** 获取当前区服ID */
	public int getNowGameID(int areaID)
	{
		AreaServerData aData=_areaServerDic.get(areaID);
		
		if(aData==null)
			return areaID;
		
		return aData.gameID;
	}
	
	/** 通过角色ID获取当前所在源区服 */
	public int getNowGameIDByPlayerID(long playerID)
	{
		return getNowGameID(BaseC.logic.getAreaIDByLogicID(playerID));
	}
	
	/** 客户端app版本 */
	public ClientVersionData getClientVersion(int type)
	{
		return _clientVersion.get(type);
	}
	
	/** 获取game客户端信息 */
	public GameServerClientSimpleData getGameClientInfo(int id)
	{
		return _gameInfos.get(id);
	}
	
	/** 根据资源版本获取重定向URL */
	public String getRedirectURL(int platformType,int resourceVersion)
	{
		if(!CommonSetting.openRedirectURL)
			return "";
		
		IntObjectMap<String> dic=_redirectURLDic.get(platformType);
		
		if(dic==null)
			return "";
		
		String str=dic.get(resourceVersion);
		
		if(str==null)
			str="";
		
		return str;
	}
	
	/** 更新客户端服务器列表load值 */
	public void refreshAreaLoad(IntIntMap dic)
	{
		dic.forEach((k,v)->
		{
			//更新
			_areaServerDic.get(k).load=v;
		});
	}
	
	/** 限制某区服注册 */
	public void limitArea(int areaID,boolean isLimitRegist)
	{
		AreaServerData areaServerData=_areaServerDic.get(areaID);
		
		//限制注册
		areaServerData.isLimitRegist=isLimitRegist;
		
		findNextRegistArea();
		
		GameServerSimpleInfoData gameSimpleInfo=LoginC.server.getGameSimpleInfo(areaServerData.gameID);
		
		if(CommonSetting.useCountryArea && gameSimpleInfo.countryID>0)
		{
			CountryInfo countryInfo=_countryInfos.get(gameSimpleInfo.countryID);
			countryInfo.findCountryNextRegistArea();
		}
	}
	
	//--login--//
	
	/** 检查消息版本 */
	private boolean checkMsgVersion(int cMsgVersion,int gMsgVersion,int httpID)
	{
		BytesHttpResponse response;
		
		if(CommonSetting.needClientMessageVersionCheck)
		{
			//校验c数据版本
			if(cMsgVersion!=CodeCheckRecord.msgDataVersion)
			{
				Ctrl.warnLog("客户端通信结构c校验不匹配",httpID);
				if((response=LoginC.server.getBytesHttpResponse(httpID))!=null)
					response.resultError(ClientLoginHttpResultType.VersionCheckFailed);
				
				return false;
			}
			
			//校验g数据版本
			if(gMsgVersion!=BaseC.config.getMsgDataVersion())
			{
				Ctrl.warnLog("客户端通信结构g校验不匹配",httpID);
				if((response=LoginC.server.getBytesHttpResponse(httpID))!=null)
					response.resultError(ClientLoginHttpResultType.VersionCheckFailed);
				
				return false;
			}
		}
		
		return true;
	}
	
	/** 用户登录(池线程) */
	public void userLogin(int httpID,int cMsgVersion,int gMsgVersion,ClientLoginData data,String ip)
	{
		if(data==null)
			return;

		addFlowLog(data.uid,FlowStepType.ClientLoginHttp);
		
		Ctrl.log("login 用户登录:",data.uid,"httpID:",httpID,"平台类型:",data.clientPlatformType,"设备类型：",data.deviceType,"设备唯一标识:",data.deviceUniqueIdentifier,"ip:",ip);
		
		BytesHttpResponse response;
		
		//当前未开放
		if(!_isOpen)
		{
			boolean can=false;
			
			if(_whiteListForIP.contains(ip))
			{
				can=true;
			}
			
			if(_whiteListForUID.contains(data.uid))
			{
				can=true;
			}
			
			if(!can)
			{
				Ctrl.warnLog("服务器不可登陆:",httpID);
				//不可登陆
				if((response=LoginC.server.getBytesHttpResponse(httpID))!=null)
					response.resultError(ClientLoginHttpResultType.ServerNotReady);
				
				return;
			}
		}
		
		if(!checkMsgVersion(cMsgVersion,gMsgVersion,httpID))
			return;
		
		int[] loginList=LoginC.server.getLoginList();
		
		int loginID=loginList[data.uid.hashCode() % loginList.length];
		
		//是自己
		if(loginID==LoginC.app.id)
		{
			doLoginNext(data,loginID,httpID,ip);
		}
		else
		{
			ClientLoginTransferServerRequest.create(data,httpID,ip).send(loginID);
		}
	}
	
	/** 回复错误码 */
	private void reErrorCode(LoginTempData tData,int code)
	{
		reErrorCode(tData.sourceLoginID,tData.httpID,code);
	}
	
	/** 回复错误码 */
	private void reErrorCode(int loginID,int httpID,int code)
	{
		if(loginID==LoginC.app.id)
		{
			BytesHttpResponse response;
			
			if((response=LoginC.server.getBytesHttpResponse(httpID))!=null)
				response.resultError(code);
		}
		else
		{
			ReClientLoginErrorServerRequest.create(httpID,code).send(loginID);
		}
	}
	
	/** 回复登陆结果 */
	private void reClientLogin(int loginID,int httpID,ClientLoginResultData data)
	{
		if(loginID==LoginC.app.id)
		{
			ClientLoginHttpResponse response;
			if((response=(ClientLoginHttpResponse)LoginC.server.getBytesHttpResponse(httpID))!=null)
			{
				addFlowLog(response.data.uid,FlowStepType.ClientLoginHttpReBack);
				response.reBack(data);
			}
			else
			{
				Ctrl.errorLog("登陆http消息丢失");
			}
		}
		else
		{
			ReClientLoginTransferServerRequest.create(httpID,data).send(loginID);
		}
	}
	
	/** 继续执行登陆 */
	public void doLoginNext(ClientLoginData data,int loginID,int httpID,String ip)
	{
		if(checkUserLoginLock(data.uid))
		{
			Ctrl.warnLog("正在登陆中:",httpID);
			reErrorCode(loginID,httpID,ClientLoginHttpResultType.IsLogining);
			return;
		}
		
		//删掉上一个
		removeUserLogin(data.uid);
		
		ClientLoginExData eData=new ClientLoginExData();
		eData.data=data;
		//eData.loginID=LoginC.app.id;
		eData.ip=ip;
		
		makeLoginData(eData);
		
		LoginTempData tData=new LoginTempData();
		tData.sourceLoginID=loginID;
		tData.httpID=httpID;
		tData.eData=eData;
		tData.timeOut=CommonSetting.centerUserLoginMaxTime;
		(tData.lock=new AffairTimeLock(CommonSetting.loginAffairTime)).lockOn();
		
		_userLoginDic.put(data.uid,tData);
		
		if(data.platform.isEmpty())
		{
			data.platform=PlatformType.Visitor;//游客
		}
		
		ThreadControl.addIOFunc(data.uid.hashCode() & ThreadControl.ioThreadNumMark,()->
		{
			checkPlatform(tData);
		});
	}
	
	/** 构造登录数据部分 */
	protected void makeLoginData(ClientLoginExData eData)
	{
		////自动绑定的归-1
		//if(CommonSetting.areaDivideType==GameAreaDivideType.AutoBindGame)
		//	eData.data.areaID=-1;
		
		//后续g层内容
	}
	
	/** 平台验证(池线程) */
	protected void checkPlatform(LoginTempData tData)
	{
		//是成人
		tData.eData.isAdult=true;
		
		checkPlatformOver(tData,true);
	}
	
	/** 平台验证结束(池线程) */
	//关注点 平台验证结束后的处理逻辑
	protected void checkPlatformOver(LoginTempData tData,boolean isSuccess)
	{
		ThreadControl.addMainFunc(()->
		{
			doCheckPlatformOver(tData,isSuccess);
		});
	}
	
	private void doCheckPlatformOver(LoginTempData tData,boolean isSuccess)
	{
		ClientLoginExData eData=tData.eData;
		
		if(!isSuccess)
		{
			Ctrl.warnLog("平台校验失败:",tData.httpID);
			reErrorCode(tData,ClientLoginHttpResultType.PlatformCheckFailed);
			return;
		}
		
		if(ShineSetting.needDebugLog)
		{
			Ctrl.debugLog("login checkPlatformOver",tData.eData.data.uid);
		}
		
		String uid=eData.data.uid;
		
		if(getLoginTempData(uid)==null)
		{
			Ctrl.warnLog("客户端登陆超时:",tData.httpID);
			reErrorCode(tData,ClientLoginHttpResultType.TimeOut);
			return;
		}
		
		//拼uid
		String puid=BaseGameUtils.getPUID(eData.data.uid,eData.data.platform);
		
		UserTable ut=BaseC.factory.createUserTable();
		ut.puid=puid;
		
		Runnable userTableNext=()->
		{
			boolean isAdult=eData.isAdult;
			
			//有变化
			if(isAdult!=ut.isAdult)
			{
				//保存一下(不等返回)
				_userTableTool.load(ut.userID,tt->
				{
					tt.isAdult=isAdult;
				});
			}
			
			//userID
			eData.userID=ut.userID;
			tData.table=ut;
			//加入到userID组
			_userLoginDicByUserID.put(ut.userID,tData);
			
			reServerList(tData);
		};
		
		//有user表后的回调
		Runnable loadUserTableOver=()->
		{
			if(getLoginTempData(uid)==null)
			{
				Ctrl.warnLog("客户端登陆超时:",tData.httpID);
				reErrorCode(tData,ClientLoginHttpResultType.TimeOut);
				
				return;
			}
			
			//多平台绑定
			if(CommonSetting.UserNeedMultiPlatformBind && ut.sourceUserID>0)
			{
				Ctrl.log("login 用户登录指向源User,uid:",uid,"userID:",ut.sourceUserID);
				
				ut.userID=ut.sourceUserID;
				ut.puid="";
				ut.load(LoginC.db.getConnect(),b->
				{
					if(b)
					{
						userTableNext.run();
					}
					else
					{
						Ctrl.errorLog("未找到User表的源表:",ut.userID);
						reErrorCode(tData,ClientLoginHttpResultType.CenterLoginFaied);
						
						return;
					}
				});
			}
			else
			{
				userTableNext.run();
			}
		};
		
		//未找到user表的回调
		Runnable userTableNotExistFunc=()->
		{
			ut.puid=puid;
			ut.platform=eData.data.platform;
			ut.isAdult=eData.isAdult;
			ut.areaID=-1;//未绑定
			ut.userID=_snowFalker.getOne();//生成一个id
			
			//插入库
			ut.insert(LoginC.db.getConnect(),b2->
			{
				if(b2)
				{
					loadUserTableOver.run();
				}
				else
				{
					Ctrl.throwError("插入账号失败",puid,ut.userID);
				}
			});
		};
		
		//puid查询
		ut.load2(LoginC.db.getConnect(),b->
		{
			//有数据
			if(b)
			{
				loadUserTableOver.run();
			}
			else
			{
				//当前不是游客，并且有之前的游客UID,进行自动绑定平台逻辑
				if(CommonSetting.canPlatformAutoBind && !CommonSetting.UserNeedMultiPlatformBind && !eData.data.platform.equals(PlatformType.Visitor) && !eData.data.visitorUID.isEmpty())
				{
					ut.puid=BaseGameUtils.getPUID(eData.data.visitorUID,PlatformType.Visitor);
					
					//puid查询
					ut.load2(LoginC.db.getConnect(),b2->
					{
						//TODO:再补下对unique信息的校验
						
						//游客账号存在
						if(b2)
						{
							doBindPlatform(ut.userID,eData.data.uid,eData.data.platform,b3->
							{
								//成功
								if(b3)
								{
									loadUserTableOver.run();
								}
								else
								{
									Ctrl.warnLog("userLogin时,绑定账号失败",eData.data.uid);
									reErrorCode(tData,ClientLoginHttpResultType.CenterLoginFaied);
								}
							});
						}
						else
						{
							Ctrl.warnLog("进行游客账号自动绑定平台时，未找到游客账号:",puid,eData.data.visitorUID);
							userTableNotExistFunc.run();
						}
					});
				}
				else
				{
					userTableNotExistFunc.run();
				}
				
			}
		});
	}
	
	/** 回复服务器列表 */
	private void reServerList(LoginTempData tData)
	{
		int lastAreaID=tData.table.areaID;
		
		if(lastAreaID<=0)
			lastAreaID=-1;
		
		switch(CommonSetting.areaDivideType)
		{
			case GameAreaDivideType.Split:
			{
				//分服模式才有区服列表
				
				IntObjectMap<AreaClientData> dic=new IntObjectMap<>(AreaClientData[]::new,_areaServerDic.size());
				
				_areaServerDic.forEachValue(v->
				{
					AreaClientData data=new AreaClientData();
					data.areaID=v.areaID;
					data.load=v.load;
					data.name=v.name;
					
					dic.put(data.areaID,data);
				});
				
				LongSet playerSet=tData.table.getPlayerSet();
				
				if(!playerSet.isEmpty())
				{
					playerSet.forEachA(k->
					{
						int areaID=BaseC.logic.getAreaIDByLogicID(k);
						AreaClientData data=dic.get(areaID);
						
						if(data!=null)
						{
							data.mark=true;
						}
						else
						{
							Ctrl.errorLog("找不到的区服ID",k,areaID);
						}
					});
				}
				
				//判定
				while(_userLoginDicByToken.contains(tData.loginToken=MathUtils.getToken()));
				
				_userLoginDicByToken.put(tData.loginToken,tData);
				tData.lock.unlock();
				
				ClientLoginResultData rData=new ClientLoginResultData();
				
				rData.loginInfo=createSelfServerInfo();
				rData.loginInfo.token=tData.loginToken;
				
				rData.version=LoginC.main.getClientVersion(tData.eData.data.clientPlatformType);
				rData.areas=dic;
				rData.lastAreaID=lastAreaID;
				rData.gameInfo=null;
				
				if(rData.version==null)
				{
					Ctrl.warnLog("该设备平台的版本数据未配置",BaseC.constlist.clientPlatform_getName(tData.eData.data.clientPlatformType));
				}
				
				reClientLogin(tData.sourceLoginID,tData.httpID,rData);
			}
				break;
			case GameAreaDivideType.AutoBindGame:
			{
				tData.eData.areaID=lastAreaID;
				userLoginNext(tData);
			}
				break;
			case GameAreaDivideType.AutoEnterGame:
			{
				tData.eData.areaID=-1;
				userLoginNext(tData);
			}
				break;
		}
	}
	
	private ClientLoginServerInfoData createSelfServerInfo()
	{
		ServerInfoData info=LoginC.server.getInfo();
		ClientLoginServerInfoData re=new ClientLoginServerInfoData();
		re.host=info.clientHost;
		re.port=info.clientHttpPort;
		return re;
	}
	
	/** 检查登录锁 */
	private boolean checkUserLoginLock(String uid)
	{
		LoginTempData tData=_userLoginDic.get(uid);
		
		return tData!=null && tData.lock.isLocking();
	}
	
	private void removeUserLogin(LoginTempData data)
	{
		removeUserLogin(data.eData.data.uid);
	}
	
	/** 移除角色登录计时锁 */
	private void removeUserLogin(String uid)
	{
		LoginTempData tData=_userLoginDic.remove(uid);
		
		if(tData!=null)
		{
			if(tData.eData.userID>0)
				_userLoginDicByUserID.remove(tData.eData.userID);
			
			if(tData.loginToken!=-1)
				_userLoginDicByToken.remove(tData.loginToken);
		}
	}
	
	/** 获取登录临时数据 */
	public LoginTempData getLoginTempData(String uid)
	{
		return _userLoginDic.get(uid);
	}
	
	/** 登陆选择 */
	public void loginSelect(int httpID,int loginToken,int areaID)
	{
		Ctrl.log("login 用户登录选择,loginToken:",loginToken);
		
		LoginTempData tData=_userLoginDicByToken.get(loginToken);
		
		BytesHttpResponse response;
		
		if(tData==null)
		{
			Ctrl.warnLog("客户端登陆超时:",httpID);
			if((response=LoginC.server.getBytesHttpResponse(httpID))!=null)
				response.resultError(ClientLoginHttpResultType.TimeOut);
			
			return;
		}
		
		Ctrl.debugLog("login 用户登录选择2:",tData.eData.data.uid);
		
		if(!CommonSetting.isAreaSplit())
		{
			areaID=tData.table.areaID;
			
			if(areaID<=0)
				areaID=-1;
			
			if(CommonSetting.areaDivideType==GameAreaDivideType.AutoEnterGame)
			{
				areaID=-1;
			}
		}
		
		//更新httpID
		tData.sourceLoginID=LoginC.app.id;
		tData.httpID=httpID;
		tData.eData.areaID=areaID;
		
		userLoginNext(tData);
	}
	
	/** 回复登陆 */
	public void onReLogin(long userID,int token,int gameID)
	{
		LoginTempData tData=_userLoginDicByUserID.get(userID);
		
		if(tData==null)
		{
			Ctrl.warnLog("登录丢失一个",userID);
			return;
		}
		
		//分服模式才有区服列表
		if(CommonSetting.isAreaSplit())
		{
			ClientLoginSelectHttpResponse response=(ClientLoginSelectHttpResponse)LoginC.server.getHttpResponse(tData.httpID);
			
			if(response==null)
			{
				Ctrl.warnLog("登录丢失一个",userID);
				return;
			}
			
			String uid=tData.eData.data.uid;
			
			removeUserLogin(tData);
			
			GameServerClientSimpleData gameClientInfo=LoginC.main.getGameClientInfo(gameID);
			
			Ctrl.debugLog("login 回复登陆结果 uid:",uid,"gameID:",gameClientInfo.id,"token:",token);
			
			ClientLoginServerInfoData info=ClientLoginServerInfoData.create(token,gameClientInfo.clientHost,gameClientInfo.clientUsePort);
			addFlowLog(uid,FlowStepType.ReGameServerInfo);
			
			response.reBack(info);
		}
		else
		{
			removeUserLogin(tData);
			
			GameServerClientSimpleData gameClientInfo=LoginC.main.getGameClientInfo(gameID);
			
			ClientVersionData clientVersion=LoginC.main.getClientVersion(tData.eData.data.clientPlatformType);
			
			if(clientVersion==null)
			{
				Ctrl.warnLog("该设备平台的版本数据未配置",BaseC.constlist.clientPlatform_getName(tData.eData.data.clientPlatformType));
			}
			
			Ctrl.log("login 用户登陆回复:",tData.eData.data.uid," loginToken:",tData.loginToken);
			
			Ctrl.debugLog("login 回复登陆结果 uid:",tData.eData.data.uid,"gameID:",gameClientInfo.id,"token:",token);
			
			ClientLoginServerInfoData info=ClientLoginServerInfoData.create(token,gameClientInfo.clientHost,gameClientInfo.clientUsePort);
			
			ClientLoginResultData rData=new ClientLoginResultData();
			rData.loginInfo=createSelfServerInfo();
			rData.gameInfo=info;
			rData.version=LoginC.main.getClientVersion(tData.eData.data.clientPlatformType);
			
			reClientLogin(tData.sourceLoginID,tData.httpID,rData);
		}
	}
	
	/** 客户端日志 */
	public void onClientLog(String uid,int type,String str)
	{
		LogControl.clientLog(SLogType.getClientMark(type)+"uid:"+uid+' '+str);
		
		if(type==SLogType.Error)
		{
			LogControl.clientErrorLog("uid:"+uid+' '+str);
		}
	}
	
	/** 登录过程临时数据 */
	protected class LoginTempData
	{
		/** 登录数据 */
		public ClientLoginExData eData;
		/** user表 */
		public UserTable table;
		/** 超时时间锁 */
		public AffairTimeLock lock;
		/** 超时时间 */
		public int timeOut;
		/** 登陆令牌 */
		public int loginToken=-1;
		/** 登陆源服ID */
		public int sourceLoginID=-1;
		/** httpID */
		public int httpID=-1;
		/** 选择的gameID */
		public int gameID;
	}
	
	/** 添加流程步日志(主线程) */
	public void addFlowLog(String uid,int step)
	{
		BaseC.logic.addFlowLog(_flowLog,uid,step);
	}
	
	//user相关逻辑
	
	protected void selectLoginArea(ClientLoginExData eData)
	{
		if(CommonSetting.useCountryArea)
		{
			eData.areaID=getNowRegistAreaForCountry(eData.data.countryID);
		}
		else
		{
			eData.areaID=getNowRegistArea();
		}
	}
	
	/** 获取一个当前可用区服ID */
	protected int getNowRegistArea()
	{
		return _canRegistArea;
	}
	
	/** 获取一个当前可用区服ID */
	protected int getNowRegistAreaForCountry(int countryID)
	{
		CountryInfo info=_countryInfos.get(countryID);
		
		if(info==null)
			return -1;
		
		return info.useAreaID;
	}
	
	/** 用户登录下一步 */
	protected void userLoginNext(LoginTempData tData)
	{
		ClientLoginExData eData=tData.eData;
		
		Ctrl.debugLog("login center 用户登录",eData.data.uid);
		
		long userID=eData.userID;
		
		boolean needBindArea=false;
		
		switch(CommonSetting.areaDivideType)
		{
			case GameAreaDivideType.Split:
			{
				needBindArea=true;
			}
				break;
			case GameAreaDivideType.AutoBindGame:
			{
				//给与分配的区服ID
				if(eData.areaID==-1)
				{
					selectLoginArea(eData);
					
					//还是没找到
					if(eData.areaID==-1)
					{
						//正在登录中
						Ctrl.warnLog("userLogin时,找不到可用的注册区服",eData.data.uid);
						reErrorCode(tData,ClientLoginHttpResultType.CenterLoginFaied);
						return;
					}
					
					needBindArea=true;
				}
			}
				break;
			case GameAreaDivideType.AutoEnterGame:
			{
				selectLoginArea(eData);
				
				//还是没找到
				if(eData.areaID==-1)
				{
					//正在登录中
					Ctrl.warnLog("userLogin时,找不到可用的注册区服",eData.data.uid);
					reErrorCode(tData,ClientLoginHttpResultType.CenterLoginFaied);
					return;
				}
			}
				break;
		}
		
		int nowGameID;
		
		if(eData.areaID<=0 || (nowGameID=getNowGameID(eData.areaID))==-1)
		{
			Ctrl.warnLog("当前区服不存在",eData.areaID);
			reErrorCode(tData,ClientLoginHttpResultType.CenterLoginFaied);
			return;
		}
		
		//需要绑定，并且不是上次选的区服
		if(needBindArea && eData.areaID!=tData.table.areaID)
		{
			int bindAreaID=eData.areaID;
			
			_userTableTool.load(userID,table->
			{
				table.areaID=bindAreaID;
			});
		}
		
		crowedDownUser(userID,nowGameID,()->
		{
			LoginTempData tData2=getLoginTempData(tData.eData.data.uid);
			
			if(tData2==null)
			{
				Ctrl.warnLog("挤掉角色后,登录信息已丢失");
				reErrorCode(tData,ClientLoginHttpResultType.CenterLoginFaied);
				return;
			}
			
			BaseSocket gameSocket=LoginC.server.getGameSocket(nowGameID);
			
			if(gameSocket==null)
			{
				Ctrl.warnLog("游戏服连接丢失",nowGameID);
				reErrorCode(tData,ClientLoginHttpResultType.ServerNotReady);
				return;
			}
			
			//绑定gameID
			tData.gameID=nowGameID;
			
			//关注点 将用户数据发送到游戏服
			gameSocket.send(UserLoginToGameServerRequest.create(eData));
		});
	}
	
	/** 检查用户是否存在 */
	private void checkUserExist(String uid,String platform,ObjectCall<Boolean> func)
	{
		UserTable table=BaseC.factory.createUserTable();
		table.puid=BaseGameUtils.getPUID(uid,platform);
		table.load2(LoginC.db.getConnect(),func);
	}
	
	/** 用户绑定账号 */
	public void bindPlatform(long userID,String uid,String platform,ObjectCall<Boolean> overCall)
	{
		checkUserExist(uid,platform,b->
		{
			if(b)
			{
				Ctrl.warnLog("用户绑定账号时，目标puid已存在",userID,uid,platform);
				overCall.apply(false);
			}
			else
			{
				doBindPlatform(userID,uid,platform,overCall);
			}
		});
	}
	
	protected void doBindPlatform(long userID,String uid,String platform,ObjectCall<Boolean> overCall)
	{
		if(!CommonSetting.UserNeedMultiPlatformBind)
		{
			doBindPlatformSingle(userID,uid,platform,overCall);
		}
		else
		{
			doBindPlatformMulti(userID,uid,platform,overCall);
		}
	}
	
	protected void doBindPlatformSingle(long userID,String uid,String platform,ObjectCall<Boolean> overCall)
	{
		VBoolean re=new VBoolean();
		
		_userTableTool.load(userID,table->
		{
			if(table==null)
			{
				Ctrl.warnLog("绑定平台时,未找到User表,userID:",userID);
				return;
			}
			
			if(!table.platform.equals(PlatformType.Visitor))
			{
				Ctrl.warnLog("必须为游客平台才能绑定");
				return;
			}
			
			if(platform.equals(PlatformType.Visitor))
			{
				Ctrl.warnLog("绑定的平台不能是游客");
				return;
			}
			
			table.puid=BaseGameUtils.getPUID(uid,platform);
			table.platform=platform;
			
			LongSet playerSet=table.getPlayerSet();
			
			if(!playerSet.isEmpty())
			{
				playerSet.forEachA(k->
				{
					onPlayerBindPlatform(k,uid,platform);
				});
			}
			
			re.value=true;
		},()->
		{
			//返回
			overCall.apply(re.value);
		});
	}
	
	protected void doBindPlatformMulti(long userID,String uid,String platform,ObjectCall<Boolean> overCall)
	{
		_userTableTool.load(userID,table->
		{
			if(table==null)
			{
				Ctrl.warnLog("绑定多平台时,未找到User表,userID:",userID);
				overCall.apply(false);
				return;
			}
			
			if(table.sourceUserID>0)
			{
				Ctrl.warnLog("绑定多平台时,不能绑定到虚拟表中:",userID);
				overCall.apply(false);
				return;
			}
			
			if(table.platform.equals(platform))
			{
				Ctrl.warnLog("绑定多平台时,不能绑定相同的平台:",userID,platform);
				overCall.apply(false);
				return;
			}
			
			//拼uid
			String puid=BaseGameUtils.getPUID(uid,platform);
			
			UserTable ut=BaseC.factory.createUserTable();
			ut.puid=puid;
			ut.platform=platform;
			ut.userID=_snowFalker.getOne();
			ut.sourceUserID=userID;
			ut.insert(LoginC.db.getConnect(),b->
			{
				if(b)
				{
					LongSet playerSet=table.getPlayerSet();
					
					if(!playerSet.isEmpty())
					{
						playerSet.forEachA(k->
						{
							onPlayerBindPlatform(k,uid,platform);
						});
					}
					
					overCall.apply(true);
				}
				else
				{
					Ctrl.warnLog("绑定多平台时,插入新表失败:",userID,puid);
					overCall.apply(false);
					return;
				}
			});
		},null);
	}
	
	/** 用户绑定账号 */
	public void bindPlatformByClient(long playerID,long userID,String uid,String platform)
	{
		bindPlatform(userID,uid,platform,b->
		{
			//失败
			if(!b)
			{
				sendInfoCodeToPlayer(playerID,InfoCodeType.BindPlatformFailed_targetUIDExist);
			}
		});
	}
	
	private void onPlayerBindPlatform(long playerID,String uid,String platform)
	{
		PlayerBindPlatformToGameServerRequest.create(playerID,uid,platform).sendToSourceGameByPlayerID(playerID);
	}
	
	/** 挤掉上个账号(除了exceptGameID的)(主线程) */
	private void crowedDownUser(long userID,int exceptGameID,Runnable func)
	{
		//TODO:实现挤掉user
		
		func.run();
	}
	
	
	private class CountryInfo
	{
		public int countryID;
		/** 当前可用注册区id */
		public int useAreaID=-1;
		/** 区服列表 */
		public IntObjectMap<AreaServerData> areaServerDic=new IntObjectMap<>(AreaServerData[]::new);
		/** 区服组 */
		public int[] areaList;
		
		public CountryInfo(int countryID)
		{
			this.countryID=countryID;
		}
		
		/** 添加区服组数据 */
		public void addAreaServerDic(IntObjectMap<AreaServerData> dic)
		{
			dic.forEachValue(v->
			{
				areaServerDic.put(v.areaID,v);
			});
			
			areaList=areaServerDic.getSortedKeyList().toArray();
			
			findCountryNextRegistArea();
		}
		
		private void findCountryNextRegistArea()
		{
			useAreaID=-1;
			
			for(int v:areaList)
			{
				if(!areaServerDic.get(v).isLimitRegist)
				{
					useAreaID=v;
					break;
				}
			}
		}
	}
	
	/** 收到user事务 */
	public void onReceiveUserWork(UserWorkData data)
	{
		_userTableTool.load(data.userID,table->
		{
			LoginC.userWork.execute(table,data);
			
			int senderID=BaseGameUtils.getWorkSenderID(data.senderIndex);
			
			//发送返回
			ReceiptUserWorkToGameServerRequest.create(data.senderIndex,data.workInstanceID,0).send(getNowGameID(senderID));
		});
	}
	
	/** 发送信息码到客户端 */
	public void sendInfoCodeToPlayer(long playerID,int code)
	{
		//发到源服
		SendInfoCodeFromLoginServerRequest.create(playerID,code).sendToSourceGameByPlayerID(playerID);
	}
	
	/** 重新加载配置(主线程) */
	public void reloadConfig(Runnable func)
	{
		Ctrl.log("reloadLoginConfig");
		
		BaseC.config.reload(this::onReloadConfig,()->
		{
			Ctrl.log("reloadLoginConfigComplete");
			
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
}
