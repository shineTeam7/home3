package com.home.commonBase.control;

import com.home.commonBase.config.game.enumT.ClientPlatformTypeConfig;
import com.home.commonBase.constlist.generate.BulletCastType;
import com.home.commonBase.constlist.generate.ClientPlatformType;
import com.home.commonBase.constlist.generate.RobotTestModeType;
import com.home.commonBase.constlist.generate.RoleGroupHandleResultType;
import com.home.commonBase.constlist.generate.SceneElementType;
import com.home.commonBase.constlist.generate.SceneForceType;
import com.home.commonBase.constlist.generate.SceneInstanceType;
import com.home.commonBase.constlist.generate.SkillTargetType;
import com.home.commonBase.constlist.generate.SkillVarSourceType;
import com.home.commonBase.constlist.generate.UnitType;

/** 常量方法控制 */
public class BaseConstControl
{
	/** 是否服务器驱动的子弹 */
	public boolean bulletCast_isBulletOnServer(int type)
	{
		return type!=BulletCastType.StraightForShow;
	}
	
	/** 是否为简单子弹 */
	public boolean bulletCast_isSimpleBullet(int type)
	{
		return type>=BulletCastType.Immediately && type<=BulletCastType.LockGroundBySpeed;
	}
	
	/** 是否自身碰撞子弹 */
	public boolean bulletCast_isSelfHit(int type)
	{
		return type==BulletCastType.HitSelfCircle || type==BulletCastType.HitSelfRect;
	}
	
	/** 获取平台名 */
	public String clientPlatform_getName(int type)
	{
		//避免找不到配置
		ClientPlatformTypeConfig config=ClientPlatformTypeConfig.get(type);
		if(config==null)
		{
			return "";
		}
		
		return config.name;
	}
	
	/** 通过名字获取类型 */
	public int clientPlatform_getTypeByName(String name)
	{
		switch(name)
		{
			case "windows":
				return ClientPlatformType.Windows;
			case "mac":
				return ClientPlatformType.Mac;
			case "android":
				return ClientPlatformType.Android;
			case "ios":
				return ClientPlatformType.Ios;
		}
		
		return -1;
	}
	
	/** 通过类型获取名字 */
	public String clientPlatform_getNameByType(int type)
	{
		switch(type)
		{
			case ClientPlatformType.Windows:
				return "windows";
			case ClientPlatformType.Mac:
				return "mac";
			case ClientPlatformType.Android:
				return "android";
			case ClientPlatformType.Ios:
				return "ios";
		}
		return "";
	}
	
	/** 是否需要登录挤号 */
	public boolean robotTestMode_needLoginCrowed(int type)
	{
		switch(type)
		{
			case RobotTestModeType.LoginCrowed:
			case RobotTestModeType.SwitchTownAndLoginCrowed:
				return true;
		}
		
		return false;
	}
	
	/** 是否上下线 */
	public boolean robotTestMode_needLoginOut(int type)
	{
		switch(type)
		{
			case RobotTestModeType.LoginOut:
			case RobotTestModeType.SwitchTownAndLoginOut:
				return true;
		}
		
		return false;
	}
	
	/** 是否需要切换主城 */
	public boolean robotTestMode_needSwitchTown(int type)
	{
		switch(type)
		{
			case RobotTestModeType.SwitchTown:
			case RobotTestModeType.SwitchTownAndLoginOut:
			case RobotTestModeType.SwitchTownAndLoginCrowed:
				return true;
		}
		
		return false;
	}
	
	/** 是否需要切换场景 */
	public boolean robotTestMode_needEnterScene(int type)
	{
		if(robotTestMode_needSwitchTown(type))
			return true;
		
		switch(type)
		{
			case RobotTestModeType.RandomMove:
				return true;
		}
		
		return false;
	}
	
	/** 是否需要返回 */
	public boolean roleGroupHandleResult_needReback(int type)
	{
		return type==RoleGroupHandleResultType.Agree || type==RoleGroupHandleResultType.Refuse;
	}
	
	/** 是否是单位类型 */
	public boolean sceneElement_isUnit(int type)
	{
		switch(type)
		{
			case SceneElementType.Point:
			case SceneElementType.Region:
				return false;
		}
		
		return true;
	}
	
	/** 是否由客户端创建初始化创建的单位 */
	public boolean sceneElement_isClientCreate(int type)
	{
		switch(type)
		{
			case SceneElementType.Npc:
			case SceneElementType.SceneEffect:
				return true;
		}
		
		return false;
	}
	
	/** 是否为中立势力 */
	public boolean sceneForce_isNeutral(int force)
	{
		switch(force)
		{
			case SceneForceType.NeutralEnemy:
			case SceneForceType.Neutral:
			case SceneForceType.NeutralFriend:
				return true;
		}
		
		return false;
	}
	
	/** 是否为主城场景 */
	public boolean sceneInstance_isTown(int type)
	{
		switch(type)
		{
			case SceneInstanceType.SingleInstance:
			case SceneInstanceType.LinedSingleInstance:
			case SceneInstanceType.AutoLinedScene:
				return true;
		}
		
		return false;
	}
	
	/** 是否为限定进入场景 */
	public boolean sceneInstance_isSignedIn(int type)
	{
		switch(type)
		{
			case SceneInstanceType.SingleInstance:
			case SceneInstanceType.LinedSingleInstance:
			case SceneInstanceType.AutoLinedScene:
				return false;
		}
		
		return true;
	}
	
	/** 是否为多人副本 */
	public boolean sceneInstance_isMultiBattle(int type)
	{
		switch(type)
		{
			case SceneInstanceType.SingleInstance:
			case SceneInstanceType.SinglePlayerBattle:
			case SceneInstanceType.LinedSingleInstance:
			case SceneInstanceType.AutoLinedScene:
				return false;
		}
		
		return true;
	}
	
	/** 是否为多实例场景 */
	public boolean sceneInstance_isMultiInstance(int type)
	{
		switch(type)
		{
			case SceneInstanceType.SingleInstance:
			case SceneInstanceType.LinedSingleInstance:
			case SceneInstanceType.AutoLinedScene:
				return false;
		}
		
		return true;
	}
	
	/** 是否为限定场景 */
	public boolean sceneInstance_isFinite(int type)
	{
		return type==SceneInstanceType.PreBattle || type==SceneInstanceType.FiniteBattle || type==SceneInstanceType.FiniteBattleWithFrameSync;
	}
	
	/** 是否是简版场景 */
	public boolean sceneInstance_isSimple(int type)
	{
		return type==SceneInstanceType.PreBattle;
	}
	
	/** 客户端是否可主动申请进入 */
	public boolean sceneInstance_canClientApplyEnter(int type)
	{
		switch(type)
		{
			case SceneInstanceType.SingleInstance:
			case SceneInstanceType.SinglePlayerBattle:
			case SceneInstanceType.LinedSingleInstance:
			case SceneInstanceType.AutoLinedScene:
			{
				return true;
			}
		}
		
		return false;
	}
	
	/** 是否需要检查施法距离 */
	public boolean skillTarget_needCheckDistance(int type)
	{
		switch(type)
		{
			case SkillTargetType.Single:
			case SkillTargetType.Ground:
				return true;
		}
		
		return false;
	}
	
	/** 是否是目标的类型 */
	public boolean skillVarSource_isTarget(int type)
	{
		switch(type)
		{
			case SkillVarSourceType.TargetAttribute:
			case SkillVarSourceType.TargetLevel:
			case SkillVarSourceType.TargetCurrentAttributePercent:
			case SkillVarSourceType.TargetCurrentAttributeLostPercent:
			case SkillVarSourceType.TargetBuffFloor:
			{
				return true;
			}
		}
		
		return false;
	}
	
	/** 是否可参与战斗 */
	public boolean unit_canFight(int type)
	{
		switch(type)
		{
			case UnitType.Character:
			case UnitType.Monster:
			case UnitType.Pet:
			case UnitType.Puppet:
			case UnitType.Vehicle:
			case UnitType.Building:
				return true;
		}
		
		return false;
	}
	
	/** 是否主单位 */
	public boolean unit_isMUnit(int type)
	{
		switch(type)
		{
			case UnitType.Character:
			case UnitType.Pet:
				return true;
		}
		
		return false;
	}
	
	/** 地图移动类型，获取mask */
	public int mapMoveType_getMask(int type)
	{
		//TODO:后续补充
		return -1;
	}
}
