namespace Shine
{
    /** 数据注册类 */
    export class ItemContainerTool extends BaseItemContainerTool
    {
        constructor(funcID:number)
        {
            super(FuncToolType.ItemContainer,funcID);
        }

        /** 遍历当前物品 */
        public forEachItem(consumer:Func):void
        {

        }
        
        /** 获取物品 */
        public getItem(index:number):ItemData
        {
            return null;
        }
        
        /** 获取某ID的第一个物品 */
        public getItemByID(id:number):ItemData
        {
            return null;
        }
        
        /** 背包是否为空 */
        public isEmpty():boolean
        {
              return false;
        }
        
        /** 获取物品总数 */
        public getItemNum(itemID:number):number
        {
             return -1;
        }
        
        /** 是否有空余格子 */
        public hasFreeGrid(num:number):boolean
        {
             return false;
        }

        
        /** 判断是否有单个物品位置(核心) */
        public doHasItemPlaceC(id:number,num:number,data:ItemData):boolean
        {
            return false;
        }
        
        /** 执行添加一个物品(核心) */
        public doAddItemC(id:number,num:number,data:ItemData):boolean
        {
            return false;
        }
        
        /** 执行移除一个物品(倒序)(核心) */
        public doRemoveItemC(id:number,num:number):boolean
        {
            return false;
        }
        
        /** 添加道具(新格子)(对应remove的completely) */
        public doAddItemNew(index:number,num:number,data:ItemData):void
        {

        }
        
        /** 添加道具部分 */
        public doAddItemPartial(index:number,num:number,data:ItemData):void
        {

        }
        
        /** 完全移除一个格子(对应add的new) */
        public doRemoveItemCompletely(index:number,num:number,data:ItemData):void
        {

        }
        
        /** 部分移除一个格子 */
        public doRemoveItemPartial(index:number,num:number,id:number):void
        {

        }

          /** 移除执行序号物品数目(核心)(不回收) */
        public doRemoveItemByIndexCNum(index:number,num:number,data:ItemData):void
        {

        }

           /** 通过ID使用物品 */
        public useItemByID(id:number,arg:UseItemArgData):boolean
        {
            return false;
        }

        
        /** 打印背包 */
        public printBag():void
        {

        }

        
        /** 获取显示列表 */
        public getShowList():SList<ItemData>
        {
            return null;
        }

        /** 更新物品 */
        public updateItem(index:number,data:ItemData):void
        {

        }

        	/** 服务器整理背包 */
        public onCleanUpByServer(list:SList<ItemData> ):void 
        {
            // this._data.items=this._list=list;

            // _cleanUpDirty=true;

            // reMakeData();

            // onRefreshAll();
        }

        
        public onAddGridNumByServer(gridNum:number):void
        {
            //this.addGridNum(gridNum-_gridNum);
        }

    }
}