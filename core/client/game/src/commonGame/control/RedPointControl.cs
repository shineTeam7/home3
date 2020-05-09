using System;
using ShineEngine;

/// <summary>
/// 红点控制
/// </summary>
[Hotfix]
public class RedPointControl
{
	/** 数据组 */
	private int[] _dic=new int[RedPointType.size];
	/** 脏标记组 */
	private bool[] _dirtyDic=new bool[RedPointType.size];

	/** 初始化统计 */
	public void initFirst()
	{
		//全dirty
		for(int i=RedPointType.size - 1;i>=0;--i)
		{
			_dirtyDic[i]=true;
		}

		//再计算
		for(int i=RedPointType.size-1;i>=0;--i)
		{
			_dic[i]=get(i);
		}
	}

	/** 更新单个 */
	public void refreshOne(int type,bool needEvent)
	{
		dirtyOne(type,needEvent);
	}

	/** 更新单个 */
	public void refreshOne(int type)
	{
		refreshOne(type,true);
	}

	/** 刷新一组*/
	public void refreshSome(params int[] args)
	{
		for(int i=0;i<args.Length;i++)
		{
			refreshOne(args[i]);
		}
	}

	/** 刷新一组*/
	public void refreshSome(bool needEvent,params int[] args)
	{
		for(int i=0;i<args.Length;i++)
		{
			refreshOne(args[i],needEvent);
		}
	}

	/** 获取红点值 */
	public int get(int type)
	{
		if(_dirtyDic[type])
		{
			_dic[type]=countOne(type);
			_dirtyDic[type]=false;
		}

		return _dic[type];
	}

	/** 是否有红点 */
	public bool has(int type)
	{
		if(_dirtyDic[type])
		{
			_dic[type]=countOne(type);
			_dirtyDic[type]=false;
		}

		return _dic[type]>0;
	}

	private void dirtyOne(int type,bool needEvent)
	{
		//已dirty跳过
		if(_dirtyDic[type])
			return;

		_dirtyDic[type]=true;

		if(needEvent)
		{
			GameC.player.dispatch(GameEventType.RefreshRedPoint,type);
		}
		
		RedPointConfig config=RedPointConfig.get(type);

		if(config.parentID>0)
		{
			dirtyOne(config.parentID,needEvent);
		}
	}

	/** 统计一个(已脏的) */
	private int countOne(int type)
	{
		RedPointConfig config;

		if((config=RedPointConfig.get(type))==null)
			return 0;

		int re=0;

		//不为空
		if(!config.children.isEmpty())
		{
			int[] values=config.children.getValues();

			for(int i=0,len=config.children.size();i<len;++i)
			{
				re+=get(values[i]);
			}
		}

		re+=toCountOne(type);

		return re;
	}

	/** 计算单个 */
	protected virtual int toCountOne(int type)
	{
		Player me=GameC.player;

		switch(type)
		{
			case RedPointType.Bag:
			{
				return me.bag.getRedPointCount();
			}
			case RedPointType.Mail:
			{
				return me.mail.getRedPointCount();
			}
		}

		return 0;
	}
}