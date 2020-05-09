using UnityEngine;
using Spine.Unity;

namespace ShineEngine
{
	
	/// <summary>
	/// 
	/// </summary>
	public class AnimationBoxCollider:MonoBehaviour
	{
		public Vector2 offset;

		public Vector2 size;

		[SerializeField]
		[SpineAnimation]
		private string _animationName;

		public AnimationBoxCollider()
		{
			size.x=1;
			size.y=1;
		}
	}
}