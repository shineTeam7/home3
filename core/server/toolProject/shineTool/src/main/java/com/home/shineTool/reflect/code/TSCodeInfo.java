package com.home.shineTool.reflect.code;

import com.home.shine.ctrl.Ctrl;
import com.home.shineTool.constlist.CodeType;
import com.home.shineTool.constlist.VarType;

public class TSCodeInfo extends CodeInfo
{
	public TSCodeInfo()
	{
		super(CodeType.TS);
	}
	
	@Override
	protected void init()
	{
		this.Null="null";
		this.Object="any";
		
		this.Boolean="boolean";
		this.Byte="number";
		this.Short="number";
		this.Int="number";
		this.Long="number";
		this.Float="number";
		this.Double="number";
		this.String="string";
		this.Char="number";
		
		this.List="SList";
		this.Set="SSet";
		this.Map="SMap";
		this.Queue="SList";
		
		this.ListV="SList";
		this.SetV="SSet";
		this.MapV="SMap";
		this.QueueV="SList";
		
		this.ubyte="number";
		this.ushort="number";
		
		this.BooleanG="boolean";
		this.ByteG="number";
		this.ShortG="number";
		this.IntG="number";
		this.LongG="number";
		this.FloatG="number";
		this.DoubleG="number";
		this.StringG="string";
		this.CharG="number";
		
		this.LongDefaultValue="0";
		this.FloatDefaultValue="0";
		this.DoubleDefaultValue="0";
	}

	///** 暂时打个补丁*/
	//public String getPossibleVarType(String type)
	//{
	//	switch (type)
	//	{
	//		case "int":
	//		case "long":
	//		case "float":
	//		case "double":
	//		{
	//			return "number";
	//		}
	//		case "String":
	//		{
	//			return "string";
	//		}
	//		default:
	//		{
	//			return type;
	//		}
	//	}
	//}
	
	@Override
	public String getVarCreate(String name,String type,String defaultValue)
	{
		return "var " + name + ":" + type + (defaultValue.isEmpty() ? "" : ("=" + defaultValue)) + ";";
	}
	
	@Override
	public String getVarTypeTrans(String name,String type)
	{
		return name + " as " + type;
	}
	
	@Override
	public String getArrayType(String elementType,boolean isValueType)
	{
		//return "Vector.<" + elementType + ">";
		return elementType + "[]";
	}
	
	@Override
	public String getListType(String elementType,boolean isValueType)
	{
		return ListV+"<" + elementType + ">";
	}
	
	@Override
	public String getSetType(String elementType,boolean isValueType)
	{
		return SetV+"<" + elementType + ">";
	}
	
	@Override
	public String getMapType(String kType,String vType,boolean isValueType)
	{
		return MapV + "<" + kType + "," + vType + ">";
	}
	
	@Override
	public String getQueueType(String elementType,boolean isValueType)
	{
		return QueueV+"<" + elementType + ">";
	}
	
	//@Override
	//public String getBaseFieldDefaultValue(String type,String value)
	//{
	//	int tt=getBaseVarType(type);
	//
	//	if(tt==VarType.Long)
	//	{
	//		return "new Long(" + value + ")";
	//	}
	//
	//	return value;
	//}
	
	@Override
	public String getVarTypeIs(String name,String type)
	{
		return name + " instanceof " + type;
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
		return name + ".size()";
	}
	
	@Override
	public String getListElement(String name,String index)
	{
		return name + ".get(" + index + ")";
	}
	
	@Override
	public String getSetLength(String name)
	{
		return name + ".size()";
	}
	
	@Override
	public String getSetContains(String name,String key)
	{
		return name+".contains("+key+")";
	}
	
	@Override
	public String getMapLength(String name)
	{
		return name + ".size()";
	}
	
	@Override
	public String getMapElement(String name,String key)
	{
		return name + ".get(" + key + ")";
	}
	
	@Override
	public String getMapElementSafe(String name,String key)
	{
		return name + ".get(" + key + ")";
	}
	
	@Override
	public String getQueueLength(String name)
	{
		return name + ".size()";
	}
	
	@Override
	public String getQueueElement(String name,String index)
	{
		return name + ".get(" + index + ")";
	}
	
	@Override
	public String createNewArray(String elementType,String len)
	{
		return createNewObject("Array<" + elementType + ">",len);
	}
	
	@Override
	public String createNewArrayWithValues(String elementType,String... args)
	{
		Ctrl.throwError("TS不支持createNewArrayWithValues");
		//暂时不支持
		return "";
		//		return createNewObject(getVarArrayType(elementType,true),len);
	}
	
	@Override
	public String getFieldWrap(String str)
	{
		if(str.startsWith("this."))
			return str;
		
		return "this."+str;
	}
	
	@Override
	public String getThisFront()
	{
		return "this.";
	}
}