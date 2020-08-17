package com.home.shineTool.tool.config;

import com.home.shine.bytes.BytesReadStream;
import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineGlobal;
import com.home.shine.support.collection.IntList;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.SList;
import com.home.shine.support.collection.SMap;
import com.home.shine.support.collection.SSet;
import com.home.shine.support.collection.StringIntMap;
import com.home.shine.support.func.ObjectCall;
import com.home.shine.utils.FileUtils;
import com.home.shine.utils.MathUtils;
import com.home.shine.utils.StringUtils;
import com.home.shineTool.constlist.CodeType;
import com.home.shineTool.constlist.ConfigDicType;
import com.home.shineTool.constlist.ConfigFieldMarkType;
import com.home.shineTool.constlist.ConfigFieldType;
import com.home.shineTool.constlist.DataGroupType;
import com.home.shineTool.constlist.ExecuteReleaseType;
import com.home.shineTool.constlist.ProjectType;
import com.home.shineTool.constlist.VarType;
import com.home.shineTool.constlist.VisitType;
import com.home.shineTool.dataEx.ConfigClassData;
import com.home.shineTool.dataEx.ConfigFieldData;
import com.home.shineTool.dataEx.ConfigFieldKeyData;
import com.home.shineTool.global.ShineToolGlobal;
import com.home.shineTool.reflect.FieldInfo;
import com.home.shineTool.reflect.cls.ClassInfo;
import com.home.shineTool.reflect.code.CodeInfo;
import com.home.shineTool.tool.FileRecordTool;
import com.home.shineTool.global.ShineToolSetting;
import com.home.shineTool.tool.data.DataDefineTool;
import com.home.shineTool.utils.ToolFileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 配置导出工具 */
public abstract class ConfigMakeTool
{
	public static String[] _arrSplitChars=new String[]{",",";","\\&","\\|"};
	
	public static String _dIntSplitChar=":";
	
	/** 用来做refEnum类型的适配 */
	private static IntList _enumTypeList;
	
	/** 起始行号 */
	private static int _startRow=5;
	/** 使用数组限制 */
	public static int useArrLength=512;
	
	/** 工程类型 */
	protected int _projectType;
	/** 工程大写前缀 */
	protected String _projectUFront="";
	/** 工程大写前缀 */
	protected String _projectLFront="";
	
	/** 工程差异名客户端 */
	private String _clientProjectDifName="";
	/** 工程差异名服务器 */
	private String _serverProjectDifName="";
	
	/** 表前缀 */
	private String _configFront;
	/** 父工具 */
	private ConfigMakeTool _parentTool;
	/** 子工具 */
	private ConfigMakeTool _childTool;
	
	/** h导出工具 */
	private HConfigExportTool _hExportTool;
	/** v导出工具 */
	private VConfigExportTool _vExportTool;
	
	
	/** 文件记录 */
	private FileRecordTool _record;
	/** 输入目录 */
	private String _inputPath;
	/** 服务器输出目录 */
	private String _serverOutPath;
	/** 服务器输出temp目录 */
	private String _serverOutTempPath;
	/** 服务器代码输出目录 */
	private String _serverCodePath;
	/** 客户端输出目录 */
	private String _clientOutPath;
	/** 客户端输出目录 */
	private String _clientOutTempPath;
	/** 客户端类输出目录 */
	private String _clientCodePath;
	
	/** 服务器输出temp热更目录 */
	private String _serverOutTempHotfixPath;
	
	//cls
	protected int _clientCode;
	protected int _serverCode;
	
	/** 源码类型 */
	private int _sourceCode=CodeType.Java;
	
	/** 完整 */
	protected SList<String> _wholeWordList=new SList<>(k->new String[k]);
	
	/** 工具所需配置组 */
	protected SSet<String> _editorNeedSet=new SSet<>();
	
	//one
	
	
	
	/** h表数据组(keyName) */
	private SMap<String,HConfigValueData> _hConfigValueDic=new SMap<>();
	/** h表获取记录组 */
	private SSet<String> _hConfigValueGotSet=new SSet<>();
	
	/** v表数据组 */
	private SMap<String,VConfigValueData> _vConfigValueDic=new SMap<>();
	/** v表获取记录组 */
	private SSet<String> _vConfigValueGotSet=new SSet<>();
	
	
	/** 执行过的文件组(useName) */
	private SSet<String> _didFiles=new SSet<>();
	
	/** 全部文件组(不包括parent)(useName) */
	private SSet<String> _allFiles=new SSet<>();
	/** 全部文件组(包括parent)(useName) */
	private SSet<String> _allFilesTotal;
	
	/** 全部新增文件组useName) */
	private SSet<String> _hAllNewFilesSet=new SSet<>();
	
	//横表
	/** 全部文件 */
	private SSet<String> _hClientAllFiles;
	/** 新增文件(useName)(比新增类要少) */
	private SList<String> _hClientNewFiles=new SList<>();
	private SSet<String> _hClientNewFilesSet=new SSet<>();
	/** 新类文件(有新增字段的)(useName) */
	private SList<String> _hClientNewClassFiles;
	private SSet<String> _hClientNewClassFilesSet=new SSet<>();
	private SList<ConfigClassData> _hClientInputList=new SList<>();
	
	private SSet<String> _hServerAllFiles;
	private SList<String> _hServerNewFiles=new SList<>();
	private SSet<String> _hServerNewFilesSet=new SSet<>();
	private SList<String> _hServerNewClassFiles;
	private SSet<String> _hServerNewClassFilesSet=new SSet<>();
	private SList<ConfigClassData> _hServerInputList=new SList<>();
	/** 不在s和不在c的横表 */
	private List<File> _hLastFiles=new ArrayList<>();
	
	//必须组
	/** useName为key */
	private SSet<String> _necessarySet;
	/** keyName为key */
	private SSet<String> _necessaryKeySet;
	
	/** 配置索引字典(key:useName) */
	private StringIntMap _configKeyDic;
	/** 配置工程字典(key:useName) */
	private StringIntMap _configProjectTypeDic;
	
	//纵表
	private SList<String> _vClientAllFiles=new SList<>();
	private SList<ConfigClassData> _vClientInputList=new SList<>();
	
	private SList<String> _vServerAllFiles=new SList<>();
	private SList<ConfigClassData> _vServerInputList=new SList<>();
	
	//自定义数据
	/** 自定义数据(客户端) */
	protected SList<String> _customClientFiles=new SList<>();
	/** 自定义数据(服务器) */
	protected SList<String> _customServerFiles=new SList<>();
	/** 自定义数据描述 */
	protected SMap<String,String> _customDescribes=new SMap<>();
	/** 自定义数据描述 */
	protected SSet<String> _customNeedSplit=new SSet<>();
	
	/** 配置版本字典(旧) */
	private SMap<String,ConfigVersionInfo> _oldVersionDic=new SMap<>();
	/** 配置版本字典(新) */
	private SMap<String,ConfigVersionInfo> _newVersionDic=new SMap<>();
	
	//h生成部分
	protected boolean _isHClient;
	
	/** h表缓存数据 */
	protected HConfigValueData _hData;
	
	protected int _codeType;
	protected CodeInfo _cCode;
	
	//版本号
	//代码版本号
	private String _clientCodeMD5;
	private String _serverCodeMD5;
	private int _clientCodeVersion;
	private int _serverCodeVersion;
	
	//enumMake
	
	protected String _constClsPath;
	protected ClassInfo _constCls;
	
	
	private int _maxID=-1;
	
	private boolean _useArrConst=false;
	private boolean _isStringKey=false;
	/** 构造Const组 */
	private boolean _makingSomeConst=false;
	
	/** 枚举类名查询字典 */
	private SMap<String,String> _clientConstNameDic=new SMap<>();
	private SMap<String,String> _serverConstNameDic=new SMap<>();
	private SMap<String,String> _clientConstNameTotalDic;
	private SMap<String,String> _serverConstNameTotalDic;
	
	protected int _start;
	
	protected int _configLen;
	
	private int _nowStart;
	
	/** 优先级列表 */
	private SList<String> _priorityList;
	
	//temp
	private HConfigValueData _currentHConfigData;
	private ConfigFieldData _currentField;
	private int _currentRow;
	private String _currentValue="";
	
	public ConfigMakeTool()
	{
		//sides=new SideInfo[2];
		//(sides[0]=new SideInfo()).index=0;
		//(sides[1]=new SideInfo()).index=1;
		
		_configLen=500;
		
		_enumTypeList=new IntList();
		_enumTypeList.add(ConfigFieldType.Array);
		_enumTypeList.add(ConfigFieldType.Array);
		_enumTypeList.add(ConfigFieldType.Int);
		
		_priorityList=new SList<>();
		//国际化
		_priorityList.add("language");
		//全局表
		_priorityList.add("global");
		//大数阶
		_priorityList.add("bigFloatRank");
		//技能影响
		_priorityList.add("skillInfluenceType");
		
	}
	
	//public void foreachSide(ObjectCall<SideInfo> func)
	//{
	//	for(int i=0;i<2;i++)
	//	{
	//		func.apply(sides[i]);
	//	}
	//}
	//
	//public SideInfo getClientSide()
	//{
	//	return sides[0];
	//}
	//
	//public SideInfo getServerSide()
	//{
	//	return sides[1];
	//}
	
	public void addCustom(String useName,String des,boolean needSplit)
	{
		addCustom(useName,des,needSplit,2);
	}
	
	public void addCustom(String useName,String des,boolean needSplit,int type)
	{
		//server
		if(type==0 || type==2)
		{
			_customServerFiles.add(useName);
		}
		
		if(type==1 || type==2)
		{
			if(!needSplit)
			{
				_customClientFiles.add(useName);
			}
		}
		
		_customDescribes.put(useName,des);
		
		//if(needSplit)
		//{
		//	_customNeedSplit.add(useName);
		//}
	}
	
	public void init(int projectType,String inputPath,String serverOutPath,String serverCodePath,int serverCodeType,String clientOutPath,String clientCodePath,int clientCodeType,String recordPath)
	{
		_projectType=projectType;
		
		switch(_projectType)
		{
			case ProjectType.Common:
			{
				_projectUFront="";
				_projectLFront="";
				_configFront="c";
				
				_clientProjectDifName="";
				_serverProjectDifName="";
			}
				break;
			case ProjectType.Game:
			{
				_projectUFront="G";
				_projectLFront="g";
				_configFront="";
				
				_clientProjectDifName="";
				_serverProjectDifName="";
			}
				break;
			case ProjectType.HotFix:
			{
				_projectUFront="H";
				_projectLFront="h";
				_configFront="h";
				
				_clientProjectDifName="";
				_serverProjectDifName="H";
			}
				break;
		}
		
		_inputPath=FileUtils.fixPath(inputPath);
		
		_serverOutPath=FileUtils.fixPath(serverOutPath);
		_serverCodePath=FileUtils.fixPath(serverCodePath);
		_serverCode=serverCodeType;
		
		_clientOutPath=FileUtils.fixPath(clientOutPath);
		_clientCodePath=FileUtils.fixPath(clientCodePath);
		_clientCode=clientCodeType;
		
		_record=new FileRecordTool(recordPath);
		_record.setVersion(ShineToolSetting.configExportVersion);
		
		//临时路径写定
		_serverOutTempPath=ShineToolGlobal.serverTempPath + "/config";
		_clientOutTempPath=ShineToolGlobal.clientTempPath + "/config";
		
		_serverOutTempHotfixPath=ShineToolGlobal.serverTempPath + "/configHotfix";
	}
	
	public void setStart(int value)
	{
		_start=value;
		
		_nowStart=_start;
	}
	
	/** 获取结束值 */
	public int getEnd()
	{
		return _start+_configLen;
		//return _nowStart;
	}
	
	/** 设置父 */
	public void setParentTool(ConfigMakeTool tool)
	{
		_parentTool=tool;
		tool._childTool=this;
	}
	
	/** 获取最下面的工具 */
	public ConfigMakeTool getBottomTool()
	{
		if(_childTool!=null)
			return _childTool.getBottomTool();
		
		return this;
	}
	
	/** 获取最上面的 */
	public ConfigMakeTool getGrandParentTool()
	{
		if(_parentTool!=null)
			return _parentTool.getGrandParentTool();
		
		return this;
	}
	
	/** 获取父工具 */
	public ConfigMakeTool getParentTool()
	{
		return _parentTool;
	}
	
	public ConfigMakeTool getChildTool()
	{
		return _childTool;
	}
	
	/** 工程类型 */
	public int getProjectType()
	{
		return _projectType;
	}
	
	/** 是否main工程 */
	public boolean isCommonProject()
	{
		return _projectType==ProjectType.Common;
	}
	
	/** 是否main工程 */
	public boolean isMainProject()
	{
		return _projectType==ProjectType.Common || _projectType==ProjectType.Game;
	}

	/** 是否game工程 */
	public boolean isLastProject()
	{
		return ProjectType.isLastProject(_projectType);
	}
	
	/** 工程U前缀 */
	public String getProjectUFront()
	{
		return _projectUFront;
	}
	
	/** 工程L前缀 */
	public String getProjectLFront()
	{
		return _projectLFront;
	}
	
	public String getClientProjectDifName()
	{
		return _clientProjectDifName;
	}
	
	public String getServerProjectDifName()
	{
		return _serverProjectDifName;
	}
	
	public HConfigExportTool getHExportTool()
	{
		return _hExportTool;
	}
	
	public VConfigExportTool getVExportTool()
	{
		return _vExportTool;
	}
	
	public SList<String> getVClientAllFiles()
	{
		return _vClientAllFiles;
	}
	
	public SList<String> getVServerAllFiles()
	{
		return _vServerAllFiles;
	}
	
	public SSet<String> getHAllNewFilesSet()
	{
		return _hAllNewFilesSet;
	}
	
	public SList<String> getHClientNewFiles()
	{
		return _hClientNewFiles;
	}
	
	public SSet<String> getHClientNewFilesSet()
	{
		return _hClientNewFilesSet;
	}
	
	public SList<String> getHServerNewFiles()
	{
		return _hServerNewFiles;
	}
	
	public SSet<String> getHServerNewFilesSet()
	{
		return _hServerNewFilesSet;
	}
	
	public SList<String> getHClientNewClassFiles()
	{
		return _hClientNewClassFiles;
	}
	
	public SSet<String> getHClientNewClassFilesSet()
	{
		return _hClientNewClassFilesSet;
	}
	
	public SList<String> getHServerNewClassFiles()
	{
		return _hServerNewClassFiles;
	}
	
	public SSet<String> getHServerNewClassFilesSet()
	{
		return _hServerNewClassFilesSet;
	}
	
	public StringIntMap getConfigKeyDic()
	{
		return _configKeyDic;
	}
	
	public StringIntMap getConfigProjectTypeDic()
	{
		return _configProjectTypeDic;
	}
	
	public SList<String> getClientCustomFiles()
	{
		return _customClientFiles;
	}
	
	public SList<String> getServerCustomFiles()
	{
		return _customServerFiles;
	}
	
	public SMap<String,String> getCustomDescribes()
	{
		return _customDescribes;
	}
	
	public String useNameToUpperName(String useName)
	{
		String temp=useName.toLowerCase();
		
		String[] values=_wholeWordList.getValues();
		String s;
		for(int i=_wholeWordList.size()-1;i>=0;--i)
		{
			if(temp.startsWith(s=values[i]))
			{
				return s.toUpperCase()+useName.substring(s.length());
			}
		}
		
		return StringUtils.ucWord(useName);
	}
	
	public String upperNameToUseName(String upperName)
	{
		String temp=upperName.toLowerCase();
		
		String[] values=_wholeWordList.getValues();
		String s;
		for(int i=_wholeWordList.size()-1;i>=0;--i)
		{
			if(temp.startsWith(s=values[i]))
			{
				return s+upperName.substring(s.length());
			}
		}
		
		return StringUtils.lcWord(upperName);
	}
	
	/** 获取实际使用表名(单名) */
	public String getUseName(File file)
	{
		String nn=FileUtils.getFileFrontName(file.getName());
		
		if(_configFront.isEmpty())
		{
//			return upperNameToUseName(nn);
			return nn;
		}
		
		if(nn.startsWith(_configFront))
		{
			return upperNameToUseName(nn.substring(1));
		}
		else
		{
			Ctrl.throwError("表缺少前缀:"+_configFront,file.getName());
		}
		
		return nn;
	}
	
	/** 获取实际使用表名(单名) */
	public String getUseNameByKeyName(String name)
	{
		int lastIndex=name.lastIndexOf("/");
		
		if(lastIndex!=-1)
		{
			return name.substring(lastIndex+1);
		}
		
		return name;
	}
	
	/** 获取主键名(单名) */
	protected String getKeyName(File file)
	{
		String path=FileUtils.fixPath(file.getPath());
		
		//
		if(!path.startsWith(_inputPath))
		{
			Ctrl.throwError("居然有不对的");
			return "";
		}
		
		path=path.substring(_inputPath.length()+1);//去"/"
		
		String nn=FileUtils.getFileFrontName(path);
		
		if(_configFront.isEmpty())
		{
			return nn;
		}
		
		int lastIndex=nn.lastIndexOf("/");
		
		if(lastIndex!=-1)
		{
			String temp=nn.substring(lastIndex+1);
			
			if(temp.startsWith(_configFront))
			{
				return nn.substring(0,lastIndex)+"/"+upperNameToUseName(temp.substring(1));
			}
			else
			{
				Ctrl.throwError("表缺少前缀:"+_configFront,file.getName());
			}
		}
		else
		{
			if(nn.startsWith(_configFront))
			{
				return upperNameToUseName(nn.substring(1));
			}
			else
			{
				Ctrl.throwError("表缺少前缀:"+_configFront,file.getName());
			}
		}
		
		return nn;
	}
	
	/** 全部文件执行记录(不包括parent) */
	public SSet<String> getAllFiles()
	{
		return _allFiles;
	}
	
	/** 全部执行文件记录 */
	public SSet<String> getAllFilesTotal()
	{
		return _allFilesTotal;
	}
	
	public SSet<String> getNecessarySet()
	{
		return _necessarySet;
	}
	
	public void foreachParentTool(ObjectCall<ConfigMakeTool> func)
	{
		ConfigMakeTool parentTool=getParentTool();
		
		if(parentTool!=null)
		{
			func.apply(parentTool);
			
			parentTool.foreachParentTool(func);
		}
	}
	
	public void foreachThisAndParentTool(ObjectCall<ConfigMakeTool> func)
	{
		func.apply(this);
		
		foreachParentTool(func);
	}
	
	/** 额外排序 */
	protected void sortEx(SList<String> list)
	{
		list.sort();
		
		for(int i=_priorityList.size()-1;i>=0;--i)
		{
			String name=_priorityList.get(i);
			
			int index;
			
			if((index=list.indexOf(name))!=-1)
			{
				list.remove(index);
				list.insert(0,name);
			}
		}
		
		
	}
	
	/** 执行 */
	public void execute()
	{
		_record.read();
		
		//读取
		if(isLastProject())
		{
			readOldConfigVersion();
		}
		
		_hClientAllFiles=_parentTool!=null ? _parentTool._hClientAllFiles.clone() : new SSet<>();
		_hServerAllFiles=_parentTool!=null ? _parentTool._hServerAllFiles.clone() : new SSet<>();
		
		_allFilesTotal=_parentTool!=null ? _parentTool._allFilesTotal.clone() : new SSet<>();
		_necessarySet=_parentTool!=null ? _parentTool._necessarySet.clone() : new SSet<>();
		_necessaryKeySet=_parentTool!=null ? _parentTool._necessaryKeySet.clone() : new SSet<>();
		
		_clientConstNameTotalDic=_parentTool!=null ? _parentTool._clientConstNameTotalDic.clone() : new SMap<>();
		_serverConstNameTotalDic=_parentTool!=null ? _parentTool._serverConstNameTotalDic.clone() : new SMap<>();
		
		_configKeyDic=_parentTool!=null ? _parentTool._configKeyDic.clone() : new StringIntMap();
		_configProjectTypeDic=_parentTool!=null ? _parentTool._configProjectTypeDic.clone() : new StringIntMap();
		
		SSet<String> nSet=new SSet<>();
		SSet<String> nKeySet=new SSet<>();
		
		//有父
		if(_parentTool!=null)
		{
			for(String v:_parentTool._necessarySet)
			{
				nSet.add(v);
			}
			
			for(String v:_parentTool._necessaryKeySet)
			{
				nKeySet.add(v);
			}
		}
		
		List<File> list=FileUtils.getDeepFileList(_inputPath,"xlsx");
		//list.sort(File::compareTo);
		sortFiles(list);
		
		for(File f : list)
		{
			//xlsx的缓存文件
			if(f.getName().startsWith("~"))
			{
				continue;
			}

			//xlsx的缓存文件mac(wps)
			if(f.getName().startsWith("."))
			{
				continue;
			}
			
			//xlsx的缓存文件mac
			if(f.getName().contains("$"))
			{
				continue;
			}
			
			String useName=getUseName(f);
			String keyName=getKeyName(f);
			
			boolean need=_record.isNewFile(f);
			
			//common有了就再执行
			if(!need)
			{
				//有父
				if(_parentTool!=null && _parentTool._didFiles.contains(useName))
				{
					need=true;
					//修改文件记录
					_record.modifyFileRecord(f);
				}
			}
			
			nSet.remove(useName);
			nKeySet.remove(keyName);
			
			_allFiles.add(useName);
			_allFilesTotal.add(useName);
			
			if(need)
			{
				_didFiles.add(useName);
				
				doOneFile(f);
			}
			else
			{
				String ex2=_record.getFileEx2(f);
				
				if(ex2.isEmpty())
				{
					Ctrl.print("不该为空",f);
				}
				
				ConfigRecordInfo info=new ConfigRecordInfo();
				info.readByString(ex2);
				
				if(ShineToolSetting.configAllNecessary || info.isNecessary)
				{
					_necessarySet.add(useName);
					_necessaryKeySet.add(keyName);
				}
				
				//client
				if(info.hasClient)
				{
					//h
					if(info.isH)
					{
						_hClientAllFiles.add(useName);
						
						if(info.isHClientNewFile)
						{
							_hClientNewFiles.add(useName);
							_hClientNewFilesSet.add(useName);
							_hAllNewFilesSet.add(useName);
						}
						
						if(info.isHClientNewClass)
						{
							_hClientNewClassFilesSet.add(useName);
						}
					}
					else
					{
						_vClientAllFiles.add(useName);
					}
				}
				
				//server
				if(info.hasServer)
				{
					//h
					if(info.isH)
					{
						_hServerAllFiles.add(useName);
						
						if(info.isHServerNewFile)
						{
							_hServerNewFiles.add(useName);
							_hServerNewFilesSet.add(useName);
							_hAllNewFilesSet.add(useName);
						}
						
						if(info.isHServerNewClass)
						{
							_hServerNewClassFilesSet.add(useName);
						}
					}
					else
					{
						_vServerAllFiles.add(useName);
					}
				}
				
				if(info.hasClientConst)
				{
					_clientConstNameDic.put(useName,info.constName);
					_clientConstNameTotalDic.put(useName,info.constName);
				}
				
				if(info.hasServerConst)
				{
					_serverConstNameDic.put(useName,info.constName);
					_serverConstNameTotalDic.put(useName,info.constName);
				}
				
				_configKeyDic.put(useName,info.configType);
				_configProjectTypeDic.put(useName,info.projectType);
			}
		}
		
		//有父
		if(_parentTool!=null)
		{
			for(String v:nKeySet)
			{
				String uName=getUseNameByKeyName(v);
				
				//父执行了,子才执行
				if(_parentTool._didFiles.contains(uName))
				{
					HConfigValueData parentData=_parentTool.getHConfigValueDataFromParent(v);
					
					//需要执行
					if(parentData!=null)
					{
						//标记执行
						_didFiles.add(uName);
						
						doHorizontalConfig(parentData,true);
					}
					else
					{
						Ctrl.throwError("不该找不到",v);
					}
				}
			}
		}
		
		_hClientNewClassFiles=_hClientNewClassFilesSet.getSortedList();
		_hServerNewClassFiles=_hServerNewClassFilesSet.getSortedList();
		
		sortEx(_hClientNewClassFiles);
		sortEx(_hServerNewClassFiles);
		sortEx(_hClientNewFiles);
		sortEx(_hServerNewFiles);
		
		_vClientAllFiles.sort();
		_vServerAllFiles.sort();
		
		//projectRoots[ServerBase],projectRoots[ClientGame]
		
		DataDefineTool define0=new DataDefineTool(_serverCodePath + "/constlist/generate/" + getProjectUFront() + "ConfigType." + CodeType.getExName(_serverCode),_nowStart,_configLen,true);
		DataDefineTool define1=new DataDefineTool(_clientCodePath + "/constlist/generate/" + getProjectUFront() + "ConfigType." + CodeType.getExName(_clientCode),_nowStart,_configLen,false);
		
		//先v
		_vExportTool=new VConfigExportTool(this);
		_vExportTool.setNeedTrace(false);
		
		_vExportTool.setConfigInput(_record,_inputPath);
		_vExportTool.setServer(_vServerInputList,_serverCodePath,_serverCode);
		_vExportTool.setClient(_vClientInputList,_clientCodePath,_clientCode);
		_vExportTool.addDefine(DataGroupType.Server,define0);
		if(ShineToolSetting.needClient)
			_vExportTool.addDefine(DataGroupType.Client,define1);
		_vExportTool.executeConfig();
		

		//后h
		_hExportTool=new HConfigExportTool(this);
		_hExportTool.setNeedTrace(false);
		
		_hExportTool.setConfigInput(_record,_inputPath);
		_hExportTool.setNececcaryDic(nSet);
		_hExportTool.setServer(_hServerInputList,_serverCodePath + "/config"+_serverProjectDifName,_serverCode);
		_hExportTool.setClient(_hClientInputList,_clientCodePath + "/config"+_clientProjectDifName,_clientCode);
		_hExportTool.addDefine(DataGroupType.Server,define0);
		if(ShineToolSetting.needClient)
			_hExportTool.addDefine(DataGroupType.Client,define1);
		_hExportTool.executeConfig();
		
		define0.write();
		if(ShineToolSetting.needClient)
			define1.write();
		
		//不属于cs的h表
		for(File f : _hLastFiles)
		{
			//补充添加记录
			_hExportTool.realAddRecord(f);
		}
		
		//版本号
		
		makeCodeVersion(list);
		
		//写出
		if(isLastProject())
		{
			writeCombine();
		}
	}
	
	public void writeRecord()
	{
		_record.write();
	}
	
	private void sortFiles(List<File> list)
	{
		SMap<String,File> dic=new SMap<>(list.size());
		
		for(File f:list)
		{
			dic.put(f.getName(),f);
		}
		
		list.clear();
		
		for(String k:dic.getSortedKeyList())
		{
			list.add(dic.get(k));
		}
	}
	
	private void readOldConfigVersion()
	{
		_oldVersionDic.clear();
		
		BytesReadStream stream=FileUtils.readFileForBytesReadStream(ShineToolGlobal.serverConfigRecordPath);
		
		//版本检测
		if(stream!=null && stream.checkVersion(ShineToolGlobal.serverConfigRecordVersion))
		{
			int len=stream.readLen();
			
			for(int i=0;i<len;i++)
			{
				ConfigVersionInfo info=new ConfigVersionInfo();
				info.readBytes(stream);
				
				_oldVersionDic.put(info.name,info);
			}
		}
	}
	
	private void writeConfigVersion()
	{
		BytesWriteStream stream=new BytesWriteStream();
		stream.writeVersion(ShineToolGlobal.serverConfigRecordVersion);
		
		stream.writeLen(_newVersionDic.size());
		
		for(String k:_newVersionDic.getSortedKeyList())
		{
			_newVersionDic.get(k).writeBytes(stream);
		}
		
		FileUtils.writeFileForBytesWriteStream(ShineToolGlobal.serverConfigRecordPath,stream);
	}
	
	private ConfigVersionInfo getVersionInfo(String name)
	{
		return _newVersionDic.computeIfAbsent(name,k->{
			ConfigVersionInfo info=new ConfigVersionInfo();
			info.name=name;
			return info;
		});
	}
	
	private boolean needConfigVersionDo(String name,boolean isClient)
	{
		ConfigVersionInfo versionInfo=getVersionInfo(name);
		
		ConfigVersionInfo old=_oldVersionDic.get(name);
		
		if(old==null)
			return true;
		
		if(isClient)
		{
			if(!old.clientValueMD5.equals(versionInfo.clientValueMD5))
				return true;
		}
		else
		{
			if(!old.serverValueMD5.equals(versionInfo.serverValueMD5))
				return true;
		}
		
		return false;
	}
	
	private void makeCodeVersion(List<File> list)
	{
		StringBuilder cb=new StringBuilder();
		StringBuilder sb=new StringBuilder();
		
		for(File v:list)
		{
			ConfigRecordInfo info=new ConfigRecordInfo();
			
			info.readByString(_record.getFileEx2(v));
			
			if(!info.clientClassMD5.isEmpty())
				cb.append(info.clientClassMD5);
			
			if(!info.serverClassMD5.isEmpty())
				sb.append(info.serverClassMD5);
		}
		
		_clientCodeMD5=StringUtils.md5(cb.toString());
		_serverCodeMD5=StringUtils.md5(sb.toString());
		
		_serverCodeVersion=makeCodeMD5(_serverCodePath + "/control/" + _projectUFront + "CodeCheckRecord." + CodeType.getExName(_serverCode),_serverCodeMD5,false);
		
		if(ShineToolSetting.needClient)
		{
			_clientCodeVersion=makeCodeMD5(_clientCodePath + "/control/" + _projectUFront + "CodeCheckRecord." + CodeType.getExName(_clientCode),_clientCodeMD5,true);
		}
	}
	
	private int makeCodeMD5(String path,String value,boolean isClient)
	{
		ClassInfo clientRootCls=ClassInfo.getClassInfoFromPathAbs(path);
		
		FieldInfo field=clientRootCls.getField("configMD5");
		
		String md5='"' + value + '"';
		
		boolean needChange=false;
		
		if(field==null)
		{
			field=new FieldInfo();
			field.visitType=VisitType.Public;
			field.isStatic=true;
			field.isConst=true;
			field.type=clientRootCls.getCode().String;
			field.name="configMD5";
			field.describe="配置文件代码版本校验值";
			clientRootCls.addField(field);
		}
		else
		{
			needChange=!md5.equals(field.defaultValue);
		}
		
		FieldInfo vField=clientRootCls.getField("configVersion");
		
		int code;
		
		if(vField==null || needChange)
		{
			field.defaultValue=md5;
			
			field=clientRootCls.getField("configVersion");
			
			if(field==null)
			{
				field=new FieldInfo();
				field.visitType=VisitType.Public;
				field.isStatic=true;
				field.isConst=true;
				field.type=clientRootCls.getCode().Int;
				field.name="configVersion";
				field.describe="配置文件代码数据结构版本号";
				clientRootCls.addField(field);
			}
			
			field.defaultValue=String.valueOf(code=MathUtils.getToken());
			
			Ctrl.print((isClient ? "client " : "server ") + getProjectUFront()+"新的配置结构版本号:" + code);
		}
		else
		{
			code=Integer.parseInt(vField.defaultValue);
		}
		
		clientRootCls.write();
		
		return code;
	}
	
	private int valueToInt(String value)
	{
		return (int)valueToDouble(value);
	}
	
	private double valueToDouble(String value)
	{
		if(value.isEmpty())
		{
			return 0.0;
		}
		
		double re=0.0;
		
		try
		{
			re=Double.parseDouble(value);
		}
		catch(Exception e)
		{
			if(_currentField!=null)
			{
				throwCurrentFieldError("数字格式不正确");
			}
			else
			{
				throwCurrentFieldError("无法转化为数字:"+value);
			}
		}
		
		return re;
	}
	
	/** 获取对应的commonFile */
	private File getConfigCommonFile(File f)
	{
		String fPath=FileUtils.getNativePath(f.getParentFile());
		
		fPath=fPath.replace(File.separator + "config",File.separator + "commonConfig");
		
		String fName=f.getName();
		
		fName=FileUtils.getFileFrontName(fName);
		
		fName="c" + useNameToUpperName(fName);
		
		File re=new File(fPath + File.separator + fName + "." + FileUtils.getFileExName(f.getName()));
		
		if(re.exists())
		{
			return re;
		}
		
		return null;
	}
	
	private void doOneFile(File f)
	{
		String keyName=getKeyName(f);
		
		HConfigValueData hData=_hConfigValueDic.get(keyName);
		
		String[][][] values;
		
		if(hData!=null)
		{
			values=hData.values;
		}
		else
		{
			//读两列
			values=ToolFileUtils.readFileForExcel(f.getPath(),2);
		}
		
		
		if(values[0].length==0)
		{
			Ctrl.print("空xlsx:",f.getName());
			return;
		}
		
		String[] temp;
		
		temp=values[0][0];
		
		if(temp.length<2)
		{
			Ctrl.print("头信息不足",f.getName());
			return;
		}
		
		int type=valueToInt(temp[0]);
		
		String printName=f.getName();
		
		if(keyName.startsWith("enum/"))
			printName="enum/"+printName;
		
		Ctrl.print("执行一个",printName);
		
		//横表
		if(type==0)
		{
			makeHorizontalConfig(values,f);
		}
		//纵表
		else if(type==1)
		{
			makeVerticalConfig(values,f);
		}
	}
	
	/** 绝对获取H表数据 */
	private VConfigValueData addVConfigValueData(String[][][] values,File f)
	{
		String keyName=getKeyName(f);
		
		VConfigValueData data=_vConfigValueDic.get(keyName);
		
		if(data!=null)
		{
			return data;
		}
		
		data=new VConfigValueData();
		data.file=f;
		data.keyName=keyName;
		data.fName=getUseName(f);
		data.values=values;
		data.read();
		
		_vConfigValueDic.put(keyName,data);
		
		return data;
	}
	
	/** 绝对获取H表数据 */
	private HConfigValueData addHConfigValueData(String[][][] values,File f)
	{
		String keyName=getKeyName(f);
		
		HConfigValueData data=_hConfigValueDic.get(keyName);
		
		if(data!=null)
		{
			return data;
		}
		
		data=new HConfigValueData();
		data.file=f;
		data.keyName=keyName;
		data.useName=getUseName(f);
		data.values=values;
		data.read();
		
		_hConfigValueDic.put(keyName,data);
		
		return data;
	}
	
	//private String getUseKeyName(String keyName)
	//{
	//	String useKeyName=keyName;
	//
	//	//有前缀
	//	if(!_configFront.isEmpty())
	//	{
	//		int lastIndex=keyName.lastIndexOf("/");
	//
	//		if(lastIndex!=-1)
	//		{
	//			useKeyName=useKeyName.substring(0,lastIndex)+"/"+_configFront+useNameToUpperName(keyName.substring(lastIndex+1));
	//		}
	//		else
	//		{
	//			useKeyName=_configFront+useNameToUpperName(keyName);
	//		}
	//	}
	//
	//	return useKeyName;
	//}
	
	/** 绝对获取v表数据(从G层) */
	private VConfigValueData getVConfigValueDataFromG(String keyName)
	{
		ConfigMakeTool bottomTool=getBottomTool();
		
		VConfigValueData selfData=bottomTool.getSelfVConfigValueData(keyName);
		
		if(selfData!=null)
			return selfData;
		
		return getVConfigValueDataFromParent(keyName);
	}
	
	/** 获取v表数据,如没有就从父找 */
	private VConfigValueData getVConfigValueDataFromParent(String keyName)
	{
		VConfigValueData sData=getSelfVConfigValueData(keyName);
		
		if(sData!=null)
			return sData;
		
		if(_parentTool!=null)
		{
			return _parentTool.getVConfigValueDataFromParent(keyName);
		}
		
		return null;
	}
	
	/** 获取自身H表数据 */
	private VConfigValueData getSelfVConfigValueData(String keyName)
	{
		VConfigValueData data=_vConfigValueDic.get(keyName);
		
		if(data==null)
		{
			if(_vConfigValueGotSet.contains(keyName))
				return null;
			
			_vConfigValueGotSet.add(keyName);
			
			String useKeyName=keyName;
			
			String fName=keyName;
			
			//有前缀
			if(!_configFront.isEmpty())
			{
				useKeyName=_configFront+useNameToUpperName(fName);
			}
			
			String path=_inputPath+"/"+useKeyName+"."+ ShineToolSetting.excelExName;
			
			File file=new File(path);
			
			if(file.exists())
			{
				String[][][] values=ToolFileUtils.readFileForExcel(file.getPath(),2);
				
				data=new VConfigValueData();
				data.file=file;
				data.keyName=keyName;
				data.fName=fName;
				data.values=values;
				data.read();
				
				_vConfigValueDic.put(keyName,data);
			}
		}
		
		return data;
	}
	
	/** 绝对获取H表数据(从G层) */
	private HConfigValueData getHConfigValueDataFromG(String keyName)
	{
		ConfigMakeTool bottomTool=getBottomTool();
		
		HConfigValueData parentData=bottomTool.getHConfigValueDataFromParent(keyName);
		
		return parentData;
	}
	
	/** 获取h表数据,如没有就从父找 */
	private HConfigValueData getHConfigValueDataFromParent(String keyName)
	{
		HConfigValueData sData=getSelfHConfigValueData(keyName);
		
		if(sData!=null)
			return sData;
		
		if(_parentTool!=null)
		{
			HConfigValueData pData=_parentTool.getHConfigValueDataFromParent(keyName);
			
			if(pData!=null)
			{
				return pData;
				//
				//if(keyName.startsWith("enum") || pData.isNecessary)
				//{
				//	return pData;
				//}
			}
		}
		
		return null;
	}
	
	/** 获取自身H表数据 */
	private HConfigValueData getSelfHConfigValueData(String keyName)
	{
		HConfigValueData data=_hConfigValueDic.get(keyName);
		
		if(data==null)
		{
			if(_hConfigValueGotSet.contains(keyName))
				return null;
			
			_hConfigValueGotSet.add(keyName);
			
			String useKeyName=keyName;
			
			String fName=keyName;
			
			//有前缀
			if(!_configFront.isEmpty())
			{
				int lastIndex=keyName.lastIndexOf("/");
				
				if(lastIndex!=-1)
				{
					fName=keyName.substring(lastIndex+1);
					useKeyName=useKeyName.substring(0,lastIndex)+"/"+_configFront+useNameToUpperName(fName);
				}
				else
				{
					useKeyName=_configFront+useNameToUpperName(fName);
				}
			}
			
			String path=_inputPath+"/"+useKeyName+"."+ ShineToolSetting.excelExName;
			
			File file=new File(path);
			
			if(file.exists())
			{
				String[][][] values=ToolFileUtils.readFileForExcel(file.getPath(),2);
				
				data=new HConfigValueData();
				data.file=file;
				data.keyName=keyName;
				data.useName=getUseName(file);
				data.values=values;
				data.read();
				
				_hConfigValueDic.put(keyName,data);
			}
		}
		
		return data;
	}
	
	/** 构造h表常量枚举 */
	private void makeHConfigConst(HConfigValueData hData)
	{
		//makeConst部分
		_hData=hData;
		
		for(int j=0;j<=2;++j)
		{
			//数据准备
			_isHClient=j==0;
			
			if(!ShineToolSetting.needClient && _isHClient)
				continue;
			
			if(_isHClient)
			{
				if(!hData.clsData.hasClient)
					continue;
			}
			else
			{
				if(!hData.clsData.hasServer)
					continue;
			}
			
			_codeType=_isHClient ? _clientCode : _serverCode;
			_cCode=CodeInfo.getCode(_codeType);
			
			if(!hData.enumClsName.isEmpty())
			{
				_makingSomeConst=true;
				toStartMakeConstConfig("generate",hData.enumClsName,hData.tableDescribe,hData.maxID,hData.clsData.useArr);
				
				if(_isHClient)
				{
					_clientConstNameDic.put(hData.useName,hData.enumClsName);
					_clientConstNameTotalDic.put(hData.useName,hData.enumClsName);
				}
				else
				{
					_serverConstNameDic.put(hData.useName,hData.enumClsName);
					_serverConstNameTotalDic.put(hData.useName,hData.enumClsName);
				}
			}
			
			boolean needOther=beginWriteHConfig();
			
			if(needOther || _makingSomeConst)
			{
				String[][] fValues=hData.firstValues;
				
				for(int i=_startRow;i<fValues.length;++i)
				{
					Map<String,String> vs=new HashMap<>();
					
					int tLen=hData.allFieldList.size();
					
					for(int k=0;k<tLen;k++)
					{
						int v=hData.allFieldList.get(k);
						
						vs.put(hData.fields[v].name,fValues[i][v]);
					}
					
					if(_makingSomeConst)
					{
						toMakeOneConstConfig(vs);
					}
					
					if(needOther)
					{
						writeOneHConfig(vs);
					}
				}
				
				if(_makingSomeConst)
				{
					toEndMakeConstConfig();
				}
				
				if(needOther)
				{
					endWriteHConfig();
				}
			}
		}
	}
	
	/** 横表 */
	private void makeHorizontalConfig(String[][][] values,File f)
	{
		//取出数据
		HConfigValueData hData=addHConfigValueData(values,f);
		
		doHorizontalConfig(hData,false);
		
		boolean has;
		
		if(hData.enumClsName.isEmpty())
		{
			has=hData.clsData.hasServer || hData.clsData.hasClient;
		}
		else
		{
			has=hData.clsData.hasSFields || hData.clsData.hasCFields;
		}
		
		if(hData.isNecessary)
		{
			_necessarySet.add(hData.useName);
			_necessaryKeySet.add(hData.keyName);
		}
		
		if(!has)
		{
			_hLastFiles.add(f);
		}
	}
	
	private void doHorizontalConfig(HConfigValueData hData,boolean isNeseccary)
	{
		_hData=hData;
		
		if(!isNeseccary)
		{
			makeHConfigConst(hData);
		}
		
		//都不属于的
		
		makeHConfigWrite(hData,true,isNeseccary);
		
		makeHConfigWrite(hData,false,isNeseccary);
		
		//注销
		_hData=null;
	}
	
	/** 构造H配置表写出(isS:是否服务器) */
	private void makeHConfigWrite(HConfigValueData hData,boolean isS,boolean isNeseccary)
	{
		boolean needDo;
		
		if(hData.enumClsName.isEmpty())
		{
			needDo=isS ? hData.clsData.hasServer : hData.clsData.hasClient;
		}
		else
		{
			needDo=isS ? hData.clsData.hasSFields : hData.clsData.hasCFields;
		}
		
		if(needDo)
		{
			if(!isNeseccary)
			{
				if(isS)
				{
					_hServerAllFiles.add(hData.useName);
					
					//放外面为了记录
					_hServerInputList.add(hData.clsData);
					
					if(_parentTool==null || !_parentTool._hServerAllFiles.contains(hData.useName))
					{
						_hServerNewFiles.add(hData.useName);
						_hServerNewFilesSet.add(hData.useName);
						_hAllNewFilesSet.add(hData.useName);
					}
					
					if(hData.clsData.hasNewSField)
					{
						_hServerNewClassFilesSet.add(hData.useName);
					}
				}
				else
				{
					_hClientAllFiles.add(hData.useName);
					
					//放外面为了记录
					_hClientInputList.add(hData.clsData);
					
					if(_parentTool==null || !_parentTool._hClientAllFiles.contains(hData.useName))
					{
						_hClientNewFiles.add(hData.useName);
						_hClientNewFilesSet.add(hData.useName);
						_hAllNewFilesSet.add(hData.useName);
					}
					
					if(hData.clsData.hasNewCField)
					{
						_hClientNewClassFilesSet.add(hData.useName);
					}
				}
			}
			
			if(isLastProject())
			{
				SList<ConfigFieldData> fieldList=new SList<>(k->new ConfigFieldData[k]);
				IntList indexList=new IntList();
				
				HConfigValueData current=hData.getGrandParent();
				
				//先构造field组
				while(true)
				{
					IntList tempList=isS ? current.sNewFieldList : current.cNewFieldList;
					
					for(int i=0;i<tempList.size();i++)
					{
						ConfigFieldData field=current.fields[tempList.get(i)];
						fieldList.add(field);
					}
					
					//是自己也停
					if(current==hData)
						break;
					
					if(current.childData==null)
						break;
					
					current=current.childData;
				}
				
				int fieldLen=fieldList.size();
				
				
				BytesWriteStream stream=new BytesWriteStream();
				BytesWriteStream hotFixStream=new BytesWriteStream();
				
				current=hData;
				
				SSet<String> allKeySet=new SSet<>();
				SSet<String> hotfixKeySet=new SSet<>();
				
				while(true)
				{
					indexList.clear();
					
					ConfigFieldData[] values=fieldList.getValues();
					ConfigFieldData v;
					
					for(int i=0,len=fieldList.size();i<len;++i)
					{
						v=values[i];
						
						ConfigFieldData field2=current.fieldDic.get(v.name);
						indexList.add(field2!=null ? field2.index : -1);
					}
					
					int[] values1=indexList.getValues();
					
					//先写g层
					String[][] fValues=current.firstValues;
					String aKey;
					
					for(int i=_startRow;i<fValues.length;++i)
					{
						//有值
						if((aKey=current.allKeyArr[i])!=null && !allKeySet.contains(aKey))
						{
							allKeySet.add(aKey);
							
							boolean hasHotfix=false;
							
							if(!isS)
							{
								if(hotfixKeySet.contains(aKey))
								{
									hasHotfix=true;
								}
								else
								{
									if(current.hotfixArr[i])
									{
										hotfixKeySet.add(aKey);
										hasHotfix=true;
									}
								}
							}
							
							for(int j=0;j<fieldLen;j++)
							{
								int index=values1[j];
								
								writeField(stream,current,fieldList.get(j),i,index!=-1 ? fValues[i][index] : "");
								
								if(hasHotfix)
								{
									writeField(hotFixStream,current,fieldList.get(j),i,index!=-1 ? fValues[i][index] : "");
								}
								
							}
						}
					}
					
					//不从parent拷贝
					if(!hData.copyFromParent)
						break;
					
					if(current.parentData==null)
						break;
					
					current=current.parentData;
					
				}
				
				BytesWriteStream streamT=new BytesWriteStream();
				
				streamT.writeLen(allKeySet.size());
				
				if(hData.clsData.useArr)
				{
					streamT.writeLen(hData.maxID + 1);
				}
				
				streamT.writeBytesStream(stream);
				
				String md5=StringUtils.md5(streamT.getByteArray());
				
				ConfigVersionInfo versionInfo=getVersionInfo(hData.useName);
				
				if(isS)
				{
					versionInfo.serverValueMD5=md5;
				}
				else
				{
					versionInfo.clientValueMD5=md5;
				}
				
				if(ShineToolSetting.needClient || isS)
				{
					String path=isS ? _serverOutTempPath : _clientOutTempPath;
					FileUtils.writeFileForBytesWriteStream(path + "/" + hData.useName + ".bin",streamT);
					
					if(!isS)
					{
						String hotfixPath=_serverOutTempHotfixPath + "/" + hData.useName + ".bin";
						
						if(!hotfixKeySet.isEmpty())
						{
							hotFixStream.insertLenToPos(0,hotfixKeySet.size());
							
							//BytesWriteStream hotfixStreamT=new BytesWriteStream();
							//hotfixStreamT.writeLen(hotfixNum);
							//hotfixStreamT.writeBytesStream(hotFixStream);
							
							FileUtils.writeFileForBytesWriteStream(hotfixPath,hotFixStream);
						}
						else
						{
							FileUtils.deleteFile(hotfixPath);
						}
					}
				}
			}
		}
	}
	
	
	/** 纵表 */
	private void makeVerticalConfig(String[][][] values,File f)
	{
		//取出数据
		VConfigValueData hData=addVConfigValueData(values,f);
		
		makeVConfigWrite(hData,true);
		
		makeVConfigWrite(hData,false);
	}
	
	/** 构造H配置表写出(isS:是否服务器) */
	private void makeVConfigWrite(VConfigValueData vData,boolean isS)
	{
		SList<String> tempDic=isS ? _vServerAllFiles : _vClientAllFiles;
		
		//不存在再添加
		if(!tempDic.contains(vData.fName))
		{
			tempDic.add(vData.fName);
		}
		
		(isS ? _vServerInputList : _vClientInputList).add(vData.clsData);
		
		if(isLastProject())
		{
			VConfigValueData current=vData.getGrandParent();
			
			BytesWriteStream stream=new BytesWriteStream();
			
			//先构造field组
			while(true)
			{
				SList<String> tempList=isS ? current.sNewFieldList : current.cNewFieldList;
				
				for(int i=0;i<tempList.size();i++)
				{
					String str=tempList.get(i);
					
					ConfigFieldData field=vData.fieldTotalDic.get(str);
					
					writeField(stream,null,field,field.index,field.value);
					//toWriteFieldType(stream,field,0,field.value);
				}
				
				//自己也停
				if(current==vData)
					break;
				
				if(current.childData==null)
					break;
				
				current=current.childData;
			}
			
			String md5=StringUtils.md5(stream.getByteArray());
			
			ConfigVersionInfo versionInfo=getVersionInfo(vData.fName);
			
			if(isS)
			{
				versionInfo.serverValueMD5=md5;
			}
			else
			{
				versionInfo.clientValueMD5=md5;
			}
			
			if(ShineToolSetting.needClient || isS)
			{
				String path=isS ? _serverOutTempPath : _clientOutTempPath;
				
				FileUtils.writeFileForBytesWriteStream(path + "/" + vData.fName + ".bin",stream);
			}
		}
	}
	
	/** 写合并数据 */
	private void writeCombine()
	{
		toWriteCombine(false);
		
		if(ShineToolSetting.needClient)
			toWriteCombine(true);
		
		if(ExecuteReleaseType.isRelease(ShineToolSetting.releaseType) && ShineToolSetting.needConfigIncrement)
		{
			writeConfigVersion();
		}
	}
	
	private void toWriteCombine(boolean isClient)
	{
		BytesWriteStream stream=new BytesWriteStream();
		stream.writeVersion(ShineGlobal.configVersion);
		
		BytesWriteStream editorStream=new BytesWriteStream();
		editorStream.writeVersion(ShineGlobal.configVersion);
		
		BytesWriteStream hotfixStream=new BytesWriteStream();
		hotfixStream.writeVersion(ShineGlobal.configVersion);
		
		//增量
		BytesWriteStream increaseStream=null;
		
		if(ShineToolSetting.needConfigIncrement)
		{
			increaseStream=new BytesWriteStream();
			increaseStream.writeVersion(ShineGlobal.configVersion);
		}
		
		//三个
		ConfigMakeTool current=getGrandParentTool();
		
		while(true)
		{
			int version=isClient ? current._clientCodeVersion : current._serverCodeVersion;
			
			stream.writeInt(version);
			editorStream.writeInt(version);
			hotfixStream.writeInt(version);
			
			if(ShineToolSetting.needConfigIncrement)
				increaseStream.writeInt(version);
			
			current=current._childTool;
			
			if(current==null)
				break;
		}
		
		int pp=stream.getPosition();
		
		SList<String> list=new SList<>();
		//先纵
		list.addAll(isClient ? _vClientAllFiles : _vServerAllFiles);
		//再自定义
		list.addAll(isClient ? _customClientFiles : _customServerFiles);
		
		//再横
		current=getGrandParentTool();
		
		while(true)
		{
			list.addAll(isClient ? current._hClientNewFiles : current._hServerNewFiles);
			
			current=current._childTool;
			
			if(current==null)
				break;
		}
		
		
		int num=0;
		int editorNum=0;
		int incrementNum=0;
		int hotfixNum=0;
		
		for(String fName : list)
		{
			if(writeOne(isClient,fName,stream,false))
			{
				num++;
			}
			
			if(isClient)
			{
				if(writeOne(isClient,fName,hotfixStream,true))
				{
					hotfixNum++;
				}
			}
			
			if(_editorNeedSet.contains(fName))
			{
				if(writeOne(isClient,fName,editorStream,false))
				{
					editorNum++;
				}
			}
			
			if(ShineToolSetting.needConfigIncrement && needConfigVersionDo(fName,isClient))
			{
				if(writeOne(isClient,fName,increaseStream,false))
				{
					incrementNum++;
				}
			}
		}
		
		stream.insertLenToPos(pp,num);
		
		editorStream.insertLenToPos(pp,editorNum);
		
		if(ShineToolSetting.needConfigIncrement)
			increaseStream.insertLenToPos(pp,incrementNum);
		
		if(hotfixNum>0)
		{
			hotfixStream.insertLenToPos(pp,hotfixNum);
		}
		
		if(ShineToolSetting.configNeedCompress)
		{
			stream.compress();
			editorStream.compress();
			
			if(ShineToolSetting.needConfigIncrement)
				increaseStream.compress();
			
			if(hotfixNum>0)
			{
				hotfixStream.compress();
			}
		}
		
		String outPath=isClient ? _clientOutPath : _serverOutPath;
		
		FileUtils.writeFileForBytesWriteStream(outPath + "/config.bin",stream);
		
		if(editorNum>0)
		{
			FileUtils.writeFileForBytesWriteStream(outPath + "/configForEditor.bin",editorStream);
		}
		
		if(ShineToolSetting.needConfigIncrement && incrementNum>0)
		{
			FileUtils.writeFileForBytesWriteStream(outPath + "/configIncrement.bin",increaseStream);
		}
		
		String hotfixPath=_serverOutPath + "/configHotfix.bin";
		
		if(hotfixNum>0)
		{
			FileUtils.writeFileForBytesWriteStream(hotfixPath,hotfixStream);
		}
		else
		{
			FileUtils.deleteFile(hotfixPath);
		}
	}
	
	private boolean writeOne(boolean isClient,String useName,BytesWriteStream stream,boolean isHotfix)
	{
		int key=_configKeyDic.get(useName);
		
		if(key<=0)
		{
			throwCurrentFieldError("不该找不到key:"+useName);
			return false;
		}
		
		String path;
		
		if(isHotfix)
		{
			path=_serverOutTempHotfixPath;
		}
		else
		{
			//自定义的 把生成物外置
			if(_customDescribes.contains(useName))
			{
				path=isClient ? ShineToolGlobal.clientSavePath + "/config" : ShineToolGlobal.serverSavePath + "/config";
			}
			else
			{
				path=isClient ? _clientOutTempPath : _serverOutTempPath;
			}
		}
		
		if(isClient && _customNeedSplit.contains(useName))
		{
			return false;
		}
		
		byte[] bb=FileUtils.readFileForBytes(path + "/" + useName + ".bin");
		
		if(bb==null)
		{
			if(!isHotfix)
			{
				//未忽略
				if(!ShineToolSetting.ignoreCustomConfigs.contains(useName))
				{
					Ctrl.print(useName+".bin"+(isClient ? "(client)":"(server)")+"不存在(如本项目不需要则无视)");
				}
			}
			
			return false;
		}
		
		stream.clearBooleanPos();
		//short
		stream.writeShort(key);
		stream.writeByteArr(bb);
		
		return true;
	}
	
	private void makeClassField(ClassInfo cls,ConfigFieldData field)
	{
		CodeInfo code=cls.getCode();
		
		FieldInfo f=new FieldInfo();
		f.describe=field.describe;
		f.name=field.name;
		f.visitType=VisitType.Public;
		
		for(int v : field.types)
		{
			switch(v)
			{
				case ConfigFieldType.Array:
				{
					f.type=code.getArrayType(f.type,false);
				}
				break;
				case ConfigFieldType.DInt:
				{
					cls.addImport(ShineToolSetting.dIntDOQName);
					f.type=StringUtils.getClassNameForQName(ShineToolSetting.dIntDOQName);
				}
				break;
				case ConfigFieldType.BigFloat:
				{
					cls.addImport(ShineToolSetting.BigFloatDOQName);
					f.type=StringUtils.getClassNameForQName(ShineToolSetting.BigFloatDOQName);
				}
				break;
				default:
				{
					f.type=code.getBaseVarInputStr(getFieldBaseType(v));
				}
				break;
			}
		}
		
		//值
		f.isStatic=field.isStatic;
		
		//不带默认值了
		//if(!field.value.isEmpty())
		//{
		//	f.defaultValue=getFieldValue(field,field.value,cls.getCodeType(),true);
		//}
		
		cls.addField(f);
	}
	
	/** 获取基础类型对应代码类型 */
	public static int getFieldBaseType(int type)
	{
		int re=0;
		
		switch(type)
		{
			case ConfigFieldType.Boolean:
			{
				re=VarType.Boolean;
			}
			break;
			case ConfigFieldType.Byte:
			{
				re=VarType.Byte;
			}
			break;
			case ConfigFieldType.UByte:
			{
				re=VarType.UByte;
			}
			break;
			case ConfigFieldType.Short:
			{
				re=VarType.Short;
			}
			break;
			case ConfigFieldType.UShort:
			{
				re=VarType.UShort;
			}
			break;
			case ConfigFieldType.Int:
			{
				re=VarType.Int;
			}
			break;
			case ConfigFieldType.Long:
			{
				re=VarType.Long;
			}
			break;
			case ConfigFieldType.Float:
			{
				re=VarType.Float;
			}
			break;
			case ConfigFieldType.Double:
			{
				re=VarType.Double;
			}
			break;
			case ConfigFieldType.String:
			{
				re=VarType.String;
			}
			break;
			case ConfigFieldType.TP:
			{
				re=VarType.Double;
			}
			break;
			case ConfigFieldType.WP:
			{
				re=VarType.Double;
			}
			break;
		}
		
		return re;
	}
	
	/** 获取field的value表示(字符串) */
	protected String getFieldValue(ConfigFieldData field,String value,int codeType,boolean wrapString)
	{
		if(value==null)
		{
			value="";
		}
		
		return toGetFieldValueType(field.typeList,0,value,codeType,wrapString);
	}
	
	private String toGetFieldValueType(IntList typeList,int index,String value,int codeType,boolean wrapString)
	{
		int type=typeList.get(index);
		
		if(type==ConfigFieldType.Array)
		{
			String ch=_arrSplitChars[typeList.length() - 2 - index];
			
			if(value.isEmpty())
			{
				return "{}";
			}
			else
			{
				String[] arr=value.split(ch);
				
				StringBuilder sb=new StringBuilder();
				sb.append("{");
				
				for(int i=0;i<arr.length;++i)
				{
					if(i>0)
					{
						sb.append(ch);
					}
					
					sb.append(toGetFieldValueType(typeList,index + 1,arr[i],codeType,wrapString));
				}
				
				sb.append("}");
				
				return sb.toString();
			}
		}
		else
		{
			return toGetFieldValueBaseType(type,value,codeType,wrapString);
		}
	}
	
	private String toGetFieldValueBaseType(int type,String value,int codeType,boolean wrapString)
	{
		CodeInfo code=CodeInfo.getCode(codeType);
		
		String re="";
		
		switch(type)
		{
			case ConfigFieldType.Boolean:
			{
				switch(value)
				{
					case "1":
					case "1.0":
					case "true":
					case "TRUE":
					{
						re=code.True;
					}
					break;
					default:
						re=code.False;
				}
			}
			break;
			case ConfigFieldType.Byte:
			case ConfigFieldType.UByte:
			case ConfigFieldType.Short:
			case ConfigFieldType.UShort:
			case ConfigFieldType.Int:
			case ConfigFieldType.TP:
			case ConfigFieldType.WP:
			{
				re=value.isEmpty() ? "" : String.valueOf((int)Double.parseDouble(value));
			}
			break;
			case ConfigFieldType.Long:
			{
				re=value.isEmpty() ? "" : String.valueOf((long)Double.parseDouble(value)) + "L";
			}
			break;
			case ConfigFieldType.Float:
			{
				re=value.isEmpty() ? "" : String.valueOf((float)Double.parseDouble(value)) + "f";
			}
			break;
			case ConfigFieldType.Double:
			{
				re=value.isEmpty() ? "" : String.valueOf(Double.parseDouble(value));
			}
			break;
			case ConfigFieldType.String:
			{
				re=value==null ? "" : value;
				
				if(wrapString)
				{
					re="\"" + re + "\"";
				}
			}
			break;
			case ConfigFieldType.DInt:
			{
				String[] arr=value.split(_dIntSplitChar);
				
				String k=arr.length>0 ? String.valueOf((int)Double.parseDouble(arr[0])) : "-1";
				String v=arr.length>1 ? String.valueOf((int)Double.parseDouble(arr[1])) : "0";
				
				re="DIntData.create(" + k + "," + v + ")";
			}
			break;
			case ConfigFieldType.BigFloat:
			{
				re="BigFloatData.createByStr(" + value + ")";
			}
			break;
		}
		
		return re;
	}
	
	private void writeField(BytesWriteStream stream,HConfigValueData current,ConfigFieldData field,int row,String value)
	{
		setCurrentField(current,field,row,value);
		
		if(_hData!=null)
		{
			//检查
			_hData.checkOneValue(field, row,value);
		}
		
		toWriteFieldType(stream,field,0,value);
		
		setCurrentField(null,null,0,"");
	}
	
	private void setCurrentField(HConfigValueData configData,ConfigFieldData field,int row,String value)
	{
		_currentHConfigData=configData;
		_currentField=field;
		_currentRow=row;
		_currentValue=value;
		
		if(_parentTool!=null)
		{
			_parentTool.setCurrentField(configData,field,row,value);
		}
	}
	
	private void toWriteFieldType(BytesWriteStream stream,ConfigFieldData field,int index,String value)
	{
		int type=field.typeList.get(index);
		
		if(type==ConfigFieldType.Array)
		{
			String ch=_arrSplitChars[field.typeList.length() - 2 - index];
			
			if(value.isEmpty())
			{
				stream.writeLen(0);
			}
			else
			{
				String[] arr=value.split(ch);
				
				stream.writeLen(arr.length);
				
				for(int i=0;i<arr.length;++i)
				{
					toWriteFieldType(stream,field,index + 1,arr[i]);
				}
			}
		}
		else
		{
			toWriteFieldBaseType(stream,type,value);
		}
	}
	
	/** 抛出属性错误 */
	private void throwCurrentFieldError(String msg)
	{
		throwFieldError(msg,_currentField,_currentRow,_currentValue);
	}
	
	/** 抛出属性错误 */
	private void throwFieldError(String msg,ConfigFieldData field,int row,String value)
	{
		Ctrl.throwError("出错:"+msg+", 表:"+field.clsData.fName+", 字段:"+field.name+", 行:"+(row+1)+", 值:"+value);
	}
	
	private void toWriteFieldBaseType(BytesWriteStream stream,int type,String str)
	{
		//_currentField=field;
		//_currentRow=row;
		
		switch(type)
		{
			case ConfigFieldType.Boolean:
			{
				switch(str)
				{
					case "1":
					case "1.0":
					case "true":
					{
						stream.writeBoolean(true);
					}
						break;
					case "0":
					case "0.0":
					case "":
					case "false":
					case "-1":
					{
						stream.writeBoolean(false);
					}
						break;
					default:
					{
						throwCurrentFieldError("boolean字段值不合法");
					}
				}
			}
				break;
			case ConfigFieldType.Byte:
			{
				int v=valueToInt(str);
				
				if(v>127)
				{
					throwCurrentFieldError("byte溢出");
				}
				
				stream.writeByte(valueToInt(str));
			}
				break;
			case ConfigFieldType.UByte:
			{
				int v=valueToInt(str);
				
				if(v>255)
				{
					throwCurrentFieldError("ubyte溢出");
				}
				
				stream.writeUnsignedByte(valueToInt(str));
			}
				break;
			case ConfigFieldType.Short:
			{
				int v=valueToInt(str);
				
				if(v>32767)
				{
					throwCurrentFieldError("short溢出");
				}
				
				stream.writeShort(valueToInt(str));
			}
				break;
			case ConfigFieldType.UShort:
			{
				int v=valueToInt(str);
				
				if(v>65535)
				{
					throwCurrentFieldError("ushort溢出");
				}
				
				stream.writeUnsignedShort(valueToInt(str));
			}
				break;
			case ConfigFieldType.Int:
			{
				stream.writeInt(valueToInt(str));
			}
				break;
			case ConfigFieldType.Long:
			{
				stream.writeLong((long)valueToDouble(str));
			}
				break;
			case ConfigFieldType.Float:
			{
				stream.writeFloat((float)valueToDouble(str));
			}
				break;
			case ConfigFieldType.Double:
			{
				stream.writeDouble(valueToDouble(str));
			}
				break;
			case ConfigFieldType.String:
			{
				stream.writeUTF(StringUtils.stringNormalReplace(str));
			}
				break;
			case ConfigFieldType.DInt:
			{
				String[] arr=str.split(_dIntSplitChar);
				
				int k=arr.length>0 ? valueToInt(arr[0]) : -1;//默认-1
				int v=arr.length>1 ? valueToInt(arr[1]) : 0;
				
				stream.writeInt(k);
				stream.writeInt(v);
			}
				break;
			case ConfigFieldType.TP:
			{
				stream.writeUnsignedShort(valueToInt(str));
			}
				break;
			case ConfigFieldType.WP:
			{
				stream.writeUnsignedShort(valueToInt(str));
			}
				break;
			case ConfigFieldType.BigFloat:
			{
				stream.writeInt(0);
				stream.writeDouble(valueToDouble(str));
			}
				break;
		}
	}
	
	public static int getShift(int type)
	{
		switch(type)
		{
			case ConfigFieldType.Byte:
			case ConfigFieldType.UByte:
			{
				return 1;
			}
			case ConfigFieldType.Short:
			case ConfigFieldType.UShort:
			{
				return 2;
			}
			case ConfigFieldType.Int:
			{
				return 4;
			}
			case ConfigFieldType.Long:
			{
				return 8;
			}
		}
		
		return 0;
	}
	
	protected class VConfigValueData
	{
		/** 文件 */
		public File file;
		/** 表名(全小写了) */
		public String fName;
		/** 使用名 */
		public String keyName;
		/** 值组 */
		public String[][][] values;
		/** 值组(第一sheet) */
		public String[][] firstValues;
		/** 表描述 */
		public String tableDescribe;
		
		/** 父数据(c没有就取m) */
		public VConfigValueData parentData;
		
		public VConfigValueData childData;
		
		/** 属性组 */
		public ConfigFieldData[] fields;
		/** 属性字典 */
		public SMap<String,ConfigFieldData> fieldDic=new SMap<>();
		/** 属性全字典(与h不同，每次会覆盖为新的) */
		public SMap<String,ConfigFieldData> fieldTotalDic;
		
		/** 客户端属性组 */
		public SList<ConfigFieldData> cFieldList=new SList<>();
		/** 服务器属性组 */
		public SList<ConfigFieldData> sFieldList=new SList<>();
		
		/** 客户端新增属性组 */
		public SList<String> cNewFieldList=new SList<>();
		/** 服务器新增属性组 */
		public SList<String> sNewFieldList=new SList<>();
		
		
		public ConfigClassData clsData;
		
		/** 读取(解析) */
		public void read()
		{
			firstValues=values[0];
			
			if(_parentTool!=null)
			{
				parentData=_parentTool.getVConfigValueDataFromParent(keyName);
				
				if(parentData!=null)
				{
					parentData.childData=this;
				}
			}
			
			readFirstValues();
		}
		
		public VConfigValueData getGrandParent()
		{
			if(parentData==null)
				return this;
			
			return parentData.getGrandParent();
		}
		
		/** 读值组(sheet1) */
		public void readFirstValues()
		{
			String[][] fValues=firstValues;
			
			if(fValues.length<1)
			{
				Ctrl.throwError("纵表列信息不足",fName);
				Ctrl.exit();
				return;
			}
			
			String[] head=fValues[0];
			
			//表描述
			tableDescribe=head.length>1 ? head[1] : "";
			
			if(head.length<3)
			{
				Ctrl.print("头信息不足",file.getName());
				return;
			}
			
			String cName=StringUtils.getClassNameForQName(head[2]);
			String pName=StringUtils.getPackageNameForQName(head[2]);
			
			
			ConfigClassData data=new ConfigClassData();
			data.fName=fName;
			data.file=file;
			
			data.hasParent=parentData!=null;
			
			data.clientCls=ClassInfo.getClassInfo(_sourceCode);
			data.clientCls.packageStr=pName;
			data.clientCls.clsName=cName;
			data.clientCls.clsDescribe=tableDescribe;
			
			data.serverCls=ClassInfo.getClassInfo(_sourceCode);
			data.serverCls.packageStr=pName;
			data.serverCls.clsName=cName;
			data.serverCls.clsDescribe=tableDescribe;
			
			
			fields=new ConfigFieldData[fValues.length];
			
			fieldTotalDic=parentData!=null ? parentData.fieldTotalDic.clone() : new SMap<>();
			
			ConfigFieldData field;
			ConfigFieldData parentField;
			String fieldName;
			String csName;
			
			for(int i=1;i<fValues.length;++i)
			{
				//名字不空,cs不空
				if(!(fieldName=fValues[i][0]).isEmpty() && !(csName=fValues[i][1]).isEmpty())
				{
					field=new ConfigFieldData();
					field.clsData=data;
					field.name=fieldName;
					field.readCSKR(csName);
					field.readType(fValues[i][2]);
					field.describe=fValues[i][3];
					field.value=fValues[i][4];
					//field.isStatic=isStatic;
					
					field.index=i;
					fields[i]=field;
					
					if(fieldDic.putIfAbsent(field.name,field)!=null)
					{
						Ctrl.throwError("主键重复:",fName,"fieldName:",field.name);
					}
					
					fieldTotalDic.put(field.name,field);//覆盖
					
					parentField=parentData!=null ? parentData.fieldTotalDic.get(fieldName) : null;
					
					if(parentField!=null)
					{
						if(!parentField.cskr.equals(field.cskr))
						{
							Ctrl.throwError("表中字段CSKR与parent表不一致:",fName,"字段名:",fieldName);
							Ctrl.exit();
							return;
						}
						
						if(!parentField.type.equals(field.type))
						{
							Ctrl.throwError("表中字段类型与parent表不一致:",fName,"字段名:",fieldName);
							Ctrl.exit();
							return;
						}
					}
					
					if(field.hasClient)
					{
						data.hasClient=true;
						
						cFieldList.add(field);
						
						if(parentField!=null)
						{
							data.clientSuperFields.add(field.name);
						}
						else
						{
							cNewFieldList.add(field.name);
							data.hasNewCField=true;

							makeClassField(data.clientCls,field);
						}
					}
					
					
					if(field.hasServer)
					{
						data.hasServer=true;
						
						sFieldList.add(field);
						
						if(parentField!=null)
						{
							data.serverSuperFields.add(field.name);
						}
						else
						{
							sNewFieldList.add(field.name);
							data.hasNewSField=true;
							
							makeClassField(data.serverCls,field);
						}
					}
				}
			}
			
			clsData=data;
		}
	}
	
	protected class HConfigValueData
	{
		/** 文件 */
		public File file;
		/** 表名(useName)(全小写了) */
		public String useName;
		/** 使用名 */
		public String keyName;
		/** 值组 */
		public String[][][] values;
		/** 值组(第一sheet) */
		public String[][] firstValues;
		/** 表描述 */
		public String tableDescribe;
		/** 集合类型 */
		public int dicType;
		/** 枚举类名 */
		public String enumClsName="";
		/** 是否是必须表 */
		public boolean isNecessary;
		/** 是否从parent复制 */
		public boolean copyFromParent;
		/** 是否无论有无有效字段都生成配置类(占位符) */
		public boolean isGenerateAlways=false;
		
		/** 父数据(c没有就取m) */
		public HConfigValueData parentData;
		/** 子数据 */
		public HConfigValueData childData;
		
		
		
		/** 属性组 */
		public ConfigFieldData[] fields;
		/** 属性字典 */
		public SMap<String,ConfigFieldData> fieldDic;
		/** 属性全字典 */
		public SMap<String,ConfigFieldData> fieldTotalDic;
		/** 服务器输出序号组(行) */
		public IntList sNewFieldList=new IntList();
		/** 客户端输出序号组(行) */
		public IntList cNewFieldList=new IntList();
		/** 总输出序号组(行) */
		public IntList allFieldList=new IntList();
		
		public int hotfixIndex=-1;
		
		/** 组合key数组(列) */
		public String[] allKeyArr;
		/** 组合key字典(列) */
		public SSet<String> allKeySet;
		
		/** 热更字段组 */
		public boolean[] hotfixArr;
		
		/** 服务器输出序号组 */
		public int[] sArr;
		/** 客户端输出序号组 */
		public int[] cArr;
		
		
		public ConfigClassData clsData;
		
		/** 最大ID */
		public int maxID;
		
		/** 字段检查方法组 */
		private SMap<String,CheckFunc> _checkFields;
		
		/** 字段值set组 */
		private SMap<String,SSet<String>> _fieldValueSetDic=new SMap<>();
		
		/** 主键set */
		private SSet<String> _primaryKeySet;
		
		//temp
		private CheckFunc _languageCheckFunc;
		private CheckFunc _internationalResourceCheckFunc;
		
		//reason
		private String _reason;
		
		/** 读取(解析) */
		public void read()
		{
			firstValues=values[0];
			
			if(_parentTool!=null)
			{
				parentData=_parentTool.getHConfigValueDataFromParent(keyName);
				
				if(parentData!=null)
				{
					parentData.childData=this;
				}
			}
			
			readFirstValues();
		}
		
		public HConfigValueData getGrandParent()
		{
			if(parentData==null)
				return this;
			
			return parentData.getGrandParent();
		}
		
		/** 检查某字段值 */
		public boolean checkOneValue(ConfigFieldData field,int row,String value)
		{
			if(ShineToolSetting.useLanguage)
			{
				switch(field.markType)
				{
					case ConfigFieldMarkType.Language:
					{
						if(_languageCheckFunc==null)
						{
							//language表检测
							_languageCheckFunc=createRootCheckFunc(field.typeList,"none|inkey(language)");
						}
						
						if(!_languageCheckFunc.checkValue(value))
						{
							throwCurrentFieldError("language条件不匹配:"+_languageCheckFunc._reason);
							return false;
						}
					}
						break;
					case ConfigFieldMarkType.InternationalResource:
					{
						if(_internationalResourceCheckFunc==null)
						{
							//internationalResource表检测
							_internationalResourceCheckFunc=createRootCheckFunc(field.typeList,"none|inkey(internationalResource)");
						}
						
						if(!_internationalResourceCheckFunc.checkValue(value))
						{
							throwCurrentFieldError("internationalResource条件不匹配"+_internationalResourceCheckFunc._reason);
							return false;
						}
					}
						break;
				}
			}
			
			if(!doCheckValue(field.name,value))
			{
				throwCurrentFieldError("约束条件不匹配:"+_reason);
				return false;
			}
			
			return true;
		}
		
		/** 读值组(sheet1) */
		public void readFirstValues()
		{
			String[][] fValues=firstValues;
			
			if(fValues.length<_startRow)
			{
				Ctrl.print("横表列信息不足",useName);
				Ctrl.exit();
				return;
			}
			
			String[] head=fValues[0];
			
			//表描述
			tableDescribe=head.length>1 ? head[1] : "";
			//集合类型
			dicType=head.length>2 ? valueToInt(head[2]) : ConfigDicType.Default;
			
			//枚举名
			enumClsName=head.length>3 ? head[3] : "";
			
			//复制parent,不是common表，有枚举,或者有标记
			
			copyFromParent=!isCommonProject() && (!enumClsName.isEmpty() || (head.length>4 && valueToInt(head[4])==1));
			
			//所有表全表
			isNecessary=ShineToolSetting.configAllNecessary;// || ((head.length>4 && valueToInt(head[4])==1));
			//是否一定生成
			isGenerateAlways=head.length>5 && valueToInt(head[5])==1;
			
			if(parentData!=null)
			{
				//有common的就用common的
				dicType=parentData.dicType;
			}
			else
			{
				copyFromParent=false;
				
				//if(copyFromParent)
				//{
				//	Ctrl.print("找不到对应的parent表",useName);
				//	Ctrl.exit();
				//	return;
				//}
			}
			
			//有枚举名的时候,默认使用数组
			if(!enumClsName.isEmpty() && dicType==ConfigDicType.Default)
			{
				dicType=ConfigDicType.UseArr;
			}
			
			fields=new ConfigFieldData[fValues[1].length];
			
			//configData部分
			
			//从父克隆基本数据
			ConfigClassData data=parentData!=null ? parentData.clsData.clone() : new ConfigClassData();
			
			clsData=data;
			data.fName=useName;
			data.file=file;
			
			data.hasParent=parentData!=null;
			
			ObjectCall<ClassInfo> run1=k->
			{
				int lastII=keyName.indexOf("/");;
				String pName=lastII==-1 ? "" : keyName.substring(0,lastII).replaceAll("\\/",".");
				
				//转置
				if(pName.equals("enum"))
				{
					pName="enumT";
				}
				
				k.packageStr=pName;
				k.clsName=useNameToUpperName(useName);
				k.clsDescribe=tableDescribe;
			};
			
			data.clientCls=ClassInfo.getClassInfo(_sourceCode);
			data.serverCls=ClassInfo.getClassInfo(_sourceCode);
			
			run1.apply(data.clientCls);
			run1.apply(data.serverCls);
			
			boolean result;
			
			ConfigFieldData field;
			String fieldName;
			ConfigFieldData parentField;
			
			fieldDic=new SMap<>();
			fieldTotalDic=parentData!=null ? parentData.fieldDic.clone() : new SMap<>();
			
			SList<ConfigFieldKeyData> keyList=new SList<>(k->new ConfigFieldKeyData[k]);
			
			//列数
			for(int i=0;i<fValues[1].length;++i)
			{
				fieldName=fValues[4][i];
				
				//有属性
				if(!fieldName.isEmpty())
				{
					field=new ConfigFieldData();
					field.clsData=data;
					field.name=fieldName;
					field.describe=fValues[1][i];
					
					parentField=parentData!=null ? parentData.fieldTotalDic.get(fieldName) : null;
					
					if(parentField!=null)
					{
						if(!parentField.cskr.equals(fValues[2][i]))
						{
							Ctrl.throwError("表中字段CSKR与parent表不一致:",useName,"字段名:",fieldName);
							Ctrl.exit();
							return;
						}
						
						if(!parentField.type.equals(fValues[3][i]))
						{
							Ctrl.throwError("表中字段类型与parent表不一致:",useName,"字段名:",fieldName);
							Ctrl.exit();
							return;
						}
					}
					
					field.readCSKR(fValues[2][i]);
					result=field.readType(fValues[3][i]);
					
					if(!result)
					{
						Ctrl.throwError("表中字段类型不识别:",useName,"字段名:",fieldName);
						Ctrl.exit();
						return;
					}
					
					if(fieldDic.contains(fieldName))
					{
						Ctrl.throwError("表中字段重名:",useName,"字段名:",fieldName);
						Ctrl.exit();
						return;
					}
					
					field.index=i;
					fields[i]=field;
					fieldDic.put(field.name,field);
					fieldTotalDic.putIfAbsent(field.name,field);
					
					if(field.name.equals("hotfix"))
					{
						hotfixIndex=i;
					}
					
					allFieldList.add(i);
					
					if(field.hasClient)
					{
						data.hasClient=true;
						
						if(!field.isKey)
							data.hasCFields=true;
						
						if(parentField!=null)
						{
							data.clientSuperFields.add(field.name);
						}
						else
						{
							cNewFieldList.add(i);
							data.hasNewCField=true;
							
							makeClassField(data.clientCls,field);
						}
					}
					
					
					if(field.hasServer)
					{
						data.hasServer=true;
						
						if(!field.isKey)
							data.hasSFields=true;
						
						if(parentField!=null)
						{
							data.serverSuperFields.add(field.name);
						}
						else
						{
							sNewFieldList.add(i);
							data.hasNewSField=true;
							
							makeClassField(data.serverCls,field);
						}
					}
					
					if(isGenerateAlways)
					{
						data.hasClient=true;
						data.hasServer=true;
						data.hasCFields=true;
						data.hasSFields=true;
						data.hasNewCField=true;
						data.hasNewSField=true;
					}
					
					if(field.isKey)
					{
						switch(field.firstType)
						{
							case ConfigFieldType.Array:
							case ConfigFieldType.Boolean:
							case ConfigFieldType.TP:
							case ConfigFieldType.WP:
							case ConfigFieldType.Float:
							case ConfigFieldType.Double:
							{
								Ctrl.throwError("不支持的key类型:" + field.firstType + "来自:" + useName);
							}
							break;
						}
						
						ConfigFieldKeyData kData=new ConfigFieldKeyData();
						kData.index=i;
						kData.name=field.name;
						kData.type=field.firstType;
						kData.shift=getShift(kData.type);
						
						keyList.add(kData);
					}
					
					switch(field.types[0])
					{
						case ConfigFieldType.BigFloat:
						{
							data.bigFloatKeys.add(field);
						}
							break;
					}
					
					switch(field.markType)
					{
						case ConfigFieldMarkType.Resource:
						{
							if(!ShineToolSetting.isClientOnlyData)
							{
								//不是字符串或字符串数组
								if(field.types[0]!=ConfigFieldType.String)
								{
									Ctrl.throwError("不支持的resourceKey类型:" + field.types[0] + "来自:" + useName);
									return;
								}
								
								data.resourceKeys.add(field.name);
							}
						}
						break;
						case ConfigFieldMarkType.Language:
						{
							//不是字符串或字符串数组
							if(field.types[0]!=ConfigFieldType.String)
							{
								Ctrl.throwError("不支持的languageKey类型:" + field.types[0] + "来自:" + useName);
								return;
							}
							
							data.languageKeys.add(field.name);
						}
						break;
						case ConfigFieldMarkType.InternationalResource:
						{
							if(!ShineToolSetting.isClientOnlyData)
							{
								//不是字符串或字符串数组
								if(field.types[0]!=ConfigFieldType.String)
								{
									Ctrl.throwError("不支持的internationalResourceKey类型:" + field.types[0] + "来自:" + useName);
									return;
								}
								
								data.internationalResourceKeys.add(field.name);
							}
							
						}
						break;
						case ConfigFieldMarkType.TimeExpression:
						{
							//不是字符串或字符串数组
							if(field.types[0]!=ConfigFieldType.String)
							{
								Ctrl.throwError("不支持的timeExpression类型:" + field.types[0] + "来自:" + useName);
								return;
							}
							
							data.timeExpressionKeys.add(field.name);
						}
						break;
					}
				}
			}
			
			if(keyList.size()==0)
			{
				Ctrl.throwError("不能没有key:" + useName);
				return;
			}
			
			data.keyArr=keyList.toArray();
			
			cArr=cNewFieldList.toArray();
			sArr=sNewFieldList.toArray();
			
			
			
			//countArr部分
			boolean needCountArr=data.getKeysCombineType()==ConfigFieldType.Int && dicType==ConfigDicType.UseArr;//要整形字典
			
			int countNum=0;
			
			if(needCountArr)
			{
				for(int j=data.keyArr.length - 1;j >= 0;--j)
				{
					countNum+=data.keyArr[j].shift;
				}
			}
			
			allKeyArr=new String[fValues.length];
			allKeySet=new SSet<>();
			hotfixArr=new boolean[fValues.length];
			
			maxID=parentData!=null ? parentData.maxID : 0;
			
			for(int i=_startRow;i<fValues.length;++i)
			{
				boolean need=false;
				
				String allKeyValue="";
				
				int value=0;
				
				int num=countNum;
				
				String keyV;
				
				for(int j=0;j<data.keyArr.length;++j)
				{
					ConfigFieldKeyData v=data.keyArr[j];
					
					keyV=fValues[i][v.index];
					
					if(j>0)
					{
						allKeyValue+="_";
					}
					
					allKeyValue+=keyV;
					
					//1个key不空就可以
					if(!keyV.isEmpty())
					{
						need=true;
					}
					
					if(needCountArr)
					{
						value|=valueToInt(keyV);
						
						num-=v.shift;
						
						if(num>0)
						{
							value<<=(8 * num);
						}
					}
				}
				
				if(need)
				{
					if(allKeySet.contains(allKeyValue))
					{
						Ctrl.throwError("表主键重复:" + allKeyValue + "来自:" + useName);
						return;
					}
					
					allKeyArr[i]=allKeyValue;
					allKeySet.add(allKeyValue);
					
					if(hotfixIndex!=-1 && StringUtils.strToBoolean(fValues[i][hotfixIndex]))
					{
						hotfixArr[i]=true;
					}
				}
				
				if(needCountArr)
				{
					if(value>maxID)
					{
						maxID=value;
					}
				}
			}
			
			if(needCountArr)
			{
				//父项
				if(parentData!=null && parentData.dicType==ConfigDicType.UseArr && parentData.clsData.useArr)
				{
					//超出范围
					if(maxID>=useArrLength)
					{
						Ctrl.throwError("g层的表,超出c层useArr的限制",useName,maxID);
						return;
					}
					
					data.useArr=true;
				}
				else
				{
					//使用条件
					data.useArr=dicType==ConfigDicType.UseArr && maxID<useArrLength;
				}
			}
			
			//赋值
			clsData=data;
		}
		
		/** 读值组(sheet2) */
		private void readSecondValues()
		{
			_checkFields=new SMap<>();
			
			String[][] cValues=values[1];
			
			if(cValues==null || cValues.length==0)
				return;
			
			if(cValues[0].length<2)
				return;
			
			String fieldName;
			
			for(String[] arr:cValues)
			{
				if(!(fieldName=arr[0]).isEmpty())
				{
					ConfigFieldData field=fieldDic.get(fieldName);
					
					IntList typeList=null;
					
					if(field!=null)
					{
						typeList=field.typeList;
					}
					else if(fieldName.equals("enum"))
					{
						typeList=_enumTypeList;
					}
					else
					{
						throwCurrentFieldError("未找到field:"+fieldName);
					}
					
					_checkFields.put(fieldName,createRootCheckFunc(typeList,arr[1]));
				}
			}
		}
		
		/** 执行检查一个 */
		private boolean doCheckValue(String fieldName,String value)
		{
			_reason=null;
			
			if(parentData!=null)
			{
				if(!parentData.doCheckValue(fieldName,value))
				{
					_reason=parentData._reason;
					return false;
				}
			}
			
			CheckFunc checkFunc=getCheckFields().get(fieldName);
			
			if(checkFunc!=null)
			{
				if(!checkFunc.checkValue(value))
				{
					_reason=checkFunc._reason;
					return false;
				}
			}
			
			return true;
		}
		
		/** 获取经检查组 */
		private SMap<String,CheckFunc> getCheckFields()
		{
			if(_checkFields==null)
			{
				readSecondValues();
			}
			
			return _checkFields;
		}
		
		//public CheckFunc
		
		/** 获取某一field的set组 */
		public SSet<String> getFieldValueSet(String fieldName)
		{
			SSet<String> re=_fieldValueSetDic.get(fieldName);
			
			if(re==null)
			{
				ConfigFieldData fData=fieldDic.get(fieldName);
				
				if(fData==null)
				{
					Ctrl.throwError("不存在的字段名",fieldName);
				}
				
				re=new SSet<>();
				
				for(int i=_startRow;i<firstValues.length;i++)
				{
					re.add(firstValues[i][fData.index]);
				}
				
				//common的
				if(parentData!=null && copyFromParent)
				{
					re.addAll(parentData.getFieldValueSet(fieldName));
				}
				
				_fieldValueSetDic.put(fieldName,re);
			}
			
			return re;
		}
		
		/** 获取主键set */
		public SSet<String> getPrimaryKeySet()
		{
			if(_primaryKeySet!=null)
				return _primaryKeySet;
			
			_primaryKeySet=new SSet<>();
			
			ConfigFieldKeyData[] keyArr=clsData.keyArr;
			
			StringBuilder sb=new StringBuilder();
			
			for(int j=_startRow;j<firstValues.length;++j)
			{
				sb.setLength(0);
				
				for(int i=0,len=keyArr.length;i<len;++i)
				{
					if(i>0)
					{
						sb.append('_');
					}
					
					sb.append(firstValues[j][keyArr[i].index]);
				}
				
				_primaryKeySet.add(sb.toString());
			}
			
			//parent的
			if(parentData!=null && copyFromParent)
			{
				_primaryKeySet.addAll(parentData.getPrimaryKeySet());
			}
			
			return _primaryKeySet;
		}
	}
	
	
	
	
	//--导出类--//
	
	/** 获取枚举类字典 */
	public SMap<String,String> getClientConstNameDic()
	{
		return _clientConstNameDic;
	}
	
	/** 获取枚举类字典 */
	public SMap<String,String> getServerConstNameDic()
	{
		return _serverConstNameDic;
	}
	
	/** 获取枚举类字典 */
	public SMap<String,String> getClientConstNameTotalDic()
	{
		return _clientConstNameTotalDic;
	}
	
	/** 获取枚举类字典 */
	public SMap<String,String> getServerConstNameTotalDic()
	{
		return _serverConstNameTotalDic;
	}
	
	public String getConstTypeClsQName(String name,boolean isClient)
	{
		name=upperNameToUseName(name);
		
		String clsName=isClient ? _clientConstNameDic.get(name) : _serverConstNameDic.get(name);
		
		if(clsName!=null)
		{
			String path=toGetConstTypeClsPath("generate",clsName,isClient);
			
			if(path!=null)
			{
				String qName=FileUtils.getPathQName(path);
				return qName;
			}
		}
		
		return "";
	}
	
	public String getConstTypeClsQNameAndParent(String name,boolean isClient)
	{
		String re=getConstTypeClsQName(name,isClient);
		
		if(re.isEmpty() && _parentTool!=null)
		{
			return _parentTool.getConstTypeClsQName(name,isClient);
		}
		
		return re;
	}
	
	protected int getFType(String name)
	{
		return _hData.fieldDic.get(name).firstType;
	}
	
	protected String getFValue(String name,Map<String,String> values)
	{
		return getFieldValue(_hData.fieldDic.get(name),values.get(name),_codeType,false);
	}
	
	protected String toGetConstTypeClsPath(String packName,String clsName)
	{
		return toGetConstTypeClsPath(packName,clsName,_isHClient);
	}
	
	protected String toGetConstTypeClsPath(String packName,String clsName,boolean isClient)
	{
		int codeType=isClient ? _clientCode : _serverCode;
		
		return getProjectPath(_projectType,isClient) + "/constlist/" + packName + "/" + (_projectUFront) + useNameToUpperName(clsName) + "." + CodeType.getExName(codeType);
	}
	
	protected void toStartMakeConstConfig(String packName,String clsName,String describe,int maxID)
	{
		toStartMakeConstConfig(packName,clsName,describe,maxID,false);
	}
	
	protected void toStartMakeConstConfig(String packName,String clsName,String describe,int maxID,boolean useArr)
	{
		_maxID=maxID;
		
		_constClsPath=toGetConstTypeClsPath(packName,clsName);
		_constCls=ClassInfo.getClassInfoFromPathAbs(_constClsPath);
		_constCls.clsDescribe=describe;
		
		_useArrConst=useArr;
		_isStringKey=false;
		
		//删掉所有的const,final和size
		
		for(String v : _constCls.getFieldNameList().clone())
		{
			FieldInfo field=_constCls.getField(v);
			
			//删所有常量和size
			if(field.isConst || field.name.equals("size"))
			{
				//删了属性
				_constCls.removeField(v);
			}
		}
		
	}
	
	protected void toMakeOneConstConfig(Map<String,String> values)
	{
		String name=StringUtils.ucWord(getFValue("name",values));
		String id=getFValue("id",values);
		
		//没有id或者没有名字
		if(id.isEmpty() || name.isEmpty())
		{
			return;
		}
		
		//字符串key
		if(getFType("id")==ConfigFieldType.String)
		{
			_isStringKey=true;
		}
		else
		{
			_isStringKey=false;
			
			int idV=Integer.parseInt(id);
			
			if(idV>_maxID)
			{
				_maxID=idV;
			}
		}
		
		
		if(_hData.parentData==null || _hData.parentData.fieldTotalDic.get(name)==null)
		{
			String describe=getFValue("describe",values);
			
			CodeInfo code=_constCls.getCode();
			
			FieldInfo field=new FieldInfo();
			field.isConst=true;
			field.isStatic=true;
			field.visitType=VisitType.Public;
			field.type=_isStringKey ? code.String : code.Int;
			field.name=name;
			field.describe=describe;
			field.defaultValue=_isStringKey ? "\""+id+"\"" : id;
			
			_constCls.addField(field);
		}
	}
	
	protected void toEndMakeConstConfig()
	{
		_constCls.removeField("size");
		
		FieldInfo field=new FieldInfo();
		field.isStatic=true;
		field.visitType=VisitType.Public;
		field.type=_constCls.getCode().Int;
		field.name="size";
		field.describe="长度";
		field.defaultValue=(!_isStringKey && _useArrConst) ? String.valueOf(_maxID + 1) : "0";
		
		//先删后加,一定末尾
		_constCls.addField(field);
		
		_constCls.writeToPath(_constClsPath);
		
		_makingSomeConst=false;
	}
	
	//接口

	/** 开始写H表 */
	protected abstract boolean beginWriteHConfig();

	/** 写一个H表 */
	protected abstract void writeOneHConfig(Map<String,String> values);

	/** 写完H表 */
	protected abstract void endWriteHConfig();
	
	public abstract String getProjectPath(int projectType,boolean isClient);
	
	
	//check相关
	
	/** 创建根检查 */
	private CheckFunc createRootCheckFunc(IntList typeList,String expression)
	{
		if(expression.isEmpty())
			return null;
		
		CheckFunc re=new CheckFunc();
		re._funcName="root";
		re._typeList=typeList;
		re._childFuncs=createCheckFunc(typeList,expression,0);
		return re;
	}
	
	private CheckFunc[][] createCheckFunc(IntList typeList,String expression,int typeIndex)
	{
		if(expression.isEmpty())
			return new CheckFunc[0][];
		
		String[] arr=StringUtils.splitEx(expression,'(',')','|');
		
		CheckFunc[][] re=new CheckFunc[arr.length][];
		CheckFunc func;
		
		for(int i=0;i<arr.length;i++)
		{
			String[] arr2=StringUtils.splitEx(arr[i],'(',')','&');
			
			CheckFunc[] tt=new CheckFunc[arr2.length];
			
			for(int j=0;j<arr2.length;j++)
			{
				func=new CheckFunc();
				func.init(typeList,arr2[j],typeIndex);
				
				tt[j]=func;
			}
			
			re[i]=tt;
		}
		
		return re;
	}
	
	//private String getCheckArgValue(String value)
	
	private class CheckFunc
	{
		private IntList _typeList;
		
		private String _funcName;
		
		private int _typeIndex;
		
		private CheckFunc[][] _childFuncs;
		
		private boolean _isNot=false;
		
		//get序号
		private int _getIndex;
		//get属性名
		private String _getFieldName;
		
		//compare
		/** 比较值 */
		private double _compareValue;
		/** 比较结果 */
		private int _compareResult;
		
		//infield
		/** 表名 */
		private String _inTableName;
		/** 字段名 */
		private String _inFieldName;
		/** 字段值组 */
		private SSet<String> _fieldValueSet;
		
		//primary
		private SSet<String> _primaryKeySet;
		
		//switch
		/** switch方法 */
		private IntObjectMap<CheckFunc[][]> _switchFuncs;
		
		//arrkey
		private int _startIndex;
		private int _endIndex;
		
		//use
		private HConfigValueData _refConfig;
		
		//reason
		private String _reason;
		
		/** 初始化表达式 */
		public void init(IntList typeList,String expression,int typeIndex)
		{
			_typeList=typeList;
			_typeIndex=typeIndex;
			
			int thisType=typeList.get(typeIndex);
			
			if(expression.startsWith("!"))
			{
				_isNot=true;
				expression=expression.substring(1);
			}
			
			int index=expression.indexOf("(");
			
			String funcName;
			String content;
			
			if(index==-1)
			{
				funcName=expression;
				content="";
			}
			else
			{
				int endIndex=StringUtils.getAnotherIndex(expression,'(',')',index);
				
				if(endIndex==-1)
				{
					Ctrl.throwError("表达式错误,小括号少结尾");
					return;
				}
				
				funcName=expression.substring(0,index);
				content=expression.substring(index+1,endIndex);
			}
			
			
			_funcName=funcName;
			
			switch(funcName)
			{
				case "for":
				{
					if(thisType!=ConfigFieldType.Array)
					{
						Ctrl.throwError("表达式错误,for需要array类型");
						return;
					}
					
					_childFuncs=createCheckFunc(_typeList,content,_typeIndex+1);
				}
					break;
				case "get":
				{
					if(thisType!=ConfigFieldType.Array)
					{
						Ctrl.throwError("表达式错误,get需要array类型");
						return;
					}
					
					int dIndex=content.indexOf(",");
					
					if(dIndex==-1)
					{
						Ctrl.throwError("表达式错误,get需要逗号分隔");
						return;
					}
					
					_getIndex=Integer.parseInt(content.substring(0,dIndex));
					_childFuncs=createCheckFunc(_typeList,content.substring(dIndex+1),_typeIndex+1);
				}
					break;
				case "getF":
				{
					int dIndex=content.indexOf(",");
					
					if(dIndex==-1)
					{
						Ctrl.throwError("表达式错误,getF需要逗号分隔");
						return;
					}
					
					_getFieldName=content.substring(0,dIndex);
					_childFuncs=createCheckFunc(_typeList,content.substring(dIndex+1),_typeIndex+1);
				}
				break;
				case "getK":
				case "getV":
				{
					if(thisType!=ConfigFieldType.DInt)
					{
						Ctrl.throwError("表达式错误,getK/getV需要DInt类型");
						return;
					}
					
					_childFuncs=createCheckFunc(_typeList,content,_typeIndex);
				}
					break;
				case "switch":
				case "switchArr":
				{
					if(thisType!=ConfigFieldType.Array)
					{
						Ctrl.throwError("表达式错误,switch需要array类型");
						return;
					}
					
					int dIndex=content.indexOf(",");
					
					if(dIndex==-1)
					{
						Ctrl.throwError("表达式错误,switch需要逗号分隔");
						return;
					}
					
					_getIndex=Integer.parseInt(content.substring(0,dIndex));
					
					String last=content.substring(dIndex+1);
					
					_switchFuncs=new IntObjectMap<>();
					
					if(!last.isEmpty())
					{
						String[] arr=StringUtils.splitEx(last,'(',')',',');
						
						for(String v:arr)
						{
							int tIndex=v.indexOf(":");
							
							if(tIndex==-1)
							{
								Ctrl.throwError("表达式错误,switch后续需要冒号分隔");
								return;
							}
							
							int ii=Integer.parseInt(v.substring(0,tIndex));
							
							_switchFuncs.put(ii,createCheckFunc(_typeList,v.substring(tIndex+1),_typeIndex));
						}
					}
					
				}
					break;
				case "switchField":
				{
					if(thisType!=ConfigFieldType.Array)
					{
						Ctrl.throwError("表达式错误,switchField需要array类型");
						return;
					}
					
					int dIndex=content.indexOf(",");
					
					if(dIndex==-1)
					{
						Ctrl.throwError("表达式错误,switchField需要逗号分隔");
						return;
					}
					
					_getFieldName=content.substring(0,dIndex);
					
					String last=content.substring(dIndex+1);
					
					_switchFuncs=new IntObjectMap<>();
					
					if(!last.isEmpty())
					{
						String[] arr=StringUtils.splitEx(last,'(',')',',');
						
						for(String v:arr)
						{
							int tIndex=v.indexOf(":");
							
							if(tIndex==-1)
							{
								Ctrl.throwError("表达式错误,switchField后续需要冒号分隔");
								return;
							}
							
							int ii=Integer.parseInt(v.substring(0,tIndex));
							
							_switchFuncs.put(ii,createCheckFunc(_typeList,v.substring(tIndex+1),_typeIndex));
						}
					}
					
				}
					break;
				case "compare":
				{
					String[] args=content.split(",");
					
					if(args.length!=2)
					{
						Ctrl.throwError("表达式错误，compare需要逗号分隔");
						return;
					}
					
					_compareValue=valueToDouble(args[0]);
					_compareResult=valueToInt(args[1]);
				}
					break;
				case "infield":
				{
					String[] args=content.split(",");
					
					if(args.length!=2)
					{
						Ctrl.throwError("表达式错误,infield需要逗号分隔");
						return;
					}
					
					_inTableName=args[0];
					_inFieldName=args[1];
					
					HConfigValueData hData=getHConfigValueDataFromG(upperNameToUseName(args[0]));
					
					if(hData!=null)
					{
						_fieldValueSet=hData.getFieldValueSet(args[1]);
					}
				}
					break;
				case "inkey":
				{
					_inTableName=content;
					
					HConfigValueData hData=getHConfigValueDataFromG(upperNameToUseName(content));
					
					if(hData!=null)
					{
						_primaryKeySet=hData.getPrimaryKeySet();
					}
				}
					break;
				case "enum":
				{
					String[] args=content.split(",");
					
					if(args.length!=1)
					{
						Ctrl.throwError("表达式错误,enum需要逗号分隔");
						return;
					}
					
					String useName=upperNameToUseName(args[0]);
					
					_inTableName=args[0];
					
					HConfigValueData hData=getHConfigValueDataFromG("enum/"+useName);
					
					if(hData!=null)
					{
						_fieldValueSet=hData.getFieldValueSet("id");
					}
					
				}
					break;
				case "none":
				case "<=0":
				{
				
				}
					break;
				case "dkey":
				{
					_inTableName=content;
					HConfigValueData hData=getHConfigValueDataFromG(upperNameToUseName(content));
					
					if(hData!=null)
					{
						_primaryKeySet=hData.getPrimaryKeySet();
					}
				}
					break;
				case "arrkey":
				{
					int sIndex=content.indexOf(",");
					
					if(sIndex==-1)
					{
						Ctrl.throwError("表达式错误,arrkey需要逗号分隔");
						return;
					}
					
					_startIndex=Integer.parseInt(content.substring(0,sIndex));
					
					int eIndex=content.indexOf(",",sIndex+1);
					
					if(eIndex==-1)
					{
						Ctrl.throwError("表达式错误,arrkey后续需要逗号分隔");
						return;
					}
					
					_endIndex=Integer.parseInt(content.substring(sIndex+1,eIndex));
					
					HConfigValueData hData=getHConfigValueDataFromG(upperNameToUseName(_inTableName=content.substring(eIndex+1)));
					
					if(hData!=null)
					{
						_primaryKeySet=hData.getPrimaryKeySet();
					}
				}
					break;
				//引用跳转
				case "ref":
				{
					String[] args=content.split(",");
					
					if(args.length!=2)
					{
						Ctrl.throwError("表达式错误,ref需要逗号分隔");
						return;
					}
					
					_inTableName=args[0];
					_inFieldName=args[1];
					
					_refConfig=getHConfigValueDataFromG(upperNameToUseName(_inTableName));
				}
					break;
				case "refEnum":
				{
					_inTableName=content;
					_inFieldName="enum";
					_refConfig=getHConfigValueDataFromG("enum/"+upperNameToUseName(_inTableName));
				}
					break;
				default:
				{
					Ctrl.throwError("不识别的方法名",funcName);
				}
					break;
			}
		}
		
		/** 执行子组 */
		private boolean checkChildren(String value)
		{
			return checkByFuncs(_childFuncs,value);
		}
		
		private boolean checkByFuncs(CheckFunc[][] funcs,String value)
		{
			String reason=null;
			
			for(int i=0;i<funcs.length;i++)
			{
				boolean tt=true;
				
				for(CheckFunc v:funcs[i])
				{
					if(!v.checkValue(value))
					{
						if(v._reason!=null)
							reason=v._reason;
						
						tt=false;
						break;
					}
				}
				
				if(tt)
				{
					return true;
				}
			}
			
			if(_reason==null)
				_reason=reason;
			
			return false;
		}
		
		/** 抛出属性错误 */
		private void recordCurrentFieldError(String msg)
		{
			recordFieldError(msg,_currentField,_currentRow,_currentValue);
		}
		
		/** 抛出属性错误 */
		private void recordFieldError(String msg,ConfigFieldData field,int row,String value)
		{
			_reason=msg + ",表:" + field.clsData.fName + ",字段:" + field.name + ",行:" + (row + 1) + ",值:" + value;
		}
		
		/** 检查值 */
		public boolean checkValue(String value)
		{
			_reason=null;
			
			int fType=_typeList.get(_typeIndex);
			
			if(ConfigFieldType.isNumberType(fType) && value.isEmpty())
			{
				//置0
				value="0";
			}
			
			switch(_funcName)
			{
				case "root":
				{
					if(!checkChildren(value))
					{
						return _isNot;
					}
				}
					break;
				case "for":
				{
					//空数组
					if(value.isEmpty())
						return true;
					
					String ch=_arrSplitChars[_typeList.length() - 2 - _typeIndex];
					String[] arr=value.split(ch);
					
					for(String v:arr)
					{
						if(!checkChildren(v))
						{
							return _isNot;
						}
					}
				}
					break;
				case "get":
				{
					String ch=_arrSplitChars[_typeList.length() - 2 - _typeIndex];
					String[] arr=value.split(ch);
					
					if(arr.length<=_getIndex)
					{
						throwCurrentFieldError("get时数组长度不足");
						return false;
					}
					
					String v=arr[_getIndex];
					
					if(!checkChildren(v))
					{
						return _isNot;
					}
				}
					break;
				case "getK":
				case "getV":
				{
					String[] arr=value.split(_dIntSplitChar);
					
					if(arr.length<2)
					{
						throwCurrentFieldError("getK时长度不足");
						return false;
					}
					
					String v=_funcName.equals("getK") ? arr[0] : arr[1];
					
					if(!checkChildren(v))
					{
						return _isNot;
					}
				}
					break;
				case "getF":
				{
					ConfigFieldData fData=_currentHConfigData.fieldDic.get(_getFieldName);
					
					if(fData==null)
					{
						throwCurrentFieldError("getF时找不到字段:"+_getFieldName);
						return false;
					}
					
					String v=_currentHConfigData.firstValues[_currentRow][fData.index];
					
					if(!checkChildren(v))
					{
						return _isNot;
					}
				}
					break;
				case "switch":
				case "switchArr":
				{
					String ch=_arrSplitChars[_typeList.length() - 2 - _typeIndex];
					String[] arr=value.split(ch);
					
					if(arr.length<=_getIndex)
					{
						throwCurrentFieldError("switch时数组长度不足");
						return false;
					}
					
					int ii=Integer.parseInt(arr[_getIndex]);
					
					CheckFunc[][] funcs=_switchFuncs.get(ii);
					
					if(funcs!=null)
					{
						if(!checkByFuncs(funcs,value))
							return _isNot;
					}
				}
					break;
				case "switchField":
				{
					String ch=_arrSplitChars[_typeList.length() - 2 - _typeIndex];
					String[] arr=value.split(ch);
					
					if(arr.length<=_getIndex)
					{
						throwCurrentFieldError("switchField时数组长度不足");
						return false;
					}
					
					ConfigFieldData fData=_currentHConfigData.fieldDic.get(_getFieldName);
					
					if(fData==null)
					{
						throwCurrentFieldError("getF时找不到字段:"+_getFieldName);
						return false;
					}
					
					String v=_currentHConfigData.firstValues[_currentRow][fData.index];
					
					int ii=Integer.parseInt(v);
					
					CheckFunc[][] funcs=_switchFuncs.get(ii);
					
					if(funcs!=null)
					{
						if(!checkByFuncs(funcs,value))
							return _isNot;
					}
				}
					break;
				case "compare":
				{
					double v=valueToDouble(value);
					
					if((Double.compare(v,_compareValue)==_compareResult)==_isNot)
					{
						recordCurrentFieldError("compare不满足");
						
						return false;
					}
					else
					{
						return true;
					}
				}
				case "infield":
				{
					if(_fieldValueSet==null)
					{
						throwCurrentFieldError("infield不满足,缺少表或缺少字段:"+_inTableName+":"+_inFieldName);
						return false;
					}
					
					//不存在
					if((_fieldValueSet.contains(value))==_isNot)
					{
						recordCurrentFieldError("infield不满足:"+_inTableName);
						
						return false;
					}
					else
					{
						return true;
					}
				}
				case "inkey":
				{
					if(_primaryKeySet==null)
					{
						throwCurrentFieldError("inkey不满足,缺少表:"+_inTableName);
						return false;
					}
					
					//不存在
					if((_primaryKeySet.contains(value))==_isNot)
					{
						recordCurrentFieldError("inkey不满足:"+_inTableName);
						
						return false;
					}
					else
					{
						return true;
					}
				}
				case "enum":
				{
					if(_fieldValueSet==null)
					{
						throwCurrentFieldError("enum不满足,缺少表"+_inTableName);
						return false;
					}
					
					//不存在
					if(_fieldValueSet.contains(value)==_isNot)
					{
						recordCurrentFieldError("enum不满足:"+_inTableName);
						
						return false;
					}
					else
					{
						return true;
					}
				}
				case "none":
				{
					boolean re=false;
					
					if(value.isEmpty())
						re=true;
					else if(ConfigFieldType.isNumberType(fType))
					{
						if(valueToInt(value)==0)
							re=true;
					}
					
					if(re==_isNot)
					{
						recordCurrentFieldError("none不满足");
						
						return false;
					}
					else
					{
						return true;
					}
				}
				case "<=0":
				{
					if(!ConfigFieldType.isNumberType(fType))
					{
						throwCurrentFieldError("字段类型不匹配");
						return false;
					}
					
					boolean re=false;
					
					if((valueToInt(value)<=0))
						re=true;
					
					
					if(re==_isNot)
					{
						recordCurrentFieldError("<=0不满足");
						
						return false;
					}
					else
					{
						return true;
					}
				}
				case "dkey":
				{
					if(fType!=ConfigFieldType.DInt)
					{
						throwCurrentFieldError("字段类型不匹配");
						return false;
					}
					
					if(_primaryKeySet==null)
					{
						throwCurrentFieldError("dkey不满足,缺少表:"+_inTableName);
						return false;
					}
					
					String v=value.replace(_dIntSplitChar,"_");
					
					//不存在
					if(_primaryKeySet.contains(v)==_isNot)
					{
						recordCurrentFieldError("dkey不满足:"+_inTableName);
						
						return false;
					}
					else
					{
						return true;
					}
				}
				case "arrkey":
				{
					if(fType!=ConfigFieldType.Array)
					{
						throwCurrentFieldError("字段类型不匹配");
						return false;
					}
					
					String ch=_arrSplitChars[_typeList.length() - 2 - _typeIndex];
					String[] arr=value.split(ch);
					
					if(arr.length<=_endIndex)
					{
						throwCurrentFieldError("get时数组长度不足");
						return false;
					}
					
					StringBuilder sb=new StringBuilder();
					
					for(int i=_startIndex;i<=_endIndex;++i)
					{
						if(i>_startIndex)
						{
							sb.append('_');
						}
						
						sb.append(arr[i]);
					}
					
					if(_primaryKeySet==null)
					{
						recordCurrentFieldError("arrkey不满足,缺少表:"+_inTableName);
						return false;
					}
					
					//不存在
					if(_primaryKeySet.contains(sb.toString())==_isNot)
					{
						recordCurrentFieldError("arrkey不满足:"+_inTableName);
						
						return false;
					}
					else
					{
						return true;
					}
				}
				case "ref":
				case "refEnum":
				{
					if(_refConfig==null)
					{
						throwCurrentFieldError("ref不满足,表缺少:"+_inTableName);
						return false;
					}
					
					if(_refConfig.doCheckValue(_inFieldName,value)==_isNot)
					{
						_reason=_refConfig._reason;
						return false;
					}
					else
					{
						return true;
					}
				}
			}
			
			return !_isNot;
		}
	}
}
