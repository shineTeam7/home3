using System;

namespace ShineEngine
{
	/// <summary>
	/// batch队列
	/// </summary>
	public class SBatchQueue<T>
	{
		private SQueue<T> _queue=new SQueue<T>();

		private Action<T> _consumer;

		public SBatchQueue(Action<T> consumer)
		{
			_consumer=consumer;
		}

		/** 添加 */
		public void add(T obj)
		{
			lock(_queue)
			{
				_queue.offer(obj);
			}
		}

		/** 执行一次 */
		public void runOnce()
		{
			lock(_queue)
			{
				Action<T> consumer=_consumer;
				SQueue<T> queue=_queue;

				if(!queue.isEmpty())
				{
					for(int i=queue.size() - 1;i>=0;--i)
					{
						try
						{
							consumer(queue.poll());
						}
						catch(Exception e)
						{
							Console.WriteLine(e);
							throw;
						}
					}
				}
			}
		}

		public void clear()
		{
			lock(_queue)
			{
				_queue.clear();
			}
		}
	}
}