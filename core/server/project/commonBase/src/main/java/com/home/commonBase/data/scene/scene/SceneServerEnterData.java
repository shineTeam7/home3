package com.home.commonBase.data.scene.scene;
import com.home.commonBase.constlist.generate.BaseDataType;
import com.home.commonBase.data.role.CharacterUseData;
import com.home.shine.bytes.BytesReadStream;
import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.data.BaseData;
import com.home.shine.support.DataWriter;
import com.home.shine.support.pool.DataPool;

/** 场景服进入数据(generated by shine) */
public class SceneServerEnterData extends BaseData
{
	/** 数据类型ID */
	public static final int dataID=BaseDataType.SceneServerEnter;
	
	/** 主角数据 */
	public CharacterUseData hero;
	
	public SceneServerEnterData()
	{
		_dataID=BaseDataType.SceneServerEnter;
	}
	
	/** 获取数据类名 */
	@Override
	public String getDataClassName()
	{
		return "SceneServerEnterData";
	}
	
	/** 读取字节流(完整版) */
	@Override
	protected void toReadBytesFull(BytesReadStream stream)
	{
		stream.startReadObj();
		
		if(stream.readBoolean())
		{
			BaseData heroT=stream.readDataFullNotNull();
			if(heroT!=null)
			{
				if(heroT instanceof CharacterUseData)
				{
					this.hero=(CharacterUseData)heroT;
				}
				else
				{
					this.hero=new CharacterUseData();
					if(!(heroT.getClass().isAssignableFrom(CharacterUseData.class)))
					{
						stream.throwTypeReadError(CharacterUseData.class,heroT.getClass());
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
	
	/** 写入字节流(完整版) */
	@Override
	protected void toWriteBytesFull(BytesWriteStream stream)
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
	
	/** 读取字节流(简版) */
	@Override
	protected void toReadBytesSimple(BytesReadStream stream)
	{
		if(stream.readBoolean())
		{
			this.hero=(CharacterUseData)stream.readDataSimpleNotNull();
		}
		else
		{
			this.hero=null;
		}
		
	}
	
	/** 写入字节流(简版) */
	@Override
	protected void toWriteBytesSimple(BytesWriteStream stream)
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
	
	/** 复制(潜拷贝) */
	@Override
	protected void toShadowCopy(BaseData data)
	{
		if(!(data instanceof SceneServerEnterData))
			return;
		
		SceneServerEnterData mData=(SceneServerEnterData)data;
		
		this.hero=mData.hero;
	}
	
	/** 复制(深拷贝) */
	@Override
	protected void toCopy(BaseData data)
	{
		if(!(data instanceof SceneServerEnterData))
			return;
		
		SceneServerEnterData mData=(SceneServerEnterData)data;
		
		if(mData.hero!=null)
		{
			this.hero=(CharacterUseData)mData.hero.clone();
		}
		else
		{
			this.hero=null;
		}
		
	}
	
	/** 是否数据一致 */
	@Override
	protected boolean toDataEquals(BaseData data)
	{
		SceneServerEnterData mData=(SceneServerEnterData)data;
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
	
	/** 转文本输出 */
	@Override
	protected void toWriteDataString(DataWriter writer)
	{
		writer.writeTabs();
		writer.sb.append("hero");
		writer.sb.append(':');
		if(this.hero!=null)
		{
			this.hero.writeDataString(writer);
		}
		else
		{
			writer.sb.append("CharacterUseData=null");
		}
		
		writer.writeEnter();
	}
	
	/** 初始化初值 */
	@Override
	public void initDefault()
	{
		
	}
	
	/** 回池 */
	@Override
	protected void toRelease(DataPool pool)
	{
		this.hero=null;
	}
	
}
