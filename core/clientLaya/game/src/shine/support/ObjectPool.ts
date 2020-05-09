namespace Shine
{
    /** 对象池 */
    export class ObjectPool<T>
    {
        /** 列表 */
		private _list:SList<T> ;
		/** 最大尺寸 */
		private  _maxSize:number;

		private  _createFunc:Func;

		private  _checkSet:SSet<T>;

		/** 析构函数 */
		private  _releaseFunc:Func;


		constructor(createFunc:Func,size:number=ShineSetting.defaultPoolSize)
		{
			this._maxSize=size;

			this._list=new SList<T>();

			this._createFunc=createFunc;

			if(ShineSetting.openCheck)
			{
				this._checkSet=new SSet<T>();
			}
		}

		/** 设置析构回调 */
		public setReleaseFunc(func:Func):void
		{
			this._releaseFunc=func;
		}

		/** 清空 */
		public clear():void
		{
			if(this._list.isEmpty())
				return;

               this._list.forEach(v => 
               {
                    if(this._releaseFunc!=null)
                    {
                        this._releaseFunc.invoke(v);
                    }
                });

			this._list.clear();

			if(ShineSetting.openCheck)
			{
				this._checkSet.clear();
			}
		}

		/** 取出一个 */
		public  getOne():T
		{
			//有
			if(!this._list.isEmpty())
			{
				var obj:T =this._list.pop();

				if(ShineSetting.openCheck)
				{
					this._checkSet.remove(obj);
				}

				return obj;
			}
			else
			{
				return this._createFunc.invoke();
			}
		}

        /** 放回一个 */
		public back(obj:T):void
		{
			if(obj==null)
			{
				Ctrl.throwError("对象池添加空对象");
				return;
			}

			//池化对象
			// if(obj instanceof IPoolObject)
			// {
			// 	//(obj as IPoolObject).clear();
			// }

			if(!ShineSetting.useObjectPool || this._list.size() >= this._maxSize)
			{
				if(this._releaseFunc!=null)
				{
					this._releaseFunc.invoke(obj);
				}

				return;
			}

			if(ShineSetting.openCheck)
			{
				if(this._checkSet.contains(obj))
				{
					Ctrl.throwError("对象池重复添加!",obj);
					return;
				}

				this._checkSet.add(obj);
			}

			this._list.add(obj);
		}
    }
}