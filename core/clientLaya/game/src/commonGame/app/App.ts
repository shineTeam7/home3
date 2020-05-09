namespace Shine
{
    /** 基础app */
    export class App
    {
        constructor()
        {
            
        }
        //启动
        public start():void
        {
            //构造必需controls
            this.makeControls();

            this.onStart();
        }

        /** 启动 */
        protected makeControls():void
        {

        }

        /** 启动 */
        protected onStart():void
        {

        }
    }
}