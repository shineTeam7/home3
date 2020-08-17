using System;
using System.Text;
using ILRuntime.CLR.Method;
using ILRuntime.Runtime.Enviorment;
using ILRuntime.Runtime.Intepreter;
using UnityEngine;
using AppDomain = ILRuntime.Runtime.Enviorment.AppDomain;
using ShineEngine;

	public class JoystickLogicAdapter : CrossBindingAdaptor
	{
		public override Type BaseCLRType
		{
			get
			{
				return typeof(JoystickLogic);
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

		public class Adaptor : JoystickLogic, CrossBindingAdaptorType
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
			protected override void registLogics()
			{
				if(!_g0)
				{
					_m0=instance.Type.GetMethod("registLogics",0);
					_g0=true;
				}
				
				if(_m0!=null && !_b0)
				{
					_b0=true;
					appdomain.Invoke(_m0,instance,null);
					_b0=false;
					
				}
				else
				{
					base.registLogics();
				}
			}
			
			IMethod _m1;
			bool _g1;
			bool _b1;
			public override void construct()
			{
				if(!_g1)
				{
					_m1=instance.Type.GetMethod("construct",0);
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
					base.construct();
				}
			}
			
			IMethod _m2;
			bool _g2;
			bool _b2;
			protected override void preInit()
			{
				if(!_g2)
				{
					_m2=instance.Type.GetMethod("preInit",0);
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
					base.preInit();
				}
			}
			
			IMethod _m3;
			bool _g3;
			bool _b3;
			protected override void init()
			{
				if(!_g3)
				{
					_m3=instance.Type.GetMethod("init",0);
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
					base.init();
				}
			}
			
			IMethod _m4;
			bool _g4;
			bool _b4;
			protected override void dispose()
			{
				if(!_g4)
				{
					_m4=instance.Type.GetMethod("dispose",0);
					_g4=true;
				}
				
				if(_m4!=null && !_b4)
				{
					_b4=true;
					appdomain.Invoke(_m4,instance,null);
					_b4=false;
					
				}
				else
				{
					base.dispose();
				}
			}
			
			IMethod _m5;
			bool _g5;
			bool _b5;
			protected override void onEnter()
			{
				if(!_g5)
				{
					_m5=instance.Type.GetMethod("onEnter",0);
					_g5=true;
				}
				
				if(_m5!=null && !_b5)
				{
					_b5=true;
					appdomain.Invoke(_m5,instance,null);
					_b5=false;
					
				}
				else
				{
					base.onEnter();
				}
			}
			
			IMethod _m6;
			bool _g6;
			bool _b6;
			protected override void onExit()
			{
				if(!_g6)
				{
					_m6=instance.Type.GetMethod("onExit",0);
					_g6=true;
				}
				
				if(_m6!=null && !_b6)
				{
					_b6=true;
					appdomain.Invoke(_m6,instance,null);
					_b6=false;
					
				}
				else
				{
					base.onExit();
				}
			}
			
			IMethod _m7;
			bool _g7;
			bool _b7;
			protected override void onResize()
			{
				if(!_g7)
				{
					_m7=instance.Type.GetMethod("onResize",0);
					_g7=true;
				}
				
				if(_m7!=null && !_b7)
				{
					_b7=true;
					appdomain.Invoke(_m7,instance,null);
					_b7=false;
					
				}
				else
				{
					base.onResize();
				}
			}
			
			IMethod _m8;
			bool _g8;
			bool _b8;
			protected override void disposeOver()
			{
				if(!_g8)
				{
					_m8=instance.Type.GetMethod("disposeOver",0);
					_g8=true;
				}
				
				if(_m8!=null && !_b8)
				{
					_b8=true;
					appdomain.Invoke(_m8,instance,null);
					_b8=false;
					
				}
				else
				{
					base.disposeOver();
				}
			}
			
			IMethod _m9;
			bool _g9;
			bool _b9;
			protected override void beforeExecuteShow()
			{
				if(!_g9)
				{
					_m9=instance.Type.GetMethod("beforeExecuteShow",0);
					_g9=true;
				}
				
				if(_m9!=null && !_b9)
				{
					_b9=true;
					appdomain.Invoke(_m9,instance,null);
					_b9=false;
					
				}
				else
				{
					base.beforeExecuteShow();
				}
			}
			
			IMethod _m10;
			bool _g10;
			bool _b10;
			protected override void initModelObject()
			{
				if(!_g10)
				{
					_m10=instance.Type.GetMethod("initModelObject",0);
					_g10=true;
				}
				
				if(_m10!=null && !_b10)
				{
					_b10=true;
					appdomain.Invoke(_m10,instance,null);
					_b10=false;
					
				}
				else
				{
					base.initModelObject();
				}
			}
			
			IMethod _m11;
			bool _g11;
			bool _b11;
			protected override UIModel createModel()
			{
				if(!_g11)
				{
					_m11=instance.Type.GetMethod("createModel",0);
					_g11=true;
				}
				
				if(_m11!=null && !_b11)
				{
					_b11=true;
					UIModel re=(UIModel)appdomain.Invoke(_m11,instance,null);
					_b11=false;
					return re;
					
				}
				else
				{
					return base.createModel();
				}
			}
			
			IMethod _m12;
			bool _g12;
			bool _b12;
			protected override void showCancel()
			{
				if(!_g12)
				{
					_m12=instance.Type.GetMethod("showCancel",0);
					_g12=true;
				}
				
				if(_m12!=null && !_b12)
				{
					_b12=true;
					appdomain.Invoke(_m12,instance,null);
					_b12=false;
					
				}
				else
				{
					base.showCancel();
				}
			}
			
			IMethod _m13;
			bool _g13;
			bool _b13;
			protected override void showOverEx()
			{
				if(!_g13)
				{
					_m13=instance.Type.GetMethod("showOverEx",0);
					_g13=true;
				}
				
				if(_m13!=null && !_b13)
				{
					_b13=true;
					appdomain.Invoke(_m13,instance,null);
					_b13=false;
					
				}
				else
				{
					base.showOverEx();
				}
			}
			
			IMethod _m14;
			bool _g14;
			bool _b14;
			protected override void hideCancel()
			{
				if(!_g14)
				{
					_m14=instance.Type.GetMethod("hideCancel",0);
					_g14=true;
				}
				
				if(_m14!=null && !_b14)
				{
					_b14=true;
					appdomain.Invoke(_m14,instance,null);
					_b14=false;
					
				}
				else
				{
					base.hideCancel();
				}
			}
			
			IMethod _m15;
			bool _g15;
			bool _b15;
			protected override void hideOver()
			{
				if(!_g15)
				{
					_m15=instance.Type.GetMethod("hideOver",0);
					_g15=true;
				}
				
				if(_m15!=null && !_b15)
				{
					_b15=true;
					appdomain.Invoke(_m15,instance,null);
					_b15=false;
					
				}
				else
				{
					base.hideOver();
				}
			}
			
			IMethod _m16;
			bool _g16;
			bool _b16;
			protected override void hideOverEx()
			{
				if(!_g16)
				{
					_m16=instance.Type.GetMethod("hideOverEx",0);
					_g16=true;
				}
				
				if(_m16!=null && !_b16)
				{
					_b16=true;
					appdomain.Invoke(_m16,instance,null);
					_b16=false;
					
				}
				else
				{
					base.hideOverEx();
				}
			}
			
			IMethod _m17;
			bool _g17;
			bool _b17;
			protected override void addShow()
			{
				if(!_g17)
				{
					_m17=instance.Type.GetMethod("addShow",0);
					_g17=true;
				}
				
				if(_m17!=null && !_b17)
				{
					_b17=true;
					appdomain.Invoke(_m17,instance,null);
					_b17=false;
					
				}
				else
				{
					base.addShow();
				}
			}
			
			IMethod _m18;
			bool _g18;
			bool _b18;
			protected override void removeShow()
			{
				if(!_g18)
				{
					_m18=instance.Type.GetMethod("removeShow",0);
					_g18=true;
				}
				
				if(_m18!=null && !_b18)
				{
					_b18=true;
					appdomain.Invoke(_m18,instance,null);
					_b18=false;
					
				}
				else
				{
					base.removeShow();
				}
			}
			
			IMethod _m19;
			bool _g19;
			bool _b19;
			protected override void preShow()
			{
				if(!_g19)
				{
					_m19=instance.Type.GetMethod("preShow",0);
					_g19=true;
				}
				
				if(_m19!=null && !_b19)
				{
					_b19=true;
					appdomain.Invoke(_m19,instance,null);
					_b19=false;
					
				}
				else
				{
					base.preShow();
				}
			}
			
			IMethod _m20;
			bool _g20;
			bool _b20;
			protected override void preHide()
			{
				if(!_g20)
				{
					_m20=instance.Type.GetMethod("preHide",0);
					_g20=true;
				}
				
				if(_m20!=null && !_b20)
				{
					_b20=true;
					appdomain.Invoke(_m20,instance,null);
					_b20=false;
					
				}
				else
				{
					base.preHide();
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
