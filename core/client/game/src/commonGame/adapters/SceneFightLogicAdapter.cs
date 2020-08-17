using System;
using System.Text;
using ILRuntime.CLR.Method;
using ILRuntime.Runtime.Enviorment;
using ILRuntime.Runtime.Intepreter;
using UnityEngine;
using AppDomain = ILRuntime.Runtime.Enviorment.AppDomain;
using ShineEngine;

	public class SceneFightLogicAdapter : CrossBindingAdaptor
	{
		public override Type BaseCLRType
		{
			get
			{
				return typeof(SceneFightLogic);
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

		public class Adaptor : SceneFightLogic, CrossBindingAdaptorType
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
			
			private object[] _p3=new object[3];
			
			private object[] _p4=new object[4];
			
			private object[] _p7=new object[7];
			
			private object[] _p6=new object[6];
			
			

			IMethod _m0;
			bool _g0;
			bool _b0;
			public override void construct()
			{
				if(!_g0)
				{
					_m0=instance.Type.GetMethod("construct",0);
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
					base.construct();
				}
			}
			
			IMethod _m1;
			bool _g1;
			bool _b1;
			public override void onSceneLoad()
			{
				if(!_g1)
				{
					_m1=instance.Type.GetMethod("onSceneLoad",0);
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
					base.onSceneLoad();
				}
			}
			
			IMethod _m2;
			bool _g2;
			bool _b2;
			public override void init()
			{
				if(!_g2)
				{
					_m2=instance.Type.GetMethod("init",0);
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
					base.init();
				}
			}
			
			IMethod _m3;
			bool _g3;
			bool _b3;
			public override void dispose()
			{
				if(!_g3)
				{
					_m3=instance.Type.GetMethod("dispose",0);
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
					base.dispose();
				}
			}
			
			IMethod _m4;
			bool _g4;
			bool _b4;
			public override void onFrame(int delay)
			{
				if(!_g4)
				{
					_m4=instance.Type.GetMethod("onFrame",1);
					_g4=true;
				}
				
				if(_m4!=null && !_b4)
				{
					_b4=true;
					_p1[0]=delay;
					appdomain.Invoke(_m4,instance,_p1);
					_p1[0]=null;
					_b4=false;
					
				}
				else
				{
					base.onFrame(delay);
				}
			}
			
			IMethod _m5;
			bool _g5;
			bool _b5;
			public override void onFixedUpdate()
			{
				if(!_g5)
				{
					_m5=instance.Type.GetMethod("onFixedUpdate",0);
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
					base.onFixedUpdate();
				}
			}
			
			IMethod _m6;
			bool _g6;
			bool _b6;
			public override void onSecond()
			{
				if(!_g6)
				{
					_m6=instance.Type.GetMethod("onSecond",0);
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
					base.onSecond();
				}
			}
			
			IMethod _m7;
			bool _g7;
			bool _b7;
			public override void onAddUnit(Unit unit)
			{
				if(!_g7)
				{
					_m7=instance.Type.GetMethod("onAddUnit",1);
					_g7=true;
				}
				
				if(_m7!=null && !_b7)
				{
					_b7=true;
					_p1[0]=unit;
					appdomain.Invoke(_m7,instance,_p1);
					_p1[0]=null;
					_b7=false;
					
				}
				else
				{
					base.onAddUnit(unit);
				}
			}
			
			IMethod _m8;
			bool _g8;
			bool _b8;
			public override void onRemoveUnit(Unit unit)
			{
				if(!_g8)
				{
					_m8=instance.Type.GetMethod("onRemoveUnit",1);
					_g8=true;
				}
				
				if(_m8!=null && !_b8)
				{
					_b8=true;
					_p1[0]=unit;
					appdomain.Invoke(_m8,instance,_p1);
					_p1[0]=null;
					_b8=false;
					
				}
				else
				{
					base.onRemoveUnit(unit);
				}
			}
			
			IMethod _m9;
			bool _g9;
			bool _b9;
			public override void afterHero()
			{
				if(!_g9)
				{
					_m9=instance.Type.GetMethod("afterHero",0);
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
					base.afterHero();
				}
			}
			
			IMethod _m10;
			bool _g10;
			bool _b10;
			public override void preRemove()
			{
				if(!_g10)
				{
					_m10=instance.Type.GetMethod("preRemove",0);
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
					base.preRemove();
				}
			}
			
			IMethod _m11;
			bool _g11;
			bool _b11;
			protected override float toCalculateBaseDamage(int[] damages,int attackValue,int defenceValue)
			{
				if(!_g11)
				{
					_m11=instance.Type.GetMethod("toCalculateBaseDamage",3);
					_g11=true;
				}
				
				if(_m11!=null && !_b11)
				{
					_b11=true;
					_p3[0]=damages;
					_p3[1]=attackValue;
					_p3[2]=defenceValue;
					float re=(float)appdomain.Invoke(_m11,instance,_p3);
					_p3[0]=null;
					_p3[1]=null;
					_p3[2]=null;
					_b11=false;
					return re;
					
				}
				else
				{
					return base.toCalculateBaseDamage(damages,attackValue,defenceValue);
				}
			}
			
			IMethod _m12;
			bool _g12;
			bool _b12;
			public override int takeDamage(int damageType,int damageValue,AttributeDataLogic targetAttribute,Unit target)
			{
				if(!_g12)
				{
					_m12=instance.Type.GetMethod("takeDamage",4);
					_g12=true;
				}
				
				if(_m12!=null && !_b12)
				{
					_b12=true;
					_p4[0]=damageType;
					_p4[1]=damageValue;
					_p4[2]=targetAttribute;
					_p4[3]=target;
					int re=(int)appdomain.Invoke(_m12,instance,_p4);
					_p4[0]=null;
					_p4[1]=null;
					_p4[2]=null;
					_p4[3]=null;
					_b12=false;
					return re;
					
				}
				else
				{
					return base.takeDamage(damageType,damageValue,targetAttribute,target);
				}
			}
			
			IMethod _m13;
			bool _g13;
			bool _b13;
			protected override void toMakeTargets(SList<Unit> list,Unit from,Unit attacker,Unit mTarget,SkillTargetData tData,AttackConfig config,AttackLevelConfig levelConfig)
			{
				if(!_g13)
				{
					_m13=instance.Type.GetMethod("toMakeTargets",7);
					_g13=true;
				}
				
				if(_m13!=null && !_b13)
				{
					_b13=true;
					_p7[0]=list;
					_p7[1]=from;
					_p7[2]=attacker;
					_p7[3]=mTarget;
					_p7[4]=tData;
					_p7[5]=config;
					_p7[6]=levelConfig;
					appdomain.Invoke(_m13,instance,_p7);
					_p7[0]=null;
					_p7[1]=null;
					_p7[2]=null;
					_p7[3]=null;
					_p7[4]=null;
					_p7[5]=null;
					_p7[6]=null;
					_b13=false;
					
				}
				else
				{
					base.toMakeTargets(list,from,attacker,mTarget,tData,config,levelConfig);
				}
			}
			
			IMethod _m14;
			bool _g14;
			bool _b14;
			protected override void doAttackMoment(int moment,Unit from,Unit attacker,Unit target,AttackData lastData,DamageOneData damageOneData)
			{
				if(!_g14)
				{
					_m14=instance.Type.GetMethod("doAttackMoment",6);
					_g14=true;
				}
				
				if(_m14!=null && !_b14)
				{
					_b14=true;
					_p6[0]=moment;
					_p6[1]=from;
					_p6[2]=attacker;
					_p6[3]=target;
					_p6[4]=lastData;
					_p6[5]=damageOneData;
					appdomain.Invoke(_m14,instance,_p6);
					_p6[0]=null;
					_p6[1]=null;
					_p6[2]=null;
					_p6[3]=null;
					_p6[4]=null;
					_p6[5]=null;
					_b14=false;
					
				}
				else
				{
					base.doAttackMoment(moment,from,attacker,target,lastData,damageOneData);
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
