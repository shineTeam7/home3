package com.home.commonManager.net.serverRequest.center;
import com.home.commonBase.constlist.generate.ServerMessageType;
import com.home.commonBase.data.login.CenterInitServerData;
import com.home.commonManager.net.serverRequest.center.base.ManagerToCenterServerRequest;
import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.control.BytesControl;
import com.home.shine.support.DataWriter;
import com.home.shine.support.pool.DataPool;

/** (generated by shine) */
public class ReloadServerConfigToCenterServerRequest extends ManagerToCenterServerRequest
{
	/** 数据类型ID */
	public static final int dataID=ServerMessageType.ReloadServerConfigToCenter;
	
	public CenterInitServerData initData;
	
	public ReloadServerConfigToCenterServerRequest()
	{
		_dataID=ServerMessageType.ReloadServerConfigToCenter;
	}
	
	@Override
	protected void copyData()
	{
		super.copyData();
		CenterInitServerData initDataTemp=initData;
		if(initDataTemp!=null)
		{
			this.initData=(CenterInitServerData)initDataTemp.clone();
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
		return "ReloadServerConfigToCenterServerRequest";
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
			writer.sb.append("CenterInitServerData=null");
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
	public static ReloadServerConfigToCenterServerRequest create(CenterInitServerData initData)
	{
		ReloadServerConfigToCenterServerRequest re=(ReloadServerConfigToCenterServerRequest)BytesControl.createRequest(dataID);
		re.initData=initData;
		return re;
	}
	
}
