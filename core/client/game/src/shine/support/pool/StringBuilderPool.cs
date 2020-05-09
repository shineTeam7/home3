using System;
using System.Text;
using System.Threading;

namespace ShineEngine
{
	/// <summary>
	/// stringBuilder池
	/// </summary>
	public class StringBuilderPool:ObjectPool<StringBuilder>
	{
		private static StringBuilderPool _instance=new StringBuilderPool();

		private static SMap<Thread,StringBuilderPool> _dic=new SMap<Thread,StringBuilderPool>();

		public StringBuilderPool():base(()=>new StringBuilder())
		{

		}

		public override void back(StringBuilder obj)
		{
			if(obj.Length>ShineSetting.stringBuilderPoolKeepSize)
				return;

			obj.Length=0;

			base.back(obj);
		}

		private static StringBuilderPool getPoolByThread(Thread thread)
		{
			StringBuilderPool pool=_dic.get(thread);

			if(pool==null)
			{
				_dic.put(thread,pool=new StringBuilderPool());
			}

			return pool;
		}

		/** 取一个 */
		public static StringBuilder create()
		{
			return _instance.getOne();
		}

		/** 还一个 */
		public static void release(StringBuilder obj)
		{
			_instance.back(obj);
		}

		/** 还一个并返回该StringBuilder的字符串 */
		public static string releaseStr(StringBuilder obj)
		{
			string v=obj.ToString();

			_instance.back(obj);

			return v;
		}

		/** 取一个 */
		public static StringBuilder createForThread()
		{
			return getPoolByThread(Thread.CurrentThread).getOne();
		}

		/** 还一个 */
		public static void releaseForThread(StringBuilder obj)
		{
			getPoolByThread(Thread.CurrentThread).back(obj);
		}

		/** 还一个并返回该StringBuilder的字符串 */
		public static string releaseStrForThread(StringBuilder obj)
		{
			string v=obj.ToString();

			getPoolByThread(Thread.CurrentThread).back(obj);

			return v;
		}
	}
}