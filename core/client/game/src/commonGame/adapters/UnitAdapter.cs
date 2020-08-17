using System;
using System.Text;
using ILRuntime.CLR.Method;
using ILRuntime.Runtime.Enviorment;
using ILRuntime.Runtime.Intepreter;
using UnityEngine;
using AppDomain = ILRuntime.Runtime.Enviorment.AppDomain;
using ShineEngine;

	public class UnitAdapter : CrossBindingAdaptor
	{
		public override Type BaseCLRType
		{
			get
			{
				return typeof(Unit);
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

		public class Adaptor : Unit, CrossBindingAdaptorType
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
			protected override void preInit()
			{
				if(!_g1)
				{
					_m1=instance.Type.GetMethod("preInit",0);
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
					base.preInit();
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
			public override void onFrame(int delay)
			{
				if(!_g3)
				{
					_m3=instance.Type.GetMethod("onFrame",1);
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
					base.onFrame(delay);
				}
			}
			
			IMethod _m4;
			bool _g4;
			bool _b4;
			public override void onFixedUpdate()
			{
				if(!_g4)
				{
					_m4=instance.Type.GetMethod("onFixedUpdate",0);
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
					base.onFixedUpdate();
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
			public override void removeAbs()
			{
				if(!_g6)
				{
					_m6=instance.Type.GetMethod("removeAbs",0);
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
					base.removeAbs();
				}
			}
			
			IMethod _m7;
			bool _g7;
			bool _b7;
			protected override UnitIdentityLogic createIdentityLogic()
			{
				if(!_g7)
				{
					_m7=instance.Type.GetMethod("createIdentityLogic",0);
					_g7=true;
				}
				
				if(_m7!=null && !_b7)
				{
					_b7=true;
					UnitIdentityLogic re=(UnitIdentityLogic)appdomain.Invoke(_m7,instance,null);
					_b7=false;
					return re;
					
				}
				else
				{
					return base.createIdentityLogic();
				}
			}
			
			IMethod _m8;
			bool _g8;
			bool _b8;
			protected override UnitPosLogic createPosLogic()
			{
				if(!_g8)
				{
					_m8=instance.Type.GetMethod("createPosLogic",0);
					_g8=true;
				}
				
				if(_m8!=null && !_b8)
				{
					_b8=true;
					UnitPosLogic re=(UnitPosLogic)appdomain.Invoke(_m8,instance,null);
					_b8=false;
					return re;
					
				}
				else
				{
					return base.createPosLogic();
				}
			}
			
			IMethod _m9;
			bool _g9;
			bool _b9;
			protected override UnitMoveLogic createMoveLogic()
			{
				if(!_g9)
				{
					_m9=instance.Type.GetMethod("createMoveLogic",0);
					_g9=true;
				}
				
				if(_m9!=null && !_b9)
				{
					_b9=true;
					UnitMoveLogic re=(UnitMoveLogic)appdomain.Invoke(_m9,instance,null);
					_b9=false;
					return re;
					
				}
				else
				{
					return base.createMoveLogic();
				}
			}
			
			IMethod _m10;
			bool _g10;
			bool _b10;
			protected override UnitAvatarLogic createAvatarLogic()
			{
				if(!_g10)
				{
					_m10=instance.Type.GetMethod("createAvatarLogic",0);
					_g10=true;
				}
				
				if(_m10!=null && !_b10)
				{
					_b10=true;
					UnitAvatarLogic re=(UnitAvatarLogic)appdomain.Invoke(_m10,instance,null);
					_b10=false;
					return re;
					
				}
				else
				{
					return base.createAvatarLogic();
				}
			}
			
			IMethod _m11;
			bool _g11;
			bool _b11;
			protected override UnitFightLogic createFightLogic()
			{
				if(!_g11)
				{
					_m11=instance.Type.GetMethod("createFightLogic",0);
					_g11=true;
				}
				
				if(_m11!=null && !_b11)
				{
					_b11=true;
					UnitFightLogic re=(UnitFightLogic)appdomain.Invoke(_m11,instance,null);
					_b11=false;
					return re;
					
				}
				else
				{
					return base.createFightLogic();
				}
			}
			
			IMethod _m12;
			bool _g12;
			bool _b12;
			protected override UnitAILogic createAILogic()
			{
				if(!_g12)
				{
					_m12=instance.Type.GetMethod("createAILogic",0);
					_g12=true;
				}
				
				if(_m12!=null && !_b12)
				{
					_b12=true;
					UnitAILogic re=(UnitAILogic)appdomain.Invoke(_m12,instance,null);
					_b12=false;
					return re;
					
				}
				else
				{
					return base.createAILogic();
				}
			}
			
			IMethod _m13;
			bool _g13;
			bool _b13;
			protected override UnitAICommandLogic createAICommandLogic()
			{
				if(!_g13)
				{
					_m13=instance.Type.GetMethod("createAICommandLogic",0);
					_g13=true;
				}
				
				if(_m13!=null && !_b13)
				{
					_b13=true;
					UnitAICommandLogic re=(UnitAICommandLogic)appdomain.Invoke(_m13,instance,null);
					_b13=false;
					return re;
					
				}
				else
				{
					return base.createAICommandLogic();
				}
			}
			
			IMethod _m14;
			bool _g14;
			bool _b14;
			protected override UnitShowLogic createShowLogic()
			{
				if(!_g14)
				{
					_m14=instance.Type.GetMethod("createShowLogic",0);
					_g14=true;
				}
				
				if(_m14!=null && !_b14)
				{
					_b14=true;
					UnitShowLogic re=(UnitShowLogic)appdomain.Invoke(_m14,instance,null);
					_b14=false;
					return re;
					
				}
				else
				{
					return base.createShowLogic();
				}
			}
			
			IMethod _m15;
			bool _g15;
			bool _b15;
			protected override UnitHeadLogic createHeadLogic()
			{
				if(!_g15)
				{
					_m15=instance.Type.GetMethod("createHeadLogic",0);
					_g15=true;
				}
				
				if(_m15!=null && !_b15)
				{
					_b15=true;
					UnitHeadLogic re=(UnitHeadLogic)appdomain.Invoke(_m15,instance,null);
					_b15=false;
					return re;
					
				}
				else
				{
					return base.createHeadLogic();
				}
			}
			
			IMethod _m16;
			bool _g16;
			bool _b16;
			protected override CharacterControlLogic createCharacterControlLogic()
			{
				if(!_g16)
				{
					_m16=instance.Type.GetMethod("createCharacterControlLogic",0);
					_g16=true;
				}
				
				if(_m16!=null && !_b16)
				{
					_b16=true;
					CharacterControlLogic re=(CharacterControlLogic)appdomain.Invoke(_m16,instance,null);
					_b16=false;
					return re;
					
				}
				else
				{
					return base.createCharacterControlLogic();
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
