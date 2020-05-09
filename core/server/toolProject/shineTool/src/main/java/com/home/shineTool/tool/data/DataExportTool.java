package com.home.shineTool.tool.data;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.SList;
import com.home.shine.support.func.ObjectCall;
import com.home.shine.utils.StringUtils;
import com.home.shineTool.constlist.CodeType;
import com.home.shineTool.constlist.DataGroupType;
import com.home.shineTool.constlist.MakeMethodType;
import com.home.shineTool.constlist.ProjectType;
import com.home.shineTool.constlist.VarType;
import com.home.shineTool.constlist.VisitType;
import com.home.shineTool.reflect.AnnotationInfo;
import com.home.shineTool.reflect.FieldInfo;
import com.home.shineTool.reflect.MethodArgInfo;
import com.home.shineTool.reflect.MethodInfo;
import com.home.shineTool.reflect.cls.ClassInfo;
import com.home.shineTool.reflect.code.CodeInfo;
import com.home.shineTool.reflect.code.CodeWriter;
import com.home.shineTool.tool.base.BaseExportTool;
import com.home.shineTool.tool.base.BaseOutputInfo;
import com.home.shineTool.global.ShineToolSetting;
import com.home.shineTool.tool.hotfix.ILClassInfo;
import com.home.shineTool.tool.hotfix.ILRuntimeAdapterTool;

/** 数据导出工具(包名两边一致) */
public class DataExportTool extends BaseExportTool
{
	/** 回车 */
	public static final String Enter=ClassInfo.Enter;
	
	/** 工程类型 */
	protected int _projectType=0;
	/** 前缀(大写) */
	protected String _front="";
	
	//temp
	private DataOutputInfo _dataOutputInfo;
	
	/** copyData sb */
	private CodeWriter _copyDataFuncWriter;
	
	//6
	private CodeWriter[] _methodWriters;
	
	/** 方法是否写入过 */
	private boolean[] _methodWrited;
	
	/** 是否在makeFunctionCode过程中出现arr的unchecked问题 */
	protected boolean _needArrayUnchecked=false;
	
	private boolean[] _needUnckeckeds;
	
	protected DataExportTool _commonTool;
	
	//父项列表
	private SList<DataExportTool> _parentList=new SList<>();
	
	//main adapters
	
	/** 是否是热更工程 */
	private boolean _isHotfix=false;
	
	private String _adapterPath;
	private CodeWriter _adapterWriter;
	
	/** 输入的完全限定名回调(用来做message版本检查) */
	private ObjectCall<String> _inputQFunc;
	
	//temp
	private boolean _messageDontCopy=false;
	
	public DataExportTool()
	{
		
	}
	
	/** 设置工程类型 */
	public void setProjectType(int type)
	{
		_projectType=type;
		
		switch(_projectType)
		{
			case ProjectType.Common:
			{
				_front="";
			}
			break;
			case ProjectType.Game:
			{
				_front="G";
			}
			break;
			case ProjectType.HotFix:
			{
				_front="H";
			}
			break;
		}
	}
	
	public void setCommonTool(DataExportTool tool)
	{
		_commonTool=tool;
	}
	
	public void addParentTool(DataExportTool tool)
	{
		if(tool==null)
			return;
		
		//一般情况下只有1个
		_parentList.add(tool);
	}
	
	protected ClassInfo getInputClsAbs(String qName)
	{
		ClassInfo cls=super.getInputClsAbs(qName);
		
		if(cls!=null)
			return cls;
		
		if(_commonTool!=null)
		{
			return _commonTool.getInputClsAbs(qName);
		}
		
		if(!_parentList.isEmpty())
		{
			for(DataExportTool v:_parentList)
			{
				ClassInfo cc=v.getInputClsAbs(qName);
				
				if(cc!=null)
					return cc;
			}
		}
		
		return null;
	}
	
	/** 获取第一个上级 */
	public DataExportTool getUpTool()
	{
		if(_commonTool!=null)
			return _commonTool;
		
		if(!_parentList.isEmpty())
		{
			return _parentList.get(0);
		}
		
		return null;
	}
	
	/** 设置是否客户端主工程(data部分用,config不用) */
	public void setHotfix(boolean value,String path,CodeWriter writer)
	{
		_isHotfix=value;
		_adapterPath=path;
		_adapterWriter=writer;
	}
	
	public void setInputQFunc(ObjectCall<String> func)
	{
		_inputQFunc=func;
	}
	
	//---make部分---//
	
	
	@Override
	protected void endExecute()
	{
		super.endExecute();
		
		//客户端的主工程
		if(_isHotfix)
		{
			BaseOutputInfo outputInfo=getOutputInfo(DataGroupType.Client);
			
			for(String v : _allInputQNameSet)
			{
				//需要继承的才生成适配器
				if(isClassNeedExtends(v))
				{
					String clsName=StringUtils.getClassNameForQName(v);
					
					ILRuntimeAdapterTool.doOneWriter(getOutClassNameByInfo(outputInfo,getCNameByClassName(clsName)),_adapterWriter);
				}
			}
		}
		
		if(_inputQFunc!=null)
		{
			for(String v : _allInputQNameSet)
			{
				_inputQFunc.apply(v);
			}
		}
	}
	
	@Override
	protected String getOutClassQNameByGroup(int group, String inQName)
	{
		//转一下
		if(group==DataGroupType.Server2)
			group=DataGroupType.Server;
		
		//转到正确的客户端和服务器
		return super.getOutClassQNameByGroup(group, inQName);
	}
	
	@Override
	protected void toMakeInput()
	{
		_customSerialize=_inputCls.hasAnnotation("CustomSerialize");
		_messageDontCopy=_inputCls.hasAnnotation("MessageDontCopy");
	}
	
	@Override
	protected void toMakeFirst()
	{
		super.toMakeFirst();
		
		_dataOutputInfo=(DataOutputInfo)_outputInfo;
		
	}
	
	@Override
	protected void toMakeBefore()
	{
		toMakeHead();
		
		toMakeMethods();
	}
	
	/** 构造头部分(类,继承,import) */
	private void toMakeHead()
	{
		//有读
		if(_dataOutputInfo.needReadFull() || _dataOutputInfo.needReadSimple())
		{
			_outputCls.addImport(ShineToolSetting.bytesReadStreamQName);
		}
		
		//有写
		if(_dataOutputInfo.needWriteFull() || _dataOutputInfo.needWriteSimple())
		{
			_outputCls.addImport(ShineToolSetting.bytesWriteStreamQName);
		}
	}
	
	@Override
	protected void toMakeMainMethod()
	{
		super.toMakeMainMethod();
		
		//推送并且有注解
		if(_dataOutputInfo.isRequest)
		{
			if(_messageDontCopy)
			{
				_mainMethodWriter.writeCustom(_outputCls.getThisFront()+"setDontCopy();");
			}
			
			if(_inputCls.hasAnnotation("MessageDontCache"))
			{
				_mainMethodWriter.writeCustom(_outputCls.getThisFront()+"setDontCache();");
			}
			
			if(_inputCls.hasAnnotation("MessageLong"))
			{
				_mainMethodWriter.writeCustom(_outputCls.getThisFront()+"setLongMessage();");
			}
			
			if(_inputCls.hasAnnotation("MessageNeedRelease"))
			{
				_mainMethodWriter.writeCustom(_outputCls.getThisFront()+"setNeedRelease();");
			}
		}
		
		//接收并且有注解
		if(_dataOutputInfo.isResponse)
		{
			if(_inputCls.hasAnnotation("MessageUseMainThread"))
			{
				_mainMethodWriter.writeCustom(_outputCls.getThisFront()+"setUseMainThread();");
			}
			else if(_inputCls.hasAnnotation("MessageUsePoolThread"))
			{
				_mainMethodWriter.writeCustom(_outputCls.getThisFront()+"setUsePoolThread();");
			}
			
			if(_inputCls.hasAnnotation("MessageLong"))
			{
				_mainMethodWriter.writeCustom(_outputCls.getThisFront()+"setLongMessage();");
			}
			
			if(_inputCls.hasAnnotation("MessageNeedRelease"))
			{
				_mainMethodWriter.writeCustom(_outputCls.getThisFront()+"setNeedRelease();");
			}
			
			String av;
			//需要功能开启
			if(_dataOutputInfo.group==DataGroupType.Server && (av=_inputCls.getAnnotationValue("NeedFunctionOpen"))!=null)
			{
				//去掉首尾"
				av=av.substring(1,av.length()-1);
				
				_outputCls.addImport(ProjectType.getEnumQName("FunctionType",_projectType));
				_mainMethodWriter.writeCustom(_outputCls.getThisFront()+"setNeedFunctionID("+_front+"FunctionType."+av+");");
			}
		}
	}
	
	@Override
	protected void toMakeSuperMainMethod()
	{
		//构造传参
		
		if(_dataOutputInfo.isCreateSetParams)
		{
			_copyDataFuncWriter=_outputCls.createWriter();
			
			_copyDataFuncWriter.writeCustom(_code.Super + ".copyData();");
			
			//TODO:C#语法支持
		}
	}
	
	/** 构造核心方法(6项) */
	private void toMakeMethods()
	{
		_methodWriters=new CodeWriter[MakeMethodType.num];
		_methodWrited=new boolean[MakeMethodType.num];
		_needUnckeckeds=new boolean[MakeMethodType.num];
		_needArrayUnchecked=false;
		
		CodeWriter writer;
		
		//通过基类名判定
		boolean hasExtend=!_outputCls.extendsClsName.equals(ShineToolSetting.dataBaseName);
		
		boolean needSuper=!_dataOutputInfo.isOverrideSuper && hasExtend;
		
		if(_dataOutputInfo.needReadFull())
		{
			_methodWriters[MakeMethodType.readFull]=writer=_outputCls.createWriter();
			
			if(needSuper)
			{
				writer.writeCustom(_code.Super + ".toReadBytesFull(" + ShineToolSetting.bytesSteamVarName + ");");
				writer.writeEmptyLine();
			}
			
			writer.writeStartReadObj();
			writer.writeEmptyLine();
		}
		
		if(_dataOutputInfo.needWriteFull())
		{
			_methodWriters[MakeMethodType.writeFull]=writer=_outputCls.createWriter();
			
			if(needSuper)
			{
				writer.writeCustom(_code.Super + ".toWriteBytesFull(" + ShineToolSetting.bytesSteamVarName + ");");
				writer.writeEmptyLine();
			}
			
			writer.writeGetWriteBytes();
			writer.writeEmptyLine();
		}
		
		if(_dataOutputInfo.needReadSimple())
		{
			_methodWriters[MakeMethodType.readSimple]=writer=_outputCls.createWriter();
			
			if(needSuper)
			{
				writer.writeCustom(_code.Super + ".toReadBytesSimple(" + ShineToolSetting.bytesSteamVarName + ");");
				writer.writeEmptyLine();
			}
		}
		
		if(_dataOutputInfo.needWriteSimple())
		{
			_methodWriters[MakeMethodType.writeSimple]=writer=_outputCls.createWriter();
			
			if(needSuper)
			{
				writer.writeCustom(_code.Super + ".toWriteBytesSimple(" + ShineToolSetting.bytesSteamVarName + ");");
				writer.writeEmptyLine();
			}
		}
		
		if(_dataOutputInfo.needCopy())
		{
			_outputCls.addImport(ShineToolSetting.dataBaseQName);
			
			_methodWriters[MakeMethodType.copy]=writer=_outputCls.createWriter();
			
			if(needSuper)
			{
				writer.writeCustom(_code.Super + ".toCopy(data);");
				writer.writeEmptyLine();
			}
			
			//有需要的
			if(_inputCls.getFieldNameList().size()>0)
			{
				writer.writeCustom("if(!(" + _code.getVarTypeIs("data",_outputCls.clsName) + "))");
				writer.writeReturnVoid(1);
				writer.writeEmptyLine();
				writer.writeVarCreate("mData",_outputCls.clsName,_code.getVarTypeTrans("data",_outputCls.clsName));
			}
			
			writer.writeEmptyLine();
		}
		
		if(_dataOutputInfo.needShadowCopy())
		{
			_outputCls.addImport(ShineToolSetting.dataBaseQName);
			
			_methodWriters[MakeMethodType.shadowCopy]=writer=_outputCls.createWriter();
			
			if(needSuper)
			{
				writer.writeCustom(_code.Super + ".toShadowCopy(data);");
				writer.writeEmptyLine();
			}
			
			//有需要的
			if(_inputCls.getFieldNameList().size()>0)
			{
				writer.writeCustom("if(!(" + _code.getVarTypeIs("data",_outputCls.clsName) + "))");
				writer.writeReturnVoid(1);
				writer.writeEmptyLine();
				writer.writeVarCreate("mData",_outputCls.clsName,_code.getVarTypeTrans("data",_outputCls.clsName));
			}
			
			writer.writeEmptyLine();
		}
		
		if(_dataOutputInfo.needDataEquals())
		{
			_methodWriters[MakeMethodType.dataEquals]=writer=_outputCls.createWriter();
			
			if(needSuper)
			{
				writer.writeCustom("if(!"+_code.Super+".toDataEquals(data))");
				writer.writeReturnBoolean(1,false);
				writer.writeEmptyLine();
			}
			
			//有需要的
			if(_inputCls.getFieldNameList().size()>0)
			{
				writer.writeVarCreate("mData",_outputCls.clsName,_code.getVarTypeTrans("data",_outputCls.clsName));
			}
		}
		
		if(_dataOutputInfo.needToWriteDataString())
		{
			_methodWriters[MakeMethodType.toWriteDataString]=writer=_outputCls.createWriter();
			
			if(needSuper)
			{
				writer.writeCustom(_code.Super + ".toWriteDataString(writer);");
				writer.writeEmptyLine();
			}
		}
		
		if(_dataOutputInfo.needInitDefault())
		{
			_methodWriters[MakeMethodType.initDefault]=writer=_outputCls.createWriter();
			
			if(needSuper)
			{
				writer.writeCustom(_code.Super + ".initDefault();");
				writer.writeEmptyLine();
			}
		}
		
		if(_dataOutputInfo.needInitFields())
		{
			_methodWriters[MakeMethodType.initFields]=writer=_outputCls.createWriter();
			
			if(needSuper)
			{
				writer.writeCustom(_code.Super + ".initFields();");
				writer.writeEmptyLine();
			}
		}
		
		if(_dataOutputInfo.needClear())
		{
			_methodWriters[MakeMethodType.clear]=writer=_outputCls.createWriter();
			
			if(needSuper)
			{
				writer.writeCustom(_code.Super + ".toClear();");
				writer.writeEmptyLine();
			}
		}
		
		if(_dataOutputInfo.needRelease())
		{
			_methodWriters[MakeMethodType.release]=writer=_outputCls.createWriter();
			
			if(needSuper)
			{
				writer.writeCustom(_code.Super + ".toRelease(pool);");
				writer.writeEmptyLine();
			}
		}
		
		//构造赋值 且 有继承
		if(_dataOutputInfo.isCreateSetParams)
		{
			_createMethod=new MethodInfo();
			
			_createWriter=_outputCls.createWriter();
			_outputCls.addImport(ShineToolSetting.bytesControlQName);
			
			if(_dataOutputInfo.createSetUsePool)
			{
				_createWriter.writeVarCreate("re",_outputCls.clsName,_code.getVarTypeTrans("BytesControl.createRequest(dataID)",_outputCls.clsName));
			}
			else
			{
				_createWriter.writeVarCreate("re",_outputCls.clsName,_outputCls.getCode().createNewObject(_outputCls.clsName));
			}
			
			if(hasExtend)
			{
				doOneExtendConstructSetParams(_inputCls.getExtendClsQName());
			}
		}
	}
	
	@Override
	protected void toMakeAfter()
	{
		if(_dataOutputInfo.needReadFull())
		{
			_methodWriters[MakeMethodType.readFull].writeDisReadBytes();
		}
		
		if(_dataOutputInfo.needWriteFull())
		{
			_methodWriters[MakeMethodType.writeFull].writeDisWriteBytes();
		}
		
		for(int i=0;i<MakeMethodType.num;++i)
		{
			if(_dataOutputInfo.needs[i])
			{
				if(i==MakeMethodType.dataEquals)
				{
					_methodWriters[i].writeReturnBoolean(true);
				}
				
				_methodWriters[i].writeEnd();
			}
		}
		
		//构造写值
		if(_dataOutputInfo.isCreateSetParams)
		{
			_copyDataFuncWriter.writeEnd();
		}
		
		MethodInfo method;
		
		if(_dataOutputInfo.isCreateSetParams)//&& group==0
		{
			//if(_dataOutputInfo.isServerOrRobot())
			//{
			//
			//}
			//else
			//{
			//	_outputCls.removeMethodByName("copyData");
			//}
			
			//copyData
			method=new MethodInfo();
			method.name="copyData";
			method.visitType=VisitType.Protected;
			method.isStatic=false;
			method.returnType=_code.Void;
			method.content=_copyDataFuncWriter.toString();
			method.isOverride=true;
			
			_outputCls.addMethod(method);
		}
		
		if(_dataOutputInfo.needToWriteDataString())
		{
			method=new MethodInfo();
			method.visitType=VisitType.Public;
			method.isStatic=false;
			method.name="getDataClassName";
			method.describe="获取数据类名";
			method.returnType=_code.String;
			CodeWriter writer=_outputCls.createWriter();
			writer.writeCustom("return \""+_outputCls.clsName+"\";");
			writer.writeEnd();
			method.content=writer.toString();
			method.isOverride=true;
			_outputCls.addMethod(method);
		}
		
		for(int i=0;i<MakeMethodType.num;++i)
		{
			method=new MethodInfo();
			method.visitType=VisitType.Protected;
			method.isStatic=false;
			method.returnType=_code.Void;
			method.isOverride=true;
			
			switch(i)
			{
				case MakeMethodType.readFull:
				{
					method.name="toReadBytesFull";
					method.describe="读取字节流(完整版)";
					method.args.add(new MethodArgInfo(ShineToolSetting.bytesSteamVarName,"BytesReadStream"));
				}
				break;
				case MakeMethodType.writeFull:
				{
					method.name="toWriteBytesFull";
					method.describe="写入字节流(完整版)";
					method.args.add(new MethodArgInfo(ShineToolSetting.bytesSteamVarName,"BytesWriteStream"));
				}
				break;
				case MakeMethodType.readSimple:
				{
					method.name="toReadBytesSimple";
					method.describe="读取字节流(简版)";
					method.args.add(new MethodArgInfo(ShineToolSetting.bytesSteamVarName,"BytesReadStream"));
				}
				break;
				case MakeMethodType.writeSimple:
				{
					method.name="toWriteBytesSimple";
					method.describe="写入字节流(简版)";
					;
					method.args.add(new MethodArgInfo(ShineToolSetting.bytesSteamVarName,"BytesWriteStream"));
				}
				break;
				case MakeMethodType.copy:
				{
					method.name="toCopy";
					method.describe="复制(深拷贝)";
					method.args.add(new MethodArgInfo("data",ShineToolSetting.dataBaseName));
				}
				break;
				case MakeMethodType.shadowCopy:
				{
					method.name="toShadowCopy";
					method.describe="复制(潜拷贝)";
					method.args.add(new MethodArgInfo("data",ShineToolSetting.dataBaseName));
				}
				break;
				case MakeMethodType.dataEquals:
				{
					method.name="toDataEquals";
					method.describe="是否数据一致";
					method.args.add(new MethodArgInfo("data",ShineToolSetting.dataBaseName));
					method.returnType=_code.Boolean;
				}
				break;
				case MakeMethodType.toWriteDataString:
				{
					//_outputCls.removeImport(ShineToolSetting.dataWriterQName);
					
					method.name="toWriteDataString";
					method.describe="转文本输出";
					method.args.add(new MethodArgInfo("writer",ShineToolSetting.dataWriterName));
				}
				break;
				case MakeMethodType.initDefault:
				{
					method.name="initDefault";
					method.describe="初始化初值";
					//改public
					method.visitType=VisitType.Public;
				}
				break;
				case MakeMethodType.initFields:
				{
					method.name="initFields";
					method.describe="初始化属性组";
					//改public
					method.visitType=VisitType.Public;
				}
					break;
				case MakeMethodType.clear:
				{
					method.name="toClear";
					method.describe="清空";
					//method.args.add(new MethodArgInfo("isAllClear",_code.Boolean));
				}
					break;
				case MakeMethodType.release:
				{
					method.name="toRelease";
					method.describe="回池";
					method.args.add(new MethodArgInfo("pool",ShineToolSetting.dataPoolName));
				}
					break;
			}
			
			if(_dataOutputInfo.needs[i])
			{
				method.content=_methodWriters[i].toString();
				
				if(_needUnckeckeds[i])
				{
					method.addAnnotation(AnnotationInfo.create("SuppressWarnings","\"unchecked\""));
				}
				
				if(_methodWrited[i] || MakeMethodType.mustWrite(i) || _customSerialize)
				{
					if(!_customSerialize || _outputCls.getMethodByName(method.name)==null)
					{
						if(i==MakeMethodType.toWriteDataString)
						{
							_outputCls.addImport(ShineToolSetting.dataWriterQName);
						}
						
						if(i==MakeMethodType.release)
						{
							_outputCls.addImport(ShineToolSetting.dataPoolQName);
						}
						
						method.content=_methodWriters[i].toString();
						_outputCls.addMethod(method);
					}
				}
				else
				{
					//移除
					_outputCls.removeMethod(method);
				}
			}
			else
			{
				//移除
				_outputCls.removeMethod(method);
			}
		}
		
		//构造写值
		if(_dataOutputInfo.isCreateSetParams)
		{
			_copyDataFuncWriter.writeEnd();
			
			_createMethod.visitType=VisitType.Public;
			
			_createMethod.name="create";
			if(_outputCls.getCodeType()==CodeType.TS)
			{
				_createMethod.name+=getCName(_inputCls.clsName);
			}
			
			_createMethod.returnType=_outputCls.clsName;
			_createMethod.isStatic=true;
			_createMethod.describe="创建实例";
			
			_createWriter.writeCustom("return re;");
			_createWriter.writeEnd();
			
			_createMethod.content=_createWriter.toString();
			
			//移除一次
			_outputCls.removeMethodByName(_createMethod.name);
			_outputCls.addMethod(_createMethod);
		}
		
		//客户端
		if(_isHotfix && _outputInfo.group==DataGroupType.Client)
		{
			//需要继承的才生成适配器
			if(isClassNeedExtends(_inputCls.getQName()))
			{
				ILClassInfo cls=new ILClassInfo(_outputCls.clsName);
				
				//数据
				if(_mark.equals("DO"))
				{
					cls.addMethod("toReadBytesFull",null,VisitType.Protected,true,"BytesReadStream","stream");
					cls.addMethod("toWriteBytesFull",null,VisitType.Protected,true,"BytesWriteStream","stream");
					cls.addMethod("toReadBytesSimple",null,VisitType.Protected,true,"BytesReadStream","stream");
					cls.addMethod("toWriteBytesSimple",null,VisitType.Protected,true,"BytesWriteStream","stream");
					cls.addMethod("toCopy",null,VisitType.Protected,true,"BaseData","data");
					cls.addMethod("toShadowCopy",null,VisitType.Protected,true,"BaseData","data");
					cls.addMethod("toDataEquals","bool",VisitType.Protected,true,"BaseData","data");
					cls.addMethod("getDataClassName","string",VisitType.Public,true);
					cls.addMethod("toWriteDataString",null,VisitType.Protected,true,"DataWriter","writer");
					cls.addMethod("initDefault",null,VisitType.Public,true);
					cls.addMethod("beforeWrite",null,VisitType.Protected,true);
					cls.addMethod("afterRead",null,VisitType.Protected,true);
					
					ILRuntimeAdapterTool.makeILCls(cls,_outputCls);
				}
				else if(_mark.equals("MO"))
				{
					DataOutputInfo info=(DataOutputInfo)_outputInfo;
					//读->response
					if(info.needReadSimple())
					{
						cls.addMethod("toReadBytesSimple",null,VisitType.Protected,true,"BytesReadStream","stream");
						cls.addMethod("getDataClassName","string",VisitType.Public,true);
						cls.addMethod("toWriteDataString",null,VisitType.Protected,true,"DataWriter","writer");
						cls.addMethod("execute",null,VisitType.Protected,false);
					}
					//写->request
					else if(info.needWriteSimple())
					{
						cls.addMethod("toWriteBytesSimple",null,VisitType.Protected,true,"BytesWriteStream","stream");
						cls.addMethod("getDataClassName","string",VisitType.Public,true);
						cls.addMethod("toWriteDataString",null,VisitType.Protected,true,"DataWriter","writer");
						cls.addMethod("copyData",null,VisitType.Protected,true);
					}
				}
				
				ILRuntimeAdapterTool.doOneClsOnly(cls,_adapterPath,false);
			}
		}
		
		//if(_outputInfo.codeType==CodeType.TS)
		//{
		//	_outputCls.removeMethodByName("toReadBytesFull");
		//	_outputCls.removeMethodByName("toWriteBytesFull");
		//	_outputCls.removeMethodByName("toDataEquals");
		//	_outputCls.removeMethodByName("toWriteDataString");
		//}
	}
	
	/** 执行一个基类构造赋值 */
	private void doOneExtendConstructSetParams(String qName)
	{
		if(qName.isEmpty())
			return;
		
		ClassInfo cls=getInputClsAbs(qName);
		
		if(cls==null)
		{
			Ctrl.print("未找到继承类",qName);
			return;
		}
		
		if(!cls.extendsClsName.isEmpty())
		{
			//递归
			doOneExtendConstructSetParams(cls.getExtendClsQName());
		}
		
		for(String k : cls.getFieldNameList())
		{
			toMakeSuperField(cls.getField(k),cls);
		}
	}
	
	//---核心构造--//
	
	/** 构造superField */
	private void toMakeSuperField(FieldInfo field,ClassInfo cls)
	{
		String fType=getVarType(field.type,false,false,cls);
		
		if(_dataOutputInfo.isCreateSetParams)
		{
			_createMethod.args.add(new MethodArgInfo(field.name,fType));
			
			String superName=_dataOutputInfo.defaultVarVisitType==VisitType.Public ? field.name : "_"+field.name;
			
			_createWriter.writeVarSet("re."+superName,field.name);
		}
	}
	
	@Override
	protected void toMakeOneField(FieldInfo field,FieldInfo outField)
	{
		String fType=getVarType(field.type,false,false);
		
		if(_dataOutputInfo.isCreateSetParams)
		{
			_createMethod.args.add(new MethodArgInfo(field.name,fType));
			_createWriter.writeVarSet("re."+outField.name,field.name);
			
			//非基础类型
			if(!isBaseType(field.type))
			{
				//先预热
				_copyDataFuncWriter.writeVarCreate(field.name + "Temp",fType,_outputCls.getFieldWrap(outField.name));
				//再深拷
				makeFunctionCode(_copyDataFuncWriter,MakeMethodType.copySelf,_outputCls.getFieldWrap(outField.name),field.type,field.name + "Temp",field);
				
				if(_needArrayUnchecked)
				{
					_needUnckeckeds[MakeMethodType.copySelf]=true;
					_needArrayUnchecked=false;
				}
			}
		}
		
		for(int i=0;i<MakeMethodType.num;++i)
		{
			if(_dataOutputInfo.needs[i])
			{
				//不是自定义序列化
				if(!_customSerialize)
				{
					String outFieldName=MakeMethodType.useMData(i) ? "mData." + outField.name : _outputCls.getFieldWrap(outField.name);
					makeFunctionCode(_methodWriters[i],i,_outputCls.getFieldWrap(outField.name),field.type,outFieldName,field);
				}
				
				_methodWrited[i]=true;
				
				if(_needArrayUnchecked)
				{
					_needUnckeckeds[i]=true;
					_needArrayUnchecked=false;
				}
			}
		}
	}
	
	//--构造核心--//
	
	/** 获取包装前的属性名 */
	private String getSimpleFieldName(String name)
	{
		if(name.charAt(0)=='_')
		{
			return name.substring(1,name.length());
		}
		else if(name.startsWith("this."))
		{
			String temp=name.substring(5,name.length());
			
			if(temp.charAt(0)=='_')
			{
				return temp.substring(1,temp.length());
			}
			
			return temp;
		}
		else
		{
			return name;
		}
	}
	
	/** 获取包装前的属性名 */
	private String getFieldNameWithOutUnderline(String name)
	{
		if(name.charAt(0)=='_')
		{
			return name.substring(1,name.length());
		}
		
		return name;
	}
	
	/** 获取包装前的属性名 */
	private String getFieldNameWithOutThis(String name)
	{
		if(name.startsWith("this."))
		{
			return name.substring(5,name.length());
		}
		
		return name;
	}
	
	protected void makeFunctionCode(CodeWriter writer,int method,String name,String type,String mName,FieldInfo sourceField)
	{
		_needArrayUnchecked=false;
		makeFunctionCode(writer,method,name,type,mName,sourceField,false);
	}
	
	/** method:MakeMethodType */
	protected void makeFunctionCode(CodeWriter writer,int method,String name,String type,String mName,FieldInfo sourceField,boolean isChildCollection)
	{
		if(mName==null)
		{
			mName=name;
		}
		
		String wtName=getFieldNameWithOutThis(name);
		String nName=getFieldNameWithOutUnderline(wtName);
		
		String typeT;
		
		String typeK;
		String typeV;
		String typeKG;
		String typeVG;
		
		String nameLen;
		String nameI;
		String nameU;
		String nameV;
		String nameK;
		
		String nameT;
		String nameR;
		
		//TS或者非_开头的，加this
		String cLeftName=isChildCollection ? name : ((_outputCls.getCodeType()==CodeType.TS || name.charAt(0)!='_') ? "this."+wtName : wtName);
		
		String cRightName=mName;
		
		//左右局部变量名
		
		String intType=_code.getVarStr(VarType.Int);
		
		boolean needRelease=false;
		//析构
		if(method==MakeMethodType.release)
		{
			if(_dataOutputInfo.isRequest && !_messageDontCopy)
				needRelease=true;
			
			if(sourceField.needRelease())
				needRelease=true;
		}
		
		if(method==MakeMethodType.shadowCopy)
		{
			writer.writeVarSet(cLeftName,cRightName);
		}
		else
		{
			int tt=_inCode.getVarType(type);
			
			if(method==MakeMethodType.toWriteDataString)
			{
				if(!isChildCollection)
				{
					writer.writeWriterTabs();
					writer.writeWriterAppend("\""+nName+"\"");
					writer.writeWriterAppend("':'");
				}
			}
			
			//基础类型
			if(VarType.isBaseType(tt))
			{
				switch(method)
				{
					case MakeMethodType.readFull:
					case MakeMethodType.readSimple:
					{
						writer.writeReadBaseType(tt,cLeftName);
					}
						break;
					case MakeMethodType.writeFull:
					case MakeMethodType.writeSimple:
					{
						writer.writeWriteBaseType(tt,cLeftName);
					}
						break;
					case MakeMethodType.copy:
					case MakeMethodType.copySelf:
					{
						writer.writeCopyBaseType(tt,cLeftName,cRightName);
					}
						break;
					case MakeMethodType.dataEquals:
					{
						if(tt==VarType.String && _code.getCodeType()==CodeType.Java)
						{
							writer.writeCustom("if(!"+cLeftName+".equals("+cRightName+"))");
						}
						else
						{
							writer.writeCustom("if("+cLeftName+"!="+cRightName+")");
						}
						
						writer.writeReturnBoolean(1,false);
					}
						break;
					case MakeMethodType.toWriteDataString:
					{
						writer.writeWriterAppend(cLeftName);
					}
						break;
					case MakeMethodType.initDefault:
					case MakeMethodType.initFields:
					{
					
					}
						break;
					case MakeMethodType.clear:
					case MakeMethodType.release:
					{
						writer.writeVarSet(cLeftName,_code.getBaseTypeDefaultValue(tt));
					}
						break;
				}
			}
			else
			{
				nameU=nName + "U";
				nameV=nName + "V";
				nameLen=nName + "Len";
				nameI=nName + "I";
				
				nameT=nName + "T";
				nameR=nName + "R";
				
				switch(tt)
				{
					case VarType.Array:
					{
						//输入元素类型
						typeT=CodeInfo.getVarArrayType(type);
						//元素输出类型
						typeV=getVarType(typeT);
						
						//byte[]的补
						if(typeT.equals(_code.Byte))
						{
							typeV=typeT;
						}
						
						_outputCls.addImport(_code.getArrayTypeImport(typeV,false));
						
						switch(method)
						{
							case MakeMethodType.readFull:
							case MakeMethodType.readSimple:
							{
								if(sourceField.maybeNull)
								{
									writer.writeCustom("if(" + _code.getReadBaseVarType(VarType.Boolean) + ")");
									writer.writeLeftBrace();
								}
								
								writer.writeVarCreate(nameLen,intType,_code.getReadLen());
								
								_outputCls.addImport(_code.getArrayTypeImport(typeV,true));
								
								//字节数组
								if(typeT.equals(_code.Byte))
								{
									writer.writeVarSet(cLeftName,ShineToolSetting.bytesSteamVarName+".readByteArr("+nameLen+")");
								}
								else
								{
									if(ShineToolSetting.isReadAllNew || isChildCollection)
									{
										writer.createArray(cLeftName,typeV,nameLen);
									}
									else
									{
										writer.clearOrCreateArray(cLeftName,typeV,nameLen);
									}
									
									writer.writeVarCreate(nameT,_code.getArrayType(typeV,false),cLeftName);
									
									writer.writeForLoopTimes(nameI,nameLen);
									
									writer.writeVarCreate(nameV,typeV);
									
									if(_outputInfo.codeType==CodeType.Java && typeV.endsWith(">"))
									{
										_needArrayUnchecked=true;
									}
									
									makeFunctionCode(writer,method,nameV,typeT,null,sourceField,true);
									
									writer.writeArraySet(nameT,nameI,nameV);
									writer.writeRightBrace();
								}
								
								if(sourceField.maybeNull)
								{
									writer.writeElseAndDoubleBrace();
									writer.writeVarSet(cLeftName,_code.Null);
									writer.writeRightBrace();
								}
							}
								break;
							case MakeMethodType.writeFull:
							case MakeMethodType.writeSimple:
							{
								writer.writeCustom("if(" + cLeftName + "!=" + _code.Null + ")");
								writer.writeLeftBrace();
								
								if(sourceField.maybeNull)
									writer.writeWriteBaseType(VarType.Boolean,_code.True);
								
								//字节数组
								if(typeT.equals(_code.Byte))
								{
									writer.writeBytesLen(_code.getArrayLength(cLeftName));
									writer.writeCustom(ShineToolSetting.bytesSteamVarName+".writeByteArr("+cLeftName+");");
								}
								else
								{
									writer.writeVarCreate(nameT,_code.getArrayType(typeV,false),cLeftName);
									writer.writeBytesLen(_code.getArrayLength(nameT));
									writer.writeForEachArray(nameT,nameV,typeV);
									
									makeFunctionCode(writer,method,nameV,typeT,null,sourceField,true);
									
									writer.writeForEachArrayEnd(typeV);
								}
								
								writer.writeElseAndDoubleBrace();
								
								if(sourceField.maybeNull)
									writer.writeWriteBaseType(VarType.Boolean,_code.False);
								else
									writer.writeNullObjError(nName);
								
								writer.writeRightBrace();
							}
								break;
							case MakeMethodType.copy:
							case MakeMethodType.copySelf:
							{
								writer.writeCustom("if(" + cRightName + "!=" + _code.Null + ")");
								writer.writeLeftBrace();
								
								//字节数组
								if(typeT.equals(_code.Byte))
								{
									_outputCls.addImport(ShineToolSetting.bytesControlQName);
									writer.writeVarSet(cLeftName,ShineToolSetting.bytesControlName+".byteArrCopy("+cRightName+")");
								}
								else
								{
									writer.writeVarCreate(nameR,_code.getArrayType(typeV,false),cRightName);
									writer.writeVarCreate(nameLen,intType,_code.getArrayLength(nameR));
									
									_outputCls.addImport(_code.getArrayTypeImport(typeV,true));
									
									if(method==MakeMethodType.copy)
									{
										if(ShineToolSetting.isReadAllNew || isChildCollection)
										{
											writer.createArray(cLeftName,typeV,nameLen);
										}
										else
										{
											writer.clearOrCreateArray(cLeftName,typeV,nameLen);
										}
									}
									else if(method==MakeMethodType.copySelf)
									{
										writer.createArray(cLeftName,typeV,nameLen);
									}
									
									//基础类型
									if(VarType.isBaseType(_inCode.getVarType(typeV)))
									{
										_outputCls.addImport(ShineToolSetting.bytesControlQName);
										writer.writeCustom(ShineToolSetting.bytesControlName+".arrayCopy("+cRightName+","+cLeftName+","+nameLen+");");
									}
									else
									{
										writer.writeVarCreate(nameT,_code.getArrayType(typeV,false),cLeftName);
										writer.writeForLoopTimes(nameI,nameLen);
										writer.writeVarCreate(nameV,typeV,_code.getArrayElement(nameR,nameI));
										
										writer.writeVarCreate(nameU,typeV);
										
										if(typeV.endsWith(">"))
										{
											_needArrayUnchecked=true;
										}
										
										makeFunctionCode(writer,method,nameU,typeT,nameV,sourceField,true);
										
										writer.writeArraySet(nameT,nameI,nameU);
										writer.writeRightBrace();
									}
								}
								
								writer.writeElseAndDoubleBrace();
								writer.writeVarSet(cLeftName,_code.Null);
								
								if(!sourceField.maybeNull)
									writer.writeNullObjError(nName);
								
								writer.writeRightBrace();
							}
								break;
							case MakeMethodType.dataEquals:
							{
								writer.writeCustom("if(" + cRightName + "!=" + _code.Null + ")");
								writer.writeLeftBrace();
								
								writer.writeCustom("if(" + cLeftName + "==" + _code.Null + ")");
								writer.writeReturnBoolean(1,false);
								
								writer.writeCustom("if("+_code.getArrayLength(cLeftName)+"!="+_code.getArrayLength(cRightName)+")");
								writer.writeReturnBoolean(1,false);
								
								writer.writeVarCreate(nameT,_code.getArrayType(typeV,false),cLeftName);
								writer.writeVarCreate(nameR,_code.getArrayType(typeV,false),cRightName);
								writer.writeVarCreate(nameLen,intType,_code.getArrayLength(nameT));
								
								_outputCls.addImport(_code.getArrayTypeImport(typeV,true));
								
								writer.writeForLoopTimes(nameI,nameLen);
								writer.writeVarCreate(nameU,typeV,_code.getArrayElement(nameT,nameI));
								writer.writeVarCreate(nameV,typeV,_code.getArrayElement(nameR,nameI));
								
								if(typeV.endsWith(">"))
								{
									_needArrayUnchecked=true;
								}
								
								makeFunctionCode(writer,method,nameU,typeT,nameV,sourceField,true);
								
								writer.writeRightBrace();
								
								writer.writeElseAndDoubleBrace();
								
								writer.writeCustom("if(" + cLeftName + "!=" + _code.Null + ")");
								writer.writeReturnBoolean(1,false);
								writer.writeRightBrace();
								
							}
								break;
							case MakeMethodType.toWriteDataString:
							{
								writer.writeWriterAppend("\"Array<"+typeV+">\"");
								
								writer.writeCustom("if(" + cLeftName + "!=" + _code.Null + ")");
								writer.writeLeftBrace();
								
								writer.writeVarCreate(nameT,_code.getArrayType(typeV,false),cLeftName);
								writer.writeVarCreate(nameLen,intType,_code.getArrayLength(nameT));
								
								writer.writeWriterAppend("'('");
								writer.writeWriterAppend(nameLen);
								writer.writeWriterAppend("')'");
								writer.writeWriterEnter();
								writer.writeWriterLeftBrace();
								
								writer.writeForLoopTimes(nameI,nameLen);
								writer.writeVarCreate(nameV,typeV,_code.getArrayElement(nameT,nameI));
								
								writer.writeWriterTabs();
								writer.writeWriterAppend(nameI);
								writer.writeWriterAppend("':'");
								
								makeFunctionCode(writer,method,nameV,typeT,null,sourceField,true);
								
								writer.writeWriterEnter();
								writer.writeRightBrace();
								
								writer.writeWriterRightBrace();
								
								writer.writeElseAndDoubleBrace();
								
								writer.writeWriterAppend("\"=null\"");
								writer.writeRightBrace();
							}
								break;
							case MakeMethodType.initDefault:
							case MakeMethodType.initFields:
							{
								if(!sourceField.maybeNull)
								{
									writer.writeVarSet(cLeftName,_code.createNewArray(typeV,"0"));
								}
							}
								break;
							case MakeMethodType.clear:
							{
								//if(!_code.isBaseType(typeV))
								//{
								//	writer.writeIfAndLeftBrace(cLeftName+"!="+_code.Null);
								//	writer.writeVarCreate(nameT,_code.getArrayType(typeV,false),cLeftName);
								//	writer.writeVarCreate(nameLen,intType,_code.getArrayLength(nameT));
								//	writer.writeForLoopTimes(nameI,nameLen);
								//	writer.writeArraySet(nameT,nameI,_code.Null);
								//	writer.writeRightBrace();
								//	writer.writeRightBrace();
								//}
								
								writer.writeVarSet(cLeftName,_code.Null);
							}
								break;
							case MakeMethodType.release:
							{
								if(needRelease)
								{
									if(!_code.isBaseType(typeV))
									{
										writer.writeIfAndLeftBrace(cLeftName+"!="+_code.Null);
										writer.writeVarCreate(nameT,_code.getArrayType(typeV,false),cLeftName);
										writer.writeForEachArray(nameT,nameV,typeV);
										
										makeFunctionCode(writer,method,nameV,typeT,null,sourceField,true);
										
										writer.writeArraySet(nameT,nameV+"I",_code.Null);
										
										writer.writeForEachArrayEnd(typeV);
										writer.writeRightBrace();
									}
								}
								else
								{
									writer.writeVarSet(cLeftName,_code.Null);
								}
							}
								break;
						}
					}
						break;
					case VarType.List:
					{
						//输入元素类型
						typeT=CodeInfo.getVarCollectionOneType(type);
						//元素输出类型
						typeV=getVarType(typeT);
						//元素输出泛型类型
						typeVG=getVarType(typeT,true);
						
						_outputCls.addImport(_code.getListTypeImport(typeVG,false));
						
						switch(method)
						{
							case MakeMethodType.readFull:
							case MakeMethodType.readSimple:
							{
								if(sourceField.maybeNull)
								{
									writer.writeCustom("if(" + _code.getReadBaseVarType(VarType.Boolean) + ")");
									writer.writeLeftBrace();
								}
								
								writer.writeVarCreate(nameLen,intType,_code.getReadLen());
								
								_outputCls.addImport(_code.getListTypeImport(typeVG,true));
								
								if(ShineToolSetting.isReadAllNew || isChildCollection)
								{
									writer.createList(cLeftName,typeVG,nameLen);
								}
								else
								{
									writer.clearOrCreateList(cLeftName,typeVG,nameLen);
								}
								
								writer.writeVarCreate(nameT,_code.getListType(typeVG,false),cLeftName);
								writer.writeForLoopTimesBack(nameI,nameLen);
								
								writer.writeVarCreate(nameV,typeV);
								
								makeFunctionCode(writer,method,nameV,typeT,null,sourceField,true);
								
								writer.writeListPush(nameT,nameV);
								writer.writeRightBrace();
								
								if(sourceField.maybeNull)
								{
									writer.writeElseAndDoubleBrace();
									writer.writeVarSet(cLeftName,_code.Null);
									writer.writeRightBrace();
								}
							}
								break;
							case MakeMethodType.writeFull:
							case MakeMethodType.writeSimple:
							{
								writer.writeCustom("if(" + cLeftName + "!=" + _code.Null + ")");
								writer.writeLeftBrace();
								
								if(sourceField.maybeNull)
									writer.writeWriteBaseType(VarType.Boolean,_code.True);
								
								writer.writeBytesLen(_code.getListLength(cLeftName));
								writer.writeForEachList(cLeftName,nameV,typeV);
								
								makeFunctionCode(writer,method,nameV,typeT,null,sourceField,true);
								
								writer.writeForEachListEnd(typeV);
								
								writer.writeElseAndDoubleBrace();
								
								if(sourceField.maybeNull)
									writer.writeWriteBaseType(VarType.Boolean,_code.False);
								else
									writer.writeNullObjError(nName);
								
								writer.writeRightBrace();
							}
								break;
							case MakeMethodType.copy:
							case MakeMethodType.copySelf:
							{
								writer.writeCustom("if(" + cRightName + "!=" + _code.Null + ")");
								writer.writeLeftBrace();
								
								_outputCls.addImport(_code.getListTypeImport(typeVG,true));
								
								if(method==MakeMethodType.copy)
								{
									if(ShineToolSetting.isReadAllNew || isChildCollection)
									{
										writer.createList(cLeftName,typeVG,_code.getListLength(cRightName));
									}
									else
									{
										writer.clearOrCreateList(cLeftName,typeVG,_code.getListLength(cRightName));
									}
								}
								else if(method==MakeMethodType.copySelf)
								{
									writer.createList(cLeftName,typeVG,_code.getListLength(cRightName));
								}
								
								writer.writeVarCreate(nameT,_code.getListType(typeVG,false),cLeftName);
								writer.writeForEachList(cRightName,nameV,typeV);
								
								writer.writeVarCreate(nameU,typeV);
								
								makeFunctionCode(writer,method,nameU,typeT,nameV,sourceField,true);
								
								writer.writeListPush(nameT,nameU);
								
								writer.writeForEachListEnd(typeV);
								
								writer.writeElseAndDoubleBrace();
								writer.writeVarSet(cLeftName,_code.Null);
								
								if(!sourceField.maybeNull)
									writer.writeNullObjError(nName);
								
								writer.writeRightBrace();
								
							}
								break;
							case MakeMethodType.dataEquals:
							{
								writer.writeCustom("if(" + cRightName + "!=" + _code.Null + ")");
								writer.writeLeftBrace();
								
								writer.writeCustom("if(" + cLeftName + "==" + _code.Null + ")");
								writer.writeReturnBoolean(1,false);
								
								writer.writeCustom("if("+_code.getListLength(cLeftName)+"!="+_code.getListLength(cRightName)+")");
								writer.writeReturnBoolean(1,false);
								
								writer.writeVarCreate(nameT,_code.getListType(typeV,false),cLeftName);
								writer.writeVarCreate(nameR,_code.getListType(typeV,false),cRightName);
								writer.writeVarCreate(nameLen,intType,_code.getListLength(nameT));
								
								_outputCls.addImport(_code.getListTypeImport(typeV,true));
								
								writer.writeForLoopTimes(nameI,nameLen);
								
								writer.writeVarCreate(nameU,typeV,_code.getListElement(nameT,nameI));
								writer.writeVarCreate(nameV,typeV,_code.getListElement(nameR,nameI));
								
								makeFunctionCode(writer,method,nameU,typeT,nameV,sourceField,true);
								
								writer.writeRightBrace();
								
								writer.writeElseAndDoubleBrace();
								writer.writeCustom("if(" + cLeftName + "!=" + _code.Null + ")");
								writer.writeReturnBoolean(1,false);
								writer.writeRightBrace();
								
							}
								break;
							case MakeMethodType.toWriteDataString:
							{
								writer.writeWriterAppend("\"List<"+typeV+">\"");
								writer.writeCustom("if(" + cLeftName + "!=" + _code.Null + ")");
								writer.writeLeftBrace();
								
								writer.writeVarCreate(nameT,_code.getListType(typeV,false),cLeftName);
								writer.writeVarCreate(nameLen,intType,_code.getListLength(nameT));
								
								writer.writeWriterAppend("'('");
								writer.writeWriterAppend(nameLen);
								writer.writeWriterAppend("')'");
								writer.writeWriterEnter();
								writer.writeWriterLeftBrace();
								
								writer.writeForLoopTimes(nameI,nameLen);
								writer.writeVarCreate(nameV,typeV,_code.getListElement(nameT,nameI));
								
								writer.writeWriterTabs();
								writer.writeWriterAppend(nameI);
								writer.writeWriterAppend("':'");
								
								makeFunctionCode(writer,method,nameV,typeT,null,sourceField,true);
								
								writer.writeWriterEnter();
								
								writer.writeRightBrace();
								
								writer.writeWriterRightBrace();
								
								writer.writeElseAndDoubleBrace();
								
								writer.writeWriterAppend("\"=null\"");
								writer.writeRightBrace();
							}
								break;
							case MakeMethodType.initDefault:
							case MakeMethodType.initFields:
							{
								if(!sourceField.maybeNull)
								{
									writer.createList(cLeftName,typeVG,null);
								}
							}
								break;
							case MakeMethodType.clear:
							{
								//writer.writeIf(cLeftName+"!="+_code.Null);
								//writer.writeCustomWithOff(1,cLeftName+".clear();");
								writer.writeVarSet(cLeftName,_code.Null);
							}
								break;
							case MakeMethodType.release:
							{
								if(needRelease)
								{
									writer.writeIfAndLeftBrace(cLeftName+"!="+_code.Null);
									
									if(!_code.isBaseType(typeV))
									{
										writer.writeForEachList(cLeftName,nameV,typeV);
										
										makeFunctionCode(writer,method,nameV,typeT,null,sourceField,true);
										
										if(_code.useSCollection())
											writer.writeCustom(nameV+"Values["+nameV+"I]="+_code.Null+";");
										
										writer.writeForEachListEnd(typeV);
										
										if(_code.useSCollection())
											writer.writeCustom(cLeftName+".justClearSize();");
										else
											writer.writeCustom(cLeftName+".clear();");
									}
									else
									{
										writer.writeCustom(cLeftName+".clear();");
									}
									
									writer.writeRightBrace();
								}
								else
								{
									writer.writeVarSet(cLeftName,_code.Null);
								}
								
							}
								break;
						}
						
					}
						break;
					case VarType.Set:
					{
						//输入元素类型
						typeT=CodeInfo.getVarCollectionOneType(type);
						//元素输出类型
						typeV=getVarType(typeT);
						//元素输出泛型类型
						typeVG=getVarType(typeT,true);
						
						_outputCls.addImport(_code.getSetTypeImport(typeVG,false));
						
						switch(method)
						{
							case MakeMethodType.readFull:
							case MakeMethodType.readSimple:
							{
								if(sourceField.maybeNull)
								{
									writer.writeCustom("if(" + _code.getReadBaseVarType(VarType.Boolean) + ")");
									writer.writeLeftBrace();
								}
								
								writer.writeVarCreate(nameLen,intType,_code.getReadLen());
								
								_outputCls.addImport(_code.getSetTypeImport(typeVG,true));
								
								if(ShineToolSetting.isReadAllNew || isChildCollection)
								{
									writer.createSet(cLeftName,typeVG,nameLen);
								}
								else
								{
									writer.clearOrCreateSet(cLeftName,typeVG,nameLen);
								}
								
								writer.writeVarCreate(nameT,_code.getSetType(typeVG,false),cLeftName);
								writer.writeForLoopTimesBack(nameI,nameLen);
								writer.writeVarCreate(nameV,typeV);
								
								makeFunctionCode(writer,method,nameV,typeT,null,sourceField,true);
								
								writer.writeSetAdd(nameT,nameV);
								writer.writeRightBrace();
								
								if(sourceField.maybeNull)
								{
									writer.writeElseAndDoubleBrace();
									writer.writeVarSet(cLeftName,_code.Null);
									writer.writeRightBrace();
								}
							}
							break;
							case MakeMethodType.writeFull:
							case MakeMethodType.writeSimple:
							{
								writer.writeCustom("if(" + cLeftName + "!=" + _code.Null + ")");
								writer.writeLeftBrace();
								
								if(sourceField.maybeNull)
									writer.writeWriteBaseType(VarType.Boolean,_code.True);
								
								writer.writeBytesLen(_code.getSetLength(cLeftName));
								writer.writeForEachSet(cLeftName,nameV,typeV);
								
								makeFunctionCode(writer,method,nameV,typeT,null,sourceField,true);
								
								writer.writeForEachSetEnd(typeV);
								
								writer.writeElseAndDoubleBrace();
								
								if(sourceField.maybeNull)
									writer.writeWriteBaseType(VarType.Boolean,_code.False);
								else
									writer.writeNullObjError(nName);
								
								writer.writeRightBrace();
								
								
							}
							break;
							case MakeMethodType.copy:
							case MakeMethodType.copySelf:
							{
								writer.writeCustom("if(" + cRightName + "!=" + _code.Null + ")");
								writer.writeLeftBrace();
								
								_outputCls.addImport(_code.getSetTypeImport(typeVG,true));
								
								if(method==MakeMethodType.copy)
								{
									if(ShineToolSetting.isReadAllNew || isChildCollection)
									{
										writer.createSet(cLeftName,typeVG,_code.getSetLength(cRightName));
									}
									else
									{
										writer.clearOrCreateSet(cLeftName,typeVG,_code.getSetLength(cRightName));
									}
								}
								else if(method==MakeMethodType.copySelf)
								{
									writer.createSet(cLeftName,typeVG,_code.getSetLength(cRightName));
								}
								
								writer.writeVarCreate(nameT,_code.getSetType(typeVG,false),cLeftName);
								writer.writeForEachSet(cRightName,nameV,typeV);
								
								writer.writeVarCreate(nameU,typeV);
								
								makeFunctionCode(writer,method,nameU,typeT,nameV,sourceField,true);
								
								writer.writeSetAdd(nameT,nameU);
								
								writer.writeForEachSetEnd(typeV);
								
								writer.writeElseAndDoubleBrace();
								writer.writeVarSet(cLeftName,_code.Null);
								
								if(!sourceField.maybeNull)
									writer.writeNullObjError(nName);
								
								writer.writeRightBrace();
							}
								break;
							case MakeMethodType.dataEquals:
							{
								writer.writeCustom("if(" + cRightName + "!=" + _code.Null + ")");
								writer.writeLeftBrace();
								
								writer.writeCustom("if(" + cLeftName + "==" + _code.Null + ")");
								writer.writeReturnBoolean(1,false);
								
								writer.writeCustom("if("+_code.getSetLength(cLeftName)+"!="+_code.getSetLength(cRightName)+")");
								writer.writeReturnBoolean(1,false);
								
								_outputCls.addImport(_code.getSetTypeImport(typeV,true));
								
								writer.writeVarCreate(nameR,_code.getSetType(typeV,false),cRightName);
								
								writer.writeForEachSet(cLeftName,nameV,typeV);
								
								writer.writeCustom("if(!"+_code.getSetContains(nameR,nameV)+")");
								writer.writeReturnBoolean(1,false);
								
								writer.writeForEachSetEnd(typeV);
								
								writer.writeElseAndDoubleBrace();
								writer.writeCustom("if(" + cLeftName + "!=" + _code.Null + ")");
								writer.writeReturnBoolean(1,false);
								writer.writeRightBrace();
								
							}
								break;
							case MakeMethodType.toWriteDataString:
							{
								writer.writeWriterAppend("\"Set<"+typeV+">\"");
								writer.writeCustom("if(" + cLeftName + "!=" + _code.Null + ")");
								writer.writeLeftBrace();
								
								writer.writeWriterAppend("'('");
								writer.writeWriterAppend(_code.getSetLength(cLeftName));
								writer.writeWriterAppend("')'");
								writer.writeWriterEnter();
								writer.writeWriterLeftBrace();
								
								writer.writeForEachSet(cLeftName,nameV,typeV);
								
								writer.writeWriterTabs();
								makeFunctionCode(writer,method,nameV,typeT,null,sourceField,true);
								
								writer.writeWriterEnter();
								
								writer.writeForEachSetEnd(typeV);
								
								writer.writeWriterRightBrace();
								
								writer.writeElseAndDoubleBrace();
								
								writer.writeWriterAppend("\"=null\"");
								writer.writeRightBrace();
							}
								break;
							case MakeMethodType.initDefault:
							case MakeMethodType.initFields:
							{
								if(!sourceField.maybeNull)
								{
									writer.createSet(cLeftName,typeVG,null);
								}
							}
								break;
							case MakeMethodType.clear:
							{
								//writer.writeIf(cLeftName+"!="+_code.Null);
								//writer.writeCustomWithOff(1,cLeftName+".clear();");
								writer.writeVarSet(cLeftName,_code.Null);
							}
								break;
							case MakeMethodType.release:
							{
								if(needRelease)
								{
									writer.writeIfAndLeftBrace(cLeftName+"!="+_code.Null);
									
									if(!_code.isBaseType(typeV))
									{
										writer.writeForEachSet(cLeftName,nameV,typeV);
										
										makeFunctionCode(writer,method,nameV,typeT,null,sourceField,true);
										
										if(_code.useSCollection())
											writer.writeCustom(nameV+"Keys["+nameV+"I]="+_code.Null+";");
										
										writer.writeForEachSetEnd(typeV);
										
										if(_code.useSCollection())
											writer.writeCustom(cLeftName+".justClearSize();");
										else
											writer.writeCustom(cLeftName+".clear();");
									}
									else
									{
										writer.writeCustom(cLeftName+".clear();");
									}
									
									writer.writeRightBrace();
								}
								else
								{
									writer.writeVarSet(cLeftName,_code.Null);
								}
								
							}
								break;
						}
					}
						break;
					case VarType.Map:
					{
						nameK=nName + "K";
						
						CodeInfo.CollectionTwoType two=CodeInfo.getVarCollectionTwoType(type);
						//						//输入元素类型
						//						typeT=getVarCollectionOneType(type);
						
						//元素key输出类型
						typeK=getVarType(two.type0);
						//元素value输出类型
						typeV=getVarType(two.type1);
						
						//元素key输出类型G
						typeKG=getVarType(two.type0,true);
						//元素value输出类型G
						typeVG=getVarType(two.type1,true);
						
						_outputCls.addImport(_code.getMapTypeImport(typeKG,typeVG,false));
						
						switch(method)
						{
							case MakeMethodType.readFull:
							case MakeMethodType.readSimple:
							{
								if(sourceField.maybeNull)
								{
									writer.writeCustom("if(" + _code.getReadBaseVarType(VarType.Boolean) + ")");
									writer.writeLeftBrace();
								}
								
								writer.writeVarCreate(nameLen,intType,_code.getReadLen());
								
								_outputCls.addImport(_code.getMapTypeImport(typeKG,typeVG,true));
								
								if(ShineToolSetting.isReadAllNew || isChildCollection)
								{
									writer.createMap(cLeftName,typeKG,typeVG,nameLen);
								}
								else
								{
									writer.clearOrCreateMap(cLeftName,typeKG,typeVG,nameLen);
								}
								
								writer.writeVarCreate(nameT,_code.getMapType(typeKG,typeVG,false),cLeftName);
								writer.writeForLoopTimesBack(nameI,nameLen);
								
								if(sourceField.mapKeyInValueKey.isEmpty())
								{
									writer.writeVarCreate(nameK,typeK);
									writer.writeVarCreate(nameV,typeV);
									
									makeFunctionCode(writer,method,nameK,two.type0,null,sourceField,true);
									makeFunctionCode(writer,method,nameV,two.type1,null,sourceField,true);
									
									writer.writeMapAdd(nameT,nameK,nameV);
								}
								else
								{
									writer.writeVarCreate(nameV,typeV);
									
									makeFunctionCode(writer,method,nameV,two.type1,null,sourceField,true);
									
									writer.writeMapAdd(nameT,nameV + "." + sourceField.mapKeyInValueKey,nameV);
								}
								
								writer.writeRightBrace();
								
								if(sourceField.maybeNull)
								{
									writer.writeElseAndDoubleBrace();
									writer.writeVarSet(cLeftName,_code.Null);
									writer.writeRightBrace();
								}
							}
							break;
							case MakeMethodType.writeFull:
							case MakeMethodType.writeSimple:
							{
								writer.writeCustom("if(" + cLeftName + "!=" + _code.Null + ")");
								writer.writeLeftBrace();
								
								if(sourceField.maybeNull)
									writer.writeWriteBaseType(VarType.Boolean,_code.True);
								
								writer.writeBytesLen(_code.getMapLength(cLeftName));
								
								_outputCls.addImport(_code.getMapEntryTypeImport(typeKG,typeVG,false));
								
								if(sourceField.mapKeyInValueKey.isEmpty())
								{
									writer.writeForEachMapEntry(cLeftName,nameK,typeKG,nameV,typeVG);
									
									makeFunctionCode(writer,method,nameK,two.type0,null,sourceField,true);
									makeFunctionCode(writer,method,nameV,two.type1,null,sourceField,true);
								}
								else
								{
									writer.writeForEachMapValue(cLeftName,typeKG,nameV,typeVG);
									
									makeFunctionCode(writer,method,nameV,two.type1,null,sourceField,true);
								}
								
								writer.writeForEachMapEnd(typeKG,typeVG);
								
								writer.writeElseAndDoubleBrace();
								
								if(sourceField.maybeNull)
									writer.writeWriteBaseType(VarType.Boolean,_code.False);
								else
									writer.writeNullObjError(nName);
								
								writer.writeRightBrace();
							}
							break;
							case MakeMethodType.copy:
							case MakeMethodType.copySelf:
							{
								String nameW=nName + "W";
								
								writer.writeCustom("if(" + cRightName + "!=" + _code.Null + ")");
								writer.writeLeftBrace();
								
								_outputCls.addImport(_code.getMapTypeImport(typeKG,typeVG,true));
								
								if(method==MakeMethodType.copy)
								{
									if(ShineToolSetting.isReadAllNew || isChildCollection)
									{
										writer.createMap(cLeftName,typeKG,typeVG,_code.getMapLength(cRightName));
									}
									else
									{
										writer.clearOrCreateMap(cLeftName,typeKG,typeVG,_code.getMapLength(cRightName));
									}
								}
								else if(method==MakeMethodType.copySelf)
								{
									writer.createMap(cLeftName,typeKG,typeVG,_code.getMapLength(cRightName));
								}
								
								writer.writeVarCreate(nameT,_code.getMapType(typeKG,typeVG,false),cLeftName);
								
								_outputCls.addImport(_code.getMapEntryTypeImport(typeKG,typeVG,false));
								
								if(sourceField.mapKeyInValueKey.isEmpty())
								{
									writer.writeForEachMapEntry(cRightName,nameK,typeKG,nameV,typeVG);
									
									writer.writeVarCreate(nameW,typeK);
									writer.writeVarCreate(nameU,typeV);
									
									makeFunctionCode(writer,method,nameW,two.type0,nameK,sourceField,true);
									makeFunctionCode(writer,method,nameU,two.type1,nameV,sourceField,true);
									
									writer.writeMapAdd(nameT,nameW,nameU);
								}
								else
								{
									writer.writeForEachMapValue(cRightName,typeKG,nameV,typeVG);
									
									writer.writeVarCreate(nameU,typeV);
									
									makeFunctionCode(writer,method,nameU,two.type1,nameV,sourceField,true);
									
									writer.writeMapAdd(nameT,nameU + "." + sourceField.mapKeyInValueKey,nameU);
								}
								
								writer.writeForEachMapEnd(typeKG,typeVG);
								
								writer.writeElseAndDoubleBrace();
								writer.writeVarSet(cLeftName,_code.Null);
								if(!sourceField.maybeNull)
									writer.writeNullObjError(nName);
								writer.writeRightBrace();
							}
							break;
							case MakeMethodType.dataEquals:
							{
								writer.writeCustom("if(" + cRightName + "!=" + _code.Null + ")");
								writer.writeLeftBrace();
								
								writer.writeCustom("if(" + cLeftName + "==" + _code.Null + ")");
								writer.writeReturnBoolean(1,false);
								
								writer.writeCustom("if("+_code.getMapLength(cLeftName)+"!="+_code.getMapLength(cRightName)+")");
								writer.writeReturnBoolean(1,false);
								
								_outputCls.addImport(_code.getMapEntryTypeImport(typeKG,typeVG,false));
								
								writer.writeVarCreate(nameR,_code.getMapType(typeKG,typeVG,false),cRightName);
								
								writer.writeForEachMapEntry(cLeftName,nameK,typeKG,nameV,typeVG);
								
								writer.writeVarCreate(nameU,typeV,_code.getMapElement(nameR,nameK));
								
								makeFunctionCode(writer,method,nameV,two.type1,nameU,sourceField,true);
								
								writer.writeForEachMapEnd(typeKG,typeVG);
								
								writer.writeElseAndDoubleBrace();
								writer.writeCustom("if(" + cLeftName + "!=" + _code.Null + ")");
								writer.writeReturnBoolean(1,false);
								writer.writeRightBrace();
							}
								break;
							case MakeMethodType.toWriteDataString:
							{
								writer.writeWriterAppend("\"Map<"+typeK+","+typeV+">\"");
								writer.writeCustom("if(" + cLeftName + "!=" + _code.Null + ")");
								writer.writeLeftBrace();
								
								writer.writeWriterAppend("'('");
								writer.writeWriterAppend(_code.getMapLength(cLeftName));
								writer.writeWriterAppend("')'");
								writer.writeWriterEnter();
								writer.writeWriterLeftBrace();
								
								writer.writeForEachMapEntry(cLeftName,nameK,typeKG,nameV,typeVG);
								
								writer.writeWriterTabs();
								
								makeFunctionCode(writer,method,nameK,two.type0,null,sourceField,true);
								
								writer.writeWriterAppend("':'");
								
								makeFunctionCode(writer,method,nameV,two.type1,null,sourceField,true);
								
								writer.writeWriterEnter();
								
								writer.writeForEachMapEnd(typeKG,typeVG);
								
								writer.writeWriterRightBrace();
								
								writer.writeElseAndDoubleBrace();
								
								writer.writeWriterAppend("\"=null\"");
								writer.writeRightBrace();
							}
								break;
							case MakeMethodType.initDefault:
							case MakeMethodType.initFields:
							{
								if(!sourceField.maybeNull)
								{
									writer.createMap(cLeftName,typeKG,typeVG,null);
								}
							}
								break;
							case MakeMethodType.clear:
							{
								//writer.writeIf(cLeftName+"!="+_code.Null);
								//writer.writeCustomWithOff(1,cLeftName+".clear();");
								writer.writeVarSet(cLeftName,_code.Null);
							}
								break;
							case MakeMethodType.release:
							{
								if(needRelease)
								{
									writer.writeIfAndLeftBrace(cLeftName+"!="+_code.Null);
									
									if(!_code.isBaseType(typeV))
									{
										writer.writeForEachMapEntry(cLeftName,nameK,typeKG,nameV,typeVG);
										
										makeFunctionCode(writer,method,nameV,two.type1,null,sourceField,true);
										
										if(_code.useSCollection())
										{
											if(_code.getCodeType()==CodeType.Java && (_code.getVarType(typeK)!=VarType.Int && _code.getVarType(typeK)!=VarType.Long))
											{
												writer.writeCustom(nameK+"Table["+nameK+"I]="+_code.Null+";");
												writer.writeCustom(nameK+"Table["+nameK+"I+1]="+_code.Null+";");
											}
											else
											{
												if(_code.getVarType(typeK)==VarType.String)
													writer.writeCustom(nameK+"Keys["+nameK+"I]="+_code.Null+";");
												else
													writer.writeCustom(nameK+"Keys["+nameK+"I]="+nameK+"FreeValue;");
												
												writer.writeCustom(nameV+"Values["+nameK+"I]="+_code.Null+";");
											}
										}
										
										writer.writeForEachMapEnd(typeKG,typeVG);
										
										if(_code.useSCollection())
											writer.writeCustom(cLeftName+".justClearSize();");
										else
											writer.writeCustom(cLeftName+".clear();");
									}
									else
									{
										writer.writeCustom(cLeftName+".clear();");
									}
									
									writer.writeRightBrace();
								}
								else
								{
									writer.writeVarSet(cLeftName,_code.Null);
								}
								
							}
								break;
						}
					}
						break;
					case VarType.Queue:
					{
						//输入元素类型
						typeT=CodeInfo.getVarCollectionOneType(type);
						//元素输出类型
						typeV=getVarType(typeT);
						//元素输出泛型类型
						typeVG=getVarType(typeT,true);
						
						_outputCls.addImport(_code.getQueueTypeImport(typeVG,false));
						
						switch(method)
						{
							case MakeMethodType.readFull:
							case MakeMethodType.readSimple:
							{
								if(sourceField.maybeNull)
								{
									writer.writeCustom("if(" + _code.getReadBaseVarType(VarType.Boolean) + ")");
									writer.writeLeftBrace();
								}
								
								writer.writeVarCreate(nameLen,intType,_code.getReadLen());
								
								_outputCls.addImport(_code.getQueueTypeImport(typeVG,true));
								
								if(ShineToolSetting.isReadAllNew || isChildCollection)
								{
									writer.createQueue(cLeftName,typeVG,nameLen);
								}
								else
								{
									writer.clearOrCreateQueue(cLeftName,typeVG,nameLen);
								}
								
								writer.writeVarCreate(nameT,_code.getQueueType(typeVG,false),cLeftName);
								writer.writeForLoopTimesBack(nameI,nameLen);
								
								writer.writeVarCreate(nameV,typeV);
								
								makeFunctionCode(writer,method,nameV,typeT,null,sourceField,true);
								
								writer.writeQueueOffer(nameT,nameV);
								writer.writeRightBrace();
								
								if(sourceField.maybeNull)
								{
									writer.writeElseAndDoubleBrace();
									writer.writeVarSet(cLeftName,_code.Null);
									writer.writeRightBrace();
								}
							}
							break;
							case MakeMethodType.writeFull:
							case MakeMethodType.writeSimple:
							{
								writer.writeCustom("if(" + cLeftName + "!=" + _code.Null + ")");
								writer.writeLeftBrace();
								if(sourceField.maybeNull)
									writer.writeWriteBaseType(VarType.Boolean,_code.True);
								
								writer.writeBytesLen(_code.getQueueLength(cLeftName));
								writer.writeForEachQueue(cLeftName,nameV,typeV);
								
								makeFunctionCode(writer,method,nameV,typeT,null,sourceField,true);
								
								writer.writeForEachQueueEnd(typeV);
								
								writer.writeElseAndDoubleBrace();
								
								if(sourceField.maybeNull)
									writer.writeWriteBaseType(VarType.Boolean,_code.False);
								else
									writer.writeNullObjError(nName);
								
								writer.writeRightBrace();
								
							}
							break;
							case MakeMethodType.copy:
							case MakeMethodType.copySelf:
							{
								writer.writeCustom("if(" + cRightName + "!=" + _code.Null + ")");
								writer.writeLeftBrace();
								
								_outputCls.addImport(_code.getQueueTypeImport(typeVG,true));
								
								if(method==MakeMethodType.copy)
								{
									if(ShineToolSetting.isReadAllNew || isChildCollection)
									{
										writer.createQueue(cLeftName,typeVG,_code.getQueueLength(cRightName));
									}
									else
									{
										writer.clearOrCreateQueue(cLeftName,typeVG,_code.getQueueLength(cRightName));
									}
								}
								else if(method==MakeMethodType.copySelf)
								{
									writer.createQueue(cLeftName,typeVG,_code.getQueueLength(cRightName));
								}
								
								writer.writeVarCreate(nameT,_code.getQueueType(typeVG,false),cLeftName);
								writer.writeForEachQueue(cRightName,nameV,typeV);
								
								writer.writeVarCreate(nameU,typeV);
								
								makeFunctionCode(writer,method,nameU,typeT,nameV,sourceField,true);
								
								writer.writeQueueOffer(nameT,nameU);
								
								writer.writeForEachQueueEnd(typeV);
								
								writer.writeElseAndDoubleBrace();
								writer.writeVarSet(cLeftName,_code.Null);
								if(!sourceField.maybeNull)
									writer.writeNullObjError(nName);
								writer.writeRightBrace();
								
							}
								break;
							case MakeMethodType.dataEquals:
							{
								writer.writeCustom("if(" + cRightName + "!=" + _code.Null + ")");
								writer.writeLeftBrace();
								
								writer.writeCustom("if(" + cLeftName + "==" + _code.Null + ")");
								writer.writeReturnBoolean(1,false);
								
								writer.writeCustom("if("+_code.getQueueLength(cLeftName)+"!="+_code.getQueueLength(cRightName)+")");
								writer.writeReturnBoolean(1,false);
								
								writer.writeVarCreate(nameT,_code.getQueueType(typeV,false),cLeftName);
								writer.writeVarCreate(nameR,_code.getQueueType(typeV,false),cRightName);
								writer.writeVarCreate(nameLen,intType,_code.getQueueLength(nameT));
								
								_outputCls.addImport(_code.getQueueTypeImport(typeV,true));
								
								writer.writeForLoopTimes(nameI,nameLen);
								
								writer.writeVarCreate(nameU,typeV,_code.getQueueElement(nameT,nameI));
								writer.writeVarCreate(nameV,typeV,_code.getQueueElement(nameR,nameI));
								
								makeFunctionCode(writer,method,nameU,typeT,nameV,sourceField,true);
								
								writer.writeRightBrace();
								
								writer.writeElseAndDoubleBrace();
								writer.writeCustom("if(" + cLeftName + "!=" + _code.Null + ")");
								writer.writeReturnBoolean(1,false);
								writer.writeRightBrace();
								
							}
							break;
							case MakeMethodType.toWriteDataString:
							{
								writer.writeWriterAppend("\"Queue<"+typeV+">\"");
								writer.writeCustom("if(" + cLeftName + "!=" + _code.Null + ")");
								writer.writeLeftBrace();
								
								writer.writeVarCreate(nameT,_code.getQueueType(typeV,false),cLeftName);
								writer.writeVarCreate(nameLen,intType,_code.getQueueLength(nameT));
								
								writer.writeWriterAppend("'('");
								writer.writeWriterAppend(nameLen);
								writer.writeWriterAppend("')'");
								writer.writeWriterEnter();
								writer.writeWriterLeftBrace();
								
								writer.writeForLoopTimes(nameI,nameLen);
								writer.writeVarCreate(nameV,typeV,_code.getQueueElement(nameT,nameI));
								
								writer.writeWriterTabs();
								writer.writeWriterAppend(nameI);
								writer.writeWriterAppend("':'");
								
								makeFunctionCode(writer,method,nameV,typeT,null,sourceField,true);
								
								writer.writeWriterEnter();
								
								writer.writeRightBrace();
								
								writer.writeWriterRightBrace();
								
								writer.writeElseAndDoubleBrace();
								
								writer.writeWriterAppend("\"=null\"");
								writer.writeRightBrace();
							}
							break;
							case MakeMethodType.initDefault:
							case MakeMethodType.initFields:
							{
								if(!sourceField.maybeNull)
								{
									writer.createQueue(cLeftName,typeVG,null);
								}
							}
								break;
							case MakeMethodType.clear:
							{
								//writer.writeIf(cLeftName+"!="+_code.Null);
								//writer.writeCustomWithOff(1,cLeftName+".clear();");
								writer.writeVarSet(cLeftName,_code.Null);
							}
								break;
							case MakeMethodType.release:
							{
								if(needRelease)
								{
									writer.writeIfAndLeftBrace(cLeftName+"!="+_code.Null);
									
									if(!_code.isBaseType(typeV))
									{
										writer.writeForEachQueue(cLeftName,nameV,typeV);
										
										makeFunctionCode(writer,method,nameV,typeT,null,sourceField,true);
										
										writer.writeForEachQueueEnd(typeV);
										
										writer.writeCustom(cLeftName+".clear();");
									}
									else
									{
										writer.writeCustom(cLeftName+".clear();");
									}
									
									writer.writeRightBrace();
								}
								else
								{
									writer.writeVarSet(cLeftName,_code.Null);
								}
								
							}
								break;
						}
						
					}
						break;
					//自定义对象
					case VarType.CustomObject:
					{
						//自定义输出类型
						String inQName =_inputCls.getImport(type);
						
						boolean needExtends=isClassNeedExtends(inQName);
						
						typeT=getCustomObjectType(type,_inputCls);
						
						if(!typeT.isEmpty())
						{
							switch(method)
							{
								case MakeMethodType.readFull:
								case MakeMethodType.readSimple:
								{
									if(sourceField.maybeNull)
									{
										writer.writeCustom("if(" + _code.getReadBaseVarType(VarType.Boolean) + ")");
										writer.writeLeftBrace();
									}
									
									if(method==MakeMethodType.readFull)
									{
										if(needExtends)
										{
											writer.writeDataReadFullForExtends(cLeftName,nameT,typeT,!sourceField.noUpgrade);
										}
										else
										{
											if(_outputInfo.isServerOrRobot())
											{
												writer.createObject(cLeftName,typeT);
											}
											else
											{
												writer.writeVarSet(cLeftName,_code.getVarTypeTrans(ShineToolSetting.bytesSteamVarName + ".createData(" + typeT + ".dataID)",typeT));
											}
											writer.writeDataReadFull(cLeftName);
										}
									}
									else if(method==MakeMethodType.readSimple)
									{
										if(needExtends)
										{
											writer.writeDataReadSimpleForExtends(cLeftName,typeT);
										}
										else
										{
											if(_outputInfo.isServerOrRobot())
											{
												writer.createObject(cLeftName,typeT);
											}
											else
											{
												writer.writeVarSet(cLeftName,_code.getVarTypeTrans(ShineToolSetting.bytesSteamVarName + ".createData(" + typeT + ".dataID)",typeT));
											}
											
											writer.writeDataReadSimple(cLeftName);
										}
									}
									
									if(sourceField.maybeNull)
									{
										writer.writeElseAndDoubleBrace();
										writer.writeVarSet(cLeftName,_code.Null);
										writer.writeRightBrace();
									}
								}
									break;
								case MakeMethodType.writeFull:
								case MakeMethodType.writeSimple:
								{
									writer.writeCustom("if(" + cLeftName + "!=" + _code.Null + ")");
									writer.writeLeftBrace();
									if(sourceField.maybeNull)
										writer.writeWriteBaseType(VarType.Boolean,_code.True);
									
									
									if(method==MakeMethodType.writeFull)
									{
										if(needExtends)
										{
											writer.writeDataFullForExtends(cLeftName);
										}
										else
										{
											writer.writeDataFull(cLeftName);
										}
									}
									else if(method==MakeMethodType.writeSimple)
									{
										if(needExtends)
										{
											writer.writeDataSimpleForExtends(cLeftName);
										}
										else
										{
											writer.writeDataSimple(cLeftName);
										}
									}
									
									writer.writeElseAndDoubleBrace();
									if(sourceField.maybeNull)
										writer.writeWriteBaseType(VarType.Boolean,_code.False);
									else
										writer.writeNullObjError(nName);
									
									writer.writeRightBrace();
								}
									break;
								case MakeMethodType.copy:
								case MakeMethodType.copySelf:
								{
									writer.writeCustom("if(" + cRightName + "!=" + _code.Null + ")");
									writer.writeLeftBrace();
									
									if(needExtends)
									{
										writer.writeVarSet(cLeftName,_code.getVarTypeTrans(_code.getDataCopy(cRightName),typeT));
									}
									else
									{
										if(_outputInfo.isServerOrRobot())
										{
											writer.createObject(cLeftName,typeT);
										}
										else
										{
											writer.writeVarSet(cLeftName,_code.getVarTypeTrans(ShineToolSetting.bytesControlName+".createData("+typeT+".dataID)",typeT));
										}
										
										writer.writeCustom(cLeftName+".copy("+cRightName+");");
									}
									
									writer.writeElseAndDoubleBrace();
									writer.writeVarSet(cLeftName,_code.Null);
									if(!sourceField.maybeNull)
										writer.writeNullObjError(nName);
									writer.writeRightBrace();
								}
									break;
								case MakeMethodType.dataEquals:
								{
									writer.writeCustom("if(" + cRightName + "!=" + _code.Null + ")");
									writer.writeLeftBrace();
									
									writer.writeCustom("if(" + cLeftName + "==" + _code.Null + ")");
									writer.writeReturnBoolean(1,false);
									
									writer.writeCustom("if(!"+cLeftName+".dataEquals("+cRightName+"))");
									writer.writeReturnBoolean(1,false);
									
									writer.writeElseAndDoubleBrace();
									writer.writeCustom("if(" + cLeftName + "!=" + _code.Null + ")");
									writer.writeReturnBoolean(1,false);
									writer.writeRightBrace();
								}
									break;
								case MakeMethodType.toWriteDataString:
								{
									writer.writeCustom("if(" + cLeftName + "!=" + _code.Null + ")");
									writer.writeLeftBrace();
									
									writer.writeCustom(cLeftName+".writeDataString(writer);");
									
									writer.writeElseAndDoubleBrace();
									
									writer.writeWriterAppend("\""+typeT+"=null\"");
									writer.writeRightBrace();
								}
									break;
								case MakeMethodType.initDefault:
								{
									if(!sourceField.maybeNull)
									{
										writer.createObject(cLeftName,typeT);
										writer.writeCustom(cLeftName+".initDefault();");
									}
								}
									break;
								case MakeMethodType.initFields:
								{
									if(!sourceField.maybeNull)
									{
										writer.createObject(cLeftName,typeT);
										//writer.writeCustom(cLeftName+".initFields();");
									}
								}
									break;
								case MakeMethodType.clear:
								{
									//if(!sourceField.maybeNull)
									//{
									//	writer.writeCustom(cLeftName+".clear();");
									//}
									//else
									//{
									//	writer.writeVarSet(cLeftName,_code.Null);
									//}
									writer.writeVarSet(cLeftName,_code.Null);
								}
									break;
								case MakeMethodType.release:
								{
									if(needRelease)
									{
										if(!sourceField.maybeNull)
										{
											writer.writeCustom(cLeftName+".release(pool);");
											writer.writeVarSet(cLeftName,_code.Null);
										}
										else
										{
											writer.writeIfAndLeftBrace(cLeftName+"!="+_code.Null);
											writer.writeCustom(cLeftName+".release(pool);");
											writer.writeVarSet(cLeftName,_code.Null);
											writer.writeRightBrace();
										}
									}
									else
									{
										writer.writeVarSet(cLeftName,_code.Null);
									}
								}
									break;
							}
						}
					}
						break;
				}
			}
			
			switch(method)
			{
				case MakeMethodType.initDefault:
				case MakeMethodType.initFields:
				case MakeMethodType.clear:
				case MakeMethodType.release:
					break;
				default:
					writer.writeEmptyLine();
			}
			
			if(method==MakeMethodType.toWriteDataString)
			{
				if(!isChildCollection)
				{
					writer.writeWriterEnter();
				}
			}
		}
	}
	
}
