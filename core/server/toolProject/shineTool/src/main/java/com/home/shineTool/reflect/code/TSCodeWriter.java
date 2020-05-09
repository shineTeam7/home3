package com.home.shineTool.reflect.code;

import com.home.shineTool.constlist.CodeType;

public class TSCodeWriter extends CodeWriter
{
	public TSCodeWriter()
	{
		super(CodeType.TS);
	}
	
	private void toWriteClearOrCreateFrontForEnsureCapacity(String name,String len)
	{
		writeCustom("if(" + name + "!=" + _code.Null + ")");
		writeLeftBrace();
		
		writeCustom(name + ".clear();");
		writeCustom(name + ".ensureCapacity(" + len + ");");
		
		writeElseAndDoubleBrace();
	}
	
	private void toWriteClearOrCreateEnd()
	{
		writeRightBrace();
		writeEmptyLine();
	}
	
	@Override
	public void clearOrCreateArray(String name,String elementType,String len)
	{
		createArray(name,elementType,len);
	}
	
	@Override
	public void writeArraySet(String name,String index,String value)
	{
		writeCustom(name + "[" + index + "]=" + value + ";");
	}
	
	@Override
	public void toWriteForEachArray(String name,String elementName,String elementType,boolean needElementVar)
	{
		toWriteForIAndLeftBrace(name,elementName,_code.getArrayLength(name));
		
		if(needElementVar)
		{
			writeVarCreate(elementName,elementType,name + "[" + elementName + "I]");
		}
		
		//writeCustom("for each(var " + elementName + ":" + elementType + " in " + name + ")");
		//writeLeftBrace();
	}
	
	@Override
	public void createList(String name,String elementType,String len)
	{
		writeVarSet(name,_code.createNewObject(_code.getListType(elementType,true)));
	}
	
	@Override
	public void clearOrCreateList(String name,String elementType,String len)
	{
		toWriteClearOrCreateFrontForEnsureCapacity(name,len);
		createList(name,elementType,len);
		toWriteClearOrCreateEnd();
	}
	
	@Override
	public void writeListPush(String name,String value)
	{
		writeCustom(name + ".add(" + value + ");");
	}
	
	@Override
	public void toWriteForEachList(String name,String elementName,String elementType)
	{
		//writeForEachArray(name,elementName,elementType);
		//writeCustom("for(var "+elementName+" of "+name + ")");
		//writeLeftBrace();
		
		toWriteForIAndLeftBrace(name,elementName,_code.getArrayLength(name));
		writeVarCreate(elementName,elementType,name + "[" + elementName + "I]");
	}
	
	@Override
	public void createSet(String name,String elementType,String len)
	{
		writeVarSet(name,_code.createNewObject(_code.getSetType(elementType,true)));
	}
	
	@Override
	public void clearOrCreateSet(String name,String elementType,String len)
	{
		toWriteClearOrCreateFrontForEnsureCapacity(name,len);
		createSet(name,elementType,len);
		toWriteClearOrCreateEnd();
	}
	
	@Override
	public void writeSetAdd(String name,String value)
	{
		writeCustom(name + ".add(" + value + ");");
	}
	
	@Override
	public void toWriteForEachSet(String name,String elementName,String elementType)
	{
		writeCustom("for(var " + elementName + " of " + name + ".getKeys())");
		writeLeftBrace();
	}
	
	@Override
	public void createMap(String name,String kType,String vType,String len)
	{
		writeVarSet(name,_code.createNewObject(_code.getMapType(kType,vType,true),len));
	}
	
	@Override
	public void clearOrCreateMap(String name,String kType,String vType,String len)
	{
		toWriteClearOrCreateFrontForEnsureCapacity(name,len);
		createMap(name,kType,vType,len);
		toWriteClearOrCreateEnd();
	}
	
	@Override
	public void writeMapAdd(String name,String key,String value)
	{
		writeCustom(name + ".put(" + key + "," + value + ");");
	}
	
	@Override
	public void writeMapAddAll(String name,String value,String kName)
	{
		writeCustom(name + ".putAll(" + value + ");");
	}
	
	@Override
	public void toWriteForEachMapEntry(String name,String kName,String kType,String vName,String vType)
	{
		writeCustom("for(var " + kName + " of " + name + ".getKeys())");
		writeLeftBrace();
		writeVarCreate(vName,vType,name + ".get(" + kName + ")");
	}
	
	@Override
	public void toWriteForEachMapValue(String name,String kType,String vName,String vType)
	{
		writeCustom("for(var " + vName + " of " + name + ".getValues())");
		writeLeftBrace();
	}

	@Override
	protected void toWriteForIAndLeftBrace(String name,String elementName,String len)
	{
		writeCustom("for(var " + elementName + "I=0," + elementName + "Len=" + len + ";" + elementName + "I<" + elementName + "Len;++" + elementName + "I)");
		writeLeftBrace();
	}

	/** i-- */
	@Override
	protected void toWriteForIAndLeftBraceBack(String name,String elementName,String len)
	{
		writeCustom("for(var " + elementName + "I=" + len + "-1;" + elementName + "I>=0;--" + elementName + "I)");
		writeLeftBrace();
	}
	
	
	//queue
	
	@Override
	public void createQueue(String name,String elementType,String len)
	{
		writeVarSet(name,_code.createNewObject(_code.getListType(elementType,true)));
	}
	
	@Override
	public void clearOrCreateQueue(String name,String elementType,String len)
	{
		toWriteClearOrCreateFrontForEnsureCapacity(name,len);
		createList(name,elementType,len);
		toWriteClearOrCreateEnd();
	}
	
	@Override
	public void writeQueueOffer(String name,String value)
	{
		writeCustom(name + ".add(" + value + ");");
	}
	
	@Override
	public void toWriteForEachQueue(String name,String elementName,String elementType)
	{
		toWriteForIAndLeftBrace(name,elementName,_code.getArrayLength(name));
		writeVarCreate(elementName,elementType,name + "[" + elementName + "I]");
	}
}