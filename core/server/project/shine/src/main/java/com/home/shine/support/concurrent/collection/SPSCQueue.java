package com.home.shine.support.concurrent.collection;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.utils.UnsafeUtils;
import sun.misc.Unsafe;

import java.util.concurrent.locks.LockSupport;

/** 单生产单消费队列 */
public class SPSCQueue extends BaseSPSCQueue
{
	private static final Unsafe unsafe=UnsafeUtils.getUnsafe();
	
	private static final long _cursor_offset;
	private static final long _consumerSequence_offset;
	
	static
	{
		long temp=0L;
		long temp2=0L;
		
		try
		{
			temp=unsafe.objectFieldOffset(SPSCQueue.class.getDeclaredField("_cursor"));
			temp2=unsafe.objectFieldOffset(SPSCQueue.class.getDeclaredField("_consumerSequence"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		_cursor_offset=temp;
		_consumerSequence_offset=temp2;
	}
	
	private final Runnable[] _queue;
	
	/** 生产者序 */
	private int _cursor=0;
	/** 消费者序 */
	private int _consumerSequence=0;
	
	private int _size;
	private int _sizeMark;
	
	public SPSCQueue()
	{
		this(ShineSetting.sThreadQueueSize);
	}
	
	public SPSCQueue(int queueSize)
	{
		_size=queueSize;
		_sizeMark=queueSize - 1;
		
		_queue=new Runnable[queueSize];
	}
	
	/** 运行一次 */
	@Override
	public void run()
	{
		int availableSequence;
		int nextSequence;
		
		if((availableSequence=unsafe.getIntVolatile(this,_cursor_offset)) >= (nextSequence=_consumerSequence + 1))
		{
			int sequence;
			Runnable[] queue=_queue;
			int sizeMark=_sizeMark;
			
			Runnable run;
			int temp;
			
			for(sequence=nextSequence;sequence<=availableSequence;++sequence)
			{
				run=queue[temp=(sequence & sizeMark)];
				queue[temp]=null;
				
				try
				{
					run.run();
				}
				catch(Exception e)
				{
					Ctrl.errorLog(e);
				}
			}
			
			unsafe.putOrderedInt(this,_consumerSequence_offset,availableSequence);
		}
	}
	
	@Override
	public void addFunc(Runnable func)
	{
		super.addFunc(func);
		
		int next=_cursor+1;
		int wrapPoint=next - _size;
		
		while(wrapPoint>(unsafe.getIntVolatile(this,_consumerSequence_offset)))
		{
			LockSupport.parkNanos(1L);
			//Thread.yield();
		}
		
		_queue[next & _sizeMark]=func;
		
		unsafe.putOrderedInt(this,_cursor_offset,next);
	}
	
	
}
