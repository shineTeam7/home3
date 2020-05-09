namespace Shine
{
	/** c层版本号枚举 */
	export class GameAreaDivideType
	{
		/** 分区分服(大区小区都分)(有合服,每个game服单独数据库) */
        public static Split:number=1;
        /** 自动绑定game服(有合服,每个game服单独数据库)(只是由center自动挑选进入game) */
        public static AutoBindGame:number=2;
	}
}