using System;
using System.Text;
using ILRuntime.CLR.Method;
using ILRuntime.Runtime.Enviorment;
using ILRuntime.Runtime.Intepreter;
using UnityEngine;
using AppDomain = ILRuntime.Runtime.Enviorment.AppDomain;
using ShineEngine;

	public class SceneRRequestAdapter : CrossBindingAdaptor
	{
		public override Type BaseCLRType
		{
			get
			{
				return typeof(SceneRRequest);
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

		public class Adaptor : SceneRRequest, CrossBindingAdaptorType
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
			protected override void toWriteBytesFull(BytesWriteStream stream)
			{
				if(!_g1)
				{
					_m1=instance.Type.GetMethod("toWriteBytesFull",1);
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
					base.toWriteBytesFull(stream);
				}
			}
			
			IMethod _m2;
			bool _g2;
			bool _b2;
			protected override void toWriteBytesSimple(BytesWriteStream stream)
			{
				if(!_g2)
				{
					_m2=instance.Type.GetMethod("toWriteBytesSimple",1);
					_g2=true;
				}
				
				if(_m2!=null && !_b2)
				{
					_b2=true;
					_p1[0]=stream;
					appdomain.Invoke(_m2,instance,_p1);
					_p1[0]=null;
					_b2=false;
					
				}
				else
				{
					base.toWriteBytesSimple(stream);
				}
			}
			
			IMethod _m3;
			bool _g3;
			bool _b3;
			public override string getDataClassName()
			{
				if(!_g3)
				{
					_m3=instance.Type.GetMethod("getDataClassName",0);
					_g3=true;
				}
				
				if(_m3!=null && !_b3)
				{
					_b3=true;
					string re=(string)appdomain.Invoke(_m3,instance,null);
					_b3=false;
					return re;
					
				}
				else
				{
					return base.getDataClassName();
				}
			}
			
			IMethod _m4;
			bool _g4;
			bool _b4;
			protected override void toWriteDataString(DataWriter writer)
			{
				if(!_g4)
				{
					_m4=instance.Type.GetMethod("toWriteDataString",1);
					_g4=true;
				}
				
				if(_m4!=null && !_b4)
				{
					_b4=true;
					_p1[0]=writer;
					appdomain.Invoke(_m4,instance,_p1);
					_p1[0]=null;
					_b4=false;
					
				}
				else
				{
					base.toWriteDataString(writer);
				}
			}
			
			IMethod _m5;
			bool _g5;
			bool _b5;
			protected override void copyData()
			{
				if(!_g5)
				{
					_m5=instance.Type.GetMethod("copyData",0);
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
					base.copyData();
				}
			}
			
			IMethod _m6;
			bool _g6;
			bool _b6;
			protected override void doWriteToStream(BytesWriteStream stream)
			{
				if(!_g6)
				{
					_m6=instance.Type.GetMethod("doWriteToStream",1);
					_g6=true;
				}
				
				if(_m6!=null && !_b6)
				{
					_b6=true;
					_p1[0]=stream;
					appdomain.Invoke(_m6,instance,_p1);
					_p1[0]=null;
					_b6=false;
					
				}
				else
				{
					base.doWriteToStream(stream);
				}
			}
			
			IMethod _m7;
			bool _g7;
			bool _b7;
			protected override void doWriteBytesSimple(BytesWriteStream stream)
			{
				if(!_g7)
				{
					_m7=instance.Type.GetMethod("doWriteBytesSimple",1);
					_g7=true;
				}
				
				if(_m7!=null && !_b7)
				{
					_b7=true;
					_p1[0]=stream;
					appdomain.Invoke(_m7,instance,_p1);
					_p1[0]=null;
					_b7=false;
					
				}
				else
				{
					base.doWriteBytesSimple(stream);
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
