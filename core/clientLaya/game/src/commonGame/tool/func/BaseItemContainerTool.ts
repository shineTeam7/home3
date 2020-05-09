namespace Shine
{
    /** 数据注册类 */
    export abstract class BaseItemContainerTool extends PlayerFuncTool
    {
        /** 有过期时间的物品组(key:index) */
	    protected  _itemTimeSet:SSet<number>=new SSet<number>();
	
        //temp
        /** 操作记录组(index:dNum) */
        protected _operateRecordList:SList<number>=new SList<number>(8);
        /** 操作记录组(id:num) */
        protected _operateRecordAddListForAutoUse:SList<number>=new SList<number>(8);
        /** 操作移除记录字典(为rollBackRemove) */
        protected _operateRecordRemoveDic:SMap<number,ItemData>=new SMap<number,ItemData>();
        
        protected _tempDic:SMap<number,number>=new SMap<number,number>();
	
        /** 格子序号改变组 */
        protected _tempAddItems:SList<ItemData> =new SList<ItemData>();

        /** 红点总数 */
        protected _redPointCount:number=0;
        
        constructor(type:number,funcID:number)
        {
            super(type,funcID);
        }
        
        public onReloadConfig():void
        {
            this.forEachItem(Func.create(this,(v)=>{v.reloadConfig()}));
        }
        
        /** 遍历当前物品 */
        abstract forEachItem(consumer:Func):void;
        
        /** 获取物品 */
        abstract getItem(index:number):ItemData;
        
        /** 获取某ID的第一个物品 */
        abstract getItemByID(id:number):ItemData;
        
        /** 背包是否为空 */
        abstract isEmpty():boolean;
        
        /** 获取物品总数 */
        abstract getItemNum(itemID:number):number;
        
        /** 是否有空余格子 */
        abstract hasFreeGrid(num:number):boolean;
        
        /** 是否可叠加 */
        protected canPlus(oldItem:ItemData,newItem:ItemData):boolean
        {
            return oldItem.id==newItem.id && oldItem.isBind==newItem.isBind && oldItem.disableTime==newItem.disableTime && BaseItemContainerTool.canPlusEx(oldItem,newItem);
        }
        
        /** 是否可叠加(额外判定部分) */
        protected static canPlusEx(oldItem:ItemData,newItem:ItemData):boolean
        {
            return true;
        }

        /** 获取红点数目 */
        public getRedPointCount():number
        {
            return this._redPointCount;
        }
	
        /** 移除执行序号物品数目(核心)(不回收) */
        protected doRemoveItemByIndexC(index:number,data:ItemData):void
        {
            this.doRemoveItemByIndexCNum(index,data.num,data);
        }
        
        /** 移除执行序号物品数目(核心)(不回收) */
        abstract doRemoveItemByIndexCNum(index:number,num:number,data:ItemData):void;
        
        public onSecond(delay:number):void
        {
            super.onSecond(delay);

            if(CommonSetting.isClientDriveLogic && !this._itemTimeSet.isEmpty())
            {
                var now:number=DateControl.getTimeMillis();
                
                var has:boolean=false;
                
                var data:ItemData;
                
                var dic:SMap<number,ItemData>;
                
                this._itemTimeSet.forEach(v =>
                 {
                     data=this.getItem(v);
                        
                     //超时
                    if(now>=data.disableTime)
                    {
                        this.doRemoveItemByIndexC(v,data);
                        has=true;
                     }
                });
                
                if(has)
                {
                    this.flushRemove(CallWayType.ItemTimeOut);
                }
            }
        }
        
        /** 结算添加结果 */
        protected flushAdd(way:number):void
        {
            // IntIntMap autoUseItems=null;
            
            if(!this._operateRecordAddListForAutoUse.isEmpty())
            {

                var values:SList<number> =this._operateRecordAddListForAutoUse;
                
                var len:number=this._operateRecordAddListForAutoUse.size();
                
                for(var i:number=0;i<len;i+=2)
                {
                   this.toUseItemId(values.get(i),values.get(i+1),null);
                    
                    // autoUseItems.addValue(values[i],values[i+1]);
                }
                
                this._operateRecordAddListForAutoUse.clear();
            }
            
            var dic:SMap<number,ItemData>=null;
            
            if(!this._operateRecordList.isEmpty())
            {
                var values:SList<number>=this._operateRecordList;
                
                var index:number;
                var num:number;
                
                //单个,并且没有自动使用
                if(this._operateRecordList.size()==2)// && autoUseItems==null
                {
                    index=values.get(0);
                    num=values.get(1);
                    var v:ItemData=this.getItem(index);
                    v.canRelease=false;
                    v.index=index;//标记index
                    
                    // //新增物品
                    // if(v.num==num)
                    // {
                    // 	toSendAddOneItem(index,v,way);
                    // }
                    // else
                    // {
                    // 	toSendAddOneItemNum(index,v.num,way);
                    // }
                    
                    this.onItemAdd(index,v,num);
                }
                else
                {
                    dic=new SMap<number,ItemData>();
                    
                    var v:ItemData;
                    
                    var len:number=this._operateRecordList.size();
                    
                    for(var i:number=0;i<len;i+=2)
                    {
                        index=values[i];
                        num=values[i + 1];
                        v=this.getItem(index);
                        
                        v.canRelease=false;//清除回收标记
                        v.index=index;//标记index
                        
                        dic.put(index,v);
                        
                        this.onItemAdd(index,v,num);
                    }
                    
                    //推送下面
                }
                
                this._operateRecordList.clear();

                this.onChanged();
            }
            
            // if(autoUseItems!=null || dic!=null)
            // {
            // 	toSendAddItem(autoUseItems,dic,way);
            // }
        }
        
        /** 结算移除结果 */
        protected flushRemove(way:number):void
        {
            if(this._operateRecordList.isEmpty())
                return;
            
            var values:SList<number>=this._operateRecordList;
            
            var index:number;
            var num:number;
            var data:ItemData;
            var oldData:ItemData;
            
            //单个
            if(this._operateRecordList.size()==2)
            {
                data=this.getItem(index=this._operateRecordList.get(0));
                num=values.get(1);
                
                // toSendRemoveOneItem(index,data!=null ? data.num : 0,way);
                
                oldData=data!=null ? data : this._operateRecordRemoveDic.get(index);
                
                this.onItemRemove(index,oldData,num);
            }
            else
            {
                // IntIntMap dic=new IntIntMap();
                
                var len:number=this._operateRecordList.size();
                
                for(var i:number=0;i<len;i+=2)
                {
                    data=this.getItem(index=values[i]);
                    num=values[i+1];
                    
                    // dic.put(index,data!=null ? data.num : 0);
                    
                    oldData=data!=null ? data : this._operateRecordRemoveDic.get(index);
                    
                    this.onItemRemove(index,oldData,num);
                }
                
                // toSendRemoveItem(dic,way);
            }

            this._operateRecordList.clear();
            
            if(!this._operateRecordRemoveDic.isEmpty())
            {
                var values2:ItemData[];
                var v:ItemData;
                
                for(var i:number=(values2=this._operateRecordRemoveDic.getValues()).length-1;i>=0;--i)
                {
                    if((v=values2[i])!=null)
                    {
                        if(v.canRelease)
                        {
                            this.releaseItem(v);
                        }
                    }
                }
                
                this._operateRecordRemoveDic.clear();
            }

            this.onChanged();
        }
        
        /** 添加操作回滚 */
        protected rollBackAdd():void
        {
            this._operateRecordAddListForAutoUse.clear();
            
            if(!this._operateRecordList.isEmpty())
            {
                var values:SList<number>=this._operateRecordList;
                var index:number;
                var num:number;
                
                var data:ItemData;
                
                for(var i:number=this._operateRecordList.size()-2;i>=0;i-=2)
                {
                    index=values.get(i);
                    num=values.get(i+1);
                    
                    data=this.getItem(index);
                    
                    //移除
                    if((data.num-=num)<=0)
                    {
                        this.doRemoveItemCompletely(index,num,data);
                        
                        //可回收
                        if(data.canRelease)
                        {
                            this.releaseItem(data);
                        }
                    }
                    else
                    {
                       this.doRemoveItemPartial(index,num,data.id);
                    }
                }
                
                this._operateRecordList.clear();
            }
        }
        
        /** 移除操作回滚 */
        protected rollBackRemove():void
        {
            if(this._operateRecordList.isEmpty())
                return;
            
            var values:SList<number>=this._operateRecordList;
            var index:number;
            var num:number;
            
            var data:ItemData;
            var removeData:ItemData;
            
            for(var i:number=this._operateRecordList.size()-2;i>=0;i-=2)
            {
                index=values.get(i);
                num=values.get(i+1);
                
                data=this.getItem(index);
                
                //被移除了
                if(data==null)
                {
                    removeData=this._operateRecordRemoveDic.get(index);
                    removeData.num=num;//数目恢复
                    //重新添加
                    this.doAddItemNew(index,num,removeData);
                }
                else
                {
                    this.doAddItemPartial(index,num,data);
                }
            }
            
            this._operateRecordList.clear();
            this._operateRecordRemoveDic.clear();
        }
        
        /** 创建物品 */
        protected createItemByType(type:number):ItemData
        {
            var re:ItemData=GameC.factory.createItemData();
            re.initIdentityByType(type);
            return re;
        }
        
        /** 析构物品 */
        protected releaseItem(data:ItemData):void
        {
        
        }
        
        //c组
        
        /** 判断是否有单个物品位置(核心) */
        abstract doHasItemPlaceC(id:number,num:number,data:ItemData):boolean;
        
        /** 执行添加一个物品(核心) */
        abstract doAddItemC(id:number,num:number,data:ItemData):boolean;
        
        /** 执行移除一个物品(倒序)(核心) */
        abstract doRemoveItemC(id:number,num:number):boolean;
        
        /** 添加道具(新格子)(对应remove的completely) */
        abstract doAddItemNew(index:number,num:number,data:ItemData):void;
        
        /** 添加道具部分 */
        abstract doAddItemPartial(index:number,num:number,data:ItemData):void;
        
        /** 完全移除一个格子(对应add的new) */
        abstract doRemoveItemCompletely(index:number,num:number,data:ItemData):void;
        
        /** 部分移除一个格子 */
        abstract doRemoveItemPartial(index:number,num:number,id:number):void;
        
        //has
        
        /** 是否有物品位置 */
        public hasItemPlace(data:ItemData):boolean
        {
            return this.doHasItemPlaceC(data.id,data.num,data);
        }
        
        /** 是否有物品位置 */
        public hasItemPlaceId(id:number,num:number):boolean
        {
            return this.doHasItemPlaceC(id,num,null);
        }
        
        /** 是否有物品位置 */
        public hasItemPlaceList(list:SList<ItemData> ):boolean
        {
            if(list.size()==1)
            {
                return this.hasItemPlace(list.get(0));
            }
            
            this._tempDic.clear();

            list.forEach(v =>
            {
                 //有必要
                if(ItemConfig.get(v.id).totalPlusMax>0)
                {
                    this._tempDic.put(v.id,v.num);
                }
            });
            
            if(!this._tempDic.isEmpty())
            {
                var config:ItemConfig;
                this._tempDic.forEach((k,v) => 
                {
                     config=ItemConfig.get(k);

                    //超出总上限
                     if((this.getItemNum(k)+v)>config.totalPlusMax)
                    {
                         return false;
                    }
                });
            }
            
            return this.hasFreeGrid(list.size());
        }
    
        //add
        
        /** 添加物品数据 */
        public addItemData(data:ItemData,way:number):boolean
        {
            if(!this.doAddItemC(data.id,data.num,data))
            {
                this.rollBackAdd();
                return false;
            }
            
            this.flushAdd(way);
            return true;
        }
        
      
        /** 添加指定id和数目的道具 */
        public addItem(id:number,num:number,way:number):boolean
        {
            if(!this.doAddItemC(id,num,null))
            {
                this.rollBackAdd();
                return false;
            }
            
            this.flushAdd(way);
            return true;
        }
        
        /** 添加一组物品 */
        public addItemsList(list:SList<ItemData>,way:number):boolean
        {

            list.forEach(v =>
            {
                
                if(!this.doAddItemC(v.id,v.data.num,v))
                {
                    this.rollBackAdd();
                    return false;
                }
            });
            
            this.flushAdd(way);
            return true;
        }
        
        /** 添加一组物品 */
        public addItems(list:DIntData[],way:number):boolean
        {
            var data:DIntData;
            
            for(var i:number=0,len=list.length;i<len;i++)
            {
                data=list[i];
                
                if(!this.doAddItemC(data.key,data.value,null))
                {
                    this.rollBackAdd();
                    return false;
                }
            }
            
            this.flushAdd(way);
            return true;
        }
        
        //contains
        
        /** 是否有指定id的物品 */
        public containsItem(id:number):boolean
        {
            return this.getItemNum(id)>0;
        }
        
        /** 是否有指定id数目的物品 */
        public containsItemNum(id:number,num:number):boolean
        {
            return this.getItemNum(id)>=num;
        }
        
         /** 移除道具(会回收一个) */
        public removeItemOne(id:number,way:number):boolean
        {
            return this.removeItem(id,1,way);
        }
        /** 移除道具(会回收) */
        public removeItem(id:number,num:number=1,way:number):boolean
        {
            if(!this.doRemoveItemC(id,num))
            {
                this.rollBackRemove();
                return false;
            }
            
            this.flushRemove(way);
            return true;
        }
        
        /** 移除指定序号的物品(全部数目)(不回收) */
        public removeItemByIndexAll(index:number,way:number):boolean
        {
            var data:ItemData=this.getItem(index);
            
            if(data==null)
                return false;
            
            this.doRemoveItemByIndexC(index,data);
            this.flushRemove(way);
            
            return true;
        }
        
        /** 移除指定序号的物品(部分数目)(不回收) */
        public removeItemByIndex(index:number,num:number,way:number):boolean
        {
            var data:ItemData=this.getItem(index);
            
            if(data==null)
                return false;
            
            if(data.num<num)
                return false;
            
            this.doRemoveItemByIndexCNum(index,num,data);
            this.flushRemove(way);
            
            return true;
        }
        
        //use
        
        /** 通过ID使用物品 */
        abstract useItemByID(id:number,arg:UseItemArgData):boolean;
        
        /** 使用物品 */
        public useItemByIndex(index:number,num:number,arg:UseItemArgData):boolean
        {
            var data:ItemData;
            
            if((data=this.getItem(index))==null)
                return false;
            
            return this.doUseItem(data,index,num,arg);
        }
        
        /** 客户端使用物品 */
        public clientUseItemByIndex(index:number,num:number,itemID:number,arg:UseItemArgData):boolean
        {
            var data:ItemData;
            
            if((data=this.getItem(index))==null)
                return false;
            
            if(data.id!=itemID)
                return false;
            
            return this.doUseItem(data,index,num,arg);
        }
        
        protected doUseItem(data:ItemData,index:number,num:number,arg:UseItemArgData):boolean
        {
            //不是道具不能使用
            if(data.config.type!=ItemType.Tool)
                return false;
            
            if(data.num<num)
                return false;
            
            if(!this.checkCanUseItem(data,num,arg,true))
                return false;
            
            if(CommonSetting.isClientDriveLogic)
            {
                var needRemove:boolean=data.num==num;
                
                this.doRemoveItemByIndexCNum(index,num,data);
                
                this.toUseItem(data,num,arg);
                this.flushRemove(CallWayType.UseItem);
                
                if(needRemove)
                {
                    //回收
                    this.releaseItem(data);
                }
            }
            else
            {
                this.toSendUseItem(data,index,num,arg);
            }
            
            return true;
        }
        
        /** 打印背包 */
        abstract printBag():void;
        
        //接口
        
        /** 单格物品添加 */
        protected onItemAdd(index:number,data:ItemData,num:number):void
        {
            //添加红点
            if(CommonSetting.isClientDriveLogic && data.config.needRedPoint)
            {
                data.hasRedPoint=true;
            }

            //加红点
            if(data.config.needRedPoint && !data.hasRedPoint)
            {
                data.hasRedPoint=true;
                this._redPointCount++;

                this.onRedPointChange();
            }
        }
        
        /** 单格物品减少 */
        protected onItemRemove(index:number,data:ItemData,num:number):void
        {
            //减红点
            if(data.hasRedPoint)
            {
                data.hasRedPoint=false;
                this._redPointCount--;

                this.onRedPointChange();
            }
        }
        
        /** 背包改变 */
        protected onChanged():void
        {
        
        }

        /** 红点被移除 */
        protected onRedPointRemoved(index:number):void
        {

        }

        /** 红点改变 */
        protected onRedPointChange():void
        {

        }

        /** 全刷(格子数增加/整理) */
        protected onRefreshAll():void
        {
            this.onChanged();
        }
        
        //client
        
        /** 更新物品 */
        abstract updateItem(index:number,data:ItemData):void;
        
        /** 执行添加物品(来自服务器) */
        private doAddItemByServer(index:number,data:ItemData,way:number):void
        {
            data.makeConfig();
            
            //先把来自服务器的标记取消,由客户端自己加
            data.hasRedPoint=false;
            
            var oldData:ItemData=this.getItem(index);
            
            var dNum:number=0;
            
            if(oldData==null)
            {
                this.doAddItemNew(index,dNum=data.num,data);
            }
            else
            {
                data.index=index;
                this.updateItem(index,data);
                
                this.doAddItemPartial(index,dNum=data.num - oldData.num,data);
                
                //TODO:oldData回收
            }
            
            this.onItemAdd(index,data,dNum);
            
            if(CallWayConfig.get(way).needAddItemNotice)
            {
                var temp:ItemData=(data.clone() as ItemData);
                temp.num=dNum;
                this._tempAddItems.add(temp);
            }
        }
        
        /** 执行添加物品(来自服务器) */
        private doRemoveItemByServer(index:number,num:number,way:number):void
        {
            var oldData:ItemData=this.getItem(index);
            
            if(oldData==null)
            {
                Ctrl.warnLog("收到移除物品时，找不到物品",this._funcID,index);
                return;
            }
            
            var dNum:number=oldData.num-num;
            
            oldData.num=num;
            
            //移除
            if(oldData.num==0)
            {
                this.doRemoveItemCompletely(index,dNum,oldData);
            }
            else
            {
                this.doRemoveItemPartial(index,dNum,oldData.id);
            }
            
            this.onItemRemove(index,oldData,dNum);
        }
        
        /** 服务器添加物品 */
        public onAddItemByServer(index:number,data:ItemData,way:number):void
        {
            this._tempAddItems.clear();
            
            this.doAddItemByServer(index,data,way);
            
            this.onChanged();
            
            if(CallWayConfig.get(way).needAddItemNotice)
            {
                this.onAddItemNotice(this._tempAddItems,way);
                this._tempAddItems.clear();
            }
        }
        
        /** 服务器添加物品 */
        public onAddItemNumByServer(index:number,num:number,way:number):void
        {
            var data:ItemData=this.getItem(index);
            
            if(data==null)
            {
                Ctrl.warnLog("服务器添加物品数目时,找不到物品",this._funcID,index);
                return;
            }
            
            var dNum:number=num - data.num;
            
            data.num=num;
            
            this.doAddItemPartial(index,dNum,data);
            
            this.onItemAdd(index,data,dNum);
            
            this.onChanged();
            
            if(CallWayConfig.get(way).needAddItemNotice)
            {
                this._tempAddItems.clear();
                
                var temp:ItemData=(data.clone() as ItemData);
                temp.num=dNum;
                this._tempAddItems.add(temp);
                this.onAddItemNotice(this._tempAddItems,way);
                this._tempAddItems.clear();
            }
        }
        
        /** 服务器添加物品 */
        public onAddItemsByServer(autoUseItems:SMap<number,number>,dic:SMap<number,ItemData>,way:number):void
        {
            var need:boolean=CallWayConfig.get(way).needAddItemNotice;
            
            if(need)
            {
                this._tempAddItems.clear();
                
                if(autoUseItems!=null)
                {
                    autoUseItems.forEach((k,v)=>
                    {
                        this._tempAddItems.add(GameC.player.bag.createItem(k,v));
                    });
                }
            }
            
            if(dic!=null)
            {
                dic.forEach((k,v)=>
                {
                    this.doAddItemByServer(k,v,way);
                })
            }
            
            this.onChanged();
            
            if(CallWayConfig.get(way).needAddItemNotice)
            {
                //TODO:合并因为到达单个物品上限的拆分问题
                this.onAddItemNotice(this._tempAddItems,way);
                this._tempAddItems.clear();
            }
        }
        
        public onRemoveItemByServer(index:number,num:number,way:number):void
        {
            this.doRemoveItemByServer(index,num,way);
        }
        
        public onRemoveItemsByServer(dic:SMap<number,number>,way:number):void
        {
            dic.forEach((k,v)=>
            {
                this.doRemoveItemByServer(k,v,way);
            });
        }
        
        public onUseItemResult(itemID:number,num:number,result:boolean):void
        {
            this.doUseItemResult(itemID,num,result);
        }

        /** 移除某序号物品红点 */
        public removeRedPoint(index:number):void
        {
            var data:ItemData=this.getItem(index);

            if(data==null)
            {
                Ctrl.warnLog("不该找不到物品");
                return;
            }

            //减红点
            if(data.hasRedPoint)
            {
                data.hasRedPoint=false;
                this._redPointCount--;

                this.onRedPointRemoved(index);

                this.onRedPointChange();
            }
        }

        /** 获得物品提示 */
        protected onAddItemNotice(list:SList<ItemData>,way:number):void
        {
        
        }
        
        
        //send
        
        protected toSendAddOneItem(index:number,data:ItemData,way:number):void
        {
        
        }
        
        protected toSendAddOneItemNum(index:number,num:number,way:number):void
        {
        
        }
        
        protected toSendAddItem(autoUseItems:SMap<number,number>,dic:SMap<number,ItemData>,way:number):void
        {
        
        }
        
        protected toSendRemoveOneItem(index:number,num:number,way:number):void
        {
        
        }
        
        protected toSendRemoveItem(dic:SMap<number,number>,way:number):void
        {
        
        }
        
        /** 发送使用物品 */
        protected toSendUseItem(data:ItemData,index:number,num:number,arg:UseItemArgData):void
        {
        
        }
        
        /** 执行使用物品 */
        protected toUseItem(data:ItemData,num:number,arg:UseItemArgData):void
        {
        
        }
        
        /** 执行使用物品通过id */
        protected toUseItemId(id:number,num:number,arg:UseItemArgData):void
        {
        
        }
        
        /** 检查是否可使用物品 */
        public checkCanUseItem(data:ItemData,num:number,arg:UseItemArgData,needNotice:boolean):boolean
        {
            return true;
        }
        
        
        /** 服务器返回使用物品结果 */
        protected doUseItemResult(id:number,num:number,result:boolean):void
        {
        
        }

        /** 比较物品 */
        protected compareItem(a1:ItemData,a2:ItemData):number
        {
            if(a2==null)
                return -1;

            if(a1==null)
                return 1;

            if(a2.num==0)
                return -1;

            if(a1.num==0)
                return 1;

            var temp1:number;
            var temp2:number;

            //先比序
            if((temp1=a1.config.sortIndex)<(temp2=a2.config.sortIndex))
                return -1;

            if(temp1>temp2)
                return 1;

            //再比id
            if((temp1=a1.id)<(temp2=a2.id))
                return -1;

            if(temp1>temp2)
                return 1;

            if(a1.isBind!=a2.isBind)
            {
                //不绑定的在前
                return !a1.isBind ? -1 : 1;
            }

            if(a1.disableTime!=a2.disableTime)
            {
                //时间长的在前
                return a1.disableTime>a2.disableTime ? -1 : 1;
            }

            //不可堆叠
            if(!BaseItemContainerTool.canPlusEx(a1,a2))
            {
                return a1.num==a2.num ? 0 : (a1.num>a2.num ? -1 : 1);
            }

            return 0;
        }

        //client

        /** 获取显示列表 */
        abstract getShowList():SList<ItemData>;

    }
}