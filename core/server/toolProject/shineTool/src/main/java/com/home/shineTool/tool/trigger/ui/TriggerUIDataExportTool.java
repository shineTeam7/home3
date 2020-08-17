package com.home.shineTool.tool.trigger.ui;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.data.trigger.TriggerBooleanData;
import com.home.shine.data.trigger.TriggerConfigData;
import com.home.shine.data.trigger.TriggerFloatData;
import com.home.shine.data.trigger.TriggerFuncData;
import com.home.shine.data.trigger.TriggerFuncListData;
import com.home.shine.data.trigger.TriggerIntData;
import com.home.shine.data.trigger.TriggerLongData;
import com.home.shine.data.trigger.TriggerObjData;
import com.home.shine.data.trigger.TriggerStringData;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.IntSet;
import com.home.shine.support.collection.SMap;
import com.home.shine.utils.FileUtils;
import com.home.shine.utils.StringUtils;
import com.home.shineTool.app.TriggerUIApp;
import com.home.shineTool.constlist.CodeType;
import com.home.shineTool.constlist.VisitType;
import com.home.shineTool.global.ShineProjectPath;
import com.home.shineTool.reflect.MethodInfo;
import com.home.shineTool.reflect.cls.ClassInfo;
import com.home.shineTool.reflect.code.CodeInfo;
import com.home.shineTool.reflect.code.CodeWriter;
import com.home.shineTool.tool.trigger.TriggerDataExportTool;
import com.home.shineTool.tool.trigger.TriggerMakeTool;

import javax.swing.*;
import java.io.File;

public class TriggerUIDataExportTool
{
	/** ui工具 */
	public TriggerUIApp uiApp;
	
	public TriggerMakeTool gameMakeTool;
	
	public TriggerDataExportTool dataTool;
	
	public TriggerMainUI mainUI;
	
	private String _rootPath;
	
	private CodeInfo _code;
	
	private IntObjectMap<MethodInfo> _methodDic;
	
	/** 组定义 */
	private IntObjectMap<String> _groupDic=new IntObjectMap<>();
	
	private IntSet _systemMethodSet;
	
	private SMap<String,File> _allFiles;
	
	
	public void init()
	{
		_rootPath=ShineProjectPath.dataDirPath + "/trigger/data";
		
		_code=CodeInfo.getCode(CodeType.Java);
		
		gameMakeTool=uiApp.export.gameTool;
		dataTool=uiApp.export.dataTool;
		
		_groupDic=new IntObjectMap<>();
		gameMakeTool.groupDefineDic.forEach((k,v)->
		{
			_groupDic.put(v,k);
		});
		
		_methodDic=mainUI.getMethodDic();
		
		_systemMethodSet=new IntSet();
		_systemMethodSet.add(1);//if
		_systemMethodSet.add(2);//while
	}
	
	public void exportAll()
	{
		_allFiles=new SMap<>();
		
		FileUtils.getDeepFileList(_rootPath,CodeType.getExName(CodeType.Java)).forEach(v->
		{
			_allFiles.put(FileUtils.fixPath(v.getPath()),v);
		});
		
		mainUI.getAllDic().forEach((groupType,v)->
		{
			v.forEach((groupID,v2)->
			{
				v2.forEachValue(trigger->
				{
					exportOneTrigger(groupType,groupID,trigger,true);
				});
			});
		});
		
		//delete
		_allFiles.forEachValueS(v->
		{
			Ctrl.print("删除文件",v.getPath());
			v.delete();
		});
		
		//保存配置
		uiApp.export.saveConfig();
		
		JOptionPane.showMessageDialog(mainUI.getPanel(),"保存成功");
	}
	
	public void exportOneTrigger(int groupType,int groupID,TriggerConfigData config,boolean needRemove)
	{
		String group=_groupDic.get(groupType)+"_"+groupID;
		
		config.name=StringUtils.ucWord(config.name);
		
		String path=_rootPath + "/" + group + "/" + config.name + "T." + CodeType.getExName(CodeType.Java);
		
		ClassInfo cls=ClassInfo.getClassInfoFromPathAbs(path);
		
		//init
		MethodInfo method=new MethodInfo();
		method.name="init";
		method.visitType=VisitType.Protected;
		method.isOverride=true;
		
		CodeWriter writer=cls.createWriter();
		writer.writeVarSet("isOpen",config.isOpen ? _code.True : _code.False);
		writer.writeVarSet("priority",String.valueOf(config.priority));
		
		writer.writeEnd();
		method.content=writer.toString();
		
		cls.addMethod(method);
		
		//three
		
		for(int i=0;i<3;i++)
		{
			method=new MethodInfo();
			
			TriggerFuncData[] funcList=null;
			
			switch(i)
			{
				case 0:
				{
					method.name="event";
					funcList=config.events;
				}
					break;
				case 1:
				{
					method.name="condition";
					funcList=config.conditions;
				}
					break;
				case 2:
				{
					method.name="action";
					funcList=config.actions;
				}
					break;
			}
			
			method.visitType=VisitType.Protected;
			method.isOverride=true;
			
			writer=cls.createWriter();
			
			if(funcList.length>0)
			{
				writeFuncList(funcList,writer);
				writer.writeEnter();
			}
			
			writer.writeEnd();
			method.content=writer.toString();
			
			cls.addMethod(method);
		}
		
		//写出
		cls.write();
		
		if(needRemove)
		{
			_allFiles.remove(path);
		}
	}
	
	/** 直接书写 */
	private void writeFuncListObj(TriggerFuncListData list,CodeWriter writer,boolean needLambda)
	{
		if(needLambda)
		{
			writer.getBuilder().append("()->");
			writer.writeEnter();
		}
		
		writer.writeLeftBrace();
		
		writeFuncList(list.funcList,writer);
		
		writer.writeEnter();
		
		writer.writeRightBraceWithOutEnter();
	}
	
	/** 直接书写 */
	private void writeFuncList(TriggerFuncData[] list,CodeWriter writer)
	{
		for(int i=0;i<list.length;i++)
		{
			if(i>0)
			{
				writer.writeEnter();
				
				writer.writeEmptyLine();
			}
			
			writer.writeOff();
			
			writeFunc(list[i],writer);
			
			if(!_systemMethodSet.contains(list[i].id))
			{
				writer.getBuilder().append(';');
			}
		}
	}
	
	private void writeObj(TriggerObjData obj,CodeWriter writer)
	{
		if(obj instanceof TriggerIntData)
		{
			writer.getBuilder().append(((TriggerIntData)obj).value);
		}
		else if(obj instanceof TriggerBooleanData)
		{
			writer.getBuilder().append(((TriggerBooleanData)obj).value ? _code.True : _code.False);
		}
		else if(obj instanceof TriggerFloatData)
		{
			writer.getBuilder().append(((TriggerFloatData)obj).value);
			writer.getBuilder().append('f');
		}
		else if(obj instanceof TriggerLongData)
		{
			writer.getBuilder().append(((TriggerLongData)obj).value);
		}
		else if(obj instanceof TriggerStringData)
		{
			StringBuilder sb=writer.getBuilder();
			sb.append('"');
			sb.append(((TriggerStringData)obj).value);
			sb.append('"');
		}
		else if(obj instanceof TriggerFuncData)
		{
			writeFunc((TriggerFuncData)obj,writer);
		}
		else if(obj instanceof TriggerFuncListData)
		{
			writeFuncListObj((TriggerFuncListData)obj,writer,true);
		}
	}
	
	private void writeFunc(TriggerFuncData func,CodeWriter writer)
	{
		StringBuilder sb=writer.getBuilder();
		
		MethodInfo methodInfo=_methodDic.get(func.id);
		sb.append(methodInfo.name);
		sb.append('(');
		
		switch(methodInfo.name)
		{
			case "if":
			{
				writeObj(func.args[0],writer);
				sb.append(')');
				writer.writeEnter();
				writeFuncListObj((TriggerFuncListData)func.args[1],writer,false);
				
				if(func.args.length>2)
				{
					writer.writeEnter();
					writer.writeCustom("else");
					writeFuncListObj((TriggerFuncListData)func.args[2],writer,false);
				}
			}
				break;
			case "while":
			{
				writeObj(func.args[0],writer);
				sb.append(')');
				writer.writeEnter();
				writeFuncListObj((TriggerFuncListData)func.args[1],writer,false);
			}
				break;
			case "openTrigger":
			case "closeTrigger":
			case "runTrigger":
			case "runTriggerAbs":
			{
				TriggerIntData iData=(TriggerIntData)func.args[0];
				TriggerConfigData tData=mainUI.getTriggerData(iData.value);
				sb.append(tData.name);
				sb.append("T.class");
				sb.append(')');
			}
				break;
			default:
			{
				for(int i=0;i<func.args.length;i++)
				{
					if(i>0)
					{
						sb.append(',');
					}
					
					writeObj(func.args[i],writer);
				}
				
				sb.append(')');
			}
				break;
		}
		
	}
}
