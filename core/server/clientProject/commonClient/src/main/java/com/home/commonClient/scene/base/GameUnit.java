package com.home.commonClient.scene.base;

import com.home.commonBase.data.scene.unit.UnitIdentityData;
import com.home.commonBase.data.scene.unit.identity.FightUnitIdentityData;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.scene.base.Unit;
import com.home.commonBase.scene.unit.UnitFightLogic;
import com.home.commonBase.scene.unit.UnitMoveLogic;
import com.home.commonClient.part.player.Player;
import com.home.commonClient.scene.unit.GameUnitFightLogic;
import com.home.commonClient.scene.unit.GameUnitMoveLogic;

/** 客户端单单位 */
public class GameUnit extends Unit
{
	private boolean _isMine=false;
	
	/** 自己 */
	public Player me;
	
	public void setIsMine(boolean value)
	{
		_isMine=value;
	}
	
	/** 是否是自己的单位 */
	public boolean isMine()
	{
		return _isMine;
	}
	
	@Override
	public void init()
	{
		if(isMine())
		{
			me=((GameScene)_scene).me;
		}
		
		super.init();
	}
	
	@Override
	protected UnitMoveLogic createMoveLogic()
	{
		return new GameUnitMoveLogic();
	}
	
	@Override
	protected UnitFightLogic createFightLogic()
	{
		return new GameUnitFightLogic();
	}
	
	@Override
	public boolean isSelfControl()
	{
		if(me==null)
			return false;
		
		UnitIdentityData identity=_data.identity;
		
		if(!BaseC.constlist.unit_canFight(identity.type))
			return false;
		
		//自己的单位
		return ((FightUnitIdentityData)identity).playerID==me.role.playerID;
	}
}
