using System;

namespace ShineEngine
{
	/** 方法队列 */
	public class SFuncQueue:SBatchQueue<Action>
	{
		public SFuncQueue():base(runFunc)
		{

		}

		private static void runFunc(Action func)
		{
			try
			{
				func();
			}
			catch(Exception e)
			{
				Ctrl.errorLogForIO(e);
			}
		}
	}
}