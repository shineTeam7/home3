using System;
using ShineEngine;
using UnityEngine;

/// <summary>
/// 单位头3D逻辑(方案一,mmo用)
/// </summary>
public class UnitHeadLogic3DOne:UnitHeadLogic
{
	private int _resourceID=-1;

	private GameObject _gameObject;

	private Transform _transform;

	protected UIModel _model;

	private UIText _nameText;

	private UIImage _bloodImage;

	private CameraTool _mainCameraTool;
	private Camera _camera;

	private bool _dirty=true;

	public override void init()
	{
		base.init();

		_camera=(_mainCameraTool=_scene.camera.mainCamera).camera;

		_resourceID=BaseGameUtils.getUIModelResourceID(getUIModelName());

		_gameObject=AssetPoolControl.getAssetAndIncrease(AssetPoolType.UnitHead,_resourceID);

		if(_model==null)
			_model=toCreateModel();

		_model.init(_gameObject);
		(_transform=_gameObject.transform).SetParent(_scene.show.getUnitHeadRoot(),false);

		initModel();

		initShow();

		refreshHeight();
	}

	public override void dispose()
	{
		base.dispose();

		_model.doDispose();
		AssetPoolControl.unloadOne(AssetPoolType.UnitHead,_resourceID,_gameObject);
		_gameObject=null;
		_resourceID=-1;
		_mainCameraTool=null;
		_camera=null;
		_dirty=true;
	}

	protected virtual void initModel()
	{
		_nameText=_model.getChild<UIText>("nameText");
		_bloodImage=_model.getChild<UIImage>("blood");
	}

	protected virtual string getUIModelName()
	{
		return "npcHead";
	}

	protected virtual UIModel toCreateModel()
	{
		return null;
	}

	protected virtual void initShow()
	{
		_nameText.setString(_unit.identity.getUnitName());
		onRefreshHp();
	}

	public override void refreshHeight()
	{
		base.refreshHeight();

		_dirty=true;
	}

	protected override void doSetPos(PosData pos)
	{
		base.doSetPos(pos);

		_dirty=true;
	}

	public override void onFrame(int delay)
	{
		if(_transform!=null)
		{
			if(_dirty || _mainCameraTool.currentFrameChanged())
			{
				_dirty=false;

				doRefreshPos();
			}
		}
	}

	protected void doRefreshPos()
	{
		_transform.position=getUnitHeadScreenPos();
	}

	public override void onRefreshHp()
	{
		AttributeDataLogic attributeDataLogic=_unit.fight.getAttributeLogic();
		_bloodImage.image.fillAmount=attributeDataLogic.getHpPercent();
	}
}