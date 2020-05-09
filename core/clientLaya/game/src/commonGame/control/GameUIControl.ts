namespace Shine
{
    /** UI控制 */
    export class GameUIControl
    {
        constructor()
        {
            
        }

        /** alert确认弹框 */
        public alert(msg:string ,sureCall:Func)
        {
            TimeDriver.instance.setTimeOut(sureCall,2000);
        }

        /** 执行文字显示 */
        public showText(str:string,type:number):void
        {
            //TODO:实现
            switch(type)
            {
                case TextShowType.Notice:
                {
                    // notice(str);
                    Ctrl.print(str);
                }
                    break;
                case TextShowType.Alert:
                {
                    // alert(str,null);
                    Ctrl.print(str);
                }
                    break;
            }
        }

        /** 显示网络延迟圈圈(isShow为false则为隐藏) */
        public showNetDelay(isShow:boolean):void
        {
            Ctrl.print("显示网络圈圈",isShow);
        }

        /** 标准提示(屏幕居中提示) */
        public notice(msg:string):void
        {

        }
    }
}