package com.home.shineTool.tool.config;

import com.home.shine.support.collection.SList;
import com.home.shine.utils.FileUtils;
import com.home.shineTool.constlist.CodeType;
import com.home.shineTool.constlist.MakeMethodType;
import com.home.shineTool.constlist.ProjectType;
import com.home.shineTool.constlist.VisitType;
import com.home.shineTool.dataEx.ConfigClassData;
import com.home.shineTool.reflect.FieldInfo;
import com.home.shineTool.reflect.MethodArgInfo;
import com.home.shineTool.reflect.MethodInfo;
import com.home.shineTool.reflect.cls.ClassInfo;
import com.home.shineTool.reflect.code.CodeWriter;
import com.home.shineTool.tool.base.BaseOutputInfo;
import com.home.shineTool.global.ShineToolSetting;
import com.home.shineTool.tool.data.DataOutputInfo;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class VConfigExportTool extends ConfigExportToolBase
{
	private CodeWriter _initWriter;
	
	protected Map<String,String> _packageDic=new HashMap<>();
	
	public VConfigExportTool(ConfigMakeTool tool)
	{
		super(tool);
		
		_isH=false;
		
		_isDefaultExtendsUseData=false;
		_defaultNameTail="";//没有默认后缀
		
		ConfigMakeTool parentMakeTool=_makeTool.getParentTool();
		
		if(parentMakeTool!=null)
		{
			_parentTool=parentMakeTool.getVExportTool();
		}
	}
	
	public void setClient(SList<ConfigClassData> inputs,String path,int codeType)
	{
		super.setClient(inputs,path,codeType);
		
		for(ConfigClassData data : inputs)
		{
			_packageDic.put(data.file.getName(),data.clientCls.packageStr);
		}
	}
	
	public void setServer(SList<ConfigClassData> inputs,String path,int codeType)
	{
		super.setServer(inputs,path,codeType);
		
		for(ConfigClassData data : inputs)
		{
			_packageDic.put(data.file.getName(),data.serverCls.packageStr);
		}
	}
	
	private boolean isData(int group)
	{
		return (group & 1)==1;
	}
	
	public void executeConfig()
	{
		//group & 1==0 Global
		//group & 1==1 GlobalReadData
		
		_isClient=false;
		//setInput(_serverPath,"",null,CodeType.Java);
		addOutput(createForData(_serverPath,0,_serverCode));
		addOutput(createForData(_serverPath,1,_serverCode));
		executeList(_serverFileList);
		_outputs.clear();

		if(ShineToolSetting.needClient)
		{
			_isClient=true;
			//setInput(_clientPath,"",null,CodeType.Java);
			addOutput(createForData(_clientPath,2,_clientCode));
			addOutput(createForData(_clientPath,3,_clientCode));
			executeList(_clientFileList);
			_outputs.clear();
		}

		recordFiles();
	}

	@Override
	protected String getOutClassQNameByGroup(int group, String inQName)
	{
		//转到正确的客户端和服务器
		return toGetOutClassQNameByGroup(group>>1, inQName);
	}
	
	@Override
	protected int addOneDefine(String cName,String des)
	{
		String useName=_makeTool.upperNameToUseName(cName);
		
		int re;
		
		//存在,才写,（并且为防止重复写，只有server写)
		if(_makeTool.getProjectType()==ProjectType.Common)//&& !_isClient
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

		//String path=f.getParent() + "/"+_packageDic.get(f.getName())+"/"+ _makeTool.useNameToUpperName(_makeTool.getUseName(f)) + "." + CodeType.getExName(_sourceCode);
		String path=f.getParent() + "/"+_packageDic.get(f.getName())+"/"+ FileUtils.getFileFrontName(f.getName()) + "." + CodeType.getExName(_sourceCode);
		//
		//path=FileUtils.fixPath(path);

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
	protected void deleteLastFiles()
	{
		//VConfig无需执行
	}
	
	@Override
	protected String getOutFilePath(File inFile)
	{
		return super.getOutFilePath(inFile);
	}
	
	@Override
	protected String getOutClassNameByInfo(BaseOutputInfo outInfo,String name)
	{
		return _makeTool.getProjectUFront() + super.getOutClassNameByInfo(outInfo,name);
	}
	
	/** 构建Data所需 */
	private DataOutputInfo createForData(String path,int group,int codeType)
	{
		DataOutputInfo re=new DataOutputInfo();
		re.path=path;
		re.nameTail=isData(group) ? "ReadData" : "";
		re.group=group;
		re.codeType=codeType;
		re.defineIndex=-1;
		re.makeIndex=-1;
		
		re.superQName=isData(group) ? ShineToolSetting.dataBaseQName : "";
		
		re.useStaticField=!isData(group);
		re.needs[MakeMethodType.readSimple]=isData(group);
		
		re.isOverrideSuper=_parentTool==null;
		//re.isOverrideSuper=true;
		
		return re;
	}
	
	@Override
	protected String toMakeExtend()
	{
		//Data
		if(isData(_outputInfo.group))
		{
			if(_parentTool!=null && _data.hasParent)
			{
				return getParentClsQName(_parentTool);
			}
		}
		
		return "";
	}
	
	@Override
	protected void toMakeFirst()
	{
		super.toMakeFirst();
		
		if(!isData(_outputInfo.group))
		{
			_initWriter=_outputCls.createWriter();
		}
		else
		{
			if(_isClient)
			{
				_outputCls.addAnnotation("Hotfix");//客户端热更标记
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
	protected void toMakeOneField(FieldInfo field,FieldInfo outField)
	{
		//game不写默认值,common需要写
		//if(!_isCommon)
		//{
		//	outField.defaultValue="";
		//}
		
		//现在改成都不要
		outField.defaultValue="";
		
		super.toMakeOneField(field,outField);
		
		boolean need=true;
		
		if(_parentTool!=null)
		{
			if(_superFields.contains(field.name))
			{
				need=false;
			}
		}
		
		////是数据
		//if(!need)
		//{
		//	//删了该属性
		//	_outputCls.removeField(field.name);
		//}
		
		//if(need || _isData)
		if(!isData(_outputInfo.group) && need)
		{
			_initWriter.writeVarSet(_outputCls.getFieldWrap(field.name),"data." + field.name);
		}
	}

	@Override
	protected void toMakeAfter()
	{
		super.toMakeAfter();
		
		if(!isData(_outputInfo.group))
		{
			_outputCls.addImport(_outputCls.getQName() + "ReadData");
			
			MethodInfo method=new MethodInfo();
			method.visitType=VisitType.Public;
			method.name="readFromData";
			method.describe="从流读取";
			method.returnType=_code.Void;
			method.isStatic=true;
			method.args.add(new MethodArgInfo("data",_outputCls.clsName + "ReadData"));
			
			//method.isVirtual=_parentTool==null;
			//method.isOverride=_parentTool!=null;
			
			_initWriter.writeEnd();
			method.content=_initWriter.toString();
			_outputCls.addMethod(method);
			
			method=_outputCls.getMethodByName("afterReadConfig");
			
			if(method==null)
			{
				method=new MethodInfo();
				method.visitType=VisitType.Public;
				method.name="afterReadConfig";
				method.describe="读取后";
				method.returnType=_code.Void;
				method.isStatic=true;
				method.makeEmptyMethod(_outputCls.getCodeType());
				_outputCls.addMethod(method);
			}
			
			method=_outputCls.getMethodByName("afterReadConfigAll");
			
			if(method==null)
			{
				method=new MethodInfo();
				method.visitType=VisitType.Public;
				method.name="afterReadConfigAll";
				method.describe="全部读取后";
				method.returnType=_code.Void;
				method.isStatic=true;
				method.makeEmptyMethod(_outputCls.getCodeType());
				_outputCls.addMethod(method);
			}
		}
		else
		{
			//MethodInfo method=_outputCls.getMethodByName("toReadBytesSimple");
			//_outputCls.removeMethod(method);
			//
			//method.visitType=VisitType.Public;
			//method.name="readBytes";
			//method.isStatic=false;
			//method.isOverride=false;
			//
			//method.isVirtual=_parentTool==null;
			//method.isOverride=_parentTool!=null;
			//
			//_outputCls.addMethod(method);
			
			//			if(_outputCls.getCodeType()==CodeType.CS)
			//			{
			//				((CSClassInfo)_outputCls).ignoreHidingWarn();
			//			}
		}
	}
	
	public ClassInfo getVInputFile(File f,boolean isClient)
	{
		ConfigClassData data=_clsDic.get(f.getName());
		
		ClassInfo cls=isClient ? data.clientCls : data.serverCls;
		
		return cls;
	}
	
	public ClassInfo getVOutQNameByFile(File f,boolean isClient,boolean isData)
	{
		int key=isData ? 1 :0;
		
		if(isClient)
			key|=2;
		
		String filePath=getOutPathFromParent(this,key,toGetInputFile(f));
		
		return ClassInfo.getClassInfoFromPath(filePath);
	}
}
