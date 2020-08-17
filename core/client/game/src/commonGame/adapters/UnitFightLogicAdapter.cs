using System;
using System.Text;
using ILRuntime.CLR.Method;
using ILRuntime.Runtime.Enviorment;
using ILRuntime.Runtime.Intepreter;
using UnityEngine;
using AppDomain = ILRuntime.Runtime.Enviorment.AppDomain;
using ShineEngine;

	public class UnitFightLogicAdapter : CrossBindingAdaptor
	{
		public override Type BaseCLRType
		{
			get
			{
				return typeof(UnitFightLogic);
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

		public class Adaptor : UnitFightLogic, CrossBindingAdaptorType
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
			
			private object[] _p4=new object[4];
			
			private object[] _p3=new object[3];
			
			private object[] _p5=new object[5];
			
			

			IMethod _m0;
			bool _g0;
			bool _b0;
			public override void setObject(SceneObject obj)
			{
				if(!_g0)
				{
					_m0=instance.Type.GetMethod("setObject",1);
					_g0=true;
				}
				
				if(_m0!=null && !_b0)
				{
					_b0=true;
					_p1[0]=obj;
					appdomain.Invoke(_m0,instance,_p1);
					_p1[0]=null;
					_b0=false;
					
				}
				else
				{
					base.setObject(obj);
				}
			}
			
			IMethod _m1;
			bool _g1;
			bool _b1;
			public override void construct()
			{
				if(!_g1)
				{
					_m1=instance.Type.GetMethod("construct",0);
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
					base.construct();
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
			public override void afterInit()
			{
				if(!_g3)
				{
					_m3=instance.Type.GetMethod("afterInit",0);
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
					base.afterInit();
				}
			}
			
			IMethod _m4;
			bool _g4;
			bool _b4;
			public override void preRemove()
			{
				if(!_g4)
				{
					_m4=instance.Type.GetMethod("preRemove",0);
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
					base.preRemove();
				}
			}
			
			IMethod _m5;
			bool _g5;
			bool _b5;
			public override void dispose()
			{
				if(!_g5)
				{
					_m5=instance.Type.GetMethod("dispose",0);
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
					base.dispose();
				}
			}
			
			IMethod _m6;
			bool _g6;
			bool _b6;
			public override void onFrame(int delay)
			{
				if(!_g6)
				{
					_m6=instance.Type.GetMethod("onFrame",1);
					_g6=true;
				}
				
				if(_m6!=null && !_b6)
				{
					_b6=true;
					_p1[0]=delay;
					appdomain.Invoke(_m6,instance,_p1);
					_p1[0]=null;
					_b6=false;
					
				}
				else
				{
					base.onFrame(delay);
				}
			}
			
			IMethod _m7;
			bool _g7;
			bool _b7;
			public override void onSecond()
			{
				if(!_g7)
				{
					_m7=instance.Type.GetMethod("onSecond",0);
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
					base.onSecond();
				}
			}
			
			IMethod _m8;
			bool _g8;
			bool _b8;
			public override void onFixedUpdate()
			{
				if(!_g8)
				{
					_m8=instance.Type.GetMethod("onFixedUpdate",0);
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
					base.onFixedUpdate();
				}
			}
			
			IMethod _m9;
			bool _g9;
			bool _b9;
			public override void onReloadConfig()
			{
				if(!_g9)
				{
					_m9=instance.Type.GetMethod("onReloadConfig",0);
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
					base.onReloadConfig();
				}
			}
			
			IMethod _m10;
			bool _g10;
			bool _b10;
			public override void onPiece(int delay)
			{
				if(!_g10)
				{
					_m10=instance.Type.GetMethod("onPiece",1);
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
					base.onPiece(delay);
				}
			}
			
			IMethod _m11;
			bool _g11;
			bool _b11;
			public override SRect getSelfBox()
			{
				if(!_g11)
				{
					_m11=instance.Type.GetMethod("getSelfBox",0);
					_g11=true;
				}
				
				if(_m11!=null && !_b11)
				{
					_b11=true;
					SRect re=(SRect)appdomain.Invoke(_m11,instance,null);
					_b11=false;
					return re;
					
				}
				else
				{
					return base.getSelfBox();
				}
			}
			
			IMethod _m12;
			bool _g12;
			bool _b12;
			public override SRect getAttackBox(AttackLevelConfig config)
			{
				if(!_g12)
				{
					_m12=instance.Type.GetMethod("getAttackBox",1);
					_g12=true;
				}
				
				if(_m12!=null && !_b12)
				{
					_b12=true;
					_p1[0]=config;
					SRect re=(SRect)appdomain.Invoke(_m12,instance,_p1);
					_p1[0]=null;
					_b12=false;
					return re;
					
				}
				else
				{
					return base.getAttackBox(config);
				}
			}
			
			IMethod _m13;
			bool _g13;
			bool _b13;
			public override SRect getAttackAround2DBox(float radius,float height)
			{
				if(!_g13)
				{
					_m13=instance.Type.GetMethod("getAttackAround2DBox",2);
					_g13=true;
				}
				
				if(_m13!=null && !_b13)
				{
					_b13=true;
					_p2[0]=radius;
					_p2[1]=height;
					SRect re=(SRect)appdomain.Invoke(_m13,instance,_p2);
					_p2[0]=null;
					_p2[1]=null;
					_b13=false;
					return re;
					
				}
				else
				{
					return base.getAttackAround2DBox(radius,height);
				}
			}
			
			IMethod _m14;
			bool _g14;
			bool _b14;
			protected override bool checkOneSkillUseCondition(int[] args)
			{
				if(!_g14)
				{
					_m14=instance.Type.GetMethod("checkOneSkillUseCondition",1);
					_g14=true;
				}
				
				if(_m14!=null && !_b14)
				{
					_b14=true;
					_p1[0]=args;
					bool re=(bool)appdomain.Invoke(_m14,instance,_p1);
					_p1[0]=null;
					_b14=false;
					return re;
					
				}
				else
				{
					return base.checkOneSkillUseCondition(args);
				}
			}
			
			IMethod _m15;
			bool _g15;
			bool _b15;
			protected override bool checkOneSkillCostCondition(DIntData data)
			{
				if(!_g15)
				{
					_m15=instance.Type.GetMethod("checkOneSkillCostCondition",1);
					_g15=true;
				}
				
				if(_m15!=null && !_b15)
				{
					_b15=true;
					_p1[0]=data;
					bool re=(bool)appdomain.Invoke(_m15,instance,_p1);
					_p1[0]=null;
					_b15=false;
					return re;
					
				}
				else
				{
					return base.checkOneSkillCostCondition(data);
				}
			}
			
			IMethod _m16;
			bool _g16;
			bool _b16;
			protected override bool checkTargetInfluenceEx(Unit target,bool[] influences)
			{
				if(!_g16)
				{
					_m16=instance.Type.GetMethod("checkTargetInfluenceEx",2);
					_g16=true;
				}
				
				if(_m16!=null && !_b16)
				{
					_b16=true;
					_p2[0]=target;
					_p2[1]=influences;
					bool re=(bool)appdomain.Invoke(_m16,instance,_p2);
					_p2[0]=null;
					_p2[1]=null;
					_b16=false;
					return re;
					
				}
				else
				{
					return base.checkTargetInfluenceEx(target,influences);
				}
			}
			
			IMethod _m17;
			bool _g17;
			bool _b17;
			public override bool checkCanUseSkill(int skillID,int skillLevel)
			{
				if(!_g17)
				{
					_m17=instance.Type.GetMethod("checkCanUseSkill",2);
					_g17=true;
				}
				
				if(_m17!=null && !_b17)
				{
					_b17=true;
					_p2[0]=skillID;
					_p2[1]=skillLevel;
					bool re=(bool)appdomain.Invoke(_m17,instance,_p2);
					_p2[0]=null;
					_p2[1]=null;
					_b17=false;
					return re;
					
				}
				else
				{
					return base.checkCanUseSkill(skillID,skillLevel);
				}
			}
			
			IMethod _m18;
			bool _g18;
			bool _b18;
			protected override void costOneSkill(DIntData data)
			{
				if(!_g18)
				{
					_m18=instance.Type.GetMethod("costOneSkill",1);
					_g18=true;
				}
				
				if(_m18!=null && !_b18)
				{
					_b18=true;
					_p1[0]=data;
					appdomain.Invoke(_m18,instance,_p1);
					_p1[0]=null;
					_b18=false;
					
				}
				else
				{
					base.costOneSkill(data);
				}
			}
			
			IMethod _m19;
			bool _g19;
			bool _b19;
			protected override void skillOver()
			{
				if(!_g19)
				{
					_m19=instance.Type.GetMethod("skillOver",0);
					_g19=true;
				}
				
				if(_m19!=null && !_b19)
				{
					_b19=true;
					appdomain.Invoke(_m19,instance,null);
					_b19=false;
					
				}
				else
				{
					base.skillOver();
				}
			}
			
			IMethod _m20;
			bool _g20;
			bool _b20;
			protected override void onUseSkill()
			{
				if(!_g20)
				{
					_m20=instance.Type.GetMethod("onUseSkill",0);
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
					base.onUseSkill();
				}
			}
			
			IMethod _m21;
			bool _g21;
			bool _b21;
			protected override void onSkillOver()
			{
				if(!_g21)
				{
					_m21=instance.Type.GetMethod("onSkillOver",0);
					_g21=true;
				}
				
				if(_m21!=null && !_b21)
				{
					_b21=true;
					appdomain.Invoke(_m21,instance,null);
					_b21=false;
					
				}
				else
				{
					base.onSkillOver();
				}
			}
			
			IMethod _m22;
			bool _g22;
			bool _b22;
			protected override void onSkillBreak()
			{
				if(!_g22)
				{
					_m22=instance.Type.GetMethod("onSkillBreak",0);
					_g22=true;
				}
				
				if(_m22!=null && !_b22)
				{
					_b22=true;
					appdomain.Invoke(_m22,instance,null);
					_b22=false;
					
				}
				else
				{
					base.onSkillBreak();
				}
			}
			
			IMethod _m23;
			bool _g23;
			bool _b23;
			protected override void doOneSkillAction(int[] args,int off)
			{
				if(!_g23)
				{
					_m23=instance.Type.GetMethod("doOneSkillAction",2);
					_g23=true;
				}
				
				if(_m23!=null && !_b23)
				{
					_b23=true;
					_p2[0]=args;
					_p2[1]=off;
					appdomain.Invoke(_m23,instance,_p2);
					_p2[0]=null;
					_p2[1]=null;
					_b23=false;
					
				}
				else
				{
					base.doOneSkillAction(args,off);
				}
			}
			
			IMethod _m24;
			bool _g24;
			bool _b24;
			protected override void doAttack(int id,int level)
			{
				if(!_g24)
				{
					_m24=instance.Type.GetMethod("doAttack",2);
					_g24=true;
				}
				
				if(_m24!=null && !_b24)
				{
					_b24=true;
					_p2[0]=id;
					_p2[1]=level;
					appdomain.Invoke(_m24,instance,_p2);
					_p2[0]=null;
					_p2[1]=null;
					_b24=false;
					
				}
				else
				{
					base.doAttack(id,level);
				}
			}
			
			IMethod _m25;
			bool _g25;
			bool _b25;
			public override void onStatusChange(bool[] changeSet)
			{
				if(!_g25)
				{
					_m25=instance.Type.GetMethod("onStatusChange",1);
					_g25=true;
				}
				
				if(_m25!=null && !_b25)
				{
					_b25=true;
					_p1[0]=changeSet;
					appdomain.Invoke(_m25,instance,_p1);
					_p1[0]=null;
					_b25=false;
					
				}
				else
				{
					base.onStatusChange(changeSet);
				}
			}
			
			IMethod _m26;
			bool _g26;
			bool _b26;
			public override void onAttributeChange(int[] changeList,int num,bool[] changeSet,int[] lastAttributes)
			{
				if(!_g26)
				{
					_m26=instance.Type.GetMethod("onAttributeChange",4);
					_g26=true;
				}
				
				if(_m26!=null && !_b26)
				{
					_b26=true;
					_p4[0]=changeList;
					_p4[1]=num;
					_p4[2]=changeSet;
					_p4[3]=lastAttributes;
					appdomain.Invoke(_m26,instance,_p4);
					_p4[0]=null;
					_p4[1]=null;
					_p4[2]=null;
					_p4[3]=null;
					_b26=false;
					
				}
				else
				{
					base.onAttributeChange(changeList,num,changeSet,lastAttributes);
				}
			}
			
			IMethod _m27;
			bool _g27;
			bool _b27;
			public override void onStartGroupCD(int groupID)
			{
				if(!_g27)
				{
					_m27=instance.Type.GetMethod("onStartGroupCD",1);
					_g27=true;
				}
				
				if(_m27!=null && !_b27)
				{
					_b27=true;
					_p1[0]=groupID;
					appdomain.Invoke(_m27,instance,_p1);
					_p1[0]=null;
					_b27=false;
					
				}
				else
				{
					base.onStartGroupCD(groupID);
				}
			}
			
			IMethod _m28;
			bool _g28;
			bool _b28;
			public override void onEndGroupCD(int groupID)
			{
				if(!_g28)
				{
					_m28=instance.Type.GetMethod("onEndGroupCD",1);
					_g28=true;
				}
				
				if(_m28!=null && !_b28)
				{
					_b28=true;
					_p1[0]=groupID;
					appdomain.Invoke(_m28,instance,_p1);
					_p1[0]=null;
					_b28=false;
					
				}
				else
				{
					base.onEndGroupCD(groupID);
				}
			}
			
			IMethod _m29;
			bool _g29;
			bool _b29;
			public override void onDead(Unit attacker,int type,bool isReal)
			{
				if(!_g29)
				{
					_m29=instance.Type.GetMethod("onDead",3);
					_g29=true;
				}
				
				if(_m29!=null && !_b29)
				{
					_b29=true;
					_p3[0]=attacker;
					_p3[1]=type;
					_p3[2]=isReal;
					appdomain.Invoke(_m29,instance,_p3);
					_p3[0]=null;
					_p3[1]=null;
					_p3[2]=null;
					_b29=false;
					
				}
				else
				{
					base.onDead(attacker,type,isReal);
				}
			}
			
			IMethod _m30;
			bool _g30;
			bool _b30;
			protected override void onRealDead(Unit attacker)
			{
				if(!_g30)
				{
					_m30=instance.Type.GetMethod("onRealDead",1);
					_g30=true;
				}
				
				if(_m30!=null && !_b30)
				{
					_b30=true;
					_p1[0]=attacker;
					appdomain.Invoke(_m30,instance,_p1);
					_p1[0]=null;
					_b30=false;
					
				}
				else
				{
					base.onRealDead(attacker);
				}
			}
			
			IMethod _m31;
			bool _g31;
			bool _b31;
			protected override void onDeadOver()
			{
				if(!_g31)
				{
					_m31=instance.Type.GetMethod("onDeadOver",0);
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
					base.onDeadOver();
				}
			}
			
			IMethod _m32;
			bool _g32;
			bool _b32;
			public override void onRevive()
			{
				if(!_g32)
				{
					_m32=instance.Type.GetMethod("onRevive",0);
					_g32=true;
				}
				
				if(_m32!=null && !_b32)
				{
					_b32=true;
					appdomain.Invoke(_m32,instance,null);
					_b32=false;
					
				}
				else
				{
					base.onRevive();
				}
			}
			
			IMethod _m33;
			bool _g33;
			bool _b33;
			protected override void onShowSkillBar()
			{
				if(!_g33)
				{
					_m33=instance.Type.GetMethod("onShowSkillBar",0);
					_g33=true;
				}
				
				if(_m33!=null && !_b33)
				{
					_b33=true;
					appdomain.Invoke(_m33,instance,null);
					_b33=false;
					
				}
				else
				{
					base.onShowSkillBar();
				}
			}
			
			IMethod _m34;
			bool _g34;
			bool _b34;
			protected override void onHideSkillBar()
			{
				if(!_g34)
				{
					_m34=instance.Type.GetMethod("onHideSkillBar",0);
					_g34=true;
				}
				
				if(_m34!=null && !_b34)
				{
					_b34=true;
					appdomain.Invoke(_m34,instance,null);
					_b34=false;
					
				}
				else
				{
					base.onHideSkillBar();
				}
			}
			
			IMethod _m35;
			bool _g35;
			bool _b35;
			public override void sendCUnitAttack(int id,int level,SkillTargetData tData,SList<Unit> targets,bool isBulletFirstHit)
			{
				if(!_g35)
				{
					_m35=instance.Type.GetMethod("sendCUnitAttack",5);
					_g35=true;
				}
				
				if(_m35!=null && !_b35)
				{
					_b35=true;
					_p5[0]=id;
					_p5[1]=level;
					_p5[2]=tData;
					_p5[3]=targets;
					_p5[4]=isBulletFirstHit;
					appdomain.Invoke(_m35,instance,_p5);
					_p5[0]=null;
					_p5[1]=null;
					_p5[2]=null;
					_p5[3]=null;
					_p5[4]=null;
					_b35=false;
					
				}
				else
				{
					base.sendCUnitAttack(id,level,tData,targets,isBulletFirstHit);
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
