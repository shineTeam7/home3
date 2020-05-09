namespace Shine
{
    /** 数据注册类 */
    export class ItemDicContainerTool extends BaseItemContainerTool
    {
        /** 数据 */
        private _toolData:ItemDicContainerData;
        /** 物品组(key:index) */
        protected _dic:SMap<number,ItemData>;
        /** 物品组(key:itemID,key2:index) */
        protected _dicByID:SMap<number,SMap<number,ItemData>>=new SMap<number,SMap<number,ItemData>>();

        /** 物品数目组 */
        private _itemNums:SMap<number,number>=new SMap<number,number>();

        /** 物品显示列表 */
        private _showList:SList<ItemData>=new SList<ItemData>();
        /** 显示列表是否变更 */
        private _showListDirty:boolean=true;

        /** 临时物品移除组 */
	    private _tempRemoveItemList:SList<ItemData>=new SList<ItemData>();

        constructor(funcID:number)
        {
            super(FuncToolType.ItemDicContainer,funcID);
        }

        protected toSetData(data:FuncToolData):void
        {
            super.toSetData(data);
            this._toolData=(data as ItemDicContainerData);
        }

        public afterReadData():void
        {
            super.afterReadData();

            this._dic=this._toolData.items;

            this.reMakeData();
        }

        public forEachItem(consumer:Func):void
        {

            this._dic.forEach(v => 
            {
                 consumer.invoke(v);
            });
        }

        /** 重新构造辅助数据 */
        private reMakeData():void
        {
            this._itemNums.clear();
            this._dicByID.clear();

            this._dic.forEach((k,v) => 
            {
                //绑定index
                v.index=k;
                v.reloadConfig();

                this.getItemDicByIDAbs(v.id).put(v.index,v);
                var value:number=this._itemNums.contains(v.id)?this._itemNums.get(v.id):0;   
                this._itemNums.put(v.id,v.num+value);
                
            });

        }

        // protected override FuncToolData createToolData()
        // {
        // 	return new ItemDicContainerData();
        // }

        // public override void onNewCreate()
        // {
        // 	base.onNewCreate();
        //
        // 	_toolData.items=new IntObjectMap<ItemData>(ItemData[]::new);
        // }

        public onSecond(delay:number):void
        {
            super.onSecond(delay);

            if(!this._itemTimeSet.isEmpty())
            {
                var now:number=DateControl.getTimeMillis();

                var has:boolean=false;

                var data:ItemData;

                var dic:SMap<number,ItemData> ;

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

        /** 获取物品组(id为key)(没有就创建) */
        private getItemDicByIDAbs(id:number):SMap<number,ItemData> 
        {
            var tDic:SMap<number,ItemData> =this._dicByID.get(id);

            if(tDic==null)
            {
                this._dicByID.put(id,tDic=new SMap<number,ItemData>());
            }

            return tDic;
        }

        //方法组

        /** 添加物品到指定空位置(正序)(以及空闲格子计算) */
        private addItemToPos(index:number,data:ItemData):void
        {
            //先扩容再检测
            if(ShineSetting.openCheck)
            {
                if(this._dic.get(index)!=null)
                {
                    Ctrl.throwError("不该已有物品");
                }
            }

            data.index=index;//标记index
            this._dic.put(index,data);

            this.getItemDicByIDAbs(data.id).put(index,data);

            this._showListDirty=true;
        }

        /** 移除指定序号的物品(倒序)(以及空闲格子计算) */
        private removeItemFromPos(index:number,data:ItemData):void
        {
            this. _dic.remove(index);
            this.getItemDicByIDAbs(data.id).remove(index);

            this._showListDirty=true;
        }

        /** 获取物品 */
        public getItem(index:number):ItemData
        {
            return this._dic.get(index);
        }

        /** 获取某ID的第一个物品 */
        public getItemByID(id:number):ItemData
        {
            var dic:SMap<number,ItemData>=this._dicByID.get(id);

            if(dic==null)
                return null;

            if(!dic.isEmpty())
            {
                var values:ItemData[];
                var v:ItemData;

                for(var i:number=(values=dic.getValues()).length-1;i>=0;--i)
                {
                    if((v=values[i])!=null)
                    {
                        return v;
                    }
                }
            }

            return null;
        }
        
        private justGetNewIndex():number
        {
            var index:number=++this._toolData.clientItemIndex;

            if(index>=ShineSetting.indexMax)
                index=ShineSetting.indexMaxHalf+1;

            return index;
        }

        /** 获取一个新物品序号 */
        private getNewIndex():number
        {
            var index:number;

            while(this._dic.contains(index=this.justGetNewIndex()));

            return index;
        }

        /** 判断是否有单个物品位置(核心) */
        public doHasItemPlaceC(id:number,num:number,data:ItemData):boolean
        {
            var config:ItemConfig;

            if(data!=null)
            {
                data.makeConfig();
                config=data.config;
            }
            else
            {
                config=ItemConfig.get(id);
            }

            //自动使用道具
            if(config.type==ItemType.Tool && config.passEnterBag)
                return true;

            var totalNum:number=this._itemNums.get(id);

            //超出总上限
            if(config.totalPlusMax>0 && (totalNum+num)>config.totalPlusMax)
                return false;

            return true;
        }

        /** 执行添加一个物品(核心) */
        public doAddItemC(id:number,num:number,data:ItemData):boolean
        {
            var config:ItemConfig;

            if(data!=null)
            {
                data.makeConfig();
                config=data.config;
            }
            else
            {
                config=ItemConfig.get(id);
            }

            if(config.type==ItemType.Tool && config.passEnterBag)
            {
                this._operateRecordAddListForAutoUse.add(id);
                this._operateRecordAddListForAutoUse.add(num);
                return true;
            }

            var totalNum:number=this._itemNums.get(id);

            //超出总上限
            if(config.totalPlusMax>0 && (totalNum+num)>config.totalPlusMax)
                return false;

            //是否单个叠加
            var isSingleOne:boolean=config.singlePlusMax==1;
            //是否绑定
            var isBind:boolean=config.bindByGet || (data!=null && data.isBind);
            //失效时间
            var disableTime:number=data!=null ? data.disableTime : config.enableTimeT.getNextTime();

            var itemPos:number;
            var index:number=-1;
            var tempIndex:number;
            var tData:ItemData;
            var dNum:number;

            var itemDic: SMap<number,ItemData>=this.getItemDicByIDAbs(id);

            //非单个
            if(!isSingleOne)
            {
                var values:ItemData[];
                var v:ItemData;

                for(var i:number=(values=itemDic.getValues()).length-1;i>=0;--i)
                {
                    if((v=values[i])!=null)
                    {
                        //满足叠加规则
                        if(!v.isSingleNumMax() && v.isBind==isBind && v.disableTime==disableTime && (data==null || BaseItemContainerTool.canPlusEx(v,data)))
                        {
                            //装的下
                            if(config.singlePlusMax<=0 || (dNum=(config.singlePlusMax-v.num))>=num)
                            {
                                dNum=num;
                                num=0;
                            }
                            else
                            {
                                num-=dNum;
                            }

                            v.num+=dNum;//计数加

                            //序号
                            index=v.index;

                            //操作记录
                            this._operateRecordList.add(index);
                            this._operateRecordList.add(dNum);

                            this.doAddItemPartial(index,dNum,v);

                            //完了
                            if(num==0)
                            {
                                return true;
                            }
                        }
                    }
                }
            }

            if(num>0)
            {
                while(true)
                {
                    //单包上限都超的
                    if(config.singlePlusMax>0 && num>config.singlePlusMax)
                    {
                        dNum=config.singlePlusMax;
                        num-=dNum;
                    }
                    else
                    {
                        dNum=num;
                        num=0;
                    }

                    //最后的一次加,或者新添加,并且单个叠加的才可直接使用原数据
                    if(data!=null && num==0 && isSingleOne)
                    {
                        tData=data;
                        tData.num=dNum;//数目再赋值
                    }
                    else
                    {
                        tData=this.createItemByType(config.type);
                        tData.id=id;
                        tData.num=dNum;
                        tData.config=config;
                        tData.canRelease=true;//标记可回收
                        //额外初始化
                        GameC.logic.initItem(tData,id);
                    }

                    //获得新序号
                    tData.index=this.getNewIndex();

                    //赋值
                    tData.isBind=isBind;
                    tData.disableTime=disableTime;

                    this._operateRecordList.add(index);
                    this._operateRecordList.add(dNum);
                    this.doAddItemNew(index,dNum,tData);

                    //完了
                    if(num==0)
                    {
                        return true;
                    }
                }
            }

            return true;
        }

        /** 添加道具(新格子)(对应remove的completely) */
        public doAddItemNew(index:number,num:number,data:ItemData):void
        {
            this.addItemToPos(index,data);
            
            var value:number=this._itemNums.contains(data.id)?this._itemNums.get(data.id):0;   
            this._itemNums.put(data.id,num+value);

            if(!data.config.enableTimeT.isEmpty())
            {
                this._itemTimeSet.add(index);
            }
        }

        public doAddItemPartial(index:number,num:number,data:ItemData):void
        {
            var value:number=this._itemNums.contains(data.id)?this._itemNums.get(data.id):0;
            this._itemNums.put(data.id,num+value);
        }

        /** 执行移除一个物品(倒序)(核心) */
        public doRemoveItemC(id:number,num:number):boolean
        {
            var totalNum:number=this._itemNums.get(id);

            if(totalNum<num)
                return false;

            var iDic:SMap<number,ItemData>=this._dicByID.get(id);

            if(iDic==null)
            {
                Ctrl.errorLog("此处不该为空");
                return false;
            }

            //只有一个
            if(iDic.size()==1)
            {
                var data:ItemData=iDic.getEver();
                
                if((num=this.doRemoveItemOneC(data,num))==0)
                {
                    return true;
                }
            }
            else
            {
                this._tempRemoveItemList.clear();

                var self:ItemDicContainerTool=this;
                
                iDic.forEachValue(v=>
                {
                    self._tempRemoveItemList.add(v);
                });

                //排序
                this._tempRemoveItemList.sort(this.compareItemForRemove);
                
                var v1:ItemData;
                
                for(var i1:number=0,len1=this._tempRemoveItemList.size();i1<len1;++i1)
                {
                    v1=this._tempRemoveItemList.get(i1);
                    
                    num=this.doRemoveItemOneC(v1,num);
                    
                    if(num==0)
                    {
                        this._tempRemoveItemList.clear();
                        return true;
                    }
                }
                
                this._tempRemoveItemList.clear();
            }
            
            Ctrl.errorLog("物品不该没移除完");

            return false;
        }

        private doRemoveItemOneC(data:ItemData,num:number):number
        {
            var dNum:number;
            
            //还有剩余
            if(data.num>num)
            {
                dNum=num;
                data.num-=num;
                num=0;
                
                this._operateRecordList.add(data.index);
                this._operateRecordList.add(dNum);
                
                this.doRemoveItemPartial(data.index,dNum,data.id);
            }
            else
            {
                dNum=data.num;
                num-=data.num;
                //data.num=0;
                
                this._operateRecordList.add(data.index);
                this._operateRecordList.add(dNum);
                this._operateRecordRemoveDic.put(data.index,data);
                data.canRelease=true;//回收标记
                
                this.doRemoveItemCompletely(data.index,dNum,data);
            }
            
            return num;
        }

        public doRemoveItemByIndexCNum(index:number,num:number,data:ItemData):void
        {
            //完全移除
            if(num==data.num)
            {
                this._operateRecordList.add(index);
                this._operateRecordList.add(num);
                this._operateRecordRemoveDic.put(index,data);
                this.doRemoveItemCompletely(index,num,data);
            }
            //部分移除
            else
            {
                data.num-=num;

                this._operateRecordList.add(index);
                this._operateRecordList.add(num);
                var value:number=this._itemNums.contains(data.id)?this._itemNums.get(data.id)-num:0;   
                this._itemNums.put(data.id,value);
            }
        }

        public doRemoveItemPartial(index:number,num:number,id:number):void
        {
            var value:number=this._itemNums.contains(id)?this._itemNums.get(id)-num:0;   

            this._itemNums.put(id,value);
        }

        /** 完全移除一个格子(对应add的new) */
        public doRemoveItemCompletely(index:number,num:number,data:ItemData):void
        {
            
            var value:number=this._itemNums.contains(data.id)?this._itemNums.get(data.id)-num:0;
            this._itemNums.put(data.id,value);

            this.removeItemFromPos(index,data);

            //失效时间
            if(!data.config.enableTimeT.isEmpty())
            {
                this._itemTimeSet.remove(index);
            }
        }

        public isEmpty():boolean
        {
            return this._dic.isEmpty();
        }

        //接口组

        /** 获取某道具的总数目 */
        public getItemNum(itemID:number):number
        {
            if(this._itemNums.get(itemID))
            {
                return this._itemNums.get(itemID);
            }
            return 0;
        }

        public hasFreeGrid(num:number):boolean
        {
            return true;
        }

        /** 通过ID使用物品 */
        public useItemByID(id:number,arg:UseItemArgData):boolean
        {
            if(this.getItemNum(id)==0)
                return false;

           var dic:SMap<number,ItemData>=this._dicByID.get(id);

            var values:ItemData[];
            var v:ItemData;

            for(var i:number=(values=dic.getValues()).length-1;i>=0;--i)
            {
                if((v=values[i])!=null)
                {
                    return this.doUseItem(v,v.index,1,arg);
                }
            }

            return false;
        }

        /** 打印背包 */
        public printBag():void
        {
            if(this.isEmpty())
            {
                Ctrl.log("bag is empty");
                return;
            }

            Ctrl.log("暂未支持");
            //ObjectUtils.printDataList(_dic);
        }

        public updateItem(index:number,data:ItemData):void
        {
            var oldData:ItemData=this._dic.get(index);

            this._dic.put(index,data);

            if(oldData!=null && oldData.id!=data.id)
            {
                this.getItemDicByIDAbs(oldData.id).remove(index);
            }
            
            this.getItemDicByIDAbs(data.id).put(index,data);

            this._showListDirty=true;
        }

        /** 获取显示列表 */
        public getShowList():SList<ItemData> 
        {
            if(this._showListDirty)
            {
                this._showListDirty=false;
                this.toCountShowList();
            }

            return this._showList;
        }

        public toCountShowList():void
        {
            this._showList.clear();

            this._dic.forEachValue(v => 
            {
                this._showList.add(v);
            });

            this._showList.sort(this.compareItem);
        }

        /** 比较物品,整理1用 */
        protected compareItemForRemove(a1:ItemData,a2:ItemData):number
        {
            return MathUtils.intCompare(a1.index,a2.index);
        }
    }
}