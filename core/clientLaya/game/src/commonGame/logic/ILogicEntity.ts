namespace Shine
{
	/** 逻辑主体 */
	export interface ILogicEntity extends ITimeEntity
	{
		/** 随机一个整形 */
		 randomInt(rangeL:number):number;
	
		/** 判定几率 */
		 randomProb(prob:number,max:number):boolean;
	
		/** 随一整形(start<=value<end) */
		randomRange(start:number,end:number):number;
	
		/** 随一整形(start<=value<=end)(包括结尾) */
		randomRange2(start:number,end:number):number;
	}
}