package com.home.shineTest.kolo;

import com.koloboke.collect.map.hash.HashLongObjMap;
import com.koloboke.compile.ConcurrentModificationUnchecked;
import com.koloboke.compile.KolobokeMap;

@KolobokeMap
@ConcurrentModificationUnchecked
public abstract class TestMapForLongObj<V> implements HashLongObjMap<V>
{
	public static <V> TestMapForLongObj<V> withExpectedSize(int expectedSize)
	{
		return new KolobokeTestMapForLongObj<>(expectedSize);
    }
	
	public abstract void justPut(long key,V value);
}
