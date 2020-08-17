using System;
using ShineEngine;

/// <summary>
/// 场景进出逻辑
/// </summary>
public class SceneInOutLogic:SceneLogicBase
{
	/** 机器人角色ID */
	private int _robotPlayerIndex=0;

	/** 指定单位组 */
	private RoleShowData[] _signedUnits;

	//--robot--//

	/** 角色使用逻辑字典 */
	private LongObjectMap<CharacterUseLogic> _logicDic;

	public override void construct()
	{

	}

	public override void init()
	{

	}



	public override void dispose()
	{
		_robotPlayerIndex=0;

		_signedUnits=null;
		_logicDic=null;
	}

	/** 获取机器人角色ID */
	protected long getRobotPlayerID()
	{
		return BaseGameUtils.makeCustomPlayerID(CustomPlayerType.SceneRobot,++_robotPlayerIndex);
	}

	public override void onFrame(int delay)
	{

	}


	/** 预进入前(客户端驱动) */
	public virtual void beforePreEnter()
	{

	}

	/** 进入前(客户端驱动) */
	public virtual void beforeEnter()
	{

	}


	//--以下是角色部分--//

	/** 创建自身角色数据 */
	public UnitData createCharacterDataSelf()
	{
		UnitData data=createMUnitDataByLogic(GameC.player.character.getCurrentCharacterUseLogic());
		makeEnterUnitData(data);
		return data;
	}

	/** 通过角色使用数据创建单位数据 */
	protected UnitData createMUnitDataByLogic(MUnitUseLogic logic)
	{
		//构造符合主角的数据
		UnitData data=GameC.factory.createUnitData();

		data.identity=logic.createIdentityData();

		bool isSelf=logic.getPlayerID()==GameC.player.role.playerID;

		//完整场景
		if(!_scene.isSimple())
		{
			//战斗数据
			//位置数据出去赋值
			_scene.unitFactory.initBaseUnitData(data);

			(data.move=new UnitMoveData()).initDefault();
			//战斗附加数据
			data.fightEx=new UnitFightExData();
			//AI数据
			data.ai=new UnitAIData();

			//不是限定场景,或者不是自己
			if(!_scene.isFinite() || !isSelf)
			{
				//取角色的数据和数据逻辑
				data.fight=logic.getData().fight;
				data.avatar=logic.getData().avatar;
				data.fightDataLogic=logic.getFightLogic();
				//				//绑定场景
				//				data.fightDataLogic.setScene(_scene);
				data.fightDataLogic.setIsDriveAll(true);
			}
			else
			{
				//拷贝数据
				data.fight=(UnitFightData)logic.getData().fight.clone();
				data.avatar=(UnitAvatarData)logic.getData().avatar.clone();

				data.fightDataLogic=GameC.pool.createUnitFightDataLogic();
				//				//绑定场景
				//				data.fightDataLogic.setScene(_scene);
				data.fightDataLogic.setIsDriveAll(true);
				//绑定数据
				data.fightDataLogic.setData(data.fight,data.avatar);
			}

		}

		return data;
	}

	/** 构造进入单位数据(客户端场景) */
	protected void makeEnterUnitData(UnitData data)
	{
		//给予一个流水
		data.instanceID=_scene.getUnitInstanceID();

		if(!_scene.isSimple())
		{
			_scene.battle.makeScenePosData(data);
			_scene.method.makeScenePosData(data);
		}

		_scene.method.makeCharacterData(data);
	}


	/** 构造预备进入数据(机器人) */
	public virtual void makePreInfo(ScenePreInfoData infoData)
	{
		infoData.signedPlayers=_signedUnits;
	}

	/** 设定指定角色组(多人用) */
	protected void setSignedPlayers(RoleShowData[] units)
	{
		_signedUnits=units;
		long[] playerIDs=new long[units.Length];

		RoleShowData data;

		for(int i=units.Length-1;i>=0;--i)
		{
			data=units[i];

			playerIDs[i]=data.playerID;
		}
	}


	/** 设置绑定角色逻辑组 */
	public void setSignedLogics(CharacterUseLogic[] logics)
	{
		_logicDic=new LongObjectMap<CharacterUseLogic>();

		int i=0;
		RoleShowData[] signedUnits=new RoleShowData[logics.Length];

		foreach(CharacterUseLogic v in logics)
		{
			_logicDic.put(v.getPlayerID(),v);
			signedUnits[i++]=v.createRoleShowData();
		}

		//设置指定角色组
		setSignedPlayers(signedUnits);
	}

	/** 获取角色逻辑 */
	public CharacterUseLogic getCharacterLogic(long playerID)
	{
		CharacterUseLogic logic;

		if(_logicDic!=null && (logic=_logicDic.get(playerID))!=null)
			return logic;

		if(playerID==GameC.player.role.playerID)
			return GameC.player.character.getCurrentCharacterUseLogic();

		return null;
	}


	/** 创建机器人角色数据(不包括自己) */
	public UnitData createCharacterDataForRobot(long playerID)
	{
		MUnitUseLogic logic=_logicDic.get(playerID);

		if(logic==null)
		{
			Ctrl.throwError("未找到角色逻辑",playerID);
		}

		UnitData data=createMUnitDataByLogic(logic);
		makeEnterUnitData(data);
		return data;
	}

	public void simpleUnitPosChanged(UnitSimpleData data)
	{
		//TODO:继续
	}

	public void simpleUnitAttributeChanged(UnitSimpleData data)
	{
		//TODO:继续
	}
}