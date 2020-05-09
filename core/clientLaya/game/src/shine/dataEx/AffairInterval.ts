namespace Shine
{
	/** 间隔重放事务 */
    export class AffairInterval
    {
	    private _func:Func;

		private _time:number;

		private _index:number=-1;

		constructor(func:Func,time:number)
		{
			this._func=func;
			this._time=time;
		}

		/** 当前是否运行中 */
		public isRunning():boolean
		{
			return this._index!=-1;
		}

		/** 开始间隔执行 */
		public start():void
		{
			if(this._index==-1)
			{
				this._index=TimeDriver.instance.setInterval(Func.create0((delay)=>{this.onInterval(delay)}),this._time);
			}
		}

		/** 停止间隔执行 */
		public stop():void
		{
			if(this._index!=-1)
			{
				TimeDriver.instance.clearInterval(this._index);
				this._index=-1;
			}
		}

		/** 开始并执行一次 */
		public startAndExecuteOnce():void
		{
			this.start();
			this.onInterval(0);
		}

		private onInterval(delay:number):void
		{
			this._func.invoke();
		}
    }
}