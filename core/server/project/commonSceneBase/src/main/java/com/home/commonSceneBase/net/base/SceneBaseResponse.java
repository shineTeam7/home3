package com.home.commonSceneBase.net.base;
import com.home.commonSceneBase.scene.base.BScene;
import com.home.shine.net.base.BaseResponse;

public abstract class SceneBaseResponse extends BaseResponse
{
	/** 主角id */
	protected long _playerID=-1;
	/** 场景 */
	public BScene scene;
	
	public SceneBaseResponse()
	{
		setNeedRelease();
	}
	
	public void setInfo(BScene scene,long playerID)
	{
		this.scene=scene;
		_playerID=playerID;
	}
	
	@Override
	protected void preExecute()
	{
		if(scene==null)
			return;
		
		doExecute();
	}
}
