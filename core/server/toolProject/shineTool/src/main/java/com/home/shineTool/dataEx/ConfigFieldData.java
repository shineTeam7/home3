package com.home.shineTool.dataEx;

import com.home.shine.support.collection.IntList;
import com.home.shineTool.constlist.ConfigFieldMarkType;
import com.home.shineTool.constlist.ConfigFieldType;

/** 配置属性数据 */
public class ConfigFieldData
{
	/** 配置表类数据 */
	public ConfigClassData clsData;
	/** 是否有服务器 */
	public boolean hasServer=false;
	/** 是否有客户端部分 */
	public boolean hasClient=false;
	/** 是否为主键 */
	public boolean isKey=false;
	/** 标记类型 */
	public int markType=ConfigFieldMarkType.None;
	/** 字段名 */
	public String name;
	/** 字段描述 */
	public String describe;
	/** cskr */
	public String cskr;
	/** 类型 */
	public String type;
	
	/** 所在序号 */
	public int index;
	
	/** 第一类型 */
	public int firstType;
	/** 类型组(逆序,子在前) */
	public int[] types;
	/** 类型组(正序,父在前) */
	public IntList typeList=new IntList();

	//纵表用
	/** 值 */
	public String value="";
	/** 是否是static属性 */
	public boolean isStatic=false;
	
	public void readCSKR(String str)
	{
		cskr=str;
		
		str=str.toLowerCase();
		
		for(int i=0;i<str.length();i++)
		{
			switch(str.charAt(i))
			{
				case 's':
				{
					hasServer=true;
				}
					break;
				case 'c':
				{
					hasClient=true;
				}
					break;
				case 'k':
				{
					isKey=true;
				}
					break;
				case 'r':
				{
					markType=ConfigFieldMarkType.Resource;
				}
					break;
				case 'l':
				{
					markType=ConfigFieldMarkType.Language;
				}
					break;
				case 'i':
				{
					markType=ConfigFieldMarkType.InternationalResource;
				}
					break;
				case 't':
				{
					markType=ConfigFieldMarkType.TimeExpression;
				}
					break;
			}
		}
	}
	
	/** 读类型 */
	public boolean readType(String str)
	{
		type=str;
		
		if(toReadType(str))
		{
			firstType=typeList.get(0);
			
			types=new int[typeList.length()];
			
			int j=0;
			for(int i=typeList.length() - 1;i >= 0;--i)
			{
				types[j++]=typeList.get(i);
			}
			
			return true;
		}
		
		return false;
	}
	
	private boolean toReadType(String str)
	{
		//数组
		if(str.endsWith("[]"))
		{
			typeList.add(ConfigFieldType.Array);
			
			String ss=str.substring(0,str.length() - 2);
			
			toReadType(ss);
		}
		else
		{
			int bType=toReadBaseType(str);
			
			if(bType==-1)
			{
				return false;
			}
			
			typeList.add(bType);
		}
		
		return true;
	}
	
	private int toReadBaseType(String str)
	{
		int re=0;
		
		//兼容大写
		str=str.toLowerCase();
		
		switch(str)
		{
			case "bool":
			case "boolean":
			{
				re=ConfigFieldType.Boolean;
			}
				break;
			case "byte":
			case "int8":
			{
				re=ConfigFieldType.Byte;
			}
			break;
			case "ubyte":
			case "unsignedbyte":
			case "uint8":
			{
				re=ConfigFieldType.UByte;
			}
				break;
			case "short":
			case "int16":
			{
				re=ConfigFieldType.Short;
			}
				break;
			case "ushort":
			case "unsignedshort":
			case "uint16":
			{
				re=ConfigFieldType.UShort;
			}
				break;
			case "int":
			case "int32":
			{
				re=ConfigFieldType.Int;
			}
				break;
			case "long":
			case "int64":
			{
				re=ConfigFieldType.Long;
			}
				break;
			case "float":
			{
				re=ConfigFieldType.Float;
			}
				break;
			case "double":
			case "number":
			{
				re=ConfigFieldType.Double;
			}
				break;
			case "string":
			case "str":
			{
				re=ConfigFieldType.String;
			}
				break;
			case "dint":
			{
				re=ConfigFieldType.DInt;
			}
				break;
			case "tp":
			case "tpercent":
			{
				re=ConfigFieldType.TP;
			}
				break;
			case "wp":
			case "wpercent":
			{
				re=ConfigFieldType.WP;
			}
				break;
			case "bf":
			case "bigfloat":
			{
				re=ConfigFieldType.BigFloat;
			}
				break;
			default:
			{
				return -1;
			}
		}
		
		return re;
	}
}
