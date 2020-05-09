package com.home.shineTool.tool.trigger;

import com.home.shine.utils.StringUtils;
import com.home.shineTool.constlist.DataGroupType;
import com.home.shineTool.constlist.ProjectType;
import com.home.shineTool.constlist.VisitType;
import com.home.shineTool.reflect.FieldInfo;
import com.home.shineTool.reflect.MethodArgInfo;
import com.home.shineTool.reflect.MethodInfo;
import com.home.shineTool.reflect.cls.ClassInfo;
import com.home.shineTool.tool.base.BaseExportTool;
import com.home.shineTool.tool.data.DataDefineTool;

import java.io.File;

/** trigger函数定义 */
public class TriggerFunctionDefineTool extends BaseExportTool
{
	private TriggerMakeTool _makeTool;
	
	private DataDefineTool _serverEventDefine;
	private DataDefineTool _clientEventDefine;
	
	public TriggerFunctionDefineTool(TriggerMakeTool makeTool)
	{
		_makeTool=makeTool;
	}
	
	public void setEventDefine(DataDefineTool serverTool,DataDefineTool clientTool)
	{
		_serverEventDefine=serverTool;
		_clientEventDefine=clientTool;
	}
	
	@Override
	protected void preExecute()
	{
		super.preExecute();
		
		//common
		if(_makeTool.getParentTool()==null)
		{
			addSystemMethod("if","如果","boolean");
			addSystemMethod("while","循环","boolean");
		}
	}
	
	@Override
	protected void toMakeInput()
	{
		for(String s : _inputCls.getMethodKeyList())
		{
			MethodInfo method=_inputCls.getMethod(s);
			
			//不是虚函数
			if(!method.isAbstract)
			{
				addOneMethod(method);
			}
		}
	}
	
	@Override
	protected void toMakeBefore()
	{
	
	}
	
	@Override
	protected void toMakeOneField(FieldInfo field,FieldInfo outField)
	{
	
	}
	
	@Override
	protected void toMakeAfter()
	{
	
	}
	
	@Override
	protected int addOneDefine(String cName,String des)
	{
		//不用默认
		return -1;
	}
	
	@Override
	protected boolean checkFileNeedDo(File f)
	{
		//全开
		return true;
	}
	
	//@Override
	//protected void endExecute()
	//{
	//	super.endExecute();
	//
	//	ClassInfo cls=getDefineClass(DataGroupType.Server);
	//
	//	DataDefineTool.getDefineDicFromCls(_makeTool.functionDefineDic,cls,true);
	//}
	
	private void addOneMethod(MethodInfo method)
	{
		String cName=StringUtils.ucWord(method.name);
		
		int index=doAddOneDefine(cName,method.describe);
		
		_makeTool.functionDefineDic.put(method.name,index);
		
		int type=3;
		
		if(_inputCls!=null && _inputCls.hasAnnotation("OnlyC"))
		{
			type=1;
		}
		else if(_inputCls!=null && _inputCls.hasAnnotation("OnlyS"))
		{
			type=2;
		}
		
		_makeTool.functionDefineTypeDic.put(index,type);
		
		addMethodContent(index,method);
		
		//判定为event
		if(_makeTool.isEventFunc(method.name))
		{
			_serverEventDefine.addOne(cName,method.describe,index);
			_clientEventDefine.addOne(cName,method.describe,index);
		}
	}
	
	/** 添加系统方法 */
	private void addSystemMethod(String name,String describe,String...args)
	{
		MethodInfo method=new MethodInfo();
		method.visitType=VisitType.Protected;
		method.name=name;
		method.describe=describe;
		for(int i=0;i<args.length;i++)
		{
			method.args.add(new MethodArgInfo("arg"+(i+1),args[i]));
		}
		
		addOneMethod(method);
	}
	
	private void addMethodContent(int index,MethodInfo method)
	{
		////特殊处理部分
		//switch(method.name)
		//{
		//	case "runTrigger":
		//	case "runTriggerAbs":
		//	{
		//		method=method.clone();
		//		method.args.get(0).type=_inCode.Int;//该类型
		//	}
		//		break;
		//}
		
		_makeTool.functionInfoDic.put(index,method);
	}
}
