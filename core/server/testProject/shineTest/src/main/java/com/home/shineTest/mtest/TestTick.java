package com.home.shineTest.mtest;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Thread)
public class TestTick {

  private int _loop=1000;
  private int _aa=0;

  private BlockingQueue<Runnable> _queue=new ArrayBlockingQueue<Runnable>(1024);

  private void doOne()
  {
    _aa++;
  }

  @Benchmark
  public void testFor()
  {
    for(int i=_loop-1;i>=0;--i)
    {
        doOne();
    }
  }

  @Benchmark
  public void testQueue()
  {
    _queue.offer(this::doOne);
    Runnable poll = _queue.poll();
    poll.run();
  }
}
