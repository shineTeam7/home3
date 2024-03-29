package com.home.commonScene.net.serverResponse.game.login;
import com.home.commonBase.constlist.generate.ServerMessageType;
import com.home.commonScene.global.SceneC;
import com.home.commonScene.net.serverResponse.game.base.GameToSceneServerResponse;
import com.home.shine.bytes.BytesReadStream;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.DataWriter;
import com.home.shine.support.pool.DataPool;

/** 玩家离开场景到场景服消息(generated by shine) */
public class PlayerLeaveSceneToSceneServerResponse extends GameToSceneServerResponse
{
	/** 数据类型ID */
	public static final int dataID=ServerMessageType.PlayerLeaveSceneToScene;
	
	/** 玩家id */
	public long playerID;
	
	public PlayerLeaveSceneToSceneServerResponse()
	{
		_dataID=ServerMessageType.PlayerLeaveSceneToScene;
	}
	
	/** 获取数据类名 */
	@Override
	public String getDataClassName()
	{
		return "PlayerLeaveSceneToSceneServerResponse";
	}
	
	/** 读取字节流(简版) */
	@Override
	protected void toReadBytesSimple(BytesReadStream stream)
	{
		super.toReadBytesSimple(stream);
		
		this.playerID=stream.readLong();
		
	}
	
	/** 转文本输出 */
	@Override
	protected void toWriteDataString(DataWriter writer)
	{
		super.toWriteDataString(writer);
		
		writer.writeTabs();
		writer.sb.append("playerID");
		writer.sb.append(':');
		writer.sb.append(this.playerID);
		
		writer.writeEnter();
	}
	
	/** 回池 */
	@Override
	protected void toRelease(DataPool pool)
	{
		super.toRelease(pool);
		
		this.playerID=0L;
	}
	
	/** 执行 */
	@Override
	protected void execute()
	{
		Ctrl.print("A1");
		SceneC.main.playerExit(playerID);
	}
	
}
