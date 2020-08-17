package com.home.commonBase.tool;

import com.home.commonBase.support.func.ObjectTriggerFunc;
import com.home.commonBase.support.func.TriggerFuncEntry;
import com.home.shine.tool.ArrayDic;

public class TriggerFuncMaker extends ArrayDic<TriggerFuncEntry>
{
	public TriggerFuncMaker()
	{
		super(TriggerFuncEntry[]::new);
	}
}
