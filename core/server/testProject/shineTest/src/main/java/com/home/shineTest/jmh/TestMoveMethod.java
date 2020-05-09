package com.home.shineTest.jmh;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import com.home.shine.ctrl.Ctrl;

/** 测试移动方式 */
@State(Scope.Thread)
public class TestMoveMethod
{
	private static int[] _moveTargets=new int[]{100,100,3000,4000,100,4000,3000,100};
	
	private static int _moveTargetIndex=0;
	
	private int _delay=16;
	private int _signDelay=16;
	private int _lastDelay=0;
	
	private int _times=0;
	
	private int _moveSpeed=450;
	private float _moveSpeedMF;
	private double _moveSpeedMD;
	
	private int _x=0;
	private int _y=0;
	
	private int _moveTargetX=-1;
	private int _moveTargetY=-1;
	
	private int _speedX;
	private int _speedY;
	
	private int _saveMoveDis=0;
	
	//method2
	
	private float _speedX2;
	private float _speedY2;
	
	private float _x2=0;
	private float _y2=0;
	
	private float _moveTargetX2=-1f;
	private float _moveTargetY2=-1f;
	
	private float _saveMoveDis2=0f;
	
	//method3
	
	private double _speedX3;
	private double _speedY3;
	
	private double _x3=0.0;
	private double _y3=0.0;
	
	private double _moveTargetX3=-1.0;
	private double _moveTargetY3=-1.0;
	
	private double _saveMoveDis3=0.0;
	
	//temp
	
	private int _index=0;
	
	public TestMoveMethod()
	{
		_moveSpeedMF=_moveSpeed/1000f;
		_moveSpeedMD=_moveSpeed/1000.0;
	}
	
	/** 选下个移动目标 */
	private void selectNextTarget()
	{
		_moveTargetX=_moveTargets[_moveTargetIndex++];
		_moveTargetY=_moveTargets[_moveTargetIndex++];
		
		if(_moveTargetIndex>=_moveTargets.length)
			_moveTargetIndex=0;
		
		Ctrl.print("换点",_moveTargetX,_moveTargetY,_index);
	}
	
	private void countSpeedXY()
	{
		_times=0;
		
		int dx=_moveTargetX-_x;
		int dy=_moveTargetY-_y;
		
//		float mDisSq=(float)dx*dx+(float)dy*dy;
		int mDisSq=dx*dx+dy*dy;
		float mDis=(float)Math.sqrt(mDisSq);
		
		float f=_delay*_moveSpeedMF/mDis;

		_speedX=(int)(f*dx);
		_speedY=(int)(f*dy);
		
//		Ctrl.print("看速度",_speedX,_speedY);
	}
	
//	@Benchmark
	public void test1()
	{
		++_index;
		
		if(_moveTargetX==-1)
		{
			selectNextTarget();
			countSpeedXY();
		}
		else
		{
//			if(++_times>10)
//			{
//				countSpeedXY();
//			}
			
			int delay=_delay;
			
			int x=_x;
			int y=_y;
			int mx=_moveTargetX;
			int my=_moveTargetY;
			
			int dx=mx-x;
			int dy=my-y;
			
//			float mDisSq=(float)dx*dx+(float)dy*dy;
//			float mDisSq=dx*dx+dy*dy;
			int mDisSq=dx*dx+dy*dy;
			
			int dis=(int)(delay*_moveSpeedMF);
			
			if(++_times>10)
			{
				int mDis=(int)Math.sqrt(mDisSq);
				
				float f=_delay*_moveSpeedMF/mDis;

				_speedX=(int)(f*dx);
				_speedY=(int)(f*dy);
			}
			
			dis+=_saveMoveDis;
			
			int disSq=dis*dis;
			
			//到达
			if(disSq>=mDisSq)
			{
				int mDis=(int)Math.sqrt(mDisSq);
				
				_saveMoveDis=dis-mDis;
				
				_x=mx;
				_y=my;
				
				selectNextTarget();
				countSpeedXY();
			}
			else
			{
				_saveMoveDis=0;
				
//				float f=dis/mDis;
//				_x=(int)(x+f*dx);
//				_y=(int)(y+f*dy);
				
				_x=x+_speedX;
				_y=y+_speedY;
			}
		}
		
//		Ctrl.print("看",_x,_y,_index);
	}
	
//	@Benchmark
	public void test2()
	{
		++_index;
		
		if(_moveTargetX2==-1f)
		{
			selectNextTarget2();
			countSpeedXY2();
		}
		else
		{
//			if(++_times>30)
//			{
//				countSpeedXY2();
//			}
			
			float x=_x2;
			float y=_y2;
			float mx=_moveTargetX2;
			float my=_moveTargetY2;
			
			int delay=_delay;
			
			float dx=mx-x;
			float dy=my-y;
			
			float mDisSq=dx*dx+dy*dy;
			
			float dis=delay*_moveSpeedMF;
			
			if(++_times>10)
			{
				_times=0;
				
				float mDis=(float)Math.sqrt(mDisSq);
				float f=_signDelay*_moveSpeedMF/mDis;

				_speedX2=f*dx;
				_speedY2=f*dy;
			}
			
			
			dis+=_saveMoveDis2;
			
			float disSq=dis*dis;
			
			//到达
			if(disSq>=mDisSq)
			{
				float mDis=(float)Math.sqrt(mDisSq);
				
				_saveMoveDis2=dis-mDis;
				
				_x2=mx;
				_y2=my;
				
				selectNextTarget2();
				countSpeedXY2();
			}
			else
			{
				_saveMoveDis2=0f;
				
//				_x2=x+_speedX2*delay;
//				_y2=y+_speedY2*delay;
				
				_x2=x+_speedX2;
				_y2=y+_speedY2;
			}
		}
		
	}
	
	/** 选下个移动目标 */
	private void selectNextTarget2()
	{
		_moveTargetX2=_moveTargets[_moveTargetIndex++];
		_moveTargetY2=_moveTargets[_moveTargetIndex++];
		
		if(_moveTargetIndex>=_moveTargets.length)
			_moveTargetIndex=0;
		
//		Ctrl.print("换点2",_moveTargetX2,_moveTargetY2,_index);
	}
	
	private void countSpeedXY2()
	{
		_times=0;
		
		float x=_x2;
		float y=_y2;
		float mx=_moveTargetX2;
		float my=_moveTargetY2;
		
		float dx=mx-x;
		float dy=my-y;
		
		float mDisSq=dx*dx+dy*dy;
		float mDis=(float)Math.sqrt(mDisSq);
		
//		float f=_moveSpeedMF/mDis;
		float f=_signDelay*_moveSpeedMF/mDis;

		_speedX2=f*dx;
		_speedY2=f*dy;
	}
	
//	@Benchmark
	public void test3()
	{
		++_index;
		
		if(_moveTargetX3==-1.0)
		{
			selectNextTarget3();
			countSpeedXY3();
		}
		else
		{
			double x=_x3;
			double y=_y3;
			double mx=_moveTargetX3;
			double my=_moveTargetY3;
			
			int delay=_delay;
			
			double dx=mx-x;
			double dy=my-y;
			
			double mDisSq=dx*dx+dy*dy;
			
			double dis=(delay*_moveSpeedMD);
			
			dis+=_saveMoveDis3;
			
			double disSq=dis*dis;
			
			//到达
			if(disSq>=mDisSq)
			{
				double mDis=Math.sqrt(mDisSq);
				
				_saveMoveDis3=dis-mDis;
				
				_x3=mx;
				_y3=my;
				
				selectNextTarget3();
				countSpeedXY3();
			}
			else
			{
				_saveMoveDis3=0;
				
				_x3=x+_speedX3*delay;
				_y3=y+_speedY3*delay;
			}
		}
		
	}
	
	/** 选下个移动目标 */
	private void selectNextTarget3()
	{
		_moveTargetX3=_moveTargets[_moveTargetIndex++];
		_moveTargetY3=_moveTargets[_moveTargetIndex++];
		
		if(_moveTargetIndex>=_moveTargets.length)
			_moveTargetIndex=0;
		
//		Ctrl.print("换点3",_moveTargetX3,_moveTargetY3,_index);
	}
	
	private void countSpeedXY3()
	{
		double x=_x3;
		double y=_y3;
		double mx=_moveTargetX3;
		double my=_moveTargetY3;
		
		double dx=mx-x;
		double dy=my-y;
		
		double mDisSq=dx*dx+dy*dy;
		double mDis=(float)Math.sqrt(mDisSq);
		
		double f=_moveSpeedMD/mDis;

		_speedX3=f*dx;
		_speedY3=f*dy;
	}
	
	
//	@Benchmark
	public void test4()
	{
		++_index;
		
		if(_moveTargetX2==-1f)
		{
			selectNextTarget2();
		}
		else
		{
			float x=_x2;
			float y=_y2;
			float mx=_moveTargetX2;
			float my=_moveTargetY2;
			
			int delay=_delay;
			
			float dx=mx-x;
			float dy=my-y;
			
			float mDisSq=dx*dx+dy*dy;
			
			float dis=delay*_moveSpeedMF;
			
			dis+=_saveMoveDis2;
			
			float mDis=(float)Math.sqrt(mDisSq);
			
			//到达
			if(dis>=mDis)
			{
				_saveMoveDis2=dis-mDis;
				
				_x2=mx;
				_y2=my;
				
				selectNextTarget2();
			}
			else
			{
				_saveMoveDis2=0f;
				
				float f=dis/mDis;
				_x2=x+f*dx;
				_y2=y+f*dy;
				
//				_x=(int)(x+f*dx);
//				_y=(int)(y+f*dy);
				
//				_x2=x+_speedX2*delay;
//				_y2=y+_speedY2*delay;
				
//				_x2=x+_speedX2;
//				_y2=y+_speedY2;
			}
		}
	}
	
//	@Benchmark
	public void test5()
	{
		int delay=_delay;
		
		_lastDelay+=delay;
		
		if(_lastDelay<_signDelay)
			return;
		
		_lastDelay-=_signDelay;
		
		int tt=1;
		
		if(_lastDelay>_signDelay)
		{
			int k=_lastDelay/_signDelay;
			_lastDelay-=_signDelay*k;
			tt+=k;
		}
		
		++_index;
		
		if(_moveTargetX2==-1f)
		{
			selectNextTarget2();
			countSpeedXY5();
		}
		else
		{
//			if(++_times>30)
//			{
//				countSpeedXY2();
//			}
			
			float x=_x2;
			float y=_y2;
			float mx=_moveTargetX2;
			float my=_moveTargetY2;
			
			
			
			float dx=mx-x;
			float dy=my-y;
			
			float mDisSq=dx*dx+dy*dy;
			
			float dis=tt*delay*_moveSpeedMF;
			
			if(++_times>10)
			{
				_times=0;
				
				float mDis=(float)Math.sqrt(mDisSq);
				float f=_signDelay*_moveSpeedMF/mDis;

				_speedX2=f*dx;
				_speedY2=f*dy;
			}
			
			
			dis+=_saveMoveDis2;
			
			float disSq=dis*dis;
			
			//到达
			if(disSq>=mDisSq)
			{
				float mDis=(float)Math.sqrt(mDisSq);
				
				_saveMoveDis2=dis-mDis;
				
				_x2=mx;
				_y2=my;
				
				selectNextTarget2();
				countSpeedXY5();
			}
			else
			{
				_saveMoveDis2=0f;
				
				_x2=x+_speedX2*tt;
				_y2=y+_speedY2*tt;
			}
		}
	}
	
	private void countSpeedXY5()
	{
		_times=0;
		
		float x=_x2;
		float y=_y2;
		float mx=_moveTargetX2;
		float my=_moveTargetY2;
		
		float dx=mx-x;
		float dy=my-y;
		
		float mDisSq=dx*dx+dy*dy;
		float mDis=(float)Math.sqrt(mDisSq);
		
		float f=_signDelay*_moveSpeedMF/mDis;

		_speedX2=f*dx;
		_speedY2=f*dy;
	}
	
	@Benchmark
	public void test6()
	{
		++_index;
		
		if(_moveTargetX2==-1f)
		{
			selectNextTarget2();
			countSpeedXY6();
		}
		else
		{
//			if(++_times>30)
//			{
//				countSpeedXY2();
//			}
			
			float x=_x2;
			float y=_y2;
			float mx=_moveTargetX2;
			float my=_moveTargetY2;
			
			
			
			float dx=mx-x;
			float dy=my-y;
			
			float mDisSq=dx*dx+dy*dy;
			
			int delay=_delay;
			
			float dis=delay*_moveSpeedMF;
			
			if(++_times>10)
			{
				_times=0;
				
				float mDis=(float)Math.sqrt(mDisSq);
				float f=_moveSpeedMF/mDis;

				_speedX2=f*dx;
				_speedY2=f*dy;
			}
			
			
			dis+=_saveMoveDis2;
			
			float disSq=dis*dis;
			
			//到达
			if(disSq>=mDisSq)
			{
				float mDis=(float)Math.sqrt(mDisSq);
				
				_saveMoveDis2=dis-mDis;
				
				_x2=mx;
				_y2=my;
				
				selectNextTarget2();
				countSpeedXY6();
			}
			else
			{
				_saveMoveDis2=0f;
				
				_x2=x+_speedX2*delay;
				_y2=y+_speedY2*delay;
			}
		}
	}
	
	private void countSpeedXY6()
	{
		_times=0;
		
		float x=_x2;
		float y=_y2;
		float mx=_moveTargetX2;
		float my=_moveTargetY2;
		
		float dx=mx-x;
		float dy=my-y;
		
		float mDisSq=dx*dx+dy*dy;
		float mDis=(float)Math.sqrt(mDisSq);
		
		float f=_moveSpeedMF/mDis;

		_speedX2=f*dx;
		_speedY2=f*dy;
	}
}
