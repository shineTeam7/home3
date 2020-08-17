using System;
using System.Text;
using ILRuntime.CLR.Method;
using ILRuntime.Runtime.Enviorment;
using ILRuntime.Runtime.Intepreter;
using UnityEngine;
using AppDomain = ILRuntime.Runtime.Enviorment.AppDomain;
using ShineEngine;

	public class SelectServerUIAdapter : CrossBindingAdaptor
	{
		public override Type BaseCLRType
		{
			get
			{
				return typeof(SelectServerUI);
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

		public class Adaptor : SelectServerUI, CrossBindingAdaptorType
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

			

			IMethod _m0;
			bool _g0;
			bool _b0;
			protected override GameObject getGameObject()
			{
				if(!_g0)
				{
					_m0=instance.Type.GetMethod("getGameObject",0);
					_g0=true;
				}
				
				if(_m0!=null && !_b0)
				{
					_b0=true;
					GameObject re=(GameObject)appdomain.Invoke(_m0,instance,null);
					_b0=false;
					return re;
					
				}
				else
				{
					return base.getGameObject();
				}
			}
			
			IMethod _m1;
			bool _g1;
			bool _b1;
			protected override void onInit()
			{
				if(!_g1)
				{
					_m1=instance.Type.GetMethod("onInit",0);
					_g1=true;
				}
				
				if(_m1!=null && !_b1)
				{
					_b1=true;
					appdomain.Invoke(_m1,instance,null);
					_b1=false;
					
				}
				else
				{
					base.onInit();
				}
			}
			
			IMethod _m2;
			bool _g2;
			bool _b2;
			protected override void onShow()
			{
				if(!_g2)
				{
					_m2=instance.Type.GetMethod("onShow",0);
					_g2=true;
				}
				
				if(_m2!=null && !_b2)
				{
					_b2=true;
					appdomain.Invoke(_m2,instance,null);
					_b2=false;
					
				}
				else
				{
					base.onShow();
				}
			}
			
			IMethod _m3;
			bool _g3;
			bool _b3;
			protected override void onHide()
			{
				if(!_g3)
				{
					_m3=instance.Type.GetMethod("onHide",0);
					_g3=true;
				}
				
				if(_m3!=null && !_b3)
				{
					_b3=true;
					appdomain.Invoke(_m3,instance,null);
					_b3=false;
					
				}
				else
				{
					base.onHide();
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
