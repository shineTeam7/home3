using ShineEngine;

/// <summary>
/// (generated by shine)
/// </summary>
public class UnitDriveResponse:UnitSResponse
{
	/// <summary>
	/// 数据类型ID
	/// </summary>
	public const int dataID=SceneBaseResponseType.UnitDrive;
	
	/// <summary>
	/// 当前位置
	/// </summary>
	public PosDirData nowPos;
	
	/// <summary>
	/// 驾驶数据
	/// </summary>
	public DriveData data;
	
	public UnitDriveResponse()
	{
		_dataID=SceneBaseResponseType.UnitDrive;
	}
	
	/// <summary>
	/// 获取数据类名
	/// </summary>
	public override string getDataClassName()
	{
		return "UnitDriveResponse";
	}
	
	/// <summary>
	/// 读取字节流(完整版)
	/// </summary>
	protected override void toReadBytesFull(BytesReadStream stream)
	{
		base.toReadBytesFull(stream);
		
		stream.startReadObj();
		
		if(stream.readBoolean())
		{
			this.nowPos=(PosDirData)stream.createData(PosDirData.dataID);
			this.nowPos.readBytesFull(stream);
		}
		else
		{
			this.nowPos=null;
		}
		
		BaseData dataT=stream.readDataFullNotNull();
		if(dataT!=null)
		{
			if(dataT is DriveData)
			{
				this.data=(DriveData)dataT;
			}
			else
			{
				this.data=new DriveData();
				if(!(dataT.GetType().IsAssignableFrom(typeof(DriveData))))
				{
					stream.throwTypeReadError(typeof(DriveData),dataT.GetType());
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
	
	/// <summary>
	/// 读取字节流(简版)
	/// </summary>
	protected override void toReadBytesSimple(BytesReadStream stream)
	{
		base.toReadBytesSimple(stream);
		
		if(stream.readBoolean())
		{
			this.nowPos=(PosDirData)stream.createData(PosDirData.dataID);
			this.nowPos.readBytesSimple(stream);
		}
		else
		{
			this.nowPos=null;
		}
		
		this.data=(DriveData)stream.readDataSimpleNotNull();
		
	}
	
	/// <summary>
	/// 转文本输出
	/// </summary>
	protected override void toWriteDataString(DataWriter writer)
	{
		base.toWriteDataString(writer);
		
		writer.writeTabs();
		writer.sb.Append("nowPos");
		writer.sb.Append(':');
		if(this.nowPos!=null)
		{
			this.nowPos.writeDataString(writer);
		}
		else
		{
			writer.sb.Append("PosDirData=null");
		}
		
		writer.writeEnter();
		writer.writeTabs();
		writer.sb.Append("data");
		writer.sb.Append(':');
		if(this.data!=null)
		{
			this.data.writeDataString(writer);
		}
		else
		{
			writer.sb.Append("DriveData=null");
		}
		
		writer.writeEnter();
	}
	
	/// <summary>
	/// 执行
	/// </summary>
	protected override void execute()
	{
		unit.move.onServerDrive(nowPos,data);
	}
	
	/// <summary>
	/// 回池
	/// </summary>
	protected override void toRelease(DataPool pool)
	{
		base.toRelease(pool);
		
		this.nowPos=null;
		this.data=null;
	}
	
}
