namespace Shine
{
	export class CenterRequest extends BaseRequest
	{
		constructor()
        {
            super();
			//关了检测
			this.setOpenCheck(false);
        }

		protected doWriteToStream(stream:BytesWriteStream)
		{
			//直接协议号和内容
			stream.natureWriteUnsignedShort(this._dataID);
			this.doWriteBytesSimple(stream);
		}
	
		/** 发送 */
		public send():void
		{
			GameC.player.sendCenter(this);
		}
	}
}