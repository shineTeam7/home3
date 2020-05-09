package com.home.shineTool.tool.base;

import com.home.shine.bytes.BytesReadStream;
import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.SList;
import com.home.shine.support.collection.SMap;
import com.home.shine.support.collection.SSet;
import com.home.shineTool.constlist.ExecuteReleaseType;
import com.home.shineTool.constlist.VarType;
import com.home.shineTool.global.ShineToolSetting;
import com.home.shineTool.reflect.FieldInfo;
import com.home.shineTool.reflect.cls.ClassInfo;
import com.home.shineTool.reflect.code.CodeInfo;

/** 记录的数据类信息数据 */
public class RecordDataClassInfo
{
	/** 类名 */
	public String clsQName;
	/** 超类名 */
	public String superQName;
	/** 是否可能被继承 */
	public boolean mayBeExtends=true;
	/** 客户端服务器类型(0:默认全部,1:server,2:client) */
	public int csType;
	
	/** 属性组 */
	public SList<DataFieldInfo> fieldList=new SList<>(DataFieldInfo[]::new);
	/** 属性组 */
	public SMap<String,DataFieldInfo> fieldMap=new SMap<>();
	
	
	//temp
	/** 继承组 */
	public SSet<String> childrenQNameList=new SSet<>();
	
	/** 是否存在不兼容修改 */
	public boolean hasBigModified=false;
	
	/** 是否临时 */
	public boolean isTemp=false;
	
	/** 写字节流 */
	public void writeBytes(BytesWriteStream stream)
	{
		stream.writeUTF(clsQName);
		stream.writeUTF(superQName);
		stream.writeBoolean(mayBeExtends);
		
		stream.writeByte(csType);
		
		stream.writeLen(fieldList.size());
		
		for(int i=0;i<fieldList.length();++i)
		{
			fieldList.get(i).writeBytes(stream);
		}
	}
	
	public void readBytes(BytesReadStream stream)
	{
		clsQName=stream.readUTF();
		superQName=stream.readUTF();
		mayBeExtends=stream.readBoolean();
		
		csType=stream.readByte();
		
		DataFieldInfo field;
		
		int len=stream.readLen();
		
		for(int i=0;i<len;i++)
		{
			field=new DataFieldInfo();
			field.readBytes(stream);
			fieldList.add(field);
			fieldMap.put(field.name,field);
		}
	}
	
	/** 转String */
	public String toString()
	{
		StringBuilder sb=new StringBuilder();
		writeString(sb,true);
		return sb.toString();
	}
	
	public void writeString(StringBuilder sb,boolean needFieldName)
	{
		sb.append(clsQName);
		sb.append(":");
		sb.append(superQName);
		sb.append(":");
		sb.append(mayBeExtends ? "true" : "false");
		sb.append(":");
		sb.append(csType);
		sb.append(":");
		
		for(int i=0;i<fieldList.length();++i)
		{
			if(i>0)
			{
				sb.append("|");
			}
			
			fieldList.get(i).writeString(sb,needFieldName);
		}
	}
	
	/** 从字符串读取 */
	public void readString(String str)
	{
		String[] arr=str.split(":",5);
		clsQName=arr[0];
		superQName=arr[1];
		mayBeExtends=arr[2]!=null && arr[2].equals("true");
		csType=arr[3]!=null ? Integer.parseInt(arr[3]) : 0;
		
		if(!arr[4].isEmpty())
		{
			String[] arr2=arr[4].split("\\|");
			
			for(int i=0;i<arr2.length;++i)
			{
				DataFieldInfo ff=new DataFieldInfo();
				ff.readString(arr2[i]);
				
				fieldList.add(ff);
				fieldMap.put(ff.name,ff);
			}
		}
	}
	
	public RecordDataClassInfo copy()
	{
		RecordDataClassInfo re=new RecordDataClassInfo();
		re.clsQName=this.clsQName;
		re.superQName=this.superQName;
		re.mayBeExtends=this.mayBeExtends;
		re.csType=this.csType;
		
		//这两个浅表就可以了
		re.fieldList=this.fieldList;
		re.fieldMap=this.fieldMap;
		
		//这个深表
		re.childrenQNameList=new SSet<>();
		
		for(String v : this.childrenQNameList)
		{
			re.childrenQNameList.add(v);
		}
		
		re.hasBigModified=this.hasBigModified;
		
		return re;
	}
	
	/** 跟旧类比较,范围是否存在不兼容修改 */
	public boolean compareWithOld(RecordDataClassInfo cls)
	{
		if(cls==null)
			return false;
		
		if(!this.clsQName.equals(cls.clsQName))
		{
			bigModifiedError(cls,"类名不匹配");
			return true;
		}
		
		if(!this.superQName.equals(cls.superQName))
		{
			bigModifiedError(cls,"基类变化");
			return true;
		}
		
		if(this.mayBeExtends!=cls.mayBeExtends)
		{
			bigModifiedError(cls,"mayBeExtends变化");
			return true;
		}
		
		if(this.csType!=cls.csType)
		{
			bigModifiedError(cls,"csType变化");
			return true;
		}
		
		for(int i=0;i<cls.fieldList.length();++i)
		{
			DataFieldInfo oldF=cls.fieldList.get(i);
			
			if(i >= this.fieldList.length())
			{
				bigModifiedError(cls,"字段数减少(删除了字段)");
				return true;
			}
			
			DataFieldInfo ff=this.fieldList.get(i);
			
			//if(!oldF.name.equals(ff.name))
			//{
			//	bigModifiedError(cls,"字段名不匹配:"+oldF.name+"->"+ff.name);
			//	return true;
			//}
			
			if(!oldF.type.equals(ff.type))
			{
				int oldTypeInt=oldF.getTypeInt();
				int typeInt=ff.getTypeInt();
				
				//均为自定义对象
				if(oldTypeInt==VarType.CustomObject && typeInt==VarType.CustomObject)
				{
					RecordDataClassInfo oldFieldCls=BaseExportTool.oldRecordClsDic.get(oldF.dataType);
					RecordDataClassInfo newFieldCls=BaseExportTool.newRecordClsDic.get(ff.dataType);
					
					//类型扩展
					if(BaseExportTool.isExtendFrom(newFieldCls,oldF.dataType))
					{
						Ctrl.print("字段类型扩展",ff.name,oldF.dataType,ff.dataType);
					}
					//类型缩减
					else if(BaseExportTool.isExtendFrom(oldFieldCls,ff.dataType))
					{
						Ctrl.print("字段类型缩减",ff.name,oldF.dataType,ff.dataType);
					}
					else
					{
						bigModifiedError(cls,"字段类型不匹配:"+oldF.name);
						return true;
					}
				}
				else
				{
					//不可升级
					if(!VarType.canUpgrade(oldTypeInt,typeInt))
					{
						bigModifiedError(cls,"字段类型不匹配:"+oldF.name);
						return true;
					}
				}
			}
			
			if(oldF.maybeNull!=ff.maybeNull)
			{
				bigModifiedError(cls,"字段maybeNull不匹配:"+oldF.name);
				return true;
			}
			
			if(oldF.mapKeyInValueKey.isEmpty()!=ff.mapKeyInValueKey.isEmpty())
			{
				bigModifiedError(cls,"字段mapKeyInValueKey不匹配:"+oldF.name);
				return true;
			}
		}
		
		return false;
	}
	
	/** 属性信息 */
	public static class DataFieldInfo
	{
		/** 包含类型组 */
		private static SList<String> _innerTypeList=new SList<>();
		
		/** 名字 */
		public String name;
		/** 类型 */
		public String type;
		/** 类型int */
		public int typeInt=-1;
		/** 包含数据类型(外部赋值的完全限定类名) */
		public String dataType="";
		
		/** 是否可能为空(Java DataExport用) */
		public boolean maybeNull=false;
		/** map的keyInValue判定 */
		public String mapKeyInValueKey="";
		
		//NoUpgrade的支持
		
		public void writeBytes(BytesWriteStream stream)
		{
			stream.writeUTF(name);
			stream.writeUTF(type);
			stream.writeUTF(dataType);
			stream.writeBoolean(maybeNull);
			stream.writeUTF(mapKeyInValueKey);
		}
		
		public void readBytes(BytesReadStream stream)
		{
			name=stream.readUTF();
			type=stream.readUTF();
			dataType=stream.readUTF();
			maybeNull=stream.readBoolean();
			mapKeyInValueKey=stream.readUTF();
		}
		
		private int getTypeInt()
		{
			if(typeInt==-1)
			{
				typeInt=CodeInfo.getCode(ShineToolSetting.defaultCodeType).getVarType(type);
			}
			
			return typeInt;
		}
		
		public void writeString(StringBuilder sb,boolean needFieldName)
		{
			if(needFieldName)
			{
				sb.append(name);
				sb.append(';');
			}
			
			sb.append(type);
			sb.append(';');
			sb.append(dataType);
			sb.append(';');
			sb.append(maybeNull ? "true" : "false");
			sb.append(';');
			sb.append(mapKeyInValueKey);
		}
		
		public void readString(String str)
		{
			String[] arr=str.split(";",5);
			
			name=arr[0];
			type=arr[1];
			dataType=arr[2];
			maybeNull=arr[3].equals("true") || arr[3].equals("1");
			mapKeyInValueKey=arr[4];
		}
		
		/** 从cls读取 */
		public void readByCls(String nn,ClassInfo cls)
		{
			FieldInfo field=cls.getField(nn);
			
			name=field.name;
			type=field.type;
			
			cls.getVarInnerType(_innerTypeList,type);
			
			if(!_innerTypeList.isEmpty())
			{
				//超了
				if(_innerTypeList.size()>1)
				{
					Ctrl.throwError("不允许出现自定义类型做key",type);
				}
				
				dataType=_innerTypeList.getLast();
				_innerTypeList.clear();
			}
			
			//去掉"<>";
			type=type.replace("<","$");
			type=type.replace(">","$");
			
			maybeNull=field.maybeNull;
			mapKeyInValueKey=field.mapKeyInValueKey;
		}
	}
	
	
	/** 获取记录类信息 */
	public static RecordDataClassInfo createByClass(ClassInfo cls)
	{
		RecordDataClassInfo info=new RecordDataClassInfo();
		info.clsQName=cls.getQName();
		info.superQName=cls.getExtendClsQName();
		info.mayBeExtends=!cls.isFinal;//不是final的
		
		if(cls.hasAnnotation("OnlyS"))
		{
			info.csType=1;
		}
		else if(cls.hasAnnotation("OnlyC"))
		{
			info.csType=2;
		}
		else
		{
			info.csType=0;
		}
		
		for(String v : cls.getFieldNameList())
		{
			DataFieldInfo ff=new DataFieldInfo();
			
			ff.readByCls(v,cls);
			
			info.fieldList.add(ff);
			info.fieldMap.put(ff.name,ff);
		}
		
		return info;
	}
	
	public static RecordDataClassInfo createByString(String str)
	{
		RecordDataClassInfo re=new RecordDataClassInfo();
		
		try
		{
			re.readString(str);
		}
		catch(Exception e)
		{
			Ctrl.warnLog("解析出错,版本升级问题",str);
			return null;
		}
		
		return re;
	}
	
	/** 不兼容修改 */
	public static void bigModifiedError(RecordDataClassInfo cls,String reason)
	{
		if(ShineToolSetting.isDataExport)
		{
			if(BaseExportTool.hasOldRecordDic && ExecuteReleaseType.isRelease(ShineToolSetting.releaseType))
			{
				Ctrl.print("不可逆修改(正式)",cls.clsQName,reason);
			}
			else
			{
				Ctrl.print("不可逆修改",cls.clsQName,reason);
			}
		}
		
	}
	
	/** 不兼容修改 */
	public static void bigModifiedErrorStr(String str)
	{
		if(ShineToolSetting.isDataExport)
		{
			if(BaseExportTool.hasOldRecordDic && ExecuteReleaseType.isRelease(ShineToolSetting.releaseType))
			{
				Ctrl.print("出现不兼容的修改(正式)",str);
			}
			else
			{
				Ctrl.print("出现不兼容的修改",str);
			}
		}
	}
}
