package com.home.commonScene.net.serverResponse.game.login;
import com.home.commonBase.constlist.generate.ServerMessageType;
import com.home.commonBase.data.scene.scene.SceneServerEnterData;
import com.home.commonScene.control.SceneLogicExecutor;
import com.home.commonScene.global.SceneC;
import com.home.commonScene.net.serverResponse.game.base.GameToSceneServerResponse;
import com.home.commonScene.net.serverResponse.game.base.PlayerGameToSceneServerResponse;
import com.home.shine.bytes.BytesReadStream;
import com.home.shine.support.DataWriter;
import com.home.shine.support.pool.DataPool;

/** 玩家进入场景服消息(generated by shine) */
public class PlayerEnterServerSceneServerResponse extends PlayerGameToSceneServerResponse
{
	/** 数据类型ID */
	public static final int dataID=ServerMessageType.PlayerEnterServerScene;
	
	/** 携带数据 */
	public SceneServerEnterData data;
	
	public PlayerEnterServerSceneServerResponse()
	{
		_dataID=ServerMessageType.PlayerEnterServerScene;
	}
	
	/** 获取数据类名 */
	@Override
	public String getDataClassName()
	{
		return "PlayerEnterServerSceneServerResponse";
	}
	
	/** 读取字节流(简版) */
	@Override
	protected void toReadBytesSimple(BytesReadStream stream)
	{
		super.toReadBytesSimple(stream);
		
		this.data=(SceneServerEnterData)stream.readDataSimpleNotNull();
		
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
			writer.sb.append("SceneServerEnterData=null");
		}
		
		writer.writeEnter();
	}
	
	/** 回池 */
	@Override
	protected void toRelease(DataPool pool)
	{
		super.toRelease(pool);
		
		this.data=null;
	}
	
	/** 执行 */
	@Override
	protected void execute()
	{
		SceneC.main.playerEnterServerScene(me,data);
	}
	
}
