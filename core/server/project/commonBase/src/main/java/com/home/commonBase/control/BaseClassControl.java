package com.home.commonBase.control;

import com.home.commonBase.baseData.BasePart;
import com.home.commonBase.constlist.scene.UnitLogicMethodType;
import com.home.commonBase.scene.base.UnitLogicBase;
import com.home.commonBase.tool.ClassMethodMarkTool;
import com.home.shine.support.collection.SMap;

public class BaseClassControl
{
	public ClassMethodMarkTool unitLogicMark=new ClassMethodMarkTool(UnitLogicBase.class,10);
	
	public void init()
	{
		unitLogicMark.addMethod(UnitLogicMethodType.onFrame,"onFrame");
		unitLogicMark.addMethod(UnitLogicMethodType.onSecond,"onSecond");
		unitLogicMark.addMethod(UnitLogicMethodType.onPiece,"onPiece");
	}
}
