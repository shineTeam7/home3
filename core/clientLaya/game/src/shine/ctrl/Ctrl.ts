namespace Shine
{
	/** 控制类 */
	export class Ctrl
	{
		private static _fixedTimer:number;

		/** 获取当前系统时间戳 */
		public static getTimer():number
		{
			return Laya.timer.currTimer;
		}

		public static getFixedTimer():number
		{
			return Laya.timer.currTimer;
		}

		public static makeFixDirty():void
		{
			this._fixedTimer=this.getTimer();
		}

	    /** 输出 */
	    public static print(...args:any[]):void
	    {
	        console.log(StringUtils.objectsToString(args));
	    }

		/** 输出 */
	    public static log(...args:any[]):void
	    {
	        console.log(StringUtils.objectsToString(args));
	    }
	
	    /** 输出 */
	    public static throwError(...args:any[]):void
	    {
	        console.log(StringUtils.objectsToString(args));
	    }
	
	    /** 输出 */
	    public static warnLog(...args:any[]):void
	    {
	        console.log(StringUtils.objectsToString(args));
	    } 
	    
	    /** 输出 */
	    public static debugLog(...args:any[]):void
	    {
	        console.log(StringUtils.objectsToString(args));
	    } 
	
	    /** 错误日志 */
	    public static errorLog(...args:any[]):void
	    {
	        console.log(StringUtils.objectsToString(args));
	    } 
	}
}