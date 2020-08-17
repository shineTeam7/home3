package com.home.shineTool.tool.trigger;

import com.home.shine.utils.StringUtils;
import com.home.shineTool.reflect.FieldInfo;
import com.home.shineTool.tool.base.BaseExportTool;

/** group定义 */
public class TriggerGroupDefineTool extends BaseExportTool
{
	private TriggerMakeTool _makeTool;
	
	public TriggerGroupDefineTool(TriggerMakeTool makeTool)
	{
		_makeTool=makeTool;
	}
	
	@Override
	protected void toMakeInput()
	{
		for(String s : _inputCls.getFieldNameList())
		{
			FieldInfo field=_inputCls.getField(s);
			
			String cName=StringUtils.ucWord(field.name);
			
			int index=doAddOneDefine(cName,field.describe,null);
			
			_makeTool.groupDefineDic.put(s,index);
			
			int type=3;
			
			if(field.hasAnnotation("OnlyC"))
			{
				type=1;
			}
			else if(field.hasAnnotation("OnlyS"))
			{
				type=2;
			}
			
			_makeTool.groupDefineTypeDic.put(index,type);
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
	protected int addOneDefine(String cName,String des,String qName)
	{
		//不用默认
		return -1;
	}
	
	@Override
	protected void endExecute()
	{
		super.endExecute();
	}
}
