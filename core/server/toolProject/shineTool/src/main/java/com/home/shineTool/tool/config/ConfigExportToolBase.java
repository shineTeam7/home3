package com.home.shineTool.tool.config;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.SList;
import com.home.shine.support.collection.SSet;
import com.home.shine.utils.StringUtils;
import com.home.shineTool.constlist.CodeType;
import com.home.shineTool.dataEx.ConfigClassData;
import com.home.shineTool.reflect.FieldInfo;
import com.home.shineTool.reflect.cls.ClassInfo;
import com.home.shineTool.tool.FileRecordTool;
import com.home.shineTool.tool.base.RecordDataClassInfo;
import com.home.shineTool.tool.data.DataExportTool;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConfigExportToolBase extends DataExportTool
{
	protected boolean _isH;
	
	protected ConfigMakeTool _makeTool;
	
	protected ConfigExportToolBase _parentTool;
	
	protected Map<String,ConfigClassData> _clsDic=new HashMap<>();
	
	protected Map<String,RecordDataClassInfo> _clientRecordClsDic=new HashMap<>();
	protected Map<String,RecordDataClassInfo> _serverRecordClsDic=new HashMap<>();
	
	/** 必须组(剩余部分) */
	protected SSet<String> _necessaryDic=new SSet<>();
	
	/** key:fileName */
	protected SSet<String> _clientSets;
	protected String _clientPath;
	protected int _clientCode;
	protected List<File> _clientFileList;
	protected List<File> _clientInputFileList;
	protected List<String> _clientUnusedList;
	
	/** key:fileName */
	protected SSet<String> _serverSets;
	protected String _serverPath;
	protected int _serverCode;
	protected List<File> _serverFileList;
	protected List<File> _serverInputFileList;
	protected List<String> _serverUnusedList;
	
	//temp
	protected boolean _isClient=false;
	protected ConfigClassData _data;
	
	protected Set<String> _superFields;
	
	protected SSet<File> _deleteLastFileDic=new SSet<>();
	
	public ConfigExportToolBase(ConfigMakeTool tool)
	{
		_makeTool=tool;
		
		_isConfigTool=true;
	}
	
	public void setConfigInput(FileRecordTool record,String inputPath)
	{
		setFileRecordTool(record);
		
		setInput(inputPath,"",null,CodeType.Java);
		
		_mark="";
		_inputRootPackge="";
	}
	
	public void setNececcaryDic(SSet<String> dic)
	{
		_necessaryDic=dic;
	}
	
	public void setClient(SList<ConfigClassData> inputs,String path,int codeType)
	{
		_clientPath=path;
		_clientCode=codeType;
		
		_clientFileList=new ArrayList<>();
		_clientInputFileList=new ArrayList<>();
		_clientSets=new SSet<>();
		_clientUnusedList=new ArrayList<>();
		
		for(ConfigClassData data : inputs)
		{
			_clientFileList.add(data.file);
			
			if(data.hasNewCField)
			{
				_clientInputFileList.add(data.file);
			}
			else
			{
				_clientUnusedList.add(data.fName);
			}
			
			_clientSets.add(data.file.getName());
			_clsDic.put(data.file.getName(),data);
		}
	}
	
	public void setServer(SList<ConfigClassData> inputs,String path,int codeType)
	{
		_serverPath=path;
		_serverCode=codeType;
		
		_serverFileList=new ArrayList<>();
		_serverInputFileList=new ArrayList<>();
		_serverSets=new SSet<>();
		_serverUnusedList=new ArrayList<>();
		
		for(ConfigClassData data : inputs)
		{
			_serverFileList.add(data.file);
			
			if(data.hasNewSField)
			{
				_serverInputFileList.add(data.file);
			}
			else
			{
				_serverUnusedList.add(data.fName);
			}
			
			_serverSets.add(data.file.getName());
			_clsDic.put(data.file.getName(),data);
		}
	}
	
	//protected String getCName(String name)
	//{
	//	if(!name.endsWith(_mark))
	//	{
	//		return StringUtils.ucWord(name);
	//	}
	//
	//	return StringUtils.ucWord(name.substring(0,name.length() - _mark.length()));
	//}
	
	protected void recordFiles()
	{
		//先删旧的
		for(File v : _deleteLastFileDic)
		{
			//Ctrl.print("删除一个",v.getName());
			_fileRecord.removePath(v.getPath());
		}
		
		SSet<String> dids=new SSet<>();
		
		for(File f : _serverFileList)
		{
			if(!dids.contains(f.getName()))
			{
				dids.add(f.getName());
				realAddRecord(f);
			}
		}
		
		for(File f : _clientFileList)
		{
			if(!dids.contains(f.getName()))
			{
				dids.add(f.getName());
				realAddRecord(f);
			}
		}
	}
	
	@Override
	public String getCNameByFile(File file)
	{
		return _makeTool.useNameToUpperName(_makeTool.getUseName(file));
	}
	
	@Override
	protected void addRecord(File f)
	{
		if(_isClient)
		{
			_clientRecordClsDic.put(f.getName(),_recordCls);
		}
		else
		{
			_serverRecordClsDic.put(f.getName(),_recordCls);
		}
	}
	
	@Override
	protected void preExecute()
	{
		super.preExecute();
	}
	
	/** 添加记录 */
	public void realAddRecord(File f)
	{
		ConfigRecordInfo info=new ConfigRecordInfo();
		info.isH=_isH;
		
		if(_clientSets.contains(f.getName()))
			info.hasClient=true;
		if(_serverSets.contains(f.getName()))
			info.hasServer=true;
		
		String useName=_makeTool.getUseName(f);
		
		info.isNecessary=_makeTool.getNecessarySet().contains(useName);
		
		if(_makeTool.getHClientNewFiles().contains(useName))
			info.isHClientNewFile=true;
		if(_makeTool.getHServerNewFiles().contains(useName))
			info.isHServerNewFile=true;
		
		if(_makeTool.getHClientNewClassFilesSet().contains(useName))
			info.isHClientNewClass=true;
		if(_makeTool.getHServerNewClassFilesSet().contains(useName))
			info.isHServerNewClass=true;
		
		ConfigClassData data=_clsDic.get(f.getName());
		
		String recordStr="";
		
		if(data!=null)
		{
			info.clientClassMD5=StringUtils.md5(getClassWriteInfo(data.clientCls));
			info.serverClassMD5=StringUtils.md5(getClassWriteInfo(data.serverCls));
			
			RecordDataClassInfo clientRecordCls=_clientRecordClsDic.get(f.getName());
			RecordDataClassInfo serverRecordCls=_serverRecordClsDic.get(f.getName());
			
			String clientRecord=clientRecordCls!=null ? clientRecordCls.toString() : "";
			String serverRecord=serverRecordCls!=null ? serverRecordCls.toString() : "";
			
			recordStr=clientRecord+"%"+serverRecord;
		}
		else
		{
			//枚举类表
		}
		
		makeFileConstClsName(f,info);
		
		//key
		info.configType=_makeTool.getConfigKeyDic().get(useName);
		info.projectType=_makeTool.getConfigProjectTypeDic().get(useName);
		
		_fileRecord.addFile(f,recordStr,info.writeToString());
	}
	
	/** 获取类的写入信息 */
	protected String getClassWriteInfo(ClassInfo cls)
	{
		StringBuilder sb=new StringBuilder();
		
		for(String str : cls.getFieldNameList())
		{
			FieldInfo field=cls.getField(str);
			
			sb.append(field.name);
			sb.append(":");
			sb.append(field.type);
			sb.append(";");
		}
		
		return sb.toString();
	}
	
	/** 构造文件常量类名 */
	protected void makeFileConstClsName(File f,ConfigRecordInfo info)
	{
	
	}
	
	@Override
	protected String getRecordEx(File f)
	{
		String ex=_fileRecord.getFileEx(f);
		
		if(ex.isEmpty())
			return "";
		
		String[] arr=ex.split("%");
		
		return _isClient ? (arr.length>0 ? arr[0] : "") : (arr.length>1 ? arr[1] : "");
	}
	
	@Override
	protected void removeLastFile(File file)
	{
		_deleteLastFileDic.add(file);
	}
	
	protected String getParentClsQName(ConfigExportToolBase tool)
	{
		String outputPath=getOutPathFromParent(tool,_outputInfo.group,_inputFile);
		
		ClassInfo superCls=ClassInfo.getSimpleClassInfoFromPath(outputPath);
		
		if(superCls==null)
		{
			ConfigExportToolBase parentTool=tool._parentTool;
			
			if(parentTool==null)
			{
				Ctrl.throwError("出现找不到基类的情况，可能是c层表只有主键没有字段,\n" +
						"如果是需要先在c层表首行F列写1来标记占位:",outputPath);
				return "";
			}
			else
			{
				return getParentClsQName(parentTool);
			}
		}
		else
		{
			return superCls.getQName();
		}
	}
}
