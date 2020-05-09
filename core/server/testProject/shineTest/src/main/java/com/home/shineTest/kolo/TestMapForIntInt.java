package com.home.shineTest.kolo;

import com.koloboke.collect.map.hash.HashIntIntMap;
import com.koloboke.compile.ConcurrentModificationUnchecked;
import com.koloboke.compile.KolobokeMap;

@KolobokeMap
@ConcurrentModificationUnchecked
public abstract class TestMapForIntInt implements HashIntIntMap
{
	public static TestMapForIntInt withExpectedSize(int expectedSize)
	{
		return new KolobokeTestMapForIntInt(expectedSize);
    }
	
	public abstract void justPut(int key,int value);
}
