package com.home.shine.thread;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.concurrent.Sequence;
import com.home.shine.utils.MathUtils;
import com.home.shine.utils.UnsafeUtils;
import sun.misc.Unsafe;

import java.util.concurrent.locks.LockSupport;

//失败之作

/** 没有用_availble组标记,而是直接通过数组的值是否为空来判定,效率低 */
@SuppressWarnings("restriction")
public class SThread3 extends AbstractThread
{
	private static final Unsafe UNSAFE=UnsafeUtils.getUnsafe();

	private static final int _queueBase=UnsafeUtils.objectArrayBase;
	private static final int _queueScaleShift=UnsafeUtils.objectArrayShift;
	
	private Runnable[] _queue;
	
	private int _size;
	private int _sizeMark;
	
	/** 生产者序 */
	private Sequence _cursor=new Sequence();
	/** 消费序延迟缓存 */
	private Sequence _gatingSequenceCache=new Sequence();
	/** 消费者序 */
	private Sequence _consumerSequence=new Sequence();
	
	public SThread3(String name,int type,int index)
	{
		super(name,type,index);
		
		int queueSize=ShineSetting.sThreadQueueSize;
		
		if(!MathUtils.isPowerOf2(queueSize))
		{
			Ctrl.throwError("必须为2次幂");
			return;
		}
		
		_size=queueSize;
		_sizeMark=queueSize - 1;
		
		_queue=new Runnable[queueSize];
	}
	
	@Override
	public void run()
	{
		_running=true;
		
		long availableSequence;
		int index;
		long bufferAddress;
		
		//local
		Runnable[] queue=_queue;
		Sequence cursor=_cursor;
		int sizeMark=_sizeMark;
		Sequence consumerSequence=_consumerSequence;
		
		long nextSequence=consumerSequence.get() + 1L;
		
		Runnable run;
		
		
		long lastTime=Ctrl.getTimer();
		int delay;
		long time;
		
		while(_running)
		{
			++_runIndex;
			
			time=Ctrl.getTimer();
			delay=(int)(time - lastTime);
			lastTime=time;
			
			_passTime+=delay;
			++_roundNum;
			
			//先时间
			
			try
			{
				tick(delay);
			}
			catch(Exception e)
			{
				Ctrl.errorLog("线程tick出错",e);
			}
			
			//再事务
			
			availableSequence=cursor.get();
			
			if(availableSequence >= nextSequence)
			{
				while(nextSequence<=availableSequence)
				{
					index=(int)nextSequence & sizeMark;
					bufferAddress=(index << _queueScaleShift) + _queueBase;
					
					run=(Runnable)UNSAFE.getAndSetObject(queue,bufferAddress,null);
					
					if(run==null)
					{
						availableSequence=nextSequence - 1L;
						break;
					}
					else
					{
						try
						{
							run.run();
						}
						catch(Exception e)
						{
							Ctrl.errorLog(e);
						}
					}
					
					++nextSequence;
				}
				
				consumerSequence.set(availableSequence);
			}
			
			//最后睡
			threadSleep();
		}
		
		
	}
	
	@Override
	protected void toAddFunc(Runnable func,AbstractThread from)
	{
		long sequence=next();
		
		int index=((int)sequence) & _sizeMark;
		
		long bufferAddress=(index << _queueScaleShift) + _queueBase;
		UNSAFE.putOrderedObject(_queue,bufferAddress,func);
	}
	
	private long next()
	{
		//local
		Sequence cursor=_cursor;
		Sequence consumerSequence=_consumerSequence;
		Sequence gatingSequenceCache=_gatingSequenceCache;
		int size=_size;

		long current;
		long next;
		long wrapPoint;
		long cachedGatingSequence;
		long sequence;

		while(true)
		{
			current=cursor.get();
			next=current + 1L;

			wrapPoint=next - size;
			cachedGatingSequence=gatingSequenceCache.get();

			if(wrapPoint>cachedGatingSequence || cachedGatingSequence>current)
			{
				sequence=consumerSequence.get();

				if(current<sequence)
				{
					sequence=current;
				}

				if(wrapPoint>sequence)
				{
					//wait
					LockSupport.parkNanos(1L);
					continue;
				}

				gatingSequenceCache.set(sequence);
			}
			else if(cursor.compareAndSet(current,next))
			{
				break;
			}
		}

		return next;
	}
	
	@Override
	public void copy(AbstractThread thread)
	{
		super.copy(thread);
		
		SThread3 thd=(SThread3)thread;
		_queue=thd._queue;
		_size=thd._size;
		_sizeMark=thd._sizeMark;
		_cursor=thd._cursor;
		_gatingSequenceCache=thd._gatingSequenceCache;
		_consumerSequence=thd._consumerSequence;
	}
}
