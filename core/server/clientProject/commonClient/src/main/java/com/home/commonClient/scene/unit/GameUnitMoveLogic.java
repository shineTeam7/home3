package com.home.commonClient.scene.unit;

import com.home.commonBase.constlist.generate.UnitAICommandType;
import com.home.commonBase.data.scene.base.DirData;
import com.home.commonBase.scene.base.SceneObject;
import com.home.commonBase.scene.unit.UnitMoveLogic;
import com.home.commonClient.net.sceneBaseRequest.unit.CUnitMoveDirRequest;
import com.home.commonClient.net.sceneBaseRequest.unit.CUnitMovePosListRequest;
import com.home.commonClient.net.sceneBaseRequest.unit.CUnitMovePosRequest;
import com.home.commonClient.net.sceneBaseRequest.unit.CUnitSpecialMoveRequest;
import com.home.commonClient.net.sceneBaseRequest.unit.CUnitStopMoveRequest;
import com.home.commonClient.scene.base.GameUnit;
import com.home.shine.utils.MathUtils;

public class GameUnitMoveLogic extends UnitMoveLogic
{
	private GameUnit _gameUnit;
	
	private boolean _needRandomMove=false;
	
	@Override
	public void setObject(SceneObject obj)
	{
		super.setObject(obj);
		
		_gameUnit=(GameUnit)obj;
	}
	
	@Override
	public void init()
	{
		super.init();
	}
	
	@Override
	protected void sendMoveDir(DirData dir,DirData realDir,float realSpeedRatio)
	{
		if(_realMoveSpeedRatio>=0f)
		{
			_gameUnit.me.send(CUnitMoveDirRequest.create(_unit.instanceID,_d.moveType,_pos,dir,realDir,Math.round(_realMoveSpeedRatio*100f)));
		}
		else
		{
			_gameUnit.me.send(CUnitMoveDirRequest.create(_unit.instanceID,_d.moveType,_pos,dir,realDir,-1));
		}
	}
	
	@Override
	protected void sendMovePos()
	{
		_gameUnit.me.send(CUnitMovePosRequest.create(_unit.instanceID,_d.moveType,_pos,_baseMovePos));
	}
	
	@Override
	protected void sendMoveList()
	{
		if(_moveList.size()==1)
		{
			_gameUnit.me.send(CUnitMovePosRequest.create(_unit.instanceID,_d.moveType,_pos,_moveList.get(0)));
		}
		else
		{
			_gameUnit.me.send(CUnitMovePosListRequest.create(_unit.instanceID,_d.moveType,_pos,_moveList));
		}
	}
	
	@Override
	protected void sendSpecialMove(int id,int[] args,boolean needSelf)
	{
		_gameUnit.me.send(CUnitSpecialMoveRequest.create(_unit.instanceID,id,_unit.pos.getPosDir(),args));
	}
	
	@Override
	protected void sendStopMove(boolean needSelf)
	{
		_gameUnit.me.send(CUnitStopMoveRequest.create(_unit.instanceID,_unit.pos.getPosDir()));
	}
	
	@Override
	public void onFrame(int delay)
	{
		super.onFrame(delay);
		
		if(_needRandomMove)
		{
			randomMove();
		}
	}
	
	public void randomMove()
	{
		_tempPos.x=_scene.originPos.x+ MathUtils.randomInt((int)_scene.sizePos.x);
		_tempPos.z=_scene.originPos.z+MathUtils.randomInt((int)_scene.sizePos.z);
		
		_unit.aiCommand.moveTo(_tempPos,this::moveOver);
		
		//失败了
		if(_unit.aiCommand.getCurrentCommand()==UnitAICommandType.MoveTo)
		{
			_needRandomMove=false;
		}
		else
		{
			_needRandomMove=true;
		}
	}
	
	private void moveOver()
	{
		_needRandomMove=true;
	}
}