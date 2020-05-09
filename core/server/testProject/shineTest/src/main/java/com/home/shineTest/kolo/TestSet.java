package com.home.shineTest.kolo;

import java.util.Set;

import com.koloboke.compile.ConcurrentModificationUnchecked;
import com.koloboke.compile.KolobokeSet;

@KolobokeSet
@ConcurrentModificationUnchecked
public abstract class TestSet<V> implements Set<V>
{

}
