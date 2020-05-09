using System;

namespace ShineEngine
{
	/// <summary>
	/// 缓动类
	/// </summary>
	public class Tween
	{
		/** 常规 */
		public static FloatTweenFactory normal=new FloatTweenFactory();

		/** 坐标 */
		public static Vector3TweenFactory vector3=new Vector3TweenFactory();

		/** 初始化 */
		public static void init()
		{
			TimeDriver.instance.setFrame(tick);
		}

		public static void tick(int delay)
		{
			normal.tick(delay);
			vector3.tick(delay);
		}
	}
}