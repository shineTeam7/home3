package com.home.commonBase.dataEx.role;

import com.home.commonBase.constlist.generate.AttributeSendSelfType;
import com.home.commonBase.tool.IAttributeTool;
import com.home.shine.support.collection.IntList;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.SList;

/** 属性计算信息 */
public abstract class AttributeCalculateInfo
{
	/** 尺寸 */
	public int size;
	/** 当前属性组 */
	public int[] currentList;
	/** 当前属性对list序号组(current->index) */
	public int[] currentToIndex;
	/** 当前属性默认满组 */
	public boolean[] currentDefaultFullSet;
	/** 当前属性是否可超过上限 */
	public boolean[] currentCanOverMax;
	/** 当前属性和上限的对应组(max->current) */
	public int[] maxToCurrentMap;
	/** 当前属性对上限的对应组(current->max) */
	public int[] currentToMaxMap;
	
	/** 自增属性组 */
	public int[] increaseList;
	/** 自增属性对序号组 */
	public int[] increaseToIndex;
	/** 自增属性对产出值组（increase->current） */
	public int[] increaseToCurrentMap;
	/** 产出值对自增属性组 (current->increase) */
	public int[] currentToIncreaseMap;
	
	/** 公式结果组 */
	public int[] formulaResultList;
	/** 公式组(无为空) */
	public int[][] formulaTypeDic;
	/** 因数对结果组(无为不需要) */
	public int[][] elementToResultDic;
	
	/** 需要派发属性列表 */
	public int[] needDispatchList;
	/** 需要派发改变set */
	public boolean[] needDispatchSet;
	
	//推送部分
	/** 全部可能推送组 */
	public int[] allMaybeSendList;
	/** 全部可能推送组对数组序号 */
	public int[] allMaybeSendToIndex;
	/** 全部可能推送组 */
	public boolean[] allMaybeSendSet;
	
	/** 实时推送自己总组 */
	public int[] sendSelfAllList;
	
	/** 实时推送自己组 */
	public int[] sendSelfAbsList;
	/** 实时推送自己组 */
	public boolean[] sendSelfAbsSet;
	
	/** 开关推送自己组 */
	public int[] sendSelfNormalList;
	/** 开关推送自己组 */
	public boolean[] sendSelfNormalSet;
	
	/** 推送其他角色组 */
	public int[] sendOtherList;
	/** 推送其他角色组 */
	public boolean[] sendOtherSet;
	
	/** 简版单位需要组 */
	public int[] simpleUnitList;
	/** 简版单位需要组 */
	public boolean[] simpleUnitSet;
	
	public void init(SList<AttributeOneInfo> list,int size)
	{
		this.size=size;
		currentToIndex=new int[size];
		currentDefaultFullSet=new boolean[size];
		currentCanOverMax=new boolean[size];
		maxToCurrentMap=new int[size];
		currentToMaxMap=new int[size];
		formulaTypeDic=new int[size][];
		elementToResultDic=new int[size][];
		increaseToCurrentMap=new int[size];
		currentToIncreaseMap=new int[size];
		increaseToIndex=new int[size];
		
		allMaybeSendToIndex=new int[size];
		allMaybeSendSet=new boolean[size];
		needDispatchSet=new boolean[size];
		sendSelfAbsSet=new boolean[size];
		sendSelfNormalSet=new boolean[size];
		sendOtherSet=new boolean[size];
		simpleUnitSet=new boolean[size];
		
		IntList tCurrentList=new IntList();
		IntList tFormulaResultList=new IntList();
		IntList tNeedDispatchList=new IntList();
		IntList tIncreaseList=new IntList();
		
		IntObjectMap<IntList> tElementToResultDic=new IntObjectMap<>();
		
		//推送组
		IntList tAllMaybeSendList=new IntList();
		IntList tSendSelfAbsList=new IntList();
		IntList tSendSelfNormalList=new IntList();
		IntList tSendSelfAllList=new IntList();
		IntList tSendOtherList=new IntList();
		IntList tSimpleUnitList=new IntList();
		
		
		AttributeOneInfo[] values=list.getValues();
		AttributeOneInfo v;
		
		for(int i=0,len=list.size();i<len;++i)
		{
			v=values[i];
			
			int type=v.id;
			
			//是当前属性
			if(v.currentMaxID>0)
			{
				currentToIndex[type]=tCurrentList.size();
				tCurrentList.add(type);
				maxToCurrentMap[v.currentMaxID]=type;
				currentToMaxMap[type]=v.currentMaxID;
				
				if(v.isCurrentDefaultFull)
				{
					currentDefaultFullSet[type]=true;
				}
				
				if(v.isCurrentCanOverMax)
				{
					currentCanOverMax[type]=true;
				}
			}
			
			if(v.increaseID>0)
			{
				increaseToIndex[type]=tIncreaseList.size();
				tIncreaseList.add(type);
				increaseToCurrentMap[type]=v.increaseID;
				currentToIncreaseMap[v.increaseID]=type;
			}
			
			if(v.formula!=null && v.formula.length>0)
			{
				tFormulaResultList.add(type);
				formulaTypeDic[type]=v.formula;
				
				for(int j=1;j<v.formula.length;j++)
				{
					tElementToResultDic.computeIfAbsent(v.formula[j],k->new IntList()).add(type);
				}
			}
			
			if(v.needDispatchChange)
			{
				tNeedDispatchList.add(type);
				needDispatchSet[type]=true;
			}
			
			if(v.sendSelfType==AttributeSendSelfType.Abs)
			{
				tSendSelfAbsList.add(type);
				sendSelfAbsSet[type]=true;
				
				tSendSelfAllList.add(type);
			}
			else if(v.sendSelfType==AttributeSendSelfType.Normal)
			{
				tSendSelfNormalList.add(type);
				sendSelfNormalSet[type]=true;
				
				tSendSelfAllList.add(type);
			}
			
			if(v.needSendOther)
			{
				tSendOtherList.add(type);
				sendOtherSet[type]=true;
			}
			
			if(v.sendSelfType!=AttributeSendSelfType.None || v.needSendOther)
			{
				allMaybeSendToIndex[type]=tAllMaybeSendList.size();
				tAllMaybeSendList.add(type);
				allMaybeSendSet[type]=true;
			}
			
			if(v.isSimpleUnitNeed)
			{
				simpleUnitSet[type]=true;
				tSimpleUnitList.add(type);
			}
		}
		
		currentList=tCurrentList.toArray();
		formulaResultList=tFormulaResultList.toArray();
		needDispatchList=tNeedDispatchList.toArray();
		increaseList=tIncreaseList.toArray();
		
		tElementToResultDic.forEach((k,v2)->
		{
			elementToResultDic[k]=v2.toArray();
		});
		
		
		//推送组
		allMaybeSendList=tAllMaybeSendList.toArray();
		sendSelfAbsList=tSendSelfAbsList.toArray();
		sendSelfNormalList=tSendSelfNormalList.toArray();
		sendSelfAllList=tSendSelfAllList.toArray();
		sendOtherList=tSendOtherList.toArray();
		simpleUnitList=tSimpleUnitList.toArray();
	}
	
	/** 计算属性公式 */
	abstract public int calculateAttribute(IAttributeTool tool,int[] formula);
}
