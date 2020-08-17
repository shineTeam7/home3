package com.home.commonGame.scene.unit;

import com.home.commonBase.global.CommonSetting;
import com.home.commonGame.part.player.Player;
import com.home.commonGame.part.player.part.SystemPart;
import com.home.commonGame.scene.base.GameUnit;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.ReCUnitSkillFailedExRequest;
import com.home.commonSceneBase.scene.unit.BUnitFightLogic;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.SList;

/** 游戏单位战斗逻辑 */
public class GameUnitFightLogic extends BUnitFightLogic
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
}
