using System;

namespace ShineEngine
{
	public class SectionTool<T>
	{
		private SList<SectionObj> _list=new SList<SectionObj>();

		private bool _leftFree;

		private int[] _keys;
		private T[] _values;

		public void put(int min,int max,T obj)
		{
			SectionObj oo=new SectionObj();
			oo.min=min;
			oo.max=max;
			oo.obj=obj;

			_list.add(oo);
		}

		private int compare(SectionObj obj1,SectionObj obj2)
		{
			return MathUtils.intCompare(obj1.min,obj2.min);
		}

		/** 构造 */
		public void make()
		{
			_list.sort(compare);

			if(_list.isEmpty())
			{
				Ctrl.errorLog("SectionTool不能没有数据");
				return;
			}

			int len=_list.size();

			_keys=new int[len+1];
			_values=new T[len+1];

			if(_list.get(0).min==-1)
			{
				_list.get(0).min=0;
				_leftFree=true;
			}

			if(_list.getLast().max==-1)
			{
				_list.getLast().max=int.MaxValue;
				_values[len]=_list.getLast().obj;
			}
			else
			{
				_values[len]=default(T);
			}

			_keys[len]=_list.getLast().max;

			SectionObj obj;

			for(int i=0;i<len;i++)
			{
				obj=_list.get(i);

				_keys[i]=obj.min;
				_values[i]=obj.obj;

				if(i<len-1)
				{
					if(obj.max<_list.get(i+1).min)
					{
						Ctrl.throwError("配置表错误,max比min小2");
					}
				}
			}
		}

		/** 获取value对应的key */
		public T get(int key)
		{
			int index=Array.BinarySearch(_keys,key);

			if(index>=0)
			{
				int last=_keys.Length-1;

				if(index==last)
				{
					//结尾的算<=
					if(key==_keys[last])
					{
						return _values[last-1];
					}
				}

				return _values[index];
			}
			else
			{
				if(index==-1)
				{
					return _leftFree ? _values[0] : default(T);
				}

				index=-index-2;

				if(index>=_values.Length)
					index=_values.Length-1;

				return _values[index];
			}
		}

		private class SectionObj
		{
			public int min;
			public int max;

			public T obj;
		}
	}
}