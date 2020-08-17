using System;
using System.Text;
using ILRuntime.CLR.Method;
using ILRuntime.Runtime.Enviorment;
using ILRuntime.Runtime.Intepreter;
using UnityEngine;
using AppDomain = ILRuntime.Runtime.Enviorment.AppDomain;
using ShineEngine;

	public class GameMainControlAdapter : CrossBindingAdaptor
	{
		public override Type BaseCLRType
		{
			get
			{
				return typeof(GameMainControl);
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

		public class Adaptor : GameMainControl, CrossBindingAdaptorType
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
			public override void init()
			{
				if(!_g0)
				{
					_m0=instance.Type.GetMethod("init",0);
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
					base.init();
				}
			}
			
			IMethod _m1;
			bool _g1;
			bool _b1;
			protected override void initStep()
			{
				if(!_g1)
				{
					_m1=instance.Type.GetMethod("initStep",0);
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
					base.initStep();
				}
			}
			
			IMethod _m2;
			bool _g2;
			bool _b2;
			protected override void onSecond(int delay)
			{
				if(!_g2)
				{
					_m2=instance.Type.GetMethod("onSecond",1);
					_g2=true;
				}
				
				if(_m2!=null && !_b2)
				{
					_b2=true;
					_p1[0]=delay;
					appdomain.Invoke(_m2,instance,_p1);
					_p1[0]=null;
					_b2=false;
					
				}
				else
				{
					base.onSecond(delay);
				}
			}
			
			IMethod _m3;
			bool _g3;
			bool _b3;
			public override void backToLogin()
			{
				if(!_g3)
				{
					_m3=instance.Type.GetMethod("backToLogin",0);
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
					base.backToLogin();
				}
			}
			
			IMethod _m4;
			bool _g4;
			bool _b4;
			protected override void clearToLogin()
			{
				if(!_g4)
				{
					_m4=instance.Type.GetMethod("clearToLogin",0);
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
					base.clearToLogin();
				}
			}
			
			IMethod _m5;
			bool _g5;
			bool _b5;
			protected override void configLoadOver()
			{
				if(!_g5)
				{
					_m5=instance.Type.GetMethod("configLoadOver",0);
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
					base.configLoadOver();
				}
			}
			
			IMethod _m6;
			bool _g6;
			bool _b6;
			protected override void stepLoadBundleInfo()
			{
				if(!_g6)
				{
					_m6=instance.Type.GetMethod("stepLoadBundleInfo",0);
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
					base.stepLoadBundleInfo();
				}
			}
			
			IMethod _m7;
			bool _g7;
			bool _b7;
			protected override void stepLoadConfig()
			{
				if(!_g7)
				{
					_m7=instance.Type.GetMethod("stepLoadConfig",0);
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
					base.stepLoadConfig();
				}
			}
			
			IMethod _m8;
			bool _g8;
			bool _b8;
			protected override void stepLoadFirstResource()
			{
				if(!_g8)
				{
					_m8=instance.Type.GetMethod("stepLoadFirstResource",0);
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
					base.stepLoadFirstResource();
				}
			}
			
			IMethod _m9;
			bool _g9;
			bool _b9;
			protected override void stepInputUser()
			{
				if(!_g9)
				{
					_m9=instance.Type.GetMethod("stepInputUser",0);
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
					base.stepInputUser();
				}
			}
			
			IMethod _m10;
			bool _g10;
			bool _b10;
			protected override void doStepInputUser(ClientLoginCacheData data,bool canOffline)
			{
				if(!_g10)
				{
					_m10=instance.Type.GetMethod("doStepInputUser",2);
					_g10=true;
				}
				
				if(_m10!=null && !_b10)
				{
					_b10=true;
					_p2[0]=data;
					_p2[1]=canOffline;
					appdomain.Invoke(_m10,instance,_p2);
					_p2[0]=null;
					_p2[1]=null;
					_b10=false;
					
				}
				else
				{
					base.doStepInputUser(data,canOffline);
				}
			}
			
			IMethod _m11;
			bool _g11;
			bool _b11;
			protected override void stepSelectServer()
			{
				if(!_g11)
				{
					_m11=instance.Type.GetMethod("stepSelectServer",0);
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
					base.stepSelectServer();
				}
			}
			
			IMethod _m12;
			bool _g12;
			bool _b12;
			protected override string getUniqueIdentifier()
			{
				if(!_g12)
				{
					_m12=instance.Type.GetMethod("getUniqueIdentifier",0);
					_g12=true;
				}
				
				if(_m12!=null && !_b12)
				{
					_b12=true;
					string re=(string)appdomain.Invoke(_m12,instance,null);
					_b12=false;
					return re;
					
				}
				else
				{
					return base.getUniqueIdentifier();
				}
			}
			
			IMethod _m13;
			bool _g13;
			bool _b13;
			protected override string createVisitorUID()
			{
				if(!_g13)
				{
					_m13=instance.Type.GetMethod("createVisitorUID",0);
					_g13=true;
				}
				
				if(_m13!=null && !_b13)
				{
					_b13=true;
					string re=(string)appdomain.Invoke(_m13,instance,null);
					_b13=false;
					return re;
					
				}
				else
				{
					return base.createVisitorUID();
				}
			}
			
			IMethod _m14;
			bool _g14;
			bool _b14;
			public override void makeClientLoginData(ClientLoginData data)
			{
				if(!_g14)
				{
					_m14=instance.Type.GetMethod("makeClientLoginData",1);
					_g14=true;
				}
				
				if(_m14!=null && !_b14)
				{
					_b14=true;
					_p1[0]=data;
					appdomain.Invoke(_m14,instance,_p1);
					_p1[0]=null;
					_b14=false;
					
				}
				else
				{
					base.makeClientLoginData(data);
				}
			}
			
			IMethod _m15;
			bool _g15;
			bool _b15;
			protected override void stepLoginHttp()
			{
				if(!_g15)
				{
					_m15=instance.Type.GetMethod("stepLoginHttp",0);
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
					base.stepLoginHttp();
				}
			}
			
			IMethod _m16;
			bool _g16;
			bool _b16;
			protected override void stepCheckCreatePlayer()
			{
				if(!_g16)
				{
					_m16=instance.Type.GetMethod("stepCheckCreatePlayer",0);
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
					base.stepCheckCreatePlayer();
				}
			}
			
			IMethod _m17;
			bool _g17;
			bool _b17;
			public override string createRandomName()
			{
				if(!_g17)
				{
					_m17=instance.Type.GetMethod("createRandomName",0);
					_g17=true;
				}
				
				if(_m17!=null && !_b17)
				{
					_b17=true;
					string re=(string)appdomain.Invoke(_m17,instance,null);
					_b17=false;
					return re;
					
				}
				else
				{
					return base.createRandomName();
				}
			}
			
			IMethod _m18;
			bool _g18;
			bool _b18;
			protected override CreatePlayerData getCreatePlayerData()
			{
				if(!_g18)
				{
					_m18=instance.Type.GetMethod("getCreatePlayerData",0);
					_g18=true;
				}
				
				if(_m18!=null && !_b18)
				{
					_b18=true;
					CreatePlayerData re=(CreatePlayerData)appdomain.Invoke(_m18,instance,null);
					_b18=false;
					return re;
					
				}
				else
				{
					return base.getCreatePlayerData();
				}
			}
			
			IMethod _m19;
			bool _g19;
			bool _b19;
			public override void onCreatePlayerSuccess(PlayerLoginData pData)
			{
				if(!_g19)
				{
					_m19=instance.Type.GetMethod("onCreatePlayerSuccess",1);
					_g19=true;
				}
				
				if(_m19!=null && !_b19)
				{
					_b19=true;
					_p1[0]=pData;
					appdomain.Invoke(_m19,instance,_p1);
					_p1[0]=null;
					_b19=false;
					
				}
				else
				{
					base.onCreatePlayerSuccess(pData);
				}
			}
			
			IMethod _m20;
			bool _g20;
			bool _b20;
			protected override void stepPlayerLogin()
			{
				if(!_g20)
				{
					_m20=instance.Type.GetMethod("stepPlayerLogin",0);
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
					base.stepPlayerLogin();
				}
			}
			
			IMethod _m21;
			bool _g21;
			bool _b21;
			protected override void showSinglePlayerLogin(PlayerListData listData)
			{
				if(!_g21)
				{
					_m21=instance.Type.GetMethod("showSinglePlayerLogin",1);
					_g21=true;
				}
				
				if(_m21!=null && !_b21)
				{
					_b21=true;
					_p1[0]=listData;
					appdomain.Invoke(_m21,instance,_p1);
					_p1[0]=null;
					_b21=false;
					
				}
				else
				{
					base.showSinglePlayerLogin(listData);
				}
			}
			
			IMethod _m22;
			bool _g22;
			bool _b22;
			public override void makeCreatePlayerData(CreatePlayerData data)
			{
				if(!_g22)
				{
					_m22=instance.Type.GetMethod("makeCreatePlayerData",1);
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
					base.makeCreatePlayerData(data);
				}
			}
			
			IMethod _m23;
			bool _g23;
			bool _b23;
			protected override PlayerListData createSinglePlayer(CreatePlayerData createData)
			{
				if(!_g23)
				{
					_m23=instance.Type.GetMethod("createSinglePlayer",1);
					_g23=true;
				}
				
				if(_m23!=null && !_b23)
				{
					_b23=true;
					_p1[0]=createData;
					PlayerListData re=(PlayerListData)appdomain.Invoke(_m23,instance,_p1);
					_p1[0]=null;
					_b23=false;
					return re;
					
				}
				else
				{
					return base.createSinglePlayer(createData);
				}
			}
			
			IMethod _m24;
			bool _g24;
			bool _b24;
			public override void reBuildPlayer(bool isFull)
			{
				if(!_g24)
				{
					_m24=instance.Type.GetMethod("reBuildPlayer",1);
					_g24=true;
				}
				
				if(_m24!=null && !_b24)
				{
					_b24=true;
					_p1[0]=isFull;
					appdomain.Invoke(_m24,instance,_p1);
					_p1[0]=null;
					_b24=false;
					
				}
				else
				{
					base.reBuildPlayer(isFull);
				}
			}
			
			IMethod _m25;
			bool _g25;
			bool _b25;
			protected override void stepPreGameStart()
			{
				if(!_g25)
				{
					_m25=instance.Type.GetMethod("stepPreGameStart",0);
					_g25=true;
				}
				
				if(_m25!=null && !_b25)
				{
					_b25=true;
					appdomain.Invoke(_m25,instance,null);
					_b25=false;
					
				}
				else
				{
					base.stepPreGameStart();
				}
			}
			
			IMethod _m26;
			bool _g26;
			bool _b26;
			protected override void stepGameStart()
			{
				if(!_g26)
				{
					_m26=instance.Type.GetMethod("stepGameStart",0);
					_g26=true;
				}
				
				if(_m26!=null && !_b26)
				{
					_b26=true;
					appdomain.Invoke(_m26,instance,null);
					_b26=false;
					
				}
				else
				{
					base.stepGameStart();
				}
			}
			
			IMethod _m27;
			bool _g27;
			bool _b27;
			public override void reloadConfig()
			{
				if(!_g27)
				{
					_m27=instance.Type.GetMethod("reloadConfig",0);
					_g27=true;
				}
				
				if(_m27!=null && !_b27)
				{
					_b27=true;
					appdomain.Invoke(_m27,instance,null);
					_b27=false;
					
				}
				else
				{
					base.reloadConfig();
				}
			}
			
			IMethod _m28;
			bool _g28;
			bool _b28;
			public override void onLoginHttpSuccess(ClientLoginResultData data)
			{
				if(!_g28)
				{
					_m28=instance.Type.GetMethod("onLoginHttpSuccess",1);
					_g28=true;
				}
				
				if(_m28!=null && !_b28)
				{
					_b28=true;
					_p1[0]=data;
					appdomain.Invoke(_m28,instance,_p1);
					_p1[0]=null;
					_b28=false;
					
				}
				else
				{
					base.onLoginHttpSuccess(data);
				}
			}
			
			IMethod _m29;
			bool _g29;
			bool _b29;
			public override void onLoginHttpFailed(int errorCode)
			{
				if(!_g29)
				{
					_m29=instance.Type.GetMethod("onLoginHttpFailed",1);
					_g29=true;
				}
				
				if(_m29!=null && !_b29)
				{
					_b29=true;
					_p1[0]=errorCode;
					appdomain.Invoke(_m29,instance,_p1);
					_p1[0]=null;
					_b29=false;
					
				}
				else
				{
					base.onLoginHttpFailed(errorCode);
				}
			}
			
			IMethod _m30;
			bool _g30;
			bool _b30;
			public override void onGameSocketClosed()
			{
				if(!_g30)
				{
					_m30=instance.Type.GetMethod("onGameSocketClosed",0);
					_g30=true;
				}
				
				if(_m30!=null && !_b30)
				{
					_b30=true;
					appdomain.Invoke(_m30,instance,null);
					_b30=false;
					
				}
				else
				{
					base.onGameSocketClosed();
				}
			}
			
			IMethod _m31;
			bool _g31;
			bool _b31;
			protected override void doGameSocketClose()
			{
				if(!_g31)
				{
					_m31=instance.Type.GetMethod("doGameSocketClose",0);
					_g31=true;
				}
				
				if(_m31!=null && !_b31)
				{
					_b31=true;
					appdomain.Invoke(_m31,instance,null);
					_b31=false;
					
				}
				else
				{
					base.doGameSocketClose();
				}
			}
			
			IMethod _m32;
			bool _g32;
			bool _b32;
			public override int getCurrentSystemLanguage()
			{
				if(!_g32)
				{
					_m32=instance.Type.GetMethod("getCurrentSystemLanguage",0);
					_g32=true;
				}
				
				if(_m32!=null && !_b32)
				{
					_b32=true;
					int re=(int)appdomain.Invoke(_m32,instance,null);
					_b32=false;
					return re;
					
				}
				else
				{
					return base.getCurrentSystemLanguage();
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
