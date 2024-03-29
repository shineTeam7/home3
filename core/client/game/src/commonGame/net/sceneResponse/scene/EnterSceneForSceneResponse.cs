using ShineEngine;

/// <summary>
/// 进入场景(场景服)(generated by shine)
/// </summary>
public class EnterSceneForSceneResponse:SceneResponse
{
	/// <summary>
	/// 数据类型ID
	/// </summary>
	public const int dataID=SceneResponseType.EnterSceneForScene;
	
	/// <summary>
	/// 进入数据
	/// </summary>
	public SceneEnterData enterData;
	
	public EnterSceneForSceneResponse()
	{
		_dataID=SceneResponseType.EnterSceneForScene;
	}
	
	/// <summary>
	/// 获取数据类名
	/// </summary>
	public override string getDataClassName()
	{
		return "EnterSceneForSceneResponse";
	}
	
	/// <summary>
	/// 读取字节流(完整版)
	/// </summary>
	protected override void toReadBytesFull(BytesReadStream stream)
	{
		base.toReadBytesFull(stream);
		
		stream.startReadObj();
		
		BaseData enterDataT=stream.readDataFullNotNull();
		if(enterDataT!=null)
		{
			if(enterDataT is SceneEnterData)
			{
				this.enterData=(SceneEnterData)enterDataT;
			}
			else
			{
				this.enterData=new SceneEnterData();
				if(!(enterDataT.GetType().IsAssignableFrom(typeof(SceneEnterData))))
				{
					stream.throwTypeReadError(typeof(SceneEnterData),enterDataT.GetType());
				}
				this.enterData.shadowCopy(enterDataT);
			}
		}
		else
		{
			this.enterData=null;
		}
		
		stream.endReadObj();
	}
	
	/// <summary>
	/// 读取字节流(简版)
	/// </summary>
	protected override void toReadBytesSimple(BytesReadStream stream)
	{
		base.toReadBytesSimple(stream);
		
		this.enterData=(SceneEnterData)stream.readDataSimpleNotNull();
		
	}
	
	/// <summary>
	/// 转文本输出
	/// </summary>
	protected override void toWriteDataString(DataWriter writer)
	{
		base.toWriteDataString(writer);
		
		writer.writeTabs();
		writer.sb.Append("enterData");
		writer.sb.Append(':');
		if(this.enterData!=null)
		{
			this.enterData.writeDataString(writer);
		}
		else
		{
			writer.sb.Append("SceneEnterData=null");
		}
		
		writer.writeEnter();
	}
	
	/// <summary>
	/// 回池
	/// </summary>
	protected override void toRelease(DataPool pool)
	{
		base.toRelease(pool);
		
		this.enterData=null;
	}
	
	/// <summary>
	/// 执行
	/// </summary>
	protected override void execute()
	{
		GameC.scene.enterScene(enterData);
	}
	
}
