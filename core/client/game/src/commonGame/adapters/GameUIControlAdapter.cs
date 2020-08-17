using System;
using System.Text;
using ILRuntime.CLR.Method;
using ILRuntime.Runtime.Enviorment;
using ILRuntime.Runtime.Intepreter;
using UnityEngine;
using AppDomain = ILRuntime.Runtime.Enviorment.AppDomain;
using ShineEngine;

	public class GameUIControlAdapter : CrossBindingAdaptor
	{
		public override Type BaseCLRType
		{
			get
			{
				return typeof(GameUIControl);
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

		public class Adaptor : GameUIControl, CrossBindingAdaptorType
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
			protected override void initUI()
			{
				if(!_g1)
				{
					_m1=instance.Type.GetMethod("initUI",0);
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
					base.initUI();
				}
			}
			
			IMethod _m2;
			bool _g2;
			bool _b2;
			public override void hideUIByRemoveScene()
			{
				if(!_g2)
				{
					_m2=instance.Type.GetMethod("hideUIByRemoveScene",0);
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
					base.hideUIByRemoveScene();
				}
			}
			
			IMethod _m3;
			bool _g3;
			bool _b3;
			public override void hideUIByRemoveSceneOver()
			{
				if(!_g3)
				{
					_m3=instance.Type.GetMethod("hideUIByRemoveSceneOver",0);
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
					base.hideUIByRemoveSceneOver();
				}
			}
			
			IMethod _m4;
			bool _g4;
			bool _b4;
			public override void showUIByEnterScene()
			{
				if(!_g4)
				{
					_m4=instance.Type.GetMethod("showUIByEnterScene",0);
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
					base.showUIByEnterScene();
				}
			}
			
			IMethod _m5;
			bool _g5;
			bool _b5;
			public override void showUIByEnterSceneOver()
			{
				if(!_g5)
				{
					_m5=instance.Type.GetMethod("showUIByEnterSceneOver",0);
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
					base.showUIByEnterSceneOver();
				}
			}
			
			IMethod _m6;
			bool _g6;
			bool _b6;
			public override void showUIByLeaveSceneOver()
			{
				if(!_g6)
				{
					_m6=instance.Type.GetMethod("showUIByLeaveSceneOver",0);
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
					base.showUIByLeaveSceneOver();
				}
			}
			
			IMethod _m7;
			bool _g7;
			bool _b7;
			public override bool checkShowUIConditions(UIConfig config)
			{
				if(!_g7)
				{
					_m7=instance.Type.GetMethod("checkShowUIConditions",1);
					_g7=true;
				}
				
				if(_m7!=null && !_b7)
				{
					_b7=true;
					_p1[0]=config;
					bool re=(bool)appdomain.Invoke(_m7,instance,_p1);
					_p1[0]=null;
					_b7=false;
					return re;
					
				}
				else
				{
					return base.checkShowUIConditions(config);
				}
			}
			
			IMethod _m8;
			bool _g8;
			bool _b8;
			public override void alert(string msg,Action sureCall)
			{
				if(!_g8)
				{
					_m8=instance.Type.GetMethod("alert",2);
					_g8=true;
				}
				
				if(_m8!=null && !_b8)
				{
					_b8=true;
					_p2[0]=msg;
					_p2[1]=sureCall;
					appdomain.Invoke(_m8,instance,_p2);
					_p2[0]=null;
					_p2[1]=null;
					_b8=false;
					
				}
				else
				{
					base.alert(msg,sureCall);
				}
			}
			
			IMethod _m9;
			bool _g9;
			bool _b9;
			public override void notice(string msg)
			{
				if(!_g9)
				{
					_m9=instance.Type.GetMethod("notice",1);
					_g9=true;
				}
				
				if(_m9!=null && !_b9)
				{
					_b9=true;
					_p1[0]=msg;
					appdomain.Invoke(_m9,instance,_p1);
					_p1[0]=null;
					_b9=false;
					
				}
				else
				{
					base.notice(msg);
				}
			}
			
			IMethod _m10;
			bool _g10;
			bool _b10;
			public override void showNetDelay(bool isShow)
			{
				if(!_g10)
				{
					_m10=instance.Type.GetMethod("showNetDelay",1);
					_g10=true;
				}
				
				if(_m10!=null && !_b10)
				{
					_b10=true;
					_p1[0]=isShow;
					appdomain.Invoke(_m10,instance,_p1);
					_p1[0]=null;
					_b10=false;
					
				}
				else
				{
					base.showNetDelay(isShow);
				}
			}
			
			IMethod _m11;
			bool _g11;
			bool _b11;
			public override void showText(string str,int type)
			{
				if(!_g11)
				{
					_m11=instance.Type.GetMethod("showText",2);
					_g11=true;
				}
				
				if(_m11!=null && !_b11)
				{
					_b11=true;
					_p2[0]=str;
					_p2[1]=type;
					appdomain.Invoke(_m11,instance,_p2);
					_p2[0]=null;
					_p2[1]=null;
					_b11=false;
					
				}
				else
				{
					base.showText(str,type);
				}
			}
			
			IMethod _m12;
			bool _g12;
			bool _b12;
			protected override void executeShowUIAction(int type,int id,object[] args)
			{
				if(!_g12)
				{
					_m12=instance.Type.GetMethod("executeShowUIAction",3);
					_g12=true;
				}
				
				if(_m12!=null && !_b12)
				{
					_b12=true;
					_p3[0]=type;
					_p3[1]=id;
					_p3[2]=args;
					appdomain.Invoke(_m12,instance,_p3);
					_p3[0]=null;
					_p3[1]=null;
					_p3[2]=null;
					_b12=false;
					
				}
				else
				{
					base.executeShowUIAction(type,id,args);
				}
			}
			
			IMethod _m13;
			bool _g13;
			bool _b13;
			public override void toShowUIByTypeArgs(GameUIBase ui,object[] args)
			{
				if(!_g13)
				{
					_m13=instance.Type.GetMethod("toShowUIByTypeArgs",2);
					_g13=true;
				}
				
				if(_m13!=null && !_b13)
				{
					_b13=true;
					_p2[0]=ui;
					_p2[1]=args;
					appdomain.Invoke(_m13,instance,_p2);
					_p2[0]=null;
					_p2[1]=null;
					_b13=false;
					
				}
				else
				{
					base.toShowUIByTypeArgs(ui,args);
				}
			}
			
			IMethod _m14;
			bool _g14;
			bool _b14;
			public override void onUIShow(GameUIBase ui)
			{
				if(!_g14)
				{
					_m14=instance.Type.GetMethod("onUIShow",1);
					_g14=true;
				}
				
				if(_m14!=null && !_b14)
				{
					_b14=true;
					_p1[0]=ui;
					appdomain.Invoke(_m14,instance,_p1);
					_p1[0]=null;
					_b14=false;
					
				}
				else
				{
					base.onUIShow(ui);
				}
			}
			
			IMethod _m15;
			bool _g15;
			bool _b15;
			public override void onUIHide(GameUIBase ui)
			{
				if(!_g15)
				{
					_m15=instance.Type.GetMethod("onUIHide",1);
					_g15=true;
				}
				
				if(_m15!=null && !_b15)
				{
					_b15=true;
					_p1[0]=ui;
					appdomain.Invoke(_m15,instance,_p1);
					_p1[0]=null;
					_b15=false;
					
				}
				else
				{
					base.onUIHide(ui);
				}
			}
			
			IMethod _m16;
			bool _g16;
			bool _b16;
			public override void repoterAction(int actionType)
			{
				if(!_g16)
				{
					_m16=instance.Type.GetMethod("repoterAction",1);
					_g16=true;
				}
				
				if(_m16!=null && !_b16)
				{
					_b16=true;
					_p1[0]=actionType;
					appdomain.Invoke(_m16,instance,_p1);
					_p1[0]=null;
					_b16=false;
					
				}
				else
				{
					base.repoterAction(actionType);
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
