using System;
using System.IO;
using ShineEngine;

namespace ShineEditor
{
	/// <summary>
	/// 文件记录插件
	/// </summary>
	public class FileRecordTool
	{


		private string _path;

		private bool _isAllClear = false;

		private SMap<string, FileRecordData> _dic = new SMap<String, FileRecordData>();

		private SSet<string> _lastSet = new SSet<string>();

		/** 是否启用md5校验 */
		private bool _useMD5 = false;

		/** md5缓存(防止多次计算) */
		private SMap<string, int> _md5Dic = new SMap<string, int>();
		/** 设定版本 */
		private string _version = "";

		private bool _isNew = true;

		public FileRecordTool(string path)
		{
			_path = path;
		}

		/** 是否启用md5校验 */
		public void setUseMD5(bool value)
		{
			_useMD5 = value;
		}

		/** 设定版本 */
		public void setVersion(string version)
		{
			_version = version;
		}

		/** 是否新的 */
		public bool isNew()
		{
			return _isNew;
		}

		/** 加载文件记录 */
		public void read(bool allClear)
		{
			_isAllClear = allClear;

			_dic.clear();
			_lastSet.clear();

			//allClear不读
			if (File.Exists(_path) && !_isAllClear)
			{
				XML xml = FileUtils.readFileForXML(_path);

				String oldVersion = xml.getProperty("version");

				//版本对
				if (oldVersion.Equals(_version))
				{
					foreach (XML xl in xml.getChildrenByName("info"))
					{
						FileRecordData data = new FileRecordData();
						data.lastModified = Convert.ToInt64(xl.getProperty("lastModified"));
						data.length = Convert.ToInt64(xl.getProperty("length"));
						data.md5 = xl.getProperty("md5");
						data.ex = xl.getProperty("ex");
						data.ex2 = xl.getProperty("ex2");
						data.ex3 = xl.getProperty("ex3");
						data.ex4 = xl.getProperty("ex4");

						_dic.put(xl.getProperty("path"), data);
                        _lastSet.add(xl.getProperty("path"));

						_isNew = false;
					}
				}
			}
		}

		/** 写信息到文件 */
		public void write()
		{
			XML xml = new XML();
			xml.name = "infoBase";
			xml.setProperty("version", _version);

			foreach (string k in _dic.getSortedMapKeys())
			{
				FileRecordData data = _dic.get(k);

				XML xl = new XML();
				xl.name="info";
				xl.setProperty("path", k);
				xl.setProperty("lastModified", Convert.ToString(data.lastModified));
				xl.setProperty("length", Convert.ToString(data.length));
				xl.setProperty("md5", data.md5);
				xl.setProperty("ex", data.ex);
				xl.setProperty("ex2", data.ex2);
				xl.setProperty("ex3", data.ex3);
				xl.setProperty("ex4", data.ex4);

				xml.appendChild(xl);
			}

			FileUtils.writeFileForXML(_path, xml);
		}

		/** 清空md5记录 */
		public void clearMD5Record()
		{
			_md5Dic.clear();
		}

		/** 添加文件记录 */
		public void addFile(string path, string ex = "", string ex2 = "", string ex3 = "", string ex4="")
		{
			FileRecordData data = new FileRecordData();

			data.lastModified=File.GetLastWriteTime(path).Ticks;

			data.length = File.GetLastAccessTime(path).Ticks;

			if (_useMD5)
			{
				data.md5 = getFileMD5(path);
			}

			data.ex = ex;
			data.ex2 = ex2;
			data.ex3 = ex3;
			data.ex4 = ex4;

			_dic.put(path, data);
		}

		private string getFileMD5(string path)
		{
			return "";
			//return MD5.hashBytes(FileUtils.readFileForBytes(f.getPath()));
		}

		/** 移除记录 */
		public void removePath(string path)
		{
			_dic.remove(path);
		}

		/** 是否有某文件的记录 */
		public bool hasFile(string path)
		{
			return _dic.contains(path);
		}

		/** 获取某文件的ex值 */
		public string getFileEx(string path)
		{
			FileRecordData data = _dic.get(path);

			if (data == null)
				return "";

			return data.ex;
		}

		/** 获取某文件的ex2值 */
		public string getFileEx2(string path)
		{
			FileRecordData data = _dic.get(path);

			if (data == null)
				return "";

			return data.ex2;
		}

		/** 获取某文件的ex3值 */
		public string getFileEx3(string path)
		{
			FileRecordData data = _dic.get(path);

			if (data == null)
				return "";

			return data.ex3;
		}

		/** 获取某文件的ex4值 */
		public string getFileEx4(string path)
		{
			FileRecordData data = _dic.get(path);

			if (data == null)
				return "";

			return data.ex4;
		}

		/** 操作过该文件 */
		public void didFile(string path)
		{
			_lastSet.remove(path);
		}

		/** 是否是新文件 */
		public bool isNewFile(string path)
		{
			if (_isAllClear)
				return true;

			_lastSet.remove(path);

			FileRecordData data = _dic.get(path);

			if (data == null)
				return true;

			if (File.GetLastWriteTime(path).Ticks != data.lastModified)
				return true;

			if (File.GetLastAccessTime(path).Ticks != data.length)
				return true;

			if (_useMD5)
			{
				if (!getFileMD5(path).Equals(data.md5))
				{
					return true;
				}
			}

			return false;
		}

		/** 剩余键组 */
		public SSet<string> getLastSet()
		{
			return _lastSet;
		}

		/** 删除lastSet中的文件 */
		public void deleteLastFiles()
		{
			foreach (string k in _lastSet)
			{
				if (File.Exists(k))
				{
					File.Delete(k);
				}

				removePath(k);
			}
		}

		/** 获取Record字典 */
		public SMap<string, FileRecordData> getRecordDic()
		{
			return _dic;
		}
	}
}
	
