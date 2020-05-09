package com.home.shineTool.tool.data;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.utils.ObjectUtils;
import com.home.shine.utils.StringUtils;
import com.home.shineTool.constlist.CodeType;
import com.home.shineTool.constlist.VisitType;
import com.home.shineTool.global.ShineToolSetting;
import com.home.shineTool.reflect.FieldInfo;
import com.home.shineTool.reflect.MainMethodInfo;
import com.home.shineTool.reflect.MethodInfo;
import com.home.shineTool.reflect.cls.CSClassInfo;
import com.home.shineTool.reflect.cls.ClassInfo;
import com.home.shineTool.reflect.code.CodeInfo;
import com.home.shineTool.reflect.code.CodeWriter;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataMessageBindTool
{
	/** 回车 */
	public static final String Enter=ClassInfo.Enter;
	
	private String _path;
	
	private DataDefineTool _responseDefine;
	private DataDefineTool _requestDefine;
	
	private ClassInfo _cls;
	
	//temp
	
	private MainMethodInfo _mainMethod;
	private CodeWriter _mainWriter;
	
	private CodeInfo _code;
	
	private Map<String,OneData> _dic=new HashMap<>();
	
	private static Pattern _reg;
	
	/** 消息绑定Tool */
	public DataMessageBindTool(String path,DataDefineTool responseDefine,DataDefineTool requestDefine)
	{
		_path=path;
		_responseDefine=responseDefine;
		_requestDefine=requestDefine;
		
		_cls=ClassInfo.getClassInfoFromPathAbs(_path);
		
		String importQName=getFuncQNameForImport();
		if(importQName!=null)
		{
			_cls.addImport(importQName);
		}
		
		_code=_cls.getCode();
		
		_reg=getReg(_cls);
		
		_cls.extendsClsName=StringUtils.getClassNameForQName(ShineToolSetting.messageBindToolQName);
		_cls.addImport(ShineToolSetting.messageBindToolQName);
		
		_cls.addImport(responseDefine.getCls().getQName());
		_cls.addImport(requestDefine.getCls().getQName());
		
		if(_cls.getCodeType()==CodeType.CS)
		{
			((CSClassInfo)_cls).addUsing("System");
		}
		
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
				
				if(!temp2.isEmpty())
				{
					String[] arr=temp2.split(",");
					
					String[] re=new String[arr.length];
					
					for(int i=0;i<arr.length;i++)
					{
						String v=arr[i];
						
						re[i]=v.substring(v.indexOf(".")+1,v.length());
					}
					
					_dic.put(temp1,new OneData(temp1,re));
				}
				else
				{
					_dic.put(temp1,new OneData(temp1,new String[0]));
				}
			}
		}
		else
		{
			_mainMethod=new MainMethodInfo();
			
			_cls.addMainMethod(_mainMethod);
		}
		
		_mainWriter=_cls.createWriter();
		
		String defineClsName=_responseDefine.getCls().clsName;
		
		//写初值
		_mainWriter.writeVarSet(_cls.getFieldWrap("offSet"),defineClsName+".off");
		_mainWriter.writeVarSet(_cls.getFieldWrap("list"),_code.createNewArray(getFuncName(),defineClsName + ".count-"+_cls.getFieldWrap("offSet")));
	}
	
	private static Pattern getReg(ClassInfo cls)
	{
		if(cls.getCodeType()==CodeType.TS)
		{
			return Pattern.compile("list\\[(.+?)\\.(.+?)\\-.*?offSet\\]=new Array\\(\\)\\.concat\\((.*?)\\);");
		}
		else
		{
			return Pattern.compile("list\\[(.+?)\\.(.+?)\\-.*?offSet\\]=.*?\\{(.*?)\\};");
		}
	}
	
	/** 添加一个 */
	public void addOne(String cName,String[] requests)
	{
		if(_responseDefine.getCls().getField(cName)==null)
		{
			Ctrl.throwError("define中不存在此类",cName);
			return;
		}
		
		_dic.put(cName,new OneData(cName,requests));
	}
	
	/** 删除一个 */
	public void removeOne(String cName)
	{
		_dic.remove(cName);
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
			FieldInfo ff=_responseDefine.getCls().getField(k);
			
			if(ff!=null)
			{
				tempDic.put(Integer.parseInt(ff.defaultValue),k);
			}
		}
		
		for(int k : ObjectUtils.getSortMapKeys(tempDic))
		{
			String n=tempDic.get(k);
			
			OneData data=_dic.get(n);
			
			String kk=_responseDefine.getCls().clsName + "." + data.cName;
			
			StringBuilder sb=new StringBuilder();
			
			for(int i=0;i<data.requests.length;i++)
			{
				if(i>0)
				{
					sb.append(",");
				}
				
				sb.append(_requestDefine.getCls().clsName+"."+data.requests[i]);
			}
			
			switch(_cls.getCodeType())
			{
				case CodeType.TS:
				{
					_mainWriter.writeCustom(_cls.getFieldWrap("list")+"[" + kk +"-"+ _cls.getFieldWrap("offSet")+"]=new Array().concat(" + sb.toString() + ");");
				}
				break;
				case CodeType.AS3:
				{
					_mainWriter.writeCustom("list[" + kk + "-offSet]=new int[]{" + sb.toString() + "};");
				}
				break;
				default:
				{
					_mainWriter.writeCustom("list[" + kk + "-offSet]=new int[]{" + sb.toString() + "};");
				}
					break;
			}
		}
		
		_mainWriter.writeEnd();
		
		_mainMethod.content=_mainWriter.toString();
		
		_cls.writeToPath(_path);
	}
	
	private class OneData
	{
		public String cName;
		
		public String[] requests;
		
		public OneData(String cName,String[] requests)
		{
			this.cName=cName;
			this.requests=requests;
		}
	}
	
	//selfDefine
	
	private String getFuncQNameForImport()
	{
		String re="";
		
		switch(_cls.getCodeType())
		{
			//case CodeType.Java:
			//{
			//	re=ShineToolSetting.globalPackage + "shine.tool.CreateDataFunc";
			//}
			//	break;
		}
		
		return re;
	}
	
	private String getFuncName()
	{
		String re="";
		
		switch(_cls.getCodeType())
		{
			case CodeType.Java:
			case CodeType.CS:
			case CodeType.AS3:
			case CodeType.TS:
			{
				re=_code.getArrayType(_code.Int,false);
			}
			break;
		}
		
		return re;
	}
}
