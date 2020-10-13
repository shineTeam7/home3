package com.home.commonClient.net.sceneBaseResponse.scene;
import com.home.commonClient.constlist.generate.GameResponseType;
import com.home.commonClient.constlist.generate.SceneBaseResponseType;
import com.home.commonClient.net.sceneBaseResponse.base.SceneSResponse;
import com.home.shine.bytes.BytesReadStream;
import com.home.shine.data.BaseData;
import com.home.shine.support.DataWriter;
import com.home.shine.support.pool.DataPool;
import com.home.shine.utils.ObjectUtils;

/** 推送场景广播消息(generated by shine) */
public class SceneRadioResponse extends SceneSResponse
{
	/** 数据类型ID */
	public static final int dataID=SceneBaseResponseType.SceneRadio;
	
	/** 数据 */
	public BaseData data;
	
	public SceneRadioResponse()
	{
		_dataID=SceneBaseResponseType.SceneRadio;
	}
	
	/** 获取数据类名 */
	@Override
	public String getDataClassName()
	{
		return "SceneRadioResponse";
	}
	
	/** 执行 */
	@Override
	protected void execute()
	{
	
	}
	
	/** 读取字节流(完整版) */
	@Override
	protected void toReadBytesFull(BytesReadStream stream)
	{
		super.toReadBytesFull(stream);
		
		stream.startReadObj();
		
		BaseData dataT=stream.readDataFullNotNull();
		if(dataT!=null)
		{
			if(dataT instanceof BaseData)
			{
				this.data=(BaseData)dataT;
			}
			else
			{
				this.data=new BaseData();
				if(!(dataT.getClass().isAssignableFrom(BaseData.class)))
				{
					stream.throwTypeReadError(BaseData.class,dataT.getClass());
				}
				this.data.shadowCopy(dataT);
			}
		}
		else
		{
			this.data=null;
		}
		
		stream.endReadObj();
	}
	
	/** 读取字节流(简版) */
	@Override
	protected void toReadBytesSimple(BytesReadStream stream)
	{
		super.toReadBytesSimple(stream);
		
		this.data=(BaseData)stream.readDataSimpleNotNull();
		
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
			writer.sb.append("BaseData=null");
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