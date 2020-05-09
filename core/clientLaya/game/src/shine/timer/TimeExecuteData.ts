namespace Shine
{
	/** 时间执行数据 */
	export class TimeExecuteData
	{
	    /** 序号 */
	    public index:number;
	
	    /** 回调 */
	    public func:Func;
	
	    /** 整形参 */
	    public intArg:number;
	
	    /** 当前时间 */
	    public time:number;
	
	    /** 总时间 */
	    public timeMax:number;
	
	    /** 用来决定是interval还是timeOut */
	    public isGoOn:boolean= false;
	
	    /** 在interval的情况下,是否每次只调用一遍 */
	    public isCut:boolean= false;
	
	    /** 是否暂停 */
	    public pause:boolean= false;
	}
}