using System;
using System.Text;
using ILRuntime.CLR.Method;
using ILRuntime.Runtime.Enviorment;
using ILRuntime.Runtime.Intepreter;
using UnityEngine;
using AppDomain = ILRuntime.Runtime.Enviorment.AppDomain;
using ShineEngine;

	public class UnitHeadLogic3DOneAdapter : CrossBindingAdaptor
	{
		public override Type BaseCLRType
		{
			get
			{
				return typeof(UnitHeadLogic3DOne);
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

		public class Adaptor : UnitHeadLogic3DOne, CrossBindingAdaptorType
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
			public override void setObject(SceneObject obj)
			{
				if(!_g0)
				{
					_m0=instance.Type.GetMethod("setObject",1);
					_g0=true;
				}
				
				if(_m0!=null && !_b0)
				{
					_b0=true;
					_p1[0]=obj;
					appdomain.Invoke(_m0,instance,_p1);
					_p1[0]=null;
					_b0=false;
					
				}
				else
				{
					base.setObject(obj);
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
			public override void init()
			{
				if(!_g2)
				{
					_m2=instance.Type.GetMethod("init",0);
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
					base.init();
				}
			}
			
			IMethod _m3;
			bool _g3;
			bool _b3;
			public override void afterInit()
			{
				if(!_g3)
				{
					_m3=instance.Type.GetMethod("afterInit",0);
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
					base.afterInit();
				}
			}
			
			IMethod _m4;
			bool _g4;
			bool _b4;
			public override void preRemove()
			{
				if(!_g4)
				{
					_m4=instance.Type.GetMethod("preRemove",0);
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
					base.preRemove();
				}
			}
			
			IMethod _m5;
			bool _g5;
			bool _b5;
			public override void dispose()
			{
				if(!_g5)
				{
					_m5=instance.Type.GetMethod("dispose",0);
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
					base.dispose();
				}
			}
			
			IMethod _m6;
			bool _g6;
			bool _b6;
			public override void onFrame(int delay)
			{
				if(!_g6)
				{
					_m6=instance.Type.GetMethod("onFrame",1);
					_g6=true;
				}
				
				if(_m6!=null && !_b6)
				{
					_b6=true;
					_p1[0]=delay;
					appdomain.Invoke(_m6,instance,_p1);
					_p1[0]=null;
					_b6=false;
					
				}
				else
				{
					base.onFrame(delay);
				}
			}
			
			IMethod _m7;
			bool _g7;
			bool _b7;
			public override void onSecond()
			{
				if(!_g7)
				{
					_m7=instance.Type.GetMethod("onSecond",0);
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
					base.onSecond();
				}
			}
			
			IMethod _m8;
			bool _g8;
			bool _b8;
			public override void onFixedUpdate()
			{
				if(!_g8)
				{
					_m8=instance.Type.GetMethod("onFixedUpdate",0);
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
					base.onFixedUpdate();
				}
			}
			
			IMethod _m9;
			bool _g9;
			bool _b9;
			public override void onReloadConfig()
			{
				if(!_g9)
				{
					_m9=instance.Type.GetMethod("onReloadConfig",0);
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
					base.onReloadConfig();
				}
			}
			
			IMethod _m10;
			bool _g10;
			bool _b10;
			public override void onPiece(int delay)
			{
				if(!_g10)
				{
					_m10=instance.Type.GetMethod("onPiece",1);
					_g10=true;
				}
				
				if(_m10!=null && !_b10)
				{
					_b10=true;
					_p1[0]=delay;
					appdomain.Invoke(_m10,instance,_p1);
					_p1[0]=null;
					_b10=false;
					
				}
				else
				{
					base.onPiece(delay);
				}
			}
			
			IMethod _m11;
			bool _g11;
			bool _b11;
			public override void refreshHeight()
			{
				if(!_g11)
				{
					_m11=instance.Type.GetMethod("refreshHeight",0);
					_g11=true;
				}
				
				if(_m11!=null && !_b11)
				{
					_b11=true;
					appdomain.Invoke(_m11,instance,null);
					_b11=false;
					
				}
				else
				{
					base.refreshHeight();
				}
			}
			
			IMethod _m12;
			bool _g12;
			bool _b12;
			protected override void doSetPos(PosData pos)
			{
				if(!_g12)
				{
					_m12=instance.Type.GetMethod("doSetPos",1);
					_g12=true;
				}
				
				if(_m12!=null && !_b12)
				{
					_b12=true;
					_p1[0]=pos;
					appdomain.Invoke(_m12,instance,_p1);
					_p1[0]=null;
					_b12=false;
					
				}
				else
				{
					base.doSetPos(pos);
				}
			}
			
			IMethod _m13;
			bool _g13;
			bool _b13;
			public override void onAttributeChange(bool[] changeSet)
			{
				if(!_g13)
				{
					_m13=instance.Type.GetMethod("onAttributeChange",1);
					_g13=true;
				}
				
				if(_m13!=null && !_b13)
				{
					_b13=true;
					_p1[0]=changeSet;
					appdomain.Invoke(_m13,instance,_p1);
					_p1[0]=null;
					_b13=false;
					
				}
				else
				{
					base.onAttributeChange(changeSet);
				}
			}
			
			IMethod _m14;
			bool _g14;
			bool _b14;
			public override void onRefreshHp()
			{
				if(!_g14)
				{
					_m14=instance.Type.GetMethod("onRefreshHp",0);
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
					base.onRefreshHp();
				}
			}
			
			IMethod _m15;
			bool _g15;
			bool _b15;
			public override void onRefreshMp()
			{
				if(!_g15)
				{
					_m15=instance.Type.GetMethod("onRefreshMp",0);
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
					base.onRefreshMp();
				}
			}
			
			IMethod _m16;
			bool _g16;
			bool _b16;
			public override void onRefreshPhysicsShield()
			{
				if(!_g16)
				{
					_m16=instance.Type.GetMethod("onRefreshPhysicsShield",0);
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
					base.onRefreshPhysicsShield();
				}
			}
			
			IMethod _m17;
			bool _g17;
			bool _b17;
			public override void onRefreshMagicShield()
			{
				if(!_g17)
				{
					_m17=instance.Type.GetMethod("onRefreshMagicShield",0);
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
					base.onRefreshMagicShield();
				}
			}
			
			IMethod _m18;
			bool _g18;
			bool _b18;
			public override void onRefreshRoleName()
			{
				if(!_g18)
				{
					_m18=instance.Type.GetMethod("onRefreshRoleName",0);
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
					base.onRefreshRoleName();
				}
			}
			
			IMethod _m19;
			bool _g19;
			bool _b19;
			protected override void doReCountCameraPos()
			{
				if(!_g19)
				{
					_m19=instance.Type.GetMethod("doReCountCameraPos",0);
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
					base.doReCountCameraPos();
				}
			}
			
			IMethod _m20;
			bool _g20;
			bool _b20;
			protected override void initModel()
			{
				if(!_g20)
				{
					_m20=instance.Type.GetMethod("initModel",0);
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
					base.initModel();
				}
			}
			
			IMethod _m21;
			bool _g21;
			bool _b21;
			protected override string getUIModelName()
			{
				if(!_g21)
				{
					_m21=instance.Type.GetMethod("getUIModelName",0);
					_g21=true;
				}
				
				if(_m21!=null && !_b21)
				{
					_b21=true;
					string re=(string)appdomain.Invoke(_m21,instance,null);
					_b21=false;
					return re;
					
				}
				else
				{
					return base.getUIModelName();
				}
			}
			
			IMethod _m22;
			bool _g22;
			bool _b22;
			protected override UIModel toCreateModel()
			{
				if(!_g22)
				{
					_m22=instance.Type.GetMethod("toCreateModel",0);
					_g22=true;
				}
				
				if(_m22!=null && !_b22)
				{
					_b22=true;
					UIModel re=(UIModel)appdomain.Invoke(_m22,instance,null);
					_b22=false;
					return re;
					
				}
				else
				{
					return base.toCreateModel();
				}
			}
			
			IMethod _m23;
			bool _g23;
			bool _b23;
			protected override void initShow()
			{
				if(!_g23)
				{
					_m23=instance.Type.GetMethod("initShow",0);
					_g23=true;
				}
				
				if(_m23!=null && !_b23)
				{
					_b23=true;
					appdomain.Invoke(_m23,instance,null);
					_b23=false;
					
				}
				else
				{
					base.initShow();
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
