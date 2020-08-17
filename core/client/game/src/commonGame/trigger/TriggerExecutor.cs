using System;
using System.Text;
using ShineEngine;

/// <summary>
/// trigger执行器
/// </summary>
public class TriggerExecutor:LogicEntity
{
	private int _groupType;
	private int _groupID;

	protected TriggerFuncMaker _funcMaker;

	/** trigger配置总组 */
	private IntObjectMap<TriggerConfigData> _configDic;

	/** 事件注册池 */
	private ObjectPool<TriggerEventRegistData> _eventRegistDataPool=new ObjectPool<TriggerEventRegistData>(()=>new TriggerEventRegistData());
	/** 参数池 */
	private ObjectPool<TriggerArg> _argPool=new ObjectPool<TriggerArg>(()=>new TriggerArg());
	/** 事件池 */
	private ObjectPool<TriggerEvent> _eventPool;
	/** 实例池 */
	private ObjectPool<TriggerInstance> _instancePool;
	/** 实例池 */
	private ObjectPool<TriggerIntervalData> _intervalPool=new ObjectPool<TriggerIntervalData>(()=>new TriggerIntervalData());
	/** runner池 */
	private ObjectPool<TriggerActionRunner> _actionRunnerPool;

	/** trigger开启组 */
	private IntObjectMap<TriggerConfigData> _openDic=new IntObjectMap<TriggerConfigData>();
	/** 激活事件组 */
	private IntObjectMap<SList<TriggerEventRegistData>> _eventActiveDic=new IntObjectMap<SList<TriggerEventRegistData>>();
	/** trigger实例组 */
	private IntObjectMap<TriggerInstance> _instanceDic=new IntObjectMap<TriggerInstance>();
	/** 需要计时器检查的instance组 */
	private SSet<TriggerIntervalData> _timerIntervalDic=new SSet<TriggerIntervalData>();
	/** 需要计时器检查的runner组 */
	private SSet<TriggerActionRunner> _timerRunnerDic=new SSet<TriggerActionRunner>();

	/** strKey变量字典 */
	public SMap<String,Object> sVarDic=new SMap<String,Object>();

	private static CustomComparer<TriggerEventRegistData> _comparer=new CustomComparer<TriggerEventRegistData>();

	//temp

	private SList<TriggerEventRegistData> _tempEventList=new SList<TriggerEventRegistData>();

	public TriggerExecutor()
	{

	}

	//构造
	public void construct()
	{
		//比较方法
		_comparer.setCompare(triggerEventRegistDataCompare);

		_eventPool=new ObjectPool<TriggerEvent>(toCreateEvent);
		_instancePool=new ObjectPool<TriggerInstance>(toCreateInstance);
		_actionRunnerPool=new ObjectPool<TriggerActionRunner>(toCreateActionRunner);

		_eventRegistDataPool.setEnable(CommonSetting.triggerUsePool);
		_argPool.setEnable(CommonSetting.triggerUsePool);
		_eventPool.setEnable(CommonSetting.triggerUsePool);
		_instancePool.setEnable(CommonSetting.triggerUsePool);
		_intervalPool.setEnable(CommonSetting.triggerUsePool);
		_actionRunnerPool.setEnable(CommonSetting.triggerUsePool);

		_funcMaker=GameC.trigger.getFuncMaker();
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

		TriggerConfigData[] values;
		TriggerConfigData v;

		for(int i=(values=_configDic.getValues()).Length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				if(v.isOpen)
				{
					openTrigger(v.id);
				}
			}
		}

		onInit();

		//刚初始化好
		triggerEvent(TriggerEventType.OnInit);
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

		foreach(var list in _eventActiveDic)
		{
			TriggerEventRegistData[] values1=list.getValues();

			for(int i=0,len=list.size();i<len;++i)
			{
				_eventRegistDataPool.back(values1[i]);
				values1[i]=null;
			}

			list.justClearSize();
		}

		int[] keys=_instanceDic.getKeys();
		TriggerInstance[] values=_instanceDic.getValues();
		int fv=_instanceDic.getFreeValue();
		TriggerInstance v;

		for(int i=keys.Length-1;i>=0;--i)
		{
			if((keys[i])!=fv)
			{
				v=values[i];
				_instancePool.back(v);
				keys[i]=fv;
				values[i]=null;
			}
		}

		_instanceDic.justClearSize();

		TriggerActionRunner[] keys2=_timerRunnerDic.getKeys();
		TriggerActionRunner k2;

		for(int i2=keys2.Length-1;i2>=0;--i2)
		{
			if((k2=keys2[i2])!=null)
			{
				_argPool.back(k2.arg);
				disposeActionRunner(k2);
				keys2[i2]=null;
			}
		}

		_timerRunnerDic.justClearSize();

		TriggerIntervalData[] keys3=_timerIntervalDic.getKeys();
		TriggerIntervalData k3;

		for(int i3=keys3.Length-1;i3>=0;--i3)
		{
			if((k3=keys3[i3])!=null)
			{
				_intervalPool.back(k3);
				keys3[i3]=null;
			}
		}

		_timerIntervalDic.justClearSize();
	}

	/** 每帧间隔 */
	public void onFrame(int delay)
	{
		if(_configDic==null)
			return;

		if(!_timerRunnerDic.isEmpty())
		{
			foreach(TriggerActionRunner k in _timerRunnerDic)
			{
				tickRunnerTimer(k,delay);
			}
		}

		if(!_timerIntervalDic.isEmpty())
		{
			foreach(TriggerIntervalData k in _timerIntervalDic)
			{
				if(k.current>0 && (k.current-=delay)<=0)
				{
					k.current=k.delay;//不累计

					runTrigger(k.instance,null);
				}
			}
		}
	}

	/** 写描述信息 */
	public override void writeInfo(StringBuilder sb)
	{
		sb.Append("groupType:");
		sb.Append(_groupType);
		sb.Append(" groupID:");
		sb.Append(_groupID);
	}

	/** 发生事件 */
	public void triggerEvent(int type)
	{
		toTriggerEvent(type,null);
	}

	/** 发生事件 */
	public void triggerEvent(int type,params object[] args)
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

		// if(CommonSetting.needTriggerLog)
		// 	log("收到trigger消息",GameC.trigger.funcNameDic[type],args);

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

		bool useTemp=false;

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
		foreach(TriggerFuncData condition in arg.instance.config.conditions)
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
	protected bool checkEventMatch(TriggerFuncData data,TriggerArg arg)
	{
		TriggerObjData[] tArgs;

		if((tArgs=data.args).Length==0)
			return true;

		object[] args=arg.evt.args;

		object value;

		try
		{
			for(int i=0;i<tArgs.Length;i++)
			{
				value=getObj(tArgs[i],arg);

				if(!(args!=null && args.Length>i && args[i].Equals(value)))
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
		if((++runner.actionIndex)>=runner.actions.Length)
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

			bool needBack;

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
		SList<TriggerEventRegistData> list=_eventActiveDic.computeIfAbsent(data.id,k=>new SList<TriggerEventRegistData>());

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
			int idx=Array.BinarySearch(values,0,list.size(),eData,_comparer);

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

		for(int i=0;i<events.Length;i++)
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

		for(int i=0;i<events.Length;i++)
		{
			evt=events[i];

			if(evt.id==TriggerEventType.OnInterval)
			{
				instance=getInstance(id);

				TriggerIntervalData[] keys1=instance.intervalDic.getKeys();
				TriggerIntervalData k1;

				for(int i1=keys1.Length-1;i1>=0;--i1)
				{
					if((k1=keys1[i1])!=null)
					{
						_timerIntervalDic.remove(k1);
						keys1[i1]=null;

						_intervalPool.back(k1);
					}
				}

				instance.intervalDic.justClearSize();
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
	public object getObj(TriggerObjData obj,TriggerArg arg)
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
	protected object getFuncValue(TriggerFuncData func,TriggerArg arg)
	{
		int returnType;

		if((returnType=GameC.trigger.getFuncReturnType(func.id))==0)
		{
			throwError("未注册trigger方法的返回值",func.id);
			return null;
		}

		if(returnType==STriggerObjectType.Void)
			return null;

		return toGetFuncValue(returnType,func,arg);
	}

	/** 获取boolean值 */
	public bool getBoolean(TriggerObjData obj,TriggerArg arg)
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
	public SList<object> getList(TriggerObjData obj,TriggerArg arg)
	{
		return (SList<object>)getObj(obj,arg);
	}

	/** 获取Map值 */
	public SMap<object,object> getMap(TriggerObjData obj,TriggerArg arg)
	{
		return (SMap<object,object>)getObj(obj,arg);
	}

	/** 获取Set值 */
	public SSet<object> getSet(TriggerObjData obj,TriggerArg arg)
	{
		return (SSet<object>)getObj(obj,arg);
	}

	/** 执行action行为 */
	public void preActionFunc(TriggerFuncData func,TriggerActionRunner runner)
	{
		if(CommonSetting.needTriggerLog)
			log("runAction:",GameC.trigger.getFuncName(func.id),func.args);

		if(!doAsyncActionFunc(func,runner.arg))
		{
			doActionFunc(func,runner.arg);
			runNextAction(runner);
		}
	}

	//--create--//

	/** 创建事件(只创建) */
	protected virtual TriggerEvent toCreateEvent()
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
	protected virtual void initEvent(TriggerEvent evt)
	{

	}

	/** 初始化当前instance */
	protected void initInstance(TriggerInstance instance)
	{

	}

	//--regist--//

	protected virtual object toGetFuncValue(int returnType,TriggerFuncData func,TriggerArg arg)
	{
		switch(returnType)
		{
			case STriggerObjectType.Object:
			{
				return getObjectFuncValue(func,arg);
			}
			case STriggerObjectType.Boolean:
			{
				return getBooleanFuncValue(func,arg);
			}
			case STriggerObjectType.Int:
			{
				return getIntFuncValue(func,arg);
			}
			case STriggerObjectType.Float:
			{
				return getFloatFuncValue(func,arg);
			}
			case STriggerObjectType.Long:
			{
				return getLongFuncValue(func,arg);
			}
			case STriggerObjectType.String:
			{
				return getStringFuncValue(func,arg);
			}
			default:
			{
				return getObjectFuncValue(func,arg);
			}
		}
	}

	/** 执行异步方法 */
	protected bool doAsyncActionFunc(TriggerFuncData func,TriggerArg arg)
	{
		bool re=false;

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
	protected virtual bool toDoAsyncActionFunc(TriggerFuncData func,TriggerArg arg)
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
					if(func.args.Length>2)
					{
						TriggerFuncListData listFunc=(TriggerFuncListData)func.args[2];

						if(listFunc.funcList.Length>0)
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
	protected void doActionFunc(TriggerFuncData func,TriggerArg arg)
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
	protected virtual void toDoActionFunc(TriggerFuncData func,TriggerArg arg)
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
	protected object getObjectFuncValue(TriggerFuncData func,TriggerArg arg)
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
	protected virtual object toGetObjectFuncValue(TriggerFuncData func,TriggerArg arg)
	{
		TriggerFuncEntry entry=_funcMaker.get(func.id);
		Func<TriggerExecutor,TriggerFuncData,TriggerArg,object> f;
		if(entry==null || (f=entry.objectFunc)==null)
		{
			throwError("未找到的方法类型,Object",func.id);
			return null;
		}

		return f(this,func,arg);
	}

	/** 获取boolean方法返回值 */
	protected bool getBooleanFuncValue(TriggerFuncData func,TriggerArg arg)
	{
		bool re=false;

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
	protected virtual bool toGetBooleanFuncValue(TriggerFuncData func,TriggerArg arg)
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
		Func<TriggerExecutor,TriggerFuncData,TriggerArg,bool> f;
		if(entry==null || (f=entry.boolFunc)==null)
		{
			throwError("未找到的方法类型,Boolean",func.id);
			return false;
		}

		return f(this,func,arg);
	}

	/** 获取int值 */
	protected int getIntFuncValue(TriggerFuncData func,TriggerArg arg)
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
	protected virtual int toGetIntFuncValue(TriggerFuncData func,TriggerArg arg)
	{
		TriggerFuncEntry entry=_funcMaker.get(func.id);
		Func<TriggerExecutor,TriggerFuncData,TriggerArg,int> f;
		if(entry==null || (f=entry.intFunc)==null)
		{
			throwError("未找到的方法类型,Int",func.id);
			return 0;
		}

		return f(this,func,arg);
	}

	/** 获取float值 */
	protected float getFloatFuncValue(TriggerFuncData func,TriggerArg arg)
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
	protected virtual float toGetFloatFuncValue(TriggerFuncData func,TriggerArg arg)
	{
		TriggerFuncEntry entry=_funcMaker.get(func.id);
		Func<TriggerExecutor,TriggerFuncData,TriggerArg,float> f;
		if(entry==null || (f=entry.floatFunc)==null)
		{
			throwError("未找到的方法类型,Float",func.id);
			return 0;
		}

		return f(this,func,arg);
	}

	/** 获取long值 */
	protected long getLongFuncValue(TriggerFuncData func,TriggerArg arg)
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
	protected virtual long toGetLongFuncValue(TriggerFuncData func,TriggerArg arg)
	{
		TriggerFuncEntry entry=_funcMaker.get(func.id);
		Func<TriggerExecutor,TriggerFuncData,TriggerArg,long> f;
		if(entry==null || (f=entry.longFunc)==null)
		{
			throwError("未找到的方法类型,Long",func.id);
			return 0;
		}

		return f(this,func,arg);
	}

	/** 获取string值 */
	protected string getStringFuncValue(TriggerFuncData func,TriggerArg arg)
	{
		string re="";

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
	protected virtual string toGetStringFuncValue(TriggerFuncData func,TriggerArg arg)
	{
		TriggerFuncEntry entry=_funcMaker.get(func.id);
		Func<TriggerExecutor,TriggerFuncData,TriggerArg,string> f;
		if(entry==null || (f=entry.stringFunc)==null)
		{
			throwError("未找到的方法类型,String",func.id);
			return "";
		}

		return f(this,func,arg);
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