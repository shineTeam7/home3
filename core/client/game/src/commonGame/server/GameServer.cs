using System;
using ShineEngine;

/// <summary>
/// 游戏服务
/// </summary>
[Hotfix]
public class GameServer:SceneBaseServer
{
	/** 当前等待的mid */
	private int _currentRequestMid=-1;
	/** 所需的resonse组 */
	private int[] _currentResponses=null;
	/** 记录消息的时间 */
	private long _requestRecordTime;
	/** 是否显示了delayUI */
	private bool _isShowDelayUI=false;

	public override void initMessage()
	{
		base.initMessage();

		//忽略log
		BytesControl.addIgnoreMessage(SendClientLogRequest.dataID);

		addRequestMaker(new GameRequestMaker());
		addResponseMaker(new GameResponseMaker());
		addClientRequestBind(new GameRequestBindTool());
		addRequestMaker(new CenterRequestMaker());
		addResponseMaker(new CenterResponseMaker());
		addClientRequestBind(new CenterRequestBindTool());
	}

	protected override void onFrame(int delay)
	{
		base.onFrame(delay);

		if(_currentRequestMid>0)
		{
			long time=DateControl.getTimeMillis() - _requestRecordTime;

			if(!_isShowDelayUI)
			{
				if(time>=Global.showNetDelayMinTime)
				{
					_isShowDelayUI=true;
					GameC.ui.showNetDelay(true);
				}
			}

			if(time>=Global.showNetDelayMaxTime)
			{
				toCancelRequestBind();
			}
		}
	}

	public void clear()
	{
		toCancelRequestBind();
	}

	private void clearRequestBind()
	{
		_currentRequestMid=-1;
		_currentResponses=null;
		_requestRecordTime=0;
		_isShowDelayUI=false;
	}

	private void toCancelRequestBind()
	{
		if(_currentRequestMid<=0)
			return;

		if(_isShowDelayUI)
		{
			_isShowDelayUI=false;
			GameC.ui.showNetDelay(false);
		}

		clearRequestBind();
	}

	protected override void onConnect()
	{
		GameC.main.connectGameSuccess();
	}

	protected override void onConnectFailed()
	{
		Ctrl.printForIO("连接失败一次");

		if(GameC.main.isRunning())
		{
			GameC.main.connectGameFailed();
		}
	}

	protected override void onClose()
	{
		Ctrl.log("客户端连接断开");

		if(GameC.main.isRunning())
		{
			GameC.main.onGameSocketClosed();
		}
	}

	/** 消息是否忽略统计 */
	public override bool isClientMessageIgnore(int mid)
	{
		if(mid==SendClientLogRequest.dataID)
			return true;

		return false;
	}

	public override bool checkRequestBind(int mid)
	{
		int[] responses=_messageBind.get(mid);

		if(responses==null)
			return true;

		//当前有了
		if(_currentRequestMid>0)
		{
			if(!_isShowDelayUI)
			{
				_isShowDelayUI=true;
				GameC.ui.showNetDelay(true);
			}

			return false;
		}
		else
		{
			_currentRequestMid=mid;
			_currentResponses=responses;
			_requestRecordTime=DateControl.getTimeMillis();
			_isShowDelayUI=false;
			return true;
		}
	}

	public override void checkResponseUnbind(int mid)
	{
		if(_currentResponses!=null)
		{
			//存在
			if(ObjectUtils.arrayIndexOf(_currentResponses,mid)!=-1)
			{
				toCancelRequestBind();
			}
		}
	}

	/** 取消某消息的绑定(传入request的ID) */
	public void cancelRequestBind(int mid)
	{
		if(_currentRequestMid<=0)
			return;

		if(_currentRequestMid==mid)
		{
			toCancelRequestBind();
		}
	}
}