using System;
using System.Text;
using ShineEngine;

/// <summary>
/// 
/// </summary>
[Hotfix]
public class BaseLogicControl
{
	private ReplaceTextTool[] _replaceTools;

	private char[] _replaceMarkArr;

	public BaseLogicControl()
	{

	}

	/** 初始化 */
	public virtual void init()
	{
		_replaceTools=new ReplaceTextTool[ReplaceTextToolType.size];

		_replaceMarkArr=new char[2];
		_replaceMarkArr[0]=CommonSetting.replaceTextColorMark;
		_replaceMarkArr[1]=CommonSetting.replaceTextMark;

		//物品提示部分
		registReplaceTextTool(ReplaceTextToolType.ItemTips,GameC.factory.createItemTipsReplaceTextTool());
		registReplaceTextTool(ReplaceTextToolType.TaskDescribe,GameC.factory.createTaskDescribeReplaceTextTool());
	}


	/** 注册替换文字工具 */
	public void registReplaceTextTool(int type,ReplaceTextTool tool)
	{
		_replaceTools[type]=tool;
	}

	/** 创建使用物品参数数据 */
	public virtual UseItemArgData createUseItemArgData(int type)
	{
		return BaseC.logic.createUseItemArgData(type);
	}

	/** 创建任务数据 */
	public TaskData createTaskData(int type)
	{
		return GameC.factory.createTaskData();
	}

	/** 初始化物品 */
	public void initItem(ItemData data,int id)
	{

	}

	/** 创建物品数据 */
	public ItemData createItem(DIntData data)
	{
		return createItem(data.key,data.value);
	}

	/** 创建物品数据(如id<=0则为空) */
	public ItemData createItem(int id,int num)
	{
		if(id<=0)
			return null;

		ItemConfig config=ItemConfig.get(id);

		ItemData data=GameC.pool.createItemData(config.type);
		data.initIdentityByType(config.type);
		data.id=id;
		data.num=num;
		data.config=config;
		//失效时间
		data.disableTime=config.enableTimeT.getNextTime();

		//额外初始化
		initItem(data,id);

		return data;
	}

	/** 通过创建物品配置创建物品(可能为空) */
	public ItemData createItemByCreateID(int createID)
	{
		if(createID<=0)
			return null;

		CreateItemConfig createConfig=CreateItemConfig.get(createID);
		ItemData data=createItem(createConfig.itemID,1);
		data.isBind=createConfig.isBind;

		makeItemDataByCreateConfig(data,createConfig);

		return data;
	}

	/** 进一步构造物品 */
	protected void makeItemDataByCreateConfig(ItemData data,CreateItemConfig config)
	{

	}

	public virtual ItemIdentityData createItemIdentityByType(int type)
	{
		switch(type)
		{
			case ItemType.Equip:
			{
				return GameC.factory.createItemEquipData();
			}
			case ItemType.Stuff:
			case ItemType.Tool:
			{
				return GameC.factory.createItemIdentityData();
			}
			default:
			{
				return GameC.factory.createItemIdentityData();
			}
		}

		return null;
	}

	/** 随机一个物品(可能为空) */
	public ItemData randomItem(int randomItemConfigID,ILogicEntity entity,int seedType)
	{
		return RandomItemConfig.get(randomItemConfigID).randomOne(entity,seedType);
	}

	/** 随机一组物品 */
	public void randomItemList(SList<ItemData> list,int randItemListConfigID,ILogicEntity entity)
	{
		RandomItemListConfig.get(randItemListConfigID).randomList(list,entity);
	}


	/// <summary>
	/// 获取单位类型
	/// </summary>
	public virtual int getUnitType(int unitDataID)
	{
		switch(unitDataID)
		{
			case BaseDataType.CharacterIdentity:
				return UnitType.Character;
			case BaseDataType.MonsterIdentity:
				return UnitType.Monster;
			case BaseDataType.NPCIdentity:
				return UnitType.Npc;
			case BaseDataType.PetIdentity:
				return UnitType.Pet;
			case BaseDataType.PuppetIdentity:
				return UnitType.Puppet;
			case BaseDataType.VehicleIdentity:
				return UnitType.Vehicle;
			case BaseDataType.SceneEffectIdentity:
				return UnitType.SceneEffect;
		}

		return -1;
	}

	/** 替换字符串(颜色,文字标记) */
	public string replaceText(int type,string text,object obj)
	{
		ReplaceTextTool tool=_replaceTools[type];

		if(tool==null)
		{
			Ctrl.throwError("不该找不到替换文字工具");
			return text;
		}

		int pos=0;

		bool isColoring=false;
		bool isMatching=false;

		StringBuilder sb=null;

		while(true)
		{
			int index=text.IndexOfAny(_replaceMarkArr,pos);

			if(index!=-1)
			{
				if(sb==null)
					sb=StringBuilderPool.create();

				//颜色
				if(text[index]==CommonSetting.replaceTextColorMark)
				{
					sb.Append(text.slice(pos,index));

					//结束
					if(isColoring)
					{
						isColoring=false;
						addColorEnd(sb);
						pos=index+1;
					}
					//开始
					else
					{
						if(text.Length<=index + CommonSetting.replaceTextColorLength)
						{
							Ctrl.throwError("替换文本的字符串缺少");
						}

						isColoring=true;
						addColorFront(sb,text.Substring(index + 1,CommonSetting.replaceTextColorLength));
						pos=index+1+CommonSetting.replaceTextColorLength;
					}
				}
				else
				{
					//结束
					if(isMatching)
					{
						isMatching=false;
						sb.Append(tool.replace(text.slice(pos,index),obj));
						pos=index+1;
					}
					//开始
					else
					{
						sb.Append(text.slice(pos,index));

						isMatching=true;
						pos=index+1;
					}
				}
			}
			else
			{
				if(sb!=null)
				{
					sb.Append(text.Substring(pos));
				}

				break;
			}
		}

		//无需替换
		if(sb==null)
			return text;

		if(isColoring || isMatching)
		{
			StringBuilderPool.release(sb);
			Ctrl.throwError("替换文本的字符串未配置完整");
			return text;
		}

		return StringBuilderPool.releaseStr(sb);
	}

	/** 添加颜色头 */
	protected virtual void addColorFront(StringBuilder sb,string colorStr)
	{
		sb.Append("<color=\"#");
		sb.Append(colorStr);
		sb.Append("\">");
	}

	/** 添加颜色尾 */
	protected virtual void addColorEnd(StringBuilder sb)
	{
		sb.Append("</color>");
	}

	/** 计算属性公式 */
	public virtual int calculateAttribute(AttributeTool tool,int[] args)
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
	public virtual int calculateSkillVar(int formulaType,int[][] args,UnitFightDataLogic self,UnitFightDataLogic target,int[] selfValues,int start)
	{
		switch(formulaType)
		{
			case AttributeFormulaType.Single:
			{
				return getOneSVar(args,0,self,target,selfValues,start);
			}
			case AttributeFormulaType.Normal2:
			{
				return (int)(getOneSVar(args,0,self,target,selfValues,start)*
						getOneSVarAddRatio(args,1,self,target,selfValues,start));
			}
			case AttributeFormulaType.Normal3:
			{
				return (int)(getOneSVar(args,0,self,target,selfValues,start)*
						getOneSVarAddRatio(args,1,self,target,selfValues,start)+
						getOneSVar(args,2,self,target,selfValues,start));
			}
			case AttributeFormulaType.Normal4:
			{
				return (int)((getOneSVar(args,0,self,target,selfValues,start)*
						getOneSVarAddRatio(args,1,self,target,selfValues,start)+
						getOneSVar(args,2,self,target,selfValues,start))*
						getOneSVarAddRatio(args,3,self,target,selfValues,start));
			}
			case AttributeFormulaType.TwoPlus:
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

	private static int getOneSVar(int[][] args,int index,UnitFightDataLogic self,UnitFightDataLogic target,int[] selfValues,int start)
	{
		int[] arr=args[index];

		if(arr[0]==SkillVarSourceType.ConstValue)
			return arr[1];

		bool isTarget;

		if((isTarget=BaseC.constlist.skillVarSource_isTarget(arr[index])) || selfValues==null)
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
}