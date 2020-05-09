using System;
using ShineEngine;

/// <summary>
/// 单位
/// </summary>
public class Unit:SceneObject
{
	/** 实例版本 */
	public int version=0;

	public int instanceID=-1;

	/** 单位类型 */
	protected int _type;
	/** 是否可战斗 */
	private bool _canFight;

	/** 是否是自己的单位(因registLogic需要，所以需要预先设定) */
	protected bool _isHero;

	/** 单位数据 */
	private UnitData _data;

	/** 一秒十次计数 */
	private int _tenTimePass=0;
	private int _threeTimePass=0;

	//logics

	/** 身份逻辑 */
	public UnitIdentityLogic identity;
	/** 造型逻辑 */
	public UnitAvatarLogic avatar;
	/** 位置逻辑 */
	public UnitPosLogic pos;
	/** 移动逻辑 */
	public UnitMoveLogic move;
	/** 战斗逻辑 */
	public UnitFightLogic fight;
	/** ai逻辑 */
	public UnitAILogic ai;
	/** ai指令逻辑 */
	public UnitAICommandLogic aiCommand;
	/** 显示逻辑 */
	public UnitShowLogic show;
	/** 头逻辑(可能为空) */
	public UnitHeadLogic head;
	/** 角色控制逻辑(角色才有) */
	public CharacterControlLogic control;

	public Unit()
	{

	}

	/** 单位类型(这里只能用getter了,因为系统有个名为getType的方法) */
	public int type
	{
		set {_type=value;}
		get {return _type;}
	}

	/** 获取单位类型(同上) */
	public int getType()
	{
		return _type;
	}

	/** 是否是主角 */
	public bool isHero
	{
		set {_isHero=value;}
		get {return _isHero;}
	}

	public bool isCharacter()
	{
		return _type==UnitType.Character;
	}

	/** 是否是主单位 */
	public bool isMUnit()
	{
		return BaseC.constlist.unit_isMUnit(_type);
	}

	/** 是否为自己控制的单位 */
	public bool isSelfControl()
	{
		if(isDriveAll())
			return true;

		if(!BaseC.constlist.unit_canFight(_data.identity.type))
			return false;

		//自己控制的单位
		return _data.getFightIdentity().controlPlayerID==GameC.player.role.playerID;
	}

	/** 是否客户端完全控制 */
	public bool isDriveAll()
	{
		return _scene.isDriveAll();
	}

	/** 是否自己驱动攻击发生 */
	public bool isSelfDriveAttackHapen()
	{
		if(isDriveAll())
			return true;

		return isSelfControl() && _scene.driveType==SceneDriveType.ServerDriveDamage;
	}

	/** 设置单位数据 */
	public void setUnitData(UnitData data)
	{
		_data=data;
		instanceID=data!=null ? data.instanceID : -1;
	}

	/** 获取单位数据 */
	public UnitData getUnitData()
	{
		return _data;
	}

	//logics

	/** 注册逻辑体 */
	protected override void registLogics()
	{
		if((identity=createIdentityLogic())!=null)
			addLogic(identity);
		else
			Ctrl.throwError("不能没有identity");

		if((pos=createPosLogic())!=null)
			addLogic(pos);

		//战斗单位再加
		if(BaseC.constlist.unit_canFight(_type))
		{
			if((avatar=createAvatarLogic())!=null)
				addLogic(avatar);

			if((move=createMoveLogic())!=null)
				addLogic(move);

			if((fight=createFightLogic())!=null)
				addLogic(fight);

			if((ai=createAILogic())!=null)
				addLogic(ai);

			if((aiCommand=createAICommandLogic())!=null)
				addLogic(aiCommand);
		}

		//角色控制逻辑
		if(isHero)
		{
			if((control=createCharacterControlLogic())!=null)
				addLogic(control);
		}

		//show放下面
		if((show=createShowLogic())!=null)
			addLogic(show);
		else
			Ctrl.throwError("不能没有show");

		//head最后
		if(BaseC.constlist.unit_needHead(_type))
		{
			if((head=createHeadLogic())!=null)
				addLogic(head);
		}
	}

	protected override void preInit()
	{
		bool simple=_scene.isSimple();

		_canFight=BaseC.constlist.unit_canFight(_type) && !simple;

		if(pos!=null)
			pos.enabled=!simple;

		if(move!=null)
			move.enabled=!simple;

		if(avatar!=null)
			avatar.enabled=!simple;

		if(fight!=null)
			fight.enabled=!simple;

		if(ai!=null)
			ai.enabled=!simple;

		show.enabled=!simple;

		if(head!=null)
			head.enabled=!simple;
	}

	public override void dispose()
	{
		base.dispose();

		_canFight=false;
	}

	/** 创建身份逻辑 */
	protected virtual UnitIdentityLogic createIdentityLogic()
	{
		switch(_type)
		{
			case UnitType.Character:
			{
				return new CharacterIdentityLogic();
			}
			case UnitType.Monster:
			{
				return new MonsterIdentityLogic();
			}
			case UnitType.Puppet:
			{
				return new PuppetIdentityLogic();
			}
			case UnitType.Vehicle:
			{
				return new VehicleIdentityLogic();
			}
		}

		return new UnitIdentityLogic();
	}

	/** 创建位置逻辑 */
	protected virtual UnitPosLogic createPosLogic()
	{
		return new UnitPosLogic();
	}

	/** 创建移动逻辑 */
	protected virtual UnitMoveLogic createMoveLogic()
	{
		return new UnitMoveLogic();
	}

	protected virtual UnitAvatarLogic createAvatarLogic()
	{
		return new UnitAvatarLogic();
	}

	/** 创建战斗逻辑 */
	protected virtual UnitFightLogic createFightLogic()
	{
		return new UnitFightLogic();
	}

	/** 创建AI逻辑 */
	protected virtual UnitAILogic createAILogic()
	{
		return new UnitAILogic();
	}

	/** 创建AI指令逻辑 */
	protected virtual UnitAICommandLogic createAICommandLogic()
	{
		return new UnitAICommandLogic();
	}

	/** 创建显示逻辑 */
	protected virtual UnitShowLogic createShowLogic()
	{
		return null;
	}

	/** 创建头显示逻辑 */
	protected virtual UnitHeadLogic createHeadLogic()
	{
		return null;
	}

	/** 创建角色控制逻辑 */
	protected virtual CharacterControlLogic createCharacterControlLogic()
	{
		return new CharacterControlLogic();
	}

	/** 刷帧 */
	public override void onFrame(int delay)
	{
		base.onFrame(delay);

		if(enabled)
		{
			int time;
			//不累积
			if((time=(_tenTimePass+=delay))>ShineSetting.pieceTime)
			{
				_tenTimePass=0;

				onPiece(time);
			}

			//不累积
			if((time=(_threeTimePass+=delay))>ShineSetting.threeTime)
			{
				_threeTimePass=0;

				onThree(time);
			}
		}
	}

	private void onPiece(int delay)
	{
		SList<SceneObjectLogicBase> logics=_logics;

		for(int i=0,len=logics.Count;i<len;++i)
		{
			((UnitLogicBase)logics[i]).onPiece(delay);
		}
	}

	private void onThree(int delay)
	{
		if(ai!=null)
			ai.onThree(delay);
	}

	/** 移除自身 */
	public override void removeAbs()
	{
		_scene.removeUnit(instanceID);
	}

	/** 能否参与战斗 */
	public bool canFight()
	{
		return _canFight;
	}

	//快捷方式

	/** 获取角色身份逻辑 */
	public CharacterIdentityLogic getCharacterIdentity()
	{
		return (CharacterIdentityLogic)identity;
	}

	/** 获取傀儡身份逻辑 */
	public PuppetIdentityLogic getPuppetIdentityLogic()
	{
		return (PuppetIdentityLogic)identity;
	}

	/** 获取怪物身份逻辑 */
	public MonsterIdentityLogic getMonsterIdentityLogic()
	{
		return (MonsterIdentityLogic)identity;
	}

	/** 获取载具身份逻辑 */
	public VehicleIdentityLogic getVehicleIdentityLogic()
	{
		return (VehicleIdentityLogic)identity;
	}

	/** 创建简版数据 */
	public UnitSimpleData createSimpleUnitData()
	{
		UnitSimpleData re=GameC.factory.createUnitSimpleData();
		makeSimpleUnitData(re);
		return re;
	}

	public void makeSimpleUnitData(UnitSimpleData re)
	{
		re.instanceID=instanceID;
		re.identity=(UnitIdentityData)_data.identity.clone();

		if(re.pos==null)
			re.pos=(UnitPosData)_data.pos.clone();
		else
			re.pos.copy(_data.pos);

		if(re.attributes==null)
			re.attributes=new IntIntMap();
		else
			re.attributes.clear();

		if(_data.fight!=null)
		{
			IntIntMap dic=_data.fight.attributes;

			foreach(int k in AttributeControl.attribute.simpleUnitList)
			{
				re.attributes.put(k,dic.get(k));
			}
		}
	}
}