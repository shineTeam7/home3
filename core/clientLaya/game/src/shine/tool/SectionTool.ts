namespace Shine
{
	export class SectionTool<T>
	{
        private _list:SList<SectionObj<T>>=new SList<SectionObj<T>>();
        constructor()
        {
        }
		private _leftFree:boolean;
	    private _keys:number[];
		private _values:T[];
		public put(min:number,max:number,obj:T):void
		{
			var oo:SectionObj<T>=new SectionObj<T>();
			oo.min=min;
			oo.max=max;
			oo.obj=obj;
			this._list.add(oo);
		}
		private compare(obj1:SectionObj<T>,obj2:SectionObj<T>):number
		{
			return MathUtils.intCompare(obj1.min,obj2.min);
		}
		/** 构造 */
		public make():void
		{
			this._list.sort(this.compare);
			
			if(this._list.isEmpty())
			{
				Ctrl.errorLog("SectionTool不能没有数据");
				return;
			}
			
			var len:number=this._list.size();
			
			this._keys=new Array<number>(len+1);

			this._values=new Array<T>(len+1);
			this._list.get(0).min = -1;
			if(this._list.get(0).min==-1)
			{
				this._list.get(0).min=0;
				this._leftFree=true;
			}
			
			if(this._list.getLast().max==-1)
			{
				this._list.getLast().max=Number.MAX_VALUE;
				this._values[len]=this._list.getLast().obj;
			}
			else
			{
				this._values[len]=null;
			}
			
			this._keys[len]=this._list.getLast().max;
			
			var obj:SectionObj<T>;
			
			for(var i:number=0;i<len;i++)
			{
				obj=this._list.get(i);
				
				this._keys[i]=obj.min;
				this._values[i]=obj.obj;
				
				if(i<len-1)
				{
					if(obj.max<this._list.get(i+1).min)
					{
						Ctrl.throwError("配置表错误,max比min小2");
					}
				}
			}
		}
		/** 获取value对应的key */
		public get(key:number):T
		{
			var index:number=this.binarySearch(this._keys,key);
			if(index>=0)
			{
				var last:number=this._keys.length-1;
				if(index==last)
				{
					if(key==this._keys[last])
					{
						return this._values[last-1];
					}
				}
				return this._values[index];
			}
			else
			{
				if(index==-1)
				{
					return this._leftFree ? this._values[0] : null;
				}
				index=-index-2;
				if(index>=this._values.length)
					index=this._values.length-1;
				return this._values[index];
			}
		}
		public binarySearch(a:number[], key:number):number {
			return this.binarySearch0(a, 0, a.length, key);
		}
		private binarySearch0(a:number[], fromIndex:number, toIndex:number,key:number) {
			let low:number = fromIndex;
			let high:number = toIndex - 1;
			while (low <= high)
			{
				let mid:number = (low + high) >>> 1;
				let midVal:number = a[mid];
				if (midVal < key)
				{
					low = mid + 1;
				}
				else if (midVal > key)
				{
					high = mid - 1;
				}
				else
				{
					return mid;
				}
			}
			return -(low + 1);
		}
    }
}