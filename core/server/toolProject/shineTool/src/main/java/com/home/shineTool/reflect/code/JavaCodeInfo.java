package com.home.shineTool.reflect.code;

import com.home.shineTool.constlist.CodeType;
import com.home.shineTool.global.ShineToolSetting;

public class JavaCodeInfo extends CodeInfo
{
	//输入源类型
	private String _inputList="List";
	private String _inputSet="Set";
	private String _inputMap="Map";
	private String _inputQueue="Queue";
	
	/** java原生集合包 */
	private String _javaCollectionPackage="java.util.";
	/** java原生集合包 */
	private String _exCollectionPackage=ShineToolSetting.exCollectionPackage + ".";
	
	public JavaCodeInfo()
	{
		super(CodeType.Java);
	}
	
	@Override
	protected void init()
	{
		this.Null="null";
		this.Object="Object";
		
		this.Boolean="boolean";
		this.Byte="byte";
		this.Short="short";
		this.Int="int";
		this.Long="long";
		this.Float="float";
		this.Double="double";
		this.String="String";
		this.List="List";
		this.Set="Set";
		this.Map="Map";
		this.Queue="Queue";
		
		this.ListV="SList";
		this.SetV="SSet";
		this.MapV="SMap";
		this.QueueV="SQueue";
		
		this.ubyte="ubyte";
		this.ushort="ushort";
		
		this.BooleanG="Boolean";
		this.ByteG="Byte";
		this.ShortG="Short";
		this.IntG="Integer";
		this.LongG="Long";
		this.FloatG="Float";
		this.DoubleG="Double";
		this.StringG="String";
		
		
	}
	
	@Override
	protected boolean isList(String type)
	{
		return type.startsWith(_inputList + "<") || type.startsWith(List + "<") || type.startsWith(ListV + "<");
	}
	
	@Override
	protected boolean isSet(String type)
	{
		return type.startsWith(_inputSet + "<") || type.startsWith(Set + "<") || type.startsWith(SetV + "<");
	}
	
	@Override
	protected boolean isMap(String type)
	{
		return type.startsWith(_inputMap + "<") || type.startsWith(Map + "<") || type.startsWith(MapV + "<");
	}
	
	@Override
	protected boolean isQueue(String type)
	{
		return type.startsWith(_inputQueue + "<") || type.startsWith(Queue + "<") || type.startsWith(QueueV + "<");
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
	protected String toGetNewArray(String elementType,String len)
	{
		if(elementType.endsWith("[]"))
		{
			return toGetNewArray(elementType.substring(0,elementType.length() - 2),len) + "[]";
		}
		else if(elementType.endsWith(">"))
		{
			return elementType.substring(0,elementType.indexOf("<")) + "[" + len + "]";
		}
		else
		{
			return elementType + "[" + len + "]";
		}
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
		return name + " instanceof " + type;
	}

	@Override
	public String getVarTypeTrans(String name,String type)
	{
		return "(" + type + ")" + name;
	}
	
	@Override
	public String getVarClsType(String name)
	{
		return name+".getClass()";
	}
	
	/** 获取类cls类型 */
	public String getClassClsType(String name)
	{
		return name+".class";
	}
	
	/** 类cls类型是否为另一个的父类 */
	public String getClsTypeIsAssignableFrom(String clsType1,String clsType2)
	{
		return clsType1+".isAssignableFrom("+clsType2+")";
	}

	@Override
	public String getArrayTypeImport(String elementType,boolean isValueType)
	{
		return null;
	}
	
	@Override
	public String getListTypeImport(String elementType,boolean isValueType)
	{
		if(isInt(elementType))
		{
			return _exCollectionPackage + "IntList";
		}
		
		if(isLong(elementType))
		{
			return _exCollectionPackage + "LongList";
		}
		
		return _exCollectionPackage + ListV;
	}

	@Override
	public String getSetTypeImport(String elementType,boolean isValueType)
	{
		if(isInt(elementType))
		{
			return _exCollectionPackage + "IntSet";
		}
		
		if(isLong(elementType))
		{
			return _exCollectionPackage + "LongSet";
		}
		
		return _exCollectionPackage + SetV;
	}

	@Override
	public String getMapTypeImport(String kType,String vType,boolean isValueType)
	{
		if(isInt(kType))
		{
			if(isInt(vType))
			{
				return _exCollectionPackage + "IntIntMap";
			}
			else if(isBoolean(vType))
			{
				return _exCollectionPackage + "IntBooleanMap";
			}
			else if(isLong(vType))
			{
				return _exCollectionPackage + "IntLongMap";
			}
			else
			{
				return _exCollectionPackage + "IntObjectMap";
			}
		}
		else if(isLong(kType))
		{
			if(isInt(vType))
			{
				return _exCollectionPackage + "LongIntMap";
			}
			else if(isLong(vType))
			{
				return _exCollectionPackage + "LongLongMap";
			}
			else
			{
				return _exCollectionPackage + "LongObjectMap";
			}
		}
		
		return _exCollectionPackage + MapV;
	}
	
	@Override
	public String getMapEntryTypeImport(String kType,String vType,boolean isValueType)
	{
		if(isInt(kType) || isLong(kType))
		{
			return null;
		}
		
		return null;
	}
	
	@Override
	public String getQueueTypeImport(String elementType,boolean isValueType)
	{
		if(isInt(elementType))
		{
			return _exCollectionPackage + "IntQueue";
		}
		
		if(isLong(elementType))
		{
			return _exCollectionPackage + "LongQueue";
		}
		
		return _exCollectionPackage + QueueV;
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
}
