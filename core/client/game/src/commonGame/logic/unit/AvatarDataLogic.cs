using System;
using ShineEngine;

/// <summary>
/// 造型数据逻辑
/// </summary>
public class AvatarDataLogic
{
	private UnitFightDataLogic _parent;

	private UnitAvatarData _data;

	//变身部分

	private int _defaultModelID=-1;
	private IntIntMap _dataParts;
	private int[] _nowParts=new int[AvatarPartType.size];

	/** 常规部件统计组 */
	private IntIntMap[] _normalPartsDic=new IntIntMap[AvatarPartType.size];
	/** 常规部件组 */
	private int[] _normalParts=new int[AvatarPartType.size];

	/** 变身造型ID */
	private int _changeFacadeID=-1;
	/** 单位变身部分(facadeID->num) */
	private IntIntMap _changeFacadeDic=new IntIntMap();

	//dispatch
	/** 状态推送标记 */
	protected bool _dispatchDirty=false;
	/** 上次派发的模型ID */
	private int _lastDispatchModelID=-1;
	/** 上次派发的外显组(推送用) */
	private int[] _lastDispatches=new int[AvatarPartType.size];


	public void setParent(UnitFightDataLogic parent)
	{
		_parent=parent;
	}

	public void setData(UnitAvatarData data)
	{
		_data=data;

		if(data!=null)
		{
			_lastDispatchModelID=data.modelID;

			int[] nowParts=_nowParts;
			IntIntMap[] normalPartsDic=_normalPartsDic;

			(_dataParts=data.parts).forEach((k,v)=>
			{
				IntIntMap map=new IntIntMap();
				map.put(v,1);
				normalPartsDic[k]=map;
				nowParts[k]=v;
			});
		}
		else
		{
			_dataParts=null;
		}
	}

	public void clear()
	{
		_data.modelID=-1;
		_dataParts.clear();
		
		//属性归零
		int[] nowParts=_nowParts;
		IntIntMap[] normalPartsDic=_normalPartsDic;
		IntIntMap partDic;
		int[] normalParts=_normalParts;
		int[] lastDispatches=_lastDispatches;
		
		for(int i=nowParts.Length - 1;i>=0;--i)
		{
			nowParts[i]=0;
			normalParts[i]=0;
			lastDispatches[i]=0;

			if((partDic=normalPartsDic[i])!=null)
			{
				partDic.clear();
			}
		}
		
		_changeFacadeID=-1;
		_changeFacadeDic.clear();
	}

	/** 设置默认模型ID(数据绑定后用) */
	public void setDefaultModelID(int id)
	{
//		if(id<=0)
//		{
//			Ctrl.throwError("默认模型ID不能为空");
//		}

		_defaultModelID=id;

		if(_parent.isDriveAll())
		{
			if(_data!=null)
			{
				_lastDispatchModelID=_data.modelID=id;
			}
		}
	}

	/** 获取当前部件ID */
	private int getPartID(int type)
	{
		int re;
		return (re=_nowParts[type])>0 ? re : -1;
	}

	/** 获取当前模型ID */
	public int getModelID()
	{
		return _data.modelID;
	}

	/** 获取当前该显示的部件(包括默认值)(没有返回-1) */
	public int getShowPart(int type)
	{
		int partID;

		if((partID=getPartID(type))!=-1)
		{
			return partID;
		}

		int modelID;

		if((modelID=_data.modelID)==-1)
		{
			return -1;
		}

		return ModelConfig.get(modelID).showPartsT.getOrDefault(type,-1);
	}

	/** 刷新造型 */
	public void refreshAvatar()
	{
		if(!_dispatchDirty)
			return;

		_dispatchDirty=false;

		doRefreshAvatar();
	}

	private void doRefreshAvatar()
	{
		int[] lastDispatches=_lastDispatches;
		int[] nowParts=_nowParts;
		int value;

		IntIntMap map=null;

		for(int i=nowParts.Length-1;i>=0;--i)
		{
			if((value=nowParts[i])!=lastDispatches[i])
			{
				lastDispatches[i]=value;

				if(map==null)
				{
					map=new IntIntMap();
				}

				map.put(i,value);
			}
		}

		int modelID;

		//改变了ModelID
		if(_lastDispatchModelID!=(modelID=_data.modelID))
		{
			_lastDispatchModelID=modelID;

			_parent.onAvatarChange(modelID);
		}
		else
		{
			if(map==null)
				return;

			_parent.onAvatarPartChange(map);
		}
	}

	private void setPart(int type,int id)
	{
		_nowParts[type]=id;
		_dataParts.put(type,id);
		_dispatchDirty=true;
	}

	/** 添加part */
	public void addPart(int type,int id)
	{
		IntIntMap dic=_normalPartsDic[type];

		if(dic==null)
		{
			_normalPartsDic[type]=(dic=new IntIntMap());
		}

		int nowID=getPartID(type);

		if(dic.addValue(id,1)==1 && id!=nowID)
		{
			if(AvatarPartConfig.get(type,id).proirity>AvatarPartConfig.get(type,nowID).proirity)
			{
				_normalParts[type]=id;

				if(_changeFacadeID==-1)
				{
					setPart(type,id);
				}
			}
		}
	}

	/** 删除part */
	public void removePart(int type,int id)
	{
		IntIntMap dic=_normalPartsDic[type];

		if(dic==null)
		{
			_normalPartsDic[type]=(dic=new IntIntMap());
		}

		int nowID=getPartID(type);

		if(dic.addValue(id,-1)==0)
		{
			if(id==nowID)
			{
				int[] cArgs={-1,-1};

				dic.forEach((k,v)=>
				{
					if(v>0)
					{
						int priority;

						if((priority=AvatarPartConfig.get(type,k).proirity)>cArgs[0])
						{
							cArgs[0]=priority;
							cArgs[1]=k;
						}
					}
				});

				int rid=cArgs[1];

				_normalParts[type]=rid;

				if(_changeFacadeID==-1)
				{
					setPart(type,id);
				}
			}
		}
	}

	/** 服务器设置部件 */
	public void setAvatarByServer(int modelID,IntIntMap values)
	{
		_lastDispatchModelID=_data.modelID=modelID;

		int[] lastDispatches=_lastDispatches;
		int[] nowParts=_nowParts;

		values.forEach((k,v)=>
		{
			lastDispatches[k]=nowParts[k]=v;
		});

		_parent.onAvatarChange(modelID);
	}

	/** 服务器设置部件 */
	public void setAvatarPartByServer(IntIntMap values)
	{
		int[] lastDispatches=_lastDispatches;
		int[] nowParts=_nowParts;

		values.forEach((k,v)=>
		{
			lastDispatches[k]=nowParts[k]=v;
		});

		_parent.onAvatarPartChange(values);
	}

	private void countAvatar()
	{
		IntIntMap dataParts=_dataParts;

		if(_changeFacadeID!=-1)
		{
			FacadeConfig config=FacadeConfig.get(_changeFacadeID);

			_data.modelID=config.modelID;

			dataParts.forEach((k,v)=>
			{
				setPart(k,-1);
			});

			foreach(DIntData v in config.parts)
			{
				setPart(v.key,v.value);
			}
		}
		else
		{
			_data.modelID=_defaultModelID;

			dataParts.forEach((k,v)=>
			{
				setPart(k,-1);
			});

			int[] normalParts;
			int value;

			for(int i=(normalParts=_normalParts).Length-1;i>=0;--i)
			{
			    if((value=normalParts[i])>0)
				{
					setPart(i,value);
				}
			}
		}
	}

	private void setFacade(int facadeID)
	{
		_changeFacadeID=facadeID;
		countAvatar();

		_dispatchDirty=true;
	}

	/** 添加造型更换 */
	public void addFacade(int facadeID)
	{
		if(_changeFacadeDic.addValue(facadeID,1)==1 && facadeID!=_changeFacadeID)
		{
			if(_changeFacadeID==-1 || FacadeConfig.get(facadeID).proirity>FacadeConfig.get(_changeFacadeID).proirity)
			{
				setFacade(facadeID);
			}
		}
	}

	/** 删除造型更换 */
	public void removeFacade(int facadeID)
	{
		if(_changeFacadeDic.addValue(facadeID,-1)==0)
		{
			if(facadeID==_changeFacadeID)
			{
				int[] cArgs={-1,-1};

				_changeFacadeDic.forEach((k,v)=>
				{
					if(v>0)
					{
						int priority;

						if((priority=FacadeConfig.get(k).proirity)>cArgs[0])
						{
							cArgs[0]=priority;
							cArgs[1]=k;
						}
					}
				});

				setFacade(cArgs[1]);
			}
		}
	}
}