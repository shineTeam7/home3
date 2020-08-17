using System;
using System.Text;
using ILRuntime.CLR.Method;
using ILRuntime.Runtime.Enviorment;
using ILRuntime.Runtime.Intepreter;
using UnityEngine;
using AppDomain = ILRuntime.Runtime.Enviorment.AppDomain;
using ShineEngine;

	public class ClientSimpleSceneAdapter : CrossBindingAdaptor
	{
		public override Type BaseCLRType
		{
			get
			{
				return typeof(ClientSimpleScene);
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

		public class Adaptor : ClientSimpleScene, CrossBindingAdaptorType
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
			public override void initSceneID(int id)
			{
				if(!_g0)
				{
					_m0=instance.Type.GetMethod("initSceneID",1);
					_g0=true;
				}
				
				if(_m0!=null && !_b0)
				{
					_b0=true;
					_p1[0]=id;
					appdomain.Invoke(_m0,instance,_p1);
					_p1[0]=null;
					_b0=false;
					
				}
				else
				{
					base.initSceneID(id);
				}
			}
			
			IMethod _m1;
			bool _g1;
			bool _b1;
			protected override void registLogics()
			{
				if(!_g1)
				{
					_m1=instance.Type.GetMethod("registLogics",0);
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
					base.registLogics();
				}
			}
			
			IMethod _m2;
			bool _g2;
			bool _b2;
			protected override void preInit()
			{
				if(!_g2)
				{
					_m2=instance.Type.GetMethod("preInit",0);
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
					base.preInit();
				}
			}
			
			IMethod _m3;
			bool _g3;
			bool _b3;
			protected override SceneUnitFactoryLogic createUnitFactoryLogic()
			{
				if(!_g3)
				{
					_m3=instance.Type.GetMethod("createUnitFactoryLogic",0);
					_g3=true;
				}
				
				if(_m3!=null && !_b3)
				{
					_b3=true;
					SceneUnitFactoryLogic re=(SceneUnitFactoryLogic)appdomain.Invoke(_m3,instance,null);
					_b3=false;
					return re;
					
				}
				else
				{
					return base.createUnitFactoryLogic();
				}
			}
			
			IMethod _m4;
			bool _g4;
			bool _b4;
			protected override SceneInOutLogic createInOutLogic()
			{
				if(!_g4)
				{
					_m4=instance.Type.GetMethod("createInOutLogic",0);
					_g4=true;
				}
				
				if(_m4!=null && !_b4)
				{
					_b4=true;
					SceneInOutLogic re=(SceneInOutLogic)appdomain.Invoke(_m4,instance,null);
					_b4=false;
					return re;
					
				}
				else
				{
					return base.createInOutLogic();
				}
			}
			
			IMethod _m5;
			bool _g5;
			bool _b5;
			protected override SceneRoleLogic createRoleLogic()
			{
				if(!_g5)
				{
					_m5=instance.Type.GetMethod("createRoleLogic",0);
					_g5=true;
				}
				
				if(_m5!=null && !_b5)
				{
					_b5=true;
					SceneRoleLogic re=(SceneRoleLogic)appdomain.Invoke(_m5,instance,null);
					_b5=false;
					return re;
					
				}
				else
				{
					return base.createRoleLogic();
				}
			}
			
			IMethod _m6;
			bool _g6;
			bool _b6;
			protected override ScenePosLogic createPosLogic()
			{
				if(!_g6)
				{
					_m6=instance.Type.GetMethod("createPosLogic",0);
					_g6=true;
				}
				
				if(_m6!=null && !_b6)
				{
					_b6=true;
					ScenePosLogic re=(ScenePosLogic)appdomain.Invoke(_m6,instance,null);
					_b6=false;
					return re;
					
				}
				else
				{
					return base.createPosLogic();
				}
			}
			
			IMethod _m7;
			bool _g7;
			bool _b7;
			protected override SceneShowLogic createShowLogic()
			{
				if(!_g7)
				{
					_m7=instance.Type.GetMethod("createShowLogic",0);
					_g7=true;
				}
				
				if(_m7!=null && !_b7)
				{
					_b7=true;
					SceneShowLogic re=(SceneShowLogic)appdomain.Invoke(_m7,instance,null);
					_b7=false;
					return re;
					
				}
				else
				{
					return base.createShowLogic();
				}
			}
			
			IMethod _m8;
			bool _g8;
			bool _b8;
			protected override SceneLoadLogic createLoadLogic()
			{
				if(!_g8)
				{
					_m8=instance.Type.GetMethod("createLoadLogic",0);
					_g8=true;
				}
				
				if(_m8!=null && !_b8)
				{
					_b8=true;
					SceneLoadLogic re=(SceneLoadLogic)appdomain.Invoke(_m8,instance,null);
					_b8=false;
					return re;
					
				}
				else
				{
					return base.createLoadLogic();
				}
			}
			
			IMethod _m9;
			bool _g9;
			bool _b9;
			protected override SceneFightLogic createFightLogic()
			{
				if(!_g9)
				{
					_m9=instance.Type.GetMethod("createFightLogic",0);
					_g9=true;
				}
				
				if(_m9!=null && !_b9)
				{
					_b9=true;
					SceneFightLogic re=(SceneFightLogic)appdomain.Invoke(_m9,instance,null);
					_b9=false;
					return re;
					
				}
				else
				{
					return base.createFightLogic();
				}
			}
			
			IMethod _m10;
			bool _g10;
			bool _b10;
			protected override SceneCameraLogic createCameraLogic()
			{
				if(!_g10)
				{
					_m10=instance.Type.GetMethod("createCameraLogic",0);
					_g10=true;
				}
				
				if(_m10!=null && !_b10)
				{
					_b10=true;
					SceneCameraLogic re=(SceneCameraLogic)appdomain.Invoke(_m10,instance,null);
					_b10=false;
					return re;
					
				}
				else
				{
					return base.createCameraLogic();
				}
			}
			
			IMethod _m11;
			bool _g11;
			bool _b11;
			protected override SceneMethodLogic createMethodLogic()
			{
				if(!_g11)
				{
					_m11=instance.Type.GetMethod("createPlayLogic",0);
					_g11=true;
				}
				
				if(_m11!=null && !_b11)
				{
					_b11=true;
					SceneMethodLogic re=(SceneMethodLogic)appdomain.Invoke(_m11,instance,null);
					_b11=false;
					return re;
					
				}
				else
				{
					return base.createMethodLogic();
				}
			}
			
			IMethod _m12;
			bool _g12;
			bool _b12;
			protected override void onStart()
			{
				if(!_g12)
				{
					_m12=instance.Type.GetMethod("onStart",0);
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
					base.onStart();
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
