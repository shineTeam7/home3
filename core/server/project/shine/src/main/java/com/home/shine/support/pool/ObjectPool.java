package com.home.shine.support.pool;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.collection.SList;
import com.home.shine.support.collection.SQueue;
import com.home.shine.support.collection.SSet;
import com.home.shine.support.func.ObjectCall;
import com.home.shine.support.func.ObjectFunc;

/** 对象池(线程非安全) */
public class ObjectPool<T> implements IPool<T>
{
	/** 是否开启 */
	private boolean _enable=true;
	/** 入池是否需要执行clear */
	private boolean _needClear=true;
	/** 列表 */
	private SQueue<T> _queue;
	/** 最大尺寸 */
	private int _maxSize;
	
	private SSet<T> _checkSet;
	
	/** 创建对象函数 */
	private ObjectFunc<T> _createFunc;
	/** 析构函数 */
	private ObjectCall<T> _releaseFunc;
	
	public ObjectPool(ObjectFunc createFunc)
	{
		this(createFunc,ShineSetting.defaultPoolSize);
	}
	
	@SuppressWarnings("unchecked")
	public ObjectPool(ObjectFunc createFunc,int size)
	{
		_maxSize=size;
		
		_queue=new SQueue<>();
		
		_createFunc=createFunc;
		
		if(ShineSetting.openCheck)
		{
			_checkSet=new SSet<>();
		}
		
		if(!ShineSetting.useObjectPool)
		{
			_enable=false;
		}
	}
	
	public void setEnable(boolean value)
	{
		if(!ShineSetting.useObjectPool)
		{
			_enable=false;
		}
		else
		{
			_enable=value;
		}
	}
	
	public void setNeedClear(boolean value)
	{
		_needClear=value;
	}
	
	/** 设置析构函数 */
	public void setReleaseFunc(ObjectCall<T> func)
	{
		_releaseFunc=func;
	}
	
	/** 清空 */
	public void clear()
	{
		if(_queue.isEmpty())
			return;
		
		_queue.forEachAndClear(v->
		{
			if(_releaseFunc!=null)
			{
				_releaseFunc.apply(v);
			}
		});
		
		if(ShineSetting.openCheck)
		{
			_checkSet.clear();
		}
	}
	
	/** 取出一个 */
	public T getOne()
	{
		//有
		if(!_queue.isEmpty())
		{
			T obj=_queue.pop();
			
			if(ShineSetting.openCheck)
			{
				_checkSet.remove(obj);
			}
			
			return obj;
		}
		else
		{
			return _createFunc.apply();
		}
	}
	
	/** 放回一个 */
	public void back(T obj)
	{
		if(obj==null)
		{
			Ctrl.errorLog("对象池添加空对象");
			return;
		}
		
		if(!_enable || _queue.size() >= _maxSize)
		{
			if(_releaseFunc!=null)
			{
				_releaseFunc.apply(obj);
			}
			
			return;
		}
		
		if(_needClear)
		{
			if(obj instanceof IPoolObject)
			{
				((IPoolObject)obj).clear();
			}
		}
		
		if(ShineSetting.openCheck)
		{
			if(_checkSet.contains(obj))
			{
				Ctrl.throwError("对象池重复添加!",obj);
				return;
			}
			
			_checkSet.add(obj);
		}
		
		_queue.offer(obj);
	}
}
