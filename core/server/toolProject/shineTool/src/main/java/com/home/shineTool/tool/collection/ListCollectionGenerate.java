package com.home.shineTool.tool.collection;

import com.home.shine.support.collection.IntList;
import com.home.shine.utils.StringUtils;
import com.home.shineTool.constlist.CodeType;
import com.home.shineTool.constlist.VarType;
import com.home.shineTool.global.ShineToolGlobal;
import com.home.shineTool.reflect.cls.ClassInfo;
import com.home.shineTool.reflect.code.CodeInfo;
import com.home.shineTool.utils.ToolFileUtils;

public class ListCollectionGenerate extends BaseCollectionGenerate
{
	public ListCollectionGenerate()
	{
	
	}
	
	@Override
	public void init()
	{
		super.init();
		
		newVarInfo(VarType.CustomObject);
		newVarInfo(VarType.Int);
		newVarInfo(VarType.Long);
		newVarInfo(VarType.Char);
	}
	
	private String getFileName(int type)
	{
		switch(type)
		{
			case VarType.CustomObject:
				return "SList";
			default:
				return StringUtils.ucWord(_javaCodeInfo.getVarStr(type))+"List";
		}
	}
	
	private String getClsName(int type)
	{
		switch(type)
		{
			case VarType.CustomObject:
				return "SList<V>";
			default:
				return StringUtils.ucWord(_javaCodeInfo.getVarStr(type))+"List";
		}
	}
	
	@Override
	protected void generateOne(VarInfo varInfo,TypeInfo typeInfo)
	{
		CodeInfo code=CodeInfo.getCode(typeInfo.codeType);
		
		ClassInfo cls=ClassInfo.getClassInfoFromPathAbs(typeInfo.dir+getFileName(varInfo.varType)+"."+CodeType.getExName(typeInfo.codeType));
		
		cls.clsName=getClsName(typeInfo.codeType);
		
	}
}
