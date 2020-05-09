package com.home.shineTool.tool.table;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.SSet;
import com.home.shine.table.DBConnect;
import com.home.shine.utils.FileUtils;
import com.home.shineTool.reflect.FieldInfo;
import com.home.shineTool.reflect.cls.ClassInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 表sql工具 */
public class TableSqlTool
{
	/** 回车 */
	public static final String Enter=ClassInfo.Enter;
	
	private String _defineClsPath;
	
	private String _path;
	
	private String _truncatePath;
	
	private String _content;
	
	private String _truncateContent;
	
	private DBConnect _connect;
	
	private String _dbName;
	
	private boolean _madedDB=false;
	
	private TableExportTool _exportTool;
	
	//是否执行过
	private static SSet<String> _didPaths=new SSet<>();
	
	public TableSqlTool(String defineClsPath,String path,String truncatePath,String url,boolean isClear)
	{
		_defineClsPath=defineClsPath;
		
		_path=path;
		
		_content=(isClear && !_didPaths.contains(path)) ? "" : FileUtils.readFileForUTF(_path);
		
		_didPaths.add(path);
		
		_truncatePath=truncatePath;
		_truncateContent=FileUtils.readFileForUTF(_truncatePath);
		
		int index=url.indexOf(",");
		
		String aa=url.substring(0,index);
		
		int wIndex;
		
		if((wIndex=aa.indexOf("?"))!=-1)
		{
			aa=aa.substring(0,wIndex);
		}
		
		int lastIndex=aa.lastIndexOf("/");
		
		String front=aa.substring(0,lastIndex);
		
		_dbName=aa.substring(lastIndex + 1,aa.length()).toLowerCase();//全小写
		
		_connect=new DBConnect(front + "/mysql" + url.substring(index,url.length()));
	}
	
	public void setExportTool(TableExportTool exportTool)
	{
		_exportTool=exportTool;
	}
	
	private void makeDB()
	{
		if(_madedDB)
		{
			return;
		}
		
		_madedDB=true;
		
		//create
		
		String dbSql="create database if not exists `" + _dbName + "` default charset= utf8;";
		//建库
		_connect.getExecutor().executeQuerySync(dbSql);
		
		String useDBSql="use `" + _dbName + "`;";
		
		_connect.getExecutor().executeQuerySync(useDBSql);
		
		Pattern reg=Pattern.compile("create database if not exists ." + _dbName + ".(.*?);",Pattern.DOTALL);
		
		Matcher m=reg.matcher(_content);
		
		if(m.find())
		{
			_content=m.replaceFirst(dbSql);
			_content=_content.replaceFirst("use ." + _dbName + ".;",useDBSql);
		}
		else
		{
			_content=dbSql + Enter + useDBSql + Enter + _content;
		}
	}
	
	/** 添加表 */
	public void addTable(String tableName,String sql)
	{
		tableName=tableName.toLowerCase();//全小写

		makeDB();

		Pattern reg=Pattern.compile("create table if not exists ." + tableName + ".(.*?);",Pattern.DOTALL);

		Matcher m=reg.matcher(_content);

		if(m.find())
		{
			_content=m.replaceFirst(sql);
		}
		else
		{
			_content+=Enter + Enter;
			_content+=sql;
		}

		String truncateSql="truncate `" + tableName + "`;";

		if(_truncateContent.indexOf(truncateSql)==-1)
		{
			_truncateContent+=Enter + Enter;
			_truncateContent+=truncateSql;
		}

		//execute

		String clean="drop table if exists `" + tableName + "`;";
		
		_connect.getExecutor().executeQuerySync(clean);

		boolean re=_connect.getExecutor().executeQuerySync(sql);

		Ctrl.print("sql:" + tableName + "执行" + (re ? "成功" : "失败"));
	}

	public void execute()
	{
		ClassInfo cls=ClassInfo.getClassInfoFromPath(_defineClsPath);

		if(cls==null)
			return;

		for(String v:cls.getFieldNameList())
		{
			FieldInfo field=cls.getField(v);
			
			String tableName=field.name.toLowerCase();
			
			String sql=_exportTool.getSql(tableName);
			
			if(sql==null)
			{
				Ctrl.throwError("找不到名为:"+tableName+"的表");
			}
			
			//添加table
			addTable(tableName,sql);
		}
	}

	/** 写出 */
	public void write()
	{
		FileUtils.writeFileForUTF(_path,_content);
		FileUtils.writeFileForUTF(_truncatePath,_truncateContent);
		
		//close
		_connect.close();
	}
}
