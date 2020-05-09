package com.home.shineTool.reflect.code;

import com.home.shineTool.constlist.CodeType;

public class CSCodeWriter extends CodeWriter
{
	public CSCodeWriter()
	{
		super(CodeType.CS);
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
	
	/** 是否是基础集合类型 */
	private boolean isBaseCollectionType(String elementType)
	{
		return _code.isInt(elementType) || _code.isLong(elementType);
	}
	
	@Override
	public void clearOrCreateArray(String name,String elementType,String len)
	{
		writeIfAndLeftBrace(name+"=="+_code.Null+" || "+_code.getArrayLength(name)+"!="+len);
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
		toWriteForIAndLeftBrace(name,elementName,_code.getArrayLength(name));
		
		if(needElementVar)
		{
			writeVarCreate(elementName,elementType,name + "[" + elementName + "I]");
		}
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
		String t=_code.getElementType(elementType);
		
		writeVarCreate(elementName + "Values",t + "[]",name + ".getValues()");
		toWriteForIAndLeftBrace(name,elementName,name + ".length()");
		writeVarCreate(elementName,t,elementName + "Values[" + elementName + "I]");
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
		String t=_code.getElementType(elementType);
		
		if(isBaseCollectionType(elementType))
		{
			writeVarCreate(elementName + "FreeValue",t,name + ".getFreeValue()");
			writeVarCreate(elementName + "Keys",t + "[]",name + ".getKeys()");
			toWriteForIAndLeftBraceBack(name,elementName,elementName + "Keys.Length");
			writeVarCreate(elementName,t,elementName + "Keys[" + elementName + "I]");
			writeCustom("if(" + elementName + "!=" + elementName + "FreeValue" + ")");
			writeLeftBrace();
		}
		else
		{
			writeVarCreate(elementName + "Keys",t + "[]",name + ".getKeys()");
			toWriteForIAndLeftBraceBack(name,elementName,elementName + "Keys.Length");
			writeCustom("if(" + elementName + "Keys[" + elementName + "I]" + "!=null)");
			writeLeftBrace();
			writeVarCreate(elementName,t,elementName + "Keys[" + elementName + "I]");
		}
	}
	
	@Override
	public void toWriteForEachSetEnd(String elementType)
	{
		writeRightBrace();
		writeRightBrace();
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
		if(isBaseCollectionType(kType))
		{
			String kt=_code.getElementType(kType);
			String vt=vType;
			
			writeVarCreate(kName + "FreeValue",kt,name + ".getFreeValue()");
			writeVarCreate(kName + "Keys",kt + "[]",name + ".getKeys()");
			writeVarCreate(vName + "Values",vt + "[]",name + ".getValues()");
			toWriteForIAndLeftBraceBack(name,kName,kName + "Keys.Length");
			
			writeVarCreate(kName,kt,kName + "Keys[" + kName + "I]");
			writeCustom("if(" + kName + "!=" + kName + "FreeValue" + ")");
			writeLeftBrace();
			
			writeVarCreate(vName,vt,vName + "Values[" + kName + "I]");
		}
		else
		{
			String kt=_code.getElementType(kType);
			String vt=vType;
			
			writeVarCreate(kName + "Keys",kt + "[]",name + ".getKeys()");
			writeVarCreate(vName + "Values",vt + "[]",name + ".getValues()");
			toWriteForIAndLeftBraceBack(name,kName,kName + "Keys.Length");
			
			writeVarCreate(kName,kt,kName + "Keys[" + kName + "I]");
			writeCustom("if(" + kName + "!=" + _code.Null + ")");
			writeLeftBrace();
			
			writeVarCreate(vName,vt,vName + "Values[" + kName + "I]");
		}
	}

	@Override
	public void toWriteForEachMapValue(String name,String kType,String vName,String vType)
	{
		if(isBaseCollectionType(kType))
		{
			String vt=vType;
			
			writeVarCreate(vName + "Values",vt + "[]",name + ".getValues()");
			toWriteForIAndLeftBraceBack(name,vName,vName + "Values.Length");
			writeVarCreate(vName,vt,vName + "Values[" + vName + "I]");
			writeCustom("if(" + vName + "!=null)");
			writeLeftBrace();
		}
		else
		{
			String vt=vType;
			
			writeVarCreate(vName + "Values",vt + "[]",name + ".getValues()");
			toWriteForIAndLeftBraceBack(name,vName,vName + "Values.Length");
			writeVarCreate(vName,vt,vName + "Values[" + vName + "I]");
			writeCustom("if(" + vName + "!=null)");
			writeLeftBrace();
		}
	}
	
	@Override
	protected void toWriteForEachMapEnd(String kType,String vTypw)
	{
		writeRightBrace();
		writeRightBrace();
	}
	
	//queue
	
	@Override
	public void createQueue(String name,String elementType,String len)
	{
		writeVarSet(name,_code.createNewObject(_code.getQueueType(elementType,true)));
	}
	
	@Override
	public void clearOrCreateQueue(String name,String elementType,String len)
	{
		toWriteClearOrCreateFrontForEnsureCapacity(name,len);
		createQueue(name,elementType,len);
		toWriteClearOrCreateEnd();
	}
	
	@Override
	public void writeQueueOffer(String name,String value)
	{
		writeCustom(name + ".offer(" + value + ");");
	}
	
	@Override
	public void toWriteForEachQueue(String name,String elementName,String elementType)
	{
		String t=_code.getElementType(elementType);
		
		writeVarCreate(elementName + "Values",t + "[]",name + ".getValues()");
		writeVarCreate(elementName + "Mark",_code.Int,name + ".getMark()");
		writeVarCreate(elementName + "Start",_code.Int,name + ".getStart()");
		toWriteForIAndLeftBrace(name,elementName,name + ".length()");
		writeVarCreate(elementName,t,elementName + "Values[("+elementName+"I+"+elementName+"Start)&" +elementName + "Mark" +"]");
	}
}
