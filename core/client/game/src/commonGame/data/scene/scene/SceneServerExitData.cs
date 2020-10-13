using ShineEngine;

/// <summary>
/// 场景服离开数据(generated by shine)
/// </summary>
public class SceneServerExitData:BaseData
{
	/// <summary>
	/// 数据类型ID
	/// </summary>
	public const int dataID=BaseDataType.SceneServerExit;
	
	/// <summary>
	/// 主角数据
	/// </summary>
	public MUnitCacheData hero;
	
	public SceneServerExitData()
	{
		_dataID=BaseDataType.SceneServerExit;
	}
	
	/// <summary>
	/// 获取数据类名
	/// </summary>
	public override string getDataClassName()
	{
		return "SceneServerExitData";
	}
	
	/// <summary>
	/// 读取字节流(完整版)
	/// </summary>
	protected override void toReadBytesFull(BytesReadStream stream)
	{
		stream.startReadObj();
		
		if(stream.readBoolean())
		{
			BaseData heroT=stream.readDataFullNotNull();
			if(heroT!=null)
			{
				if(heroT is MUnitCacheData)
				{
					this.hero=(MUnitCacheData)heroT;
				}
				else
				{
					this.hero=new MUnitCacheData();
					if(!(heroT.GetType().IsAssignableFrom(typeof(MUnitCacheData))))
					{
						stream.throwTypeReadError(typeof(MUnitCacheData),heroT.GetType());
					}
					this.hero.shadowCopy(heroT);
				}
			}
			else
			{
				this.hero=null;
			}
		}
		else
		{
			this.hero=null;
		}
		
		stream.endReadObj();
	}
	
	/// <summary>
	/// 写入字节流(完整版)
	/// </summary>
	protected override void toWriteBytesFull(BytesWriteStream stream)
	{
		stream.startWriteObj();
		
		if(this.hero!=null)
		{
			stream.writeBoolean(true);
			stream.writeDataFullNotNull(this.hero);
		}
		else
		{
			stream.writeBoolean(false);
		}
		
		stream.endWriteObj();
	}
	
	/// <summary>
	/// 读取字节流(简版)
	/// </summary>
	protected override void toReadBytesSimple(BytesReadStream stream)
	{
		if(stream.readBoolean())
		{
			this.hero=(MUnitCacheData)stream.readDataSimpleNotNull();
		}
		else
		{
			this.hero=null;
		}
		
	}
	
	/// <summary>
	/// 写入字节流(简版)
	/// </summary>
	protected override void toWriteBytesSimple(BytesWriteStream stream)
	{
		if(this.hero!=null)
		{
			stream.writeBoolean(true);
			stream.writeDataSimpleNotNull(this.hero);
		}
		else
		{
			stream.writeBoolean(false);
		}
		
	}
	
	/// <summary>
	/// 复制(潜拷贝)
	/// </summary>
	protected override void toShadowCopy(BaseData data)
	{
		if(!(data is SceneServerExitData))
			return;
		
		SceneServerExitData mData=(SceneServerExitData)data;
		
		this.hero=mData.hero;
	}
	
	/// <summary>
	/// 复制(深拷贝)
	/// </summary>
	protected override void toCopy(BaseData data)
	{
		if(!(data is SceneServerExitData))
			return;
		
		SceneServerExitData mData=(SceneServerExitData)data;
		
		if(mData.hero!=null)
		{
			this.hero=(MUnitCacheData)mData.hero.clone();
		}
		else
		{
			this.hero=null;
		}
		
	}
	
	/// <summary>
	/// 是否数据一致
	/// </summary>
	protected override bool toDataEquals(BaseData data)
	{
		SceneServerExitData mData=(SceneServerExitData)data;
		if(mData.hero!=null)
		{
			if(this.hero==null)
				return false;
			if(!this.hero.dataEquals(mData.hero))
				return false;
		}
		else
		{
			if(this.hero!=null)
				return false;
		}
		
		return true;
	}
	
	/// <summary>
	/// 转文本输出
	/// </summary>
	protected override void toWriteDataString(DataWriter writer)
	{
		writer.writeTabs();
		writer.sb.Append("hero");
		writer.sb.Append(':');
		if(this.hero!=null)
		{
			this.hero.writeDataString(writer);
		}
		else
		{
			writer.sb.Append("MUnitCacheData=null");
		}
		
		writer.writeEnter();
	}
	
	/// <summary>
	/// 初始化初值
	/// </summary>
	public override void initDefault()
	{
		
	}
	
	/// <summary>
	/// 回池
	/// </summary>
	protected override void toRelease(DataPool pool)
	{
		this.hero=null;
	}
	
}