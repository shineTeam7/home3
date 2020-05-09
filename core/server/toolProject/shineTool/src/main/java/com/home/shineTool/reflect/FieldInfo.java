package com.home.shineTool.reflect;

import com.home.shineTool.constlist.VisitType;

/** 值属性信息 */
public class FieldInfo extends PropertyInfo
{
	/** 属性类型 */
	public String type;
	/** 是否常量 */
	public boolean isConst=false;
	/** 是否readonly(C#用) */
	public boolean isReadOnly=false;
	/** 是否是volatile */
	public boolean isVolatile=false;
	/** 默认值 */
	public String defaultValue="";
	
	//以下是Data构造
	/** 是否可能为空(Java DataExport用) */
	public boolean maybeNull=false;
	///** 是否可能被继承(Java DataExport用) */
	//public boolean maybeExtends=false;
	/** map的keyInValue判定 */
	public String mapKeyInValueKey="";
	
	/** 是否不需要类型升级 */
	public boolean noUpgrade=false;
	
	/** 获取使用的字段名(是否加this) */
	public String getUseFieldName()
	{
		return visitType==VisitType.Public ? "this." + name : name;
	}
	
	@Override
	public void copy(PropertyInfo target)
	{
		super.copy(target);
		
		FieldInfo field=(FieldInfo)target;
		
		type=field.type;
		isConst=field.isConst;
		isReadOnly=field.isReadOnly;
		isVolatile=field.isVolatile;
		defaultValue=field.defaultValue;
		maybeNull=field.maybeNull;
		mapKeyInValueKey=field.mapKeyInValueKey;
		noUpgrade=field.noUpgrade;
	}
}
