package com.home.shineTool.tool;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.XML;
import com.home.shine.utils.FileUtils;
import com.home.shine.utils.ObjectUtils;
import com.home.shine.utils.StringUtils;
import com.home.shineTool.dataEx.FileRecordData;
import com.home.shineTool.global.ShineToolSetting;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** 文件记录工具 */
public class FileRecordTool
{
	private File _file;
	
	private Map<String,FileRecordData> _dic=new HashMap<>();
	
	private Set<String> _lastSet=new HashSet<>();
	
	/** 是否启用md5校验 */
	private boolean _useMD5=false;
	
	/** md5缓存(防止多次计算) */
	private Map<String,Integer> _md5Dic=new HashMap<>();
	/** 设定版本 */
	private String _version="";
	
	private boolean _isNew=true;
	
	public FileRecordTool(String path)
	{
		_file=new File(path);
	}
	
	/** 是否启用md5校验 */
	public void setUseMD5(boolean value)
	{
		_useMD5=value;
	}
	
	/** 设定版本 */
	public void setVersion(String version)
	{
		_version=version;
	}
	
	/** 是否新的 */
	public boolean isNew()
	{
		return _isNew;
	}
	
	/** 加载文件记录 */
	public void read()
	{
		_dic.clear();
		_lastSet.clear();
		
		if(_file.exists() && !ShineToolSetting.isAllRefresh)
		{
			XML xml=FileUtils.readFileForXML(_file.getPath());
			
			String oldVersion=xml.getProperty("version");
			
			//版本对
			if(oldVersion.equals(_version))
			{
				for(XML xl : xml.getChildrenByName("info"))
				{
					FileRecordData data=new FileRecordData();
					data.lastModified=Long.parseLong(xl.getProperty("lastModified"));
					
					if(ShineToolSetting.isAll)
					{
						//直接置空,来达到必执行的效果
						data.lastModified=0;
					}
					
					data.length=Long.parseLong(xl.getProperty("length"));
					data.md5=xl.getProperty("md5");
					data.ex=xl.getProperty("ex");
					data.ex2=xl.getProperty("ex2");
					
					String path=xl.getProperty("path");
					
					_dic.put(path,data);
					_lastSet.add(path);
					
					_isNew=false;
				}
			}
		}
	}
	
	/** 写信息到文件 */
	public void write()
	{
		XML xml=new XML();
		xml.setName("infoBase");
		xml.setProperty("version",_version);
		
		for(String k : ObjectUtils.getSortMapKeys(_dic))
		{
			FileRecordData data=_dic.get(k);
			
			XML xl=new XML();
			xl.setName("info");
			xl.setProperty("path",k);
			xl.setProperty("lastModified",String.valueOf(data.lastModified));
			xl.setProperty("length",String.valueOf(data.length));
			
			if(!data.md5.isEmpty())
				xl.setProperty("md5",data.md5);
			
			if(!data.ex.isEmpty())
				xl.setProperty("ex",data.ex);
			
			if(!data.ex2.isEmpty())
				xl.setProperty("ex2",data.ex2);
			
			xml.appendChild(xl);
		}
		
		FileUtils.writeFileForXML(_file.getPath(),xml);
	}
	
	/** 清空md5记录 */
	public void clearMD5Record()
	{
		_md5Dic.clear();
	}
	
	/** 添加文件记录 */
	public void addFile(File f)
	{
		addFile(f,"","");
	}
	
	/** 添加文件记录 */
	public void addFile(File f,String ex)
	{
		addFile(f,ex,"");
	}
	
	public void addFile(File f,String ex,String ex2)
	{
		FileRecordData data=new FileRecordData();
		
		data.lastModified=FileUtils.getFileLastModified(f);
		
		data.length=f.length();
		
		if(_useMD5)
		{
			data.md5=getFileMD5(f);
		}
		
		data.ex=ex;
		data.ex2=ex2;
		
		_dic.put(f.getPath(),data);
	}
	
	private String getFileMD5(File f)
	{
		return StringUtils.md5(FileUtils.readFileForBytes(f.getPath()));
	}
	
	/** 移除记录 */
	public void removePath(String path)
	{
		_dic.remove(path);
	}
	
	/** 是否有某文件的记录 */
	public boolean hasFile(File f)
	{
		return _dic.containsKey(f.getPath());
	}
	
	/** 获取某文件的ex值(默认为为文件的Class信息) */
	public String getFileEx(File f)
	{
		FileRecordData data=_dic.get(f.getPath());
		
		if(data==null)
		{
			return "";
		}
		
		return data.ex;
	}
	
	/** 获取某文件的ex2值 */
	public String getFileEx2(File f)
	{
		FileRecordData data=_dic.get(f.getPath());
		
		if(data==null)
		{
			return "";
		}
		
		return data.ex2;
	}
	
	/** 操作过该文件 */
	public void didFile(File f)
	{
		String path=f.getPath();
		
		_lastSet.remove(path);
	}
	
	/** 修改文件记录 */
	public void modifyFileRecord(File f)
	{
		FileRecordData data=_dic.get(f.getPath());
		
		if(data==null)
			return;
		
		//标记修改
		data.length=0;
		data.lastModified=0;
	}
	
	/** 是否是新文件 */
	public boolean isNewFile(File f)
	{
		if(ShineToolSetting.isAll)
		{
			return true;
		}
		
		String path=f.getPath();
		
		_lastSet.remove(path);
		
		FileRecordData data=_dic.get(path);
		
		if(data==null)
		{
			return true;
		}
		
		if(FileUtils.getFileLastModified(f)!=data.lastModified)
		{
			return true;
		}
		
		if(f.length()!=data.length)
		{
			return true;
		}
		
		if(_useMD5)
		{
			if(!getFileMD5(f).equals(data.md5))
			{
				return true;
			}
		}
		
		return false;
	}
	
	/** 剩余键组 */
	public Set<String> getLastSet()
	{
		return _lastSet;
	}
	
	/** 删除lastSet中的文件 */
	public void deleteLastFiles(boolean needFileDelete)
	{
		for(String k : _lastSet)
		{
			if(needFileDelete)
			{
				File f=new File(k);
				
				if(f.exists())
				{
					f.delete();
				}
			}
			
			Ctrl.warnLog("删除无用路径",k);
			removePath(k);
		}
	}
	
	
	/** 获取Record字典 */
	public Map<String,FileRecordData> getRecordDic()
	{
		return _dic;
	}
}
