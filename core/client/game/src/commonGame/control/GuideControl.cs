using System;
using ShineEngine;

/// <summary>
/// 引导控制
/// </summary>
public class GuideControl
{
	private GuideTriggerExecutor _executor;

	/** 构造 */
	public void construct()
	{
		_executor=GameC.factory.createGuideTriggerExecutor();
		_executor.construct();
		TimeDriver.instance.setFrame(onFrame);
	}

	/** 初始化 */
	public void init()
	{
		_executor.init(TriggerGroupType.Guide,1);//默认1
	}

	public void dispose()
	{
		_executor.dispose();
	}

	private void onFrame(int delay)
	{
		_executor.onFrame(delay);
	}

	/** 发生事件 */
	public void triggerEvent(int type)
	{
		_executor.triggerEvent(type);
	}

	/** 发生事件 */
	public void triggerEvent(int type,params object[] args)
	{
		_executor.triggerEvent(type,args);
	}
}