package com.home.shineTool.tool.export;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.SList;
import com.home.shine.utils.StringUtils;
import com.home.shineTool.constlist.CodeType;
import com.home.shineTool.constlist.DataGroupType;
import com.home.shineTool.constlist.VisitType;
import com.home.shineTool.global.ShineToolSetting;
import com.home.shineTool.reflect.MethodArgInfo;
import com.home.shineTool.reflect.MethodInfo;
import com.home.shineTool.reflect.cls.ClassInfo;
import com.home.shineTool.reflect.code.CodeWriter;
import com.home.shineTool.tool.base.BaseExportTool;
import com.home.shineTool.tool.base.RecordDataClassInfo;
import com.home.shineTool.tool.data.DataExportTool;

public class DataDataExportTool extends DataExportTool
{
	/** 角色事务 */
	private static final int Work_Player=1;
	/** 区服事务 */
	private static final int Work_Area=2;
	/** 中心服事务 */
	private static final int Work_Center=3;
	
	/** 常规事务 */
	private static final int NormalWork=1;
	/** TCC事务到达 */
	private static final int TCCWork=2;
	/** TCC事务结果 */
	private static final int TCCWorkResult=3;
	
	//playerWork
	private static final int PlayerWork=0;
	private static final int PlayerToRoleGroupTCCResult=1;
	private static final int PlayerToPlayerTCC=2;
	private static final int PlayerToPlayerTCCResult=3;
	
	//area
	private static final int AreaWork=0;
	private static final int RoleGroupWork=1;
	private static final int PlayerToRoleGroupTCC=2;
	
	//center
	private static final int CenterWork=0;
	
	/** 根导出工具 */
	private BaseDataExportTool _rootExportTool;
	
	private DataDataExportTool _superTool;
	
	/** 角色事务控制类 */
	private ClassInfo _playerWorkControl;
	/** 区服事务控制类 */
	private ClassInfo _areaWorkControl;
	/** 中心服事务控制类 */
	private ClassInfo _centerWorkControl;
	
	private WorkInfo[] _playerWorkArr;
	private WorkInfo[] _areaWorkArr;
	private WorkInfo[] _centerWorkArr;
	
	private SList<WorkInfo> _playerWorkFindList;
	private SList<WorkInfo> _areaWorkFindList;
	private SList<WorkInfo> _centerWorkFindList;
	
	private boolean _hasAny=false;
	
	public void setRootDataExportTool(BaseDataExportTool tool)
	{
		_rootExportTool=tool;
	}
	
	private void initWork()
	{
		WorkInfo workInfo;
		
		_playerWorkArr=new WorkInfo[4];
		_playerWorkFindList=new SList<>();
		
		_areaWorkArr=new WorkInfo[3];
		_areaWorkFindList=new SList<>();
		
		_centerWorkArr=new WorkInfo[1];
		_centerWorkFindList=new SList<>();
		
		//center
		_centerWorkFindList.add(_centerWorkArr[CenterWork]=new WorkInfo(ShineToolSetting.centerWorkDOQName,NormalWork,Work_Center,"regist","注册","registOne"));
		
		//先area
		_areaWorkFindList.add(_areaWorkArr[AreaWork]=new WorkInfo(ShineToolSetting.areaWorkDOQName,NormalWork,Work_Area,"regist","注册","registOne"));
		_areaWorkFindList.add(workInfo=_areaWorkArr[RoleGroupWork]=new WorkInfo(ShineToolSetting.roleGroupWorkDOQName,NormalWork,Work_Area,"registRoleGroup","注册玩家群事务","registOneRoleGroup"));
		workInfo.hasRoleGroup=true;
		_areaWorkFindList.add(workInfo=_areaWorkArr[PlayerToRoleGroupTCC]=new WorkInfo(ShineToolSetting.playerToRoleGroupTCCWorkDOQName,TCCWork,Work_Area,"registRoleGroupTCC","注册玩家群TCC事务","registOneRoleGroupTCC"));
		workInfo.hasRoleGroup=true;
		
		//再player
		_playerWorkFindList.add(_playerWorkArr[PlayerWork]=new WorkInfo(ShineToolSetting.playerWorkDOQName,NormalWork,Work_Player,"regist","注册","registOne"));
		_areaWorkFindList.add(_playerWorkArr[PlayerToRoleGroupTCCResult]=new WorkInfo(ShineToolSetting.playerToRoleGroupTCCWorkDOQName,TCCWorkResult,Work_Player,"registRoleGroupTCCResult","注册玩家群TCC事务结果","registOneRoleGroupTCCResult"));
		_playerWorkFindList.add(_playerWorkArr[PlayerToPlayerTCC]=new WorkInfo(ShineToolSetting.playerToPlayerTCCWorkDOQName,TCCWork,Work_Player,"registPlayerTCC","注册玩家TCC事务到达","registOnePlayerTCC"));
		_playerWorkFindList.add(_playerWorkArr[PlayerToPlayerTCCResult]=new WorkInfo(ShineToolSetting.playerToPlayerTCCWorkDOQName,TCCWorkResult,Work_Player,"registPlayerTCCResult","注册玩家TCC事务结果","registOnePlayerTCCResult"));
	}
	
	/** 检查当前是否需要执行 */
	@Override
	protected boolean checkNeedDoCurrent()
	{
		boolean need=true;
		
		if(_outputInfo.isClientOrRobot())
		{
			if(checkInputClassDontOut(_inputCls.getQName(),1))
			{
				need=false;
			}
		}
		else
		{
			if(checkInputClassDontOut(_inputCls.getQName(),2))
			{
				need=false;
			}
		}
		
		return need;
	}
	
	@Override
	protected void toExecuteFileList()
	{
		//无论是G还是H，都可用来做下面createClass的逻辑
		_superTool=(DataDataExportTool)getUpTool();
		
		initWork();
		
		if(_rootExportTool instanceof NormalDataExportTool)
		{
			NormalDataExportTool rootE=(NormalDataExportTool)_rootExportTool;
			
			String mark=_superTool==null ? "" : "G";
			String path=rootE.projectRoots[NormalDataExportTool.ServerGame]+"/control/"+mark+"PlayerWorkControl."+CodeType.getExName(getOutputInfo(DataGroupType.Server).codeType);
			
			_playerWorkControl=ClassInfo.getClassInfoFromPath(path);
			
			if(_playerWorkControl==null)
			{
				if(_superTool==null)
				{
					Ctrl.throwError("C层未找到");
				}
				else
				{
					_playerWorkControl=ClassInfo.getClassInfoFromPathAbs(path);
					_playerWorkControl.extendsClsName=_superTool._playerWorkControl.clsName;
					_playerWorkControl.addImport(_superTool._playerWorkControl.getQName());
				}
			}
			
			path=rootE.projectRoots[NormalDataExportTool.ServerGame]+"/control/"+mark+"AreaWorkControl."+CodeType.getExName(getOutputInfo(DataGroupType.Server).codeType);
			
			_areaWorkControl=ClassInfo.getClassInfoFromPath(path);
			
			if(_areaWorkControl==null)
			{
				if(_superTool==null)
				{
					Ctrl.throwError("C层未找到");
				}
				else
				{
					_areaWorkControl=ClassInfo.getClassInfoFromPathAbs(path);
					_areaWorkControl.extendsClsName=_superTool._areaWorkControl.clsName;
					_areaWorkControl.addImport(_superTool._areaWorkControl.getQName());
				}
			}
			
			path=rootE.projectRoots[NormalDataExportTool.ServerCenter]+"/control/"+mark+"CenterWorkControl."+CodeType.getExName(getOutputInfo(DataGroupType.Server).codeType);
			
			_centerWorkControl=ClassInfo.getClassInfoFromPath(path);
			
			if(_centerWorkControl==null)
			{
				if(_superTool==null)
				{
					Ctrl.throwError("C层未找到");
				}
				else
				{
					_centerWorkControl=ClassInfo.getClassInfoFromPathAbs(path);
					_centerWorkControl.extendsClsName=_superTool._centerWorkControl.clsName;
					_centerWorkControl.addImport(_superTool._centerWorkControl.getQName());
				}
			}
			
			for(WorkInfo v:_playerWorkArr)
			{
				addRegistMethod(_playerWorkControl,v.methodName,v.describe);
			}
			
			for(WorkInfo v:_areaWorkArr)
			{
				addRegistMethod(_areaWorkControl,v.methodName,v.describe);
			}
			
			for(WorkInfo v:_centerWorkArr)
			{
				addRegistMethod(_centerWorkControl,v.methodName,v.describe);
			}
		}
		
		super.toExecuteFileList();
	}
	
	private void addRegistMethod(ClassInfo cls,String name,String describe)
	{
		MethodInfo registMethod=cls.getMethodByName(name);
		
		if(registMethod==null)
		{
			registMethod=new MethodInfo();
			registMethod.name=name;
			registMethod.visitType=VisitType.Protected;
			registMethod.isOverride=true;
			registMethod.returnType=cls.getCode().Void;
			registMethod.describe=describe;
			
			cls.addMethod(registMethod);
		}
	}
	
	@Override
	protected void toMakeAfter()
	{
		super.toMakeAfter();
	
		//server
		if(_outputInfo.group==DataGroupType.Server)
		{
			RecordDataClassInfo cls=BaseExportTool.newRecordClsDic.get(_inputCls.getQName());
			
			//是事务
			if(isExtendFrom(cls,ShineToolSetting.workDOQName))
			{
				String front=_outputCls.clsName.substring(0,_outputCls.clsName.length()-_outputInfo.nameTail.length());
				
				//角色事务
				if(isExtendFrom(cls,ShineToolSetting.playerWorkDOQName))
				{
					doOneFunc(cls,front,_playerWorkFindList);
				}
				//区服事务
				else if(isExtendFrom(cls,ShineToolSetting.areaWorkDOQName))
				{
					doOneFunc(cls,front,_areaWorkFindList);
				}
				//中心服
				else if(isExtendFrom(cls,ShineToolSetting.centerWorkDOQName))
				{
					doOneFunc(cls,front,_centerWorkFindList);
				}
			}
		}
	}
	
	private void doOneFunc(RecordDataClassInfo cls,String front,SList<WorkInfo> list)
	{
		WorkInfo workInfo;
		
		for(int i=list.size()-1;i>=0;--i)
		{
			workInfo=list.get(i);
			
			if(isExtendFrom(cls,workInfo.qName))
			{
				if(workInfo.entityType==Work_Player)
				{
					addPlayerFunc(front,workInfo.dataName,workInfo.type);
				}
				else if(workInfo.entityType==Work_Area)
				{
					addAreaFunc(workInfo.hasRoleGroup,front,workInfo.dataName,workInfo.type);
				}
				else if(workInfo.entityType==Work_Center)
				{
					addCenterFunc(front,workInfo.dataName,workInfo.type);
				}
				
				workInfo.hasDid=true;
				_hasAny=true;
				
				//不是结果组
				if(workInfo.type!=TCCWorkResult)
				{
					return;
				}
			}
		}
	}
	
	private void addPlayerFunc(String front,String workName,int type)
	{
		MethodInfo method=new MethodInfo();
		method.name="do"+front;
		method.visitType=VisitType.Private;
		
		if(type==TCCWork)
			method.returnType=_outputCls.getCode().Int;
		else
			method.returnType=_outputCls.getCode().Void;
		
		method.describe=_outputCls.clsDescribe;
		method.args.add(new MethodArgInfo("me","Player"));
		method.args.add(new MethodArgInfo("wData",workName));
		
		if(type==TCCWorkResult)
		{
			method.name+="Result";
			method.args.add(new MethodArgInfo("result",_outputCls.getCode().Int));
		}
		
		//没有
		if(_playerWorkControl.getMethod(method.getKey())==null)
		{
			CodeWriter writer=_outputCls.createWriter();
			
			writer.writeVarCreate("data",_outputCls.clsName,_outputCls.getCode().getVarTypeTrans("wData",_outputCls.clsName));
			if(_superTool!=null)
			{
				String me=ShineToolSetting.needHotfix ? "hme" : "gme";
				String player=ShineToolSetting.needHotfix ? "HPlayer" : "GPlayer";
				writer.writeVarCreate(me,player,_outputCls.getCode().getVarTypeTrans("me",player));
			}
			
			writer.writeEmptyLine();
			
			if(type==TCCWork)
			{
				writer.writeReturnBoolean(true);
			}
			
			writer.writeEnd();
			method.content=writer.toString();
			
			_playerWorkControl.addImport(_outputCls.getQName());
			_playerWorkControl.addMethod(method);
		}
	}
	
	private void addAreaFunc(boolean isRoleGroup,String front,String workName,int type)
	{
		MethodInfo method=new MethodInfo();
		method.name="do"+front;
		method.visitType=VisitType.Private;
		
		if(type==TCCWork)
			method.returnType=_outputCls.getCode().Int;
		else
			method.returnType=_outputCls.getCode().Void;
		
		method.describe=_outputCls.clsDescribe;
		
		if(isRoleGroup)
		{
			method.args.add(new MethodArgInfo("me","RoleGroup"));
		}
		
		method.args.add(new MethodArgInfo("wData",workName));
		
		if(type==TCCWorkResult)
		{
			method.name+="Result";
			method.args.add(new MethodArgInfo("result",_outputCls.getCode().Int));
		}
		
		//没有
		if(_areaWorkControl.getMethod(method.getKey())==null)
		{
			CodeWriter writer=_outputCls.createWriter();
			
			writer.writeVarCreate("data",_outputCls.clsName,_outputCls.getCode().getVarTypeTrans("wData",_outputCls.clsName));
			
			//if(_superTool!=null)
			//{
			//	String me=ShineToolSetting.needHotfix ? "hme" : "gme";
			//	String player=ShineToolSetting.needHotfix ? "HPlayer" : "GPlayer";
			//	writer.writeVarCreate(me,player,_outputCls.getCode().getVarTypeTrans("me",player));
			//}
			
			writer.writeEmptyLine();
			
			if(type==TCCWork)
			{
				writer.writeReturn("0");
			}
			
			writer.writeEnd();
			method.content=writer.toString();
			
			_areaWorkControl.addImport(_outputCls.getQName());
			_areaWorkControl.addMethod(method);
		}
	}
	
	private void addCenterFunc(String front,String workName,int type)
	{
		MethodInfo method=new MethodInfo();
		method.name="do"+front;
		method.visitType=VisitType.Private;
		
		if(type==TCCWork)
			method.returnType=_outputCls.getCode().Int;
		else
			method.returnType=_outputCls.getCode().Void;
		
		method.describe=_outputCls.clsDescribe;
		
		//if(isRoleGroup)
		//{
		//	method.args.add(new MethodArgInfo("me","RoleGroup"));
		//}
		
		method.args.add(new MethodArgInfo("wData",workName));
		
		if(type==TCCWorkResult)
		{
			method.name+="Result";
			method.args.add(new MethodArgInfo("result",_outputCls.getCode().Int));
		}
		
		//没有
		if(_centerWorkControl.getMethod(method.getKey())==null)
		{
			CodeWriter writer=_outputCls.createWriter();
			
			writer.writeVarCreate("data",_outputCls.clsName,_outputCls.getCode().getVarTypeTrans("wData",_outputCls.clsName));
			
			writer.writeEmptyLine();
			
			if(type==TCCWork)
			{
				writer.writeReturn("0");
			}
			
			writer.writeEnd();
			method.content=writer.toString();
			
			_centerWorkControl.addImport(_outputCls.getQName());
			_centerWorkControl.addMethod(method);
		}
	}
	
	@Override
	protected void endExecute()
	{
		super.endExecute();
		
		if(_hasAny)
		{
			endRegistFirst(_playerWorkControl,_playerWorkArr);
			endRegistFirst(_areaWorkControl,_areaWorkArr);
			endRegistFirst(_centerWorkControl,_centerWorkArr);
			
			_allInputQNameSet.getSortedList().forEach(k->
			{
				RecordDataClassInfo cls=BaseExportTool.newRecordClsDic.get(k);
				
				//是事务
				if(isExtendFrom(cls,ShineToolSetting.workDOQName))
				{
					//角色事务
					if(isExtendFrom(cls,ShineToolSetting.playerWorkDOQName))
					{
						doOneWrite(cls,_playerWorkFindList);
					}
					else if(isExtendFrom(cls,ShineToolSetting.areaWorkDOQName))
					{
						doOneWrite(cls,_areaWorkFindList);
					}
					else if(isExtendFrom(cls,ShineToolSetting.centerWorkDOQName))
					{
						doOneWrite(cls,_centerWorkFindList);
					}
				}
			});
			
			endRegistLast(_playerWorkControl,_playerWorkArr);
			endRegistLast(_areaWorkControl,_areaWorkArr);
			endRegistLast(_centerWorkControl,_centerWorkArr);
		}
	}
	
	private void doOneWrite(RecordDataClassInfo cls,SList<WorkInfo> list)
	{
		WorkInfo workInfo;
		
		for(int i=list.size()-1;i>=0;--i)
		{
			workInfo=list.get(i);
			
			if(isExtendFrom(cls,workInfo.qName))
			{
				addRegistOne(workInfo,cls);
				
				//不是结果组
				if(workInfo.type!=TCCWorkResult)
				{
					return;
				}
			}
		}
	}
	
	private void endRegistFirst(ClassInfo cls,WorkInfo[] arr)
	{
		WorkInfo workInfo;
		
		for(int i=0;i<arr.length;i++)
		{
			workInfo=arr[i];
			
			workInfo.registMethod=cls.getMethodByName(workInfo.methodName);
			
			workInfo.writer=cls.createWriter();
			
			if(_superTool!=null)
			{
				workInfo.writer.writeSuperMethod(workInfo.registMethod);
				workInfo.writer.writeEmptyLine();
			}
		}
	}
	
	private void endRegistLast(ClassInfo cls,WorkInfo[] arr)
	{
		WorkInfo workInfo;
		
		for(int i=0;i<arr.length;i++)
		{
			workInfo=arr[i];
			
			workInfo.writer.writeEnd();
			workInfo.registMethod.content=workInfo.writer.toString();
		}
		
		cls.write();
	}
	
	private void addRegistOne(WorkInfo info,RecordDataClassInfo cls)
	{
		String cName=StringUtils.getClassNameForQName(cls.clsQName);
		cName=cName.substring(0,cName.length()-_mark.length());
		String outName=cName+getOutputInfo(DataGroupType.Server).nameTail;
		
		if(info.type==TCCWorkResult)
		{
			cName+="Result";
		}
		
		info.writer.writeCustom(info.registOneMethodName+"("+outName+".dataID,this::do"+cName+");");
	}
	
	private class WorkInfo
	{
		/** DO完全限定名 */
		public String qName;
		
		public String dataName;
		
		/** 事务类型 */
		public int type;
		/** 主体类型 */
		public int entityType;
		
		public String methodName;
		
		public String describe;
		
		public String registOneMethodName;
		
		/** 是否有执行的 */
		public boolean hasDid;
		
		public boolean hasRoleGroup=false;
		
		public MethodInfo registMethod;
		
		public CodeWriter writer;
		
		public WorkInfo(String qName,int type,int entityType,String methodName,String describe,String registOneMethodName)
		{
			this.qName=qName;
			String temp=StringUtils.getClassNameForQName(qName);
			this.dataName=temp.substring(0,temp.length()-2)+"Data";
			
			this.type=type;
			this.entityType=entityType;
			this.methodName=methodName;
			this.describe=describe;
			this.registOneMethodName=registOneMethodName;
		}
		
	}
}
