package com.home.shineTool.tool.trigger;

import com.home.shine.utils.StringUtils;
import com.home.shineTool.constlist.DataGroupType;
import com.home.shineTool.reflect.FieldInfo;
import com.home.shineTool.reflect.cls.ClassInfo;
import com.home.shineTool.tool.base.BaseExportTool;
import com.home.shineTool.tool.data.DataDefineTool;

public class TriggerObjectDefineTool extends BaseExportTool
{
	private TriggerMakeTool _makeTool;
	
	public TriggerObjectDefineTool(TriggerMakeTool makeTool)
	{
		_makeTool=makeTool;
	}
	
	@Override
	protected void toMakeInput()
	{
	
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
	protected void endExecute()
	{
		super.endExecute();
		
		ClassInfo cls=getDefineClass(DataGroupType.Server);
		
		//DataDefineTool.getDefineDicFromCls(_makeTool.objectDefineDic,cls,true);
		
		for(String s : cls.getFieldNameList())
		{
			FieldInfo field=cls.getField(s);
			
			if(!field.name.equals("off") && !field.name.equals("count"))
			{
				String fName=field.name+_mark;
				
				_makeTool.objectDefineDic.put(fName,Integer.parseInt(field.defaultValue));
				
				_makeTool.objectStrDefineDic.put(fName,cls.clsName+"."+field.name);
			}
		}
		
	}
}