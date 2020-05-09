package com.home.shineTool.tool.export;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.SMap;
import com.home.shineTool.constlist.DataGroupType;
import com.home.shineTool.constlist.VisitType;
import com.home.shineTool.global.ShineToolSetting;
import com.home.shineTool.reflect.FieldInfo;
import com.home.shineTool.reflect.MethodArgInfo;
import com.home.shineTool.reflect.MethodInfo;
import com.home.shineTool.reflect.cls.ClassInfo;
import com.home.shineTool.reflect.code.CodeWriter;
import com.home.shineTool.tool.data.DataExportTool;

public class HttpResponseResultExportTool extends DataExportTool
{
	private SMap<String,MethodInfo> _reBackDic=new SMap<>();
	
	private MethodInfo _currentReBackMethod;
	private CodeWriter _currentReBackWriter;
	
	@Override
	protected void toMakeBefore()
	{
		super.toMakeBefore();
		
		//构造赋值 且 有继承
		if(_outputInfo.group==DataGroupType.Server)
		{
			_currentReBackMethod=new MethodInfo();
			_currentReBackMethod.visitType=VisitType.Public;
			_currentReBackMethod.returnType=_outputCls.getCode().Void;
			_currentReBackMethod.name="reBack";
			_currentReBackMethod.describe="收到返回方法";
			
			_currentReBackWriter=_outputCls.createWriter();
			
			//通过基类名判定
			boolean hasExtend=!_outputCls.extendsClsName.equals(ShineToolSetting.dataBaseName);
			
			if(hasExtend)
			{
				doOneExtend(_inputCls.getExtendClsQName());
			}
		}
	}
	
	/** 执行一个基类构造赋值 */
	private void doOneExtend(String qName)
	{
		if(qName.isEmpty())
			return;
		
		ClassInfo cls=getInputClsAbs(qName);
		
		if(cls==null)
		{
			Ctrl.print("未找到继承类",qName);
			return;
		}
		
		if(!cls.extendsClsName.isEmpty())
		{
			//递归
			doOneExtend(cls.getExtendClsQName());
		}
		
		for(String k : cls.getFieldNameList())
		{
			toMakeSuperFieldForReBack(cls.getField(k),cls);
		}
	}
	
	/** 构造superField */
	private void toMakeSuperFieldForReBack(FieldInfo field,ClassInfo cls)
	{
		String fType=getVarType(field.type,false,false,cls);
		
		_currentReBackMethod.args.add(new MethodArgInfo(field.name,fType));
		_currentReBackWriter.writeVarSet("_re."+field.name,field.name);
	}
	
	@Override
	protected void toMakeOneField(FieldInfo field,FieldInfo outField)
	{
		super.toMakeOneField(field,outField);
		
		String fType=getVarType(field.type,false,false);
		
		if(_outputInfo.group==DataGroupType.Server)
		{
			_currentReBackMethod.args.add(new MethodArgInfo(field.name,fType));
			_currentReBackWriter.writeVarSet("_re."+field.name,field.name);
		}
	}
	
	@Override
	protected void toMakeAfter()
	{
		super.toMakeAfter();
		
		if(_outputInfo.group==DataGroupType.Server)
		{
			_currentReBackWriter.writeCustom("resultData(_re);");
			_currentReBackWriter.writeEnd();
			
			_currentReBackMethod.content=_currentReBackWriter.toString();
			
			////移除一次
			//_outputCls.removeMethodByName(_currentReBackMethod.name);
			//_outputCls.addMethod(_currentReBackMethod);
			
			_reBackDic.put(_outputCls.getQName(),_currentReBackMethod);
		}
	}
	
	public MethodInfo getReBackMethod(String key)
	{
		return _reBackDic.get(key);
	}
}
