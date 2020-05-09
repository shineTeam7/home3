package com.home.commonBase.logic.unit;

import com.home.commonBase.config.game.CDConfig;
import com.home.commonBase.config.game.SkillConfig;
import com.home.commonBase.config.game.SkillLevelConfig;
import com.home.commonBase.data.scene.base.CDData;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.dataEx.VBoolean;
import com.home.shine.dataEx.VInt;
import com.home.shine.support.collection.IntIntMap;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.SList;

/** 冷却时间数据逻辑 */
public class CDDataLogic
{
	protected UnitFightDataLogic _parent;
	
	/** CD组 */
	private IntObjectMap<CDData> _cds;
	/** 最长组CD */
	protected IntObjectMap<CDData> _groupMaxCDs=new IntObjectMap<>(CDData[]::new);
	/** 组CD计数 */
	private IntIntMap _groupCDCount=new IntIntMap();
	/** 组CD改变千分比组 */
	private IntIntMap _groupCDChangePercents=new IntIntMap();
	/** 组CD改变值组 */
	private IntIntMap _groupCDChangeValues=new IntIntMap();
	
	public void setParent(UnitFightDataLogic parent)
	{
		_parent=parent;
	}
	
	public void setData(IntObjectMap<CDData> cds)
	{
		_cds=cds;
		
		if(cds!=null && !cds.isEmpty())
		{
			IntObjectMap<CDData> groupMaxCDs=_groupMaxCDs;
			IntIntMap groupCDCount=_groupCDCount;
			
			cds.forEachValue(data->
			{
				int lastTime=data.getLastTime();
				
				CDConfig config=data.config=CDConfig.get(data.id);
				
				CDData oldMax;
				
				for(int v : config.groups)
				{
					groupCDCount.addValue(v,1);
					
					if((oldMax=groupMaxCDs.get(v))==null || oldMax.getLastTime()<lastTime)
					{
						groupMaxCDs.put(v,data);
					}
				}
			});
		}
	}
	
	public void clear()
	{
		_cds.clear();
		_groupMaxCDs.clear();
		_groupCDCount.clear();
		_groupCDChangePercents.clear();
		_groupCDChangeValues.clear();
	}
	
	public void reloadConfig()
	{
		_cds.forEachValue(v->
		{
			v.reloadConfig();
		});
	}
	
	/** 一秒十次 */
	public void onPiece(int delay)
	{
		IntObjectMap<CDData> cds;
		
		if((cds=_cds).isEmpty())
			return;
		
		IntObjectMap<CDData> groupMaxCDs=_groupMaxCDs;
		IntIntMap groupCDCount=_groupCDCount;
		UnitFightDataLogic parent=_parent;
		
		cds.forEachValueS(data->
		{
			if((data.timePass+=delay) >= data.timeMax)
			{
				cds.remove(data.id);
				
				//移除对应组CD
				for(int gv:data.config.groups)
				{
					groupCDCount.addValue(gv,-1);
					
					if(groupMaxCDs.get(gv)==data)
					{
						groupMaxCDs.remove(gv);
						
						parent.onEndGroupCD(gv);
					}
				}
			}
		});
	}
	
	/** 获取CD */
	public IntObjectMap<CDData> getCDs()
	{
		return _cds;
	}
	
	//cd部分
	
	/** 增加组CD时间上限千分比 */
	public void addGroupTimeMaxPercent(int groupID,int value)
	{
		_groupCDChangePercents.addValue(groupID,value);
		
		_parent.sendAddGroupTimeMaxPercent(groupID,value);
	}
	
	/** 增加组CD时间上限值 */
	public void addGroupTimeMaxValue(int groupID,int value)
	{
		_groupCDChangeValues.addValue(groupID,value);
		
		_parent.sendAddGroupTimeMaxValue(groupID,value);
	}
	
	/** 增加当前组CD时间经过(cd减少) */
	public void addGroupTimePass(int groupID,int value)
	{
		if(value==0)
			return;
		
		if(_cds.isEmpty())
			return;
		
		//只添加,触发还是基于onTen
		
		IntIntMap groupCDCount=_groupCDCount;
		
		boolean has=false;
		boolean needReCount=false;
		
		CDData[] values=_cds.getValues();
		CDData data;
		int vv;
		
		for(int i=values.length - 1;i >= 0;--i)
		{
			if((data=values[i])!=null)
			{
				if(data.config.hasGroup(groupID))
				{
					has=true;
					
					vv=(data.timePass) + value;
					
					if(vv>data.timeMax)
					{
						vv=data.timeMax;
					}
					
					if(vv<0)
					{
						vv=0;
					}
					
					data.timePass=vv;
					
					if(data.config.groups.length>1)
					{
						for(int gv:data.config.groups)
						{
							if(gv!=groupID)
							{
								//不止这个
								if(groupCDCount.get(gv)>1)
								{
									needReCount=true;
								}
							}
						}
					}
					
				}
			}
		}
		
		if(has)
		{
			
			if(needReCount)
			{
				reCountGroupCDMax();
			}
			
			//推送
			_parent.sendAddGroupTimePass(groupID,value);
		}
	}
	
	/** 根据keep移除cd组(不推送) */
	public void removeCDByKeep(int keepType)
	{
		VBoolean has=new VBoolean();
		IntObjectMap<CDData> cds=_cds;
		IntObjectMap<CDData> groupMaxCDs=_groupMaxCDs;
		IntIntMap groupCDCount=_groupCDCount;
		UnitFightDataLogic parent=_parent;
		VInt executeNum=new VInt();
		
		cds.forEachValueS(data->
		{
			//大于等于的移除
			if(data.config.keepType>=keepType)
			{
				has.value=true;
				cds.remove(data.id);
				
				//移除对应组CD
				for(int gv:data.config.groups)
				{
					if(groupMaxCDs.get(gv)==data)
					{
						groupMaxCDs.remove(gv);
						++executeNum.value;
					}
					
					if(groupCDCount.addValue(gv,-1)<=0)
					{
						--executeNum.value;
						parent.onEndGroupCD(gv);
					}
				}
			}
		});
		
		if(has.value)
		{
			if(executeNum.value!=0)
			{
				reCountGroupCDMax();
			}
			
			//不推送,因不需要
		}
	}
	
	/** 添加CD组 */
	public void reAddCDs(IntObjectMap<CDData> dic,int dTime)
	{
		if(dic.isEmpty())
			return;
		
		IntObjectMap<CDData> cds=_cds;
		
		if(!cds.isEmpty())
		{
			Ctrl.throwError("这时cd组不应该有值");
		}
		
		IntObjectMap<CDData> groupMaxCDs=_groupMaxCDs;
		IntIntMap groupCDCount=_groupCDCount;
		UnitFightDataLogic parent=_parent;
		
		dic.forEachValue(v->
		{
			v.config=CDConfig.get(v.id);
			v.timePass+=dTime;
			
			//依然有效
			if(v.timePass<v.timeMax)
			{
				cds.put(v.id,v);
				
				for(int gv:v.config.groups)
				{
					groupCDCount.addValue(gv,1);
					
					CDData oldMax=groupMaxCDs.get(gv);
					
					if(oldMax==null || oldMax.getLastTime()<v.getLastTime())
					{
						groupMaxCDs.put(gv,v);
					}
					
					//新的
					if(oldMax==null)
					{
						parent.onStartGroupCD(gv);
					}
				}
			}
		});
		
		//不推送
	}
	
	/** 重新计算组CD上限 */
	private void reCountGroupCDMax()
	{
		IntObjectMap<CDData> groupMaxCDs=_groupMaxCDs;
		
		_cds.forEachValue(data->
		{
			int lastTime=data.getLastTime();
			CDData gData;
			
			for(int gv:data.config.groups)
			{
				if((gData=groupMaxCDs.get(gv))==null || gData.getLastTime()<lastTime)
				{
					groupMaxCDs.put(gv,data);
				}
			}
		});
	}
	
	/** 移除组cd */
	public void removeGroupCD(int groupID)
	{
		VBoolean has=new VBoolean();
		IntObjectMap<CDData> cds=_cds;
		IntObjectMap<CDData> groupMaxCDs=_groupMaxCDs;
		IntIntMap groupCDCount=_groupCDCount;
		UnitFightDataLogic parent=_parent;
		VInt executeNum=new VInt();
		
		cds.forEachValueS(data->
		{
			if(data.config.hasGroup(groupID))
			{
				has.value=true;
				
				cds.remove(data.id);
				
				for(int gv:data.config.groups)
				{
					if(groupMaxCDs.get(gv)==data)
					{
						groupMaxCDs.remove(gv);
						++executeNum.value;
					}
					
					if(groupCDCount.addValue(gv,-1)<=0)
					{
						--executeNum.value;
						parent.onEndGroupCD(gv);
					}
				}
			}
		});
		
		if(has.value)
		{
			if(executeNum.value!=0)
			{
				reCountGroupCDMax();
			}
			
			//推送
			_parent.sendRemoveGroupCD(groupID);
		}
	}
	
	//技能CD部分
	
	/** 获取某个技能的技能数据(最长的一个) */
	public CDData getSkillCD(int skillID)
	{
		int maxLastTime=0;
		CDData maxCDData=null;
		
		SkillConfig config=SkillConfig.get(skillID);
		
		CDData data;
		int lastTime;
		
		IntObjectMap<CDData> groupMaxCDs=_groupMaxCDs;
		
		for(int v : config.underGroupCDs)
		{
			if((data=groupMaxCDs.get(v))!=null)
			{
				lastTime=data.getLastTime();
				
				if(lastTime>maxLastTime)
				{
					maxCDData=data;
				}
			}
		}
		
		return maxCDData;
	}
	
	/** 获取某技能剩余冷却时间(为0就是没冷却) */
	public int getSkillLastCD(int id)
	{
		CDData cdData=getSkillCD(id);
		
		return cdData!=null ? cdData.getLastTime() : 0;
	}
	
	/** 获取技能冷却时间是否结束 */
	public boolean isSkillReady(int id)
	{
		return getSkillLastCD(id)==0;
	}
	
	/** 获取某组剩余CD */
	public int getGroupCD(int id)
	{
		CDData cdData=_groupMaxCDs.get(id);
		
		return cdData!=null ? cdData.getLastTime() : 0;
	}
	
	/** 是否在某组cd中 */
	public boolean isInGroupCD(int id)
	{
		return getGroupCD(id)>0;
	}
	
	/** 开始一个技能CD */
	public void startSkillCD(int skillID,int skillLevel,boolean needSend)
	{
		CDData data;
		
		SList<CDData> datas=null;
		
		if(needSend)
		{
			datas=new SList<>();
		}
		
		for(int v : SkillLevelConfig.get(skillID,skillLevel).bringCDs)
		{
			if((data=toStartOneCD(v))!=null)
			{
				if(needSend)
				{
					datas.add(data);
				}
			}
		}
		
		if(needSend && !datas.isEmpty())
		{
			_parent.sendStartCDs(datas);
		}
	}
	
	protected CDData toStartOneCD(int id)
	{
		CDConfig config=CDConfig.get(id);
		
		return toStartOneCD(config,config.cd);
	}
	
	private CDData toStartOneCD(CDConfig config,int time)
	{
		int id=config.id;
		int groupChangePercent=0;
		int groupChangeValue=0;
		
		//千分比和数值均累加
		for(int gv : config.groups)
		{
			groupChangePercent+=_groupCDChangePercents.get(gv);
			groupChangeValue+=_groupCDChangeValues.get(gv);
		}
		
		int timeMax=(int)(time * ((1000f + groupChangePercent) / 1000f) + groupChangeValue);
		
		CDData data=null;
		
		if(timeMax>0)
		{
			CDData oldData;
			
			if((oldData=_cds.get(id))==null)
			{
				data=new CDData();
				data.id=id;
				data.config=config;
				
				_cds.put(data.id,data);
			}
			else
			{
				data=oldData;
			}
			
			data.timePass=0;
			data.timeMax=timeMax;
			
			for(int gv : config.groups)
			{
				//计数加1
				if(oldData==null)
				{
					_groupCDCount.addValue(gv,1);
				}
				
				CDData oldMax=_groupMaxCDs.get(gv);
				
				if(oldMax==null || oldMax.getLastTime()<timeMax)
				{
					_groupMaxCDs.put(gv,data);
				}
				
				//新的
				if(oldMax==null)
				{
					_parent.onStartGroupCD(gv);
				}
			}
		}
		
		return data;
	}
	
	/** 开始一个CD */
	public void startCD(int id)
	{
		CDData data=toStartOneCD(id);
		
		if(data!=null)
		{
			SList<CDData> list=new SList<>();
			list.add(data);
			_parent.sendStartCDs(list);
		}
	}
	
	/** 开始一个变量CD */
	public void startCDVar(int id,int varID)
	{
		CDData data=toStartOneCD(CDConfig.get(id),_parent.getSkillVarValue(varID));
		
		if(data!=null)
		{
			SList<CDData> list=new SList<>();
			list.add(data);
			_parent.sendStartCDs(list);
		}
	}
	
	/** 开始服务器CD组 */
	public void startCDsByServer(SList<CDData> list)
	{
		IntObjectMap<CDData> cds=_cds;
		IntObjectMap<CDData> groupMaxCDs=_groupMaxCDs;
		IntIntMap groupCDCount=_groupCDCount;
		UnitFightDataLogic parent=_parent;
		
		CDData oldData;
		CDData data;
		CDConfig config;
		
		CDData[] values=list.getValues();
		CDData v;
		
		for(int i=list.size()-1;i>=0;--i)
		{
			v=values[i];
			
			config=CDConfig.get(v.id);
			
			if((oldData=cds.get(v.id))==null)
			{
				data=v;
				data.config=config;
				
				cds.put(data.id,data);
			}
			else
			{
				data=oldData;
				data.timePass=v.timePass;
				data.timeMax=v.timeMax;
			}
			
			for(int gv : config.groups)
			{
				//计数加1
				if(oldData==null)
				{
					groupCDCount.addValue(gv,1);
				}
				
				CDData oldMax=groupMaxCDs.get(gv);
				
				if(oldMax==null || oldMax.getLastTime()<data.timeMax)
				{
					groupMaxCDs.put(gv,data);
				}
				
				//新的
				if(oldMax==null)
				{
					parent.onStartGroupCD(gv);
				}
			}
		}
		
		//_parent.onCDChange();
	}
}
