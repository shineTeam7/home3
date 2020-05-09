namespace Shine
{
    /** 数据注册类 */
    export class PlayerPageShowTool extends PlayerFuncTool
    {
        /** 每页显示数目 */
        private _eachPageShowMax:number;
        
        /** 数据 */
        private _list:SList<KeyData> = new SList<KeyData>();
        /** 锁字典 */
        private _lockDic:SMap<number,number>=new SMap<number,number>();
        /** 需要提送标记 */
        private _needSendSet:SSet<number>=new SSet<number>();

        private _currentPage:number=-1;

        constructor(funcID:number,eachPageShowMax:number)
        {
            super(FuncToolType.PageShow,funcID)

            this._eachPageShowMax=eachPageShowMax;
        }

        /** 接受数据 */
        public onReceivePage(page:number,dic:SMap<number,KeyData>,changePageSet:SSet<number>):void
        {
            if(changePageSet!=null && !changePageSet.isEmpty())
            {
                changePageSet.forEach(k=>
                {
                    //清CD
                    this._lockDic.put(k,0);
                });
            }

           var list:SList<KeyData>=this._list;

            if(dic!=null)
            {
                dic.forEach((k,v)=>
                {
                    this.ensureSize(k);
                    list[k]=v;
                });

                //有值才刷新
                this.onRefreshPage(page);
            }
        }

        /** 确保index能放下 */
        private ensureSize(index:number):void
        {
            var list:SList<KeyData>;

            if((list=this._list).size()>index)
                return;

            for(var i=index-list.size();i>=0;--i)
            {
                list.add(null);
            }
        }

        /** 关闭当前页 */
        public closePage():void
        {
            this.setCurrentPage(-1);
        }

        /** 选择当前观察页(用完了传-1) */
        public setCurrentPage(page:number):void
        {
            if(this._currentPage==page)
                return;

           this._currentPage=page;

            if(page!=-1)
            {
                this.getOnePage(page);
            }
        }

        /** 获取某页数据(调用) */
        private getOnePage(page:number):void
        {
            /** 未到时间 */
            if(this._lockDic.get(page)>0)
            {
                this._needSendSet.add(page);
            }
            else
            {
                this._lockDic.put(page,Global.pageToolShowCD);
                this.sendGet(page);
            }
        }

        /** 每秒调用 */
        public onSecond(delay:number):void
        {
            var needSendSet: SSet<number>=this._needSendSet;
            var dic:SMap<number,number>=this._lockDic;


            dic.forEach((k,v) => 
            {
                this._lockDic.put(k,--v);

                if(v<=0)
                {
                    if(needSendSet.contains(k))
                    {
                        needSendSet.remove(k);

                        //是当前页才发送
                        if(this._currentPage==k)
                        {
                            this._lockDic.put(k,Global.pageToolShowCD);
                            this.sendGet(k);
                        }
                    }
                }
            });
        }

        /** 推送获取 */
        private sendGet(page:number):void
        {
            FuncGetPageShowRequest.createFuncGetPageShow(this._funcID,page).send();
        }

        /** 刷新页码 */
        private onRefreshPage(page:number):void
        {
            var arr:number[]=[this._funcID,page]

            this.me.dispatch(GameEventType.FuncRefreshPageShow,arr);
        }

        /** 获取当前数据 */
        public getList():SList<KeyData>
        {
            return this._list;
        }

        /** 获取每页显示上限 */
        public getEachPageShowMax():number
        {
            return this._eachPageShowMax;
        }

        /** 清空数据 */
        public clear():void
        {
            this._list.clear();
            this. _lockDic.clear();
            this._needSendSet.clear();

            if(this._currentPage!=-1)
            {
                this.getOnePage(this._currentPage);
            }
        }

    }
}