package com.home.commonData.player.list;

import com.home.commonData.player.server.AchievementSPO;
import com.home.commonData.player.server.ActivitySPO;
import com.home.commonData.player.server.BagSPO;
import com.home.commonData.player.server.CharacterSPO;
import com.home.commonData.player.server.EquipSPO;
import com.home.commonData.player.server.FriendSPO;
import com.home.commonData.player.server.FuncSPO;
import com.home.commonData.player.server.GuideSPO;
import com.home.commonData.player.server.PetSPO;
import com.home.commonData.player.server.UnionSPO;
import com.home.commonData.player.server.MailSPO;
import com.home.commonData.player.server.QuestSPO;
import com.home.commonData.player.server.RoleSPO;
import com.home.commonData.player.server.SceneSPO;
import com.home.commonData.player.server.SocialSPO;
import com.home.commonData.player.server.SystemSPO;
import com.home.commonData.player.server.TeamSPO;
import com.home.shineData.support.NecessaryPart;

/** 玩家数据主体 */
public class PlayerLO
{
	/** 系统 */
	@NecessaryPart
	SystemSPO system;
	/** 通用功能 */
	@NecessaryPart
	FuncSPO func;
	/** 活动 */
	@NecessaryPart
	ActivitySPO activity;
	/** 玩家 */
	@NecessaryPart
	RoleSPO role;
	/** 场景 */
	@NecessaryPart
	SceneSPO scene;
	/** 角色 */
	@NecessaryPart
	CharacterSPO character;
	/** 社交 */
	@NecessaryPart
	SocialSPO social;
	/** 背包 */
	@NecessaryPart
	BagSPO bag;
	/** 邮件 */
	@NecessaryPart
	MailSPO mail;
	/** 任务 */
	@NecessaryPart
	QuestSPO quest;
	/** 引导 */
	@NecessaryPart
	GuideSPO guide;
	/** 好友 */
	@NecessaryPart
	FriendSPO friend;
	/** 装备 */
	EquipSPO equip;
	/** 组队 */
	TeamSPO team;
	/** 工会 */
	UnionSPO union;
	/** 成就 */
	AchievementSPO achievement;
	/** 宠物 */
	PetSPO pet;
}
