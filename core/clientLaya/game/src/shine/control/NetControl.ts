namespace Shine
{
	export class NetControl
	{
	   	/** httpRequest组 */
		private static _httpRequestDic:SSet<BaseHttpRequest>=new SSet<BaseHttpRequest>();
	
		/** 是否析构了 */
		private static _disposed:boolean=false;
	
		/// <summary>
		/// 初始化
		/// </summary>
		public static init(): void
		{
			TimeDriver.instance.setFrame(Func.create(this,this.onFrame));
		}
	
		/// <summary>
		/// 析构
		/// </summary>
		public static dispose(): void
		{
			if (this._disposed)
				return;
	
			this._disposed = true;
	
			this._httpRequestDic.forEach(v =>
			{
				v.dispose();
			});
	
			this._httpRequestDic.clear();
	
		}
	
		/** 检查http消息 */
		private static onFrame(delay: number): void 
		{
			this._httpRequestDic.forEach(v => 
			{
				if(v.isDone()) 
				{
					this._httpRequestDic.remove(v);
				}
				else
				{
					v.timeOut -= delay;
	
					if (v.timeOut <= 0) 
					{
						v.dispose();
						this._httpRequestDic.remove(v);
					}
				}
			});
		}
	
		/// <summary>
		/// 添加httpRequest
		/// </summary>
		public static addHttpRequest(request: BaseHttpRequest): void
		{
			this._httpRequestDic.add(request);
		}
	
		/// <summary>
		/// 删除httpRequest
		/// </summary>
		public static removeHttpRequest(request: BaseHttpRequest): void
		{
			this._httpRequestDic.remove(request);
		}
	
	}
}