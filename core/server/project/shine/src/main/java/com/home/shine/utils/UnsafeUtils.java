package com.home.shine.utils;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;

import com.home.shine.ctrl.Ctrl;

import sun.misc.Unsafe;

/** unsafe相关 */
@SuppressWarnings("restriction")
public class UnsafeUtils
{
	private static final Unsafe _unsafe;
	
	public static final int intArrayBase;
	public static final int intArrayShift;
	
	public static final int longArrayBase;
	public static final int longArrayShift;
	
	public static final int objectArrayBase;
	public static final int objectArrayShift;
	
	static
	{
		try
		{
			final PrivilegedExceptionAction<Unsafe> action=new PrivilegedExceptionAction<Unsafe>()
			{
				public Unsafe run() throws Exception
				{
					Field theUnsafe=Unsafe.class.getDeclaredField("theUnsafe");
					theUnsafe.setAccessible(true);
					return (Unsafe)theUnsafe.get(null);
				}
			};

			_unsafe=AccessController.doPrivileged(action);
		}
		catch(Exception e)
		{
			throw new RuntimeException("Unable to load unsafe",e);
		}

		intArrayBase=_unsafe.arrayBaseOffset(int[].class);

		int scale=_unsafe.arrayIndexScale(int[].class);
		
		if(scale==4)
		{
			intArrayShift=2;
		}
		else if(scale==8)
		{
			intArrayShift=3;
		}
		else
		{
			Ctrl.errorLog("int[] scale出错");
			intArrayShift=0;
		}
		
		longArrayBase=_unsafe.arrayBaseOffset(long[].class);
		
		scale=_unsafe.arrayIndexScale(long[].class);
		
		if(scale==4)
		{
			longArrayShift=2;
		}
		else if(scale==8)
		{
			longArrayShift=3;
		}
		else
		{
			Ctrl.errorLog("long[] scale出错");
			longArrayShift=0;
		}
		
		objectArrayBase=_unsafe.arrayBaseOffset(Object[].class);

		scale=_unsafe.arrayIndexScale(Object[].class);
		
		if(scale==4)
		{
			objectArrayShift=2;
		}
		else if(scale==8)
		{
			objectArrayShift=3;
		}
		else
		{
			Ctrl.errorLog("Object[] scale出错");
			objectArrayShift=0;
		}
	}

	/** 获取unsafe实例 */
	public static Unsafe getUnsafe()
	{
		return _unsafe;
	}
}
