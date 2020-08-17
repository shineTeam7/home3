package com.home.commonBase.support.func;

import com.home.commonBase.trigger.TriggerArg;
import com.home.commonBase.trigger.TriggerExecutor;
import com.home.shine.data.trigger.TriggerFuncData;

public interface StringTriggerFunc
{
	String apply(TriggerExecutor e,TriggerFuncData func,TriggerArg arg);
}
