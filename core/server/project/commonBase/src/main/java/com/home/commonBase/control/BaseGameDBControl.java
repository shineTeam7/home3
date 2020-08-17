package com.home.commonBase.control;

import com.home.commonBase.data.social.RoleSocialData;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.table.table.GlobalTable;
import com.home.commonBase.table.table.PlayerTable;
import com.home.commonBase.table.table.RoleGroupTable;
import com.home.commonBase.table.table.RoleSocialTable;
import com.home.shine.ShineSetup;
import com.home.shine.bytes.BytesReadStream;
import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.constlist.TableStateType;
import com.home.shine.control.DBControl;
import com.home.shine.control.ThreadControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.data.BaseData;
import com.home.shine.data.DateData;
import com.home.shine.dataEx.TableBatchData;
import com.home.shine.global.ShineGlobal;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.collection.LongObjectMap;
import com.home.shine.support.collection.LongSet;
import com.home.shine.support.collection.SList;
import com.home.shine.support.func.ObjectCall;
import com.home.shine.table.BaseTable;
import com.home.shine.table.DBConnect;
import com.home.shine.utils.BytesUtils;
import com.home.shine.utils.FileUtils;
import com.home.shine.utils.ObjectUtils;
import com.mysql.cj.protocol.a.NativePacketPayload;

import java.sql.Connection;
import java.sql.PreparedStatement;

public abstract class BaseGameDBControl extends BaseDBControl
{
	protected int _appID;
	
	/** DB写线程用的临时写流 */
	protected BytesWriteStream _tempStream=BytesWriteStream.create();
	/** 写库序号 */
	protected int _writeIndex=0;
	
	private int _intervalIndex=-1;
	
	/** 开始构造写入时间 */
	private long _beginMakeWrite=-1;
	
	/** 开始写入时间 */
	private long _beginWrite=-1;
	
	/** 超时索引 */
	private int _timeOutIndex=-1;
	
	/** 写完毕回调 */
	private Runnable _writeOverFunc;
	
	/** 本次是否退出写入 */
	protected boolean _isExitWrite;
	
	//global
	/** 全局表 */
	protected GlobalTable _globalTable;
	
	protected boolean _needInsertGlobal=false;
	
	/** db本地文件路径 */
	protected String _dbFilePath="";
	
	/** 本次从文件读出 */
	protected boolean _useDBFile=false;
	
	//roleSocial
	/** 玩家社交数据改变字典 */
	protected LongObjectMap<RoleSocialTable> _roleSocialTables=new LongObjectMap<>(RoleSocialTable[]::new);
	/** 需要更新的玩家数据组 */
	protected LongSet _roleSocialUpdates=new LongSet();
	/** 玩家社交数据批量 */
	protected TableBatchData _roleSocialBatch=new TableBatchData();
	
	//temp
	/** 写流(db写线程用) */
	protected BytesWriteStream _writeStream=BytesWriteStream.create();
	/** 写流(主线程用) */
	protected BytesWriteStream _mainWriteStream=BytesWriteStream.create();
	
	protected int[] _tempResult=new int[4];
	
	public void initWithAppID(int value)
	{
		_appID=value;
		init();
	}
	
	@Override
	protected void toInit()
	{
		super.toInit();
		
		if(ShineSetup.isExiting())
			return;
		
		_globalTable=BaseC.factory.createGameGlobalTable();
		_globalTable.key=_appID;
		
		int nowDataVersion=getDataVersion();
		
		if(!_dbFilePath.isEmpty())
		{
			BytesReadStream stream=FileUtils.readFileForBytesReadStream(_dbFilePath);
			
			if(stream!=null)
			{
				if(stream.checkVersion(ShineGlobal.localDBFileVersion))
				{
					_useDBFile=true;
					toReadFile(stream);
				}
				//else
				//{
				//	//不符合版本的直接删除记录
				//	FileUtils.deleteFile(_dbFilePath);
				//}
			}
		}
		
		if(!_useDBFile)
		{
			//不符合版本的直接删除记录
			FileUtils.deleteFile(_dbFilePath);
			
			if(_globalTable.loadSync(_connect))
			{
				toLoadDB();
			}
			else
			{
				//新服
				_globalTable.dataVersion=nowDataVersion;
				_globalTable.data=BytesUtils.EmptyByteArr;
				_globalTable.setNeedInsert();
			}
		}
		
		//版本不匹配
		if(_globalTable.dataVersion!=nowDataVersion)
		{
			ShineSetup.exit("数据库结构版本不匹配,需要清库!,old:" + _globalTable.dataVersion + ",new:" + nowDataVersion);
			return;
		}
		
		initIntervalWrite();
	}
	
	protected void initIntervalWrite()
	{
		_intervalIndex=ThreadControl.getMainTimeDriver().setInterval(this::onWrite,CommonSetting.dbWriteDelay * 1000);
	}
	
	@Override
	protected void toDispose()
	{
		super.toDispose();
		
		if(_intervalIndex!=-1)
		{
			ThreadControl.getMainTimeDriver().clearInterval(_intervalIndex);
			_intervalIndex=-1;
		}
	}
	
	protected void toLoadDB()
	{
	
	}
	
	/** 间隔到了(主线程) */
	private void onWrite(int delay)
	{
		boolean lastFinish=true;
		
		if(_beginMakeWrite!=-1)
		{
			lastFinish=false;
			
			if(CommonSetting.openDBWriteTimeOutCheck)
			{
				Ctrl.errorLog("上次写库没做完");
			}
		}
		
		if(_beginWrite!=-1)
		{
			lastFinish=false;
			
			if(CommonSetting.openDBWriteTimeOutCheck)
			{
				Ctrl.errorLog("上次写库没做完2");
			}
		}
		
		writeOnce(lastFinish);
	}
	
	/** 写库一次(主线程) */
	private void writeOnce(boolean lastFinish)
	{
		_beginMakeWrite=Ctrl.getTimer();
		
		int index=++_writeIndex;
		
		if(CommonSetting.openDBWriteTimeOutCheck)
		{
			if(_timeOutIndex!=-1)
			{
				ThreadControl.getMainTimeDriver().clearTimeOut(_timeOutIndex);
			}
			
			_timeOutIndex=ThreadControl.getMainTimeDriver().setTimeOut(this::makeDBTimeOut,CommonSetting.dbWriteOnceMaxTime * 1000);
		}
		
		makeDBWrites(index,lastFinish);
	}
	
	/** 初始化后续 */
	public void initLast()
	{
		//如果上次是从文件读出的，直接写一次库
		if(_useDBFile)
		{
			writeOnce(false);
		}
	}
	
	/** 退出时写入 */
	public void writeForExit(Runnable overCall)
	{
		if(!_inited)
		{
			overCall.run();
			return;
		}
		
		_writeOverFunc=overCall;
		_isExitWrite=true;
		
		writeOnce(false);
	}
	
	/** db写完(主线程) */
	protected void makeDBWriteOver(int index,Object obj)
	{
		if(obj==null) {
			Ctrl.errorLog("写库空对象");
			return;
		}
		
		//过期
		if(index!=_writeIndex)
		{
			Ctrl.errorLog("写库过期");
			return;
		}
		
		if(_timeOutIndex!=-1)
		{
			ThreadControl.getMainTimeDriver().clearTimeOut(_timeOutIndex);
			_timeOutIndex=-1;
		}
		
		if(ShineSetting.needDetailLog)
		{
			long delay=Ctrl.getTimer() - _beginMakeWrite;
			
			Ctrl.runningLog(_clsName+" makeDBWrites耗时:" + delay + "ms");
		}
		
		_beginMakeWrite=-1;
		_beginWrite=Ctrl.getTimer();
		
		ThreadControl.addDBWriteFunc(()->
		{
			toWrite(index,obj);
		});
	}
	
	/** 构造db数据超时(主线程) */
	protected void makeDBTimeOut()
	{
		_timeOutIndex=-1;
		
		Ctrl.errorLog(_clsName+"写库超时");
		
		makeDBWriteOver(_writeIndex,null);
	}
	
	/** db写(db线程) */
	private void toWrite(int index,Object obj)
	{
		long begin=ShineSetting.needDetailLog ? Ctrl.getTimer() : 0;
		
		boolean re=writeDB(obj);
		
		if(ShineSetting.needDetailLog)
		{
			long delay=Ctrl.getTimer() - begin;
			
			Ctrl.runningLog(_clsName+" writeDB耗时:" + delay + "ms");
		}
		
		//成功
		if(re)
		{
			if(_useDBFile)
			{
				_useDBFile=false;
				FileUtils.deleteFile(_dbFilePath);
			}
		}
		else
		{
			writeFile(obj);
		}
		
		ThreadControl.addMainFunc(()->
		{
			preAfterWriteDB(index,obj,re);
		});
	}
	
	/** 构造DB写入对象(主线程) */
	protected void makeDBWrites(int index,boolean lastFinish)
	{
		makeDBWriteOver(index,null);
	}
	
	/** 写DB方法(DB写线程)(返回是否成功) */
	protected boolean writeDB(Object obj)
	{
		BaseDBWriteTempData tData=(BaseDBWriteTempData)obj;
		
		//取连接
		Connection con=_connect.getMainConnection();
		
		if(con==null)
		{
			Ctrl.errorLog("gameDB同步,con为空");
			return false;
		}
		
		tData.hasError=false;
		
		toWriteDB(tData,con);
		
		boolean hasError=tData.hasError;
		
		try
		{
			if(hasError)
			{
				con.rollback();
			}
			else
			{
				con.commit();
			}
		}
		catch(Exception e)
		{
			Ctrl.errorLog("DB commit/rollback执行出错",e);
		}
		
		_connect.reMainConnection(con);
		
		if(hasError)
		{
			Ctrl.errorLog("gameDB写入出错");
		}
		
		toWriteEndDB(tData);
		
		return !hasError;
	}
	
	/** 写DB后(主线程) */
	protected void afterWriteDB(BaseDBWriteTempData data)
	{
		SList<RoleSocialWriteTempData> fDic;
		if(!(fDic=data.roleSocials).isEmpty())
		{
			RoleSocialWriteTempData[] values=fDic.getValues();
			RoleSocialWriteTempData v;
			
			RoleSocialTable table;
			
			for(int i=0,len=fDic.size();i<len;++i)
			{
				v=values[i];
				
				table=v.table;
				
				if(table.tableState==TableStateType.NeedInsert)
					table.setNormal();
				
				if(table.needRemoveAfterWrite())
					removeRoleSocialTable(table);
			}
		}
	}
	
	/** 预备写DB后(主线程) */
	private void preAfterWriteDB(int index,Object obj,boolean success)
	{
		BaseDBWriteTempData tData=(BaseDBWriteTempData)obj;
		
		_beginWrite=-1;
		
		//过期
		if(index!=_writeIndex)
		{
			if(ShineSetting.isRelease)
				Ctrl.errorLog("写库过期3");
			
			return;
		}
		
		//成功才回调
		if(success)
		{
			afterWriteDB(tData);
		}
		
		//清空
		tData.clear();
		
		//视为退出标记
		if(_writeOverFunc!=null)
		{
			Runnable func=_writeOverFunc;
			_writeOverFunc=null;
			func.run();
		}
	}
	
	/** 全局表 */
	public GlobalTable getGlobalTable()
	{
		return _globalTable;
	}
	
	/** 是否是新的global数据 */
	public boolean isNewGlobal()
	{
		return _globalTable.tableState==TableStateType.NeedInsert;
	}
	
	/** 写global表 */
	protected boolean writeGlobalTable(BaseData data)
	{
		_tempStream.clear();
		data.writeBytesFull(_tempStream);
		//数据结构版本
		_globalTable.dataVersion=getDataVersion();
		_globalTable.data=_tempStream.getByteArray();
		_globalTable.saveDate=DateData.getNow();
		
		if(_globalTable.tableState==TableStateType.NeedInsert)
		{
			//成功后去掉标记
			if(_globalTable.insertSync(_connect))
			{
				_globalTable.setNormal();
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return _globalTable.updateSync(_connect);
		}
	}
	
	/** 写入到本地文件 */
	protected void writeFile(Object obj)
	{
		BytesWriteStream stream=_writeStream;
		
		stream.clear();
		stream.writeVersion(ShineGlobal.localDBFileVersion);
		
		toWriteFile(stream,obj);
		
		FileUtils.writeFileForBytesWriteStream(_dbFilePath,stream);
		stream.clear();
		_useDBFile=true;//标记
	}
	
	/** 写入到本地文件 */
	protected void toWriteFile(BytesWriteStream stream,Object obj)
	{
		BaseDBWriteTempData data=(BaseDBWriteTempData)obj;
		
		_globalTable.writeTableStream(stream);
		
		//players
		stream.writeLen(data.roleSocials.size());
		
		data.roleSocials.forEach(v->
		{
			v.table.writeTableStream(stream);
		});
	}
	
	/** 读取本地文件内容 */
	protected void toReadFile(BytesReadStream stream)
	{
		_globalTable.readTableStream(stream);
		
		int len=stream.readLen();
		
		_roleSocialTables.ensureCapacity(len);
		RoleSocialTable table;
		
		for(int i=0;i<len;i++)
		{
			table=BaseC.factory.createRoleSocialTable();
			table.readTableStream(stream);
			
			_roleSocialTables.put(table.playerID,table);
		}
	}
	
	protected void writeDBGlobal(BaseDBWriteTempData tData,Connection con)
	{
		_needInsertGlobal=false;
		
		boolean hasError=tData.hasError;
		//同时写所有部分
		BytesWriteStream tempStream=_writeStream;
		//global
		tempStream.clear();
		tData.globalListData.writeBytesFull(tempStream);
		
		//数据结构版本
		_globalTable.dataVersion=getDataVersion();
		_globalTable.data=tempStream.getByteArray();
		_globalTable.saveDate=DateData.getNow();
		
		long t=Ctrl.getTimer();
		
		PreparedStatement ps=null;
		
		int size=0;
		int realSize=0;
		
		try
		{
			NativePacketPayload pp=DBControl.getNativePacketPayload(con);
			
			if(_globalTable.tableState==TableStateType.NeedInsert)
			{
				_needInsertGlobal=true;
				
				ps=con.prepareStatement(_globalTable.getInsertStr());
				size+=_globalTable.insertToPs(ps).getWriteSize();
				
				_globalTable.setNormal();
			}
			else
			{
				ps=con.prepareStatement(_globalTable.getUpdateStr());
				
				size=_globalTable.updateToPs(ps,false).getWriteSize();
			}
			
			int re=ps.executeUpdate();
			
			if(pp!=null)
				realSize=pp.getPosition();
			
			if(re<0)
			{
				hasError=true;
			}
		}
		catch(Exception e)
		{
			hasError=true;
			Ctrl.errorLog(e);
		}
		
		if(!DBControl.closeStatement(ps))
			hasError=true;
		
		if(hasError)
			tData.hasError=true;
		
		if(ShineSetting.needDetailLog)
		{
			Ctrl.runningLog("writeGlobal countSize:"+size+" realSize:"+realSize);
		}
		
		_tempResult[0]=size;
		_tempResult[1]=(int)(Ctrl.getTimer()-t);
	}
	
	protected void writeEndGlobal(BaseDBWriteTempData tData)
	{
		if(tData.hasError)
		{
			if(_needInsertGlobal)
				_globalTable.setNeedInsert();
		}
	}
	
	/** 构造玩家部分的写入 */
	protected void makeDBWriteRoleSocials(BaseDBWriteTempData tempData,LongObjectMap<RoleSocialData> dic)
	{
		tempData.roleSocials.ensureCapacity(dic.size());
		
		_roleSocialUpdates.forEachAndClear(v->
		{
			RoleSocialTable table=_roleSocialTables.get(v);
			
			if(table==null)
			{
				Ctrl.warnLog("更新时找不到RoleSocialTable",v);
				return;
			}
			
			RoleSocialWriteTempData tData=new RoleSocialWriteTempData();
			tData.table=table;
			tData.data=dic.get(table.playerID);
			
			tempData.roleSocials.add(tData);
		});
	}
	
	protected void writeDBRoleSocials(BaseDBWriteTempData tData,Connection con)
	{
		boolean hasError=tData.hasError;
		
		BytesWriteStream tempStream=_writeStream;
		
		long t=Ctrl.getTimer();
		int roleSocialWriteNum=0;
		
		SList<RoleSocialWriteTempData> roleSocials=tData.roleSocials;
		
		//写roleGroup
		if(!roleSocials.isEmpty())
		{
			_roleSocialBatch.clear();
			_roleSocialBatch.setConnection(con);
			_roleSocialBatch.hasError=hasError;
			
			try
			{
				RoleSocialWriteTempData[] values=roleSocials.getValues();
				RoleSocialWriteTempData v;
				
				for(int i=roleSocials.size()-1;i>=0;--i)
				{
					if((v=values[i])!=null)
					{
						try
						{
							if(v.data!=null)
							{
								tempStream.clear();
								v.data.writeBytesFull(tempStream);
								v.table.data=tempStream.getByteArray();
							}
							
							_roleSocialBatch.addTable(v.table);
						}
						catch(Exception e)
						{
							Ctrl.errorLog(e);
						}
						
						roleSocialWriteNum++;
					}
				}
				
				_roleSocialBatch.executeBatch();
			}
			catch(Exception e)
			{
				_roleSocialBatch.hasError=true;
				Ctrl.errorLog(e);
			}
			
			_roleSocialBatch.closePs();
			if(_roleSocialBatch.hasError)
			{
				hasError=true;
			}
		}
		
		if(hasError)
			tData.hasError=true;
		
		if(ShineSetting.needDetailLog)
		{
			Ctrl.runningLog("game服 writeRoleSocial totalSize:"+_roleSocialBatch.getTotalWriteSize());
		}
		
		_tempResult[0]=_roleSocialBatch.getTotalWriteSize();
		_tempResult[1]=(int)(Ctrl.getTimer()-t);
		_tempResult[2]=roleSocialWriteNum;
	}
	
	protected void writeEndRoleRoleSocials(BaseDBWriteTempData tData)
	{
		_roleSocialBatch.end(tData.hasError);
		
		if(tData.hasError)
		{
			SList<RoleSocialWriteTempData> fDic;
			if(!(fDic=tData.roleSocials).isEmpty())
			{
				RoleSocialWriteTempData[] values=fDic.getValues();
				RoleSocialWriteTempData v;
				
				for(int i=0,len=fDic.size();i<len;++i)
				{
					v=values[i];
					//放回
					_roleSocialTables.put(v.table.playerID,v.table);
				}
			}
		}
	}
	
	protected void writeDBLog(int[] result,String name)
	{
		if(ShineSetting.needDetailLog)
		{
			Ctrl.runningLog("table:"+name+" dbSize:"+result[0]+" writeTime:"+result[1]);
		}
	}
	
	protected void toWriteDB(BaseDBWriteTempData tData,Connection con)
	{
		writeDBGlobal(tData,con);
		writeDBLog(_tempResult,"global");
		
		writeDBRoleSocials(tData,con);
		writeDBLog(_tempResult,"roleSocials");
	}
	
	protected void toWriteEndDB(BaseDBWriteTempData tData)
	{
		writeEndRoleRoleSocials(tData);
	}
	
	private RoleSocialTable getRoleSocialTableAbs(long playerID)
	{
		RoleSocialTable table=_roleSocialTables.get(playerID);
		
		if(table==null)
		{
			table=BaseC.factory.createRoleSocialTable();
			table.playerID=playerID;
			table.data=ObjectUtils.EmptyByteArr;
			table.setNormal();
			_roleSocialTables.put(playerID,table);
		}
		
		return table;
	}
	
	/** 添加新社交数据(主线程) */
	public void addNewRoleSocial(RoleSocialData data)
	{
		RoleSocialTable table=getRoleSocialTableAbs(data.showData.playerID);
		table.setNeedInsert();
		
		//本次可以不序列化
		_mainWriteStream.clear();
		data.writeBytesFull(_mainWriteStream);
		table.data=_mainWriteStream.getByteArray();
		
		_roleSocialUpdates.add(table.playerID);
	}
	
	/** 更新玩家社交数据 */
	public void refreshRoleSocial(long playerID)
	{
		RoleSocialTable table=getRoleSocialTable(playerID);
		
		if(table!=null)
		{
			table.setNeedRemoveAfterWrite(false);
			_roleSocialUpdates.add(playerID);
		}
	}
	
	/** 移除玩家社交数据 */
	public void deleteRoleSocial(long playerID)
	{
		getRoleSocialTableAbs(playerID).setNeedDelete();
		
		_roleSocialUpdates.add(playerID);
	}
	
	/** 更新玩家社交数据 */
	public void addRoleSocial(RoleSocialData data)
	{
		RoleSocialTable table=getRoleSocialTableAbs(data.showData.playerID);
		table.setNeedRemoveAfterWrite(false);
		
		_mainWriteStream.clear();
		data.writeBytesFull(_mainWriteStream);
		table.data=_mainWriteStream.getByteArray();
		
		_roleSocialUpdates.add(table.playerID);
	}
	
	/** 移除玩家社交数据 */
	public void removeRoleSocial(long playerID)
	{
		RoleSocialTable table=getRoleSocialTable(playerID);
		
		if(table!=null)
		{
			_roleSocialUpdates.add(table.playerID);
			table.setNeedRemoveAfterWrite(true);
		}
	}
	
	/** 加载所有玩家社交数据 */
	public LongObjectMap<RoleSocialData> loadRoleSocialTables()
	{
		RoleSocialTable table=BaseC.factory.createRoleSocialTable();
		SList<BaseTable> list=table.loadAllSync(_connect);
		
		LongObjectMap<RoleSocialData> re=new LongObjectMap<>(RoleSocialData[]::new);
		
		BytesReadStream stream=BytesReadStream.create();
		
		BaseTable[] values=list.getValues();
		RoleSocialTable v;
		RoleSocialTable v2;
		
		RoleSocialData sData;
		
		for(int i=0,len=list.size();i<len;++i)
		{
			v=(RoleSocialTable)values[i];
			
			//如现在有，就用,为了ReadFile
			if((v2=_roleSocialTables.get(v.playerID))!=null)
			{
				v=v2;
			}
			
			stream.setBuf(v.data);
			
			sData=BaseC.factory.createRoleSocialData();
			
			try
			{
				sData.readBytesFull(stream);
			}
			catch(Exception e)
			{
				Ctrl.errorLog(e);
			}
			
			re.put(v.playerID,sData);
		}
		
		return re;
	}
	
	private void addRoleSocialTable(RoleSocialTable table)
	{
		_roleSocialTables.put(table.playerID,table);
	}
	
	private void removeRoleSocialTable(RoleSocialTable table)
	{
		_roleSocialTables.remove(table.playerID);
	}
	
	public RoleSocialTable getRoleSocialTable(long playerID)
	{
		return _roleSocialTables.get(playerID);
	}
	
	/** 读取玩家群表 */
	public void loadRoleSocialTable(long playerID,ObjectCall<RoleSocialTable> func)
	{
		RoleSocialTable t=getRoleSocialTable(playerID);
		
		if(t!=null)
		{
			//已删除
			if(t.needRemove())
			{
				t=null;
			}
			else
			{
				t.setNeedRemoveAfterWrite(false);
			}
			
			func.apply(t);
			return;
		}
		
		RoleSocialTable table=BaseC.factory.createRoleSocialTable();
		table.playerID=playerID;
		table.load(_connect,v->
		{
			if(v)
			{
				addRoleSocialTable(table);
				func.apply(table);
			}
			else
			{
				func.apply(null);
			}
		});
	}
	
	/** db写入临时数据 */
	public class BaseDBWriteTempData
	{
		public int index;
		
		public boolean hasError=false;
		
		/** 全局列表数据 */
		public BaseData globalListData;
		
		//roleSocial
		/** 玩家社交数据 */
		public SList<RoleSocialWriteTempData> roleSocials=new SList<>(RoleSocialWriteTempData[]::new);
		
		public void clear()
		{
			hasError=false;
			roleSocials.clear();
		}
	}
	
	public class RoleSocialWriteTempData
	{
		public RoleSocialTable table;
		/** 数据 */
		public RoleSocialData data;
	}
}
