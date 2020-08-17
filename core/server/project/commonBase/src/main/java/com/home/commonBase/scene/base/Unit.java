package com.home.commonBase.scene.base;

import com.home.commonBase.constlist.generate.UnitType;
import com.home.commonBase.constlist.system.SceneDriveType;
import com.home.commonBase.control.AttributeControl;
import com.home.commonBase.data.scene.unit.UnitData;
import com.home.commonBase.data.scene.unit.UnitIdentityData;
import com.home.commonBase.data.scene.unit.UnitPosData;
import com.home.commonBase.data.scene.unit.UnitSimpleData;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.scene.unit.BuildingIdentityLogic;
import com.home.commonBase.scene.unit.MonsterIdentityLogic;
import com.home.commonBase.scene.unit.OperationIdentityLogic;
import com.home.commonBase.scene.unit.PuppetIdentityLogic;
import com.home.commonBase.scene.unit.UnitAICommandLogic;
import com.home.commonBase.scene.unit.UnitAILogic;
import com.home.commonBase.scene.unit.UnitAOILogic;
import com.home.commonBase.scene.unit.UnitAOITowerLogic;
import com.home.commonBase.scene.unit.UnitAvatarLogic;
import com.home.commonBase.scene.unit.UnitFightLogic;
import com.home.commonBase.scene.unit.UnitIdentityLogic;
import com.home.commonBase.scene.unit.UnitMoveLogic;
import com.home.commonBase.scene.unit.UnitPosLogic;
import com.home.commonBase.scene.unit.VehicleIdentityLogic;
import com.home.shine.constlist.SLogType;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.net.base.BaseRequest;
import com.home.shine.net.socket.BaseSocket;
import com.home.shine.support.collection.IntIntMap;
import com.home.shine.support.pool.StringBuilderPool;
import com.home.shine.utils.StringUtils;

/** 场景单位 */
public class Unit extends SceneObject
{
	/** 实例版本 */
	public int version=0;
	/** 单位流水ID */
	public int instanceID=-1;
	
	/** 单位类型 */
	protected int _type;
	/** 是否可战斗 */
	private boolean _canFight;
	/** 是否需要tick */
	protected boolean _needTick=false;
	
	/** 单位数据 */
	protected UnitData _data;
	
	//logics
	/** 身份逻辑 */
	public UnitIdentityLogic identity;
	/** 造型逻辑 */
	public UnitAvatarLogic avatar;
	/** 位置逻辑 */
	public UnitPosLogic pos;
	/** 移动逻辑 */
	public UnitMoveLogic move;
	/** AOI逻辑 */
	public UnitAOILogic aoi;
	/** 战斗逻辑 */
	public UnitFightLogic fight;
	/** ai逻辑 */
	public UnitAILogic ai;
	/** ai逻辑 */
	public UnitAICommandLogic aiCommand;
	
	//扩展引用
	/** 灯塔aoi */
	public UnitAOITowerLogic aoiTower;
	
	private boolean _isDriveAll;
	
	private int _tenTimePass=0;
	private int _threeTimePass=0;
	
	/** 缓存socket */
	private BaseSocket _socket;
	
	private boolean _socketDirty=true;
	
	
	@Override
	public void init()
	{
		_canFight=BaseC.constlist.unit_canFight(_type) && !_scene.isSimple();
		
		super.init();
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		_socketDirty=true;
		_canFight=false;
	}
	
	/** 添加到aoi后 */
	public void afterAOIAdd()
	{
		if(canFight())
		{
			fight.afterAOIAdd();
		}
	}
	
	/** 设置类型 */
	public void setType(int type)
	{
		_type=type;
		//先发赋值一次
		_canFight=BaseC.constlist.unit_canFight(_type);
	}
	
	/** 单位类型 */
	public int getType()
	{
		return _type;
	}
	
	/** 是否自己控制(对服务器而言为true) */
	public boolean isSelfControl()
	{
		return true;
	}
	
	/** 是否是角色 */
	public boolean isCharacter()
	{
		return _type==UnitType.Character;
	}
	
	/** 是否是怪物 */
	public boolean isMonster()
	{
		return _type==UnitType.Monster;
	}
	
	/** 是否是主单位 */
	public boolean isMUnit()
	{
		return BaseC.constlist.unit_isMUnit(_type);
	}
	
	@Override
	public void setScene(Scene scene)
	{
		super.setScene(scene);
		
		_isDriveAll=scene!=null ? scene.isDriveAll() : false;
	}
	
	/** 是否本端驱动主要(服务器端就是true) */
	public boolean isDriveAll()
	{
		return _isDriveAll;
	}
	
	/** 是否自己驱动攻击发生 */
	public boolean isSelfDriveAttackHappen()
	{
		if(_isDriveAll)
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
	
	/** 是否可战斗 */
	public boolean canFight()
	{
		return _canFight;
	}
	
	public boolean needTick()
	{
		return _needTick;
	}
	
	//logics
	
	/** 注册逻辑体 */
	@Override
	protected void registLogics()
	{
		addLogic(identity=createIdentityLogic());
		
		if((pos=createPosLogic())!=null)
			addLogic(pos);
		
		if((aoi=createAOILogic())!=null)
		{
			addLogic(aoi);
			
			if(aoi instanceof UnitAOITowerLogic)
			{
				aoiTower=(UnitAOITowerLogic)aoi;
			}
		}
		
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
		
		if(canFight())
		{
			_needTick=true;
		}
	}
	
	@Override
	protected void preInit()
	{
		boolean simple=_scene.isSimple();
		
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
	}
	
	//logics
	
	/** 创建身份逻辑 */
	protected UnitIdentityLogic createIdentityLogic()
	{
		switch(_type)
		{
			case UnitType.Monster:
			{
				return new MonsterIdentityLogic();
			}
			case UnitType.Puppet:
			{
				return new PuppetIdentityLogic();
			}
			case UnitType.Operation:
			{
				return new OperationIdentityLogic();
			}
			case UnitType.Building:
			{
				return new BuildingIdentityLogic();
			}
			case UnitType.Vehicle:
			{
				return new VehicleIdentityLogic();
			}
		}
		
		return new UnitIdentityLogic();
	}
	
	/** 创建位置逻辑 */
	protected UnitPosLogic createPosLogic()
	{
		return new UnitPosLogic();
	}
	
	/** 创建移动逻辑 */
	protected UnitMoveLogic createMoveLogic()
	{
		return new UnitMoveLogic();
	}
	
	/** 创建AOI逻辑 */
	protected UnitAOILogic createAOILogic()
	{
		return new UnitAOILogic();
	}
	
	/** 创建造型逻辑 */
	protected UnitAvatarLogic createAvatarLogic()
	{
		return new UnitAvatarLogic();
	}
	
	/** 创建战斗逻辑 */
	protected UnitFightLogic createFightLogic()
	{
		return new UnitFightLogic();
	}
	
	/** 创建AI逻辑 */
	protected UnitAILogic createAILogic()
	{
		return new UnitAILogic();
	}
	
	/** 创建AI指令逻辑 */
	protected UnitAICommandLogic createAICommandLogic()
	{
		return new UnitAICommandLogic();
	}
	
	/** 刷帧 */
	@Override
	public void onFrame(int delay)
	{
		super.onFrame(delay);
		
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
		SceneObjectLogicBase[] values=_logics.getValues();
		
		for(int i=0, len=_logics.size();i<len;++i)
		{
			((UnitLogicBase)values[i]).onPiece(delay);
		}
	}
	
	private void onThree(int delay)
	{
		if(ai!=null)
			ai.onThree(delay);
	}
	
	/** 即将写入 */
	public void beforeWrite()
	{
		if(move!=null)
			move.beforeWrite();
		
		if(fight!=null)
			fight.beforeWrite();
	}
	
	@Override
	public void removeAbs()
	{
		_scene.toRemoveUnit(this,true);
	}
	
	/** 是否有连接 */
	public boolean hasSocket()
	{
		return getSocket()!=null;
	}
	
	/** 获取socket(只有玩家单位有,包括Character在内的所有玩家控制的单位) */
	public BaseSocket getSocket()
	{
		if(_socket==null)
		{
			if(_socketDirty)
			{
				_socketDirty=false;
				_socket=identity.getSocket();
			}
		}
		
		return _socket;
	}
	
	/** 将socket置空 */
	public void makeSocketDirty()
	{
		_socket=null;
		_socketDirty=true;
	}
	
	/** 发消息 */
	public void send(BaseRequest request)
	{
		if(!enabled)
		{
			Ctrl.log("不该给失效单位推送消息",request.getDataID());
		}
		
		if(!identity.socketReady)
			return;
		
		BaseSocket socket;
		
		if((socket=getSocket())!=null)
		{
			socket.send(request);
		}
	}
	
	/** 广播消息 */
	public void radioMessage(BaseRequest request,boolean needSelf)
	{
		if(_scene!=null)
		{
			_scene.aoi.radioMessage(this,request,needSelf);
		}
	}
	
	/** 发送消息码 */
	public void sendInfoCode(int code)
	{
	
	}
	
	/** 获取描述信息 */
	public String getInfo()
	{
		StringBuilder sb=StringBuilderPool.create();
		writeInfo(sb);
		return StringBuilderPool.releaseStr(sb);
	}
	
	/** 写描述信息 */
	private void writeInfo(StringBuilder sb)
	{
		sb.append("instanceID:");
		sb.append(_data.instanceID);
		sb.append(" type:");
		sb.append(_type);
		sb.append(" identity:");
		identity.writeInfo(sb);
	}
	
	protected void sendWarnLog(String str)
	{
	
	}
	
	//快捷方式
	
	/** 警告日志 */
	public void warnLog(String str)
	{
		StringBuilder sb=StringBuilderPool.create();
		sb.append(str);
		sb.append(' ');
		writeInfo(sb);
		
		String re=sb.toString();
		Ctrl.toLog(sb,SLogType.Warning,1);
		sendWarnLog(re);
	}
	
	/** 警告日志 */
	public void warnLog(Object... args)
	{
		StringBuilder sb=StringBuilderPool.create();
		StringUtils.writeObjectsToStringBuilder(sb,args);
		sb.append(' ');
		writeInfo(sb);
		
		String re=sb.toString();
		Ctrl.toLog(sb,SLogType.Warning,1);
		sendWarnLog(re);
	}
	
	//转化
	
	/** 获取傀儡身份逻辑 */
	public PuppetIdentityLogic getPuppetIdentityLogic()
	{
		return (PuppetIdentityLogic)identity;
	}
	
	/** 获取建筑逻辑 */
	public BuildingIdentityLogic getBuildingIdentityLogic()
	{
		return (BuildingIdentityLogic)identity;
	}
	
	/** 获取操作体逻辑 */
	public OperationIdentityLogic getOperationIdentityLogic()
	{
		return (OperationIdentityLogic)identity;
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
	
	/** 获取角色 */
	public Role getRole()
	{
		return identity.getRole();
	}
	
	/** 创建简版数据 */
	public UnitSimpleData createSimpleUnitData()
	{
		UnitSimpleData re=BaseC.factory.createUnitSimpleData();
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
			
			for(int k:AttributeControl.attribute.simpleUnitList)
			{
				re.attributes.put(k,dic.get(k));
			}
		}
	}
	
	/** m类消息是否需要广播自己 */
	public boolean needRadioSelf()
	{
		return identity.isCUnitNotM();
	}
}
