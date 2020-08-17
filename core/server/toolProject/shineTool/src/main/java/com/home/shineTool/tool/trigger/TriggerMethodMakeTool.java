package com.home.shineTool.tool.trigger;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.SList;
import com.home.shine.support.collection.SMap;
import com.home.shine.support.collection.SSet;
import com.home.shine.utils.FileUtils;
import com.home.shine.utils.ObjectUtils;
import com.home.shine.utils.StringUtils;
import com.home.shineTool.constlist.CodeType;
import com.home.shineTool.constlist.DataGroupType;
import com.home.shineTool.constlist.VarType;
import com.home.shineTool.constlist.VisitType;
import com.home.shineTool.global.ShineToolSetting;
import com.home.shineTool.reflect.FieldInfo;
import com.home.shineTool.reflect.MainMethodInfo;
import com.home.shineTool.reflect.MethodArgInfo;
import com.home.shineTool.reflect.MethodInfo;
import com.home.shineTool.reflect.cls.CSClassInfo;
import com.home.shineTool.reflect.cls.ClassInfo;
import com.home.shineTool.reflect.code.CodeInfo;
import com.home.shineTool.reflect.code.CodeWriter;
import com.home.shineTool.tool.data.DataDefineTool;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TriggerMethodMakeTool
{
	/** 回车 */
	public static final String Enter=ClassInfo.Enter;
	
	private TriggerMakeTool _makeTool;
	
	private String _registerPath;
	private String _methodPath;
	
	private DataDefineTool _define;
	
	private ClassInfo _registerCls;
	private ClassInfo _methodCls;
	
	private CodeInfo _inputCode;
	
	//temp
	
	private MainMethodInfo _mainMethod;
	private CodeWriter _mainWriter;
	
	private CodeInfo _outCode;
	
	private SSet<String> _dic=new SSet<>();
	private SMap<String,MethodPair> _methodDic=new SMap<>();
	
	private static Pattern _reg=Pattern.compile("list\\[(.+?)\\.(.+?)\\-.*?offSet\\]=.*?delegate_(.+?);");
	
	private SMap<String,MethodInfo> _oldMethodDic=new SMap<>();
	
	public TriggerMethodMakeTool(String registerPath,String methodPath,DataDefineTool define,TriggerMakeTool makeTool)
	{
		_registerPath=registerPath;
		_methodPath=methodPath;
		
		_define=define;
		
		_makeTool=makeTool;
		
		_inputCode=CodeInfo.getCode(ShineToolSetting.defaultCodeType);
		
		_registerCls=ClassInfo.getVoidClassInfoFromPath(_registerPath);
		_outCode=_registerCls.getCode();
		
		_registerCls.extendsClsName=StringUtils.getClassNameForQName(ShineToolSetting.TriggerFuncMakerQName);
		_registerCls.addImport(ShineToolSetting.TriggerFuncMakerQName);
		
		_registerCls.addImport(_define.getCls().getQName());
		
		_registerCls.addImport(ShineToolSetting.triggerExecutorQName);
		_registerCls.addImport(ShineToolSetting.triggerFuncDataQName);
		_registerCls.addImport(ShineToolSetting.triggerFuncEntryQName);
		_registerCls.addImport(ShineToolSetting.triggerArgQName);
		
		_methodCls=ClassInfo.getClassInfoFromPathAbs(_methodPath);
		_methodCls.addImport(ShineToolSetting.triggerExecutorQName);
		_methodCls.addImport(ShineToolSetting.triggerArgQName);
		
		if(_outCode.getCodeType()==CodeType.CS)
		{
			((CSClassInfo)_registerCls).addUsing("System");
			((CSClassInfo)_methodCls).addUsing("System");
		}
		
		_methodCls.getMethodDic().forEachValue(v->
		{
			if(isMakeFunc(v))
			{
				_oldMethodDic.put(v.getKey(),v);
			}
		});
		
		//main
		
		_mainMethod=_registerCls.getMainMethod();
		
		if(_mainMethod!=null)
		{
			//获取正则
			
			Matcher m=_reg.matcher(_mainMethod.content);
			
			while(m.find())
			{
				String temp1=m.group(2);
				//String temp2=m.group(3);
				
				_dic.add(temp1);
			}
		}
		else
		{
			_mainMethod=new MainMethodInfo();
			
			_registerCls.addMainMethod(_mainMethod);
		}
		
		FieldInfo mField=new FieldInfo();
		mField.type=_methodCls.clsName;
		mField.name="_m";
		mField.visitType=VisitType.Private;
		mField.describe="方法组";
		mField.defaultValue=_outCode.createNewObject(_methodCls.clsName);
		
		_registerCls.addImport(_methodCls.getQName());
		_registerCls.addField(mField);
		
		_mainWriter=_registerCls.createWriter();
		
		String defineClsName=_define.getCls().clsName;
		
		//写初值
		_mainWriter.writeVarSet(_registerCls.getFieldWrap("offSet"),defineClsName+".off");
		_mainWriter.writeVarSet(_registerCls.getFieldWrap("list"),_outCode.createNewArray(StringUtils.getClassNameForQName(ShineToolSetting.triggerFuncEntryQName),defineClsName + ".count-"+ _registerCls.getFieldWrap("offSet")));
	}
	
	
	protected boolean isMakeFunc(MethodInfo methodInfo)
	{
		return methodInfo.name.startsWith("func_");
	}
	
	/** 定义插件 */
	public DataDefineTool getDefine()
	{
		return _define;
	}
	
	/** 添加一个(要求define中必须先有) */
	public void addOne(MethodInfo methodInfo,ClassInfo inputCls,String triggerCls)
	{
		String cName=StringUtils.ucWord(methodInfo.name);
		
		//define没有
		if(_define.getCls().getField(cName)==null)
		{
			Ctrl.throwError("define中不存在此类",cName);
			return;
		}
		
		if(_makeTool.isEventFunc(methodInfo.name))
		{
			//event不生成
			return;
		}
		
		_dic.add(cName);
		
		if(triggerCls.isEmpty())
		{
			triggerCls=ShineToolSetting.triggerExecutorQName;
		}
		
		MethodPair methodPair=new MethodPair();
		methodPair.methodInfo=methodInfo;
		methodPair.cls=inputCls;
		methodPair.triggerClsQName=triggerCls;
		methodPair.triggerClsName=StringUtils.getClassNameForQName(triggerCls);
		
		_methodDic.put(cName,methodPair);
	}
	
	/** 删除一个 */
	public void removeOne(String cName)
	{
		//有就删了旧的
		if(_dic.contains(cName))
		{
			_dic.remove(cName);
			
			//删导入
			_registerCls.removeMethodByName("func_" + cName);
		}
	}
	
	/** 写入文件 */
	public void write()
	{
		//有了再加
		if(_dic.size()>0)
		{
			_methodCls.addImport(ShineToolSetting.CtrlQName);
		}
		
		Map<Integer,String> tempDic=new HashMap<>();
		
		for(String k : _dic)
		{
			FieldInfo ff=_define.getCls().getField(k);
			
			if(ff!=null)
			{
				tempDic.put(Integer.parseInt(ff.defaultValue),k);
			}
		}
		
		String executorClsName=StringUtils.getClassNameForQName(ShineToolSetting.triggerExecutorQName);
		String funcDataClsName=StringUtils.getClassNameForQName(ShineToolSetting.triggerFuncDataQName);
		String argClsName=StringUtils.getClassNameForQName(ShineToolSetting.triggerArgQName);
		String entryClsName=StringUtils.getClassNameForQName(ShineToolSetting.triggerFuncEntryQName);
		
		boolean isClient=_define.group==DataGroupType.Client;
		
		for(int k : ObjectUtils.getSortMapKeys(tempDic))
		{
			String cName=tempDic.get(k);
			
			MethodPair methodPair=_methodDic.get(cName);
			
			if(methodPair!=null)
			{
				//int sc=_makeTool.functionDefineTypeDic.get(k);
				int sc=_makeTool.functionDefineTypeDic.get(_makeTool.functionDefineDic.get(methodPair.methodInfo.name));
				
				//不匹配
				if(!((isClient && (sc & 1)==1) || (!isClient && (sc >> 1)==1)))
				{
					continue;
				}
				
				String kk=_define.getCls().clsName + "." + cName;
				
				String deleName="delegate_"+cName;
				String funcName="func_"+cName;
				
				String registFuncName="";
				
				if(_registerCls.getCodeType()==CodeType.Java)
				{
					registFuncName="this::"+deleName;
				}
				else if(_registerCls.getCodeType()==CodeType.TS)
				{
					Ctrl.throwError("暂不支持TS");
					//_mainWriter.writeCustom(_cls.getFieldWrap("list")+"[" + kk + "-"+_cls.getFieldWrap("offSet")+"]=this." + funcName + ";");
				}
				else
				{
					registFuncName=deleName;
				}
				
				MethodInfo inputMethod=methodPair.methodInfo;
				ClassInfo cls=methodPair.cls;
				
				String registReturnType=getOutType(inputMethod.returnType,cls,true);
				String returnType=getOutType(inputMethod.returnType,cls,false);
				
				String createMethodName="";
				
				if(registReturnType.equals(_outCode.Void))
					createMethodName="createVoid";
				else if(registReturnType.equals(_outCode.Boolean))
					createMethodName="createBoolean";
				else if(registReturnType.equals(_outCode.Int))
					createMethodName="createInt";
				else if(registReturnType.equals(_outCode.Long))
					createMethodName="createLong";
				else if(registReturnType.equals(_outCode.Float))
					createMethodName="createFloat";
				else if(registReturnType.equals(_outCode.String))
					createMethodName="createString";
				else if(registReturnType.equals(_outCode.Object))
					createMethodName="createObject";
				
				_mainWriter.writeCustom("list[" + kk + "-offSet]=" + entryClsName+"."+createMethodName+"(" + registFuncName + ");");
				
				MethodInfo method;
				
				method=new MethodInfo();
				method.name=deleName;
				method.visitType=VisitType.Private;
				method.returnType=registReturnType;
				//method.isFinal=true;
				method.args.add(new MethodArgInfo("e",executorClsName));
				method.args.add(new MethodArgInfo("func",funcDataClsName));
				method.args.add(new MethodArgInfo("arg",argClsName));
				
				
				CodeWriter writer=_registerCls.createWriter();
				
				StringBuilder sb=new StringBuilder();
				
				sb.append("_m.");
				sb.append(funcName);
				sb.append('(');
				
				//不是基类
				if(!methodPair.triggerClsName.equals(executorClsName))
				{
					_registerCls.addImport(methodPair.triggerClsQName);
					_methodCls.addImport(methodPair.triggerClsQName);
					
					sb.append('(');
					sb.append(methodPair.triggerClsName);
					sb.append(')');
				}
				
				sb.append("e,arg");
				
				SList<MethodArgInfo> args=inputMethod.args;
				
				for(int i=0;i<args.size();i++)
				{
					sb.append(",");
					
					MethodArgInfo arg=args.get(i);
					
					String outType=getOutType(arg.type,cls);
					
					int varType=_outCode.getVarType(arg.type);
					
					//修正为Int
					if(arg.type.startsWith("Class<?"))
						varType=VarType.Int;
					
					if(VarType.isBaseType(varType))
					{
						sb.append("e.");
						
						switch(varType)
						{
							case VarType.Boolean:
							{
								sb.append("getBoolean");
							}
							break;
							case VarType.Int:
							{
								sb.append("getInt");
							}
							break;
							case VarType.Long:
							{
								sb.append("getLong");
							}
							break;
							case VarType.Float:
							{
								sb.append("getFloat");
							}
							break;
							case VarType.String:
							{
								sb.append("getString");
							}
							break;
						}
					}
					else
					{
						//不是Object类型
						if(!outType.equals(_outCode.Object))
						{
							sb.append('(');
							sb.append(outType);
							sb.append(')');
						}
						
						sb.append("e.");
						sb.append("getObj");
					}
					
					sb.append("(func.args[");
					sb.append(i);
					sb.append("],arg)");
				}
				
				sb.append(')');
				
				if(!registReturnType.equals(_outCode.Void))
				{
					writer.writeReturn(sb.toString());
				}
				else
				{
					writer.writeCustom(sb.toString()+";");
				}
				
				writer.writeEnd();
				method.content=writer.toString();
				
				_registerCls.addMethod(method);
				
				
				method=new MethodInfo();
				method.name=funcName;
				method.describe=inputMethod.describe;
				method.visitType=VisitType.Public;
				method.returnType=returnType;
				
				method.args.add(new MethodArgInfo("e",methodPair.triggerClsName));
				method.args.add(new MethodArgInfo("a",argClsName));
				
				for(MethodArgInfo arg : inputMethod.args)
				{
					MethodArgInfo argC=arg.clone();
					argC.type=getOutType(arg.type,cls);
					method.args.add(argC);
				}
				
				MethodInfo method1=_methodCls.getMethod(method.getKey());
				
				//标记移除
				_oldMethodDic.remove(method.getKey());
				
				if(method1!=null)
				{
					method1.describe=inputMethod.describe;
					method1.visitType=VisitType.Public;
				}
				else
				{
					writer=_methodCls.createWriter();
					
					writer.writeShouldDo(method.name);
					
					//不是void
					if(!method.returnType.equals(_outCode.Void))
					{
						writer.writeReturn(_outCode.getDefaultValue(method.returnType));
					}
					
					writer.writeEnd();
					method.content=writer.toString();
					
					_methodCls.addMethod(method);
				}
			}
		}
		
		if(!_oldMethodDic.isEmpty())
		{
			_oldMethodDic.forEachValue(v->
			{
				_methodCls.removeMethod(v);
			});
		}
		
		_mainWriter.writeEnd();
		
		_mainMethod.content=_mainWriter.toString();
		
		_registerCls.writeToPath(_registerPath);
		_methodCls.writeToPath(_methodPath);
	}
	
	private String getOutType(String type,ClassInfo inputCls)
	{
		return getOutType(type,inputCls,false);
	}
	
	private String getOutType(String type,ClassInfo inputCls,boolean isRegist)
	{
		if(type.equals(_inputCode.Void))
			return _outCode.Void;
		
		int varType=_inputCode.getVarType(type);
		
		if(VarType.isBaseType(varType))
		{
			return _outCode.getVarStr(varType);
		}
		
		if(varType==VarType.CustomObject)
		{
			//修正为Int
			if(type.startsWith("Class<?"))
				return _outCode.Int;
			
			if(isRegist || inputCls==null)
				return _outCode.Object;
			
			ClassInfo typeCls=_makeTool.getInputCls(inputCls.getImport(type));
			
			if(typeCls==null)
				return _outCode.Object;
			
			String targetQName=typeCls.getAnnotationValue("TriggerClass");
			
			if(targetQName==null || targetQName.isEmpty())
				return _outCode.Object;
			
			targetQName=StringUtils.cutOutsideOne(targetQName);
			
			_registerCls.addImport(targetQName);
			_methodCls.addImport(targetQName);
			return StringUtils.getClassNameForQName(targetQName);
		}
		
		if(isRegist)
			return _outCode.Object;
		
		switch(varType)
		{
			case VarType.Array:
			{
				String elementType=CodeInfo.getVarArrayType(type);
				String outType=getOutType(elementType,inputCls);
				String eImport=_outCode.getArrayTypeImport(outType,false);
				_registerCls.addImport(eImport);
				_methodCls.addImport(eImport);
				return _outCode.getArrayType(outType,false);
			}
			case VarType.Set:
			{
				String elementType=CodeInfo.getVarCollectionOneType(type);
				String outType=getOutType(elementType,inputCls);
				String eImport=_outCode.getSetTypeImport(elementType,false);
				_registerCls.addImport(eImport);
				_methodCls.addImport(eImport);
				return _outCode.getSetType(outType,false);
			}
			case VarType.List:
			{
				String elementType=CodeInfo.getVarCollectionOneType(type);
				String outType=getOutType(elementType,inputCls);
				String eImport=_outCode.getListTypeImport(elementType,false);
				_registerCls.addImport(eImport);
				_methodCls.addImport(eImport);
				return _outCode.getListType(outType,false);
			}
			case VarType.Queue:
			{
				String elementType=CodeInfo.getVarCollectionOneType(type);
				String outType=getOutType(elementType,inputCls);
				String eImport=_outCode.getQueueTypeImport(elementType,false);
				_registerCls.addImport(eImport);
				_methodCls.addImport(eImport);
				return _outCode.getQueueType(outType,false);
			}
			case VarType.Map:
			{
				CodeInfo.CollectionTwoType twoT=CodeInfo.getVarCollectionTwoType(type);
				String kType=getOutType(twoT.type0,inputCls);
				String vType=getOutType(twoT.type1,inputCls);
				
				String eImport=_outCode.getMapTypeImport(kType,vType,false);
				_registerCls.addImport(eImport);
				_methodCls.addImport(eImport);
				return _outCode.getMapType(kType,vType,false);
			}
		}
		
		return "";
	}
	
	private class MethodPair
	{
		public MethodInfo methodInfo;
		
		public ClassInfo cls;
		
		public String triggerClsQName;
		
		public String triggerClsName;
	}
}
