package com.home.commonData.data.system;

import java.util.List;

/** 客户端离线事务组消息 */
public final class ClientOfflineWorkListDO
{
	/** 当前发送序号 */
	int index;
	/** 数据组 */
	List<ClientOfflineWorkDO> list;
	/** 客户端随机种子序号 */
	int clientRandomSeedIndex;
}
