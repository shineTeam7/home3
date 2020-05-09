using System;
using ILRuntime.CLR.Method;
using ILRuntime.Runtime.Enviorment;
using ILRuntime.Runtime.Intepreter;
using UnityEngine;
using AppDomain = ILRuntime.Runtime.Enviorment.AppDomain;
namespace ShineEngine
{
	public class MonoBehaviourAdapter : CrossBindingAdaptor
	{
		public override Type BaseCLRType
		{
			get
			{
				return typeof(MonoBehaviour);
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

		public class Adaptor : MonoBehaviour, CrossBindingAdaptorType
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
			void Start()
			{
				if(!_g0)
				{
					_m0=instance.Type.GetMethod("Start",0);
					_g0=true;
				}
				
				if(_m0!=null)
				{
					appdomain.Invoke(_m0,instance,null);
					
				}
			}
			
			IMethod _m1;
			bool _g1;
			void Update()
			{
				if(!_g1)
				{
					_m1=instance.Type.GetMethod("Update",0);
					_g1=true;
				}
				
				if(_m1!=null)
				{
					appdomain.Invoke(_m1,instance,null);
					
				}
			}
			
			IMethod _m2;
			bool _g2;
			void FixedUpdate()
			{
				if(!_g2)
				{
					_m2=instance.Type.GetMethod("FixedUpdate",0);
					_g2=true;
				}
				
				if(_m2!=null)
				{
					appdomain.Invoke(_m2,instance,null);
					
				}
			}
			
			IMethod _m3;
			bool _g3;
			void OnGUI()
			{
				if(!_g3)
				{
					_m3=instance.Type.GetMethod("OnGUI",0);
					_g3=true;
				}
				
				if(_m3!=null)
				{
					appdomain.Invoke(_m3,instance,null);
					
				}
			}
			
			IMethod _m4;
			bool _g4;
			void OnDestroy()
			{
				if(!_g4)
				{
					_m4=instance.Type.GetMethod("OnDestroy",0);
					_g4=true;
				}
				
				if(_m4!=null)
				{
					appdomain.Invoke(_m4,instance,null);
					
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