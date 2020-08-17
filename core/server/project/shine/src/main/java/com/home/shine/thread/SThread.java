package com.home.shine.thread;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.concurrent.Sequence;
import com.home.shine.utils.MathUtils;
import com.home.shine.utils.UnsafeUtils;
import sun.misc.Unsafe;

import java.util.concurrent.locks.LockSupport;

/** 自定义Thread(基于disruptor)(节点用Node,赋值Runnable) */
@SuppressWarnings("restriction")
public class SThread extends AbstractThread
{
	private static final Unsafe UNSAFE=UnsafeUtils.getUnsafe();
	private static final int _availbleBase=UnsafeUtils.intArrayBase;
	private static final int _availbleScaleShift=UnsafeUtils.intArrayShift;
	
	private Node[] _queue;
	
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
	
	public SThread(String name,int type,int index)
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
		
		_queue=new Node[queueSize];
		
		for(int i=0;i<queueSize;++i)
		{
			_queue[i]=new Node();
		}
		
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
		Node[] queue=_queue;
		int[] availableBuffer=_availableBuffer;
		Sequence cursor=_cursor;
		int sizeMark=_sizeMark;
		int sizeShift=_sizeShift;
		Sequence consumerSequence=_consumerSequence;
		
		long nextSequence=consumerSequence.get() + 1L;
		
		Node node;
		Runnable run;
		
		
		long lastTime=Ctrl.getTimer();
		long time;
		int delay;
		int tickMax=_tickDelay;
		int tickTime=0;
		
		while(_running)
		{
			if(_pause)
			{
				if(_resume)
				{
					_resume=false;
					_pause=false;
					
					Runnable resumeFunc=_resumeFunc;
					_resumeFunc=null;
					if(resumeFunc!=null)
					{
						try
						{
							resumeFunc.run();
						}
						catch(Exception e)
						{
							Ctrl.errorLog(e);
						}
					}
				}
			}
			else
			{
				++_runIndex;
				
				time=Ctrl.getTimer();
				delay=(int)(time - lastTime);
				lastTime=time;
				
				//防止系统时间改小
				if(delay<0)
					delay=0;
				
				if(delay>0)
				{
					if((tickTime+=delay)>=tickMax)
					{
						try
						{
							tick(tickTime);
						}
						catch(Exception e)
						{
							Ctrl.errorLog("线程tick出错",e);
						}
						
						tickTime=0;
					}
					
					_passTime+=delay;
				}
				
				try
				{
					runEx();
				}
				catch(Exception e)
				{
					Ctrl.errorLog("线程runEx出错",e);
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
						node=queue[(int)nextSequence & sizeMark];
						run=node.run;
						node.run=null;
						
						try
						{
							run.run();
						}
						catch(Exception e)
						{
							Ctrl.errorLog(e);
						}
						
						++nextSequence;
						
						++_funcNum;
					}
					
					consumerSequence.set(availableSequence);
				}
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
		int flag=(int)(sequence >>> _sizeShift);
		
		_queue[index].run=func;
		
		long bufferAddress=(index << _availbleScaleShift) + _availbleBase;
		UNSAFE.putOrderedInt(_availableBuffer,bufferAddress,flag);
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
					//Thread.yield();
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
		
		SThread thd=(SThread)thread;
		_queue=thd._queue;
		_size=thd._size;
		_sizeMark=thd._sizeMark;
		_sizeShift=thd._sizeShift;
		_availableBuffer=thd._availableBuffer;
		_cursor=thd._cursor;
		_gatingSequenceCache=thd._gatingSequenceCache;
		_consumerSequence=thd._consumerSequence;
	}
	
	/** 节点 */
	//	@SuppressWarnings("unused")
	private static class Node
	{
		public long p0,p1,p2,p3,p4,p5,p6;
		
		public Runnable run;
		
		public long p8,p9,p10,p11,p12,p13,p14;
		
		public Node()
		{
			
		}
	}
}
