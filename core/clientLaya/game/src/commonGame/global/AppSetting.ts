namespace Shine
{
    /** 应用配置 */
    export class AppSetting
    {
        /** 初始化 */
        public static init():void
        {
            if(ShineSetting.isRelease)
                this.initRelease();
            else
                this.initDebug();
        }

        private static initRelease():void
        {
            ShineSetting.needPingCut=true;
        }

        private static initDebug():void
        {
            ShineSetting.needPingCut=false;
            
            //其他配置
        }
    }
}