using System;
using ShineEngine;
using UnityEngine;
using UnityEngine.UI;

/// <summary>
/// 场景3D显示逻辑基类(方案一,mmo用)
/// </summary>
public class SceneShowLogic3DOne:SceneShowLogic
{
	protected ObjectPool<DamageNumShow> _damagePool=new ObjectPool<DamageNumShow>(()=>new DamageNumShow());

	private int _damageResourceID;

	protected int _damageInstanceID=0;

	private IntObjectMap<DamageNumShow> _damageShows=new IntObjectMap<DamageNumShow>();

	protected float _damageNumRandomX=10f;
	protected float _damageNumRandomY=10f;
	protected int _damageNumLastTime=1000;
	protected float _damageNumFlyHeight=30f;

	public SceneShowLogic3DOne()
	{

	}

	public override void construct()
	{

	}

	public override void init()
	{
		base.init();

		_damageResourceID=BaseGameUtils.getUIModelResourceID("damageNum");
	}

	public override void dispose()
	{
		base.dispose();

		_damageShows.forEachValueAndClear(v=>
		{
			v.dispose();
		});
	}

	public override void onFrame(int delay)
	{
		base.onFrame(delay);
	}

	public override void showOneDamage(Unit unit,int damageType,int damageValue)
	{
		if(unit.head!=null && unit.head.isHeadFrontOfCamera())
		{
			Vector3 midPos=unit.head.getUnitMidScreenPos();

			Rect rect=_scene.camera.mainCamera.camera.pixelRect;

			//在屏幕内
			if(rect.Contains(midPos))
			{
				showDamageAt(midPos,damageType,damageValue);
			}
		}
	}

	protected DamageNumShow createDamageNumShow(int damageType,int damageValue)
	{
		DamageNumShow show=_damagePool.getOne();
		GameObject gameObject=AssetPoolControl.getAssetAndIncrease(AssetPoolType.SceneFrontUI,_damageResourceID);
		show.instanceID=++_damageInstanceID;
		show.parent=this;
		show.gameObject=gameObject;
		gameObject.SetActive(true);
		gameObject.transform.SetParent(_scene.show.getFrontUIRoot());

		_damageShows.put(show.instanceID,show);

		makeDamageNum(gameObject,damageType,damageValue);

		return show;
	}

	/** 在屏幕指定位置显示伤害数字 */
	protected void showDamageAt(Vector3 pos,int damageType,int damageValue)
	{
		DamageNumShow show=createDamageNumShow(damageType,damageValue);
		show.show(pos);
	}

	protected void makeDamageNum(GameObject obj,int damageType,int damageValue)
	{
		Text text=obj.transform.GetChild(0).GetComponent<Text>();

		Color color=Color.yellow;

		switch(damageType)
		{
			case SkillDamageType.PhysicsDamage:
			case SkillDamageType.MagicDamage:
			case SkillDamageType.HolyDamage:
			{
				color=Color.red;
				text.text="-"+damageValue.ToString();
			}
				break;
			case SkillDamageType.HpAdd:
			{
				color=Color.green;
				text.text="+"+damageValue.ToString();
			}
				break;
			case SkillDamageType.MpAdd:
			{
				color=Color.cyan;
				text.text="+"+damageValue.ToString();
			}
				break;
			case SkillDamageType.MpSub:
			{
				color=Color.blue;
				text.text="-"+damageValue.ToString();
			}
				break;
		}

		text.color=color;
	}

	public class DamageNumShow:PoolObject
	{
		public SceneShowLogic3DOne parent;

		public int instanceID;

		public GameObject gameObject;

		public int tweenIndex=-1;

		public void show(Vector3 vec)
		{

			vec.x=MathUtils.randomOffSetF(vec.x,parent._damageNumRandomX);
			vec.y=MathUtils.randomOffSetF(vec.y,parent._damageNumRandomY);

			gameObject.transform.position=vec;

			Vector3 end=vec;
			end.y+=parent._damageNumFlyHeight;

			tweenIndex=Tween.vector3.create(vec,end,parent._damageNumLastTime,v=>{ gameObject.transform.position=v; },()=>
			{
				dispose();

			},EaseType.OutQuad);
		}

		public override void clear()
		{
			base.clear();

			instanceID=0;
			gameObject=null;
			clearTween();
		}

		public void clearTween()
		{
			if(tweenIndex!=-1)
			{
				Tween.vector3.kill(tweenIndex);
				tweenIndex=-1;
			}
		}

		public void dispose()
		{
			parent._damageShows.remove(instanceID);
			clearTween();

			AssetPoolControl.unloadOne(AssetPoolType.SceneFrontUI,parent._damageResourceID,gameObject);
			parent._damagePool.back(this);
		}
	}
}