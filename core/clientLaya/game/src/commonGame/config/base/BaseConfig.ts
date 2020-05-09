namespace Shine
{
	/** 基础配置 */
    export class BaseConfig extends BaseData
    {
        constructor()
        {
            super();
        }
        
        protected afterRead(): void
        {
            this.afterReadConfig();
        }

        /** 读完单个配置 */
        protected afterReadConfig():void
        {

        }

        /** 更新内容 */
        public refresh():void
        {
            this.generateRefresh();
        }

        /** 生成的刷新 */
        protected generateRefresh():void
        {

        }
    }
}