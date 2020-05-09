namespace Shine
{
    /** 加载控制 */
    export class BytesControl
    {
		/** 构造组 */
		private static _createList=new Array<Function>(ShineSetting.dataMaxNum);

		/** 协议忽略mid */
		private static _messageIgnoreSet:SSet<number>=new SSet<number>();

		/** 添加构造器 */
		public static addMaker(maker:DataMaker):void
		{
			var len:number=maker.offSet + maker.list.length;

			var func:Function;

			for(var i:number=maker.offSet;i<len;++i)
			{
				func=maker.list[i - maker.offSet];

				if(func!=null)
				{
					this._createList[i]=func;
				}
			}
		}

		/** 初始化 */
		public static init():void
		{
			this.addMaker(new ShineDataMaker());

			//添加shine消息
			for(var i:number=ShineRequestType.off;i<ShineRequestType.count;i++)
			{
				this.addIgnoreMessage(i);
			}
		}

		//---Data---//

		/** 通过id取得Data类型 */
		public static getDataByID( dataID:number):BaseData
		{
			if(dataID==-1)
				return null;

			var func:Function=this._createList[dataID];

			if(func==null)
			{
				Ctrl.throwError("找不到Data类型" + dataID);
				return null;
			}

			return func();
		}

		/** 数组拷贝(从src拷贝到des) */
		public static arrayCopy(src:Array<any>, des:Array<any>, length:number):void
		{
            for (var index = 0; index < length; index++)
            {
                 des[index] = src[index];
            }
		}

		/** 添加要被忽略的消息mid */
		public static addIgnoreMessage(mid:number):void
		{
			this._messageIgnoreSet.add(mid);
		}

		/** 是否shine消息 */
		public static isIgnoreMessage(mid:number):boolean
		{
			return this._messageIgnoreSet.contains(mid);
		}
    }
}