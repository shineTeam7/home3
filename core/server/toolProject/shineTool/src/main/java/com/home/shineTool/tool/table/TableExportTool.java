package com.home.shineTool.tool.table;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.collection.SList;
import com.home.shine.support.collection.SMap;
import com.home.shine.support.collection.SSet;
import com.home.shine.utils.FileUtils;
import com.home.shine.utils.StringUtils;
import com.home.shineTool.constlist.DataGroupType;
import com.home.shineTool.constlist.VisitType;
import com.home.shineTool.reflect.FieldInfo;
import com.home.shineTool.reflect.MethodArgInfo;
import com.home.shineTool.reflect.MethodInfo;
import com.home.shineTool.reflect.cls.ClassInfo;
import com.home.shineTool.reflect.code.CodeWriter;
import com.home.shineTool.global.ShineToolSetting;
import com.home.shineTool.tool.base.BaseOutputInfo;
import com.home.shineTool.tool.data.DataExportTool;

import java.util.HashSet;
import java.util.Set;

/** 数据表导出工具 */
public class TableExportTool extends DataExportTool
{
	private SMap<String,String> _sqlDic=new SMap<>();
	
	private boolean _isCommon=false;
	
	private TableExportTool _commonTool;
	
	private SList<FieldInfo> _fieldList;
	
	private SMap<String,FieldInfo> _fieldDic;
	/** 表名(首字母小写) */
	private String _tableName;
	
	private SList<String> _primaryKeyList;
	/** 索引列表(包括主键) */
	private SMap<String,SList<String>> _indexDic;
	
	private SList<String> _indexKeyList;
	/** 自增组 */
	private Set<String> _autoIncrementSet;
	
	private StringBuilder _sqlB;
	
	public TableExportTool()
	{
		_useNatureByteArr=true;
	}
	
	public void setIsCommon(boolean value)
	{
		_isCommon=value;
	}
	
	public boolean isCommon()
	{
		return _isCommon;
	}
	
	public void setCommonTool(TableExportTool tool)
	{
		_commonTool=tool;
	}
	
	protected ClassInfo getInputClsAbs(String qName)
	{
		ClassInfo cls=super.getInputClsAbs(qName);
		
		if(cls!=null)
			return cls;
		
		if(_commonTool!=null)
		{
			return _commonTool.getInputClsAbs(qName);
		}
		
		return null;
	}
	
	@Override
	protected void toMakeInput()
	{
		super.toMakeInput();
		
		String tName=_inputCls.clsName;
		
		if(_superInputCls!=null)
		{
			tName=_superInputCls.clsName;
			
			if(_inputCls.clsDescribe.isEmpty())
				_inputCls.clsDescribe=_superInputCls.clsDescribe;
		}
		
		_tableName=getCNameByClassName(tName).toLowerCase();//全小写
		
		makeExistFieldList();
		
		_indexDic=new SMap<>();
		_autoIncrementSet=new HashSet<>();
		_primaryKeyList=new SList<>();
		_indexKeyList=new SList<>();
		
		_indexKeyList.add("Primary");
		_indexDic.put("Primary",new SList<>());
		
		for(FieldInfo f : _fieldList)
		{
			//有主键
			if(f.hasAnnotation("PrimaryKey"))
			{
				_primaryKeyList.add(f.name);
				_indexDic.get("Primary").add(f.name);
			}
			
			if(f.hasAnnotation("AutoIncrementKey"))
			{
				_autoIncrementSet.add(f.name);
			}
			
			if(f.hasAnnotation("IndexKey"))
			{
				f.annotationList.forEach(v->
				{
					if(v.name.equals("IndexKey"))
					{
						SList<String> list=_indexDic.get(v.args[0]);
						
						if(list==null)
						{
							_indexDic.put(v.args[0],list=new SList<>());
							_indexKeyList.add(v.args[0]);
						}
						
						list.add(f.name);
					}
				});
			}
		}
		
		if(_primaryKeyList.isEmpty())
		{
			Ctrl.throwError("不能没有主键");
		}
		
		_sqlB=new StringBuilder();
		_sqlB.append("create table if not exists `" + _tableName + "` (");
	}
	
	protected String getOutPackageStr(String inPackageStr)
	{
		String outPackageStr=super.getOutPackageStr(inPackageStr);
		
		String last=_outputInfo.path.substring(_outputInfo.path.lastIndexOf('/') + 1);
		
		String usePackage=outPackageStr.substring(0,outPackageStr.lastIndexOf('.')) + '.' + last;
		
		return usePackage;
	}
	
	/** 获取还存在的字段列表 */
	protected void makeExistFieldList()
	{
		_fieldList=new SList<>();
		_fieldDic=new SMap<>();
		
		SSet<String> eSet=new SSet<>();
		
		FieldInfo field;
		
		if(_superInputCls!=null)
		{
			//field组
			for(String k : _superInputCls.getFieldNameList())
			{
				if(!eSet.contains(k))
				{
					eSet.add(k);
					
					if((field=_inputCls.getField(k))==null)
					{
						field=_superInputCls.getField(k);
					}
					
					if(field!=null)
					{
						_fieldList.add(field);
						_fieldDic.put(field.name,field);
					}
				}
			}
		}
		
		//field组
		for(String k : _inputCls.getFieldNameList())
		{
			if(!eSet.contains(k))
			{
				eSet.add(k);
				
				field=_inputCls.getField(k);
				
				if(field!=null)
				{
					_fieldList.add(field);
					_fieldDic.put(field.name,field);
				}
			}
		}
	}
	
	/** 通过info获得输出类完全限定名 */
	private String getOutClassQNameByInfo(BaseOutputInfo outInfo,String inQName)
	{
		//在里面
		if(inQName.startsWith(_inputRootPackge) && inQName.endsWith(_mark))
		{
			String last=outInfo.rootPackage + inQName.substring(_inputRootPackge.length(),inQName.length());
			
			last=last.substring(0,last.length() - _mark.length()) + outInfo.nameTail;
			
			return last;
		}
		
		return "";
	}
	
	@Override
	protected void toMakeMainMethod()
	{
		super.toMakeMainMethod();
		
		//多主键
		if(_primaryKeyList.length()>1  && _outputInfo.defineIndex==DataGroupType.Server)
		{
			_mainMethodWriter.writeVarSet("_isMultiPrimaryKey",_outputCls.getCode().False);
		}
	}
	
	@Override
	protected void toMakeBefore()
	{
		int index=_outputs.indexOf(_outputInfo);
		
		switch(index)
		{
			case 0:
			{
				toMakeTable();
				toMakeSql();
				
				super.toMakeBefore();
			}
				break;
			case 1:
			{
				toMakeTask();
			}
				break;
			case 2:
			{
				toMakeResult();
			}
				break;
		}
	}
	
	@Override
	protected void toMakeOneField(FieldInfo field,FieldInfo outField)
	{
		int index=_outputs.indexOf(_outputInfo);
		
		switch(index)
		{
			case 0:
			{
				super.toMakeOneField(field,outField);
			}
				break;
		}
	}
	
	@Override
	protected void toMakeAfter()
	{
		int index=_outputs.indexOf(_outputInfo);
		
		switch(index)
		{
			case 0:
			{
				super.toMakeAfter();
			}
			break;
		}
	}
	
	private void toMakeTable()
	{
		//		FieldInfo field=new FieldInfo();
		//		field.name="instance";
		//		field.type=_outputCls.clsName;
		//		field.isStatic=true;
		//		field.isConst=true;
		//		field.visitType=VisitType.Public;
		//		field.describe="单例(为类static方法)";
		//		field.defaultValue=_code.createNewObject(_outputCls.clsName);
		//
		//		_outputCls.addField(field);
		
		toMakeInsertStr();
		toMakeDeleteStr();
		toMakeUpdateStr();
		toMakeSelectStr();
		
		//select
		
		String index="index";
		
		MethodInfo method=new MethodInfo();
		method.name="getSelectInStrByIndex";
		method.returnType=_code.String;
		method.visitType=VisitType.Public;
		method.isOverride=true;
		method.args.add(new MethodArgInfo(index,_code.Int));
		
		CodeWriter writer=_outputCls.createWriter();
		writer.writeCustom("switch(" + index + ")");
		writer.writeLeftBrace();
		
		for(int i=0;i<_indexKeyList.length();i++)
		{
			SList<String> list=_indexDic.get(_indexKeyList.get(i));
			
			toMakeSelectInStr(i,list);
			
			writer.writeCustom("case " + i + ":");
			writer.writeCustomWithOff(1,"return " + "_selectInStr" + i + ";");
		}
		
		writer.writeRightBrace();
		writer.writeEmptyLine();
		writer.writeCustom("return null;");
		writer.writeEnd();
		
		method.content=writer.toString();
		_outputCls.addMethod(method);
		
		//createTask
		
		_outputCls.addImport(ShineToolSetting.taskQName);
		
		String outQName=getOutClassQNameByInfo(_outputs.get(1),_inputCls.getQName());
		String outClsName=StringUtils.getClassNameForQName(outQName);
		
		_outputCls.addImport(outQName);
		
		method=new MethodInfo();
		method.name="createTask";
		method.returnType=StringUtils.getClassNameForQName(ShineToolSetting.taskQName);
		method.visitType=VisitType.Protected;
		method.isOverride=true;
		
		writer=_outputCls.createWriter();
		
		writer.writeVarCreate("task",outClsName,_code.createNewObject(outClsName));
		
		for(FieldInfo f : _fieldList)
		{
			writer.writeVarSet("task." + f.name,"this." + f.name);
		}
		
		writer.writeCustom("return task;");
		writer.writeEnd();
		
		method.content=writer.toString();
		_outputCls.addMethod(method);
		
		//readByResult
		
		_outputCls.addImport(ShineToolSetting.resultQName);
		
		outQName=getOutClassQNameByInfo(_outputs.get(2),_inputCls.getQName());
		outClsName=StringUtils.getClassNameForQName(outQName);
		
		_outputCls.addImport(outQName);
		
		method=new MethodInfo();
		method.name="readByResult";
		method.returnType=_code.Void;
		method.visitType=VisitType.Public;
		method.isOverride=true;
		method.args.add(new MethodArgInfo("result",StringUtils.getClassNameForQName(ShineToolSetting.resultQName)));
		
		writer=_outputCls.createWriter();
		
		writer.writeVarCreate("re",outClsName,_code.getVarTypeTrans("result",outClsName));
		
		for(FieldInfo f : _fieldList)
		{
			writer.writeVarSet("this." + f.name,"re." + f.name);
		}
		
		writer.writeEnd();
		
		method.content=writer.toString();
		_outputCls.addMethod(method);
		
		//toReadValues
		
		method=new MethodInfo();
		method.name="toReadValues";
		method.returnType=_code.Void;
		method.visitType=VisitType.Protected;
		method.isOverride=true;
		
		writer=_outputCls.createWriter();
		
		for(FieldInfo f : _fieldList)
		{
			toReadOneTableField(f,writer);
		}
		
		writer.writeEnd();
		
		method.content=writer.toString();
		_outputCls.addMethod(method);
	}
	
	private void toMakeInsertStr()
	{
		FieldInfo field=new FieldInfo();
		field.name="_insertStr";
		field.type=_code.String;
		field.visitType=VisitType.Private;
		field.isStatic=true;
		field.isConst=true;
		field.describe="增";
		
		StringBuilder sb=new StringBuilder();
		
		sb.append('"');
		
		if(_inputCls.hasAnnotation("UseReplaceInsteadOfInsert"))
		{
			sb.append("replace into `");
		}
		else
		{
			sb.append("insert into `");
		}
		
		sb.append(_tableName);
		sb.append("` (");
		
		int i=0;
		
		for(FieldInfo f : _fieldList)
		{
			//不是自增的
			if(!_autoIncrementSet.contains(f.name))
			{
				if(i>0)
				{
					sb.append(",");
				}
				
				sb.append("`");
				sb.append(f.name);
				sb.append("`");
				
				++i;
			}
		}
		
		sb.append(" ) values (");
		
		int len=i;
		
		for(i=0;i<len;++i)
		{
			if(i>0)
			{
				sb.append(",");
			}
			
			sb.append("?");
		}
		
		sb.append(")");
		
		if(ShineSetting.sqlUseEnd)
			sb.append(":");
		
		sb.append('"');
		
		field.defaultValue=sb.toString();
		
		_outputCls.addField(field);
		
		//
		
		MethodInfo method=new MethodInfo();
		method.name="getInsertStr";
		method.returnType=_code.String;
		method.visitType=VisitType.Public;
		method.isOverride=true;
		
		CodeWriter writer=_outputCls.createWriter();
		writer.writeCustom("return " + field.name + ";");
		writer.writeEnd();
		
		method.content=writer.toString();
		_outputCls.addMethod(method);
	}
	
	private void toMakeDeleteStr()
	{
		FieldInfo field=new FieldInfo();
		field.name="_deleteStr";
		field.type=_code.String;
		field.visitType=VisitType.Private;
		field.isStatic=true;
		field.isConst=true;
		field.describe="删";
		
		StringBuilder sb=new StringBuilder();
		
		sb.append('"');
		sb.append("delete from `");
		sb.append(_tableName);
		sb.append("` where ");
		
		for(int i=0;i<_primaryKeyList.length();i++)
		{
			if(i>0)
			{
				sb.append(" and ");
			}
			
			sb.append("`");
			sb.append(_primaryKeyList.get(i));
			sb.append("` = ?");
		}
		
		if(ShineSetting.sqlUseEnd)
			sb.append(":");
		
		sb.append('"');
		
		field.defaultValue=sb.toString();
		
		_outputCls.addField(field);
		
		//
		
		MethodInfo method=new MethodInfo();
		method.name="getDeleteStr";
		method.returnType=_code.String;
		method.visitType=VisitType.Public;
		method.isOverride=true;
		
		CodeWriter writer=_outputCls.createWriter();
		writer.writeCustom("return " + field.name + ";");
		writer.writeEnd();
		
		method.content=writer.toString();
		_outputCls.addMethod(method);
	}
	
	private void toMakeUpdateStr()
	{
		FieldInfo field=new FieldInfo();
		field.name="_updateStr";
		field.type=_code.String;
		field.visitType=VisitType.Private;
		field.isStatic=true;
		field.isConst=true;
		field.describe="改";
		
		StringBuilder sb=new StringBuilder();
		
		sb.append('"');
		sb.append("update `");
		sb.append(_tableName);
		sb.append("` set ");
		
		for(int i=0;i<_fieldList.length();i++)
		{
			if(i>0)
			{
				sb.append(",");
			}
			
			sb.append("`");
			sb.append(_fieldList.get(i).name);
			sb.append("` = ?");
		}
		
		sb.append(" where ");
		
		for(int i=0;i<_primaryKeyList.length();i++)
		{
			if(i>0)
			{
				sb.append(" and ");
			}
			
			sb.append("`");
			sb.append(_primaryKeyList.get(i));
			sb.append("` = ?");
		}
		
		if(ShineSetting.sqlUseEnd)
			sb.append(":");
		
		sb.append('"');
		
		field.defaultValue=sb.toString();
		
		_outputCls.addField(field);
		
		//
		
		MethodInfo method=new MethodInfo();
		method.name="getUpdateStr";
		method.returnType=_code.String;
		method.visitType=VisitType.Public;
		method.isOverride=true;
		
		CodeWriter writer=_outputCls.createWriter();
		writer.writeCustom("return " + field.name + ";");
		writer.writeEnd();
		
		method.content=writer.toString();
		_outputCls.addMethod(method);
	}
	
	private void toMakeSelectStr()
	{
		FieldInfo field=new FieldInfo();
		field.name="_selectStr";
		field.type=_code.String;
		field.visitType=VisitType.Private;
		field.isStatic=true;
		field.isConst=true;
		field.describe="查询头";
		
		StringBuilder sb=new StringBuilder();
		
		sb.append('"');
		sb.append("select ");
		
		int i=0;
		
		for(FieldInfo f : _fieldList)
		{
			if(i>0)
			{
				sb.append(",");
			}
			
			sb.append("`");
			sb.append(f.name);
			sb.append("`");
			
			++i;
		}
		
		sb.append(" from `");
		sb.append(_tableName);
		sb.append("`");
		
		if(ShineSetting.sqlUseEnd)
			sb.append(":");
		
		sb.append('"');
		
		field.defaultValue=sb.toString();
		
		_outputCls.addField(field);
		
		//
		
		MethodInfo method=new MethodInfo();
		method.name="getSelectStr";
		method.returnType=_code.String;
		method.visitType=VisitType.Public;
		method.isOverride=true;
		
		CodeWriter writer=_outputCls.createWriter();
		writer.writeCustom("return " + field.name + ";");
		writer.writeEnd();
		
		method.content=writer.toString();
		_outputCls.addMethod(method);
	}
	
	private void toMakeSelectInStr(int index,SList<String> list)
	{
		FieldInfo field=new FieldInfo();
		field.name="_selectInStr" + index;
		field.type=_code.String;
		field.visitType=VisitType.Private;
		field.isStatic=true;
		field.isConst=true;
		field.describe="查" + index;
		
		StringBuilder sb=new StringBuilder();
		
		sb.append('"');
		sb.append("select ");
		
		for(int i=0;i<_fieldList.length();i++)
		{
			if(i>0)
			{
				sb.append(",");
			}
			
			sb.append("`");
			sb.append(_fieldList.get(i).name);
			sb.append("`");
		}
		
		sb.append(" from `");
		sb.append(_tableName);
		sb.append("` where ");
		
		//单一的支持batch
		if(list.length()==1)
		{
			sb.append('`');
			sb.append(list.get(0));
			sb.append("` in (");
			sb.append('"');
		}
		else
		{
			for(int i=0;i<list.length();i++)
			{
				if(i>0)
				{
					sb.append(" and ");
				}
				
				sb.append('`');
				sb.append(list.get(i));
				sb.append("` = ?");
			}
			
			sb.append(';');
			sb.append('"');
		}
		
		field.defaultValue=sb.toString();
		
		_outputCls.addField(field);
	}
	
	private void toMakeTask()
	{
		//createTable
		_outputCls.addImport(ShineToolSetting.tableQName);
		
		String outQName=getOutClassQNameByInfo(_outputs.get(0),_inputCls.getQName());
		String outClsName=StringUtils.getClassNameForQName(outQName);
		
		_outputCls.addImport(outQName);
		
		MethodInfo method=new MethodInfo();
		method.name="createTable";
		method.returnType=StringUtils.getClassNameForQName(ShineToolSetting.tableQName);
		method.visitType=VisitType.Public;
		method.isOverride=true;
		
		CodeWriter writer=_outputCls.createWriter();
		writer.writeCustom("return " + _code.createNewObject(outClsName) + ";");
		writer.writeEnd();
		
		method.content=writer.toString();
		_outputCls.addMethod(method);
		
		//createResult
		_outputCls.addImport(ShineToolSetting.resultQName);
		
		outQName=getOutClassQNameByInfo(_outputs.get(2),_inputCls.getQName());
		outClsName=StringUtils.getClassNameForQName(outQName);
		
		_outputCls.addImport(outQName);
		
		method=new MethodInfo();
		method.name="createResult";
		method.returnType=StringUtils.getClassNameForQName(ShineToolSetting.resultQName);
		method.visitType=VisitType.Public;
		method.isOverride=true;
		
		writer=_outputCls.createWriter();
		writer.writeCustom("return " + _code.createNewObject(outClsName) + ";");
		writer.writeEnd();
		
		method.content=writer.toString();
		_outputCls.addMethod(method);
		
		//toSetValues
		
		method=new MethodInfo();
		method.name="toSetValues";
		method.returnType=_code.Void;
		method.visitType=VisitType.Protected;
		method.isOverride=true;
		method.args.add(new MethodArgInfo("isInsert",_code.Boolean));
		
		writer=_outputCls.createWriter();
		
		for(FieldInfo f : _fieldList)
		{
			//自增的
			if(_autoIncrementSet.contains(f.name))
			{
				writer.writeCustom("if(!isInsert)");
				writer.writeLeftBrace();
				toSetOneTableField(f,writer);
				writer.writeRightBrace();
			}
			else
			{
				toSetOneTableField(f,writer);
			}
		}
		
		writer.writeEnd();
		
		method.content=writer.toString();
		_outputCls.addMethod(method);
		
		//getPrimaryKeyByIndex
		
		method=new MethodInfo();
		method.name="getPrimaryKeyByIndex";
		method.returnType=_code.Object;
		method.visitType=VisitType.Public;
		method.isOverride=true;
		method.args.add(new MethodArgInfo("index",_code.Int));
		
		writer=_outputCls.createWriter();
		
		writer.writeCustom("switch(index)");
		writer.writeLeftBrace();
		
		for(int i=0;i<_indexKeyList.length();i++)
		{
			SList<String> list=_indexDic.get(_indexKeyList.get(i));
			
			//单一的才生成,这也是select
			if(list.length()==1)
			{
				writer.writeCustom("case " + i + ":");
				writer.writeCustomWithOff(1,"return " + list.get(0) + ";");
			}
		}
		
		writer.writeRightBrace();
		
		writer.writeCustom("return null;");
		writer.writeEnd();
		
		method.content=writer.toString();
		_outputCls.addMethod(method);
		
		//toSetPrimaryKeyByIndex
		
		method=new MethodInfo();
		method.name="toSetPrimaryKeyByIndex";
		method.returnType=_code.Void;
		method.visitType=VisitType.Protected;
		method.isOverride=true;
		method.args.add(new MethodArgInfo("index",_code.Int));
		
		writer=_outputCls.createWriter();
		
		writer.writeSwitch("index");
		
		for(int i=0;i<_indexKeyList.length();i++)
		{
			SList<String> list=_indexDic.get(_indexKeyList.get(i));
			
			writer.writeCase(String.valueOf(i));
			
			for(int j=0;j<list.length();j++)
			{
				toSetOneTableField(_fieldDic.get(list.get(j)),writer);
			}
			
			writer.endCase();
		}
		
		writer.writeRightBrace();
		writer.writeEnd();
		
		method.content=writer.toString();
		_outputCls.addMethod(method);
	}
	
	private void toMakeResult()
	{
		//toReadValues
		
		MethodInfo method=new MethodInfo();
		method.name="toReadValues";
		method.returnType=_code.Void;
		method.visitType=VisitType.Protected;
		method.isOverride=true;
		
		CodeWriter writer=_outputCls.createWriter();
		
		for(FieldInfo f : _fieldList)
		{
			toReadOneTableField(f,writer);
		}
		
		writer.writeEnd();
		
		method.content=writer.toString();
		_outputCls.addMethod(method);
		
		//getPrimaryKeyByIndex
		
		method=new MethodInfo();
		method.name="getPrimaryKeyByIndex";
		method.returnType=_code.Object;
		method.visitType=VisitType.Public;
		method.isOverride=true;
		method.args.add(new MethodArgInfo("index",_code.Int));
		
		writer=_outputCls.createWriter();
		
		writer.writeCustom("switch(index)");
		writer.writeLeftBrace();
		
		for(int i=0;i<_indexKeyList.length();i++)
		{
			SList<String> list=_indexDic.get(_indexKeyList.get(i));
			
			//单一的才生成,因为是select
			if(list.length()==1)
			{
				writer.writeCustom("case " + i + ":");
				writer.writeCustomWithOff(1,"return " + list.get(0) + ";");
			}
		}
		
		writer.writeRightBrace();
		writer.writeEmptyLine();
		writer.writeCustom("return null;");
		writer.writeEnd();
		
		method.content=writer.toString();
		_outputCls.addMethod(method);
	}
	
	//方法
	
	/** 设置一个TableField */
	private void toSetOneTableField(FieldInfo field,CodeWriter writer)
	{
		switch(field.type)
		{
			case "boolean":
			{
				writer.writeCustom("setBoolean(" + field.name + ");");
			}
			break;
			case "byte":
			{
				writer.writeCustom("setByte(" + field.name + ");");
			}
			break;
			case "short":
			{
				writer.writeCustom("setShort(" + field.name + ");");
			}
			break;
			case "int":
			{
				writer.writeCustom("setInt(" + field.name + ");");
			}
			break;
			case "long":
			{
				writer.writeCustom("setLong(" + field.name + ");");
			}
			break;
			case "String":
			{
				writer.writeCustom("setString(" + field.name + ");");
			}
			break;
			case "byte[]":
			{
				writer.writeCustom("setBytes(" + field.name + ");");
			}
			break;
			case "DateDO":
			{
				writer.writeCustom("setDate(" + field.name + ");");
			}
			break;
		}
	}
	
	private void toReadOneTableField(FieldInfo field,CodeWriter writer)
	{
		switch(field.type)
		{
			case "boolean":
			{
				writer.writeVarSet(field.name,"readBoolean()");
			}
			break;
			case "byte":
			{
				writer.writeVarSet(field.name,"readByte()");
			}
			break;
			case "short":
			{
				writer.writeVarSet(field.name,"readShort()");
			}
			break;
			case "int":
			{
				writer.writeVarSet(field.name,"readInt()");
			}
			break;
			case "long":
			{
				writer.writeVarSet(field.name,"readLong()");
			}
			break;
			case "String":
			{
				writer.writeVarSet(field.name,"readString()");
			}
			break;
			case "byte[]":
			{
				writer.writeVarSet(field.name,"readBytes()");
			}
			break;
			case "DateDO":
			{
				writer.writeVarSet(field.name,"readDate()");
			}
			break;
		}
	}
	
	private String getSqlType(String type,boolean isLong)
	{
		switch(type)
		{
			case "boolean":
				return "tinyint(1)";
			case "byte":
				return "tinyint(3)";
			case "short":
				return "smallint(5)";
			case "int":
				return "int(10)";
			case "long":
				return "bigint(20)";
			case "String":
				return isLong ? "text" : "varchar(128)";
			case "byte[]":
				return "longblob";
			case "DateDO":
				return "date";
		}
		
		return "";
	}
	
	private void toMakeSql()
	{
		int num=0;
		
		for(FieldInfo f : _fieldList)
		{
			if(num>0)
			{
				_sqlB.append(",");
			}
			
			_sqlB.append(Enter);
			_sqlB.append("\t");
			
			_sqlB.append("`");
			_sqlB.append(f.name);
			_sqlB.append("` ");
			_sqlB.append(getSqlType(f.type,f.hasAnnotation("UseText")));
			_sqlB.append(" not null");
			
			if(_autoIncrementSet.contains(f.name))
			{
				_sqlB.append(" auto_increment");
			}
			
			++num;
		}
		
		_sqlB.append(",");
		_sqlB.append(Enter);
		_sqlB.append("\t");
		
		_sqlB.append("primary key (");
		
		for(int i=0;i<_primaryKeyList.length();i++)
		{
			if(i>0)
				_sqlB.append(',');
			
			_sqlB.append('`');
			_sqlB.append(_primaryKeyList.get(i));
			_sqlB.append('`');
		}
		
		_sqlB.append(')');
		
		//跳过主键
		for(int i=1;i<_indexKeyList.length();i++)
		{
			String name=_indexKeyList.get(i);
			
			SList<String> list=_indexDic.get(name);
			
			_sqlB.append(",");
			_sqlB.append(Enter);
			_sqlB.append("\t");
			
			//TODO:唯一
			////自增,加唯一
			//if(_autoIncrementSet.contains(idx))
			//{
			//	_sqlB.append("unique ");
			//}
			
			_sqlB.append("key `");
			_sqlB.append(name);
			_sqlB.append("` (");
			
			for(int j=0;j<list.length();j++)
			{
				if(j>0)
					_sqlB.append(',');
				
				_sqlB.append('`');
				_sqlB.append(list.get(j));
				_sqlB.append('`');
			}
			
			_sqlB.append(")");
		}
		
		_sqlB.append(Enter);
		_sqlB.append(") ");
		
		String engine=_inputCls.hasAnnotation("UseMyisam") ? "MyISAM" : "InnoDB";
		
		_sqlB.append("engine="+engine+" default charset= utf8 comment='");
		_sqlB.append(_inputCls.clsDescribe);
		_sqlB.append("';");
		
		
		String sql=_sqlB.toString();
		
		_sqlDic.put(_tableName,sql);
	}
	
	/** 获取sql */
	public String getSql(String tableName)
	{
		String re=_sqlDic.get(tableName);
		
		if(re==null)
		{
			if(_commonTool!=null)
			{
				re=_commonTool.getSql(tableName);
			}
		}
		
		return re;
	}
	
	public String getMd5()
	{
		StringBuilder sb=new StringBuilder();
		
		for(String k:_sqlDic.getSortedKeyList())
		{
			sb.append(k);
			sb.append(_sqlDic.get(k));
		}
		
		return StringUtils.md5(sb.toString());
	}
}
