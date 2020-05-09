package com.home.shineTest.mtest;

import com.home.shine.support.collection.IntIntMap;
import com.home.shine.support.collection.IntObjectMap;
import org.openjdk.jmh.annotations.Benchmark;

public class MSceneFrameTest
{
	private static final int SceneNum=20;
	
	private static final int UnitNum=50*200;
	
	private static final int FrameDelay=33;
	
	private static IntObjectMap<Scene> _sceneDic=new IntObjectMap<>(k->new Scene[k]);
	
	static
	{
		for(int i=0;i<SceneNum;i++)
		{
			Scene scene=new Scene();
			scene.instanceID=i+1;
			scene.init();
			
			_sceneDic.put(scene.instanceID,scene);
		}
	}
	
	@Benchmark
	public void testTick()
	{
		Scene[] values;
		Scene v;
		
		for(int i=(values=_sceneDic.getValues()).length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				v.onFrame(FrameDelay);
			}
		}
	}
	
	private static class Scene
	{
		public int instanceID;
		
		private IntObjectMap<Unit> _units=new IntObjectMap<>(k->new Unit[k]);
		
		public void init()
		{
			for(int i=0;i<UnitNum;i++)
			{
				addUnit(i+1);
			}
		}
		
		public void onFrame(int delay)
		{
			Unit[] values;
			Unit v;
			
			for(int i=(values=_units.getValues()).length-1;i>=0;--i)
			{
				if((v=values[i])!=null)
				{
					//if(v.frameNeedCount>0)
					//{
					//	v.onFrame(delay);
					//}
					
					v.doSth();
					
					v.onFrame(delay);
				}
			}
		}
		
		public void addUnit(int id)
		{
			Unit unit=new Unit();
			unit.instanceID=id;
			_units.put(id,unit);
		}
	}
	
	private static class Unit
	{
		public int instanceID;
		
//		public int frameNeedCount=0;
		
		public int _tickTime=0;
		
		private int _aa;

//		private IntIntMap _dic=new IntIntMap();

		private long[] _aa2=new long[8];

		public void onFrame(int delay)
		{
			if((_tickTime-=delay)<0)
			{
				_tickTime=1000;

				doSth();
			}
		}
		
		public void doSth()
		{
			++_aa;

//			if(((++_aa) & 1)==1)
//			{
//				_dic.put(1,2);
//			}
//			else
//			{
//				_dic.remove(1);
//			}
		}
	}
}