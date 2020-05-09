namespace Shine
{
    /** 超时事务(秒/毫秒都可) */
    export class AffairTimeOut
    {
        private _timeMax:number;

		private _time:number;

		private _func:Func;

		constructor(func:Func,time:number=0)
		{
            this._func=func;
            this._time=0;

            if(time==0)
            {
                time=ShineSetting.affairDefaultExecuteTime;
            }
            
            this._timeMax=time;
		}

		/** 开始 */
		public start():void
		{
			this._time=this._timeMax;
		}

		/** 停止 */
		public stop():void
		{
			this._time=0;
		}

		public isRunning():boolean
		{
			return this._time>0;
		}

		public onDelay(delay:number):void
		{
			if(this._time==0)
				return;

			if((this._time-=delay)<=0)
			{
				this._time=0;

				if(this._func!=null)
					this._func.invoke();
			}
		}

		public onSecond():void
		{
			this.onDelay(1);
		}
    }
}