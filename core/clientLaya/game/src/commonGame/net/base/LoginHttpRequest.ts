namespace Shine
{
	export abstract class LoginHttpRequest extends BytesHttpRequest
	{
	    constructor()
		{
			super();
			this.setNeedFullWrite(ShineSetting.clientMessageUseFull);
			this._url=LocalSetting.loginHttpURL + "/" + ShineSetting.bytesHttpCmd;
		}
	}
}