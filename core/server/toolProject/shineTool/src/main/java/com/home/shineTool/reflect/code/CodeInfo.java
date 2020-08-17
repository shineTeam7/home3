package com.home.shineTool.reflect.code;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.StringIntMap;
import com.home.shineTool.constlist.CodeType;
import com.home.shineTool.constlist.VarType;
import com.home.shineTool.reflect.cls.ClassInfo;
import com.home.shineTool.global.ShineToolSetting;

import java.util.HashMap;

/** 基础代码信息 */
public abstract class CodeInfo
{
	/** 回车 */
	public static final String Enter=ClassInfo.Enter;
	
	private static CodeInfo _java=new JavaCodeInfo();
	
	private static CodeInfo _as3=new AS3CodeInfo();
	
	private static CodeInfo _cs=new CSCodeInfo();
	
	private static CodeInfo _ts=new TSCodeInfo();
	
	/** 获取代码信息 */
	public static CodeInfo getCode(int codeType)
	{
		switch(codeType)
		{
			case CodeType.Java:
			{
				return _java;
			}
			case CodeType.AS3:
			{
				return _as3;
			}
			case CodeType.CS:
			{
				return _cs;
			}
			case CodeType.TS:
			{
				return _ts;
			}
		}
		
		return null;
	}
	
	private int _codeType;
	//各自
	
	//空
	public String Null;
	
	//实值类型
	
	public String Object;
	public String Boolean;
	public String Byte;
	public String Short;
	public String Int;
	public String Long;
	public String Float;
	public String Double;
	public String String;
	public String Char;
	public String Array;
	public String List;
	public String Set;
	public String Map;
	public String Queue;
	
	//泛型类型
	
	public String BooleanG;
	public String ByteG;
	public String ShortG;
	public String IntG;
	public String LongG;
	public String FloatG;
	public String DoubleG;
	public String StringG;
	public String CharG;
	
	//默认值
	public String BooleanDefaultValue="false";
	public String ByteDefaultValue="0";
	public String ShortDefaultValue="0";
	public String IntDefaultValue="0";
	public String LongDefaultValue="0L";
	public String FloatDefaultValue="0f";
	public String DoubleDefaultValue="0.0";
	public String StringDefaultValue="\"\"";
	public String CharDefaultValue="0";
	
	//辅助类型
	
	public String ubyte;
	public String ushort;
	
	//值类型
	//
	/** 返回值空 */
	public String Void="void";
	//boolean
	public String True="true";
	public String False="false";
	
	public String ListV;
	public String SetV;
	public String MapV;
	public String QueueV;
	
	//其他
	/** 自身对象名 */
	public String This="this";
	/** 父类对象名 */
	public String Super="super";
	
	/** 类信息(辅助写入用的) */
	protected ClassInfo _cls;
	
	//count
	/** 基础类型 */
	private StringIntMap _strToTypeDic=new StringIntMap();
	
	private IntObjectMap<String> _baseTypeInputStrDic=new IntObjectMap<>();
	
	private IntObjectMap<String> _baseTypeInputGStrDic=new IntObjectMap<>();
	
	private IntObjectMap<String> _typeStrDic=new IntObjectMap<>();
	
	private IntObjectMap<String> _typeGStrDic=new IntObjectMap<>();
	
	private IntObjectMap<String> _typeDefaultValueDic=new IntObjectMap<>();
	
	public CodeInfo(int codeType)
	{
		_codeType=codeType;
		
		_cls=ClassInfo.getClassInfo(codeType);
		
		init();
		initNext();
	}
	
	/** 获取类类型 */
	public int getCodeType()
	{
		return _codeType;
	}
	
	public boolean useSCollection()
	{
		return CodeType.useSCollection(_codeType);
	}
	
	/** 获取类辅助 */
	public ClassInfo getCls()
	{
		return _cls;
	}
	
	protected abstract void init();
	
	private void initNext()
	{
		_strToTypeDic.put(Boolean,VarType.Boolean);
		_strToTypeDic.put(Byte,VarType.Byte);
		_strToTypeDic.put(ubyte,VarType.UByte);
		_strToTypeDic.put(Short,VarType.Short);
		_strToTypeDic.put(ushort,VarType.UShort);
		_strToTypeDic.put(Int,VarType.Int);
		_strToTypeDic.put(Long,VarType.Long);
		_strToTypeDic.put(Float,VarType.Float);
		_strToTypeDic.put(Double,VarType.Double);
		_strToTypeDic.put(String,VarType.String);
		_strToTypeDic.put(Char,VarType.Char);
		
		_strToTypeDic.put(BooleanG,VarType.Boolean);
		_strToTypeDic.put(ByteG,VarType.Byte);
		_strToTypeDic.put(ShortG,VarType.Short);
		_strToTypeDic.put(IntG,VarType.Int);
		_strToTypeDic.put(LongG,VarType.Long);
		_strToTypeDic.put(FloatG,VarType.Float);
		_strToTypeDic.put(DoubleG,VarType.Double);
		_strToTypeDic.put(StringG,VarType.String);
		_strToTypeDic.put(CharG,VarType.Char);
		
		_baseTypeInputStrDic.put(VarType.Boolean,Boolean);
		_baseTypeInputStrDic.put(VarType.Byte,Byte);
		_baseTypeInputStrDic.put(VarType.UByte,ubyte);
		_baseTypeInputStrDic.put(VarType.Short,Short);
		_baseTypeInputStrDic.put(VarType.UShort,ushort);
		_baseTypeInputStrDic.put(VarType.Int,Int);
		_baseTypeInputStrDic.put(VarType.Long,Long);
		_baseTypeInputStrDic.put(VarType.Float,Float);
		_baseTypeInputStrDic.put(VarType.Double,Double);
		_baseTypeInputStrDic.put(VarType.String,String);
		_baseTypeInputStrDic.put(VarType.Char,Char);
		
		_baseTypeInputGStrDic.put(VarType.Boolean,BooleanG);
		_baseTypeInputGStrDic.put(VarType.Byte,ByteG);
		_baseTypeInputGStrDic.put(VarType.UByte,ubyte);
		_baseTypeInputGStrDic.put(VarType.Short,ShortG);
		_baseTypeInputGStrDic.put(VarType.UShort,ushort);
		_baseTypeInputGStrDic.put(VarType.Int,IntG);
		_baseTypeInputGStrDic.put(VarType.Long,LongG);
		_baseTypeInputGStrDic.put(VarType.Float,FloatG);
		_baseTypeInputGStrDic.put(VarType.Double,DoubleG);
		_baseTypeInputGStrDic.put(VarType.String,StringG);
		_baseTypeInputGStrDic.put(VarType.Char,CharG);
		
		_typeStrDic.put(VarType.Boolean,Boolean);
		_typeStrDic.put(VarType.Byte,ShineToolSetting.useIntInsteadByte ? Int : Byte);
		_typeStrDic.put(VarType.UByte,Int);
		_typeStrDic.put(VarType.Short,Int);
		_typeStrDic.put(VarType.UShort,Int);
		_typeStrDic.put(VarType.Int,Int);
		_typeStrDic.put(VarType.Long,Long);
		_typeStrDic.put(VarType.Float,Float);
		_typeStrDic.put(VarType.Double,Double);
		_typeStrDic.put(VarType.String,String);
		_typeStrDic.put(VarType.Char,Char);
		
		_typeGStrDic.put(VarType.Boolean,BooleanG);
		_typeGStrDic.put(VarType.Byte,ShineToolSetting.useIntInsteadByte ? IntG : ByteG);
		_typeGStrDic.put(VarType.UByte,IntG);
		_typeGStrDic.put(VarType.Short,IntG);
		_typeGStrDic.put(VarType.UShort,IntG);
		_typeGStrDic.put(VarType.Int,IntG);
		_typeGStrDic.put(VarType.Long,LongG);
		_typeGStrDic.put(VarType.Float,FloatG);
		_typeGStrDic.put(VarType.Double,DoubleG);
		_typeGStrDic.put(VarType.String,StringG);
		_typeGStrDic.put(VarType.Char,CharG);
		
		_typeDefaultValueDic.put(VarType.Boolean,BooleanDefaultValue);
		_typeDefaultValueDic.put(VarType.Byte,ByteDefaultValue);
		_typeDefaultValueDic.put(VarType.UByte,ByteDefaultValue);
		_typeDefaultValueDic.put(VarType.Short,ShortDefaultValue);
		_typeDefaultValueDic.put(VarType.UShort,ShortDefaultValue);
		_typeDefaultValueDic.put(VarType.Int,IntDefaultValue);
		_typeDefaultValueDic.put(VarType.Long,LongDefaultValue);
		_typeDefaultValueDic.put(VarType.Float,FloatDefaultValue);
		_typeDefaultValueDic.put(VarType.Double,DoubleDefaultValue);
		_typeDefaultValueDic.put(VarType.String,StringDefaultValue);
		_typeDefaultValueDic.put(VarType.Char,CharDefaultValue);
	}
	
	/** 获取基础类型(取不到返回-1) */
	public int getBaseVarType(String type)
	{
		if(_strToTypeDic.contains(type))
		{
			return _strToTypeDic.get(type);
		}
		
		return -1;
	}
	
	/** 获取集合变量类型(in类型:java)(找不到返回-1) */
	public int getCollectionVarType(String type)
	{
		if(isArray(type))
		{
			return VarType.Array;
		}
		
		if(isList(type))
		{
			return VarType.List;
		}
		
		if(type.startsWith(Set + "<"))
		{
			return VarType.Set;
		}
		
		if(type.startsWith(Map + "<"))
		{
			return VarType.Map;
		}
		
		if(type.startsWith(Queue + "<"))
		{
			return VarType.Queue;
		}
		
		return -1;
	}
	
	/** 是否是集合类型(in类型) */
	protected boolean isCollectionType(String type)
	{
		return getCollectionVarType(type)!=-1;
	}
	
	/** 是否是基础类型(in类型) */
	public boolean isBaseType(String type)
	{
		return getBaseVarType(type)!=-1;
	}
	
	protected boolean isArray(String type)
	{
		return type.endsWith("[]");
	}
	
	/** 是否List */
	protected boolean isList(String type)
	{
		return type.startsWith(List + "<");
	}
	
	/** 是否Set */
	protected boolean isSet(String type)
	{
		return type.startsWith(Set + "<");
	}
	
	/** 是否Map */
	protected boolean isMap(String type)
	{
		return type.startsWith(Map + "<");
	}
	
	/** 是否Map */
	protected boolean isQueue(String type)
	{
		return type.startsWith(Queue + "<");
	}
	
	/** 获取变量类型(声明类型) */
	public int getVarType(String type)
	{
		int tt=getBaseVarType(type);
		
		if(tt!=-1)
		{
			return tt;
		}
		
		tt=getCollectionVarType(type);
		
		if(tt!=-1)
		{
			return tt;
		}
		
		//		if(type.equals("Bytes"))
		//			return VarType.Bytes;
		
		return VarType.CustomObject;
	}
	
	/** 获取数组中的类型 */
	public static String getVarArrayType(String str)
	{
		int index=str.lastIndexOf("[");
		
		return str.substring(0,index).trim();
	}
	
	/** 获取List中单个类型 */
	public static String getVarCollectionOneType(String str)
	{
		int left=str.indexOf("<");
		int right=str.lastIndexOf(">");
		
		String re=str.substring(left + 1,right);
		
		return re.trim();
	}
	
	/** 获取Map中两个类型 */
	public static CollectionTwoType getVarCollectionTwoType(String str)
	{
		int left=str.indexOf("<");
		int right=str.lastIndexOf(">");
		
		String re=str.substring(left + 1,right);
		
		int index=re.indexOf(",");
		
		CollectionTwoType cc=new CollectionTwoType();
		cc.type0=re.substring(0,index).trim();
		cc.type1=re.substring(index + 1,re.length()).trim();
		
		return cc;
	}
	
	/** 双类型 */
	public static class CollectionTwoType
	{
		public String type0;
		
		public String type1;
	}
	
	/** 获取基础变量输入类型(找不到返回null) */
	public String getBaseVarInputStr(int type)
	{
		return _baseTypeInputStrDic.get(type);
	}
	
	/** 获取基础变量输入类型(泛型)(找不到返回null) */
	public String getBaseVarInputGStr(int type)
	{
		return _baseTypeInputGStrDic.get(type);
	}
	
	/** 获取变量类型(找不到返回null) */
	public String getVarStr(int type)
	{
		return _typeStrDic.get(type);
	}
	
	/** 获取变量泛型类型(找不到返回null) */
	public String getVarGStr(int type)
	{
		return _typeGStrDic.get(type);
	}
	
	//泛型
	public boolean isBoolean(String type)
	{
		return type.equals(Boolean) || type.equals(BooleanG);
	}
	
	public boolean isInt(String type)
	{
		return type.equals(Int) || type.equals(IntG);
	}
	
	public boolean isLong(String type)
	{
		return type.equals(Long) || type.equals(LongG);
	}
	
	public boolean isString(String type)
	{
		return type.equals(String) || type.equals(StringG);
	}
	
	/** 获取元素类型 */
	public String getElementType(String type)
	{
		if(isInt(type))
		{
			return Int;
		}
		
		if(isLong(type))
		{
			return Long;
		}
		
		return type;
	}
	
	//--片段--//
	
	/** 获取变量创建(结尾带;) */
	public abstract String getVarCreate(String name,String type,String defaultValue);
	
	/** 变量类型断定(a is T) */
	public abstract String getVarTypeIs(String name,String type);
	
	/** 变量类型转换((T)a) */
	public abstract String getVarTypeTrans(String name,String type);
	
	/** 获取变量cls类型 */
	public String getVarClsType(String name)
	{
		return "";
	}
	
	/** 获取类cls类型 */
	public String getClassClsType(String name)
	{
		return "";
	}
	
	/** 类cls类型是否为另一个的父类 */
	public String getClsTypeIsAssignableFrom(String clsType1,String clsType2)
	{
		return "";
	}
	
	/** 获取读基础类型 */
	public String getReadBaseVarType(int type)
	{
		String re="";
		
		switch(type)
		{
			case VarType.Boolean:
			{
				re=ShineToolSetting.bytesSteamVarName + ".readBoolean()";
			}
			break;
			case VarType.Byte:
			{
				re=ShineToolSetting.bytesSteamVarName + ".readByte()";
			}
			break;
			case VarType.UByte:
			{
				re=ShineToolSetting.bytesSteamVarName + ".readUnsignedByte()";
			}
			break;
			case VarType.Short:
			{
				re=ShineToolSetting.bytesSteamVarName + ".readShort()";
			}
			break;
			case VarType.UShort:
			{
				re=ShineToolSetting.bytesSteamVarName + ".readUnsignedShort()";
			}
			break;
			case VarType.Int:
			{
				re=ShineToolSetting.bytesSteamVarName + ".readInt()";
			}
			break;
			case VarType.Long:
			{
				re=ShineToolSetting.bytesSteamVarName + ".readLong()";
			}
			break;
			case VarType.Float:
			{
				re=ShineToolSetting.bytesSteamVarName + ".readFloat()";
			}
			break;
			case VarType.Double:
			{
				re=ShineToolSetting.bytesSteamVarName + ".readDouble()";
			}
			break;
			case VarType.String:
			{
				re=ShineToolSetting.bytesSteamVarName + ".readUTF()";
			}
			break;
		}
		
		return re;
	}
	
	/** 创建数组 */
	public String createNewArray(String elementType,String len)
	{
		return "new " + toGetNewArray(elementType,len);
	}
	
	protected String toGetNewArray(String elementType,String len)
	{
		if(elementType.endsWith("[]"))
		{
			return toGetNewArray(elementType.substring(0,elementType.length() - 2),len) + "[]";
		}
		else
		{
			return elementType + "[" + len + "]";
		}
	}
	
	/** 创建数组赋初值 */
	public String createNewArrayWithValues(String elementType,String... args)
	{
		StringBuilder sb=new StringBuilder();
		sb.append("new " + elementType + "[]{");
		
		for(int i=0;i<args.length;++i)
		{
			if(i>0)
			{
				sb.append(",");
			}
			
			sb.append(args);
		}
		
		return sb.toString();
	}
	
	/** 获取创建新类(可带参) */
	public String createNewObject(String clsName,String... args)
	{
		StringBuilder sb=new StringBuilder();
		sb.append("new ");
		sb.append(clsName);
		sb.append("(");
		
		String str;
		int index=0;
		
		for(int i=0;i<args.length;++i)
		{
			if((str=args[i])!=null)
			{
				if(index>0)
				{
					sb.append(",");
				}
				
				sb.append(str);
				
				++index;
			}
		}
		
		sb.append(")");
		
		return sb.toString();
	}
	
	/** 获取读取长度 */
	public String getReadLen()
	{
		return ShineToolSetting.bytesSteamVarName + ".readLen()";
	}
	
	/** 获取数据复制(深拷) */
	public String getDataCopy(String data)
	{
		//		return ShineToolSetting.bytesControlName+".getDataCopy("+data+")";
		return data + ".clone()";
	}
	
	//** 获取基础对象默认值 */
	public String getBaseTypeDefaultValue(int type)
	{
		String re=_typeDefaultValueDic.get(type);
		
		if(re==null)
		{
			Ctrl.throwError("未找到基础类型默认值",type);
		}
		
		return re;
	}
	
	/** 获取对象默认值 */
	public String getDefaultValue(String type)
	{
		int tt=getBaseVarType(type);
		
		if(tt!=-1)
		{
			return getBaseTypeDefaultValue(tt);
		}
		else
		{
			return Null;
		}
	}
	
	/** 获取基础类型默认值 */
	public String getBaseFieldDefaultValue(String type,String value)
	{
		return value;
	}
	
	//--collections--//
	
	/** 获取Array声明 */
	public abstract String getArrayType(String elementType,boolean isValueType);
	
	/** 获取List声明 */
	public abstract String getListType(String elementType,boolean isValueType);
	
	/** 获取Set声明 */
	public abstract String getSetType(String elementType,boolean isValueType);
	
	/** 获取Map声明 */
	public abstract String getMapType(String kType,String vType,boolean isValueType);
	
	/** 获取Queue声明 */
	public abstract String getQueueType(String elementType,boolean isValueType);
	
	//--import--//
	
	/** 获取Array声明导入 */
	public abstract String getArrayTypeImport(String elementType,boolean isValueType);
	
	/** 获取List声明导入 */
	public abstract String getListTypeImport(String elementType,boolean isValueType);
	
	/** 获取Set声明导入 */
	public abstract String getSetTypeImport(String elementType,boolean isValueType);
	
	/** 获取Map声明导入 */
	public abstract String getMapTypeImport(String kType,String vType,boolean isValueType);
	
	/** 获取Map的Entry声明导入 */
	public abstract String getMapEntryTypeImport(String kType,String vType,boolean isValueType);
	
	/** 获取Queue声明导入 */
	public abstract String getQueueTypeImport(String elementType,boolean isValueType);
	
	//--操作--//
	
	//Array
	
	/** 获取数组长度 */
	public abstract String getArrayLength(String name);
	
	/** 获取数组元素 */
	public abstract String getArrayElement(String name,String index);
	
	/** 获取数组长度 */
	public String isArrayNotEmpty(String name)
	{
		return getArrayLength(name)+">0";
	}
	
	//List
	
	/** 获取数组长度 */
	public String isCollectionNotEmpty(String name)
	{
		return "!"+name+".isEmpty()";
	}
	
	/** 获取List长度 */
	public abstract String getListLength(String name);
	
	/** 获取List元素 */
	public abstract String getListElement(String name,String index);
	//Set
	
	/** 获取Set长度 */
	public abstract String getSetLength(String name);
	
	/** 获取Set包含判定 */
	public abstract String getSetContains(String name,String key);
	//Map
	
	/** 获取Map长度 */
	public abstract String getMapLength(String name);
	
	/** 获取Map元素 */
	public abstract String getMapElement(String name,String key);
	
	/** 安全获取Map元素 */
	public abstract String getMapElementSafe(String name,String key);
	
	//Queue
	/** 获取Queue长度 */
	public abstract String getQueueLength(String name);
	
	/** 获取Queue元素 */
	public abstract String getQueueElement(String name,String index);
	
	/** 获取String为空的表达式 */
	public String getStringIsEmpty(String name)
	{
		return name+".isEmpty()";
	}
	
	/** 类属性包装 */
	public String getFieldWrap(String str)
	{
		return str;
	}
	
	/** 获取类属性包装的this */
	public String getThisFront()
	{
		return "";
	}
}
