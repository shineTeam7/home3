using ShineEngine;

/// <summary>
/// 攻击伤害单个数据()(generated by shine)
/// </summary>
public class AttackDamageOneResponse:SceneSResponse
{
	/// <summary>
	/// 数据类型ID
	/// </summary>
	public const int dataID=SceneBaseResponseType.AttackDamageOne;
	
	/// <summary>
	/// 来源单位
	/// </summary>
	public int fromInstanceID;
	
	/// <summary>
	/// 目标数据
	/// </summary>
	public SkillTargetData target;
	
	/// <summary>
	/// 攻击ID
	/// </summary>
	public int id;
	
	/// <summary>
	/// 攻击等级
	/// </summary>
	public int level;
	
	/// <summary>
	/// 伤害数据
	/// </summary>
	public DamageOneData data;
	
	public AttackDamageOneResponse()
	{
		_dataID=SceneBaseResponseType.AttackDamageOne;
	}
	
	/// <summary>
	/// 获取数据类名
	/// </summary>
	public override string getDataClassName()
	{
		return "AttackDamageOneResponse";
	}
	
	/// <summary>
	/// 读取字节流(完整版)
	/// </summary>
	protected override void toReadBytesFull(BytesReadStream stream)
	{
		base.toReadBytesFull(stream);
		
		stream.startReadObj();
		
		this.fromInstanceID=stream.readInt();
		
		BaseData targetT=stream.readDataFullNotNull();
		if(targetT!=null)
		{
			if(targetT is SkillTargetData)
			{
				this.target=(SkillTargetData)targetT;
			}
			else
			{
				this.target=new SkillTargetData();
				if(!(targetT.GetType().IsAssignableFrom(typeof(SkillTargetData))))
				{
					stream.throwTypeReadError(typeof(SkillTargetData),targetT.GetType());
				}
				this.target.shadowCopy(targetT);
			}
		}
		else
		{
			this.target=null;
		}
		
		this.id=stream.readInt();
		
		this.level=stream.readInt();
		
		BaseData dataT=stream.readDataFullNotNull();
		if(dataT!=null)
		{
			if(dataT is DamageOneData)
			{
				this.data=(DamageOneData)dataT;
			}
			else
			{
				this.data=new DamageOneData();
				if(!(dataT.GetType().IsAssignableFrom(typeof(DamageOneData))))
				{
					stream.throwTypeReadError(typeof(DamageOneData),dataT.GetType());
				}
				this.data.shadowCopy(dataT);
			}
		}
		else
		{
			this.data=null;
		}
		
		stream.endReadObj();
	}
	
	/// <summary>
	/// 读取字节流(简版)
	/// </summary>
	protected override void toReadBytesSimple(BytesReadStream stream)
	{
		base.toReadBytesSimple(stream);
		
		this.fromInstanceID=stream.readInt();
		
		this.target=(SkillTargetData)stream.readDataSimpleNotNull();
		
		this.id=stream.readInt();
		
		this.level=stream.readInt();
		
		this.data=(DamageOneData)stream.readDataSimpleNotNull();
		
	}
	
	/// <summary>
	/// 转文本输出
	/// </summary>
	protected override void toWriteDataString(DataWriter writer)
	{
		base.toWriteDataString(writer);
		
		writer.writeTabs();
		writer.sb.Append("fromInstanceID");
		writer.sb.Append(':');
		writer.sb.Append(this.fromInstanceID);
		
		writer.writeEnter();
		writer.writeTabs();
		writer.sb.Append("target");
		writer.sb.Append(':');
		if(this.target!=null)
		{
			this.target.writeDataString(writer);
		}
		else
		{
			writer.sb.Append("SkillTargetData=null");
		}
		
		writer.writeEnter();
		writer.writeTabs();
		writer.sb.Append("id");
		writer.sb.Append(':');
		writer.sb.Append(this.id);
		
		writer.writeEnter();
		writer.writeTabs();
		writer.sb.Append("level");
		writer.sb.Append(':');
		writer.sb.Append(this.level);
		
		writer.writeEnter();
		writer.writeTabs();
		writer.sb.Append("data");
		writer.sb.Append(':');
		if(this.data!=null)
		{
			this.data.writeDataString(writer);
		}
		else
		{
			writer.sb.Append("DamageOneData=null");
		}
		
		writer.writeEnter();
	}
	
	/// <summary>
	/// 执行
	/// </summary>
	protected override void execute()
	{
		scene.fight.onAttackDamageFromServer(fromInstanceID,id,level,target,data);
	}
	
	/// <summary>
	/// 回池
	/// </summary>
	protected override void toRelease(DataPool pool)
	{
		base.toRelease(pool);
		
		this.fromInstanceID=0;
		this.target=null;
		this.id=0;
		this.level=0;
		this.data=null;
	}
	
}
