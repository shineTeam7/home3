package com.home.commonGame.scene.unit;

import com.home.commonBase.data.scene.unit.identity.CharacterIdentityData;
import com.home.commonBase.scene.base.Unit;
import com.home.commonBase.scene.unit.UnitIdentityLogic;
import com.home.commonGame.part.player.Player;
import com.home.commonGame.part.player.part.SystemPart;
import com.home.commonSceneBase.scene.unit.CharacterIdentityLogic;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.net.socket.BaseSocket;
import com.home.shine.support.collection.IntSet;

public class GameCharacterIdentityLogic extends CharacterIdentityLogic
{
	protected Player _me;
	
	private CharacterIdentityData _data;
	
	/** 所控制单位组 */
	private IntSet _controlUnits=new IntSet();
	
	@Override
	public void init()
	{
		super.init();
		
		_data=(CharacterIdentityData)_unit.getUnitData().identity;
		_unitName=_data.roleShowData.name;
	}
	
	@Override
	public void dispose()
	{
		unbindAllControlUnit();
		
		super.dispose();
		
		_data=null;
		_me=null;
	}
	
	/** 获取身份数据 */
	public CharacterIdentityData getData()
	{
		return _data;
	}
	
	@Override
	public void writeInfo(StringBuilder sb)
	{
		sb.append(" 角色ID:");
		sb.append(_data.playerID);
		sb.append(" 名字:");
		sb.append(_data.roleShowData.name);
	}
	
	/** 设置玩家绑定 */
	public void setPlayer(Player player)
	{
		_me=player;
	}
	
	/** 玩家 */
	public Player getPlayer()
	{
		return _me;
	}
	
	@Override
	protected BaseSocket toGetSocket()
	{
		if(_me==null)
			return null;
		
		//场景未就绪(重连中)
		if(!_me.scene.isSceneSocketReady())
			return null;
		
		SystemPart sPart;
		
		//没准备好,视为空
		if(!(sPart=_me.system).getSocketReady())
			return null;
		
		return sPart.socket;
	}
	
	/** socket重置 */
	public void onSocketReplace()
	{
		_controlUnits.forEachA(k->
		{
			//置空
			_scene.getUnit(k).makeSocketDirty();
		});
		
		//已经包括自己
		//_unit.makeSocketDirty();
	}
}
