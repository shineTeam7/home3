using System;
using System.Text;
using ILRuntime.CLR.Method;
using ILRuntime.Runtime.Enviorment;
using ILRuntime.Runtime.Intepreter;
using UnityEngine;
using AppDomain = ILRuntime.Runtime.Enviorment.AppDomain;
using ShineEngine;

	public class RectDataAdapter : CrossBindingAdaptor
	{
		public override Type BaseCLRType
		{
			get
			{
				return typeof(RectData);
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

		public class Adaptor : RectData, CrossBindingAdaptorType
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
			protected override void toReadBytesFull(BytesReadStream stream)
			{
				if(!_g1)
				{
					_m1=instance.Type.GetMethod("toReadBytesFull",1);
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
					base.toReadBytesFull(stream);
				}
			}
			
			IMethod _m2;
			bool _g2;
			bool _b2;
			protected override void toWriteBytesFull(BytesWriteStream stream)
			{
				if(!_g2)
				{
					_m2=instance.Type.GetMethod("toWriteBytesFull",1);
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
					base.toWriteBytesFull(stream);
				}
			}
			
			IMethod _m3;
			bool _g3;
			bool _b3;
			protected override void toReadBytesSimple(BytesReadStream stream)
			{
				if(!_g3)
				{
					_m3=instance.Type.GetMethod("toReadBytesSimple",1);
					_g3=true;
				}
				
				if(_m3!=null && !_b3)
				{
					_b3=true;
					_p1[0]=stream;
					appdomain.Invoke(_m3,instance,_p1);
					_p1[0]=null;
					_b3=false;
					
				}
				else
				{
					base.toReadBytesSimple(stream);
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
			
			IMethod _m5;
			bool _g5;
			bool _b5;
			protected override void toCopy(BaseData data)
			{
				if(!_g5)
				{
					_m5=instance.Type.GetMethod("toCopy",1);
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
					base.toCopy(data);
				}
			}
			
			IMethod _m6;
			bool _g6;
			bool _b6;
			protected override void toShadowCopy(BaseData data)
			{
				if(!_g6)
				{
					_m6=instance.Type.GetMethod("toShadowCopy",1);
					_g6=true;
				}
				
				if(_m6!=null && !_b6)
				{
					_b6=true;
					_p1[0]=data;
					appdomain.Invoke(_m6,instance,_p1);
					_p1[0]=null;
					_b6=false;
					
				}
				else
				{
					base.toShadowCopy(data);
				}
			}
			
			IMethod _m7;
			bool _g7;
			bool _b7;
			protected override bool toDataEquals(BaseData data)
			{
				if(!_g7)
				{
					_m7=instance.Type.GetMethod("toDataEquals",1);
					_g7=true;
				}
				
				if(_m7!=null && !_b7)
				{
					_b7=true;
					_p1[0]=data;
					bool re=(bool)appdomain.Invoke(_m7,instance,_p1);
					_p1[0]=null;
					_b7=false;
					return re;
					
				}
				else
				{
					return base.toDataEquals(data);
				}
			}
			
			IMethod _m8;
			bool _g8;
			bool _b8;
			public override string getDataClassName()
			{
				if(!_g8)
				{
					_m8=instance.Type.GetMethod("getDataClassName",0);
					_g8=true;
				}
				
				if(_m8!=null && !_b8)
				{
					_b8=true;
					string re=(string)appdomain.Invoke(_m8,instance,null);
					_b8=false;
					return re;
					
				}
				else
				{
					return base.getDataClassName();
				}
			}
			
			IMethod _m9;
			bool _g9;
			bool _b9;
			protected override void toWriteDataString(DataWriter writer)
			{
				if(!_g9)
				{
					_m9=instance.Type.GetMethod("toWriteDataString",1);
					_g9=true;
				}
				
				if(_m9!=null && !_b9)
				{
					_b9=true;
					_p1[0]=writer;
					appdomain.Invoke(_m9,instance,_p1);
					_p1[0]=null;
					_b9=false;
					
				}
				else
				{
					base.toWriteDataString(writer);
				}
			}
			
			IMethod _m10;
			bool _g10;
			bool _b10;
			public override void initDefault()
			{
				if(!_g10)
				{
					_m10=instance.Type.GetMethod("initDefault",0);
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
					base.initDefault();
				}
			}
			
			IMethod _m11;
			bool _g11;
			bool _b11;
			protected override void beforeWrite()
			{
				if(!_g11)
				{
					_m11=instance.Type.GetMethod("beforeWrite",0);
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
					base.beforeWrite();
				}
			}
			
			IMethod _m12;
			bool _g12;
			bool _b12;
			protected override void afterRead()
			{
				if(!_g12)
				{
					_m12=instance.Type.GetMethod("afterRead",0);
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
					base.afterRead();
				}
			}
			
			IMethod _m13;
			bool _g13;
			bool _b13;
			public override void initListData()
			{
				if(!_g13)
				{
					_m13=instance.Type.GetMethod("initListData",0);
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
					base.initListData();
				}
			}
			
			IMethod _m14;
			bool _g14;
			bool _b14;
			protected override void toRelease(DataPool pool)
			{
				if(!_g14)
				{
					_m14=instance.Type.GetMethod("toRelease",1);
					_g14=true;
				}
				
				if(_m14!=null && !_b14)
				{
					_b14=true;
					_p1[0]=pool;
					appdomain.Invoke(_m14,instance,_p1);
					_p1[0]=null;
					_b14=false;
					
				}
				else
				{
					base.toRelease(pool);
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
