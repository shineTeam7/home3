namespace Shine
{
	export class SimpleHttpRequest extends BaseHttpRequest 
	{
	    private _completeFunc: Function;
	
	    private _errorFunc: Function;
	
	    private _postData: string;
	
	    private _resultData: string;
	
	    constructor() 
	    {
			super();
	        this._method = HttpMethodType.Get;
	    }
	
	    protected write(): void
	    {
	        this._postStream = new BytesWriteStream();
	        this._postStream.writeUTFBytes(this._postData);
	    }
	
	    protected read(): void 
	    {
	        this._resultData = this._resultStream.readUFTBytes(this._resultStream.bytesAvailable());
	    }
	
	    protected onComplete(): void 
	    {
	        if (this._completeFunc != null)
	            this._completeFunc(this._resultData);
	    }
	
	    protected onError(): void
	    {
	        if (this._errorFunc != null)
	            this._errorFunc();
	    }
	
	    public static create(): SimpleHttpRequest 
	    {
	        return new SimpleHttpRequest();
	    }
	
	    /* http请求 */
	    public static httpRequest(url: string, method: number = HttpMethodType.Get, data: string = null, completeFunc: Function = null, errorFunc: Function = null): void 
	    {
	        if (url.indexOf('?') == -1) 
	        {
	            url += "?v=" + MathUtils.randomInt(1000000);
	        }
	        else 
	        {
	            url += "&v=" + MathUtils.randomInt(1000000);
	        }
	            
	        var request: SimpleHttpRequest = SimpleHttpRequest.create();
	        request._url = url;
	        request._method = method;
	        request._postData = data;
	        request._completeFunc = completeFunc;
	        request._errorFunc = errorFunc;
	
	        request.send();
	    }
	}
}