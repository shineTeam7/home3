package com.home.commonData.data.login;

import com.home.commonData.data.scene.scene.SceneEnterArgDO;
import com.home.commonData.data.social.RoleSocialDO;
import com.home.commonData.data.social.roleGroup.PlayerRoleGroupDO;
import com.home.shineData.support.MapKeyInValue;
import com.home.shineData.support.OnlyS;

import java.util.Map;

/** 角色切换游戏服额外传输数据(在PlayerList之外) */
@OnlyS
public class PlayerSwitchGameDO
{
	/** 是否需要中心服角色显示信息 */
	boolean needPlayerRoleShowForCenter;
	/** 是否是切换登录 */
	boolean isSwitchLogin;
	/** 社交数据组 */
	@MapKeyInValue("showData.playerID")
	Map<Long,RoleSocialDO> roleSocialDatas;
	/** 玩家群组(key:funcID) */
	Map<Integer,Map<Long,PlayerRoleGroupDO>> roleGroups;
	/** 下个进入场景数据 */
	SceneEnterArgDO nextEnterSceneData;
}
