package com.home.commonSceneBase.net.sceneBaseRequest.unit;
import com.home.commonSceneBase.constlist.generate.SceneBaseRequestType;
import com.home.commonSceneBase.net.sceneBaseRequest.base.UnitSRequest;
import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.control.BytesControl;
import com.home.shine.support.DataWriter;
import com.home.shine.support.collection.IntIntMap;
import com.home.shine.support.pool.DataPool;

/** 刷新单位显示部件数据(generated by shine) */
public class RefreshUnitAvatarPartRequest extends UnitSRequest
{
	/** 数据类型ID */
	public static final int dataID=SceneBaseRequestType.RefreshUnitAvatarPart;
	
	/** 改变组 */
	public IntIntMap parts;
	
	public RefreshUnitAvatarPartRequest()
	{
		_dataID=SceneBaseRequestType.RefreshUnitAvatarPart;
	}
	
	@Override
	protected void copyData()
	{
		super.copyData();
		IntIntMap partsTemp=parts;
		if(partsTemp!=null)
		{
			this.parts=new IntIntMap(partsTemp.size());
			IntIntMap partsT=this.parts;
			if(!partsTemp.isEmpty())
			{
				int partsKFreeValue=partsTemp.getFreeValue();
				int[] partsKTable=partsTemp.getTable();
				for(int partsKI=partsKTable.length-2;partsKI>=0;partsKI-=2)
				{
					if(partsKTable[partsKI]!=partsKFreeValue)
					{
						int partsK=partsKTable[partsKI];
						int partsV=partsKTable[partsKI+1];
						int partsW;
						int partsU;
						partsW=partsK;
						
						partsU=partsV;
						
						partsT.put(partsW,partsU);
					}
				}
			}
		}
		else
		{
			this.parts=null;
			nullObjError("parts");
		}
		
	}
	
	/** 获取数据类名 */
	@Override
	public String getDataClassName()
	{
		return "RefreshUnitAvatarPartRequest";
	}
	
	/** 转文本输出 */
	@Override
	protected void toWriteDataString(DataWriter writer)
	{
		super.toWriteDataString(writer);
		
		writer.writeTabs();
		writer.sb.append("parts");
		writer.sb.append(':');
		writer.sb.append("Map<int,int>");
		if(this.parts!=null)
		{
			writer.sb.append('(');
			writer.sb.append(this.parts.size());
			writer.sb.append(')');
			writer.writeEnter();
			writer.writeLeftBrace();
			if(!this.parts.isEmpty())
			{
				int partsKFreeValue=this.parts.getFreeValue();
				int[] partsKTable=this.parts.getTable();
				for(int partsKI=partsKTable.length-2;partsKI>=0;partsKI-=2)
				{
					if(partsKTable[partsKI]!=partsKFreeValue)
					{
						int partsK=partsKTable[partsKI];
						int partsV=partsKTable[partsKI+1];
						writer.writeTabs();
						writer.sb.append(partsK);
						
						writer.sb.append(':');
						writer.sb.append(partsV);
						
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
	
	/** 写入字节流(完整版) */
	@Override
	protected void toWriteBytesFull(BytesWriteStream stream)
	{
		super.toWriteBytesFull(stream);
		
		stream.startWriteObj();
		
		if(this.parts!=null)
		{
			stream.writeLen(this.parts.size());
			if(!this.parts.isEmpty())
			{
				int partsKFreeValue=this.parts.getFreeValue();
				int[] partsKTable=this.parts.getTable();
				for(int partsKI=partsKTable.length-2;partsKI>=0;partsKI-=2)
				{
					if(partsKTable[partsKI]!=partsKFreeValue)
					{
						int partsK=partsKTable[partsKI];
						int partsV=partsKTable[partsKI+1];
						stream.writeInt(partsK);
						
						stream.writeInt(partsV);
						
					}
				}
			}
		}
		else
		{
			nullObjError("parts");
		}
		
		stream.endWriteObj();
	}
	
	/** 写入字节流(简版) */
	@Override
	protected void toWriteBytesSimple(BytesWriteStream stream)
	{
		super.toWriteBytesSimple(stream);
		
		if(this.parts!=null)
		{
			stream.writeLen(this.parts.size());
			if(!this.parts.isEmpty())
			{
				int partsKFreeValue=this.parts.getFreeValue();
				int[] partsKTable=this.parts.getTable();
				for(int partsKI=partsKTable.length-2;partsKI>=0;partsKI-=2)
				{
					if(partsKTable[partsKI]!=partsKFreeValue)
					{
						int partsK=partsKTable[partsKI];
						int partsV=partsKTable[partsKI+1];
						stream.writeInt(partsK);
						
						stream.writeInt(partsV);
						
					}
				}
			}
		}
		else
		{
			nullObjError("parts");
		}
		
	}
	
	/** 回池 */
	@Override
	protected void toRelease(DataPool pool)
	{
		super.toRelease(pool);
		
		if(this.parts!=null)
		{
			this.parts.clear();
		}
	}
	
	/** 创建实例 */
	public static RefreshUnitAvatarPartRequest create(int instanceID,IntIntMap parts)
	{
		RefreshUnitAvatarPartRequest re=(RefreshUnitAvatarPartRequest)BytesControl.createRequest(dataID);
		re.instanceID=instanceID;
		re.parts=parts;
		return re;
	}
	
}
