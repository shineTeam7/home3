package com.home.shineTool.reflect.code;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.utils.StringUtils;
import com.home.shineTool.constlist.CodeType;
import com.home.shineTool.global.ShineToolSetting;

public class JavaCodeWriter extends CodeWriter
{
	public JavaCodeWriter()
	{
		super(CodeType.Java);
		
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
	
	/** 写循环结束 */
	@SuppressWarnings("unused")
	private void toWriteForEachEnd()
	{
		_off--;
		_cls.writeMethodTab(_sb,_off);
		_sb.append("}");
		_sb.append(")");
		_sb.append(";");
		_sb.append(Enter);
	}
	
	private String getUName(String name)
	{
		int index;
		
		if((index=name.lastIndexOf('.'))!=-1)
		{
			return name.substring(index+1,name.length());
		}
		
		return name;
	}
	
	/** 是否是基础集合类型 */
	private boolean isBaseCollectionType(String elementType)
	{
		return _code.isInt(elementType) || _code.isLong(elementType);
	}
	
	/** 替换掉泛型 */
	private String cutVType(String str)
	{
		int index=str.indexOf('<');
		
		if(index==-1)
			return str;
		
		int end=StringUtils.getAnotherIndex(str,'<','>',index);
		
		if(end==-1)
		{
			Ctrl.throwError("不该找不到另一半");
			return str;
		}
		
		return str.substring(0,index)+str.substring(end+1);
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
		if(ShineToolSetting.isSCollectionSignedArrayCreate && !isBaseCollectionType(elementType))
		{
			//String uName=getUName(name);
			
			writeVarSet(name,_code.createNewObject(_code.getListType(elementType,true),cutVType(elementType)+"[]::new",len));
		}
		else
		{
			writeVarSet(name,_code.createNewObject(_code.getListType(elementType,true),len));
		}
	}
	
	@Override
	public void clearOrCreateList(String name,String elementType,String len)
	{
		writeCustom("if(" + name + "!=" + _code.Null + ")");
		writeLeftBrace();
		writeCustom(name + ".clear();");
		
		writeCustom(name + ".ensureCapacity(" + len + ");");
		
		writeElseAndDoubleBrace();
		
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
		if(ShineToolSetting.isSCollectionSignedArrayWrite || isBaseCollectionType(elementType))
		{
			String t=_code.getElementType(elementType);
			
			writeVarCreate(elementName + "Values",t + "[]",name + ".getValues()");
			toWriteForIAndLeftBrace(name,elementName,name + ".length()");
			writeVarCreate(elementName,t,elementName + "Values[" + elementName + "I]");
		}
		else
		{
			String t=_code.getElementType(elementType);
			
			writeVarCreate(elementName + "Values",_code.Object + "[]",name + ".getValues()");
			toWriteForIAndLeftBrace(name,elementName,name + ".length()");
			writeVarCreate(elementName,t,_code.getVarTypeTrans(elementName + "Values[" + elementName + "I]",t));
		}
	}
	
	@Override
	public void createSet(String name,String elementType,String len)
	{
		if(ShineToolSetting.isSCollectionSignedArrayCreate && !isBaseCollectionType(elementType))
		{
			//String uName=getUName(name);
			
			writeVarSet(name,_code.createNewObject(_code.getSetType(elementType,true),cutVType(elementType)+"[]::new",len));
		}
		else
		{
			writeVarSet(name,_code.createNewObject(_code.getSetType(elementType,true),len));
		}
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
		if(isBaseCollectionType(elementType))
		{
			String t=_code.getElementType(elementType);
			writeVarCreate(elementName + "FreeValue",t,name + ".getFreeValue()");
			writeVarCreate(elementName + "Keys",t + "[]",name + ".getKeys()");
			toWriteForIAndLeftBraceBack(name,elementName,elementName + "Keys.length");
			writeVarCreate(elementName,t,elementName + "Keys[" + elementName + "I]");
			writeCustom("if(" + elementName + "!=" + elementName + "FreeValue" + ")");
			writeLeftBrace();
		}
		else
		{
			String t=_code.getElementType(elementType);
			
			if(ShineToolSetting.isSCollectionSignedArrayWrite)
			{
				writeVarCreate(elementName + "Keys",t + "[]",name + ".getKeys()");
				toWriteForIAndLeftBraceBack(name,elementName,elementName + "Keys.length");
				writeVarCreate(elementName,t,elementName + "Keys[" + elementName + "I]");
				writeCustom("if(" + elementName + "!=null)");
				writeLeftBrace();
			}
			else
			{
				writeVarCreate(elementName + "Keys",_code.Object + "[]",name + ".getKeys()");
				toWriteForIAndLeftBraceBack(name,elementName,elementName + "Keys.length");
				writeCustom("if(" + elementName + "Keys[" + elementName + "I]" + "!=null)");
				writeLeftBrace();
				writeVarCreate(elementName,t,_code.getVarTypeTrans(elementName + "Keys[" + elementName + "I]",t));
			}
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
		boolean need=false;
		
		if(_code.isInt(kType))
		{
			if(!_code.isInt(vType) && !_code.isLong(vType) && !_code.isBoolean(vType))
			{
				need=true;
			}
		}
		else if(_code.isLong(kType))
		{
			if(!_code.isInt(vType) && !_code.isLong(vType))
			{
				need=true;
			}
		}
		
		if(need && ShineToolSetting.isSCollectionSignedArrayCreate)
		{
			//String uName=getUName(name);
			
			writeVarSet(name,_code.createNewObject(_code.getMapType(kType,vType,true),cutVType(vType)+"[]::new",len));
		}
		else
		{
			writeVarSet(name,_code.createNewObject(_code.getMapType(kType,vType,true),len));
		}
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
		String kt=_code.getElementType(kType);
		
		String vt=vType;
		boolean isObj=false;
		
		if(isBaseCollectionType(kType))
		{
			if(_code.isInt(kType) && _code.isInt(vType))
			{
				vt=_code.Int;
				
				String lt=_code.Long;
				
				//双int
				
				writeVarCreate(kName + "FreeValue",kt,name + ".getFreeValue()");
				writeVarCreate(kName + "Table",lt + "[]",name + ".getTable()");
				writeVarCreate(kName + "Entry",lt);
				
				toWriteForIAndLeftBraceBack(name,kName,kName + "Table.length");
				
				writeVarSet(kName + "Entry",kName + "Table[" + kName + "I]");
				writeVarCreate(kName,kt,_code.getVarTypeTrans(kName + "Entry",kt));
				writeCustom("if(" + kName + "!=" + kName + "FreeValue" + ")");
				writeLeftBrace();
				
				writeVarCreate(vName,vt,_code.getVarTypeTrans("(" + kName + "Entry>>>32)",vt));
				
				return;
			}
			else if(_code.isBoolean(vType))
			{
				isObj=false;
				vt=_code.Boolean;
			}
			else if(_code.isInt(vType))
			{
				isObj=false;
				vt=_code.Int;
			}
			else if(_code.isLong(vType))
			{
				isObj=false;
				vt=_code.Long;
			}
			else
			{
				isObj=true;
				vt=ShineToolSetting.isSCollectionSignedArrayWrite ? vType : _code.Object;
			}
			
			writeVarCreate(kName + "FreeValue",kt,name + ".getFreeValue()");
			writeVarCreate(kName + "Keys",kt + "[]",name + ".getKeys()");
			writeVarCreate(vName + "Values",vt + "[]",name + ".getValues()");
			toWriteForIAndLeftBraceBack(name,kName,kName + "Keys.length");
			
			writeVarCreate(kName,kt,kName + "Keys[" + kName + "I]");
			writeCustom("if(" + kName + "!=" + kName + "FreeValue" + ")");
			writeLeftBrace();
			
			if(!isObj || ShineToolSetting.isSCollectionSignedArrayWrite)
			{
				writeVarCreate(vName,vt,vName + "Values[" + kName + "I]");
			}
			else
			{
				writeVarCreate(vName,vType,_code.getVarTypeTrans(vName + "Values[" + kName + "I]",vType));
			}
		}
		else
		{
			String ot=_code.Object;
			
			
			writeVarCreate(kName + "Table",ot + "[]",name + ".getTable()");
			
			writeCustom("for(int " + kName + "I=" + kName + "Table.length" + "-2;" + kName + "I>=0;" + kName + "I-=2)");
			writeLeftBrace();
			
			writeCustom("if(" + kName + "Table[" + kName + "I]" + "!=null)");
			writeLeftBrace();
			
			writeVarCreate(kName,kt,_code.getVarTypeTrans(kName + "Table[" + kName + "I]",kt));
			writeVarCreate(vName,vType,_code.getVarTypeTrans(kName + "Table[" + kName + "I+1]",vType));
		}
	}
	
	@Override
	public void toWriteForEachMapValue(String name,String kType,String vName,String vType)
	{
		String kt=_code.getElementType(kType);
		String vt=vType;
		
		if(isBaseCollectionType(kType))
		{
			if(_code.isInt(kType) && _code.isInt(vType))
			{
				vt=_code.Int;
				
				String lt=_code.Long;
				
				//双int
				String kName=vName + "K";
				
				writeVarCreate(kName + "FreeValue",kt,name + ".getFreeValue()");
				writeVarCreate(kName + "Table",lt + "[]",name + ".getTable()");
				writeVarCreate(kName + "Entry",lt);
				
				toWriteForIAndLeftBraceBack(name,kName,kName + "Table.length");
				
				writeVarSet(kName + "Entry",kName + "Table[" + kName + "I]");
				writeVarCreate(kName,kt,_code.getVarTypeTrans(kName + "Entry",kt));
				writeCustom("if(" + kName + "!=" + kName + "FreeValue" + ")");
				writeLeftBrace();
				
				writeVarCreate(vName,vt,_code.getVarTypeTrans("(" + kName + "Entry>>>32)",vt));
				
				return;
			}
			
			if(ShineToolSetting.isSCollectionSignedArrayWrite)
			{
				writeVarCreate(vName + "Values",vt + "[]",name + ".getValues()");
				toWriteForIAndLeftBraceBack(name,vName,vName + "Values.length");
				writeVarCreate(vName,vt,vName + "Values[" + vName + "I]");
				writeCustom("if(" + vName + "!=null)");
				writeLeftBrace();
			}
			else
			{
				writeVarCreate(vName + "Values",_code.Object + "[]",name + ".getValues()");
				toWriteForIAndLeftBraceBack(name,vName,vName + "Values.length");
				writeCustom("if(" + vName + "Values[" + vName + "I]!=null)");
				writeLeftBrace();
				writeVarCreate(vName,vType,_code.getVarTypeTrans(vName + "Values[" + vName + "I]",vType));
			}
		}
		else
		{
			String ot=_code.Object;
			
			writeVarCreate(vName + "Table",ot + "[]",name + ".getTable()");
			
			writeCustom("for(int " + vName + "I=" + vName + "Table.length" + "-2;" + vName + "I>=0;" + vName + "I-=2)");
			writeLeftBrace();
			
			writeCustom("if(" + vName + "Table[" + vName + "I]" + "!=null)");
			writeLeftBrace();
			
			writeVarCreate(vName,vType,_code.getVarTypeTrans(vName + "Table[" + vName + "I+1]",vType));
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
		if(ShineToolSetting.isSCollectionSignedArrayCreate && !isBaseCollectionType(elementType))
		{
			//String uName=getUName(name);
			
			writeVarSet(name,_code.createNewObject(_code.getQueueType(elementType,true),cutVType(elementType)+"[]::new",len));
		}
		else
		{
			writeVarSet(name,_code.createNewObject(_code.getQueueType(elementType,true),len));
		}
	}
	
	@Override
	public void clearOrCreateQueue(String name,String elementType,String len)
	{
		writeCustom("if(" + name + "!=" + _code.Null + ")");
		writeLeftBrace();
		writeCustom(name + ".clear();");
		
		writeCustom(name + ".ensureCapacity(" + len + ");");
		
		writeElseAndDoubleBrace();
		
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
		
		if(ShineToolSetting.isSCollectionSignedArrayWrite || isBaseCollectionType(elementType))
		{
			writeVarCreate(elementName + "Values",t + "[]",name + ".getValues()");
			writeVarCreate(elementName + "Mark",_code.Int,name + ".getMark()");
			writeVarCreate(elementName + "Start",_code.Int,name + ".getStart()");
			toWriteForIAndLeftBrace(name,elementName,name + ".length()");
			writeVarCreate(elementName,t,elementName + "Values[("+elementName+"I+"+elementName+"Start)&" +elementName + "Mark" +"]");
		}
		else
		{
			writeVarCreate(elementName + "Values",_code.Object + "[]",name + ".getValues()");
			writeVarCreate(elementName + "Mark",_code.Int,name + ".getMark()");
			writeVarCreate(elementName + "Start",_code.Int,name + ".getStart()");
			toWriteForIAndLeftBrace(name,elementName,name + ".length()");
			writeVarCreate(elementName,t,_code.getVarTypeTrans(elementName + "Values[("+elementName+"I+"+elementName+"Start)&" +elementName + "Mark" +"]",t));
		}
	}
}
