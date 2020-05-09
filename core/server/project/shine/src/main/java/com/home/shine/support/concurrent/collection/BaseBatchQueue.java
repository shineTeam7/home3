package com.home.shine.support.concurrent.collection;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.concurrent.Sequence;
import com.home.shine.utils.MathUtils;
import com.home.shine.utils.UnsafeUtils;
import sun.misc.Unsafe;

import java.util.concurrent.locks.LockSupport;

/** 多生产单消费batch队列 */
@SuppressWarnings("restriction")
public abstract class BaseBatchQueue<V>
{
	private static final Unsafe UNSAFE=UnsafeUtils.getUnsafe();
	private static final int _availbleBase=UnsafeUtils.intArrayBase;
	private static final int _availbleScaleShift=UnsafeUtils.intArrayShift;
	
	private final Node<V>[] _queue;
	
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
	
	@SuppressWarnings("unchecked")
	public BaseBatchQueue(int queueSize)
	{
		if(!MathUtils.isPowerOf2(queueSize))
		{
			Ctrl.throwError("必须为2次幂");
		}
		
		_size=queueSize;
		_sizeMark=queueSize - 1;
		_sizeShift=MathUtils.log2(queueSize);
		
		_queue=new Node[queueSize];
		
		for(int i=0;i<queueSize;++i)
		{
			_queue[i]=new Node<V>();
		}
		
		_availableBuffer=new int[queueSize];
		
		long bufferAddress;
		
		for(int i=queueSize - 1;i >= 0;--i)
		{
			bufferAddress=(i << _availbleScaleShift) + _availbleBase;
			UNSAFE.putOrderedInt(_availableBuffer,bufferAddress,-1);
		}
	}
	
	/** 执行一次 */
	public void runOnce()
	{
		long availableSequence;
		long sequence;
		int index;
		int flag;
		long bufferAddress;
		
		//local
		Node<V>[] queue=_queue;
		int[] availableBuffer=_availableBuffer;
		Sequence cursor=_cursor;
		int sizeMark=_sizeMark;
		int sizeShift=_sizeShift;
		Sequence consumerSequence=_consumerSequence;
		
		long nextSequence=consumerSequence.get() + 1L;
		
		Node<V> node;
		V obj;
		
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
			
			while(nextSequence<=availableSequence)
			{
				node=queue[(int)nextSequence & sizeMark];
				obj=node.obj;
				node.obj=null;
				
				try
				{
					acceptOne(obj);
				}
				catch(Exception e)
				{
					Ctrl.errorLog(e);
				}
				
				++nextSequence;
			}
			
			consumerSequence.set(availableSequence);
		}
	}
	
	protected abstract void acceptOne(V obj);
	
	/** 添加元素 */
	public void add(V obj)
	{
		if(obj==null)
		{
			return;
		}
		
		long sequence=next();
		
		int index=((int)sequence) & _sizeMark;
		int flag=(int)(sequence >>> _sizeShift);
		
		_queue[index].obj=obj;
		
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
	
	/** 节点 */
	//	@SuppressWarnings("unused")
	private static class Node<V>
	{
		//		public long p0,p1,p2,p3,p4,p5,p6;
		
		public V obj;
		
		//		public long p8,p9,p10,p11,p12,p13,p14;
		
		public Node()
		{
			
		}
	}
}
