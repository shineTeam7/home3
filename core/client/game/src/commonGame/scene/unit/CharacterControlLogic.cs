using System;
using ILRuntime.Runtime;
using ShineEngine;
using UnityEngine;
using UnityEngine.AI;

/// <summary>
/// 角色控制逻辑
/// </summary>
public class CharacterControlLogic:UnitLogicBase
{
	private bool _isDirMoving=false;
	//摇杆部分
	private DirData _moveDir=new DirData();

	/** 缓存当前摇杆方向 */
	private float _cacheDir;
	/** 缓存摄像机方向 */
	private float _cacheCameraAxisY;

	//temp
	private PosData _tempPos=new PosData();

	protected CharacterController _controller;

	public override void afterInit()
	{
		base.afterInit();

		_controller=_unit.show.gameObject.AddComponent<CharacterController>();

		//默认修改
		_controller.skinWidth=0.01f;
		_controller.stepOffset=0.3f;
	}

	public override void preRemove()
	{
		base.preRemove();

		if(_controller!=null)
		{
			GameObject.Destroy(_controller);
			_controller=null;
		}
	}

	public override void dispose()
	{
		base.dispose();

		_isDirMoving=false;
	}

	public CharacterController getCharacterController()
	{
		return _controller;
	}

	public override void onFrame(int delay)
	{
		base.onFrame(delay);

		if(_isDirMoving)
		{
			if(!CommonSetting.cameraCacheDir)
			{
				float mainCameraAxisY=_scene.camera.mainCamera.currentAxisY;

				if(mainCameraAxisY!=_cacheCameraAxisY)
				{
					_cacheCameraAxisY=mainCameraAxisY;
					doMoveDir(false);
				}
			}

			//如不是就修正
			if(_unit.aiCommand.getCurrentCommand()!=UnitAICommandType.MoveDir)
			{
				doMoveDir(false);
			}
		}
	}

	/** 移动到目标通过鼠标点击 */
	public void moveToByClick(Vector3 vec)
	{
		//TODO:fixPos

		//主角移动
		_tempPos.setByVector(vec);

		if(_scene.hero.move.isOnVehicle())
		{
			//当前在载具上
			return;
		}

		_scene.hero.aiCommand.moveTo(_tempPos,null);
		// _scene.hero.aiCommand.attackMoveTo(_tempPos,null);
	}

	/** 开始移动朝向 */
	public void startMoveDir()
	{
		//存好摄像机位置
		_cacheCameraAxisY=_scene.camera.mainCamera.currentAxisY;
	}

	/** 常规移动朝向 */
	public void moveDir(float dir)
	{
		_cacheDir=dir;

		if(_isDirMoving)
		{
			doMoveDir(false);
		}
		else
		{
			_isDirMoving=true;
			doMoveDir(true);
		}
	}

	/** 取消移动朝向 */
	public void cancelMoveDir()
	{
		if(!_isDirMoving)
			return;

		_isDirMoving=false;
		_unit.aiCommand.reDefaultCommand();
	}

	private void doMoveDir(bool isFirst)
	{
		float dir=MathUtils.directionCut(_cacheDir - _cacheCameraAxisY);

		// float speed=0.1f;
		// Vector3 vec=Vector3.zero;
		// vec.x=(float)(Math.Cos(dir)*speed);
		// vec.z=(float)(Math.Sin(dir)*speed);
		//
		// CollisionFlags c=_controller.Move(vec);

		if(isFirst || _moveDir.direction!=dir)
		{
			_moveDir.direction=dir;

			if(_scene.play.canOperate())
			{
				_unit.aiCommand.moveDir(_moveDir);
			}
		}
	}
}