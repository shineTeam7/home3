namespace Shine
{
    /** 数据注册类 */
    export class DataRegister
    {
        constructor()
        {
            
        }

        protected add(maker:DataMaker):void
        {
            BytesControl.addMaker(maker);
        }

        public regist():void
        {
            
        }
    }
}