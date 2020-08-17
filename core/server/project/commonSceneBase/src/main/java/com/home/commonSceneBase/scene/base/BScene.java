package com.home.commonSceneBase.scene.base;

import com.home.commonBase.data.scene.scene.SceneLocationData;
import com.home.commonBase.scene.base.Scene;
import com.home.commonBase.scene.base.Unit;
import com.home.commonBase.scene.scene.SceneAOILogic;
import com.home.commonBase.scene.scene.SceneBattleLogic;
import com.home.commonBase.scene.scene.SceneInOutLogic;
import com.home.commonBase.scene.scene.SceneMethodLogic;
import com.home.commonSceneBase.scene.scene.BSceneBattleLogic;
import com.home.commonSceneBase.scene.scene.BSceneInoutLogic;
import com.home.commonSceneBase.scene.scene.BSceneMethodLogic;
import com.home.commonSceneBase.scene.scene.SceneAOIAllLogic;
import com.home.commonSceneBase.scene.scene.ScenePlayerLogic;

/** 服务器基础场景 */
public class BScene extends Scene
{
	/** 玩家相关逻辑 */
	public ScenePlayerLogic player;
	/** 方法逻辑 */
	public BSceneMethodLogic bMethod;
	
	/** 注册逻辑体 */
	protected void registLogics()
	{
		super.registLogics();
		
		player=createPlayerLogic();
		bMethod=(BSceneMethodLogic)method;
	}
	
	protected ScenePlayerLogic createPlayerLogic()
	{
		return new ScenePlayerLogic();
	}
	
	@Override
	protected SceneMethodLogic createMethodLogic()
	{
		return new BSceneMethodLogic();
	}
	
	@Override
	protected SceneInOutLogic createInOutLogic()
	{
		return new BSceneInoutLogic();
	}
	
	@Override
	protected SceneBattleLogic createBattleLogic()
	{
		return new BSceneBattleLogic();
	}
	
	@Override
	protected SceneAOILogic createAOILogic()
	{
		return new SceneAOIAllLogic();
	}
	
	/** 检查该单位是否可被某玩家控制 */
	public boolean checkUnitCanBeControl(Unit unit,long playerID)
	{
		return unit.identity.controlPlayerID==playerID;
	}
	
	/** 获取客户端控制单位 */
	public Unit getPlayerControlUnit(long playerID,int instanceID)
	{
		if(!method.canOperate())
		{
			warnLog("当前场景状态不可操作",playerID);
			return null;
		}
		
		Unit unit=getUnit(instanceID);
		
		if(unit==null)
		{
			warnLog("客户端操怍时,获取单位为空",playerID,instanceID);
			return null;
		}
		
		if(!checkUnitCanBeControl(unit,playerID))
		{
			warnLog("客户端操作时,单位不可控",playerID,unit.instanceID);
			return null;
		}
		
		return unit;
	}
	
	/** 创建场景进入数据 */
	public SceneLocationData createLocationData()
	{
		SceneLocationData re=new SceneLocationData();
		makeSceneLocationData(re);
		return re;
	}
	
	protected void makeSceneLocationData(SceneLocationData data)
	{
		data.sceneID=getConfig().id;
		data.executorIndex=_executor.getIndex();
		data.instanceID=instanceID;
		data.lineID=_lineID;
	}
}
