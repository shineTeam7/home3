namespace Shine
{
    /** 数据注册类 */
    export interface IPlayerFuncTool
    {
        setMe(player:Player):void;

        /** 从库中读完数据后(做数据的补充解析)(onNewCreate后也会调用一次) */
	    afterReadDataSecond():void;
    }
}