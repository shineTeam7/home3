package com.home.commonManager.net.serverRequest.login;
import com.home.commonBase.constlist.generate.ServerMessageType;
import com.home.commonBase.data.login.LoginInitServerData;
import com.home.commonManager.net.serverRequest.login.base.ManagerToLoginServerRequest;
import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.control.BytesControl;
import com.home.shine.support.DataWriter;
import com.home.shine.support.pool.DataPool;

/** (generated by shine) */
public class ReloadServerConfigToLoginServerRequest extends ManagerToLoginServerRequest
{
	/** 数据类型ID */
	public static final int dataID=ServerMessageType.ReloadServerConfigToLogin;
	
	public LoginInitServerData initData;
	
	public ReloadServerConfigToLoginServerRequest()
	{
		_dataID=ServerMessageType.ReloadServerConfigToLogin;
	}
	
	@Override
	protected void copyData()
	{
		super.copyData();
		LoginInitServerData initDataTemp=initData;
		if(initDataTemp!=null)
		{
			this.initData=(LoginInitServerData)initDataTemp.clone();
		}
		else
		{
			this.initData=null;
			nullObjError("initData");
		}
		
	}
	
	/** 获取数据类名 */
	@Override
	public String getDataClassName()
	{
		return "ReloadServerConfigToLoginServerRequest";
	}
	
	/** 写入字节流(简版) */
	@Override
	protected void toWriteBytesSimple(BytesWriteStream stream)
	{
		super.toWriteBytesSimple(stream);
		
		if(this.initData!=null)
		{
			stream.writeDataSimpleNotNull(this.initData);
		}
		else
		{
			nullObjError("initData");
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
	
	/** 回池 */
	@Override
	protected void toRelease(DataPool pool)
	{
		super.toRelease(pool);
		
		this.initData.release(pool);
		this.initData=null;
	}
	
	/** 创建实例 */
	public static ReloadServerConfigToLoginServerRequest create(LoginInitServerData initData)
	{
		ReloadServerConfigToLoginServerRequest re=(ReloadServerConfigToLoginServerRequest)BytesControl.createRequest(dataID);
		re.initData=initData;
		return re;
	}
	
}
