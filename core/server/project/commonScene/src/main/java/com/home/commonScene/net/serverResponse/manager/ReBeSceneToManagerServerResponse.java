package com.home.commonScene.net.serverResponse.manager;
import com.home.commonBase.constlist.generate.ServerMessageType;
import com.home.commonBase.data.login.SceneInitServerData;
import com.home.commonScene.global.SceneC;
import com.home.commonScene.net.serverResponse.manager.base.ManagerToSceneServerResponse;
import com.home.shine.bytes.BytesReadStream;
import com.home.shine.constlist.SocketType;
import com.home.shine.support.DataWriter;
import com.home.shine.support.pool.DataPool;

/** (generated by shine) */
public class ReBeSceneToManagerServerResponse extends ManagerToSceneServerResponse
{
	/** 本登录服信息 */
	public SceneInitServerData initData;
	
	/** 数据类型ID */
	public static final int dataID=ServerMessageType.ReBeSceneToManager;
	
	public ReBeSceneToManagerServerResponse()
	{
		_dataID=ServerMessageType.ReBeSceneToManager;
	}
	
	/** 获取数据类名 */
	@Override
	public String getDataClassName()
	{
		return "ReBeSceneToManagerServerResponse";
	}
	
	/** 读取字节流(简版) */
	@Override
	protected void toReadBytesSimple(BytesReadStream stream)
	{
		super.toReadBytesSimple(stream);
		
		if(stream.readBoolean())
		{
			this.initData=(SceneInitServerData)stream.readDataSimpleNotNull();
		}
		else
		{
			this.initData=null;
		}
		
	}
	
	/** 转文本输出 */
	@Override
	protected void toWriteDataString(DataWriter writer)
	{
		super.toWriteDataString(writer);
		
		writer.writeTabs();
		writer.sb.append("initData");
		writer.sb.append(':');
		if(this.initData!=null)
		{
			this.initData.writeDataString(writer);
		}
		else
		{
			writer.sb.append("SceneInitServerData=null");
		}
		
		writer.writeEnter();
	}
	
	/** 回池 */
	@Override
	protected void toRelease(DataPool pool)
	{
		super.toRelease(pool);
		
		this.initData=null;
	}
	
	/** 执行 */
	@Override
	protected void execute()
	{
		SceneC.server.reBack(SocketType.Manager);
		
		if(initData!=null)
		{
			SceneC.main.setInitData(initData);
			SceneC.server.onConnectManagerOver();
		}
	}
	
}