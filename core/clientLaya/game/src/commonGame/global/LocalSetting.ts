namespace Shine
{
	export class LocalSetting
	{
	    /** 登录连接组 */
		public static loginURLs:SList<string>=new SList<string>();
	
		/** 登录服短链地址 */
		public static loginHttpURL:string;

		constructor()
        {
			
        }

		/** 初始化 */
		public static init(xml:XMLDocument):void
		{
			var attr:NodeList = xml.childNodes[0].childNodes;

			for (var i = 0; i < attr.length; i++)
			{
				if (attr[i].nodeName == "loginURL")
				{
					for (var index = 0; index < attr[i]["attributes"].length; index++) 
					{
						this.loginURLs.add(attr[i]["attributes"][index].nodeValue);
					}
				}
			}

			this.loginHttpURL=this.getRandomLoginURL();
		}
	
		/** 获取一个随机的loginURL */
		public static getRandomLoginURL():string
		{
			return this.loginURLs.get(MathUtils.randomInt(this.loginURLs.length));
		}

		/** 设置登录url */
		public static setLoginURL(host:string,port:number):void
		{
			this.loginHttpURL="http://" + host + ":" + port;
		}
	
	}
}