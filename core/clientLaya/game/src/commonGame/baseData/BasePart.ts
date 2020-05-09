namespace Shine
{
	export abstract class BasePart 
	{
	   	/** 数据 */
		private _data:BaseData;
	
		constructor()
		{
		}

	
		/** 设置数据 */
		public setData(data:BaseData):void
		{
			this._data=data;
		}
	
		/** 获得数据 */
		public  getData():BaseData
		{
			return this._data;
		}
	
		/** 构建partData(深拷) */
		public makePartData():BaseData
		{
			this.beforeMakeData();
	
			var data:BaseData=this.createPartData();
	
			data.copy(this._data);
	
			//深拷
			return data;
		}
	
		/** 构造模块数据 */
		protected createPartData():BaseData
		{
			return null;
		}
	
		/** 构造数据前 */
		protected abstract beforeMakeData():void;
	
		/** 构造(new过程) */
		public abstract construct():void;
	
		/** 初始化 */
		public abstract init():void;
	
		/** 析构(回池)*/
		public abstract dispose():void;
	
		/** 读完数据后 */
		public abstract afterReadData():void;
	
		/** 读完数据后 */
		public afterReadDataSecond():void
		{
	
		}

		/** 登录前 */
		public beforeLogin():void
		{
	
		}
	
		/** 每秒调用 */
		public onSecond(delay:number):void
		{
	
		}
	
		/** 每天调用 */
		public onDaily():void
		{
	
		}
	
		/** 配置表更新后 */
		public onReloadConfig():void
		{
	
		}
	}
}