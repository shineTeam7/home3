using System;
using ShineEngine;

/// <summary>
/// 属性计算信息
/// </summary>
public class AttributeCalculateInfo
{
	/** 尺寸 */
	public int size;
	/** 当前属性组 */
	public int[] currentList;
	/** 当前属性对list序号组(current->index) */
	public int[] currentToIndex;
	/** 当前属性默认满组 */
	public bool[] currentDefaultFullSet;
	/** 当前属性是否可超过上限 */
	public bool[] currentCanOverMax;
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
	public bool[] needDispatchSet;

	/** 公式方法 */
	public Func<AttributeTool,int[],int> formulaFunc;

	/** 简版单位需要组 */
	public int[] simpleUnitList;
	/** 简版单位需要组 */
	public bool[] simpleUnitSet;

	public void init(SList<AttributeOneInfo> list,int size)
	{
		this.size=size;
		currentToIndex=new int[size];
		currentDefaultFullSet=new bool[size];
		currentCanOverMax=new bool[size];
		maxToCurrentMap=new int[size];
		currentToMaxMap=new int[size];
		formulaTypeDic=new int[size][];
		elementToResultDic=new int[size][];
		increaseToCurrentMap=new int[size];
		currentToIncreaseMap=new int[size];
		increaseToIndex=new int[size];

		needDispatchSet=new bool[size];
		simpleUnitSet=new bool[size];

		IntList tCurrentList=new IntList();
		IntList tFormulaResultList=new IntList();
		IntList tNeedDispatchList=new IntList();
		IntList tIncreaseList=new IntList();
		IntList tSimpleUnitList=new IntList();

		IntObjectMap<IntList> tElementToResultDic=new IntObjectMap<IntList>();

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

			if(v.formula!=null && v.formula.Length>0)
			{
				tFormulaResultList.add(type);
				formulaTypeDic[type]=v.formula;

				for(int j=1;j<v.formula.Length;j++)
				{
					tElementToResultDic.computeIfAbsent(v.formula[j],k=>new IntList()).add(type);
				}
			}

			//以客户端推送方式为准
			if(v.sendSelfType!=AttributeSendSelfType.None)
			{
				tNeedDispatchList.add(type);
				needDispatchSet[type]=true;
			}

			if(v.isSimpleUnitNeed)
			{
				tSimpleUnitList.add(type);
				simpleUnitSet[type]=true;
			}
		}

		currentList=tCurrentList.toArray();
		formulaResultList=tFormulaResultList.toArray();
		needDispatchList=tNeedDispatchList.toArray();
		increaseList=tIncreaseList.toArray();

		tElementToResultDic.forEach((k,v2)=>
		{
			elementToResultDic[k]=v2.toArray();
		});

		simpleUnitList=tSimpleUnitList.toArray();
	}

	/** 计算属性公式 */
	public int calculateAttribute(AttributeTool tool,int[] formula)
	{
		return formulaFunc(tool,formula);
	}
}