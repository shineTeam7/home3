package com.home.commonBase.tool;

import com.home.commonBase.dataEx.role.AttributeCalculateInfo;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.global.Global;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.data.DIntData;
import com.home.shine.support.collection.IntIntMap;
import com.home.shine.utils.MathUtils;

/** 属性计算工具 */
public abstract class AttributeTool implements IAttributeTool
{
	protected AttributeCalculateInfo _info;
	
	/** 是否是主单位 */
	private boolean _isM;
	
	protected IntIntMap _attributesDataDic;
	
	protected int[] _attributes;
	
	/** 当前属性的max值缓存 */
	protected int[] _currentMaxCache;
	/** 修改标记(只用于当前属性和组属性) */
	protected boolean[] _attributeModifications;
	/** 属性修改标记 */
	protected boolean _attributeModified=false;
	
	
	//send
	/** 属性推送标记 */
	protected boolean _sendDirty=false;
	/** 上次推送属性组 */
	protected int[] _lastSends;
	/** 推送组(也做临时组) */
	protected int[] _sendList;
	
	/** 普通属性改变记录组 */
	private int[] _normalLastAttributes;
	/** 普通推送是否开 */
	private boolean _normalSendOpen=false;
	
	//dispatch
	/** 逻辑派发标记 */
	protected boolean _dispatchDirty=false;
	/** 上次派发属性组 */
	protected int[] _lastDispatch;
	/** 上次派发属性组 */
	protected int[] _dispatchList;
	/** 派发set */
	protected boolean[] _changeSet;
	
	//increase
	/** 是否需要自增属性组 */
	private boolean[] _increaseNeedSet;
	private boolean _needIncrease;
	/** 行进时间 */
	private int _passTime;
	
	/** 数据信息 */
	public void setInfo(AttributeCalculateInfo info)
	{
		_info=info;
		
		_attributes=new int[info.size];
		_attributeModifications=new boolean[info.size];
		_changeSet=new boolean[info.size];
		
		_lastDispatch=new int[info.size];
		
		_currentMaxCache=new int[info.currentList.length];
		
		_sendList=new int[info.allMaybeSendList.length];
		_lastSends=new int[info.allMaybeSendList.length];
		
		_dispatchList=new int[info.needDispatchList.length];
		
		_increaseNeedSet=new boolean[info.increaseList.length];
	}
	
	/** 是否是主单位 */
	public void setIsM(boolean value)
	{
		_isM=value;
	}
	
	/** 设置数据 */
	public void setData(IntIntMap values)
	{
		_attributesDataDic=values;
		
		if(values!=null && !values.isEmpty())
		{
			int[] attributes=_attributes;
			int[] lastSends=_lastSends;
			int[] lastDispatches=_lastDispatch;
			boolean[] allMaybeSendSet=_info.allMaybeSendSet;
			int[] allMaybeSendToIndex=_info.allMaybeSendToIndex;
			boolean[] needDispatchSet=_info.needDispatchSet;
			
			values.forEach((k,v)->
			{
				attributes[k]=v;
				
				int currentID;
				if((currentID=_info.maxToCurrentMap[k])>0)
					_currentMaxCache[_info.currentToIndex[currentID]]=v;
				
				if(allMaybeSendSet[k])
					lastSends[allMaybeSendToIndex[k]]=v;
				
				if(needDispatchSet[k])
					lastDispatches[k]=v;
			});
			
			_sendDirty=false;
			_dispatchDirty=false;
			
			if(!CommonSetting.isClient || CommonSetting.isClientDriveLogic)
			{
				countNeedIncrease();
			}
		}
	}
	
	private void clearAttributesToDefault()
	{
		//属性归零
		int[] attributes=_attributes;
		boolean[] attributeModifications=_attributeModifications;
		int[] lastAttributes=_lastDispatch;
		boolean[] changeSet=_changeSet;
		int[] normalLastAttributes=_normalLastAttributes;
		
		for(int i=_info.size - 1;i >= 0;--i)
		{
			attributes[i]=0;
			attributeModifications[i]=false;
			lastAttributes[i]=0;
			changeSet[i]=false;
			
			if(normalLastAttributes!=null)
			{
				normalLastAttributes[i]=0;
			}
		}
		
		int[] currentMaxCache=_currentMaxCache;
		
		for(int i=currentMaxCache.length-1;i>=0;--i)
		{
			currentMaxCache[i]=0;
		}
		
		int[] sendList=_sendList;
		int[] lastSends=_lastSends;
		
		for(int i=_info.allMaybeSendList.length-1;i>=0;--i)
		{
			sendList[i]=0;
			lastSends[i]=0;
		}
		
		int[] dispatchList=_dispatchList;
		
		for(int i=dispatchList.length-1;i>=0;--i)
		{
			dispatchList[i]=0;
		}
		
		_attributeModified=false;
		_sendDirty=false;
		_dispatchDirty=false;
		_normalSendOpen=false;
		_needIncrease=false;
		_passTime=0;
	}
	
	//方法组
	
	/** 获取属性组 */
	public int[] getAttributes()
	{
		return _attributes;
	}
	
	/** 清理数据 */
	public void clear()
	{
		//属性归零
		_attributesDataDic.clear();
		
		clearAttributesToDefault();
	}
	
	private void writeSendDic(int[] list)
	{
		doRefresh();
		
		IntIntMap dic=_attributesDataDic;
		dic.clear();
		
		int[] attributes=_attributes;
		int type;
		int v;
		
		for(int i=list.length - 1;i >= 0;--i)
		{
			if((v=(attributes[type=list[i]]))!=0)
				dic.put(type,v);
		}
	}
	
	public void writeForSelf()
	{
		writeSendDic(_info.sendSelfAbsList);
	}
	
	public void writeForOther()
	{
		writeSendDic(_info.sendOtherList);
	}
	
	public void writeForCopy()
	{
		doRefresh();
		
		IntIntMap dic=_attributesDataDic;
		dic.clear();
		
		int[] attributes=_attributes;
		int v;
		
		for(int i=attributes.length-1;i>=0;--i)
		{
			if((v=(attributes[i]))!=0)
				dic.put(i,v);
		}
	}
	
	/** 计算单个属性 */
	private void countOneAttribute(int type)
	{
		int[] formula;
		if((formula=_info.formulaTypeDic[type])!=null)
		{
			_attributes[type]=_info.calculateAttribute(this,formula);
		}
		
		int maxID;
		
		if((maxID=_info.currentToMaxMap[type])>0)
		{
			int sIndex;
			int oldMax=_currentMaxCache[sIndex=_info.currentToIndex[type]];
			int nowMax=getAttribute(maxID);
			
			//上限变更
			if(nowMax!=oldMax)
			{
				countCurrentAtMaxChange(type,oldMax,nowMax);
				//记录新的
				_currentMaxCache[sIndex]=nowMax;
			}
			
			int[] attributes;
			int nowValue=(attributes=_attributes)[type];
			//范围
			if(!_info.currentCanOverMax[type] && nowValue>nowMax)
			{
				attributes[type]=nowValue=nowMax;
			}
			
			if(nowValue<0)
			{
				attributes[type]=0;
			}
		}
		
		_attributeModifications[type]=false;
	}
	
	/** 上限改变时的当前属性刷新 */
	private void countCurrentAtMaxChange(int type,int oldMax,int newMax)
	{
		if(Global.keepCurrentPercentAtMaxChange)
		{
			if(oldMax>0)
			{
				int[] attributes=_attributes;
				float percent=(float)newMax / oldMax;
				attributes[type]=Math.round(percent * attributes[type]);
			}
		}
	}
	
	/** 计算属性(不推送) */
	public void countAttributes()
	{
		_attributeModified=false;
		
		boolean[] attributeModifications=_attributeModifications;
		AttributeCalculateInfo info=_info;
		
		for(int k:info.formulaResultList)
		{
			if(attributeModifications[k])
			{
				countOneAttribute(k);
			}
		}
		
		for(int k:info.currentList)
		{
			if(attributeModifications[k])
			{
				countOneAttribute(k);
			}
		}
		
		countNeedIncrease();
	}
	
	private void countNeedIncrease()
	{
		_needIncrease=false;
		
		AttributeCalculateInfo info=_info;
		int[] attributes=_attributes;
		int currentID;
		int maxID;
		int index;
		for(int k:info.increaseList)
		{
			index=info.increaseToIndex[k];
			//有值
			if(attributes[k]!=0)
			{
				//无上限值或还没满
				boolean b=(maxID=info.currentToMaxMap[currentID=info.increaseToCurrentMap[k]])<=0 || attributes[currentID]<attributes[maxID];
				
				if(b)
				{
					_increaseNeedSet[index]=true;
					_needIncrease=true;
				}
			}
			else
			{
				_increaseNeedSet[index]=false;
			}
		}
	}
	
	/** 每帧间隔 */
	public void onPiece(int delay)
	{
		refreshAttributes();
		
		if(!CommonSetting.isClient || CommonSetting.isClientDriveLogic)
		{
			//再计算增加
			if(_needIncrease)
			{
				if((_passTime+=delay)>=Global.increaseAttributeDelay)
				{
					_passTime-=Global.increaseAttributeDelay;
					increaseOnce();
				}
			}
		}
	}
	
	/** 计算并刷新推送属性组 */
	public void refreshAttributes()
	{
		doRefresh();
		
		if(_sendDirty)
		{
			countSendAttributes();
		}
		
		if(_dispatchDirty)
		{
			countDispatchAttributes();
		}
	}
	
	/** 加一次 */
	private void increaseOnce()
	{
		AttributeCalculateInfo info=_info;
		
		boolean[] increaseNeedSet=_increaseNeedSet;
		
		int type;
		int value;
		
		for(int i=increaseNeedSet.length-1;i>=0;--i)
		{
			if(increaseNeedSet[i])
			{
				value=getAttribute(type=info.increaseList[i]);
				addOneAttribute(info.increaseToCurrentMap[type],value);
			}
		}
	}
	
	private void doRefresh()
	{
		if(!CommonSetting.isClient || CommonSetting.isClientDriveLogic)
		{
			//如需要先计算
			if(_attributeModified)
			{
				countAttributes();
			}
		}
	}
	
	/** 统计推送属性 */
	private void countSendAttributes()
	{
		_sendDirty=false;
		
		int[] attributes=_attributes;
		int[] lastSends=_lastSends;
		
		int value;
		int index;
		
		int[] allMaybeSendToIndex=_info.allMaybeSendToIndex;
		boolean[] sendSelfAbsSet=_info.sendSelfAbsSet;
		boolean[] sendSelfNormalSet=_info.sendSelfNormalSet;
		boolean[] sendOtherSet=_info.sendOtherSet;
		int[] normalAttributeChanges=_normalLastAttributes;
		
		IntIntMap sendSelfDic=null;
		IntIntMap sendOtherDic=null;
		
		for(int k:_info.allMaybeSendList)
		{
			if((value=attributes[k])!=lastSends[index=allMaybeSendToIndex[k]])
			{
				lastSends[index]=value;
				
				if(_isM)
				{
					if(sendSelfAbsSet[k])
					{
						if(sendSelfDic==null)
							sendSelfDic=new IntIntMap();
						
						sendSelfDic.put(k,value);
					}
					else if(sendSelfNormalSet[k] && _normalSendOpen)
					{
						if(sendSelfDic==null)
							sendSelfDic=new IntIntMap();
						
						sendSelfDic.put(k,value);
						
						if(normalAttributeChanges!=null)
							normalAttributeChanges[k]=value;
					}
				}
				
				if(sendOtherSet[k])
				{
					if(sendOtherDic==null)
						sendOtherDic=new IntIntMap();
					
					sendOtherDic.put(k,value);
				}
			}
		}
		
		if(sendSelfDic!=null)
		{
			toSendSelf(sendSelfDic);
		}
		
		if(sendOtherDic!=null)
		{
			toSendOther(sendOtherDic);
		}
	}
	
	/** 统计派发属性 */
	private void countDispatchAttributes()
	{
		_dispatchDirty=false;
		
		int num=0;
		
		int[] attributes=_attributes;
		int[] lastDispatch=_lastDispatch;
		int[] dispatchList=_dispatchList;
		boolean[] dispatchSet=_changeSet;
		
		for(int k:_info.needDispatchList)
		{
			if(attributes[k]!=lastDispatch[k])
			{
				dispatchList[num++]=k;
				dispatchSet[k]=true;
			}
		}
		
		if(num>0)
		{
			try
			{
				toDispatchAttribute(dispatchList,num,dispatchSet);
			}
			catch(Exception e)
			{
				Ctrl.errorLog(e);
			}
			
			int type;
			for(int i=num-1;i>=0;--i)
			{
				dispatchSet[type=dispatchList[i]]=false;
				lastDispatch[type]=attributes[type];
			}
		}
	}
	
	/** 推送自己 */
	abstract protected void toSendSelf(IntIntMap dic);
	/** 广播别人 */
	abstract protected void toSendOther(IntIntMap dic);
	/** 派发刷新属性 */
	abstract protected void toDispatchAttribute(int[] changeList,int num,boolean[] changeSet);
	
	/** 设置单个属性值 */
	public void setOneAttribute(int type,int value)
	{
		if(_info.formulaTypeDic[type]!=null)
		{
			Ctrl.errorLog("不能设置属性的公式输出值:",type);
			return;
		}
		
		_attributes[type]=value;
		
		makeDirty(type);
	}
	
	/** 标记脏 */
	private void makeDirty(int type)
	{
		//是当前属性
		if(_info.currentToMaxMap[type]>0)
		{
			_attributeModifications[type]=true;
			_attributeModified=true;
		}
		
		//是当前属性的max
		int k;
		if((k=_info.maxToCurrentMap[type])>0)
		{
			_attributeModifications[type]=true;
			_attributeModifications[k]=true;
			_attributeModified=true;
		}
		
		if(_info.allMaybeSendSet[type])
		{
			_sendDirty=true;
		}
		
		if(_info.needDispatchSet[type])
		{
			_dispatchDirty=true;
		}
		
		int[] results;
		//有影响的结果组
		if((results=_info.elementToResultDic[type])!=null)
		{
			for(int v:results)
			{
				_attributeModifications[v]=true;
				_attributeModified=true;
				
				makeDirty(v);
			}
		}
	}
	
	/** 获取属性 */
	@Override
	public int getAttribute(int type)
	{
		//当前属性有变化
		if(_attributeModifications[type])
		{
			countOneAttribute(type);
		}
		
		return _attributes[type];
	}
	
	public float getAttributeF(int type)
	{
		return getAttribute(type);
	}
	
	/** 获取属性增加率 */
	public float getRatio(int type)
	{
		int re;
		
		if((re=getAttribute(type))==0)
			return 0f;
		
		return re/1000f;
	}
	
	/** 获取属性增加率 */
	@Override
	public float getAddRatio(int type)
	{
		int re;
		
		if((re=getAttribute(type))==0)
			return 1f;
		
		return (re+1000f)/1000f;
	}
	
	/** 返回被某加成影响过的结果值 */
	public int getAddRatioResult(int type,int value)
	{
		int re;
		
		if((re=getAttribute(type))==0)
			return value;
		
		return (int)((re+1000f)/1000f*value);//为防止溢出
	}
	
	/** 获取当前属性百分比(float) */
	public float getCurrentPercentF(int type)
	{
		int max;
		if((max=getAttribute(_info.currentToMaxMap[type]))<=0)
			return 0f;
		
		return (float)getAttribute(type)*1000/max;
	}
	
	/** 获取当前属性千分比 */
	public int getCurrentPercent(int type)
	{
		int max;
		if((max=getAttribute(_info.currentToMaxMap[type]))<=0)
			return 0;
		
		return getAttribute(type)*1000/max;
	}
	
	/** 服务器设置属性 */
	public void setAttributesByServer(IntIntMap dic)
	{
		int[] attributes=_attributes;
		boolean[] allMaybeSendSet=_info.allMaybeSendSet;
		boolean[] needDispatchSet=_info.needDispatchSet;
		
		dic.forEach((k,v)->
		{
			attributes[k]=v;
			
			if(allMaybeSendSet[k])
			{
				_sendDirty=true;
			}
			
			if(needDispatchSet[k])
			{
				_dispatchDirty=true;
			}
		});
		
		//立刻计算
		if(_sendDirty)
		{
			countSendAttributes();
		}
		
		if(_dispatchDirty)
		{
			countDispatchAttributes();
		}
	}
	
	/** 设置普通推送开关(界面开关用) */
	public void setNormalSendOpen(boolean bb)
	{
		_normalSendOpen=bb;
		
		if(bb)
		{
			IntIntMap sendDic=null;
			
			if(_normalLastAttributes==null)
				_normalLastAttributes=new int[_info.size];
			
			int[] normalLastAttributes=_normalLastAttributes;
			int[] attributes=_attributes;
			int[] needSendList=_info.sendSelfNormalList;
			int type;
			int value;
			
			for(int i=needSendList.length - 1;i >= 0;--i)
			{
				type=needSendList[i];
				
				if((value=attributes[type])!=normalLastAttributes[type])
				{
					normalLastAttributes[type]=value;
					
					if(sendDic==null)
						sendDic=new IntIntMap();
					
					sendDic.put(type,value);
				}
			}
			
			if(sendDic!=null)
			{
				toSendSelf(sendDic);
			}
		}
	}
	
	
	/** 减一个属性(不能操作组属性的总值) */
	public void subOneAttribute(int type,int value)
	{
		setOneAttribute(type,_attributes[type] - value);
	}
	
	/** 加一个属性(不能操作组属性的总值) */
	public void addOneAttribute(int type,int value)
	{
		setOneAttribute(type,_attributes[type] + value);
	}
	
	/** 加一组属性 */
	public void addAttributes(int[] attrs)
	{
		int[] attributes=_attributes;
		int v;
		
		for(int i=attrs.length - 1;i >= 0;--i)
		{
			if((v=attrs[i])!=0)
			{
				setOneAttribute(i,attributes[i] + v);
			}
		}
	}
	
	/** 减一组属性 */
	public void subAttributes(int[] attrs)
	{
		int[] attributes=_attributes;
		int v;
		
		for(int i=attrs.length - 1;i >= 0;--i)
		{
			if((v=attrs[i])!=0)
			{
				setOneAttribute(i,attributes[i] - v);
			}
		}
	}
	
	/** 添加一组属性 */
	public void addAttributes(DIntData[] list)
	{
		int[] attributes=_attributes;
		DIntData v;
		
		for(int i=list.length - 1;i >= 0;--i)
		{
			if((v=list[i]).value!=0)
			{
				setOneAttribute(v.key,attributes[v.key] + v.value);
			}
		}
	}
	
	/** 减少一组属性 */
	public void subAttributes(DIntData[] list)
	{
		int[] attributes=_attributes;
		DIntData v;
		
		for(int i=list.length - 1;i >= 0;--i)
		{
			if((v=list[i]).value!=0)
			{
				setOneAttribute(v.key,attributes[v.key] - v.value);
			}
		}
	}
	
	/** 让当前属性回归默认 */
	public void makeCurrentToDefault()
	{
		int[] currentList=_info.currentList;
		int type;
		
		for(int i=currentList.length-1;i>=0;--i)
		{
			type=currentList[i];
			
			if(_info.currentDefaultFullSet[type])
			{
				//+1规则
				setOneAttribute(type,getAttribute(type+1));
			}
			else
			{
				setOneAttribute(type,0);
			}
		}
	}
	
	//快捷方式//
	
	/** 某当前属性是否满值 */
	public boolean isCurrentFull(int type)
	{
		return getAttribute(type)>=getAttribute(_info.currentToMaxMap[type]);
	}
	
	/** 增加上限百分比的当前属性 */
	public void addCurrentPercent(int type,int value)
	{
		if(isCurrentFull(type))
			return;
		
		addOneAttribute(type,(int)(value/1000f*getAttribute(_info.currentToMaxMap[type])));
	}
}
