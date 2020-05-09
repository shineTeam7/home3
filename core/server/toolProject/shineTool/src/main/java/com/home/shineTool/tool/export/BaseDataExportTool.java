package com.home.shineTool.tool.export;

import java.util.ArrayList;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.SMap;
import com.home.shine.support.collection.SSet;
import com.home.shine.support.collection.StringIntMap;
import com.home.shine.support.func.ObjectCall;
import com.home.shine.utils.StringUtils;
import com.home.shineTool.constlist.CodeType;
import com.home.shineTool.constlist.DataGroupType;
import com.home.shineTool.constlist.MakeMethodType;
import com.home.shineTool.constlist.ProjectType;
import com.home.shineTool.constlist.VisitType;
import com.home.shineTool.global.ShineToolGlobal;
import com.home.shineTool.reflect.FieldInfo;
import com.home.shineTool.reflect.MethodInfo;
import com.home.shineTool.reflect.cls.ClassInfo;
import com.home.shineTool.reflect.code.CodeWriter;
import com.home.shineTool.tool.FileRecordTool;
import com.home.shineTool.tool.base.BaseExportTool;
import com.home.shineTool.global.ShineToolSetting;
import com.home.shineTool.global.ShineToolSetting.GroupPackageData;
import com.home.shineTool.tool.base.RecordDataClassInfo;
import com.home.shineTool.tool.data.DataDefineTool;
import com.home.shineTool.tool.data.DataExportTool;
import com.home.shineTool.tool.data.DataMakerTool;
import com.home.shineTool.tool.data.DataOutputInfo;
import com.home.shineTool.tool.data.DataMessageBindTool;
import com.home.shineTool.tool.part.PartListOutputInfo;
import com.home.shineTool.tool.part.PartOutputInfo;

/** 数据构造基础封装 */
public abstract class BaseDataExportTool
{
	/** 文件记录 */
	protected FileRecordTool _record;
	
	protected int _serverCodeType=CodeType.Java;
	/** 客户端代码类型 */
	protected int _clientCodeType=0;
	
	protected String _serverExName;
	protected String _clientExName;
	
	/** 是否公共 */
	protected boolean _isCommon=false;
	/** 前缀 */
	protected String _front="";
	
	//temp
	protected static SMap<String,DataExportTool> _dataExports=new SMap<>();
	
	//ilruntime
	
	//protected String _adapterPath;
	//protected ClassInfo _ILRuntimeControlCls;
	//protected CodeWriter _IlRuntimeWriter;
	
	protected SMap<String,Boolean> _tempDic=new SMap<>();
	protected SSet<String> _tempSet=new SSet<>();
	
	/** 构造shine的包 */
	public static void makeShinePackage()
	{
		ShineToolSetting.groupPackageChangeDic.computeIfAbsent(0,k->new ArrayList<>()).add(new GroupPackageData(ShineToolSetting.globalPackage + "shineData.data",ShineToolSetting.globalPackage + "shine.data","DO","Data"));
		ShineToolSetting.groupPackageChangeDic.computeIfAbsent(1,k->new ArrayList<>()).add(new GroupPackageData(ShineToolSetting.globalPackage + "shineData.data",ShineToolSetting.globalPackage + "shine.data","DO","Data"));
		ShineToolSetting.groupPackageChangeDic.computeIfAbsent(2,k->new ArrayList<>()).add(new GroupPackageData(ShineToolSetting.globalPackage + "shineData.data",ShineToolSetting.globalPackage + "shine.data","DO","Data"));
	}
	
	public void setIsCommon(boolean bool)
	{
		_isCommon=bool;
		_front=_isCommon ? "" : "G";
	}
	
	/** 标准初始化 */
	public void initNormal(String recordName)
	{
		_clientCodeType=ShineToolSetting.getClientCodeType();
		
		_record=new FileRecordTool(ShineToolGlobal.recordPath + "/" + recordName + ".xml");
		_record.setVersion(ShineToolSetting.dataExportVersion);
		
		_serverExName=CodeType.getExName(_serverCodeType);
		_clientExName=CodeType.getExName(_clientCodeType);
		
		initNext();
	}
	
	public void execute()
	{
		_record.read();
		
		//shine
		makeShinePackage();
		
		toExecute();
		
		//		toCountModify();
		
		over();
	}
	
	/** 注册定义 */
	public void registDefine()
	{
		//shine
		makeShinePackage();
		
	}
	
	/** 获取文件记录工具 */
	public FileRecordTool getRecordTool()
	{
		return _record;
	}
	
	/** 结束 */
	private void over()
	{
		//error处理
		
		if(_record!=null)
		{
			_record.deleteLastFiles(false);
			_record.write();
		}
	}
	
	protected int countProjectType(boolean isH)
	{
		if(_isCommon)
		{
			return ProjectType.Common;
		}
		else
		{
			if(isH)
			{
				return ProjectType.HotFix;
			}
			else
			{
				return ProjectType.Game;
			}
		}
	}
	
	protected abstract void initNext();
	
	protected abstract void toExecute();
	
	//	protected abstract void toCountModify();
	
	private void makeClientOutput(DataOutputInfo info)
	{
		if(_clientCodeType==CodeType.TS)
		{
			info.needs[MakeMethodType.readFull]=false;
			info.needs[MakeMethodType.writeFull]=false;
			info.needs[MakeMethodType.toWriteDataString]=false;
			info.needs[MakeMethodType.dataEquals]=false;
		}
	}
	
	/** 构建Data所需 */
	protected DataOutputInfo createForData(String path,int group)
	{
		DataOutputInfo re=new DataOutputInfo();
		re.path=path;
		re.nameTail="Data";
		re.setGroup(group);
		
		re.needAll();
		
		switch(group)
		{
			//服务器
			case DataGroupType.Server:
			{
				re.codeType=_serverCodeType;
			}
				break;
			//客户端
			case DataGroupType.Client:
			{
				re.codeType=_clientCodeType;
				
				//data部分要完整的
				
				//re.needs[MakeMethodType.readFull]=false;
				//re.needs[MakeMethodType.writeFull]=false;
				
				makeClientOutput(re);
			}
				break;
			//机器人
			case DataGroupType.Robot:
			{
				re.codeType=_serverCodeType;
				
				//re.needs[MakeMethodType.readFull]=false;
				//re.needs[MakeMethodType.writeFull]=false;
			}
				break;
		}
		
		return re;
	}
	
	/** 构建Result所需 */
	private DataOutputInfo createForResult(String path,int group,boolean isClient)
	{
		DataOutputInfo re=new DataOutputInfo();
		re.path=path;
		re.nameTail="Result";
		re.setGroup(group);
		re.makeIndex=-1;
		
		switch(group)
		{
			//服务器
			case DataGroupType.Server:
			{
				re.codeType=_serverCodeType;
			}
				break;
			//客户端
			case DataGroupType.Client:
			{
				re.codeType=_clientCodeType;
				
				makeClientOutput(re);
			}
				break;
			//机器人
			case DataGroupType.Robot:
			{
				re.codeType=_serverCodeType;
			}
				break;
		}
		
		makeDataOutputInfoRead(re,isClient);
		makeDataOutputInfoWrite(re,isClient);
		
		return re;
	}
	
	private ClassInfo ensureSuperCls(DataExportTool tool,int group,String superClsPath,String baseQName,ObjectCall<ClassInfo> func)
	{
		ClassInfo cls=ClassInfo.getClassInfoFromPath(superClsPath);
		
		if(cls==null)
		{
			cls=ClassInfo.getClassInfoFromPathAbs(superClsPath);
			
			DataExportTool upTool=tool.getUpTool();
			
			if(upTool!=null)
			{
				DataOutputInfo outputInfo=(DataOutputInfo)upTool.getOutputInfo(group);
				
				ClassInfo sCls=outputInfo.superCls;
				
				if(sCls==null)
				{
					Ctrl.throwError("不应该找不到");
				}
				else
				{
					cls.addImport(sCls.getQName());
					cls.extendsClsName=sCls.clsName;
				}
			}
			else
			{
				cls.addImport(baseQName);
				cls.extendsClsName=StringUtils.getClassNameForQName(baseQName);
			}
			
			cls.setNeedGenerateMark(false);
			
			func.apply(cls);
			
			cls.write();
		}
		
		return cls;
	}
	
	private void makeDataOutputInfoWrite(DataOutputInfo re,boolean isClient)
	{
		if(isClient && (_isCommon || ShineToolSetting.useMessageFull))
		{
			re.needs[MakeMethodType.writeFull]=true;
			
			if(_isCommon)
				re.needs[MakeMethodType.writeSimple]=true;
		}
		else
		{
			re.needs[MakeMethodType.writeSimple]=true;
		}
	}
	
	private void makeDataOutputInfoRead(DataOutputInfo re,boolean isClient)
	{
		if(isClient && (_isCommon || ShineToolSetting.useMessageFull))
		{
			re.needs[MakeMethodType.readFull]=true;
			
			if(_isCommon)
				re.needs[MakeMethodType.readSimple]=true;
		}
		else
		{
			re.needs[MakeMethodType.readSimple]=true;
		}
	}
	
	/** 构建Request所需 */
	protected void createForRequest(DataExportTool tool,String path,int group,String middle,String superClsPath,boolean isClient)
	{
		DataOutputInfo re=new DataOutputInfo();
		re.path=path;
		re.setGroup(group);
		re.nameTail=middle + "Request";
		
		ClassInfo cls=ensureSuperCls(tool,group,superClsPath,ShineToolSetting.baseRequestQName,v->
		{
		
		});
		
		re.superCls=cls;
		
		re.superQName=cls.getQName();
		//re.makeIndex=-1;
		
		makeDataOutputInfoWrite(re,isClient);
		
		re.needs[MakeMethodType.toWriteDataString]=true;
		re.needs[MakeMethodType.release]=true;
		
		re.isCreateSetParams=true;
		re.createSetUsePool=true;
		//re.defaultVarVisitType=VisitType.Public;
		
		re.outputExFunc=this::requestEx;
		
		re.isRequest=true;
		
		switch(group)
		{
			//服务器
			case DataGroupType.Server:
			{
				re.codeType=_serverCodeType;
			}
				break;
			//客户端
			case DataGroupType.Client:
			{
				re.codeType=_clientCodeType;
				
				makeClientOutput(re);
			}
				break;
			//机器人
			case DataGroupType.Robot:
			{
				re.codeType=_serverCodeType;
			}
				break;
		}
		
		tool.addOutput(re);
	}
	
	/** 响应附加行为 */
	private void requestEx(int group,ClassInfo inCls,ClassInfo cls)
	{
		replaceF(cls);
	}
	
	private void replaceF(ClassInfo cls)
	{
		//SList<String> ss=new SList<>();
		//ss.addAll(cls.getFieldNameList());
		//
		//for(String k :ss)
		//{
		//	FieldInfo field=cls.getField(k);
		//
		//	if(field!=null && !field.name.startsWith("_"))
		//	{
		//		String nn="_"+field.name;
		//
		//		cls.removeField(nn);
		//	}
		//}
	}
	
	/** 构建Response所需 */
	protected void createForResponse(DataExportTool tool,String path,int group,String middle,String superClsPath,boolean isClient)
	{
		DataOutputInfo re=new DataOutputInfo();
		re.path=path;
		re.setGroup(group);
		
		//为了serverMessage
		if(group==DataGroupType.Server2)
		{
			re.defineIndex=DataGroupType.Server;
			//re.makeIndex=DataGroupType.Server;
		}
		
		re.nameTail=middle + "Response";
		
		ClassInfo cls=ensureSuperCls(tool,group,superClsPath,ShineToolSetting.baseResponseQName,v->
		{
			v.isAbstract=true;
		});
		
		re.superCls=cls;
		re.superQName=cls.getQName();
		
		makeDataOutputInfoRead(re,isClient);
		
		re.needs[MakeMethodType.toWriteDataString]=true;
		re.needs[MakeMethodType.release]=true;
		
		//re.defaultVarVisitType=VisitType.Public;
		
		re.outputExFunc=this::responseEx;
		
		re.isResponse=true;
		
		switch(group)
		{
			//服务器
			case DataGroupType.Server:
			{
				re.codeType=_serverCodeType;
			}
				break;
			//客户端
			case DataGroupType.Client:
			{
				re.codeType=_clientCodeType;
				makeClientOutput(re);
			}
				break;
			//机器人
			case DataGroupType.Robot:
			{
				re.codeType=_serverCodeType;
			}
				break;
		}
		
		tool.addOutput(re);
	}
	
	/** 响应附加行为 */
	private void responseEx(int group,ClassInfo inCls,ClassInfo cls)
	{
		if(!cls.isAbstract)
		{
			MethodInfo method;
			
			//if(group==0 && inCls.hasAnnotation("MessageUseMainThread"))
			//{
			//	method=new MethodInfo();
			//	method.name="dispatch";
			//	method.visitType=VisitType.Public;
			//	method.isOverride=true;
			//
			//	CodeWriter writer=CodeWriter.createByType(cls.getCodeType());
			//
			//	String qName=ShineToolSetting.globalPackage + "shine.constlist.ThreadType";
			//
			//	cls.addImport(qName);
			//
			//	writer.writeVarSet("_threadType","ThreadType.Common");
			//	writer.writeCustom("doDispatch();");
			//	writer.writeEnd();
			//
			//	method.content=writer.toString();
			//
			//	cls.addMethod(method);
			//}
			
			method=cls.getMethodByName("execute");
			
			if(method==null)
			{
				method=new MethodInfo();
				method.visitType=VisitType.Protected;
				method.name="execute";
				method.isOverride=true;
				method.describe="执行";
				
				CodeWriter writer=cls.createWriter();
				writer.writeEnd();
				
				method.content=writer.toString();
				
				cls.addMethod(method);
			}
			else
			{
				method.visitType=VisitType.Protected;
				//method.describe="执行";
			}
			
			replaceF(cls);
			//DataExportTool.replaceField(cls,method);
		}
	}
	
	/** 构建Request所需 */
	private void createForHttpRequest(DataExportTool tool,String path,int group,String middle,String superClsPath,boolean isClient)
	{
		DataOutputInfo re=new DataOutputInfo();
		re.path=path;
		re.setGroup(group);
		re.nameTail=middle + "HttpRequest";
		
		ClassInfo cls=ensureSuperCls(tool,group,superClsPath,ShineToolSetting.baseHttpRequestQName,v->
		{
			v.isAbstract=true;
		});
		
		re.superCls=cls;
		re.superQName=cls.getQName();
		re.makeIndex=-1;
		
		makeDataOutputInfoWrite(re,isClient);
		
		re.isCreateSetParams=true;
		//re.defaultVarVisitType=VisitType.Protected;
		re.outputExFunc=this::httpRequestEx;
		
		switch(group)
		{
			//服务器
			case DataGroupType.Server:
			{
				re.codeType=_serverCodeType;
			}
				break;
			//客户端
			case DataGroupType.Client:
			{
				re.codeType=_clientCodeType;
			}
				break;
			//机器人
			case DataGroupType.Robot:
			{
				re.codeType=_serverCodeType;
			}
				break;
		}
		
		tool.addOutput(re);
	}
	
	/** 响应附加行为 */
	private void httpRequestEx(int group,ClassInfo inCls,ClassInfo cls)
	{
		if(!cls.isAbstract)
		{
			String nn="re";
			
			FieldInfo field=cls.getField(nn);
			
			if(field==null)
			{
				nn="_re";
				field=cls.getField(nn);
			}
			
			MethodInfo method;
			
			if(field!=null)
			{
				method=new MethodInfo();
				method.visitType=VisitType.Protected;
				method.name="toRead";
				method.isOverride=true;
				method.describe="执行";
				
				CodeWriter writer=cls.createWriter();
				writer.writeVarSet(cls.getFieldWrap(nn),cls.getCode().createNewObject(field.type));
				writer.writeCustom( cls.getThisFront()+"readResult("+cls.getFieldWrap(nn)+","+cls.getFieldWrap("_resultStream);"));
				writer.writeEnd();
				
				method.content=writer.toString();
				
				cls.addMethod(method);
				
				method=cls.getMethodByName("getResult");
				
				if(method==null)
				{
					method=new MethodInfo();
					method.visitType=VisitType.Public;
					method.name="getResult";
					method.describe="获取结果";
					method.returnType=field.type;
					
					writer=cls.createWriter();
					writer.writeCustom("return "+cls.getFieldWrap(field.name)+";");
					writer.writeEnd();
					
					method.content=writer.toString();
					
					cls.addMethod(method);
				}
			}
			else
			{
				cls.removeMethodByName("toRead");
				cls.removeMethodByName("getResult");
			}
			
			method=cls.getMethodByName("onComplete");
			
			if(method==null)
			{
				method=new MethodInfo();
				method.visitType=VisitType.Protected;
				method.name="onComplete";
				method.isOverride=true;
				method.describe="执行";
				
				CodeWriter writer=cls.createWriter();
				writer.writeEnd();
				
				method.content=writer.toString();
				
				cls.addMethod(method);
			}
			else
			{
				method.visitType=VisitType.Protected;
			}
			
			if(!inCls.isAbstract)
			{
				//sendMSync
				method=new MethodInfo();
				method.visitType=VisitType.Public;
				method.name="sendMSync";
				method.describe="同步执行";
				
				CodeWriter writer=cls.createWriter();
				writer.writeCustom(cls.getFieldWrap("doSendSync();"));
				
				if(field!=null)
				{
					method.returnType=field.type;
					writer.writeCustom("return "+cls.getFieldWrap(field.name)+";");
				}
				else
				{
					method.returnType=cls.getCode().Void;
					writer.writeCustom("return;");
				}
				
				writer.writeEnd();
				method.content=writer.toString();
				cls.addMethod(method);
			}
			else
			{
				cls.removeMethodByName("sendMSync");
			}
			
			replaceF(cls);
		}
	}
	
	/** 构建HttpResponse所需 */
	public void createForHttpResponse(DataExportTool tool,String path,int group,String middle,String superClsPath,boolean isClient)
	{
		DataOutputInfo re=new DataOutputInfo();
		re.path=path;
		re.setGroup(group);
		re.nameTail=middle + "HttpResponse";
		
		ClassInfo cls=ensureSuperCls(tool,group,superClsPath,ShineToolSetting.baseHttpResponseQName,v->
		{
			v.isAbstract=true;
		});
		
		re.superCls=cls;
		re.superQName=cls.getQName();
		
		makeDataOutputInfoRead(re,isClient);
		
		//re.defaultVarVisitType=VisitType.Protected;
		re.outputExFunc=this::httpResponseEx;
		
		switch(group)
		{
			//服务器
			case DataGroupType.Server:
			{
				re.codeType=_serverCodeType;
			}
				break;
			//客户端
			case DataGroupType.Client:
			{
				re.codeType=_clientCodeType;
			}
				break;
			//机器人
			case DataGroupType.Robot:
			{
				re.codeType=_serverCodeType;
			}
				break;
		}
		
		tool.addOutput(re);
	}
	
	/** 响应附加行为 */
	private void httpResponseEx(int group,ClassInfo inCls,ClassInfo cls)
	{
		if(!cls.isAbstract)
		{
			MethodInfo method=cls.getMethodByName("execute");
			
			if(method==null)
			{
				method=new MethodInfo();
				method.visitType=VisitType.Protected;
				method.name="execute";
				method.isOverride=true;
				method.describe="执行";
				
				CodeWriter writer=cls.createWriter();
				writer.writeEnd();
				
				method.content=writer.toString();
				
				cls.addMethod(method);
			}
			else
			{
				//method.describe="执行";
			}
			
			replaceF(cls);
		}
	}
	
	/** 构建Part所需(type:0:base,1:server,2:client) */
	protected DataOutputInfo createForPart(String path,int group,String middle,int type)
	{
		PartOutputInfo re=new PartOutputInfo();
		re.path=path;
		re.setGroup(group);
		re.type=type;
		re.nameTail=middle + "PartData";
		re.isCreateSetParams=false;
		
		//服务器的客户端的话
		if(re.group==DataGroupType.Server && type==2)
		{
			re.superQName=ShineToolSetting.clientBasePartDataQName;
		}
		
		switch(type)
		{
			case 0:
			case 1:
			case 2:
			{
				re.needAll();
			}
				break;
			//case 2:
			//{
			//	re.needs[MakeMethodType.readSimple]=true;
			//	re.needs[MakeMethodType.writeSimple]=true;
			//	re.needs[MakeMethodType.copy]=true;
			//	re.needs[MakeMethodType.shadowCopy]=true;
			//}
			//	break;
		}
		
		switch(group)
		{
			//服务器
			case DataGroupType.Server:
			{
				re.codeType=_serverCodeType;
			}
				break;
			//客户端
			case DataGroupType.Client:
			{
				re.codeType=_clientCodeType;
			}
				break;
			//机器人
			case DataGroupType.Robot:
			{
				re.codeType=_serverCodeType;
			}
				break;
		}
		
		return re;
	}
	
	/** 构建Part所需(type:0:base,1:server,2:client) */
	protected PartListOutputInfo createForList(String dataPath,String partPath,int group,String middle)
	{
		PartListOutputInfo re=new PartListOutputInfo();
		re.path=dataPath;
		re.partPath=partPath;
		re.setGroup(group);
		re.nameTail=middle + "ListData";
		//		re.superQName=ShineToolSetting.globalPackage+defaultSuperQName;
		re.isCreateSetParams=false;
		
		re.needAll();
		re.needs[MakeMethodType.copySelf]=false;
		re.needs[MakeMethodType.initDefault]=false;
		//re.needs[MakeMethodType.initFields]=true;
		
		//全复写
		re.isOverrideSuper=true;

		switch(group)
		{
			//服务器
			case DataGroupType.Server:
			{
				re.codeType=_serverCodeType;
			}
				break;
			//服务器客户端定义
			case DataGroupType.ClientDefine:
			{
				re.codeType=_serverCodeType;
				re.nameTail=middle+"ClientListData";
			}
				break;
			//客户端
			case DataGroupType.Client:
			{
				re.codeType=_clientCodeType;
			}
				break;
			//机器人
			case DataGroupType.Robot:
			{
				re.codeType=_serverCodeType;
			}
				break;
		}
		
		return re;
	}
	
	/** 协议过滤方法(0:正常(都有)1:request,response都无,2:只有response(makerIndex==-1的不导出)) */
	protected int messageFilter(String fileName)
	{
		if(fileName.endsWith("TransMO"))
		{
			return 1;
		}
		
		if(fileName.endsWith("TransPassMO"))
		{
			return 2;
		}
		
		return 0;
	}
	
	protected String getPathLast(String path)
	{
		if(path==null)
			return "";
		
		String last=path.substring(path.lastIndexOf("/") + 1,path.length());
		
		if(_isCommon)
		{
			return last;
		}
		
		return last;
		//		return _front3+StringUtils.ucWord(last);
	}
	
	/** 获取common工程对应的路径 */
	public String getCommonPath(String path)
	{
		return path;
	}
	
	/** 构造一个data(不包含execute) */
	protected DataExportTool makeOneData(String key,String front,boolean isH,String inputPath,String serverPath,String clientPath,String robotPath,int start,int len,boolean needAdapter,BaseExportTool.InputFilter filter,DataExportTool parent)
	{
		DataDefineTool define0=new DataDefineTool(serverPath + "/constlist/generate/" + front + "DataType." + _serverExName,start,len,true);
		DataDefineTool define1=new DataDefineTool(clientPath + "/constlist/generate/" + front + "DataType." + _clientExName,start,len,false);
		
		DataMakerTool maker0=new DataMakerTool(serverPath + "/tool/generate/" + front + "DataMaker." + _serverExName,define0);
		DataMakerTool maker1=new DataMakerTool(clientPath + "/tool/generate/" + front + "DataMaker." + _clientExName,define1);
		
		DataDataExportTool export=new DataDataExportTool();
		export.setRootDataExportTool(this);
		export.setFileRecordTool(_record);
		export.setProjectType(countProjectType(isH));
		export.addDefine(DataGroupType.Server,define0);
		if(ShineToolSetting.needClient)
			export.addDefine(DataGroupType.Client,define1);
		export.addMaker(DataGroupType.Server,maker0);
		if(ShineToolSetting.needClient)
			export.addMaker(DataGroupType.Client,maker1);
		
		if(robotPath!=null)
		{
			DataDefineTool define2=new DataDefineTool(robotPath + "/constlist/generate/" + front + "DataType." + _serverExName,start,len,false);
			DataMakerTool maker2=new DataMakerTool(robotPath + "/tool/generate/" + front + "DataMaker." + _serverExName,define2);
			export.addDefine(DataGroupType.Robot,define2);
			export.addMaker(DataGroupType.Robot,maker2);
		}
		
		if(_isCommon)
		{
			_dataExports.put(key,export);
		}
		else
		{
			export.setCommonTool(_dataExports.get(key));
		}
		
		export.addParentTool(parent);
		
		export.setInput(inputPath,"DO",filter);
		
		//export.setHotfix(needAdapter,_adapterPath,_IlRuntimeWriter);
		
		String dataDirName=key.equals("hotfixData") ? "dataH" : "data";
		
		DataOutputInfo out=createForData(serverPath + "/"+dataDirName,DataGroupType.Server);
		
		export.addOutput(out);
		
		if(ShineToolSetting.needClient)
			export.addOutput(createForData(clientPath + "/data",DataGroupType.Client));
		
		if(robotPath!=null)
		{
			export.addOutput(createForData(robotPath + "/data",DataGroupType.Robot));
		}
		else
		{
			//记录robot的package
			export.recordGroupPackageByOutInfo(out,DataGroupType.Robot);
		}
		
		return export;
	}
	
	protected DataExportTool makeOneRequest(String keyFront,String front,boolean isH,String inputPath,String serverPath,String clientPath,String robotPath,int start,int len,boolean needAdapter,DataExportTool parent)
	{
		boolean hasClient=clientPath!=null;
		boolean hasRobot=robotPath!=null;
		
		DataDefineTool define0=new DataDefineTool(serverPath + "/constlist/generate/" + front + "RequestType." + _serverExName,start,len,true);
		DataDefineTool define1=hasClient ? new DataDefineTool(clientPath + "/constlist/generate/" + front + "ResponseType." + _clientExName,start,len,false) : null;
		DataDefineTool define2=hasRobot ? new DataDefineTool(robotPath + "/constlist/generate/" + front + "ResponseType." + _serverExName,start,len,false) : null;
		DataMakerTool maker0=new DataMakerTool(serverPath + "/tool/generate/" + front + "RequestMaker." + _serverExName,define0);
		DataMakerTool maker1=hasClient ? new DataMakerTool(clientPath + "/tool/generate/" + front + "ResponseMaker." + _clientExName,define1) : null;
		DataMakerTool maker2=hasRobot ? new DataMakerTool(robotPath + "/tool/generate/" + front + "ResponseMaker." + _serverExName,define2) : null;
		
		DataExportTool export=new DataExportTool();
		export.setFileRecordTool(_record);
		export.setProjectType(countProjectType(isH));
		export.addDefine(DataGroupType.Server,define0);
		if(hasClient)
			export.addDefine(DataGroupType.Client,define1);
		if(hasRobot)
			export.addDefine(DataGroupType.Robot,define2);
		
		export.addMaker(DataGroupType.Server,maker0);
		if(hasClient)
			export.addMaker(DataGroupType.Client,maker1);
		if(hasRobot)
			export.addMaker(DataGroupType.Robot,maker2);
		
		String serverFront=keyFront.isEmpty() ? "request" : keyFront+"Request";
		String clientFront=keyFront.isEmpty() ? "response" : keyFront+"Response";
		
		if(_isCommon)
		{
			_dataExports.put(serverFront,export);
		}
		else
		{
			export.setCommonTool(_dataExports.get(serverFront));
		}
		
		export.addParentTool(parent);
		
		export.setInput(inputPath,"MO",this::messageFilter);
		//export.setHotfix(needAdapter,_adapterPath,_IlRuntimeWriter);
		
		createForRequest(export,serverPath + "/net/"+serverFront,DataGroupType.Server,"",serverPath + "/net/base/"+ front + "Request."+_serverExName,true);
		if(hasClient)
			createForResponse(export,clientPath + "/net/"+clientFront,DataGroupType.Client,"",clientPath + "/net/base/"+ front + "Response."+_clientExName,true);
		if(hasRobot)
			createForResponse(export,robotPath + "/net/"+clientFront,DataGroupType.Robot,"",robotPath + "/net/base/"+ front + "Response."+_serverExName,true);
		
		if(_isCommon || ShineToolSetting.useMessageFull)
		{
			export.setInputQFunc(v->
			{
				countOneMessage(v);
			});
		}
		
		export.executeAll();
		
		return export;
	}
	
	protected DataExportTool makeOneResponse(String keyFront,String front,boolean isH,String inputPath,String serverPath,String clientPath,String robotPath,int start,int len,DataExportTool requestTool,DataExportTool parent)
	{
		boolean hasClient=clientPath!=null;
		boolean hasRobot=robotPath!=null;
		
		DataDefineTool define0=new DataDefineTool(serverPath + "/constlist/generate/" + front + "ResponseType." + _serverExName,start,len,true);
		DataDefineTool define1=hasClient ? new DataDefineTool(clientPath + "/constlist/generate/" + front + "RequestType." + _clientExName,start,len,false) : null;
		DataDefineTool define2=hasRobot ? new DataDefineTool(robotPath + "/constlist/generate/" + front + "RequestType." + _serverExName,start,len,false) : null;
		DataMakerTool maker0=new DataMakerTool(serverPath + "/tool/generate/" + front + "ResponseMaker." + _serverExName,define0);
		DataMakerTool maker1=hasClient ? new DataMakerTool(clientPath + "/tool/generate/" + front + "RequestMaker." + _clientExName,define1) : null;
		DataMakerTool maker2=hasRobot ? new DataMakerTool(robotPath + "/tool/generate/" + front + "RequestMaker." + _serverExName,define2) : null;
		
		DataMessageBindTool messageBind0=new DataMessageBindTool(serverPath + "/tool/generate/" + front + "ResponseBindTool." + _serverExName,define0,requestTool.getDefineTool(DataGroupType.Server));
		DataMessageBindTool messageBind1=hasClient ? new DataMessageBindTool(clientPath + "/tool/generate/" + front + "RequestBindTool." + _clientExName,define1,requestTool.getDefineTool(DataGroupType.Client)) : null;
		DataMessageBindTool messageBind2=hasRobot ? new DataMessageBindTool(robotPath + "/tool/generate/" + front + "RequestBindTool." + _serverExName,define2,requestTool.getDefineTool(DataGroupType.Robot)) : null;
		
		DataExportTool export=new DataExportTool();
		export.setFileRecordTool(_record);
		export.setProjectType(countProjectType(isH));
		export.addDefine(DataGroupType.Server,define0);
		if(hasClient)
			export.addDefine(DataGroupType.Client,define1);
		if(hasRobot)
			export.addDefine(DataGroupType.Robot,define2);
		
		export.addMaker(DataGroupType.Server,maker0);
		
		if(hasClient)
			export.addMaker(DataGroupType.Client,maker1);
		if(hasRobot)
			export.addMaker(DataGroupType.Robot,maker2);
		
		export.addMessageBind(DataGroupType.Server,messageBind0);
		if(hasClient)
			export.addMessageBind(DataGroupType.Client,messageBind1);
		if(hasRobot)
			export.addMessageBind(DataGroupType.Robot,messageBind2);
		
		String serverFront=keyFront.isEmpty() ? "response" : keyFront+"Response";
		String clientFront=keyFront.isEmpty() ? "request" : keyFront+"Request";
		
		if(_isCommon)
		{
			_dataExports.put(serverFront,export);
		}
		else
		{
			export.setCommonTool(_dataExports.get(serverFront));
		}
		
		export.addParentTool(parent);
		
		export.setInput(inputPath,"MO",this::messageFilter);
		//export.setHotfix(isClientMain,_adapterPath,_IlRuntimeWriter);
		
		createForResponse(export,serverPath + "/net/"+serverFront,DataGroupType.Server,"",serverPath + "/net/base/" + front + "Response."+_serverExName,true);
		if(hasClient)
			createForRequest(export,clientPath + "/net/"+clientFront,DataGroupType.Client,"",clientPath + "/net/base/" + front + "Request."+_clientExName,true);
		if(hasRobot)
			createForRequest(export,robotPath + "/net/"+clientFront,DataGroupType.Robot,"",robotPath + "/net/base/" + front + "Request."+_serverExName,true);
		
		if(_isCommon || ShineToolSetting.useMessageFull)
		{
			export.setInputQFunc(v->
			{
				countOneMessage(v);
			});
		}
		
		export.executeAll();
		
		return export;
	}
	
	protected HttpResponseResultExportTool makeOneHttpResponseResult(String keyFront,String front,String inputPath,String serverPath,String clientPath,String robotPath,int start,int len,boolean isClient)
	{
		boolean hasClient=clientPath!=null;
		boolean hasRobot=robotPath!=null;
		
		DataDefineTool define0=new DataDefineTool(serverPath + "/constlist/generate/" + front + "HttpResponseResultType." + _serverExName,start,len,true);
		DataDefineTool define1=hasClient ? new DataDefineTool(clientPath + "/constlist/generate/" + front + "HttpResponseResultType." + _clientExName,start,len,false) : null;
		DataDefineTool define2=hasRobot ? new DataDefineTool(robotPath + "/constlist/generate/" + front + "HttpResponseResultType." + _serverExName,start,len,false) : null;
		
		HttpResponseResultExportTool export=new HttpResponseResultExportTool();
		export.setFileRecordTool(_record);
		export.addDefine(DataGroupType.Server,define0);
		if(hasClient)
			export.addDefine(DataGroupType.Client,define1);
		if(hasRobot)
			export.addDefine(DataGroupType.Robot,define2);
		
		keyFront+="Result";
		
		if(_isCommon)
		{
			_dataExports.put(keyFront,export);
		}
		else
		{
			export.setCommonTool(_dataExports.get(keyFront));
		}
		
		export.setInput(inputPath,"RO");
		export.addOutput(createForResult(serverPath + "/net/httpResponseResult",0,isClient));
		if(hasClient)
			export.addOutput(createForResult(clientPath + "/net/httpResponseResult",1,isClient));
		if(hasRobot)
			export.addOutput(createForResult(robotPath + "/net/httpResponseResult",2,isClient));
		
		
		if(isClient && (_isCommon || ShineToolSetting.useMessageFull))
		{
			export.setInputQFunc(v->
			{
				countOneMessage(v);
			});
		}
		
		export.executeAll();
		
		return export;
	}
	
	protected void makeOneHttpResponse(String keyFront,String front,String inputPath,String serverPath,String clientPath,String robotPath,int start,int len,boolean isClient,HttpResponseResultExportTool resultTool)
	{
		boolean hasClient=clientPath!=null;
		boolean hasRobot=robotPath!=null;
		
		DataDefineTool define0=new DataDefineTool(serverPath + "/constlist/generate/" + front + "HttpResponseType." + _serverExName,start,len,true);
		DataDefineTool define1=hasClient ? new DataDefineTool(clientPath + "/constlist/generate/" + front + "HttpRequestType." + _clientExName,start,len,false) : null;
		DataDefineTool define2=hasRobot ? new DataDefineTool(robotPath + "/constlist/generate/" + front + "HttpRequestType." + _serverExName,start,len,false) : null;
		DataMakerTool maker0=new DataMakerTool(serverPath + "/tool/generate/" + front + "HttpResponseMaker." + _serverExName,define0);
		
		HttpResponseExportTool export=new HttpResponseExportTool();
		export.setResultTool(resultTool);
		export.setFileRecordTool(_record);
		export.addDefine(DataGroupType.Server,define0);
		if(hasClient)
			export.addDefine(DataGroupType.Client,define1);
		if(hasRobot)
			export.addDefine(DataGroupType.Robot,define2);
		export.addMaker(DataGroupType.Server,maker0);
		
		if(_isCommon)
		{
			_dataExports.put(keyFront,export);
		}
		else
		{
			export.setCommonTool(_dataExports.get(keyFront));
		}
		
		export.setInput(inputPath,"MO",this::messageFilter);
		createForHttpResponse(export,serverPath + "/net/httpResponse",DataGroupType.Server,"",serverPath + "/net/base/" + front + "HttpResponse."+_serverExName,isClient);
		if(hasClient)
			createForHttpRequest(export,clientPath + "/net/httpRequest",DataGroupType.Client,"",clientPath + "/net/base/" + front + "HttpRequest."+_clientExName,isClient);
		if(hasRobot)
			createForHttpRequest(export,robotPath + "/net/httpRequest",DataGroupType.Robot,"",robotPath + "/net/base/" + front + "HttpRequest."+_serverExName,isClient);
		
		if(isClient && (_isCommon || ShineToolSetting.useMessageFull))
		{
			export.setInputQFunc(v->
			{
				countOneMessage(v);
			});
		}
		
		export.executeAll();
	}
	
	/** 统计一个节点(返回是否存在不兼容变化)(输入类名) */
	protected boolean countOneNodeModified(String qName)
	{
		return toCountOneNodeModified(qName,0);
	}
	
	/** 统计一个节点(返回是否存在不兼容变化)(输入类名)(type:0->上下都查，1->上,2->下) */
	protected boolean toCountOneNodeModified(String qName,int type)
	{
		Boolean v=_tempDic.get(qName);
		if(v!=null)
		{
			if(v.equals(true))
				return v;
		}
		
		boolean re=doCountOneNodeModified(qName,type);
		_tempDic.put(qName,re);
		return re;
	}
	
	/** 统计一个节点(返回是否存在不兼容变化)(输入类名)(type:0->上下都查，1->上,2->下) */
	protected boolean doCountOneNodeModified(String qName,int type)
	{
		RecordDataClassInfo cls=BaseExportTool.newRecordClsDic.get(qName);
		
		if(cls==null)
		{
			return false;
		}
		
		if(cls.hasBigModified)
		{
			return true;
		}
		
		//已执行的暂时标记位未修改
		if(_tempSet.contains(qName))
			return false;
		
		_tempSet.add(qName);
		
		RecordDataClassInfo vCls=BaseExportTool.oldRecordClsDic.get(qName);
		
		//旧记录没有，视为没变化
		if(vCls==null)
		{
			return false;
		}
		
		if(type!=2 && !cls.superQName.isEmpty())
		{
			if(toCountOneNodeModified(cls.superQName,1))
			{
				return true;
			}
		}
		
		////LO不统计属性的继承更改
		if(type!=1 && !qName.endsWith("LO") && vCls.mayBeExtends)
		{
			for(String cv : vCls.childrenQNameList)
			{
				//被删了
				if(BaseExportTool.newRecordClsDic.get(cv)==null)
				{
					RecordDataClassInfo.bigModifiedError(vCls,"子类被删除:"+cv);
					return true;
				}
				
				if(toCountOneNodeModified(cv,2))
				{
					return true;
				}
			}
		}
		
		
		for(RecordDataClassInfo.DataFieldInfo v : vCls.fieldList)
		{
			if(!v.dataType.isEmpty())
			{
				if(toCountOneNodeModified(v.dataType,0))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	protected void countOneNodeStr(StringBuilder sb,String qName)
	{
		toCountOneNodeStr(sb,qName,0);
	}
	
	protected void toCountOneNodeStr(StringBuilder sb,String qName,int type)
	{
		//已存在
		
		if(_tempSet.contains(qName))
			return;
		
		_tempSet.add(qName);
		
		RecordDataClassInfo cls=BaseExportTool.newRecordClsDic.get(qName);
		
		if(cls==null)
		{
			return;
		}
		
		if(type!=2 && !cls.superQName.isEmpty())
		{
			toCountOneNodeStr(sb,cls.superQName,1);
		}
		
		//_tempDic.add(qName);
		
		cls.writeString(sb,false);
		
		
		////LO不统计属性的继承更改
		if(type!=1 && cls.mayBeExtends)
		{
			for(String cv : cls.childrenQNameList)
			{
				toCountOneNodeStr(sb,cv,2);
				
				////不存在再加(避免死循环)
				//if(!_tempDic.contains(cv))
				//{
				//
				//}
			}
		}
		
		for(RecordDataClassInfo.DataFieldInfo v : cls.fieldList)
		{
			if(!v.dataType.isEmpty())
			{
				toCountOneNodeStr(sb,v.dataType,0);
			}
		}
	}
	
	protected void countOneMessage(String qName)
	{
		//需要复写
	}
}
