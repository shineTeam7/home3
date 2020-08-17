using System;
using System.Text;
using ILRuntime.CLR.Method;
using ILRuntime.Runtime.Enviorment;
using ILRuntime.Runtime.Intepreter;
using UnityEngine;
using AppDomain = ILRuntime.Runtime.Enviorment.AppDomain;
using ShineEngine;

	public class FriendPartAdapter : CrossBindingAdaptor
	{
		public override Type BaseCLRType
		{
			get
			{
				return typeof(FriendPart);
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

		public class Adaptor : FriendPart, CrossBindingAdaptorType
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
			public override void setData(BaseData data)
			{
				if(!_g0)
				{
					_m0=instance.Type.GetMethod("setData",1);
					_g0=true;
				}
				
				if(_m0!=null && !_b0)
				{
					_b0=true;
					_p1[0]=data;
					appdomain.Invoke(_m0,instance,_p1);
					_p1[0]=null;
					_b0=false;
					
				}
				else
				{
					base.setData(data);
				}
			}
			
			IMethod _m1;
			bool _g1;
			bool _b1;
			protected override BaseData createPartData()
			{
				if(!_g1)
				{
					_m1=instance.Type.GetMethod("createPartData",0);
					_g1=true;
				}
				
				if(_m1!=null && !_b1)
				{
					_b1=true;
					BaseData re=(BaseData)appdomain.Invoke(_m1,instance,null);
					_b1=false;
					return re;
					
				}
				else
				{
					return base.createPartData();
				}
			}
			
			IMethod _m2;
			bool _g2;
			protected override void beforeMakeData()
			{
				if(!_g2)
				{
					_m2=instance.Type.GetMethod("beforeMakeData",0);
					_g2=true;
				}
				
				if(_m2!=null)
				{
					appdomain.Invoke(_m2,instance,null);
					
				}
			}
			
			IMethod _m3;
			bool _g3;
			public override void construct()
			{
				if(!_g3)
				{
					_m3=instance.Type.GetMethod("construct",0);
					_g3=true;
				}
				
				if(_m3!=null)
				{
					appdomain.Invoke(_m3,instance,null);
					
				}
			}
			
			IMethod _m4;
			bool _g4;
			public override void init()
			{
				if(!_g4)
				{
					_m4=instance.Type.GetMethod("init",0);
					_g4=true;
				}
				
				if(_m4!=null)
				{
					appdomain.Invoke(_m4,instance,null);
					
				}
			}
			
			IMethod _m5;
			bool _g5;
			public override void dispose()
			{
				if(!_g5)
				{
					_m5=instance.Type.GetMethod("dispose",0);
					_g5=true;
				}
				
				if(_m5!=null)
				{
					appdomain.Invoke(_m5,instance,null);
					
				}
			}
			
			IMethod _m6;
			bool _g6;
			public override void onNewCreate()
			{
				if(!_g6)
				{
					_m6=instance.Type.GetMethod("onNewCreate",0);
					_g6=true;
				}
				
				if(_m6!=null)
				{
					appdomain.Invoke(_m6,instance,null);
					
				}
			}
			
			IMethod _m7;
			bool _g7;
			public override void afterReadData()
			{
				if(!_g7)
				{
					_m7=instance.Type.GetMethod("afterReadData",0);
					_g7=true;
				}
				
				if(_m7!=null)
				{
					appdomain.Invoke(_m7,instance,null);
					
				}
			}
			
			IMethod _m8;
			bool _g8;
			bool _b8;
			public override void afterReadDataSecond()
			{
				if(!_g8)
				{
					_m8=instance.Type.GetMethod("afterReadDataSecond",0);
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
					base.afterReadDataSecond();
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
			public override void setMe(Player player)
			{
				if(!_g13)
				{
					_m13=instance.Type.GetMethod("setMe",1);
					_g13=true;
				}
				
				if(_m13!=null && !_b13)
				{
					_b13=true;
					_p1[0]=player;
					appdomain.Invoke(_m13,instance,_p1);
					_p1[0]=null;
					_b13=false;
					
				}
				else
				{
					base.setMe(player);
				}
			}
			
			IMethod _m14;
			bool _g14;
			public override void onFunctionOpen(int id)
			{
				if(!_g14)
				{
					_m14=instance.Type.GetMethod("onFunctionOpen",1);
					_g14=true;
				}
				
				if(_m14!=null)
				{
					_p1[0]=id;
					appdomain.Invoke(_m14,instance,_p1);
					_p1[0]=null;
					
				}
			}
			
			IMethod _m15;
			bool _g15;
			public override void onFunctionClose(int id)
			{
				if(!_g15)
				{
					_m15=instance.Type.GetMethod("onFunctionClose",1);
					_g15=true;
				}
				
				if(_m15!=null)
				{
					_p1[0]=id;
					appdomain.Invoke(_m15,instance,_p1);
					_p1[0]=null;
					
				}
			}
			
			IMethod _m16;
			bool _g16;
			bool _b16;
			public override void onActivityOpen(int id,bool atTime)
			{
				if(!_g16)
				{
					_m16=instance.Type.GetMethod("onActivityOpen",2);
					_g16=true;
				}
				
				if(_m16!=null && !_b16)
				{
					_b16=true;
					_p2[0]=id;
					_p2[1]=atTime;
					appdomain.Invoke(_m16,instance,_p2);
					_p2[0]=null;
					_p2[1]=null;
					_b16=false;
					
				}
				else
				{
					base.onActivityOpen(id,atTime);
				}
			}
			
			IMethod _m17;
			bool _g17;
			bool _b17;
			public override void onActivityClose(int id,bool atTime)
			{
				if(!_g17)
				{
					_m17=instance.Type.GetMethod("onActivityClose",2);
					_g17=true;
				}
				
				if(_m17!=null && !_b17)
				{
					_b17=true;
					_p2[0]=id;
					_p2[1]=atTime;
					appdomain.Invoke(_m17,instance,_p2);
					_p2[0]=null;
					_p2[1]=null;
					_b17=false;
					
				}
				else
				{
					base.onActivityClose(id,atTime);
				}
			}
			
			IMethod _m18;
			bool _g18;
			bool _b18;
			public override void onActivityReset(int id,bool atTime)
			{
				if(!_g18)
				{
					_m18=instance.Type.GetMethod("onActivityReset",2);
					_g18=true;
				}
				
				if(_m18!=null && !_b18)
				{
					_b18=true;
					_p2[0]=id;
					_p2[1]=atTime;
					appdomain.Invoke(_m18,instance,_p2);
					_p2[0]=null;
					_p2[1]=null;
					_b18=false;
					
				}
				else
				{
					base.onActivityReset(id,atTime);
				}
			}
			
			IMethod _m19;
			bool _g19;
			bool _b19;
			public override void onLevelUp(int oldLevel)
			{
				if(!_g19)
				{
					_m19=instance.Type.GetMethod("onLevelUp",1);
					_g19=true;
				}
				
				if(_m19!=null && !_b19)
				{
					_b19=true;
					_p1[0]=oldLevel;
					appdomain.Invoke(_m19,instance,_p1);
					_p1[0]=null;
					_b19=false;
					
				}
				else
				{
					base.onLevelUp(oldLevel);
				}
			}
			
			IMethod _m20;
			bool _g20;
			bool _b20;
			public override void onApplyAddFriendForG(ApplyAddFriendData data)
			{
				if(!_g20)
				{
					_m20=instance.Type.GetMethod("onApplyAddFriendForG",1);
					_g20=true;
				}
				
				if(_m20!=null && !_b20)
				{
					_b20=true;
					_p1[0]=data;
					appdomain.Invoke(_m20,instance,_p1);
					_p1[0]=null;
					_b20=false;
					
				}
				else
				{
					base.onApplyAddFriendForG(data);
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
