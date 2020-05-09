package com.home.shine.thread;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.concurrent.Sequence;
import com.home.shine.utils.MathUtils;
import com.home.shine.utils.UnsafeUtils;
import sun.misc.Unsafe;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.LockSupport;

/** 自定义Thread2(基于disruptor)(没用Node,直接数组存放,用unsafe的putO和getV) */
@SuppressWarnings("restriction")
public class SThread2 extends AbstractThread
{
	private static final Unsafe UNSAFE=UnsafeUtils.getUnsafe();

	private static final int _availbleBase=UnsafeUtils.intArrayBase;
	private static final int _availbleScaleShift=UnsafeUtils.intArrayShift;

	private static final int _queueBase=UnsafeUtils.objectArrayBase;
	private static final int _queueScaleShift=UnsafeUtils.objectArrayShift;
	
	private Runnable[] _queue;
	
	/** 达到上限的补充 */
	private ConcurrentLinkedQueue<Runnable> _cQueue=new ConcurrentLinkedQueue<>();
	/** 是否有C组内容 */
	private volatile boolean _hasCQueue=false;
	
	private int _size;
	private int _sizeMark;
	private int _sizeShift;
	
	/** 多生产者就绪标记 */
	private int[] _availableBuffer;
	/** 生产者序 */
	private Sequence _cursor=new Sequence();
	/** 消费序延迟缓存 */
	private Sequence _gatingSequenceCache=new Sequence();
	/** 消费者序 */
	private Sequence _consumerSequence=new Sequence();
	
	public SThread2(String name,int type,int index)
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
		_sizeShift=MathUtils.log2(queueSize);
		
		_queue=new Runnable[queueSize];
		
		_availableBuffer=new int[queueSize];
		
		long bufferAddress;
		
		for(int i=queueSize - 1;i >= 0;--i)
		{
			bufferAddress=(i << _availbleScaleShift) + _availbleBase;
			UNSAFE.putOrderedInt(_availableBuffer,bufferAddress,-1);
		}
	}
	
	@Override
	public void run()
	{
		_running=true;
		
		long availableSequence;
		long sequence;
		int index;
		int flag;
		long bufferAddress;
		
		//local
		Runnable[] queue=_queue;
		ConcurrentLinkedQueue<Runnable> cQueue=_cQueue;
		int[] availableBuffer=_availableBuffer;
		Sequence cursor=_cursor;
		int sizeMark=_sizeMark;
		int sizeShift=_sizeShift;
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
				for(sequence=nextSequence;sequence<=availableSequence;++sequence)
				{
					index=((int)sequence) & sizeMark;
					flag=(int)(sequence >>> sizeShift);
					bufferAddress=(index << _availbleScaleShift) + _availbleBase;
					
					//不是
					if(UNSAFE.getIntVolatile(availableBuffer,bufferAddress)!=flag)
					{
						availableSequence=sequence - 1L;
						break;
					}
				}
				
				while(_running && nextSequence<=availableSequence)
				{
					index=(int)nextSequence & sizeMark;
					bufferAddress=(index << _queueScaleShift) + _queueBase;
					
					run=(Runnable)UNSAFE.getObjectVolatile(queue,bufferAddress);
					UNSAFE.putOrderedObject(queue,bufferAddress,null);
					
					try
					{
						run.run();
					}
					catch(Exception e)
					{
						Ctrl.errorLog(e);
					}
					
					++nextSequence;
				}
				
				consumerSequence.set(availableSequence);
			}
			
			if(_hasCQueue)
			{
				_hasCQueue=false;

				while(_running)
				{
					if((run=cQueue.poll())==null)
					{
						break;
					}

					try
					{
						run.run();
					}
					catch(Exception e)
					{
						Ctrl.errorLog(e);
					}
				}
			}
			
			//最后睡
			threadSleep();
		}
	}
	
	@Override
	protected void toAddFunc(Runnable func,AbstractThread from)
	{
		//local
		Sequence cursor=_cursor;
		Sequence consumerSequence=_consumerSequence;
		Sequence gatingSequenceCache=_gatingSequenceCache;
		int size=_size;
		int waitCount=ShineSetting.sThreadWaitCount;
		int waitRound=ShineSetting.sThreadWaitRound;
		
		long current;
		long next;
		long wrapPoint;
		long cachedGatingSequence;
		long sequence;
		
		int count=0;
		int round=0;
		SThread2 selfThread=null;
		
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
					
					if((++count)==waitCount)
					{
						count=0;
						
						if(selfThread==null)
						{
							Thread thd;
							
							if((thd=Thread.currentThread()) instanceof SThread2)
							{
								if((selfThread=(SThread2)thd).isFull())
								{
									addToCQueue(func);
									return;
								}
							}
						}
						else
						{
							if(selfThread.isFull())
							{
								addToCQueue(func);
								return;
							}
						}

						if((++round)==waitRound)
						{
							addToCQueue(func);
							return;
						}
					}
					
					continue;
				}
				
				gatingSequenceCache.set(sequence);
			}
			else if(cursor.compareAndSet(current,next))
			{
				break;
			}
		}
		
		int index=((int)next) & _sizeMark;
		int flag=(int)(next >>> _sizeShift);
		
		long bufferAddress=(index << _queueScaleShift) + _queueBase;
		UNSAFE.putOrderedObject(_queue,bufferAddress,func);
		
		bufferAddress=(index << _availbleScaleShift) + _availbleBase;
		UNSAFE.putOrderedInt(_availableBuffer,bufferAddress,flag);
	}
	
	private void addToCQueue(Runnable func)
	{
		//加到C组
		_cQueue.offer(func);
		_hasCQueue=true;
	}
	
	/** 当前是否满了 */
	private boolean isFull()
	{
		long current=_cursor.get();
		long wrapPoint=current + 1L - _size;
		long cachedGatingSequence=_gatingSequenceCache.get();
		long sequence;
		
		if(wrapPoint>cachedGatingSequence || cachedGatingSequence>current)
		{
			sequence=_consumerSequence.get();
			
			if(current<sequence)
			{
				sequence=current;
			}
			
			if(wrapPoint>sequence)
			{
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void copy(AbstractThread thread)
	{
		super.copy(thread);
		
		SThread2 thd=(SThread2)thread;
		_queue=thd._queue;
		_cQueue=thd._cQueue;
		_hasCQueue=thd._hasCQueue;
		_size=thd._size;
		_sizeMark=thd._sizeMark;
		_sizeShift=thd._sizeShift;
		_availableBuffer=thd._availableBuffer;
		_cursor=thd._cursor;
		_gatingSequenceCache=thd._gatingSequenceCache;
		_consumerSequence=thd._consumerSequence;
	}
}

