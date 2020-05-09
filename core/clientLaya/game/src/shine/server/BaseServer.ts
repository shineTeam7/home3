namespace Shine
{
	export class BaseServer
	{
	    	/** 连接 */
		private _socket:BaseSocket;
	
		/** 组构造器 */
		private _maker:DataMaker=new DataMaker();

		protected _messageBind:MessageBindTool=new MessageBindTool();
	
		constructor()
		{
			this._socket=new BaseSocket();
			this._socket.setConnectCall(new Func(this,this.onConnect));
			this._socket.setConnectFailedCall(new Func(this,this.onConnectFailed));
			this._socket.setCloseCall(new Func(this,this.onClose));
			this._socket.setCreateResponseFunc(new Func(this,this.createResponse));
		}
	
		public init():void
		{
			TimeDriver.instance.setFrame(Func.create(this,this.onFrame));

			this.addResponseMaker(new ShineResponseMaker());
		}
	
		/** 析构 */
		public dispose():void
		{
			this._socket.closeAndClear();
		}

		protected onFrame(delay:number):void
		{
			this._socket.onFrame(delay);
		}
	
		/* 关闭连接 */
		public close():void
		{
			this._socket.close();
		}
	
		/* 获取连接 */
		public getSocket():BaseSocket
		{
			return this._socket;
		}
	
		/** 连接 */
		public connect(host:string,port:number):void
		{
			this._socket.connect(host,port);
		}
	
		/** 创建响应对象 */
		public createResponse(mid:number):BaseResponse
		{
			return this._maker.getDataByID(mid) as BaseResponse;
		}
	
		/** 添加构造器 */
		public addResponseMaker(maker:DataMaker):void
		{
			this._maker.addDic(maker);
		}

		/** 添加客客户端发送消息绑定 */
		public addClientRequestBind(tool:MessageBindTool):void
		{
			this._messageBind.addDic(tool);
		}
	
		protected onConnect():void
		{

		}
	
		protected onConnectFailed():void
		{

		}
	
		protected onClose():void
		{

		}

		/** 检查消息绑定(返回是否可发送) */
		public checkRequestBind(mid:number):boolean
		{
			return true;
		}

		/** 检查消息的解绑 */
		public checkResponseUnbind(mid:number):void
		{

		}
	}
}