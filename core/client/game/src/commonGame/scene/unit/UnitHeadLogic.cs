using System;
using ShineEngine;
using UnityEngine;

/// <summary>
/// 单位头显示逻辑
/// </summary>
public class UnitHeadLogic:UnitLogicBase
{
	protected CameraTool _mainCamera;

	private bool _cameraPosDirty=false;

	protected bool _isFrontOfCamera=false;

	protected Vector3 _unitHeadScreenPos=new Vector3();

	protected Vector3 _unitMidScreenPos=new Vector3();

	public override void init()
	{
		base.init();

		_mainCamera=_scene.camera.mainCamera;
		_cameraPosDirty=true;

	}

	public override void dispose()
	{
		base.dispose();

		_mainCamera=null;
	}

	/** 更新单位头高度 */
	public virtual void refreshHeight()
	{

	}

	/** 显示层设置坐标 */
	public void setPos(PosData pos)
	{
		doSetPos(pos);
	}

	protected virtual void doSetPos(PosData pos)
	{
		_cameraPosDirty=true;
	}

	/** 属性变化 */
	public virtual void onAttributeChange(bool[] changeSet)
	{
		if(changeSet[AttributeType.Hp] || changeSet[AttributeType.HpMax])
		{
			onRefreshHp();
		}

		if(changeSet[AttributeType.Mp] || changeSet[AttributeType.MpMax])
		{
			onRefreshMp();
		}

		if(changeSet[AttributeType.PhysicsShield])
		{
			onRefreshPhysicsShield();
		}

		if(changeSet[AttributeType.MagicShield])
		{
			onRefreshMagicShield();
		}
	}

	/** 刷新Hp */
	public virtual void onRefreshHp()
	{

	}

	/** 刷新Mp */
	public virtual void onRefreshMp()
	{

	}

	public virtual void onRefreshPhysicsShield()
	{

	}

	public virtual void onRefreshMagicShield()
	{

	}

	/** 更新玩家名字 */
	public virtual void onRefreshRoleName()
	{

	}

	protected void reCountCameraPos()
	{
		if(_cameraPosDirty || _mainCamera.currentFrameChanged())
		{
			_cameraPosDirty=false;
			doReCountCameraPos();
		}
	}

	protected virtual void doReCountCameraPos()
	{
		Vector3 vec0=_unit.show.transform.position;
		Vector3 vec1=vec0;
		Vector3 vec2=vec0;

		vec1.y+=_unit.show.getUnitHeight();
		vec2.y+=(_unit.show.getUnitHeight() / 2);

		Camera camera=_mainCamera.camera;

		Transform cameraTransform=camera.transform;

		Vector3 dir = vec1 - cameraTransform.position;
		float dot = Vector3.Dot(cameraTransform.forward, dir);//判断物体是否在相机前面

		_isFrontOfCamera=dot>0;

		if(_isFrontOfCamera)
		{
			_unitHeadScreenPos=camera.WorldToScreenPoint(vec1);
			_unitMidScreenPos=camera.WorldToScreenPoint(vec2);
		}
		else
		{
			_unitHeadScreenPos.Set(-2000,-2000,0);
			_unitMidScreenPos=_unitHeadScreenPos;
		}
	}

	/** 单位头位置，是否在摄像机前 */
	public bool isHeadFrontOfCamera()
	{
		reCountCameraPos();
		return _isFrontOfCamera;
	}

	public Vector3 getUnitHeadScreenPos()
	{
		reCountCameraPos();
		return _unitHeadScreenPos;
	}

	public Vector3 getUnitMidScreenPos()
	{
		reCountCameraPos();
		return _unitMidScreenPos;
	}
}