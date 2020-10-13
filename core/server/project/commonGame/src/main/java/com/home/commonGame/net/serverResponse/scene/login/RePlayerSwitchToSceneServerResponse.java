package com.home.commonGame.net.serverResponse.scene.login;
import com.home.commonBase.constlist.generate.ServerMessageType;
import com.home.commonGame.net.serverResponse.scene.base.PlayerSceneToGameServerResponse;
import com.home.shine.bytes.BytesReadStream;
import com.home.shine.support.DataWriter;
import com.home.shine.support.pool.DataPool;

/** 回复玩家切换到场景(generated by shine) */
public class RePlayerSwitchToSceneServerResponse extends PlayerSceneToGameServerResponse
{
	/** 数据类型ID */
	public static final int dataID=ServerMessageType.RePlayerSwitchToScene;
	
	/** 端口 */
	public int port;
	
	/** 地址 */
	public String host="";
	
	/** 令牌 */
	public int token;
	
	public RePlayerSwitchToSceneServerResponse()
	{
		_dataID=ServerMessageType.RePlayerSwitchToScene;
	}
	
	/** 获取数据类名 */
	@Override
	public String getDataClassName()
	{
		return "RePlayerSwitchToSceneServerResponse";
	}
	
	/** 读取字节流(简版) */
	@Override
	protected void toReadBytesSimple(BytesReadStream stream)
	{
		super.toReadBytesSimple(stream);
		
		this.token=stream.readInt();
		
		this.host=stream.readUTF();
		
		this.port=stream.readInt();
		
	}
	
	/** 转文本输出 */
	@Override
	protected void toWriteDataString(DataWriter writer)
	{
		super.toWriteDataString(writer);
		
		writer.writeTabs();
		writer.sb.append("token");
		writer.sb.append(':');
		writer.sb.append(this.token);
		
		writer.writeEnter();
		writer.writeTabs();
		writer.sb.append("host");
		writer.sb.append(':');
		writer.sb.append(this.host);
		
		writer.writeEnter();
		writer.writeTabs();
		writer.sb.append("port");
		writer.sb.append(':');
		writer.sb.append(this.port);
		
		writer.writeEnter();
	}
	
	/** 回池 */
	@Override
	protected void toRelease(DataPool pool)
	{
		super.toRelease(pool);
		
		this.token=0;
		this.host="";
		this.port=0;
	}
	
	/** 执行 */
	@Override
	protected void execute()
	{
		me.addFunc(()->
		{
			me.getExecutor().playerSwitchToSceneNext(me,fromSceneServerID,token,host,port);
		});
	}
	
}