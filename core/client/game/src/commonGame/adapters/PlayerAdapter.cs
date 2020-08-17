using System;
using System.Text;
using ILRuntime.CLR.Method;
using ILRuntime.Runtime.Enviorment;
using ILRuntime.Runtime.Intepreter;
using UnityEngine;
using AppDomain = ILRuntime.Runtime.Enviorment.AppDomain;
using ShineEngine;

	public class PlayerAdapter : CrossBindingAdaptor
	{
		public override Type BaseCLRType
		{
			get
			{
				return typeof(Player);
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

		public class Adaptor : Player, CrossBindingAdaptorType
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
			
			private object[] _p2=new object[2];
			
			

			IMethod _m0;
			bool _g0;
			bool _b0;
			protected override void registParts()
			{
				if(!_g0)
				{
					_m0=instance.Type.GetMethod("registParts",0);
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
					base.registParts();
				}
			}
			
			IMethod _m1;
			bool _g1;
			bool _b1;
			public override PlayerListData createListData()
			{
				if(!_g1)
				{
					_m1=instance.Type.GetMethod("createListData",0);
					_g1=true;
				}
				
				if(_m1!=null && !_b1)
				{
					_b1=true;
					PlayerListData re=(PlayerListData)appdomain.Invoke(_m1,instance,null);
					_b1=false;
					return re;
					
				}
				else
				{
					return base.createListData();
				}
			}
			
			IMethod _m2;
			bool _g2;
			bool _b2;
			public override void readListData(PlayerListData listData)
			{
				if(!_g2)
				{
					_m2=instance.Type.GetMethod("readListData",1);
					_g2=true;
				}
				
				if(_m2!=null && !_b2)
				{
					_b2=true;
					_p1[0]=listData;
					appdomain.Invoke(_m2,instance,_p1);
					_p1[0]=null;
					_b2=false;
					
				}
				else
				{
					base.readListData(listData);
				}
			}
			
			IMethod _m3;
			bool _g3;
			bool _b3;
			public override void writeListData(PlayerListData listData)
			{
				if(!_g3)
				{
					_m3=instance.Type.GetMethod("writeListData",1);
					_g3=true;
				}
				
				if(_m3!=null && !_b3)
				{
					_b3=true;
					_p1[0]=listData;
					appdomain.Invoke(_m3,instance,_p1);
					_p1[0]=null;
					_b3=false;
					
				}
				else
				{
					base.writeListData(listData);
				}
			}
			
			IMethod _m4;
			bool _g4;
			bool _b4;
			public override void construct()
			{
				if(!_g4)
				{
					_m4=instance.Type.GetMethod("construct",0);
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
					base.construct();
				}
			}
			
			IMethod _m5;
			bool _g5;
			bool _b5;
			public override void newInitData()
			{
				if(!_g5)
				{
					_m5=instance.Type.GetMethod("newInitData",0);
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
					base.newInitData();
				}
			}
			
			IMethod _m6;
			bool _g6;
			bool _b6;
			public override void init()
			{
				if(!_g6)
				{
					_m6=instance.Type.GetMethod("init",0);
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
					base.init();
				}
			}
			
			IMethod _m7;
			bool _g7;
			bool _b7;
			public override void dispose()
			{
				if(!_g7)
				{
					_m7=instance.Type.GetMethod("dispose",0);
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
					base.dispose();
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
			public override void beforeLogin()
			{
				if(!_g9)
				{
					_m9=instance.Type.GetMethod("beforeLogin",0);
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
					base.beforeLogin();
				}
			}
			
			IMethod _m10;
			bool _g10;
			bool _b10;
			public override void onSecond(int delay)
			{
				if(!_g10)
				{
					_m10=instance.Type.GetMethod("onSecond",1);
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
					base.onSecond(delay);
				}
			}
			
			IMethod _m11;
			bool _g11;
			bool _b11;
			public override void onDaily()
			{
				if(!_g11)
				{
					_m11=instance.Type.GetMethod("onDaily",0);
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
					base.onDaily();
				}
			}
			
			IMethod _m12;
			bool _g12;
			bool _b12;
			public override void onReloadConfig()
			{
				if(!_g12)
				{
					_m12=instance.Type.GetMethod("onReloadConfig",0);
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
					base.onReloadConfig();
				}
			}
			
			IMethod _m13;
			bool _g13;
			bool _b13;
			public override void onActivityOpen(int id,bool atTime)
			{
				if(!_g13)
				{
					_m13=instance.Type.GetMethod("onActivityOpen",2);
					_g13=true;
				}
				
				if(_m13!=null && !_b13)
				{
					_b13=true;
					_p2[0]=id;
					_p2[1]=atTime;
					appdomain.Invoke(_m13,instance,_p2);
					_p2[0]=null;
					_p2[1]=null;
					_b13=false;
					
				}
				else
				{
					base.onActivityOpen(id,atTime);
				}
			}
			
			IMethod _m14;
			bool _g14;
			bool _b14;
			public override void onActivityClose(int id,bool atTime)
			{
				if(!_g14)
				{
					_m14=instance.Type.GetMethod("onActivityClose",2);
					_g14=true;
				}
				
				if(_m14!=null && !_b14)
				{
					_b14=true;
					_p2[0]=id;
					_p2[1]=atTime;
					appdomain.Invoke(_m14,instance,_p2);
					_p2[0]=null;
					_p2[1]=null;
					_b14=false;
					
				}
				else
				{
					base.onActivityClose(id,atTime);
				}
			}
			
			IMethod _m15;
			bool _g15;
			bool _b15;
			public override void onActivityReset(int id,bool atTime)
			{
				if(!_g15)
				{
					_m15=instance.Type.GetMethod("onActivityReset",2);
					_g15=true;
				}
				
				if(_m15!=null && !_b15)
				{
					_b15=true;
					_p2[0]=id;
					_p2[1]=atTime;
					appdomain.Invoke(_m15,instance,_p2);
					_p2[0]=null;
					_p2[1]=null;
					_b15=false;
					
				}
				else
				{
					base.onActivityReset(id,atTime);
				}
			}
			
			IMethod _m16;
			bool _g16;
			bool _b16;
			public override void onNewCreate()
			{
				if(!_g16)
				{
					_m16=instance.Type.GetMethod("onNewCreate",0);
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
					base.onNewCreate();
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
