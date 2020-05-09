using System;

namespace ShineEngine
{
	public class FloatTweenFactory:TweenFactoryBase<float>
	{
		protected override float getValueFunc(float start,float end,float progress)
		{
			return start + (end - start) * progress;
		}
	}
}