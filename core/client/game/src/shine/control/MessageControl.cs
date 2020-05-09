using System;
using ShineEngine;

/// <summary>
/// 消息控制(成对消息)
/// </summary>
public class MessageControl
{
	/** 需要绑定的request组 */
	private IntObjectMap<IntSet> _requestSet=new IntObjectMap<IntSet>();


	/** 当前等待中的请求ID */
	private int _currentRequestID=-1;
	/** 失效时间 */
	private long _timeOut=0;
	/** response唤醒组 */
	private IntSet _responseReSet;

	public void init()
	{
		TimeDriver.instance.setFrame(onFrame);
		generateRegist();
	}

	private static void onFrame(int delay)
	{

	}

	/** 生成注册 */
	protected virtual void generateRegist()
	{

	}

	protected void registOne(int requestID,params int[] responses)
	{
		IntSet dd=new IntSet();
		dd.addAll(responses);
		_requestSet.put(requestID,dd);

		// foreach(int id in responses)
		// {
		// 	_responseReDic.computeIfAbsent(id,v=>new IntList()).add(requestID);
		// }
	}

	/** 检测消息是否可以发出 */
	public bool checkRequest(int requestID)
	{
		IntSet intSet=_requestSet.get(requestID);

		//TODO:待会继续

		if(_currentRequestID==-1)
		{

		}

		return true;
	}
}