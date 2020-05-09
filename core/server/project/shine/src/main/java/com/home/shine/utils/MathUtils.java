package com.home.shine.utils;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.data.DIntData;
import com.home.shine.support.collection.IntList;
import com.home.shine.support.collection.IntSet;

import java.util.Random;

/** 数学方法 */
public class MathUtils
{
	private static final float _fd=0.000001f;
	private static final double _dd=0.000001;
	
	/** float PI */
	public static final float fPI=(float)Math.PI;
	/** float PI*2 */
	public static final float fPI2=(float)(Math.PI+Math.PI);
	/** float PI/2 */
	public static final float fPI_half=(float)Math.PI/2;
	
	/** 弧度转角度 */
	public static final float RadToDeg=180f/fPI;
	/** 弧度转角度 */
	public static final float DegToRad=fPI/180f;
	
	private static Random _random=new Random();

	private static char chs[] = new char[36];
	static {
		for(int i = 0; i < 10 ; i++) {
			chs[i] = (char)('0' + i);
		}
		for(int i = 10; i < chs.length; i++) {
			chs[i] = (char)('A' + (i - 10));
		}
	}

	/** float相等比较 */
	public static boolean floatEquals(float a,float b)
	{
		if(a==b)
			return true;
		
		float d=a - b;
		
		return d>-_fd && d<_fd;
	}
	
	/** double相等比较 */
	public static boolean doubleEquals(double a,double b)
	{
		if(a==b)
			return true;
		
		double d=a - b;
		
		return d>-_dd && d<_dd;
	}
	
	/** 是否2次幂 */
	public static boolean isPowerOf2(int n)
	{
		return (n & (n - 1))==0;
	}
	
	/** 获取该数字的2次幂(re>=n) */
	public static int getPowerOf2(int n)
	{
		if((n & (n - 1))==0)
		{
			return n;
		}
		
		return Integer.MIN_VALUE >>> (Integer.numberOfLeadingZeros(n) - 1);
	}
	
	/** 获取该数字的字节使用位数 */
	public static int getIntBitNum(int n)
	{
		return 32-Integer.numberOfLeadingZeros(n);
	}
	
	/** 取2为底对数 */
	public static int log2(int i)
	{
		int r=0;
		while((i >>= 1)!=0)
		{
			++r;
		}
		return r;
	}
	
	/** 整数向上取整除法 */
	public static final int divCeil(int a,int b)
	{
		if(b==0)
			return 0;
		
		return (int)Math.ceil((double)a/b);
	}
	
	/** 最大值 */
	public static final int max(int... args)
	{
		if(args.length==0)
			return 0;
		
		int re=Integer.MIN_VALUE;
		
		for(int v:args)
		{
			if(v>re)
				re=v;
		}
		
		return re;
	}
	
	/** 最小值 */
	public static final int min(int... args)
	{
		if(args.length==0)
			return 0;
		
		int re=Integer.MAX_VALUE;
		
		for(int v:args)
		{
			if(v<re)
				re=v;
		}
		
		return re;
	}
	
	/** 随一0-1的double */
	public static double random()
	{
		return _random.nextDouble();
	}
	
	/** 随一0-1的float */
	public static float randomFloat()
	{
		return _random.nextFloat();
	}
	
	/** 随一-1 ~ 1的float */
	public static float randomFloatBetweenOne()
	{
		return _random.nextFloat()*2-1;
	}
	
	/** 随一个完全的整形 */
	public static int randomInt()
	{
		return _random.nextInt();
	}
	
	/** 随一整形(0<=value<range) */
	public static int randomInt(int range)
	{
		return _random.nextInt(range);
	}
	
	/** 随一整形(start<=value<end) */
	public static int randomRange(int start,int end)
	{
		if(end<=start)
			return -1;
		
		return start+randomInt(end-start);
	}
	
	/** 随一整形(start<=value<=end)(包括结尾) */
	public static int randomRange2(int start,int end)
	{
		if(start==end)
			return start;
		
		return randomRange(start,end+1);
	}
	
	/** 随机一个朝向 */
	public static float randomDirection()
	{
		return randomFloat()*fPI-fPI2;
	}
	
	/** 随机上下浮动 */
	public static int randomOffSet(int value,int offset)
	{
		return value-offset+randomInt(offset<<1);
	}
	
	/** 随机上下浮动(千分比) */
	public static int randomOffSetPercent(int value,int offset)
	{
		return (int)(value*(1f+(randomInt(offset<<1)-offset)/1000f));
	}
	
	/** ran一个bool值 */
	public static boolean randomBoolean()
	{
		return _random.nextBoolean();
	}
	
	/** 判定千分位几率 */
	public static boolean randomProb(int prob)
	{
		return randomProb(prob,1000);
	}
	
	/** 判定几率 */
	public static boolean randomProb(int prob,int max)
	{
		if(prob >= max)
		{
			return true;
		}
		
		if(prob<=0)
		{
			return false;
		}
		
		return randomFloat()<(float)prob / max;
	}
	
	/** 从一组数据中随机一个 */
	public static int randomFromIntArr(int[] arr)
	{
		if(arr.length==0)
			return -1;
		
		return arr[randomInt(arr.length)];
	}
	
	/** 非重置随机一组len个,(0<=x<total) */
	public static void randomSomeNotReset(IntList reList,int len,int total)
	{
		reList.clear();
		
		//数据不足
		if(total<=len)
		{
			for(int i=0;i<len;i++)
			{
				reList.add(i);
			}
		}
		else
		{
			//2倍量以上
			if(total>=len*2)
			{
				IntSet temp=new IntSet();
				
				while(reList.size()<len)
				{
					int v=MathUtils.randomInt(total);
					
					if(temp.add(v))
					{
						reList.add(v);
					}
				}
			}
			else
			{
				IntList temp=new IntList();
				
				for(int i=0;i<total;i++)
				{
					temp.add(i);
				}
				
				for(int i=0;i<len;i++)
				{
					reList.add(temp.remove(MathUtils.randomInt(temp.size())));
				}
			}
		}
	}

	/** 非重置随机一组 随机value*/
	public static IntList randomSomeValueNotReset(IntList total, int n)
	{
		if(total.length()<n)
		{
			return total;
		}

		IntList result = new IntList();

		IntList reList = new IntList();
		randomSomeNotReset(reList,n,total.size());
		reList.forEach(index->result.add(total.get(index)));

		return result;
	}
	
	/** 获取一个令牌 */
	public static int getToken()
	{
		return randomRange(1,100000000);//1亿
	}
	
	/** 十进制表示的二进制数转义 */
	public static int numberTenToTwo(int value)
	{
		int temp=1;
		int re=0;
		
		int tt;
		
		while(value>0)
		{
			tt=value % 10;
			
			if(tt!=0)
			{
				re|=temp;
			}
			
			value/=10;
			temp<<=1;
		}
		
		return re;
	}
	
	/** 权重随机 */
	public static int randomWeight(DIntData[] randoms,int weight)
	{
		int rand=randomInt(weight);
		int k;
		for(int i=0,len=randoms.length;i<len;++i)
		{
			if(rand<(k=randoms[i].key))
			{
				return randoms[i].value;
			}
			else
			{
				rand-=k;
			}
		}
		
		Ctrl.errorLog("权重随机失败!!");
		
		return -1;
	}
	
	/** 上下限获取 */
	public static int clamp(int value,int min,int max)
	{
		if (value < min)
			return min;
		if (value > max)
			return max;
		return value;
	}
	
	/** 上下限获取 */
	public static float clamp(float value,float min,float max)
	{
		if (value < min)
			return min;
		if (value > max)
			return max;
		return value;
	}
	
	/** 上下限获取 */
	public static double clamp(double value,double min,double max)
	{
		if (value < min)
			return min;
		if (value > max)
			return max;
		return value;
	}
	
	/** 弧度归位(-π 到 π) */
	public static float directionCut(float direction)
	{
		while(direction>MathUtils.fPI)
			direction-=MathUtils.fPI2;
		
		while(direction<-MathUtils.fPI)
			direction+=MathUtils.fPI2;
		
		return direction;
	}
	
	/** 角度归位(-180 到 180) */
	public static float angleCut(float angle)
	{
		while(angle>180)
			angle-=360;
		
		while(angle<-180)
			angle+=360;
		
		return angle;
	}
	
	/** 反向 */
	public static float directionInverse(float direction)
	{
		return directionCut(direction+MathUtils.fPI);
	}
	
	/** 弧度转角度(带cut) */
	public static float directionToAngle(float direction)
	{
		return angleCut(direction/MathUtils.fPI*180f);
	}
	
	/** 角度转弧度(带cut) */
	public static float angleToDirection(float angle)
	{
		return directionCut(angle/180f*MathUtils.fPI);
	}
	
	/** 符号相同 */
	public static boolean sameSymbol(float a,float b)
	{
		return (a<0 && b<0) || (a>=0 && b>=0);
	}

	/**
	 * 转换方法
	 * @param num       元数据字符串
	 * @param fromRadix 元数据的进制类型
	 * @param toRadix   目标进制类型
	 * @return
	 */
	public static String binaryTransRadix(String num, int fromRadix, int toRadix)
	{
		int number = Integer.valueOf(num, fromRadix);
		StringBuilder sb = new StringBuilder();
		while (number != 0) {
			sb.append(chs[number%toRadix]);
			number = number / toRadix;
		}
		return sb.reverse().toString();
	}
}
