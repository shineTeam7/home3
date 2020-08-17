package com.home.commonClient.scene.unit;

import com.home.commonBase.data.scene.base.BulletData;
import com.home.commonBase.data.scene.fight.SkillTargetData;
import com.home.commonBase.scene.base.SceneObject;
import com.home.commonBase.scene.base.Unit;
import com.home.commonBase.scene.unit.UnitFightLogic;
import com.home.commonClient.net.sceneBaseRequest.unit.CUnitAddBulletRequest;
import com.home.commonClient.net.sceneBaseRequest.unit.CUnitAttackRequest;
import com.home.commonClient.net.sceneBaseRequest.unit.CUnitBulletHitRequest;
import com.home.commonClient.net.sceneBaseRequest.unit.CUnitKillSelfRequest;
import com.home.commonClient.net.sceneBaseRequest.unit.CUnitSkillOverRequest;
import com.home.commonClient.net.sceneBaseRequest.unit.CUnitUseSkillExRequest;
import com.home.commonClient.net.sceneBaseRequest.unit.CUnitUseSkillRequest;
import com.home.commonClient.scene.base.GameUnit;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.IntList;
import com.home.shine.support.collection.SList;
import com.home.shine.utils.MathUtils;

public class GameUnitFightLogic extends UnitFightLogic
{
	private GameUnit _gameUnit;
	
	@Override
	public void setObject(SceneObject obj)
	{
		super.setObject(obj);
		
		_gameUnit=(GameUnit)obj;
	}
	
	@Override
	protected int doSkillProbReplace(SList<int[]> list,boolean isInitiative,int skillID,int clientUseSkillID,int seedIndex)
	{
		if(isInitiative && !_unit.isDriveAll())
		{
			//不是自己的单位
			if(!_unit.isSelfControl())
			{
				Ctrl.throwError("不是自己的单位");
				return skillID;
			}
			
			int[] v;
			
			for(int i=0,len=list.size();i<len;++i)
			{
				v=list.get(i);
				
				if(_gameUnit.me.system.getClientRandom(_buffDataLogic.getUseSkillProb(v[3])))
				{
					skillID=v[2];
					break;
				}
			}
		}
		else
		{
			int[] v;
			
			for(int i=0,len=list.size();i<len;++i)
			{
				v=list.get(i);
				
				if(MathUtils.randomProb(_buffDataLogic.getUseSkillProb(v[3])))
				{
					skillID=v[2];
				}
			}
		}
		
		return skillID;
	}
	
	@Override
	protected void sendClientUseSkill(int skillID,int useSkillID, SkillTargetData tData,boolean hasProbReplace)
	{
		//有几率替换技能
		if(hasProbReplace)
		{
			_gameUnit.me.send(CUnitUseSkillExRequest.create(_unit.instanceID,skillID,tData,_unit.pos.getPosDir(),false,useSkillID,_gameUnit.me.system.getSeedIndex()));
		}
		else
		{
			_gameUnit.me.send(CUnitUseSkillRequest.create(_unit.instanceID,skillID,tData,_unit.pos.getPosDir(),false));
		}
	}
	
	@Override
	protected void sendClientSkillOver()
	{
		_gameUnit.me.send(CUnitSkillOverRequest.create(_unit.instanceID));
	}
	
	@Override
	protected void sendClientKillSelf()
	{
		_gameUnit.me.send(CUnitKillSelfRequest.create(_unit.instanceID));
	}
	
	/** 推送客户端主动发起攻击 */
	@Override
	public void sendCUnitAttack(int id,int level,SkillTargetData tData,SList<Unit> targets,boolean isBulletFirstHit)
	{
		IntList sendList=new IntList();
		
		targets.forEach(k->
		{
			sendList.add(k.instanceID);
		});
		
		_gameUnit.me.send(CUnitAttackRequest.create(_unit.instanceID,id,level,tData,sendList,isBulletFirstHit));
	}
	
	@Override
	protected void sendClientAddBullet(BulletData data)
	{
		_gameUnit.me.send(CUnitAddBulletRequest.create(_unit.instanceID,data));
	}
	
	@Override
	public void sendClientBulletHit(int id,int level,SkillTargetData tData)
	{
		_gameUnit.me.send(CUnitBulletHitRequest.create(_unit.instanceID,id,level,tData));
	}
}
