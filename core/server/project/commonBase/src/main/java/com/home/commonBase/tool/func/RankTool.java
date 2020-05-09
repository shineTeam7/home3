package com.home.commonBase.tool.func;

import com.home.commonBase.constlist.generate.FuncToolType;
import com.home.commonBase.data.func.FuncToolData;
import com.home.commonBase.data.social.rank.RankData;
import com.home.commonBase.data.social.rank.RankToolData;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.collection.LongObjectMap;
import com.home.shine.support.collection.SList;

import java.util.Arrays;
import java.util.Comparator;

/** 排行插件基类 */
public abstract class RankTool extends FuncTool implements IRankTool
{
	/** 最大尺寸 */
	private int _maxNum;
	/** 最小允许排行值 */
	protected long _valueMin;
	/** 数据 */
	protected RankToolData _data;
	/** 当前字典 */
	private LongObjectMap<RankData> _dic=new LongObjectMap<>(RankData[]::new);
	
	/** 排行值下限 */
	private volatile long _valueLimit;
	
	private Comparator<RankData> _comparator;
	
	private RankData _tempData;
	
	/** 是否需要反向compare */
	protected boolean _needReverseCompare=false;
	
	public RankTool(int funcID,int maxNum,int valueMin)
	{
		super(FuncToolType.Rank,funcID);
		
		_maxNum=maxNum;
		_valueLimit=_valueMin=valueMin;
		
		_comparator=this::compare;
		
		_tempData=toCreateRankData();
	}
	
	/** 设置是否需要反向compare */
	public void needReverseCompare(boolean value)
	{
		_needReverseCompare=value;
	}
	
	@Override
	protected void toSetData(FuncToolData data)
	{
		super.toSetData(data);
		
		_data=(RankToolData)data;
	}
	
	@Override
	public void afterReadDataSecond()
	{
		super.afterReadDataSecond();
		
		readDicByList(_dic,_data.list);
	}
	
	public RankToolData getData()
	{
		return _data;
	}
	
	@Override
	protected FuncToolData createToolData()
	{
		return new RankToolData();
	}
	
	@Override
	public void onNewCreate()
	{
		super.onNewCreate();
		
		_data.version=1;
	}
	
	/** 清空榜(包括修改数据) */
	public void clear()
	{
		_data.list.clear();
		_dic.clear();
	}
	
	/** 获取版本 */
	public int getVersion()
	{
		return _data.version;
	}
	
	/** 获取下限匹配值 */
	public long getValueLimit()
	{
		return _valueLimit;
	}
	
	/** 通过list读取数据到dic */
	private void readDicByList(LongObjectMap<RankData> dic,SList<RankData> list)
	{
		dic.clear();
		
		RankData[] values=list.getValues();
		RankData v;
		
		for(int i=list.length()-1;i>=0;--i)
		{
			(v=values[i]).rank=i+1;
			dic.put(v.key,v);
		}
		
		if(_maxNum>0 && list.size()==_maxNum)
		{
			_valueLimit=list.getLast().value;
		}
		else
		{
			_valueLimit=_valueMin;
		}
	}
	
	/** 获取排行数据 */
	public SList<RankData> getList()
	{
		return _data.list;
	}
	
	/** 获取某key当前排行数据(没有返回null) */
	public RankData getRankData(long key)
	{
		return _dic.get(key);
	}
	
	/** 获取某key的排名 */
	public int getRank(long key)
	{
		RankData data;
		
		if((data=_dic.get(key))!=null)
		{
			return data.rank;
		}
		
		return -1;
	}
	
	/** 提交排行数据 */
	public void commitRank(int version,long key,long value,long[] args)
	{
		int re=toCommitRank(version,key,value,args,null);
		
		afterCommitRank(key,re,value);
	}
	
	/** 直接提交排行数据(工具用) */
	public void commitRank(int version,RankData data)
	{
		int re=toCommitRank(version,data.key,data.value,null,data);
		
		afterCommitRank(data.key,re,data.value);
	}
	
	/** 提交排行数据(返回排名)(vData有值时，key,value,args失效) */
	protected int toCommitRank(int version,long key,long value,long[] args,RankData vData)
	{
		//赛季已过
		if(version!=_data.version)
			return -1;
		
		RankData oldData;
		RankData data;
		
		RankData tempData;
		
		if(vData!=null)
		{
			(tempData=_tempData).copy(vData);
		}
		else
		{
			(tempData=_tempData).key=key;
			tempData.value=value;
			makeRankData(tempData,args);
		}
		
		if((oldData=_dic.get(key))==null)
		{
			if(value<_valueLimit)
				return -1;
			
			int insertIndex=getInsertIndex(tempData);
			
			//如相同往后排
			int index=insertIndex<0 ? -insertIndex-1 : insertIndex+1;
			
			SList<RankData> list=_data.list;
			
			//有上限,出榜
			if(_maxNum>0 && index>=_maxNum)
			{
				return -1;
			}
			else
			{
				if(vData!=null)
				{
					data=vData;
				}
				else
				{
					data=toCreateRankData();
					data.key=key;
					data.value=value;
					makeRankData(data,args);
				}
				
				list.insert(index,data);
				
				RankData[] values=list.getValues();
				int len=list.size();
				
				//修改名次
				for(int i=index;i<len;++i)
				{
					values[i].rank=i+1;
				}
				
				//添加dic
				_dic.put(data.key,data);
				
				//有上限
				if(_maxNum>0)
				{
					if(list.size()>_maxNum)
					{
						//移除
						RankData pop=list.pop();
						_dic.remove(pop.key);
					}
					
					//满了
					if(list.size()==_maxNum)
					{
						_valueLimit=list.getLast().value;
					}
				}
				
				return data.rank;
			}
		}
		else
		{
			int oldRank=oldData.rank;
			
			if(vData!=null)
			{
				//更新插入数据
				oldData=vData;
				_dic.put(key,vData);
			}
			
			int rank=toRefreshData(oldData,tempData,oldRank);
			//更新匹配值
			oldData.value=value;
			
			if(vData==null)
			{
				makeRankData(oldData,args);
			}
			
			return rank;
		}
	}
	
	/** 获取插入的位置 */
	private int getInsertIndex(RankData data)
	{
		SList<RankData> list=_data.list;
		
		//int re=Arrays.binarySearch(list.getValues(),0,list.size(),data,_comparator);
		//return re<0 ? -re-1 : re+1;
		
		return Arrays.binarySearch(list.getValues(),0,list.size(),data,_comparator);
	}
	
	/** 刷新方法(data,要插入数据,temp:比较数据,oldRank:旧排名) */
	private int toRefreshData(RankData data,RankData tempData,int oldRank)
	{
		int oldIndex=oldRank-1;
		
		SList<RankData> list=_data.list;
		
		if(ShineSetting.openCheck)
		{
			if(list.get(oldIndex).key!=data.key)
			{
				Ctrl.throwError("出错,数据错误");
			}
		}
		
		int insertIndex=getInsertIndex(tempData);
		
		int index;
		
		if(insertIndex<0)
		{
			insertIndex=-insertIndex-1;
			
			if(insertIndex<oldIndex)
			{
				index=insertIndex;
			}
			else if(insertIndex>oldIndex+1)
			{
				index=insertIndex-1;
			}
			else
			{
				index=oldIndex;
			}
		}
		else
		{
			index=insertIndex;
		}
		
		int rank=index+1;
		
		RankData[] values=list.getValues();
		
		if(index!=oldIndex)
		{
			//掉
			if(index>oldIndex)
			{
				for(int i=oldIndex;i<index;++i)
				{
					(values[i]=values[i+1]).rank=i+1;
				}
			}
			else
			{
				for(int i=oldIndex;i>index;--i)
				{
					(values[i]=values[i-1]).rank=i+1;
				}
			}
			
			(values[index]=data).rank=rank;
			
			//有上限
			if(_maxNum>0 && list.size()==_maxNum)
			{
				_valueLimit=list.getLast().value;
			}
		}
		
		return rank;
	}
	
	/** 删除排行数据(主线程) */
	public void removeRankData(int version,long key)
	{
		if(version!=_data.version)
			return;
		
		removeData(key);
	}
	
	/** 删除数据(删号用) */
	private void removeData(long key)
	{
		RankData oldData=_dic.get(key);
		
		//没有数据
		if(oldData==null)
			return;
		
		SList<RankData> list=_data.list;
		int oldIndex=oldData.rank-1;
		
		if(ShineSetting.openCheck)
		{
			if(list.get(oldIndex).key!=oldData.key)
			{
				Ctrl.throwError("出错,数据错误");
			}
		}
		
		//dic移除
		_dic.remove(key);
		
		list.remove(oldIndex);
		
		RankData[] values=list.getValues();
		int len=list.size();
		
		//修改名次
		for(int i=oldIndex;i<len;++i)
		{
			values[i].rank=i+1;
		}
		
		if(_maxNum>0)
		{
			//归零
			_valueLimit=_valueMin;
		}
		
		//移除
		afterCommitRank(key,-1,0);
	}
	
	/** 重置 */
	public void reset()
	{
		_data.version++;
		
		beforeReset();
		
		_data.list.clear();
		_dic.clear();
	}
	
	/** 比较方法(可override) */
	protected int compare(RankData arg0,RankData arg1)
	{
		int re = -Long.compare(arg0.value,arg1.value);
		
		if(re!=0)
		{
			return _needReverseCompare?-re:re;
		}
		
		return Long.compare(arg0.key,arg1.key);
	}
	
	/** 创建rankData(只创建类) */
	protected abstract RankData toCreateRankData();
	/** 构造rank数据组 */
	protected abstract void makeRankData(RankData data,long[] args);
	
	/** 提交排行后 */
	protected abstract void afterCommitRank(long key,int rank,long value);
	
	protected abstract void beforeReset();
	
	//测试
	
	///** 输出排行榜(测试用) */
	//private void printRankList()
	//{
	//	Ctrl.print("输出排行榜");
	//	_data.list.forEachA(k->
	//	{
	//		Ctrl.print("data:",k.toString());
	//	});
	//}
}
