namespace Shine
{
	export class IndexMaker
	{
	    private _min:number;
	
		private _max:number;
	
		private _canRound:boolean;
	
		private _index:number;
	
		constructor(min:number=0,max:number=2147483646,canRound:boolean=false)
		{
			this._min=min;
			this._max=max;
			this._canRound=canRound;
	
			this._index=this._min;
		}
	
		/** 重置 */
		public reset():void
		{
			this._index=this._min;
		}
	
		/** 取一个序号 */
		public get():number
		{
			var re:number=++this._index;
	
			if(re >= this._max)
			{
				if(this._canRound)
				{
					this._index=this._min;
					re=++this._index;
				}
				else
				{
					Ctrl.throwError("序号构造溢出");
				}
			}
	
			return re;
		}
	}
}