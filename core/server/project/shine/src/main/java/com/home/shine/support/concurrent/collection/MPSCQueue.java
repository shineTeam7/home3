package com.home.shine.support.concurrent.collection;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.concurrent.Sequence;
import com.home.shine.utils.MathUtils;
import com.home.shine.utils.UnsafeUtils;
import sun.misc.Unsafe;

import java.util.concurrent.locks.LockSupport;

public class MPSCQueue extends BaseConcurrentQueue
{
	private static final Unsafe UNSAFE=UnsafeUtils.getUnsafe();
	
	private static final int _availbleBase=UnsafeUtils.intArrayBase;
	private static final int _availbleScaleShift=UnsafeUtils.intArrayShift;
	
	private static final int _queueBase=UnsafeUtils.objectArrayBase;
	private static final int _queueScaleShift=UnsafeUtils.objectArrayShift;
	
	private final Runnable[] _queue;
	
	private int _size;
	private int _sizeMark;
	private int _sizeShift;
	
	/** 多生产者就绪标记 */
	private final int[] _availableBuffer;
	/** 生产者序 */
	private Sequence _cursor=new Sequence();
	/** 消费序延迟缓存 */
	private Sequence _gatingSequenceCache=new Sequence();
	/** 消费者序 */
	private Sequence _consumerSequence=new Sequence();
	
	public MPSCQueue()
	{
		this(ShineSetting.sThreadQueueSize);
	}
	
	public MPSCQueue(int queueSize)
	{
		if(!MathUtils.isPowerOf2(queueSize))
		{
			Ctrl.throwError("必须为2次幂");
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
		long availableSequence;
		long sequence;
		int index;
		int flag;
		long bufferAddress;
		
		//local
		Runnable[] queue=_queue;
		int[] availableBuffer=_availableBuffer;
		Sequence cursor=_cursor;
		int sizeMark=_sizeMark;
		int sizeShift=_sizeShift;
		Sequence consumerSequence=_consumerSequence;
		
		long nextSequence=consumerSequence.get() + 1L;
		
		Runnable run;
		
		if((availableSequence=cursor.get()) >= nextSequence)
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
			
			int num=0;
			
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
				++num;
			}
			
			_executeNum=num;
			
			consumerSequence.set(availableSequence);
		}
	}
	
	/** 添加执行方法 */
	@Override
	public void addFunc(Runnable func)
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
		
		int index=((int)next) & _sizeMark;
		int flag=(int)(next >>> _sizeShift);
		
		long bufferAddress=(index << _queueScaleShift) + _queueBase;
		UNSAFE.putOrderedObject(_queue,bufferAddress,func);
		
		bufferAddress=(index << _availbleScaleShift) + _availbleBase;
		UNSAFE.putOrderedInt(_availableBuffer,bufferAddress,flag);
	}
	
	@Override
	public void clear()
	{
		Runnable[] queue=_queue;
		
		for(int i=queue.length-1;i>=0;--i)
		{
		    queue[i]=null;
		}
		
		int[] availableBuffer=_availableBuffer;
		
		for(int i=availableBuffer.length-1;i>=0;--i)
		{
		    availableBuffer[i]=-1;
		}
		
		_cursor.set(0L);
		_gatingSequenceCache.set(0L);
		_consumerSequence.set(0L);
	}
}
