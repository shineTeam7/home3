package com.home.shineTool.tool.part;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.SList;
import com.home.shine.support.collection.SMap;
import com.home.shine.support.collection.SSet;
import com.home.shine.support.collection.StringIntMap;
import com.home.shine.support.func.ObjectCall2;
import com.home.shine.support.func.ObjectCall3;
import com.home.shine.support.func.ObjectFunc2;
import com.home.shine.utils.FileUtils;
import com.home.shine.utils.StringUtils;
import com.home.shineTool.constlist.CodeType;
import com.home.shineTool.constlist.DataGroupType;
import com.home.shineTool.constlist.ProjectType;
import com.home.shineTool.constlist.VisitType;
import com.home.shineTool.global.ShineToolSetting;
import com.home.shineTool.reflect.FieldInfo;
import com.home.shineTool.reflect.MethodArgInfo;
import com.home.shineTool.reflect.MethodInfo;
import com.home.shineTool.reflect.cls.ClassInfo;
import com.home.shineTool.reflect.code.CodeWriter;
import com.home.shineTool.tool.base.BaseOutputInfo;
import com.home.shineTool.tool.data.DataExportTool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/** 模块列表工具 */
public class PartListTool extends DataExportTool
{
	private static final int FieldCopy=1;
	private static final int FieldExtend=2;
	private static final int FieldNew=3;
	
	/** 继承情况1(3->2,2->1) */
	private static final int ExtendCase1=1;
	/** 继承情况2(3->1) */
	private static final int ExtendCase2=2;
	/** 继承情况3(3->2->1) */
	private static final int ExtendCase3=3;
	/** 继承情况4(2->1) */
	private static final int ExtendCase4=4;
	
	/** 主名 */
	private String _mainName;
	
	
	
	
	/** 是否需要客户端部分 */
	private boolean _needClient=false;
	/** 客户端list工具 */
	private PartListTool _clientListTool;
	/** 是否必须执行 */
	private boolean _needDo=false;
	
	private File _onlyInFile;
	private ClassInfo _onlyInClass;
	
	/** 是否构造了neccesary */
	private boolean _makeNeccesaryd=false;
	private boolean _makePred=false;
	
	private ObjectFunc2<String,String> _getCommonPathFunc;
	
	//parts
	
	private ObjectCall2<ClassInfo,Boolean> _mainExCall;
	private ObjectCall3<ClassInfo,Boolean,Boolean> _partExCall;
	
	private ClassInfo _rootCls;
	
	/** 字段类型(见:FieldCopy,FieldExtend,FieldNew) */
	private StringIntMap _fieldTypeDic=new StringIntMap();

	/** 字段原始类型(0:从common引用,1:common继承,2:新增)(也作为是否存在属性的标记) */
	private StringIntMap _fieldTypeNatureDic=new StringIntMap();
	/** 字段名映射字典(转化前到转化后) */
	private SMap<String,String> _fieldKeyDic=new SMap<>();
	/** 字段名映射字典(转化后到转化前) */
	private SMap<String,String> _fieldKeyDicT=new SMap<>();
	/** 继承的工程type(value:ExtendCase) */
	private StringIntMap _fieldTypeExtendsDic=new StringIntMap();
	///** 继承的工程type */
	//private SMap<String,Integer> _fieldTypeExtendsDic=new SMap<>();
	
	
	private CodeWriter _registPartsWriter;
	
	private CodeWriter _readListDataWriter;
	
	private CodeWriter _writeListDataWriter;
	
	private CodeWriter _writeShadowListDataWriter;
	
	private CodeWriter _makeClientListDataWriter;
	
	/** 父工具 */
	private PartListTool _parentTool;
	
	/** 归属导出工具 */
	private PartDataExportTool _belongExportTool;
	///** 客户端导出工具 */
	//private PartDataExportTool _clientExportTool;
	
	public PartListTool()
	{
		
	}
	
	public void setMainName(String str)
	{
		_mainName=str;
	}
	
	public void setParentTool(PartListTool tool)
	{
		_parentTool=tool;
		
		addParentTool(tool);
	}
	
	public void setClientTool(PartListTool tool)
	{
		_clientListTool=tool;
		_needClient=true;
	}
	
	public void setNeedDo(boolean value)
	{
		_needDo=value;
	}
	
	/** 设置归属导出工具 */
	public void setBelongExport(PartDataExportTool serverTool)//,PartDataExportTool clientTool
	{
		_belongExportTool=serverTool;
		//_clientExportTool=clientTool;
	}
	
	/** 设置唯一文件 */
	public void setOnlyInFile(File file,ClassInfo cls)
	{
		_onlyInFile=file;
		_onlyInClass=cls;
	}
	
	@Override
	protected List<File> getInputFileList()
	{
		List<File> list=new ArrayList<>();
		list.add(_onlyInFile);
		return list;
	}
	
	@Override
	protected ClassInfo toGetInputClass(File f)
	{
		if(!_makeNeccesaryd)
		{
			_makeNeccesaryd=true;
			_inputCls=_onlyInClass;
			makeNecessary();
		}
		
		return _onlyInClass;
	}
	
	public PartDataExportTool getBelongExportTool()
	{
		return _belongExportTool;
	}
	
	//public PartDataExportTool getClientExportTool()
	//{
	//	return _clientExportTool;
	//}
	
	public void setExCalls(ObjectCall2<ClassInfo,Boolean> mainFunc,ObjectCall3<ClassInfo,Boolean,Boolean> partFunc)
	{
		_mainExCall=mainFunc;
		_partExCall=partFunc;
	}
	
	/** 获取common路径方法 */
	public void setCommonPathFunc(ObjectFunc2<String,String> func)
	{
		_getCommonPathFunc=func;
	}
	
	@Override
	protected void makeOneNext()
	{
		super.makeOneNext();
		
		////客户端定义不需要
		//if(_outputInfo.group!=DataGroupType.ClientDefine)
		//{
		//	makeParts();
		//}
		
		//ClientDefine视为构造机器人
		makeParts();
	}
	
	@Override
	protected boolean checkFileNeedDoEx(File file)
	{
		if(_needDo)
			return true;
		
		//有父
		if(_parentTool!=null)
		{
			if(!_parentTool._didFiles.isEmpty())
			{
				return true;
			}
			
			return _parentTool.checkFileNeedDoEx(file);
		}
		
		return false;
	}
	
	private PartListTool getProjectToolByType(int type)
	{
		if(_projectType==type)
			return this;
		
		if(_parentTool==null)
			return null;
		
		return _parentTool.getProjectToolByType(type);
	}
	
	private int getProjectTypeByQName(String qName)
	{
		if(qName.contains(".commonData."))
			return ProjectType.Common;
		
		if(qName.contains(".hPlayer."))
			return ProjectType.HotFix;
		
		return ProjectType.Game;
	}
	
	/** 获取继承字段 */
	private PartListTool getThisFieldTool(String name)
	{
		int tt=_fieldTypeNatureDic.get(name);
		
		//copy不算
		if(tt>FieldCopy)
			return this;
		
		if(_parentTool==null)
			return this;
		
		return _parentTool.getThisFieldTool(name);
	}
	
	/** 获取继承字段 */
	private PartListTool getSuperFieldTool(String name)
	{
		if(_parentTool==null)
			return null;
		
		int tt=_parentTool._fieldTypeNatureDic.get(name);

		//copy不算
		if(tt>FieldCopy)
			return _parentTool;
		
		return _parentTool.getSuperFieldTool(name);
	}
	
	/** 只有基类缺失的时候才走的修复(暂时先这么处理) */
	protected void preDoInput()
	{
		_inputCls=_onlyInClass;
		_inputFile=_onlyInFile;
		
		_fieldTypeDic.clear();
		
		if(_parentTool!=null)
		{
			if(!_parentTool._makePred)
			{
				_parentTool.preDoInput();
			}
			
			makeNecessary();
			
			//下面是处理g的问题
			
			List<String> list=new ArrayList<>();
			list.addAll(_inputCls.getFieldNameList());
			
			for(String k : list)
			{
				FieldInfo field=_inputCls.getField(k);
				
				int type=getProjectTypeByQName(_inputCls.getImport(field.type));
				
				PartListTool superTool=getSuperFieldTool(k);
				
				//fieldType
				int fType;
				
				//基类
				if(type<_projectType)
				{
					fType=FieldCopy;
				}
				else
				{
					//有继承
					if(superTool!=null)
					{
						fType=FieldExtend;
					}
					else
					{
						fType=FieldNew;
					}
				}
				
				_fieldTypeNatureDic.put(k,fType);
			}
		}
		else
		{
			//common补充
			for(String k : _inputCls.getFieldNameList())
			{
				_fieldTypeDic.put(k,FieldNew);
				_fieldTypeNatureDic.put(k,FieldNew);
				_fieldKeyDic.put(k,k);
				_fieldKeyDicT.put(k,k);
			}
		}
		
		_makePred=true;
	}
	
	private void makeNecessary()
	{
		if(_parentTool==null)
			return;
		
		//处理必须的问题
		ClassInfo parentInputCls=_parentTool._onlyInClass;
		
		if(parentInputCls==null)
		{
			Ctrl.throwError("不能找不到基类");
		}
		else
		{
			FieldInfo ff;
			FieldInfo ft;
			
			List<FieldInfo> fields=new ArrayList<>();
			
			for(String k:parentInputCls.getFieldNameList())
			{
				//必须的(热更工程全必须)
				if((ff=parentInputCls.getField(k)).hasAnnotation("NecessaryPart") || _projectType==ProjectType.HotFix)
				{
					if((ft=_inputCls.getField(k))==null)
					{
						//加导入
						_inputCls.addImport(parentInputCls.getImport(ff.type));
						fields.add(ff);
					}
					else
					{
						//先移除(待会排序)
						_inputCls.removeField(k);
						fields.add(ft);
					}
				}
			}
			
			//倒序加入
			for(int i=fields.size()-1;i>=0;--i)
			{
				ff=fields.get(i);
				
				//直接加入
				_inputCls.addField(ff,0);
			}
		}
	}
	
	@Override
	protected void toMakeInputFirst()
	{
		super.toMakeInputFirst();
		
		_fieldTypeDic.clear();
		
		if(_parentTool!=null)
		{
			//没执行，就执行一下
			if(!_parentTool._makePred)
			{
				_parentTool.preDoInput();
			}
			
			makeNecessary();
			
			//下面是处理g的问题
			
			List<String> list=new ArrayList<>();
			list.addAll(_inputCls.getFieldNameList());
			
			String mark=_front.toLowerCase();
			
			for(String k : list)
			{
				FieldInfo field=_inputCls.getField(k);
				
				int type=getProjectTypeByQName(_inputCls.getImport(field.type));

				PartListTool thisTool=getProjectToolByType(type);
				
				PartListTool superTool=getSuperFieldTool(k);

				//fieldType
				int fType;
				//继承类型
				int extendCase=0;
				
				String newFieldName=null;
				
				//基类
				if(type<_projectType)
				{
					fType=FieldCopy;
					
					newFieldName=thisTool._fieldKeyDic.get(k);
					
					//是game
					if(type==ProjectType.Game)
					{
						int tt=superTool._fieldTypeNatureDic.get(k);
						
						if(tt==FieldExtend)
						{
							extendCase=ExtendCase4;
						}
					}
				}
				else
				{
					//有继承
					if(superTool!=null)
					{
						fType=FieldExtend;
						
						newFieldName=mark + k;
						
						if(superTool._projectType==(_projectType-1))
						{
							int tt=superTool._fieldTypeNatureDic.get(k);
							
							if(tt==FieldExtend)
							{
								extendCase=ExtendCase3;
							}
							else
							{
								extendCase=ExtendCase1;
							}
						}
						else if(superTool._projectType==(_projectType-2))
						{
							extendCase=ExtendCase2;
						}
					}
					else
					{
						fType=FieldNew;
					}
				}
				
				if(newFieldName!=null)
				{
					//改变
					int fieldIndex=_inputCls.getFieldIndex(k);
					_inputCls.removeField(k);
					field.name=newFieldName;
					_inputCls.addField(field,fieldIndex);
				}
				
				_fieldKeyDic.put(k,field.name);
				_fieldKeyDicT.put(field.name,k);
				_fieldTypeDic.put(field.name,fType);
				_fieldTypeNatureDic.put(k,fType);
				
				if(extendCase>0)
				{
					_fieldTypeExtendsDic.put(field.name,extendCase);
				}
			}
		}
		else
		{
			//common补充
			for(String k : _inputCls.getFieldNameList())
			{
				_fieldTypeDic.put(k,FieldNew);
				_fieldTypeNatureDic.put(k,FieldNew);
				_fieldKeyDic.put(k,k);
				_fieldKeyDicT.put(k,k);
			}
		}
		
		_makePred=true;
	}
	
	@Override
	protected void toMakeOver()
	{
		super.toMakeOver();
		//还原
		_fieldKeyDic.forEach((k,v)->
		{
			if(!k.equals(v))
			{
				FieldInfo field=_inputCls.getField(v);
				
				int fieldIndex=_inputCls.getFieldIndex(v);
				_inputCls.removeField(v);
				field.name=k;
				_inputCls.addField(field,fieldIndex);
			}
		});
	}
	
	
	protected boolean isNeedAddField(FieldInfo field)
	{
		if(_fieldTypeDic.get(field.name)==FieldCopy)
		{
			return false;
		}
		
		return true;
	}
	
	@Override
	protected String toMakeExtend()
	{
		if(_parentTool!=null)
		{
			if(!_inputCls.extendsClsName.isEmpty())
			{
				Ctrl.throwError("LO不可使用继承类");
			}
			
			String clsName=_parentTool.getOutClassQNameByGroup(_outputInfo.group,_parentTool._inputCls.getQName());

			return clsName;
		}

		return "";
	}
	
	protected String getOutClassQNameByGroup(int group,String inQName)
	{
		//这里是兼容clientDefine
		String re=super.getOutClassQNameByGroup(group,inQName);
		
		if(group==DataGroupType.ClientDefine)
		{
			if(re.isEmpty())
			{
				re=super.getOutClassQNameByGroup(DataGroupType.Server,inQName);
			}
		}
		
		return re;
	}
	
	@Override
	protected void toMakeAfter()
	{
		super.toMakeAfter();
		
		//不是common
		if(_parentTool!=null)
		{
			if(_outputInfo.group!=DataGroupType.ClientDefine)
			{
				MethodInfo method=new MethodInfo();
				method.name="initListData";
				method.visitType=VisitType.Public;
				method.describe="初始化列表数据";
				method.returnType=_code.Void;
				method.isOverride=true;
				
				CodeWriter writer=_outputCls.createWriter();
				
				for(String k : _inputCls.getFieldNameList())
				{
					FieldInfo field=_inputCls.getField(k);
					
					String cName=field.type.substring(0,field.type.length()-3)+"PartData";
					
					writer.writeVarSet(_outputCls.getFieldWrap(k),_code.createNewObject(cName));
					
					writeFieldExtend(writer,k);
				}
				
				writer.writeEnd();
				method.content=writer.toString();
				
				_outputCls.addMethod(method);
			}
		}
	}
	
	private void writeFieldExtend(CodeWriter writer,String fieldName)
	{
		int value=_fieldTypeExtendsDic.get(fieldName);

		if(value>0)
		{
			String kk=fieldName.substring(_front.length());
			
			switch(value)
			{
				case ExtendCase1:
				{
					writer.writeVarSet(_outputCls.getFieldWrap(StringUtils.lcWord(_parentTool._front)+kk),_outputCls.getFieldWrap(fieldName));
				}
					break;
				case ExtendCase2:
				case ExtendCase4:
				{
					writer.writeVarSet(_outputCls.getFieldWrap(StringUtils.lcWord(_parentTool._parentTool._front)+kk),_outputCls.getFieldWrap(fieldName));
				}
					break;
				case ExtendCase3:
				{
					writer.writeVarSet(_outputCls.getFieldWrap(StringUtils.lcWord(_parentTool._front)+kk),_outputCls.getFieldWrap(fieldName));
					writer.writeVarSet(_outputCls.getFieldWrap(StringUtils.lcWord(_parentTool._parentTool._front)+kk),_outputCls.getFieldWrap(fieldName));
				}
					break;
				default:
				//case ExtendCase4:
				//{
				//	writer.writeVarSet(StringUtils.lcWord(_parentTool._parentTool._front)+kk,fieldName);
				//}
				//	break;
			}
		}
	}
	
	
	
	private PartListTool getToolByType(int type)
	{
		if(_projectType==type)
			return this;
		
		if(_parentTool!=null)
			return _parentTool.getToolByType(type);
		
		return null;
	}
	
	private String getRootPath(int group)
	{
		PartListOutputInfo outputInfo=(PartListOutputInfo)getOutputInfo(group);
		
		String frontPath=outputInfo.partPath;
		
		String rootClsPath=frontPath + "/" + _front + _mainName + "." + CodeType.getExName(outputInfo.codeType);
		
		return rootClsPath;
	}
	
	private String getListDataPath(int group)
	{
		BaseOutputInfo outputInfo=getOutputInfo(group);
		
		boolean isRobot=outputInfo.group==DataGroupType.ClientDefine || outputInfo.group==DataGroupType.Robot;
		
		return outputInfo.path + "/" + _front + _mainName+(isRobot ? "Client" : "")+"ListData" + "." + CodeType.getExName(outputInfo.codeType);
	}
	
	private String getClientListDataPath()
	{
		BaseOutputInfo outputInfo=_clientListTool.getOutputInfo(DataGroupType.ClientDefine);
		
		return outputInfo.path + "/" + _front + _mainName+"ClientListData" + "." + CodeType.getExName(outputInfo.codeType);
	}
	
	private String getPartPath(int group,String clsName)
	{
		PartListOutputInfo outputInfo=(PartListOutputInfo)getOutputInfo(group);
		
		String frontPath=outputInfo.partPath;
		
		String nn=clsName.substring(0,clsName.length() -3);//SPO
		String partClsName=nn + "Part";
		
		String partClsPath=frontPath + "/part/" + partClsName+"." + CodeType.getExName(outputInfo.codeType);
		
		return partClsPath;
	}
	
	private void makeParts()
	{
		int endLen="SPO".length();
		
		boolean hasParent=_parentTool!=null;
		
		boolean isServer=_outputInfo.group==DataGroupType.Server;
		
		boolean isRobot=_outputInfo.group==DataGroupType.ClientDefine || _outputInfo.group==DataGroupType.Robot;
		
		if(!isServer && !isRobot && ShineToolSetting.isClientOnlyData)
		{
			return;
		}
		
		String frontPath=_outputInfo.path;
		
		String dataPathPackage=FileUtils.getPathPackage(frontPath.substring(0,frontPath.lastIndexOf("/"))+"/" + _front + _mainName+"." + CodeType.getExName(_outputInfo.codeType));
		
		_rootCls=ClassInfo.getClassInfoFromPathAbs(getRootPath(_outputInfo.group));
		ClassInfo parentRootCls=null;
		
		if(hasParent)
		{
			parentRootCls=ClassInfo.getSimpleClassInfoFromPath(_parentTool.getRootPath(_outputInfo.group));
		}
		
		PartListTool commonTool=getToolByType(ProjectType.Common);
		
		String basePartName=_mainName + "BasePart";
		
		ClassInfo commonCls=ClassInfo.getSimpleClassInfoFromPath(commonTool.getRootPath(_outputInfo.group));
		
		if(commonCls==null)
		{
			Ctrl.throwError("找不到的必须类:"+commonTool.getRootPath(_outputInfo.group));
		}
		
		//默认基类
		String basePartQName=commonCls.packageStr + ".base." + basePartName;
		
		_rootCls.addImport(basePartQName);
		_rootCls.clsDescribe=_outputCls.clsDescribe;
		
		//common
		if(!hasParent)
		{
			FieldInfo field=new FieldInfo();
			field.name="_list";
			field.type=_code.getArrayType(basePartName,false);
			field.visitType=VisitType.Protected;
			
			_rootCls.addField(field);
			
			//MainMethodInfo mainMethod=new MainMethodInfo();
			//CodeWriter writer=_rootCls.createWriter();
			//writer.writeCustom("registParts();");
			//writer.writeEnd();
			//mainMethod.content=writer.toString();
			//
			//_rootCls.addMainMethod(mainMethod);
		}
		else
		{
			_rootCls.addImport(parentRootCls.getQName());
			_rootCls.extendsClsName=parentRootCls.clsName;
		}
		
		CodeWriter writer=_rootCls.createWriter();
		writer.writeEnd();
		
		//preInit
		MethodInfo method=new MethodInfo();
		method.visitType=VisitType.Protected;
		method.isVirtual=!hasParent;
		method.isOverride=hasParent;
		method.name="registParts";
		method.describe="注册部件";
		method.returnType=_code.Void;
		method.content=writer.toString();
		
		_rootCls.addMethod(method);
		
		String commonListDataQName=FileUtils.getPathQName(commonTool.getListDataPath(_outputInfo.group));
		String commonListDataName=StringUtils.getClassNameForQName(commonListDataQName);
		
		String listDataQName=FileUtils.getPathQName(getListDataPath(_outputInfo.group));
		String listDataName=StringUtils.getClassNameForQName(listDataQName);
		
		_rootCls.addImport(commonListDataQName);
		_rootCls.addImport(listDataQName);
		
		method=new MethodInfo();
		method.visitType=VisitType.Public;
		method.isVirtual=!hasParent;
		method.isOverride=hasParent;
		method.name="createListData";
		method.describe="创建列表数据";
		method.returnType=commonListDataName;
		writer=_rootCls.createWriter();
		writer.writeCustom("return " + _code.createNewObject(listDataName) + ";");
		writer.writeEnd();
		
		method.content=writer.toString();
		_rootCls.addMethod(method);
		
		//readListData
		method=new MethodInfo();
		method.visitType=VisitType.Public;
		method.isVirtual=!hasParent;
		method.isOverride=hasParent;
		method.name="readListData";
		method.describe="从列表数据读取";
		method.args.add(new MethodArgInfo("listData",commonListDataName));
		method.returnType=_code.Void;
		writer=_rootCls.createWriter();
		writer.writeEnd();
		method.content=writer.toString();
		
		_rootCls.addMethod(method);
		
		//writeListData
		method=new MethodInfo();
		method.visitType=VisitType.Public;
		method.isVirtual=!hasParent;
		method.isOverride=hasParent;
		method.name="writeListData";
		method.describe="写列表数据(深拷)";
		method.args.add(new MethodArgInfo("listData",commonListDataName));
		method.returnType=_code.Void;
		writer=_rootCls.createWriter();
		writer.writeEnd();
		method.content=writer.toString();
		
		_rootCls.addMethod(method);
		
		if(isServer)
		{
			//writeShadowListData
			method=new MethodInfo();
			method.visitType=VisitType.Public;
			method.isOverride=hasParent;
			method.name="writeShadowListData";
			method.describe="写列表数据(潜拷)";
			method.args.add(new MethodArgInfo("listData",commonListDataName));
			method.returnType=_code.Void;
			writer=_rootCls.createWriter();
			writer.writeEnd();
			method.content=writer.toString();
			
			_rootCls.addMethod(method);
			
			//client
			if(_needClient)
			{
				String commonClientListDataQName=FileUtils.getPathQName(commonTool.getClientListDataPath());
				String commonClientListDataName=StringUtils.getClassNameForQName(commonClientListDataQName);
				
				//makeClientListData
				method=new MethodInfo();
				method.visitType=VisitType.Public;
				method.name="makeClientListData";
				method.describe="构造客户端列表数据";
				method.returnType=commonClientListDataName;
				method.content=writer.toString();
				_rootCls.addImport(commonClientListDataQName);
				
				_rootCls.addMethod(method);
			}
		}
		
		if(_mainExCall!=null)
		{
			_mainExCall.apply(_rootCls,isServer);
		}
		
		_registPartsWriter=_rootCls.createWriter();
		
		_registPartsWriter.createArray(_outputCls.getFieldWrap("_list"),basePartName,String.valueOf(_inputCls.getFieldNameList().size()));
		_registPartsWriter.writeVarCreate("i",_code.Int,"0");
		_registPartsWriter.writeEmptyLine();
		
		
		_readListDataWriter=_rootCls.createWriter();
		
		String readListDataName="listData";
		
		if(hasParent)
		{
			_readListDataWriter.writeVarCreate("mData",listDataName,_code.getVarTypeTrans(readListDataName,listDataName));
			_readListDataWriter.writeEmptyLine();
			
			readListDataName="mData";
		}
		
		_rootCls.addImport(listDataQName);
		
		String writeListDataName="listData";
		
		_writeListDataWriter=_rootCls.createWriter();
		
		if(hasParent)
		{
			_writeListDataWriter.writeVarCreate("mData",listDataName,_code.getVarTypeTrans(writeListDataName,listDataName));
			_writeListDataWriter.writeEmptyLine();
		}
		
		if(isServer)
		{
			_writeShadowListDataWriter=_rootCls.createWriter();
			
			if(hasParent)
			{
				_writeShadowListDataWriter.writeVarCreate("mData",listDataName,_code.getVarTypeTrans(writeListDataName,listDataName));
				_writeShadowListDataWriter.writeEmptyLine();
			}
			
			if(_needClient)
			{
				String clientListDataQName=FileUtils.getPathQName(getClientListDataPath());
				String clientListDataName=StringUtils.getClassNameForQName(clientListDataQName);
				
				_rootCls.addImport(clientListDataQName);
				
				_makeClientListDataWriter=_rootCls.createWriter();
				_makeClientListDataWriter.writeVarCreate("listData",clientListDataName,_code.createNewObject(clientListDataName));
				_makeClientListDataWriter.writeEmptyLine();
			}
		}
		
		if(hasParent)
		{
			writeListDataName="mData";
		}
		
		//fields
		
		//TODO:增加额外处理组,来实现继承
		
		//查重用
		SSet<String> tempSets=new SSet<>();
		
		SList<FieldInfo> fields=new SList<>();
		
		//_belongExportTool.getExecutedInputClsDic()
		
		for(String k : _inputCls.getFieldNameList())
		{
			FieldInfo inField=_inputCls.getField(k);
			
			if(inField.type.endsWith("PO"))//SPO or CPO
			{
				fields.add(inField);
				tempSets.add(inField.type);
			}
		}
		
		_belongExportTool.getExecutedInputClsDic().forEach((k,v)->
		{
			if(!tempSets.contains(k.clsName))
			{
				FieldInfo field=new FieldInfo();
				field.name="ex";
				field.type=k.clsName;
				field.describe=k.clsDescribe;
				
				fields.add(field);
				tempSets.add(field.type);
			}
		});
		
		for(FieldInfo inField : fields)
		{
			String ffName=_fieldKeyDicT.get(inField.name);
			
			if(ffName==null)
				ffName=inField.name;
			
			PartListTool thisFieldTool=getThisFieldTool(ffName);
			
			String inputRootPathT=thisFieldTool._inputRootPath;
			inputRootPathT=inputRootPathT.substring(0,inputRootPathT.lastIndexOf("/"))+"/"+(isServer ? "server" : "client")+"/";//因为是SPO
			
			//输入类
			String iPath=inputRootPathT + inField.type + "." + CodeType.getExName(_inCode.getCodeType());
			
			//ClassInfo inFieldCls=ClassInfo.getSimpleClassInfoFromPathAbs(iPath);
			ClassInfo inFieldCls=ClassInfo.getSimpleClassInfoFromPath(iPath);
			
			if(inFieldCls==null)
			{
				Ctrl.throwError("找不到输入文件,是否忘记配置",inField.type);
			}
			
			//是否是属性添加过来的
			boolean isField=_inputCls.getField(inField.name)!=null;
			
			boolean needDo=false;
			
			String partClsPath;
			
			if(isField)
			{
				if(!_fieldTypeDic.contains(inField.name))
				{
					Ctrl.throwError("不该找不到",inField.name);
				}
				
				int fieldGType=_fieldTypeDic.get(inField.name);
				
				if(fieldGType==FieldCopy)
				{
					int projectType=getProjectTypeByQName(_inputCls.getImport(inField.type));
					
					PartListTool pTool=getToolByType(projectType);
					
					partClsPath=pTool.getPartPath(_outputInfo.group,inField.type);
				}
				else
				{
					partClsPath=getPartPath(_outputInfo.group,inField.type);
				}
				
				needDo=fieldGType!=FieldCopy;
			}
			else
			{
				partClsPath=getPartPath(_outputInfo.group,inField.type);
				needDo=true;
			}
			
			
			ClassInfo partCls=ClassInfo.getClassInfoFromPathAbs(partClsPath);
			
			String nn=inField.type.substring(0,inField.type.length() -endLen);
			String partDataClsName=nn + (isRobot ? "ClientPartData" : "PartData");
			
			if(needDo)
			{
				//继承QName
				String exQName=basePartQName;
				boolean hasExtend=false;
				
				//有继承
				if(!inFieldCls.extendsClsName.isEmpty())
				{
					String superQName=inFieldCls.getImport(inFieldCls.extendsClsName);
					
					int superPType=getProjectTypeByQName(superQName);
					
					String partPath=getToolByType(superPType).getPartPath(_outputInfo.group,inFieldCls.extendsClsName);
					
					exQName=FileUtils.getPathQName(partPath);
					
					hasExtend=true;
				}
				
				partCls.addImport(exQName);
				partCls.extendsClsName=StringUtils.getClassNameForQName(exQName);
				partCls.clsDescribe=inField.describe;
				
				if(hasParent)
				{
					//me
					FieldInfo fme=new FieldInfo();
					fme.name=_front.toLowerCase()+"me";
					fme.type=_rootCls.clsName;
					fme.visitType=VisitType.Public;
					fme.describe="角色对象";
					
					partCls.addField(fme);
					
					partCls.addImport(_rootCls.getQName());
					
					//setMe
					partCls.addImport(commonCls.getQName());
					
					MethodInfo setMeMethod=new MethodInfo();
					setMeMethod.name="setMe";
					setMeMethod.visitType=VisitType.Public;
					setMeMethod.args.add(new MethodArgInfo("player",commonCls.clsName));
					setMeMethod.isOverride=true;
					
					CodeWriter mWriter=partCls.createWriter();
					mWriter.writeSuperMethod(setMeMethod);
					mWriter.writeVarSet(_outputCls.getFieldWrap(fme.name),_code.getVarTypeTrans("player",_rootCls.clsName));
					mWriter.writeEnd();
					setMeMethod.content=mWriter.toString();
					
					partCls.addMethod(setMeMethod);
				}
				
				String ddFront;
				
				if(partCls.getCodeType()==CodeType.TS)
				{
					if(!isField)
						ddFront="s";
					else
						ddFront=_front.toLowerCase();
				}
				else
				{
					ddFront="";
				}
				
				String dataFieldName="_"+ddFront+"d";
				
				//if(!ddFront.isEmpty())
				//{
				//	partCls.removeField("_d");
				//}
				
				FieldInfo ff=new FieldInfo();
				ff.name=dataFieldName;
				ff.type=partDataClsName;
				ff.visitType=VisitType.Private;
				ff.describe="数据";
				
				partCls.addField(ff);
				
				String dataQName=dataPathPackage + (isRobot ? ".clientData." : ".data.") + ff.type;
				
				partCls.addImport(dataQName);
				
				partCls.addImport(ShineToolSetting.dataBaseQName);
				
				//setData
				
				method=new MethodInfo();
				method.name="setData";
				method.args.add(new MethodArgInfo("data",ShineToolSetting.dataBaseName));
				method.visitType=VisitType.Public;
				method.isOverride=true;
				
				writer=partCls.createWriter();
				
				writer.writeSuperMethod(method);
				writer.writeEmptyLine();
				writer.writeVarSet(_outputCls.getFieldWrap(dataFieldName),_code.getVarTypeTrans("data",ff.type));
				writer.writeEnd();
				method.content=writer.toString();
				
				partCls.addMethod(method);
				
				//getData
				
				method=new MethodInfo();
				
				method.name="get"+_front+"PartData";
				
				method.describe="获取数据";
				method.visitType=VisitType.Public;
				method.returnType=ff.type;
				
				writer=partCls.createWriter();
				writer.writeCustom("return " + _outputCls.getFieldWrap(dataFieldName)+";");
				writer.writeEnd();
				method.content=writer.toString();
				
				partCls.addMethod(method);
				
				//createPartData
				method=new MethodInfo();
				method.name="createPartData";
				method.visitType=VisitType.Protected;
				method.isOverride=true;
				method.returnType=ShineToolSetting.dataBaseName;
				
				writer=partCls.createWriter();
				writer.writeCustom("return " + _code.createNewObject(ff.type) + ";");
				
				writer.writeEnd();
				method.content=writer.toString();
				
				partCls.addMethod(method);
				
				if(isServer)
				{
					if(_needClient)
					{
						partCls.addImport(ShineToolSetting.clientBasePartDataQName);
						
						String clientBaseDataName=StringUtils.getClassNameForQName(ShineToolSetting.clientBasePartDataQName);
						
						//createClientData
						method=new MethodInfo();
						method.name="createClientData";
						method.visitType=VisitType.Protected;
						method.isOverride=true;
						method.returnType=clientBaseDataName;
						
						writer=partCls.createWriter();
						
						String clientDataQName=dataQName.replace(".data.",".clientData.");
						clientDataQName=clientDataQName.substring(0,clientDataQName.length() - "PartData".length()) + "ClientPartData";
						
						String clientDataName=StringUtils.getClassNameForQName(clientDataQName);
						
						partCls.addImport(clientDataQName);
						
						writer.writeCustom("return " + _code.createNewObject(clientDataName) + ";");
						
						writer.writeEnd();
						method.content=writer.toString();
						
						partCls.addMethod(method);
						
						//writeClientData
						method=new MethodInfo();
						method.name="writeClientData";
						method.visitType=VisitType.Protected;
						method.isOverride=true;
						method.returnType=_code.Void;
						method.args.add(new MethodArgInfo("data",clientBaseDataName));
						
						writer=partCls.createWriter();
						
						if(hasExtend)
						{
							writer.writeSuperMethod(method);
							writer.writeEmptyLine();
						}
						
						writer.writeCustom("toWriteClientData(" + _code.getVarTypeTrans("data",clientDataName) + ");");
						writer.writeEnd();
						
						method.content=writer.toString();
						
						partCls.addMethod(method);
						
						//toWriteClientData
						
						method=partCls.getMethodByName("toWriteClientData");
						
						if(method==null)
						{
							method=new MethodInfo();
							
							writer=partCls.createWriter();
							writer.writeEnd();
							
							method.content=writer.toString();
						}
						
						method.name="toWriteClientData";
						method.describe="写客户端数据具体执行(已执行过copyServer了,就是说CPO中与SPO同名同类型的属性无需再赋值)";
						method.visitType=VisitType.Private;
						method.returnType=_code.Void;
						method.args.clear();
						method.args.add(new MethodArgInfo("data",clientDataName));
						
						partCls.addMethod(method);
					}
				}
				
				//partEx部分
				
				if(_partExCall!=null)
				{
					_partExCall.apply(partCls,isServer,hasExtend);
				}
				
				//					if(isNewPart)
				//					{
				////						if(_partExCall!=null)
				////						{
				////							_partExCall.call(partCls,hasExtend);
				////						}
				//					}
				
				partCls.writeToPath(partClsPath);
				
				if(isField)
				{
					//加属性
					FieldInfo rootF=new FieldInfo();
					rootF.name=inField.name;
					rootF.describe=inField.describe;
					rootF.type=partCls.clsName;
					rootF.visitType=VisitType.Public;
					
					_rootCls.addField(rootF);
				}
			}
			
			if(isField)
			{
				_rootCls.addImport(partCls.getQName());
				
				//root
				
				String pName=inField.name;
				String pType=partCls.clsName;
				
				//registParts
				_registPartsWriter.writeVarSet(_outputCls.getFieldWrap(pName),_code.createNewObject(pType));
				
				writeFieldExtend(_registPartsWriter,pName);
				
				//是否有继承
				_registPartsWriter.writeCustom(_outputCls.getFieldWrap(pName) + ".setMe(this);");
				
				_registPartsWriter.writeArraySet(_outputCls.getFieldWrap("_list"),"i++",_outputCls.getFieldWrap(pName));
				
				_registPartsWriter.writeEmptyLine();
				
				//readListData
				_readListDataWriter.writeCustom("this." + pName + ".setData(" + readListDataName + "." + inField.name + ");");
				
				//makeListData
				
				_rootCls.addImport(_outputCls.getImport(partDataClsName));
				_writeListDataWriter.writeVarSet(writeListDataName + "." + pName,_code.getVarTypeTrans("this." + pName + ".makePartData()",partDataClsName));
				
				if(isServer)
				{
					_writeShadowListDataWriter.writeVarSet(writeListDataName + "." + pName,_code.getVarTypeTrans("this." + pName + ".makeShadowPartData()",partDataClsName));
					
					if(_needClient)
					{
						String sQName=_outputCls.getImport(partDataClsName);
						sQName=sQName.replace(".data.",".clientData.");
						sQName=sQName.substring(0,sQName.length() - "PartData".length()) + "ClientPartData";
						String sType=StringUtils.getClassNameForQName(sQName);
						
						_rootCls.addImport(sQName);
						_makeClientListDataWriter.writeVarSet("listData." + inField.name,_code.getVarTypeTrans("this." + pName + ".makeClientPartData()",sType));
					}
					
				}
			}
		}
		
		_registPartsWriter.writeEnd();
		_rootCls.getMethodByName("registParts").content=_registPartsWriter.toString();
		
		_readListDataWriter.writeEnd();
		_rootCls.getMethodByName("readListData").content=_readListDataWriter.toString();
		
		_writeListDataWriter.writeEnd();
		_rootCls.getMethodByName("writeListData").content=_writeListDataWriter.toString();
		
		if(isServer)
		{
			_writeShadowListDataWriter.writeEnd();
			_rootCls.getMethodByName("writeShadowListData").content=_writeShadowListDataWriter.toString();
			
			//client
			
			if(_needClient)
			{
				_makeClientListDataWriter.writeEmptyLine();
				_makeClientListDataWriter.writeCustom("return listData;");
				_makeClientListDataWriter.writeEnd();
				_rootCls.getMethodByName("makeClientListData").content=_makeClientListDataWriter.toString();
			}
			
		}
		
		_rootCls.write();
	}
	
	/** 读取上次位置记录的 */
	protected void readLastPos()
	{
		_inputFileDic.clear();
		
		//String cName=getCName(FileUtils.getFileFrontName(_onlyInFile.getName()));
		//_inputFileDic.put(cName,_onlyInFile);
	}
	
	protected void addRecord(File f)
	{
		super.addRecord(f);
		//
		////客户端不谢记录
		//if(!_isClient)
		//{
		//	super.addRecord(f);
		//}
	}
}
