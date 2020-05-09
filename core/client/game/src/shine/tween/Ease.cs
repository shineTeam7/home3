using System;

namespace ShineEngine
{
	public static class Ease
	{
		public static Func<float,float,float,float,float> getEaseFunc(int ease)
		{
			switch(ease)
			{
				case EaseType.Linear:
					return easeLiner;
				case EaseType.InQuad:
					return easeInQuad;
				case EaseType.OutQuad:
					return easeOutQuad;
				case EaseType.InOutQuad:
					return easeInOutQuad;
				case EaseType.InCubic:
					return easeInCubic;
				case EaseType.OutCubic:
					return easeOutCubic;
				case EaseType.InOutCubic:
					return easeInOutCubic;
				case EaseType.InQuart:
					return easeInQuart;
				case EaseType.OutQuart:
					return easeOutQuart;
				case EaseType.InOutQuart:
					return easeInOutQuart;
				case EaseType.InQuint:
					return easeInQuint;
				case EaseType.OutQuint:
					return easeOutQuint;
				case EaseType.InOutQuint:
					return easeInOutQuint;
				case EaseType.InSine:
					return easeInSine;
				case EaseType.OutSine:
					return easeOutSine;
				case EaseType.InOutSine:
					return easeInOutSine;
				case EaseType.InExpo:
					return easeInExpo;
				case EaseType.OutExpo:
					return easeOutExpo;
				case EaseType.InOutExpo:
					return easeInOutExpo;
				case EaseType.InCirc:
					return easeInCirc;
				case EaseType.OutCirc:
					return easeOutCirc;
				case EaseType.InOutCirc:
					return easeInOutCirc;
				case EaseType.InElastic:
					return easeInElastic;
				case EaseType.OutElastic:
					return easeOutElastic;
				case EaseType.InOutElastic:
					return easeInOutElastic;
				case EaseType.InBack:
					return easeInBack;
				case EaseType.OutBack:
					return easeOutBack;
				case EaseType.InOutBack:
					return easeInOutBack;
				case EaseType.InBounce:
					return easeInBounce;
				case EaseType.OutBounce:
					return easeOutBounce;
				case EaseType.InOutBounce:
					return easeInOutBounce;
			}

			return easeLiner;
		}

		private static float easeLiner(float t,float b,float c,float d)
		{
			return c * t / d + b;
		}

		private static float easeInQuad(float t,float b,float c,float d)
		{
			return c * (t/=d) * t + b;
		}

		private static float easeOutQuad(float t,float b,float c,float d)
		{
			return -c * (t/=d) * (t - 2) + b;
		}

		private static float easeInOutQuad(float t,float b,float c,float d)
		{
			if((t/=d / 2)<1)
				return c / 2 * t * t + b;

			return -c / 2 * ((--t) * (t - 2) - 1) + b;
		}

		private static float easeInCubic(float t,float b,float c,float d)
		{
			return c * (t/=d) * t * t + b;
		}

		private static float easeOutCubic(float t,float b,float c,float d)
		{
			return c * ((t=t / d - 1) * t * t + 1) + b;
		}

		private static float easeInOutCubic(float t,float b,float c,float d)
		{
			if((t/=d / 2)<1)
				return c / 2 * t * t * t + b;

			return c / 2 * ((t-=2) * t * t + 2) + b;
		}

		private static float easeInQuart(float t,float b,float c,float d)
		{
			return c * (t/=d) * t * t * t + b;
		}

		private static float easeOutQuart(float t,float b,float c,float d)
		{
			return -c * ((t=t / d - 1) * t * t * t - 1) + b;
		}

		private static float easeInOutQuart(float t,float b,float c,float d)
		{
			if((t/=d / 2)<1)
				return c / 2 * t * t * t * t + b;

			return -c / 2 * ((t-=2) * t * t * t - 2) + b;
		}

		private static float easeInQuint(float t,float b,float c,float d)
		{
			return c * (t/=d) * t * t * t * t + b;
		}

		private static float easeOutQuint(float t,float b,float c,float d)
		{
			return c * ((t=t / d - 1) * t * t * t * t + 1) + b;
		}

		private static float easeInOutQuint(float t,float b,float c,float d)
		{
			if((t/=d / 2)<1)
				return c / 2 * t * t * t * t * t + b;

			return c / 2 * ((t-=2) * t * t * t * t + 2) + b;
		}

		private static float easeInSine(float t,float b,float c,float d)
		{
			return (float)(-c * Math.Cos(t / d * (Math.PI / 2)) + c + b);
		}

		private static float easeOutSine(float t,float b,float c,float d)
		{
			return (float)(c * Math.Sin(t / d * (Math.PI / 2)) + b);
		}

		private static float easeInOutSine(float t,float b,float c,float d)
		{
			return (float)(-c / 2 * (Math.Cos(Math.PI * t / d) - 1) + b);
		}

		private static float easeInExpo(float t,float b,float c,float d)
		{
			return MathUtils.floatEquals(t,0) ? b : (float)(c * Math.Pow(2,10 * (t / d - 1)) + b);
		}

		private static float easeOutExpo(float t,float b,float c,float d)
		{
			return MathUtils.floatEquals(t,d) ? b + c : (float)(c * (-Math.Pow(2,-10 * t / d) + 1) + b);
		}

		private static float easeInOutExpo(float t,float b,float c,float d)
		{
			if(MathUtils.floatEquals(t,0))
				return b;
			if(MathUtils.floatEquals(t,d))
				return b + c;
			if((t/=d / 2)<1)
				return (float)(c / 2 * Math.Pow(2,10 * (t - 1)) + b);

			return (float)(c / 2 * (-Math.Pow(2,-10 * --t) + 2) + b);
		}

		private static float easeInCirc(float t,float b,float c,float d)
		{
			return (float)(-c * (Math.Sqrt(1 - (t/=d) * t) - 1) + b);
		}

		private static float easeOutCirc(float t,float b,float c,float d)
		{
			return (float)(c * Math.Sqrt(1 - (t=t / d - 1) * t) + b);
		}

		private static float easeInOutCirc(float t,float b,float c,float d)
		{
			if((t/=d / 2)<1)
				return (float)(-c / 2 * (Math.Sqrt(1 - t * t) - 1) + b);

			return (float)(c / 2 * (Math.Sqrt(1 - (t-=2) * t) + 1) + b);
		}

		private static float easeInElastic(float t,float b,float c,float d)
		{
			float s;
			float p;
			float a=c;
			if(MathUtils.floatEquals(t,0))
				return b;
			if(MathUtils.floatEquals(t/=d,1))
				return b + c;

			p=d * .3f;
			if(a<Math.Abs(c))
			{
				a=c;
				s=p / 4;
			}
			else
				s=(float)(p / (2 * Math.PI) * Math.Asin(c / a));

			return (float)(-(a * Math.Pow(2,10 * (t-=1)) * Math.Sin((t * d - s) * (2 * Math.PI) / p)) + b);
		}

		private static float easeOutElastic(float t,float b,float c,float d)
		{
			float s;
			float p;
			float a=c;
			if(MathUtils.floatEquals(t,0))
				return b;
			if(MathUtils.floatEquals(t/=d,1))
				return b + c;

			p=d * .3f;
			if(a<Math.Abs(c))
			{
				a=c;
				s=p / 4;
			}
			else
				s=(float)(p / (2 * Math.PI) * Math.Asin(c / a));

			return (float)(a * Math.Pow(2,-10 * t) * Math.Sin((t * d - s) * (2 * Math.PI) / p) + c + b);
		}

		private static float easeInOutElastic(float t,float b,float c,float d)
		{
			float s;
			float p;
			float a=c;
			if(MathUtils.floatEquals(t,0))
				return b;
			if(MathUtils.floatEquals(t/=d / 2,2))
				return b + c;

			p=d * (.3f * 1.5f);
			if(a<Math.Abs(c))
			{
				a=c;
				s=p / 4;
			}
			else
				s=(float)(p / (2 * Math.PI) * Math.Asin(c / a));

			if(t<1)
				return (float)(-.5f * (a * Math.Pow(2,10 * (t-=1)) * Math.Sin((t * d - s) * (2 * Math.PI) / p)) + b);

			return (float)(a * Math.Pow(2,-10 * (t-=1)) * Math.Sin((t * d - s) * (2 * Math.PI) / p) * .5f + c + b);
		}

		private static float easeInBack(float t,float b,float c,float d)
		{
			float s=1.70158f;
			return c * (t/=d) * t * ((s + 1) * t - s) + b;
		}

		private static float easeOutBack(float t,float b,float c,float d)
		{
			float s=1.70158f;
			return c * ((t=t / d - 1) * t * ((s + 1) * t + s) + 1) + b;
		}

		private static float easeInOutBack(float t,float b,float c,float d)
		{
			float s=1.70158f;
			if((t/=d / 2)<1)
				return c / 2 * (t * t * (((s*=1.525f) + 1) * t - s)) + b;

			return c / 2 * ((t-=2) * t * (((s*=1.525f) + 1) * t + s) + 2) + b;
		}

		private static float easeInBounce(float t,float b,float c,float d)
		{
			return c - easeOutBounce(d - t,0,c,d) + b;
		}

		private static float easeOutBounce(float t,float b,float c,float d)
		{
			if((t/=d)<1 / 2.75f)
			{
				return c * (7.5625f * t * t) + b;
			}
			else if(t<2 / 2.75f)
			{
				return c * (7.5625f * (t-=1.5f / 2.75f) * t + .75f) + b;
			}
			else if(t<2.5f / 2.75f)
			{
				return c * (7.5625f * (t-=2.25f / 2.75f) * t + .9375f) + b;
			}
			else
			{
				return c * (7.5625f * (t-=2.625f / 2.75f) * t + .984375f) + b;
			}
		}

		private static float easeInOutBounce(float t,float b,float c,float d)
		{
			if(t<d / 2)
				return easeInBounce(t * 2,0,c,d) * .5f + b;

			return easeOutBounce(t * 2 - d,0,c,d) * .5f + c * .5f + b;
		}
	}
}