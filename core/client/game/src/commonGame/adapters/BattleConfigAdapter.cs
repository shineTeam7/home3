using System;
using System.Text;
using ILRuntime.CLR.Method;
using ILRuntime.Runtime.Enviorment;
using ILRuntime.Runtime.Intepreter;
using UnityEngine;
using AppDomain = ILRuntime.Runtime.Enviorment.AppDomain;
using ShineEngine;

	public class BattleConfigAdapter : CrossBindingAdaptor
	{
		public override Type BaseCLRType
		{
			get
			{
				return typeof(BattleConfig);
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

		public class Adaptor : BattleConfig, CrossBindingAdaptorType
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
			public override void clear()
			{
				if(!_g0)
				{
					_m0=instance.Type.GetMethod("clear",0);
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
					base.clear();
				}
			}
			
			IMethod _m1;
			bool _g1;
			bool _b1;
			protected override void toReadBytesSimple(BytesReadStream stream)
			{
				if(!_g1)
				{
					_m1=instance.Type.GetMethod("toReadBytesSimple",1);
					_g1=true;
				}
				
				if(_m1!=null && !_b1)
				{
					_b1=true;
					_p1[0]=stream;
					appdomain.Invoke(_m1,instance,_p1);
					_p1[0]=null;
					_b1=false;
					
				}
				else
				{
					base.toReadBytesSimple(stream);
				}
			}
			
			IMethod _m2;
			bool _g2;
			bool _b2;
			protected override void afterReadConfig()
			{
				if(!_g2)
				{
					_m2=instance.Type.GetMethod("afterReadConfig",0);
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
					base.afterReadConfig();
				}
			}
			
			IMethod _m3;
			bool _g3;
			bool _b3;
			protected override void generateRefresh()
			{
				if(!_g3)
				{
					_m3=instance.Type.GetMethod("generateRefresh",0);
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
					base.generateRefresh();
				}
			}
			
			IMethod _m4;
			bool _g4;
			bool _b4;
			protected override void toWriteBytesSimple(BytesWriteStream stream)
			{
				if(!_g4)
				{
					_m4=instance.Type.GetMethod("toWriteBytesSimple",1);
					_g4=true;
				}
				
				if(_m4!=null && !_b4)
				{
					_b4=true;
					_p1[0]=stream;
					appdomain.Invoke(_m4,instance,_p1);
					_p1[0]=null;
					_b4=false;
					
				}
				else
				{
					base.toWriteBytesSimple(stream);
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
