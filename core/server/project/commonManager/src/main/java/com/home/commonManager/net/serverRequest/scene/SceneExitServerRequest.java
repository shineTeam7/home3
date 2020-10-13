package com.home.commonManager.net.serverRequest.scene;
import com.home.commonBase.constlist.generate.ServerMessageType;
import com.home.commonManager.net.serverRequest.scene.base.ManagerToSceneServerRequest;
import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.control.BytesControl;

/** 场景服退出消息(generated by shine) */
public class SceneExitServerRequest extends ManagerToSceneServerRequest
{
	/** 数据类型ID */
	public static final int dataID=ServerMessageType.SceneExit;
	
	public SceneExitServerRequest()
	{
		_dataID=ServerMessageType.SceneExit;
	}
	
	@Override
	protected void copyData()
	{
		super.copyData();
	}
	
	/** 获取数据类名 */
	@Override
	public String getDataClassName()
	{
		return "SceneExitServerRequest";
	}
	
	/** 创建实例 */
	public static SceneExitServerRequest create()
	{
		SceneExitServerRequest re=(SceneExitServerRequest)BytesControl.createRequest(dataID);
		return re;
	}
	
}