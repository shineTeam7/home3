package com.home.shineTest.kolo;

import com.koloboke.collect.map.hash.HashIntObjMap;
import com.koloboke.compile.ConcurrentModificationUnchecked;
import com.koloboke.compile.KolobokeMap;

@KolobokeMap
@ConcurrentModificationUnchecked
public abstract class TestMapForIntObj<V> implements HashIntObjMap<V>
{
	public static <V> TestMapForIntObj<V> withExpectedSize(int expectedSize)
	{
		return new KolobokeTestMapForIntObj<>(expectedSize);
    }
	
	public abstract void justPut(int key,V value);
}
