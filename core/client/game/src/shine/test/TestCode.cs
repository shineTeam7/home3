using System;

namespace ShineEngine
{
	/// <summary>
	/// 测试代码类
	/// </summary>
	public class TestCode
	{
		public static void testMethod(Action func,int loop)
		{
			int warmupLoop=loop * 20;

			for(int i=0;i<warmupLoop;++i)
			{
				func();
			}

			long t=Ctrl.getNanoTime();

			for(int i=0;i<loop;++i)
			{
				func();
			}

			Ctrl.print(Ctrl.getNanoTime() - t);
		}
	}
}