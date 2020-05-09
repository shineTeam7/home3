package com.home.shineTool.tool.data;

import com.home.shineTool.constlist.MakeMethodType;
import com.home.shineTool.reflect.cls.ClassInfo;
import com.home.shineTool.tool.base.BaseOutputInfo;

/** 数据输出类 */
public class DataOutputInfo extends BaseOutputInfo
{
	/** 是否使用create传参(为了协议,还有copyData这一步) */
	public boolean isCreateSetParams=false;
	/** createSet是否启用池(http不启用) */
	public boolean createSetUsePool=false;
	
	/** 是否是协议推送 */
	public boolean isRequest=false;
	/** 是否是协议接收 */
	public boolean isResponse=false;
	
	public boolean[] needs=new boolean[MakeMethodType.num];
	//
	//	//6
	//	public boolean needReadFull=true;
	//	public boolean needWriteFull=true;
	//	public boolean needReadSimple=true;
	//	public boolean needWriteSimple=true;
	//	public boolean needCopy=true;
	//	public boolean needShadowCopy=true;
	
	/** 是否复写基类的读写方法 */
	public boolean isOverrideSuper=false;
	
	/** 基类cls */
	public ClassInfo superCls;
	
	//temp

	public DataOutputInfo()
	{
		
	}
	
	public void needAll()
	{
		needs[MakeMethodType.readFull]=true;
		needs[MakeMethodType.writeFull]=true;
		needs[MakeMethodType.readSimple]=true;
		needs[MakeMethodType.writeSimple]=true;
		needs[MakeMethodType.copy]=true;
		needs[MakeMethodType.shadowCopy]=true;
		needs[MakeMethodType.dataEquals]=true;
		needs[MakeMethodType.toWriteDataString]=true;
		needs[MakeMethodType.initDefault]=true;
		needs[MakeMethodType.initFields]=false;
		//needs[MakeMethodType.clear]=true;
		needs[MakeMethodType.release]=true;
	}
	
	public boolean needReadFull()
	{
		return needs[MakeMethodType.readFull];
	}
	
	public boolean needWriteFull()
	{
		return needs[MakeMethodType.writeFull];
	}
	
	public boolean needReadSimple()
	{
		return needs[MakeMethodType.readSimple];
	}
	
	public boolean needWriteSimple()
	{
		return needs[MakeMethodType.writeSimple];
	}
	
	public boolean needCopy()
	{
		return needs[MakeMethodType.copy];
	}
	
	public boolean needShadowCopy()
	{
		return needs[MakeMethodType.shadowCopy];
	}
	
	public boolean needDataEquals()
	{
		return needs[MakeMethodType.dataEquals];
	}
	
	public boolean needToWriteDataString()
	{
		return needs[MakeMethodType.toWriteDataString];
	}
	
	public boolean needInitDefault()
	{
		return needs[MakeMethodType.initDefault];
	}
	
	public boolean needInitFields()
	{
		return needs[MakeMethodType.initFields];
	}
	
	public boolean needClear()
	{
		return needs[MakeMethodType.clear];
	}
	
	public boolean needRelease()
	{
		return needs[MakeMethodType.release];
	}
}
