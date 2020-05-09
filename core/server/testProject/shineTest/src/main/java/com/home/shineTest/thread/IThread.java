package com.home.shineTest.thread;

public interface IThread
{
	void addFunc(Runnable func);
	
	void start();
}
