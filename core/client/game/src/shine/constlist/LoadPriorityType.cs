namespace ShineEngine
{
	/// <summary>
	/// 加载优先级类型
	/// </summary>
	public class LoadPriorityType
	{
		/** 最优先 */
		public const int Top=0;

		/** 加载过的 */
		public const int Loaded=1;

		/** 界面(模型) */
		public const int UI=2;

		/** 默认 */
		public const int Default=3;

		/** 场景主角 */
		public const int SceneHero=4;

		/** 预加载 */
		public const int Back=6;

		/** 界面动画(界面动画资源) */
		public const int UIAnimation=5;

		/** 场景其他单位 */
		public const int SceneUnit=7;

		/** 场景动画 */
		public const int SceneAnimation=8;

		/** 声音 */
		public const int Sound=9;

		/** 地图资源(地图切片) */
		public const int SceneSource=10;

		/** 字体 */
		public const int Font=11;

		/** 最下 */
		public const int Bottom=12;
	}
}