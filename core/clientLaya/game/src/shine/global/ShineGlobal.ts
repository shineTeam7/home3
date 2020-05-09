namespace Shine
{
	/** 全局数据 */
	export class ShineGlobal
	{
	    /** config文件路径 */
		public static configPath:string="config.bin";
		/** 本地配置文件路径 */
		public static settingPath:string="setting.xml";

		public static configVersion:number=1;
		public static playerSaveVersion:number=1;
		public static localSaveVersion:number=1;
		public static loginDataVersion:number=1;
	}
}