using ShineEngine;

/// <summary>
/// 排行榜基础数据(generated by shine)
/// </summary>
public class RankData:KeyData
{
	/// <summary>
	/// 数据类型ID
	/// </summary>
	public const int dataID=BaseDataType.Rank;
	
	/// <summary>
	/// 排行值(如以后一个值不够用,再补,理论上应该是够的)
	/// </summary>
	public long value;
	
	/// <summary>
	/// 排行值刷新时间
	/// </summary>
	public long valueRefreshTime;
	
	/// <summary>
	/// 排名(从1开始)
	/// </summary>
	public int rank;
	
	public RankData()
	{
		_dataID=BaseDataType.Rank;
	}
	
	/// <summary>
	/// 读取字节流(简版)
	/// </summary>
	protected override void toReadBytesSimple(BytesReadStream stream)
	{
		base.toReadBytesSimple(stream);
		
		this.rank=stream.readInt();
		
		this.value=stream.readLong();
		
		this.valueRefreshTime=stream.readLong();
		
	}
	
	/// <summary>
	/// 写入字节流(简版)
	/// </summary>
	protected override void toWriteBytesSimple(BytesWriteStream stream)
	{
		base.toWriteBytesSimple(stream);
		
		stream.writeInt(this.rank);
		
		stream.writeLong(this.value);
		
		stream.writeLong(this.valueRefreshTime);
		
	}
	
	/// <summary>
	/// 复制(潜拷贝)
	/// </summary>
	protected override void toShadowCopy(BaseData data)
	{
		base.toShadowCopy(data);
		
		if(!(data is RankData))
			return;
		
		RankData mData=(RankData)data;
		
		this.rank=mData.rank;
		this.value=mData.value;
		this.valueRefreshTime=mData.valueRefreshTime;
	}
	
	/// <summary>
	/// 复制(深拷贝)
	/// </summary>
	protected override void toCopy(BaseData data)
	{
		base.toCopy(data);
		
		if(!(data is RankData))
			return;
		
		RankData mData=(RankData)data;
		
		this.rank=mData.rank;
		
		this.value=mData.value;
		
		this.valueRefreshTime=mData.valueRefreshTime;
		
	}
	
	/// <summary>
	/// 是否数据一致
	/// </summary>
	protected override bool toDataEquals(BaseData data)
	{
		if(!base.toDataEquals(data))
			return false;
		
		RankData mData=(RankData)data;
		if(this.rank!=mData.rank)
			return false;
		
		if(this.value!=mData.value)
			return false;
		
		if(this.valueRefreshTime!=mData.valueRefreshTime)
			return false;
		
		return true;
	}
	
	/// <summary>
	/// 获取数据类名
	/// </summary>
	public override string getDataClassName()
	{
		return "RankData";
	}
	
	/// <summary>
	/// 转文本输出
	/// </summary>
	protected override void toWriteDataString(DataWriter writer)
	{
		base.toWriteDataString(writer);
		
		writer.writeTabs();
		writer.sb.Append("rank");
		writer.sb.Append(':');
		writer.sb.Append(this.rank);
		
		writer.writeEnter();
		writer.writeTabs();
		writer.sb.Append("value");
		writer.sb.Append(':');
		writer.sb.Append(this.value);
		
		writer.writeEnter();
		writer.writeTabs();
		writer.sb.Append("valueRefreshTime");
		writer.sb.Append(':');
		writer.sb.Append(this.valueRefreshTime);
		
		writer.writeEnter();
	}
	
	/// <summary>
	/// 初始化初值
	/// </summary>
	public override void initDefault()
	{
		base.initDefault();
		
	}
	
	/// <summary>
	/// 读取字节流(完整版)
	/// </summary>
	protected override void toReadBytesFull(BytesReadStream stream)
	{
		base.toReadBytesFull(stream);
		
		stream.startReadObj();
		
		this.rank=stream.readInt();
		
		this.value=stream.readLong();
		
		this.valueRefreshTime=stream.readLong();
		
		stream.endReadObj();
	}
	
	/// <summary>
	/// 写入字节流(完整版)
	/// </summary>
	protected override void toWriteBytesFull(BytesWriteStream stream)
	{
		base.toWriteBytesFull(stream);
		
		stream.startWriteObj();
		
		stream.writeInt(this.rank);
		
		stream.writeLong(this.value);
		
		stream.writeLong(this.valueRefreshTime);
		
		stream.endWriteObj();
	}
	
	/// <summary>
	/// 回池
	/// </summary>
	protected override void toRelease(DataPool pool)
	{
		base.toRelease(pool);
		
		this.rank=0;
		this.value=0L;
		this.valueRefreshTime=0L;
	}
	
}
