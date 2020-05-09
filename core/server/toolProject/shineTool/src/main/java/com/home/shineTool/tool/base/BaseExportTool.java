package com.home.shineTool.tool.base;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.SList;
import com.home.shine.support.collection.SMap;
import com.home.shine.support.collection.SSet;
import com.home.shine.support.collection.StringIntMap;
import com.home.shine.utils.FileUtils;
import com.home.shine.utils.StringUtils;
import com.home.shineTool.constlist.CodeType;
import com.home.shineTool.constlist.VarType;
import com.home.shineTool.constlist.VisitType;
import com.home.shineTool.global.ShineToolSetting;
import com.home.shineTool.reflect.FieldInfo;
import com.home.shineTool.reflect.MainMethodInfo;
import com.home.shineTool.reflect.MethodInfo;
import com.home.shineTool.reflect.cls.ClassInfo;
import com.home.shineTool.reflect.code.CodeInfo;
import com.home.shineTool.reflect.code.CodeWriter;
import com.home.shineTool.tool.FileRecordTool;
import com.home.shineTool.global.ShineToolSetting.GroupPackageData;
import com.home.shineTool.tool.base.RecordDataClassInfo.DataFieldInfo;
import com.home.shineTool.tool.data.DataDefineTool;
import com.home.shineTool.tool.data.DataMakerTool;
import com.home.shineTool.tool.data.DataMessageBindTool;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/** 类导出工具基类(从一目录读类文件,然后逐一生成其他文件),包括文件更改删除判定 */
public abstract class BaseExportTool
{
	/** 是否有旧记录输入(isRelease用) */
	public static boolean hasOldRecordDic=false;
	/** 旧记录组(输入类QName为key) */
	public static SMap<String,RecordDataClassInfo> oldRecordClsDic=new SMap<>();
	/** 新记录组(输入类QName为key)(全组) */
	public static SMap<String,RecordDataClassInfo> newRecordClsDic=new SMap<>();
	/** 记录组的cs类型统计(做 c/s 选择输出out) */
	public static StringIntMap recordClsCSTypeDic=new StringIntMap();
	
	/** 回车 */
	public static final String Enter=ClassInfo.Enter;
	
	/** 是否是配置表的工具 */
	protected boolean _isConfigTool=false;
	
	/** 是否需要输出 */
	protected boolean _needTrace=true;
	/** 默认名字后缀 */
	protected String _defaultNameTail="Data";
	
	/** 是否需要记录 */
	private boolean _needRecord=true;
	
	/** 文件记录 */
	protected FileRecordTool _fileRecord;
	/** 数据源码是java */
	protected int _sourceCode=CodeType.Java;
	/** 定义组 */
	protected List<DataDefineTool> _defines=new ArrayList<>();
	private IntObjectMap<DataDefineTool> _defineDic=new IntObjectMap<>();
	/** 构造组 */
	private IntObjectMap<DataMakerTool> _makerDic=new IntObjectMap<>();
	/** 消息绑定组 */
	private IntObjectMap<DataMessageBindTool> _messageBindDic=new IntObjectMap<>();
	
	/** 输出组 */
	protected List<BaseOutputInfo> _outputs=new ArrayList<>();
	/** 输出组字典 */
	protected IntObjectMap<BaseOutputInfo> _outputDic=new IntObjectMap<>();
	
	//input
	/** 是否只第一次执行(已存在的output不管) */
	protected boolean _onlyFirst=false;
	/** 原生的byte[]是否不转义 */
	protected boolean _useNatureByteArr=true;
	/** 是否继承默认用BaseData */
	protected boolean _isDefaultExtendsUseData=true;
	
	/** 结尾标记 */
	protected String _mark="DO";
	/** 输入根路径 */
	protected String _inputRootPath;
	/** 输入根路径的文件 */
	protected File _inputRootFile;
	/** 输入根native路径 */
	private String _inputRootNativePath;
	/** 输入根包头 */
	protected String _inputRootPackge;
	/** 输入过滤(检查的类输入文件) */
	protected InputFilter _inputFilter;
	
	/** 输入文件字典(存的原始输入文件) */
	protected Map<String,File> _inputFileDic=new HashMap<>();
	
	/** 执行过的文件记录 */
	protected SSet<String> _didFiles=new SSet<>();
	
	/** 本次全部文件的QName组 */
	protected SSet<String> _allInputQNameSet=new SSet<>();
	
	/** 输入类字典(执行过的)(key是完全限定名) */
	protected SMap<String,ClassInfo> _inputClassDic=new SMap<>();
	
	/** 执行过的类->文件映射 */
	private SMap<ClassInfo,File> _executedInputCls=new SMap<>();
	
	/** 输入文件记录组 */
	private SList<InputInfo> _inputFileInfos=new SList<>();
	
	//nowInput
	
	/** 当前输入文件(类输入文件,不同于原始输入文件) */
	protected File _inputFile;
	/** 当前输入Class */
	protected ClassInfo _inputCls;
	/** 输出Class的基类 */
	protected ClassInfo _superInputCls;
	/** 旧属性Set(用来判断位置) */
	private Set<String> _oldInputFieldSet;
	/** 旧属性Set2(用来删除) */
	private Set<String> _oldInputFieldSet2;
	/** 旧输入文件(存在判断 当前输入是否被移动过 ) */
	protected File _oldInputFile;
	/** 源code类型 */
	protected CodeInfo _inCode;
	/** 是否需要写序列化 */
	protected boolean _customSerialize=false;
	
	//nowOutput
	
	/** 当前输出信息 */
	protected BaseOutputInfo _outputInfo;
	/** 输出class */
	protected ClassInfo _outputCls;
	/** 输出classPath */
	protected String _outputClsPath;
	/** 代码信息(output) */
	protected CodeInfo _code;
	
	//mainMethod
	
	protected MainMethodInfo _mainMethod;
	/** 构造函数 */
	protected CodeWriter _mainMethodWriter;
	
	protected MethodInfo _createMethod;
	/** 构造函数 sb */
	protected CodeWriter _createWriter;
	
	//record
	/** 旧记录 */
	private RecordDataClassInfo _oldRecordCls;
	/** 新记录 */
	protected RecordDataClassInfo _recordCls;
	
	public BaseExportTool()
	{
		
	}
	
	/** 是否需要输出 */
	public void setNeedTrace(boolean bool)
	{
		_needTrace=bool;
	}
	
	/** 设置文件记录 */
	public void setFileRecordTool(FileRecordTool fileRecord)
	{
		_fileRecord=fileRecord;
	}
	
	/** 添加定义 */
	public void addDefine(int group,DataDefineTool define)
	{
		_defines.add(define);
		_defineDic.put(group,define);
	}
	
	/** 添加构造 */
	public void addMaker(int group,DataMakerTool maker)
	{
		_makerDic.put(group,maker);
	}
	
	/** 添加消息绑定 */
	public void addMessageBind(int group,DataMessageBindTool tool)
	{
		_messageBindDic.put(group,tool);
	}
	
	/** 设置输入 */
	public void setInput(String path)
	{
		setInput(path,"");
	}
	
	/** 设置输入 */
	public void setInput(String path,String mark)
	{
		setInput(path,mark,null);
	}
	
	/** 设置输入 */
	public void setInput(String path,String mark,InputFilter filter)
	{
		setInput(path,mark,filter,CodeType.Java);
	}
	
	/** 设置输入 */
	public void setInput(String path,String mark,InputFilter filter,int sourceCode)
	{
		_inputRootPath=FileUtils.fixPath(path);
		
		_inputRootFile=new File(_inputRootPath);
		_inputRootNativePath=FileUtils.getNativePath(_inputRootFile);
		
		_sourceCode=sourceCode;
		_inCode=CodeInfo.getCode(_sourceCode);
		_inputFilter=filter;
		
		if(mark!=null && !mark.isEmpty())
		{
			_mark=mark;
		}
		
		_inputRootPackge=FileUtils.getPathPackage(_inputRootPath);
	}
	
	/** 添加输出 */
	public void addOutput(BaseOutputInfo output)
	{
		output.rootPackage=FileUtils.getPathPackage(output.path);
		
		_outputs.add(output);
		_outputDic.put(output.group,output);
	}
	
	/** 获取输出信息 */
	public BaseOutputInfo getOutputInfo(int group)
	{
		return _outputDic.get(group);
	}
	
	public String getMark()
	{
		return _mark;
	}
	
	public String getInputRootPath()
	{
		return _inputRootPath;
	}
	
	//元方法
	
	public String getCNameByFile(File file)
	{
		return getCName(FileUtils.getFileFrontName(file.getName()));
	}
	
	/** 获取类名简名(去掉mark)(并且首字母大写) */
	protected String getCNameByClassName(String name)
	{
		return getCName(name);
	}
	
	/** 获取类名简名(去掉mark)(并且首字母大写) */
	protected String getCName(String name)
	{
		if(!name.endsWith(_mark))
		{
			return StringUtils.ucWord(name);
		}
		
		return StringUtils.ucWord(name.substring(0,name.length() - _mark.length()));
	}
	
	/** 添加输入文件 */
	protected void addInputClass(ClassInfo cls)
	{
		_inputClassDic.put(cls.getQName(),cls);
	}
	
	/** 通过完全限定名获取类 */
	private ClassInfo getInputClass(String qName)
	{
		return _inputClassDic.get(qName);
	}
	
	/** 执行过的input类对文件映射 */
	public SMap<ClassInfo,File> getExecutedInputClsDic()
	{
		return _executedInputCls;
	}
	
	/** 添加一条定义(0为主) */
	protected int addOneDefine(String cName,String des)
	{
		return doAddOneDefine(cName,des);
	}
	
	protected int doAddOneDefine(String cName,String des)
	{
		if(_defines.isEmpty())
		{
			return -1;
		}
		
		int num=-1;
		
		for(DataDefineTool define : _defines)
		{
			num=define.addOne(cName,des,num);
		}
		
		return num;
	}
	
	/** 添加一条make */
	private void addOneMake(String cName,String clsName,String packageStr)
	{
		if(_makerDic.isEmpty())
			return;
		
		if(_outputInfo.makeIndex!=-1)
		{
			_makerDic.get(_outputInfo.makeIndex).addOne(cName,clsName,packageStr);
		}
	}
	
	private void removeOneMake(String cName)
	{
		if(_makerDic.isEmpty())
			return;
		
		if(_outputInfo.makeIndex!=-1)
		{
			_makerDic.get(_outputInfo.makeIndex).removeOne(cName);
		}
	}
	
	/** 添加一条绑定 */
	private void addOneMessageBind(String cName,String[] requests)
	{
		if(_messageBindDic.isEmpty())
			return;
		
		if(_outputInfo.defineIndex!=-1)
		{
			DataMessageBindTool tool=_messageBindDic.get(_outputInfo.defineIndex);
			
			if(tool!=null)
			{
				tool.addOne(cName,requests);
			}
		}
	}
	
	/** 获取输出文件路径 */
	public String getOutFilePath(int group,File inFile)
	{
		BaseOutputInfo outputInfo=getOutputInfo(group);
		
		return toGetOutPath(outputInfo,this,inFile);
	}
	
	private String toGetOutPath(BaseOutputInfo outputInfo,BaseExportTool tool,File inFile)
	{
		String fPath=FileUtils.getNativePath(inFile);
		
		if(fPath.startsWith(_inputRootNativePath))
		{
			String cName=getCNameByFile(inFile);
			
			if(cName.isEmpty())
			{
				Ctrl.throwError("名字为空");
				return null;
			}
			
			//去掉名字了
			String temp=fPath.substring(_inputRootNativePath.length(),fPath.length() - inFile.getName().length());
			
			String clsName=tool.getOutClassNameByInfo(outputInfo,cName);
			
			return FileUtils.fixPath((outputInfo.path + temp + clsName + "." + CodeType.getExName(outputInfo.codeType)));
		}
		else
		{
			Ctrl.throwError("不可能," + fPath + " _inputRootNativePath:"+_inputRootNativePath);
		}
		
		return "";
	}
	
	/** 获取输出文件(inFile:类输入文件) */
	protected String getOutFilePath(File inFile)
	{
		String inClsName=FileUtils.getFileFrontName(inFile.getName());
		
		if(_inputFilter!=null)
		{
			int re=_inputFilter.check(inClsName);
			
			//过滤
			if(re==2 && _outputInfo.makeIndex==-1)
			{
				return null;
			}
		}
		
		return toGetOutPath(_outputInfo,this,inFile);
	}
	
	protected String getOutPathFromParent(BaseExportTool parent,int group,File inFile)
	{
		return toGetOutPath(parent.getOutputInfo(group),parent,inFile);
	}
	
	/** 获取输出类名(传入CName) */
	protected String getOutClassNameByCName(String name)
	{
		return getOutClassNameByInfo(_outputInfo,name);
	}
	
	/** 获取输出类名 */
	protected String getOutClassNameByInfo(BaseOutputInfo outInfo,String name)
	{
		if(outInfo.nameTail.isEmpty())
		{
			return name + _defaultNameTail;
		}
		
		return name + outInfo.nameTail;
	}
	
	/** 获取输出包名 */
	protected String getOutPackageStr(String inPackageStr)
	{
		return getOutPackageStrByInfo(_outputInfo,inPackageStr);
	}
	
	/** 获取输出包名 */
	private String getOutPackageStrByInfo(BaseOutputInfo outInfo,String inPackageStr)
	{
		if(inPackageStr.startsWith(_inputRootPackge))
		{
			String tt;
			
			if(_inputRootPackge.isEmpty())
			{
				if(inPackageStr.isEmpty())
					tt="";
				else
					tt="."+inPackageStr;
			}
			else
			{
				tt=inPackageStr.substring(_inputRootPackge.length(),inPackageStr.length());
			}
			
			return outInfo.rootPackage + tt;
		}
		
		List<GroupPackageData> list=ShineToolSetting.groupPackageChangeDic.get(outInfo.group);
		
		if(list!=null)
		{
			for(GroupPackageData data : list)
			{
				if(inPackageStr.startsWith(data.inputRootPackge))
				{
					return data.outRootPackage + inPackageStr.substring(data.inputRootPackge.length(),inPackageStr.length());
				}
			}
		}
		
		return inPackageStr;
	}
	
	/** 通过info获得输出类完全限定名 */
	protected String getOutClassQNameByGroup(int group,String inQName)
	{
		return toGetOutClassQNameByGroup(group,inQName);
	}
	
	protected String toGetOutClassQNameByGroup(int group,String inQName)
	{
		List<GroupPackageData> list=ShineToolSetting.groupPackageChangeDic.get(group);
		
		if(list!=null)
		{
			for(GroupPackageData data : list)
			{
				//在里面
				if(inQName.startsWith(data.inputRootPackge) && inQName.endsWith(data.mark))
				{
					String last=data.outRootPackage + inQName.substring(data.inputRootPackge.length(),inQName.length());
					
					last=last.substring(0,last.length() - data.mark.length()) + data.tail;
					
					return last;
				}
			}
		}
		
		return "";
	}
	
	/** 输入过滤 */
	public static interface InputFilter
	{
		/** 输入过滤方法(0:正常(都有)1:request,response都无,2:只有response(makerIndex==-1的不导出))(1为跳过) */
		int check(String fin);
	}
	
	/** 把in的信息能复制的复制到out上(visitType,name) */
	protected void copyFieldInfoForNV(FieldInfo inField,FieldInfo outField)
	{
		copyFieldInfoByNV(inField.name,inField.visitType,outField);
	}
	
	/** 把in的信息能复制的复制到out上(visitType,name) */
	protected void copyFieldInfoByNV(String name,int visitType,FieldInfo outField)
	{
		if(visitType!=VisitType.None)
		{
			outField.visitType=visitType;
		}
		else
		{
			outField.visitType=_outputInfo.defaultVarVisitType;
		}
		
		if(outField.visitType==VisitType.Public)
		{
			outField.name=name;
		}
		else
		{
			outField.name="_" + name;
		}
	}
	
	/** 获取输出属性名 */
	protected String getOutFieldNameByVisitType(String name,int visitType)
	{
		FieldInfo out=new FieldInfo();
		copyFieldInfoByNV(name,visitType,out);
		
		return out.name;
	}
	
	/** 获取使用输出属性名 */
	protected String getUseOutFieldNameByVisitType(String name,int visitType)
	{
		FieldInfo out=new FieldInfo();
		copyFieldInfoByNV(name,visitType,out);
		
		return out.getUseFieldName();
	}
	
	/** 获取输入类(绝对,没有就创建) */
	protected ClassInfo getInputClsAbs(String qName)
	{
		//相同包
		if(qName.startsWith(_inputRootPackge))
		{
			ClassInfo cls=getInputClass(qName);
			
			if(cls!=null)
			{
				return cls;
			}
			
			String nn=qName.substring(_inputRootPackge.length(),qName.length());
			
			nn=nn.replaceAll("\\.","/");
			
			String path=FileUtils.fixPath(_inputRootPath) + nn + "." + CodeType.getExName(_sourceCode);
			
			cls=ClassInfo.getClassInfoFromPathAbs(path);
			
			addInputClass(cls);
			
			return cls;
		}
		
		return null;
	}

	/** 获取自定义 */
	protected String getCustomObjectTypeQName(String type,ClassInfo inputCls)
	{
		ClassInfo eCls=inputCls;

		String pStr=eCls.getImportPackage(type);

		//有基类
		while(pStr.isEmpty() && !eCls.extendsClsName.isEmpty())
		{
			eCls=getInputClsAbs(eCls.getExtendClsQName());

			if(eCls==null)
			{
				break;
			}
			else
			{
				pStr=eCls.getImportPackage(type);
			}
		}

		if(!pStr.isEmpty())
		{
			String inQName=pStr + "." + type;

			String ex="";

			if(_outputInfo.outputExExtendFunc!=null)
			{
				ex=_outputInfo.outputExExtendFunc.execute(inQName);

				if(!ex.isEmpty())
				{
					_outputCls.addImport(ex);

					return ex;
				}
			}

			String outQName=getOutClassQNameByGroup(_outputInfo.group,inQName);

			if(!outQName.isEmpty())
			{
				_outputCls.addImport(outQName);

				return outQName;
			}
		}

		return "";
	}

	/** 获取自定义 */
	protected String getCustomObjectType(String type,ClassInfo inputCls)
	{
		String qName=getCustomObjectTypeQName(type,inputCls);
		
		return StringUtils.getClassNameForQName(qName);
	}

	/** 该输入类是否需要继承(输入QName) */
	protected boolean isClassNeedExtends(String qName)
	{
		String clsName=StringUtils.getClassNameForQName(qName);
		
		switch(clsName)
		{
			case "BaseDO":
			case "TimePassDO":
				return true;
		}
		
		RecordDataClassInfo record=newRecordClsDic.get(qName);
		
		return record!=null && record.mayBeExtends;
	}
	
	
	/** 获取定义插件的类 */
	protected ClassInfo getDefineClass()
	{
		return getDefineClass(_outputInfo.defineIndex);
	}
	
	public DataDefineTool getDefineTool(int group)
	{
		if(group==-1)
		{
			return null;
		}
		
		return _defineDic.get(group);
	}
	
	public ClassInfo getDefineClass(int group)
	{
		if(group==-1)
		{
			return null;
		}
		
		return _defineDic.get(group).getCls();
	}
	
	//--执行--//
	
	public void setNeedRecord(boolean value)
	{
		_needRecord=value;
	}
	
	/** 执行全部(包括define和maker的write) */
	public void executeAll()
	{
		execute();
		
		for(DataDefineTool define : _defines)
		{
			define.write();
		}
		
		for(DataMakerTool maker : _makerDic)
		{
			maker.write();
		}
		
		for(DataMessageBindTool tool : _messageBindDic)
		{
			tool.write();
		}
	}
	
	/** 获取输入文件列表 */
	protected List<File> getInputFileList()
	{
		return FileUtils.getDeepFileList(_inputRootPath,CodeType.getExName(_sourceCode));
	}
	
	/** 执行 */
	public void execute()
	{
		List<File> list=getInputFileList();
		
		executeList(list);
	}
	
	/** 执行一组文件 */
	public void executeList(List<File> list)
	{
		preExecute();
		
		SMap<String,File> dic=new SMap<>();
		
		for(File f : list)
		{
			dic.put(f.getName(),f);
		}
		
		for(String key:dic.getSortedKeyList())
		{
			try
			{
				preExecuteFile(dic.get(key));
			}
			catch(Exception e)
			{
				Ctrl.throwError("preExecuteFile出错:"+key,e);
			}
		}
		
		toExecuteFileList();
		
		endExecute();
	}
	
	/** 记录一组文件 */
	public void recordFileList(List<File> list)
	{
		for(File f : list)
		{
			addRecord(f);
		}
	}
	
	/** 预备执行 */
	protected void preExecute()
	{
		recordGroupPackage();
		
		readLastPos();
		
		_inputClassDic.clear();
	}
	
	/** 结束执行 */
	protected void endExecute()
	{
		deleteLastFiles();
	}
	
	/** 执行一个文件 */
	public void executeFile(File f)
	{
		preExecuteFile(f);
		
		toExecuteFileList();
	}
	
	private void preExecuteFile(File f)
	{
		if(_needRecord)
		{
			_fileRecord.didFile(f);
		}
		
		String fName=FileUtils.getFileFrontName(f.getName());
		
		String cName=getCNameByFile(f);
		
		if(!fName.endsWith(_mark))
		{
			Ctrl.throwError("文件结尾出错:" + fName+" 需要以"+_mark+"为结尾命名");
			
			return;
		}
		
		//这个不能加,在H,V 里面都有用
		//		f=_inputFile;
		
		//跳过
		if(_inputFilter!=null && _inputFilter.check(fName)==1)
		{
			return;
		}
		
		boolean need=checkFileNeedDo(f);
		
		//强制执行
		if(ShineToolSetting.isAll)
		{
			need=true;
		}
		
		RecordDataClassInfo oldRecordCls=null;
		String oldQName=null;
		
		//没有记录
		if(!hasOldRecordDic)
		{
			String ex=getRecordEx(f);
			
			oldRecordCls=ex.isEmpty() ? null : RecordDataClassInfo.createByString(ex);
			
			if(oldRecordCls!=null)
			{
				oldQName=oldRecordCls.clsQName;
				addToRecordDic(oldRecordClsDic,oldRecordCls);
			}
		}
		else
		{
			ClassInfo inputCls=toGetInputClass(f);
			oldQName=inputCls.getQName();
			
			oldRecordCls=oldRecordClsDic.get(oldQName);
		}
		
		if(!need)
		{
			if(oldRecordCls!=null)
			{
				addNewRecord(oldRecordCls.copy());
				
				didInputCls(oldRecordCls.clsQName);
				
				_allInputQNameSet.add(oldRecordCls.clsQName);
			}
			else
			{
				if(oldQName==null)
				{
					ClassInfo inputCls=toGetInputClass(f);
					oldQName=inputCls.getQName();
				}
				
				Ctrl.print("没找到",cName);
				
				_allInputQNameSet.add(oldQName);
			}
			
			////删除输入记录(这句可以没有)
			//_inputFileDic.remove(cName);
			
			return;
		}
		
		_didFiles.add(f.getPath());
		
		File inputFile=toGetInputFile(f);
		ClassInfo inputCls=toGetInputClass(f);
		
		_allInputQNameSet.add(inputCls.getQName());
		
		_executedInputCls.put(inputCls,inputFile);
		
		RecordDataClassInfo newRecordCls=RecordDataClassInfo.createByClass(inputCls);
		
		InputInfo info=new InputInfo();
		info.file=f;
		info.inputFile=inputFile;
		info.cName=cName;
		info.inputCls=inputCls;
		info.oldRecord=oldRecordCls;
		info.newRecord=newRecordCls;
		
		addNewRecord(newRecordCls);
		
		_inputFileInfos.add(info);
		
		setInputCls(inputCls);
		
		preMakeInput();
	}
	
	protected void setInputCls(ClassInfo cls)
	{
		_inputCls=cls;
		
		if(!_inputCls.extendsClsName.isEmpty())
		{
			_superInputCls=getInputClsAbs(_inputCls.getExtendClsQName());
		}
		else
		{
			_superInputCls=null;
		}
	}
	
	protected boolean checkFileNeedDo(File f)
	{
		boolean need=true;
		
		if(_needRecord)
		{
			need=_fileRecord.isNewFile(f);
			
			if(!need)
			{
				need=checkFileNeedDoEx(f);
			}
		}
		
		return need;
	}
	
	protected void toExecuteFileList()
	{
		if(_inputFileInfos.isEmpty())
			return;
		
		_inputFileInfos.forEach(v->
		{
			try
			{
				toExecuteFile(v);
			}
			catch(Exception e)
			{
				Ctrl.throwError("toExecuteFile出错:"+v.cName,e);
			}
		});
		
		_inputFileInfos.clear();
	}
	
	private void toExecuteFile(InputInfo info)
	{
		File f=info.file;
		_inputFile=info.inputFile;
		setInputCls(info.inputCls);
		_oldRecordCls=info.oldRecord;
		_recordCls=info.newRecord;
		String cName=info.cName;
		
		if(_needTrace)
		{
			Ctrl.print("执行一个",_inputCls.clsName);
		}
		
		if(_isConfigTool)
		{
			//再一次,为了Config
			
			setInputCls(toGetInputClass(f));
		}
		
		_oldInputFile=null;
		
		File oldFile=_inputFileDic.get(cName);
		
		if(oldFile!=null)
		{
			//旧的不在了，被移动到新的了
			if(!oldFile.exists())
			{
				String p1=FileUtils.getNativePath(oldFile);
				String p2=FileUtils.getNativePath(f);
				
				if(!p1.equals(p2))
				{
					//旧输入文件
					_oldInputFile=toGetInputFile(oldFile);
				}
			}
		}
		
		//检测属性重名
		checkSuperFieldsSame(_recordCls);
		
		//添加记录
		addInputClass(_inputCls);
		
		//不是虚类
		if(!_inputCls.isAbstract)
		{
			addOneDefine(cName,_inputCls.clsDescribe);
		}
		
		//删除输入记录
		_inputFileDic.remove(cName);
		
		checkInput();
		
		toMakeInputFirst();
		//先构造
		toMakeInput();
		
		//此次再读取旧记录
		readExFromRecord();
		
		for(BaseOutputInfo out : _outputs)
		{
			makeOne(out);
		}
		
		//比较
		if(_recordCls.compareWithOld(_oldRecordCls))
		{
			_recordCls.hasBigModified=true;
		}
		
		toMakeOver();
		
		if(_needRecord)
		{
			addRecord(f);
		}
	}
	
	public SSet<String> getDidFiles()
	{
		return _didFiles;
	}
	
	/** 获取全部输入类的完全限定名 */
	public SSet<String> getAllInputQNameSet()
	{
		return _allInputQNameSet;
	}
	
	/** 检查文件是否需要执行额外步骤 */
	protected boolean checkFileNeedDoEx(File file)
	{
		return false;
	}
	
	/** 执行一个输入类 */
	protected void didInputCls(String qName)
	{
	
	}
	
	/** 获取文件的ex记录 */
	protected String getRecordEx(File f)
	{
		return _fileRecord.getFileEx(f);
	}
	
	private void addNewRecord(RecordDataClassInfo cls)
	{
		addToRecordDic(newRecordClsDic,cls);
	}
	
	/** 判断cls继承 */
	public static boolean isExtendFrom(RecordDataClassInfo cls,String qName)
	{
		if(cls==null)
			return false;
		
		String sName;
		if((sName=cls.superQName)==null || sName.isEmpty())
			return false;
		
		if(sName.equals(qName))
			return true;
		
		RecordDataClassInfo sCls=newRecordClsDic.get(sName);
		
		if(sCls!=null)
		{
			return isExtendFrom(sCls,qName);
		}
		
		return false;
	}
	
	private void addToRecordDic(SMap<String,RecordDataClassInfo> dic,RecordDataClassInfo cls)
	{
		RecordDataClassInfo oldCls=dic.get(cls.clsQName);
		
		if(oldCls!=null)
		{
			cls.childrenQNameList=oldCls.childrenQNameList;
		}
		
		dic.put(cls.clsQName,cls);
		
		if(!cls.superQName.isEmpty())
		{
			RecordDataClassInfo scls=dic.get(cls.superQName);
			
			if(scls==null)
			{
				dic.put(cls.superQName,scls=new RecordDataClassInfo());
				scls.isTemp=true;
			}
			
			scls.childrenQNameList.add(cls.clsQName);
		}
	}
	
	private static void checkSuperFieldsSame(RecordDataClassInfo cls)
	{
		checkSuperFields(cls,cls.superQName);
	}
	
	private static void checkSuperFields(RecordDataClassInfo cls,String superName)
	{
		if(superName.isEmpty())
			return;
		
		RecordDataClassInfo scls=newRecordClsDic.get(superName);
		
		if(scls!=null)
		{
			scls.fieldMap.forEach((k,v)->
			{
				//重复
				if(cls.fieldMap.contains(k))
				{
					Ctrl.throwError("存在重复的属性名:",k,StringUtils.getClassNameForQName(cls.clsQName)+"与基类"+StringUtils.getClassNameForQName(superName));
				}
			});
			
			checkSuperFields(cls,scls.superQName);
		}
		else
		{
			Ctrl.throwError("不该找不到基类");
		}
	}
	
	/** 添加记录 */
	protected void addRecord(File f)
	{
		_fileRecord.addFile(f,_recordCls.toString());
	}
	
	/** 通过输入File获取inputFile */
	protected File toGetInputFile(File f)
	{
		return f;
	}
	
	/** 通过输入File获取ClassInfo(注意这个f不是inputFile) */
	protected ClassInfo toGetInputClass(File f)
	{
		return ClassInfo.getClassInfoFromPathAbs(f.getPath());
	}
	
	/** 读取上次位置记录的 */
	protected void readLastPos()
	{
		_inputFileDic.clear();
		
		String path=FileUtils.fixPath2(_inputRootPath);
		
		for(String v : _fileRecord.getRecordDic().keySet())
		{
			if(v.startsWith(path))
			{
				File f=new File(v);
				
				String cName=getCNameByFile(f);
				
				if(_inputFileDic.containsKey(cName))
				{
					Ctrl.warnLog("输入文件fileRecord中，有重复的CName记录",cName,f.getPath());
				}
				
				_inputFileDic.put(cName,f);
			}
		}
	}
	
	/** 从文件中读取ex的信息 */
	private void readExFromRecord()
	{
		if(_oldRecordCls!=null)
		{
			_oldInputFieldSet=new HashSet<>();
			_oldInputFieldSet2=new HashSet<>();
			
			for(DataFieldInfo v : _oldRecordCls.fieldList)
			{
				_oldInputFieldSet.add(v.name);
				_oldInputFieldSet2.add(v.name);
			}
			
			//现在的
			for(String z : _inputCls.getFieldNameList())
			{
				_oldInputFieldSet2.remove(z);
			}
		}
		else
		{
			_oldInputFieldSet=null;
			_oldInputFieldSet2=null;
		}
	}
	
	/** 记录组包变化 */
	public void recordGroupPackage()
	{
		for(BaseOutputInfo out : _outputs)
		{
			toRecordGroupPackageByOutInfo(out,out.group);
		}
	}
	
	/** 记录某out信息为某group(补丁,为了robot) */
	public void recordGroupPackageByOutInfo(BaseOutputInfo out,int group)
	{
		out.rootPackage=FileUtils.getPathPackage(out.path);
		
		toRecordGroupPackageByOutInfo(out,group);
	}
	
	private void toRecordGroupPackageByOutInfo(BaseOutputInfo out,int group)
	{
		//输入路径为空,就不记录了
		if(_inputRootPackge.isEmpty())
		{
			return;
		}
		
		ShineToolSetting.groupPackageChangeDic.computeIfAbsent(group,k->new ArrayList<>()).add(new GroupPackageData(_inputRootPackge,out.rootPackage,_mark,out.nameTail));
	}
	
	protected void deleteLastFiles()
	{
		//看剩余
		for(Entry<String,File> kv : _inputFileDic.entrySet())
		{
			File lastF=kv.getValue();
			File lastInputFile=toGetInputFile(lastF);
			
			//跳过
			if(_inputFilter!=null && _inputFilter.check(FileUtils.getFileFrontName(lastInputFile.getName()))>0)
			{
				continue;
			}
			
			//不存在了
			if(!lastF.exists())
			{
				//if(_needTrace)
				//{
				//	Ctrl.print("删除一个",lastF.getName());
				//}
				
				for(BaseOutputInfo out : _outputs)
				{
					_outputInfo=out;
					
					if(_outputInfo.defineIndex!=-1)
					{
						_defineDic.get(_outputInfo.defineIndex).removeOne(kv.getKey());
					}
					
					if(_outputInfo.makeIndex!=-1)
					{
						_makerDic.get(_outputInfo.makeIndex).removeOne(kv.getKey());
						
						DataMessageBindTool tool=_messageBindDic.get(_outputInfo.makeIndex);
						
						if(tool!=null)
						{
							tool.removeOne(kv.getKey());
						}
					}
					
					String oldOutPath=getOutFilePath(lastInputFile);
					
					File outOutFile=new File(oldOutPath);
					
					if(outOutFile.exists())
					{
						beforeDeleteOutFile(outOutFile);
						
						Ctrl.print("删除一个文件",outOutFile.getName());
						
						//删除旧文件
						outOutFile.delete();
					}
				}
				
				//删文件记录
				removeLastFile(lastF);
			}
		}
	}
	
	/** 删除上一个文件 */
	protected void removeLastFile(File file)
	{
		_fileRecord.removePath(file.getPath());
	}
	
	/** 删除输出文件前 */
	protected void beforeDeleteOutFile(File file)
	{

	}
	
	/** 执行一个输出 */
	private void makeOne(BaseOutputInfo output)
	{
		_outputInfo=output;
		
		makeOneNext();
	}
	
	protected void makeOneNext()
	{
		_outputCls=null;
		
		String cName=getCNameByClassName(_inputCls.clsName);
		
		_outputClsPath=getOutFilePath(_inputFile);
		
		if(_outputClsPath.isEmpty())
		{
			return;
		}
		
		//优先使用当前目录的
		_outputCls=ClassInfo.getClassInfoFromPath(_outputClsPath);
		
		if(!checkNeedDoCurrent())
		{
			removeOneMake(cName);
			
			if(_outputCls!=null)
			{
				_outputCls.delete();
			}
			
			return;
		}
		
		if(_outputCls==null)
		{
			if(_oldInputFile!=null)
			{
				String oldOutPath=getOutFilePath(_oldInputFile);
				
				File outOutFile=new File(oldOutPath);
				
				if(outOutFile.exists())
				{
					if(_onlyFirst)
					{
						return;
					}
					
					ClassInfo oldOutCls=ClassInfo.getClassInfoFromPathAbs(oldOutPath);
					
					//删除旧文件
					outOutFile.delete();
					
					//旧文件
					_outputCls=oldOutCls;
				}
			}
		}
		else
		{
			if(_onlyFirst)
			{
				return;
			}
		}
		
		if(_outputCls==null)
		{
			_outputCls=ClassInfo.getClassInfoFromPathAbs(_outputClsPath);
		}
		
		_outputCls.packageStr=getOutPackageStr(_inputCls.packageStr);
		//_outputCls.clsName=getOutClassNameByCName(cName);
		
		_outputCls.clsDescribe=_inputCls.clsDescribe;
		
		//虚类不构造
		if(!_inputCls.isAbstract)
		{
			addOneMake(cName,_outputCls.clsName,_outputCls.packageStr);
			
			if(!_messageBindDic.isEmpty())
			{
				String av=_inputCls.getAnnotationValue("ResponseBind");
				
				if(av!=null)
				{
					if(av.startsWith("{"))
					{
						//去掉前后{}
						av=av.substring(1,av.length()-1);
					}
					
					if(av.isEmpty())
					{
						addOneMessageBind(cName,new String[0]);
					}
					else
					{
						String[] arr=av.split(",");
						
						String[] re=new String[arr.length];
						
						for(int i=0;i<arr.length;i++)
						{
							String v=arr[i];
							v=v.substring(0,v.indexOf('.')-_mark.length());//去掉class和MO
							re[i]=v;
						}
						
						addOneMessageBind(cName,re);
					}
				}
			}
			
		}
		
		_code=CodeInfo.getCode(_outputInfo.codeType);
		
		//先删了旧属性
		if(_oldInputFieldSet2!=null)
		{
			for(String z : _oldInputFieldSet2)
			{
				//两种都删
				if(_outputCls.removeFieldAndImport(z) || _outputCls.removeFieldAndImport("_" + z))
				{
					Ctrl.print("删除一个属性",_outputInfo.group,z);
				}
			}
		}
		
		toMakeOne();
		
		if(_outputInfo.outputExFunc!=null)
		{
			_outputInfo.outputExFunc.execute(_outputInfo.group,_inputCls,_outputCls);
		}
		
		_outputCls.writeToPath(_outputClsPath);
	}
	
	/** 检查当前是否需要执行 */
	protected boolean checkNeedDoCurrent()
	{
		return true;
	}
	
	/** 检查某cls是否不需要输出 */
	protected boolean checkInputClassDontOut(String qName,int mark)
	{
		return toGetInputClassOutMark(qName)==mark;
	}
	
	/** 检查某cls是否不需要输出 */
	private int toGetInputClassOutMark(String qName)
	{
		int vv=recordClsCSTypeDic.getOrDefault(qName,-1);
		
		if(vv!=-1)
		{
			return vv;
		}
		
		recordClsCSTypeDic.put(qName,0);
		int v=doGetInputClassOutMark(qName);
		recordClsCSTypeDic.put(qName,v);
		
		return v;
	}
	
	/** 检查某cls是否不需要输出 */
	private int doGetInputClassOutMark(String qName)
	{
		RecordDataClassInfo info=BaseExportTool.newRecordClsDic.get(qName);
		
		if(info==null)
			return 0;
			
		if(info.csType>0)
			return info.csType;
		
		if(!info.superQName.isEmpty())
		{
			int superMark=toGetInputClassOutMark(info.superQName);
			
			if(superMark>0)
				return superMark;
		}
		
		DataFieldInfo[] values=info.fieldList.getValues();
		DataFieldInfo v;
		
		for(int i=0,len=info.fieldList.size();i<len;++i)
		{
			v=values[i];
			
			if(!v.dataType.isEmpty())
			{
				int fieldMark=toGetInputClassOutMark(v.dataType);
				
				if(fieldMark>0)
					return fieldMark;
			}
		}
		
		return 0;
	}
	
	
	private void toMakeOne()
	{
		//define import
		ClassInfo cls=getDefineClass();
		
		if(cls!=null && !_inputCls.isAbstract)
		{
			_outputCls.addImport(cls.getQName());
		}
		
		//继承部分
		if(!_inputCls.extendsClsName.isEmpty())// && _inputCls.extendsClsName.endsWith(_mark)
		{
			if(_superInputCls!=null)
			{
				_outputCls.extendsClsName=getOutClassNameByCName(getCName(_superInputCls.clsName));
			}
			else
			{
				String outQName=getOutClassQNameByGroup(_outputInfo.group,_inputCls.getExtendClsQName());
				
				if(!outQName.isEmpty())
				{
					_outputCls.extendsClsName=StringUtils.getClassNameForQName(outQName);
				}
				
				//				String tt=getDefineFieldTransType(_inputCls.extendsClsName);
				//
				//				if(!tt.isEmpty())
				//				{
				//					_outputCls.extendsClsName=tt;
				//				}
			}
			
			String pp=_inputCls.getImportPackage(_inputCls.extendsClsName);
			
			if(!_outputCls.extendsClsName.isEmpty() && !pp.isEmpty())
			{
				_outputCls.addImport(getOutPackageStr(pp) + "." + _outputCls.extendsClsName);
			}
		}
		else
		{
			String ex="";
			
			if(_outputInfo.outputExExtendFunc!=null)
			{
				ex=_outputInfo.outputExExtendFunc.execute(_inputCls.getQName());
			}
			
			if(ex.isEmpty())
			{
				ex=toMakeExtend();
			}
			
			if(ex.isEmpty())
			{
				ex=_outputInfo.superQName;
			}
			
			if(ex.isEmpty())
			{
				if(_isDefaultExtendsUseData)
				{
					_outputCls.extendsClsName=ShineToolSetting.dataBaseName;
					_outputCls.addImport(ShineToolSetting.dataBaseQName);
				}
				else
				{
					_outputCls.extendsClsName="";
				}
			}
			else
			{
				_outputCls.extendsClsName=StringUtils.getClassNameForQName(ex);
				_outputCls.addImport(ex);
			}
		}
		
		toMakeFirst();
		
		toMakeMainMethod();
		
		toMakeBefore();
		toMakeFields();
		toMakeAfter();
		
		toMakeMainMethodEnd();
	}
	
	/** 补充构造继承 */
	protected String toMakeExtend()
	{
		return "";
	}
	
	protected void toMakeMainMethod()
	{
		String cName=getCNameByClassName(_inputCls.clsName);
		
		ClassInfo defineCls=getDefineClass();
		
		_mainMethod=new MainMethodInfo();
		_mainMethod.visitType=VisitType.Public;
		_mainMethod.name=_outputCls.clsName;
		
		_mainMethodWriter=_outputCls.createWriter();
		
		toMakeSuperMainMethod();
		
		//需要写
		if(defineCls!=null && !_inputCls.isAbstract)
		{
			_mainMethodWriter.writeVarSet(_outputCls.getFieldWrap(_outputInfo.defineVarName),defineCls.clsName + "." + cName);
		}
	}
	
	private void toMakeMainMethodEnd()
	{
		//有东西再弄构造函数
		if(!_mainMethodWriter.isEmpty())
		{
			_mainMethodWriter.writeEnd();
			
			_mainMethod.content=_mainMethodWriter.toString();
			
			_outputCls.addMainMethod(_mainMethod);
		}
	}
	
	/** 超类构造 */
	protected void toMakeSuperMainMethod()
	{
		
	}
	
	/** 构造开头 */
	protected void toMakeFirst()
	{
		if(!_outputInfo.staticDefineVarName.isEmpty())
		{
			ClassInfo defineCls=getDefineClass();
			
			//需要写
			if(defineCls!=null && !_inputCls.isAbstract)
			{
				String cName=getCNameByClassName(_inputCls.clsName);
				
				FieldInfo field=new FieldInfo();
				field.visitType=VisitType.Public;
				field.isStatic=true;
				field.isConst=true;
				field.type=_code.Int;
				field.name=_outputInfo.staticDefineVarName;
				field.describe="数据类型ID";
				field.defaultValue=defineCls.clsName + "." + cName;
				
				_outputCls.addField(field,0);
			}
		}
	}
	
	private void checkInput()
	{
		for(String name:_inputCls.getFieldNameList())
		{
			if(ShineToolSetting.usedInputFieldName.contains(name))
			{
				Ctrl.throwError("输入类有非法字段名,class:",_inputCls.clsName,"fieldName:",name);
			}
		}
	}
	
	/** 构造输入前(无论该inputCls是否需要执行，主要为了PartListTool) */
	protected void toMakeInputFirst()
	{
	
	}
	
	/** 预备执行输入部分(第一轮) */
	protected void preMakeInput()
	{
	
	}

	/** 输入部分(第二轮) */
	protected abstract void toMakeInput();

	/** 构造Field前 */
	protected abstract void toMakeBefore();

	/** 构造Field */
	protected abstract void toMakeOneField(FieldInfo field,FieldInfo outField);

	/** 构造Field后 */
	protected abstract void toMakeAfter();
	
	/** 构造结束 */
	protected void toMakeOver()
	{
		if(_oldInputFile!=null)
		{
			//移除旧记录
			_fileRecord.removePath(_oldInputFile.getPath());
		}
	}
	
	protected void toMakeFields()
	{
		//field组
		for(String k : _inputCls.getFieldNameList())
		{
			FieldInfo inField=_inputCls.getField(k);
			
			if(isNeedMakeField(inField))
			{
				toMakeField(inField);
			}
		}
	}
	
	/** 是否需要构造属性 */
	protected boolean isNeedMakeField(FieldInfo field)
	{
		return true;
	}
	
	/** 是否需要添加属性 */
	protected boolean isNeedAddField(FieldInfo field)
	{
		return true;
	}
	
	private void toMakeField(FieldInfo field)
	{
		FieldInfo outField=new FieldInfo();
		
		outField.describe=field.describe;
		//从input读取
		outField.isStatic=field.isStatic;
		
		if(_outputInfo.useStaticField)
		{
			outField.isStatic=true;
		}

		//_tempInField=field;
		outField.type=getVarType(field.type,false,false);
		//_tempInField=null;

		//默认值
		outField.defaultValue=getFieldDefaultValue(field);
		
		//复制name,visit
		copyFieldInfoForNV(field,outField);
		
		toMakeOneField(field,outField);

		//需要添加
		if(isNeedAddField(field))
		{
			List<String> fieldList=_inputCls.getFieldNameList();
			
			//以前有
			if(_oldInputFieldSet==null || _oldInputFieldSet.contains(field.name))
			{
				_outputCls.addField(outField);
			}
			else
			{
				int index=fieldList.indexOf(field.name) - 1;
				
				while(true)
				{
					if(index<0)
					{
						break;
					}
					
					String z=fieldList.get(index);
					
					//有
					if(_oldInputFieldSet.contains(z))
					{
						break;
					}
					
					index--;
				}
				
				_outputCls.addField(outField,index + 1);
			}
		}
		else
		{
			//删了这个属性
			_outputCls.removeField(field.name);
		}
	}
	
	//--base--//
	
	protected String getVarType(String type)
	{
		return getVarType(type,false,false);
	}
	
	protected String getVarType(String type,boolean isGType)
	{
		return getVarType(type,isGType,false);
	}
	
	protected String getVarType(String type,boolean isGType,boolean isValueType)
	{
		return getVarType(type,isGType,isValueType,_inputCls);
	}
	
	/** 获取变量实际使用类型(传入inCls的Field类型)(isGType:是否返回泛型类型,否则就是原型类型)(isValueType:是值类型,还是声明类型) */
	protected String getVarType(String type,boolean isGType,boolean isValueType,ClassInfo inputCls)
	{
		int tt=_inCode.getVarType(type);
		
		//基础类型
		if(VarType.isBaseType(tt))
		{
			if(tt==VarType.Long)
			{
				//AS3特有
				if(_outputInfo.codeType==CodeType.AS3)
				{
					_outputCls.addImport(ShineToolSetting.as3LongQName);
				}
			}
			
			if(isGType)
			{
				return _code.getVarGStr(tt);
			}
			else
			{
				return _code.getVarStr(tt);
			}
		}
		//高级类型
		else
		{
			String re="";
			
			switch(tt)
			{
				case VarType.Array:
				{
					String typeT=CodeInfo.getVarArrayType(type);
					
					//基础类型
					String outTypeT;
					
					if(_useNatureByteArr && type.equals(ShineToolSetting.byteArrName))
					{
						//还原
						outTypeT=_code.Byte;
					}
					else
					{
						outTypeT=getVarType(typeT,false,isValueType,inputCls);
					}
					
					re=_code.getArrayType(outTypeT,isValueType);
					
					String ii=_code.getArrayTypeImport(outTypeT,isValueType);
					
					if(ii!=null)
					{
						_outputCls.addImport(ii);
					}
				}
					break;
				case VarType.List:
				{
					String typeT=CodeInfo.getVarCollectionOneType(type);
					
					String outTypeT=getVarType(typeT,true,isValueType,inputCls);
					
					re=_code.getListType(outTypeT,isValueType);
					
					String ii=_code.getListTypeImport(outTypeT,isValueType);
					
					if(ii!=null)
					{
						_outputCls.addImport(ii);
					}
				}
					break;
				case VarType.Set:
				{
					String typeT=CodeInfo.getVarCollectionOneType(type);
					
					String outTypeT=getVarType(typeT,true,isValueType,inputCls);
					
					re=_code.getSetType(outTypeT,isValueType);
					
					String ii=_code.getSetTypeImport(outTypeT,isValueType);
					
					if(ii!=null)
					{
						_outputCls.addImport(ii);
					}
				}
					break;
				case VarType.Map:
				{
					CodeInfo.CollectionTwoType cc=CodeInfo.getVarCollectionTwoType(type);
					
					String outTypeK=getVarType(cc.type0,true,isValueType,inputCls);
					
					String outTypeV=getVarType(cc.type1,true,isValueType,inputCls);
					
					re=_code.getMapType(outTypeK,outTypeV,isValueType);
					
					String ii=_code.getMapTypeImport(outTypeK,outTypeV,isValueType);
					
					if(ii!=null)
					{
						_outputCls.addImport(ii);
					}
				}
					break;
				case VarType.Queue:
				{
					String typeT=CodeInfo.getVarCollectionOneType(type);
					
					String outTypeT=getVarType(typeT,true,isValueType,inputCls);
					
					re=_code.getQueueType(outTypeT,isValueType);
					
					String ii=_code.getQueueTypeImport(outTypeT,isValueType);
					
					if(ii!=null)
					{
						_outputCls.addImport(ii);
					}
				}
					break;
				case VarType.CustomObject:
				{
					re=getCustomObjectType(type,inputCls);
				}
					break;
				//				case VarType.Bytes:
				//				{
				//					re="Bytes";
				//
				//					_outputCls.addImport(ShineToolSetting.bytesQName);
				//				}
				//					break;
			}
			
			return re;
		}
	}
	
	/** 获取属性默认值 */
	protected String getFieldDefaultValue(FieldInfo field)
	{
		String dd=field.defaultValue;
		
		if(dd.isEmpty())
		{
			//返回字符串默认值
			if(_code.isString(field.type))
			{
				return "\"\"";
			}
			
			return "";
		}
		
		//创建对象的
		if(dd.startsWith("new"))
		{
			//集合类型
			if(isCollectionType(field.type))
			{
				dd=_code.createNewObject(getVarType(field.type,false,true));
			}
			else
			{
				for(Entry<String,String> kv : ShineToolSetting.varTransDic.entrySet())
				{
					int zIndex=dd.lastIndexOf(kv.getKey());

					if(zIndex!=-1)
					{
						dd=dd.substring(0,zIndex) + kv.getValue() + dd.substring(zIndex + 2,dd.length());
					}
				}
			}
			
			return dd;
		}
		else
		{
			return _code.getBaseFieldDefaultValue(field.type,field.defaultValue);
		}
	}
	
	/** 是否是集合类型(in类型) */
	protected boolean isCollectionType(String type)
	{
		return _inCode.getCollectionVarType(type)!=-1;
	}
	
	/** 是否是基础类型(in类型) */
	protected boolean isBaseType(String type)
	{
		return _inCode.getBaseVarType(type)!=-1;
	}
	
	private class InputInfo
	{
		public File file;
		
		public File inputFile;
		
		public String cName;
		
		public ClassInfo inputCls;
		
		public RecordDataClassInfo oldRecord;
		
		public RecordDataClassInfo newRecord;
	}
}
