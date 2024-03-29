package com.home.commonBase.trigger;

import com.home.commonBase.constlist.generate.TriggerEventType;
import com.home.commonBase.constlist.generate.TriggerFunctionType;
import com.home.commonBase.constlist.system.TriggerActionTimerType;
import com.home.commonBase.constlist.system.TriggerChildRunnerType;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.logic.LogicEntity;
import com.home.commonBase.support.func.BooleanTriggerFunc;
import com.home.commonBase.support.func.FloatTriggerFunc;
import com.home.commonBase.support.func.IntTriggerFunc;
import com.home.commonBase.support.func.LongTriggerFunc;
import com.home.commonBase.support.func.ObjectTriggerFunc;
import com.home.commonBase.support.func.StringTriggerFunc;
import com.home.commonBase.support.func.TriggerFuncEntry;
import com.home.commonBase.tool.TriggerFuncMaker;
import com.home.shine.constlist.STriggerObjectType;
import com.home.shine.data.trigger.TriggerBooleanData;
import com.home.shine.data.trigger.TriggerConfigData;
import com.home.shine.data.trigger.TriggerFloatData;
import com.home.shine.data.trigger.TriggerFuncData;
import com.home.shine.data.trigger.TriggerFuncListData;
import com.home.shine.data.trigger.TriggerIntData;
import com.home.shine.data.trigger.TriggerLongData;
import com.home.shine.data.trigger.TriggerObjData;
import com.home.shine.data.trigger.TriggerStringData;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.SList;
import com.home.shine.support.collection.SMap;
import com.home.shine.support.collection.SSet;
import com.home.shine.support.pool.ObjectPool;
import com.home.shine.utils.MathUtils;

import java.util.Arrays;
import java.util.Objects;

/** trigger执行器 */
public class TriggerExecutor extends LogicEntity
{
	private int _groupType;
	private int _groupID;
	
	protected TriggerFuncMaker _funcMaker;
	
	/** trigger配置总组 */
	private IntObjectMap<TriggerConfigData> _configDic;

	/** 事件注册池 */
	private ObjectPool<TriggerEventRegistData> _eventRegistDataPool=new ObjectPool<>(TriggerEventRegistData::new);
	/** 参数池 */
	private ObjectPool<TriggerArg> _argPool=new ObjectPool<>(TriggerArg::new);
	/** 事件池 */
	private ObjectPool<TriggerEvent> _eventPool=new ObjectPool<>(this::toCreateEvent);
	/** 实例池 */
	private ObjectPool<TriggerInstance> _instancePool=new ObjectPool<>(this::toCreateInstance);
	/** 实例池 */
	private ObjectPool<TriggerIntervalData> _intervalPool=new ObjectPool<>(TriggerIntervalData::new);
	/** runner池 */
	private ObjectPool<TriggerActionRunner> _actionRunnerPool=new ObjectPool<>(this::toCreateActionRunner);

	/** trigger开启组 */
	private IntObjectMap<TriggerConfigData> _openDic=new IntObjectMap<>(TriggerConfigData[]::new);
	/** 激活事件组 */
	private IntObjectMap<SList<TriggerEventRegistData>> _eventActiveDic=new IntObjectMap<>();
	/** trigger实例组 */
	private IntObjectMap<TriggerInstance> _instanceDic=new IntObjectMap<>(TriggerInstance[]::new);
	/** 需要计时器检查的instance组 */
	private SSet<TriggerIntervalData> _timerIntervalDic=new SSet<>(TriggerIntervalData[]::new);
	/** 需要计时器检查的runner组 */
	private SSet<TriggerActionRunner> _timerRunnerDic=new SSet<>(TriggerActionRunner[]::new);
	
	/** strKey变量字典 */
	public SMap<String,Object> sVarDic=new SMap<>();
	
	//temp
	
	private SList<TriggerEventRegistData> _tempEventList=new SList<>(TriggerEventRegistData[]::new);
	
	//构造
	public void construct()
	{
		_eventRegistDataPool.setEnable(CommonSetting.triggerUsePool);
		_argPool.setEnable(CommonSetting.triggerUsePool);
		_eventPool.setEnable(CommonSetting.triggerUsePool);
		_instancePool.setEnable(CommonSetting.triggerUsePool);
		_intervalPool.setEnable(CommonSetting.triggerUsePool);
		_actionRunnerPool.setEnable(CommonSetting.triggerUsePool);
		
		_funcMaker=BaseC.trigger.getFuncMaker();
	}
	
	/** 通过组初始化 */
	public void init(int groupType,int groupID)
	{
		_configDic=TriggerConfig.getGroupDic(_groupType=groupType,_groupID=groupID);

		//没有配置
		if(_configDic==null)
		{
			return;
		}
		
		_configDic.forEachValue(v->
		{
			if(v.isOpen)
			{
				openTrigger(v.id);
			}
		});

		onInit();
		
		//刚初始化好
		triggerEvent(TriggerEventType.OnInit);
	}
	
	public boolean isEnable()
	{
		return _configDic!=null;
	}
	
	/** 初始化接口 */
	protected void onInit()
	{
	
	}

	public void dispose()
	{
		if(_configDic==null)
			return;

		_configDic=null;
		_openDic.clear();

		_eventActiveDic.forEachValue(list->
		{
			list.forEachAndClear(v->
			{
				_eventRegistDataPool.back(v);
			});
		});
		
		_instanceDic.forEachValueS(v->
		{
			_instancePool.back(v);
		});
		
		_instanceDic.clear();
		
		_timerRunnerDic.forEachS(v->
		{
			_argPool.back(v.arg);
			disposeActionRunner(v);
		});
		
		_timerRunnerDic.clear();
		
		_timerIntervalDic.forEachS(v->
		{
			_intervalPool.back(v);
		});
		
		_timerIntervalDic.clear();
	}

	/** 每帧间隔 */
	public void onFrame(int delay)
	{
		if(_configDic==null)
			return;
		
		if(!_timerRunnerDic.isEmpty())
		{
			_timerRunnerDic.forEachS(k->
			{
				tickRunnerTimer(k,delay);
			});
		}
		
		if(!_timerIntervalDic.isEmpty())
		{
			_timerIntervalDic.forEachS(k->
			{
				if(k.current>0 && (k.current-=delay)<=0)
				{
					k.current=k.delay;//不累计
					
					runTrigger(k.instance,null);
				}
			});
		}
	}
	
	/** 写描述信息 */
	@Override
	public void writeInfo(StringBuilder sb)
	{
		sb.append("groupType:");
		sb.append(_groupType);
		sb.append(" groupID:");
		sb.append(_groupID);
	}
	@Override
	protected void sendWarnLog(String str)
	{
	
	}
	
	/** 发生事件 */
	public void triggerEvent(int type)
	{
		toTriggerEvent(type,null);
	}

	/** 发生事件 */
	public void triggerEvent(int type,Object...args)
	{
		toTriggerEvent(type,args);
	}

	/** 获取可用实例 */
	protected TriggerInstance getInstance(int id)
	{
		TriggerInstance instance;

		if((instance=_instanceDic.get(id))==null)
		{
			_instanceDic.put(id,instance=doCreateInstance(id));
		}

		return instance;
	}

	private TriggerInstance doCreateInstance(int id)
	{
		TriggerConfigData config=_configDic.get(id);

		if(config==null)
		{
			warnLog("不该找不到配置",id);
			return null;
		}

		TriggerInstance one=_instancePool.getOne();
		one.config=config;
		initInstance(one);
		return one;
	}

	/** 析构actionRunner */
	private void disposeActionRunner(TriggerActionRunner runner)
	{
		if(runner.timerType>0)
		{
			_timerRunnerDic.remove(runner);
		}
		
		_actionRunnerPool.back(runner);
	}
	
	private void toTriggerEvent(int type,Object[] args)
	{
		if(_configDic==null)
			return;
		
		//if(CommonSetting.needTriggerLog)
		//	log("收到trigger消息",BaseC.trigger.getFuncName(type),args);
		
		SList<TriggerEventRegistData> list=_eventActiveDic.get(type);

		if(list==null || list.isEmpty())
			return;

		TriggerEvent evt=_eventPool.getOne();
		try
		{
			//设置参数
			evt.setArgs(type,args);
			
			initEvent(evt);
		}
		catch(Exception e)
		{
			errorLog("triggerEvent 设置参数错误",e);
		}

		evt.refCount++;
		
		TriggerEventRegistData[] values;
		TriggerEventRegistData v;
		
		boolean useTemp=false;
		
		if(list.size()>1)
		{
			useTemp=true;
			
			_tempEventList.clear();
			
			values=list.getValues();
			
			for(int i=0,len=list.size();i<len;++i)
			{
				values[i].isUsing=true;
				_tempEventList.add(values[i]);
			}
			
			list=_tempEventList;
		}
		
		values=list.getValues();
		
		for(int i=0,len=list.size();i<len;++i)
		{
			v=values[i];
			
			TriggerArg arg=_argPool.getOne();
			arg.instance=getInstance(v.config.id);
			arg.evt=evt;
			
			//检查事件
			if(!checkEventMatch(v.data,arg))
			{
				_argPool.back(arg);
				continue;
			}
			
			toRunTrigger(arg);
		}
		
		if(useTemp)
		{
			values=_tempEventList.getValues();
			
			for(int i=0,len=_tempEventList.size();i<len;++i)
			{
				v=values[i];
				
				if(v.needRelease)
				{
					_eventRegistDataPool.back(v);
				}
			}
			
			_tempEventList.clear();
		}

		//回收
		if((--evt.refCount)==0)
		{
			_eventPool.back(evt);
		}
	}
	
	/** 执行trigger(带condition) */
	private void toRunTrigger(TriggerArg arg)
	{
		//检查环境
		for(TriggerFuncData condition:arg.instance.config.conditions)
		{
			//不满足
			if(!getBooleanFuncValue(condition,arg))
			{
				_argPool.back(arg);
				return;
			}
		}
		
		toRunTriggerAbs(arg);
	}
	
	/** 执行trigger(不带condition) */
	private void toRunTriggerAbs(TriggerArg arg)
	{
		if(arg.evt!=null)
			arg.evt.refCount++;
		
		TriggerActionRunner runner=_actionRunnerPool.getOne();
		runner.initByTrigger(arg);
		runNextAction(runner);
	}
	
	/** 执行触发器(检查条件) */
	protected void runTrigger(int id,TriggerActionRunner runner)
	{
		runTrigger(getInstance(id),runner);
	}
	
	/** 执行触发器(检查条件) */
	protected void runTrigger(TriggerInstance instance,TriggerActionRunner runner)
	{
		TriggerArg arg=_argPool.getOne();
		arg.instance=instance;
		arg.parentRunner=runner;
		toRunTrigger(arg);
	}
	
	/** 执行触发器(检查条件) */
	protected void runTriggerAbs(int id,TriggerActionRunner runner)
	{
		TriggerArg arg=_argPool.getOne();
		arg.instance=getInstance(id);
		arg.parentRunner=runner;
		toRunTriggerAbs(arg);
	}

	/** 检查事件匹配 */
	protected boolean checkEventMatch(TriggerFuncData data,TriggerArg arg)
	{
		TriggerObjData[] tArgs;

		if((tArgs=data.args).length==0)
			return true;

		Object[] args=arg.evt.args;

		Object value;

		try
		{
			for(int i=0;i<tArgs.length;i++)
			{
				value=getObj(tArgs[i],arg);
				
				if(!(args!=null && args.length>i && args[i].equals(value)))
				{
					return false;
				}
			}
		}
		catch(Exception e)
		{
			errorLog("checkEventMatch出错",e);
		}

		return true;
	}

	/** 执行下一个动作 */
	protected void runNextAction(TriggerActionRunner runner)
	{
		//执行完了
		if((++runner.actionIndex)>=runner.actions.length)
		{
			completeCurrentAction(runner);
		}
		else
		{
			preActionFunc(runner.getCurrentAction(),runner);
		}
	}
	
	/** 完成当前方法区 */
	protected void completeCurrentAction(TriggerActionRunner runner)
	{
		//没有子项父
		if(runner.parent==null)
		{
			TriggerEvent evt;
			if((evt=runner.arg.evt)!=null)
			{
				//回收event
				if((--evt.refCount)==0)
				{
					_eventPool.back(evt);
				}
			}
			
			TriggerActionRunner parentRunner=runner.arg.parentRunner;
			
			//回收arg
			_argPool.back(runner.arg);
			disposeActionRunner(runner);
			
			if(parentRunner!=null)
			{
				runNextAction(parentRunner);
			}
		}
		else
		{
			TriggerActionRunner parent=runner.parent;
			
			boolean needBack;
			
			switch(runner.childType)
			{
				case TriggerChildRunnerType.If:
				{
					needBack=true;
				}
					break;
				case TriggerChildRunnerType.While:
				{
					needBack=!getBoolean(parent.getCurrentAction().args[0],runner.arg);
				}
					break;
				case TriggerChildRunnerType.ForLoop:
				{
					if((++parent.loop)>=parent.loopCount)
					{
						parent.loopCount=-1;
						parent.loop=0;
						
						needBack=true;
					}
					else
					{
						needBack=false;
					}
				}
					break;
				case TriggerChildRunnerType.ForEachList:
				{
					int index=++parent.loop;
					SList<Object> list=parent.foreachList;
					
					if(index>=list.size())
					{
						parent.foreachList=null;
						parent.loop=0;
						needBack=true;
					}
					else
					{
						needBack=false;
					}
				}
					break;
				default:
				{
					needBack=true;
					throwError("不支持的子组类型");
				}
					break;
			}
			
			if(needBack)
			{
				disposeActionRunner(runner);
				parent.arg.runner=parent;
				runNextAction(parent);
			}
			else
			{
				//再运行
				runner.actionIndex=-1;
				runNextAction(runner);
			}
		}
	}
	
	/** 运行子方法组 */
	protected void runChildActions(int type,TriggerFuncData[] list,TriggerActionRunner parent)
	{
		TriggerActionRunner runner=_actionRunnerPool.getOne();
		runner.initByAction(type,list,parent);
		runNextAction(runner);
	}

	private static int triggerEventRegistDataCompare(TriggerEventRegistData arg1,TriggerEventRegistData arg2)
	{
		if(arg1.priority==arg2.priority)
			return 0;
		
		//大的在前
		return arg1.priority>arg2.priority ? -1 : 1;
	}
	
	protected void addEventListener(TriggerConfigData config,int index,TriggerFuncData data)
	{
		SList<TriggerEventRegistData> list=_eventActiveDic.computeIfAbsent(data.id,k->new SList<>(TriggerEventRegistData[]::new));

		TriggerEventRegistData eData=_eventRegistDataPool.getOne();
		eData.config=config;
		eData.index=index;
		eData.data=data;
		eData.priority=config.priority;

		if(list.isEmpty())
		{
			list.add(eData);
		}
		else
		{
			TriggerEventRegistData[] values=list.getValues();
			int idx=Arrays.binarySearch(values,0,list.size(),eData,TriggerExecutor::triggerEventRegistDataCompare);
			
			if(idx>=0)
			{
				list.insert(idx,eData);
			}
			else
			{
				list.insert(-idx+1,eData);
			}
		}
	}

	protected void removeEventListener(TriggerConfigData config,int index,TriggerFuncData data)
	{
		SList<TriggerEventRegistData> list=_eventActiveDic.get(data.id);

		if(list==null)
			return;

		TriggerEventRegistData[] values=list.getValues();

		for(int i=list.size()-1;i>=0;--i)
		{
			if(values[i].config.id==config.id)
			{
				TriggerEventRegistData eData=list.remove(i);
				
				if(eData.isUsing)
				{
					eData.needRelease=true;
				}
				else
				{
					_eventRegistDataPool.back(eData);
				}
			}
		}

	}
	
	/** 开启trigger */
	public void openTrigger(int id)
	{
		TriggerConfigData config=_configDic.get(id);
		
		if(config==null)
		{
			throwError("未找到trigger配置",id);
			return;
		}
		
		if(_openDic.contains(id))
		{
			warnLog("trigger已开启",id);
			return;
		}
		
		_openDic.put(id,config);
		
		
		TriggerFuncData[] events=config.events;
		TriggerFuncData evt;
		TriggerInstance instance;
		
		for(int i=0;i<events.length;i++)
		{
			evt=events[i];
			
			if(evt.id==TriggerEventType.OnInterval)
			{
				instance=getInstance(id);
				
				TriggerIntervalData iData=_intervalPool.getOne();
				
				TriggerArg one=_argPool.getOne();
				one.instance=instance;
				iData.current=iData.delay=getInt(evt.args[0],one);
				_argPool.back(one);
				
				iData.instance=instance;
				
				instance.intervalDic.add(iData);
				_timerIntervalDic.add(iData);
			}
			else
			{
				addEventListener(config,i,events[i]);
			}
		}
	}
	
	/** 关闭trigger */
	public void closeTrigger(int id)
	{
		TriggerConfigData config=_openDic.remove(id);
		
		if(config==null)
		{
			warnLog("trigger已关闭",id);
			return;
		}
		
		TriggerFuncData[] events=config.events;
		TriggerFuncData evt;
		TriggerInstance instance;
		
		for(int i=0;i<events.length;i++)
		{
			evt=events[i];
			
			if(evt.id==TriggerEventType.OnInterval)
			{
				instance=getInstance(id);
				
				instance.intervalDic.forEachAndClear(v->
				{
					_timerIntervalDic.remove(v);
					_intervalPool.back(v);
				});
			}
			else
			{
				removeEventListener(config,i,evt);
			}
		}
	}
	
	protected void tickRunnerTimer(TriggerActionRunner runner,int delay)
	{
		if(runner.currentTime>0 && (runner.currentTime-=delay)<=0)
		{
			runner.currentTime=0;
			
			switch(runner.timerType)
			{
				case TriggerActionTimerType.Wait:
				{
					removeTimeRunner(runner);
					runNextAction(runner);
				}
					break;
				case TriggerActionTimerType.WaitUtil:
				{
					if(getBoolean(runner.getCurrentAction().args[0],runner.arg))
					{
						removeTimeRunner(runner);
						runNextAction(runner);
					}
					else
					{
						//继续等
						runner.currentTime=runner.timeMax;
					}
				}
					break;
			}
		}
	}

	protected void addTimeRunner(TriggerActionRunner runner)
	{
		_timerRunnerDic.add(runner);
	}

	protected void removeTimeRunner(TriggerActionRunner runner)
	{
		_timerRunnerDic.remove(runner);
	}

	/** 获取对象值 */
	public Object getObj(TriggerObjData obj,TriggerArg arg)
	{
		try
		{
			switch(obj.getDataID())
			{
				case TriggerBooleanData.dataID:
				{
					return ((TriggerBooleanData)obj).value;
				}
				case TriggerIntData.dataID:
				{
					return ((TriggerIntData)obj).value;
				}
				case TriggerFloatData.dataID:
				{
					return ((TriggerFloatData)obj).value;
				}
				case TriggerLongData.dataID:
				{
					return ((TriggerLongData)obj).value;
				}
				case TriggerStringData.dataID:
				{
					return ((TriggerStringData)obj).value;
				}
				case TriggerFuncData.dataID:
				{
					return getFuncValue((TriggerFuncData)obj,arg);
				}
			}
		}
		catch(Exception e)
		{
			errorLog("getObj出错",e);
		}

		return null;
	}

	/** 获取方法值 */
	protected Object getFuncValue(TriggerFuncData func,TriggerArg arg)
	{
		int returnType;

		if((returnType=BaseC.trigger.getFuncReturnType(func.id))==0)
		{
			throwError("未注册trigger方法的返回值",func.id);
			return null;
		}
		
		if(returnType==STriggerObjectType.Void)
			return null;

		return toGetFuncValue(returnType,func,arg);
	}

	/** 获取boolean值 */
	public boolean getBoolean(TriggerObjData obj,TriggerArg arg)
	{
		if(obj.getDataID()==TriggerBooleanData.dataID)
		{
			return ((TriggerBooleanData)obj).value;
		}
		else if(obj.getDataID()==TriggerFuncData.dataID)
		{
			return getBooleanFuncValue((TriggerFuncData)obj,arg);
		}

		throwError("不支持的数据类型,boolean",obj.getDataClassName());
		return false;
	}

	/** 获取int值 */
	public int getInt(TriggerObjData obj,TriggerArg arg)
	{
		if(obj.getDataID()==TriggerIntData.dataID)
		{
			return ((TriggerIntData)obj).value;
		}
		else if(obj.getDataID()==TriggerFuncData.dataID)
		{
			return getIntFuncValue((TriggerFuncData)obj,arg);
		}

		throwError("不支持的数据类型,int",obj.getDataClassName());
		return 0;
	}
	
	/** 获取float值 */
	public float getFloat(TriggerObjData obj,TriggerArg arg)
	{
		if(obj.getDataID()==TriggerFloatData.dataID)
		{
			return ((TriggerFloatData)obj).value;
		}
		else if(obj.getDataID()==TriggerFuncData.dataID)
		{
			return getFloatFuncValue((TriggerFuncData)obj,arg);
		}
		
		throwError("不支持的数据类型,float",obj.getDataClassName());
		return 0f;
	}
	
	/** 获取float值 */
	public long getLong(TriggerObjData obj,TriggerArg arg)
	{
		if(obj.getDataID()==TriggerLongData.dataID)
		{
			return ((TriggerLongData)obj).value;
		}
		else if(obj.getDataID()==TriggerFuncData.dataID)
		{
			return getLongFuncValue((TriggerFuncData)obj,arg);
		}
		
		throwError("不支持的数据类型,long",obj.getDataClassName());
		return 0L;
	}
	
	/** 获取String值 */
	public String getString(TriggerObjData obj,TriggerArg arg)
	{
		if(obj.getDataID()==TriggerStringData.dataID)
		{
			return ((TriggerStringData)obj).value;
		}
		else if(obj.getDataID()==TriggerFuncData.dataID)
		{
			return getStringFuncValue((TriggerFuncData)obj,arg);
		}

		throwError("不支持的数据类型,String",obj.getDataClassName());
		return "";
	}
	
	/** 获取List值 */
	public SList<Object> getList(TriggerObjData obj,TriggerArg arg)
	{
		return (SList<Object>)getObj(obj,arg);
	}
	
	/** 获取Map值 */
	public SMap<Object,Object> getMap(TriggerObjData obj,TriggerArg arg)
	{
		return (SMap<Object,Object>)getObj(obj,arg);
	}
	
	/** 获取Set值 */
	public SSet<Object> getSet(TriggerObjData obj,TriggerArg arg)
	{
		return (SSet<Object>)getObj(obj,arg);
	}

	/** 执行action行为 */
	public void preActionFunc(TriggerFuncData func,TriggerActionRunner runner)
	{
		if(CommonSetting.needTriggerLog)
			log("runAction:",BaseC.trigger.getFuncName(func.id),func.args);
		
		if(!doAsyncActionFunc(func,runner.arg))
		{
			doActionFunc(func,runner.arg);
			runNextAction(runner);
		}
	}
	
	//--create--//
	
	/** 创建事件(只创建) */
	protected TriggerEvent toCreateEvent()
	{
		return new TriggerEvent();
	}
	
	/** 创建实例(只创建) */
	protected TriggerInstance toCreateInstance()
	{
		return new TriggerInstance();
	}
	
	/** 创建action运行器(只创建) */
	protected TriggerActionRunner toCreateActionRunner()
	{
		return new TriggerActionRunner();
	}
	
	/** 初始化当前event环境 */
	protected void initEvent(TriggerEvent event)
	{
	
	}
	
	/** 初始化当前instance */
	protected void initInstance(TriggerInstance instance)
	{
	
	}
	
	//--regist--//
	
	protected Object toGetFuncValue(int returnType,TriggerFuncData func,TriggerArg arg)
	{
		switch(returnType)
		{
			case STriggerObjectType.Object:
				return getObjectFuncValue(func,arg);
			case STriggerObjectType.Boolean:
				return getBooleanFuncValue(func,arg);
			case STriggerObjectType.Int:
				return getIntFuncValue(func,arg);
			case STriggerObjectType.Float:
				return getFloatFuncValue(func,arg);
			case STriggerObjectType.Long:
				return getLongFuncValue(func,arg);
			case STriggerObjectType.String:
				return getStringFuncValue(func,arg);
			//case STriggerObjectType.List:
			//	return getListFuncValue(func,arg);
			//case STriggerObjectType.Map:
			//	return getMapFuncValue(func,arg);
			//case STriggerObjectType.Set:
			//	return getSetFuncValue(func,arg);
			default:
			{
				return getObjectFuncValue(func,arg);
			}
		}
	}
	
	/** 执行异步方法 */
	protected final boolean doAsyncActionFunc(TriggerFuncData func,TriggerArg arg)
	{
		boolean re=false;
		
		try
		{
			re=toDoAsyncActionFunc(func,arg);
		}
		catch(Exception e)
		{
			errorLog("doAsyncActionFunc出错",e);
		}
		
		return re;
	}
	
	/** 执行异步方法 */
	protected boolean toDoAsyncActionFunc(TriggerFuncData func,TriggerArg arg)
	{
		switch(func.id)
		{
			case TriggerFunctionType.If:
			{
				if(getBoolean(func.args[0],arg))
				{
					TriggerFuncListData listFunc=(TriggerFuncListData)func.args[1];
					
					runChildActions(TriggerChildRunnerType.If,listFunc.funcList,arg.runner);
				}
				else
				{
					if(func.args.length>2)
					{
						TriggerFuncListData listFunc=(TriggerFuncListData)func.args[2];
						
						if(listFunc.funcList.length>0)
						{
							runChildActions(TriggerChildRunnerType.If,listFunc.funcList,arg.runner);
						}
						else
						{
							//直接下一个
							runNextAction(arg.runner);
						}
					}
					else
					{
						//直接下一个
						runNextAction(arg.runner);
					}
				}
			}
				break;
			case TriggerFunctionType.While:
			{
				if(getBoolean(func.args[0],arg))
				{
					TriggerFuncListData listFunc=(TriggerFuncListData)func.args[1];
					
					runChildActions(TriggerChildRunnerType.While,listFunc.funcList,arg.runner);
				}
				else
				{
					//直接下一个
					runNextAction(arg.runner);
				}
			}
				break;
			case TriggerFunctionType.ForLoop:
			{
				int loop=getInt(func.args[0],arg.runner.arg);
				
				if(loop<=0)
				{
					//直接下一个
					runNextAction(arg.runner);
				}
				else
				{
					arg.runner.loopCount=loop;
					arg.runner.loop=0;
					
					TriggerFuncListData listFunc=(TriggerFuncListData)func.args[1];
					
					runChildActions(TriggerChildRunnerType.ForLoop,listFunc.funcList,arg.runner);
				}
			}
				break;
			case TriggerFunctionType.RunTrigger:
			{
				runTrigger(getInt(func.args[0],arg),arg.runner);
			}
				break;
			case TriggerFunctionType.RunTriggerAbs:
			{
				runTriggerAbs(getInt(func.args[0],arg),arg.runner);
			}
				break;
			case TriggerFunctionType.BreakTrigger:
			{
				//直接完成根节点
				completeCurrentAction(arg.runner.root);
			}
				break;
			case TriggerFunctionType.BreakLoop:
			{
				if(arg.runner.isInLoop())
				{
					TriggerActionRunner parent=arg.runner.parent;
					if(parent==null)
					{
						throwError("不该找不到父");
						return false;
					}
					
					disposeActionRunner(arg.runner);
					parent.arg.runner=parent;
					runNextAction(parent);
				}
				else
				{
					warnLog("breakLoop时，不是循环");
					runNextAction(arg.runner);
				}
			}
				break;
			case TriggerFunctionType.ContinueLoop:
			{
				if(arg.runner.isInLoop())
				{
					completeCurrentAction(arg.runner);
				}
				else
				{
					warnLog("continueLoop时，不是循环");
					runNextAction(arg.runner);
				}
			}
				break;
			case TriggerFunctionType.Wait:
			{
				arg.runner.timerType=TriggerActionTimerType.Wait;
				arg.runner.currentTime=arg.runner.timeMax=getInt(func.args[0],arg);
				addTimeRunner(arg.runner);
			}
				break;
			case TriggerFunctionType.WaitUtil:
			{
				//判定成功直接过
				if(getBoolean(arg.runner.getCurrentAction().args[0],arg))
				{
					runNextAction(arg.runner);
				}
				else
				{
					//加入等待
					arg.runner.timerType=TriggerActionTimerType.WaitUtil;
					arg.runner.currentTime=arg.runner.timeMax=getInt(func.args[1],arg);
					addTimeRunner(arg.runner);
				}
			}
				break;
			case TriggerFunctionType.ForeachList:
			{
				SList<Object> list=getList(func.args[0],arg);
				
				if(list.isEmpty())
				{
					//直接下一个
					runNextAction(arg.runner);
				}
				else
				{
					arg.runner.foreachList=list;
					arg.runner.loop=0;
					
					TriggerFuncListData listFunc=(TriggerFuncListData)func.args[1];
					runChildActions(TriggerChildRunnerType.ForEachList,listFunc.funcList,arg.runner);
				}
			}
				break;
			default:
			{
				return false;
			}
		}
		
		return true;
	}
	
	/** 执行同步方法 */
	protected final void doActionFunc(TriggerFuncData func,TriggerArg arg)
	{
		try
		{
			toDoActionFunc(func,arg);
		}
		catch(Exception e)
		{
			errorLog("doActionFunc出错",e);
		}
	}
	
	/** 执行同步方法 */
	protected void toDoActionFunc(TriggerFuncData func,TriggerArg arg)
	{
		TriggerFuncEntry entry=_funcMaker.get(func.id);
		if(entry==null)
		{
			throwError("未找到的方法类型,void",func.id);
			return;
		}
		
		if(!entry.doEver(this,func,arg))
		{
			throwError("未找到的方法类型,void",func.id);
			return;
		}
	}
	
	/** 获取Object方法返回值 */
	protected final Object getObjectFuncValue(TriggerFuncData func,TriggerArg arg)
	{
		Object re=null;
		
		try
		{
			re=toGetObjectFuncValue(func,arg);
		}
		catch(Exception e)
		{
			errorLog("getObjectFuncValue出错",e);
		}
		
		return re;
	}
	
	/** 获取Object方法返回值 */
	protected Object toGetObjectFuncValue(TriggerFuncData func,TriggerArg arg)
	{
		TriggerFuncEntry entry=_funcMaker.get(func.id);
		ObjectTriggerFunc f;
		if(entry==null || (f=entry.objectFunc)==null)
		{
			throwError("未找到的方法类型,Object",func.id);
			return null;
		}
		
		return f.apply(this,func,arg);
	}
	
	/** 获取boolean方法返回值 */
	protected final boolean getBooleanFuncValue(TriggerFuncData func,TriggerArg arg)
	{
		boolean re=false;
		
		try
		{
			re=toGetBooleanFuncValue(func,arg);
		}
		catch(Exception e)
		{
			errorLog("getBooleanFuncValue出错",e);
		}
		
		return re;
	}
	
	/** 获取boolean方法返回值 */
	protected boolean toGetBooleanFuncValue(TriggerFuncData func,TriggerArg arg)
	{
		switch(func.id)
		{
			case TriggerFunctionType.And:
			{
				if(!getBoolean(func.args[0],arg))
					return false;
				
				return getBoolean(func.args[1],arg);
			}
			case TriggerFunctionType.And2:
			{
				if(!getBoolean(func.args[0],arg))
					return false;
				
				if(!getBoolean(func.args[1],arg))
					return false;
				
				return getBoolean(func.args[2],arg);
			}
			case TriggerFunctionType.Or:
			{
				if(getBoolean(func.args[0],arg))
					return true;
				
				return getBoolean(func.args[1],arg);
			}
			case TriggerFunctionType.Or2:
			{
				if(getBoolean(func.args[0],arg))
					return true;
				
				if(getBoolean(func.args[1],arg))
					return true;
				
				return getBoolean(func.args[2],arg);
			}
		}
		
		TriggerFuncEntry entry=_funcMaker.get(func.id);
		BooleanTriggerFunc f;
		if(entry==null || (f=entry.boolFunc)==null)
		{
			throwError("未找到的方法类型,Boolean",func.id);
			return false;
		}
		
		return f.apply(this,func,arg);
	}
	
	/** 获取int值 */
	protected final int getIntFuncValue(TriggerFuncData func,TriggerArg arg)
	{
		int re=0;
		
		try
		{
			re=toGetIntFuncValue(func,arg);
		}
		catch(Exception e)
		{
			errorLog("getIntFuncValue出错",e);
		}
		
		return re;
	}
	
	/** 获取int值 */
	protected int toGetIntFuncValue(TriggerFuncData func,TriggerArg arg)
	{
		TriggerFuncEntry entry=_funcMaker.get(func.id);
		IntTriggerFunc f;
		if(entry==null || (f=entry.intFunc)==null)
		{
			throwError("未找到的方法类型,Int",func.id);
			return 0;
		}
		
		return f.apply(this,func,arg);
	}
	
	/** 获取float值 */
	protected final float getFloatFuncValue(TriggerFuncData func,TriggerArg arg)
	{
		float re=0f;
		
		try
		{
			re=toGetFloatFuncValue(func,arg);
		}
		catch(Exception e)
		{
			errorLog("getFloatFuncValue出错",e);
		}
		
		return re;
	}
	
	/** 获取float值 */
	protected float toGetFloatFuncValue(TriggerFuncData func,TriggerArg arg)
	{
		TriggerFuncEntry entry=_funcMaker.get(func.id);
		FloatTriggerFunc f;
		if(entry==null || (f=entry.floatFunc)==null)
		{
			throwError("未找到的方法类型,Float",func.id);
			return 0;
		}
		
		return f.apply(this,func,arg);
	}
	
	/** 获取long值 */
	protected final long getLongFuncValue(TriggerFuncData func,TriggerArg arg)
	{
		long re=0L;
		
		try
		{
			re=toGetLongFuncValue(func,arg);
		}
		catch(Exception e)
		{
			errorLog("getLongFuncValue出错",e);
		}
		
		return re;
	}
	
	/** 获取long值 */
	protected long toGetLongFuncValue(TriggerFuncData func,TriggerArg arg)
	{
		TriggerFuncEntry entry=_funcMaker.get(func.id);
		LongTriggerFunc f;
		if(entry==null || (f=entry.longFunc)==null)
		{
			throwError("未找到的方法类型,Float",func.id);
			return 0;
		}
		
		return f.apply(this,func,arg);
	}
	
	/** 获取string值 */
	protected final String getStringFuncValue(TriggerFuncData func,TriggerArg arg)
	{
		String re="";
		
		try
		{
			re=toGetStringFuncValue(func,arg);
		}
		catch(Exception e)
		{
			errorLog("getStringFuncValue出错",e);
		}
		
		return re;
	}
	
	/** 获取string值 */
	protected String toGetStringFuncValue(TriggerFuncData func,TriggerArg arg)
	{
		TriggerFuncEntry entry=_funcMaker.get(func.id);
		StringTriggerFunc f;
		if(entry==null || (f=entry.stringFunc)==null)
		{
			throwError("未找到的方法类型,Float",func.id);
			return "";
		}
		
		return f.apply(this,func,arg);
	}
	
	
	//--functions--//
	
	
	
	/** 输出文字 */
	public void print(String str)
	{
		log(str);
	}
	
	//--boolean--//
	
	
	//--int--//
	
	//--long--//
}
