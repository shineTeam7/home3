using System;
using ShineEngine;

/// <summary>
/// 消息派发器
/// </summary>
[Hotfix]
public class SEventRegister:SBaseEventRegister<object>
{
	protected NormalEvt _nEvt=new NormalEvt();

	public NormalEvt nEvt
	{
		get {return _nEvt;}
	}

	/** 派发默认消息 */
	public void dispatchNormal(int type)
	{
		dispatch(type,_nEvt);
	}
}