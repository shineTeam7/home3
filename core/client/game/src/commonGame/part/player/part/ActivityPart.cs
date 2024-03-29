using ShineEngine;
using System;

/// <summary>
/// 活动(generated by shine)
/// </summary>
public class ActivityPart:PlayerBasePart
{
	/** 数据 */
	private ActivityPartData _d;
	
	public override void setData(BaseData data)
	{
		base.setData(data);
		
		_d=(ActivityPartData)data;
	}
	
	/// <summary>
	/// 获取数据
	/// </summary>
	public ActivityPartData getPartData()
	{
		return _d;
	}
	
	/// <summary>
	/// 构造函数(只在new后调用一次,再次从池中取出不会调用)
	/// </summary>
	public override void construct()
	{
		
	}
	
	/// <summary>
	/// 构造数据前
	/// </summary>
	protected override void beforeMakeData()
	{
		
	}
	
	/// <summary>
	/// 初始化(创建后刚调用,与dispose成对)
	/// </summary>
	public override void init()
	{
		
	}
	
	/// <summary>
	/// 析构(回池前调用,与init成对)
	/// </summary>
	public override void dispose()
	{
		
	}
	
	/// <summary>
	/// 从库中读完数据后(做数据的补充解析)(onNewCreate后也会调用一次)(主线程)
	/// </summary>
	public override void afterReadData()
	{
		ActivityData sData;

		ActivityConfig[] values;
		ActivityConfig v;

		for(int i=(values=ActivityConfig.getDic().getValues()).Length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				if(checkEnable(v))
				{
					sData=_d.datas.get(v.id);

					//补充
					if(sData==null)
					{
						sData=new ActivityData();
						sData.id=v.id;
						sData.nextResetTime=0;
						sData.lastTurnTime=0;
						sData.nextTurnTime=0;
						sData.joinTimes=0;

						_d.datas.put(sData.id,sData);
					}

					sData.config=v;
				}
				else
				{
					//直接移除
					_d.datas.remove(v.id);
				}
			}
		}

		//fixed
		OtherUtils.removeNotExistFromDic1WithDic2(_d.datas,ActivityConfig.getDic());
	}
	
	public override void beforeLogin()
	{
		bool needTick=CommonSetting.isClientDriveLogic && me.system.isOfflineRunning();

		ActivityConfig config;

		ActivityData[] values;
		ActivityData v;

		for(int i=(values=_d.datas.getValues()).Length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				if(needTick)
				{
					runOnTime(v);
				}

				config=v.config;

				long nextCanTime=config.canSeeTimeT.getNextTime();
				long nextCantTime=config.cantSeeTimeT.getNextTime();

				//都小于0
				if(nextCanTime<=0 && nextCantTime<=0)
				{
					v.isCanSee=false;
					v.nextSeeTurnTime=-1L;
				}
				else
				{
					if(nextCanTime<=0 || nextCantTime<=0)
					{
						v.isCanSee=true;
						v.nextSeeTurnTime=Math.Max(nextCanTime,nextCantTime);
					}
					else
					{
						if(nextCanTime>nextCantTime)
						{
							v.isCanSee=true;
							v.nextSeeTurnTime=nextCantTime;
						}
						else
						{
							v.isCanSee=false;
							v.nextSeeTurnTime=nextCanTime;
						}
					}
				}
			}
		}
	}
	
	/// <summary>
	/// 每秒调用
	/// </summary>
	public override void onSecond(int delay)
	{
		long now=me.getTimeMillis();

		ActivityData[] values;
		ActivityData v;

		bool needTick=CommonSetting.isClientDriveLogic && me.system.isOfflineRunning();
		
		for(int i=(values=_d.datas.getValues()).Length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				if(v.nextSeeTurnTime>0 && now>v.nextSeeTurnTime)
				{
					//运行中
					if(v.isCanSee)
					{
						doActivityCantSee(v);
					}
					//关闭中
					else
					{
						doActivityCanSee(v);
					}
				}

				if(needTick)
				{
					if(v.nextTurnTime>0 && now>v.nextTurnTime)
					{
						//运行中
						if(v.isRunning)
						{
							doActivityCloseAtTime(v);
						}
						else
						{
							doActivityOpenAtTime(v);
						}
					}

					if(v.nextResetTime>0 && now>v.nextResetTime)
					{
						doActivityResetAtTime(v);
					}
				}
			}
		}
	}
	
	/// <summary>
	/// 功能开启(id:功能ID)
	/// </summary>
	public override void onFunctionOpen(int id)
	{
		
	}
	
	/// <summary>
	/// 功能关闭(id:功能ID)
	/// </summary>
	public override void onFunctionClose(int id)
	{
		
	}
	
	public override void onReloadConfig()
	{
		ActivityData[] values;
		ActivityData v;

		for(int i=(values=_d.datas.getValues()).Length - 1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				v.reloadConfig();
			}
		}
	}
	
	/// <summary>
	/// 活动是否开启
	/// </summary>
	public bool isActivityOpen(int id)
	{
		if(!_d.datas.contains(id))
        {
        	return false;
        }
        		
		return _d.datas.get(id).isRunning;
	}
	
	/// <summary>
	/// 活动是否可见
	/// </summary>
	public bool isActivityCanSee(int id)
	{
		return _d.datas.get(id).isCanSee;
	}
	
	/// <summary>
	/// 获取活动数据
	/// </summary>
	public ActivityData getActivity(int id)
	{
		return _d.datas.get(id);
	}
	
	/// <summary>
	/// 检查是否可参加活动
	/// </summary>
	public bool checkCanJoinActivity(int id,bool needNotice)
	{
		ActivityData data=_d.datas.get(id);

		if(data==null)
		{
			me.warnLog("检查参加活动时，找不到活动",id);
			return false;
		}

		if(!data.isRunning)
		{
			me.warnLog("检查参加活动时，活动未开始",id);
			return false;
		}

		ActivityConfig config=data.config;

		if(config.joinCount>0 && data.joinTimes>=config.joinCount)
		{
			me.warnLog("检查参加活动时，次数不足",id);
			return false;
		}

		if(!me.role.checkRoleConditions(config.joinConditions,needNotice))
		{
			me.warnLog("检查参加活动时，进入条件不满足",id);
			return false;
		}

		return true;
	}
	
	/// <summary>
	/// 检查是否可参加活动
	/// </summary>
	public bool checkEnable(ActivityConfig config)
	{
		if(config.enableConditions.Length==0)
			return true;
		
		foreach(int[] arr in config.enableConditions)
		{
			if(!checkOneEnable(arr))
				return false;
		}

		return true;
	}
	
	/// <summary>
	/// 检查活动是否失效
	/// </summary>
	public bool checkInvalid(ActivityConfig config)
	{
		if(config.invalidConditions.Length==0)
			return false;
		
		foreach(int[] arr in config.invalidConditions)
		{
			if(!checkOneEnable(arr))
				return false;
		}

		return true;
	}
	
	protected bool checkOneEnable(int[] arr)
	{
		switch(arr[0])
		{
			case ActivityEnableConditionType.ClientPlatform:
			{
				return SystemControl.clientPlatform==arr[1];
			}
		}

		return true;
	}
	
	/** 检查单个初始化 */
	private void runOnTime(ActivityData data)
	{
		ActivityConfig config=ActivityConfig.get(data.id);

		long nextEnd=config.endTimeT.getNextTime();
		long nextStart=config.startTimeT.getNextTime();

		bool isRunning;

		//没有关闭时间
		if(config.endTimeT.isEmpty())
		{
			isRunning=true;
		}
		else
		{
			//已关闭
			if(nextEnd==-1L)
			{
				isRunning=false;
			}
			else
			{
				if(nextStart==-1L)
				{
					isRunning=true;
				}
				else
				{
					//根据两个时间差判定开关
					isRunning=nextStart>nextEnd;
				}
			}
		}

		if(data.isRunning!=isRunning || data.nextTurnTime==0)
		{
			if(isRunning)
				doActivityOpen(data,nextEnd,false);
			else
				doActivityClose(data,nextStart,false);
		}
		else
		{
			long nextTurn=isRunning ? nextEnd : nextStart;

			//需要计算
			if(data.nextTurnTime!=nextTurn)
			{
				//配置变更
				if(data.nextTurnTime==-1)
				{
					data.nextTurnTime=nextTurn;
				}
				else
				{
					long now=me.getTimeMillis();

					//之前的时间
					if(data.nextTurnTime<now)
					{
						//开关一下
						if(isRunning)
						{
							doActivityClose(data,nextStart,false);
							doActivityOpen(data,nextEnd,false);
						}
						else
						{
							doActivityOpen(data,nextEnd,false);
							doActivityClose(data,nextStart,false);
						}
					}
					else
					{
						data.nextTurnTime=nextTurn;
					}
				}
			}
		}

		if(data.nextResetTime==0)
		{
			data.nextResetTime=config.resetTimeT.getNextTime();
		}
		else
		{
			long nextResetTime=config.resetTimeT.getNextTime();

			if(nextResetTime!=data.nextResetTime)
			{
				//配置变更
				if(data.nextResetTime==-1)
				{
					data.nextResetTime=nextResetTime;
				}
				else
				{
					long now=me.getTimeMillis();

					if(data.nextResetTime<now)
					{
						doActivityReset(data,nextResetTime,false);
					}
				}
			}
		}
	}
	
	private void doActivityCanSee(ActivityData data)
	{
		data.isCanSee=true;
		data.nextSeeTurnTime=data.config.cantSeeTimeT.getNextTime();

		me.dispatch(GameEventType.ActivitySee,data.id);
	}
	
	private void doActivityCantSee(ActivityData data)
	{
		data.isCanSee=false;
		data.nextSeeTurnTime=data.config.canSeeTimeT.getNextTime();

		me.dispatch(GameEventType.ActivityCantSee,data.id);
	}
	
	/** 活动开启 */
	private void doActivityOpenAtTime(ActivityData data)
	{
		doActivityOpen(data,data.nextTurnTime,data.config.endTimeT.getNextTime(),true);
	}
	
	/** 活动开启 */
	private void doActivityOpen(ActivityData data,long nextTurnTime,bool atTime)
	{
		doActivityOpen(data,data.config.startTimeT.getPrevTime(),nextTurnTime,atTime);
	}
	
	/// <summary>
	/// 服务器活动开启
	/// </summary>
	public void doActivityOpen(ActivityData data,long lastTurnTime,long nextTurnTime,bool atTIme)
	{
		//不启用的或者失效的
		if(!checkEnable(data.config) || checkInvalid(data.config))
			return;

		data.isRunning=true;
		data.lastTurnTime=lastTurnTime;
		data.nextTurnTime=nextTurnTime;

		me.onActivityOpen(data.id,atTIme);
		
		me.dispatch(GameEventType.ActivityOpen,data.id);
	}
	
	/** 活动关闭 */
	private void doActivityCloseAtTime(ActivityData data)
	{
		doActivityClose(data,data.nextTurnTime,data.config.startTimeT.getNextTime(),true);
	}
	
	/** 活动关闭 */
	private void doActivityClose(ActivityData data,long nextTurnTime,bool atTime)
	{
		doActivityClose(data,data.config.endTimeT.getPrevTime(),nextTurnTime,atTime);
	}
	
	/// <summary>
	/// 服务器活动关闭
	/// </summary>
	public void doActivityClose(ActivityData data,long lastTurnTime,long nextTurnTime,bool atTIme)
	{
		//不启用的
		if(!checkEnable(data.config))
			return;


		data.lastTurnTime=lastTurnTime;
		data.nextTurnTime=nextTurnTime;

		if(data.isRunning)
		{
			data.isRunning=false;
			me.onActivityClose(data.id,atTIme);
			me.dispatch(GameEventType.ActivityClose,data.id);
		}
	}
	
	/** 活动重置 */
	private void doActivityResetAtTime(ActivityData data)
	{
		doActivityReset(data,data.config.resetTimeT.getNextTime(),true);
	}
	
	/// <summary>
	/// 服务器活动重置
	/// </summary>
	public void doActivityReset(ActivityData data,long nextTime,bool atTIme)
	{
		data.joinTimes=0;
		data.nextResetTime=nextTime;
		me.onActivityReset(data.id,atTIme);
		
		me.dispatch(GameEventType.ActivityReset,data.id);
	}
	
	/// <summary>
	/// 服务器活动重置
	/// </summary>
	public void onActivitySwitchByServer(int id,bool isRunning,long lastTurnTime,long nextTurnTime,bool atTIme)
	{
		ActivityData data=_d.datas.get(id);

		if(isRunning)
			doActivityOpen(data,lastTurnTime,nextTurnTime,atTIme);
		else
			doActivityClose(data,lastTurnTime,nextTurnTime,atTIme);
	}
	
	/// <summary>
	/// 服务器活动重置
	/// </summary>
	public void onActivityResetByServer(int id,long nextTime,bool atTIme)
	{
		doActivityReset(_d.datas.get(id),nextTime,atTIme);
	}
	
	/// <summary>
	/// 活动完成一次
	/// </summary>
	public void activityCompleteOnce(int id,int joinTimes)
	{
		ActivityData data=_d.datas.get(id);
		data.joinTimes=joinTimes;

		me.dispatch(GameEventType.ActivityCompleteOnce,id);
	}
	
	protected override BaseData createPartData()
	{
		return new ActivityPartData();
	}
	
	/// <summary>
	/// 使用激活码
	/// </summary>
	public void useActivationCode(string code)
	{
		//转大写
		code=code.ToUpper();

		me.send(UseActivationCodeRequest.create(code));
	}
	
}
