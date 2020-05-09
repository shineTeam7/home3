namespace Shine
{
    /** 登录步骤数据 */
    export class StepData
    {
	    /** id */
		public  id:number;

		/** 前置ID组 */
		public preIDs:number[];

		/** 方法 */
		public func:Func ;

		/** 状态(0:未开始,1:执行中,2:已完成) */
		public state:number=StepTool.None;
    }
}