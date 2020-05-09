namespace Shine
{
    /** 数据注册类 */
    export class PlayerItemDicContainerTool extends ItemDicContainerTool implements IPlayerFuncTool
    {
        public me:Player;

        constructor(funcID:number)
        {
            super(funcID);
        }

        public setMe(player:Player):void
        {
            this.me=player;
        }

        public createItemByType(type:number)
        {
            return this.me.bag.createItemByType(type);
        }

        public releaseItem(data:ItemData):void
        {
             this.me.bag.releaseItem(data);
        }

        public onItemAdd(index:number,data:ItemData,num:number):void
        {
            super.onItemAdd(index,data,num);
            //new number[]{_funcID,index}
            var arr:number[] = [this._funcID,index];

            this.me.dispatch(GameEventType.FuncItemContainerRefreshGrid,arr);

            if(this._funcID==FunctionType.MainBag)
            {
                 this.me.dispatch(GameEventType.MainBagRefreshGrid,index);
                 this.me.dispatch(GameEventType.MainBagItemChange,data.id);
            }
        }

        public onItemRemove(index:number,data:ItemData,num:number):void
        {
            super.onItemRemove(index,data,num);
            var arr:number[] = [this._funcID,index];
            this.me.dispatch(GameEventType.FuncItemContainerRefreshGrid,arr);

            if(this._funcID==FunctionType.MainBag)
            {
                 this.me.dispatch(GameEventType.MainBagRefreshGrid,index);
                 this.me.dispatch(GameEventType.MainBagItemChange,data.id);
            }
        }

        public onChanged():void
        {
            super.onChanged();

             this.me.dispatch(GameEventType.FuncItemContainerChange,this._funcID);

            if(this._funcID==FunctionType.MainBag)
            {
                 this.me.dispatch(GameEventType.MainBagChange);
            }

            // this.me.bag.printBag();
        }

        public onRefreshAll():void
        {
             this.me.dispatch(GameEventType.FuncItemContainerRefreshAll,this._funcID);

            if(this._funcID==FunctionType.MainBag)
            {
                 this.me.dispatch(GameEventType.MainBagRefreshAll);
            }

            super.onRefreshAll();
        }

        public checkCanUseItem(data:ItemData,num:number,arg:UseItemArgData,needNotice:boolean):boolean
        {
            return  this.me.bag.checkItemUseConditions(data,num,arg,needNotice);
        }

        public toSendUseItem(data:ItemData,index:number,num:number,arg:UseItemArgData):void
        {
             this.me.send(FuncUseItemRequest.createFuncUseItem(this._funcID,index,num,data.id,arg));
        }

        public toUseItem(data:ItemData,num:number,arg:UseItemArgData):void
        {
             this.me.bag.toUseItem(data,num,arg);
        }

        /** 执行使用物品通过id */
        public toUseItemId(id:number,num:number,arg:UseItemArgData):void
        {
            this.me.bag.toUseItemId(id,num,arg);
        }

        /** 服务器返回使用物品结果 */
        public doUseItemResult(id:number,num:number,result:boolean):void
        {
            if(this._funcID==FunctionType.MainBag)
            {
                 this.me.bag.useItemResult(id,num,result);
            }
        }

        public onRedPointChange():void
        {
            if(this._funcID==FunctionType.MainBag)
            {
               // GameC.redPoint.refreshOne(RedPointType.Bag);
            }
        }

        public onRedPointRemoved(index:number):void
        {
            super.onRedPointRemoved(index);

            //发送服务器
             this.me.send(FuncItemRemoveRedPointRequest.createFuncItemRemoveRedPoint(this._funcID,index));

            var arr:number[] = [this._funcID,index];
            //更新单格
             this.me.dispatch(GameEventType.FuncItemContainerRefreshGrid,arr);

            if(this._funcID==FunctionType.MainBag)
            {
                 this.me.dispatch(GameEventType.MainBagRefreshGrid,index);
            }
        }

        public onAddItemNotice(list:SList<ItemData>,way:number):void
        {
            var param:SMap<string,Object> =new SMap<string,Object>();
            param.put("list",list);
            param.put("way",way);

            this.me.dispatch(GameEventType.AddItemNotice,param);
        }
    }
}