package com.home.commonGame.tool.func;

/** 逻辑服拍卖行工具 */
public interface IGameAuctionTool
{
	/** 获取物品建议价格 */
	int getItemPrice(int id);
	/** 获取一个上架物品实例id */
	long getItemInstanceID();
}
