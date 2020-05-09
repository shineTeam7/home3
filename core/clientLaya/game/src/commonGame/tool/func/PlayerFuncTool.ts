namespace Shine
{
    /** 数据注册类 */
    export class PlayerFuncTool extends FuncTool implements IPlayerFuncTool
    {
        /** 玩家自身 */
        public me:Player;
        
        constructor(type:number,funcID:number)
        {
            super(type,funcID)
        }

        /** 设置me */
        public setMe(player:Player ):void
        {
            this.me=player;
        }

        public afterReadDataSecond():void
        {

        }
    }
}