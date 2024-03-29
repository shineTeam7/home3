package com.home.commonScene.net.serverRequest.game.login;
import com.home.commonBase.constlist.generate.ServerMessageType;
import com.home.commonBase.data.scene.scene.SceneServerExitData;
import com.home.commonScene.net.serverRequest.game.base.PlayerSceneToGameServerRequest;
import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.control.BytesControl;
import com.home.shine.support.DataWriter;
import com.home.shine.support.pool.DataPool;

/** 玩家离开场景完毕到game服(generated by shine) */
public class PlayerLeaveSceneOverToGameServerRequest extends PlayerSceneToGameServerRequest
{
	/** 数据类型ID */
	public static final int dataID=ServerMessageType.PlayerLeaveSceneOverToGame;
	
	/** 携带数据 */
	public SceneServerExitData data;
	
	public PlayerLeaveSceneOverToGameServerRequest()
	{
		_dataID=ServerMessageType.PlayerLeaveSceneOverToGame;
	}
	
	@Override
	protected void copyData()
	{
		super.copyData();
		SceneServerExitData dataTemp=data;
		if(dataTemp!=null)
		{
			this.data=(SceneServerExitData)dataTemp.clone();
		}
		else
		{
			this.data=null;
			nullObjError("data");
		}
		
	}
	
	/** 获取数据类名 */
	@Override
	public String getDataClassName()
	{
		return "PlayerLeaveSceneOverToGameServerRequest";
	}
	
	/** 写入字节流(简版) */
	@Override
	protected void toWriteBytesSimple(BytesWriteStream stream)
	{
		super.toWriteBytesSimple(stream);
		
		if(this.data!=null)
		{
			stream.writeDataSimpleNotNull(this.data);
		}
		else
		{
			nullObjError("data");
		}
		
	}
	
	/** 转文本输出 */
	@Override
	protected void toWriteDataString(DataWriter writer)
	{
		super.toWriteDataString(writer);
		
		writer.writeTabs();
		writer.sb.append("data");
		writer.sb.append(':');
		if(this.data!=null)
		{
			this.data.writeDataString(writer);
		}
		else
		{
			writer.sb.append("SceneServerExitData=null");
		}
		
		writer.writeEnter();
	}
	
	/** 回池 */
	@Override
	protected void toRelease(DataPool pool)
	{
		super.toRelease(pool);
		
		this.data.release(pool);
		this.data=null;
	}
	
	/** 创建实例 */
	public static PlayerLeaveSceneOverToGameServerRequest create(long playerID,SceneServerExitData data)
	{
		PlayerLeaveSceneOverToGameServerRequest re=(PlayerLeaveSceneOverToGameServerRequest)BytesControl.createRequest(dataID);
		re.playerID=playerID;
		re.data=data;
		return re;
	}
	
}
