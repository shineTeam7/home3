namespace Shine
{
	export class DataMaker extends ArrayDic<Function>
	{
	    /** 偏移 */
	    public offSet: number;
	
	    /** 构造组 */
	    public list: Function[];
	
	    /** 通过id取得Data类型 */
	    public getDataByID(dataID: number): BaseData 
	    {
			var func:Function=this.get(dataID);

	        if (func==null)
	            return null;
	
	        return func();
	    }
	}
}