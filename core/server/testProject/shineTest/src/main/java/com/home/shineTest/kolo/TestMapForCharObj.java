package com.home.shineTest.kolo;

import com.koloboke.collect.map.hash.HashCharObjMap;
import com.koloboke.compile.ConcurrentModificationUnchecked;
import com.koloboke.compile.KolobokeMap;

@KolobokeMap
@ConcurrentModificationUnchecked
public abstract class TestMapForCharObj<V> implements HashCharObjMap<V>
{
	public static <V> TestMapForCharObj<V> withExpectedSize(int expectedSize)
	{
		return new KolobokeTestMapForCharObj<>(expectedSize);
	}
	
	public abstract void justPut(char key,V value);
}


