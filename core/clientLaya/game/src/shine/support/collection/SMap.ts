namespace Shine
{
	/** 集合 */
	export class SMap<K,V>
	{
	    private _keys:K[];
	    private _values:V[];
	
	    private _size:number=0;
	
	    constructor(size?:number)
	    {
	        this._size=0;
	        this._keys=new Array<K>();
	        this._values=new Array<V>();
	    }
	
	    /** 是否包含 */
	    public contains(key:K):boolean
	    {
	        return this._keys.indexOf(key) >= 0;
	    }
	
	    public containsKey(key:K):boolean
	    {
	        return this.contains(key);
	    }
	
	    public containsValue(value:V):boolean
	    {
	        return this._values.indexOf(value) >= 0;
	    }
	
	    public put(key:K,value:V):void
	    {
	        var index=this._keys.indexOf(key);
			if (index >= 0){
				this._values[index]=value;
				return;
	        }
	        
	        this._size++;
			this._keys.push(key);
	        this._values.push(value);
	    }
	
	    public remove(key:K):V
	    {
	        var index=this._keys.indexOf(key);
	        if (index<0)
	            return null;
	
	        var value=this._values[index];
	        this._keys.splice(index,1);
	        this._values.splice(index,1);
	        this._size--;
	        return value;
	    }
	
	    public get(key:K):V
	    {
	        var index=this._keys.indexOf(key);
	        if (index<0)
	            return null;
	        return this._values[index];
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

		/** 添加一组 */
		public putAll(dic:SMap<K,V>):void
		{
			for(var i=0;i<this._size;++i)
	        {
	            this.put(dic._keys[i],dic._values[i]);
	        }
		}
	
	    /** 清空 */
	    public clear():void
	    {
	        if(this.isEmpty())
	            return;
	        this._values.length=0;
	        this._keys.length=0;
	        this._size=0;
	    }
	
	    public clone():SMap<K,V>
	    {
	        if(this.isEmpty())
	            return new SMap<K,V>();
	        
	        var re:SMap<K,V>=new SMap<K,V>();
	        re._size=this._size;
	        for(var i=0;i<this._size;++i)
	        {
	            re._keys[i]=this._keys[i];
	            re._values[i]=this._values[i];
	        }
	
	        return re;
	    }
	
	    /** 没有就赋值(成功添加返回null,否则返回原值) */
	    public putIfAbsent(key:K,value:V):V
	    {
	        var old=this.get(key);
	        if(old != null)
	            return old;
	        
	        this._size++;
			this._keys.push(key);
	        this._values.push(value);
	        return null;
	    }
	
	    /** 没有就赋值,有就返回原值 */
	    public computeIfAbsent(key:K,func:Function):V
	    {
	        var old=this.get(key);
	        if(old != null)
	            return old;
	
	        var value=func(key);
	        this._size++;
	        this._keys.push(key);
	        this._values.push(value);
	        return value;
	    }
	
	    /** 遍历 */
	    public forEach(consumer:(key:K,value:V)=>void):void
	    {
	        for(var i=0;i<this._size;++i)
	        {
	            consumer(this._keys[i],this._values[i]);
	        }
	    }
	
	    /** 遍历，通过返回false来break */
	    public forEachSome(consumer:(key:K,value:V)=>boolean):void
	    {
	        for(var i=0;i<this._size;++i)
	        {
	            if(!consumer(this._keys[i],this._values[i]))
	                break;
	        }
	    }
	
	    // TODO 考虑返回拷贝
	    /** 键组 */
	    public getKeys():K[]
	    {
	        return this._keys;
	    }
	
	    /** 值组 */
	    public getValues():V[]
	    {
	        return this._values;
	    }
	
	    /** 扩容 */
	    public ensureCapacity(capacity: number): void 
	    {
	        
	    }
	
	    /** 遍历 */
	    public forEachValue(consumer:(value:V)=>void):void
	    {
	        for(var i=0;i<this._size;++i)
	        {
	            consumer(this._values[i]);
	        }
	    }   

		public getOrDefault(key:K,defaultValue:V):V
		{
			if(this.isEmpty())
			{
				return defaultValue;
			}
			
			var index:number =	this._keys.indexOf(key);

			if(index>=0)
			{
				return this._values[index];
			}
				
			return defaultValue;
		}

		public getEver():V
		{
			if(this.isEmpty())
			{
				return null;
			}

			return this._values[0];
		}
	}
}