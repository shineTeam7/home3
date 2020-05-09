package com.home.shineTool.tool.config;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.SList;
import com.home.shine.support.collection.SMap;
import com.home.shine.support.collection.SSet;
import com.home.shine.support.func.ObjectCall;
import com.home.shine.support.func.ObjectCall3;
import com.home.shine.utils.FileUtils;
import com.home.shine.utils.StringUtils;
import com.home.shineTool.constlist.CodeType;
import com.home.shineTool.constlist.ConfigFieldType;
import com.home.shineTool.constlist.DataGroupType;
import com.home.shineTool.constlist.MakeMethodType;
import com.home.shineTool.constlist.VarType;
import com.home.shineTool.constlist.VisitType;
import com.home.shineTool.dataEx.ConfigFieldData;
import com.home.shineTool.dataEx.ConfigFieldKeyData;
import com.home.shineTool.reflect.FieldInfo;
import com.home.shineTool.reflect.MethodArgInfo;
import com.home.shineTool.reflect.MethodInfo;
import com.home.shineTool.reflect.cls.ClassInfo;
import com.home.shineTool.reflect.code.CodeInfo;
import com.home.shineTool.reflect.code.CodeWriter;
import com.home.shineTool.tool.base.BaseOutputInfo;
import com.home.shineTool.global.ShineToolSetting;
import com.home.shineTool.tool.data.DataOutputInfo;

import java.io.File;
import java.util.List;

public class HConfigExportTool extends ConfigExportToolBase
{
	//cls
	
	private ClassInfo _configDataCls;
	
	private CodeInfo _conCode;
	
	//cache
	private ClassInfo _clientConfigDataCls;
	private ClassInfo _serverConfigDataCls;
	
	/** h的新组 */
	private SList<String> _hNewList;
	/** v的新组 */
	private SList<String> _vList;
	
	/** 自定义组 */
	private SList<String> _customList;
	
	/** 全部新组(包括v和自定义) */
	private SList<String> _newListAll=new SList<>(k->new String[k]);
	/** 全部操作组(包括v和自定义) */
	private SList<String> _allListAll=new SList<>(k->new String[k]);
	
	
	public HConfigExportTool(ConfigMakeTool tool)
	{
		super(tool);
		
		_isH=true;
		
		ConfigMakeTool parentMakeTool=_makeTool.getParentTool();
		
		if(parentMakeTool!=null)
		{
			_parentTool=parentMakeTool.getHExportTool();
		}
	}
	
	public void executeConfig()
	{
		//TODO:解决config输出writeSimple的问题
		
		_isClient=false;
		
		doOneSide();
		addOutput(createForData(_serverPath + "/game", DataGroupType.Server,_serverCode));
		executeList(_serverInputFileList);
		doOneSideEnd();
		
		_outputs.clear();
		
		if(ShineToolSetting.needClient)
		{
			_isClient=true;
			
			doOneSide();
			addOutput(createForData(_clientPath + "/game",DataGroupType.Client,_clientCode));
			executeList(_clientInputFileList);
			doOneSideEnd();
		}
		
		recordFiles();
	}
	
	//@Override
	//protected void toMakeInput()
	//{
	//	super.toMakeInput();
	//
	//	_inputFile
	//}
	
	@Override
	protected int addOneDefine(String cName,String des)
	{
		String useName=_makeTool.upperNameToUseName(cName);
		
		int re;
		
		//存在,才写
		if(_makeTool.getHAllNewFilesSet().contains(useName) || _makeTool.getCustomDescribes().contains(useName))
		{
			re=super.addOneDefine(cName,des);
			_makeTool.getConfigKeyDic().put(useName,re);
			_makeTool.getConfigProjectTypeDic().put(useName,_makeTool.getProjectType());
		}
		else
		{
			re=_makeTool.getConfigKeyDic().get(useName);
		}
		
		return re;
	}
	
	@Override
	protected File toGetInputFile(File f)
	{
		//return super.toGetInputFile(f);

		//String path=f.getParent() + "/" + _makeTool.useNameToUpperName(_makeTool.getUseName(f)) + "." + CodeType.getExName(_sourceCode);
		String path=f.getPath();

		path=FileUtils.fixPath(path).replaceAll("\\/enum\\/","/enumT/");

		return new File(path);
	}
	
	@Override
	protected ClassInfo toGetInputClass(File f)
	{
		_data=_clsDic.get(f.getName());
		
		_superFields=_isClient ? _data.clientSuperFields : _data.serverSuperFields;
		
		return _isClient ? _data.clientCls : _data.serverCls;
	}
	
	@Override
	protected String getOutClassNameByInfo(BaseOutputInfo outInfo,String name)
	{
		return _makeTool.getProjectUFront() + super.getOutClassNameByInfo(outInfo,name);
	}
	
	/** 获取重复属性前缀(小写) */
	protected String getRepeatFieldFront(ClassInfo outCls)
	{
		if(outCls.getCodeType()==CodeType.TS)
			return _makeTool.getProjectLFront();
		
		return "";
	}
	
	/** 构建Data所需(0:服务器,1:客户端,2:机器人) */
	private static DataOutputInfo createForData(String path,int group,int codeType)
	{
		DataOutputInfo re=new DataOutputInfo();
		re.path=path;
		re.nameTail="Config";
		re.group=group;
		re.codeType=codeType;
		re.defineIndex=-1;
		re.makeIndex=-1;
		
		re.superQName=ShineToolSetting.configBaseQName;
		
		re.needs[MakeMethodType.readSimple]=true;
		
		//不是ts就补上写
		if(codeType!=CodeType.TS)
		{
			//写也补上
			re.needs[MakeMethodType.writeSimple]=true;
		}
		
		//re.isOverrideSuper=true;
		
		return re;
	}
	
	private String getCodePath(boolean isClient)
	{
		return isClient ? _clientPath : _serverPath;
	}
	
	protected ClassInfo getConfigDefineCls(String useName)
	{
		int pType=_makeTool.getConfigProjectTypeDic().get(useName);
		
		if(pType<=0)
		{
			Ctrl.throwError("不该找不到配置工程类型",useName);
		}
		
		ConfigMakeTool makeTool=_makeTool;
		
		while(pType<makeTool.getProjectType())
		{
			makeTool=makeTool.getParentTool();
		}
		
		return makeTool.getHExportTool().getDefineClass(_isClient ? DataGroupType.Client : DataGroupType.Server);
	}
	
	private void doOneSide()
	{
		String path=getCodePath(_isClient);
		
		int codeType=_isClient ? _clientCode : _serverCode;
		
		_conCode=CodeInfo.getCode(codeType);
		
		String configDataPath=path + "/base/" + _makeTool.getProjectUFront() + "ConfigReadData." + CodeType.getExName(codeType);
		
		_configDataCls=ClassInfo.getClassInfoFromPathAbs(configDataPath);
		
		//记录
		if(_isClient)
		{
			_clientConfigDataCls=_configDataCls;
			
			_configDataCls.addAnnotation("Hotfix");//客户端需要热更标记
		}
		else
		{
			_serverConfigDataCls=_configDataCls;
		}
		
		if(_parentTool!=null)
		{
			HConfigExportTool parentTool=(HConfigExportTool)_parentTool;
			ClassInfo parentDataCls=_isClient ? parentTool._clientConfigDataCls : parentTool._serverConfigDataCls;
			
			//继承
			_configDataCls.extendsClsName=parentDataCls.clsName;
			_configDataCls.addImport(parentDataCls.getQName());
		}
		
		_newListAll.clear();
		
		_hNewList=_isClient ? _makeTool.getHClientNewFiles() : _makeTool.getHServerNewFiles();
		
		//v的部分
		VConfigExportTool vExportTool=_makeTool.getVExportTool();
		List<File> vList=_isClient ? vExportTool._clientFileList : vExportTool._serverFileList;
		
		for(File f : vList)
		{
			String vUseName=_makeTool.getUseName(f);
			
			ClassInfo vInCls=vExportTool.getVInputFile(f,_isClient);
			
			ClassInfo vCls=vExportTool.getVOutQNameByFile(f,_isClient,true);
			_configDataCls.addImport(vCls.getQName());
			
			boolean isSuper=_parentTool==null;
			
			if(isSuper)
			{
				FieldInfo field=new FieldInfo();
				field.name=vUseName;
				field.type=vCls.clsName;
				field.describe=vInCls.clsDescribe;
				field.visitType=VisitType.Public;
				_configDataCls.addField(field);
			}
			
			String vCName=_makeTool.useNameToUpperName(vUseName);
			
			ClassInfo vCls2=vExportTool.getVOutQNameByFile(f,_isClient,false);
			_configDataCls.addImport(vCls2.getQName());
			
			MethodInfo method=new MethodInfo();
			method.visitType=VisitType.Protected;
			method.isVirtual=!(method.isOverride=!isSuper);
			method.name="read" + vCName;
			method.describe="读取" + vInCls.clsDescribe;
			method.returnType=_conCode.Void;
			method.args.add(new MethodArgInfo(ShineToolSetting.bytesSteamVarName,StringUtils.getClassNameForQName(ShineToolSetting.bytesReadStreamQName)));
			
			CodeWriter writer=_configDataCls.createWriter();
			
			writer.writeVarSet(_configDataCls.getFieldWrap(vUseName),_conCode.createNewObject(vCls.clsName));
			writer.writeDataReadSimple(_configDataCls.getFieldWrap(vUseName));
			writer.writeEnd();
			
			method.content=writer.toString();
			_configDataCls.addMethod(method);
			
		}
		
		_vList=_isClient ? _makeTool.getVClientAllFiles() : _makeTool.getVServerAllFiles();
		_customList=_isClient ? _makeTool.getClientCustomFiles() : _makeTool.getServerCustomFiles();
		SMap<String,String> customDescribes=_makeTool.getCustomDescribes();
		
		for(String v:_vList)
		{
			//isSuper
			if(_parentTool==null)
			{
				_newListAll.add(v);
			}
		}
		
		for(String v:_customList)
		{
			if(_parentTool==null)
			{
				addOneDefine(_makeTool.useNameToUpperName(v),customDescribes.get(v));
				_newListAll.add(v);
			}
		}
		
		for(String v : _hNewList)
		{
			_newListAll.add(v);
		}
	}
	
	private void doOneSideEnd()
	{
		String path=getCodePath(_isClient);
		int codeType=_isClient ? _clientCode : _serverCode;
		
		int group=_isClient ? DataGroupType.Client : DataGroupType.Server;
		ClassInfo defineClass=getDefineClass(group);
		_configDataCls.addImport(defineClass.getQName());
		
		SList<String> newClassList=_isClient ? _makeTool.getHClientNewClassFiles() : _makeTool.getHServerNewClassFiles();
		
		SList<String> newCustomList=_parentTool==null ? _customList : new SList<>();
		
		//readFromData
		MethodInfo method=new MethodInfo();
		method.visitType=VisitType.Public;
		method.isVirtual=!(method.isOverride=_parentTool!=null);
		method.name="setToConfigOne";
		method.describe="设置值到Config上";
		method.returnType=_conCode.Void;
		method.args.add(new MethodArgInfo("type",_conCode.Int));
		
		CodeWriter writer=_configDataCls.createWriter();
		
		if(method.isOverride)
		{
			writer.writeSuperMethod(method);
			writer.writeEmptyLine();
		}
		
		MethodInfo method2=null;
		CodeWriter writer2=null;
		
		if(_isClient)
		{
			method2=new MethodInfo();
			method2.visitType=VisitType.Public;
			method2.isVirtual=!(method2.isOverride=_parentTool!=null);
			method2.name="addToConfigOne";
			method2.describe="添加到Config上";
			method2.returnType=_conCode.Void;
			method2.args.add(new MethodArgInfo("type",_conCode.Int));
			
			writer2=_configDataCls.createWriter();
			
			if(method2.isOverride)
			{
				writer2.writeSuperMethod(method2);
				writer2.writeEmptyLine();
			}
		}
		
		if(_vList.size()+newClassList.size()+newCustomList.size()>0)
		{
			writer.writeSwitch("type");
			
			if(_isClient)
			{
				writer2.writeSwitch("type");
			}
			
			for(String v : _vList)
			{
				String upName=_makeTool.useNameToUpperName(v);
				String ccName=_makeTool.getProjectUFront() + upName;
				
				ClassInfo configDefineCls=getConfigDefineCls(v);
				_configDataCls.addImport(configDefineCls.getQName());
				
				writer.writeCase(configDefineCls.clsName+"."+upName);
				
				String vv=_parentTool==null ? _configDataCls.getFieldWrap(v) : _configDataCls.getCode().getVarTypeTrans(_configDataCls.getFieldWrap(v),ccName+"ReadData");
				writer.writeCustom(ccName+".readFromData("+vv+");");
				writer.writeCustom(ccName+".afterReadConfig();");
				
				writer.endCase();
			}
			
			for(String v : newCustomList)
			{
				String upName=_makeTool.useNameToUpperName(v);
				
				ClassInfo configDefineCls=getConfigDefineCls(v);
				_configDataCls.addImport(configDefineCls.getQName());
				
				writer.writeCase(configDefineCls.clsName+"."+upName);
				writer.writeCustom( _configDataCls.getThisFront()+"setToConfig"+upName+"();");
				writer.endCase();
			}
			
			for(String v : newClassList)
			{
				String upName=_makeTool.useNameToUpperName(v);
				
				ClassInfo configDefineCls=getConfigDefineCls(v);
				_configDataCls.addImport(configDefineCls.getQName());
				
				writer.writeCase(configDefineCls.clsName+"."+upName);
				
				if(_isClient)
					writer2.writeCase(configDefineCls.clsName+"."+upName);
				
				String dName=_configDataCls.getFieldWrap(_makeTool.getProjectLFront()+v+"Dic");
				
				String ccName=_makeTool.getProjectUFront() + upName + "Config";
				String cMark=_makeTool.getProjectUFront();
				
				writer.writeCustom(ccName + ".set" + cMark + "Dic(" + dName + ");");
				writer.endCase();
				
				if(_isClient)
				{
					writer2.writeCustom(ccName + ".add" + cMark + "Dic(" + dName + ");");
					writer2.endCase();
				}
			}
			
			writer.endSwitch();
			
			if(_isClient)
			{
				writer2.endSwitch();
			}
		}
		
		writer.writeEnd();
		method.content=writer.toString();
		_configDataCls.addMethod(method);
		
		if(_isClient)
		{
			writer2.writeEnd();
			method2.content=writer2.toString();
			_configDataCls.addMethod(method2);
		}
		
		//afterReadConfigAll
		method=new MethodInfo();
		method.visitType=VisitType.Public;
		method.isVirtual=!(method.isOverride=_parentTool!=null);
		method.name="afterReadConfigAllOne";
		method.describe="读完所有配置";
		method.returnType=_conCode.Void;
		method.args.add(new MethodArgInfo("type",_conCode.Int));
		
		writer=_configDataCls.createWriter();
		
		if(method.isOverride)
		{
			writer.writeSuperMethod(method);
			writer.writeEmptyLine();
		}
		
		if(_vList.size()+newClassList.size()+newCustomList.size()>0)
		{
			writer.writeSwitch("type");
			
			for(String v : _vList)
			{
				String upName=_makeTool.useNameToUpperName(v);
				ClassInfo configDefineCls=getConfigDefineCls(v);
				_configDataCls.addImport(configDefineCls.getQName());
				
				writer.writeCase(configDefineCls.clsName+"."+upName);
				
				String ccName=_makeTool.getProjectUFront() + upName;
				writer.writeCustom(ccName+".afterReadConfigAll();");
				
				writer.endCase();
			}
			
			for(String v : newCustomList)
			{
				String upName=_makeTool.useNameToUpperName(v);
				ClassInfo configDefineCls=getConfigDefineCls(v);
				_configDataCls.addImport(configDefineCls.getQName());
				
				writer.writeCase(configDefineCls.clsName+"."+upName);
				writer.writeCustom(_configDataCls.getThisFront()+"afterReadConfigAll"+upName+"();");
				writer.endCase();
			}
			
			for(String v : newClassList)
			{
				String upName=_makeTool.useNameToUpperName(v);
				ClassInfo configDefineCls=getConfigDefineCls(v);
				_configDataCls.addImport(configDefineCls.getQName());
				
				writer.writeCase(configDefineCls.clsName+"."+upName);
				
				String ccName=_makeTool.getProjectUFront() + upName + "Config";
				writer.writeCustom(ccName+".afterReadConfigAll();");
				
				writer.endCase();
			}
			
			
			
			writer.endSwitch();
		}
		
		
		writer.writeEnd();
		method.content=writer.toString();
		_configDataCls.addMethod(method);
		
		//makeConstSize
		method=new MethodInfo();
		method.visitType=VisitType.Public;
		method.isVirtual=!(method.isOverride=_parentTool!=null);
		method.name="makeConstSize";
		method.describe="构造常量size";
		method.returnType=_conCode.Void;
		
		writer=_configDataCls.createWriter();
		
		if(method.isOverride)
		{
			writer.writeSuperMethod(method);
			writer.writeEmptyLine();
		}
		
		//有子项才做
		if(_parentTool!=null)
		{
			ConfigMakeTool parentMakeTool=_makeTool.getParentTool();
			
			SMap<String,String> constMap=_isClient ? _makeTool.getClientConstNameDic() : _makeTool.getServerConstNameDic();
			SMap<String,String> parentConstMap=_isClient ? parentMakeTool.getClientConstNameTotalDic() : parentMakeTool.getServerConstNameTotalDic();
			
			for(String key : constMap.getSortedKeyList())
			{
				String constQName=_makeTool.getConstTypeClsQName(key,_isClient);
				
				//存在
				if(!constQName.isEmpty())
				{
					//common中存在
					if(parentConstMap.contains(key))
					{
						String parentConstQName=parentMakeTool.getConstTypeClsQNameAndParent(key,_isClient);
						
						_configDataCls.addImport(constQName);
						_configDataCls.addImport(parentConstQName);
						
						//size赋值
						writer.writeVarSet(StringUtils.getClassNameForQName(parentConstQName) + ".size",StringUtils.getClassNameForQName(constQName) + ".size");
					}
				}
			}
		}
		
		writer.writeEnd();
		method.content=writer.toString();
		_configDataCls.addMethod(method);
		
		//configDataCls
		
		_configDataCls.addImport(ShineToolSetting.bytesReadStreamQName);
		
		//readBytes
		method=new MethodInfo();
		method.visitType=VisitType.Protected;
		method.isVirtual=!(method.isOverride=_parentTool!=null);
		method.name="readBytesOne";
		method.describe="从流读取单个";
		method.returnType=_conCode.Void;
		method.args.add(new MethodArgInfo("type",_conCode.Int));
		method.args.add(new MethodArgInfo(ShineToolSetting.bytesSteamVarName,StringUtils.getClassNameForQName(ShineToolSetting.bytesReadStreamQName)));
		
		writer=_configDataCls.createWriter();
		
		method.isVirtual=_parentTool==null;
		method.isOverride=_parentTool!=null;
		
		if(method.isOverride)
		{
			writer.writeSuperMethod(method);
			writer.writeEmptyLine();
		}
		
		if(_newListAll.size()>0)
		{
			writer.writeSwitch("type");
			
			for(String v : _newListAll)
			{
				String upName=_makeTool.useNameToUpperName(v);
				
				ClassInfo configDefineCls=getConfigDefineCls(v);
				_configDataCls.addImport(configDefineCls.getQName());
				
				writer.writeCase(configDefineCls.clsName+"."+upName);
				writer.writeCustom(_configDataCls.getThisFront()+"read" + _makeTool.useNameToUpperName(v) + "(" + ShineToolSetting.bytesSteamVarName + ");");
				writer.endCase();
			}
			
			writer.endSwitch();
		}
		
		writer.writeEnd();
		method.content=writer.toString();
		_configDataCls.addMethod(method);
		
		//refreshData
		method=new MethodInfo();
		method.visitType=VisitType.Public;
		method.isVirtual=_parentTool==null;
		method.isOverride=_parentTool!=null;
		method.name="refreshDataOne";
		method.describe="刷新数据";
		method.returnType=_conCode.Void;
		method.args.add(new MethodArgInfo("type",_conCode.Int));
		
		writer=_configDataCls.createWriter();
		
		method.isVirtual=_parentTool==null;
		method.isOverride=_parentTool!=null;
		
		if(method.isOverride)
		{
			writer.writeSuperMethod(method);
			writer.writeEmptyLine();
		}
		
		if(_hNewList.size()>0)
		{
			writer.writeSwitch("type");
			
			for(String v : _hNewList)
			{
				String upName=_makeTool.useNameToUpperName(v);
				
				ClassInfo configDefineCls=getConfigDefineCls(v);
				_configDataCls.addImport(configDefineCls.getQName());
				
				writer.writeCase(configDefineCls.clsName+"."+upName);
				
				writer.writeCustom(_configDataCls.getThisFront()+"refresh" + upName + "();");
				
				writer.endCase();
			}
			
			writer.endSwitch();
		}
		
		writer.writeEnd();
		method.content=writer.toString();
		_configDataCls.addMethod(method);
		
		
		
		//unused
		
		//delete部分
		List<String> unused=_isClient ? _clientUnusedList : _serverUnusedList;
		
		for(String v:unused)
		{
			String upName=_makeTool.useNameToUpperName(v);
			String ccName=_makeTool.getProjectUFront() + upName;
			
			BaseOutputInfo info=getOutputInfo(_isClient ? DataGroupType.Client : DataGroupType.Server);
			
			String clsPath=info.path + "/"+ccName+info.nameTail+"."+CodeType.getExName(codeType);
			
			File ff=new File(clsPath);
			
			if(ff.exists())
			{
				ff.delete();
			}
			
			MethodInfo mm;
			
			if((mm=_configDataCls.getMethodByName("read"+upName))!=null)
				_configDataCls.removeMethod(mm);
			
			if((mm=_configDataCls.getMethodByName("refresh"+upName))!=null)
				_configDataCls.removeMethod(mm);
		}
		
		_configDataCls.writeToPath(path + "/base/" + _makeTool.getProjectUFront() + "ConfigReadData." + CodeType.getExName(codeType));
	}
	
	@Override
	protected String toMakeExtend()
	{
		if(_parentTool!=null && _data.hasParent)
		{
			return getParentClsQName(_parentTool);
		}
		
		return "";
	}
	
	@Override
	protected void toMakeFirst()
	{
		super.toMakeFirst();
		
		int keyVarType=ConfigMakeTool.getFieldBaseType(_data.getKeysCombineType());
		String keyType=_code.getVarGStr(keyVarType);
		String mapImport=_code.getMapTypeImport(keyType,_outputCls.clsName,false);
		String mapType=_code.getMapType(keyType,_outputCls.clsName,false);
		String arrType=_code.getArrayType(_outputCls.clsName,false);
		
		boolean useArr=_data.useArr;
		
		String dicType=useArr ? arrType : mapType;
		
		FieldInfo field;
		MethodInfo method;
		CodeWriter writer;
		
		//global
		
		_configDataCls.addImport(_outputCls.getQName());
		
		if(!useArr)
		{
			_outputCls.addImport(mapImport);
		}
		
		String cMark=_makeTool.getProjectUFront();
		
		field=new FieldInfo();
		field.visitType=VisitType.Private;
		field.isStatic=true;
		field.name="_"+getRepeatFieldFront(_outputCls)+"dic";
		field.describe="存储集合";
		field.type=dicType;
		_outputCls.addField(field);
		
		method=new MethodInfo();
		method.visitType=VisitType.Public;
		method.isStatic=true;
		method.name="get" + cMark;
		method.describe="获取";
		method.returnType=_outputCls.clsName;
		
		for(ConfigFieldKeyData key : _data.keyArr)
		{
			method.args.add(new MethodArgInfo(key.name,_code.getVarStr(ConfigMakeTool.getFieldBaseType(key.type))));
		}
		
		writer=_outputCls.createWriter();
		
		String ffName=_outputCls.getFieldWrap(field.name);
		
		if(useArr)
		{
			String keyV;
			
			if(_data.keyArr.length>1)
			{
				keyV="arrIndex";
				writer.writeVarCreate(keyV,_data.getKeysCombineValue("",writer.getCode()));
			}
			else
			{
				keyV=_data.getKeysCombineValue("",writer.getCode());
			}
			
			writer.writeCustom("return " + keyV + ">=0 && " + keyV + "<" + _code.getArrayLength(ffName) + " ? " + _code.getArrayElement(ffName,keyV) + " : " + _code.Null + ";");
			
			//writer.writeCustom("return "+_code.getArrayElement(field.name,_data.getKeysCombineValue(""))+";");
		}
		else
		{
			writer.writeCustom("return " + _code.getMapElementSafe(ffName,_data.getKeysCombineValue("",writer.getCode())) + ";");
		}
		
		writer.writeEnd();
		
		method.content=writer.toString();
		_outputCls.addMethod(method);
		
		//temp
		_outputCls.removeMethodByName("set"+cMark+"Dic");
		
		method=new MethodInfo();
		method.visitType=VisitType.Public;
		method.isStatic=true;
		method.name="set" + cMark + "Dic";
		method.describe="设置字典";
		method.args.add(new MethodArgInfo("dic",dicType));
		method.returnType=_outputCls.getCode().Void;
		
		writer=_outputCls.createWriter();
		writer.writeVarSet(_outputCls.getThisFront()+field.name,"dic");
		
		writer.writeEnd();
		
		method.content=writer.toString();
		
		_outputCls.addMethodAfterName(method,"get" + cMark,false);
		
		if(_isClient)
		{
			method=new MethodInfo();
			method.visitType=VisitType.Public;
			method.isStatic=true;
			method.name="add" + cMark + "Dic";
			method.describe="添加字典(热更用)";
			method.args.add(new MethodArgInfo("dic",dicType));
			method.returnType=_outputCls.getCode().Void;
			
			writer=_outputCls.createWriter();
			
			if(useArr)
			{
				writer.writeCustom("ObjectUtils.arrayPutAll("+_outputCls.getThisFront()+field.name+",dic);");
			}
			else
			{
				writer.writeCustom(_outputCls.getThisFront()+field.name+".putAll(dic);");
			}
			
			writer.writeEnd();
			
			method.content=writer.toString();
			
			_outputCls.addMethodAfterName(method,"get" + cMark,false);
		}
		
		method=new MethodInfo();
		method.visitType=VisitType.Public;
		method.isStatic=true;
		method.name="get" + cMark + "Dic";
		method.describe="获取全部";
		method.returnType=field.type;
		
		writer=_outputCls.createWriter();
		
		writer.writeCustom("return " +_outputCls.getThisFront()+ field.name + ";");
		writer.writeEnd();
		
		method.content=writer.toString();
		_outputCls.addMethod(method);
		
		//configDataCls
		
		_configDataCls.addImport(_outputCls.getQName());
		
		if(!useArr)
		{
			_configDataCls.addImport(_code.getMapTypeImport(_code.getVarGStr(keyVarType),_outputCls.clsName,false));
			_configDataCls.addImport(_code.getMapTypeImport(_code.getVarGStr(keyVarType),_outputCls.clsName,true));
		}
		
		String useName=_makeTool.upperNameToUseName(_inputCls.clsName);
		
		String baseDicName=useName+"Dic";
		
		field=new FieldInfo();
		field.visitType=VisitType.Public;
		field.name=_makeTool.getProjectLFront() + baseDicName;
		
		field.describe=_outputCls.clsDescribe + "字典";
		
		if(useArr)
		{
			field.type=_code.getArrayType(_outputCls.clsName,false);
		}
		else
		{
			field.type=_code.getMapType(_code.getVarGStr(keyVarType),_outputCls.clsName,false);
		}
		
		//		field.defaultValue=_code.createNewObject(_code.getMapType(_code.getVarGStr(keyVarType),_outputCls.clsName,true));
		
		_configDataCls.addField(field);

		boolean hasSuper=false;
		
		if(_makeTool.getParentTool()!=null && _makeTool.getParentTool().getAllFilesTotal().contains(useName))
		{
			hasSuper=true;
		}
		
		SSet<String> newFiles=_isClient ? _makeTool.getHClientNewFilesSet() : _makeTool.getHServerNewFilesSet();
		
		//read
		
		method=new MethodInfo();
		method.visitType=VisitType.Protected;
		
		method.isVirtual=!hasSuper;
		method.isOverride=hasSuper;
		
		method.name="read" + _inputCls.clsName;
		method.describe="读取" + _outputCls.clsDescribe;
		method.returnType=_code.Void;
		method.args.add(new MethodArgInfo(ShineToolSetting.bytesSteamVarName,StringUtils.getClassNameForQName(ShineToolSetting.bytesReadStreamQName)));
		
		writer=_outputCls.createWriter();
		
		writer.writeVarCreate("config",_outputCls.clsName);
		
		writer.writeVarCreate("len",_code.Int,_code.getReadLen());
		
		if(useArr)
		{
			writer.writeVarCreate("size",_code.Int,_code.getReadLen());
			writer.createArray(_outputCls.getFieldWrap(field.name),_outputCls.clsName,"size");
		}
		else
		{
			writer.createMap(_outputCls.getFieldWrap(field.name),_code.getVarGStr(keyVarType),_outputCls.clsName,"len");
		}

		if(hasSuper)
		{
			CodeWriter tWriter=writer;
			_makeTool.foreachParentTool(k->
			{
				SSet<String> newClsSet=_isClient ? k.getHClientNewClassFilesSet() : k.getHServerNewClassFilesSet();
				
				//有
				if(newClsSet.contains(useName))
				//if(k.getAllFiles().contains(useName))
				{
					HConfigExportTool hTool=k.getHExportTool();
					
					//获取超类名
					String superQName=getParentClsQName(hTool);
					
					_configDataCls.addImport(superQName);
					
					String superClsName=StringUtils.getClassNameForQName(superQName);
					
					String dicName=k.getProjectLFront() + baseDicName;
					
					if(useArr)
					{
						tWriter.createArray(_outputCls.getFieldWrap(dicName),superClsName,"size");
					}
					else
					{
						tWriter.createMap(_outputCls.getFieldWrap(dicName),_code.getVarGStr(keyVarType),superClsName,"len");
					}
				}
				
			});
		}
		
		writer.writeForLoopTimes("i","len");
		
		writer.writeVarSet("config",_code.createNewObject(_outputCls.clsName));
		
		writer.writeDataReadSimple("config");
		
		if(useArr)
		{
			writer.writeArraySet(_outputCls.getFieldWrap(field.name),_data.getKeysCombineValue("config.",writer.getCode()),"config");
		}
		else
		{
			writer.writeMapAdd(_outputCls.getFieldWrap(field.name),_data.getKeysCombineValue("config.",writer.getCode()),"config");
		}
		
		if(hasSuper)
		{
			CodeWriter tWriter=writer;
			_makeTool.foreachParentTool(k->
			{
				SSet<String> newClsSet=_isClient ? k.getHClientNewClassFilesSet() : k.getHServerNewClassFilesSet();
				
				//有
				if(newClsSet.contains(useName))
				{
					String dicName=k.getProjectLFront() + baseDicName;
					
					if(useArr)
					{
						tWriter.writeArraySet(_outputCls.getFieldWrap(dicName),_data.getKeysCombineValue("config.",tWriter.getCode()),"config");
					}
					else
					{
						tWriter.writeMapAdd(_outputCls.getFieldWrap(dicName),_data.getKeysCombineValue("config.",tWriter.getCode()),"config");
					}
				}
			});
		}
		
		writer.writeRightBrace();
		
		writer.writeEnd();
		
		method.content=writer.toString();
		_configDataCls.addMethod(method);
		
		//refresh
		
		//新的
		if(newFiles.contains(useName))
		{
			method=new MethodInfo();
			method.visitType=VisitType.Private;
			method.name="refresh" + _inputCls.clsName;
			method.describe="刷新" + _outputCls.clsDescribe;
			method.returnType=_code.Void;
			
			writer=_outputCls.createWriter();
			
			if(useArr)
			{
				writer.writeForEachArray(_outputCls.getFieldWrap(field.name),"config",_outputCls.clsName,true);
				writer.writeCustom("if(config!="+_code.Null+")");
				writer.writeCustomWithOff(1,"config.refresh();");
				writer.writeForEachArrayEnd();
			}
			else
			{
				writer.writeForEachMapValue(_outputCls.getFieldWrap(field.name),keyType,"config",_outputCls.clsName);
				writer.writeCustom("config.refresh();");
				writer.writeForEachMapEnd();
			}
			
			writer.writeEnd();
			
			method.content=writer.toString();
			_configDataCls.addMethod(method);
		}
	}
	
	@Override
	protected void toMakeAfter()
	{
		super.toMakeAfter();
		
		//删除以下两个
		
		_outputCls.removeMethodByName("generateAfterReadConfig");//放一下
		
		//MethodInfo method1=_outputCls.getMethod("get,string");
		//if(method1!=null)
		//	_outputCls.removeMethod(method1);
		//
		//MethodInfo method2=_outputCls.getMethod("get,String");
		//if(method2!=null)
		//	_outputCls.removeMethod(method2);
		
		//客户端并且有资源相关
		if((_isClient && !_data.resourceKeys.isEmpty())
				|| !_data.languageKeys.isEmpty()
				|| (_isClient && !_data.internationalResourceKeys.isEmpty())
				|| !_data.timeExpressionKeys.isEmpty()
				|| !_data.bigFloatKeys.isEmpty())
		{
			MethodInfo method=new MethodInfo();
			method.visitType=VisitType.Protected;
			method.isOverride=true;
			method.name="generateRefresh";
			method.describe="生成刷新配置";
			method.returnType=_code.Void;
			
			CodeWriter writer=_outputCls.createWriter();
			
			if(_parentTool!=null && _data.hasParent)
			{
				writer.writeSuperMethod(method);
				writer.writeEmptyLine();
			}
			
			boolean has=false;
			
			for(String key : _data.languageKeys)
			{
				if(_isClient && ShineToolSetting.isClientOnlyData)
					continue;
				
				FieldInfo ff=_outputCls.getField(key);
				
				if(ff!=null)
				{
					if(_code.isString(ff.type))
					{
						has=true;
						
						FieldInfo tf=new FieldInfo();
						tf.name="_"+ff.name;
						tf.type=ff.type;
						tf.describe=ff.describe+"原值";
						tf.visitType=VisitType.Private;
						_outputCls.addField(tf);
						
						_outputCls.addImport(ShineToolSetting.globalPackage+"commonBase.config.game.LanguageConfig");
						
						writer.writeCustom("if("+_outputCls.getFieldWrap(tf.name)+"=="+_code.Null+")");
						writer.writeCustomWithOff(1,_outputCls.getFieldWrap(tf.name)+"="+_outputCls.getFieldWrap(ff.name)+";");
						writer.writeVarSet(_outputCls.getFieldWrap(ff.name),"LanguageConfig.getText(" + _outputCls.getFieldWrap(tf.name) + ")");
						writer.writeEmptyLine();
					}
				}
			}
			
			for(String key : _data.timeExpressionKeys)
			{
				FieldInfo ff=_outputCls.getField(key);
				
				if(ff!=null)
				{
					if(_code.isString(ff.type))
					{
						has=true;
						
						FieldInfo tf=new FieldInfo();
						tf.name=ff.name+"T";
						tf.type="TimeExpression";
						tf.describe=ff.describe+"(时间表达式)";
						tf.visitType=VisitType.Public;
						_outputCls.addField(tf);
						
						_outputCls.addImport(ShineToolSetting.globalPackage+"shine.dataEx.TimeExpression");
						
						writer.writeVarSet(_outputCls.getFieldWrap(tf.name),_code.createNewObject("TimeExpression",_outputCls.getFieldWrap(ff.name)));
						writer.writeEmptyLine();
					}
				}
			}
			
			if(_isClient)
			{
				SSet<String> temp=new SSet<>();
				
				for(String key : _data.internationalResourceKeys)
				{
					FieldInfo ff=_outputCls.getField(key);
					
					if(ff!=null)
					{
						if(_code.isString(ff.type))
						{
							has=true;
							
							temp.add(ff.name);
							
							FieldInfo tf=new FieldInfo();
							tf.name="_"+ff.name;
							tf.type=ff.type;
							tf.describe=ff.describe+"原值";
							tf.visitType=VisitType.Private;
							_outputCls.addField(tf);
							
							_outputCls.addImport(ShineToolSetting.globalPackage+"commonBase.config.game.InternationalResourceConfig");
							
							writer.writeCustom("if("+_outputCls.getFieldWrap(tf.name)+"=="+_code.Null+")");
							writer.writeCustomWithOff(1,_outputCls.getFieldWrap(tf.name)+"="+_outputCls.getFieldWrap(ff.name)+";");
							writer.writeVarSet(_outputCls.getFieldWrap(ff.name),"InternationalResourceConfig.getText(" + _outputCls.getFieldWrap(tf.name) + ")");
							writer.writeEmptyLine();
						}
					}
				}
				
				for(String key : _data.resourceKeys)
				{
					temp.add(key);
				}
				
				for(String key : temp)
				{
					FieldInfo ff=_outputCls.getField(key);
					
					if(ff!=null)
					{
						if(_code.isString(ff.type))
						{
							has=true;
							
							_outputCls.addImport("shine.control.LoadControl");
							
							FieldInfo tf=new FieldInfo();
							tf.name=ff.name + "T";
							tf.describe=ff.describe + "(资源转)";
							tf.visitType=ff.visitType;
							tf.type=_code.Int;
							_outputCls.addField(tf,_outputCls.getFieldIndex(ff.name) + 1);
							
							writer.writeVarSet(_outputCls.getFieldWrap(tf.name),"LoadControl.getResourceIDByName(" + _outputCls.getFieldWrap(ff.name) + ")");
						}
						//数组
						else if(_code.getCollectionVarType(ff.type)==VarType.Array)
						{
							has=true;
							
							_outputCls.addImport("shine.control.LoadControl");
							
							FieldInfo tf=new FieldInfo();
							tf.name=ff.name + "T";
							tf.describe=ff.describe + "(资源转)";
							tf.visitType=ff.visitType;
							tf.type=_code.getArrayType(_code.Int,false);
							
							_outputCls.addField(tf,_outputCls.getFieldIndex(ff.name) + 1);
							
							writer.writeVarSet(_outputCls.getFieldWrap(tf.name),"LoadControl.getResourceIDsByNames(" + _outputCls.getFieldWrap(ff.name) + ")");
						}
					}
				}
			}
			
			for(ConfigFieldData conF:_data.bigFloatKeys)
			{
				FieldInfo ff=_outputCls.getField(conF.name);
				
				if(ff!=null)
				{
					has=true;
					writeFieldForEach(ff,conF,writer,v->
					{
						writer.writeCustom(v+".refresh();");
					});
				}
			}
			
			if(has)
			{
				writer.writeEnd();
				
				method.content=writer.toString();
				
				//添加
				_outputCls.addMethod(method);
			}
			else
			{
				_outputCls.removeMethodByName("generateRefresh");
			}
		}
		else
		{
			_outputCls.removeMethodByName("generateRefresh");
		}
		
		MethodInfo aMethod=_outputCls.getMethodByName("afterReadConfig");
		
		//空方法移除
		if(aMethod!=null && aMethod.isEmpty(_outputCls.getCodeType()))
		{
			_outputCls.removeMethod(aMethod);
		}
		
		MethodInfo method;
		
		//改方法现在为手动override
		
		//if(_outputCls.getMethodByName("afterReadConfig")==null)
		//{
		//	method=new MethodInfo();
		//	method.visitType=VisitType.Protected;
		//	method.isOverride=true;
		//	method.name="afterReadConfig";
		//	method.describe="读完表后处理";
		//	method.returnType=_code.Void;
		//	method.makeEmptyMethodWithSuper(_outputCls.getCodeType());
		//	_outputCls.addMethod(method);
		//}
		
		//没有就补
		if(_outputCls.getMethodByName("afterReadConfigAll")==null)
		{
			method=new MethodInfo();
			method.visitType=VisitType.Public;
			method.isStatic=true;
			method.name="afterReadConfigAll";
			method.describe="读完所有表后处理";
			method.returnType=_code.Void;
			method.makeEmptyMethod(_outputCls.getCodeType());
			_outputCls.addMethod(method);
		}
		
		////客户端的main工程
		//if(_makeTool.isMainProject() && _isClient)
		//{
		//	ILClassInfo cls=new ILClassInfo(_outputCls.clsName);
		//	cls.addMethod("toReadBytesSimple",null,VisitType.Protected,true,"BytesReadStream","stream");
		//	cls.addMethod("afterReadConfig",null,VisitType.Protected,true);
		//	cls.addMethod("generateRefresh",null,VisitType.Protected,true);
		//
		//	ILRuntimeAdapterTool.makeILCls(cls,_outputCls);
		//	ILRuntimeAdapterTool.doOneClsOnly(cls,_adapterPath,false);
		//}
		
		
	}
	
	private void writeFieldForEach(FieldInfo field, ConfigFieldData configField,CodeWriter writer,ObjectCall<String> func)
	{
		String tt=field.type;
		String nn=field.name;
		
		if(configField.typeList.size()==1)
		{
			func.apply(writer.getCode().getThisFront()+nn);
			return;
		}
		
		int ss=configField.typeList.size()-1;
		for(int i=0;i<ss;i++)
		{
			int type=configField.typeList.get(i);
			
			//数组
			if(type==ConfigFieldType.Array)
			{
				String eType=writer.getCode().getVarArrayType(tt);
				String eName=nn+"A"+i;
				
				writer.writeForEachArray(nn,eName,eType);
				
				nn=eName;
				tt=eType;
			}
			else
			{
				Ctrl.throwError("config遍历不支持其他类型",type);
			}
		}
		
		func.apply(nn);
		
		for(int i=0;i<ss;i++)
		{
			int type=configField.typeList.get(i);
			
			//数组
			if(type==ConfigFieldType.Array)
			{
				writer.writeForEachArrayEnd();
			}
			else
			{
				Ctrl.throwError("config遍历不支持其他类型",type);
			}
		}
	}
	
	@Override
	protected void endExecute()
	{
		super.endExecute();
		
		//SSet<String> keys=new SSet<>();
		//
		//SList<String> list=_isClient ? _makeTool.getHClientNewClassFiles() : _makeTool.getHServerNewClassFiles();
		//
		//for(String v : list)
		//{
		//	keys.add(_makeTool.useNameToUpperName(v));
		//}
		//
		//for(String v:_vList)
		//{
		//	keys.add(_makeTool.useNameToUpperName(v));
		//}
		//
		//_configDataCls.getMethodDic().forEachValueS(v->
		//{
		//	String key=null;
		//
		//	if(v.name.startsWith("read"))
		//		key=v.name.substring(4);
		//
		//	if(v.name.startsWith("refresh"))
		//		key=v.name.substring(7);
		//
		//	//排除readBytes和refreshData
		//	if(key!=null && !keys.contains(key) && !key.equals("Data") && !key.equals("Bytes"))
		//	{
		//		_configDataCls.removeMethod(v);
		//	}
		//});
		
		
		MethodInfo method;
		
		for(File v : _deleteLastFileDic)
		{
			String useName=_makeTool.getUseName(v);
			
			//不是自定义
			if(!_makeTool.getCustomDescribes().contains(useName))
			{
				String key=_makeTool.useNameToUpperName(useName);
				
				method=_configDataCls.getMethodByName("read" + key);
				
				if(method!=null)
					_configDataCls.removeMethod(method);
				
				method=_configDataCls.getMethodByName("refresh" + key);
				
				if(method!=null)
					_configDataCls.removeMethod(method);
			}
		}
	}
	
	protected boolean isNeedAddField(FieldInfo field)
	{
		if(_parentTool!=null)
		{
			if(_superFields.contains(field.name))
			{
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	protected void makeFileConstClsName(File f,ConfigRecordInfo info)
	{
		String useName=_makeTool.getUseName(f);
		
		String cName=_makeTool.getClientConstNameDic().get(useName);
		
		if(!StringUtils.isNullOrEmpty(cName))
		{
			info.constName=cName;
			info.hasClientConst=true;
		}
		
		String sName=_makeTool.getServerConstNameDic().get(useName);
		
		if(!StringUtils.isNullOrEmpty(sName))
		{
			info.constName=sName;
			info.hasServerConst=true;
		}
	}
	
	@Override
	protected void beforeDeleteOutFile(File file)
	{
		ClassInfo cls=ClassInfo.getSimpleClassInfoFromPathAbs(file.getPath());
		
		//首字母大写的
		String useName=cls.clsName.substring(0,cls.clsName.length() - "Config".length());
		
		String front=_makeTool.getProjectUFront();
		
		if(!front.isEmpty())
		{
			useName=useName.substring(front.length());
		}
		
		String dicName=_makeTool.upperNameToUseName(useName) + "Dic";
		
		dicName=_makeTool.getProjectLFront()+dicName;
		
		_configDataCls.removeField(dicName);
		_configDataCls.removeMethodByName("read" + useName);
		_configDataCls.removeImport(cls.getQName());
	}
	
}
