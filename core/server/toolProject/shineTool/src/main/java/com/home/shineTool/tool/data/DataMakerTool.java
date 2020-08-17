package com.home.shineTool.tool.data;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.SMap;
import com.home.shine.utils.ObjectUtils;
import com.home.shine.utils.StringUtils;
import com.home.shineTool.constlist.CodeType;
import com.home.shineTool.constlist.VisitType;
import com.home.shineTool.reflect.FieldInfo;
import com.home.shineTool.reflect.MainMethodInfo;
import com.home.shineTool.reflect.MethodInfo;
import com.home.shineTool.reflect.cls.CSClassInfo;
import com.home.shineTool.reflect.cls.ClassInfo;
import com.home.shineTool.reflect.code.CodeInfo;
import com.home.shineTool.reflect.code.CodeWriter;
import com.home.shineTool.global.ShineToolSetting;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 数据构造类 */
public class DataMakerTool
{
	/** 回车 */
	public static final String Enter=ClassInfo.Enter;
	
	private String _path;
	
	private DataDefineTool _define;
	
	private ClassInfo _cls;
	
	//temp
	
	private MainMethodInfo _mainMethod;
	private CodeWriter _mainWriter;
	
	private CodeInfo _code;
	
	private Map<String,OneData> _dic=new HashMap<>();
	
	private static Pattern _reg=Pattern.compile("list\\[(.+?)\\.(.+?)\\-.*?offSet\\]=.*?create(.+?);");
	
	private SMap<String,MethodInfo> _oldMethodDic=new SMap<>();
	
	public DataMakerTool(String path,DataDefineTool define)
	{
		_path=path;
		_define=define;
		
		_cls=ClassInfo.getClassInfoFromPathAbs(_path);
		
		_cls.addImport(getFuncQNameForImport());
		
		_code=_cls.getCode();
		
		_cls.extendsClsName=StringUtils.getClassNameForQName(ShineToolSetting.dataMakerQName);
		_cls.addImport(ShineToolSetting.dataMakerQName);
		
		_cls.addImport(_define.getCls().getQName());
		
		if(_cls.getCodeType()==CodeType.CS)
		{
			((CSClassInfo)_cls).addUsing("System");
		}
		
		_cls.getMethodDic().forEachValue(v->
		{
			if(isMakeFunc(v))
			{
				_oldMethodDic.put(v.name,v);
			}
		});
		
		//main
		
		_mainMethod=_cls.getMainMethod();
		
		if(_mainMethod!=null)
		{
			//获取正则
			//_reg=getReg(_cls);
			
			Matcher m=_reg.matcher(_mainMethod.content);
			
			while(m.find())
			{
				String temp1=m.group(2);
				String temp2=m.group(3);
				
				_dic.put(temp1,new OneData(temp1,temp2,_cls.getImportPackage(temp2)));
			}
		}
		else
		{
			_mainMethod=new MainMethodInfo();
			
			_cls.addMainMethod(_mainMethod);
		}
		
		_mainWriter=_cls.createWriter();
		
		String defineClsName=_define.getCls().clsName;
		
		//写初值
		_mainWriter.writeVarSet(_cls.getFieldWrap("offSet"),defineClsName+".off");
		_mainWriter.writeVarSet(_cls.getFieldWrap("list"),_code.createNewArray(getFuncName(),defineClsName + ".count-"+_cls.getFieldWrap("offSet")));
	}
	
	protected boolean isMakeFunc(MethodInfo methodInfo)
	{
		return methodInfo.name.startsWith("create");
	}
	
	/** 定义插件 */
	public DataDefineTool getDefine()
	{
		return _define;
	}
	
	/** 添加一个(要求define中必须先有) */
	public void addOne(String cName,String clsName,String packageStr)
	{
		//define没有
		if(_define.getCls().getField(cName)==null)
		{
			Ctrl.throwError("define中不存在此类",clsName);
			return;
		}
		
		//移动
		if(_dic.containsKey(cName))
		{
			OneData oldData=_dic.remove(cName);
			
			//删导入
			_cls.removeImport(oldData.clsQName);
		}
		
		_dic.put(cName,new OneData(cName,clsName,packageStr));
	}
	
	/** 删除一个 */
	public void removeOne(String cName)
	{
		//有就删了旧的
		if(_dic.containsKey(cName))
		{
			OneData oldData=_dic.remove(cName);
			
			//删导入
			_cls.removeImport(oldData.packageStr + "." + oldData.clsName);
			_cls.removeMethodByName("create" + oldData.clsName);
		}
	}
	
	/** 写入文件 */
	public void write()
	{
		//有了再加
		if(_dic.size()>0)
		{
			_cls.addImport(ShineToolSetting.dataBaseQName);
		}
		
		Map<Integer,String> tempDic=new HashMap<>();
		
		for(String k : _dic.keySet())
		{
			FieldInfo ff=_define.getCls().getField(k);
			
			if(ff!=null)
			{
				tempDic.put(Integer.parseInt(ff.defaultValue),k);
			}
		}
		
		for(int k : ObjectUtils.getSortMapKeys(tempDic))
		{
			String n=tempDic.get(k);
			
			OneData data=_dic.get(n);
			
			String kk=_define.getCls().clsName + "." + data.cName;
			
			_cls.addImport(data.packageStr + "." + data.clsName);
			
			if(_cls.getCodeType()==CodeType.Java)
			{
				_mainWriter.writeCustom("list[" + kk + "-offSet]=this::create" + data.clsName + ";");
			}
			else if(_cls.getCodeType()==CodeType.TS)
			{
				_mainWriter.writeCustom(_cls.getFieldWrap("list")+"[" + kk + "-"+_cls.getFieldWrap("offSet")+"]=this.create" + data.clsName + ";");
			}
			else
			{
				_mainWriter.writeCustom("list[" + kk + "-offSet]=create" + data.clsName + ";");
			}
			
			//method
			
			MethodInfo method=new MethodInfo();
			method.name="create" + data.clsName;
			method.visitType=VisitType.Private;
			method.returnType=ShineToolSetting.dataBaseName;
			
			CodeWriter writer=_cls.createWriter();
			writer.writeCustom("return " + _code.createNewObject(data.clsName) + ";");
			writer.writeEnd();
			method.content=writer.toString();
			
			_oldMethodDic.remove(method.name);
			
			_cls.addMethod(method);
		}
		
		if(!_oldMethodDic.isEmpty())
		{
			_oldMethodDic.forEachValue(v->
			{
				_cls.removeMethod(v);
			});
		}
		
		_mainWriter.writeEnd();
		
		_mainMethod.content=_mainWriter.toString();
		
		_cls.writeToPath(_path);
	}
	
	private class OneData
	{
		public String cName;
		
		public String clsName;
		
		public String packageStr;
		
		public String clsQName;
		
		public OneData(String cName,String clsName,String packageStr)
		{
			this.cName=cName;
			this.clsName=clsName;
			this.packageStr=packageStr;
			this.clsQName=packageStr+"."+clsName;
		}
	}
	
	//selfDefine
	
	private String getFuncQNameForImport()
	{
		String re="";
		
		switch(_cls.getCodeType())
		{
			case CodeType.Java:
			{
				re=ShineToolSetting.globalPackage + "shine.tool.CreateDataFunc";
			}
			break;
		}
		
		return re;
	}
	
	private String getFuncName()
	{
		String re="";
		
		switch(_cls.getCodeType())
		{
			case CodeType.Java:
			{
				re="CreateDataFunc";
			}
			break;
			case CodeType.CS:
			{
				re="Func<" + ShineToolSetting.dataBaseName + ">";
			}
			break;
			case CodeType.AS3:
			{
				re="Function";
			}
			break;
			case CodeType.TS:
			{
				re="Function";
			}
			break;
		}
		
		return re;
	}
}
