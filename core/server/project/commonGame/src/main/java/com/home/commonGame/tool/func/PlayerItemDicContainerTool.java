package com.home.commonGame.tool.func;

import com.home.commonBase.data.item.ItemData;
import com.home.commonBase.data.item.UseItemArgData;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.tool.func.ItemDicContainerTool;
import com.home.commonGame.net.request.func.item.FuncAddItemRequest;
import com.home.commonGame.net.request.func.item.FuncAddOneItemNumRequest;
import com.home.commonGame.net.request.func.item.FuncAddOneItemRequest;
import com.home.commonGame.net.request.func.item.FuncRemoveItemRequest;
import com.home.commonGame.net.request.func.item.FuncRemoveOneItemRequest;
import com.home.commonGame.part.player.Player;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.collection.IntIntMap;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.timer.ITimeEntity;

public class PlayerItemDicContainerTool extends ItemDicContainerTool implements IPlayerFuncTool
{
	/** 角色 */
	public Player me;
	
	public PlayerItemDicContainerTool(int funcID)
	{
		super(funcID);
	}
	
	@Override
	public void setMe(Player player)
	{
		me=player;
	}
	
	@Override
	public ITimeEntity getTimeEntity() {
		return me;
	}
	
	@Override
	public void afterReadDataSecond()
	{
	
	}
	
	@Override
	public void beforeLoginOnMain()
	{
	
	}
	
	@Override
	public void beforeEnterOnMain()
	{
	
	}
	
	@Override
	public void beforeLogin()
	{
	
	}
	
	@Override
	public long countFightForce()
	{
		return 0;
	}
	
	@Override
	protected ItemData createItemByType(int type)
	{
		return me.bag.createItemByType(type);
	}
	
	@Override
	protected void releaseItem(ItemData data)
	{
		me.bag.releaseItem(data);
	}
	
	protected void toSendAddOneItem(int index,ItemData data,int way)
	{
		me.send(FuncAddOneItemRequest.create(_funcID,way,index,data));
	}
	
	@Override
	protected void toSendAddOneItemNum(int index,int num,int way)
	{
		me.send(FuncAddOneItemNumRequest.create(_funcID,way,index,num));
	}
	
	protected void toSendAddItem(IntIntMap autoUseItems,IntObjectMap<ItemData> dic,int way)
	{
		me.send(FuncAddItemRequest.create(_funcID,way,autoUseItems,dic));
	}
	
	protected void toSendRemoveOneItem(int index,int num,int way)
	{
		me.send(FuncRemoveOneItemRequest.create(_funcID,way,index,num));
	}
	
	protected void toSendRemoveItem(IntIntMap dic,int way)
	{
		me.send(FuncRemoveItemRequest.create(_funcID,way,dic));
	}
	
	@Override
	protected boolean checkCanUseItem(ItemData data,int num,UseItemArgData arg)
	{
		return me.bag.checkItemUseConditions(data,num,arg);
	}
	
	@Override
	protected void toUseItem(ItemData data,int num,UseItemArgData arg)
	{
		me.bag.toUseItem(data,num,arg);
	}
	
	@Override
	protected void toUseItem(int id,int num,UseItemArgData arg,int way)
	{
		me.bag.toUseItem(id,num,arg,way);
	}
	
	@Override
	protected int justGetNewIndex()
	{
		if(CommonSetting.isClient || me.system.isClientOfflining())
		{
			int index=++_data.clientItemIndex;
			
			if(index>= ShineSetting.indexMax)
				index=ShineSetting.indexMaxHalf+1;
			
			return index;
		}
		else
		{
			int index=++_data.serverItemIndex;
			
			if(index>=ShineSetting.indexMaxHalf)
				index=1;
			
			return index;
		}
	}
	
	/** 单格物品添加 */
	@Override
	protected void onItemAdd(int index,ItemData data,int num,int way)
	{
		super.onItemAdd(index,data,num,way);
		
		if(_isBag)
		{
			me.bag.onItemAdd(index,data,num,way);
		}
	}
	
	/** 单格物品移除 */
	@Override
	protected void onItemRemove(int index,ItemData data,int num,int way)
	{
		super.onItemRemove(index,data,num,way);
		
		if(_isBag)
		{
			me.bag.onItemRemove(index,data,num,way);
		}
	}
	
	@Override
	protected void onItemNumChanged(int id)
	{
		super.onItemNumChanged(id);
		
		if(_isBag)
		{
			me.bag.onItemNumChanged(id);
		}
	}
}