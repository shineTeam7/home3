using System;
using System.Collections.Generic;

namespace ShineEngine
{
	public class CustomComparer<V>:IComparer<V>
	{
		private Comparison<V> _value;

		public void setCompare(Comparison<V> value)
		{
			_value=value;
		}

		public int Compare(V x,V y)
		{
			return _value(x,y);
		}
	}
}