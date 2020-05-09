package com.home.shineTool.tool.trigger;

import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.constlist.STriggerObjectType;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.data.trigger.TriggerBooleanData;
import com.home.shine.data.trigger.TriggerConfigData;
import com.home.shine.data.trigger.TriggerFloatData;
import com.home.shine.data.trigger.TriggerFuncData;
import com.home.shine.data.trigger.TriggerFuncListData;
import com.home.shine.data.trigger.TriggerIntData;
import com.home.shine.data.trigger.TriggerLongData;
import com.home.shine.data.trigger.TriggerObjData;
import com.home.shine.data.trigger.TriggerStringData;
import com.home.shine.support.XML;
import com.home.shine.support.collection.IntIntMap;
import com.home.shine.support.collection.IntList;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.SList;
import com.home.shine.support.collection.SMap;
import com.home.shine.support.collection.StringIntMap;
import com.home.shine.utils.FileUtils;
import com.home.shine.utils.StringUtils;
import com.home.shineTool.global.ShineToolSetting;
import com.home.shineTool.reflect.FieldInfo;
import com.home.shineTool.reflect.MainMethodInfo;
import com.home.shineTool.reflect.MethodArgInfo;
import com.home.shineTool.reflect.MethodInfo;
import com.home.shineTool.tool.base.BaseExportTool;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** trigger数据导出 */
public class TriggerDataExportTool extends BaseExportTool
{
	private static Pattern _fieldReg=Pattern.compile("([^ ^\\n^\\t]+)( ?)=( ?)([^ ^\\n^\\t]+?);");
	/** 过滤 */
	private static Pattern _filterReg=Pattern.compile("[\\+\\*\\/\\%\\|\\&\\!]");//\-
	
	private static Pattern _m1Reg=Pattern.compile("([^ ^\\n^\\t]*?)\\(");
	
	private TriggerMakeTool _makeTool;
	
	/** 保存路径 */
	private String _savePath;
	
	/** trigger cName->index */
	private SMap<String,TriggerInfo> _indexDic=new SMap<>();
	/** trigger index->cName */
	private IntObjectMap<TriggerInfo> _indexDic2=new IntObjectMap<>();
	
	private int _maxIndex=0;
	
	private IntObjectMap<TriggerConfigData> _serverDic=new IntObjectMap<>(k->new TriggerConfigData[k]);
	private IntObjectMap<TriggerConfigData> _clientDic=new IntObjectMap<>(k->new TriggerConfigData[k]);
	
	private TriggerConfigData _currentData;
	
	public TriggerDataExportTool(TriggerMakeTool makeTool)
	{
		_makeTool=makeTool;
	}
	
	public void setSavePath(String path)
	{
		_savePath=path;
		
		if(!ShineToolSetting.isAllRefresh)
		{
			XML xml=FileUtils.readFileForXML(_savePath);
			
			if(xml!=null)
			{
				xml.children().forEach(v->
				{
					TriggerInfo info=new TriggerInfo();
					info.id=Integer.parseInt(v.getProperty("id"));
					info.name=v.getProperty("name");
					info.groupType=Integer.parseInt(v.getProperty("groupType"));
					info.groupID=Integer.parseInt(v.getProperty("groupID"));
					
					_indexDic.put(info.name,info);
					_indexDic2.put(info.id,info);
					
					if(_maxIndex<info.id)
						_maxIndex=info.id;
				});
			}
		}
	}
	
	@Override
	protected void preMakeInput()
	{
		super.preMakeInput();
		
		String qName=_inputCls.getQName();
		
		String cName=qName.substring(_inputRootPackge.length()+1);
		
		TriggerInfo info=_indexDic.get(cName);
		
		if(info==null)
		{
			info=new TriggerInfo();
			info.id=++_maxIndex;
			info.name=cName;
			
			String[] groupN=cName.substring(0,cName.indexOf(".")).split("_");
			
			String gName=groupN[0];
			int gID=0;
			
			if(groupN.length>1)
			{
				gID=Integer.parseInt(groupN[1]);
			}
			
			int gType=_makeTool.groupDefineDic.get(gName);
			
			if(gType<0)
			{
				Ctrl.throwError("未找到组:"+gName);
				return;
			}
			
			info.groupType=gType;
			info.groupID=gID;
			
			_indexDic.put(cName,info);
			_indexDic2.put(info.id,info);
		}
	}
	
	@Override
	protected void toMakeInput()
	{
		String qName=_inputCls.getQName();
		
		String cName=qName.substring(_inputRootPackge.length()+1);
		
		TriggerInfo info=_indexDic.get(cName);
		
		TriggerConfigData cData=new TriggerConfigData();
		cData.id=info.id;
		cData.name=_inputCls.clsName.substring(0,_inputCls.clsName.length()-1);
		cData.groupType=info.groupType;
		cData.groupID=info.groupID;
		cData.isOpen=false;
		cData.priority=0;
		
		_currentData=cData;
		
		MethodInfo initFunc=_inputCls.getMethodByName("init");
		if(initFunc!=null)
		{
			Matcher matcher=_fieldReg.matcher(initFunc.content);
			
			while(matcher.find())
			{
				String group=matcher.group(1);
				
				if(group.startsWith("//"))
					continue;
				
				if(group.equals("isOpen"))
				{
					cData.isOpen=matcher.group(4).equals("true") ? true : false;
				}
				else if(group.equals("priority"))
				{
					cData.priority=Integer.parseInt(matcher.group(4));
				}
			}
		}
		
		MethodInfo partFunc=_inputCls.getMethodByName("event");
		if(partFunc!=null)
			cData.events=toMakeOneM(partFunc.content,1).funcList;
		else
			cData.events=new TriggerFuncData[0];
		
		partFunc=_inputCls.getMethodByName("condition");
		if(partFunc!=null)
			cData.conditions=toMakeOneM(partFunc.content,2).funcList;
		else
			cData.conditions=new TriggerFuncData[0];
		
		partFunc=_inputCls.getMethodByName("action");
		if(partFunc!=null)
			cData.actions=toMakeOneM(partFunc.content,3).funcList;
		else
			cData.actions=new TriggerFuncData[0];
		
		
		int sc=_makeTool.groupDefineTypeDic.get(cData.groupType);
		
		//c
		if((sc & 1)==1)
		{
			_clientDic.put(cData.id,cData);
		}
		
		//s
		if((sc >> 1)==1)
		{
			_serverDic.put(cData.id,cData);
		}
	}
	
	@Override
	protected void toMakeBefore()
	{
	
	}
	
	@Override
	protected void toMakeOneField(FieldInfo field,FieldInfo outField)
	{
	
	}
	
	@Override
	protected void toMakeAfter()
	{
	
	}
	
	@Override
	protected int addOneDefine(String cName,String des)
	{
		//不用默认
		return -1;
	}
	
	@Override
	protected void endExecute()
	{
		super.endExecute();
		
		writeOne(_serverDic,_makeTool.getServerOutTempPath());
		if(ShineToolSetting.needClient)
			writeOne(_clientDic,_makeTool.getClientOutTempPath());
		
		writeSaveXML();
	}
	
	private void writeOne(IntObjectMap<TriggerConfigData> dic,String path)
	{
		//写文件
		BytesWriteStream stream=new BytesWriteStream();
		
		stream.writeLen(dic.size());
		
		IntList keyList=dic.getSortedKeyList();
		
		int[] values=keyList.getValues();
		
		for(int i=0,len=keyList.size();i<len;++i)
		{
			dic.get(values[i]).writeBytesSimple(stream);
		}
		
		FileUtils.writeFileForBytesWriteStream(path+"/trigger.bin",stream);
	}
	
	private void writeSaveXML()
	{
		XML xml=new XML();
		xml.setName("trigger");
		
		IntList keyList=_indexDic2.getSortedKeyList();
		
		int[] values=keyList.getValues();
		
		for(int i=0,len=keyList.size();i<len;++i)
		{
			TriggerInfo info=_indexDic2.get(values[i]);
			
			XML child=new XML();
			child.setName("one");
			child.setProperty("id",String.valueOf(values[i]));
			child.setProperty("name",info.name);
			child.setProperty("groupType",String.valueOf(info.groupType));
			child.setProperty("groupID",String.valueOf(info.groupID));
			
			xml.appendChild(child);
		}
		
		FileUtils.writeFileForXML(_savePath,xml);
	}
	
	protected boolean checkFileNeedDo(File f)
	{
		return true;
	}
	
	/** 1:event,2:condition,3:action */
	private TriggerFuncListData toMakeOneM(String content,int type)
	{
		//先去掉注释行
		StringBuilder sb=new StringBuilder();
		
		String[] arr=content.split("\\n");
		
		for(String v:arr)
		{
			if(!v.contains("//"))
			{
				sb.append(v);
				sb.append("\n");
			}
		}
		
		String str=sb.toString();
		
		Matcher fMatch=_filterReg.matcher(str);
		if(fMatch.find())
		{
			Ctrl.throwError("存在不支持的字符:"+fMatch.group());
			return null;
		}
		
		//check
		TriggerFuncListDataProxy list=readFuncList(str);
		
		list.funcList.forEach(v->
		{
			MethodInfo methodInfo=_makeTool.functionInfoDic.get(v.id);
			int aType=_makeTool.getObjectType(methodInfo.returnType);
			
			switch(type)
			{
				case 1:
				{
					if(aType!=STriggerObjectType.Void)
					{
						Ctrl.throwError("不是event方法");
					}
					
					if(!_makeTool.isEventFunc(methodInfo.name))
					{
						Ctrl.throwError("不是event方法");
					}
				}
					break;
				case 2:
				{
					if(aType!=STriggerObjectType.Boolean)
					{
						Ctrl.throwError("不是condition方法");
					}
				}
					break;
				case 3:
				{
					if(aType!=STriggerObjectType.Void)
					{
						Ctrl.throwError("不是action方法");
					}
					
					if(_makeTool.isEventFunc(methodInfo.name))
					{
						Ctrl.throwError("不是action方法");
					}
				}
					break;
			}
		});
		
		return list.createFuncList();
	}
	
	private TriggerFuncListDataProxy readFuncList(String str)
	{
		TriggerFuncListDataProxy re=new TriggerFuncListDataProxy();
		
		int start=0;
		
		while(true)
		{
			TriggerFuncDataProxy funcP=readOneFuncWhole(str,start);
			
			if(funcP==null)
				break;
			
			re.funcList.add(funcP.createFunc());
			
			start=funcP.endIndex+1;
		}
		
		return re;
	}
	
	/** 将接下来的一个语句读取为list,包括{}或单语句 (一定是void方法) */
	private TriggerFuncListDataProxy readFuncListOneP(String str,int start)
	{
		int nextCharIndex=StringUtils.getNextEnableChar(str,start);
		
		if(nextCharIndex==-1)
			return null;
		
		char c=str.charAt(nextCharIndex);
		
		if(c=='{')
		{
			int anotherIndex=StringUtils.getAnotherIndex(str,'{','}',nextCharIndex);
			
			if(anotherIndex==-1)
			{
				Ctrl.throwError("不该找不到");
				return null;
			}
			
			String cStr=str.substring(nextCharIndex + 1,anotherIndex);
			
			TriggerFuncListDataProxy re=readFuncList(cStr);
			
			if(re==null)
			{
				Ctrl.throwError("不该读取失败");
				return null;
			}
			
			re.endIndex=anotherIndex;
			
			return re;
		}
		else
		{
			TriggerFuncDataProxy funcP=readOneFuncWhole(str,nextCharIndex);
			
			if(funcP==null)
			{
				Ctrl.throwError("不该读取失败");
				return null;
			}
			TriggerFuncListDataProxy re=new TriggerFuncListDataProxy();
			re.funcList.add(funcP.createFunc());
			re.endIndex=funcP.endIndex;
			
			return re;
		}
	}
	
	/** 完整读取一个方法(void方法) */
	private TriggerFuncDataProxy readOneFuncWhole(String str,int start)
	{
		int endIndex=StringUtils.getAnotherIndex(str,'(',')',start);
		
		//int nextEndIndex=str.indexOf(';',start);
		//TODO:这里可做=的赋值
		
		
		if(endIndex==-1)
			return null;
		
		String cc=str.substring(start,endIndex+1);
		
		TriggerFuncDataProxy funcP=readOneFuncSimple(cc);
		
		if(funcP==null)
		{
			Ctrl.throwError("不该找不到方法");
			return null;
		}
		
		switch(funcP.name)
		{
			case "if":
			{
				int nextCharIndex=StringUtils.getNextEnableChar(str,endIndex + 1);
				
				if(nextCharIndex==-1)
				{
					Ctrl.throwError("if需要后续方法");
					return null;
				}
				
				TriggerFuncListDataProxy listP=readFuncListOneP(str,nextCharIndex);
				
				if(listP==null)
				{
					Ctrl.throwError("不该读取失败");
					return null;
				}
				
				funcP.argList.add(listP.createFuncList());
				endIndex=listP.endIndex;
				
				int nextCharIndex2=StringUtils.getNextEnableChar(str,endIndex+1);
				
				//还有
				if(nextCharIndex2!=-1)
				{
					char c2=str.charAt(nextCharIndex2);
					
					if(c2=='e')
					{
						//下一个是else
						if(nextCharIndex2+4<str.length() && str.substring(nextCharIndex2,nextCharIndex2+4).equals("else"))
						{
							nextCharIndex2+=4;
							
							TriggerFuncListDataProxy listP2=readFuncListOneP(str,nextCharIndex2);
							
							if(listP2==null)
							{
								Ctrl.throwError("不该读取失败");
								return null;
							}
							
							funcP.argList.add(listP2.createFuncList());
							endIndex=listP2.endIndex;
						}
					}
				}
			}
				break;
			case "while":
			{
				int nextCharIndex=StringUtils.getNextEnableChar(str,endIndex + 1);
				
				if(nextCharIndex==-1)
				{
					Ctrl.throwError("while需要后续方法");
					return null;
				}
				
				TriggerFuncListDataProxy listP=readFuncListOneP(str,nextCharIndex);
				
				if(listP==null)
				{
					Ctrl.throwError("不该读取失败");
					return null;
				}
				
				funcP.argList.add(listP.createFuncList());
				endIndex=listP.endIndex;
			}
				break;
			//case "forLoop":
			//{
			//	if(str.charAt(endIndex+1)!=';')
			//	{
			//		Ctrl.throwError("不该不是结尾;");
			//	}
			//	else
			//	{
			//		endIndex++;
			//	}
			//
			//	int nextCharIndex=StringUtils.getNextEnableChar(str,endIndex + 1);
			//
			//	if(nextCharIndex==-1)
			//	{
			//		Ctrl.throwError("forLoop需要后续方法");
			//		return null;
			//	}
			//
			//	TriggerFuncListDataProxy listP=readFuncListOneP(str,nextCharIndex);
			//
			//	if(listP==null)
			//	{
			//		Ctrl.throwError("不该读取失败");
			//		return null;
			//	}
			//
			//	funcP.argList.add(listP.createFuncList());
			//	endIndex=listP.endIndex;
			//}
			//	break;
			default:
			{
				if(str.charAt(endIndex+1)!=';')
				{
					Ctrl.throwError("不该不是结尾;");
				}
				else
				{
					endIndex++;
				}
				
			}
				break;
		}
		
		funcP.endIndex=endIndex;
		
		//MethodInfo methodInfo=_makeTool.functionInfoDic.get(funcP.id);
		//
		////不是空方法
		//if(!methodInfo.returnType.equals(_inCode.Void))
		//{
		//	Ctrl.throwError("不该不是空方法");
		//}
		
		return funcP;
	}
	
	//TODO:add read参数，并且指定类型，和调用方法名
	
	
	/** 读取方法主体 */
	private TriggerFuncDataProxy readOneFuncSimple(String str)
	{
		Matcher matcher=_m1Reg.matcher(str);
		
		if(!matcher.find())
		{
			Ctrl.throwError("不该找不到,格式不匹配");
			return null;
		}
		
		String funcName=matcher.group(1);
		
		int fID=_makeTool.functionDefineDic.get(funcName);
		
		if(fID==0)
		{
			Ctrl.throwError("不存在的方法",funcName);
			return null;
		}
		
		TriggerFuncDataProxy pp=new TriggerFuncDataProxy();
		pp.name=funcName;
		pp.id=fID;
		
		MethodInfo methodInfo=_makeTool.functionInfoDic.get(fID);
		
		if(methodInfo==null)
		{
			Ctrl.throwError("不存在的方法2",funcName);
			return null;
		}
		
		int end=matcher.end();
		
		//没了
		if(end==str.length()-1)
		{
			return pp;
		}
		else
		{
			String cc=str.substring(matcher.end(),str.length()-1);
			
			String[] arr=StringUtils.splitEx(cc,'(',')',',');
			
			String v;
			
			switch(funcName)
			{
				case "openTrigger":
				case "closeTrigger":
				case "runTrigger":
				case "runTriggerAbs":
				{
					if(arr.length<1)
					{
						Ctrl.throwError("不能没有参数");
						return null;
					}
					
					v=arr[0];
					
					if(v.length()==0)
					{
						Ctrl.throwError("参数不该为空",funcName);
						return null;
					}
					
					if(!v.endsWith(".class"))
					{
						Ctrl.throwError("需要class类型");
						return null;
					}
					
					String typeStr=v.substring(0,v.length() - 6);
					
					String cName=_inputCls.getImport(typeStr).substring(_inputRootPackge.length()+1);
					
					TriggerInfo info=_indexDic.get(cName);
					
					if(info==null)
					{
						Ctrl.throwError("找不到该trigger信息");
						return null;
					}
					
					//不匹配
					if(_currentData.groupType!=info.groupType || _currentData.groupID!=info.groupID)
					{
						Ctrl.throwError("所调用的trigger不是相同包下");
						return null;
					}
					
					TriggerIntData data=new TriggerIntData();
					data.value=info.id;
					pp.argList.add(data);
					
					return pp;
				}
			}
			
			for(int i=0;i<arr.length;i++)
			{
				v=arr[i].trim();
				
				if(v.length()==0)
				{
					Ctrl.throwError("参数不该为空",funcName);
					return null;
				}
				
				MethodArgInfo argInfo=methodInfo.args.get(i);
				
				int aType=_makeTool.getObjectType(argInfo.type);
				
				//值类型
				if(v.indexOf('(')==-1)
				{
					//TODO:继续解析其他符号
					
					pp.argList.add(getValueData(aType,v));
				}
				//方法类型
				else if(v.startsWith("()->"))
				{
					int nextCharIndex=StringUtils.getNextEnableChar(v,4);
					
					if(v.charAt(nextCharIndex)!='{')
					{
						Ctrl.throwError("Runnable后续应该接{",funcName);
						return null;
					}
					
					TriggerFuncListDataProxy listP=readFuncListOneP(v,nextCharIndex);
					
					if(listP==null)
					{
						Ctrl.throwError("不该读取失败");
						return null;
					}
					
					pp.argList.add(listP.createFuncList());
				}
				else
				{
					TriggerFuncDataProxy funcP=readOneFuncSimple(v);
					
					MethodInfo fInfo=_makeTool.functionInfoDic.get(funcP.id);
					
					int rType=_makeTool.getObjectType(fInfo.returnType);
					
					//类型不匹配
					if(!isTypeMatch(aType,rType))
					{
						Ctrl.throwError("类型不匹配",fInfo.name);
						return null;
					}
					
					pp.argList.add(funcP.createFunc());
				}
			}
		}
		
		return pp;
	}
	
	private boolean isTypeMatch(int needType,int inputType)
	{
		//均不可为空
		if(needType==STriggerObjectType.Void || inputType==STriggerObjectType.Void)
			return false;
		
		//object可任意
		if(needType==STriggerObjectType.Object)
			return true;
		
		return needType==inputType;
	}
	
	private TriggerObjData getValueData(int type,String value)
	{
		value=value.trim();
		
		switch(type)
		{
			case STriggerObjectType.Void:
			{
				Ctrl.throwError("不可为空值");
				return null;
			}
			case STriggerObjectType.Object:
			{
				if(value.equals(_inCode.True))
				{
					TriggerBooleanData data=new TriggerBooleanData();
					data.value=true;
					return data;
				}
				else if(value.equals(_inCode.False))
				{
					TriggerBooleanData data=new TriggerBooleanData();
					data.value=true;
					return data;
				}
				else if(value.startsWith("\""))
				{
					TriggerStringData data=new TriggerStringData();
					data.value=value.substring(1,value.length()-1);//去掉""
					return data;
				}
				else if(value.contains("."))
				{
					TriggerFloatData data=new TriggerFloatData();
					data.value=Float.parseFloat(value);
					return data;
				}
				else
				{
					try
					{
						long vv=Long.parseLong(value);
						
						if(vv>Integer.MAX_VALUE)
						{
							TriggerLongData data=new TriggerLongData();
							data.value=vv;
							return data;
						}
						else
						{
							TriggerIntData data=new TriggerIntData();
							data.value=(int)vv;
							return data;
						}
					}
					catch(Exception e)
					{
						Ctrl.errorLog("变量错误",e);
						//TODO:处理变量问题
					}
				}
			}
			case STriggerObjectType.Boolean:
			{
				TriggerBooleanData data=new TriggerBooleanData();
				data.value=value.equals(_inCode.True) ? true : false;
				return data;
			}
			case STriggerObjectType.Int:
			{
				TriggerIntData data=new TriggerIntData();
				data.value=Integer.parseInt(value);
				return data;
			}
			case STriggerObjectType.Float:
			{
				TriggerFloatData data=new TriggerFloatData();
				data.value=Float.parseFloat(value);
				return data;
			}
			case STriggerObjectType.Long:
			{
				TriggerLongData data=new TriggerLongData();
				data.value=Long.parseLong(value);
				return data;
			}
			case STriggerObjectType.String:
			{
				TriggerStringData data=new TriggerStringData();
				data.value=value.substring(1,value.length()-1);//去掉""
				return data;
			}
			default:
			{
				Ctrl.throwError("不支持的值类型",type);
			}
		}
		
		return null;
	}
	
	
	private class TriggerFuncDataProxy extends TriggerFuncData
	{
		public String name;
		
		public SList<TriggerObjData> argList=new SList<>(k->new TriggerObjData[k]);
		
		public int endIndex=-1;
		
		public TriggerFuncData createFunc()
		{
			TriggerFuncData re=new TriggerFuncData();
			re.id=this.id;
			re.args=argList.toArray();
			return re;
		}
	}
	
	private class TriggerFuncListDataProxy extends TriggerFuncListData
	{
		public SList<TriggerFuncData> funcList=new SList<>(k->new TriggerFuncData[k]);
		
		public int endIndex=-1;
		
		public TriggerFuncListData createFuncList()
		{
			TriggerFuncListData re=new TriggerFuncListData();
			re.funcList=funcList.toArray();
			return re;
		}
	}
	
	private class TriggerInfo
	{
		public int id;
		
		public String name;
		
		public int groupType;
		
		public int groupID;
	}
}
