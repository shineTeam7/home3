using ShineEngine;

/// <summary>
/// 活动表(generated by shine)
/// </summary>
public class ActivityConfig:BaseConfig
{
	/** 存储集合 */
	private static IntObjectMap<ActivityConfig> _dic;
	
	/// <summary>
	/// 活动id
	/// </summary>
	public int id;
	
	/// <summary>
	/// 开启时间
	/// </summary>
	public string startTime;
	
	/// <summary>
	/// 关闭时间
	/// </summary>
	public string endTime;
	
	/// <summary>
	/// 取消可视时间
	/// </summary>
	public string cantSeeTime;
	
	/// <summary>
	/// 可视时间
	/// </summary>
	public string canSeeTime;
	
	/// <summary>
	/// 生效条件
	/// </summary>
	public int[][] enableConditions;
	
	/// <summary>
	/// 失效条件
	/// </summary>
	public int[][] invalidConditions;
	
	/// <summary>
	/// 重置时间
	/// </summary>
	public string resetTime;
	
	/// <summary>
	/// 参与条件
	/// </summary>
	public int[][] joinConditions;
	
	/// <summary>
	/// 全部次数完成奖励
	/// </summary>
	public int rewardComplete;
	
	/// <summary>
	/// 单次奖励
	/// </summary>
	public int rewardOnce;
	
	/// <summary>
	/// 参与次数
	/// </summary>
	public int joinCount;
	
	/// <summary>
	/// 开启时间(时间表达式)
	/// </summary>
	public TimeExpression startTimeT;
	
	/// <summary>
	/// 关闭时间(时间表达式)
	/// </summary>
	public TimeExpression endTimeT;
	
	/// <summary>
	/// 重置时间(时间表达式)
	/// </summary>
	public TimeExpression resetTimeT;
	
	/// <summary>
	/// 可视时间(时间表达式)
	/// </summary>
	public TimeExpression canSeeTimeT;
	
	/// <summary>
	/// 取消可视时间(时间表达式)
	/// </summary>
	public TimeExpression cantSeeTimeT;
	
	/// <summary>
	/// 获取
	/// </summary>
	public static ActivityConfig get(int id)
	{
		return _dic.get(id);
	}
	
	/// <summary>
	/// 设置字典
	/// </summary>
	public static void setDic(IntObjectMap<ActivityConfig> dic)
	{
		_dic=dic;
	}
	
	/// <summary>
	/// 添加字典(热更用)
	/// </summary>
	public static void addDic(IntObjectMap<ActivityConfig> dic)
	{
		_dic.putAll(dic);
	}
	
	/// <summary>
	/// 获取全部
	/// </summary>
	public static IntObjectMap<ActivityConfig> getDic()
	{
		return _dic;
	}
	
	/// <summary>
	/// 读取字节流(简版)
	/// </summary>
	protected override void toReadBytesSimple(BytesReadStream stream)
	{
		base.toReadBytesSimple(stream);
		
		this.id=stream.readShort();
		
		this.startTime=stream.readUTF();
		
		this.endTime=stream.readUTF();
		
		this.resetTime=stream.readUTF();
		
		this.canSeeTime=stream.readUTF();
		
		this.cantSeeTime=stream.readUTF();
		
		int enableConditionsLen=stream.readLen();
		if(this.enableConditions==null || this.enableConditions.Length!=enableConditionsLen)
		{
			this.enableConditions=new int[enableConditionsLen][];
		}
		int[][] enableConditionsT=this.enableConditions;
		for(int enableConditionsI=0;enableConditionsI<enableConditionsLen;++enableConditionsI)
		{
			int[] enableConditionsV;
			int enableConditionsVLen=stream.readLen();
			enableConditionsV=new int[enableConditionsVLen];
			int[] enableConditionsVT=enableConditionsV;
			for(int enableConditionsVI=0;enableConditionsVI<enableConditionsVLen;++enableConditionsVI)
			{
				int enableConditionsVV;
				enableConditionsVV=stream.readInt();
				
				enableConditionsVT[enableConditionsVI]=enableConditionsVV;
			}
			
			enableConditionsT[enableConditionsI]=enableConditionsV;
		}
		
		int invalidConditionsLen=stream.readLen();
		if(this.invalidConditions==null || this.invalidConditions.Length!=invalidConditionsLen)
		{
			this.invalidConditions=new int[invalidConditionsLen][];
		}
		int[][] invalidConditionsT=this.invalidConditions;
		for(int invalidConditionsI=0;invalidConditionsI<invalidConditionsLen;++invalidConditionsI)
		{
			int[] invalidConditionsV;
			int invalidConditionsVLen=stream.readLen();
			invalidConditionsV=new int[invalidConditionsVLen];
			int[] invalidConditionsVT=invalidConditionsV;
			for(int invalidConditionsVI=0;invalidConditionsVI<invalidConditionsVLen;++invalidConditionsVI)
			{
				int invalidConditionsVV;
				invalidConditionsVV=stream.readInt();
				
				invalidConditionsVT[invalidConditionsVI]=invalidConditionsVV;
			}
			
			invalidConditionsT[invalidConditionsI]=invalidConditionsV;
		}
		
		int joinConditionsLen=stream.readLen();
		if(this.joinConditions==null || this.joinConditions.Length!=joinConditionsLen)
		{
			this.joinConditions=new int[joinConditionsLen][];
		}
		int[][] joinConditionsT=this.joinConditions;
		for(int joinConditionsI=0;joinConditionsI<joinConditionsLen;++joinConditionsI)
		{
			int[] joinConditionsV;
			int joinConditionsVLen=stream.readLen();
			joinConditionsV=new int[joinConditionsVLen];
			int[] joinConditionsVT=joinConditionsV;
			for(int joinConditionsVI=0;joinConditionsVI<joinConditionsVLen;++joinConditionsVI)
			{
				int joinConditionsVV;
				joinConditionsVV=stream.readInt();
				
				joinConditionsVT[joinConditionsVI]=joinConditionsVV;
			}
			
			joinConditionsT[joinConditionsI]=joinConditionsV;
		}
		
		this.joinCount=stream.readInt();
		
		this.rewardOnce=stream.readInt();
		
		this.rewardComplete=stream.readInt();
		
	}
	
	/// <summary>
	/// 读完所有表后处理
	/// </summary>
	public static void afterReadConfigAll()
	{
		
	}
	
	/// <summary>
	/// 生成刷新配置
	/// </summary>
	protected override void generateRefresh()
	{
		startTimeT=new TimeExpression(startTime);
		
		endTimeT=new TimeExpression(endTime);
		
		resetTimeT=new TimeExpression(resetTime);
		
		canSeeTimeT=new TimeExpression(canSeeTime);
		
		cantSeeTimeT=new TimeExpression(cantSeeTime);
		
	}
	
	/// <summary>
	/// 写入字节流(简版)
	/// </summary>
	protected override void toWriteBytesSimple(BytesWriteStream stream)
	{
		base.toWriteBytesSimple(stream);
		
		stream.writeShort(this.id);
		
		stream.writeUTF(this.startTime);
		
		stream.writeUTF(this.endTime);
		
		stream.writeUTF(this.resetTime);
		
		stream.writeUTF(this.canSeeTime);
		
		stream.writeUTF(this.cantSeeTime);
		
		if(this.enableConditions!=null)
		{
			int[][] enableConditionsT=this.enableConditions;
			stream.writeLen(enableConditionsT.Length);
			for(int enableConditionsVI=0,enableConditionsVLen=enableConditionsT.Length;enableConditionsVI<enableConditionsVLen;++enableConditionsVI)
			{
				int[] enableConditionsV=enableConditionsT[enableConditionsVI];
				if(enableConditionsV!=null)
				{
					int[] enableConditionsVT=enableConditionsV;
					stream.writeLen(enableConditionsVT.Length);
					for(int enableConditionsVVI=0,enableConditionsVVLen=enableConditionsVT.Length;enableConditionsVVI<enableConditionsVVLen;++enableConditionsVVI)
					{
						int enableConditionsVV=enableConditionsVT[enableConditionsVVI];
						stream.writeInt(enableConditionsVV);
						
					}
				}
				else
				{
					nullObjError("enableConditionsV");
				}
				
			}
		}
		else
		{
			nullObjError("enableConditions");
		}
		
		if(this.invalidConditions!=null)
		{
			int[][] invalidConditionsT=this.invalidConditions;
			stream.writeLen(invalidConditionsT.Length);
			for(int invalidConditionsVI=0,invalidConditionsVLen=invalidConditionsT.Length;invalidConditionsVI<invalidConditionsVLen;++invalidConditionsVI)
			{
				int[] invalidConditionsV=invalidConditionsT[invalidConditionsVI];
				if(invalidConditionsV!=null)
				{
					int[] invalidConditionsVT=invalidConditionsV;
					stream.writeLen(invalidConditionsVT.Length);
					for(int invalidConditionsVVI=0,invalidConditionsVVLen=invalidConditionsVT.Length;invalidConditionsVVI<invalidConditionsVVLen;++invalidConditionsVVI)
					{
						int invalidConditionsVV=invalidConditionsVT[invalidConditionsVVI];
						stream.writeInt(invalidConditionsVV);
						
					}
				}
				else
				{
					nullObjError("invalidConditionsV");
				}
				
			}
		}
		else
		{
			nullObjError("invalidConditions");
		}
		
		if(this.joinConditions!=null)
		{
			int[][] joinConditionsT=this.joinConditions;
			stream.writeLen(joinConditionsT.Length);
			for(int joinConditionsVI=0,joinConditionsVLen=joinConditionsT.Length;joinConditionsVI<joinConditionsVLen;++joinConditionsVI)
			{
				int[] joinConditionsV=joinConditionsT[joinConditionsVI];
				if(joinConditionsV!=null)
				{
					int[] joinConditionsVT=joinConditionsV;
					stream.writeLen(joinConditionsVT.Length);
					for(int joinConditionsVVI=0,joinConditionsVVLen=joinConditionsVT.Length;joinConditionsVVI<joinConditionsVVLen;++joinConditionsVVI)
					{
						int joinConditionsVV=joinConditionsVT[joinConditionsVVI];
						stream.writeInt(joinConditionsVV);
						
					}
				}
				else
				{
					nullObjError("joinConditionsV");
				}
				
			}
		}
		else
		{
			nullObjError("joinConditions");
		}
		
		stream.writeInt(this.joinCount);
		
		stream.writeInt(this.rewardOnce);
		
		stream.writeInt(this.rewardComplete);
		
	}
	
}
