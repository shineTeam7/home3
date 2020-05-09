namespace Shine
{
    /** 数据注册类 */
    export class FuncTool
    {
        /** 功能ID */
        protected _funcID:number;

        private _type:number;

        private _data:FuncToolData;
        
        public isAdded:boolean=false;

        public toolIndex:number=-1;

        constructor(type:number,funcID:number)
        {
            this._type=type;
            this._funcID=funcID;
        }

        /** 插件类型 */
        public getType():number
        {
            return this._type;
        }

        /** 获取功能ID */
        public getFuncID():number
        {
            return this._funcID;
        }

        /** 初始化 */
        public init():void
        {

        }

        /** 析构 */
        public dispose():void
        {

        }

        /** 每秒 */
        public onSecond(delay:number):void
        {

        }

        /** 重载配置 */
        public onReloadConfig():void
        {

        }

        /** 设置数据 */
        public setData(data:FuncToolData):void
        {
            if(data!=null)
            {
                data.funcID=this._funcID;
                this.toSetData(data);
                this.afterReadData();
            }
            else
            {
                // FuncToolData newData=createToolData();
                //
                // if(newData!=null)
                // {
                // 	newData.funcID=_funcID;
                // 	toSetData(newData);
                // 	onNewCreate();
                // 	afterReadData();
                // }
            }
        }

        protected toSetData(data:FuncToolData):void
        {
            this._data=data;
        }

        /** 读数据后 */
        public afterReadData():void
        {

        }
    }
}