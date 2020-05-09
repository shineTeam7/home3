using System;
using ShineEngine;
using UnityEngine;

/// <summary>
/// 单位特效
/// </summary>
public class UnitEffect
{
	/** id */
	public int id;
	/** 配置 */
	protected EffectConfig _config;

	/** 场景 */
	protected Scene _scene;

	/** 单位 */
	protected Unit _unit;
	/** 子弹 */
	protected Bullet _bullet;

	/** 是否播放完毕 */
	private bool _isOver=false;

	private AssetPoolLoadTool _loadTool;

	protected GameObject _gameObject;
	protected Transform _transform;

	public virtual void construct()
	{
		_loadTool=new AssetPoolLoadTool(AssetPoolType.UnitModel,loadOver,null);
	}

	public virtual void setConfig(EffectConfig config)
	{
		_config=config;
		id=config.id;
	}

	/** 设置场景 */
	public void setScene(Scene scene)
	{
		_scene=scene;
	}

	/** 绑定单位 */
	public void bindUnit(Unit unit)
	{
		_unit=unit;
	}

	public void bindBullet(Bullet bullet)
	{
		_bullet=bullet;
	}

	/** 开始显示 */
	public virtual void init()
	{
		int souceID;

		if((souceID=_config.sourceT)>0)
		{
			_loadTool.loadOne(souceID);
		}
		else
		{
			_loadTool.clear();

			onLoadOver(null);
			afterLoadOver();
		}
	}

	/** 结束显示 */
	public virtual void dispose()
	{
		_loadTool.clear();

		_config=null;
		_unit=null;
		_bullet=null;
		_scene=null;
		_isOver=false;

		if(_gameObject!=null)
		{
			// GameObject.Destroy(_gameObject);
			_gameObject=null;
		}

		_transform=null;
	}

	public GameObject gameObject
	{
		get {return _gameObject;}
	}

	public Transform transform
	{
		get {return _transform;}
	}

	private void loadOver(GameObject obj)
	{
		if(_config==null)
			return;

		obj.transform.localPosition=Vector3.zero;

		onLoadOver(obj);

		afterLoadOver();
	}

	protected virtual void onLoadOver(GameObject obj)
	{
		if(_config.soundT>0)
			GameC.audio.playSound(_config.soundT);

		if(obj!=null)
		{
			bindGameObject(obj);
		}
	}

	protected virtual void afterLoadOver()
	{
		if(_unit!=null)
		{
			//TODO:这里需要对位置

			onSetPos(_unit.pos.getPos());
			onSetDir(_unit.pos.getDir());

		}
		else if(_bullet!=null)
		{
			onSetPos(_bullet.pos.getPos());
			onSetDir(_bullet.pos.getDir());
		}

		if(_unit!=null)
		{
			_unit.show.effectLoadOver(this);
		}
		else if(_bullet!=null)
		{
			_bullet.show.effectLoadOver();
		}
	}

	/** 绑定位置到Unit */
	protected void bindGameObject(GameObject obj)
	{
		obj.SetActive(true);
		obj.layer=LayerType.Effect;

		_gameObject=obj;
		_transform=obj.transform;

		if(_unit!=null)
		{
			_transform.SetParent(_unit.show.transform);
		}
		else
		{
			_transform.SetParent(_scene.show.getEffectLayer().transform);
		}
	}

	/** 是否播放完毕 */
	public bool isOver()
	{
		return _isOver;
	}

	/** 播放完毕 */
	protected void playOver()
	{
		if(!_config.isOnce)
			return;

		if(_isOver)
			return;

		_isOver=true;

		if(_unit!=null)
		{
			_unit.show.effectPlayOver(this);
		}
		else if(_bullet!=null)
		{
			_bullet.show.effectPlayOver();
		}
	}

	/** 单位更改位置 */
	public virtual void onSetPos(PosData pos)
	{
		if(_bullet!=null && _transform!=null)
		{
			_transform.position=pos.getVector();
		}
	}

	/** 单位更改朝向 */
	public virtual void onSetDir(DirData dir)
	{
		
	}
}