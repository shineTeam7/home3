namespace Shine
{
	export class BaseHttpRequest extends BaseData
	{
		/** www对象 */
		private _www:Laya.HttpRequest;
	
		/** 地址 */
		protected _url:string;
	
		/** httpMethod */
		protected _method:number=HttpMethodType.Get;
	
		/** post数据 */
		protected _postStream:BytesWriteStream;
	
		/** 结果数据 */
		protected _resultStream:BytesReadStream;
	
	    /* 超时时间,默认30秒 */
	    public timeOut: number = 30 * 1000;
	
	    /** 是否处理 */
	    private _dided: boolean = false;
	
	    constructor()
	    {
			super();
	    }
	
	    /** 把数据拷贝下 */
	    protected copyData(): void  
	    {
	    }
	
	    /* 发送 */
	    public send(): void 
	    {
	        this.write();
	
	        this._www=new Laya.HttpRequest();

	        this._www.once(Laya.Event.COMPLETE, this, this.preComplete);
			this._www.once(Laya.Event.ERROR, this, this.preError);
	
	        if (this._method == HttpMethodType.Get) 
	        {
	            this._www.send(this._url, null, 'get', 'text');
	        }
	        else if (this._method == HttpMethodType.Post)
	        {
				var arr:ArrayBuffer=this._postStream.getByteArray();

				if(ShineSetting.clientUseBase64)
				{
					this._www.send(this._url,Base64.encode(arr) , 'post', 'arraybuffer');//['content-type', 'pplication/octet-stream']
				}
				else
				{
					this._www.send(this._url,arr , 'post', 'arraybuffer');
				}
	        }
	
	        NetControl.addHttpRequest(this);
	    }
	
		protected write():void
	    {
	
	    }
	
	    /** 是否已结束 */
	    public isDone():boolean
	    {
	        return this._dided;
	    }
	
		/** 析构 */
		public dispose():void
	    {
	        if (this._dided)
	            return;
	
	        this._dided = true;
	
	        this.onError();
	
	    }
	
		/** 预完成 */
		private preComplete():void
	    {
	        if (this._dided)
	            return;
	
	        this._dided = true;
	
	        this._resultStream = new BytesReadStream(this._www.data);
	
	        this.read();
	
	        this.onComplete();
	    }
	
	    private preError():void
	    {
	        if (this._dided)
	            return;
	
	        this._dided = true;
	
	        this.onError();
	    }
	
		protected read():void
	    {
	
	    }
	
		/** IO出错 */
		protected onError():void
	    {
	
	    }
	
		/** 完成*/
		protected onComplete():void
	    {
	
	    }
	}
}