using System;

namespace ShineEngine
{
	/// <summary>
	/// 基础数据
	/// </summary>
	public class BaseData:PoolObject
	{
		/** 数据类型ID */
		protected int _dataID=-1;

		/** 是否是空读出来的(BytesStream为0继续读的) */
		private bool _isEmptyRead=false;

		/** 回收链表尾 */
		public BaseData releaseLinkTail;

		public BaseData()
		{

		}

		/// <summary>
		/// 获取数据ID
		/// </summary>
		public int getDataID()
		{
			return _dataID;
		}

		/** 是否相同类型 */
		public bool isSameType(BaseData data)
		{
			return _dataID==data._dataID;
		}

		/** 是否空读出 */
		public bool isEmptyRead()
		{
			return _isEmptyRead;
		}

		/// <summary>
		/// 读取字节流(完整版,为DB)
		/// </summary>
		public void readBytesFull(BytesReadStream stream)
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

		/// <summary>
		/// 写入字节流(完整版,为DB)
		/// </summary>
		public void writeBytesFull(BytesWriteStream stream)
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

		/// <summary>
		/// 读取字节流(不加长度标记)(完整版,为DB)
		/// </summary>
		public void readBytesFullWithoutLen(BytesReadStream stream)
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

		/// <summary>
		/// 写入字节流不加长度标记)(完整版,为DB)
		/// </summary>
		public void writeBytesFullWithoutLen(BytesWriteStream stream)
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

		/// <summary>
		/// 读取字节流(简版,为通信)
		/// </summary>
		public void readBytesSimple(BytesReadStream stream)
		{
			toReadBytesSimple(stream);

			afterRead();
		}

		/// <summary>
		/// 写入字节流(简版,为通信)
		/// </summary>
		public void writeBytesSimple(BytesWriteStream stream)
		{
			beforeWrite();

			toWriteBytesSimple(stream);
		}

		/// <summary>
		/// 实际读取字节流(完整版,为DB)
		/// </summary>
		protected virtual void toReadBytesFull(BytesReadStream stream)
		{
		}

		/// <summary>
		/// 实际写入字节流(完整版,为DB)
		/// </summary>
		protected virtual void toWriteBytesFull(BytesWriteStream stream)
		{
		}

		/// <summary>
		/// 实际读取字节流(简版,为通信)
		/// </summary>
		protected virtual void toReadBytesSimple(BytesReadStream stream)
		{
		}

		/// <summary>
		/// 实际写入字节流(简版,为通信)
		/// </summary>
		protected virtual void toWriteBytesSimple(BytesWriteStream stream)
		{
		}

		/// <summary>
		/// 复制(深拷)(从目标往自己身上赋值)
		/// </summary>
		public void copy(BaseData data)
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

		/// <summary>
		/// 克隆数据
		/// </summary>
		public BaseData clone()
		{
			BaseData re=BytesControl.getDataByID(_dataID);

			re.copy(this);

			return re;
		}

		/// <summary>
		/// 复制
		/// </summary>
		protected virtual void toCopy(BaseData data)
		{
		}

		/// <summary>
		/// 复制(潜拷)(从目标往自己身上赋值)
		/// </summary>
		public void shadowCopy(BaseData data)
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
		protected virtual void toShadowCopy(BaseData data)
		{

		}

		/** 是否数据一致 */
		public bool dataEquals(BaseData data)
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
		protected virtual bool toDataEquals(BaseData data)
		{
			return true;
		}

		public string toDataString()
		{
			DataWriter writer=new DataWriter();

			writeDataString(writer);

			return writer.releaseStr();
		}

		/** 获取数据类名 */
		public virtual string getDataClassName()
		{
			return "";
		}

		public void writeDataString(DataWriter writer)
		{
			writer.sb.Append(getDataClassName());
			writer.writeEnter();

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

		protected virtual void toWriteDataString(DataWriter writer)
		{

		}

		/** 初始化初值 */
		public virtual void initDefault()
		{

		}

		/** 空对象错误 */
		public void nullObjError(string fieldName)
		{
			Ctrl.throwError("数据写入时为空,dataName:"+getDataClassName()+"fieldName:"+fieldName);
		}

		//接口

		/** 写前 */
		protected virtual void beforeWrite()
		{

		}

		/** 读后 */
		protected virtual void afterRead()
		{

		}

		/** 初始化列表数据(ListData用) */
		public virtual void initListData()
		{

		}

		public override void clear()
		{

		}

		/** 回池 */
		public void release(DataPool pool)
		{
			toRelease(pool);
			pool.releaseData(this);
		}

		protected virtual void toRelease(DataPool pool)
		{

		}
	}
}