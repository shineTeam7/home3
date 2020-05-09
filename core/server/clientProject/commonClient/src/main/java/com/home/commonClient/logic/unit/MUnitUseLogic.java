package com.home.commonClient.logic.unit;

import com.home.commonBase.constlist.generate.BuffKeepType;
import com.home.commonBase.data.role.MUnitCacheData;
import com.home.commonBase.data.role.MUnitUseData;
import com.home.commonBase.data.scene.unit.UnitAvatarData;
import com.home.commonBase.data.scene.unit.UnitInfoData;
import com.home.commonBase.data.scene.unit.identity.MUnitIdentityData;
import com.home.commonBase.logic.unit.AttributeDataLogic;
import com.home.commonBase.logic.unit.BuffDataLogic;
import com.home.commonClient.part.player.Player;
import com.home.commonClient.tool.func.PlayerEquipContainerTool;
import com.home.shine.control.DateControl;

/** 主角单位使用逻辑 */
public abstract class MUnitUseLogic
{
	protected MUnitUseData _data;
	/** 序号 */
	public int index=-1;
	
	/** 自身数据逻辑 */
	private MUnitFightDataLogic _fightLogic;
	/** 属性逻辑(快捷方式) */
	private AttributeDataLogic _attributeLogic;
	/** buff逻辑(快捷方式) */
	private BuffDataLogic _buffLogic;
	
	//缓存部分
	/** 主城缓存数据 */
	private MUnitCacheData _townCacheData;
	
	//--本体--//
	/** 绑定角色(归属) */
	protected Player _player;
	
	//--控制--//
	/** 归属角色ID */
	private long _belongPlayerID=-1L;
	/** 控制角色 */
	protected Player _controlPlayer;
	
	/** 装备工具 */
	private PlayerEquipContainerTool _equipTool;
	
	public MUnitUseLogic()
	{
		//TODO:处理离线cd,buff逻辑
	}
	
	/** 更新数据 */
	public void onPiece(int delay)
	{
		_fightLogic.onPiece(delay);
	}
	
	/** 每份时间 */
	public void onPieceEx(int delay)
	{
		_fightLogic.onPieceEx(delay);
	}
	
	protected void setData(MUnitUseData data)
	{
		_data=data;
	}
	
	/** 构建战斗逻辑(isRobot:是否机器人) */
	public void init(MUnitUseData data,boolean isRobot)
	{
		setData(data);
		
		MUnitFightDataLogic fightLogic=_fightLogic=new MUnitFightDataLogic();
		//构造
		fightLogic.construct();
		
		if(isRobot)
		{
			//开全控
			//fightLogic.setIsDriveAll(true);
		}
		
		//绑定
		fightLogic.setUseLogic(this);
		//构造
		makeFightDataLogic(fightLogic);
		
		//标记index
		index=fightLogic.index=data.mIndex;
		
		fightLogic.setData(_data.fight,_data.avatar);
		
		_attributeLogic=fightLogic.attribute;
		_buffLogic=fightLogic.buff;
		
		int fightUnitID=data.getFightUnitID();
		
		fightLogic.initByFightUnitConfig(fightUnitID);
		
		if(isRobot)
		{
			fightLogic.initByFightUnitLevelConfig(fightUnitID,data.level);
		}
	}
	
	/** 绑定主角 */
	public void setPlayer(Player player)
	{
		if(player==null)
		{
			if(_player!=null)
			{
				_player.character.removeMUnitUseLogic(this);
			}
		}
		else
		{
			player.character.addMUnitUseLogic(this);
		}
		
		_player=player;
		
		if(_fightLogic!=null)
		{
			_fightLogic.setPlayer(player);
		}
	}
	
	/** 设置归属角色ID */
	public void setBelongPlayerID(long playerID)
	{
		_belongPlayerID=playerID;
	}
	
	/** 设置控制角色 */
	public void setControlPlayer(Player player)
	{
		_controlPlayer=player;
	}
	
	/** 析构 */
	public void dispose()
	{
		if(_fightLogic!=null)
		{
			_fightLogic.setData(null,null);
		}
		
		setPlayer(null);
		
		_attributeLogic=null;
		_buffLogic=null;
		index=-1;
	}
	
	protected void makeFightDataLogic(MUnitFightDataLogic logic)
	{
		logic.index=0;
	}
	
	/** 获取数据 */
	public MUnitUseData getData()
	{
		return _data;
	}
	
	/** 获取等级 */
	public int getLevel()
	{
		return _data.level;
	}
	
	/** 获取货币 */
	public int getCurrency(int type)
	{
		if(_player!=null)
		{
			return (int)_player.role.getCurrency(type);
		}
		
		return 0;
	}
	
	/** 获取战斗逻辑 */
	public MUnitFightDataLogic getFightLogic()
	{
		return _fightLogic;
	}
	
	/** 获取属性逻辑 */
	public AttributeDataLogic getAttributeLogic()
	{
		return _attributeLogic;
	}
	
	/** 获取buff逻辑 */
	public BuffDataLogic getBuffLogic()
	{
		return _buffLogic;
	}
	
	/** 创建身份数据 */
	public MUnitIdentityData createIdentityData()
	{
		MUnitIdentityData data=toCreateIdentityData();
		makeIdentityData(data);
		return data;
	}
	
	/** 创建身份数据(创建类+类型) */
	protected abstract MUnitIdentityData toCreateIdentityData();
	
	/** 构造身份数据 */
	protected void makeIdentityData(MUnitIdentityData data)
	{
		if(_player!=null)
		{
			data.controlPlayerID=data.playerID=_player.role.playerID;
		}
		else
		{
			data.playerID=_belongPlayerID;
			
			if(_controlPlayer!=null)
			{
				data.controlPlayerID=_controlPlayer.role.playerID;
			}
		}
		
		data.id=_data.id;
		data.level=_data.level;
	}
	
	/** 创建单位信息数据 */
	public UnitInfoData createUnitInfoData()
	{
		UnitInfoData data=new UnitInfoData();
		
		data.identity=createIdentityData();
		data.avatar=(UnitAvatarData)_data.avatar.clone();
		
		return data;
	}
	
	/** 缓存主城类数据 */
	public void cacheForTown()
	{
		if(_townCacheData==null)
		{
			(_townCacheData=new MUnitCacheData()).initDefault();
		}
		
		_fightLogic.saveCache(_townCacheData,BuffKeepType.InTown);
	}
	
	/** 读取主城类缓存 */
	public void loadTownCache()
	{
		if(_townCacheData!=null)
		{
			_fightLogic.loadCache(_townCacheData);
		}
	}
	
	/** 获取主单位功能id */
	public int getMUnitFuncID(int funcID)
	{
		return -1;
	}

	/** 设置出战标记 */
	public void setIsWorking(boolean value)
	{
		if(_data.isWorking==value)
			return;
		
		_data.isWorking=value;
		
		if(value)
		{
			_player.character.getMUnitUseLogicOnWorking().put(index,this);
		}
		else
		{
			_player.character.getMUnitUseLogicOnWorking().remove(index);
		}
	}
	
	/** 是否出战中 */
	public boolean isWorking()
	{
		return _data.isWorking;
	}
	
	public long getTimeMillis()
	{
		if(_player!=null)
			return _player.getTimeMillis();
		
		return DateControl.getTimeMillis();
	}
	
	//--下面是机器人部分--//
	
	
	/** 获取归属角色ID */
	public long getPlayerID()
	{
		return _player!=null ? _player.role.playerID : _belongPlayerID;
	}
	
	/** 装备组 */
	public PlayerEquipContainerTool getEquipTool()
	{
		return _equipTool;
	}
	
	public void setEquipTool(PlayerEquipContainerTool tool)
	{
		_equipTool=tool;
	}
}
