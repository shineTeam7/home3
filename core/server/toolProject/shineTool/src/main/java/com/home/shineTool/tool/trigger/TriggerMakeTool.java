package com.home.shineTool.tool.trigger;

import com.home.shine.constlist.STriggerObjectType;
import com.home.shine.support.collection.IntIntMap;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.SMap;
import com.home.shine.support.collection.StringIntMap;
import com.home.shine.utils.FileUtils;
import com.home.shine.utils.StringUtils;
import com.home.shineTool.constlist.CodeType;
import com.home.shineTool.constlist.DataGroupType;
import com.home.shineTool.constlist.ProjectType;
import com.home.shineTool.constlist.VisitType;
import com.home.shineTool.global.ShineToolGlobal;
import com.home.shineTool.global.ShineToolSetting;
import com.home.shineTool.reflect.FieldInfo;
import com.home.shineTool.reflect.MethodInfo;
import com.home.shineTool.reflect.cls.ClassInfo;
import com.home.shineTool.reflect.code.CodeInfo;
import com.home.shineTool.reflect.code.CodeWriter;
import com.home.shineTool.tool.FileRecordTool;
import com.home.shineTool.tool.data.DataDefineTool;
import com.home.shineTool.tool.export.NormalConfigMakeTool;

/** trigger构造工具 */
public class TriggerMakeTool
{
	/** 工程类型 */
	protected int _projectType;
	/** 工程大写前缀 */
	protected String _projectUFront="";
	/** 工程大写前缀 */
	protected String _projectLFront="";
	
	/** 文件记录 */
	private FileRecordTool _record;
	/** 输入目录 */
	private String _inputPath;
	/** 服务器输出temp目录 */
	private String _serverOutTempPath;
	/** 服务器代码输出目录 */
	private String _serverCodePath;
	/** 客户端输出目录 */
	private String _clientOutTempPath;
	/** 客户端类输出目录 */
	private String _clientCodePath;
	
	//cls
	protected int _clientCode;
	protected int _serverCode;
	
	/** 父工具 */
	private TriggerMakeTool _parentTool;
	/** 子工具 */
	private TriggerMakeTool _childTool;
	
	/** 组定义  */
	public StringIntMap groupDefineDic;
	/** 组定义类型 */
	public IntIntMap groupDefineTypeDic;
	
	/** 对象定义(应用字典)  */
	public StringIntMap objectDefineDic;
	/** 对象定义(定义字典)  */
	public SMap<String,String> objectStrDefineDic;
	
	/** 方法定义 */
	public StringIntMap functionDefineDic;
	/** 方法定义类型 */
	public IntIntMap functionDefineTypeDic;
	/** 方法信息 */
	public IntObjectMap<MethodInfo> functionInfoDic;
	
	protected ClassInfo _clientControlCls;
	protected ClassInfo _servefControlCls;
	
	public String getProjectPath(int projectType,boolean isClient)
	{
		return NormalConfigMakeTool.getProjectPathS(projectType,isClient);
	}
	
	public void init(int projectType,String inputPath,String serverOutTempPath,String serverCodePath,int serverCodeType,String clientOutTempPath,String clientCodePath,int clientCodeType,String recordPath)
	{
		_projectType=projectType;
		
		switch(_projectType)
		{
			case ProjectType.Common:
			{
				_projectUFront="";
				_projectLFront="";
			}
			break;
			case ProjectType.Game:
			{
				_projectUFront="G";
				_projectLFront="g";
			}
			break;
			case ProjectType.HotFix:
			{
				_projectUFront="H";
				_projectLFront="h";
			}
			break;
		}
		
		_inputPath=FileUtils.fixPath(inputPath);
		
		_serverOutTempPath=FileUtils.fixPath(serverOutTempPath);
		_serverCodePath=FileUtils.fixPath(serverCodePath);
		_serverCode=serverCodeType;
		
		_clientOutTempPath=FileUtils.fixPath(clientOutTempPath);
		_clientCodePath=FileUtils.fixPath(clientCodePath);
		_clientCode=clientCodeType;
		
		_record=new FileRecordTool(recordPath);
		_record.setVersion(ShineToolSetting.configExportVersion);
	}
	
	/** 设置父 */
	public void setParentTool(TriggerMakeTool tool)
	{
		_parentTool=tool;
		tool._childTool=this;
	}
	
	public TriggerMakeTool getParentTool()
	{
		return _parentTool;
	}
	
	public void execute()
	{
		if(_parentTool==null)
		{
			groupDefineDic=new StringIntMap();
			groupDefineTypeDic=new IntIntMap();
			objectDefineDic=new StringIntMap();
			objectStrDefineDic=new SMap<>();
			functionDefineDic=new StringIntMap();
			functionDefineTypeDic=new IntIntMap();
			functionInfoDic=new IntObjectMap<>(k->new MethodInfo[k]);
			
			CodeInfo code=CodeInfo.getCode(CodeType.Java);
			
			objectDefineDic.put(code.Void,STriggerObjectType.Void);
			objectStrDefineDic.put(code.Void,"STriggerObjectType.Void");
			objectDefineDic.put(code.Object,STriggerObjectType.Object);
			objectStrDefineDic.put(code.Object,"STriggerObjectType.Object");
			objectDefineDic.put(code.Boolean,STriggerObjectType.Boolean);
			objectStrDefineDic.put(code.Boolean,"STriggerObjectType.Boolean");
			objectDefineDic.put(code.Int,STriggerObjectType.Int);
			objectStrDefineDic.put(code.Int,"STriggerObjectType.Int");
			objectDefineDic.put(code.Float,STriggerObjectType.Float);
			objectStrDefineDic.put(code.Float,"STriggerObjectType.Float");
			objectDefineDic.put(code.Long,STriggerObjectType.Long);
			objectStrDefineDic.put(code.Long,"STriggerObjectType.Long");
			objectDefineDic.put(code.String,STriggerObjectType.String);
			objectStrDefineDic.put(code.String,"STriggerObjectType.String");
			objectDefineDic.put(code.List,STriggerObjectType.List);
			objectStrDefineDic.put(code.List,"STriggerObjectType.List");
			objectDefineDic.put(code.Map,STriggerObjectType.Map);
			objectStrDefineDic.put(code.Map,"STriggerObjectType.Map");
			objectDefineDic.put(code.Set,STriggerObjectType.Set);
			objectStrDefineDic.put(code.Set,"STriggerObjectType.Set");
			
			objectDefineDic.put("Runnable",STriggerObjectType.Runnable);
			objectStrDefineDic.put("Runnable","STriggerObjectType.Runnable");
			
			//补充方法
		}
		else
		{
			groupDefineDic=_parentTool.groupDefineDic.clone();
			groupDefineTypeDic=_parentTool.groupDefineTypeDic.clone();
			objectDefineDic=_parentTool.objectDefineDic.clone();
			objectStrDefineDic=_parentTool.objectStrDefineDic.clone();
			functionDefineDic=_parentTool.functionDefineDic.clone();
			functionDefineTypeDic=_parentTool.functionDefineTypeDic.clone();
			functionInfoDic=_parentTool.functionInfoDic.clone();
		}
		
		toExecuteGroupDefine();
		toExecuteObjectDefine();
		toExecuteFunctionDefine();
		
		//game
		if(_projectType==ProjectType.Game)
		{
			toExecuteExport();
		}
	}
	
	private void toExecuteGroupDefine()
	{
		int start=_parentTool==null ? 1 : 20;
		DataDefineTool define0=new DataDefineTool(_serverCodePath+"/constlist/generate/" + _projectUFront + "TriggerGroupType."+CodeType.getExName(_serverCode),start,20,true);
		DataDefineTool define1=new DataDefineTool(_clientCodePath+"/constlist/generate/" + _projectUFront + "TriggerGroupType."+CodeType.getExName(_clientCode),start,20,false);
		
		TriggerGroupDefineTool export=new TriggerGroupDefineTool(this);
		export.setNeedTrace(false);
		export.setFileRecordTool(_record);
		export.addDefine(DataGroupType.Server,define0);
		if(ShineToolSetting.needClient)
			export.addDefine(DataGroupType.Client,define1);
		export.setInput(_inputPath+"/trigger/group","T",null,CodeType.Java);
		
		export.execute();
		
		define0.write();
		if(ShineToolSetting.needClient)
			define1.write();
	}
	
	private void toExecuteObjectDefine()
	{
		int start=_parentTool==null ? 20 : 70;
		DataDefineTool define0=new DataDefineTool(_serverCodePath+"/constlist/generate/" + _projectUFront + "TriggerObjectType."+CodeType.getExName(_serverCode),start,50,true);
		DataDefineTool define1=new DataDefineTool(_clientCodePath+"/constlist/generate/" + _projectUFront + "TriggerObjectType."+CodeType.getExName(_clientCode),start,50,false);
		
		TriggerObjectDefineTool export=new TriggerObjectDefineTool(this);
		export.setNeedTrace(false);
		export.setFileRecordTool(_record);
		export.addDefine(DataGroupType.Server,define0);
		if(ShineToolSetting.needClient)
			export.addDefine(DataGroupType.Client,define1);
		export.setInput(_inputPath+"/trigger/type","T",null,CodeType.Java);
		
		export.execute();
		
		define0.write();
		if(ShineToolSetting.needClient)
			define1.write();
	}
	
	private void toExecuteFunctionDefine()
	{
		int start=_parentTool==null ? 1 : 1000;
		DataDefineTool define0=new DataDefineTool(_serverCodePath+"/constlist/generate/" + _projectUFront + "TriggerFunctionType."+CodeType.getExName(_serverCode),start,1000,true);
		DataDefineTool define1=new DataDefineTool(_clientCodePath+"/constlist/generate/" + _projectUFront + "TriggerFunctionType."+CodeType.getExName(_clientCode),start,1000,false);
		
		DataDefineTool eventDefine0=new DataDefineTool(_serverCodePath+"/constlist/generate/" + _projectUFront + "TriggerEventType."+CodeType.getExName(_serverCode),start,1000,false);
		DataDefineTool eventDefine1=new DataDefineTool(_clientCodePath+"/constlist/generate/" + _projectUFront + "TriggerEventType."+CodeType.getExName(_clientCode),start,1000,false);
		
		TriggerFunctionDefineTool export=new TriggerFunctionDefineTool(this);
		export.setNeedTrace(false);
		export.setFileRecordTool(_record);
		export.addDefine(DataGroupType.Server,define0);
		if(ShineToolSetting.needClient)
			export.addDefine(DataGroupType.Client,define1);
		export.setEventDefine(eventDefine0,eventDefine1);
		export.setInput(_inputPath+"/trigger/base","T",null,CodeType.Java);
		
		export.execute();
		
		define0.write();
		if(ShineToolSetting.needClient)
			define1.write();
		eventDefine0.write();
		if(ShineToolSetting.needClient)
			eventDefine1.write();
		
		doFunctionRegist(false,export);
		if(ShineToolSetting.needClient)
			doFunctionRegist(true,export);
	}
	
	private void doFunctionRegist(boolean isClient,TriggerFunctionDefineTool export)
	{
		String path=(isClient ? _clientCodePath : _serverCodePath)+"/control/"+_projectUFront+"TriggerControl."+CodeType.getExName(isClient ? _clientCode : _serverCode);
		
		ClassInfo cls=ClassInfo.getClassInfoFromPath(path);
		
		if(cls==null)
		{
			cls=ClassInfo.getClassInfoFromPathAbs(path);
			cls.clsDescribe="trigger控制";
			
			if(_parentTool!=null)
			{
				ClassInfo superCls=isClient ? _parentTool._clientControlCls : _parentTool._servefControlCls;
				cls.addImport(superCls.getQName());
				cls.extendsClsName=superCls.clsName;
			}
			else
			{
				FieldInfo field=new FieldInfo();
				field.name="funcReturnTypeDic";
				field.type=cls.getCode().getArrayType(cls.getCode().Int,true);
				field.describe="方法返回值类型字典";
				field.visitType=VisitType.Public;
				field.defaultValue=cls.getCode().createNewArray(cls.getCode().Int,"2000");
				
				cls.addField(field);

//				field=new FieldInfo();
//				field.name="funcReturnTypeDic";
//				field.type=cls.getCode().getArrayType(cls.getCode().Int,true);
//				field.describe="方法返回值类型字典";
//				field.visitType=VisitType.Public;
//				field.defaultValue=cls.getCode().createNewArray(cls.getCode().Int,"2000");
//
//				cls.addField(field);
			}
		}
		
		
		
		if(isClient)
			_clientControlCls=cls;
		else
			_servefControlCls=cls;
		
		MethodInfo method=new MethodInfo();
		method.name="registReturnType";
		method.describe="注册返回值";
		method.visitType=VisitType.Protected;
		method.setOverride(_parentTool!=null);
		
		CodeWriter writer=cls.createWriter();
		
		if(_parentTool!=null)
		{
			writer.writeSuperMethod(method);
			writer.writeEmptyLine();
		}
		
		ClassInfo defineClass=export.getDefineClass(isClient ? DataGroupType.Client : DataGroupType.Server);
		cls.addImport(defineClass.getQName());
		
		functionInfoDic.getSortedKeyList().forEach(k->
		{
			MethodInfo methodInfo=functionInfoDic.get(k);
			int sc=functionDefineTypeDic.get(k);
			
			//匹配
			if((isClient && (sc & 1)==1) || (!isClient && (sc >> 1)==1))
			{
				String dName=StringUtils.ucWord(methodInfo.name);
				
				//存在
				if(defineClass.getField(dName)!=null)
				{
					String tt=objectStrDefineDic.get(methodInfo.returnType);
					
					writer.writeCustom(defineClass.getThisFront()+"funcReturnTypeDic["+defineClass.clsName+"."+dName+"]="+tt+";");
					writer.writeCustom(defineClass.getThisFront()+"funcNameDic["+defineClass.clsName+"."+dName+"]=\""+methodInfo.name+"\";");
				}
				
				
			}
		});
		
		writer.writeEnd();
		method.content=writer.toString();
		
		cls.addMethod(method);
		cls.write();
	}
	
	private void toExecuteExport()
	{
		TriggerDataExportTool export=new TriggerDataExportTool(this);
		
		export.setNeedTrace(false);
		export.setFileRecordTool(_record);
		export.setInput(_inputPath+"/trigger/data","T",null,CodeType.Java);
		export.setSavePath(ShineToolGlobal.configSavePath+"/trigger.xml");
		
		export.execute();
	}
	
	/** 获取对象类型定义 */
	public int getObjectType(String type)
	{
		return objectDefineDic.get(type);
	}
	
	public String getServerOutTempPath()
	{
		return _serverOutTempPath;
	}
	
	public String getClientOutTempPath()
	{
		return _clientOutTempPath;
	}
	
	public boolean isEventFunc(String methodName)
	{
		return methodName.startsWith("on");
	}
}
