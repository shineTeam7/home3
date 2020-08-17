using System;
using System.Text;
using ILRuntime.CLR.Method;
using ILRuntime.Runtime.Enviorment;
using ILRuntime.Runtime.Intepreter;
using UnityEngine;
using AppDomain = ILRuntime.Runtime.Enviorment.AppDomain;
using ShineEngine;

	public class RolePartAdapter : CrossBindingAdaptor
	{
		public override Type BaseCLRType
		{
			get
			{
				return typeof(RolePart);
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

		public class Adaptor : RolePart, CrossBindingAdaptorType
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
			
			private object[] _p3=new object[3];
			
			

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
			public override void doCreatePlayer(CreatePlayerData data)
			{
				if(!_g20)
				{
					_m20=instance.Type.GetMethod("doCreatePlayer",1);
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
					base.doCreatePlayer(data);
				}
			}
			
			IMethod _m21;
			bool _g21;
			bool _b21;
			public override void makeRoleShowData(RoleShowData data)
			{
				if(!_g21)
				{
					_m21=instance.Type.GetMethod("makeRoleShowData",1);
					_g21=true;
				}
				
				if(_m21!=null && !_b21)
				{
					_b21=true;
					_p1[0]=data;
					appdomain.Invoke(_m21,instance,_p1);
					_p1[0]=null;
					_b21=false;
					
				}
				else
				{
					base.makeRoleShowData(data);
				}
			}
			
			IMethod _m22;
			bool _g22;
			bool _b22;
			public override void makeRoleSimpleShowData(RoleSimpleShowData data)
			{
				if(!_g22)
				{
					_m22=instance.Type.GetMethod("makeRoleSimpleShowData",1);
					_g22=true;
				}
				
				if(_m22!=null && !_b22)
				{
					_b22=true;
					_p1[0]=data;
					appdomain.Invoke(_m22,instance,_p1);
					_p1[0]=null;
					_b22=false;
					
				}
				else
				{
					base.makeRoleSimpleShowData(data);
				}
			}
			
			IMethod _m23;
			bool _g23;
			bool _b23;
			protected override void toLogAddCurrency(int type,long value,int way)
			{
				if(!_g23)
				{
					_m23=instance.Type.GetMethod("toLogAddCurrency",3);
					_g23=true;
				}
				
				if(_m23!=null && !_b23)
				{
					_b23=true;
					_p3[0]=type;
					_p3[1]=value;
					_p3[2]=way;
					appdomain.Invoke(_m23,instance,_p3);
					_p3[0]=null;
					_p3[1]=null;
					_p3[2]=null;
					_b23=false;
					
				}
				else
				{
					base.toLogAddCurrency(type,value,way);
				}
			}
			
			IMethod _m24;
			bool _g24;
			bool _b24;
			protected override void toLogCostCurrency(int type,long value,int way)
			{
				if(!_g24)
				{
					_m24=instance.Type.GetMethod("toLogCostCurrency",3);
					_g24=true;
				}
				
				if(_m24!=null && !_b24)
				{
					_b24=true;
					_p3[0]=type;
					_p3[1]=value;
					_p3[2]=way;
					appdomain.Invoke(_m24,instance,_p3);
					_p3[0]=null;
					_p3[1]=null;
					_p3[2]=null;
					_b24=false;
					
				}
				else
				{
					base.toLogCostCurrency(type,value,way);
				}
			}
			
			IMethod _m25;
			bool _g25;
			bool _b25;
			protected override void onAddCurrency(int type,long value)
			{
				if(!_g25)
				{
					_m25=instance.Type.GetMethod("onAddCurrency",2);
					_g25=true;
				}
				
				if(_m25!=null && !_b25)
				{
					_b25=true;
					_p2[0]=type;
					_p2[1]=value;
					appdomain.Invoke(_m25,instance,_p2);
					_p2[0]=null;
					_p2[1]=null;
					_b25=false;
					
				}
				else
				{
					base.onAddCurrency(type,value);
				}
			}
			
			IMethod _m26;
			bool _g26;
			bool _b26;
			protected override void onCostCurrency(int type,long value)
			{
				if(!_g26)
				{
					_m26=instance.Type.GetMethod("onCostCurrency",2);
					_g26=true;
				}
				
				if(_m26!=null && !_b26)
				{
					_b26=true;
					_p2[0]=type;
					_p2[1]=value;
					appdomain.Invoke(_m26,instance,_p2);
					_p2[0]=null;
					_p2[1]=null;
					_b26=false;
					
				}
				else
				{
					base.onCostCurrency(type,value);
				}
			}
			
			IMethod _m27;
			bool _g27;
			bool _b27;
			protected override void toRefreshCurrency(int type,long value)
			{
				if(!_g27)
				{
					_m27=instance.Type.GetMethod("toRefreshCurrency",2);
					_g27=true;
				}
				
				if(_m27!=null && !_b27)
				{
					_b27=true;
					_p2[0]=type;
					_p2[1]=value;
					appdomain.Invoke(_m27,instance,_p2);
					_p2[0]=null;
					_p2[1]=null;
					_b27=false;
					
				}
				else
				{
					base.toRefreshCurrency(type,value);
				}
			}
			
			IMethod _m28;
			bool _g28;
			bool _b28;
			public override void onBindPlatform(string uid,string platform)
			{
				if(!_g28)
				{
					_m28=instance.Type.GetMethod("onBindPlatform",2);
					_g28=true;
				}
				
				if(_m28!=null && !_b28)
				{
					_b28=true;
					_p2[0]=uid;
					_p2[1]=platform;
					appdomain.Invoke(_m28,instance,_p2);
					_p2[0]=null;
					_p2[1]=null;
					_b28=false;
					
				}
				else
				{
					base.onBindPlatform(uid,platform);
				}
			}
			
			IMethod _m29;
			bool _g29;
			bool _b29;
			public override bool checkOneRoleCondition(int[] args,bool needNotice)
			{
				if(!_g29)
				{
					_m29=instance.Type.GetMethod("checkOneRoleCondition",2);
					_g29=true;
				}
				
				if(_m29!=null && !_b29)
				{
					_b29=true;
					_p2[0]=args;
					_p2[1]=needNotice;
					bool re=(bool)appdomain.Invoke(_m29,instance,_p2);
					_p2[0]=null;
					_p2[1]=null;
					_b29=false;
					return re;
					
				}
				else
				{
					return base.checkOneRoleCondition(args,needNotice);
				}
			}
			
			IMethod _m30;
			bool _g30;
			bool _b30;
			public override void doOneRoleAction(int[] args,int num,int way)
			{
				if(!_g30)
				{
					_m30=instance.Type.GetMethod("doOneRoleAction",3);
					_g30=true;
				}
				
				if(_m30!=null && !_b30)
				{
					_b30=true;
					_p3[0]=args;
					_p3[1]=num;
					_p3[2]=way;
					appdomain.Invoke(_m30,instance,_p3);
					_p3[0]=null;
					_p3[1]=null;
					_p3[2]=null;
					_b30=false;
					
				}
				else
				{
					base.doOneRoleAction(args,num,way);
				}
			}
			
			IMethod _m31;
			bool _g31;
			bool _b31;
			public override bool checkOneRoleConditionForAction(int[] args,int num)
			{
				if(!_g31)
				{
					_m31=instance.Type.GetMethod("checkOneRoleConditionForAction",2);
					_g31=true;
				}
				
				if(_m31!=null && !_b31)
				{
					_b31=true;
					_p2[0]=args;
					_p2[1]=num;
					bool re=(bool)appdomain.Invoke(_m31,instance,_p2);
					_p2[0]=null;
					_p2[1]=null;
					_b31=false;
					return re;
					
				}
				else
				{
					return base.checkOneRoleConditionForAction(args,num);
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
