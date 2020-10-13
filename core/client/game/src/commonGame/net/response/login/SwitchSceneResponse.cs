using ShineEngine;

/// <summary>
/// 切换到场景服消息(generated by shine)
/// </summary>
public class SwitchSceneResponse:GameResponse
{
	/// <summary>
	/// 数据类型ID
	/// </summary>
	public const int dataID=GameResponseType.SwitchScene;
	
	/// <summary>
	/// 服务器目标
	/// </summary>
	public ClientLoginServerInfoData info;
	
	public SwitchSceneResponse()
	{
		_dataID=GameResponseType.SwitchScene;
	}
	
	/// <summary>
	/// 获取数据类名
	/// </summary>
	public override string getDataClassName()
	{
		return "SwitchSceneResponse";
	}
	
	/// <summary>
	/// 读取字节流(完整版)
	/// </summary>
	protected override void toReadBytesFull(BytesReadStream stream)
	{
		base.toReadBytesFull(stream);
		
		stream.startReadObj();
		
		BaseData infoT=stream.readDataFullNotNull();
		if(infoT!=null)
		{
			if(infoT is ClientLoginServerInfoData)
			{
				this.info=(ClientLoginServerInfoData)infoT;
			}
			else
			{
				this.info=new ClientLoginServerInfoData();
				if(!(infoT.GetType().IsAssignableFrom(typeof(ClientLoginServerInfoData))))
				{
					stream.throwTypeReadError(typeof(ClientLoginServerInfoData),infoT.GetType());
				}
				this.info.shadowCopy(infoT);
			}
		}
		else
		{
			this.info=null;
		}
		
		stream.endReadObj();
	}
	
	/// <summary>
	/// 读取字节流(简版)
	/// </summary>
	protected override void toReadBytesSimple(BytesReadStream stream)
	{
		base.toReadBytesSimple(stream);
		
		this.info=(ClientLoginServerInfoData)stream.readDataSimpleNotNull();
		
	}
	
	/// <summary>
	/// 转文本输出
	/// </summary>
	protected override void toWriteDataString(DataWriter writer)
	{
		base.toWriteDataString(writer);
		
		writer.writeTabs();
		writer.sb.Append("info");
		writer.sb.Append(':');
		if(this.info!=null)
		{
			this.info.writeDataString(writer);
		}
		else
		{
			writer.sb.Append("ClientLoginServerInfoData=null");
		}
		
		writer.writeEnter();
	}
	
	/// <summary>
	/// 回池
	/// </summary>
	protected override void toRelease(DataPool pool)
	{
		base.toRelease(pool);
		
		this.info=null;
	}
	
	/// <summary>
	/// 执行
	/// </summary>
	protected override void execute()
	{
		GameC.main.onSwitchScene(info);
	}
	
}