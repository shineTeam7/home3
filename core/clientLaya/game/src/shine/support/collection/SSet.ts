namespace Shine
{
	/** Set */
	export class SSet<K>
	{
	    private _size:number=0;
	    private _keys:K[];
		
		constructor(size?:number)
	    {
	        this._size=0;
	        this._keys=new Array<K>();
	    }

	    /** 是否包含 */
	    public contains(key:K):boolean
	    {
	        return this._keys.indexOf(key)>=0;
	    }
	
	    public add(key:K):boolean
	    {
	        if(this.contains(key))
	            return false;
	        
	        this._keys.push(key);
	        this._size++;
	        return true;
	    }
	
	    public remove(key:K):boolean
	    {
	        var index=this._keys.indexOf(key);
	        if(index<0)
	            return false;
	        this._keys.splice(index,1);
	        this._size--;
	        return true;
	    }
	
	    public size():number
	    {
	        return this._size;
	    }
	
	    public get length():number
	    {
	        return this._size;
	    }
	
	    public isEmpty():boolean
	    {
	        return this._size==0;
	    }
	
	    /** 清空 */
	    public clear():void
	    {
	        if(this.isEmpty())
	            return;
	
	        this._keys.length=0;
	        this._size=0;
	    }
	
	    public clone():SSet<K>
	    {
	        if(this.isEmpty())
	            return new SSet<K>();
	        
	        var re:SSet<K>=new SSet<K>();
	        re._size=this._size;
	        for(var i=0;i<this._size;++i)
	        {
	            re._keys[i]=this._keys[i];
	        }
	        return re;
	    }
	
	    /** 遍历 */
	    public forEach(consumer:(key:K)=>void):void
	    {
	        if(this.isEmpty())
	            return;
	
	        for(var i=0;i<this._size;++i)
	        {
	            consumer(this._keys[i]);
	        }
	    }
	    
	    // TODO 考虑返回拷贝
	    public getKeys():K[]
	    {
	        return this._keys;
	    }
	
	    /** 扩容 */
	    public ensureCapacity(consumer:number):void
	    {
	        
	    }
	}
}