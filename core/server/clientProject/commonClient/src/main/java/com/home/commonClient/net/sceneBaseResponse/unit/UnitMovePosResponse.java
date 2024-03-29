package com.home.commonClient.net.sceneBaseResponse.unit;
import com.home.commonBase.data.scene.base.PosData;
import com.home.commonClient.constlist.generate.GameResponseType;
import com.home.commonClient.constlist.generate.SceneBaseResponseType;
import com.home.commonClient.net.sceneBaseResponse.base.UnitSResponse;
import com.home.shine.bytes.BytesReadStream;
import com.home.shine.support.DataWriter;
import com.home.shine.support.pool.DataPool;

/** 单位移动位置(generated by shine) */
public class UnitMovePosResponse extends UnitSResponse
{
	/** 数据类型ID */
	public static final int dataID=SceneBaseResponseType.UnitMovePos;
	
	/** 移动类型 */
	public int type;
	
	/** 服务器首点移动时间(同步用) */
	public int moveTime;
	
	/** 目标位置 */
	public PosData targetPos;
	
	public UnitMovePosResponse()
	{
		_dataID=SceneBaseResponseType.UnitMovePos;
	}
	
	/** 执行 */
	@Override
	protected void execute()
	{
		scene.getFightUnitAbs(instanceID).move.onServerMovePos(type,targetPos);
	}
	
	/** 获取数据类名 */
	@Override
	public String getDataClassName()
	{
		return "UnitMovePosResponse";
	}
	
	/** 读取字节流(完整版) */
	@Override
	protected void toReadBytesFull(BytesReadStream stream)
	{
		super.toReadBytesFull(stream);
		
		stream.startReadObj();
		
		this.type=stream.readInt();
		
		this.targetPos=new PosData();
		this.targetPos.readBytesFull(stream);
		
		this.moveTime=stream.readInt();
		
		stream.endReadObj();
	}
	
	/** 读取字节流(简版) */
	@Override
	protected void toReadBytesSimple(BytesReadStream stream)
	{
		super.toReadBytesSimple(stream);
		
		this.type=stream.readInt();
		
		this.targetPos=new PosData();
		this.targetPos.readBytesSimple(stream);
		
		this.moveTime=stream.readInt();
		
	}
	
	/** 转文本输出 */
	@Override
	protected void toWriteDataString(DataWriter writer)
	{
		super.toWriteDataString(writer);
		
		writer.writeTabs();
		writer.sb.append("type");
		writer.sb.append(':');
		writer.sb.append(this.type);
		
		writer.writeEnter();
		writer.writeTabs();
		writer.sb.append("targetPos");
		writer.sb.append(':');
		if(this.targetPos!=null)
		{
			this.targetPos.writeDataString(writer);
		}
		else
		{
			writer.sb.append("PosData=null");
		}
		
		writer.writeEnter();
		writer.writeTabs();
		writer.sb.append("moveTime");
		writer.sb.append(':');
		writer.sb.append(this.moveTime);
		
		writer.writeEnter();
	}
	
	/** 回池 */
	@Override
	protected void toRelease(DataPool pool)
	{
		super.toRelease(pool);
		
		this.type=0;
		this.targetPos=null;
		this.moveTime=0;
	}
	
}
