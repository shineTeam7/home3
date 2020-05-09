package com.home.commonGame.scene.unit;

import com.home.commonBase.data.scene.base.BuffData;
import com.home.commonBase.data.scene.base.BulletData;
import com.home.commonBase.data.scene.base.CDData;
import com.home.commonBase.data.scene.base.PosDirData;
import com.home.commonBase.data.scene.fight.DamageOneData;
import com.home.commonBase.data.scene.fight.SkillTargetData;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.scene.base.Unit;
import com.home.commonBase.scene.unit.UnitAOILogic;
import com.home.commonBase.scene.unit.UnitAOITowerLogic;
import com.home.commonBase.scene.unit.UnitFightLogic;
import com.home.commonGame.net.request.scene.unit.AddBulletRequest;
import com.home.commonGame.net.request.scene.unit.AttackDamageOneRequest;
import com.home.commonGame.net.request.scene.unit.AttackDamageRequest;
import com.home.commonGame.net.request.scene.unit.ReCUnitSkillFailedExRequest;
import com.home.commonGame.net.request.scene.unit.ReCUnitSkillFailedRequest;
import com.home.commonGame.net.request.scene.unit.RefreshUnitAttributesRequest;
import com.home.commonGame.net.request.scene.unit.RefreshUnitStatusRequest;
import com.home.commonGame.net.request.scene.unit.RemoveBulletRequest;
import com.home.commonGame.net.request.scene.unit.UnitAddBuffRequest;
import com.home.commonGame.net.request.scene.unit.UnitAddGroupTimeMaxPercentRequest;
import com.home.commonGame.net.request.scene.unit.UnitAddGroupTimeMaxValueRequest;
import com.home.commonGame.net.request.scene.unit.UnitAddGroupTimePassRequest;
import com.home.commonGame.net.request.scene.unit.UnitDeadRequest;
import com.home.commonGame.net.request.scene.unit.UnitRefreshBuffRequest;
import com.home.commonGame.net.request.scene.unit.UnitRemoveBuffRequest;
import com.home.commonGame.net.request.scene.unit.UnitRemoveGroupCDRequest;
import com.home.commonGame.net.request.scene.unit.UnitReviveRequest;
import com.home.commonGame.net.request.scene.unit.UnitSkillOverRequest;
import com.home.commonGame.net.request.scene.unit.UnitStartCDsRequest;
import com.home.commonGame.net.request.scene.unit.UnitUseSkillRequest;
import com.home.commonGame.part.player.Player;
import com.home.commonGame.part.player.part.SystemPart;
import com.home.commonGame.scene.base.GameUnit;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.IntBooleanMap;
import com.home.shine.support.collection.IntIntMap;
import com.home.shine.support.collection.SList;

/** 游戏单位战斗逻辑 */
public class GameUnitFightLogic extends UnitFightLogic
{
	/** 检查技能几率替换结果 */
	@Override
	protected boolean checkSkillProbReplaceResult(SList<int[]> list,int skillID,int clientUseSkillID,int seedIndex)
	{
		if(!CommonSetting.needClientRandomSeeds)
		{
			//没启用客户端种子
			Ctrl.errorLog("没启用客户端种子,但是有客户端几率替换技能");
			return false;
		}
		
		Player player=((GameUnit)_unit).getControlPlayer();
		
		if(player==null)
			return false;
		
		SystemPart sp=player.system;
		
		int nowSeedIndex=sp.getSeedIndex();
		
		int[] v;
		int reID=skillID;
		
		for(int i=0,len=list.size();i<len;++i)
		{
			v=list.get(i);
			
			if(sp.getClientRandom(_buffDataLogic.getUseSkillProb(v[3])))
			{
				reID=v[2];
				break;
			}
		}
		
		//序号没对上或者ID不对
		if(sp.getSeedIndex()!=seedIndex || reID!=clientUseSkillID)
		{
			sp.setSeedIndex(nowSeedIndex);
			//技能失败
			player.send(ReCUnitSkillFailedExRequest.create(_unit.instanceID,skillID,nowSeedIndex));
			return false;
		}
		
		return true;
	}
	
	/** 推送使用技能 */
	@Override
	protected void sendServerUseSkill(int skillID,int skillLevel,SkillTargetData tData,PosDirData posDir,boolean needSelf)
	{
		_unit.radioMessage(UnitUseSkillRequest.create(_unit.instanceID,skillID,skillLevel,tData,posDir),needSelf);
	}
	
	/** 推送攻击伤害 */
	@Override
	public void sendAttackDamage(int id,int level,SkillTargetData targetData,SList<DamageOneData> damageList,SList<Unit> targets)
	{
		if(CommonSetting.isDamageOnlyRadioSelfAndTarget)
		{
			if(damageList!=null)
			{
				UnitAOILogic aoi=_unit.aoi;
				int instanceID=_unit.instanceID;
				
				DamageOneData[] values1=damageList.getValues();
				Unit[] values=targets.getValues();
				Unit v;
				
				for(int i=0,len=damageList.size();i<len;++i)
				{
					//可见
					if(aoi.isSee(v=values[i]))
					{
						v.send(AttackDamageOneRequest.create(instanceID,targetData,id,level,values1[i]));
					}
				}
			}
			
			_unit.send(AttackDamageRequest.create(_unit.instanceID,targetData,id,level,damageList));
		}
		else
		{
			_unit.radioMessage(AttackDamageRequest.create(_unit.instanceID,targetData,id,level,damageList),true);
		}
	}
	
	/** 推送单位死亡 */
	@Override
	public void sendUnitDead(int attackerInstanceID,boolean isReal,int type)
	{
		_unit.radioMessage(UnitDeadRequest.create(_unit.instanceID,attackerInstanceID,type,isReal),true);
	}
	
	/** 复活消息 */
	@Override
	protected void sendUnitRevive()
	{
		_unit.radioMessage(UnitReviveRequest.create(_unit.instanceID),true);
	}
	
	/** 推送他人属性 */
	@Override
	public void sendOtherAttribute(IntIntMap dic)
	{
		_unit.radioMessage(RefreshUnitAttributesRequest.create(_unit.instanceID,dic),_unit.identity.isCUnitNotM());
		
		UnitAOITowerLogic aoiTower;
		if((aoiTower=_unit.aoiTower)!=null)
		{
			((GameUnitAOITowerLogic)aoiTower).doAttributeChanged(dic);
		}
	}
	
	/** 推送他人状态 */
	@Override
	public void sendOtherStatus(IntBooleanMap dic)
	{
		_unit.radioMessage(RefreshUnitStatusRequest.create(_unit.instanceID,dic),_unit.identity.isCUnitNotM());
	}
	
	/** 推送CD */
	@Override
	public void sendStartCDs(SList<CDData> cds)
	{
		if(_unit.identity.isCUnitNotM())
		{
			_unit.send(UnitStartCDsRequest.create(_unit.instanceID,cds));
		}
	}
	
	/** 推送移除组CD */
	@Override
	public void sendRemoveGroupCD(int groupID)
	{
		if(_unit.identity.isCUnitNotM())
		{
			_unit.send(UnitRemoveGroupCDRequest.create(_unit.instanceID,groupID));
		}
	}
	
	/** 推送增加组CD上限百分比 */
	@Override
	public void sendAddGroupTimeMaxPercent(int groupID,int value)
	{
		if(_unit.identity.isCUnitNotM())
		{
			_unit.send(UnitAddGroupTimeMaxPercentRequest.create(_unit.instanceID,groupID,value));
		}
	}
	
	/** 推送增加组CD上限值 */
	@Override
	public void sendAddGroupTimeMaxValue(int groupID,int value)
	{
		if(_unit.identity.isCUnitNotM())
		{
			_unit.send(UnitAddGroupTimeMaxValueRequest.create(_unit.instanceID,groupID,value));
		}
	}
	
	/** 推送增加组CD时间经过 */
	@Override
	public void sendAddGroupTimePass(int groupID,int value)
	{
		if(_unit.identity.isCUnitNotM())
		{
			_unit.send(UnitAddGroupTimePassRequest.create(_unit.instanceID,groupID,value));
		}
	}
	
	/** 推送添加buff */
	@Override
	public void sendAddBuff(BuffData data)
	{
		//主角的自己推
		_unit.radioMessage(UnitAddBuffRequest.create(_unit.instanceID,data),_unit.identity.isCUnitNotM());
	}
	
	/** 推送删除buff */
	@Override
	public void sendRemoveBuff(int instanceID)
	{
		//主角的自己推
		_unit.radioMessage(UnitRemoveBuffRequest.create(_unit.instanceID,instanceID),_unit.identity.isCUnitNotM());
	}
	
	/** 推送刷新buff */
	@Override
	public void sendRefreshBuff(int instanceID,int lastTime,int lastNum)
	{
		//主角的自己推
		_unit.radioMessage(UnitRefreshBuffRequest.create(_unit.instanceID,instanceID,lastTime,lastNum),_unit.identity.isCUnitNotM());
	}
	
	/** 推送刷新buff剩余次数 */
	@Override
	public void sendRefreshBuffLastNum(int instanceID,int num)
	{
		//只显示用的就不推了
		
		//if(_unit.isCUnitNotM())
		//{
		//	_unit.send(UnitRefreshBuffLastNumRequest.create(_unit.instanceID,instanceID,num));
		//}
	}
	
	//--单位部分--//
	
	@Override
	protected void sendClientSkillFailed(int skillID)
	{
		_unit.send(ReCUnitSkillFailedRequest.create(_unit.instanceID,skillID));
	}
	
	/** 推送添加子弹 */
	protected void sendAddBullet(BulletData data,boolean needSelf)
	{
		_unit.radioMessage(AddBulletRequest.create(_unit.instanceID,data),needSelf);
	}
	
	/** 推送移除子弹 */
	protected void sendRemoveBullet(int bulletInstanceID,boolean needSelf)
	{
		_unit.radioMessage(RemoveBulletRequest.create(_unit.instanceID,bulletInstanceID),needSelf);
	}
	
	@Override
	protected void sendSkillOver(boolean needBreak)
	{
		_unit.radioMessage(UnitSkillOverRequest.create(_unit.instanceID,needBreak),true);
	}
}
