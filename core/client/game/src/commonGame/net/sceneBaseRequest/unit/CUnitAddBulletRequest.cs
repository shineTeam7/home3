using ShineEngine;

/// <summary>
/// 客户端主控单位添加子弹(generated by shine)
/// </summary>
public class CUnitAddBulletRequest:CUnitRRequest
{
	/// <summary>
	/// 数据类型ID
	/// </summary>
	public const int dataID=SceneBaseRequestType.CUnitAddBullet;
	
	/// <summary>
	/// 子弹数据
	/// </summary>
	public BulletData bullet;
	
	public CUnitAddBulletRequest()
	{
		_dataID=SceneBaseRequestType.CUnitAddBullet;
	}
	
	/// <summary>
	/// 获取数据类名
	/// </summary>
	public override string getDataClassName()
	{
		return "CUnitAddBulletRequest";
	}
	
	/// <summary>
	/// 转文本输出
	/// </summary>
	protected override void toWriteDataString(DataWriter writer)
	{
		base.toWriteDataString(writer);
		
		writer.writeTabs();
		writer.sb.Append("bullet");
		writer.sb.Append(':');
		if(this.bullet!=null)
		{
			this.bullet.writeDataString(writer);
		}
		else
		{
			writer.sb.Append("BulletData=null");
		}
		
		writer.writeEnter();
	}
	
	/// <summary>
	/// 写入字节流(完整版)
	/// </summary>
	protected override void toWriteBytesFull(BytesWriteStream stream)
	{
		base.toWriteBytesFull(stream);
		
		stream.startWriteObj();
		
		if(this.bullet!=null)
		{
			stream.writeDataFullNotNull(this.bullet);
		}
		else
		{
			nullObjError("bullet");
		}
		
		stream.endWriteObj();
	}
	
	/// <summary>
	/// 写入字节流(简版)
	/// </summary>
	protected override void toWriteBytesSimple(BytesWriteStream stream)
	{
		base.toWriteBytesSimple(stream);
		
		if(this.bullet!=null)
		{
			stream.writeDataSimpleNotNull(this.bullet);
		}
		else
		{
			nullObjError("bullet");
		}
		
	}
	
	/// <summary>
	/// 回池
	/// </summary>
	protected override void toRelease(DataPool pool)
	{
		base.toRelease(pool);
		
		this.bullet.release(pool);
		this.bullet=null;
	}
	
	protected override void copyData()
	{
		base.copyData();
		BulletData bulletTemp=bullet;
		if(bulletTemp!=null)
		{
			this.bullet=(BulletData)bulletTemp.clone();
		}
		else
		{
			this.bullet=null;
			nullObjError("bullet");
		}
		
	}
	
	/// <summary>
	/// 创建实例
	/// </summary>
	public static CUnitAddBulletRequest create(int instanceID,BulletData bullet)
	{
		CUnitAddBulletRequest re=(CUnitAddBulletRequest)BytesControl.createRequest(dataID);
		re.instanceID=instanceID;
		re.bullet=bullet;
		return re;
	}
	
}
