using System;
using UnityEngine;

namespace ShineEngine
{
	public class Vector3TweenFactory:TweenFactoryBase<Vector3>
	{
		protected override Vector3 getValueFunc(Vector3 start,Vector3 end,float progress)
		{
			return start + (end - start) * progress;
		}
	}
}