using System;
using ShineEngine;

/// <summary>
/// 动作执行器
/// </summary>
public class TriggerActionRunner:IPoolObject
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

	public SList<object> foreachList=null;

	/** 计时器类型 */
	public int timerType;
	/** 时间上限 */
	public int timeMax;
	/** 当前时间 */
	public int currentTime;

	/** 局部变量字典 */
	private SMap<string,object> _localVarDic;

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
		this.root=runner.root;
		this.parent=runner;
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
	public bool isInLoop()
	{
		return childType==TriggerChildRunnerType.ForLoop || childType==TriggerChildRunnerType.While;
	}

	/** 获取局部变量 */
	public object getLocalVar(string str)
	{
		SMap<string,object> dic;
		if((dic=root._localVarDic)==null)
			return null;

		return dic.get(str);
	}

	/** 设置局部变量 */
	public void setLocalVar(string str,object value)
	{
		SMap<string,object> dic;
		if((dic=root._localVarDic)==null)
		{
			root._localVarDic=dic=new SMap<string,object>();
		}

		dic.put(str,value);
	}
}