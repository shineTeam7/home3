using System;
using System.Collections.Generic;

namespace ShineEngine
{
	/// <summary>
	/// 测试自定义字典
	/// </summary>
	public class TestCustomCollection
	{
		private int _len=256;

		private One[] _arr;

		private SMap<int,One> _dic=new SMap<int,One>();
		private IntObjectMap<One> _cDic=new IntObjectMap<One>();
		private Dictionary<int,One> _nDic=new Dictionary<int,One>();

		private int _re=0;

		private class One
		{
			public int id;

			public int level;

			public static One create(int id,int level)
			{
				One re=new One();

				re.id=id;
				re.level=level;

				return re;
			}
		}

		public void init()
		{
			_arr=new One[_len];

			for(int i=0;i<_len;++i)
			{
				_arr[i]=One.create(i,i);
				// _dic.put(i+1,_arr[i]);
				// _cDic.put(i+1,_arr[i]);
			}
		}


		public void foreach1()
		{
			_dic.forEachValue(v=>
			{
				_re=v.level;
			});
		}

		public void foreach2()
		{
			One[] values=_cDic.getValues();
			One v;

			for(int i=values.Length - 1;i>=0;--i)
			{
				if((v=values[i])!=null)
				{
					_re=v.level;
				}
			}
		}

		public void test1()
		{
			_dic.clear();

			for(int i=1;i<_len;++i)
			{
				_dic.put(i,_arr[i]);
			}

			for(int i=1;i<_len;++i)
			{
				_re=_dic.get(i).level;
			}
		}

		public void test2()
		{
			_cDic.clear();

			for(int i=1;i<_len;++i)
			{
				_cDic.put(i,_arr[i]);
			}

			for(int i=1;i<_len;++i)
			{
				_re=_cDic.get(i).level;
			}
		}

		public void test3()
		{
			_nDic.Clear();

			for(int i=1;i<_len;++i)
			{
				_nDic.Add(i,_arr[i]);
			}

			for(int i=1;i<_len;++i)
			{
				_re=_nDic[i].level;
			}
		}
	}
}