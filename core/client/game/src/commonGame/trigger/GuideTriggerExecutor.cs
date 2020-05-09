using System;
using ShineEngine;

/// <summary>
/// 引导trigger执行器
/// </summary>
public class GuideTriggerExecutor:TriggerExecutor
{
	protected override void toDoActionFunc(TriggerFuncData func,TriggerArg arg)
	{
		switch(func.id)
		{
			case TriggerFunctionType.SetGuideMainStep:
			{
				GameC.player.guide.setMainStep(getInt(func.args[0],arg));
			}
				break;
			case TriggerFunctionType.ShowUI:
			{
				//TODO:实现
				// GameC.ui.showUIByType(getInt(func.args[0],runner.arg));
			}
				break;
			case TriggerFunctionType.HideUI:
			{
				//TODO:实现
				// GameC.ui.showUIByType(getInt(func.args[0],runner.arg));
			}
				break;
			default:
			{
				base.toDoActionFunc(func,arg);
			}
				break;
		}
	}


	protected override int toGetIntFuncValue(TriggerFuncData func,TriggerArg arg)
	{
		switch(func.id)
		{
			case TriggerFunctionType.GetGuideMainStep:
				return GameC.player.guide.getMainStep();
			default:
				return base.toGetIntFuncValue(func,arg);
		}
	}
}