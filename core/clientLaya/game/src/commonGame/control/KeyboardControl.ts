namespace Shine
{
	export class KeyboardControl extends SBaseEventRegister<number>
	{
		constructor()
        {
            super();
        }

	    public init():void
		{
			SKeyboardControl.keyFunc=Func.create(this,this.onSKey);
		}
	
		private onSKey(code:number,isDown:boolean):void
		{
			this.onKey(code,isDown);
	
			this.dispatch(isDown ? KeyEventType.KeyDown : KeyEventType.KeyUp,code);
		}
	
		/** 键盘操作 */
		protected onKey(code:number,isDown:boolean):void
		{
			switch(code)
			{
				case Laya.Keyboard.ESCAPE:
				{
					if(!isDown)
					{
						this.escDown();
					}
				}
					break;
				case Laya.Keyboard.NUMPAD_ADD:
				{
					if(!isDown)
					{
						this.escDown();
					}
				}
					break;
			}
		}
	
		/** esc退出方法 */
		protected escDown():boolean
		{
			// if(CommonSetting.useReporter && GameC.debug.getReporter().show)
			// {
			// 	GameC.debug.getReporter().hideReporter();
			// 	return true;
			// }
	
			// if(CommonSetting.useDebug && GameC.debug.getGMCommandUI().isShow())
			// {
			// 	GameC.debug.getGMCommandUI().hide();
			// 	return true;
			// }
	
			// if(CommonSetting.useReporter && !GameC.debug.getReporter().show)
			// {
			// 	GameC.debug.getReporter().showReporter();
			// 	return true;
			// }
	
			//其他
	
			return false;
		}
	}
}