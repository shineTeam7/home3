namespace Shine
{
export class TimeUtils 
{
	/** 一天的毫秒数 */
	private static _dayTime:number=60 * 60 * 24*1000;
	
	// /** 1970年1月1日8点 */
	// private static _zeroDate:Date=new Date(0);
	
	/** 创建cron表达式 */
	// public static createCronExpression(cron:String):Cron.CronTime
	// {
	// 	var ex:Cron.CronTime=null;
		
	// 	try
	// 	{
	// 		ex=Cron.time(cron,null);
	// 	}
	// 	catch(err)
	// 	{
	// 		Ctrl.errorLog(err);
	// 	}
		
	// 	return ex;
	// }
	
	// /** 获取下一个cron时刻(ms)(出错或过去时间返回-1) */
	// public static getNextCronTime(cron:Cron.CronTime, time:number):number
	// {
	// 	var date:Date=cron.getNextValidTimeAfter(new Date(time));
		
	// 	//出错
	// 	if(date==null)
	// 	{
	// 		return -1;//
	// 	}
		
	// 	return date.getTime();
	// }
	
		// /** 获取下一个cron时刻(ms)出错或过去时间返回-1 */
		// public static getNextCronTime(cron:CronExpression,time:number):number
		// {
		// 	var re:number;

		// 	try
		// 	{
		// 		var date:DateTimeOffset=cron.GetNextValidTimeAfter(new DateTimeOffset((time+startTime)*timeScale,TimeSpan.Zero));
		// 		re=(date.Ticks/timeScale-startTime);
		// 	}
		// 	catch(Exception e)
		// 	{
		// 		re=-1L;
		// 	}

		// 	return re;
		// }

	// /** 获取下一个cron时刻(ms) */
	// public static getNextCronTimeFromNow(cron:CronExpression):number
	// {
	// 	return this.getNextCronTime(cron,DateControl.getTimeMillis());
	// }
	
	// /** 获取下一个cron时刻,从当前开始(ms) */
	// public static getNextCronTimeFromNowStr(cron:String):number
	// {
	// 	return this.getNextCronTime(this.createCronExpression(cron),DateControl.getTimeMillis());
	// }
	
	// /** 获取下一个cron时刻 */
	// public static  getNextCronDate(cron:string,data:DateData ):DateData
	// {
	// 	var date:DateData=new DateData();
		
	// 	date.initByTimeMillis(this.getNextCronTime(cron,data.getTimeMillis()));
		
	// 	return date;
	// }
	
	/** 获取下一个日期(ms)(每天0点) */
	public static  getNextDailyTime( now:number):number
	{
		return (now  /this._dayTime + 1) * this._dayTime;
	}
	
	/** 获取明天0点(秒) */
	public static getNextDailyTimeFromNow():number
	{
		return this.getNextDailyTime(DateControl.getTimeMillis());
	}
	
	// //显示部分

	
	
	// /** 获取是否为同一天 */
	// public static isSameDay(time1:number,time2:number):boolean
	// {
	// 	return this.getDisBetweenDay(time1,time2)==0;
	// }
	
	/** 获取两个日期相差几天 startTime小,endTime大,如果反了，返回负值，使用时自行判定*/
	public static getDisBetweenDay(startTime:number, endTime:number):number
	{
		return Math.floor(((this.getNextDailyTime(endTime)-this.getNextDailyTime(startTime))/this._dayTime));
	}
	
	// /** 小时转毫秒 */
	// public static hourToMillisecond( hour:number):number
	// {
	// 	return hour * 60 * 60 * 1000;
	// }
}
}