using System;
using ShineEngine;

/// <summary>
///
/// </summary>
public class PlayerItemDicContainerTool:ItemDicContainerTool,IPlayerFuncTool
{
	public Player me;

	public PlayerItemDicContainerTool(int funcID):base(funcID)
	{

	}

	public void setMe(Player player)
	{
		me=player;
	}

	protected override ItemData createItemByType(int type)
	{
		return me.bag.createItemByType(type);
	}

	protected override void releaseItem(ItemData data)
	{
		me.bag.releaseItem(data);
	}

	protected override void onItemAdd(int index,ItemData data,int num,int way)
	{
		base.onItemAdd(index,data,num,way);

		me.dispatch(GameEventType.FuncItemContainerRefreshGrid,new int[]{_funcID,index});

		if(_funcID==FunctionType.MainBag)
		{
			me.dispatch(GameEventType.MainBagRefreshGrid,index);
			me.dispatch(GameEventType.MainBagItemChange,data.id);
		}
	}

	protected override void onItemRemove(int index,ItemData data,int num,int way)
	{
		base.onItemRemove(index,data,num,way);

		me.dispatch(GameEventType.FuncItemContainerRefreshGrid,new int[]{_funcID,index});

		if(_funcID==FunctionType.MainBag)
		{
			me.dispatch(GameEventType.MainBagRefreshGrid,index);
			me.dispatch(GameEventType.MainBagItemChange,data.id);
		}
	}

	protected override void onChanged()
	{
		base.onChanged();

		me.dispatch(GameEventType.FuncItemContainerChange,_funcID);

		if(_funcID==FunctionType.MainBag)
		{
			me.dispatch(GameEventType.MainBagChange);
		}

		//me.bag.printBag();
	}

	protected override void onRefreshAll()
	{
		me.dispatch(GameEventType.FuncItemContainerRefreshAll,_funcID);

		if(_funcID==FunctionType.MainBag)
		{
			me.dispatch(GameEventType.MainBagRefreshAll);
		}

		base.onRefreshAll();
	}

	public override bool checkCanUseItem(ItemData data,int num,UseItemArgData arg,bool needNotice)
	{
		return me.bag.checkItemUseConditions(data,num,arg,needNotice);
	}

	protected override void toSendUseItem(ItemData data,int index,int num,UseItemArgData arg)
	{
		me.send(FuncUseItemRequest.create(_funcID,index,num,data.id,arg));
	}

	protected override void toUseItem(ItemData data,int num,UseItemArgData arg)
	{
		me.bag.toUseItem(data,num,arg);
	}

	protected override void toUseItem(int id,int num,UseItemArgData arg)
	{
		me.bag.toUseItem(id,num,arg);
	}

	/** 服务器返回使用物品结果 */
	protected override void doUseItemResult(int id,int num,Boolean result)
	{
		if(_funcID==FunctionType.MainBag)
		{
			me.bag.useItemResult(id,num,result);
		}
	}

	protected override void onRedPointChange()
	{
		if(_funcID==FunctionType.MainBag)
		{
			GameC.redPoint.refreshOne(RedPointType.Bag);
		}
	}

	protected override void onRedPointRemoved(int index)
	{
		base.onRedPointRemoved(index);

		//发送服务器
		me.send(FuncItemRemoveRedPointRequest.create(_funcID,index));

		//更新单格
		me.dispatch(GameEventType.FuncItemContainerRefreshGrid,new int[]{_funcID,index});

		if(_funcID==FunctionType.MainBag)
		{
			me.dispatch(GameEventType.MainBagRefreshGrid,index);
		}
	}

	protected override void onAddItemNotice(SList<ItemData> list,int way)
	{
		SMap<string,object> param=new SMap<string, object>();
		param.put("list",list);
		param.put("way",way);

		me.dispatch(GameEventType.AddItemNotice,param);
	}
}