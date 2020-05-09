package com.home.shineTool.tool.hotfix;

import com.home.shine.support.collection.SList;
import com.home.shineTool.constlist.VisitType;
import com.home.shineTool.reflect.MethodArgInfo;

/** ILRuntime方法信息 */
public class ILMethodInfo
{
	/** 方法名 */
	public String name;
	/** 返回类型 */
	public String returnType="";
	/** 访问类型 */
	public int visitType=VisitType.Public;
	/** 是否需要考虑base调用 */
	public boolean needBaseCall=true;
	/** 参数组 */
	public SList<MethodArgInfo> args=new SList<>(k->new MethodArgInfo[k]);
	
	/** 获取方法key */
	public String getKey()
	{
		if(args.isEmpty())
		{
			return name;
		}
		
		StringBuilder sb=new StringBuilder();
		sb.append(name);
		
		for(MethodArgInfo v : args)
		{
			sb.append(",");
			sb.append(v.type);
		}
		
		return sb.toString();
	}
}
