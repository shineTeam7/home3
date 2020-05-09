namespace Shine
{
	export class GameC
	{
		/** 应用实例 */
		public static app:GameApp;
		/** 角色对象 */
		public static player:Player;
		/** server */
		public static server:GameServer;
		/** 主控制 */
		public static main:GameMainControl;
		/** 工厂控制 */
		public static factory:GameFactoryControl;
		/** 配置控制 */
		public static config:ConfigControl;
		/** 基础逻辑控制 */
		public static logic:BaseLogicControl;
		/** 场景控制 */
		public static scene:SceneControl;
		/** 信息控制 */
		public static info:InfoControl;
		/** 信息控制 */
		public static pool:GamePoolControl;
		/** 键盘控制 */
		public static keyboard:KeyboardControl;
		/** ui控制 */
		public static ui:GameUIControl;
		/** 本地本地存储控制 */
		public static save:LocalSaveControl;
		
	}
}