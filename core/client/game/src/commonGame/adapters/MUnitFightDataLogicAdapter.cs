using System;
using System.Text;
using ILRuntime.CLR.Method;
using ILRuntime.Runtime.Enviorment;
using ILRuntime.Runtime.Intepreter;
using UnityEngine;
using AppDomain = ILRuntime.Runtime.Enviorment.AppDomain;
using ShineEngine;

	public class MUnitFightDataLogicAdapter : CrossBindingAdaptor
	{
		public override Type BaseCLRType
		{
			get
			{
				return typeof(MUnitFightDataLogic);
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

		public class Adaptor : MUnitFightDataLogic, CrossBindingAdaptorType
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
			public override bool isDriveAll()
			{
				if(!_g1)
				{
					_m1=instance.Type.GetMethod("isDriveAll",0);
					_g1=true;
				}
				
				if(_m1!=null && !_b1)
				{
					_b1=true;
					bool re=(bool)appdomain.Invoke(_m1,instance,null);
					_b1=false;
					return re;
					
				}
				else
				{
					return base.isDriveAll();
				}
			}
			
			IMethod _m2;
			bool _g2;
			bool _b2;
			public override bool isSelfDriveAttackHapen()
			{
				if(!_g2)
				{
					_m2=instance.Type.GetMethod("isSelfDriveAttackHapen",0);
					_g2=true;
				}
				
				if(_m2!=null && !_b2)
				{
					_b2=true;
					bool re=(bool)appdomain.Invoke(_m2,instance,null);
					_b2=false;
					return re;
					
				}
				else
				{
					return base.isSelfDriveAttackHapen();
				}
			}
			
			IMethod _m3;
			bool _g3;
			bool _b3;
			public override void doBuffIntervalAttack(BuffIntervalActionData data)
			{
				if(!_g3)
				{
					_m3=instance.Type.GetMethod("doBuffIntervalAttack",1);
					_g3=true;
				}
				
				if(_m3!=null && !_b3)
				{
					_b3=true;
					_p1[0]=data;
					appdomain.Invoke(_m3,instance,_p1);
					_p1[0]=null;
					_b3=false;
					
				}
				else
				{
					base.doBuffIntervalAttack(data);
				}
			}
			
			IMethod _m4;
			bool _g4;
			bool _b4;
			public override int getSkillVarValueT(int varID,int adderID)
			{
				if(!_g4)
				{
					_m4=instance.Type.GetMethod("getSkillVarValueT",2);
					_g4=true;
				}
				
				if(_m4!=null && !_b4)
				{
					_b4=true;
					_p2[0]=varID;
					_p2[1]=adderID;
					int re=(int)appdomain.Invoke(_m4,instance,_p2);
					_p2[0]=null;
					_p2[1]=null;
					_b4=false;
					return re;
					
				}
				else
				{
					return base.getSkillVarValueT(varID,adderID);
				}
			}
			
			IMethod _m5;
			bool _g5;
			bool _b5;
			public override int getSkillVarSourceValue(int[] args,bool isSelf)
			{
				if(!_g5)
				{
					_m5=instance.Type.GetMethod("getSkillVarSourceValue",2);
					_g5=true;
				}
				
				if(_m5!=null && !_b5)
				{
					_b5=true;
					_p2[0]=args;
					_p2[1]=isSelf;
					int re=(int)appdomain.Invoke(_m5,instance,_p2);
					_p2[0]=null;
					_p2[1]=null;
					_b5=false;
					return re;
					
				}
				else
				{
					return base.getSkillVarSourceValue(args,isSelf);
				}
			}
			
			IMethod _m6;
			bool _g6;
			bool _b6;
			public override void onStatusChange(bool[] changeSet)
			{
				if(!_g6)
				{
					_m6=instance.Type.GetMethod("onStatusChange",1);
					_g6=true;
				}
				
				if(_m6!=null && !_b6)
				{
					_b6=true;
					_p1[0]=changeSet;
					appdomain.Invoke(_m6,instance,_p1);
					_p1[0]=null;
					_b6=false;
					
				}
				else
				{
					base.onStatusChange(changeSet);
				}
			}
			
			IMethod _m7;
			bool _g7;
			bool _b7;
			public override void onAttributeChange(int[] changeList,int num,bool[] changeSet,int[] lastAttributes)
			{
				if(!_g7)
				{
					_m7=instance.Type.GetMethod("onAttributeChange",4);
					_g7=true;
				}
				
				if(_m7!=null && !_b7)
				{
					_b7=true;
					_p4[0]=changeList;
					_p4[1]=num;
					_p4[2]=changeSet;
					_p4[3]=lastAttributes;
					appdomain.Invoke(_m7,instance,_p4);
					_p4[0]=null;
					_p4[1]=null;
					_p4[2]=null;
					_p4[3]=null;
					_b7=false;
					
				}
				else
				{
					base.onAttributeChange(changeList,num,changeSet,lastAttributes);
				}
			}
			
			IMethod _m8;
			bool _g8;
			bool _b8;
			public override void onCDChange()
			{
				if(!_g8)
				{
					_m8=instance.Type.GetMethod("onCDChange",0);
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
					base.onCDChange();
				}
			}
			
			IMethod _m9;
			bool _g9;
			bool _b9;
			public override void onAddBuff(BuffData data)
			{
				if(!_g9)
				{
					_m9=instance.Type.GetMethod("onAddBuff",1);
					_g9=true;
				}
				
				if(_m9!=null && !_b9)
				{
					_b9=true;
					_p1[0]=data;
					appdomain.Invoke(_m9,instance,_p1);
					_p1[0]=null;
					_b9=false;
					
				}
				else
				{
					base.onAddBuff(data);
				}
			}
			
			IMethod _m10;
			bool _g10;
			bool _b10;
			public override void onRemoveBuff(BuffData data)
			{
				if(!_g10)
				{
					_m10=instance.Type.GetMethod("onRemoveBuff",1);
					_g10=true;
				}
				
				if(_m10!=null && !_b10)
				{
					_b10=true;
					_p1[0]=data;
					appdomain.Invoke(_m10,instance,_p1);
					_p1[0]=null;
					_b10=false;
					
				}
				else
				{
					base.onRemoveBuff(data);
				}
			}
			
			IMethod _m11;
			bool _g11;
			bool _b11;
			public override void onRefreshBuff(BuffData data)
			{
				if(!_g11)
				{
					_m11=instance.Type.GetMethod("onRefreshBuff",1);
					_g11=true;
				}
				
				if(_m11!=null && !_b11)
				{
					_b11=true;
					_p1[0]=data;
					appdomain.Invoke(_m11,instance,_p1);
					_p1[0]=null;
					_b11=false;
					
				}
				else
				{
					base.onRefreshBuff(data);
				}
			}
			
			IMethod _m12;
			bool _g12;
			bool _b12;
			public override void onAvatarChange(int modelID)
			{
				if(!_g12)
				{
					_m12=instance.Type.GetMethod("onAvatarChange",1);
					_g12=true;
				}
				
				if(_m12!=null && !_b12)
				{
					_b12=true;
					_p1[0]=modelID;
					appdomain.Invoke(_m12,instance,_p1);
					_p1[0]=null;
					_b12=false;
					
				}
				else
				{
					base.onAvatarChange(modelID);
				}
			}
			
			IMethod _m13;
			bool _g13;
			bool _b13;
			public override void onAvatarPartChange(IntIntMap dic)
			{
				if(!_g13)
				{
					_m13=instance.Type.GetMethod("onAvatarPartChange",1);
					_g13=true;
				}
				
				if(_m13!=null && !_b13)
				{
					_b13=true;
					_p1[0]=dic;
					appdomain.Invoke(_m13,instance,_p1);
					_p1[0]=null;
					_b13=false;
					
				}
				else
				{
					base.onAvatarPartChange(dic);
				}
			}
			
			IMethod _m14;
			bool _g14;
			bool _b14;
			public override void onAddSkill(SkillData data)
			{
				if(!_g14)
				{
					_m14=instance.Type.GetMethod("onAddSkill",1);
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
					base.onAddSkill(data);
				}
			}
			
			IMethod _m15;
			bool _g15;
			bool _b15;
			public override void onRemoveSkill(SkillData data)
			{
				if(!_g15)
				{
					_m15=instance.Type.GetMethod("onRemoveSkill",1);
					_g15=true;
				}
				
				if(_m15!=null && !_b15)
				{
					_b15=true;
					_p1[0]=data;
					appdomain.Invoke(_m15,instance,_p1);
					_p1[0]=null;
					_b15=false;
					
				}
				else
				{
					base.onRemoveSkill(data);
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
