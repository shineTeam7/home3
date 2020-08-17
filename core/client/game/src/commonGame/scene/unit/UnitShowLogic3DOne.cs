using System;
using ShineEngine;
using UnityEngine;

/// <summary>
/// 单位3D显示逻辑(方案一,mmo用)
/// </summary>
public class UnitShowLogic3DOne:UnitShowLogic
{
	//temp

	protected GameObject _mainObj;

	private Animator _animator;

	private Animation _animation;

	private AnimationState _animationState;

	public UnitShowLogic3DOne()
	{

	}

	protected override void disposeMainShow()
	{
		base.disposeMainShow();

		_animator=null;
		_animation=null;
		_animationState=null;
	}

	protected override void doSetDir(DirData dir)
	{
		if(_transform!=null)
		{
			Quaternion qt=new Quaternion();
			dir.setToQuaternion(ref qt);
			_transform.rotation=qt;
		}
	}

	protected override void onModelLoadOver(int modelID,GameObject obj)
	{
		setMainObj(obj);
		updateShow();
	}

	protected virtual void setMainObj(GameObject obj)
	{
		_mainObj=obj;

		_animator=_mainObj.GetComponent<Animator>();

		_animation=_mainObj.GetComponent<Animation>();

		//挂上
		//		gameObject.transform.parent=_transform;
		obj.transform.SetParent(_transform,false);
		obj.transform.localPosition=Vector3.zero;
		obj.transform.localEulerAngles=Vector3.zero;

		//TODO:设置Layer
	}

	protected override void toPlayMotion(int id,float speed,float startFrame,bool isLoop)
	{
		string useName=MotionConfig.get(id).useName;

		if(_animator!=null)
		{
			_animator.speed=speed;

			try
			{
				_animator.CrossFade(useName,0f,0,0f);
			}
			catch(Exception e)
			{
				Ctrl.errorLog(e,"播放动画失败",useName,_unit.avatar.getModelID());
			}

		}
		else if(_animation!=null)
		{
			_animation.wrapMode=isLoop ? WrapMode.Loop : WrapMode.Once;

			try
			{
				_animation.CrossFade(useName);
				_animationState=_animation[useName];
				_animationState.speed=speed;
			}
			catch(Exception e)
			{
				Ctrl.errorLog(e,"播放动画失败",useName,_unit.avatar.getModelID());
			}
		}
	}

	public override void setSpeed(float speed)
	{
		if(_animator!=null)
		{
			_animator.speed=speed;
		}
		else if(_animationState!=null)
		{
			_animationState.speed=speed;
		}
	}
}