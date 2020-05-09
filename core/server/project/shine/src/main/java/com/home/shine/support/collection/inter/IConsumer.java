package com.home.shine.support.collection.inter;

import java.util.function.BiConsumer;

public interface IConsumer<T> extends BiConsumer<T,Object>
{
	void execute(T v);
	
	default void accept(T arg0,Object arg1)
	{
		execute(arg0);
	}

	;
}
