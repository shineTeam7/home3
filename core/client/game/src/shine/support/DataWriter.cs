using System.Text;
using System.Threading;

namespace ShineEngine
{
	public class DataWriter
	{
		/** 回车 */
		private const string Enter="\r\n";
		/** 换行 */
		private const string Tab="\t";

		public StringBuilder sb;

		protected int _off=0;

		public DataWriter()
		{
			sb=StringBuilderPool.create();
		}

		/** 释放并获取string */
		public string releaseStr()
		{
			return StringBuilderPool.releaseStr(sb);
		}

		private void writeSomeTab(int num)
		{
			if(ShineSetting.needDataStringOneLine)
				return;

			if(num==0)
				return;

			for(int i=0;i<num;i++)
			{
				sb.Append(Tab);
			}
		}

		/** 写左边大括号(右缩进) */
		public void writeEnter()
		{
			if(ShineSetting.needDataStringOneLine)
			{
				//换空格
				sb.Append(' ');
				return;
			}

			sb.Append(Enter);
		}

		public void writeTabs()
		{
			writeSomeTab(_off);
		}

		/** 写左边大括号(右缩进) */
		public void writeLeftBrace()
		{
			writeSomeTab(_off);
			sb.Append("{");
			writeEnter();
			_off++;
		}

		/** 写右边大括号(右缩进) */
		public void writeRightBrace()
		{
			_off--;
			writeSomeTab(_off);
			sb.Append("}");
//			writeEnter();
			//TODO:这里不空格,在外面调空格,如有问题再改
		}

		/** 写空行 */
		public void writeEmptyLine()
		{
			writeSomeTab(_off);
			writeEnter();
		}

		/** 写自定义行 */
		public void writeCustom(string content)
		{
			writeSomeTab(_off);
			sb.Append(content);
			writeEnter();
		}
	}
}