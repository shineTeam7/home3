using System;
using System.Text;
using System.IO;
using System.Security.Cryptography;
using UnityEngine;

namespace ShineEngine
{
	/// <summary>
	/// 文件方法组
	/// </summary>
	public static class FileUtils
	{
		/** 获取bundle资源使用名字 */
		public static string getBundleResourceUseName(string name)
		{
			return ShineGlobal.sourceHeadL + name.ToLower();
		}

		//--文件读写--//

		/// <summary>
		/// 获取文件扩展名
		/// </summary>
		public static string getFileExName(string name)
		{
			return name.slice(name.LastIndexOf('.') + 1).ToLower();
		}

		/// <summary>
		/// 获取文件前名
		/// </summary>
		public static string getFileFrontName(string name)
		{
			return name.slice(0,name.LastIndexOf('.'));
		}

		/** 获取文件名 */
		public static string getFileName(string path)
		{
			return path.slice(path.LastIndexOf('/') + 1);
		}

		/** 获取文件前名 */
		public static string getFileNameWithoutEx(string path)
		{
			return getFileFrontName(getFileName(path));
		}

		/** 获取文件所在目录 */
		public static string getFileDicPath(string path)
		{
			return path.slice(0,path.LastIndexOf('/'));
		}

		/** 通过Assets路径(Assets/source/...)取bundle资源相对路径(从Assets/source/后面开始，不包含Assets/source/的目录)(没找到返回"") */
		public static string getBundleResourcePath(string path)
		{
			if (path.StartsWith(ShineGlobal.sourceHeadU))
			{
				return path.Substring(ShineGlobal.sourceHeadU.Length);
			}

			return "";
		}

		/** 获取一个绝对路径的资源相对路径(Assets及以后内容) */
		public static string getAssetUsePath(string path)
		{
			int index=path.IndexOf(ShineGlobal.sourceHeadU);

			if(index!=null)
			{
				path=path.Substring(index);
			}

			return path;
		}

		/// <summary>
		/// 写二进制文件
		/// </summary>
		public static void writeFileForBytesWriteStream(string path,BytesWriteStream stream)
		{
			surePath(path);
			FileStream fs=new FileStream(path,FileMode.Create);
			fs.Write(stream.getBuf(),0,stream.length());
			fs.Close();
		}

		/// <summary>
		/// 写二进制文件
		/// </summary>
		public static void writeFileForBytes(string path,byte[] bytes)
		{
			surePath(path);
			FileStream fs=new FileStream(path,FileMode.Create,FileAccess.Write);
			fs.Write(bytes,0,bytes.Length);
			fs.Close();
		}

		/// <summary>
		/// 写二进制文件
		/// </summary>
		public static void writeFileForBytes(string path,BytesWriteStream bytes)
		{
			surePath(path);
			FileStream fs=new FileStream(path,FileMode.Create,FileAccess.Write);
			fs.Write(bytes.getBuf(),0,bytes.length());
			fs.Close();
		}

		/// <summary>
		/// 写字符串文件
		/// </summary>
		public static void writeFileForUTF(string path,string str)
		{
			writeFileForBytes(path,Encoding.UTF8.GetBytes(str));
		}

		/// <summary>
		/// 写xml文件
		/// </summary>
		public static void writeFileForXML(string path,XML xml)
		{
			writeFileForUTF(path,xml.ToString());
		}

		/// <summary>
		/// 读取二进制文件
		/// </summary>
		public static byte[] readFileForBytes(string path)
		{
			if(!File.Exists(path))
				return null;

			FileStream fs=new FileStream(path,FileMode.Open,FileAccess.Read);
			byte[] bs=new byte[fs.Length];
			fs.Read(bs,0,bs.Length);
			fs.Close();
			fs.Dispose();
			return bs;
		}

		/// <summary>
		/// 从文件读出字节读流
		/// </summary>
		public static BytesReadStream readFileForBytesReadStream(string path)
		{
			if(!File.Exists(path))
				return null;

			return new BytesReadStream(readFileForBytes(path));
		}

		/// <summary>
		/// 读字符串文件
		/// </summary>
		public static string readFileForUTF(string path)
		{
			if(!File.Exists(path))
				return "";

			byte[] b=readFileForBytes(path);

			return Encoding.UTF8.GetString(b,0,b.Length);
		}

		/// <summary>
		/// 读xml
		/// </summary>
		public static XML readFileForXML(string path)
		{
			if(!File.Exists(path))
				return null;

			return XML.readXMLByString(readFileForUTF(path));
		}

		//--Resources读取--//

		/// <summary>
		/// 从Resources目录读取资源
		/// </summary>
		public static string readResourceForUTF(string path)
		{
			TextAsset text=Resources.Load<TextAsset>(getFileFrontName(path));

			if(text==null)
				return "";

			return text.text;
		}

		/// <summary>
		/// 从Resources目录读取xml
		/// </summary>
		public static XML readResourceForXML(string path)
		{
			return XML.readXMLByString(readResourceForUTF(path));
		}

		/** 文件是否存在 */
		public static bool fileExists(string path)
		{
			return File.Exists(path);
		}

		/** 删除文件 */
		public static bool deleteFile(string path)
		{
			if(File.Exists(path))
			{
				File.Delete(path);
				return true;
			}

			return false;
		}

		/** 清空文件夹 */
		public static void clearDir(string path)
		{
			if(!Directory.Exists(path))
				return;

			string[] files=Directory.GetFiles(path);
			for(int i=0;i<files.Length;i++)
			{
				File.Delete(files[i]);
			}

			string[] directories=Directory.GetDirectories(path);
			for(int i=0;i<directories.Length;i++)
			{
				string directory=directories[i];
				clearDir(directory);
				Directory.Delete(directory);
			}
		}

		//--path相关--//

		/** 路径修复(\to/) */
		public static string fixPath(string path)
		{
			return path.Replace('\\','/');
		}

		/** 取完整修复路径 */
		public static string fixAndFullPath(string path)
		{
			return fixPath(Path.GetFullPath(path));
		}

		/** 将两个路径结合起来,支持/.. */
		public static string subPath(string path,string part)
		{
			return fixPath(Path.GetFullPath(path + part));
		}

		/** 递归获取文件列表(fix过的) */
		public static string[] getDeepFileList(string path)
		{
			string[] re=Directory.GetFiles(path,"*.*",SearchOption.AllDirectories);

			for(int i=re.Length - 1;i>=0;--i)
			{
				re[i]=fixPath(re[i]);
			}

			return re;
		}

		/** 递归获取文件列表(fix过的) */
		public static string[] getDeepFileList(string path,string exName)
		{
			return toGetFiles(true,path,exName);
		}

		/** 获取文件列表(fix过的) */
		public static string[] getFileList(string path,string exName)
		{
			return toGetFiles(false,path,exName);
		}

		private static string[] toGetFiles(bool isDeep,string path,string exName)
		{
			string[] re=Directory.GetFiles(path,"*." + exName,isDeep ? SearchOption.AllDirectories : SearchOption.TopDirectoryOnly);

			for(int i=re.Length - 1;i>=0;--i)
			{
				re[i]=fixPath(re[i]);
			}

			return re;
		}

		/** 递归获取文件列表（多格式） */
		public static SList<string> getDeepFileMutipleList(string path,params string[] exNames)
		{
			return toGetFileMutipleList(true,path,exNames);
		}

		/** 递归获取文件列表（多格式） */
		public static SList<string> getFileMutipleList(string path,params string[] exNames)
		{
			return toGetFileMutipleList(false,path,exNames);
		}

		/** 递归获取文件列表（多格式） */
		private static SList<string> toGetFileMutipleList(bool isDeep,string path,params string[] exNames)
		{
			SList<string> re=new SList<string>();

			foreach(string exName in exNames)
			{
				string[] files=Directory.GetFiles(path,"*." + exName,isDeep ? SearchOption.AllDirectories : SearchOption.TopDirectoryOnly);

				for(int i=files.Length - 1;i>=0;--i)
				{
					if(files[i].EndsWith(".DS_Store"))
						continue;

					re.add(fixPath(files[i]));
				}
			}

			return re;
		}

		/** 递归获取所有文件列表(excludes:排除扩展名) */
		public static SList<string> getDeepFileListForIgnore(string path,params string[] exIgnore)
		{
			return toGetDeepFileListForIgnore(true,path,exIgnore);
		}

		/** 递归获取文件列表(excludes:排除扩展名) */
		public static SList<string> getFileListForIgnore(string path,params string[] exIgnore)
		{
			return toGetDeepFileListForIgnore(false,path,exIgnore);
		}

		private static SList<string> toGetDeepFileListForIgnore(bool isDeep,string path,params string[] exIgnore)
		{
			string[] files=Directory.GetFiles(path,"*",isDeep ? SearchOption.AllDirectories : SearchOption.TopDirectoryOnly);

			SList<string> re=new SList<string>();

			string s;

			for(int i=files.Length - 1;i>=0;--i)
			{
				s=files[i];

				if(s.EndsWith(".DS_Store"))
					continue;

				if(exIgnore.Length>0)
				{
					if(Array.IndexOf(exIgnore,getFileExName(s))!=-1)
					{
						continue;
					}
				}

				re.add(fixPath(s));
			}

			return re;
		}

		/** 获取文件的md5值 */
		public static string getFileMD5(string path)
		{
			byte[] bytes=readFileForBytes(path);

			if(bytes==null)
				return "";

			return StringUtils.md5(bytes);
		}

		private static void surePath(string path)
		{
			int index=path.LastIndexOf("/");

			if(index!=-1)
			{
				string temp=path.Substring(0,index);

				if(!Directory.Exists(temp))
					Directory.CreateDirectory(temp);
			}
		}

		/** 拷贝文件(会创建对应文件夹) */
		public static void copyFile(string srcPath,string targetPath)
		{
			surePath(targetPath);
			File.Copy(srcPath,targetPath,true);
		}
	}
}