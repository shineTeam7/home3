namespace ShineEngine
{
	/// <summary>
	/// 序号生成器(上下限均不会达到)(min<value<max)
	/// </summary>
	public class IndexMaker
	{
		private int _min;

		private int _max;

		private bool _canRound;

		private int _index;

		public IndexMaker():this(0,int.MaxValue,false)
		{

		}

		public IndexMaker(int min,int max,bool canRound)
		{
			_min=min;
			_max=max;
			_canRound=canRound;

			_index=_min;
		}

		/** 重置 */
		public void reset()
		{
			_index=_min;
		}

		/** 取一个序号 */
		public int get()
		{
			int re=++_index;

			if(re >= _max)
			{
				if(_canRound)
				{
					_index=_min;
					re=++_index;
				}
				else
				{
					Ctrl.throwError("序号构造溢出");
				}
			}

			return re;
		}
	}
}