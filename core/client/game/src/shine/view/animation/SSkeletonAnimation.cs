using System;
using Spine;
using Spine.Unity;
using UnityEngine;

namespace ShineEngine
{
	/// <summary>
	/// 骨骼动画
	/// </summary>
	public class SSkeletonAnimation
	{
		private bool _inited=false;
		private Transform _transform;
		/** 骨骼动画 */
		private SkeletonAnimation _spineAnimation;
		/** 骨骼 */
		private Skeleton _skeleton;

		private SMap<string,float> _durationDic=new SMap<string,float>();

		private string _action;
		/** 速度 */
		private float _speed=1f;

		private bool _isLoop=false;

		/** 完成回调(isLoop为false的时候有效) */
		private Action _overCall;

		public void initObject(GameObject obj)
		{
			if(_inited)
			{
				dispose();
			}

			_inited=true;

			obj.SetActive(true);

			_transform=obj.transform;
			_spineAnimation=obj.GetComponent<SkeletonAnimation>();
			_spineAnimation.timeScale=1f;

			var animations=_spineAnimation.SkeletonDataAsset.GetSkeletonData(true).Animations;
			for(int i=0;i<animations.Count;i++)
			{
				string name=animations.Items[i].Name;
				float duration=animations.Items[i].Duration;

				_durationDic.put(name,duration);
			}

			(_skeleton=_spineAnimation.Skeleton).SetToSetupPose();
//			_spineAnimation.state.ClearTracks();

			_spineAnimation.state.Complete+=onActionOver;
		}

		/** 析构 */
		public void dispose()
		{
			if(!_inited)
				return;

			_inited=false;

			_transform=null;
			_spineAnimation=null;
			_durationDic.clear();
			_overCall=null;
		}

		/** 是否初始化过 */
		public bool inited()
		{
			return _inited;
		}

		private void onActionOver(TrackEntry entry)
		{
			if(!_inited)
				return;

			if(!_isLoop)
			{
				_spineAnimation.skeleton.PoseWithAnimation(_action,0.9999f,_isLoop);

				if(_overCall!=null)
					_overCall();
			}
		}

		/** 获取spine动画类 */
		public Skeleton getSkeleton()
		{
			return _skeleton;
		}

		public void pause()
		{
			if(!_inited)
				return;

		}

		public void resume()
		{
			if(!_inited)
				return;

		}

		public void setSkin(string value)
		{
			if(!_inited)
				return;

			_skeleton.SetSkin(value);
		}

		public void setColor(Color color)
		{
			if(!_inited)
				return;

			_skeleton.SetColor(color);
		}

		public void setScale(float scale)
		{
			if(!_inited)
				return;

			_transform.localScale=new Vector3(scale,scale,1f);
		}

		/** 设置x翻转 */
		public void setFlipX(bool value)
		{
			if(!_inited)
				return;

			_skeleton.FlipX=value;
		}

		/** 设置速率 */
		public void setSpeed(float speed)
		{
			_speed=speed;
			_spineAnimation.timeScale=speed;
		}

		/** 设置播放完成回调 */
		public void setActionOverCall(Action func)
		{
			_overCall=func;
		}

		public void playAction(string action,bool isLoop=true)
		{
			playAction(action,1f,0,isLoop);
		}

		public void playAction(string action,float speed,float startTime,bool isLoop)
		{
			if(!_inited)
				return;

			_action=action;

//			_skeleton.SetToSetupPose();
//			_spineAnimation.state.ClearTracks();

			_speed=speed;
			_isLoop=isLoop;
			_spineAnimation.timeScale=speed;

			_spineAnimation.skeleton.PoseWithAnimation(action,0.1f,isLoop);
			_spineAnimation.state.SetAnimation(0,action,isLoop);

//			Ctrl.print("看状态",_spineAnimation.state.Start);
		}
	}
}