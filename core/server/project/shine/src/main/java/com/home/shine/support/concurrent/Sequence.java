package com.home.shine.support.concurrent;

import com.home.shine.utils.UnsafeUtils;

import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class Sequence
{
	static final long INITIAL_VALUE=-1L;
	private static final Unsafe UNSAFE;
	private static final long VALUE_OFFSET;

	static
	{
		UNSAFE=UnsafeUtils.getUnsafe();
		final int base=UNSAFE.arrayBaseOffset(long[].class);
		final int scale=UNSAFE.arrayIndexScale(long[].class);
		VALUE_OFFSET=base + (scale * 7);
	}

	private final long[] paddedValue=new long[15];

	/**
	 * Create a sequence initialised to -1.
	 */
	public Sequence()
	{
		this(INITIAL_VALUE);
	}

	/**
	 * Create a sequence with a specified initial value.
	 *
	 * @param initialValue The initial value for this sequence.
	 */
	public Sequence(final long initialValue)
	{
		UNSAFE.putOrderedLong(paddedValue,VALUE_OFFSET,initialValue);
	}

	/**
	 * Perform a volatile read of this sequence's value.
	 *
	 * @return The current value of the sequence.
	 */
	public long get()
	{
		return UNSAFE.getLongVolatile(paddedValue,VALUE_OFFSET);
	}

	/**
	 * Perform an ordered write of this sequence.  The intent is
	 * a Store/Store barrier between this write and any previous
	 * store.
	 *
	 * @param value The new value for the sequence.
	 */
	public void set(final long value)
	{
		UNSAFE.putOrderedLong(paddedValue,VALUE_OFFSET,value);
	}

	/**
	 * Performs a volatile write of this sequence.  The intent is
	 * a Store/Store barrier between this write and any previous
	 * write and a Store/Load barrier between this write and any
	 * subsequent volatile read.
	 *
	 * @param value The new value for the sequence.
	 */
	public void setVolatile(final long value)
	{
		UNSAFE.putLongVolatile(paddedValue,VALUE_OFFSET,value);
	}

	/**
	 * Perform a compare and set operation on the sequence.
	 *
	 * @param expectedValue The expected current value.
	 * @param newValue      The value to update to.
	 * @return true if the operation succeeds, false otherwise.
	 */
	public boolean compareAndSet(final long expectedValue,final long newValue)
	{
		return UNSAFE.compareAndSwapLong(paddedValue,VALUE_OFFSET,expectedValue,newValue);
	}

	/**
	 * Atomically increment the sequence by one.
	 *
	 * @return The value after the increment
	 */
	public long incrementAndGet()
	{
		return addAndGet(1L);
	}

	/**
	 * Atomically add the supplied value.
	 *
	 * @param increment The value to add to the sequence.
	 * @return The value after the increment.
	 */
	public long addAndGet(final long increment)
	{
		long currentValue;
		long newValue;

		do
		{
			currentValue=get();
			newValue=currentValue + increment;
		}
		while(!compareAndSet(currentValue,newValue));

		return newValue;
	}

	public String toString()
	{
		return Long.toString(get());
	}
}
