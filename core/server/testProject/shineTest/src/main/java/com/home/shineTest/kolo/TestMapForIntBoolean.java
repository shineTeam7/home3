package com.home.shineTest.kolo;

import com.koloboke.collect.map.hash.HashIntByteMap;
import com.koloboke.compile.ConcurrentModificationUnchecked;
import com.koloboke.compile.KolobokeMap;

@KolobokeMap
@ConcurrentModificationUnchecked
public abstract class TestMapForIntBoolean implements HashIntByteMap
{
	public static TestMapForIntBoolean withExpectedSize(int expectedSize)
	{
		return new KolobokeTestMapForIntBoolean(expectedSize);
    }
	
	public abstract void justPut(int key,byte value);
}
