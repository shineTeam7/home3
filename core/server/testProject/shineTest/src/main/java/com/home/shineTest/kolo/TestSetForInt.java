package com.home.shineTest.kolo;

import com.koloboke.collect.set.hash.HashIntSet;
import com.koloboke.compile.ConcurrentModificationUnchecked;
import com.koloboke.compile.KolobokeSet;

@KolobokeSet
@ConcurrentModificationUnchecked
public abstract class TestSetForInt implements HashIntSet
{
	public static TestSetForInt withExpectedSize(int expectedSize)
	{
		return new KolobokeTestSetForInt(expectedSize);
    }
}
