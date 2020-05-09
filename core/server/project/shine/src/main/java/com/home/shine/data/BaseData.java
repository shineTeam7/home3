package com.home.shine.data;

import com.home.shine.bytes.BytesReadStream;
import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.control.BytesControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.DataWriter;
import com.home.shine.support.pool.DataPool;
import com.home.shine.support.pool.PoolObject;

/** 基础数据 */
public class BaseData extends PoolObject implements Cloneable
{
	/** 数据类型ID */
	protected int _dataID=-1;
	
	/** 是否是空读出来的(BytesStream为0继续读的) */
	private boolean _isEmptyRead=false;
	
	/** 创建线程序号 */
	public byte createThreadInstance=-1;
	/** 回收链表尾 */
	public BaseData releaseLinkTail;
	
	public BaseData()
	{
		
	}
	
	public int getDataID()
	{
		return _dataID;
	}
	
	/** 是否相同类型 */
	public boolean isSameType(BaseData data)
	{
		return _dataID==data._dataID;
	}
	
	/** 是否空读出 */
	public boolean isEmptyRead()
	{
		return _isEmptyRead;
	}
	
	/** 读取字节流(完整版,为DB) */
	public final void readBytesFull(BytesReadStream stream)
	{
		//空读标记
		_isEmptyRead=stream.isEmpty();
		
		stream.startReadObj();
		
		try
		{
			toReadBytesFull(stream);
		}
		catch(Exception e)
		{
			Ctrl.errorLog(e);
		}
		
		stream.endReadObj();
		
		try
		{
			afterRead();
		}
		catch(Exception e)
		{
			Ctrl.errorLog(e);
		}
	}
	
	/** 写入字节流(完整版,为DB) */
	public final void writeBytesFull(BytesWriteStream stream)
	{
		try
		{
			beforeWrite();
		}
		catch(Exception e)
		{
			Ctrl.errorLog(e);
		}
		
		stream.startWriteObj();
		
		try
		{
			toWriteBytesFull(stream);
		}
		catch(Exception e)
		{
			Ctrl.errorLog(e);
		}
		
		stream.endWriteObj();
	}
	
	/** 读取字节流(不加长度标记)(完整版,为DB) */
	public final void readBytesFullWithoutLen(BytesReadStream stream)
	{
		try
		{
			toReadBytesFull(stream);
		}
		catch(Exception e)
		{
			Ctrl.errorLog(e);
		}
		
		try
		{
			afterRead();
		}
		catch(Exception e)
		{
			Ctrl.errorLog(e);
		}
	}
	
	/** 写入字节流不加长度标记)(完整版,为DB) */
	public final void writeBytesFullWithoutLen(BytesWriteStream stream)
	{
		try
		{
			beforeWrite();
		}
		catch(Exception e)
		{
			Ctrl.errorLog(e);
		}
		
		try
		{
			toWriteBytesFull(stream);
		}
		catch(Exception e)
		{
			Ctrl.errorLog(e);
		}
	}
	
	/** 读取字节流(简版,为通信) */
	public final void readBytesSimple(BytesReadStream stream)
	{
		toReadBytesSimple(stream);
		
		afterRead();
	}
	
	/** 写入字节流(简版,为通信) */
	public final void writeBytesSimple(BytesWriteStream stream)
	{
		beforeWrite();
		
		toWriteBytesSimple(stream);
	}
	
	/** 实际读取字节流(完整版,为DB) */
	protected void toReadBytesFull(BytesReadStream stream)
	{
		
	}
	
	/** 实际写入字节流(完整版,为DB) */
	protected void toWriteBytesFull(BytesWriteStream stream)
	{
		
	}
	
	/** 实际读取字节流(简版,为通信) */
	protected void toReadBytesSimple(BytesReadStream stream)
	{
		
	}
	
	/** 实际写入字节流(简版,为通信) */
	protected void toWriteBytesSimple(BytesWriteStream stream)
	{
		
	}
	
	/** 复制(深拷)(从目标往自己身上赋值) */
	public final void copy(BaseData data)
	{
		try
		{
			data.beforeWrite();
		}
		catch(Exception e)
		{
			Ctrl.errorLog(e);
		}
		
		try
		{
			toCopy(data);
		}
		catch(Exception e)
		{
			Ctrl.errorLog(e);
		}
		
		try
		{
			afterRead();
		}
		catch(Exception e)
		{
			Ctrl.errorLog(e);
		}
	}
	
	/** 克隆数据 */
	public BaseData clone()
	{
		BaseData re=BytesControl.getDataByID(_dataID);
		
		re.copy(this);
		
		return re;
	}
	
	/** 复制 */
	protected void toCopy(BaseData data)
	{
		
	}
	
	/** 复制(潜拷)(从目标往自己身上赋值) */
	public final void shadowCopy(BaseData data)
	{
		try
		{
			data.beforeWrite();
		}
		catch(Exception e)
		{
			Ctrl.errorLog(e);
		}
		
		try
		{
			toShadowCopy(data);
		}
		catch(Exception e)
		{
			Ctrl.errorLog(e);
		}
		
		try
		{
			afterRead();
		}
		catch(Exception e)
		{
			Ctrl.errorLog(e);
		}
	}
	
	/** 复制 */
	protected void toShadowCopy(BaseData data)
	{
		
	}
	
	/** 是否数据一致 */
	public final boolean dataEquals(BaseData data)
	{
		if(data==null)
			return false;
		
		if(data._dataID!=_dataID)
			return false;
		
		try
		{
			return toDataEquals(data);
		}
		catch(Exception e)
		{
			Ctrl.errorLog(e);
		}
		
		return false;
	}
	
	/** 是否数据一致 */
	protected boolean toDataEquals(BaseData data)
	{
		return true;
	}
	
	public final String toDataString()
	{
		DataWriter writer=new DataWriter();
		
		writeDataString(writer);
		
		return writer.releaseStr();
	}
	
	/** 获取数据类名 */
	public String getDataClassName()
	{
		return "";
	}
	
	public final void writeDataString(DataWriter writer)
	{
		writer.sb.append(getDataClassName());
		writer.writeEnter();
		
		writer.writeLeftBrace();
		
		try
		{
			beforeWrite();
		}
		catch(Exception e)
		{
			Ctrl.errorLog(e);
		}
		
		try
		{
			toWriteDataString(writer);
		}
		catch(Exception e)
		{
			Ctrl.errorLog(e);
		}
		
		writer.writeRightBrace();
	}
	
	protected void toWriteDataString(DataWriter writer)
	{
	
	}
	
	/** 初始化初值 */
	public void initDefault()
	{
	
	}
	
	/** 空对象错误 */
	public void nullObjError(String fieldName)
	{
		if(!ShineSetting.isDBUpdate)
		{
			Ctrl.throwError("数据写入时为空,dataName:"+getDataClassName()+"fieldName:"+fieldName);
		}
	}
	
	//接口
	
	/** 写前 */
	protected void beforeWrite()
	{
		
	}
	
	/** 读后 */
	protected void afterRead()
	{
		
	}
	
	/** 清空 */
	@Override
	public void clear()
	{
	
	}
	
	
	/** 初始化列表数据(ListData用) */
	public void initListData()
	{
	
	}
	
	/** 释放回池 */
	public final void release(DataPool pool)
	{
		//toClearNext();
		toRelease(pool);
	}
	
	protected void toRelease(DataPool pool)
	{
	
	}
}
