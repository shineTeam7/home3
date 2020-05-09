namespace Shine
{
	/** 启动入口 */
	export class ShineSetup
	{
		private static _inited:boolean=false;

		private static _exited:boolean=false;

		private static _exitRun:Func;

		//behaviours
		private static _lastTime:number;

	    /** 启动 */
	    public static setup(exitFunc:Func):void
	    {
			if(this._inited)
				return;

			this._inited=true;
			this._exitRun=exitFunc;

			this._lastTime=Ctrl.getTimer();

			DateControl.init();
			NetControl.init();
			BytesControl.init();

			SKeyboardControl.init();
			// STouchControl.init();
			// CameraControl.init();
			// Tween.init();

			// LoadControl.init();
			// ResourceInfoControl.init();
			// UIControl.init();
			// AssetControl.init();

			Laya.timer.frameLoop(1,this,this.onFrame);
	    }

		/** 退出(入口) */
		public static exit(str?:string):void
		{
			if(str!=null)
			{
				Ctrl.errorLog(str);
			}

			if(this._exited)
				return;

			this._exited=true;

			Ctrl.print("shine exit");

			if(this._exitRun!=null)
				this._exitRun.invoke();

			NetControl.dispose();

			//TODO:找到laya退出方法
		}

		private static onFrame():void
		{
			var now:number=Ctrl.getTimer();
			var dd:number=now - this._lastTime;
			this._lastTime=now;

			TimeDriver.instance.tick(dd);
		}
	}
}