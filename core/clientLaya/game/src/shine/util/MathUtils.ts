namespace Shine
{
	export class MathUtils 
	{
		/** float PI */
		public static fPI: number = Math.PI;
		/** float PI*2 */
		public static fPI2: number = (Math.PI + Math.PI);
		/** float PI/2 */
		public static fPI_half: number = Math.PI / 2;

		/** 弧度转角度 */
		public static RadToDeg: number = 180 / MathUtils.fPI;
		/** 弧度转角度 */
		public static DegToRad: number = MathUtils.fPI / 180;

		/** float相等比较 */
		public static floatEquals(a: number, b: number): boolean 
		{
			if (a == b)
				return true;

			var d: number = a - b;

			return d > -0.000001 && d < 0.000001;
		}

		/** 是否2次幂 */
		public static isPowerOf2(n: number): boolean 
		{
			return (n & (n - 1)) == 0;
		}

		/** 取2为底对数 */
		public static log2(i: number): number 
		{
			var r: number = 0;
			while ((i >>= 1) != 0) 
			{
				++r;
			}

			return r;
		}

		/** 获取该数字的2次幂(re>=cap) */
		public static getPowerOf2(cap: number): number 
		{
			if ((cap & (cap - 1)) == 0) 
			{
				return cap;
			}

			let n = cap - 1;
			n |= n >>> 1;
			n |= n >>> 2;
			n |= n >>> 4;
			n |= n >>> 8;
			n |= n >>> 16;
			return (n < 0) ? 1 : n + 1;
		}

		/**（区别于%运算，例：absMode(-1,3) = 2) */
		public static absMod(a: number, b: number): number 
		{
			return a - Math.floor(a / b) * b;
		}

		/** 产生一个(0-1)的double */
		public static random(): number
		{
			return Math.random();
		}

		/** 随机int值 */
		public static randomInt(len:number):number
		{
			return Math.floor(Math.random()*len);
		}

		public static randomProb(prob:number,max:number):boolean
		{
			if(prob >= max)
			{
				return true;
			}

			if(prob<=0)
			{
				return false;
			}

			return this.random()<prob / max;
		}

		/** 上下限获取 */
		public static clamp(value:number,min:number,max:number):number
		{
			if (value < min)
				value = min;
			else if (value > max)
				value = max;
			return value;
		}

		/** 整数比较 */
		public static intCompare(x:number,y:number):number
		{
			return (x < y) ? -1 : ((x == y) ? 0 : 1);
		}

		/** double比较 */
		public static doubleCompare(x:number,y:number):number
		{
			return (x < y) ? -1 : ((x == y) ? 0 : 1);
		}
	}
}