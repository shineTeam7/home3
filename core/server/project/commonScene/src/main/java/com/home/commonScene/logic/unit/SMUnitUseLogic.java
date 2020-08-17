package com.home.commonScene.logic.unit;

import com.home.commonBase.constlist.generate.SceneForceType;
import com.home.commonBase.data.role.MUnitCacheData;
import com.home.commonBase.data.role.MUnitSaveData;
import com.home.commonBase.data.role.MUnitUseData;
import com.home.commonBase.data.scene.unit.UnitAvatarData;
import com.home.commonBase.data.scene.unit.UnitInfoData;
import com.home.commonBase.data.scene.unit.identity.MUnitIdentityData;
import com.home.commonBase.logic.unit.AttributeDataLogic;
import com.home.commonBase.logic.unit.BuffDataLogic;
import com.home.commonBase.scene.base.Unit;
import com.home.commonScene.global.SceneC;
import com.home.commonScene.part.ScenePlayer;
import com.home.shine.control.DateControl;
import com.home.shine.net.base.BaseRequest;

public abstract class SMUnitUseLogic
{
	protected MUnitUseData _data;
	
	protected MUnitSaveData _saveData;
	
	/** 序号 */
	public int index=-1;
	
	/** 自身数据逻辑 */
	protected SMUnitFightDataLogic _fightLogic;
	/** 属性逻辑(快捷方式) */
	private AttributeDataLogic _attributeLogic;
	/** buff逻辑(快捷方式) */
	private BuffDataLogic _buffLogic;
	
	//--本体--//
	/** 绑定角色(归属) */
	protected ScenePlayer _player;
	
	//--控制--//
	/** 归属角色ID */
	private long _belongPlayerID=-1L;
	/** 控制角色 */
	protected ScenePlayer _controlPlayer;
	
	//--临时--//
	/** 所添加单位(Character没有) */
	public Unit unit;
	
	public SMUnitUseLogic()
	{
	
	}
	
	public boolean isCharacter()
	{
		return false;
	}
	
	/** 更新数据 */
	public void onPiece(int delay)
	{
		//无
		if(_fightLogic.getUnit()==null)
		{
			_fightLogic.onPiece(delay);
		}
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
	
	/** 构建战斗逻辑 */
	public void init(MUnitUseData data)
	{
		setData(data);
		
		SMUnitFightDataLogic fightLogic=_fightLogic=SceneC.factory.createMUnitFightDataLogic();
		//构造
		fightLogic.construct();
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
		fightLogic.initByFightUnitLevelConfig(fightUnitID,data.level);
	}
	
	/** 析构 */
	public void dispose()
	{
		_fightLogic.setData(null,null);
		setPlayer(null);
		
		_data=null;
		_saveData=null;
		
		_attributeLogic=null;
		_buffLogic=null;
		index=-1;
		unit=null;
	}
	
	/** 推送消息(归属者) */
	public void send(BaseRequest request)
	{
		//是归属者，不是控制者
		if(_player!=null)
		{
			_player.send(request);
		}
	}
	
	/** 绑定主角 */
	public void setPlayer(ScenePlayer player)
	{
		_player=player;
		//同时绑定控制角色
		setControlPlayer(player);
	}
	
	/** 设置归属角色ID */
	public void setBelongPlayerID(long playerID)
	{
		_belongPlayerID=playerID;
	}
	
	/** 设置控制角色 */
	public void setControlPlayer(ScenePlayer player)
	{
		_controlPlayer=player;
	}
	
	/** 获取控制角色 */
	public ScenePlayer getControlPlayer()
	{
		return _controlPlayer;
	}
	
	/** 创建客户端推送数据 */
	public MUnitUseData createSendClientData()
	{
		//切换写入
		getFightLogic().switchSendSelf();
		
		return _data;
	}
	
	/** 结束构造客户端推送 */
	public void endSendClientData()
	{
		getFightLogic().endSwitchSend();
	}
	
	protected void makeFightDataLogic(SMUnitFightDataLogic logic)
	{
	
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
	
	/** 获取战斗逻辑 */
	public SMUnitFightDataLogic getFightLogic()
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
			data.controlPlayerID=data.playerID=_player.playerID;
		}
		else
		{
			data.playerID=_belongPlayerID;
			
			if(_controlPlayer!=null)
			{
				data.controlPlayerID=_controlPlayer.playerID;
			}
		}
		
		data.mIndex=index;
		data.force=SceneForceType.Player;
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
	
	/** 升级 */
	public void onLevelUp(int newLevel)
	{
		int oldLevel=_data.level;
		
		int fightUnitID=_data.getFightUnitID();
		//基础数据升级
		_fightLogic.fightUnitLevelUp(fightUnitID,oldLevel,newLevel);
		
		//TODO:其他部分
	}
	
	/** 保存缓存 */
	public void saveCache(int type)
	{
		saveCache(_saveData,type);
	}
	
	/** 保存缓存 */
	public void saveCache(MUnitSaveData saveData,int type)
	{
		if(saveData.cache==null)
		{
			(saveData.cache=new MUnitCacheData()).initDefault();
		}
		
		_fightLogic.saveCache(saveData.cache,type);
	}
	
	/** 读取缓存 */
	public void loadCache()
	{
		if(_saveData.cache!=null)
		{
			loadCache(_saveData.cache);
		}
	}
	
	/** 读取缓存 */
	public void loadCache(MUnitCacheData data)
	{
		if(data!=null)
		{
			_fightLogic.loadCache(data);
		}
	}
	
	/** 清除缓存 */
	public void clearCache()
	{
		_saveData.cache=null;
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
		return _player!=null ? _player.playerID : _belongPlayerID;
	}
	
	/** 是否是机器人(别人控制) */
	public boolean isRobot()
	{
		return _player==null && _controlPlayer!=null;
	}
}
