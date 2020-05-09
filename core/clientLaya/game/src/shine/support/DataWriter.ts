namespace Shine
{
	export class DataWriter
	{
		private static Enter: string = "\r\n";
		private static Tab: string = "\t";

		private _str: string;
		protected _off: number = 0;

		constructor()
		{
			this._str = "";
		}

			/** 释放并获取string */
		public releaseStr(): string
		{
			return this._str;
		}
		
		/** 写左边大括号(右缩进) */
		public writeEnter(): void
		{
			if(ShineSetting.needDataStringOneLine)
			{
				//换空格
				this._str += ' ';
				return;
			}

			this._str += DataWriter.Enter;
		}
		
		public writeTabs(): void
		{
			this.writeSomeTab(this._off);
		}
		
		/** 写左边大括号(右缩进) */
		public writeLeftBrace(): void
		{
			this.writeSomeTab(this._off);
			this._str += "{";
			this.writeEnter();
			this._off++;
		}
		
		/** 写右边大括号(右缩进) */
		public writeRightBrace(): void
		{
			this._off--;
			this.writeSomeTab(this._off);
			this._str += "}";
			//this.writeEnter();
			//TODO:这里不空格,在外面调空格,如有问题再改
		}
		
		/** 写空行 */
		public writeEmptyLine(): void
		{
			this.writeSomeTab(this._off);
			this.writeEnter();
		}
		
		/** 写自定义行 */
		public writeCustom(content: string): void
		{
			this.writeSomeTab(this._off);
			this._str += content;
			this.writeEnter();
		}
		
		private writeSomeTab(num: number): void
		{
			if(ShineSetting.needDataStringOneLine)
				return;
				
			if(num == 0)
				return;
			
			for(let i=0; i<num; i++)
			{
				this._str += DataWriter.Tab;
			}
		}

		public get sb(): DataWriter
		{
			return this;
		}
		
		public append(str: any): void
		{
			this._str += str;
		}
	}
}