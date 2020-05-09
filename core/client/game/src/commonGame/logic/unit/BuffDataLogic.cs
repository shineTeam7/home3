using System;
using ShineEngine;

/// <summary>
/// buff数据逻辑
/// </summary>
public class BuffDataLogic
{
	private UnitFightDataLogic _parent;

	/** data的buff组 */
	private IntObjectMap<BuffData> _buffDataDic;
	/** buff组 */
	private IntObjectMap<BuffData> _buffDatas=new IntObjectMap<BuffData>();
	/** buff组(id组)(非AllExist)) */
	private IntObjectMap<BuffData> _buffDatasByID=new IntObjectMap<BuffData>();
	/** buff数据ID组(id->instanceID->data)(AllExist用) */
	private IntObjectMap<IntObjectMap<BuffData>> _buffDatasForAllExist=new IntObjectMap<IntObjectMap<BuffData>>();

	/** buff数据subNum组(subNumType) */
	private IntObjectMap<BuffData>[] _buffDatasBySubNums=new IntObjectMap<BuffData>[BuffSubNumType.size];

	/** 流水ID构造器 */
	private IndexMaker _buffInstanceIDMaker=new IndexMaker(CommonSetting.buffInstanceIDMax/2,CommonSetting.buffInstanceIDMax,true);

	//count

	/** buff几率改变组 */
	private IntIntMap _skillProbChanges=new IntIntMap();

	/** 间隔触发Action */
	private IntObjectMap<BuffIntervalActionData> _intervalActions=new IntObjectMap<BuffIntervalActionData>();
	/** 攻击判定几率触发结果 */
	private IntObjectMap<int[]>[] _attackProbActions=new IntObjectMap<int[]>[AttackMomentType.size];

	/** 释放技能几率触发结果 */
	private IntObjectMap<int[]> _useSkillProbActions=new IntObjectMap<int[]>();

	/** 变量属性字典 */
	private IntIntMap _attributeVarDic=new IntIntMap();
	/** 护盾buff字典 */
	private IntObjectMap<SList<DIntData>> _shieldBuffDic=new IntObjectMap<SList<DIntData>>();

	/** 技能替换字典(同时只能替换一个) */
	private IntIntMap _skillReplaceDic=new IntIntMap();
	/** 技能替换字典 */
	private IntObjectMap<SList<int[]>> _skillProbReplaceDic=new IntObjectMap<SList<int[]>>();


	/** 技能升级字典 */
	private IntIntMap _skillLevelUpDic=new IntIntMap();
	/** buff升级字典 */
	private IntIntMap _buffLevelUpDic=new IntIntMap();
	/** buff持续时间提升组 */
	private IntIntMap _buffLastTimeAddDic=new IntIntMap();

	/** 刷新变量属性字典 */
	private IntObjectMap<BuffAddAttributeRefreshVarData> _attributeRefreshVarDic=new IntObjectMap<BuffAddAttributeRefreshVarData>();
	/** 添加刷新变量属性记录字典 */
	private IntIntMap _addAttributeRefreshVarASet=new IntIntMap();

	/** 忽略buff组字典字典 */
	private IntIntMap _ignoreBuffGroupDic=new IntIntMap();

	public BuffDataLogic()
	{

	}

	public void setParent(UnitFightDataLogic parent)
	{
		_parent=parent;
	}

	/** 设置数据 */
	public void setData(IntObjectMap<BuffData> datas)
	{
		_buffDataDic=datas;

		if(datas!=null && !datas.isEmpty())
		{
			BuffData[] values=datas.getValues();
			BuffData data;

			for(int i=values.Length - 1;i>=0;--i)
			{
				if((data=values[i])!=null)
				{
					data.config=BuffConfig.get(data.id);
					data.levelConfig=BuffLevelConfig.get(data.id,data.level);

					toAddBuffToDic(data);

					doAddActions(data,_parent.isDriveAll());
				}
			}

			for(int i=values.Length - 1;i>=0;--i)
			{
				if((data=values[i])!=null)
				{
					data.timeMax=getBuffUseLastTime(data);
				}
			}
		}
	}

	public void reloadConfig()
	{
		BuffData[] values;
		BuffData v;

		for(int i=(values=_buffDataDic.getValues()).Length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				v.reloadConfig();
			}
		}
	}

	public void clear()
	{
		if(_buffDataDic!=null)
		{
			_buffDataDic.clear();
		}

		_buffDatas.clear();
		_buffDatasByID.clear();
		_buffDatasForAllExist.clear();

		foreach(IntObjectMap<BuffData> v in _buffDatasBySubNums)
		{
			if(v!=null)
			{
				v.clear();
			}
		}

		_buffInstanceIDMaker.reset();
		_skillProbChanges.clear();
		_intervalActions.clear();

		foreach(IntObjectMap<int[]> v in _attackProbActions)
		{
			if(v!=null)
			{
				v.clear();
			}
		}

		_useSkillProbActions.clear();

		SList<DIntData>[] values=_shieldBuffDic.getValues();
		SList<DIntData> v2;

		for(int i=values.Length-1;i>=0;--i)
		{
			if((v2=values[i])!=null)
			{
				v2.clear();
			}
		}

		_skillReplaceDic.clear();
		_skillProbReplaceDic.clear();

		_skillLevelUpDic.clear();
		_buffLevelUpDic.clear();
		_buffLastTimeAddDic.clear();

		_attributeRefreshVarDic.clear();
		_addAttributeRefreshVarASet.clear();
	}

	/** 取一个单位实例ID */
	private int getBuffInstanceID()
	{
		int re;

		while(_buffDatas.contains(re=_buffInstanceIDMaker.get()));

		return re;
	}

	/** 每秒十次 */
	public void onPiece(int delay)
	{
		if(!_buffDatas.isEmpty())
		{
			//是客户端驱动
			if(_parent.isSelfDriveAttackHapen())
			{
				//先触发间隔
				if(!_intervalActions.isEmpty())
				{
					AttributeDataLogic attributeLogic=_parent.attribute;

					foreach(BuffIntervalActionData v in _intervalActions)
					{
						v.timePass+=delay;

						if(v.timePass>=v.delay)
						{
							v.timePass-=v.delay;

							switch(v.type)
							{
								case BuffIntervalActionType.Attack:
								{
									//触发一次
									_parent.doBuffIntervalAttack(v);
								}
									break;
								case BuffIntervalActionType.AddAttribute:
								{
									if(!_parent.isDriveAll())
										return;

									attributeLogic.addOneAttribute(v.key,v.value);
								}
									break;
								case BuffIntervalActionType.AddAttributeVar:
								{
									if(!_parent.isDriveAll())
										return;

									attributeLogic.addOneAttribute(v.key,_parent.getSkillVarValueT(v.value,v.adderInstanceID));
								}
									break;
							}
						}
					}
				}
			}

			foreach(BuffData data in _buffDatas)
			{
				//有持续时间
				if(data.lastTime>0)
				{
					if((data.lastTime-=delay)<=0)
					{
						data.lastTime=0;

						//本端驱动才移除
						if(_parent.isDriveAll())
						{
							buffOver(data);
						}
					}
				}
			}
		}
	}

	/** 获取全部buff组 */
	public IntObjectMap<BuffData> getBuffDatas()
	{
		return _buffDatas;
	}

	/** 获取buff数据 */
	private BuffData getBuffData(int instanceID)
	{
		return _buffDatas.get(instanceID);
	}

	/** 获取某buff的叠加层数 */
	public int getBuffFloor(int id)
	{
		BuffData data;

		if((data=_buffDatasByID.get(id))==null)
			return 0;

		if(data.config.plusType!=BuffPlusType.AddFloor)
			return 0;

		return data.lastNum;
	}

	/** 添加buff */
	public BuffData addBuff(int id,int level)
	{
		return addBuff(id,level,-1);
	}

	/** 添加buff */
	public BuffData addBuff(int id,int level,int adderInstanceID)
	{
		return addOneBuff(id,level,adderInstanceID,null);
	}

	/** 添加buff通过数据 */
	public BuffData addBuffByData(BuffData data)
	{
		return addOneBuff(data.id,data.level,data.adderInstanceID,data);
	}

	/** 添加一组buff */
	public void addBuffs(DIntData[] list)
	{
		foreach(DIntData v in list)
		{
			addBuff(v.key,v.value);
		}
	}

	/** 是否有buff(为一些绑定逻辑使用) */
	public bool hasBuff(int id)
	{
		if(BuffConfig.get(id).plusType==BuffPlusType.AllExist)
		{
			IntObjectMap<BuffData> dic;

			return (dic=_buffDatasForAllExist.get(id))!=null && !dic.isEmpty();
		}
		else
		{
			return _buffDatasByID.get(id)!=null;
		}
	}

	/** 删除buff通过流水ID(服务器也用) */
	public void removeBuffByInstanceID(int instanceID)
	{
		BuffData data=getBuffData(instanceID);

		if(data==null)
			return;

		toRemoveBuff(data);
	}

	/** 删除buff通过ID */
	public void removeBuffByID(int id)
	{
		if(BuffConfig.get(id).plusType==BuffPlusType.AllExist)
		{
			IntObjectMap<BuffData> dic;

			if((dic=_buffDatasForAllExist.get(id))!=null && !dic.isEmpty())
			{
				foreach(BuffData data in dic)
				{
					toRemoveBuff(data);
				}
			}
		}
		else
		{
			BuffData data;

			if((data=_buffDatasByID.get(id))!=null)
			{
				toRemoveBuff(data);
			}
		}
	}

	/** 死亡时移除buff */
	public void removeBuffAtDead()
	{
		foreach(BuffData v in _buffDatas)
		{
			if(v.config.removeAtDead)
			{
				toRemoveBuff(v);
			}
		}
	}

	/** 移除buff通过group */
	public void removeBuffByGroup(int group)
	{
		foreach(BuffData v in _buffDatas)
		{
			if(v.config.hasGroup(group))
			{
				toRemoveBuff(v);
			}
		}

	}

	/** 移除指定保留类型的buff */
	public void removeBuffByKeep(int keepType)
	{
		foreach(BuffData v in _buffDatas)
		{
			if(v.config.keepType>=keepType)
			{
				toRemoveBuff(v);
			}
		}
	}

	/** 消耗buff次数 */
	public void subBuffNum(int type,int arg0)
	{
		//不是客户端驱动全部
		if(!_parent.isDriveAll())
			return;

		IntObjectMap<BuffData> dic=_buffDatasBySubNums[type];

		if(dic==null || dic.isEmpty())
			return;

		int bArg;

		foreach(BuffData v in dic)
		{
			//0为任意类型
			if((bArg=v.config.subNumsDicT[type])==0 || bArg==arg0)
			{
				if(v.lastNum>0)
				{
					//没了
					if(--v.lastNum==0)
					{
						buffOver(v);
					}
					else
					{
						refreshOneBuffAttributeVar(v);
					}
				}
			}
		}
	}

	/** 消耗buff次数数组 */
	public void subBuffNumArr(int type,int[] arr)
	{
		IntObjectMap<BuffData> dic=_buffDatasBySubNums[type];

		if(dic==null || dic.isEmpty())
			return;

		int bArg;

		foreach(BuffData v in dic)
		{
			bArg=v.config.subNumsDicT[type];

			foreach(int t in arr)
			{
				//0为任意类型
				if(bArg==0 || bArg==t)
				{
					if(v.lastNum>0)
					{
						//没了
						if(--v.lastNum==0)
						{
							toRemoveBuff(v);
						}
						else
						{
							refreshOneBuffAttributeVar(v);
						}
					}

					break;
				}
			}
		}
	}

	//server部分

	/** 服务器添加buff */
	public void addBuffByServer(BuffData data)
	{
		data.config=BuffConfig.get(data.id);
		data.levelConfig=BuffLevelConfig.get(data.id,data.level);
		data.timeMax=getBuffUseLastTime(data);

		toAddBuff(data);
	}

	/** 服务器刷新buff */
	public void refreshBuffByServer(int instanceID,int lastTime,int lastNum)
	{
		BuffData data=getBuffData(instanceID);

		if(data==null)
			return;

		data.lastTime=lastTime;
		data.lastNum=lastNum;

		_parent.onRefreshBuff(data);
	}

	/** 服务器刷新buff */
	public void refreshBuffLastNumByServer(int instanceID,int num)
	{
		BuffData data=getBuffData(instanceID);

		if(data==null)
			return;

		data.lastNum=num;

		_parent.onRefreshBuff(data);
	}

	/** 获取某技能的替换ID(没有返回原ID) */
	public int getSkillReplaceID(int skillID)
	{
		int re=_skillReplaceDic.get(skillID);

		return re>0 ? re : skillID;
	}

	/** 获取技能几率替换字典 */
	public SList<int[]> getSkillProbReplaceList(int skillID)
	{
		return _skillProbReplaceDic.get(skillID);
	}

	/** 获取技能实际使用等级 */
	public int getSkillUseLevel(int skillID,int level)
	{
		SkillConfig config=SkillConfig.get(skillID);

		foreach(int v in config.groups)
		{
			level+=_skillLevelUpDic.getOrDefault(v,0);
		}

		//if(level<1)
		//	level=1;

		return level;
	}

	/** 获取buff时机使用时间 */
	private int getBuffUseLastTime(BuffData data)
	{
		int re=data.levelConfig.lastTime;

		foreach(int v in data.config.groups)
		{
			re+=_buffLastTimeAddDic.getOrDefault(v,0);
		}

		return re;
	}

	/** 获取buff使用等级 */
	private int getBuffUseLevel(BuffConfig config,int level)
	{
		foreach(int v in config.groups)
		{
			level+=_buffLevelUpDic.getOrDefault(v,0);
		}

		return level;
	}

	private BuffData addOneBuff(int id,int level,int adderInstanceID,BuffData eData)
	{
		BuffConfig config=BuffConfig.get(id);

		//先看忽略
		foreach(int v in config.groups)
		{
			if(_ignoreBuffGroupDic.get(v)>0)
				return null;
		}

		BuffData re;
		BuffData oldData;
		BuffData data;

		switch(config.plusType)
		{
			case BuffPlusType.Normal:
			{
				if((oldData=_buffDatasByID.get(id))!=null)
				{
					//新的等级更高
					if(level>oldData.level)
					{
						toRemoveBuff(oldData);
						toAddBuff(re=toCreateBuff(id,level,adderInstanceID,eData));
					}
					else
					{
						toRefreshBuff(re=oldData);
					}
				}
				else
				{
					toAddBuff(re=toCreateBuff(id,level,adderInstanceID,eData));
				}
			}
				break;
			case BuffPlusType.Replace:
			{
				if((oldData=_buffDatasByID.get(id))!=null)
				{
					toRemoveBuff(oldData);
				}

				toAddBuff(re=toCreateBuff(id,level,adderInstanceID,eData));
			}
				break;
			case BuffPlusType.AllExist:
			{
				IntObjectMap<BuffData> dic=_buffDatasForAllExist.get(id);

				if(dic==null)
				{
					toAddBuff(re=toCreateBuff(id,level,adderInstanceID,eData));
				}
				else
				{
					if(config.plusMax>0 && dic.size()>=config.plusMax)
					{
						BuffData temp=null;

						BuffData[] values=dic.getValues();

						for(int i=values.Length - 1;i>=0;--i)
						{
							if((data=values[i])!=null)
							{
								if(temp==null)
								{
									temp=data;
								}
								else if(data.instanceID<temp.instanceID) //更小
								{
									temp=data;
								}
							}
						}

						//删了最早的
						if(temp!=null)
						{
							toRemoveBuff(temp);
						}
					}

					toAddBuff(re=toCreateBuff(id,level,adderInstanceID,eData));
				}
			}

				break;
			case BuffPlusType.AddFloor:
			{
				if((oldData=_buffDatasByID.get(id))!=null)
				{
					//新的等级更高
					if(level>oldData.level)
					{
						toRemoveBuff(oldData);
						re=toCreateBuff(id,level,adderInstanceID,eData);
						//继承层数
						re.lastNum=oldData.lastNum;

						toAddBuff(re);
					}
					else
					{
						//没满级
						if(oldData.lastNum<config.plusMax)
						{
							//加
							oldData.lastNum++;

							refreshOneBuffAttributeVar(oldData);

							//刷新buff时间
							toRefreshBuff(re=oldData);

							//到达上限
							if(oldData.lastNum==config.plusMax)
							{
								doPlusMaxActions(oldData);
							}
						}
						else
						{
							re=oldData;

							if(oldData.levelConfig.lastTime>0)
							{
								//刷新buff时间
								toRefreshBuff(oldData);
							}
						}
					}
				}
				else
				{
					toAddBuff(re=toCreateBuff(id,level,adderInstanceID,eData));
				}
			}
				break;
			default:
			{
				re=null;
				Ctrl.throwError("不支持的buff plusType",config.plusType);
			}
				break;
		}

		return re;
	}

	private BuffData toCreateBuff(int id,int level,int adderInstanceID,BuffData eData)
	{
		BuffData buff=eData!=null ? eData : GameC.pool.buffDataPool.getOne();

		buff.instanceID=getBuffInstanceID();
		buff.id=id;
		buff.config=BuffConfig.get(id);
		buff.level=getBuffUseLevel(buff.config,level);
		buff.levelConfig=BuffLevelConfig.get(id,buff.level);
		//客户端用
		buff.timeMax=getBuffUseLastTime(buff);

		buff.adderInstanceID=adderInstanceID;

		if(eData==null)
		{
			buff.lastTime=getBuffUseLastTime(buff);
			buff.lastNum=buff.levelConfig.lastNum;
			//叠层强制1
			if(buff.config.plusType==BuffPlusType.AddFloor)
			{
				buff.lastNum=1;
			}
		}

		return buff;
	}

	private void toAddBuff(BuffData data)
	{
		toAddBuffToDic(data);

		doAddActions(data,true);

		_parent.onAddBuff(data);
	}

	private void toAddBuffToDic(BuffData data)
	{
		_buffDatas.put(data.instanceID,data);

		if(data.config.plusType==BuffPlusType.AllExist)
		{
			_buffDatasForAllExist.computeIfAbsent(data.id,k=>new IntObjectMap<BuffData>()).put(data.instanceID,data);
		}
		else
		{
			_buffDatasByID.put(data.id,data);
		}

		int[][] subNums;

		if((subNums=data.config.subNums).Length>0)
		{
			foreach(int[] v in subNums)
			{
				IntObjectMap<BuffData> dic=_buffDatasBySubNums[v[0]];

				if(dic==null)
				{
					_buffDatasBySubNums[v[0]]=dic=new IntObjectMap<BuffData>();
				}

				dic.put(data.instanceID,data);
			}
		}
	}

	/** 执行移除buff */
	private void toRemoveBuff(BuffData data)
	{
		if(toRemoveBuffFromDic(data))
		{
			doRemoveActions(data,true);

			_parent.onRemoveBuff(data);

			GameC.pool.buffDataPool.back(data);
		}
	}

	private bool toRemoveBuffFromDic(BuffData data)
	{
		if(_buffDatas.remove(data.instanceID)==null)
			return false;

		if(data.config.plusType==BuffPlusType.AllExist)
		{
			_buffDatasForAllExist.get(data.id).remove(data.instanceID);
		}
		else
		{
			_buffDatasByID.remove(data.id);
		}

		int[][] subNums;

		if((subNums=data.config.subNums).Length>0)
		{
			foreach(int[] v in subNums)
			{
				_buffDatasBySubNums[v[0]].remove(data.instanceID);
			}

		}

		return true;
	}

	/** 刷新buff */
	private void toRefreshBuff(BuffData data)
	{
		bool isChange=false;

		int newTime;

		if((newTime=getBuffUseLastTime(data))!=data.lastTime)
		{
			data.lastNum=newTime;
			isChange=true;
		}

		//不是叠层才刷
		if(data.config.plusType!=BuffPlusType.AddFloor)
		{
			if(data.lastNum!=data.levelConfig.lastNum)
			{
				data.lastNum=data.levelConfig.lastNum;
				isChange=true;
			}
		}

		if(isChange)
		{
			_parent.onRefreshBuff(data);
		}
	}

	/** buff结束 */
	private void buffOver(BuffData data)
	{
		//先动作再移除
		foreach(int[] v in data.levelConfig.overActions)
		{
			doOneOverAction(v);
		}

		toRemoveBuff(data);
	}

	private void doPlusMaxActions(BuffData data)
	{
		//叠加上限动作
		foreach(int[] v in data.levelConfig.plusMaxActions)
		{
			doOneOverAction(v);
		}
	}

	/** 执行添加动作(isFull:是否完整添加) */
	private void doAddActions(BuffData data,bool isFull)
	{
		int[][] actions=data.levelConfig.actions;

		for(int i=0;i<actions.Length;++i)
		{
			doOneAction(data,i,actions[i],true,isFull);
		}
	}

	/** 执行添加动作(isFull:是否完整添加) */
	private void doRemoveActions(BuffData data,bool isFull)
	{
		int[][] actions=data.levelConfig.actions;

		for(int i=0;i<actions.Length;++i)
		{
			doOneAction(data,i,actions[i],false,isFull);
		}
	}

	//prob

	/** 添加技能几率 */
	public void addSkillProb(int probID,int value)
	{
		_skillProbChanges.addValue(probID,value);
	}

	/** 获取某probID的使用技能几率值 */
	public int getUseSkillProb(int probID)
	{
		return SkillProbConfig.get(probID).value + _skillProbChanges.get(probID);
	}

	/** 获取攻击时几率触发攻击组 */
	public IntObjectMap<int[]> getAttackProbActionDic(int moment)
	{
		return _attackProbActions[moment];
	}

	/** 获取释放技能几率触发动作组 */
	public IntObjectMap<int[]> getUseSkillProbActions()
	{
		return _useSkillProbActions;
	}

	//shield

	/** 护盾吸收伤害 */
	public void onShieldDamage(int type,int value)
	{
		SList<DIntData> list=_shieldBuffDic.get(type);

		if(list!=null)
		{
			while(!list.isEmpty())
			{
				DIntData v=list.get(0);

				if(value<v.value)
				{
					v.value-=value;

					break;
				}
				else
				{
					value-=v.value;
					v.value=0;

					//盾爆了就删除buff
					removeBuffByInstanceID(v.key);
				}
			}
		}
	}

	/** 属性变化接口 */
	public void onAttributeChange(int[] changeList,int length,bool[] changeSet,int[] lastAttributes)
	{
		IntIntMap vSet;

		if((vSet=_addAttributeRefreshVarASet).isEmpty())
			return;

		bool has=false;

		for(int i=length-1;i>=0;--i)
		{
			//有
			if(vSet.getOrDefault(changeList[i],0)>0)
			{
				has=true;
				break;
			}
		}

		if(has)
		{
			refreshAttributesVar();
		}
	}

	/** 刷新变量属性 */
	private void refreshAttributesVar()
	{
		int value;

		BuffAddAttributeRefreshVarData[] values;
		BuffAddAttributeRefreshVarData v;

		for(int i=(values=_attributeRefreshVarDic.getValues()).Length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				value=_parent.getSkillVarValueT(v.varID,v.adderInstanceID);

				if(value!=v.value)
				{
					_parent.attribute.addOneAttribute(v.type,value-v.value);

					v.value=value;
				}
			}
		}
	}

	/** 刷新单个buff的变量属性 */
	private void refreshOneBuffAttributeVar(BuffData data)
	{
		int[][] actions=data.levelConfig.actions;
		int[] args;

		for(int index=0;index<actions.Length;++index)
		{
			args=actions[index];

			switch(args[0])
			{
				case BuffActionType.AddAttributeVar:
				{
					int oldValue=_attributeVarDic.remove(data.instanceID<< CommonSetting.buffActionIndexOff | index);

					int value=_parent.getSkillVarValueT(args[2],data.adderInstanceID);

					if(oldValue!=value)
					{
						_parent.attribute.addOneAttribute(args[1],value-oldValue);

						_attributeVarDic.put(data.instanceID<< CommonSetting.buffActionIndexOff | index,value);
					}
				}
					break;
				case BuffActionType.AddAttributeRefreshVar:
				{
					BuffAddAttributeRefreshVarData oldData=_attributeRefreshVarDic.get(data.instanceID<< CommonSetting.buffActionIndexOff | index);

					int value=_parent.getSkillVarValueT(oldData.varID,data.adderInstanceID);

					if(oldData.value!=value)
					{
						_parent.attribute.addOneAttribute(args[1],value-oldData.value);

						oldData.value=value;
					}
				}
					break;
			}
		}
	}

	/** 刷新当前buff等级 */
	private void refreshBuffLevelUp(int groupID,int level)
	{
		BuffData[] dataValues=_buffDatas.getValues();
		BuffData data;

		for(int i=dataValues.Length - 1;i >= 0;--i)
		{
			if((data=dataValues[i])!=null)
			{
				if(data.config.hasGroup(groupID))
				{
					doRemoveActions(data,true);

					_parent.onRemoveBuff(data);

					data.level+=level;
					data.levelConfig=BuffLevelConfig.get(data.id,data.level);
					data.timeMax=getBuffUseLastTime(data);

					doAddActions(data,true);

					_parent.onAddBuff(data);
				}
			}
		}
	}

	private int compareSkillProbArgs(int[] a,int[] b)
	{
		if(a[2]<b[2])
			return -1;

		if(a[2]>b[2])
			return 1;

		if(a[3]<b[3])
			return -1;

		if(a[3]>b[3])
			return 1;

		return 0;
	}

	//buff响应

	/** 执行单个动作 */
	private void doOneAction(BuffData data,int index,int[] args,bool isAdd,bool isFull)
	{
		bool isDriveAll=_parent.isDriveAll();

		switch(args[0])
		{
			case BuffActionType.AddStatus:
			{
				if(!isDriveAll)
					return;

				if(!isFull)
					return;

				if(isAdd)
				{
					_parent.status.addStatus(args[1]);
				}
				else
				{
					_parent.status.subStatus(args[1]);
				}
			}
				break;
			case BuffActionType.AddAttribute:
			{
				if(!isDriveAll)
					return;

				if(!isFull)
					return;

				_parent.attribute.addOneAttribute(args[1],isAdd ? args[2] : -args[2]);
			}
				break;
			case BuffActionType.AddAttributeVar:
			{
				if(!isDriveAll)
					return;

				if(!isFull)
					return;

				if(isAdd)
				{
					int value=_parent.getSkillVarValueT(args[2],data.adderInstanceID);

					_parent.attribute.addOneAttribute(args[1],value);

					_attributeVarDic.put(data.instanceID<< CommonSetting.buffActionIndexOff | index,value);
				}
				else
				{
					int value=_attributeVarDic.remove(data.instanceID<< CommonSetting.buffActionIndexOff | index);

					_parent.attribute.subOneAttribute(args[1],value);

				}
			}
				break;
			case BuffActionType.IntervalMakeAttack:
			{
				//不是客户端驱动战斗
//				if(!SceneDriveType.isClientDriveAttackHapen(CommonSetting.sceneDriveType))
//					return;

				//不是客户端驱动战斗
				if(!_parent.isSelfDriveAttackHapen())
					return;

				if(isAdd)
				{
					BuffIntervalActionData mData=GameC.pool.buffIntervalActionDataPool.getOne();
					mData.adderInstanceID=data.adderInstanceID;
					mData.readFromConfig(args);

					mData.type=BuffIntervalActionType.Attack;

					Unit selfUnit=_parent.getUnit();

					UnitFightDataLogic attackerLogic=null;

					if(data.adderInstanceID==-1)
					{
						attackerLogic=_parent;
					}
					else
					{
						Unit attacker;

						if(selfUnit!=null && (attacker=selfUnit.getScene().getFightUnit(data.adderInstanceID))!=null)
						{
							attackerLogic=attacker.fight.getDataLogic();
						}
					}

					//存在再添加
					if((args.Length>4 && args[4]>0) && attackerLogic!=null)
					{
						mData.calculateSelfAttackValue(attackerLogic);
					}

					_intervalActions.put(data.instanceID << CommonSetting.buffActionIndexOff | index,mData);
				}
				else
				{
					BuffIntervalActionData mData=_intervalActions.remove(data.instanceID << CommonSetting.buffActionIndexOff | index);

					if(mData==null)
					{
						Ctrl.throwError("不该找不到BuffIntervalActionData");
					}
					else
					{
						GameC.pool.buffIntervalActionDataPool.back(mData);
					}
				}
			}
				break;
			case BuffActionType.AddGroupCDTimeMaxPercent:
			{
				if(!isDriveAll)
					return;

				_parent.cd.addGroupTimeMaxPercent(args[1],isAdd ? args[2] : -args[2]);
			}
				break;
			case BuffActionType.AddGroupCDTimeMaxValue:
			{
				if(!isDriveAll)
					return;

				_parent.cd.addGroupTimeMaxValue(args[1],isAdd ? args[2] : -args[2]);
			}
				break;
			case BuffActionType.AddSkillProb:
			{
				addSkillProb(args[1],isAdd ? args[2] : -args[2]);
			}
				break;
			case BuffActionType.ChangeFacade:
			{
				if(!isDriveAll)
					return;

				if(isAdd)
				{
					_parent.avatar.addFacade(args[1]);
				}
				else
				{
					_parent.avatar.removeFacade(args[1]);
				}
			}
				break;
			case BuffActionType.AddAvatarPart:
			{
				if(!isDriveAll)
					return;

				if(isAdd)
				{
					_parent.avatar.addPart(args[1],args[2]);
				}
				else
				{
					_parent.avatar.removePart(args[1],args[2]);
				}
			}
				break;
			case BuffActionType.AttackProbAction:
			{
				if(!isDriveAll)
					return;

				IntObjectMap<int[]> dic=_attackProbActions[args[1]];

				if(dic==null)
				{
					dic=new IntObjectMap<int[]>();
					_attackProbActions[args[1]]=dic;
				}

				if(isAdd)
				{
					dic.put(data.instanceID << CommonSetting.buffActionIndexOff | index,args);
				}
				else
				{
					dic.remove(data.instanceID << CommonSetting.buffActionIndexOff | index);
				}
			}
				break;
			case BuffActionType.AddShield:
			case BuffActionType.AddShieldVar:
			{
				if(!isDriveAll)
					return;

				if(!isFull)
				{
					Ctrl.throwError("不支持初始化添加护盾");
					return;
				}

				//盾同一个buff就存在一个,不然会互斥

				if(isAdd)
				{
					int value;

					if(args[0]==BuffActionType.AddShield)
					{
						value=args[2];
					}
					else
					{
						value=_parent.getSkillVarValueT(args[2],data.adderInstanceID);
					}

					//盾值
					_parent.attribute.addOneAttribute(args[1],value);

					_shieldBuffDic.computeIfAbsent(args[1],k=>new SList<DIntData>()).add(DIntData.create(data.instanceID,value));
				}
				else
				{
					SList<DIntData> list=_shieldBuffDic.get(args[1]);
					DIntData v;
					for(int i=0,len=list.length();i<len;++i)
					{
						if((v=list.get(i)).key==data.instanceID)
						{
							//移除剩余盾值
							if(v.value>0)
							{
								_parent.attribute.subOneAttribute(args[1],v.value);
							}

							list.remove(i);
							--len;
							--i;
						}
					}
				}
			}
				break;
			case BuffActionType.SkillReplace:
			{
				if(isAdd)
				{
					if(ShineSetting.openCheck)
					{
						if(_skillReplaceDic.contains(args[1]))
						{
							Ctrl.throwError("目前，相同技能ID同时只支持一个替换技能");
						}
					}

					_skillReplaceDic.put(args[1],args[2]);
				}
				else
				{
					_skillReplaceDic.remove(args[1]);
				}

			}
				break;
			case BuffActionType.SkillProbReplace:
			{
				SList<int[]> list=_skillProbReplaceDic.computeIfAbsent(args[1],k=>new SList<int[]>());

				if(isAdd)
				{
					int[] a;

					for(int i=0,len=list.length();i<len;++i)
					{
						a=list.get(i);

						if(compareSkillProbArgs(args,a)<=0)
						{
							list.insert(i,args);
							return;
						}
					}

					list.add(args);
				}
				else
				{
					int[] a;

					for(int i=0,len=list.length();i<len;++i)
					{
						a=list.get(i);

						if(args[2]==a[2] && args[3]==a[3])
						{
							list.remove(i);
							break;
						}
					}
				}
			}
				break;
			case BuffActionType.IntervalAddAttribute:
			{
				if(!isDriveAll)
					return;

				if(!isFull)
					return;

				if(isAdd)
				{
					BuffIntervalActionData mData=GameC.pool.buffIntervalActionDataPool.getOne();
					mData.type=BuffIntervalActionType.AddAttribute;
					mData.readFromConfig(args);

					_intervalActions.put(data.instanceID << CommonSetting.buffActionIndexOff | index,mData);
				}
				else
				{
					BuffIntervalActionData mData=_intervalActions.remove(data.instanceID << CommonSetting.buffActionIndexOff | index);

					if(mData==null)
					{
						Ctrl.throwError("不该找不到BuffIntervalActionData");
					}
					else
					{
						GameC.pool.buffIntervalActionDataPool.back(mData);
					}
				}
			}
				break;
			case BuffActionType.IntervalAddAttributeVar:
			{
				if(!isDriveAll)
					return;

				if(!isFull)
					return;

				if(isAdd)
				{
					BuffIntervalActionData mData=GameC.pool.buffIntervalActionDataPool.getOne();
					mData.type=BuffIntervalActionType.AddAttributeVar;
					mData.readFromConfig(args);

					_intervalActions.put(data.instanceID << CommonSetting.buffActionIndexOff | index,mData);
				}
				else
				{
					BuffIntervalActionData mData=_intervalActions.remove(data.instanceID << CommonSetting.buffActionIndexOff | index);

					if(mData==null)
					{
						Ctrl.throwError("不该找不到BuffIntervalActionData");
					}
					else
					{
						GameC.pool.buffIntervalActionDataPool.back(mData);
					}
				}
			}
				break;
			case BuffActionType.SkillLevelUp:
			{
				_skillLevelUpDic.addValue(args[1],isAdd ? args[2] : -args[2]);
			}
				break;
			case BuffActionType.AddBuffLastTime:
			{
				if(!isDriveAll)
					return;

				_buffLastTimeAddDic.addValue(args[1],isAdd ? args[2] : -args[2]);
			}
				break;
			case BuffActionType.BuffLevelUp:
			{
				if(!isDriveAll)
					return;

				int level=isAdd ? args[2] : -args[2];

				_buffLevelUpDic.addValue(args[1],level);

				//需要立即更新
				if(args.Length>3 && args[3]>0)
				{
					if(data.config.hasGroup(args[1]))
					{
						Ctrl.throwError("不能影响自己所在组的buffLevel");
						return;
					}

					refreshBuffLevelUp(args[1],level);
				}
			}
				break;
			case BuffActionType.AddAttributeRefreshVar:
			{
				if(!isDriveAll)
					return;

				if(!isFull)
					return;

				SkillVarConfig vConfig=SkillVarConfig.get(args[2]);

				foreach(int[] v in vConfig.args)
				{
					switch(v[0])
					{
						case SkillVarSourceType.SelfAttribute:
						case SkillVarSourceType.TargetAttribute:
						{
							_addAttributeRefreshVarASet.addValue(v[1],isAdd ? 1 : -1);
						}
							break;
						case SkillVarSourceType.SelfCurrentAttributePercent:
						case SkillVarSourceType.TargetCurrentAttributePercent:
						case SkillVarSourceType.SelfCurrentAttributeLostPercent:
						case SkillVarSourceType.TargetCurrentAttributeLostPercent:
						{
							//当前+max
							_addAttributeRefreshVarASet.addValue(v[1],isAdd ? 1 : -1);
							_addAttributeRefreshVarASet.addValue(AttributeControl.attribute.currentToMaxMap[v[1]],isAdd ? 1 : -1);
						}
							break;
					}
				}

				if(isAdd)
				{
					int value=_parent.getSkillVarValueT(vConfig.id,data.adderInstanceID);

					_parent.attribute.addOneAttribute(args[1],value);

					BuffAddAttributeRefreshVarData bData=new BuffAddAttributeRefreshVarData();
					bData.adderInstanceID=data.adderInstanceID;
					bData.varID=vConfig.id;
					bData.type=args[1];
					bData.value=value;

					_attributeRefreshVarDic.put(data.instanceID<< CommonSetting.buffActionIndexOff | index,bData);
				}
				else
				{
					BuffAddAttributeRefreshVarData bData=_attributeRefreshVarDic.remove(data.instanceID<< CommonSetting.buffActionIndexOff | index);

					_parent.attribute.subOneAttribute(args[1],bData.value);
				}
			}
				break;
			case BuffActionType.UseSkillProbAction:
			{
				if(!isDriveAll)
					return;

				if(isAdd)
				{
					_useSkillProbActions.put(data.instanceID << CommonSetting.buffActionIndexOff | index,args);
				}
				else
				{
					_useSkillProbActions.remove(data.instanceID << CommonSetting.buffActionIndexOff | index);
				}
			}
				break;
			case BuffActionType.IgnoreBuffGroup:
			{
				if(!isDriveAll)
					return;

				if(!isFull)
					return;

				_ignoreBuffGroupDic.addValue(args[0],isAdd ? 1 : -1);

				if(isAdd)
				{
					removeBuffByGroup(args[0]);
				}
			}
				break;
		}
	}

	/** 执行一个完成动作 */
	protected virtual void doOneOverAction(int[] args)
	{
		bool isDriveAll=_parent.isDriveAll();

		switch(args[0])
		{
			case BuffOverActionType.AddAttribute:
			{
				if(!isDriveAll)
					return;

				_parent.attribute.addOneAttribute(args[1],args[2]);
			}
				break;
			case BuffOverActionType.AddAttributeVar:
			{
				if(!isDriveAll)
					return;

				_parent.attribute.addOneAttribute(args[1],_parent.getSkillVarValue(args[2]));
			}
				break;
			case BuffOverActionType.MakeAttack:
			{
				_parent.doBuffAttack(args[1],args[2]);
			}
				break;
			case BuffOverActionType.AddBuff:
			{
				if(!isDriveAll)
					return;

				addBuff(args[1],args[2]);
			}
				break;
			case BuffOverActionType.RemoveBuffByID:
			{
				if(!isDriveAll)
					return;

				removeBuffByID(args[1]);
			}
				break;
			case BuffOverActionType.StartCD:
			{
				if(!isDriveAll)
					return;

				_parent.cd.startCD(args[1]);
			}
				break;
			case BuffOverActionType.StartCDVar:
			{
				if(!isDriveAll)
					return;

				_parent.cd.startCDVar(args[1],args[2]);
			}
				break;
			default:
			{
				_parent.doOneBuffOverActionEx(args);
			}
				break;
		}
	}

}