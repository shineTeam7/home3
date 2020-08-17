package com.home.commonBase.trigger;

import com.home.commonBase.constlist.system.TriggerChildRunnerType;
import com.home.shine.data.trigger.TriggerFuncData;
import com.home.shine.support.collection.SList;
import com.home.shine.support.collection.SMap;
import com.home.shine.support.pool.IPoolObject;

/** 动作执行器 */
public class TriggerActionRunner implements IPoolObject
{
	/** 参数数据 */
	public TriggerArg arg;
	/** 动作序号 */
	public int actionIndex=-1;
	/** 动作组 */
	public TriggerFuncData[] actions;
	/** 根项 */
	public TriggerActionRunner root=null;
	/** 父项 */
	public TriggerActionRunner parent=null;
	/** 子项类型(循环类型) */
	public int childType=0;
	
	/** 循环次数 */
	public int loopCount=-1;
	/** 当前循环值 */
	public int loop=0;
	
	public SList<Object> foreachList=null;
	
	/** 计时器类型 */
	public int timerType;
	/** 时间上限 */
	public int timeMax;
	/** 当前时间 */
	public int currentTime;
	
	/** 局部变量字典 */
	private SMap<String,Object> _localVarDic;
	
	@Override
	public void clear()
	{
		arg=null;
		actionIndex=-1;
		actions=null;
		root=null;
		parent=null;
		childType=0;
		
		loopCount=-1;
		loop=0;
		foreachList=null;
		
		timerType=0;
		timeMax=0;
		currentTime=0;
		
		if(_localVarDic!=null)
			_localVarDic.clear();
	}
	
	/** 初始化(通过trigger调用) */
	public void initByTrigger(TriggerArg arg)
	{
		this.arg=arg;
		this.root=this;
		actionIndex=-1;
		actions=arg.instance.config.actions;
		
		arg.runner=this;
	}
	
	/** 初始化(通过trigger调用) */
	public void initByAction(int type,TriggerFuncData[] list,TriggerActionRunner runner)
	{
		this.arg=runner.arg;
		this.parent=runner;
		this.root=runner.root;
		this.childType=type;
		actionIndex=-1;
		actions=list;
		
		arg.runner=this;
	}
	
	/** 获取当前行为 */
	public TriggerFuncData getCurrentAction()
	{
		return actions[actionIndex];
	}
	
	/** 是否在循环中(forLoop,while都算) */
	public boolean isInLoop()
	{
		switch(childType)
		{
			case TriggerChildRunnerType.While:
			case TriggerChildRunnerType.ForLoop:
			case TriggerChildRunnerType.ForEachList:
				return true;
		}
		
		return false;
	}
	
	///** 执行下一个 */
	//public void next()
	//{
	//	arg.executor.runNextAction(this);
	//}
	
	/** 获取局部变量 */
	public Object getLocalVar(String str)
	{
		SMap<String,Object> dic;
		if((dic=root._localVarDic)==null)
			return null;
		
		return dic.get(str);
	}
	
	/** 设置局部变量 */
	public void setLocalVar(String str,Object value)
	{
		SMap<String,Object> dic;
		if((dic=root._localVarDic)==null)
		{
			root._localVarDic=dic=new SMap<>();
		}
		
		dic.put(str,value);
	}
}
