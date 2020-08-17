package com.home.shineTool.reflect.code;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.pool.StringBuilderPool;
import com.home.shine.utils.StringUtils;
import com.home.shineTool.constlist.CodeType;
import com.home.shineTool.constlist.VarType;
import com.home.shineTool.reflect.MethodInfo;
import com.home.shineTool.reflect.cls.ClassInfo;
import com.home.shineTool.global.ShineToolSetting;

/** 方法代码书写器 */
public abstract class CodeWriter
{
	/** 回车 */
	public static final String Enter=ClassInfo.Enter;
	
	protected StringBuilder _sb;
	
	private int _codeType;
	
	protected CodeInfo _code;
	
	protected ClassInfo _cls;
	
	protected int _off=0;
	
	private int _beginIndex;
	
	//empty
	
	private int _emptyLen;
	
	public CodeWriter(int type)
	{
		_codeType=type;
		_code=CodeInfo.getCode(_codeType);
		_cls=_code.getCls();
		_sb=new StringBuilder();
		
		//开始标记
	}
	
	public static CodeWriter createByCls(ClassInfo cls)
	{
		CodeWriter re=createByType(cls.getCodeType());
		re._cls=cls;
		return re;
	}
	
	/** 根据类型创建(自动写begin) */
	public static CodeWriter createByType(int codeType)
	{
		CodeWriter writer=null;
		
		switch(codeType)
		{
			case CodeType.Java:
			{
				writer=new JavaCodeWriter();
			}
				break;
			case CodeType.AS3:
			{
				writer=new AS3CodeWriter();
			}
				break;
			case CodeType.CS:
			{
				writer=new CSCodeWriter();
			}
				break;
			case CodeType.TS:
			{
				writer=new TSCodeWriter();
			}
				break;
			default:
			{
				Ctrl.throwError("不支持");
			}
		}
		
		
		writer.writeBegin();
		writer._emptyLen=writer._sb.length();
		
		return writer;
	}
	
	public int getCodeType()
	{
		return _codeType;
	}
	
	public CodeInfo getCode()
	{
		return _code;
	}
	
	/** 转化String */
	public String toString()
	{
		return _sb.toString();
	}
	
	/** 获取字符串构建器 */
	public StringBuilder getBuilder()
	{
		return _sb;
	}
	
	//	/** 只加字符 */
	//	public void append(String str)
	//	{
	//		_sb.append(str);
	//	}
	
	/** 是否什么都没写 */
	public boolean isEmpty()
	{
		return _sb.length()==_emptyLen;
	}
	
	/** 插入到开始位置(为super) */
	public void writeToBeginIndex(String str)
	{
		StringBuilder sb=new StringBuilder();
		_cls.writeMethodTab(sb,0);
		sb.append(str);
		sb.append(Enter);
		
		_sb.insert(_beginIndex,sb.toString());
	}
	
	/** 写方法开始(,"}") */
	public void writeBegin()
	{
		_sb.append("{");
		_sb.append(Enter);
		
		_beginIndex=_sb.length();
	}
	
	/** 写方法结束(-1,"}") */
	public void writeEnd()
	{
		//什么都没写过
		if(isEmpty())
		{
			//补一空行
			writeEmptyLine();
		}
		
		_cls.writeMethodTab(_sb,-1);
		_sb.append("}");
		_off=0;
	}
	
	/** 写方法结束(-1,"}")，并且返回string */
	public String writeEndAndString()
	{
		writeEnd();
		return toString();
	}
	
	/** 写左边大括号(右缩进) */
	public void writeLeftBrace()
	{
		_cls.writeMethodTab(_sb,_off);
		_sb.append("{");
		_sb.append(Enter);
		_off++;
	}
	
	/** 写右边大括号(右缩进) */
	public void writeRightBrace()
	{
		_off--;
		_cls.writeMethodTab(_sb,_off);
		_sb.append("}");
		_sb.append(Enter);
	}
	
	/** 写右边大括号(右缩进,不加回车) */
	public void writeRightBraceWithOutEnter()
	{
		_off--;
		_cls.writeMethodTab(_sb,_off);
		_sb.append("}");
	}
	
	/** 写当前缩进 */
	public void writeOff()
	{
		_cls.writeMethodTab(_sb,_off);
	}
	
	/** 书写回车 */
	public void writeEnter()
	{
		_sb.append(Enter);
	}
	
	/** 写空行 */
	public void writeEmptyLine()
	{
		_cls.writeMethodTab(_sb,_off);
		_sb.append(Enter);
	}
	
	/** 写自定义行 */
	public void writeCustom(String content)
	{
		_cls.writeMethodTab(_sb,_off);
		_sb.append(content);
		_sb.append(Enter);
	}
	
	/** 写自定义行带偏差 */
	public void writeCustomWithOff(int off,String content)
	{
		_cls.writeMethodTab(_sb,_off + off);
		_sb.append(content);
		_sb.append(Enter);
	}
	
	/** 写对象函数 */
	public void writeObjFuc(String obj,String func,String...args)
	{
		writeCustom(obj+"."+func+"("+ StringUtils.join(args,",")+");");
	}
	
	/** 写静态函数 */
	public void writeStaticFuc(String func,String...args)
	{
		writeCustom(func+"("+ StringUtils.join(args,",")+");");
	}
	
	/** 写空返回(return) */
	public void writeReturnVoid()
	{
		writeCustom("return;");
	}
	
	/** 写返回空(return null) */
	public void writeReturnNull()
	{
		writeCustom("return "+_code.Null+";");
	}
	
	public void writeReturnVoid(int off)
	{
		writeCustomWithOff(off,"return;");
	}
	
	public void writeReturnBoolean(boolean re)
	{
		writeCustom("return "+(re ? _code.True : _code.False)+";");
	}
	
	public void writeReturnBoolean(int off,boolean re)
	{
		writeCustomWithOff(off,"return "+(re ? _code.True : _code.False)+";");
	}
	
	public void writeReturn(String str)
	{
		writeCustom("return "+str+";");
	}
	
	/** 写if(不带left brace) */
	public void writeIf(String str)
	{
		writeCustom("if("+str+")");
	}
	
	/** 写if(带left brace) */
	public void writeIfAndLeftBrace(String str)
	{
		writeCustom("if("+str+")");
		writeLeftBrace();
	}
	
	/** 写else */
	public void writeElseAndDoubleBrace()
	{
		writeRightBrace();
		writeCustom("else");
		writeLeftBrace();
	}
	
	/** 写else if */
	public void writeElseIfAndDoubleBrace(String str)
	{
		writeRightBrace();
		writeCustom("else if("+str+")");
		writeLeftBrace();
	}
	
	//--语句--//
	
	/** 写变量赋值 */
	public void writeVarSet(String name,String value)
	{
		writeCustom(name + "=" + value + ";");
	}
	
	/** 写变量创建 */
	public void writeVarCreate(String name,String type)
	{
		writeVarCreate(name,type,"");
	}
	
	//	/** 写变量创建,跟默认值 */
	//	public void writeVarCreateOrNull(String name,String type)
	//	{
	//		String t=_code.getDefaultValue(type);
	//
	//		if(t.equals(_code.Null))
	//		{
	//			writeVarCreate(name,type,t);
	//		}
	//		else
	//		{
	//			writeVarCreate(name,type,"");
	//		}
	//	}
	
	/** 写变量创建,跟默认值 */
	public void writeVarCreateWithDefault(String name,String type)
	{
		writeVarCreate(name,type,_code.getDefaultValue(type));
	}
	
	/** 写变量创建(T a=?;) */
	public void writeVarCreate(String name,String type,String defaultValue)
	{
		writeCustom(_code.getVarCreate(name,type,defaultValue));
	}
	
	/** 写读基础类型 */
	public void writeReadBaseType(int type,String name)
	{
		writeVarSet(name,_code.getReadBaseVarType(type));
	}
	
	/** 写写基础类型 */
	public void writeWriteBaseType(int type,String name)
	{
		switch(type)
		{
			case VarType.Boolean:
			{
				writeObjFuc(ShineToolSetting.bytesSteamVarName,"writeBoolean",name);
			}
				break;
			case VarType.Byte:
			{
				writeObjFuc(ShineToolSetting.bytesSteamVarName,"writeByte",name);
			}
				break;
			case VarType.UByte:
			{
				writeObjFuc(ShineToolSetting.bytesSteamVarName,"writeUnsignedByte",name);
			}
				break;
			case VarType.Short:
			{
				writeObjFuc(ShineToolSetting.bytesSteamVarName,"writeShort",name);
			}
				break;
			case VarType.UShort:
			{
				writeObjFuc(ShineToolSetting.bytesSteamVarName,"writeUnsignedShort",name);
			}
				break;
			case VarType.Int:
			{
				writeObjFuc(ShineToolSetting.bytesSteamVarName,"writeInt",name);
			}
				break;
			case VarType.Long:
			{
				writeObjFuc(ShineToolSetting.bytesSteamVarName,"writeLong",name);
			}
				break;
			case VarType.Float:
			{
				writeObjFuc(ShineToolSetting.bytesSteamVarName,"writeFloat",name);
			}
				break;
			case VarType.Double:
			{
				writeObjFuc(ShineToolSetting.bytesSteamVarName,"writeDouble",name);
			}
				break;
			case VarType.String:
			{
				writeObjFuc(ShineToolSetting.bytesSteamVarName,"writeUTF",name);
			}
				break;
		}
	}
	
	/** 写拷贝基础类型 */
	public void writeCopyBaseType(int type,String name,String value)
	{
		writeVarSet(name,value);
	}
	
	/** 写for循环头(带LeftBrace了) */
	public void writeForLoopTimes(String iName,String len)
	{
		writeCustom("for(" + _code.getVarCreate(iName,_code.Int,"0") + iName + "<" + len + ";++" + iName + ")");
		writeLeftBrace();
	}
	
	/** 写for循环头(带LeftBrace了,倒序) */
	public void writeForLoopTimesBack(String iName,String len)
	{
		writeCustom("for(" + _code.getVarCreate(iName,_code.Int,len + "-1") + iName + ">=0;--" + iName + ")");
		writeLeftBrace();
	}
	
	/** 写字节长度 */
	public void writeBytesLen(String len)
	{
		writeCustom(ShineToolSetting.bytesSteamVarName + ".writeLen(" + len + ");");
	}
	
	//data部分
	
	/** 写数据完整读(为继承) */
	public void writeDataReadFullForExtends(String fieldName,String nameT,String type,boolean needUpgrade)
	{
		//需要考虑升级
		if(needUpgrade)
		{
			_cls.addImport(ShineToolSetting.dataBaseQName);
			writeVarCreate(nameT,ShineToolSetting.dataBaseName,ShineToolSetting.bytesSteamVarName + ".readDataFullNotNull()");
			
			writeIfAndLeftBrace(nameT+"!="+_code.Null);
			
			writeIfAndLeftBrace( _code.getVarTypeIs(nameT,type));
			writeVarSet(fieldName,_code.getVarTypeTrans(nameT,type));
			writeElseAndDoubleBrace();
			writeVarSet(fieldName,_code.createNewObject(type));
			
			//能获取类
			if(CodeType.canGetClass(_code.getCodeType()))
			{
				writeIfAndLeftBrace("!(" + _code.getClsTypeIsAssignableFrom(_code.getVarClsType(nameT),_code.getClassClsType(type)) + ")");
				writeObjFuc(ShineToolSetting.bytesSteamVarName,"throwTypeReadError",_code.getClassClsType(type),_code.getVarClsType(nameT));
				writeRightBrace();
			}
			
			writeObjFuc(fieldName,"shadowCopy",nameT);
			writeRightBrace();
			
			writeElseAndDoubleBrace();
			writeVarSet(fieldName,_code.Null);
			writeRightBrace();
		}
		else
		{
			writeVarSet(fieldName,_code.getVarTypeTrans(ShineToolSetting.bytesSteamVarName + ".readDataFullNotNull()",type));
		}
	}
	
	/** 写数据完整读(不考虑继承) */
	public void writeDataReadFull(String data)
	{
		writeCustom(data + ".readBytesFull(" + ShineToolSetting.bytesSteamVarName + ");");
	}
	
	/** 写数据简版读(为继承) */
	public void writeDataReadSimpleForExtends(String data,String type)
	{
		writeVarSet(data,_code.getVarTypeTrans(ShineToolSetting.bytesSteamVarName + ".readDataSimpleNotNull()",type));
	}
	
	/** 写数据简版读(不考虑继承) */
	public void writeDataReadSimple(String data)
	{
		writeCustom(data + ".readBytesSimple(" + ShineToolSetting.bytesSteamVarName + ");");
	}
	
	/** 写完整数据(为继承) */
	public void writeDataFullForExtends(String data)
	{
		writeCustom(ShineToolSetting.bytesSteamVarName + ".writeDataFullNotNull(" + data + ");");
	}
	
	/** 写完整数据(不考虑继承) */
	public void writeDataFull(String data)
	{
		writeCustom(data + ".writeBytesFull(" + ShineToolSetting.bytesSteamVarName + ");");
	}
	
	/** 写简版数据(为继承) */
	public void writeDataSimpleForExtends(String data)
	{
		writeCustom(ShineToolSetting.bytesSteamVarName + ".writeDataSimpleNotNull(" + data + ");");
	}
	
	/** 写简版数据(不考虑继承) */
	public void writeDataSimple(String data)
	{
		writeCustom(data + ".writeBytesSimple(" + ShineToolSetting.bytesSteamVarName + ");");
	}
	
	
	//--模板--//
	
	/** 写获取读字节 */
	public void writeStartReadObj()
	{
		writeCustom("stream.startReadObj();");
	}
	
	/** 写获取读完字节 */
	public void writeDisReadBytes()
	{
		writeCustom("stream.endReadObj();");
	}
	
	/** 写获取写字节 */
	public void writeGetWriteBytes()
	{
		writeCustom("stream.startWriteObj();");
	}
	
	/** 写获取写完字节 */
	public void writeDisWriteBytes()
	{
		writeCustom("stream.endWriteObj();");
	}
	
	/** 写获取写完字节 */
	public void writeNullObjError(String fieldName)
	{
		writeCustom("nullObjError(\""+fieldName+"\");");
	}
	
	//--集合操作--//
	
	//Array

	/** 创建数组 */
	public void createArray(String name,String elementType,String len)
	{
		writeVarSet(name,_code.createNewArray(elementType,len));
	}
	
	/** 清空或创建数组 */
	public abstract void clearOrCreateArray(String name,String elementType,String len);

	/** 数组添加 */
	public abstract void writeArraySet(String name,String index,String value);

	/** 数组遍历(写left brace) */
	protected abstract void toWriteForEachArray(String name,String elementName,String elementType,boolean needElementVar);
	
	public void writeForEachArray(String name,String elementName,String elementType,boolean needElementVar)
	{
		//writeIfAndLeftBrace(_code.isArrayNotEmpty(name));
		toWriteForEachArray(name,elementName,elementType,needElementVar);
	}
	
	/** 数组遍历(写left brace) */
	public void writeForEachArray(String name,String elementName,String elementType)
	{
		writeForEachArray(name,elementName,elementType,true);
	}
	
	/** Array遍历结束(写right brace) */
	public void writeForEachArrayEnd()
	{
		writeForEachArrayEnd("");
	}
	
	/** Array遍历结束(写right brace) */
	public void writeForEachArrayEnd(String elementType)
	{
		//writeRightBrace();
		writeRightBrace();
	}
	
	//List

	/** 创建List */
	public abstract void createList(String name,String elementType,String len);

	/** 清空或创建List */
	public abstract void clearOrCreateList(String name,String elementType,String len);

	/** List push */
	public abstract void writeListPush(String name,String value);

	/** List遍历(写left brace) */
	protected abstract void toWriteForEachList(String name,String elementName,String elementType);
	
	public void writeForEachList(String name,String elementName,String elementType)
	{
		writeIfAndLeftBrace(_code.isCollectionNotEmpty(name));
		toWriteForEachList(name,elementName,elementType);
	}
	
	/** List遍历结束(写right brace) */
	public void writeForEachListEnd()
	{
		writeForEachListEnd("");
	}

	/** List遍历结束(写right brace) */
	public void writeForEachListEnd(String elementType)
	{
		writeRightBrace();
		writeRightBrace();
	}
	
	//Set

	/** 创建Set */
	public abstract void createSet(String name,String elementType,String len);

	/** 清空或创建Set */
	public abstract void clearOrCreateSet(String name,String elementType,String len);

	/** Set add */
	public abstract void writeSetAdd(String name,String value);

	/** Set遍历(写left brace) */
	protected abstract void toWriteForEachSet(String name,String elementName,String elementType);
	
	public void writeForEachSet(String name,String elementName,String elementType)
	{
		writeIfAndLeftBrace(_code.isCollectionNotEmpty(name));
		toWriteForEachSet(name,elementName,elementType);
	}
	
	/** Set遍历结束(写right brace) */
	protected void toWriteForEachSetEnd(String elementType)
	{
		writeRightBrace();
	}
	
	/** Set遍历结束(写right brace) */
	public void writeForEachSetEnd()
	{
		writeForEachSetEnd("");
	}
	
	/** Set遍历结束(写right brace) */
	public void writeForEachSetEnd(String elementType)
	{
		toWriteForEachSetEnd(elementType);
		writeRightBrace();
	}
	
	//Map

	/** 创建Map */
	public abstract void createMap(String name,String kType,String vType,String len);

	/** 清空或创建Map */
	public abstract void clearOrCreateMap(String name,String kType,String vType,String len);

	/** Map add */
	public abstract void writeMapAdd(String name,String key,String value);

	/** Map addAll */
	public abstract void writeMapAddAll(String name,String value,String kName);

	/** Map遍历(写left brace) */
	protected abstract void toWriteForEachMapEntry(String name,String kName,String kType,String vName,String vType);
	
	public void writeForEachMapEntry(String name,String kName,String kType,String vName,String vType)
	{
		writeIfAndLeftBrace(_code.isCollectionNotEmpty(name));
		toWriteForEachMapEntry(name,kName,kType,vName,vType);
	}

	/** Map遍历value(写left brace) */
	protected abstract void toWriteForEachMapValue(String name,String kType,String vName,String vType);
	
	public void writeForEachMapValue(String name,String kType,String vName,String vType)
	{
		writeIfAndLeftBrace(_code.isCollectionNotEmpty(name));
		toWriteForEachMapValue(name,kType,vName,vType);
	}
	
	/** Map遍历结束(写right brace) */
	protected void toWriteForEachMapEnd(String kType,String vType)
	{
		writeRightBrace();
	}
	
	public void writeForEachMapEnd()
	{
		writeForEachMapEnd("","");
	}
	
	/** Map遍历结束(写right brace) */
	public void writeForEachMapEnd(String kType,String vType)
	{
		toWriteForEachMapEnd(kType,vType);
		writeRightBrace();
	}
	
	//Queue
	
	/** 创建List */
	public abstract void createQueue(String name,String elementType,String len);
	
	/** 清空或创建List */
	public abstract void clearOrCreateQueue(String name,String elementType,String len);
	
	/** List push */
	public abstract void writeQueueOffer(String name,String value);
	
	/** List遍历(写left brace) */
	protected abstract void toWriteForEachQueue(String name,String elementName,String elementType);
	
	public void writeForEachQueue(String name,String elementName,String elementType)
	{
		writeIfAndLeftBrace(_code.isCollectionNotEmpty(name));
		toWriteForEachQueue(name,elementName,elementType);
	}
	
	/** List遍历结束(写right brace) */
	public void writeForEachQueueEnd()
	{
		writeForEachQueueEnd("");
	}
	
	/** List遍历结束(写right brace) */
	public void writeForEachQueueEnd(String elementType)
	{
		writeRightBrace();
		writeRightBrace();
	}
	
	//其他
	
	protected void toWriteForIAndLeftBrace(String name,String elementName,String len)
	{
		writeCustom("for(int " + elementName + "I=0," + elementName + "Len=" + len + ";" + elementName + "I<" + elementName + "Len;++" + elementName + "I)");
		writeLeftBrace();
	}
	
	/** i-- */
	protected void toWriteForIAndLeftBraceBack(String name,String elementName,String len)
	{
		writeCustom("for(int " + elementName + "I=" + len + "-1;" + elementName + "I>=0;--" + elementName + "I)");
		writeLeftBrace();
	}
	
	/** 写创建对象 */
	public void createObject(String name,String type,String... args)
	{
		writeVarSet(name,_code.createNewObject(type,args));
	}
	
	/** 写创建对象如果为空 */
	public void createObjectIfNull(String name,String type,String... args)
	{
		writeCustom("if(" + name + "==" + _code.Null + ")");
		writeLeftBrace();
		createObject(name,type,args);
		writeRightBrace();
	}
	
	/** 写switch */
	public void writeSwitch(String name)
	{
		writeCustom("switch("+name+")");
		writeLeftBrace();
	}
	
	/** 写case */
	public void writeCase(String name)
	{
		writeCustom("case "+name+":");
		writeLeftBrace();
	}
	
	public void endCase()
	{
		writeRightBrace();
		writeCustomWithOff(1,"break;");
	}
	
	public void endSwitch()
	{
		writeRightBrace();
	}
	
	public void writeTry()
	{
		writeCustom("try ");
		writeLeftBrace();
	}
	
	public void writeCatch()
	{
		writeRightBrace();
		writeCustom("catch("+"Exception"+" "+"e"+")");
		writeLeftBrace();
	}
	
	public void writeEndTry()
	{
		writeRightBrace();
	}
	
	/** 写基类 */
	public void writeSuperMethod(MethodInfo method)
	{
		StringBuilder sb=StringBuilderPool.create();
		
		sb.append(_code.Super);
		sb.append(".");
		sb.append(method.name);
		sb.append("(");
		
		for(int i=0;i<method.args.size();i++)
		{
			if(i>0)
				sb.append(",");
			
			sb.append(method.args.get(i).name);
		}
		
		sb.append(")");
		sb.append(";");
		
		writeCustom(StringBuilderPool.releaseStr(sb));
	}
	
	//--writer--//
	
	public void writeWriterTabs()
	{
		writeCustom("writer.writeTabs();");
	}
	
	public void writeWriterEnter()
	{
		writeCustom("writer.writeEnter();");
	}
	
	public void writeWriterLeftBrace()
	{
		writeCustom("writer.writeLeftBrace();");
	}
	
	public void writeWriterRightBrace()
	{
		writeCustom("writer.writeRightBrace();");
	}
	
	public void writeWriterAppend(String content)
	{
		if(_codeType==CodeType.CS)
		{
			writeCustom("writer.sb.Append("+content+");");
		}
		else
		{
			writeCustom("writer.sb.append("+content+");");
		}
	}
	
	public void writeShouldDo(String mark)
	{
		writeCustom("Ctrl.warnLog(\"function "+mark+",need implement\");");
	}
}
