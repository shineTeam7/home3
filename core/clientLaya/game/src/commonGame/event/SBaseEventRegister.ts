namespace Shine
{
	/** 基础事件注册 */
	export class SBaseEventRegister<T>
	{
		private _indexToTypeDic:SMap<number,number>=new SMap<number,number>();
	
		private _listenerDic:SMap<number,SMap<number,BaseEventAction<T>>> = new SMap<number,SMap<number,BaseEventAction<T>>> ();
		/** 序号 */
		private _index:number=0;
	
		public addListenerFun(type:number,listener:Func):number
		{
			var func:BaseEventAction<T>=new BaseEventAction();
			func.func=listener;
	
			return this.addListener(type,func);
		}
	
		/**添加监听 */
		public addListener(type:number,func:BaseEventAction<T>):number
		{
			var dic:SMap<number,BaseEventAction<T>> = this._listenerDic.get(type);
	
			if(dic==null)
			{
				this._listenerDic.put(type,dic = new SMap<number,BaseEventAction<T>>());
			}
	
			var index:number=++this._index;
	
			dic.put(index,func);
			this._indexToTypeDic.put(index,type);
	
			return index;
		}
	
		/** 移除监听 */
		public removeListener(type:number,index:number):void
		{
			var dic:SMap<number,BaseEventAction<T>> = this._listenerDic.get(type);
	
			if(dic==null)
			{
				return;
			}
	
			dic.remove(index);
			this._indexToTypeDic.remove(index);
		}
	
		/** 移除监听通过序号 */
		public removeListener1(index:number):void
		{
			var type:number=this._indexToTypeDic.get(index);
	
			if(type>0)
			{
				this.removeListener(type,index);
			}
		}
	
		/** 删除所有监听 */
		public removeAllListener():void
		{
			this._listenerDic.clear();
		}
	
		/** 派发消息 */
		public dispatch(type:number,data:T=null):void
		{
			var dic:SMap<number,BaseEventAction<T>> = this._listenerDic.get(type);
	
			if(dic==null)
			{
				return;
			}
			
			// var keys:number[] =dic.getKeys();
			// var values:BaseEventAction<T>[] =dic.getValues();
			// var fv:number=dic.getFreeValue();
			// var k:number;
	
			//**********不确定的**********
			dic.forEach((k,v)=>{
				v.execute(data);
			});

			// for(var i:number=keys.length-1;i>=0;--i)
			// {
			// 	if((k=keys[i])!=fv)
			// 	{
			// 		values[i].execute(data);
	
			// 		if(k!=keys[i])
			// 		{
			// 			++i;
			// 		}
			// 	}
			// }
		}

	}
}