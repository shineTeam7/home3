namespace Shine
{
	export class DateControl
	{
	    /** 偏移时间(ms) */
	    private static _offTime: number = 0;
	
	    /** 缓存当前毫秒数 */
	    private static _timeMillis: number;
	    /** 缓存当前秒数 */
	    private static _timeSeconds: number;
	
	    /** 下个0点时间 */
	    private static _nextDailyTime: number;
	
	    /** 初始化 */
	    public static init(): void 
	    {

			//计算时区
			// try
			// {
			// 	TimeZoneInfo tzf=TimeZoneInfo.FindSystemTimeZoneById(ShineSetting.timeZone);

			// 	zoneOffset = (int) tzf.BaseUtcOffset.TotalMilliseconds;
			// }
			// catch (Exception e)
			// {
			// 	Ctrl.errorLog("初始化时区出错",e.Message);
			// }

	        var time: number = this.getCurrentTimeMillis();
	
	        this._timeMillis = time;
	        this._timeSeconds = time / 1000;
	
	        this._nextDailyTime = TimeUtils.getNextDailyTime(this._timeMillis);
	    }
	
	    /** 设置当前时间(毫秒)(来自服务器) */
	    public static setCurrentTime(time: number): void
	     {
	        this.setOffTime(time - this.getNativeTimeMillis());
	    }
	
	    /** 设置偏移时间(毫秒) */
	    public static setOffTime(offTime: number): void 
	    {
	        this._offTime = offTime;
	
	        this.makeFixDirty();
	    }
	
	    public static getOffTime(): number 
	    {
	        return this._offTime;
	    }
	
	    /** 使缓存失效 */
	    public static makeFixDirty(): void
	    {
	        var time: number = this._timeSeconds = (this._timeMillis = this.getCurrentTimeMillis()) / 1000;
	
	        if (time >= this._nextDailyTime)
	         {
	           this._nextDailyTime = TimeUtils.getNextDailyTime(time);
	        }
	    }
	
	    /** 获取原生本地时间 */
	    private static getNativeTimeMillis(): number 
	    {
	        return Date.now();
	    }
	
	    /** 获取当前时间的毫秒时间 */
	    public static getCurrentTimeMillis(): number
	    {
	        return this.getNativeTimeMillis() + this._offTime;
	    }
	
	    /** 获取毫秒时间(缓存的) */
	    public static getTimeMillis(): number 
	    {
	        return this._timeMillis;
	    }
	
	    /** 获取秒时间(缓存的) */
	    public static getTimeSeconds(): number 
	    {
	        return this._timeSeconds;
	    }
	
	    /** 获取当前日期 */
	    public static getNow(): DateData 
	    {
	        var dt:Date = new Date(Date.now());
	        var data: DateData = new DateData();
	        data.initByDateTimeDate(dt);
	
	        return data;
	    }
	}
}