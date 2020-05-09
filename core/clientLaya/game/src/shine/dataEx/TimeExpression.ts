namespace Shine
{
	export class TimeExpression
	{
		/** 空 */
		private None:number=0;
		/** 秒倒计时 */
		private SecondTimeOut:number=1;
		/** cron表达式 */
		private Cron:number=9;
	
		/** 类型 */
		private _type:number;
		/** 参数 */
		private _arg:number;
		// /** cron表达式 */
		// private _cron:CronExpression;
	
		constructor(str:string)
		{
			if(str==null || str.length==0)
			{
				this._type=this.None;
				return;
			}
	
			var index:number=str.indexOf(":");
	
			if(index!=-1)
			{
				this._type=parseInt(str.slice(0,index));
				this._arg=parseInt(str.slice(index+1,str.length));
			}
			else
			{
				this._type=this.Cron;
	
				try
				{
					// this._cron=new CronExpression(str);
				}
				catch(err)
				{
					Ctrl.throwError("cron表达式错误",err);
				}
			}
		}
	
		/** 是否为空 */
		public isEmpty():boolean
		{
			return this._type==this.None;
		}
	
		/** 获取下个时间(秒)(如为空返回-1L) */
		public getNextTime(from:number=DateControl.getTimeMillis()):number
		{
			switch(this._type)
			{
				case this.None:
				{
					return -1;
				}
				case this.SecondTimeOut:
				{
					return from+this._arg;
				}
				case this.Cron:
				{
					// return TimeUtils.getNextCronTime(this._cron,from);
					return 0;
				}
				default:
				{
					Ctrl.throwError("不支持的时间表达式类型",this._type);
				}
					break;
			}
	
			return from;
		}

		/** 获取上个时间(ms)(如为空返回-1L) */
		public getPrevTime(from:number=DateControl.getTimeMillis()):number
		{
			switch(this._type)
			{
				case this.None:
				{
					return -1;
				}
				case this.SecondTimeOut:
				{
					return from-(this._arg*1000);
				}
				case this.Cron:
				{
					// return TimeUtils.getPrevCronTime(_cron,from);
					return 0;
				}
				default:
				{
					Ctrl.throwError("不支持的时间表达式类型",this._type);
				}
				break;
			}
			
			return from;
		}

		/** 获取下个时间 */
		public static getNextTimeS(str:string ,time:number=DateControl.getTimeMillis()):number
		{
			// return new TimeExpression(str).getNextTime(time);
			return 0;
		}
	}
}