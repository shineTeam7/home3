package com.home.shineTool.dataEx;

import com.home.shine.ctrl.Ctrl;
import com.home.shineTool.constlist.CodeType;
import com.home.shineTool.constlist.ConfigFieldType;
import com.home.shineTool.reflect.cls.ClassInfo;
import com.home.shineTool.reflect.code.CodeInfo;
import com.home.shineTool.tool.config.ConfigMakeTool;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 配置表类数据 */
public class ConfigClassData
{
	/** 标识名字 */
	public String fName;
	
	public File file;
	
	/** 是否有父 */
	public boolean hasParent;
	
	public ClassInfo clientCls;
	
	public ClassInfo serverCls;
	
	public Set<String> clientSuperFields=new HashSet<>();
	
	public Set<String> serverSuperFields=new HashSet<>();
	
	/** 是否有客户端字段 */
	public boolean hasClient=false;
	/** 是否有服务器字段 */
	public boolean hasServer=false;
	
	/** 是否有客户端非主键字段 */
	public boolean hasCFields=false;
	/** 是否有服务器非主键字段 */
	public boolean hasSFields=false;
	
	/** 是否有新增的客户端字段 */
	public boolean hasNewCField=false;
	/** 是否有新增的服务器字段 */
	public boolean hasNewSField=false;
	
	public ConfigFieldKeyData[] keyArr;
	
	/** 资源字段组 */
	public List<String> resourceKeys=new ArrayList<>();
	/** 国际化文本字段组 */
	public List<String> languageKeys=new ArrayList<>();
	/** 国际化资源字段组 */
	public List<String> internationalResourceKeys=new ArrayList<>();
	/** 时间表达式字段组 */
	public List<String> timeExpressionKeys=new ArrayList<>();
	/** 大float组 */
	public List<ConfigFieldData> bigFloatKeys=new ArrayList<>();
	
	/** 使用数组存储 */
	public boolean useArr=false;
	
	/** 克隆一个 */
	public ConfigClassData clone()
	{
		ConfigClassData re=new ConfigClassData();
		re.fName=fName;
		re.hasClient=hasClient;
		re.hasServer=hasServer;
		re.hasCFields=hasCFields;
		re.hasSFields=hasSFields;
		
		return re;
	}
	
	/** 获取键的组合类型 */
	public int getKeysCombineType()
	{
		int num=0;
		
		for(ConfigFieldKeyData v : keyArr)
		{
			switch(v.type)
			{
				case ConfigFieldType.String:
				case ConfigFieldType.TP:
				case ConfigFieldType.WP:
				case ConfigFieldType.BigFloat:
				{
					return ConfigFieldType.String;
				}
				case ConfigFieldType.Byte:
				case ConfigFieldType.UByte:
				{
					num+=1;
				}
				break;
				case ConfigFieldType.Short:
				case ConfigFieldType.UShort:
				{
					num+=2;
				}
				break;
				case ConfigFieldType.Int:
				{
					num+=4;
				}
				break;
				case ConfigFieldType.Long:
				{
					num+=8;
				}
				break;
			}
		}
		
		if(num<=4)
		{
			return ConfigFieldType.Int;
		}
		
		if(num<=8)
		{
			return ConfigFieldType.Long;
		}
		
		return ConfigFieldType.String;
	}
	
	/** 获取键的组合值 */
	public String getKeysCombineValue(String front,CodeInfo code)
	{
		int keyType=getKeysCombineType();
		
		String re="";
		
		switch(keyType)
		{
			case ConfigFieldType.Long:
			case ConfigFieldType.Int:
			{
				boolean isLong=keyType==ConfigFieldType.Long;
				
				StringBuilder sb=new StringBuilder();
				
				int num=0;
				
				for(int i=keyArr.length - 1;i >= 0;--i)
				{
					ConfigFieldKeyData v=keyArr[i];
					
					if(num>0)
					{
						//ts的long型
						if(code.getCodeType()==CodeType.TS && isLong)
						{
							sb.insert(0," + ");
							sb.insert(0," * " + String.valueOf((long)1<<(8 * num)));
						}
						else
						{
							sb.insert(0," | ");
							sb.insert(0," << " + (8 * num));
						}
					}
					
					String vs=front + v.name;
					
					//long型，并且不是ts
					if(isLong && code.getCodeType()!=CodeType.TS)
					{
						vs=code.getVarTypeTrans(vs,code.Long);
					}
					
					sb.insert(0,vs);
					
					num+=ConfigMakeTool.getShift(v.type);
				}
				
				re=sb.toString();
			}
			break;
			case ConfigFieldType.String:
			{
				StringBuilder sb=new StringBuilder();
				
				for(int i=0;i<keyArr.length;++i)
				{
					ConfigFieldKeyData v=keyArr[i];
					
					if(i>0)
					{
						sb.append("+\"_\"+");
					}
					
					sb.append(front + v.name);
				}
				
				re=sb.toString();
			}
			break;
		}
		
		return re;
	}
}
