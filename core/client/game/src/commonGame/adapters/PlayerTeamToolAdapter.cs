using System;
using System.Text;
using ILRuntime.CLR.Method;
using ILRuntime.Runtime.Enviorment;
using ILRuntime.Runtime.Intepreter;
using UnityEngine;
using AppDomain = ILRuntime.Runtime.Enviorment.AppDomain;
using ShineEngine;

	public class PlayerTeamToolAdapter : CrossBindingAdaptor
	{
		public override Type BaseCLRType
		{
			get
			{
				return typeof(PlayerTeamTool);
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

		public class Adaptor : PlayerTeamTool, CrossBindingAdaptorType
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
			
			

			IMethod _m0;
			bool _g0;
			bool _b0;
			public override void construct()
			{
				if(!_g0)
				{
					_m0=instance.Type.GetMethod("construct",0);
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
					base.construct();
				}
			}
			
			IMethod _m1;
			bool _g1;
			bool _b1;
			public override void init()
			{
				if(!_g1)
				{
					_m1=instance.Type.GetMethod("init",0);
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
					base.init();
				}
			}
			
			IMethod _m2;
			bool _g2;
			bool _b2;
			public override void dispose()
			{
				if(!_g2)
				{
					_m2=instance.Type.GetMethod("dispose",0);
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
					base.dispose();
				}
			}
			
			IMethod _m3;
			bool _g3;
			bool _b3;
			public override void onSecond(int delay)
			{
				if(!_g3)
				{
					_m3=instance.Type.GetMethod("onSecond",1);
					_g3=true;
				}
				
				if(_m3!=null && !_b3)
				{
					_b3=true;
					_p1[0]=delay;
					appdomain.Invoke(_m3,instance,_p1);
					_p1[0]=null;
					_b3=false;
					
				}
				else
				{
					base.onSecond(delay);
				}
			}
			
			IMethod _m4;
			bool _g4;
			bool _b4;
			public override void onNewCreate()
			{
				if(!_g4)
				{
					_m4=instance.Type.GetMethod("onNewCreate",0);
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
					base.onNewCreate();
				}
			}
			
			IMethod _m5;
			bool _g5;
			bool _b5;
			public override void onReloadConfig()
			{
				if(!_g5)
				{
					_m5=instance.Type.GetMethod("onReloadConfig",0);
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
					base.onReloadConfig();
				}
			}
			
			IMethod _m6;
			bool _g6;
			bool _b6;
			protected override FuncToolData createToolData()
			{
				if(!_g6)
				{
					_m6=instance.Type.GetMethod("createToolData",0);
					_g6=true;
				}
				
				if(_m6!=null && !_b6)
				{
					_b6=true;
					FuncToolData re=(FuncToolData)appdomain.Invoke(_m6,instance,null);
					_b6=false;
					return re;
					
				}
				else
				{
					return base.createToolData();
				}
			}
			
			IMethod _m7;
			bool _g7;
			bool _b7;
			protected override void toSetData(FuncToolData data)
			{
				if(!_g7)
				{
					_m7=instance.Type.GetMethod("toSetData",1);
					_g7=true;
				}
				
				if(_m7!=null && !_b7)
				{
					_b7=true;
					_p1[0]=data;
					appdomain.Invoke(_m7,instance,_p1);
					_p1[0]=null;
					_b7=false;
					
				}
				else
				{
					base.toSetData(data);
				}
			}
			
			IMethod _m8;
			bool _g8;
			bool _b8;
			public override void afterReadData()
			{
				if(!_g8)
				{
					_m8=instance.Type.GetMethod("afterReadData",0);
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
					base.afterReadData();
				}
			}
			
			IMethod _m9;
			bool _g9;
			bool _b9;
			public override void afterReadDataSecond()
			{
				if(!_g9)
				{
					_m9=instance.Type.GetMethod("afterReadDataSecond",0);
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
					base.afterReadDataSecond();
				}
			}
			
			IMethod _m10;
			bool _g10;
			bool _b10;
			protected override PlayerRoleGroup toCreatePlayerRoleGroup()
			{
				if(!_g10)
				{
					_m10=instance.Type.GetMethod("toCreatePlayerRoleGroup",0);
					_g10=true;
				}
				
				if(_m10!=null && !_b10)
				{
					_b10=true;
					PlayerRoleGroup re=(PlayerRoleGroup)appdomain.Invoke(_m10,instance,null);
					_b10=false;
					return re;
					
				}
				else
				{
					return base.toCreatePlayerRoleGroup();
				}
			}
			
			IMethod _m11;
			bool _g11;
			bool _b11;
			protected override bool canCreateRoleGroupEx()
			{
				if(!_g11)
				{
					_m11=instance.Type.GetMethod("canCreateRoleGroupEx",0);
					_g11=true;
				}
				
				if(_m11!=null && !_b11)
				{
					_b11=true;
					bool re=(bool)appdomain.Invoke(_m11,instance,null);
					_b11=false;
					return re;
					
				}
				else
				{
					return base.canCreateRoleGroupEx();
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
