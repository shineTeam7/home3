package com.home.commonGame.scene.unit;

import com.home.commonBase.data.scene.base.DirData;
import com.home.commonBase.data.scene.base.PosData;
import com.home.commonBase.data.scene.base.PosDirData;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.scene.unit.UnitMoveLogic;
import com.home.commonGame.net.request.scene.unit.ReCUnitPullBackRequest;
import com.home.commonGame.net.request.scene.unit.UnitDriveRequest;
import com.home.commonGame.net.request.scene.unit.UnitGetOffVehicleRequest;
import com.home.commonGame.net.request.scene.unit.UnitGetOnVehicleRequest;
import com.home.commonGame.net.request.scene.unit.UnitMoveDirRequest;
import com.home.commonGame.net.request.scene.unit.UnitMovePosListRequest;
import com.home.commonGame.net.request.scene.unit.UnitMovePosRequest;
import com.home.commonGame.net.request.scene.unit.UnitSetPosDirRequest;
import com.home.commonGame.net.request.scene.unit.UnitSpecialMoveRequest;
import com.home.commonGame.net.request.scene.unit.UnitStopMoveRequest;

/** 单位移动逻辑 */
public class GameUnitMoveLogic extends UnitMoveLogic
{
	@Override
	protected void sendStopMove(boolean needSelf)
	{
		_unit.radioMessage(UnitStopMoveRequest.create(_unit.instanceID,_unit.pos.getPosDir()),needSelf);
	}
	
	@Override
	protected void sendSpecialMove(int id,int[] args,boolean needSelf)
	{
		_unit.radioMessage(UnitSpecialMoveRequest.create(_unit.instanceID,id,_unit.pos.getPosDir(),args,_d.specialMoveLastTime,_d.baseMovePos),needSelf);
	}
	
	@Override
	protected void sendPullBack()
	{
		_unit.send(ReCUnitPullBackRequest.create(_unit.instanceID,_unit.pos.getPosDir()));
	}
	
	@Override
	protected void sendMoveDir(DirData dir,DirData realDir,float realSpeedRatio)
	{
		_unit.radioMessage(UnitMoveDirRequest.create(_unit.instanceID,_d.moveType,dir,realDir,realSpeedRatio<0f ? -1 : Math.round(realSpeedRatio*100f)),_currentMoveIsInitiative);
	}
	
	@Override
	protected void sendMovePos()
	{
		int time=0;
		
		if(CommonSetting.needMoveLerp)
		{
			time=(int)(_scenePosLogic.calculatePosDistance(_pos,_d.baseMovePos)/_useMoveSpeedM);
		}
		
		_unit.radioMessage(UnitMovePosRequest.create(_unit.instanceID,_d.moveType,_d.baseMovePos,time),_currentMoveIsInitiative);
	}
	
	@Override
	protected void sendMoveList()
	{
		int time=0;
		
		if(CommonSetting.needMoveLerp)
		{
			time=(int)(_scenePosLogic.calculatePosDistance(_pos,_moveList.get(0))/_useMoveSpeedM);
		}
		
		if(_moveList.size()==1)
		{
			_unit.radioMessage(UnitMovePosRequest.create(_unit.instanceID,_d.moveType,_moveList.get(0),time),_currentMoveIsInitiative);
		}
		else
		{
			_unit.radioMessage(UnitMovePosListRequest.create(_unit.instanceID,_d.moveType,_moveList,time),_currentMoveIsInitiative);
		}
	}
	
	@Override
	protected void sendDrive()
	{
		_unit.radioMessage(UnitDriveRequest.create(_unit.instanceID,_unit.pos.getPosDir(),_d.driveData),_currentMoveIsInitiative);
	}
	
	@Override
	protected void sendGetOnVehicle(int vehicleInstanceID,int index)
	{
		_unit.radioMessage(UnitGetOnVehicleRequest.create(_unit.instanceID,vehicleInstanceID,index),true);
	}
	
	@Override
	protected void sendGetOffVehicle(PosData pos)
	{
		_unit.radioMessage(UnitGetOffVehicleRequest.create(_unit.instanceID,pos),true);
	}
}
