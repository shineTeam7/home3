namespace Shine
{
	export class SKeyboardControl 
	{
		/** 按键枚举存储 */
		private static _keys:number[];
	
		/** 按下字典 */
		private static _downSet:SSet<number>= new SSet<number>();
	
		private static _ctrlCount:number= 0;
		private static _shiftCount:number= 0;
		private static _altCount:number= 0;
		private static _commondCount:number= 0;
	
		/** 键盘响应组 */
		public static keyFunc:Func;
	
		public static init():void
		{
			Laya.stage.on(Laya.Event.KEY_DOWN, this, this.onKeyDown);
			Laya.stage.on(Laya.Event.KEY_UP, this, this.onKeyUp);
		}
	
		/** 键盘按下 */
		private static onKeyDown(evt:KeyboardEvent)
		{
			if (this._downSet.contains(evt.keyCode))
				return;
	
			this._downSet.add(evt.keyCode);
	
			this.onKey(evt.keyCode, true);
		}
	
		/** 设置键盘弹起 */
		private static onKeyUp(evt:KeyboardEvent ):void
		{
			if (!this._downSet.contains(evt.keyCode))
				return;
	
			this._downSet.remove(evt.keyCode);
	
			this.onKey(evt.keyCode, false);
		}
	
		private static onKey(code:number , isDown:boolean):void
		{
			switch (code)
			 {
				case Laya.Keyboard.CONTROL:
					{
						if (isDown)
							this._ctrlCount++;
						else
							this._ctrlCount--;
					}
					break;
				case Laya.Keyboard.SHIFT:
					{
						if (isDown)
							this._shiftCount++;
						else
							this._shiftCount--;
					}
					break;
				case Laya.Keyboard.ALTERNATE:
					{
						if (isDown)
							this._altCount++;
						else
							this._altCount--;
					}
					break;
				case Laya.Keyboard.COMMAND:
					{
						if (isDown)
							this._commondCount++;
						else
							this._commondCount--;
					}
					break;
			}
	
			//TODO:其他esc

			if(this.keyFunc!=null)
			{
				this.keyFunc.invoke(code, isDown);
			}

			// if (!isDown || code == Laya.Keyboard.ESCAPE) {
			// 	this.keyFunc.invoke(code, isDown);
			// }
		}
	
		/** ctrl是否按下 */
		public static isCtrlDown():boolean
		{
			return this._ctrlCount != 0;
		}
	
			/** shift是否按下 */
		public static isShiftDown():boolean
		{
			return this._shiftCount != 0;
		}
	
			/** alt是否按下 */
		public static isAltDown():boolean
		{
			return  this._altCount != 0;
		}
	
		/** command是否按下 */
		public static isCommoandDown():boolean
		{
			return  this._commondCount != 0;
		}
	}
}