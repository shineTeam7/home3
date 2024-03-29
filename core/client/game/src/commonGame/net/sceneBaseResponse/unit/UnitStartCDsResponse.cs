using ShineEngine;

/// <summary>
/// 单位开始CD组(generated by shine)
/// </summary>
public class UnitStartCDsResponse:UnitSResponse
{
	/// <summary>
	/// 数据类型ID
	/// </summary>
	public const int dataID=SceneBaseResponseType.UnitStartCDs;
	
	/// <summary>
	/// 冷却组
	/// </summary>
	public SList<CDData> cds;
	
	public UnitStartCDsResponse()
	{
		_dataID=SceneBaseResponseType.UnitStartCDs;
	}
	
	/// <summary>
	/// 执行
	/// </summary>
	protected override void execute()
	{
		unit.fight.getCDDataLogic().startCDsByServer(cds);
	}
	
	/// <summary>
	/// 获取数据类名
	/// </summary>
	public override string getDataClassName()
	{
		return "UnitStartCDsResponse";
	}
	
	/// <summary>
	/// 读取字节流(完整版)
	/// </summary>
	protected override void toReadBytesFull(BytesReadStream stream)
	{
		base.toReadBytesFull(stream);
		
		stream.startReadObj();
		
		int cdsLen=stream.readLen();
		if(this.cds!=null)
		{
			this.cds.clear();
			this.cds.ensureCapacity(cdsLen);
		}
		else
		{
			this.cds=new SList<CDData>();
		}
		
		SList<CDData> cdsT=this.cds;
		for(int cdsI=cdsLen-1;cdsI>=0;--cdsI)
		{
			CDData cdsV;
			cdsV=(CDData)stream.createData(CDData.dataID);
			cdsV.readBytesFull(stream);
			
			cdsT.add(cdsV);
		}
		
		stream.endReadObj();
	}
	
	/// <summary>
	/// 读取字节流(简版)
	/// </summary>
	protected override void toReadBytesSimple(BytesReadStream stream)
	{
		base.toReadBytesSimple(stream);
		
		int cdsLen=stream.readLen();
		if(this.cds!=null)
		{
			this.cds.clear();
			this.cds.ensureCapacity(cdsLen);
		}
		else
		{
			this.cds=new SList<CDData>();
		}
		
		SList<CDData> cdsT=this.cds;
		for(int cdsI=cdsLen-1;cdsI>=0;--cdsI)
		{
			CDData cdsV;
			cdsV=(CDData)stream.createData(CDData.dataID);
			cdsV.readBytesSimple(stream);
			
			cdsT.add(cdsV);
		}
		
	}
	
	/// <summary>
	/// 转文本输出
	/// </summary>
	protected override void toWriteDataString(DataWriter writer)
	{
		base.toWriteDataString(writer);
		
		writer.writeTabs();
		writer.sb.Append("cds");
		writer.sb.Append(':');
		writer.sb.Append("List<CDData>");
		if(this.cds!=null)
		{
			SList<CDData> cdsT=this.cds;
			int cdsLen=cdsT.size();
			writer.sb.Append('(');
			writer.sb.Append(cdsLen);
			writer.sb.Append(')');
			writer.writeEnter();
			writer.writeLeftBrace();
			for(int cdsI=0;cdsI<cdsLen;++cdsI)
			{
				CDData cdsV=cdsT.get(cdsI);
				writer.writeTabs();
				writer.sb.Append(cdsI);
				writer.sb.Append(':');
				if(cdsV!=null)
				{
					cdsV.writeDataString(writer);
				}
				else
				{
					writer.sb.Append("CDData=null");
				}
				
				writer.writeEnter();
			}
			writer.writeRightBrace();
		}
		else
		{
			writer.sb.Append("=null");
		}
		
		writer.writeEnter();
	}
	
	/// <summary>
	/// 回池
	/// </summary>
	protected override void toRelease(DataPool pool)
	{
		base.toRelease(pool);
		
		this.cds=null;
	}
	
}
