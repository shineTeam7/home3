using System;
using ShineEngine;

/// <summary>
///
/// </summary>
public class EventAction:SBaseEventRegister<object>.BaseEventAction
{
	public static EventAction create(Action action)
	{
		EventAction re=new EventAction();
		re.func=action;
		return re;
	}

	public static EventAction create(Action<object> action)
	{
		EventAction re=new EventAction();
		re.func2=action;
		return re;
	}
}