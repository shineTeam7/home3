using System;
using ShineEngine;

namespace ShineEditor
{
	/// <summary>
	/// 
	/// </summary>
	public class FileRecordData
	{
		/** 上次修改日期 */
		public long lastModified;
		/** 尺寸 */
		public long length;
		/** md5 */
		public string md5 = "";
		/** 附加信息(根目录) */
		public string ex = "";
		/** 附加信息2(字段排序) */
		public string ex2 = "";
		/** 附加信息3(表类型和前后端标记) */
		public string ex3 = "";
		/** 附加信息4(表横纵md5) */
		public string ex4 = "";

		public FileRecordData()
		{

		}
	}
}

