using System;
using ILRuntime.CLR.Method;
using ILRuntime.Runtime.Enviorment;
using ILRuntime.Runtime.Intepreter;
using UnityEngine;
using AppDomain = ILRuntime.Runtime.Enviorment.AppDomain;
namespace ShineEngine
{
	public class BaseResponseAdapter : CrossBindingAdaptor
	{
		public override Type BaseCLRType
		{
			get
			{
				return typeof(BaseResponse);
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

		public class Adaptor : BaseResponse, CrossBindingAdaptorType
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
			protected override void toReadBytesSimple(BytesReadStream stream)
			{
				if(!_g0)
				{
					_m0=instance.Type.GetMethod("toReadBytesSimple",1);
					_g0=true;
				}
				
				if(_m0!=null && !_b0)
				{
					_b0=true;
					_p1[0]=stream;
					appdomain.Invoke(_m0,instance,_p1);
					_p1[0]=null;
					_b0=false;
					
				}
				else
				{
					base.toReadBytesSimple(stream);
				}
			}
			
			IMethod _m1;
			bool _g1;
			bool _b1;
			public override BaseResponse readFromStream(BytesReadStream stream)
			{
				if(!_g1)
				{
					_m1=instance.Type.GetMethod("readFromStream",1);
					_g1=true;
				}
				
				if(_m1!=null && !_b1)
				{
					_b1=true;
					_p1[0]=stream;
					BaseResponse re=(BaseResponse)appdomain.Invoke(_m1,instance,_p1);
					_p1[0]=null;
					_b1=false;
					return re;
					
				}
				else
				{
					return base.readFromStream(stream);
				}
			}
			
			IMethod _m2;
			bool _g2;
			bool _b2;
			protected override void preExecute()
			{
				if(!_g2)
				{
					_m2=instance.Type.GetMethod("preExecute",0);
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
					base.preExecute();
				}
			}
			
			IMethod _m3;
			bool _g3;
			protected override void execute()
			{
				if(!_g3)
				{
					_m3=instance.Type.GetMethod("execute",0);
					_g3=true;
				}
				
				if(_m3!=null)
				{
					appdomain.Invoke(_m3,instance,null);
					
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
}