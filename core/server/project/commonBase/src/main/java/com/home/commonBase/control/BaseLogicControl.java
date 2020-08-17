package com.home.commonBase.control;

import com.home.commonBase.config.game.CreateItemConfig;
import com.home.commonBase.config.game.FlowStepConfig;
import com.home.commonBase.config.game.ItemConfig;
import com.home.commonBase.config.game.RandomItemConfig;
import com.home.commonBase.config.game.RandomItemListConfig;
import com.home.commonBase.constlist.generate.AttributeFormulaType;
import com.home.commonBase.constlist.generate.ItemType;
import com.home.commonBase.constlist.generate.MailType;
import com.home.commonBase.constlist.generate.RewardLevelFormulaType;
import com.home.commonBase.constlist.generate.SkillVarFormulaType;
import com.home.commonBase.constlist.generate.SkillVarSourceType;
import com.home.commonBase.data.item.ItemData;
import com.home.commonBase.data.item.ItemIdentityData;
import com.home.commonBase.data.item.PlayerMailData;
import com.home.commonBase.data.item.UseItemArgData;
import com.home.commonBase.data.mail.MailData;
import com.home.commonBase.data.quest.TaskData;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.logic.LogicEntity;
import com.home.commonBase.logic.unit.UnitFightDataLogic;
import com.home.commonBase.tool.IAttributeTool;
import com.home.commonBase.utils.BaseGameUtils;
import com.home.shine.constlist.SLogType;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.data.DIntData;
import com.home.shine.dataEx.LogInfo;
import com.home.shine.support.collection.SList;
import com.home.shine.support.collection.SSet;
import com.home.shine.timer.ITimeEntity;

/** base逻辑控制 */
public class BaseLogicControl
{
	/** 添加账号流程日志 */
	public void addFlowLog(LogInfo info,String uid,int step)
	{
		addFlowLog(info,uid,-1L,step);
	}
	
	/** 添加账号流程日志 */
	public void addFlowLog(LogInfo info,String uid,long playerID,int step)
	{
		info.putHead("flow");
		info.put("uid",uid);
		
		if(playerID>0)
		{
			info.put("playerID",playerID);
		}
		
		info.put("step",step);
		
		if(FlowStepConfig.get(step)!=null)
			info.put("describe",FlowStepConfig.get(step).describe);
		
		Ctrl.toLog(info.getStringAndClear(),SLogType.Action,1);
		
		BaseGameUtils.recordStep(step);
	}
	
	/** 创建邮件数据 */
	public MailData createMailData(int type)
	{
		switch(type)
		{
			case MailType.PlayerMail:
			{
				return new PlayerMailData();
			}
		}

		return BaseC.factory.createMailData();
	}

	/** 创建任务数据 */
	public TaskData createTaskData(int type)
	{
		return BaseC.factory.createTaskData();
	}

	/** 创建使用物品参数数据 */
	public UseItemArgData createUseItemArgData(int type)
	{
		return BaseC.factory.createUseItemArgData();
	}

	/** 初始化物品 */
	public void initItem(ItemData data,int id)
	{

	}
	
	/** 创建物品数据 */
	public ItemData createItem(DIntData data,ITimeEntity entity)
	{
		return createItem(data.key,data.value,entity);
	}
	
	/** 创建物品数据(如id<=0则为空) */
	public ItemData createItem(int id,int num,ITimeEntity entity)
	{
		if(id<=0)
			return null;
		
		ItemConfig config=ItemConfig.get(id);
		
		ItemData data=BaseC.factory.createItemData();
		data.initIdentityByType(config.type);
		data.id=id;
		data.num=num;
		data.config=config;
		//失效时间
		data.disableTime=config.enableTimeT.getNextTime(entity);
		
		//额外初始化
		initItem(data,id);
		
		return data;
	}
	
	/** 通过创建物品配置创建物品(可能为空) */
	public ItemData createItemByCreateID(int createID,ITimeEntity entity)
	{
		if(createID<=0)
			return null;
		
		CreateItemConfig createConfig=CreateItemConfig.get(createID);
		ItemData data=createItem(createConfig.itemID,1,entity);
		data.isBind=createConfig.isBind;
		
		makeItemDataByCreateConfig(data,createConfig);
		
		return data;
	}
	
	/** 进一步构造物品 */
	protected void makeItemDataByCreateConfig(ItemData data,CreateItemConfig config)
	{
	
	}

	/** 通过类型创建物品身份数据 */
	public ItemIdentityData createItemIdentityByType(int type)
	{
		switch(type)
		{
			case ItemType.Equip:
			{
				return BaseC.factory.createEquipData();
			}
			case ItemType.Stuff:
			case ItemType.Tool:
			{
				return BaseC.factory.createItemIdentityData();
			}
		}

		return null;
	}
	
	/** 随机一个物品(可能为空) */
	public ItemData randomItem(int randomItemConfigID,LogicEntity entity,int seedType)
	{
		return RandomItemConfig.get(randomItemConfigID).randomOne(entity,seedType);
	}
	
	/** 随机一组物品 */
	public void randomItemList(SList<ItemData> list,int randItemListConfigID,LogicEntity entity)
	{
		RandomItemListConfig.get(randItemListConfigID).randomList(list,entity);
	}
	
	/** 计算属性公式 */
	public int calculateAttribute(IAttributeTool tool,int[] args)
	{
		switch(args[0])
		{
			case AttributeFormulaType.Single:
			{
				return tool.getAttribute(args[1]);
			}
			case AttributeFormulaType.Normal2:
			{
				return (int)(tool.getAttribute(args[1])*tool.getAddRatio(args[2]));
			}
			case AttributeFormulaType.Normal3:
			{
				return (int)(tool.getAttribute(args[1])*tool.getAddRatio(args[2])+tool.getAttribute(args[3]));
			}
			case AttributeFormulaType.Normal4:
			{
				return (int)((tool.getAttribute(args[1])*tool.getAddRatio(args[2])+tool.getAttribute(args[3]))*tool.getAddRatio(args[4]));
			}
			case AttributeFormulaType.TwoPlus:
			{
				return tool.getAttribute(args[1])+tool.getAttribute(args[2]);
			}
		}
		
		return 0;
	}
	
	/** 计算属性公式 */
	public int calculateSkillVar(int formulaType,int[][] args,UnitFightDataLogic self,UnitFightDataLogic target,int[] selfValues,int start)
	{
		switch(formulaType)
		{
			case SkillVarFormulaType.Single:
			{
				return getOneSVar(args,0,self,target,selfValues,start);
			}
			case SkillVarFormulaType.Normal2:
			{
				return (int)(getOneSVar(args,0,self,target,selfValues,start)*
						getOneSVarAddRatio(args,1,self,target,selfValues,start));
			}
			case SkillVarFormulaType.Normal3:
			{
				return (int)(getOneSVar(args,0,self,target,selfValues,start)*
						getOneSVarAddRatio(args,1,self,target,selfValues,start)+
						getOneSVar(args,2,self,target,selfValues,start));
			}
			case SkillVarFormulaType.Normal4:
			{
				return (int)((getOneSVar(args,0,self,target,selfValues,start)*
						getOneSVarAddRatio(args,1,self,target,selfValues,start)+
						getOneSVar(args,2,self,target,selfValues,start))*
						getOneSVarAddRatio(args,3,self,target,selfValues,start));
			}
			case SkillVarFormulaType.TwoPlus:
			{
				return getOneSVar(args,0,self,target,selfValues,start)+
						getOneSVar(args,1,self,target,selfValues,start);
			}
		}

		return 0;
	}
	
	private static float getOneSVarAddRatio(int[][] args,int index,UnitFightDataLogic self,UnitFightDataLogic target,int[] selfValues,int start)
	{
		int re=getOneSVar(args,index,self,target,selfValues,start);
		
		if(re==0)
			return 1f;
		
		return (re+1000f)/1000f;
	}
	
	protected static int getOneSVar(int[][] args,int index,UnitFightDataLogic self,UnitFightDataLogic target,int[] selfValues,int start)
	{
		int[] arr=args[index];
		
		if(arr[0]==SkillVarSourceType.ConstValue)
			return arr[1];
		
		boolean isTarget;
		
		if((isTarget=BaseC.constlist.skillVarSource_isTarget(arr[0])) || selfValues==null)
		{
			if(isTarget)
			{
				if(target==null)
					return 0;
				
				return target.getSkillVarSourceValue(arr,false);
			}
			else
			{
				if(self==null)
					return 0;
				
				return self.getSkillVarSourceValue(arr,true);
			}
		}
		else
		{
			return selfValues[start+index];
		}
	}
	
	/** 计算等级奖励 */
	protected long toCalculateRewardLevel(long value,int level,int[] args)
	{
		switch(args[0])
		{
			case RewardLevelFormulaType.Single:
			{
				return value;
			}
			case RewardLevelFormulaType.Line:
			{
				return value+(level-args[1])*args[2];
			}
		}
		
		return value;
	}
	
	/** 计算等级奖励 */
	public int calculateRewardLevel(int value,int level,int[] args)
	{
		long re=toCalculateRewardLevel(value,level,args);
		
		if(re<0)
			re=0;
		
		return (int)re;
	}
	
	/** 计算等级奖励 */
	public long calculateRewardLevel(long value,int level,int[] args)
	{
		long re=toCalculateRewardLevel(value,level,args);
		
		if(re<0)
			re=0;
		
		return re;
	}

	/** 从logicID上反解areaID(创建服) */
	public int getAreaIDByLogicID(long logicID)
	{
		return (int)(logicID/CommonSetting.areaRegistMax);
	}
}
