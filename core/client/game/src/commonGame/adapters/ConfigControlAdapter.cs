using System;
using System.Text;
using ILRuntime.CLR.Method;
using ILRuntime.Runtime.Enviorment;
using ILRuntime.Runtime.Intepreter;
using UnityEngine;
using AppDomain = ILRuntime.Runtime.Enviorment.AppDomain;
using ShineEngine;

	public class ConfigControlAdapter : CrossBindingAdaptor
	{
		public override Type BaseCLRType
		{
			get
			{
				return typeof(ConfigControl);
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

		public class Adaptor : ConfigControl, CrossBindingAdaptorType
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
			public override void preInit()
			{
				if(!_g0)
				{
					_m0=instance.Type.GetMethod("preInit",0);
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
					base.preInit();
				}
			}
			
			IMethod _m1;
			bool _g1;
			bool _b1;
			public override int getMsgDataVersion()
			{
				if(!_g1)
				{
					_m1=instance.Type.GetMethod("getMsgDataVersion",0);
					_g1=true;
				}
				
				if(_m1!=null && !_b1)
				{
					_b1=true;
					int re=(int)appdomain.Invoke(_m1,instance,null);
					_b1=false;
					return re;
					
				}
				else
				{
					return base.getMsgDataVersion();
				}
			}
			
			IMethod _m2;
			bool _g2;
			bool _b2;
			public override int getDBDataVersion()
			{
				if(!_g2)
				{
					_m2=instance.Type.GetMethod("getDBDataVersion",0);
					_g2=true;
				}
				
				if(_m2!=null && !_b2)
				{
					_b2=true;
					int re=(int)appdomain.Invoke(_m2,instance,null);
					_b2=false;
					return re;
					
				}
				else
				{
					return base.getDBDataVersion();
				}
			}
			
			IMethod _m3;
			bool _g3;
			bool _b3;
			protected override int getGameConfigVersion()
			{
				if(!_g3)
				{
					_m3=instance.Type.GetMethod("getGameConfigVersion",0);
					_g3=true;
				}
				
				if(_m3!=null && !_b3)
				{
					_b3=true;
					int re=(int)appdomain.Invoke(_m3,instance,null);
					_b3=false;
					return re;
					
				}
				else
				{
					return base.getGameConfigVersion();
				}
			}
			
			IMethod _m4;
			bool _g4;
			bool _b4;
			protected override int getHotfixConfigVersion()
			{
				if(!_g4)
				{
					_m4=instance.Type.GetMethod("getHotfixConfigVersion",0);
					_g4=true;
				}
				
				if(_m4!=null && !_b4)
				{
					_b4=true;
					int re=(int)appdomain.Invoke(_m4,instance,null);
					_b4=false;
					return re;
					
				}
				else
				{
					return base.getHotfixConfigVersion();
				}
			}
			
			IMethod _m5;
			bool _g5;
			bool _b5;
			public override void refreshConfig()
			{
				if(!_g5)
				{
					_m5=instance.Type.GetMethod("refreshConfig",0);
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
					base.refreshConfig();
				}
			}
			
			IMethod _m6;
			bool _g6;
			bool _b6;
			public override void refreshConfigForLanguage()
			{
				if(!_g6)
				{
					_m6=instance.Type.GetMethod("refreshConfigForLanguage",0);
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
					base.refreshConfigForLanguage();
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
