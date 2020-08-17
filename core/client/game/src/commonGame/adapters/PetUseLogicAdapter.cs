using System;
using System.Text;
using ILRuntime.CLR.Method;
using ILRuntime.Runtime.Enviorment;
using ILRuntime.Runtime.Intepreter;
using UnityEngine;
using AppDomain = ILRuntime.Runtime.Enviorment.AppDomain;
using ShineEngine;

	public class PetUseLogicAdapter : CrossBindingAdaptor
	{
		public override Type BaseCLRType
		{
			get
			{
				return typeof(PetUseLogic);
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

		public class Adaptor : PetUseLogic, CrossBindingAdaptorType
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
			public override bool isCharacter()
			{
				if(!_g0)
				{
					_m0=instance.Type.GetMethod("isCharacter",0);
					_g0=true;
				}
				
				if(_m0!=null && !_b0)
				{
					_b0=true;
					bool re=(bool)appdomain.Invoke(_m0,instance,null);
					_b0=false;
					return re;
					
				}
				else
				{
					return base.isCharacter();
				}
			}
			
			IMethod _m1;
			bool _g1;
			bool _b1;
			protected override void setData(MUnitUseData data)
			{
				if(!_g1)
				{
					_m1=instance.Type.GetMethod("setData",1);
					_g1=true;
				}
				
				if(_m1!=null && !_b1)
				{
					_b1=true;
					_p1[0]=data;
					appdomain.Invoke(_m1,instance,_p1);
					_p1[0]=null;
					_b1=false;
					
				}
				else
				{
					base.setData(data);
				}
			}
			
			IMethod _m2;
			bool _g2;
			bool _b2;
			protected override void makeFightDataLogic(MUnitFightDataLogic logic)
			{
				if(!_g2)
				{
					_m2=instance.Type.GetMethod("makeFightDataLogic",1);
					_g2=true;
				}
				
				if(_m2!=null && !_b2)
				{
					_b2=true;
					_p1[0]=logic;
					appdomain.Invoke(_m2,instance,_p1);
					_p1[0]=null;
					_b2=false;
					
				}
				else
				{
					base.makeFightDataLogic(logic);
				}
			}
			
			IMethod _m3;
			bool _g3;
			bool _b3;
			public override int getCurrency(int type)
			{
				if(!_g3)
				{
					_m3=instance.Type.GetMethod("getCurrency",1);
					_g3=true;
				}
				
				if(_m3!=null && !_b3)
				{
					_b3=true;
					_p1[0]=type;
					int re=(int)appdomain.Invoke(_m3,instance,_p1);
					_p1[0]=null;
					_b3=false;
					return re;
					
				}
				else
				{
					return base.getCurrency(type);
				}
			}
			
			IMethod _m4;
			bool _g4;
			bool _b4;
			protected override MUnitIdentityData toCreateIdentityData()
			{
				if(!_g4)
				{
					_m4=instance.Type.GetMethod("toCreateIdentityData",0);
					_g4=true;
				}
				
				if(_m4!=null && !_b4)
				{
					_b4=true;
					MUnitIdentityData re=(MUnitIdentityData)appdomain.Invoke(_m4,instance,null);
					_b4=false;
					return re;
					
				}
				else
				{
					return base.toCreateIdentityData();
				}
			}
			
			IMethod _m5;
			bool _g5;
			bool _b5;
			protected override void makeIdentityData(MUnitIdentityData data)
			{
				if(!_g5)
				{
					_m5=instance.Type.GetMethod("makeIdentityData",1);
					_g5=true;
				}
				
				if(_m5!=null && !_b5)
				{
					_b5=true;
					_p1[0]=data;
					appdomain.Invoke(_m5,instance,_p1);
					_p1[0]=null;
					_b5=false;
					
				}
				else
				{
					base.makeIdentityData(data);
				}
			}
			
			IMethod _m6;
			bool _g6;
			bool _b6;
			public override int getMUnitFuncID(int funcID)
			{
				if(!_g6)
				{
					_m6=instance.Type.GetMethod("getMUnitFuncID",1);
					_g6=true;
				}
				
				if(_m6!=null && !_b6)
				{
					_b6=true;
					_p1[0]=funcID;
					int re=(int)appdomain.Invoke(_m6,instance,_p1);
					_p1[0]=null;
					_b6=false;
					return re;
					
				}
				else
				{
					return base.getMUnitFuncID(funcID);
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
