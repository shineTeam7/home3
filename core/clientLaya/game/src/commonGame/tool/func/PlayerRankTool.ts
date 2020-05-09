namespace Shine
{
    /** 数据注册类 */
    export class PlayerRankTool extends PlayerFuncTool
    {
            /** 数据 */
        private _rankData:PlayerRankData;

        /** 翻页显示插件 */
        private _pageShowTool:PlayerPageShowTool;

        constructor(funcID:number)
        {
            super(FuncToolType.Rank,funcID)
        }

        /** 获取显示排行 */
        public getRank():number
        {
            return this._rankData.rank;
        }

        /** 获取排行数据 */
        public getRankData():PlayerRankData 
        {
            return this._rankData;
        }

        protected toSetData(data:FuncToolData ):void
        {
            super.toSetData(data);

            var dd:RankSimpleData=(data as RankSimpleData);

            this._rankData=GameC.factory.createPlayerRankData();
            this._rankData.key=this.me.role.getPlayerID();
            this._rankData.showData=this.me.role.getSelfRoleShowData();

            this._rankData.rank=dd.rank;
           this. _rankData.value=dd.value;
        }

        /** 刷新排行 */
        public onRefreshRank(rank:number,value:number):void
        {
            this._rankData.rank=rank;
            this._rankData.value=value;

            this.me.dispatch(GameEventType.FuncRefreshRank,this._funcID);
        }

        /** 重置排行榜 */
        public onResetRank():void
        {
            this._rankData.value=0;
            this._rankData.rank=-1;
            
            this.me.dispatch(GameEventType.FuncRefreshRank,this._funcID);
        }

        /** 绑定翻页显示插件 */
        public bindPageShowTool(eachShowNum:number):void
        {
           this.me.func.registFuncTool(this._pageShowTool=new PlayerPageShowTool(this._funcID,eachShowNum));
        }

        /** 获取翻页显示插件 */
        public getPageShowTool():PlayerPageShowTool 
        {
            return this._pageShowTool;
        }
    }
}