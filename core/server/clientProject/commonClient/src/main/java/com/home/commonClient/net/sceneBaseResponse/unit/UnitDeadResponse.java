package com.home.commonClient.net.sceneBaseResponse.unit;
import com.home.commonClient.constlist.generate.GameResponseType;
import com.home.commonClient.constlist.generate.SceneBaseResponseType;
import com.home.commonClient.net.sceneBaseResponse.base.UnitSResponse;
import com.home.shine.bytes.BytesReadStream;
import com.home.shine.support.DataWriter;
import com.home.shine.support.pool.DataPool;

/** 单位死亡消息(generated by shine) */
public class UnitDeadResponse extends UnitSResponse
{
	/** 数据类型ID */
	public static final int dataID=SceneBaseResponseType.UnitDead;
	
	/** 死亡类型 */
	public int type;
	
	/** 是否真死亡(否则就是死后复活) */
	public boolean isReal;
	
	/** 击杀者实例ID */
	public int attackerInstanceID;
	
	public UnitDeadResponse()
	{
		_dataID=SceneBaseResponseType.UnitDead;
	}
	
	/** 执行 */
	protected void execute()
	{
		scene.getFightUnitAbs(instanceID).fight.onDead(scene.getFightUnit(attackerInstanceID),type,isReal);
	}
	
	/** 获取数据类名 */
	@Override
	public String getDataClassName()
	{
		return "UnitDeadResponse";
	}
	
	/** 读取字节流(完整版) */
	@Override
	protected void toReadBytesFull(BytesReadStream stream)
	{
		super.toReadBytesFull(stream);
		
		stream.startReadObj();
		
		this.attackerInstanceID=stream.readInt();
		
		this.type=stream.readInt();
		
		this.isReal=stream.readBoolean();
		
		stream.endReadObj();
	}
	
	/** 读取字节流(简版) */
	@Override
	protected void toReadBytesSimple(BytesReadStream stream)
	{
		super.toReadBytesSimple(stream);
		
		this.attackerInstanceID=stream.readInt();
		
		this.type=stream.readInt();
		
		this.isReal=stream.readBoolean();
		
	}
	
	/** 转文本输出 */
	@Override
	protected void toWriteDataString(DataWriter writer)
	{
		super.toWriteDataString(writer);
		
		writer.writeTabs();
		writer.sb.append("attackerInstanceID");
		writer.sb.append(':');
		writer.sb.append(this.attackerInstanceID);
		
		writer.writeEnter();
		writer.writeTabs();
		writer.sb.append("type");
		writer.sb.append(':');
		writer.sb.append(this.type);
		
		writer.writeEnter();
		writer.writeTabs();
		writer.sb.append("isReal");
		writer.sb.append(':');
		writer.sb.append(this.isReal);
		
		writer.writeEnter();
	}
	
	/** 回池 */
	@Override
	protected void toRelease(DataPool pool)
	{
		super.toRelease(pool);
		
		this.attackerInstanceID=0;
		this.type=0;
		this.isReal=false;
	}
	
}