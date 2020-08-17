using System;
using ShineEngine;

/// <summary>
/// 冷却数据逻辑
/// </summary>
public class CDDataLogic
{
	private UnitFightDataLogic _parent;

	/** CD组 */
	private IntObjectMap<CDData> _cds;

	/** 最长组CD */
	private IntObjectMap<CDData> _groupMaxCDs=new IntObjectMap<CDData>();
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

			CDData[] values=cds.getValues();
			CDData data;

			for(int i=values.Length - 1;i>=0;--i)
			{
				if((data=values[i])!=null)
				{
					int lastTime=data.getLastTime();

					CDConfig config=data.config=CDConfig.get(data.id);

					foreach(int v in config.groups)
					{
						groupCDCount.addValue(v,1);
						
						CDData oldMax=groupMaxCDs.get(v);

						if(oldMax==null || oldMax.getLastTime()<lastTime)
						{
							groupMaxCDs.put(v,data);
						}
					}
				}
			}
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
		CDData[] values;
		CDData v;

		for(int i=(values=_cds.getValues()).Length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				v.reloadConfig();
			}
		}
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

		foreach(CDData data in _cds)
		{
			if((data.timePass+=delay) >= data.timeMax)
			{
				cds.remove(data.id);

				//移除对应组CD
				foreach(int gv in data.config.groups)
				{
					groupCDCount.addValue(gv,-1);

					if(groupMaxCDs.get(gv)==data)
					{
						groupMaxCDs.remove(gv);

						parent.onEndGroupCD(gv);
					}
				}
			}
		}
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
	}

	/** 增加组CD时间上限值 */
	public void addGroupTimeMaxValue(int groupID,int value)
	{
		_groupCDChangeValues.addValue(groupID,value);
	}

	/** 增加当前组CD时间经过(cd减少) */
	public void addGroupTimePass(int groupID,int value)
	{
		if(value==0)
			return;

		if(_cds.isEmpty())
			return;

		bool has=false;
		//只添加,触发还是基于onTen

		IntIntMap groupCDCount=_groupCDCount;

		bool needReCount=false;

		CDData[] values=_cds.getValues();
		CDData data;
		int vv;

		for(int i=values.Length - 1;i >= 0;--i)
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

					if(data.config.groups.Length>1)
					{
						foreach(int gv in data.config.groups)
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

			_parent.onCDChange();
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
		CDData[] values=dic.getValues();
		CDData v;

		for(int i=values.Length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				v.config=CDConfig.get(v.id);
				v.timePass+=dTime;

				//依然有效
				if(v.timePass<v.timeMax)
				{
					cds.put(v.id,v);

					foreach(int gv in v.config.groups)
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
			}
		}

		_parent.onCDChange();
	}

	private void reCountGroupCDMax()
	{
		IntObjectMap<CDData> groupMaxCDs=_groupMaxCDs;

		//FIXME:SMap
		CDData[] values=_cds.getValues();
		CDData data;
		CDData gData;
		int lastTime;

		for(int i=values.Length - 1;i >= 0;--i)
		{
			if((data=values[i])!=null)
			{
				lastTime=data.getLastTime();

				foreach(int gv in data.config.groups)
				{
					if((gData=groupMaxCDs.get(gv))==null || gData.getLastTime()<lastTime)
					{
						groupMaxCDs.put(gv,data);
					}
				}
			}
		}
	}

	/** 移除组cd */
	public void removeGroupCD(int groupID)
	{
		bool has=false;
		IntObjectMap<CDData> cds=_cds;
		IntObjectMap<CDData> groupMaxCDs=_groupMaxCDs;
		IntIntMap groupCDCount=_groupCDCount;
		UnitFightDataLogic parent=_parent;
		//FIXME:SMap
		int executeNum=0;

		foreach(CDData data in cds)
		{
			if(data.config.hasGroup(groupID))
			{
				has=true;

				cds.remove(data.id);

				foreach(int gv in data.config.groups)
				{
					if(groupMaxCDs.get(gv)==data)
					{
						groupMaxCDs.remove(gv);
						++executeNum;
					}

					if(groupCDCount.addValue(gv,-1)<=0)
					{
						--executeNum;
						parent.onEndGroupCD(gv);
					}
				}
			}
		}

		if(has)
		{
			if(executeNum!=0)
			{
				reCountGroupCDMax();
			}

			_parent.onCDChange();
		}
	}

	/** 根据keep移除cd组(不推送) */
	public void removeCDByKeep(int keepType)
	{
		IntObjectMap<CDData> cds=_cds;
		IntObjectMap<CDData> groupMaxCDs=_groupMaxCDs;
		IntIntMap groupCDCount=_groupCDCount;
		UnitFightDataLogic parent=_parent;
		int executeNum=0;

		bool has=false;

		foreach(CDData data in cds)
		{
			//大于等于的移除
			if(data.config.keepType>=keepType)
			{
				has=true;
				cds.remove(data.id);

				//移除对应组CD
				foreach(int gv in data.config.groups)
				{
					if(groupMaxCDs.get(gv)==data)
					{
						groupMaxCDs.remove(gv);
						++executeNum;
					}

					if(groupCDCount.addValue(gv,-1)<=0)
					{
						--executeNum;
						parent.onEndGroupCD(gv);
					}
				}
			}
		}

		if(has)
		{
			if(executeNum!=0)
			{
				reCountGroupCDMax();
			}

			_parent.onCDChange();
		}
	}

	//技能CD部分

	/** 获取某个技能的技能数据(最长的一个) */
	public CDData getSkillCD(int id)
	{
		int maxLastTime=0;
		CDData maxCDData=null;

		SkillConfig config=SkillConfig.get(id);
		CDData data;

		IntObjectMap<CDData> groupMaxCDs=_groupMaxCDs;

		foreach(int v in config.underGroupCDs)
		{
			if((data=groupMaxCDs.get(v))!=null)
			{
				int lastTime=data.getLastTime();

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

		if(cdData==null)
			return 0;

		return cdData.getLastTime();
	}

	/** 获取技能冷却时间是否结束 */
	public bool isSkillReady(int id)
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
	public bool isInGroupCD(int id)
	{
		return getGroupCD(id)>0;
	}

	/** 开始一个技能CD */
	public void startSkillCD(int skillID,int skillLevel)
	{
		foreach(int v in SkillLevelConfig.get(skillID,skillLevel).bringCDs)
		{
			toStartOneCD(v);
		}

		_parent.onCDChange();
	}

	private CDData toStartOneCD(int id)
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
		foreach(int gv in config.groups)
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

			foreach(int gv in config.groups)
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
			_parent.onCDChange();
		}
	}

	/** 开始一个变量CD */
	public void startCDVar(int id,int varID)
	{
		CDData data=toStartOneCD(CDConfig.get(id),_parent.getSkillVarValue(varID));

		if(data!=null)
		{
			_parent.onCDChange();
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

		foreach(CDData v in list)
		{
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

			foreach(int gv in config.groups)
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

		_parent.onCDChange();
	}
}