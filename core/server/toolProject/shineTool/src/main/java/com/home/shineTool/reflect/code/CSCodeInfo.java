package com.home.shineTool.reflect.code;

import com.home.shineTool.constlist.CodeType;

/** C#代码 */
public class CSCodeInfo extends CodeInfo
{
	public CSCodeInfo()
	{
		super(CodeType.CS);
	}
	
	@Override
	protected void init()
	{
		this.Null="null";
		this.Object="object";
		
		this.Boolean="bool";
		this.Byte="byte";
		this.Short="short";
		this.Int="int";
		this.Long="long";
		this.Float="float";
		this.Double="double";
		this.String="string";
		this.List="List";
		this.Set="HashSet";
		this.Map="Dictionary";
		this.Queue="Queue";
		
		this.ListV="SList";
		this.SetV="SSet";
		this.MapV="SMap";
		this.QueueV="SQueue";
		
		this.ubyte="ubyte";
		this.ushort="ushort";
		
		this.BooleanG="bool";
		this.ByteG="byte";
		this.ShortG="short";
		this.IntG="int";
		this.LongG="long";
		this.FloatG="float";
		this.DoubleG="double";
		this.StringG="string";
		
		this.Super="base";
	}

	@Override
	public String getVarCreate(String name,String type,String defaultValue)
	{
		return type + " " + name + (defaultValue.isEmpty() ? "" : ("=" + defaultValue)) + ";";
	}

	@Override
	public String getArrayType(String elementType,boolean isValueType)
	{
		return elementType + "[]";
	}
	
	@Override
	public String getListType(String elementType,boolean isValueType)
	{
		if(isInt(elementType))
		{
			return "IntList";
		}
		
		if(isLong(elementType))
		{
			return "LongList";
		}
		
		return ListV + "<" + elementType + ">";
	}

	@Override
	public String getSetType(String elementType,boolean isValueType)
	{
		if(isInt(elementType))
		{
			return "IntSet";
		}
		
		if(isLong(elementType))
		{
			return "LongSet";
		}
		
		return SetV + "<" + elementType + ">";
	}

	@Override
	public String getMapType(String kType,String vType,boolean isValueType)
	{
		if(isInt(kType))
		{
			if(isInt(vType))
			{
				return "IntIntMap";
			}
			else if(isBoolean(vType))
			{
				return "IntBooleanMap";
			}
			else if(isLong(vType))
			{
				return "IntLongMap";
			}
			else
			{
				return "IntObjectMap<" + vType + ">";
			}
		}
		else if(isLong(kType))
		{
			if(isInt(vType))
			{
				return "LongIntMap";
			}
			else if(isLong(vType))
			{
				return "LongLongMap";
			}
			else
			{
				return "LongObjectMap<" + vType + ">";
			}
		}
		
		return MapV + "<" + kType + "," + vType + ">";
	}
	
	@Override
	public String getQueueType(String elementType,boolean isValueType)
	{
		if(isInt(elementType))
		{
			return "IntQueue";
		}
		
		if(isLong(elementType))
		{
			return "LongQueue";
		}
		
		return QueueV + "<" + elementType + ">";
	}
	
	@Override
	public String getVarTypeIs(String name,String type)
	{
		return name + " is " + type;
	}

	@Override
	public String getVarTypeTrans(String name,String type)
	{
		//return name + " as " + type;
		return "(" + type + ")" + name;
	}
	
	@Override
	public String getVarClsType(String name)
	{
		return name+".GetType()";
	}
	
	/** 获取类cls类型 */
	public String getClassClsType(String name)
	{
		return "typeof("+name+")";
	}
	
	/** 类cls类型是否为另一个的父类 */
	public String getClsTypeIsAssignableFrom(String clsType1,String clsType2)
	{
		return clsType1+".IsAssignableFrom("+clsType2+")";
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
		return null;
	}

	@Override
	public String getMapTypeImport(String kType,String vType,boolean isValueType)
	{
		return null;
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
		return name + ".Length";
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
}
