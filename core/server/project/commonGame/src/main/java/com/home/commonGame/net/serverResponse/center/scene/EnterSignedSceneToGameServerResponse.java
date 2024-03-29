package com.home.commonGame.net.serverResponse.center.scene;
import com.home.commonBase.constlist.generate.ServerMessageType;
import com.home.commonBase.data.scene.scene.SceneEnterArgData;
import com.home.commonBase.data.scene.scene.SceneLocationData;
import com.home.commonGame.global.GameC;
import com.home.commonGame.net.serverResponse.center.base.PlayerToGameServerResponse;
import com.home.shine.bytes.BytesReadStream;
import com.home.shine.support.DataWriter;
import com.home.shine.support.pool.DataPool;

/** 进入指定场景到game(暂时废弃)(generated by shine) */
public class EnterSignedSceneToGameServerResponse extends PlayerToGameServerResponse
{
	/** 数据类型ID */
	public static final int dataID=ServerMessageType.EnterSignedSceneToGame;
	
	/** 场景位置数据 */
	public SceneLocationData data;
	
	public EnterSignedSceneToGameServerResponse()
	{
		_dataID=ServerMessageType.EnterSignedSceneToGame;
	}
	
	/** 读取字节流(简版) */
	@Override
	protected void toReadBytesSimple(BytesReadStream stream)
	{
		super.toReadBytesSimple(stream);
		
		this.data=(SceneLocationData)stream.readDataSimpleNotNull();
		
	}
	
	/** 执行 */
	@Override
	protected void execute()
	{
		me.warnLog("需要实现");
	}
	
	/** 获取数据类名 */
	@Override
	public String getDataClassName()
	{
		return "EnterSignedSceneToGameServerResponse";
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
			writer.sb.append("SceneLocationData=null");
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
	
}
