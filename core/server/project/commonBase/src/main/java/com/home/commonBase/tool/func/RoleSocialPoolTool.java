package com.home.commonBase.tool.func;

import com.home.commonBase.constlist.generate.FuncToolType;
import com.home.commonBase.data.func.FuncToolData;
import com.home.commonBase.data.role.RoleShowChangeData;
import com.home.commonBase.data.social.RoleSocialData;
import com.home.commonBase.data.social.RoleSocialPoolData;
import com.home.commonBase.data.social.RoleSocialPoolToolData;
import com.home.shine.support.collection.SList;

import java.util.Comparator;

/** 角色社交数据池插件 */
public abstract class RoleSocialPoolTool extends FuncTool
{
	/** 最大数 */
	protected int _maxNum;
	/** 裁剪间隔(s) */
	private int _cutDelay;
	
	/** 是否启用自定义社交数据 */
	protected boolean _useCustom;
	
	/** 数据 */
	protected RoleSocialPoolToolData _data;
	
	private Comparator<RoleSocialPoolData> _comparator;
	
	private SList<RoleSocialPoolData> _list=new SList<>(RoleSocialPoolData[]::new);
	
	public RoleSocialPoolTool(int funcID,int maxNum,int cutDelay,boolean useCustom)
	{
		super(FuncToolType.RoleSocialPool,funcID);
		
		_maxNum=maxNum;
		_cutDelay=cutDelay;
		_useCustom=useCustom;
		
		_comparator=this::cutCompare;
	}
	
	/** 是否启用自定义社交数据 */
	public boolean isUseCustom()
	{
		return _useCustom;
	}
	
	@Override
	protected void toSetData(FuncToolData data)
	{
		super.toSetData(data);
		
		_data=(RoleSocialPoolToolData)data;
	}
	
	@Override
	public void afterReadData()
	{
		super.afterReadData();
		
		_list.clear();
		_list.ensureCapacity(_data.dic.size());
		
		_data.dic.forEachValue(v->
		{
			_list.add(v);
		});
		
		_list.sort(_comparator);
	}
	
	public RoleSocialPoolToolData getData()
	{
		return _data;
	}
	
	@Override
	protected FuncToolData createToolData()
	{
		return new RoleSocialPoolToolData();
	}
	
	@Override
	public void onNewCreate()
	{
		super.onNewCreate();
		
		_data.nextCutTime=getTimeEntity().getTimeMillis()+(_cutDelay*1000);
	}
	
	@Override
	public void onSecond(int delay)
	{
		long now=getTimeEntity().getTimeMillis();
		
		if(now>=_data.nextCutTime)
		{
			_data.nextCutTime=now+(_cutDelay*1000);
			
			checkCut();
		}
	}
	
	/** 某玩家是否在其中 */
	public boolean hasPlayer(long playerID)
	{
		return _data.dic.contains(playerID);
	}
	
	/** 添加过滤 */
	protected abstract boolean addFilter(RoleSocialData data);
	
	/** 裁剪比较(要保留的在前，被裁剪的在后) */
	protected int cutCompare(RoleSocialPoolData d0,RoleSocialPoolData d1)
	{
		return -Long.compare(d0.inTime,d1.inTime);
	}
	
	/** 尝试添加一个数据 */
	public void addOne(RoleSocialData data)
	{
		RoleSocialPoolData pData=_data.dic.get(data.showData.playerID);
		
		if(pData!=null)
		{
			//更新时间戳
			pData.inTime=getTimeEntity().getTimeMillis();
			pData.data=data;
			return;
		}
		else
		{
			//可以添加
			if(addFilter(data))
			{
				pData=new RoleSocialPoolData();
				pData.data=data;
				pData.inTime=getTimeEntity().getTimeMillis();
				
				_data.dic.put(data.showData.playerID,pData);
				//添加到末尾
				_list.add(pData);
			}
		}
	}
	
	/** 刷新一个 */
	public void refreshOne(long playerID,RoleShowChangeData data)
	{
		RoleSocialPoolData pData=_data.dic.get(playerID);
		
		if(pData!=null)
		{
			//更新时间戳
			pData.inTime=getTimeEntity().getTimeMillis();
			pData.data.onChange(data);
		}
	}
	
	/** 删除一个 */
	private void removeOne(long playerID)
	{
		_data.dic.remove(playerID);
		
		onRemoveOne(playerID);
	}
	
	/** 移除一个 */
	protected void onRemoveOne(long playerID)
	{
	
	}
	
	/** 检查是否需要删除 */
	protected void checkCut()
	{
		if(_data.dic.size()>_maxNum)
		{
			cutOnce();
		}
	}
	
	/** 执行一次裁剪 */
	protected void cutOnce()
	{
		_list.clear();
		_list.ensureCapacity(_data.dic.size());
		RoleSocialPoolData[] values1=_list.getValues();
		
		int j=0;
		
		RoleSocialPoolData[] values;
		RoleSocialPoolData v;
		
		for(int i=(values=_data.dic.getValues()).length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				values1[j++]=v;
			}
		}
		
		_list.sort(_comparator);
		
		int len=_data.dic.size();
		
		for(int i=_maxNum;i<len;i++)
		{
			removeOne(values1[i].data.showData.playerID);
			values1[i]=null;
		}
		
		_list.justGet(_maxNum);
	}
	
	/** 获取数据列表 */
	public SList<RoleSocialPoolData> getDataList()
	{
		return _list;
	}
}
