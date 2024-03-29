package com.home.commonClient.net.sceneBaseResponse.unit;
import com.home.commonClient.constlist.generate.GameResponseType;
import com.home.commonClient.constlist.generate.SceneBaseResponseType;
import com.home.commonClient.net.sceneBaseResponse.base.UnitSResponse;
import com.home.shine.bytes.BytesReadStream;
import com.home.shine.support.DataWriter;
import com.home.shine.support.collection.IntBooleanMap;
import com.home.shine.support.pool.DataPool;

/** 刷新单位状态(generated by shine) */
public class RefreshUnitStatusResponse extends UnitSResponse
{
	/** 数据类型ID */
	public static final int dataID=SceneBaseResponseType.RefreshUnitStatus;
	
	/** 改变的状态组 */
	public IntBooleanMap status;
	
	public RefreshUnitStatusResponse()
	{
		_dataID=SceneBaseResponseType.RefreshUnitStatus;
	}
	
	/** 执行 */
	protected void execute()
	{
		scene.getFightUnitAbs(instanceID).fight.getDataLogic().status.setStatusByServer(status);
	}
	
	/** 获取数据类名 */
	@Override
	public String getDataClassName()
	{
		return "RefreshUnitStatusResponse";
	}
	
	/** 读取字节流(完整版) */
	@Override
	protected void toReadBytesFull(BytesReadStream stream)
	{
		super.toReadBytesFull(stream);
		
		stream.startReadObj();
		
		int statusLen=stream.readLen();
		if(this.status!=null)
		{
			this.status.clear();
			this.status.ensureCapacity(statusLen);
		}
		else
		{
			this.status=new IntBooleanMap(statusLen);
		}
		
		IntBooleanMap statusT=this.status;
		for(int statusI=statusLen-1;statusI>=0;--statusI)
		{
			int statusK;
			boolean statusV;
			statusK=stream.readInt();
			
			statusV=stream.readBoolean();
			
			statusT.put(statusK,statusV);
		}
		
		stream.endReadObj();
	}
	
	/** 读取字节流(简版) */
	@Override
	protected void toReadBytesSimple(BytesReadStream stream)
	{
		super.toReadBytesSimple(stream);
		
		int statusLen=stream.readLen();
		if(this.status!=null)
		{
			this.status.clear();
			this.status.ensureCapacity(statusLen);
		}
		else
		{
			this.status=new IntBooleanMap(statusLen);
		}
		
		IntBooleanMap statusT=this.status;
		for(int statusI=statusLen-1;statusI>=0;--statusI)
		{
			int statusK;
			boolean statusV;
			statusK=stream.readInt();
			
			statusV=stream.readBoolean();
			
			statusT.put(statusK,statusV);
		}
		
	}
	
	/** 转文本输出 */
	@Override
	protected void toWriteDataString(DataWriter writer)
	{
		super.toWriteDataString(writer);
		
		writer.writeTabs();
		writer.sb.append("status");
		writer.sb.append(':');
		writer.sb.append("Map<int,boolean>");
		if(this.status!=null)
		{
			writer.sb.append('(');
			writer.sb.append(this.status.size());
			writer.sb.append(')');
			writer.writeEnter();
			writer.writeLeftBrace();
			if(!this.status.isEmpty())
			{
				int statusKFreeValue=this.status.getFreeValue();
				int[] statusKKeys=this.status.getKeys();
				boolean[] statusVValues=this.status.getValues();
				for(int statusKI=statusKKeys.length-1;statusKI>=0;--statusKI)
				{
					int statusK=statusKKeys[statusKI];
					if(statusK!=statusKFreeValue)
					{
						boolean statusV=statusVValues[statusKI];
						writer.writeTabs();
						writer.sb.append(statusK);
						
						writer.sb.append(':');
						writer.sb.append(statusV);
						
						writer.writeEnter();
					}
				}
			}
			writer.writeRightBrace();
		}
		else
		{
			writer.sb.append("=null");
		}
		
		writer.writeEnter();
	}
	
	/** 回池 */
	@Override
	protected void toRelease(DataPool pool)
	{
		super.toRelease(pool);
		
		this.status=null;
	}
	
}
