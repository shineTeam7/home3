using System;
using ShineEngine;

/// <summary>
/// 显示控制
/// </summary>
public class ViewControl:SEventRegister
{
	private static ViewControl _instance;

	/** 单例 */
	public static ViewControl instance
	{
		get
		{
			if(_instance==null)
			{
				_instance=new ViewControl();
			}

			return _instance;
		}
	}

	public ViewControl()
	{

	}
}