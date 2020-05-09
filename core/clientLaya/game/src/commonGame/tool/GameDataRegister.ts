namespace Shine
{
    /** 数据注册类 */
    export class GameDataRegister extends DataRegister
    {
        constructor()
        {
            super();
        }

        public regist():void
        {
            super.regist();

            this.add(new BaseDataMaker());

            this.add(new PlayerPartDataMaker());
            this.add(new PlayerListDataMaker());
        }
    }
}