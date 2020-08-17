package com.home.commonBase.utils;

import com.home.commonBase.config.game.SkillVarConfig;
import com.home.commonBase.constlist.generate.SkillVarSourceType;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.logic.unit.UnitFightDataLogic;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.ACStringFilter;
import com.home.shine.support.collection.IntList;
import com.home.shine.support.collection.SList;
import com.home.shine.utils.MathUtils;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/** 基础游戏方法 */
public class BaseGameUtils
{
	private static final char _replaceChar= '*';
	private static final char[] _ignoreChars= {	'　', ' ', '*', '-', '_', '+', '/', '.', '(', ')', '&', '%', '$', '#', '@', '!' };
	
	private static ACStringFilter _strFilter=new ACStringFilter(_ignoreChars,_replaceChar,ShineSetting.acStringFilterKeepWholeWords);
	
	private static AtomicInteger[] _steps=new AtomicInteger[1000];
	
	/** 检测emoji */
	//private static Pattern _emojiCheck = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]");
	private static Pattern _emojiCheck = Pattern.compile("[^\u0000-\uFFFF]");
	
	/** 符号正则检查 */
	private static Pattern _symbolCheck = Pattern.compile("[\\r\\n\\t,.:; ?^&|`<>=+\\-*\\\\]");
	
	static
	{
		for(int i=_steps.length-1;i>=0;--i)
		{
			_steps[i]=new AtomicInteger(0);
		}
	}
	
	public static void initStrFilter(SList<String> list)
	{
		_strFilter.init(list);
	}
	
	/** 是否有符号 */
	public static boolean hasSymbol(String str)
	{
		return _symbolCheck.matcher(str).find();
	}
	
	/** 是否有表情 */
	public static boolean hasEmoji(String str)
	{
		return _emojiCheck.matcher(str).find();
	}
	
	/** 是否有敏感词 */
	public static boolean hasSensitiveWord(String str)
	{
		if(str.isEmpty())
			return false;

		if(hasSymbol(str))
			return true;

		if(hasEmoji(str))
			return true;
		
		return _strFilter.contain(str);
	}
	
	/** 是否有敏感词(聊天用,不检查emoji) */
	public static String replaceSensitiveWord(String str)
	{
		if(str.isEmpty())
			return "";
		
		return _strFilter.replace(str);
	}
	
	/** 记步 */
	public static void recordStep(int step)
	{
		if(CommonSetting.needRecordStep)
		{
			int num=_steps[step].incrementAndGet();
			
			Ctrl.log("step:"+step+" num:"+num);
		}
	}
	
	/** 计算一个变量值 */
	public static int calculateOneSkillVarValue(int[] args,UnitFightDataLogic self,UnitFightDataLogic target)
	{
		int key;
		
		if((key=args[0])==SkillVarSourceType.ConstValue)
			return args[1];
		
		if(BaseC.constlist.skillVarSource_isTarget(key))
		{
			if(target==null)
				return 0;
			
			return target.getSkillVarSourceValue(args,false);
		}
		else
		{
			if(self==null)
				return 0;
			
			return self.getSkillVarSourceValue(args,true);
		}
	}
	
	/** 计算技能变量值(完整) */
	public static int calculateSkillVarValueFull(int varID,UnitFightDataLogic self,UnitFightDataLogic target)
	{
		return calculateSkillVarValueFull(SkillVarConfig.get(varID),self,target);
	}
	
	/** 计算技能变量值(完整) */
	public static int calculateSkillVarValueFull(SkillVarConfig config,UnitFightDataLogic self,UnitFightDataLogic target)
	{
		return BaseC.logic.calculateSkillVar(config.formulaType,config.args,self,target,null,0);
	}
	
	/** 计算技能变量值(完整) */
	public static int calculateSkillVarValueForSelf(SkillVarConfig config,int[] selfValues,int start,UnitFightDataLogic target)
	{
		return BaseC.logic.calculateSkillVar(config.formulaType,config.args,null,target,selfValues,start);
	}
	
	/** 生成logicID */
	public static long makeLogicID(int areaID,int index)
	{
		if(index >= CommonSetting.areaRegistMax)
		{
			Ctrl.throwError("没有新角色ID了");
			return -1L;
		}
		
		//单服1亿注册上限
		return (long)areaID * CommonSetting.areaRegistMax + index;
	}
	
	/** 生成自定义playerID */
	public static long makeCustomLogicID(int type,int index)
	{
		return makeLogicID(CommonSetting.areaMax+type,index);
	}

	/** 获取puid */
	public static String getPUID(String uid,String platform)
	{
		return platform+"_"+uid;
	}
	
	/** 计算事务发起者索引 */
	public static int getWorkSenderIndex(int type,int id)
	{
		return type<<16 | id;
	}
	
	/** 计算事务发起者类型 */
	public static int getWorkSenderType(int index)
	{
		return index>>16;
	}
	
	/** 计算事务发起者ID */
	public static int getWorkSenderID(int index)
	{
		return index & 0xffff;
	}
	
	/** 检查客户端次数 */
	public static boolean checkClientNum(int num)
	{
		return num>0 && num<CommonSetting.clientNumMax;
	}

	/** 从数组中随机指定数量数据 */
	public static <T> SList<T> randomDataList(SList<T> list,int num)
	{
		IntList indexList=new IntList(list.size());

		for(int i=0;i<list.length();++i)
		{
			indexList.add(i);
		}

		IntList resList=new IntList(num);

		for(int i=0;i<num;i++)
		{
			if(indexList.length()==0)
				break;

			int index = MathUtils.randomRange(0,indexList.length());
			resList.add(indexList.get(index));
			indexList.remove(index);
		}

		SList<T> resultList = new SList<>(indexList.length());

		for(int i=0;i<resList.length();++i)
		{
			resultList.add(list.get(resList.get(i)));
		}

		return resultList;
	}
}
