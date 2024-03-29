using ShineEngine;
using System;

/// <summary>
/// (generated by shine)
/// </summary>
public class TriggerMethod
{
	/// <summary>
	/// 如果
	/// </summary>
	public void func_If(TriggerExecutor e,TriggerArg a,bool arg1,object arg2,object arg3)
	{
		Ctrl.warnLog("function func_If,need implement");
	}
	
	/// <summary>
	/// while循环
	/// </summary>
	public void func_While(TriggerExecutor e,TriggerArg a,bool arg1,object arg2)
	{
		Ctrl.warnLog("function func_While,need implement");
	}
	
	/// <summary>
	/// 获取字符串key自定义对象
	/// </summary>
	public object func_GetSVar(TriggerExecutor e,TriggerArg a,string key)
	{
		return e.sVarDic.get(key);
	}
	
	/// <summary>
	/// 获取局部字符串key自定义对象
	/// </summary>
	public object func_GetLocalVar(TriggerExecutor e,TriggerArg a,string key)
	{
		return a.runner.getLocalVar(key);
	}
	
	/// <summary>
	/// 非
	/// </summary>
	public bool func_Not(TriggerExecutor e,TriggerArg a,bool arg)
	{
		return !arg;
	}
	
	/// <summary>
	/// 并且
	/// </summary>
	public bool func_And(TriggerExecutor e,TriggerArg a,bool arg1,bool arg2)
	{
		return arg1 && arg2;
	}
	
	/// <summary>
	/// 并且2
	/// </summary>
	public bool func_And2(TriggerExecutor e,TriggerArg a,bool arg1,bool arg2,bool arg3)
	{
		return arg1 && arg2 && arg3;
	}
	
	/// <summary>
	/// 或者
	/// </summary>
	public bool func_Or(TriggerExecutor e,TriggerArg a,bool arg1,bool arg2)
	{
		return arg1 || arg2;
	}
	
	/// <summary>
	/// 或者2(3元)
	/// </summary>
	public bool func_Or2(TriggerExecutor e,TriggerArg a,bool arg1,bool arg2,bool arg3)
	{
		return arg1 || arg2 || arg3;
	}
	
	/// <summary>
	/// 判断相等
	/// </summary>
	public bool func_Equals(TriggerExecutor e,TriggerArg a,object arg1,object arg2)
	{
		return ObjectUtils.equals(arg1,arg2);
	}
	
	/// <summary>
	/// 判断int相等
	/// </summary>
	public bool func_EqualsInt(TriggerExecutor e,TriggerArg a,int arg1,int arg2)
	{
		return arg1==arg2;
	}
	
	/// <summary>
	/// 判断float相等
	/// </summary>
	public bool func_EqualsFloat(TriggerExecutor e,TriggerArg a,float arg1,float arg2)
	{
		return MathUtils.floatEquals(arg1,arg2);
	}
	
	/// <summary>
	/// 判断long相等
	/// </summary>
	public bool func_EqualsLong(TriggerExecutor e,TriggerArg a,long arg1,long arg2)
	{
		return arg1==arg2;
	}
	
	/// <summary>
	/// 判断string相等
	/// </summary>
	public bool func_EqualsString(TriggerExecutor e,TriggerArg a,string arg1,string arg2)
	{
		return arg1!=null && arg1.Equals(arg2);
	}
	
	/// <summary>
	/// 比较int,type:CompareType
	/// </summary>
	public bool func_CompareInt(TriggerExecutor e,TriggerArg a,int arg1,int arg2,int type)
	{
		switch(type)
		{
			case CompareType.Equal:
				return arg1==arg2;
			case CompareType.NotEqual:
				return arg1!=arg2;
			case CompareType.GreaterThan:
				return arg1>arg2;
			case CompareType.GreaterThanOrEqual:
				return arg1>=arg2;
			case CompareType.LessThan:
				return arg1<arg2;
			case CompareType.LessThanOrEqual:
				return arg1<=arg2;
		}

		return false;
	}
	
	/// <summary>
	/// 比较Float,type:CompareType
	/// </summary>
	public bool func_CompareFloat(TriggerExecutor e,TriggerArg a,float arg1,float arg2,int type)
	{
		switch(type)
		{
			case CompareType.Equal:
				return MathUtils.floatEquals(arg1,arg2);
			case CompareType.NotEqual:
				return !MathUtils.floatEquals(arg1,arg2);
			case CompareType.GreaterThan:
				return arg1>arg2;
			case CompareType.GreaterThanOrEqual:
				return arg1>=arg2;
			case CompareType.LessThan:
				return arg1<arg2;
			case CompareType.LessThanOrEqual:
				return arg1<=arg2;
		}

		return false;
	}
	
	/// <summary>
	/// 比较long,type:CompareType
	/// </summary>
	public bool func_CompareLong(TriggerExecutor e,TriggerArg a,long arg1,long arg2,int type)
	{
		switch(type)
		{
			case CompareType.Equal:
				return arg1==arg2;
			case CompareType.NotEqual:
				return arg1!=arg2;
			case CompareType.GreaterThan:
				return arg1>arg2;
			case CompareType.GreaterThanOrEqual:
				return arg1>=arg2;
			case CompareType.LessThan:
				return arg1<arg2;
			case CompareType.LessThanOrEqual:
				return arg1<=arg2;
		}

		return false;
	}
	
	/// <summary>
	/// 大于int
	/// </summary>
	public bool func_GreaterThanInt(TriggerExecutor e,TriggerArg a,int arg1,int arg2)
	{
		return arg1>arg2;
	}
	
	/// <summary>
	/// 大于等于int
	/// </summary>
	public bool func_GreaterThanOrEqualInt(TriggerExecutor e,TriggerArg a,int arg1,int arg2)
	{
		return arg1>=arg2;
	}
	
	/// <summary>
	/// 小于int
	/// </summary>
	public bool func_LessThanInt(TriggerExecutor e,TriggerArg a,int arg1,int arg2)
	{
		return arg1<arg2;
	}
	
	/// <summary>
	/// 小于等于int
	/// </summary>
	public bool func_LessThanOrEqualInt(TriggerExecutor e,TriggerArg a,int arg1,int arg2)
	{
		return arg1<=arg2;
	}
	
	/// <summary>
	/// 大于float
	/// </summary>
	public bool func_GreaterThanFloat(TriggerExecutor e,TriggerArg a,float arg1,float arg2)
	{
		return arg1>arg2;
	}
	
	/// <summary>
	/// 大于等于float
	/// </summary>
	public bool func_GreaterThanOrEqualFloat(TriggerExecutor e,TriggerArg a,float arg1,float arg2)
	{
		return arg1>=arg2;
	}
	
	/// <summary>
	/// 小于float
	/// </summary>
	public bool func_LessThanFloat(TriggerExecutor e,TriggerArg a,float arg1,float arg2)
	{
		return arg1<arg2;
	}
	
	/// <summary>
	/// 小于等于float
	/// </summary>
	public bool func_LessThanOrEqualFloat(TriggerExecutor e,TriggerArg a,float arg1,float arg2)
	{
		return arg1<=arg2;
	}
	
	/// <summary>
	/// 大于long
	/// </summary>
	public bool func_GreaterThanLong(TriggerExecutor e,TriggerArg a,long arg1,long arg2)
	{
		return arg1>arg2;
	}
	
	/// <summary>
	/// 大于等于long
	/// </summary>
	public bool func_GreaterThanOrEqualLong(TriggerExecutor e,TriggerArg a,long arg1,long arg2)
	{
		return arg1>=arg2;
	}
	
	/// <summary>
	/// 小于long
	/// </summary>
	public bool func_LessThanLong(TriggerExecutor e,TriggerArg a,long arg1,long arg2)
	{
		return arg1<arg2;
	}
	
	/// <summary>
	/// 小于等于long
	/// </summary>
	public bool func_LessThanOrEqualLong(TriggerExecutor e,TriggerArg a,long arg1,long arg2)
	{
		return arg1<=arg2;
	}
	
	/// <summary>
	/// 获取字符串key boolean
	/// </summary>
	public bool func_GetSBoolean(TriggerExecutor e,TriggerArg a,string key)
	{
		object obj=e.sVarDic.get(key);
		return obj!=null ? (bool)obj : false;
	}
	
	/// <summary>
	/// random一个bool值
	/// </summary>
	public bool func_RandomBoolean(TriggerExecutor e,TriggerArg a)
	{
		return MathUtils.randomBoolean();
	}
	
	/// <summary>
	/// int加法
	/// </summary>
	public int func_AddInt(TriggerExecutor e,TriggerArg a,int arg1,int arg2)
	{
		return arg1+arg2;
	}
	
	/// <summary>
	/// int减法
	/// </summary>
	public int func_SubInt(TriggerExecutor e,TriggerArg a,int arg1,int arg2)
	{
		return arg1-arg2;
	}
	
	/// <summary>
	/// int乘法
	/// </summary>
	public int func_MulInt(TriggerExecutor e,TriggerArg a,int arg1,int arg2)
	{
		return arg1*arg2;
	}
	
	/// <summary>
	/// int除法
	/// </summary>
	public int func_DivInt(TriggerExecutor e,TriggerArg a,int arg1,int arg2)
	{
		return arg1/arg2;
	}
	
	/// <summary>
	/// int取反
	/// </summary>
	public int func_InvertInt(TriggerExecutor e,TriggerArg a,int arg)
	{
		return -arg;
	}
	
	/// <summary>
	/// int求余
	/// </summary>
	public int func_RestInt(TriggerExecutor e,TriggerArg a,int arg1,int arg2)
	{
		return arg1%arg2;
	}
	
	/// <summary>
	/// int绝对值
	/// </summary>
	public int func_AbsInt(TriggerExecutor e,TriggerArg a,int arg)
	{
		return Math.Abs(arg);
	}
	
	/// <summary>
	/// int自增
	/// </summary>
	public int func_Add1Int(TriggerExecutor e,TriggerArg a,int arg)
	{
		return arg+1;
	}
	
	/// <summary>
	/// int自减
	/// </summary>
	public int func_Sub1Int(TriggerExecutor e,TriggerArg a,int arg)
	{
		return arg-1;
	}
	
	/// <summary>
	/// float转int
	/// </summary>
	public int func_ConvertFloat2Int(TriggerExecutor e,TriggerArg a,float arg)
	{
		return (int)arg;
	}
	
	/// <summary>
	/// long转int
	/// </summary>
	public int func_ConvertLong2Int(TriggerExecutor e,TriggerArg a,long arg)
	{
		return (int)arg;
	}
	
	/// <summary>
	/// 获取字符串key int
	/// </summary>
	public int func_GetSInt(TriggerExecutor e,TriggerArg a,string key)
	{
		object obj=e.sVarDic.get(key);
		return obj!=null ? (int)obj : 0;
	}
	
	/// <summary>
	/// 获取全局变量int
	/// </summary>
	public int func_GetCurrentLoopIndex(TriggerExecutor e,TriggerArg a)
	{
		TriggerActionRunner p;
		if((p=a.runner.parent)!=null)
			return p.loop;
		else
			return 0;
	}
	
	/// <summary>
	/// 获取当前list遍历元素
	/// </summary>
	public object func_GetCurrentListElement(TriggerExecutor e,TriggerArg a)
	{
		TriggerActionRunner p;
		if((p=a.runner.parent)!=null)
			return p.foreachList.get(p.loop);
		else
			return null;
	}
	
	/// <summary>
	/// 随一整形(0<=value<range)
	/// </summary>
	public int func_RandomInt(TriggerExecutor e,TriggerArg a,int range)
	{
		return MathUtils.randomInt(range);
	}
	
	/// <summary>
	/// 随一整形(start<=value<end)
	/// </summary>
	public int func_RandomRange(TriggerExecutor e,TriggerArg a,int start,int end)
	{
		return MathUtils.randomRange(start,end);
	}
	
	/// <summary>
	/// 获取triggerGM指令str变量
	/// </summary>
	public int func_GetTriggerGMCommandIntArg(TriggerExecutor e,TriggerArg a,int key)
	{
		return (int)a.evt.args[key];
	}
	
	/// <summary>
	/// float加法
	/// </summary>
	public float func_AddFloat(TriggerExecutor e,TriggerArg a,float arg1,float arg2)
	{
		return arg1+arg2;
	}
	
	/// <summary>
	/// float减法
	/// </summary>
	public float func_SubFloat(TriggerExecutor e,TriggerArg a,float arg1,float arg2)
	{
		return arg1-arg2;
	}
	
	/// <summary>
	/// float乘法
	/// </summary>
	public float func_MulFloat(TriggerExecutor e,TriggerArg a,float arg1,float arg2)
	{
		return arg1*arg2;
	}
	
	/// <summary>
	/// float除法
	/// </summary>
	public float func_DivFloat(TriggerExecutor e,TriggerArg a,float arg1,float arg2)
	{
		return arg1/arg2;
	}
	
	/// <summary>
	/// float取反
	/// </summary>
	public float func_InvertFloat(TriggerExecutor e,TriggerArg a,float arg)
	{
		return -arg;
	}
	
	/// <summary>
	/// float绝对值
	/// </summary>
	public float func_AbsFloat(TriggerExecutor e,TriggerArg a,int arg)
	{
		return Math.Abs(arg);
	}
	
	/// <summary>
	/// 获取字符串key变量float
	/// </summary>
	public float func_GetSFloat(TriggerExecutor e,TriggerArg a,string key)
	{
		object obj=e.sVarDic.get(key);
		return obj!=null ? (float)obj : 0f;
	}
	
	/// <summary>
	/// long转float
	/// </summary>
	public float func_ConvertInt2Float(TriggerExecutor e,TriggerArg a,int arg)
	{
		return (float)arg;
	}
	
	/// <summary>
	/// float转float
	/// </summary>
	public float func_ConvertLong2Float(TriggerExecutor e,TriggerArg a,long arg)
	{
		return (float)arg;
	}
	
	/// <summary>
	/// long加法
	/// </summary>
	public long func_AddLong(TriggerExecutor e,TriggerArg a,long arg1,long arg2)
	{
		return arg1+arg2;
	}
	
	/// <summary>
	/// long减法
	/// </summary>
	public long func_SubLong(TriggerExecutor e,TriggerArg a,long arg1,long arg2)
	{
		return arg1-arg2;
	}
	
	/// <summary>
	/// long乘法
	/// </summary>
	public long func_MulLong(TriggerExecutor e,TriggerArg a,long arg1,long arg2)
	{
		return arg1*arg2;
	}
	
	/// <summary>
	/// long除法
	/// </summary>
	public long func_DivLong(TriggerExecutor e,TriggerArg a,long arg1,long arg2)
	{
		return arg1/arg2;
	}
	
	/// <summary>
	/// long取反
	/// </summary>
	public long func_InvertLong(TriggerExecutor e,TriggerArg a,long arg)
	{
		return -arg;
	}
	
	/// <summary>
	/// long绝对值
	/// </summary>
	public long func_AbsLong(TriggerExecutor e,TriggerArg a,long arg)
	{
		return Math.Abs(arg);
	}
	
	/// <summary>
	/// int转long
	/// </summary>
	public long func_ConvertInt2Long(TriggerExecutor e,TriggerArg a,int arg)
	{
		return (long)arg;
	}
	
	/// <summary>
	/// float转long
	/// </summary>
	public long func_ConvertFloat2Long(TriggerExecutor e,TriggerArg a,float arg)
	{
		return (long)arg;
	}
	
	/// <summary>
	/// 获取字符串key变量long
	/// </summary>
	public long func_GetSLong(TriggerExecutor e,TriggerArg a,string key)
	{
		object obj=e.sVarDic.get(key);
		return obj!=null ? (long)obj : 0l;
	}
	
	/// <summary>
	/// 获取当前时间戳
	/// </summary>
	public long func_GetTimeMillis(TriggerExecutor e,TriggerArg a)
	{
		return e.getTimeMillis();
	}
	
	/// <summary>
	/// string相加
	/// </summary>
	public string func_AddStr(TriggerExecutor e,TriggerArg a,string arg1,string arg2)
	{
		return arg1+arg2;
	}
	
	/// <summary>
	/// int转string
	/// </summary>
	public string func_ConvertInt2Str(TriggerExecutor e,TriggerArg a,int arg)
	{
		return arg.ToString();
	}
	
	/// <summary>
	/// float转string
	/// </summary>
	public string func_ConvertFloat2Str(TriggerExecutor e,TriggerArg a,float arg)
	{
		return arg.ToString();
	}
	
	/// <summary>
	/// long转string
	/// </summary>
	public string func_ConvertLong2Str(TriggerExecutor e,TriggerArg a,long arg)
	{
		return arg.ToString();
	}
	
	/// <summary>
	/// bool转string
	/// </summary>
	public string func_ConvertBool2Str(TriggerExecutor e,TriggerArg a,bool arg)
	{
		return arg.ToString();
	}
	
	/// <summary>
	/// 获取字符串key变量string
	/// </summary>
	public string func_GetSString(TriggerExecutor e,TriggerArg a,string key)
	{
		object obj=e.sVarDic.get(key);
		return obj!=null ? (string)obj : "";
	}
	
	/// <summary>
	/// 等待(ms)
	/// </summary>
	public void func_Wait(TriggerExecutor e,TriggerArg a,int delay)
	{
		Ctrl.warnLog("function func_Wait,need implement");
	}
	
	/// <summary>
	/// 等待某条件成立(checkDelay:检查间隔(ms))
	/// </summary>
	public void func_WaitUtil(TriggerExecutor e,TriggerArg a,bool condition,int checkDelay)
	{
		Ctrl.warnLog("function func_WaitUtil,need implement");
	}
	
	/// <summary>
	/// 输出字符串
	/// </summary>
	public void func_Print(TriggerExecutor e,TriggerArg a,string str)
	{
		e.print(str);
	}
	
	/// <summary>
	/// 循环loop次(i从0到loop-1)
	/// </summary>
	public void func_ForLoop(TriggerExecutor e,TriggerArg a,int loop,object func)
	{
		Ctrl.warnLog("function func_ForLoop,need implement");
	}
	
	/// <summary>
	/// 结束当前trigger执行动作
	/// </summary>
	public void func_BreakTrigger(TriggerExecutor e,TriggerArg a)
	{
		Ctrl.warnLog("function func_BreakTrigger,need implement");
	}
	
	/// <summary>
	/// 结束当前循环(对while有效)
	/// </summary>
	public void func_BreakLoop(TriggerExecutor e,TriggerArg a)
	{
		Ctrl.warnLog("function func_BreakLoop,need implement");
	}
	
	/// <summary>
	/// 跳过剩余脚本，继续执行循环(对while有效)
	/// </summary>
	public void func_ContinueLoop(TriggerExecutor e,TriggerArg a)
	{
		Ctrl.warnLog("function func_ContinueLoop,need implement");
	}
	
	/// <summary>
	/// 设置自定义字符串key变量
	/// </summary>
	public void func_SetSVar(TriggerExecutor e,TriggerArg a,string key,object value)
	{
		e.sVarDic.put(key,value);
	}
	
	/// <summary>
	/// 设置局部字符串key变量
	/// </summary>
	public void func_SetLocalVar(TriggerExecutor e,TriggerArg a,string key,object value)
	{
		a.runner.setLocalVar(key,value);
	}
	
	/// <summary>
	/// 获取event int参数
	/// </summary>
	public int func_GetEventIntArgs(TriggerExecutor e,TriggerArg a,int index)
	{
		return (int)a.evt.args[index];
	}
	
	/// <summary>
	/// 获取event bool参数
	/// </summary>
	public bool func_GetEventBoolArgs(TriggerExecutor e,TriggerArg a,int index)
	{
		return (bool)a.evt.args[index];
	}
	
	/// <summary>
	/// 获取event string参数
	/// </summary>
	public string func_GetEventStringArgs(TriggerExecutor e,TriggerArg a,int index)
	{
		return (string)a.evt.args[index];
	}
	
	/// <summary>
	/// 获取list长度
	/// </summary>
	public int func_GetListSize(TriggerExecutor e,TriggerArg a,SList<object> list)
	{
		return list.size();
	}
	
	/// <summary>
	/// list添加
	/// </summary>
	public void func_ListAdd(TriggerExecutor e,TriggerArg a,SList<object> list,object value)
	{
		list.add(value);
	}
	
	/// <summary>
	/// list删除
	/// </summary>
	public void func_ListRemove(TriggerExecutor e,TriggerArg a,SList<object> list,int index)
	{
		list.remove(index);
	}
	
	/// <summary>
	/// list删除元素
	/// </summary>
	public bool func_ListRemoveObj(TriggerExecutor e,TriggerArg a,SList<object> list,object value)
	{
		return list.removeObj(value);
	}
	
	/// <summary>
	/// list清空
	/// </summary>
	public void func_ListClear(TriggerExecutor e,TriggerArg a,SList<object> list)
	{
		list.clear();
	}
	
	/// <summary>
	/// list查询
	/// </summary>
	public int func_ListIndexOf(TriggerExecutor e,TriggerArg a,SList<object> list,object value)
	{
		return list.indexOf(value);
	}
	
	/// <summary>
	/// list查询
	/// </summary>
	public bool func_ListContains(TriggerExecutor e,TriggerArg a,SList<object> list,object value)
	{
		return list.indexOf(value)!=-1;
	}
	
	/// <summary>
	/// 遍历list
	/// </summary>
	public void func_ForeachList(TriggerExecutor e,TriggerArg a,SList<object> list,object func)
	{
		Ctrl.warnLog("function func_ForeachList,need implement");
	}
	
	/// <summary>
	/// list是否为空
	/// </summary>
	public bool func_ListIsEmpty(TriggerExecutor e,TriggerArg a,SList<object> list)
	{
		return list.isEmpty();
	}
	
	/// <summary>
	/// 获取Map长度
	/// </summary>
	public int func_GetMapSize(TriggerExecutor e,TriggerArg a,SMap<object,object> map)
	{
		return map.size();
	}
	
	/// <summary>
	/// map添加
	/// </summary>
	public void func_MapPut(TriggerExecutor e,TriggerArg a,SMap<object,object> map,object key,object value)
	{
		map.put(key,value);
	}
	
	/// <summary>
	/// Map删除
	/// </summary>
	public bool func_MapRemove(TriggerExecutor e,TriggerArg a,SMap<object,object> map,object key)
	{
		return map.remove(key)!=null;
	}
	
	/// <summary>
	/// Map清空
	/// </summary>
	public void func_MapClear(TriggerExecutor e,TriggerArg a,SMap<object,object> map)
	{
		map.clear();
	}
	
	/// <summary>
	/// Map包含
	/// </summary>
	public bool func_MapContains(TriggerExecutor e,TriggerArg a,SMap<object,object> map,object key)
	{
		return map.contains(key);
	}
	
	/// <summary>
	/// map是否为空
	/// </summary>
	public bool func_MapIsEmpty(TriggerExecutor e,TriggerArg a,SMap<object,object> map)
	{
		return map.isEmpty();
	}
	
	/// <summary>
	/// 获取Set长度
	/// </summary>
	public int func_GetSetSize(TriggerExecutor e,TriggerArg a,SSet<object> value)
	{
		return value.size();
	}
	
	/// <summary>
	/// Set添加
	/// </summary>
	public void func_SetAdd(TriggerExecutor e,TriggerArg a,SSet<object> map,object key)
	{
		map.add(key);
	}
	
	/// <summary>
	/// Set删除
	/// </summary>
	public bool func_SetRemove(TriggerExecutor e,TriggerArg a,SSet<object> map,object key)
	{
		return map.remove(key);
	}
	
	/// <summary>
	/// Set清空
	/// </summary>
	public void func_SetClear(TriggerExecutor e,TriggerArg a,SSet<object> map)
	{
		map.clear();
	}
	
	/// <summary>
	/// Set包含
	/// </summary>
	public bool func_SetContains(TriggerExecutor e,TriggerArg a,SSet<object> map,object key)
	{
		return map.contains(key);
	}
	
	/// <summary>
	/// Set是否为空
	/// </summary>
	public bool func_SetIsEmpty(TriggerExecutor e,TriggerArg a,SSet<object> map)
	{
		return map.isEmpty();
	}
	
	/// <summary>
	/// 当做List
	/// </summary>
	public SList<object> func_AsList(TriggerExecutor e,TriggerArg a,object obj)
	{
		return (SList<object>)obj;
	}
	
	/// <summary>
	/// 创建空List
	/// </summary>
	public SList<object> func_CreateList(TriggerExecutor e,TriggerArg a)
	{
		return new SList<object>();
	}
	
	/// <summary>
	/// 当做Map
	/// </summary>
	public SMap<object,object> func_AsMap(TriggerExecutor e,TriggerArg a,object obj)
	{
		return (SMap<object,object>)obj;
	}
	
	/// <summary>
	/// 创建空Map
	/// </summary>
	public SMap<object,object> func_CreateMap(TriggerExecutor e,TriggerArg a)
	{
		return new SMap<object,object>();
	}
	
	/// <summary>
	/// 当做Set
	/// </summary>
	public SSet<object> func_AsSet(TriggerExecutor e,TriggerArg a,object obj)
	{
		return (SSet<object>)obj;
	}
	
	/// <summary>
	/// 创建空Set
	/// </summary>
	public SSet<object> func_CreateSet(TriggerExecutor e,TriggerArg a)
	{
		return new SSet<object>();
	}
	
	/// <summary>
	/// 开启trigger
	/// </summary>
	public void func_OpenTrigger(TriggerExecutor e,TriggerArg a,int cls)
	{
		e.openTrigger(cls);
	}
	
	/// <summary>
	/// 关闭trigger
	/// </summary>
	public void func_CloseTrigger(TriggerExecutor e,TriggerArg a,int cls)
	{
		e.closeTrigger(cls);
	}
	
	/// <summary>
	/// 执行trigger(需要检查环境)
	/// </summary>
	public void func_RunTrigger(TriggerExecutor e,TriggerArg a,int cls)
	{
		Ctrl.warnLog("function func_RunTrigger,need implement");
	}
	
	/// <summary>
	/// 执行trigger(不检查环境)
	/// </summary>
	public void func_RunTriggerAbs(TriggerExecutor e,TriggerArg a,int cls)
	{
		Ctrl.warnLog("function func_RunTriggerAbs,need implement");
	}
	
	/// <summary>
	/// 获取局部字符串key boolean
	/// </summary>
	public bool func_GetLocalBoolean(TriggerExecutor e,TriggerArg a,string key)
	{
		object obj=a.runner.getLocalVar(key);
		return obj!=null ? (bool)obj : false;
	}
	
	/// <summary>
	/// 获取局部字符串key int
	/// </summary>
	public int func_GetLocalInt(TriggerExecutor e,TriggerArg a,string key)
	{
		object obj=a.runner.getLocalVar(key);
		return obj!=null ? (int)obj : 0;
	}
	
	/// <summary>
	/// 获取局部字符串key变量float
	/// </summary>
	public float func_GetLocalFloat(TriggerExecutor e,TriggerArg a,string key)
	{
		object obj=a.runner.getLocalVar(key);
		return obj!=null ? (float)obj : 0f;
	}
	
	/// <summary>
	/// 获取局部字符串key变量long
	/// </summary>
	public long func_GetLocalLong(TriggerExecutor e,TriggerArg a,string key)
	{
		object obj=a.runner.getLocalVar(key);
		return obj!=null ? (long)obj : 0L;
	}
	
	/// <summary>
	/// 获取局部字符串key变量string
	/// </summary>
	public string func_GetLocalString(TriggerExecutor e,TriggerArg a,string key)
	{
		object obj=a.runner.getLocalVar(key);
		return obj!=null ? (string)obj : "";
	}
	
	/// <summary>
	/// 是否为空
	/// </summary>
	public bool func_IsNull(TriggerExecutor e,TriggerArg a,object arg1)
	{
		return arg1==null;
	}

	/// <summary>
	/// 是否不为空
	/// </summary>
	public bool func_NotNull(TriggerExecutor e,TriggerArg a,object arg1)
	{
		return arg1!=null;
	}
	
	/// <summary>
	/// 该毫秒值是否为过去时间
	/// </summary>
	public bool func_IsTimeMillisPass(TriggerExecutor e,TriggerArg a,long time)
	{
		return time<e.getTimeMillis();
	}
	
	/// <summary>
	/// 删除自定义字符串key变量
	/// </summary>
	public void func_RemoveSVar(TriggerExecutor e,TriggerArg a,string key)
	{
		e.sVarDic.remove(key);
	}
	
	/// <summary>
	/// c单位是否存活
	/// </summary>
	public bool func_UnitIsAlive(SceneTriggerExecutor e,TriggerArg a,Unit unit)
	{
		return unit.fight.isAlive();
	}
	
	/// <summary>
	/// 对于某单位来说，点是否可走
	/// </summary>
	public bool func_IsPosEnabled(SceneTriggerExecutor e,TriggerArg a,Unit unit,PosData pos)
	{
		Ctrl.warnLog("function func_IsPosEnabled,need implement");
		return false;
	}
	
	/// <summary>
	/// 当前触发单位
	/// </summary>
	public Unit func_TriggerUnit(SceneTriggerExecutor e,TriggerArg a)
	{
		return ((SceneTriggerEvent)a.evt).triggerUnit;
	}
	
	/// <summary>
	/// 计算点距离
	/// </summary>
	public float func_PosDistance(SceneTriggerExecutor e,TriggerArg a,PosData pos0,PosData pos1)
	{
		return e.getScene().pos.calculatePosDistance2D(pos0,pos1);
	}
	
	/// <summary>
	/// 获取单位的战斗单位id
	/// </summary>
	public int func_GetUnitFightUnitID(SceneTriggerExecutor e,TriggerArg a,Unit unit)
	{
		return unit.fight.getFightUnitConfig().id;
	}
	
	/// <summary>
	/// 通过实例id获取单位
	/// </summary>
	public Unit func_GetUnit(SceneTriggerExecutor e,TriggerArg a,int instanceID)
	{
		return e.getScene().getUnit(instanceID);
	}
	
	/// <summary>
	/// 创建并添加傀儡
	/// </summary>
	public Unit func_CreateAddPuppet(SceneTriggerExecutor e,TriggerArg a,int id,int level,PosData pos,Unit master,int lastTime)
	{
		return e.getScene().unitFactory.createAddPuppet(id,level,pos,master,lastTime);
	}
	
	/// <summary>
	/// 获取单位位置
	/// </summary>
	public PosData func_GetUnitPos(SceneTriggerExecutor e,TriggerArg a,Unit unit)
	{
		return unit.pos.getPos();
	}
	
	/// <summary>
	/// 获取场景排放配置位置
	/// </summary>
	public PosData func_GetScenePlacePos(SceneTriggerExecutor e,TriggerArg a,int instanceID)
	{
		ScenePlaceElementConfig eConfig=e.getScene().getPlaceConfig().getElement(instanceID);

		if(eConfig==null)
		{
			Ctrl.errorLog("未找到场景摆放配置",instanceID);
			return null;
		}
		PosData re=new PosData();
		re.setByFArr(eConfig.pos);

		return re;
	}
	
	/// <summary>
	/// 极坐标取点
	/// </summary>
	public PosData func_PosPolar(SceneTriggerExecutor e,TriggerArg a,PosData pos,float distance,DirData dir)
	{
		PosData re=new PosData();
		e.getScene().pos.polar2D(re,distance,dir);
		e.getScene().pos.addPos(re,pos);
		return re;
	}
	
	/// <summary>
	/// 点相加
	/// </summary>
	public PosData func_AddPos(SceneTriggerExecutor e,TriggerArg a,PosData pos0,PosData pos1)
	{
		PosData re=new PosData();
		re.copyPos(pos0);
		e.getScene().pos.addPos(re,pos1);
		return re;
	}
	
	/// <summary>
	/// 获取单位朝向
	/// </summary>
	public DirData func_GetUnitDir(SceneTriggerExecutor e,TriggerArg a,Unit unit)
	{
		return unit.pos.getDir();
	}
	
	/// <summary>
	/// 朝向 相加
	/// </summary>
	public DirData func_AddDir(SceneTriggerExecutor e,TriggerArg a,DirData dir,DirData value)
	{
		DirData re=new DirData();
		re.copyDir(dir);
		re.direction=MathUtils.directionCut(re.direction+value.direction);
		//re.directionX+=dir2.directionX;
		return re;
	}
	
	/// <summary>
	/// 朝向 相加
	/// </summary>
	public DirData func_AddDirFloat(SceneTriggerExecutor e,TriggerArg a,DirData dir,float value)
	{
		DirData re=new DirData();
		re.copyDir(dir);
		re.direction=MathUtils.directionCut(re.direction+value);
		return re;
	}
	
	/// <summary>
	/// 强制击杀单位
	/// </summary>
	public void func_KillUnit(SceneTriggerExecutor e,TriggerArg a,Unit unit)
	{
		e.getScene().fight.killUnit(unit);
	}
	
	/// <summary>
	/// 移除单位
	/// </summary>
	public void func_RemoveUnit(SceneTriggerExecutor e,TriggerArg a,Unit unit)
	{
		unit.removeLater();
	}
	
	/// <summary>
	/// 移动到目标单位
	/// </summary>
	public void func_MoveToUnit(SceneTriggerExecutor e,TriggerArg a,Unit unit,Unit target,float radius)
	{
		unit.aiCommand.moveTo(target,radius,null);
	}
	
	/// <summary>
	/// 单位添加属性
	/// </summary>
	public void func_UnitAddAttribute(SceneTriggerExecutor e,TriggerArg a,Unit unit,int type,int value)
	{
		unit.fight.getAttributeLogic().addOneAttribute(type,value);
	}
	
	/// <summary>
	/// 单位治疗生命千分比
	/// </summary>
	public void func_UnitAddHpPercent(SceneTriggerExecutor e,TriggerArg a,Unit unit,int percent)
	{
		unit.fight.getAttributeLogic().addHPPercent(percent);
	}
	
	/// <summary>
	/// 获取主引导步
	/// </summary>
	public int func_GetGuideMainStep(GuideTriggerExecutor e,TriggerArg a)
	{
		return GameC.player.guide.getMainStep();
	}
	
	/// <summary>
	/// 设置主引导步
	/// </summary>
	public void func_SetGuideMainStep(GuideTriggerExecutor e,TriggerArg a,int value)
	{
		GameC.player.guide.setMainStep(value);
	}
	
	/// <summary>
	/// 显示UI(key:UI表主键)
	/// </summary>
	public void func_ShowUI(GuideTriggerExecutor e,TriggerArg a,int key)
	{
		//TODO:实现
		// GameC.ui.showUIByType(key);
		Ctrl.warnLog("function func_HideUI,need implement");
	}
	
	/// <summary>
	/// 隐藏UI(key:UI表主键)
	/// </summary>
	public void func_HideUI(GuideTriggerExecutor e,TriggerArg a,int key)
	{
		//TODO:实现
		// GameC.ui.showUIByType(key);
		Ctrl.warnLog("function func_HideUI,need implement");
	}
	
	/// <summary>
	/// 强制类型转化为单位
	/// </summary>
	public Unit func_AsUnit(SceneTriggerExecutor e,TriggerArg a,object obj)
	{
		return (Unit)obj;
	}
	
	/// <summary>
	/// 强制类型转化为点
	/// </summary>
	public PosData func_AsPos(SceneTriggerExecutor e,TriggerArg a,object obj)
	{
		return (PosData)obj;
	}
	
	/// <summary>
	/// 强制类型转化为朝向
	/// </summary>
	public DirData func_AsDir(SceneTriggerExecutor e,TriggerArg a,object obj)
	{
		return (DirData)obj;
	}
	

	
}
