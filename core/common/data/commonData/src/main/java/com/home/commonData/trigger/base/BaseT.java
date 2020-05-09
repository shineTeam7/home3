package com.home.commonData.trigger.base;

import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class BaseT
{
	/** 是否开启 */
	protected boolean isOpen=false;
	/** 优先级 */
	protected int priority=0;
	
	protected abstract void init();
	protected abstract void event();
	protected abstract void condition();
	protected abstract void action();
	
	//--function--//
	//--object--//
	/** 获取字符串key自定义对象 */
	protected Object getSVar(String key){return null;}
	/** 获取局部字符串key自定义对象 */
	protected Object getLocalVar(String key){return null;}
	
	//--boolean--//
	/** 非 */
	protected boolean not(boolean arg){return !arg;}
	/** 并且 */
	protected boolean and(boolean arg1,boolean arg2){return arg1 && arg2;}
	/** 并且2 */
	protected boolean and2(boolean arg1,boolean arg2,boolean arg3){return arg1 && arg2 && arg3;}
	/** 或者 */
	protected boolean or(boolean arg1,boolean arg2){return arg1 || arg2;}
	/** 或者2 */
	protected boolean or2(boolean arg1,boolean arg2,boolean arg3){return arg1 || arg2 || arg3;}
	/** 判断相等 */
	protected boolean equals(Object arg1,Object arg2){return arg1==arg2;}
	/** 判断int相等 */
	protected boolean equalsInt(int arg1,int arg2){return arg1==arg2;}
	/** 判断float相等 */
	protected boolean equalsFloat(float arg1,float arg2){return arg1==arg2;}
	/** 判断long相等 */
	protected boolean equalsLong(long arg1,long arg2){return arg1==arg2;}
	/** 判断string相等 */
	protected boolean equalsString(String arg1,String arg2){return arg1.equals(arg2);}
	/** 大于int */
	protected boolean greaterThanInt(int arg1,int arg2){return arg1>arg2;}
	/** 大于等于int */
	protected boolean greaterThanOrEqualInt(int arg1,int arg2){return arg1>=arg2;}
	/** 小于int */
	protected boolean lessThanInt(int arg1,int arg2){return arg1<arg2;}
	/** 小于等于int */
	protected boolean lessThanOrEqualInt(int arg1,int arg2){return arg1<=arg2;}
	/** 大于float */
	protected boolean greaterThanFloat(float arg1,float arg2){return arg1>arg2;}
	/** 大于等于float */
	protected boolean greaterThanOrEqualFloat(float arg1,float arg2){return arg1>=arg2;}
	/** 小于float */
	protected boolean lessThanFloat(float arg1,float arg2){return arg1<arg2;}
	/** 小于等于float */
	protected boolean lessThanOrEqualFloat(float arg1,float arg2){return arg1<=arg2;}
	/** 大于long */
	protected boolean greaterThanLong(long arg1,long arg2){return arg1>arg2;}
	/** 大于等于long */
	protected boolean greaterThanOrEqualLong(long arg1,long arg2){return arg1>=arg2;}
	/** 小于long */
	protected boolean lessThanLong(long arg1,long arg2){return arg1<arg2;}
	/** 小于等于long */
	protected boolean lessThanOrEqualLong(long arg1,long arg2){return arg1<=arg2;}
	/** 获取字符串key boolean */
	protected boolean getSBoolean(String key){return false;}
	/** 删除自定义字符串key变量 */
	protected boolean removeSVar(String key){return false;}
	
	/** random一个bool值 */
	protected boolean randomBoolean(){return false;}
	
	//--int--//
	/** int加法 */
	protected int addInt(int arg1,int arg2){return arg1+arg2;}
	/** int减法 */
	protected int subInt(int arg1,int arg2){return arg1-arg2;}
	/** int乘法 */
	protected int mulInt(int arg1,int arg2){return arg1*arg2;}
	/** int除法 */
	protected int divInt(int arg1,int arg2){return arg1/arg2;}
	/** int取反 */
	protected int invertInt(int arg){return -arg;}
	/** int求余 */
	protected int restInt(int arg1,int arg2){return arg1%arg2;}
	/** int绝对值 */
	protected int absInt(int arg){return arg>=0 ? arg : -arg;}
	/** int自增 */
	protected int add1Int(int arg){return ++arg;}
	/** int自减 */
	protected int sub1Int(int arg){return --arg;}
	/** float转int */
	protected int convertFloat2Int(float arg){return (int)arg;}
	/** long转int */
	protected int convertLong2Int(long arg){return (int)arg;}
	/** 获取字符串key int */
	protected int getSInt(String key){return 0;}
	///** 获取全局变量int */
	//protected int getInt(int key){return 0;}
	///** 获取循环变量(index为第几层循环) */
	//protected int getLoopIndex(int index){return 0;}
	/** 获取当前层循环变量(index:0) */
	protected int getCurrentLoopIndex(){return 0;}
	/** 获取当前list遍历元素 */
	protected Object getCurrentListElement(){return null;}
	
	/** 随一整形(0<=value<range) */
	protected int randomInt(int range){return 0;}
	/** 随一整形(start<=value<end) */
	protected int randomRange(int start,int end){return 0;}
	/** 获取triggerGM指令str变量 */
	protected int getTriggerGMCommandIntArg(int key){return 0;}
	
	
	//--float--//
	/** float加法 */
	protected float addFloat(float arg1,float arg2){return arg1+arg2;}
	/** float减法 */
	protected float subFloat(float arg1,float arg2){return arg1-arg2;}
	/** float乘法 */
	protected float mulFloat(float arg1,float arg2){return arg1*arg2;}
	/** float除法 */
	protected float divFloat(float arg1,float arg2){return arg1/arg2;}
	/** float取反 */
	protected float invertFloat(float arg){return -arg;}
	/** float绝对值 */
	protected float absFloat(int arg){return arg>=0f ? arg : -arg;}
	/** 获取字符串key变量float */
	protected float getSFloat(String key){return 0f;}
	///** 获取全局变量float */
	//protected float getFloat(int key){return 0f;}
	
	/** long转float */
	protected float convertInt2Float(int arg){return (float)arg;}
	/** float转float */
	protected float convertLong2Float(long arg){return (float)arg;}
	
	//--long--//
	/** long加法 */
	protected long addLong(long arg1,long arg2){return arg1+arg2;}
	/** long减法 */
	protected long subLong(long arg1,long arg2){return arg1-arg2;}
	/** long乘法 */
	protected long mulLong(long arg1,long arg2){return arg1*arg2;}
	/** long除法 */
	protected long divLong(long arg1,long arg2){return arg1/arg2;}
	/** long取反 */
	protected long invertLong(long arg){return -arg;}
	/** long绝对值 */
	protected long absLong(long arg){return arg>=0L ? arg : -arg;}
	/** int转long */
	protected long convertInt2Long(int arg){return (long)arg;}
	/** float转long */
	protected long convertFloat2Long(float arg){return (long)arg;}
	/** 获取字符串key变量long */
	protected long getSLong(String key){return 0L;}
	///** 获取全局变量long */
	//protected long getLong(int key){return 0L;}
	
	/** 获取当前时间戳 */
	protected long getTimeMillis(){return 0L;}
	
	//--string--//
	/** string相加 */
	protected String addStr(String arg1,String arg2){return arg1+arg2;}
	/** int转string */
	protected String convertInt2Str(int arg){return String.valueOf(arg);}
	/** float转string */
	protected String convertFloat2Str(float arg){return String.valueOf(arg);}
	/** long转string */
	protected String convertLong2Str(long arg){return String.valueOf(arg);}
	/** bool转string */
	protected String convertBool2Str(boolean arg){return String.valueOf(arg);}
	/** 获取字符串key变量string */
	protected String getSString(String key){return "";}
	///** 获取全局变量string */
	//protected String getString(int key){return "";}
	
	//--collection--//
	
	
	protected List asList(Object obj){return null;}
	/** 创建空List */
	protected List createList(){return null;}
	/** 获取list长度 */
	protected int getListSize(List list){return 0;}
	/** list添加 */
	protected void listAdd(List list,Object value){}
	/** list删除 */
	protected void listRemove(List list,int index){}
	/** list删除元素 */
	protected boolean listRemoveObj(List list,Object value){return false;}
	/** list清空 */
	protected void listClear(List list){}
	/** list查询 */
	protected int listIndexOf(List list,Object value){return -1;}
	/** list查询 */
	protected boolean listContains(List list,Object value){return false;}
	/** 遍历list */
	protected void foreachList(List list,Runnable func){}
	/** list是否为空 */
	protected boolean listIsEmpty(List list){return false;}
	
	protected Map asMap(Object obj){return null;}
	/** 创建空Map */
	protected Map createMap(){return null;}
	/** 获取Map长度 */
	protected int getMapSize(Map map){return 0;}
	/** map添加 */
	protected void mapPut(Map map,Object key,Object value){}
	/** Map删除 */
	protected boolean mapRemove(Map map,Object key){return false;}
	/** Map清空 */
	protected void mapClear(Map map){}
	/** Map包含 */
	protected boolean mapContains(Map map,Object key){return false;}
	/** map是否为空 */
	protected boolean mapIsEmpty(Map map){return false;}
	
	protected Set asSet(Object obj){return null;}
	/** 创建空Set */
	protected Set createSet(){return null;}
	/** 获取Set长度 */
	protected int getSetSize(Set value){return 0;}
	/** Set添加 */
	protected void setAdd(Set map,Object key){}
	/** Set删除 */
	protected boolean setRemove(Set map,Object key){return false;}
	/** Set清空 */
	protected void setClear(Set map){}
	/** Set包含 */
	protected boolean setContains(Set map,Object key){return false;}
	/** Set是否为空 */
	protected boolean setIsEmpty(Set map){return false;}
	
	//--event--//
	/** trigger初始化好 */
	protected void onInit(){}
	/** 间隔执行 */
	protected void onInterval(int delay){}
	/** triggerGM指令 */
	protected void onTriggerGMCommand(){}
	
	//--action--//
	
	//system
	/** 开启trigger */
	protected void openTrigger(Class<? extends BaseT> cls){}
	/** 关闭trigger */
	protected void closeTrigger(Class<? extends BaseT> cls){}
	/** 执行trigger(需要检查环境) */
	protected void runTrigger(Class<? extends BaseT> cls){}
	/** 执行trigger(不检查环境) */
	protected void runTriggerAbs(Class<? extends BaseT> cls){}
	/** 等待(ms) */
	protected void wait(int delay){}
	/** 等待某条件成立(checkDelay:检查间隔(ms)) */
	protected void waitUtil(boolean condition,int checkDelay){}
	/** 输出字符串 */
	protected void print(String str){}
	/** 循环loop次(i从0到loop-1) */
	protected void forLoop(int loop,Runnable func){}
	/** 结束当前trigger执行动作 */
	protected void breakTrigger(){}
	/** 结束当前循环(对while有效) */
	protected void breakLoop(){}
	/** 跳过剩余脚本，继续执行循环(对while有效) */
	protected void continueLoop(){}
	/** 设置自定义字符串key变量 */
	protected void setSVar(String key,Object value){}
	
	
	/** 设置局部字符串key变量 */
	protected void setLocalVar(String key,Object value){}
	
	///** 设置全局变量boolean */
	//protected void setBoolean(int key,boolean value){}
	///** 设置全局变量int */
	//protected void setInt(int key,int value){}
	///** 设置全局变量float */
	//protected void setFloat(int key,float value){}
	///** 设置全局变量long */
	//protected void setLong(int key,long value){}
	///** 设置全局变量String */
	//protected void setString(int key,String value){}
	
	/** 获取event int参数 */
	protected int getEventIntArgs(int index){return 0;}
	/** 获取event bool参数 */
	protected boolean getEventBoolArgs(int index){return false;}
	/** 获取event string参数 */
	protected String getEventStringArgs(int index){return "";}
}
