package com.home.shineTest.mtest;

import com.home.shineTest.thread.IThread;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/** 公司项目线程包装 */
public class TThread implements IThread
{
	private final Queue<Runnable> queue = new ArrayDeque<Runnable>();
	private Runnable active;
	
	private ThreadPoolExecutor executor;
	
	public TThread()
	{
		executor = new ThreadPoolExecutor(
				//Runtime.getRuntime().availableProcessors() * 2,
				8,
				Integer.MAX_VALUE, 60,
				TimeUnit.SECONDS, new SynchronousQueue<>(), (r) ->
				new Thread(r, "MapWorker")
		);
	}
	
	@Override
	public void addFunc(Runnable func)
	{
		execute(func);
	}
	
	@Override
	public void start()
	{
	
	}
	
	
	public synchronized void execute(final Runnable r)
	{
		queue.offer(() -> {
			
			try {
				r.run();
			} finally {
				synchronized (TThread.this) {
					scheduleNext();
				}
			}
		});
		if (active == null) {
			scheduleNext();
		}
	}
	
	private void scheduleNext() {
		if ((active = queue.poll()) != null) {
			executor.execute(active);
		}
	}
}
