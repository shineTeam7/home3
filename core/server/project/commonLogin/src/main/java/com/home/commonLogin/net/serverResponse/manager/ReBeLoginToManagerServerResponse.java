package com.home.commonLogin.net.serverResponse.manager;
import com.home.commonBase.constlist.generate.ServerMessageType;
import com.home.commonBase.data.login.LoginInitServerData;
import com.home.commonLogin.global.LoginC;
import com.home.commonLogin.net.serverResponse.manager.base.ManagerToLoginServerResponse;
import com.home.shine.bytes.BytesReadStream;
import com.home.shine.constlist.SocketType;
import com.home.shine.support.DataWriter;
import com.home.shine.support.pool.DataPool;

/** 回复be登录服消息(generated by shine) */
public class ReBeLoginToManagerServerResponse extends ManagerToLoginServerResponse
{
	/** 数据类型ID */
	public static final int dataID=ServerMessageType.ReBeLoginToManager;
	
	/** 初始化数据 */
	public LoginInitServerData initData;
	
	public ReBeLoginToManagerServerResponse()
	{
		_dataID=ServerMessageType.ReBeLoginToManager;
	}
	
	/** 获取数据类名 */
	@Override
	public String getDataClassName()
	{
		return "ReBeLoginToManagerServerResponse";
	}
	
	/** 读取字节流(简版) */
	@Override
	protected void toReadBytesSimple(BytesReadStream stream)
	{
		super.toReadBytesSimple(stream);
		
		if(stream.readBoolean())
		{
			this.initData=(LoginInitServerData)stream.readDataSimpleNotNull();
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
			writer.sb.append("LoginInitServerData=null");
		}
		
		writer.writeEnter();
	}
	
	/** 执行 */
	@Override
	protected void execute()
	{
		LoginC.server.reBack(SocketType.Manager);
		
		if(initData!=null)
		{
			LoginC.main.setInitData(initData);
			LoginC.server.onConnectManagerOver();
		}
	}
	
	/** 回池 */
	@Override
	protected void toRelease(DataPool pool)
	{
		super.toRelease(pool);
		
		this.initData=null;
	}
	
}