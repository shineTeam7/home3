package com.home.commonScene.net.base;
import com.home.commonScene.part.ScenePlayer;
import com.home.commonScene.server.SceneReceiveSocket;
import com.home.shine.constlist.ThreadType;
import com.home.shine.global.ShineSetting;
import com.home.shine.net.base.BaseResponse;

public abstract class SceneResponse extends BaseResponse
{
	/** 主角 */
	protected ScenePlayer me;
	
	public SceneResponse()
	{
		//标记池线程
		_threadType=ThreadType.Pool;
		setNeedFullRead(ShineSetting.clientMessageUseFull);
	}
	
	public void setPlayer(ScenePlayer player)
	{
		me=player;
	}
	
	@Override
	public void dispatch()
	{
		ScenePlayer player;
		
		if((player=((SceneReceiveSocket)socket).player)==null)
		{
			doPlayerNull();
		}
		else
		{
			setPlayer(player);
			
			//主线程的
			if(_threadType==ThreadType.Main)
			{
				//主线程
				player.addMainFunc(this);
			}
			else
			{
				//派发
				player.addFunc(this);
			}
		}
	}
	
	/** 执行player为空的情况 */
	public void doPlayerNull()
	{
		//默认不执行
	}
}
