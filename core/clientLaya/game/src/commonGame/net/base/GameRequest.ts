namespace Shine
{
	export class GameRequest extends BaseRequest
	{
	    public constructor()
		{
			super();
			//客户端request
			this.setOpenCheck(true);
		}
	
		/** 发送 */
		public send():void
		{
			GameC.server.getSocket().send(this);
		}
	}
}