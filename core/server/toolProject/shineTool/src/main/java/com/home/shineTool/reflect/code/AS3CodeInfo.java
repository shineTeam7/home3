package com.home.shineTool.reflect.code;

import com.home.shineTool.constlist.CodeType;
import com.home.shineTool.constlist.VarType;

public class AS3CodeInfo extends CodeInfo
{
	public AS3CodeInfo()
	{
		super(CodeType.AS3);
	}
	
	@Override
	protected void init()
	{
		this.Null="null";
		this.Object="Object";
		
		this.Boolean="Boolean";
		this.Byte="int";
		this.Short="int";
		this.Int="int";
		this.Long="Long";
		this.Float="Number";
		this.Double="Number";
		this.String="String";
		this.Char="char";
		
		this.List="Vector";
		this.Set="Keys";
		this.Map="Dictionary";
		this.Queue="Vector";
		
		this.ListV="Vector";
		this.SetV="Keys";
		this.MapV="Dictionary";
		this.QueueV="Vector";
		
		this.ubyte="ubyte";
		this.ushort="ushort";
		
		this.BooleanG="Boolean";
		this.ByteG="int";
		this.ShortG="int";
		this.IntG="int";
		this.LongG="Long";
		this.FloatG="Number";
		this.DoubleG="Number";
		this.StringG="String";
		this.CharG="char";
	}
	
	@Override
	public String getVarCreate(String name,String type,String defaultValue)
	{
		return "var " + name + ":" + type + (defaultValue.isEmpty() ? "" : ("=" + defaultValue)) + ";";
	}
	
	@Override
	public String getVarTypeTrans(String name,String type)
	{
		return name + " is " + type;
	}

	@Override
	public String getArrayType(String elementType,boolean isValueType)
	{
		return "Vector.<" + elementType + ">";
	}
	
	@Override
	public String getListType(String elementType,boolean isValueType)
	{
		return "Vector.<" + elementType + ">";
	}

	@Override
	public String getSetType(String elementType,boolean isValueType)
	{
		return "Keys";
	}

	@Override
	public String getMapType(String kType,String vType,boolean isValueType)
	{
		return "Dictionary";
	}
	
	@Override
	public String getQueueType(String elementType,boolean isValueType)
	{
		return "Vector.<" + elementType + ">";
	}
	
	@Override
	public String getBaseFieldDefaultValue(String type,String value)
	{
		int tt=getBaseVarType(type);
		
		if(tt==VarType.Long)
		{
			return "new Long(" + value + ")";
		}
		
		return value;
	}

	@Override
	public String getVarTypeIs(String name,String type)
	{
		return name + " as " + type;
	}

	@Override
	public String getArrayTypeImport(String elementType,boolean isValueType)
	{
		return null;
	}

	@Override
	public String getListTypeImport(String elementType,boolean isValueType)
	{
		return null;
	}

	@Override
	public String getSetTypeImport(String elementType,boolean isValueType)
	{
		return "shine.support.Keys";
	}

	@Override
	public String getMapTypeImport(String kType,String vType,boolean isValueType)
	{
		return "shine.support.Dictionary";
	}

	@Override
	public String getMapEntryTypeImport(String kType,String vType,boolean isValueType)
	{
		return null;
	}
	
	@Override
	public String getQueueTypeImport(String elementType,boolean isValueType)
	{
		return null;
	}
	
	@Override
	public String getArrayLength(String name)
	{
		return name + ".length";
	}
	
	@Override
	public String getArrayElement(String name,String index)
	{
		return name + "[" + index + "]";
	}
	
	@Override
	public String getListLength(String name)
	{
		return name + ".length";
	}
	
	@Override
	public String getListElement(String name,String index)
	{
		return name + "[" + index + "]";
	}

	@Override
	public String getSetLength(String name)
	{
		return name + ".length";
	}
	
	@Override
	public String getSetContains(String name,String key)
	{
		return name+".hasOwnProperty("+key+")";
	}

	@Override
	public String getMapLength(String name)
	{
		return name + ".length";
	}

	@Override
	public String getMapElement(String name,String key)
	{
		return name + "[" + key + "]";
	}
	
	@Override
	public String getMapElementSafe(String name,String key)
	{
		return name + "[" + key + "]";
	}
	
	@Override
	public String getQueueLength(String name)
	{
		return name + ".length";
	}
	
	@Override
	public String getQueueElement(String name,String index)
	{
		return name + "[" + index + "]";
	}

	@Override
	public String createNewArray(String elementType,String len)
	{
		return createNewObject(getArrayType(elementType,true),len);
	}
	
	@Override
	public String createNewArrayWithValues(String elementType,String... args)
	{
		//暂时不支持
		return "";
		//		return createNewObject(getVarArrayType(elementType,true),len);
	}
}
