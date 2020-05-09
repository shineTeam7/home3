package com.home.shineTool.reflect.code;

import com.home.shineTool.constlist.CodeType;
import com.home.shineTool.global.ShineToolSetting;

public class AS3CodeWriter extends CodeWriter
{
	public AS3CodeWriter()
	{
		super(CodeType.AS3);
	}

	@Override
	public void clearOrCreateArray(String name,String elementType,String len)
	{
		writeCustom("if(" + name + "!=" + _code.Null + ")");
		writeLeftBrace();
		writeCustom(name + ".length=0;");
		writeElseAndDoubleBrace();
		createArray(name,elementType,len);
		writeRightBrace();
	}
	
	@Override
	public void writeArraySet(String name,String index,String value)
	{
		writeCustom(name + "[" + index + "]=" + value + ";");
	}
	
	@Override
	public void toWriteForEachArray(String name,String elementName,String elementType,boolean needElementVar)
	{
		writeCustom("for each(var " + elementName + ":" + elementType + " in " + name + ")");
		writeLeftBrace();
	}

	@Override
	public void createList(String name,String elementType,String len)
	{
		createArray(name,elementType,len);
	}

	@Override
	public void clearOrCreateList(String name,String elementType,String len)
	{
		clearOrCreateArray(name,elementType,len);
	}

	@Override
	public void writeListPush(String name,String value)
	{
		writeCustom(name + ".push(" + value + ");");
	}

	@Override
	public void toWriteForEachList(String name,String elementName,String elementType)
	{
		writeForEachArray(name,elementName,elementType);
	}

	@Override
	public void createSet(String name,String elementType,String len)
	{
		writeVarSet(name,_code.createNewObject(_code.getSetType(elementType,true)));
	}

	@Override
	public void clearOrCreateSet(String name,String type,String len)
	{
		createSet(name,type,len);
	}

	@Override
	public void writeSetAdd(String name,String value)
	{
		writeCustom(name + ".set(" + value + ");");
	}

	@Override
	public void toWriteForEachSet(String name,String elementName,String elementType)
	{
		writeCustom("for(var " + elementName + ":" + elementType + " in " + name + ")");
		writeLeftBrace();
	}

	@Override
	public void createMap(String name,String kType,String vType,String len)
	{
		writeVarSet(name,_code.createNewObject(_code.getMapType(kType,vType,true)));
	}

	@Override
	public void clearOrCreateMap(String name,String kType,String vType,String len)
	{
		createMap(name,kType,vType,len);
	}

	@Override
	public void writeMapAdd(String name,String key,String value)
	{
		writeCustom(name + ".set(" + key + "," + value + ");");
	}
	
	@Override
	public void writeMapAddAll(String name,String value,String kName)
	{
		writeCustom("for(var " + kName + ":" + "*" + " in " + value + ")");
		writeLeftBrace();
		writeCustom(name + ".set(" + kName + "," + value + ".get(" + kName + "));");
		writeRightBrace();
	}

	@Override
	public void toWriteForEachMapEntry(String name,String kName,String kType,String vName,String vType)
	{
		writeCustom("for(var " + kName + ":" + kType + " in " + name + ")");
		writeLeftBrace();
		writeVarCreate(vName,vType,name + ".get(" + kName + ")");
	}
	
	@Override
	public void toWriteForEachMapValue(String name,String kType,String vName,String vType)
	{
		writeCustom("for each(var " + vName + ":" + vType + " in " + name + ")");
		writeLeftBrace();
	}
	
	//queue
	
	@Override
	public void createQueue(String name,String elementType,String len)
	{
		createArray(name,elementType,len);
	}
	
	@Override
	public void clearOrCreateQueue(String name,String elementType,String len)
	{
		clearOrCreateArray(name,elementType,len);
	}
	
	@Override
	public void writeQueueOffer(String name,String value)
	{
		writeCustom(name + ".push(" + value + ");");
	}
	
	@Override
	public void toWriteForEachQueue(String name,String elementName,String elementType)
	{
		writeForEachArray(name,elementName,elementType);
	}
}
