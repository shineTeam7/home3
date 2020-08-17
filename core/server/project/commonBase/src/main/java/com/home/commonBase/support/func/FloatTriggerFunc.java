package com.home.commonBase.support.func;

import com.home.commonBase.trigger.TriggerArg;
import com.home.commonBase.trigger.TriggerExecutor;
import com.home.shine.data.trigger.TriggerFuncData;

public interface FloatTriggerFunc
{
	float apply(TriggerExecutor e,TriggerFuncData func,TriggerArg arg);
}
