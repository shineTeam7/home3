package com.home.shine.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.home.shine.bytes.BytesReadStream;
import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.XML;

/** 文件方法 */
public class FileUtils
{
	/** BOM头 0xEF 0xBB 0xBF*/
	private static final byte[] UTF_8_BOM = new byte[]{-17, -69, -65};
	/** 获取文件扩展名 */
	public static String getFileExName(String name)
	{
		return name.substring(name.lastIndexOf('.') + 1,name.length()).toLowerCase();
	}
	
	/** 返回文件前名 */
	public static String getFileFrontName(String name)
	{
		return name.substring(0,name.lastIndexOf('.'));
	}
	
	/** 获取文件最后修改日期 */
	public static long getFileLastModified(File file)
	{
		//fix:兼容mac系统，IDE调试，获取出后三位为0的情况
		return (file.lastModified()/1000)*1000;
	}
	
	/** 返回一个路径下的所有子文件,不包括文件夹,exName:是否制定扩展名 */
	public static List<File> getDeepFileList(String path,String... exName)
	{
		List<File> list=new ArrayList<>();
		
		File f=new File(path);
		
		if(!f.exists())
		{
			return list;
		}
		
		toGetDeepFileList(list,f,false,exName);
		
		return list;
	}
	
	private static void toGetDeepFileList(List<File> list,File file,boolean hasDic,String[] exName)
	{
		File[] files=file.listFiles();
		
		if(files!=null)
		{
			for(File f : files)
			{
				String fName=f.getName();
				
				if(fName.startsWith("~"))
					continue;
				
				if(fName.startsWith("."))
					continue;
				
				if(f.isDirectory())
				{
					if(hasDic)
					{
						list.add(f);
					}
					
					toGetDeepFileList(list,f,hasDic,exName);
				}
				else
				{
					if(exName.length==0)
					{
						list.add(f);
					}
					else
					{
						for(String v : exName)
						{
							if(v.equals(getFileExName(fName)))
							{
								list.add(f);
							}
						}
					}
				}
			}
		}
	}
	
	/** 获取文件本地路径 */
	public static String getNativePath(File f)
	{
		String re="";
		
		try
		{
			re=f.getCanonicalPath();
			
		}
		catch(IOException e)
		{
			Ctrl.errorLog(e);
		}
		
		return re;
	}
	
	/** 获取两文件的目录差(不在同级返回"") */
	public static String getRelativePath(File dic,File file)
	{
		try
		{
			String fPath=file.getCanonicalPath();
			String dicPath=dic.getCanonicalPath();
			
			if(fPath.startsWith(dicPath))
			{
				return fPath.substring(dicPath.length(),fPath.length());
			}
			
		}
		catch(IOException e)
		{
			Ctrl.errorLog(e);
		}
		
		return "";
	}
	
	/** 清空某路径 */
	public static void clearDir(String path)
	{
		clearDir(path,true);
	}
	
	/** 清空某路径 */
	public static void clearDir(String path,boolean isDeep)
	{
		File dir=new File(path);
		
		if(dir.exists() && dir.isDirectory())
		{
			toClearDir(dir,isDeep);
		}
	}
	
	private static void toClearDir(File dir,boolean isDeep)
	{
		File[] files=dir.listFiles();
		
		if(files!=null)
		{
			for(File f : files)
			{
				if(isDeep && f.isDirectory())
				{
					toClearDir(dir,isDeep);
				}
				
				f.delete();
			}
		}
	}
	
	/** 删除斍 */
	public static void deleteFile(String path)
	{
		File file=new File(path);
		
		if(file.exists())
		{
			file.delete();
		}
	}
	
	//	/** 将一个文件从一个目录放置到零一个目录中(保持目录结构)(返回结果path) */
	//	public static String changeFileDic(File file,File fromDic,File toDic)
	//	{
	//		try
	//		{
	//			String fPath=file.getCanonicalPath();
	//			String fromDicPath=fromDic.getCanonicalPath();
	//
	//			if(fPath.startsWith(fromDicPath))
	//			{
	//				String temp=fPath.substring(fromDicPath.length(),fPath.length());
	//
	//
	//				return fPath.substring(dicPath.length(),fPath.length());
	//			}
	//
	//		}
	//		catch(IOException e)
	//		{
	//			e.printStackTrace();
	//		}
	//
	//		return "";
	//	}
	
	/** 读取二进制文件(可从网络加载) */
	public static byte[] loadFileForBytes(String path)
	{
		boolean net=false;
		
		if(path.length() >= 4 && path.startsWith("http"))
		{
			net=true;
		}
		
		byte[] bytes=null;
		
		int len=0;
		
		if(net)
		{
			URL url=null;
			
			try
			{
				url=new URL(path);
			}
			catch(MalformedURLException e)
			{
				Ctrl.errorLog("创建URL失败" + path);
				return bytes;
			}
			
			URLConnection urlConnection=null;
			
			try
			{
				urlConnection=url.openConnection();
			}
			catch(IOException e)
			{
				Ctrl.errorLog("获取URL连接失败" + path);
				return bytes;
			}
			
			InputStream inputStream=null;
			
			try
			{
				inputStream=urlConnection.getInputStream();
			}
			catch(IOException e)
			{
				Ctrl.errorLog("获取URL输入流失败" + path);
				return bytes;
			}
			
			try
			{
				len=inputStream.available();
			}
			catch(IOException e1)
			{
				Ctrl.errorLog("获取输入流长度失败" + path);
				return bytes;
			}
			
			bytes=new byte[len];
			
			try
			{
				inputStream.read(bytes);
			}
			catch(IOException e)
			{
				Ctrl.errorLog("输入流读取失败" + path);
				return bytes;
			}
			
			try
			{
				inputStream.close();
				
			}
			catch(IOException e)
			{
				Ctrl.errorLog("关闭输入流失败" + path);
			}
		}
		else
		{
			return readFileForBytes(path);
		}
		
		return bytes;
	}

	/** 从文件中读取字节 */
	public static FileInputStream readFileForInputStream(String path)
	{
		FileInputStream fileInputStream=null;

		try
		{
			fileInputStream=new FileInputStream(path);
		}
		catch(FileNotFoundException e)
		{
			//			Ctrl.throwError("创建文件输入流失败"+path);
		}

		return fileInputStream;
	}

	/** 从文件中读取字节 */
	public static byte[] readFileForBytes(String path)
	{
		byte[] bytes=null;
		
		int len=0;
		
		FileInputStream fileInputStream=readFileForInputStream(path);
		
 		if(fileInputStream==null)
			return bytes;

		boolean isError=false;
		
		try
		{
			len=fileInputStream.available();
		}
		catch(IOException e)
		{
			Ctrl.errorLog("创建文件输入流长度失败" + path);
			isError=true;
		}
		
		if(!isError)
		{
			bytes=new byte[len];
			
			try
			{
				fileInputStream.read(bytes);
			}
			catch(IOException e)
			{
				Ctrl.errorLog("文件输入流读取失败" + path);
			}
		}
		
		try
		{
			fileInputStream.close();
		}
		catch(IOException e)
		{
			Ctrl.errorLog("关闭输入流失败" + path);
		}
		
		return bytes;
	}
	
	/** 将写流写进文件(off-length) */
	public static void writeFileForBytesWriteStream(String path,BytesWriteStream stream)
	{
		writeFileForBytes(path,stream.getBuf(),0,stream.length());
	}
	
	/** 写二进制文件 */
	public static void writeFileForBytes(String path,byte[] bytes)
	{
		writeFileForBytes(path,bytes,0,bytes.length);
	}
	
	/** 写二进制文件 */
	public static void writeFileForBytes(String path,byte[] bytes,int off,int len)
	{
		File f=new File(path);
		
		if(!f.exists())
		{
			if(!f.getParentFile().exists())
			{
				f.getParentFile().mkdirs();
			}
			
			try
			{
				f.createNewFile();
				
			}
			catch(IOException e)
			{
				Ctrl.errorLog(e);
			}
		}
		
		try
		{
			FileOutputStream stream=new FileOutputStream(f);
			stream.write(bytes,off,len);
			stream.close();
		}
		catch(Exception e)
		{
			Ctrl.errorLog(e);
		}
	}
	
	/** 读取UTF文件(没有返回空字符串) */
	public static String readFileForUTF(String path)
	{
		byte[] bytes=readFileForBytes(path);
		
		if(bytes==null)
		{
			return "";
		}

		String re=new String(bytes,BytesUtils.UTFCharset);
		
		return re;
	}

	/** 读取UTF文件，并去掉bom头(没有返回空字符串) */
	public static String readFileForUTFWithNoBom(String path)
	{
		byte[] bytes = readFileForBytes(path);

		if(bytes == null)
		{
			return "";
		}

		//带Bom头
		if(bytes[0] == UTF_8_BOM[0] && bytes[1] == UTF_8_BOM[1] && bytes[2] == UTF_8_BOM[2])
		{
			byte[] temp = new byte[bytes.length - UTF_8_BOM.length];
			System.arraycopy(bytes, UTF_8_BOM.length, temp, 0, bytes.length - UTF_8_BOM.length);
			bytes = temp;
		}

		return new String(bytes,BytesUtils.UTFCharset);
	}
	
	/** 写UTF文件 */
	public static void writeFileForUTF(String path,String str)
	{
		byte[] bytes=str.getBytes(BytesUtils.UTFCharset);
		
		writeFileForBytes(path,bytes);
	}
	
	/** 读取XML */
	public static XML readFileForXML(String path)
	{
		String xmlStr=readFileForUTFWithNoBom(path);
		
		return readXML(xmlStr);
	}
	
	/** 写XML文件 */
	public static void writeFileForXML(String path,XML xml)
	{
		writeFileForUTF(path,xml.toString());
	}
	
	/** 解析xml */
	public static XML readXML(String content)
	{
		if(content==null || content.isEmpty())
		{
			return null;
		}
		
		StringReader sr=new StringReader(content);
		InputSource is=new InputSource(sr);
		DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
		DocumentBuilder builder=null;
		try
		{
			builder=factory.newDocumentBuilder();
		}
		catch(ParserConfigurationException e)
		{
			Ctrl.errorLog("创建文档builder失败");
			return null;
		}
		
		Document doc=null;
		
		try
		{
			doc=builder.parse(is);
		}
		catch(Exception e)
		{
			Ctrl.errorLog("解析文档失败");
			return null;
		}
		
		return XML.readXMLByDocument(doc);
	}
	
	/** 从文件读出字节读流 */
	public static BytesReadStream readFileForBytesReadStream(String path)
	{
		byte[] bytes=readFileForBytes(path);
		if(bytes==null)
			return null;
		
		return BytesReadStream.create(readFileForBytes(path));
	}
	
	/** 修复路径(把所有的\变成/) */
	public static String fixPath(String path)
	{
		if(path==null)
		{
			return "";
		}
		
		return path.replace('\\','/');
	}
	
	/** 修复路径(把所有的\变成/)(根据文件系统格式选择分隔符) */
	public static String fixPath2(String path)
	{
		return path.replace('/',File.separatorChar);
	}
	
	/** 获取某路径的代码跟路径(结尾不带'/') */
	public static String getCodeRoot(String path)
	{
		File f=new File(path);
		
		boolean isDic=path.indexOf('.')==-1;
		
		String pp=null;
		
		try
		{
			pp=f.getCanonicalPath();
		}
		catch(IOException e)
		{
			Ctrl.errorLog(e);
		}
		
		pp=fixPath(pp);
		
		if(!isDic)
		{
			pp=pp.substring(0,pp.length() - f.getName().length());
		}
		
		String find="/src/main/java/";
		
		int index=pp.lastIndexOf(find);
		
		if(index!=-1)
		{
			pp=pp.substring(0,index + find.length());
		}
		else
		{
			find="/src/";
			index=pp.lastIndexOf(find);
			
			if(index!=-1)
			{
				pp=pp.substring(0,index + find.length());
			}
			else
			{
				return "";
			}
		}
		
		if(pp.charAt(pp.length() - 1)=='/')
		{
			pp=pp.substring(0,pp.length() - 1);
		}
		
		return pp;
	}
	
	/** 获取某路径对应的包 */
	public static String getPathPackage(String path)
	{
		File f=new File(path);
		
		String pp=null;
		
		try
		{
			pp=f.getCanonicalPath();
		}
		catch(IOException e)
		{
			Ctrl.errorLog(e);
		}
		
		pp=fixPath(pp);
		
		int aIndex=pp.lastIndexOf('/');
		int bIndex=pp.lastIndexOf('.');
		
		//是文件
		if(aIndex!=-1 && bIndex!=-1 && bIndex>aIndex)
		{
			pp=pp.substring(0,aIndex);
		}
		
		String find="/src/main/java/";
		
		int index=pp.lastIndexOf(find);
		
		if(index!=-1)
		{
			pp=pp.substring(index + find.length(),pp.length());
		}
		else
		{
			find="/src/";
			index=pp.lastIndexOf(find);
			
			if(index!=-1)
			{
				pp=pp.substring(index + find.length(),pp.length());
			}
			else
			{
				return "";
			}
		}
		
		if(pp.charAt(pp.length() - 1)=='/')
		{
			pp=pp.substring(0,pp.length() - 1);
		}
		
		pp=pp.replace('/','.');
		
		return pp;
	}
	
	/** 获取路径对应的QName */
	public static String getPathQName(String path)
	{
		String pp=fixPath(path);
		String fName=getFileFrontName(pp=pp.substring(pp.lastIndexOf("/") + 1));
		
		return getPathPackage(path) + "." + fName;
	}
	
	public static File getRenameFile(File file,String newName)
	{
		String path=file.getPath();
		String name=file.getName();
		return new File(path.substring(0,path.length()-name.length())+newName+"."+getFileExName(name));
	}
	
	/** 拷贝文件 */
	public static void copyFile(File file,File targetFile)
	{
		try
		{
			Files.copy(file.toPath(),targetFile.toPath(),StandardCopyOption.REPLACE_EXISTING);
		}
		catch(Exception e)
		{
			Ctrl.errorLog("拷贝文件失败",e);
		}
	}
	
	public int compareFile(File file1,File file2)
	{
		return file1.getName().compareTo(file2.getName());
	}
}
