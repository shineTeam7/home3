package com.home.shineTool.tool.export;

import java.util.Map;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.SList;
import com.home.shine.utils.FileUtils;
import com.home.shine.utils.StringUtils;
import com.home.shineTool.constlist.CodeType;
import com.home.shineTool.constlist.ProjectType;
import com.home.shineTool.constlist.VisitType;
import com.home.shineTool.global.ShineProjectPath;
import com.home.shineTool.global.ShineToolGlobal;
import com.home.shineTool.reflect.FieldInfo;
import com.home.shineTool.reflect.MainMethodInfo;
import com.home.shineTool.reflect.MethodInfo;
import com.home.shineTool.reflect.cls.ClassInfo;
import com.home.shineTool.reflect.code.CodeInfo;
import com.home.shineTool.reflect.code.CodeWriter;
import com.home.shineTool.global.ShineToolSetting;
import com.home.shineTool.tool.config.ConfigMakeTool;
import com.home.shineTool.utils.ToolFileUtils;

/** 配置生成工具 */
public class NormalConfigMakeTool extends ConfigMakeTool
{
	private static final int UI=1;
	private static final int UILogic=2;
	private static final int Text=3;
	
	
	private int _makeIndex=-1;
	private CodeWriter _writer;
	
	//ui
	
	private String _uiClsPath;
	private ClassInfo _uiCls;
	
	private SList<ClassInfo> _parentUIClsList=new SList<>();
	
	//text
	
	
	public NormalConfigMakeTool()
	{
		//特殊名字表
		_wholeWordList.add("cd");
		_wholeWordList.add("ui");
		_wholeWordList.add("npc");
		
		//editor所用
		_editorNeedSet.add("font");
		
		//自定义配置
		addCustom("trigger","触发器配置",false);
		addCustom("scenePlaceEditor","场景摆放元素(编辑器)",true);
		addCustom("mapInfo","地图阻挡数据(方格)",true);
	}
	
	public static String getProjectPathS(int projectType,boolean isClient)
	{
		switch(projectType)
		{
			case ProjectType.Common:
			{
				if(isClient)
				{
					return ShineProjectPath.clientCommonGameDirPath;
				}
				else
				{
					return ShineProjectPath.serverCommonBaseDirPath;
				}
			}
			case ProjectType.Game:
			{
				if(isClient)
				{
					return ShineProjectPath.clientGameDirPath;
				}
				else
				{
					return ShineProjectPath.serverBaseDirPath;
				}
			}
			case ProjectType.HotFix:
			{
				if(isClient)
				{
					return ShineProjectPath.clientHotfixDirPath;
				}
				else
				{
					return ShineProjectPath.serverBaseDirPath;
				}
			}
		}
		
		return "";
	}
	
	@Override
	public String getProjectPath(int projectType,boolean isClient)
	{
		return getProjectPathS(projectType,isClient);
	}
	
	protected String getGamePath()
	{
		return getProjectPath(_projectType,_isHClient);
	}
	
	@Override
	protected boolean beginWriteHConfig()
	{
		switch(_hData.useName)
		{
			case "ui":
			{
				if(ShineToolSetting.isClientOnlyData)
					return false;
				
				if(_isHClient)
				{
					_makeIndex=UI;
					startMakeUIConfig();
					
					return true;
				}
			}
				break;
			case "uiLogic":
			{
				if(_isHClient)
				{
					_makeIndex=UILogic;
					return true;
				}
			}
				break;
			case "text":
			{
				_makeIndex=Text;
				startMakeTextConfig();

				return true;
			}
		}
		
		return false;
	}
	
	@Override
	protected void writeOneHConfig(Map<String,String> values)
	{
		switch(_makeIndex)
		{
			case UI:
			{
				if(ShineToolSetting.isClientOnlyData)
					return;
				
				//if(isMainProject())
				//	return;
				
				makeOneUIConfig(values);
			}
				break;
			case UILogic:
			{
				makeOneUILogicConfig(values);
			}
				break;
			case Text:
			{
				makeOneTextConfig(values);
			}
				break;
		}
	}

	@Override
	protected void endWriteHConfig()
	{
		switch(_makeIndex)
		{
			case UI:
			{
				if(ShineToolSetting.isClientOnlyData)
					return;
				
				//if(isMainProject())
				//	return;
				
				endMakeUIConfig();
			}
				break;
			case UILogic:
			{
				//if(isMainProject())
				//	return;
				
				//endMakeUIConfig();
			}
				break;
			case Text:
			{
				endMakeTextConfig();
			}
				break;
		}
		
		_makeIndex=-1;
	}
	
	protected NormalConfigMakeTool getParentNormalTool()
	{
		return (NormalConfigMakeTool)getParentTool();
	}
	
	private void startMakeUIConfig()
	{
		toStartMakeConstConfig("ui","UIInstanceType","UI实例类型",-1);
		
		_uiClsPath=getGamePath() + "/control/" + _projectUFront + "GameUI" + "." + CodeType.getExName(_codeType);
		_uiCls=ClassInfo.getClassInfoFromPathAbs(_uiClsPath);
		
		ConfigMakeTool parentTool=this;
		
		while((parentTool=parentTool.getParentTool())!=null)
		{
			String projectPath=getProjectPath(parentTool.getProjectType(),_isHClient);
			
			String commonUIControlClsPath=projectPath + "/control/" +parentTool.getProjectUFront()+ "GameUI" + "." + CodeType.getExName(_codeType);
			
			_parentUIClsList.add(ClassInfo.getClassInfoFromPathAbs(commonUIControlClsPath));
			
			String parentCPath=projectPath + "/global/"+parentTool.getProjectUFront()+"GameC" + "." + CodeType.getExName(_codeType);
			
			_uiCls.addImport(FileUtils.getPathQName(parentCPath));
		}
		
		if(ProjectType.isLastProject(getProjectType()))
		{
			_writer=_uiCls.createWriter();
		}
	}
	
	private void makeOneUIConfig(Map<String,String> values)
	{
		toMakeOneConstConfig(values);
		
		String existType=getFValue("existType",values);
		String name=getFValue("name",values);
		String describe=getFValue("describe",values);
		String packageName=getFValue("packageName",values);
		
		//名字空
		if(name.isEmpty())
		{
			return;
		}
		
		String ucName=StringUtils.ucWord(name);
		
		String pp=packageName.replace('.','/');
		
		if(!pp.isEmpty())
		{
			if(!pp.endsWith("/"))
			{
				pp+='/';
			}
		}
		
		String uiClsPath=getGamePath() + "/view/ui/" + pp + _projectUFront + ucName + "UI" + "." + CodeType.getExName(_codeType);
		
		ClassInfo uiCls=ClassInfo.getClassInfoFromPath(uiClsPath);
		
		ConfigMakeTool parentTool=getParentTool();
		
		ClassInfo parentUICls=null;
		
		if(parentTool!=null)
		{
			String projectPath=getProjectPath(parentTool.getProjectType(),_isHClient);
			
			String parentUIClsPath=projectPath + "/view/ui/" + pp + parentTool.getProjectUFront() + ucName + "UI" + "." + CodeType.getExName(_codeType);
			
			parentUICls=ClassInfo.getSimpleClassInfoFromPath(parentUIClsPath);
		}
		
		String uiInstanceID=_constCls.clsName + "." + ucName;
		
		//初始生成
		if(uiCls==null)
		{
			uiCls=ClassInfo.getVoidClassInfoFromPath(uiClsPath);
			
			//MainMethodInfo mainMethod=new MainMethodInfo();
			
			if(_hData.parentData==null || _hData.parentData.fieldTotalDic.get(name)==null)
			{
				String sQName="commonGame.view.ui.base.GameUIBase";
				
				uiCls.addImport(sQName);
				uiCls.extendsClsName="GameUIBase";
				//mainMethod.superArgs.add(uiInstanceID);
			}
			else
			{
				if(parentUICls==null)
				{
					Ctrl.throwError("找不到基类",uiCls.clsName);
				}
				
				uiCls.addImport(parentUICls.getQName());
				uiCls.extendsClsName=parentUICls.clsName;
			}
			
			String uMark=getProjectUFront();
			
			//TODO:暂时先只做game工程的输出,后面工程调整完了再补充hotfix工程的输出
			
			uiCls.addImport("game.player."+uMark+"Player");
			
			FieldInfo gmeField=new FieldInfo();
			gmeField.visitType=VisitType.Public;
			gmeField.type=uMark+"Player";
			gmeField.name=getProjectLFront()+"me";
			gmeField.describe="玩家对象";
			gmeField.defaultValue=uMark+"GameC.player";
			
			uiCls.addField(gmeField);
			
			if(ProjectType.isLastProject(getProjectType()))
			{
				String modelName="UIModel_"+name+"UI";
				
				uiCls.addImport("game.view.uiGenerate.models."+modelName);
				
				FieldInfo modelField=new FieldInfo();
				modelField.visitType=VisitType.Private;
				modelField.type=modelName;
				modelField.name="_model";
				modelField.describe="UI模型";
				modelField.defaultValue="";
				
				uiCls.addField(modelField);
				
				uiCls.addImport("shine.view.ui.element.UIModel");
				
				MethodInfo method=new MethodInfo();
				method.visitType=VisitType.Protected;
				method.isOverride=true;
				method.name="createModel";
				method.returnType="UIModel";
				
				CodeWriter writer=uiCls.createWriter();
				writer.writeCustom("return _model="+uiCls.getCode().createNewObject(modelName)+";");
				writer.writeEnd();
				method.content=writer.toString();
				uiCls.addMethod(method);
			}
			
			
			//mainMethod.makeEmptyMethod(_codeType);
			//
			//uiCls.addMainMethod(mainMethod);
			
			MethodInfo method=new MethodInfo();
			method.visitType=VisitType.Protected;
			method.isOverride=true;
			method.name="registLogics";
			method.makeEmptyMethodWithSuper(_codeType);
			uiCls.addMethod(method);
			
			method=new MethodInfo();
			method.visitType=VisitType.Protected;
			method.isOverride=true;
			method.name="init";
			method.makeEmptyMethodWithSuper(_codeType);
			uiCls.addMethod(method);
			
			method=new MethodInfo();
			method.visitType=VisitType.Protected;
			method.isOverride=true;
			method.name="dispose";
			method.makeEmptyMethodWithSuper(_codeType);
			uiCls.addMethod(method);
			
			method=new MethodInfo();
			method.visitType=VisitType.Protected;
			method.isOverride=true;
			method.name="onEnter";
			method.makeEmptyMethodWithSuper(_codeType);
			uiCls.addMethod(method);
			
			method=new MethodInfo();
			method.visitType=VisitType.Protected;
			method.isOverride=true;
			method.name="onExit";
			method.makeEmptyMethodWithSuper(_codeType);
			uiCls.addMethod(method);
			
			if(ShineToolSetting.needUIResize)
			{
				method=new MethodInfo();
				method.visitType=VisitType.Protected;
				method.isOverride=true;
				method.name="onResize";
				method.makeEmptyMethodWithSuper(_codeType);
				uiCls.addMethod(method);
			}
			
			uiCls.write();
		}
		else
		{
			//一次性的代码
			
			////不是common
			//if(!_isCommon)
			//{
			//	uiCls.addImport("game.player.GPlayer");
			//
			//	FieldInfo gmeField=new FieldInfo();
			//	gmeField.visitType=VisitType.Public;
			//	gmeField.type="GPlayer";
			//	gmeField.name="gme";
			//	gmeField.describe="玩家对象";
			//	gmeField.defaultValue="GGameC.player";
			//
			//	uiCls.addField(gmeField);
			//
			//	uiCls.write();
			//}
		}
		
		//单例(1和3输出)
		if(existType.equals("1") || existType.equals("3"))
		{
			//String uName=_isCommon ? name : "g" + name;
			FieldInfo field=new FieldInfo();
			field.visitType=VisitType.Public;
			field.isStatic=true;
			field.type=uiCls.clsName;
			field.name=name;
			field.describe=describe;
			
			_uiCls.addImport(uiCls.getQName());
			_uiCls.addField(field);
			
			if(ProjectType.isLastProject(getProjectType()))
			{
				_writer.writeVarSet(name,_cCode.createNewObject(uiCls.clsName));
				_writer.writeCustom(name+".construct("+uiInstanceID+");");
				
				for(ClassInfo v : _parentUIClsList)
				{
					if(v.getField(name)!=null)
					{
						_writer.writeVarSet(v.clsName+"."+name,name);
					}
				}
				
				_writer.writeCustom("GameC.ui.registUI(" + name + ");");
			}
			
		}
		//多实例
		else if(existType.equals("2"))
		{
			MethodInfo method=new MethodInfo();
			method.visitType=VisitType.Public;
			method.isStatic=true;
			method.returnType=uiCls.clsName;
			method.name="get"+StringUtils.ucWord(name);
			method.describe=describe;
			
			//GameC.ui.registMultiUI(GUIInstanceType.Alert,()=>new GAlertUI());
			CodeWriter writer=uiCls.createWriter();
			writer.writeCustom("return "+_cCode.getVarTypeTrans("GameC.ui.getMultiUI("+uiInstanceID+")",uiCls.clsName)+";");
			writer.writeEnd();
			
			method.content=writer.toString();
			
			_uiCls.addImport(uiCls.getQName());
			
			_uiCls.addMethod(method);
			
			if(ProjectType.isLastProject(getProjectType()))
			{
				//TODO:强制客户端为C#了
				//_writer.writeCustom("GameC.ui.registMultiUI(" + uiInstanceID + ",()=>"+_cCode.createNewObject(uiCls.clsName)+");");
				_writer.writeCustom("GameC.ui.registMultiUI(" + uiInstanceID + ",()=>{");
				_writer.writeCustomWithOff(1,uiCls.clsName+" re="+_cCode.createNewObject(uiCls.clsName)+";");
				_writer.writeCustomWithOff(1,"re.construct("+uiInstanceID+");");
				_writer.writeCustomWithOff(1,"return re;");
				_writer.writeCustom("});");
				//+_cCode.createNewObject(uiCls.clsName)+");
			}
			
		}
	}
	
	/** 构造单个UI逻辑配置 */
	private void makeOneUILogicConfig(Map<String,String> values)
	{
		String name=getFValue("name",values);
		//String describe=getFValue("describe",values);
		String packageName=getFValue("packageName",values);
		
		//名字空
		if(name.isEmpty())
		{
			return;
		}
		
		String ucName=StringUtils.ucWord(name);
		
		String pp=packageName.replace('.','/');
		
		if(!pp.isEmpty())
		{
			if(!pp.endsWith("/"))
			{
				pp+='/';
			}
		}
		
		String uiClsPath=getGamePath() + "/view/ui/" + pp + _projectUFront + ucName + "UILogic" + "." + CodeType.getExName(_codeType);
		
		ClassInfo uiCls=ClassInfo.getClassInfoFromPath(uiClsPath);
		
		ConfigMakeTool parentTool=getParentTool();
		
		ClassInfo parentUICls=null;
		
		if(parentTool!=null)
		{
			String projectPath=getProjectPath(parentTool.getProjectType(),_isHClient);
			
			String parentUIClsPath=projectPath + "/view/ui/" + pp + parentTool.getProjectUFront() + ucName + "UILogic" + "." + CodeType.getExName(_codeType);
			
			parentUICls=ClassInfo.getSimpleClassInfoFromPath(parentUIClsPath);
		}
		
		
		//初始生成
		if(uiCls==null)
		{
			uiCls=ClassInfo.getVoidClassInfoFromPath(uiClsPath);
			
			if(_hData.parentData==null || _hData.parentData.fieldTotalDic.get(name)==null)
			{
				String sQName="commonGame.view.ui.base.GameUILogicBase";
				
				uiCls.addImport(sQName);
				uiCls.extendsClsName="GameUILogicBase";
			}
			else
			{
				if(parentUICls==null)
				{
					Ctrl.throwError("找不到基类",uiCls.clsName);
				}
				
				uiCls.addImport(parentUICls.getQName());
				uiCls.extendsClsName=parentUICls.clsName;
			}
			
			String uMark=getProjectUFront();
			
			//TODO:暂时先只做game工程的输出,后面工程调整完了再补充hotfix工程的输出
			
			uiCls.addImport("game.player."+uMark+"Player");
			
			FieldInfo gmeField=new FieldInfo();
			gmeField.visitType=VisitType.Public;
			gmeField.type=uMark+"Player";
			gmeField.name=getProjectLFront()+"me";
			gmeField.describe="玩家对象";
			gmeField.defaultValue=uMark+"GameC.player";
			
			uiCls.addField(gmeField);
			
			//不生成model
			
			//if(ProjectType.isLastProject(getProjectType()))
			//{
			//	String modelName="UIModel_"+name+"UILogic";
			//
			//	uiCls.addImport("game.view.uiGenerate.models."+modelName);
			//
			//	FieldInfo modelField=new FieldInfo();
			//	modelField.visitType=VisitType.Private;
			//	modelField.type=modelName;
			//	modelField.name="_model";
			//	modelField.describe="UI模型";
			//	modelField.defaultValue="";
			//
			//	uiCls.addField(modelField);
			//
			//	uiCls.addImport("shine.view.ui.element.UIModel");
			//
			//	MethodInfo method=new MethodInfo();
			//	method.visitType=VisitType.Protected;
			//	method.isOverride=true;
			//	method.name="createModel";
			//	method.returnType="UIModel";
			//
			//	CodeWriter writer=uiCls.createWriter();
			//	writer.writeCustom("return _model="+uiCls.getCode().createNewObject(modelName)+";");
			//	writer.writeEnd();
			//	method.content=writer.toString();
			//	uiCls.addMethod(method);
			//}
			
			
			MethodInfo method;
			
			//method=new MethodInfo();
			//method.visitType=VisitType.Protected;
			//method.isOverride=true;
			//method.name="registLogics";
			//method.makeEmptyMethodWithSuper(_codeType);
			//uiCls.addMethod(method);
			
			method=new MethodInfo();
			method.visitType=VisitType.Protected;
			method.isOverride=true;
			method.name="init";
			method.makeEmptyMethodWithSuper(_codeType);
			uiCls.addMethod(method);
			
			method=new MethodInfo();
			method.visitType=VisitType.Protected;
			method.isOverride=true;
			method.name="dispose";
			method.makeEmptyMethodWithSuper(_codeType);
			uiCls.addMethod(method);
			
			method=new MethodInfo();
			method.visitType=VisitType.Protected;
			method.isOverride=true;
			method.name="onEnter";
			method.makeEmptyMethodWithSuper(_codeType);
			uiCls.addMethod(method);
			
			method=new MethodInfo();
			method.visitType=VisitType.Protected;
			method.isOverride=true;
			method.name="onExit";
			method.makeEmptyMethodWithSuper(_codeType);
			uiCls.addMethod(method);
			
			if(ShineToolSetting.needUIResize)
			{
				method=new MethodInfo();
				method.visitType=VisitType.Protected;
				method.isOverride=true;
				method.name="onResize";
				method.makeEmptyMethodWithSuper(_codeType);
				uiCls.addMethod(method);
			}
			
			uiCls.write();
		}
		else
		{
			//一次性的代码
			
			////不是common
			//if(!_isCommon)
			//{
			//	uiCls.addImport("game.player.GPlayer");
			//
			//	FieldInfo gmeField=new FieldInfo();
			//	gmeField.visitType=VisitType.Public;
			//	gmeField.type="GPlayer";
			//	gmeField.name="gme";
			//	gmeField.describe="玩家对象";
			//	gmeField.defaultValue="GGameC.player";
			//
			//	uiCls.addField(gmeField);
			//
			//	uiCls.write();
			//}
		}
		
	}
	
	private void endMakeUIConfig()
	{
		toEndMakeConstConfig();
		
		if(ProjectType.isLastProject(getProjectType()))
		{
			_writer.writeEnd();
			
			MethodInfo method=new MethodInfo();
			method.visitType=VisitType.Public;
			//method.isVirtual=_isCommon;
			//method.isOverride=!_isCommon;
			method.isStatic=true;
			method.name="init";
			method.describe="初始化UI";
			method.content=_writer.toString();
			
			_uiCls.addMethod(method);
		}
		
		_uiCls.writeToPath(_uiClsPath);
	}
	
	/** 构造text表 */
	private void startMakeTextConfig()
	{
		toStartMakeConstConfig("generate","TextEnum","程序文本枚举",-1);
		
		_constCls.removeAllField();
		
		_writer=_constCls.createWriter();
		_constCls.addImport(ShineToolSetting.globalPackage+"commonBase.config.game.TextConfig");
	}
	
	private void makeOneTextConfig(Map<String,String> values)
	{
		String key=getFValue("key",values);
		String ucKey=StringUtils.ucWord(key);
		
		//没有key
		if(key.isEmpty())
		{
			return;
		}
		
		if(_hData.parentData==null || _hData.parentData.fieldTotalDic.get(key)==null)
		{
			String describe=getFValue("describe",values);
			
			CodeInfo code=_constCls.getCode();
			
			FieldInfo field=new FieldInfo();
			field.isStatic=true;
			field.visitType=VisitType.Public;
			field.type=code.String;
			field.name=ucKey;
			field.describe=describe;
			
			_constCls.addField(field);
			
			_writer.writeVarSet(_constCls.getFieldWrap(ucKey),"TextConfig.getText(\""+key+"\")");
		}
	}
	
	private void endMakeTextConfig()
	{
		_writer.writeEnd();
		
		MethodInfo method=new MainMethodInfo();
		method.name="readConfig";
		method.visitType=VisitType.Public;
		method.isStatic=true;
		method.describe="从配置读取";
		method.content=_writer.toString();
		
		_constCls.addMethod(method);
		
		_constCls.writeToPath(_constClsPath);
	}
}
