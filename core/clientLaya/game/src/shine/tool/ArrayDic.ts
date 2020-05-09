namespace Shine
{
    /** 数组字典 */
    export class ArrayDic<T>
    {
        /** 偏移 */
	    public offSet: number;
	
	    /** 构造组 */
	    public list: T[];

        /** 获取数据 */
	    public get(key: number): T 
	    {
	        var index: number = key - this.offSet;
	
	        if (index < 0 || index >= this.list.length)
	            return null;
	
	        return this.list[index];
	    }

        public addDic(dic: ArrayDic<T>): void 
	    {
	        if (this.list == null) 
	        {
	            this.list = dic.list;
	            this.offSet = dic.offSet;
	        }
	        else 
	        {
	            var off:number= Math.min(dic.offSet , this.offSet);
	            var nowLen:number= this.offSet + this.list.length;
	            var targetLen:number= dic.offSet + dic.list.length;
	            var max:number= Math.max(targetLen , nowLen);
	
	            var d:number;
	            var len:number;
	
	            //有变化
	            if (!(off == this.offSet && max == nowLen)) 
	            {
	               var temp:T[] = new Array<T>(max - off);
	
	                d = this.offSet - off;
	                len = this.list.length;
	                for (var i:number= 0; i < len;++i)
	                {
	                    temp[i + d] = this.list[i];
	                }
	
	                this.offSet = off;
	                this.list = temp;
	            }
	
	            d = dic.offSet - this.offSet;
	            len = dic.list.length;
	
	            for (var i:number= 0; i < len;++i)
	            {
	                this.list[i + d] = dic.list[i];
	            }
	        }
	    }
    }
}