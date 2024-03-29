package com.home.commonSceneBase.net.sceneBaseResponse.unit;
import com.home.commonBase.constlist.system.SceneDriveType;
import com.home.commonBase.data.scene.base.PosDirData;
import com.home.commonSceneBase.constlist.generate.SceneBaseResponseType;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.UnitSyncCommandRequest;
import com.home.commonSceneBase.net.sceneBaseResponse.base.CUnitRResponse;
import com.home.shine.bytes.BytesReadStream;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.DataWriter;
import com.home.shine.support.pool.DataPool;

/** 单位同步参数指令(generated by shine) */
public class CUnitSyncCommandResponse extends CUnitRResponse
{
	/** 数据类型ID */
	public static final int dataID=SceneBaseResponseType.CUnitSyncCommand;
	
	/** 当前位置 */
	public PosDirData posDir;
	
	/** 指令 */
	public int type;
	
	/** 整形参数组 */
	public int[] ints;
	
	/** float参数组 */
	public float[] floats;
	
	public CUnitSyncCommandResponse()
	{
		_dataID=SceneBaseResponseType.CUnitSyncCommand;
	}
	
	/** 执行 */
	@Override
	protected void execute()
	{
		//只有damage模式可用
		if(scene.driveType!=SceneDriveType.ServerDriveDamage)
		{
			unit.warnLog("不该收的消息",dataID);
			return;
		}
		
		//同步位置
		unit.pos.setByPosDir(posDir);
		
		scene.aoi.radioMessage(unit,UnitSyncCommandRequest.create(instanceID,posDir,type,ints,floats),false);
	}
	
	/** 获取数据类名 */
	@Override
	public String getDataClassName()
	{
		return "CUnitSyncCommandResponse";
	}
	
	/** 读取字节流(完整版) */
	@Override
	protected void toReadBytesFull(BytesReadStream stream)
	{
		super.toReadBytesFull(stream);
		
		stream.startReadObj();
		
		if(stream.readBoolean())
		{
			this.posDir=new PosDirData();
			this.posDir.readBytesFull(stream);
		}
		else
		{
			this.posDir=null;
		}
		
		this.type=stream.readInt();
		
		if(stream.readBoolean())
		{
			int intsLen=stream.readLen();
			if(this.ints==null || this.ints.length!=intsLen)
			{
				this.ints=new int[intsLen];
			}
			int[] intsT=this.ints;
			for(int intsI=0;intsI<intsLen;++intsI)
			{
				int intsV;
				intsV=stream.readInt();
				
				intsT[intsI]=intsV;
			}
		}
		else
		{
			this.ints=null;
		}
		
		if(stream.readBoolean())
		{
			int floatsLen=stream.readLen();
			if(this.floats==null || this.floats.length!=floatsLen)
			{
				this.floats=new float[floatsLen];
			}
			float[] floatsT=this.floats;
			for(int floatsI=0;floatsI<floatsLen;++floatsI)
			{
				float floatsV;
				floatsV=stream.readFloat();
				
				floatsT[floatsI]=floatsV;
			}
		}
		else
		{
			this.floats=null;
		}
		
		stream.endReadObj();
	}
	
	/** 读取字节流(简版) */
	@Override
	protected void toReadBytesSimple(BytesReadStream stream)
	{
		super.toReadBytesSimple(stream);
		
		if(stream.readBoolean())
		{
			this.posDir=new PosDirData();
			this.posDir.readBytesSimple(stream);
		}
		else
		{
			this.posDir=null;
		}
		
		this.type=stream.readInt();
		
		if(stream.readBoolean())
		{
			int intsLen=stream.readLen();
			if(this.ints==null || this.ints.length!=intsLen)
			{
				this.ints=new int[intsLen];
			}
			int[] intsT=this.ints;
			for(int intsI=0;intsI<intsLen;++intsI)
			{
				int intsV;
				intsV=stream.readInt();
				
				intsT[intsI]=intsV;
			}
		}
		else
		{
			this.ints=null;
		}
		
		if(stream.readBoolean())
		{
			int floatsLen=stream.readLen();
			if(this.floats==null || this.floats.length!=floatsLen)
			{
				this.floats=new float[floatsLen];
			}
			float[] floatsT=this.floats;
			for(int floatsI=0;floatsI<floatsLen;++floatsI)
			{
				float floatsV;
				floatsV=stream.readFloat();
				
				floatsT[floatsI]=floatsV;
			}
		}
		else
		{
			this.floats=null;
		}
		
	}
	
	/** 转文本输出 */
	@Override
	protected void toWriteDataString(DataWriter writer)
	{
		super.toWriteDataString(writer);
		
		writer.writeTabs();
		writer.sb.append("posDir");
		writer.sb.append(':');
		if(this.posDir!=null)
		{
			this.posDir.writeDataString(writer);
		}
		else
		{
			writer.sb.append("PosDirData=null");
		}
		
		writer.writeEnter();
		writer.writeTabs();
		writer.sb.append("type");
		writer.sb.append(':');
		writer.sb.append(this.type);
		
		writer.writeEnter();
		writer.writeTabs();
		writer.sb.append("ints");
		writer.sb.append(':');
		writer.sb.append("Array<int>");
		if(this.ints!=null)
		{
			int[] intsT=this.ints;
			int intsLen=intsT.length;
			writer.sb.append('(');
			writer.sb.append(intsLen);
			writer.sb.append(')');
			writer.writeEnter();
			writer.writeLeftBrace();
			for(int intsI=0;intsI<intsLen;++intsI)
			{
				int intsV=intsT[intsI];
				writer.writeTabs();
				writer.sb.append(intsI);
				writer.sb.append(':');
				writer.sb.append(intsV);
				
				writer.writeEnter();
			}
			writer.writeRightBrace();
		}
		else
		{
			writer.sb.append("=null");
		}
		
		writer.writeEnter();
		writer.writeTabs();
		writer.sb.append("floats");
		writer.sb.append(':');
		writer.sb.append("Array<float>");
		if(this.floats!=null)
		{
			float[] floatsT=this.floats;
			int floatsLen=floatsT.length;
			writer.sb.append('(');
			writer.sb.append(floatsLen);
			writer.sb.append(')');
			writer.writeEnter();
			writer.writeLeftBrace();
			for(int floatsI=0;floatsI<floatsLen;++floatsI)
			{
				float floatsV=floatsT[floatsI];
				writer.writeTabs();
				writer.sb.append(floatsI);
				writer.sb.append(':');
				writer.sb.append(floatsV);
				
				writer.writeEnter();
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
		
		this.posDir=null;
		this.type=0;
		this.ints=null;
		this.floats=null;
	}
	
}
