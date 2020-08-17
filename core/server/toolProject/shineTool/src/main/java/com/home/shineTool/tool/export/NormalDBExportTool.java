package com.home.shineTool.tool.export;

import com.home.shine.serverConfig.ServerConfig;
import com.home.shine.support.collection.SSet;
import com.home.shineTool.constlist.CodeType;
import com.home.shineTool.constlist.DataGroupType;
import com.home.shineTool.constlist.MakeMethodType;
import com.home.shineTool.global.ShineProjectPath;
import com.home.shineTool.global.ShineToolGlobal;
import com.home.shineTool.global.ShineToolSetting;
import com.home.shineTool.tool.FileRecordTool;
import com.home.shineTool.tool.base.BaseExportTool;
import com.home.shineTool.tool.base.RecordDataClassInfo;
import com.home.shineTool.tool.data.DataDefineTool;
import com.home.shineTool.tool.data.DataOutputInfo;
import com.home.shineTool.tool.table.TableExportTool;
import com.home.shineTool.tool.table.TableSqlTool;

/** 通用db导出工具 */
public class NormalDBExportTool
{
	/** 文件记录 */
	protected FileRecordTool _record;
	
	protected int _serverCodeType=CodeType.Java;
	protected String _serverExName;
	
	/** 是否公共 */
	protected boolean _isCommon=false;
	/** 前缀 */
	protected String _front="";
	
	protected TableExportTool _exportTool;
	
	/** common层工具 */
	private NormalDBExportTool _commonTool;
	
	private String _md5;
	
	public void setIsCommon(boolean bool)
	{
		_isCommon=bool;
		_front=_isCommon ? "" : "G";
	}
	
	/** 标准初始化 */
	public void initNormal(String recordName)
	{
		_record=new FileRecordTool(ShineToolGlobal.recordPath + "/" + recordName + ".xml");
		_record.setVersion(ShineToolSetting.dataExportVersion);
		
		_serverExName=CodeType.getExName(_serverCodeType);
		
		initNext();
	}
	
	public void setCommonTool(NormalDBExportTool tool)
	{
		_commonTool=tool;
	}
	
	protected void initNext()
	{
	
	}
	
	public void execute()
	{
		_record.read();
		
		String codeEx=CodeType.getExName(CodeType.Java);
		
		String pathH=ShineProjectPath.getDataPath(_isCommon);
		String baseH=ShineProjectPath.getServerBasePath(_isCommon);
		
		String centerURL=ServerConfig.getCenterConfig().mysql;
		
		int idStart=_isCommon ? 1 : 50;
		
		//shine包
		
		doExport(pathH + "/table/table",baseH + "/table",baseH + "/constlist/generate/" + _front + "BaseTableType.java",idStart,50);
		
		TableSqlTool centerSql=new TableSqlTool(pathH + "/table/list/"+_front+"CenterTLO."+codeEx,centerURL,_record.isNew());
		centerSql.setExportTool(_exportTool);
		centerSql.execute();
		centerSql.write();
		
		SSet<String> gameMysqlSet=new SSet<>();
		
		ServerConfig.getGameConfigDic().forEachValue(v->
		{
			if(!v.isAssist)
			{
				String gameURL=v.mysql;
				
				//之前没有
				if(gameMysqlSet.add(gameURL))
				{
					TableSqlTool gameSql=new TableSqlTool(pathH + "/table/list/"+_front+"GameTLO."+codeEx,gameURL,_record.isNew());
					gameSql.setExportTool(_exportTool);
					gameSql.execute();
					gameSql.write();
				}
			}
		});
		
		_record.write();
		
		////md5部分
		//for(String v:exportTool.getAllInputQNameSet().getSortedList())
		//{
		//	RecordDataClassInfo recordCls=BaseExportTool.newRecordClsDic.get(v);
		//
		//	recordCls.writeString(_md5Builder);
		//}
	}
	
	/** 执行一个 */
	public void doExport(String inputPath,String outPath,String definePath,int startNum,int len)
	{
		DataDefineTool tableDefine=new DataDefineTool(definePath,startNum,len,true);
		
		TableExportTool export=new TableExportTool();
		_exportTool=export;
		export.setFileRecordTool(_record);
		export.setIsCommon(_isCommon);
		
		if(!_isCommon)
		{
			export.setCommonTool(_commonTool._exportTool);
		}
		
		export.addDefine(DataGroupType.Server,tableDefine);
		export.setInput(inputPath,"TO",null,CodeType.Java);
		
		DataOutputInfo out0=new DataOutputInfo();
		out0.path=outPath + "/table";
		out0.codeType=CodeType.Java;
		out0.nameTail="Table";
		out0.defineIndex=DataGroupType.Server;
		//out0.defineVarName="_tableID";
		out0.staticDefineVarName="";
		
		out0.needs[MakeMethodType.writeFull]=true;
		out0.needs[MakeMethodType.readFull]=true;
		
		out0.superQName=ShineToolSetting.tableQName;
		export.addOutput(out0);
		
		DataOutputInfo out1=new DataOutputInfo();
		out1.path=outPath + "/task";
		out1.codeType=CodeType.Java;
		out1.nameTail="DBTask";
		out1.superQName=ShineToolSetting.taskQName;
		export.addOutput(out1);
		
		DataOutputInfo out2=new DataOutputInfo();
		out2.path=outPath + "/result";
		out2.codeType=CodeType.Java;
		out2.nameTail="DBResult";
		out2.superQName=ShineToolSetting.resultQName;
		export.addOutput(out2);
		
		export.execute();
		
		_md5=export.getMd5();
		
		tableDefine.write();
	}
	
	public String getMd5()
	{
		return _md5;
	}
}
