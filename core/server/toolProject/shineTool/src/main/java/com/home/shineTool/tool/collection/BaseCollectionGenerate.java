package com.home.shineTool.tool.collection;

import com.home.shineTool.constlist.CodeType;
import com.home.shineTool.constlist.VarType;
import com.home.shineTool.global.ShineProjectPath;
import com.home.shineTool.global.ShineToolGlobal;
import com.home.shineTool.global.ShineToolSetting;
import com.home.shineTool.reflect.code.CodeInfo;
import com.home.shineTool.reflect.code.JavaCodeInfo;
import com.home.shineTool.utils.ToolFileUtils;

public class BaseCollectionGenerate
{
	protected CodeInfo _javaCodeInfo;
	
	protected VarInfo[] _types=new VarInfo[VarType.size];
	
	protected TypeInfo[] _codeTypes=new TypeInfo[CodeType.size];
	
	public BaseCollectionGenerate()
	{
	
	}
	
	public void init()
	{
		_javaCodeInfo=CodeInfo.getCode(CodeType.Java);
		
		TypeInfo javaType=new TypeInfo();
		javaType.codeType=CodeType.Java;
		javaType.dir=ShineProjectPath.serverShineDirPath+"/support/collection/";
		_codeTypes[javaType.codeType]=javaType;
		
		TypeInfo csType=new TypeInfo();
		csType.codeType=CodeType.CS;
		csType.dir=ShineToolGlobal.clientMainSrcPath + "/shine/support/collection/";
		_codeTypes[csType.codeType]=csType;
	}
	
	protected void addVarInfo(VarInfo info)
	{
		_types[info.varType]=info;
	}
	
	public void execute()
	{
		init();
		
		doExecute();
	}
	
	protected void doExecute()
	{
		for(VarInfo varType : _types)
		{
			if(varType!=null)
			{
				for(TypeInfo codeType : _codeTypes)
				{
					if(codeType!=null)
					{
						generateOne(varType,codeType);
					}
				}
			}
		}
	}
	
	protected void generateOne(VarInfo varInfo,TypeInfo typeInfo)
	{
	
	}
	
	protected class TypeInfo
	{
		public int codeType;
		
		public String dir;
	}
	
	protected class VarInfo
	{
		public int varType;
	}
	
	protected VarInfo newVarInfo(int type)
	{
		VarInfo re=new VarInfo();
		re.varType=type;
		
		addVarInfo(re);
		return re;
	}
}
