package com.home.commonData.message.game.request.item;

import com.home.commonData.data.item.ItemDO;
import com.home.shineData.support.MaybeNull;

import java.util.List;

/** 添加奖励消息 */
public class AddRewardMO
{
	/** 调用方式 */
	int way;
	/** 奖励ID */
	int rewardID;
	/** 等级 */
	int level;
	/** 奖励次数 */
	int num;
	/** 随机物品组结果 */
	@MaybeNull
	List<ItemDO> randomItemList;
}
