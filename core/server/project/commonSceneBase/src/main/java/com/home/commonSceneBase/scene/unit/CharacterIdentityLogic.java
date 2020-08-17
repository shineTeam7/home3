package com.home.commonSceneBase.scene.unit;

import com.home.commonBase.data.scene.unit.identity.CharacterIdentityData;
import com.home.commonBase.scene.base.Unit;
import com.home.commonBase.scene.unit.UnitIdentityLogic;
import com.home.commonSceneBase.part.IScenePlayer;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.net.socket.BaseSocket;
import com.home.shine.support.collection.IntSet;

public class CharacterIdentityLogic extends UnitIdentityLogic
{
	protected IScenePlayer _me;
	
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
	public void setPlayer(IScenePlayer player)
	{
		_me=player;
	}
	
	/** 玩家 */
	public IScenePlayer getPlayer()
	{
		return _me;
	}
	
	@Override
	protected BaseSocket toGetSocket()
	{
		if(_me==null)
			return null;
		
		//场景未就绪(重连中)
		if(!_me.isSocketReady())
			return null;
		
		return _me.getSocket();
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
	
	/** 清空所有控制单位 */
	protected void unbindAllControlUnit()
	{
		_controlUnits.forEachS(k->
		{
			Unit unit;
			if((unit=_scene.getUnit(k))!=null)
			{
				unit.identity.unbindControlLogic();
			}
			else
			{
				Ctrl.throwError("不该找不到单位",unit.getInfo());
			}
		});
	}
	
	public void addControlUnit(int instanceID)
	{
		_controlUnits.add(instanceID);
	}
	
	public void removeControlUnit(int instanceID)
	{
		_controlUnits.remove(instanceID);
	}
	
	/** 获取控制单位组 */
	public IntSet getControlUnits()
	{
		return _controlUnits;
	}
}
