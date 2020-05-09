package com.home.shineTool.tool.export;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.collection.SMap;
import com.home.shine.support.func.ObjectCall;
import com.home.shine.support.func.ObjectCall2;
import com.home.shine.support.func.ObjectCall3;
import com.home.shine.support.func.ObjectFunc2;
import com.home.shine.utils.FileUtils;
import com.home.shine.utils.StringUtils;
import com.home.shineTool.constlist.CodeType;
import com.home.shineTool.constlist.DataGroupType;
import com.home.shineTool.constlist.ProjectType;
import com.home.shineTool.constlist.VisitType;
import com.home.shineTool.reflect.FieldInfo;
import com.home.shineTool.reflect.MethodArgInfo;
import com.home.shineTool.reflect.MethodInfo;
import com.home.shineTool.reflect.cls.ClassInfo;
import com.home.shineTool.reflect.code.CodeInfo;
import com.home.shineTool.reflect.code.CodeWriter;
import com.home.shineTool.tool.base.RecordDataClassInfo;
import com.home.shineTool.global.ShineToolSetting;
import com.home.shineTool.tool.data.DataDefineTool;
import com.home.shineTool.tool.data.DataExportTool;
import com.home.shineTool.tool.data.DataMakerTool;
import com.home.shineTool.tool.part.PartDataExportTool;
import com.home.shineTool.tool.part.PartListTool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/** 标准数据导出形态 */
public abstract class NormalDataExportTool extends BaseDataExportTool
{
	/** 角色离线事务数据完全限定类名 */
	private static String _playerOfflineWorkListQName=ShineToolSetting.globalPackage+"commonData.data.system.PlayerWorkListDO";
	/** 客户端角色离线本地缓存完全限定类名 */
	private static String _clientPlayerLocalCacheQName=ShineToolSetting.globalPackage+"commonData.data.system.ClientPlayerLocalCacheDO";
	/** 客户端离线事务数据完全限定类名 */
	private static String _clientOfflineWorkListQName=ShineToolSetting.globalPackage+"commonData.data.system.ClientOfflineWorkListDO";
	/** 客户端离线缓存附加数据完全限定类名 */
	private static String _playerOfflineCacheExQName=ShineToolSetting.globalPackage+"commonData.data.login.PlayerOfflineCacheExDO";
	
	//工程类型
	public static final int ServerBase=1;
	public static final int ServerCenter=2;
	public static final int ServerLogin=3;
	public static final int ServerGame=4;
	public static final int ServerManager=5;
	public static final int ServerRobot=7;
	
	public static final int GMClient=6;//目前也用java做客户端
	public static final int ClientGame=8;
	public static final int ClientHotfix=9;
	
	/** 客户端信息 */
	public static final int ClientMessage=10;
	
	public static final int typeSize=20;
	
	/** 服务器工程根组 */
	public String[] projectRoots=new String[typeSize];
	
	/** 数据工程根 */
	protected String _dataProjectRoot;
	
	//indexStarts
	
	protected int _start;
	
	protected int _gameDataLen;
	protected int _hotfixDataLen;
	
	protected int _gameMessageLen;
	protected int _centerMessageLen;
	protected int _hotfixMessageLen;
	protected int _managerHttpMessageLen;
	protected int _loginHttpMessageLen;
	protected int _serverMessageLen;
	
	protected int _listLen;
	protected int _centerGlobalPartLen;
	
	/** centerPlayer已做法,但是目前预留100在此 */
	protected int _centerPlayerPartLen;
	protected int _gameGlobalPartLen;
	protected int _playerPartLen;
	
	private int _nowStart;
	
	/** common工程对象 */
	protected NormalDataExportTool _commonExport;
	
	//partEx
	
	//count
	
	public boolean[] hasBigModifieds=new boolean[NormalDataExportTool.typeSize];
	public StringBuilder[] versionStrs=new StringBuilder[NormalDataExportTool.typeSize];
	
	//c<->g
	
	private SMap<String,PartListTool> _serverPartListToolDic=new SMap<>();
	
	private SMap<String,PartListTool> _clientPartListToolDic=new SMap<>();
	
	public NormalDataExportTool()
	{
		//unity
		_clientCodeType=ShineToolSetting.getClientCodeType();
		
		//默认值
		_gameDataLen=500;
		_hotfixDataLen=300;
		
		_gameMessageLen=1000;
		_hotfixMessageLen=500;
		_centerMessageLen=300;
		_managerHttpMessageLen=200;
		_loginHttpMessageLen=100;
		_serverMessageLen=1000;
		
		_listLen=1;//实际就1个
		_centerGlobalPartLen=100;
		_centerPlayerPartLen=100;
		_gameGlobalPartLen=100;
		_playerPartLen=200;
		
		for(int i=versionStrs.length-1;i>=0;--i)
		{
			versionStrs[i]=new StringBuilder();
		}
	}
	
	public static String getTypeName(int type)
	{
		switch(type)
		{
			case ServerBase:
			//case ClientBase:
				return "base";
			case ServerGame:
			case ClientGame:
				return "game";
			case ClientHotfix:
				return "hotfix";
			case ServerCenter:
				return "center";
			case ServerLogin:
				return "login";
			case ServerRobot:
				return "robot";
			case ServerManager:
				return "manager";
			//case GMClient:
			//	return "gmClient";
			default:
			{
				Ctrl.throwError("有未配置的类型");
			}
		}
		
		return "";
	}
	
	/** 设置start */
	public void setStart(int value)
	{
		_start=value;
	}
	
	/** 获取结束值 */
	public int getEnd()
	{
		return _nowStart;
	}
	
	/** 设置common */
	public void setCommonExport(NormalDataExportTool common)
	{
		_commonExport=common;
	}
	
	/** 获取common工程对应的路径 */
	@Override
	public String getCommonPath(String path)
	{
		if(_isCommon)
			return path;
		
		String temp;
		
		for(int i=projectRoots.length-1;i>=0;--i)
		{
			if((temp=projectRoots[i])!=null)
			{
				if(path.startsWith(temp))
				{
					return _commonExport.projectRoots[i] + path.substring(temp.length(),path.length());
				}
			}
		}
		
		return path;
	}
	
	@Override
	public void toExecute()
	{
		_nowStart=_start;
		
		initILRuntime();
		
		makeDatas(true);
		
		if(ShineToolSetting.isAllRefreshMessage)
			ShineToolSetting.isAllRefresh=true;
		
		makeClientMessages();
		makeClientHttpMessages();
		makeServerMessages();
		
		if(ShineToolSetting.isAllRefreshMessage)
			ShineToolSetting.isAllRefresh=false;
		
		makeListDatas();
		
		writeILRunTime();
	}
	
	/** 注册定义 */
	public void registDefine()
	{
		super.registDefine();
		
		makeDatas(false);
	}
	
	/** 初始化IlRuntime写出 */
	private void initILRuntime()
	{
//		ILRuntimeAdapterTool.init();
//
//		String path;
//
//		if(_commonExport==null)
//		{
//			path=ShineToolGlobal.clientMainSrcPath + "/commonGame/control/CILRuntimeControl.cs";
//			_adapterPath=ShineToolGlobal.clientMainSrcPath + "/commonGame/adapters/data";
//		}
//		else
//		{
//			path=ShineToolGlobal.clientMainSrcPath + "/game/control/GILRuntimeControl.cs";
//			_adapterPath=ShineToolGlobal.clientMainSrcPath + "/game/adapters/data";
//		}
//
//		_ILRuntimeControlCls=ClassInfo.getClassInfoFromPath(path);
//
//		_IlRuntimeWriter=_ILRuntimeControlCls.createWriter();
//		_IlRuntimeWriter.writeCustom("base.initDataAdapters(appdomain);");
//		_IlRuntimeWriter.writeEmptyLine();
	}
	
	private void writeILRunTime()
	{
//		MethodInfo method=_ILRuntimeControlCls.getMethodByName("initDataAdapters");
//
//		if(method==null)
//		{
//			method=new MethodInfo();
//			method.name="initDataAdapters";
//			method.describe="初始化数据适配器";
//			method.returnType=_ILRuntimeControlCls.getCode().Void;
//			method.visitType=VisitType.Protected;
//			method.isOverride=true;
//			method.args.add(new MethodArgInfo("appdomain","AppDomain"));
//
//			_ILRuntimeControlCls.addMethod(method);
//		}
//
//		_IlRuntimeWriter.writeEnd();
//		method.content=_IlRuntimeWriter.toString();
//		_ILRuntimeControlCls.write();
	}
	
	/** 数据部分(needExecute:是否是执行,否则就是注册包信息) */
	private void makeDatas(boolean needExecute)
	{
		//Ctrl.print("dataStart",_nowStart);
		
		DataExportTool gameData=makeOneData("gameData",_front + "Base",false,_dataProjectRoot + "/data",projectRoots[ServerBase],projectRoots[ClientGame],null,_nowStart,_gameDataLen,true,null,null);
		_nowStart+=_gameDataLen;
		
		if(needExecute)
			gameData.executeAll();
		else
			gameData.recordGroupPackage();
		
		//hotfix
		if(ShineToolSetting.needHotfix && !_isCommon)
		{
			DataExportTool hData=makeOneData("hotfixData","Hotfix",true,_dataProjectRoot + "/hData",projectRoots[ServerBase],projectRoots[ClientHotfix],null,_nowStart,_hotfixDataLen,false,null,gameData);
			_nowStart+=_hotfixDataLen;
			
			if(needExecute)
				hData.executeAll();
			else
				hData.recordGroupPackage();
		}
		
		//Ctrl.print("dataEnd",_nowStart);
	}
	
	/** 客户端socket通信部分 */
	private void makeClientMessages()
	{
		//game
		DataExportTool gameRequest=makeOneRequest("",_front + "Game",false,_dataProjectRoot + "/message/game/request",projectRoots[ServerGame],projectRoots[ClientGame],projectRoots[ServerRobot],_nowStart,_gameMessageLen,true,null);
		_nowStart+=_gameMessageLen;
		
		DataExportTool gameResponse=makeOneResponse("",_front + "Game",false,_dataProjectRoot + "/message/game/response",projectRoots[ServerGame],projectRoots[ClientGame],projectRoots[ServerRobot],_nowStart,_gameMessageLen,gameRequest,null);
		_nowStart+=_gameMessageLen;

		//game
		DataExportTool centerRequest=makeOneRequest("center",_front + "Center",false,_dataProjectRoot + "/message/center/request",projectRoots[ServerCenter],projectRoots[ClientGame],projectRoots[ServerRobot],_nowStart,_centerMessageLen,true,null);
		_nowStart+=_centerMessageLen;
		
		DataExportTool centerResponse=makeOneResponse("center",_front + "Center",false,_dataProjectRoot + "/message/center/response",projectRoots[ServerCenter],projectRoots[ClientGame],projectRoots[ServerRobot],_nowStart,_centerMessageLen,centerRequest,null);
		_nowStart+=_centerMessageLen;
		
		//hotfix
		if(ShineToolSetting.needHotfix && !_isCommon)
		{
			DataExportTool hGameRequest=makeOneRequest("h","HGame",true,_dataProjectRoot + "/message/game/hRequest",projectRoots[ServerGame],projectRoots[ClientHotfix],projectRoots[ServerRobot],_nowStart,_hotfixMessageLen,false,gameRequest);
			_nowStart+=_hotfixMessageLen;
			
			makeOneResponse("h","HGame",true,_dataProjectRoot + "/message/game/hResponse",projectRoots[ServerGame],projectRoots[ClientHotfix],projectRoots[ServerRobot],_nowStart,_hotfixMessageLen,hGameRequest,gameResponse);
			_nowStart+=_hotfixMessageLen;
			
			DataExportTool hCenterRequest=makeOneRequest("hCenter","HCenter",true,_dataProjectRoot + "/message/center/hRequest",projectRoots[ServerCenter],projectRoots[ClientHotfix],projectRoots[ServerRobot],_nowStart,_centerMessageLen,false,centerRequest);
			_nowStart+=_centerMessageLen;
			
			makeOneResponse("hCenter","HCenter",true,_dataProjectRoot + "/message/center/hResponse",projectRoots[ServerCenter],projectRoots[ClientHotfix],projectRoots[ServerRobot],_nowStart,_centerMessageLen,hCenterRequest,centerResponse);
			_nowStart+=_centerMessageLen;
		}
		
		////common和game都统计
		//String qName=FileUtils.getPathPackage(inFilePath) + "." + ff + "LO";
		//
		//countOneModify(dataServerType,qName);
		//
		//if(needClient)
		//{
		//	qName=FileUtils.getPathPackage(clientInPath) + "." + ff + "CLO";
		//
		//	countOneModify(clientDataType,qName);
		//}
	}
	
	/** 客户端http通信部分 */
	private void makeClientHttpMessages()
	{
		HttpResponseResultExportTool resultTool;
		
		//loginResult
		resultTool=makeOneHttpResponseResult("loginHttp",_front + "Login",_dataProjectRoot + "/message/login/httpResponseResult",projectRoots[ServerLogin],projectRoots[ClientGame],projectRoots[ServerRobot],_nowStart,_loginHttpMessageLen,true);
		_nowStart+=_loginHttpMessageLen;
		
		//loginResponse
		
		makeOneHttpResponse("loginHttp",_front + "Login",_dataProjectRoot + "/message/login/httpResponse",projectRoots[ServerLogin],projectRoots[ClientGame],projectRoots[ServerRobot],_nowStart,_loginHttpMessageLen,true,resultTool);
		_nowStart+=_loginHttpMessageLen;
		
		
		//manager
		resultTool=makeOneHttpResponseResult("managerHttp",_front + "Manager",_dataProjectRoot + "/message/manager/httpResponseResult",projectRoots[ServerManager],null,projectRoots[GMClient],_nowStart,_managerHttpMessageLen,false);
		_nowStart+=_managerHttpMessageLen;
		
		
		makeOneHttpResponse("managerHttp",_front + "Manager",_dataProjectRoot + "/message/manager/httpResponse",projectRoots[ServerManager],null,projectRoots[GMClient],_nowStart,_managerHttpMessageLen,false,resultTool);
		_nowStart+=_managerHttpMessageLen;
	}
	
	/** 服务器通信部分 */
	private void makeServerMessages()
	{
		DataDefineTool define=new DataDefineTool(projectRoots[ServerBase] + "/constlist/generate/" + _front + "ServerMessageType." + _serverExName,_nowStart,_serverMessageLen,true);
		_nowStart+=_serverMessageLen;
		
		//gameRequest
		DataMakerTool centerRequestMaker=new DataMakerTool(projectRoots[ServerCenter] + "/tool/generate/" + _front + "CenterServerRequestMaker." + _serverExName,define);
		DataMakerTool gameRequestMaker=new DataMakerTool(projectRoots[ServerGame] + "/tool/generate/" + _front + "GameServerRequestMaker." + _serverExName,define);
		DataMakerTool loginRequestMaker=new DataMakerTool(projectRoots[ServerLogin] + "/tool/generate/" + _front + "LoginServerRequestMaker." + _serverExName,define);
		DataMakerTool managerRequestMaker=new DataMakerTool(projectRoots[ServerManager] + "/tool/generate/" + _front + "ManagerServerRequestMaker." + _serverExName,define);
		
		DataMakerTool centerResponseMaker=new DataMakerTool(projectRoots[ServerCenter] + "/tool/generate/" + _front + "CenterServerResponseMaker." + _serverExName,define);
		DataMakerTool gameResponseMaker=new DataMakerTool(projectRoots[ServerGame] + "/tool/generate/" + _front + "GameServerResponseMaker." + _serverExName,define);
		DataMakerTool loginResponseMaker=new DataMakerTool(projectRoots[ServerLogin] + "/tool/generate/" + _front + "LoginServerResponseMaker." + _serverExName,define);
		DataMakerTool managerResponseMaker=new DataMakerTool(projectRoots[ServerManager] + "/tool/generate/" + _front + "ManagerServerResponseMaker." + _serverExName,define);
		
		ObjectFunc2<DataMakerTool,Integer> getRequestMaker=k->
		{
			switch(k)
			{
				case ServerCenter:
					return centerRequestMaker;
				case ServerLogin:
					return loginRequestMaker;
				case ServerGame:
					return gameRequestMaker;
				case ServerManager:
					return managerRequestMaker;
				default:
				{
					Ctrl.throwError("不该找不到maker");
				}
				
			}
			
			return null;
		};
		
		ObjectFunc2<DataMakerTool,Integer> getResponseMaker=k->
		{
			switch(k)
			{
				case ServerCenter:
					return centerResponseMaker;
				case ServerLogin:
					return loginResponseMaker;
				case ServerGame:
					return gameResponseMaker;
				case ServerManager:
					return managerResponseMaker;
				default:
				{
					Ctrl.throwError("不该找不到maker");
				}
				
			}
			
			return null;
		};
		
		
		
		/** b为是否使用基类定义 */
		ObjectCall3<Integer,Integer,Boolean> makeOne=(k,v,b)->
		{
			String kName=getTypeName(k);
			String vName=getTypeName(v);
			
			String fKey=k+","+v;
			
			String ucKName=StringUtils.ucWord(kName);
			String ucVName=StringUtils.ucWord(vName);
			
			DataExportTool export=new DataExportTool();
			export.setFileRecordTool(_record);
			export.addDefine(DataGroupType.Server,define);
			export.addMaker(DataGroupType.Server,getRequestMaker.apply(k));
			export.addMaker(DataGroupType.Server2,getResponseMaker.apply(v));
			
			if(_isCommon)
			{
				_dataExports.put(fKey,export);
			}
			else
			{
				export.setCommonTool(_dataExports.get(fKey));
			}
			
			String requestName=b ? "base/" + ucKName+"ServerRequest" : "serverRequest/"+vName+"/base/" + ucKName+"To"+ucVName+"ServerRequest";
			String responseName=b ? "base/" + ucVName+"ServerResponse" : "serverResponse/"+kName+"/base/" + ucKName+"To"+ucVName+"ServerResponse";
			
			export.setInput(_dataProjectRoot + "/message/"+kName+"/serverRequest/"+vName,"MO",this::messageFilter);
			createForRequest(export,projectRoots[k] + "/net/serverRequest/"+vName,DataGroupType.Server,"Server",getCommonPath(projectRoots[k]) + "/net/"+requestName+"."+_serverExName,false);
			createForResponse(export,projectRoots[v] + "/net/serverResponse/"+kName,DataGroupType.Server2,"Server",getCommonPath(projectRoots[v]) + "/net/" + responseName+"."+_serverExName,false);
			export.execute();
		};
		
		//TODO:这里现在还有个问题，就是生成的非base基类，的基类是最继承的BaseRequest而不是对应server的基类
		
		//login<->manager
		makeOne.apply(ServerLogin,ServerManager,false);
		makeOne.apply(ServerManager,ServerLogin,false);
		
		//center<->manager
		makeOne.apply(ServerCenter,ServerManager,false);
		makeOne.apply(ServerManager,ServerCenter,false);
		
		//game<->manager
		makeOne.apply(ServerGame,ServerManager,false);
		makeOne.apply(ServerManager,ServerGame,false);
		
		//center<->game
		makeOne.apply(ServerCenter,ServerGame,false);
		makeOne.apply(ServerGame,ServerCenter,false);
		
		//login<->game
		makeOne.apply(ServerLogin,ServerGame,false);
		makeOne.apply(ServerGame,ServerLogin,false);
		
		//game<->game
		makeOne.apply(ServerGame,ServerGame,false);
		
		//login<->login
		makeOne.apply(ServerLogin,ServerLogin,false);
		
		define.write();
		
		centerRequestMaker.write();
		gameRequestMaker.write();
		loginRequestMaker.write();
		managerRequestMaker.write();
		
		centerResponseMaker.write();
		gameResponseMaker.write();
		loginResponseMaker.write();
		managerResponseMaker.write();
	}
	
	private void makeOneListDatas(String front,boolean isH,int len,int dataServerType,int partServerType,boolean needCountServer,boolean needClient,ObjectCall2<ClassInfo,Boolean> mainExCall,ObjectCall3<ClassInfo,Boolean,Boolean> partExCall)
	{
		String nFront=front;
		
		if(isH)
		{
			front=front.substring(1);//去掉H
		}
		
		String mFront=isH ? "H" : _front;
		
		String ucFront=StringUtils.ucWord(front);
		String lcFront=StringUtils.lcWord(front);
		
		String serverLcFront=isH ? mFront.toLowerCase()+ucFront : lcFront;
		String clientLcFront=lcFront;
		
		String ff=mFront+ucFront;
		
		//角色部分
		
		DataDefineTool define0;
		DataDefineTool define1;
		DataDefineTool define2;
		DataMakerTool maker0;
		DataMakerTool maker1;
		DataMakerTool maker2;
		
		String dataServerRootPath=projectRoots[dataServerType];
		String partServerRootPath=projectRoots[partServerType];
		
		//serverData
		
		define0=new DataDefineTool(dataServerRootPath + "/constlist/generate/" + ff + "PartDataType." + _serverExName,_nowStart,len,true);
		_nowStart+=len;
		
		maker0=new DataMakerTool(dataServerRootPath + "/tool/generate/" + ff + "PartDataMaker." + _serverExName,define0);
		
		PartDataExportTool export1=new PartDataExportTool();
		export1.setFileRecordTool(_record);
		export1.addDefine(DataGroupType.Server,define0);
		export1.addMaker(DataGroupType.Server,maker0);
		export1.setInput(_dataProjectRoot + "/" + serverLcFront + "/server","SPO");
		export1.addOutput(createForPart(dataServerRootPath + "/part/" + serverLcFront + "/data",DataGroupType.Server,"",1));
		
		PartListTool partServerListTool=null;
		PartListTool partClientListTool=null;
		
		if(isH)
		{
			partServerListTool=_serverPartListToolDic.get("Player");
			
			if(needClient)
				partClientListTool=_clientPartListToolDic.get("Player");
		}
		else
		{
			if(_commonExport!=null)
			{
				partServerListTool=_commonExport._serverPartListToolDic.get(nFront);
				
				if(needClient)
					partClientListTool=_commonExport._clientPartListToolDic.get(nFront);
			}
		}
		
		if(partServerListTool!=null)
		{
			export1.addParentTool(partServerListTool.getBelongExportTool());
		}
	
		export1.executeAll();
		
		PartDataExportTool export2=null;
		
		//clientData
		int clientDataType=isH ? ClientHotfix:ClientGame;
		String clientRoot=projectRoots[clientDataType];
		
		if(needClient)
		{
			define0=new DataDefineTool(dataServerRootPath + "/constlist/generate/" + ff + "PartClientDataType." + _serverExName,_nowStart,len,true);
			define1=new DataDefineTool(clientRoot + "/constlist/generate/" + ff + "PartDataType." + _clientExName,_nowStart,len,false);
			_nowStart+=len;
			
			maker0=new DataMakerTool(dataServerRootPath + "/tool/generate/" + ff + "PartClientDataMaker." + _serverExName,define0);
			maker1=new DataMakerTool(clientRoot + "/tool/generate/" + ff + "PartDataMaker." + _clientExName,define1);
			
			export2=new PartDataExportTool();
			export2.setFileRecordTool(_record);
			export2.addDefine(DataGroupType.Server,define0);
			if(ShineToolSetting.needClient)
				export2.addDefine(DataGroupType.Client,define1);
			export2.addMaker(DataGroupType.Server,maker0);
			if(ShineToolSetting.needClient)
				export2.addMaker(DataGroupType.Client,maker1);
			export2.setInput(_dataProjectRoot + "/" + serverLcFront + "/client","CPO");
			export2.addOutput(createForPart(dataServerRootPath + "/part/" + serverLcFront + "/clientData",DataGroupType.Server,"Client",2));
			if(ShineToolSetting.needClient)
				export2.addOutput(createForPart(clientRoot + "/part/" + clientLcFront + "/data",DataGroupType.Client,"",2));
			
			//这个适配还是继续用吧(不过改成了S->C)  (适配关闭)
			//export2.addExInputDic(export1.getExecutedInputClsDic());
			//export2.setPackageReplace("server","client");
			//export2.setMarkReplace("SPO","CPO");
			
			if(partClientListTool!=null)
			{
				export2.addParentTool(partClientListTool.getBelongExportTool());
			}
			
			export2.executeAll();
		}
		
		//list
		len=_listLen;
		
		//list
		
		define0=new DataDefineTool(dataServerRootPath + "/constlist/generate/" + ff + "ListDataType." + _serverExName,_nowStart,len,true);
		_nowStart+=len;
		
		maker0=new DataMakerTool(dataServerRootPath + "/tool/generate/" + ff + "ListDataMaker." + _serverExName,define0);
		
		int projectType=countProjectType(isH);
		
		PartListTool listTool=new PartListTool();
		
		listTool.setFileRecordTool(_record);
		listTool.setMainName(ucFront);
		listTool.setProjectType(projectType);
		
		listTool.setExCalls(mainExCall,partExCall);
		
		listTool.addDefine(DataGroupType.Server,define0);
		listTool.addMaker(DataGroupType.Server,maker0);
		
		listTool.setInput(_dataProjectRoot + "/" + serverLcFront + "/list","LO");
		
		String inFilePath=_dataProjectRoot + "/" + serverLcFront + "/list/"+ff+"LO."+CodeType.getExName(CodeType.Java);
		String clientInPath=null;
		
		ClassInfo inCls=ClassInfo.getClassInfoFromPathAbs(inFilePath);
		
		listTool.setOnlyInFile(new File(inFilePath),inCls);
		
		listTool.addOutput(createForList(dataServerRootPath + "/part/" + serverLcFront + "/list",partServerRootPath + "/part/" + serverLcFront,DataGroupType.Server,""));
		
		//绑定归属
		listTool.setBelongExport(export1);
		
		if(_commonExport!=null)
			listTool.setCommonPathFunc(this::getCommonPath);
		
		if(partServerListTool!=null)
			listTool.setParentTool(partServerListTool);
		
		_serverPartListToolDic.put(nFront,listTool);
		
		
		PartListTool clientListTool=null;
		
		if(needClient)
		{
			clientListTool=new PartListTool();
			
			//不记录
			clientListTool.setFileRecordTool(_record);
			clientListTool.setMainName(ucFront);
			clientListTool.setProjectType(projectType);
			
			clientListTool.setExCalls(mainExCall,partExCall);
			
			clientListTool.setInput(_dataProjectRoot + "/" + serverLcFront + "/list","CLO");
			
			clientInPath=_dataProjectRoot + "/" + serverLcFront + "/list/"+ff+"CLO."+CodeType.getExName(CodeType.Java);
			
			clientListTool.setOnlyInFile(new File(clientInPath),getCLOCls(inCls));
			
			//clientListTool.addOutput(createForList(dataServerRootPath + "/" + serverLcFront + "/list",DataGroupType.Server,""));
			//clientListTool.setServerClsRoot(partServerRootPath + "/" + serverLcFront + "/list");
			
			//绑定归属
			clientListTool.setBelongExport(export2);
			
			if(_commonExport!=null)
				clientListTool.setCommonPathFunc(this::getCommonPath);
			
			if(partClientListTool!=null)
				clientListTool.setParentTool(partClientListTool);
			
			_clientPartListToolDic.put(nFront,clientListTool);
			
			
			define0=new DataDefineTool(dataServerRootPath + "/constlist/generate/" + ff + "ListClientDataType." + _serverExName,_nowStart,len,true);
			define1=new DataDefineTool(clientRoot + "/constlist/generate/" + ff + "ListDataType." + _clientExName,_nowStart,len,false);
			//define2=new DataDefineTool(projectRoots[ServerRobot] + "/constlist/generate/" + ff + "ListDataType." + _serverExName,_nowStart,len,false);
			_nowStart+=len;
			
			maker0=new DataMakerTool(dataServerRootPath + "/tool/generate/" + ff + "ListClientDataMaker." + _serverExName,define0);
			maker1=new DataMakerTool(clientRoot + "/tool/generate/" + ff + "ListDataMaker." + _clientExName,define1);
			//maker2=new DataMakerTool(projectRoots[ServerRobot] + "/tool/generate/" + ff + "ListDataMaker." + _serverExName,define2);
			
			clientListTool.addDefine(DataGroupType.ClientDefine,define0);
			if(ShineToolSetting.needClient)
				clientListTool.addDefine(DataGroupType.Client,define1);
			//clientListTool.addDefine(DataGroupType.Robot,define2);
			
			clientListTool.addMaker(DataGroupType.ClientDefine,maker0);
			if(ShineToolSetting.needClient)
				clientListTool.addMaker(DataGroupType.Client,maker1);
			//clientListTool.addMaker(DataGroupType.Robot,maker2);
			
			clientListTool.addOutput(createForList(dataServerRootPath + "/part/" + serverLcFront + "/list",projectRoots[ServerRobot] + "/part/" + serverLcFront,DataGroupType.ClientDefine,""));
			if(ShineToolSetting.needClient)
				clientListTool.addOutput(createForList(clientRoot + "/part/" + clientLcFront + "/list",clientRoot + "/part/" + clientLcFront,DataGroupType.Client,""));
			//clientListTool.addOutput(createForList(projectRoots[ServerRobot] + "/part/" + serverLcFront + "/list",DataGroupType.Robot,""));
			
			listTool.setClientTool(clientListTool);
		}
		
		listTool.executeAll();
		
		if(clientListTool!=null)
		{
			if(!listTool.getDidFiles().isEmpty())
			{
				clientListTool.setNeedDo(true);
			}
			
			clientListTool.executeAll();
		}
		
		//common和game都统计
		String qName=FileUtils.getPathPackage(inFilePath) + "." + ff + "LO";
		
		if(needCountServer)
		{
			countOneModify(dataServerType,qName);
		}
		
		if(needClient)
		{
			qName=FileUtils.getPathPackage(clientInPath) + "." + ff + "CLO";
			
			countOneModify(clientDataType,qName);
			
			//也统计message
			countOneMessage(qName);
		}
	}
	
	public void countOneModify(int type,String qName)
	{
		//game才统计
		//if(_isCommon)
		//	return;
		
		toCountOneModify(type,qName);
	}
	
	protected void toCountOneModify(int type,String qName)
	{
		_tempDic.clear();
		_tempSet.clear();
		if(!hasBigModifieds[type] && countOneNodeModified(qName))
		{
			hasBigModifieds[type]=true;
			
			RecordDataClassInfo.bigModifiedErrorStr(qName);
		}
		
		_tempSet.clear();
		countOneNodeStr(versionStrs[type],qName);
	}
	
	@Override
	protected void countOneMessage(String qName)
	{
		toCountOneModify(ClientMessage,qName);
	}
	
	/** 获取CLO转化 */
	private ClassInfo getCLOCls(ClassInfo inCls)
	{
		//再来一个
		ClassInfo re=ClassInfo.getClassInfoFromPathAbs(inCls.getPath());
		//换下名字
		re.clsName=re.clsName.substring(0,re.clsName.length()-2)+"CLO";
		
		for(String k : re.getFieldNameList())
		{
			FieldInfo field=re.getField(k);
			
			String v=field.type;
			
			if(v.endsWith("PO"))
			{
				String pp=re.getImportPackage(v);
				
				re.removeImportByClsName(v);
				
				pp=pp.substring(0,pp.lastIndexOf(".")) + ".client";
				v=v.substring(0,v.length() - 3) + "CPO";
				
				re.addImport(pp + "." + v);
				
				field.type=v;
			}
		}
		
		return re;
	}
	
	/** 构造列表数据(数据组合) */
	private void makeListDatas()
	{
		//Ctrl.print("listStart",_nowStart);
		
		setPartMethods();
		
		makeOneListDatas("CenterGlobal",false,_centerGlobalPartLen,ServerBase,ServerCenter,true,false,this::globalCenterMainEx,this::globalCenterPartEx);
		//makeOneListDatas("CenterPlayer",_centerPlayerPartLen,ServerBase,ServerCenter,false,false,false,this::centerPlayerMainEx,this::centerPlayerPartEx);
		_nowStart+=_centerPlayerPartLen;
		_nowStart+=_listLen;
		
		makeOneListDatas("GameGlobal",false,_gameGlobalPartLen,ServerBase,ServerGame,true,false,this::globalGameMainEx,this::globalGamePartEx);
		makeOneListDatas("Player",false,_playerPartLen,ServerBase,ServerGame,true,true,this::playerMainEx,this::playerPartEx);
		
		if(ShineToolSetting.needHotfix &&!_isCommon)
		{
			makeOneListDatas("HPlayer",true,_playerPartLen,ServerBase,ServerGame,true,true,this::playerMainEx,this::playerPartEx);
		}
		
		if(ShineToolSetting.outlineWorkUseTable)
		{
			countOneModify(ServerGame,_playerOfflineWorkListQName);
		}
		
		countOneModify(ClientHotfix,_clientPlayerLocalCacheQName);
		
		if(ShineToolSetting.useClientOfflineGame)
		{
			countOneModify(ClientHotfix,_clientOfflineWorkListQName);
			countOneModify(ClientHotfix,_playerOfflineCacheExQName);
		}
		
		//Ctrl.print("listEnd",_nowStart);
	}
	
	/** 客户端与服务器共有基础方法,center的Main也有 */
	private List<MethodOperate> _doubleBaseMethods=new ArrayList<>();
	/** 双端基础方法 */
	private List<MethodOperate> _baseMethods=new ArrayList<>();
	/** 主体方法,只server */
	private List<MethodOperate> _mainMethods=new ArrayList<>();
	/** 双端part方法 */
	private List<MethodOperate> _partMethods=new ArrayList<>();
	/** serverPlayer主方法 */
	private List<MethodOperate> _serverPlayerMethods=new ArrayList<>();
	/** clientPlayer主方法 */
	private List<MethodOperate> _clientPlayerMethods=new ArrayList<>();
	/** player part1方法 */
	private List<MethodOperate> _playerPart1Methods=new ArrayList<>();
	
	/** 添加方法的事务 */
	private MethodOperate createAddMethod(String name,String describe,int visitType)
	{
		return createAddMethod(name,describe,visitType,"");
	}
	
	/** 添加方法的事务 */
	private MethodOperate createAddMethod(String name,String describe,int visitType,String afterName,MethodArgInfo... args)
	{
		return createAddMethod(name,describe,visitType,afterName,null,args);
	}
	
	/** 添加方法的事务 */
	private MethodOperate createAddMethod(String name,String describe,int visitType,String afterName,ObjectCall<CodeWriter> exFunc,MethodArgInfo... args)
	{
		MethodOperate obj=new MethodOperate();
		
		MethodInfo method=new MethodInfo();
		method.name=name;
		method.describe=describe;
		method.visitType=visitType;
		
		for(MethodArgInfo arg : args)
		{
			method.args.add(arg);
		}
		
		if(afterName==null)
		{
			afterName="";
		}
		
		obj.method=method;
		obj.isAdd=true;
		obj.afterName=afterName;
		obj.exFunc=exFunc;
		
		return obj;
	}
	
	/** 移除方法的事务 */
	protected MethodOperate createRemoveMethod(String name,MethodArgInfo... args)
	{
		MethodOperate obj=new MethodOperate();
		
		MethodInfo method=new MethodInfo();
		method.name=name;
		
		for(MethodArgInfo arg : args)
		{
			method.args.add(arg);
		}
		
		obj.method=method;
		obj.isAdd=false;
		
		return obj;
	}
	
	private void addMethodToList(List<MethodOperate> list,MethodOperate obj)
	{
		if(list.size()>0 && obj.isAdd && obj.afterName.isEmpty())
		{
			obj.afterName=list.get(list.size() - 1).method.name;
		}
		
		list.add(obj);
	}
	
	/** 设置方法组 */
	private void setPartMethods()
	{
		//beforeMakeData由PartList做了,是protected的
		
		//addMethodToList(_doubleBaseMethods,createAddMethod("construct","构造函数(只在new后调用一次,再次从池中取出不会调用)",VisitType.Public,"",this::constructExFunc));
		addMethodToList(_doubleBaseMethods,createAddMethod("init","初始化(创建后刚调用,与dispose成对)",VisitType.Public,"construct"));
		addMethodToList(_doubleBaseMethods,createAddMethod("dispose","析构(回池前调用,与init成对)",VisitType.Public,"init"));
		addMethodToList(_doubleBaseMethods,createAddMethod("afterReadData","从库中读完数据后(做数据的补充解析)(onNewCreate后也会调用一次)(主线程)",VisitType.Public,"dispose"));
		
		
		addMethodToList(_baseMethods,createAddMethod("onNewCreate","新创建时(该主对象在服务器上第一次被创建时的动作(一生只一次),(只对数据赋值就好,自定义数据构造的部分写到afterReadData里,因为这个完事儿就会回调到))",VisitType.Public));
		
		addMethodToList(_mainMethods,createAddMethod("construct","构造函数(只在new后调用一次,再次从池中取出不会调用)",VisitType.Public,"",this::constructExFunc));
		addMethodToList(_mainMethods,createAddMethod("newInitData","初次构造数据(只为new出Data,跟onCreate不是一回事)",VisitType.Public));
		addMethodToList(_mainMethods,createAddMethod("onSecond","每秒调用",VisitType.Public,"newInitData",new MethodArgInfo("delay","int")));
		addMethodToList(_mainMethods,createAddMethod("onDaily","每天调用(上线时如隔天也会调用,)",VisitType.Public,"onSecond"));
		addMethodToList(_mainMethods,createAddMethod("onReloadConfig","配置表更新后(配置替换)",VisitType.Public,"onDaily"));
		addMethodToList(_mainMethods,createAddMethod("onActivityOpen","活动开启",VisitType.Public,"onReloadConfig",new MethodArgInfo("id","int"),new MethodArgInfo("atTime","boolean")));
		addMethodToList(_mainMethods,createAddMethod("onActivityClose","活动关闭",VisitType.Public,"onActivityOpen",new MethodArgInfo("id","int"),new MethodArgInfo("atTime","boolean")));
		addMethodToList(_mainMethods,createAddMethod("onActivityReset","活动重置",VisitType.Public,"onActivityClose",new MethodArgInfo("id","int"),new MethodArgInfo("atTime","boolean")));
		
		addMethodToList(_partMethods,createAddMethod("construct","构造函数(只在new后调用一次,再次从池中取出不会调用)",VisitType.Public,""));
		addMethodToList(_partMethods,createAddMethod("beforeMakeData","构造数据前",VisitType.Protected));
		
		addMethodToList(_playerPart1Methods,createAddMethod("writeClientData","写客户端数据(copyServer过后的)",VisitType.Protected,"",new MethodArgInfo("data","BaseClientPartData")));
		
		
		//addMethodToList(_serverPlayerMethods,createAddMethod("onLeave","登出(每次角色登出或切出时调用)",VisitType.Public));
		addMethodToList(_serverPlayerMethods,createAddMethod("onFunctionOpen","功能开启(id:功能ID)",VisitType.Public,"",new MethodArgInfo("id","int")));
		addMethodToList(_serverPlayerMethods,createAddMethod("onFunctionClose","功能关闭(id:功能ID)",VisitType.Public,"",new MethodArgInfo("id","int")));
		
		
		addMethodToList(_clientPlayerMethods,createAddMethod("onFunctionOpen","功能开启(id:功能ID)",VisitType.Public,"",new MethodArgInfo("id","int")));
		addMethodToList(_clientPlayerMethods,createAddMethod("onFunctionClose","功能关闭(id:功能ID)",VisitType.Public,"",new MethodArgInfo("id","int")));
	}
	
	private void constructExFunc(CodeWriter writer)
	{
		writer.writeCustom("registParts();");
		writer.writeEmptyLine();
	}
	
	/** 执行主体方法组 */
	private void doMethodEx(ClassInfo cls,List<MethodOperate> list,boolean isMain,boolean hasExtend,String partBaseName)
	{
		CodeInfo sourceCode=CodeInfo.getCode(CodeType.Java);
		
		CodeInfo code=cls.getCode();
		
		CodeWriter writer;
		
		for(MethodOperate obj : list)
		{
			if(obj.isAdd)
			{
				MethodInfo info=obj.method;
				
				MethodInfo method=cls.getMethodByName(info.name);
				
				boolean need=false;
				
				if(method==null)// || isMain //main的强制刷新
				{
					method=new MethodInfo();
					need=true;
				}
				
				method.name=info.name;
				method.describe=info.describe;
				method.visitType=info.visitType;
				
				if(isMain)
				{
					method.isVirtual=_isCommon;
					method.isOverride=!_isCommon;
				}
				else
				{
					method.isVirtual=false;
					method.isOverride=true;
				}
				
				method.isStatic=false;
				method.returnType=code.Void;
				
				method.args.clear();
				
				for(MethodArgInfo arg : info.args)
				{
					MethodArgInfo tArg=arg.clone();
					
					int baseVarType=sourceCode.getBaseVarType(tArg.type);
					
					if(baseVarType!=-1)
					{
						//替换基础类型
						tArg.type=code.getBaseVarInputStr(baseVarType);
					}
					
					method.args.add(tArg);
				}
				
				if(need)
				{
					String args;
					
					if(method.args.size()>0)
					{
						StringBuilder sb=new StringBuilder();
						
						for(int i=0;i<method.args.size();++i)
						{
							if(i>0)
							{
								sb.append(",");
							}
							
							sb.append(method.args.get(i).name);
						}
						
						args=sb.toString();
						
						
					}
					else
					{
						args="";
					}
					
					writer=cls.createWriter();
					
					if(hasExtend)
					{
						writer.writeSuperMethod(method);
						writer.writeEmptyLine();
					}
					
					if(obj.exFunc!=null)
						obj.exFunc.apply(writer);
					
					if(isMain)
					{
						writer.writeVarCreate("list",code.getArrayType(partBaseName,true),cls.getFieldWrap("_list"));
						writer.writeForEachArray("list","v",partBaseName,false);
						writer.writeCustom(code.getArrayElement("list","vI") + "." + method.name + "(" + args + ");");
						writer.writeForEachArrayEnd(partBaseName);
					}
					
					writer.writeEnd();
					
					method.content=writer.toString();
				}
				
				cls.addMethodAfterName(method,obj.afterName,false);
			}
			else
			{
				cls.removeMethod(obj.method);
			}
		}
	}
	
	
	
	//private ObjectCall2<ClassInfo,Boolean> createMain(String partBaseName)
	//{
	//	if(_isCommon)
	//	{
	//		doMethodEx(cls,_doubleBaseMethods,true,!_isCommon,"MainGlobalBasePart");
	//		doMethodEx(cls,_baseMethods,true,!_isCommon,"MainGlobalBasePart");
	//		doMethodEx(cls,_mainMethods,true,!_isCommon,"MainGlobalBasePart");
	//	}
	//}
	
	private void doGlobalMain(ClassInfo cls,String partBaseName)
	{
		if(_isCommon)
		{
			doMethodEx(cls,_doubleBaseMethods,true,!_isCommon,partBaseName);
			doMethodEx(cls,_baseMethods,true,!_isCommon,partBaseName);
			doMethodEx(cls,_mainMethods,true,!_isCommon,partBaseName);
		}
	}
	
	private void doGlobalPart(ClassInfo cls,boolean hasGame,boolean hasExtends,String partBaseName)
	{
		if(_isCommon || hasGame)
		{
			doMethodEx(cls,_doubleBaseMethods,false,hasExtends,partBaseName);
			doMethodEx(cls,_baseMethods,false,hasExtends,partBaseName);
			doMethodEx(cls,_partMethods,false,hasExtends,partBaseName);
		}
		
		//doMethodEx(cls,_doubleBaseMethods,false,hasExtends,"CenterGlobalBasePart");
		//
		//if(isServer)
		//{
		//	doMethodEx(cls,_baseMethods,false,hasExtends,"CenterGlobalBasePart");
		//	doMethodEx(cls,_partMethods,false,hasExtends,"CenterGlobalBasePart");
		//}
	}
	
	//private void globalMainMainEx(ClassInfo cls,boolean isServer)
	//{
	//	doGlobalMain(cls,"MainGlobalBasePart");
	//}
	//
	//private void globalMainPartEx(ClassInfo cls,boolean isServer,boolean hasExtends)
	//{
	//	doGlobalPart(cls,false,hasExtends,"MainGlobalBasePart");
	//}
	
	private void globalCenterMainEx(ClassInfo cls,boolean isServer)
	{
		doGlobalMain(cls,"CenterGlobalBasePart");
		
	}
	
	private void globalCenterPartEx(ClassInfo cls,boolean isServer,boolean hasExtends)
	{
		doGlobalPart(cls,true,hasExtends,"CenterGlobalBasePart");
		
		deleteMethod(cls);
	}
	
	private void globalGameMainEx(ClassInfo cls,boolean isServer)
	{
		doGlobalMain(cls,"GameGlobalBasePart");
		
	}
	
	private void globalGamePartEx(ClassInfo cls,boolean isServer,boolean hasExtends)
	{
		doGlobalPart(cls,true,hasExtends,"GameGlobalBasePart");
		
		deleteMethod(cls);
	}
	
	private void centerPlayerMainEx(ClassInfo cls,boolean isServer)
	{
		if(_isCommon)
		{
			doMethodEx(cls,_doubleBaseMethods,true,!_isCommon,"CenterPlayerBasePart");
			
			doMethodEx(cls,_baseMethods,true,!_isCommon,"CenterPlayerBasePart");
			doMethodEx(cls,_mainMethods,true,!_isCommon,"CenterPlayerBasePart");
			doMethodEx(cls,_serverPlayerMethods,true,!_isCommon,"CenterPlayerBasePart");
		}
		
	}
	
	private void centerPlayerPartEx(ClassInfo cls,boolean isServer,boolean hasExtends)
	{
		doMethodEx(cls,_doubleBaseMethods,false,hasExtends,"CenterPlayerBasePart");
		
		doMethodEx(cls,_baseMethods,false,hasExtends,"CenterPlayerBasePart");
		doMethodEx(cls,_partMethods,false,hasExtends,"CenterPlayerBasePart");
		
		doMethodEx(cls,_serverPlayerMethods,false,hasExtends,"CenterPlayerBasePart");
		
		deleteMethod(cls);
	}
	
	private void playerMainEx(ClassInfo cls,boolean isServer)
	{
		if(_isCommon)
		{
			doMethodEx(cls,_doubleBaseMethods,true,!_isCommon,"PlayerBasePart");
			
			doMethodEx(cls,_baseMethods,true,!_isCommon,"PlayerBasePart");
			doMethodEx(cls,_mainMethods,true,!_isCommon,"PlayerBasePart");
			
			if(isServer)
			{
				doMethodEx(cls,_serverPlayerMethods,true,!_isCommon,"PlayerBasePart");
			}
		}
		
	}
	
	private void playerPartEx(ClassInfo cls,boolean isServer,boolean hasExtends)
	{
		doMethodEx(cls,_doubleBaseMethods,false,hasExtends,"PlayerBasePart");
		
		if(isServer)
			doMethodEx(cls,_baseMethods,false,hasExtends,"PlayerBasePart");
		
		doMethodEx(cls,_partMethods,false,hasExtends,"PlayerBasePart");
		
		if(isServer)
		{
			doMethodEx(cls,_playerPart1Methods,false,hasExtends,"PlayerBasePart");
			doMethodEx(cls,_serverPlayerMethods,false,hasExtends,"PlayerBasePart");
		}
		else
		{
			doMethodEx(cls,_clientPlayerMethods,false,hasExtends,"PlayerBasePart");
		}
		
		deleteMethod(cls);
	}
	
	private void deleteMethod(ClassInfo cls)
	{
		//String name="onReloadConfig";
		//
		//MethodInfo method=cls.getMethodByName(name);
		//
		//if(method!=null && method.isEmpty(cls.getCodeType()))
		//{
		//	cls.removeMethod(method);
		//}
	}
	
	private class MethodOperate
	{
		public boolean isAdd;
		/** 在某名字后 */
		public String afterName;
		
		public MethodInfo method;
		
		public ObjectCall<CodeWriter> exFunc;
	}
}
