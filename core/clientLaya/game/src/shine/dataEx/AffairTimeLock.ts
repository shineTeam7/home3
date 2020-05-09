namespace Shine
{
	/** 事务计时锁(s) */
    export class AffairTimeLock
    {
        private _timeMax:number;

		/** 上次检查时间(s) */
		private _lastCheckTime:number=-1;
		public constructor(time:number=ShineSetting.affairDefaultExecuteTime)
		{
			this._timeMax=time;
			this._lastCheckTime=-1;
		}

		/** 锁定 */
		public lockOn():void
		{
			this._lastCheckTime=DateControl.getTimeSeconds();
		}

		/** 解锁 */
		public unlock():void
		{
			this._lastCheckTime=-1;
		}

		/** 是否锁定中 */
		public isLocking():boolean
		{
			return this._lastCheckTime!=-1 && (DateControl.getTimeSeconds()-this._lastCheckTime)<this._timeMax;
		}
    }
}