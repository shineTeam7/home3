using System.Text;

namespace ShineEngine
{
	public class LogInfo
	{
		private StringBuilder _sb;

		private string _head;
		private int _num=0;

		public LogInfo()
		{
			_sb=StringBuilderPool.create();
		}

		public LogInfo(string head)
		{
			_sb=StringBuilderPool.create();

			putHead(head);
		}

		/** 写头 */
		public void putHead(string head)
		{
			_head=head;

			_sb.Length=0;
			_num=0;
			_sb.Append(head);
			_sb.Append(' ');
		}

		/** 写时间戳 */
		public void putTimestamp()
		{
			put("timestamp",DateControl.getTimeMillis());
		}

		private void putKey(string key)
		{
			if(_num>0)
			{
				_sb.Append('&');
			}

			_sb.Append(key);
			_sb.Append('=');
		}

		/** 添加 */
		public void put(string key,string value)
		{
			putKey(key);
			_sb.Append(value);
			++_num;
		}

		/** 添加 */
		public void put(string key,long value)
		{
			putKey(key);
			_sb.Append(value);
			++_num;
		}

		/** 添加 */
		public void put(string key,int value)
		{
			putKey(key);
			_sb.Append(value);
			++_num;
		}

		/** 添加 */
		public void put(string key,bool value)
		{
			putKey(key);
			_sb.Append(value ? "true" : "false");
			++_num;
		}

		/** 返回字符串并析构 */
		public string releaseString()
		{
			StringBuilder sb=_sb;
			_sb=null;
			return StringBuilderPool.releaseStr(sb);
		}

		public string getString()
		{
			return _sb.ToString();
		}

		public void clear()
		{
			_sb.Length=0;
			_num=0;
		}

		public void clearToHead()
		{
			_sb.Length=0;
			_num=0;
			_sb.Append(_head);
			_sb.Append(' ');
		}

		/** 返回字符串并析构 */
		public string getStringAndClear()
		{
			string re=getString();
			clear();
			return re;
		}

		/** 返回字符串并析构 */
		public string getStringAndClearToHead()
		{
			string re=getString();
			clearToHead();
			return re;
		}
	}
}