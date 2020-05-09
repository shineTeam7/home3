package com.home.commonGame.scene.base;

import com.home.commonBase.constlist.generate.SceneInstanceType;
import com.home.commonBase.constlist.generate.SceneType;
import com.home.commonBase.data.scene.scene.SceneEnterArgData;
import com.home.commonBase.data.scene.scene.SceneLocationData;
import com.home.commonBase.scene.base.Scene;
import com.home.commonBase.scene.base.Unit;
import com.home.commonBase.scene.scene.SceneAOILogic;
import com.home.commonBase.scene.scene.SceneInOutLogic;
import com.home.commonBase.scene.scene.ScenePlayLogic;
import com.home.commonBase.scene.scene.SceneRoleLogic;
import com.home.commonGame.control.LogicExecutor;
import com.home.commonGame.global.GameC;
import com.home.commonGame.part.player.Player;
import com.home.commonGame.scene.scene.GameSceneInOutLogic;
import com.home.commonGame.scene.scene.GameScenePlayLogic;
import com.home.commonGame.scene.scene.GameSceneRoleLogic;
import com.home.commonGame.scene.scene.SceneAOIAllLogic;
import com.home.shine.ctrl.Ctrl;

/** 场景 */
public class GameScene extends Scene
{
	/** 游戏进出逻辑 */
	public GameSceneInOutLogic gameInOut;
	
	/** 游戏玩法逻辑 */
	public GameScenePlayLogic gamePlay;
	
	/** 游戏角色逻辑 */
	public GameSceneRoleLogic gameRole;
	
	@Override
	protected void registLogics()
	{
		super.registLogics();
		
		gameInOut=(GameSceneInOutLogic)inout;
		gamePlay=(GameScenePlayLogic)play;
		gameRole=(GameSceneRoleLogic)role;
	}
	
	//logics
	
	/** 创建进出逻辑 */
	protected SceneInOutLogic createInOutLogic()
	{
		return new GameSceneInOutLogic();
	}
	
	@Override
	protected SceneRoleLogic createRoleLogic()
	{
		return new GameSceneRoleLogic();
	}
	
	@Override
	protected SceneAOILogic createAOILogic()
	{
		//TODO:根据场景类型选择AOI
		return new SceneAOIAllLogic();
	}
	
	@Override
	protected ScenePlayLogic createPlayLogic()
	{
		return new GameScenePlayLogic();
	}
	
	/** 获取执行器 */
	public LogicExecutor getGameExecutor()
	{
		return (LogicExecutor)_executor;
	}
	
	@Override
	public void dispose()
	{
		//清人
		gameInOut.clearAllPlayer();
		
		super.dispose();
	}
	
	//接口组
	
	/** 创建场景进入数据 */
	public SceneLocationData createLocationData()
	{
		SceneLocationData re=new SceneLocationData();
		re.sceneID=getConfig().id;
		re.gameID=GameC.app.id;
		re.executorIndex=_executor.getIndex();
		re.instanceID=instanceID;
		re.lineID=_lineID;
		return re;
	}
	
	/** 检查该单位是否可被某玩家控制 */
	public boolean checkUnitCanBeControl(Unit unit,Player player)
	{
		return unit.identity.controlPlayerID==player.role.playerID;
	}
	
	/** 获取客户端控制单位 */
	public Unit getPlayerControlUnit(Player player,int instanceID)
	{
		if(!play.canOperate())
		{
			player.warnLog("当前场景状态不可操作");
			return null;
		}
		
		Unit unit=getUnit(instanceID);
		
		if(unit==null)
		{
			player.warnLog("客户端操怍时,获取单位为空",instanceID);
			return null;
		}
		
		if(!checkUnitCanBeControl(unit,player))
		{
			player.warnLog("客户端操作时,单位不可控",unit.instanceID);
			return null;
		}
		
		return unit;
	}
	
	/** 移除该场景 */
	public void removeScene()
	{
		//析构过了
		if(!_inited)
			return;
		
		getGameExecutor().removeScene(instanceID);
	}
}
