package com.home.commonScene.net.sceneRequest.scene;
import com.home.commonBase.data.scene.scene.SceneEnterData;
import com.home.commonScene.constlist.generate.SceneRequestType;
import com.home.commonScene.net.base.SceneRequest;
import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.control.BytesControl;
import com.home.shine.support.DataWriter;
import com.home.shine.support.pool.DataPool;

/** 进入场景(场景服)(generated by shine) */
public class EnterSceneForSceneRequest extends SceneRequest
{
	/** 数据类型ID */
	public static final int dataID=SceneRequestType.EnterSceneForScene;
	
	/** 进入数据 */
	public SceneEnterData enterData;
	
	public EnterSceneForSceneRequest()
	{
		_dataID=SceneRequestType.EnterSceneForScene;
	}
	
	@Override
	protected void copyData()
	{
		super.copyData();
		SceneEnterData enterDataTemp=enterData;
		if(enterDataTemp!=null)
		{
			this.enterData=(SceneEnterData)enterDataTemp.clone();
		}
		else
		{
			this.enterData=null;
			nullObjError("enterData");
		}
		
	}
	
	/** 获取数据类名 */
	@Override
	public String getDataClassName()
	{
		return "EnterSceneForSceneRequest";
	}
	
	/** 写入字节流(完整版) */
	@Override
	protected void toWriteBytesFull(BytesWriteStream stream)
	{
		super.toWriteBytesFull(stream);
		
		stream.startWriteObj();
		
		if(this.enterData!=null)
		{
			stream.writeDataFullNotNull(this.enterData);
		}
		else
		{
			nullObjError("enterData");
		}
		
		stream.endWriteObj();
	}
	
	/** 写入字节流(简版) */
	@Override
	protected void toWriteBytesSimple(BytesWriteStream stream)
	{
		super.toWriteBytesSimple(stream);
		
		if(this.enterData!=null)
		{
			stream.writeDataSimpleNotNull(this.enterData);
		}
		else
		{
			nullObjError("enterData");
		}
		
	}
	
	/** 转文本输出 */
	@Override
	protected void toWriteDataString(DataWriter writer)
	{
		super.toWriteDataString(writer);
		
		writer.writeTabs();
		writer.sb.append("enterData");
		writer.sb.append(':');
		if(this.enterData!=null)
		{
			this.enterData.writeDataString(writer);
		}
		else
		{
			writer.sb.append("SceneEnterData=null");
		}
		
		writer.writeEnter();
	}
	
	/** 回池 */
	@Override
	protected void toRelease(DataPool pool)
	{
		super.toRelease(pool);
		
		this.enterData.release(pool);
		this.enterData=null;
	}
	
	/** 创建实例 */
	public static EnterSceneForSceneRequest create(SceneEnterData enterData)
	{
		EnterSceneForSceneRequest re=(EnterSceneForSceneRequest)BytesControl.createRequest(dataID);
		re.enterData=enterData;
		return re;
	}
	
}