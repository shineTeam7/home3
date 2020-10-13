using ShineEngine;

/// <summary>
/// AOI灯塔刷新消息(删除一组+添加一组)(generated by shine)
/// </summary>
public class AOITowerRefreshResponse:SceneSResponse
{
	/// <summary>
	/// 数据类型ID
	/// </summary>
	public const int dataID=SceneBaseResponseType.AOITowerRefresh;
	
	/// <summary>
	/// 移除单位组
	/// </summary>
	public IntList removeUnits;
	
	/// <summary>
	/// 添加单位组
	/// </summary>
	public SList<UnitData> addUnits;
	
	public AOITowerRefreshResponse()
	{
		_dataID=SceneBaseResponseType.AOITowerRefresh;
	}
	
	/// <summary>
	/// 获取数据类名
	/// </summary>
	public override string getDataClassName()
	{
		return "AOITowerRefreshResponse";
	}
	
	/// <summary>
	/// 读取字节流(完整版)
	/// </summary>
	protected override void toReadBytesFull(BytesReadStream stream)
	{
		base.toReadBytesFull(stream);
		
		stream.startReadObj();
		
		int removeUnitsLen=stream.readLen();
		if(this.removeUnits!=null)
		{
			this.removeUnits.clear();
			this.removeUnits.ensureCapacity(removeUnitsLen);
		}
		else
		{
			this.removeUnits=new IntList();
		}
		
		IntList removeUnitsT=this.removeUnits;
		for(int removeUnitsI=removeUnitsLen-1;removeUnitsI>=0;--removeUnitsI)
		{
			int removeUnitsV;
			removeUnitsV=stream.readInt();
			
			removeUnitsT.add(removeUnitsV);
		}
		
		int addUnitsLen=stream.readLen();
		if(this.addUnits!=null)
		{
			this.addUnits.clear();
			this.addUnits.ensureCapacity(addUnitsLen);
		}
		else
		{
			this.addUnits=new SList<UnitData>();
		}
		
		SList<UnitData> addUnitsT=this.addUnits;
		for(int addUnitsI=addUnitsLen-1;addUnitsI>=0;--addUnitsI)
		{
			UnitData addUnitsV;
			BaseData addUnitsVT=stream.readDataFullNotNull();
			if(addUnitsVT!=null)
			{
				if(addUnitsVT is UnitData)
				{
					addUnitsV=(UnitData)addUnitsVT;
				}
				else
				{
					addUnitsV=new UnitData();
					if(!(addUnitsVT.GetType().IsAssignableFrom(typeof(UnitData))))
					{
						stream.throwTypeReadError(typeof(UnitData),addUnitsVT.GetType());
					}
					addUnitsV.shadowCopy(addUnitsVT);
				}
			}
			else
			{
				addUnitsV=null;
			}
			
			addUnitsT.add(addUnitsV);
		}
		
		stream.endReadObj();
	}
	
	/// <summary>
	/// 读取字节流(简版)
	/// </summary>
	protected override void toReadBytesSimple(BytesReadStream stream)
	{
		base.toReadBytesSimple(stream);
		
		int removeUnitsLen=stream.readLen();
		if(this.removeUnits!=null)
		{
			this.removeUnits.clear();
			this.removeUnits.ensureCapacity(removeUnitsLen);
		}
		else
		{
			this.removeUnits=new IntList();
		}
		
		IntList removeUnitsT=this.removeUnits;
		for(int removeUnitsI=removeUnitsLen-1;removeUnitsI>=0;--removeUnitsI)
		{
			int removeUnitsV;
			removeUnitsV=stream.readInt();
			
			removeUnitsT.add(removeUnitsV);
		}
		
		int addUnitsLen=stream.readLen();
		if(this.addUnits!=null)
		{
			this.addUnits.clear();
			this.addUnits.ensureCapacity(addUnitsLen);
		}
		else
		{
			this.addUnits=new SList<UnitData>();
		}
		
		SList<UnitData> addUnitsT=this.addUnits;
		for(int addUnitsI=addUnitsLen-1;addUnitsI>=0;--addUnitsI)
		{
			UnitData addUnitsV;
			addUnitsV=(UnitData)stream.readDataSimpleNotNull();
			
			addUnitsT.add(addUnitsV);
		}
		
	}
	
	/// <summary>
	/// 转文本输出
	/// </summary>
	protected override void toWriteDataString(DataWriter writer)
	{
		base.toWriteDataString(writer);
		
		writer.writeTabs();
		writer.sb.Append("removeUnits");
		writer.sb.Append(':');
		writer.sb.Append("List<int>");
		if(this.removeUnits!=null)
		{
			IntList removeUnitsT=this.removeUnits;
			int removeUnitsLen=removeUnitsT.size();
			writer.sb.Append('(');
			writer.sb.Append(removeUnitsLen);
			writer.sb.Append(')');
			writer.writeEnter();
			writer.writeLeftBrace();
			for(int removeUnitsI=0;removeUnitsI<removeUnitsLen;++removeUnitsI)
			{
				int removeUnitsV=removeUnitsT.get(removeUnitsI);
				writer.writeTabs();
				writer.sb.Append(removeUnitsI);
				writer.sb.Append(':');
				writer.sb.Append(removeUnitsV);
				
				writer.writeEnter();
			}
			writer.writeRightBrace();
		}
		else
		{
			writer.sb.Append("=null");
		}
		
		writer.writeEnter();
		writer.writeTabs();
		writer.sb.Append("addUnits");
		writer.sb.Append(':');
		writer.sb.Append("List<UnitData>");
		if(this.addUnits!=null)
		{
			SList<UnitData> addUnitsT=this.addUnits;
			int addUnitsLen=addUnitsT.size();
			writer.sb.Append('(');
			writer.sb.Append(addUnitsLen);
			writer.sb.Append(')');
			writer.writeEnter();
			writer.writeLeftBrace();
			for(int addUnitsI=0;addUnitsI<addUnitsLen;++addUnitsI)
			{
				UnitData addUnitsV=addUnitsT.get(addUnitsI);
				writer.writeTabs();
				writer.sb.Append(addUnitsI);
				writer.sb.Append(':');
				if(addUnitsV!=null)
				{
					addUnitsV.writeDataString(writer);
				}
				else
				{
					writer.sb.Append("UnitData=null");
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
	/// 执行
	/// </summary>
	protected override void execute()
	{
		int[] values1=removeUnits.getValues();

		for(int i1=0,len1=removeUnits.size();i1<len1;++i1)
		{
			scene.removeUnit(values1[i1]);
		}

		UnitData[] values=addUnits.getValues();

		for(int i=0,len=addUnits.size();i<len;++i)
		{
			scene.addUnit(values[i]);
		}
	}
	
	/// <summary>
	/// 回池
	/// </summary>
	protected override void toRelease(DataPool pool)
	{
		base.toRelease(pool);
		
		this.removeUnits=null;
		this.addUnits=null;
	}
	
}