package com.home.shine.table;

import com.home.shine.control.DBControl;
import com.home.shine.control.ThreadControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/** DB连接池(一个库一连接) */
//关注点 数据库连接池
public class DBConnect
{
	private DBTaskExecutor _executor;
	
	private String _url="";
	
	/** 连接池 */
	private Connection[] _cons;
	
	private Connection _mainConnection;
	
	private Properties _mysqlProp;
	
	private BasicDataSource _dataSource;
	
	//temp
	private int _temp;
	
	private boolean[] _checkMarks;

	public DBConnect(String mysql)
	{
		if(mysql==null)
		{
			Ctrl.throwError("mysql链接为空");
		}
		
		Ctrl.log("创建数据库连接",mysql);
		
		String[] arr=mysql.split(",");
		
		_url=arr[0];
		
		String user="";
		String password="";
		
		if(arr.length>1)
		{
			user=arr[1];
		}
		
		if(arr.length>2)
		{
			password=arr[2];
		}

		//prop
		_mysqlProp=new Properties();
		_mysqlProp.setProperty("user",user);
		_mysqlProp.setProperty("password",password);
		//_mysqlProp.setProperty("maxAllowedPacket","65535");
		_mysqlProp.setProperty("useUnicode","true");
		_mysqlProp.setProperty("characterEncoding","utf-8");
		_mysqlProp.setProperty("rewriteBatchedStatements","true");
		_mysqlProp.setProperty("zeroDateTimeBehavior","round");
		_mysqlProp.setProperty("useSSL","false");
		_mysqlProp.setProperty("allowMultiQueries","true");
		_mysqlProp.setProperty("serverTimezone","UTC");
		_mysqlProp.setProperty("useServerPrepStmts","true");

		if(ShineSetting.useDBCP)
		{
			_checkMarks=new boolean[ShineSetting.dbThreadNum+1];

			_mysqlProp.setProperty("serverTimezone","GMT%2B8");

			Properties dbcp2Prop=new Properties();
			dbcp2Prop.setProperty("username",user);
			dbcp2Prop.setProperty("password",password);
			dbcp2Prop.setProperty("maxTotal",String.valueOf(-1));
			dbcp2Prop.setProperty("maxIdle",String.valueOf(50));
			dbcp2Prop.setProperty("minIdle",String.valueOf((ShineSetting.dbThreadNum + 1)*2));
			dbcp2Prop.setProperty("initialSize",String.valueOf((ShineSetting.dbThreadNum + 1)*2));
			dbcp2Prop.setProperty("timeBetweenEvictionRunsMillis","1800000");
			dbcp2Prop.setProperty("minEvictableIdleTimeMillis","3600000");
			dbcp2Prop.setProperty("maxWaitMillis","20000");
			dbcp2Prop.setProperty("testOnBorrow","true");
			dbcp2Prop.setProperty("testOnReturn","true");
			dbcp2Prop.setProperty("testWhileIdle","true");
		    dbcp2Prop.setProperty("removeAbandonedOnBorrow","false");
			dbcp2Prop.setProperty("validationQuery","select 1");

			StringBuilder sb=new StringBuilder(_url);
			_temp=0;

			_mysqlProp.forEach((k, v) -> {

				sb.append((++_temp)==1 ? '?' : '&');
				sb.append(k);
				sb.append('=');
				sb.append(v);
			});

			dbcp2Prop.setProperty("url",_url=sb.toString());

			try
			{
				_dataSource=BasicDataSourceFactory.createDataSource(dbcp2Prop);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				Ctrl.throwError("初始化DBConnect连接失败:" + _url);
			}
			
			Connection con=getMainConnection();
			
			if(con==null)
			{
				Ctrl.throwError("初始化DBConnect连接失败:" + _url);
				return;
			}
			else
			{
				reMainConnection(con);
			}
		}
		else
		{
			//多一条给唯一连接
			_cons=new Connection[ShineSetting.dbThreadNum];
			
			for(int i=0;i<ShineSetting.dbThreadNum;++i)
			{
				if((_cons[i]=createCon())==null)
				{
					Ctrl.throwError("初始化DBConnect连接失败:" + _url);
					return;
				}
			}
			
			if((_mainConnection=createCon())==null)
			{
				Ctrl.throwError("初始化DBConnect连接失败:" + _url);
				return;
			}
		}
		
		_executor=new DBTaskExecutor();
		_executor.init(this);
		
		DBControl.addExecutor(_executor);
	}
	
	/** 获取executor */
	public DBTaskExecutor getExecutor()
	{
		return _executor;
	}
	
	/** 获取指定编号的连接(失败返回null) */
	public Connection getConnection(int index)
	{
		if(ShineSetting.useDBCP)
		{
			if(ShineSetting.openCheck)
			{
				if(_checkMarks[index])
				{
					Ctrl.errorLog("连接获取超出限制",index);
					return null;
				}
				else
				{
					_checkMarks[index]=true;
				}
			}
			
			Connection con;
			try
			{
				con=_dataSource.getConnection();
				con.setAutoCommit(false);
				
				return con;
			}
			catch(Exception e)
			{
				if(ShineSetting.openCheck)
				{
					_checkMarks[index]=false;
				}
				
				Ctrl.errorLog(e);
				return null;
			}
		}
		else
		{
			Connection con=_cons[index];
			
			if(con!=null)
			{
				try
				{
					if(con.isClosed())
					{
						con=_cons[index]=createCon();
						Ctrl.warnLog("一条db池连接失效(已关闭)",index);
					}
				}
				catch(Exception e)
				{
					Ctrl.errorLog(e);
				}
			}
			
			return con;
		}
	}
	
	/** 获取主连接(失败返回null) */
	public Connection getMainConnection()
	{
		if(ShineSetting.useDBCP)
		{
			return getConnection(ShineSetting.dbThreadNum);
		}
		else
		{
			Connection con=_mainConnection;
			
			if(con!=null)
			{
				try
				{
					if(con.isClosed())
					{
						con=_mainConnection=createCon();
						
						Ctrl.warnLog("db主连接失效(已关闭)");
					}
				}
				catch(Exception e)
				{
					Ctrl.errorLog(e);
				}
			}
			
			return con;
		}
	}
	
	public void reConnection(Connection con,int index)
	{
		if(ShineSetting.useDBCP)
		{
			try
			{
				con.close();
			}
			catch(Exception e)
			{
				Ctrl.errorLog(e);
			}
			
			if(ShineSetting.openCheck)
			{
				if(!_checkMarks[index])
				{
					Ctrl.errorLog("连接回收超出限制",index);
					return;
				}
				else
				{
					_checkMarks[index]=false;
				}
			}
		}
	}
	
	public void reMainConnection(Connection con)
	{
		if(ShineSetting.useDBCP)
		{
			reConnection(con,ShineSetting.dbThreadNum);
		}
	}
	
	private Connection createCon()
	{
		Connection con=null;
		try
		{
			con=DriverManager.getConnection(_url,_mysqlProp);
			con.setAutoCommit(false);
		}
		catch(Exception e)
		{
			Ctrl.errorLog("数据库连接创建失败:" + _url,e);
		}
		
		return con;
	}
	
	/** 关闭 */
	public void close()
	{
		if(ShineSetting.useDBCP)
		{
			try
			{
				_dataSource.close();
			}
			catch(Exception e)
			{
				Ctrl.errorLog(e);
			}
		}
		else
		{
			for(Connection con : _cons)
			{
				if(con!=null)
				{
					try
					{
						con.close();
					}
					catch(Exception e)
					{
						Ctrl.errorLog(e);
					}
				}
			}
		}
	}
	
	/** 保持连接 */
	public void keep()
	{
		if(ShineSetting.useDBCP)
			return;

		ThreadControl.addDBWriteFunc(()->
		{
			keepOne(_mainConnection);
		});
		
		for(int i=0;i<ShineSetting.dbThreadNum;++i)
		{
			int index=i;
			
			ThreadControl.addDBPoolFunc(index,()->
			{
				keepOne(_cons[index]);
			});
		}
	}
	
	/** 保持连接一个 */
	private static void keepOne(Connection con)
	{
		executeQuerySync(con,"show tables;");
	}
	
	/** 执行query(同步) */
	private static void executeQuerySync(Connection con,String sql)
	{
		if(con==null)
		{
			Ctrl.warnLog("executeQuerySync时,con为null");
			return;
		}
		
		try
		{
			con.createStatement().execute(sql);
		}
		catch(Exception e)
		{
			Ctrl.errorLog(e);
		}
	}
}
