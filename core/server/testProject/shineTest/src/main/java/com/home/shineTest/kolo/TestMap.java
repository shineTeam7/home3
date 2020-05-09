package com.home.shineTest.kolo;

import java.util.Map;

import com.koloboke.compile.ConcurrentModificationUnchecked;
import com.koloboke.compile.KolobokeMap;
import com.koloboke.compile.NullKeyAllowed;

@KolobokeMap
@NullKeyAllowed(false)
@ConcurrentModificationUnchecked
public abstract class TestMap<K,V> implements Map<K,V>
{
	public static <K,V> TestMap<K,V> withExpectedSize(int expectedSize)
	{
		return new KolobokeTestMap<>(expectedSize);
    }
	
	public abstract void justPut(K key, V value);
}
