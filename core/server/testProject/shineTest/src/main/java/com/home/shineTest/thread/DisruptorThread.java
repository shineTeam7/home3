package com.home.shineTest.thread;

import com.home.shine.constlist.ThreadType;
import com.home.shine.thread.AbstractThread;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.dsl.Disruptor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DisruptorThread extends AbstractThread
{
	private Disruptor<Evt> _disruptor;
	
	private Thread _thread;
	
	@SuppressWarnings("unchecked")
	public DisruptorThread(String name)
	{
		super(name,ThreadType.Pool,0);
		
		ExecutorService exec = Executors.newSingleThreadExecutor(k->
		{
			return _thread=new Thread(k,name);
		});
		
		EventFactory<Evt> factory=()->{return new Evt();};
		_disruptor=new Disruptor<>(factory,1024,exec);
		_disruptor.handleEventsWith(new EvtHandle());
	}
	
	@Override
	public synchronized void start()
	{
		_disruptor.start();
		
		//super.start();
	}
	
	public void addFunc(Runnable func)
	{
		if(Thread.currentThread()==_thread)
		{
			func.run();
			return;
		}
		
		_disruptor.publishEvent(new EvtT(func));
	}
	
	@Override
	protected void toAddFunc(Runnable func,AbstractThread from)
	{
	
	}
	
	private class Evt
	{
		public Runnable run;
	}
	
	private class EvtHandle implements EventHandler<Evt>
	{
		@Override
		public void onEvent(Evt event,long sequence,boolean endOfBatch) throws Exception
		{
			event.run.run();
			event.run=null;
		}
	}
	
	private class EvtT implements EventTranslator<Evt>
	{
		private Runnable _run;
		
		public EvtT(Runnable run)
		{
			_run=run;
		}
		
		@Override
		public void translateTo(Evt event,long sequence)
		{
			event.run=_run;
		}
	}
}