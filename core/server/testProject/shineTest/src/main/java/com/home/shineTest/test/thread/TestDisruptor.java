package com.home.shineTest.test.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.dsl.Disruptor;

public class TestDisruptor extends BaseTestThread
{
	private Disruptor<Evt> _disruptor;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void initThread()
	{
		ExecutorService exec = Executors.newFixedThreadPool(4);
		EventFactory<Evt> factory=()->{return new Evt();};
		_disruptor=new Disruptor<>(factory,1024,exec);
		_disruptor.handleEventsWith(new EvtHandle());
		_disruptor.start();
	}
	
	@Override
	protected void toTestOnce(int index)
	{
		EvtT t=new EvtT(this);
		int len=_waveNum;
		Disruptor<Evt> disruptor=_disruptor;
		
		for(int i=0;i<len;++i)
		{
			disruptor.publishEvent(t);
		}
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
		private BaseTestThread _self;
		
		public EvtT(BaseTestThread self)
		{
			_self=self;
		}
		
		@Override
		public void translateTo(Evt event,long sequence)
		{
			//event.run=_self::execute;
		}
	}
}
