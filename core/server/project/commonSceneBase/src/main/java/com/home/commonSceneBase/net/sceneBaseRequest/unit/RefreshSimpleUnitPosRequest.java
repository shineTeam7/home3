package com.home.commonSceneBase.net.sceneBaseRequest.unit;
import com.home.commonBase.data.scene.base.DirData;
import com.home.commonBase.data.scene.base.PosData;
import com.home.commonSceneBase.constlist.generate.SceneBaseRequestType;
import com.home.commonSceneBase.net.sceneBaseRequest.base.SceneSRequest;
import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.control.BytesControl;
import com.home.shine.support.DataWriter;
import com.home.shine.support.pool.DataPool;

/** 刷新简版单位位置消息(generated by shine) */
public class RefreshSimpleUnitPosRequest extends SceneSRequest
{
	/** 数据类型ID */
	public static final int dataID=SceneBaseRequestType.RefreshSimpleUnitPos;
	
	public int instanceID;
	
	public PosData pos;
	
	public DirData dir;
	
	public RefreshSimpleUnitPosRequest()
	{
		_dataID=SceneBaseRequestType.RefreshSimpleUnitPos;
	}
	
	@Override
	protected void copyData()
	{
		super.copyData();
		PosData posTemp=pos;
		if(posTemp!=null)
		{
			this.pos=new PosData();
			this.pos.copy(posTemp);
		}
		else
		{
			this.pos=null;
			nullObjError("pos");
		}
		
		DirData dirTemp=dir;
		if(dirTemp!=null)
		{
			this.dir=new DirData();
			this.dir.copy(dirTemp);
		}
		else
		{
			this.dir=null;
			nullObjError("dir");
		}
		
	}
	
	/** 获取数据类名 */
	@Override
	public String getDataClassName()
	{
		return "RefreshSimpleUnitPosRequest";
	}
	
	/** 写入字节流(完整版) */
	@Override
	protected void toWriteBytesFull(BytesWriteStream stream)
	{
		super.toWriteBytesFull(stream);
		
		stream.startWriteObj();
		
		stream.writeInt(this.instanceID);
		
		if(this.pos!=null)
		{
			this.pos.writeBytesFull(stream);
		}
		else
		{
			nullObjError("pos");
		}
		
		if(this.dir!=null)
		{
			this.dir.writeBytesFull(stream);
		}
		else
		{
			nullObjError("dir");
		}
		
		stream.endWriteObj();
	}
	
	/** 写入字节流(简版) */
	@Override
	protected void toWriteBytesSimple(BytesWriteStream stream)
	{
		super.toWriteBytesSimple(stream);
		
		stream.writeInt(this.instanceID);
		
		if(this.pos!=null)
		{
			this.pos.writeBytesSimple(stream);
		}
		else
		{
			nullObjError("pos");
		}
		
		if(this.dir!=null)
		{
			this.dir.writeBytesSimple(stream);
		}
		else
		{
			nullObjError("dir");
		}
		
	}
	
	/** 转文本输出 */
	@Override
	protected void toWriteDataString(DataWriter writer)
	{
		super.toWriteDataString(writer);
		
		writer.writeTabs();
		writer.sb.append("instanceID");
		writer.sb.append(':');
		writer.sb.append(this.instanceID);
		
		writer.writeEnter();
		writer.writeTabs();
		writer.sb.append("pos");
		writer.sb.append(':');
		if(this.pos!=null)
		{
			this.pos.writeDataString(writer);
		}
		else
		{
			writer.sb.append("PosData=null");
		}
		
		writer.writeEnter();
		writer.writeTabs();
		writer.sb.append("dir");
		writer.sb.append(':');
		if(this.dir!=null)
		{
			this.dir.writeDataString(writer);
		}
		else
		{
			writer.sb.append("DirData=null");
		}
		
		writer.writeEnter();
	}
	
	/** 回池 */
	@Override
	protected void toRelease(DataPool pool)
	{
		super.toRelease(pool);
		
		this.instanceID=0;
		this.pos.release(pool);
		this.pos=null;
		this.dir.release(pool);
		this.dir=null;
	}
	
	/** 创建实例 */
	public static RefreshSimpleUnitPosRequest create(int instanceID,PosData pos,DirData dir)
	{
		RefreshSimpleUnitPosRequest re=(RefreshSimpleUnitPosRequest)BytesControl.createRequest(dataID);
		re.instanceID=instanceID;
		re.pos=pos;
		re.dir=dir;
		return re;
	}
	
}