package com.home.shineTool.tool.part;

import com.home.shine.support.collection.SMap;
import com.home.shine.support.collection.SSet;
import com.home.shine.utils.FileUtils;
import com.home.shine.utils.StringUtils;
import com.home.shineTool.constlist.MakeMethodType;
import com.home.shineTool.constlist.VisitType;
import com.home.shineTool.reflect.FieldInfo;
import com.home.shineTool.reflect.MethodArgInfo;
import com.home.shineTool.reflect.MethodInfo;
import com.home.shineTool.reflect.cls.ClassInfo;
import com.home.shineTool.reflect.code.CodeWriter;
import com.home.shineTool.global.ShineToolSetting;
import com.home.shineTool.tool.data.DataExportTool;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/** 部件数据 */
public class PartDataExportTool extends DataExportTool
{
	private Map<String,ClassInfo> _exDic=new HashMap<>();
	
	private Map<String,File> _exFileDic=new HashMap<>();
	
	private Map<File,ClassInfo> _fileToClsDic=new HashMap<>();
	
	private String _from;
	
	private String _to;
	
	private String _fromMark="";
	
	private String _toMark="";
	
	private boolean _isEx=false;
	
	private CodeWriter _copyServerWriter;
	
	private ClassInfo _serverCls;
	
	private SSet<String> _didInputFiles=new SSet<>();
	
	public void addExInputDic(SMap<ClassInfo,File> exDic)
	{
		exDic.forEach((k,v) -> {
			_exDic.put(k.getQName(),k);
			_exFileDic.put(k.getQName(),v);
		});
	}
	
	public void setPackageReplace(String from,String to)
	{
		_from=from;
		_to=to;
	}
	
	public void setMarkReplace(String fromMark,String toMark)
	{
		_fromMark=fromMark;
		_toMark=toMark;
	}
	
	@Override
	protected void addInputClass(ClassInfo cls)
	{
		super.addInputClass(cls);
		
		didInputCls(cls.getQName());
	}
	
	@Override
	protected void didInputCls(String qName)
	{
		if(!_isEx && !_toMark.isEmpty())
		{
			if(qName.endsWith(_toMark))
			{
				qName=qName.substring(0,qName.length() - _toMark.length()) + _fromMark;
				
				String pp=qName.replace("." + _to + ".","." + _from + ".");
				
				if(_exDic.containsKey(pp))
				{
					_exDic.remove(pp);
				}
			}
		}
	}
	
	@Override
	protected void endExecute()
	{
		doEx();
		
		super.endExecute();
	}
	
	private void doEx()
	{
		_isEx=true;
		
		for(ClassInfo cls : _exDic.values())
		{
			File f=_exFileDic.get(cls.getQName());
			
			ClassInfo cc=ClassInfo.getClassInfo(cls.getCodeType());
			
			//先复制import
			for(String s : cls.getImports())
			{
				cc.addImport(s);
			}
			
			cc.clsName=cls.clsName.substring(0,cls.clsName.length() - _fromMark.length()) + _toMark;
			cc.clsDescribe=cls.clsDescribe;
			cc.addImport(cls.getQName());
			cc.packageStr=cls.packageStr.replace("." + _from,"." + _to);
			
			//有继承
			if(!cls.extendsClsName.isEmpty())
			{
				String eQName=cls.getExtendClsQName();
				eQName=eQName.substring(0,eQName.length() - _fromMark.length()) + _toMark;
				eQName=eQName.replace("." + _from + ".","." + _to + ".");
				cc.addImport(eQName);
				cc.extendsClsName=StringUtils.getClassNameForQName(eQName);
			}
			
			//属性copy
			for(String d : cls.getFieldNameList())
			{
				FieldInfo ff=cls.getField(d);
				
				cc.addField(ff);
			}
			
			String p=f.getPath();
			
			String front=FileUtils.getFileFrontName(p);
			
			String ex=FileUtils.getFileExName(p);
			
			front=front.replace(File.separator + _from + File.separator,File.separator + _to + File.separator);
			
			f=new File(front.substring(0,front.length() - _fromMark.length()) + _toMark + "." + ex);
			
			_fileToClsDic.put(f,cc);
			
			setNeedRecord(false);
			executeFile(f);
			setNeedRecord(true);
		}
	}
	
	/** 通过输入File获取ClassInfo */
	protected ClassInfo toGetInputClass(File f)
	{
		if(!_isEx)
		{
			return super.toGetInputClass(f);
		}
		else
		{
			return _fileToClsDic.get(f);
		}
	}
	
	@Override
	protected void toMakeBefore()
	{
		super.toMakeBefore();
		
		_didInputFiles.add(_inputCls.clsName);
		
		PartOutputInfo pOutInfo=(PartOutputInfo)_outputInfo;
		
		//服务器的客户端
		if(pOutInfo.group==0 && pOutInfo.type==2)
		{
			_outputCls.addImport(ShineToolSetting.dataBaseQName);
			
			_copyServerWriter=_outputCls.createWriter();
			
			//通过基类名判定
			boolean hasExtend=!_outputCls.extendsClsName.equals(ShineToolSetting.dataBaseName);
			
			if(hasExtend)
			{
				_copyServerWriter.writeCustom(_code.Super + ".toCopyFromServer(data);");
			}
			
			//有需要的
			if(_inputCls.getFieldNameList().size()>0)
			{
				//又一次定制
				String serverClsName=_outputCls.clsName.substring(0,_outputCls.clsName.length() - "ClientPartData".length()) + "PartData";
				
				String serverPackName=_outputCls.packageStr.substring(0,_outputCls.packageStr.length() - "clientData".length()) + "data";
				
				_outputCls.addImport(serverPackName + "." + serverClsName);
				
				_copyServerWriter.writeCustom("if(!(" + _code.getVarTypeIs("data",serverClsName) + "))");
				_copyServerWriter.writeReturnVoid(1);
				_copyServerWriter.writeEmptyLine();
				_copyServerWriter.writeVarCreate("mData",serverClsName,_code.getVarTypeTrans("data",serverClsName));
				
				//
				String pp=_outputClsPath;
				
				pp=pp.replace("/clientData/","/data/");
				pp=pp.replace("ClientPartData","PartData");
				
				_serverCls=ClassInfo.getClassInfoFromPath(pp);
			}
			
			_copyServerWriter.writeEmptyLine();
		}
		else
		{
			_copyServerWriter=null;
		}
	}
	
	@Override
	protected void toMakeOneField(FieldInfo field,FieldInfo outField)
	{
		super.toMakeOneField(field,outField);
		
		//服务器的客户端
		if(_copyServerWriter!=null && _serverCls!=null)
		{
			FieldInfo serverField=_serverCls.getField(outField.name);
			
			//属性存在,并且类型一致
			if(serverField!=null && serverField.type.equals(outField.type))
			{
				//String tName=outField.getUseFieldName();
				
				int copyType=ShineToolSetting.copyServerUseShadowCopy ? MakeMethodType.shadowCopy : MakeMethodType.copy;
				
				makeFunctionCode(_copyServerWriter,copyType,_outputCls.getFieldWrap(outField.name),field.type,"mData." + outField.name,field);
				_copyServerWriter.writeEmptyLine();
			}
		}
	}
	
	@Override
	protected void toMakeAfter()
	{
		super.toMakeAfter();
		
		if(_copyServerWriter!=null)
		{
			_copyServerWriter.writeEnd();
			
			//copy
			MethodInfo method=new MethodInfo();
			method.name="toCopyFromServer";
			method.describe="从服务器数据上复制数据"+(ShineToolSetting.copyServerUseShadowCopy ? "(潜拷,因InitClient会copy)" : "(深拷)");
			method.visitType=VisitType.Protected;
			method.isStatic=false;
			method.returnType=_code.Void;
			method.args.add(new MethodArgInfo("data","BaseData"));
			method.content=_copyServerWriter.toString();
			method.isOverride=true;
			
			_outputCls.addMethod(method);
			
			//销毁
			_copyServerWriter=null;
		}
	}
	
	/** 获取执行过的输入文件 */
	public SSet<String> getDidInputFiles()
	{
		return _didInputFiles;
	}
}
