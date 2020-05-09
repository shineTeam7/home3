package com.home.commonData.data.mail;

import com.home.commonData.data.item.ItemDO;
import com.home.shineData.support.MaybeNull;
import java.util.List;

/** 邮件数据 */
public class MailDO
{
	/** 实例ID */
	int instanceID;
	/** 邮件ID */
	int id;
	/** 时间戳 */
	long time;
	/** 参数组(如全自定义邮件，则args[0]为title,args[1]为content) */
	@MaybeNull
	String[] args;
	/** 物品列表 */
	@MaybeNull
	List<ItemDO> itemList;
	/** 是否阅读过 */
	boolean readed;
	/** 是否领取过*/
	boolean rewarded;
	/** 是否有红点 */
	boolean hasRedPoint;
	/** 平台 */
	String platform;
}
