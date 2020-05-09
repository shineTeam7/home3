using System;
using System.Data;
using System.IO;
using ShineEngine;
using System.Diagnostics;
using System.Security;
using System.Text;
using Excel;
using UnityEngine;

namespace ShineEditor
{
	/// <summary>
	/// 工具文件方法
	/// </summary>
	public static class ToolFileUtils
	{
		/** 通过资源绝对路径取工程相对路径(Assets/...)(没找到返回"") */
		public static string getAssetsPath(string path)
		{
			if(path.StartsWith(ShineToolGlobal.gamePath))
			{
				return path.Substring(ShineToolGlobal.gamePath.Length+1);
			}

			return "";
		}

		/** 通过资源绝对路径取工程相对路径(Assets/后的部分)(没找到返回"") */
		public static string getAssetsPathWithoutAssets(string path)
		{
			if(path.StartsWith(ShineToolGlobal.assetsPath))
			{
				return path.Substring(ShineToolGlobal.assetsPath.Length+1);
			}

			return "";
		}

		/** 通过资源绝对路径取bundle资源相对路径(从Assets/source/后面开始，不包含Assets/source/的目录)(没找到返回"") */
		public static string getResourcePath(string path)
		{
			if (path.StartsWith(ShineToolGlobal.outSourcePath))
			{
				return path.Substring(ShineToolGlobal.outSourcePath.Length + 1);
			}

			return "";
		}

		// /** 清空文件夹 */
		// public static void clearDir(string path)
		// {
		// 	if(!Directory.Exists(path))
		// 		return;
  //
		// 	string[] files=Directory.GetFiles(path);
		// 	for(int i=0;i<files.Length;i++)
		// 	{
		// 		File.Delete(files[i]);
		// 	}
  //
		// 	string[] directories=Directory.GetDirectories(path);
		// 	for(int i=0;i<directories.Length;i++)
		// 	{
		// 		string directory=directories[i];
		// 		clearDir(directory);
		// 		Directory.Delete(directory);
		// 	}
		// }

		/** 复制文件夹 */
		public static void CopyDir(string fromDir, string toDir)
		{
			if (!Directory.Exists(fromDir))
				return;

			if (!Directory.Exists(toDir))
			{
				Directory.CreateDirectory(toDir);
			}

			SList<string> files = FileUtils.getDeepFileListForIgnore(fromDir);
			foreach (string fromFileName in files)
			{
				string fileName = Path.GetFileName(fromFileName);
				string toFileName = Path.Combine(toDir, fileName);
				File.Copy(fromFileName, toFileName);
			}
			string[] fromDirs = Directory.GetDirectories(fromDir);
			foreach (string fromDirName in fromDirs)
			{
				string dirName = Path.GetFileName(fromDirName);
				string toDirName = Path.Combine(toDir, dirName);
				CopyDir(fromDirName, toDirName);
			}
		}

		public static long getFileLength(string path)
		{
			return new FileInfo(path).Length;
		}

		private static void toExecuteEx(string filePath,bool isMac,params string[] args)
		{
			ProcessStartInfo info = new ProcessStartInfo(filePath);

			if (isMac)
			{
				info.CreateNoWindow=false;
				info.ErrorDialog=true;


				info.UseShellExecute=false;
				info.RedirectStandardOutput=true;
				info.RedirectStandardError=true;
				info.RedirectStandardInput=true;
				info.StandardOutputEncoding=System.Text.UTF8Encoding.UTF8;
				info.StandardErrorEncoding=System.Text.UTF8Encoding.UTF8;
			}
			else
			{
				info.CreateNoWindow=false;
				info.ErrorDialog=true;
				//解决win10系统下调用shell脚本报错
				if (Environment.OSVersion.Version.Major == 10)
				{
					info.UseShellExecute=false;
				}
				else
				{
					info.UseShellExecute=true;
				}
				

//				info.CreateNoWindow=true;
//				info.ErrorDialog=true;
//				info.UseShellExecute=false;
//				info.RedirectStandardOutput=true;
//				info.RedirectStandardError=true;
//				info.RedirectStandardInput=true;
//				info.StandardOutputEncoding=System.Text.UTF8Encoding.UTF8;
//				info.StandardErrorEncoding=System.Text.UTF8Encoding.UTF8;
			}

			//info.WindowStyle=ProcessWindowStyle.Hidden;

			string path=FileUtils.fixPath(filePath);
			string exName=FileUtils.getFileExName(path);
			int index=path.LastIndexOf("/");
			string parentPath=index==-1 ? "" : path.Substring(0,index);

			info.WorkingDirectory=parentPath;
			
			StringBuilder sb=new StringBuilder(path.slice(index+1,path.Length));

			if (args.Length > 0)
			{
				for (int i = 0; i < args.Length; i++)
				{
					sb.Append(" ");
					sb.Append(args[i]);
				}
			}

			info.Arguments = sb.ToString();

			switch(exName)
			{
				case "bat":
				{
					info.FileName="cmd.exe";
				}
					break;
				case "py":
				{
					info.FileName="python";
				}
					break;
				case "sh":
				{
					info.FileName="sh";
				}
					break;
			}

			if (isMac)
			{
				using(Process process=Process.Start(info))
				{
					using(StreamReader reader=process.StandardOutput)
					{
						string result="";
						while(result!=null)
						{
							result=reader.ReadLine();

							if(result!=null && !result.isEmpty())
							{
								Ctrl.print(result);
							}
						}
					}
				}
			}
			else
			{
				Process task = Process.Start(info);
				task.WaitForExit();
				task.Close();
			}
		}


		/** 执行服务器tool脚本 */
		public static void executeServerTool(string name,params string[] args)
		{
			if(Application.platform==RuntimePlatform.OSXEditor)
			{
				toExecuteEx(ShineToolGlobal.serverToolsMacPath + "/"+name+".sh",true,args);
			}
			else if(Application.platform==RuntimePlatform.WindowsEditor)
			{
				toExecuteEx(ShineToolGlobal.serverToolsPath + "/"+name+"ForEditor"+".py",false,args);
			}
		}

		public static void executeServerToolWin(string name)
		{
			toExecuteEx(ShineToolGlobal.serverToolsPath + "/" + name+ ".py", false, "");
		}


		/** 从文件中读取excel值组(只读sheet1)(将不同类型的都转为String) */
		public static string[][] readFileForExcelFirstSheet(string path)
		{
			try
			{
				FileStream stream = File.Open(path, FileMode.Open, FileAccess.Read, FileShare.ReadWrite);
				IExcelDataReader excelReader = ExcelReaderFactory.CreateOpenXmlReader(stream);

				DataSet result = excelReader.AsDataSet();

				DataTable table=result.Tables[0];

				int columns = table.Columns.Count;
				int rows = table.Rows.Count;

				string[][] re=new string[rows][];

				for (int i = 0; i < rows; ++i)
				{
					DataRow row=table.Rows[i];

					string[] arr=new string[columns];

					for(int j=0;j<columns;++j)
					{
						arr[j]=row[j].ToString();
					}

					re[i]=arr;
				}

				return re;
			}
			catch (Exception e)
			{
				Ctrl.errorLog(e);
			}

			return null;
		}
	}
}