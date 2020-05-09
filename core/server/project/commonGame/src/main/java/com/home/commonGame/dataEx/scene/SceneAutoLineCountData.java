package com.home.commonGame.dataEx.scene;

import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.global.Global;
import com.home.shine.control.DateControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.dataEx.VBoolean;
import com.home.shine.support.collection.LongLongMap;
import com.home.shine.support.collection.SList;
import com.home.shine.support.pool.ObjectPool;

/** 自动分线场景统计数据 */
public class SceneAutoLineCountData
{
	private ObjectPool<OneData> _pool=new ObjectPool<>(OneData::new);
	/** 统计数 */
	private SList<OneData> _dic=new SList<>(OneData[]::new);
	
	/** 每条线最多人数 */
	public int lineMax;
	/** 当前空闲序号 */
	public int nowIndex=0;
	
	public SceneAutoLineCountData()
	{
		_pool.setEnable(CommonSetting.logicUsePool);
	}
	
	public void onSecond()
	{
		if(!_dic.isEmpty())
		{
			long now=DateControl.getTimeMillis();
			
			OneData[] values=_dic.getValues();
			OneData v;
			
			for(int i=0,len=_dic.size();i<len;++i)
			{
				v=values[i];
				
				if(v!=null && v.check(now) && v.getNum()<lineMax && i<nowIndex)
				{
					//更改标记
					nowIndex=i;
				}
			}
		}
	}
	
	private OneData getOne(int index)
	{
		if(index>= _dic.size())
		{
			_dic.setLength(index+1);
		}
		
		OneData re=_dic.get(index);
		
		if(re==null)
		{
			_dic.set(index,re=_pool.getOne());
		}
		
		return re;
	}
	
	public void addOne(long playerID,int index,boolean isPre)
	{
		OneData one=getOne(index);
		
		if(isPre)
		{
			one.addPre(playerID);
		}
		else
		{
			one.addOne(playerID);
		}
		
		//达到
		if(one.getNum()>=lineMax && nowIndex==index)
		{
			OneData[] values=_dic.getValues();
			
			int size=_dic.size();
			for(int i=index+1;i<size;i++)
			{
				if(values[i].getNum()<lineMax)
				{
					nowIndex=i;
					return;
				}
			}
			
			//新的
			nowIndex=_dic.size();
			
			if(nowIndex>=CommonSetting.sceneAutoLineMax)
			{
				Ctrl.errorLog("超出分线极限",nowIndex);
			}
		}
	}
	
	public void removeOne(int index)
	{
		if(index>= _dic.size())
		{
			Ctrl.errorLog("不该尺寸不足");
			return;
		}
		
		OneData one=getOne(index);
		one.removeOne();
		
		if(one.getNum()<lineMax && index<nowIndex)
		{
			//更改标记
			nowIndex=index;
		}
	}
	
	private class OneData
	{
		public int realNum;
		
		/** 预备进入字典 */
		private LongLongMap _preDic=new LongLongMap();
		
		public void addPre(long playerID)
		{
			_preDic.put(playerID,DateControl.getTimeMillis()+Global.autoLinedScenePreTimeOut*1000);
		}
		
		public boolean check(long now)
		{
			VBoolean re=new VBoolean();
			
			if(!_preDic.isEmpty())
			{
				_preDic.forEachS((k,v)->
				{
					if(v<now)
					{
						_preDic.remove(k);
						
						re.value=true;
					}
				});
			}
			
			return re.value;
		}
		
		public int getNum()
		{
			return realNum+_preDic.size();
		}
		
		public void addOne(long playerID)
		{
			_preDic.remove(playerID);
			realNum++;
		}
		
		public void removeOne()
		{
			realNum--;
		}
	}
}
