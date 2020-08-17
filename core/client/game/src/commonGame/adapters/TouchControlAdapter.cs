using System;
using System.Text;
using ILRuntime.CLR.Method;
using ILRuntime.Runtime.Enviorment;
using ILRuntime.Runtime.Intepreter;
using UnityEngine;
using AppDomain = ILRuntime.Runtime.Enviorment.AppDomain;
using ShineEngine;

	public class TouchControlAdapter : CrossBindingAdaptor
	{
		public override Type BaseCLRType
		{
			get
			{
				return typeof(TouchControl);
			}
		}

		public override Type AdaptorType
		{
			get
			{
				return typeof(Adaptor);
			}
		}

		public override object CreateCLRInstance(AppDomain appdomain, ILTypeInstance instance)
		{
			return new Adaptor(appdomain, instance);
		}

		public class Adaptor : TouchControl, CrossBindingAdaptorType
		{
			private ILTypeInstance instance;
			private AppDomain appdomain;

			public Adaptor()
			{

			}

			public Adaptor(AppDomain appdomain, ILTypeInstance instance)
			{
				this.appdomain = appdomain;
				this.instance = instance;
			}

			public ILTypeInstance ILInstance { get { return instance; } set { instance = value; } }

			private object[] _p1=new object[1];
			
			private object[] _p2=new object[2];
			
			

			IMethod _m0;
			bool _g0;
			bool _b0;
			protected override void onMouse(bool isDown)
			{
				if(!_g0)
				{
					_m0=instance.Type.GetMethod("onMouse",1);
					_g0=true;
				}
				
				if(_m0!=null && !_b0)
				{
					_b0=true;
					_p1[0]=isDown;
					appdomain.Invoke(_m0,instance,_p1);
					_p1[0]=null;
					_b0=false;
					
				}
				else
				{
					base.onMouse(isDown);
				}
			}
			
			IMethod _m1;
			bool _g1;
			bool _b1;
			protected override void onMouseWheel(float wheel)
			{
				if(!_g1)
				{
					_m1=instance.Type.GetMethod("onMouseWheel",1);
					_g1=true;
				}
				
				if(_m1!=null && !_b1)
				{
					_b1=true;
					_p1[0]=wheel;
					appdomain.Invoke(_m1,instance,_p1);
					_p1[0]=null;
					_b1=false;
					
				}
				else
				{
					base.onMouseWheel(wheel);
				}
			}
			
			IMethod _m2;
			bool _g2;
			bool _b2;
			protected override void onTouch(Touch touch,bool isDown)
			{
				if(!_g2)
				{
					_m2=instance.Type.GetMethod("onTouch",2);
					_g2=true;
				}
				
				if(_m2!=null && !_b2)
				{
					_b2=true;
					_p2[0]=touch;
					_p2[1]=isDown;
					appdomain.Invoke(_m2,instance,_p2);
					_p2[0]=null;
					_p2[1]=null;
					_b2=false;
					
				}
				else
				{
					base.onTouch(touch,isDown);
				}
			}
			
			IMethod _m3;
			bool _g3;
			bool _b3;
			protected override void onTouchOne(bool isDown)
			{
				if(!_g3)
				{
					_m3=instance.Type.GetMethod("onTouchOne",1);
					_g3=true;
				}
				
				if(_m3!=null && !_b3)
				{
					_b3=true;
					_p1[0]=isDown;
					appdomain.Invoke(_m3,instance,_p1);
					_p1[0]=null;
					_b3=false;
					
				}
				else
				{
					base.onTouchOne(isDown);
				}
			}
			
			
			public override string ToString()
			{
				IMethod m = appdomain.ObjectType.GetMethod("ToString", 0);
				m = instance.Type.GetVirtualMethod(m);
				if (m == null || m is ILMethod)
				{
					return instance.ToString();
				}
				else
					return instance.Type.FullName;
			}
		}
	}
