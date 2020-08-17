using System;
using System.Text;
using ILRuntime.CLR.Method;
using ILRuntime.Runtime.Enviorment;
using ILRuntime.Runtime.Intepreter;
using UnityEngine;
using AppDomain = ILRuntime.Runtime.Enviorment.AppDomain;
using ShineEngine;

	public class ConfigReadDataAdapter : CrossBindingAdaptor
	{
		public override Type BaseCLRType
		{
			get
			{
				return typeof(ConfigReadData);
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

		public class Adaptor : ConfigReadData, CrossBindingAdaptorType
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
			public override void afterReadConfigAll()
			{
				if(!_g0)
				{
					_m0=instance.Type.GetMethod("afterReadConfigAll",0);
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
					base.afterReadConfigAll();
				}
			}
			
			IMethod _m1;
			bool _g1;
			bool _b1;
			public override void makeConstSize()
			{
				if(!_g1)
				{
					_m1=instance.Type.GetMethod("makeConstSize",0);
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
					base.makeConstSize();
				}
			}
			
			IMethod _m2;
			bool _g2;
			bool _b2;
			public override void setToConfigOne(int type)
			{
				if(!_g2)
				{
					_m2=instance.Type.GetMethod("setToConfigOne",1);
					_g2=true;
				}
				
				if(_m2!=null && !_b2)
				{
					_b2=true;
					_p1[0]=type;
					appdomain.Invoke(_m2,instance,_p1);
					_p1[0]=null;
					_b2=false;
					
				}
				else
				{
					base.setToConfigOne(type);
				}
			}
			
			IMethod _m3;
			bool _g3;
			bool _b3;
			public override void afterReadConfigAllOne(int type)
			{
				if(!_g3)
				{
					_m3=instance.Type.GetMethod("afterReadConfigAllOne",1);
					_g3=true;
				}
				
				if(_m3!=null && !_b3)
				{
					_b3=true;
					_p1[0]=type;
					appdomain.Invoke(_m3,instance,_p1);
					_p1[0]=null;
					_b3=false;
					
				}
				else
				{
					base.afterReadConfigAllOne(type);
				}
			}
			
			IMethod _m4;
			bool _g4;
			bool _b4;
			protected override void readBytesOne(int type,BytesReadStream stream)
			{
				if(!_g4)
				{
					_m4=instance.Type.GetMethod("readBytesOne",2);
					_g4=true;
				}
				
				if(_m4!=null && !_b4)
				{
					_b4=true;
					_p2[0]=type;
					_p2[1]=stream;
					appdomain.Invoke(_m4,instance,_p2);
					_p2[0]=null;
					_p2[1]=null;
					_b4=false;
					
				}
				else
				{
					base.readBytesOne(type,stream);
				}
			}
			
			IMethod _m5;
			bool _g5;
			bool _b5;
			public override void refreshDataOne(int type)
			{
				if(!_g5)
				{
					_m5=instance.Type.GetMethod("refreshDataOne",1);
					_g5=true;
				}
				
				if(_m5!=null && !_b5)
				{
					_b5=true;
					_p1[0]=type;
					appdomain.Invoke(_m5,instance,_p1);
					_p1[0]=null;
					_b5=false;
					
				}
				else
				{
					base.refreshDataOne(type);
				}
			}
			
			IMethod _m6;
			bool _g6;
			bool _b6;
			public override BaseConfig readBytesOneSplit(int type,BytesReadStream stream)
			{
				if(!_g6)
				{
					_m6=instance.Type.GetMethod("readBytesOneSplit",2);
					_g6=true;
				}
				
				if(_m6!=null && !_b6)
				{
					_b6=true;
					_p2[0]=type;
					_p2[1]=stream;
					BaseConfig re=(BaseConfig)appdomain.Invoke(_m6,instance,_p2);
					_p2[0]=null;
					_p2[1]=null;
					_b6=false;
					return re;
					
				}
				else
				{
					return base.readBytesOneSplit(type,stream);
				}
			}
			
			IMethod _m7;
			bool _g7;
			bool _b7;
			protected override void readCD(BytesReadStream stream)
			{
				if(!_g7)
				{
					_m7=instance.Type.GetMethod("readCD",1);
					_g7=true;
				}
				
				if(_m7!=null && !_b7)
				{
					_b7=true;
					_p1[0]=stream;
					appdomain.Invoke(_m7,instance,_p1);
					_p1[0]=null;
					_b7=false;
					
				}
				else
				{
					base.readCD(stream);
				}
			}
			
			IMethod _m8;
			bool _g8;
			bool _b8;
			protected override void readActivity(BytesReadStream stream)
			{
				if(!_g8)
				{
					_m8=instance.Type.GetMethod("readActivity",1);
					_g8=true;
				}
				
				if(_m8!=null && !_b8)
				{
					_b8=true;
					_p1[0]=stream;
					appdomain.Invoke(_m8,instance,_p1);
					_p1[0]=null;
					_b8=false;
					
				}
				else
				{
					base.readActivity(stream);
				}
			}
			
			IMethod _m9;
			bool _g9;
			bool _b9;
			protected override void readCallWay(BytesReadStream stream)
			{
				if(!_g9)
				{
					_m9=instance.Type.GetMethod("readCallWay",1);
					_g9=true;
				}
				
				if(_m9!=null && !_b9)
				{
					_b9=true;
					_p1[0]=stream;
					appdomain.Invoke(_m9,instance,_p1);
					_p1[0]=null;
					_b9=false;
					
				}
				else
				{
					base.readCallWay(stream);
				}
			}
			
			IMethod _m10;
			bool _g10;
			bool _b10;
			protected override void readBuff(BytesReadStream stream)
			{
				if(!_g10)
				{
					_m10=instance.Type.GetMethod("readBuff",1);
					_g10=true;
				}
				
				if(_m10!=null && !_b10)
				{
					_b10=true;
					_p1[0]=stream;
					appdomain.Invoke(_m10,instance,_p1);
					_p1[0]=null;
					_b10=false;
					
				}
				else
				{
					base.readBuff(stream);
				}
			}
			
			IMethod _m11;
			bool _g11;
			bool _b11;
			protected override void readEffect(BytesReadStream stream)
			{
				if(!_g11)
				{
					_m11=instance.Type.GetMethod("readEffect",1);
					_g11=true;
				}
				
				if(_m11!=null && !_b11)
				{
					_b11=true;
					_p1[0]=stream;
					appdomain.Invoke(_m11,instance,_p1);
					_p1[0]=null;
					_b11=false;
					
				}
				else
				{
					base.readEffect(stream);
				}
			}
			
			IMethod _m12;
			bool _g12;
			bool _b12;
			protected override void readSceneEffect(BytesReadStream stream)
			{
				if(!_g12)
				{
					_m12=instance.Type.GetMethod("readSceneEffect",1);
					_g12=true;
				}
				
				if(_m12!=null && !_b12)
				{
					_b12=true;
					_p1[0]=stream;
					appdomain.Invoke(_m12,instance,_p1);
					_p1[0]=null;
					_b12=false;
					
				}
				else
				{
					base.readSceneEffect(stream);
				}
			}
			
			IMethod _m13;
			bool _g13;
			bool _b13;
			protected override void readSkillProb(BytesReadStream stream)
			{
				if(!_g13)
				{
					_m13=instance.Type.GetMethod("readSkillProb",1);
					_g13=true;
				}
				
				if(_m13!=null && !_b13)
				{
					_b13=true;
					_p1[0]=stream;
					appdomain.Invoke(_m13,instance,_p1);
					_p1[0]=null;
					_b13=false;
					
				}
				else
				{
					base.readSkillProb(stream);
				}
			}
			
			IMethod _m14;
			bool _g14;
			bool _b14;
			protected override void readSpecialMove(BytesReadStream stream)
			{
				if(!_g14)
				{
					_m14=instance.Type.GetMethod("readSpecialMove",1);
					_g14=true;
				}
				
				if(_m14!=null && !_b14)
				{
					_b14=true;
					_p1[0]=stream;
					appdomain.Invoke(_m14,instance,_p1);
					_p1[0]=null;
					_b14=false;
					
				}
				else
				{
					base.readSpecialMove(stream);
				}
			}
			
			IMethod _m15;
			bool _g15;
			bool _b15;
			protected override void readRandomItem(BytesReadStream stream)
			{
				if(!_g15)
				{
					_m15=instance.Type.GetMethod("readRandomItem",1);
					_g15=true;
				}
				
				if(_m15!=null && !_b15)
				{
					_b15=true;
					_p1[0]=stream;
					appdomain.Invoke(_m15,instance,_p1);
					_p1[0]=null;
					_b15=false;
					
				}
				else
				{
					base.readRandomItem(stream);
				}
			}
			
			IMethod _m16;
			bool _g16;
			bool _b16;
			protected override void readText(BytesReadStream stream)
			{
				if(!_g16)
				{
					_m16=instance.Type.GetMethod("readText",1);
					_g16=true;
				}
				
				if(_m16!=null && !_b16)
				{
					_b16=true;
					_p1[0]=stream;
					appdomain.Invoke(_m16,instance,_p1);
					_p1[0]=null;
					_b16=false;
					
				}
				else
				{
					base.readText(stream);
				}
			}
			
			IMethod _m17;
			bool _g17;
			bool _b17;
			protected override void readFlowStep(BytesReadStream stream)
			{
				if(!_g17)
				{
					_m17=instance.Type.GetMethod("readFlowStep",1);
					_g17=true;
				}
				
				if(_m17!=null && !_b17)
				{
					_b17=true;
					_p1[0]=stream;
					appdomain.Invoke(_m17,instance,_p1);
					_p1[0]=null;
					_b17=false;
					
				}
				else
				{
					base.readFlowStep(stream);
				}
			}
			
			IMethod _m18;
			bool _g18;
			bool _b18;
			protected override void readCharacter(BytesReadStream stream)
			{
				if(!_g18)
				{
					_m18=instance.Type.GetMethod("readCharacter",1);
					_g18=true;
				}
				
				if(_m18!=null && !_b18)
				{
					_b18=true;
					_p1[0]=stream;
					appdomain.Invoke(_m18,instance,_p1);
					_p1[0]=null;
					_b18=false;
					
				}
				else
				{
					base.readCharacter(stream);
				}
			}
			
			IMethod _m19;
			bool _g19;
			bool _b19;
			protected override void readMonster(BytesReadStream stream)
			{
				if(!_g19)
				{
					_m19=instance.Type.GetMethod("readMonster",1);
					_g19=true;
				}
				
				if(_m19!=null && !_b19)
				{
					_b19=true;
					_p1[0]=stream;
					appdomain.Invoke(_m19,instance,_p1);
					_p1[0]=null;
					_b19=false;
					
				}
				else
				{
					base.readMonster(stream);
				}
			}
			
			IMethod _m20;
			bool _g20;
			bool _b20;
			protected override void readStatus(BytesReadStream stream)
			{
				if(!_g20)
				{
					_m20=instance.Type.GetMethod("readStatus",1);
					_g20=true;
				}
				
				if(_m20!=null && !_b20)
				{
					_b20=true;
					_p1[0]=stream;
					appdomain.Invoke(_m20,instance,_p1);
					_p1[0]=null;
					_b20=false;
					
				}
				else
				{
					base.readStatus(stream);
				}
			}
			
			IMethod _m21;
			bool _g21;
			bool _b21;
			protected override void readProgressBar(BytesReadStream stream)
			{
				if(!_g21)
				{
					_m21=instance.Type.GetMethod("readProgressBar",1);
					_g21=true;
				}
				
				if(_m21!=null && !_b21)
				{
					_b21=true;
					_p1[0]=stream;
					appdomain.Invoke(_m21,instance,_p1);
					_p1[0]=null;
					_b21=false;
					
				}
				else
				{
					base.readProgressBar(stream);
				}
			}
			
			IMethod _m22;
			bool _g22;
			bool _b22;
			protected override void readAchievement(BytesReadStream stream)
			{
				if(!_g22)
				{
					_m22=instance.Type.GetMethod("readAchievement",1);
					_g22=true;
				}
				
				if(_m22!=null && !_b22)
				{
					_b22=true;
					_p1[0]=stream;
					appdomain.Invoke(_m22,instance,_p1);
					_p1[0]=null;
					_b22=false;
					
				}
				else
				{
					base.readAchievement(stream);
				}
			}
			
			IMethod _m23;
			bool _g23;
			bool _b23;
			protected override void readRandomItemList(BytesReadStream stream)
			{
				if(!_g23)
				{
					_m23=instance.Type.GetMethod("readRandomItemList",1);
					_g23=true;
				}
				
				if(_m23!=null && !_b23)
				{
					_b23=true;
					_p1[0]=stream;
					appdomain.Invoke(_m23,instance,_p1);
					_p1[0]=null;
					_b23=false;
					
				}
				else
				{
					base.readRandomItemList(stream);
				}
			}
			
			IMethod _m24;
			bool _g24;
			bool _b24;
			protected override void readSkillLevel(BytesReadStream stream)
			{
				if(!_g24)
				{
					_m24=instance.Type.GetMethod("readSkillLevel",1);
					_g24=true;
				}
				
				if(_m24!=null && !_b24)
				{
					_b24=true;
					_p1[0]=stream;
					appdomain.Invoke(_m24,instance,_p1);
					_p1[0]=null;
					_b24=false;
					
				}
				else
				{
					base.readSkillLevel(stream);
				}
			}
			
			IMethod _m25;
			bool _g25;
			bool _b25;
			protected override void readAttackLevel(BytesReadStream stream)
			{
				if(!_g25)
				{
					_m25=instance.Type.GetMethod("readAttackLevel",1);
					_g25=true;
				}
				
				if(_m25!=null && !_b25)
				{
					_b25=true;
					_p1[0]=stream;
					appdomain.Invoke(_m25,instance,_p1);
					_p1[0]=null;
					_b25=false;
					
				}
				else
				{
					base.readAttackLevel(stream);
				}
			}
			
			IMethod _m26;
			bool _g26;
			bool _b26;
			protected override void readAttack(BytesReadStream stream)
			{
				if(!_g26)
				{
					_m26=instance.Type.GetMethod("readAttack",1);
					_g26=true;
				}
				
				if(_m26!=null && !_b26)
				{
					_b26=true;
					_p1[0]=stream;
					appdomain.Invoke(_m26,instance,_p1);
					_p1[0]=null;
					_b26=false;
					
				}
				else
				{
					base.readAttack(stream);
				}
			}
			
			IMethod _m27;
			bool _g27;
			bool _b27;
			protected override void readMail(BytesReadStream stream)
			{
				if(!_g27)
				{
					_m27=instance.Type.GetMethod("readMail",1);
					_g27=true;
				}
				
				if(_m27!=null && !_b27)
				{
					_b27=true;
					_p1[0]=stream;
					appdomain.Invoke(_m27,instance,_p1);
					_p1[0]=null;
					_b27=false;
					
				}
				else
				{
					base.readMail(stream);
				}
			}
			
			IMethod _m28;
			bool _g28;
			bool _b28;
			protected override void readBattle(BytesReadStream stream)
			{
				if(!_g28)
				{
					_m28=instance.Type.GetMethod("readBattle",1);
					_g28=true;
				}
				
				if(_m28!=null && !_b28)
				{
					_b28=true;
					_p1[0]=stream;
					appdomain.Invoke(_m28,instance,_p1);
					_p1[0]=null;
					_b28=false;
					
				}
				else
				{
					base.readBattle(stream);
				}
			}
			
			IMethod _m29;
			bool _g29;
			bool _b29;
			protected override void readSceneType(BytesReadStream stream)
			{
				if(!_g29)
				{
					_m29=instance.Type.GetMethod("readSceneType",1);
					_g29=true;
				}
				
				if(_m29!=null && !_b29)
				{
					_b29=true;
					_p1[0]=stream;
					appdomain.Invoke(_m29,instance,_p1);
					_p1[0]=null;
					_b29=false;
					
				}
				else
				{
					base.readSceneType(stream);
				}
			}
			
			IMethod _m30;
			bool _g30;
			bool _b30;
			protected override void readTaskType(BytesReadStream stream)
			{
				if(!_g30)
				{
					_m30=instance.Type.GetMethod("readTaskType",1);
					_g30=true;
				}
				
				if(_m30!=null && !_b30)
				{
					_b30=true;
					_p1[0]=stream;
					appdomain.Invoke(_m30,instance,_p1);
					_p1[0]=null;
					_b30=false;
					
				}
				else
				{
					base.readTaskType(stream);
				}
			}
			
			IMethod _m31;
			bool _g31;
			bool _b31;
			protected override void readUnitMoveType(BytesReadStream stream)
			{
				if(!_g31)
				{
					_m31=instance.Type.GetMethod("readUnitMoveType",1);
					_g31=true;
				}
				
				if(_m31!=null && !_b31)
				{
					_b31=true;
					_p1[0]=stream;
					appdomain.Invoke(_m31,instance,_p1);
					_p1[0]=null;
					_b31=false;
					
				}
				else
				{
					base.readUnitMoveType(stream);
				}
			}
			
			IMethod _m32;
			bool _g32;
			bool _b32;
			protected override void readRoleShowDataPartType(BytesReadStream stream)
			{
				if(!_g32)
				{
					_m32=instance.Type.GetMethod("readRoleShowDataPartType",1);
					_g32=true;
				}
				
				if(_m32!=null && !_b32)
				{
					_b32=true;
					_p1[0]=stream;
					appdomain.Invoke(_m32,instance,_p1);
					_p1[0]=null;
					_b32=false;
					
				}
				else
				{
					base.readRoleShowDataPartType(stream);
				}
			}
			
			IMethod _m33;
			bool _g33;
			bool _b33;
			protected override void readMailType(BytesReadStream stream)
			{
				if(!_g33)
				{
					_m33=instance.Type.GetMethod("readMailType",1);
					_g33=true;
				}
				
				if(_m33!=null && !_b33)
				{
					_b33=true;
					_p1[0]=stream;
					appdomain.Invoke(_m33,instance,_p1);
					_p1[0]=null;
					_b33=false;
					
				}
				else
				{
					base.readMailType(stream);
				}
			}
			
			IMethod _m34;
			bool _g34;
			bool _b34;
			protected override void readModelBindPosType(BytesReadStream stream)
			{
				if(!_g34)
				{
					_m34=instance.Type.GetMethod("readModelBindPosType",1);
					_g34=true;
				}
				
				if(_m34!=null && !_b34)
				{
					_b34=true;
					_p1[0]=stream;
					appdomain.Invoke(_m34,instance,_p1);
					_p1[0]=null;
					_b34=false;
					
				}
				else
				{
					base.readModelBindPosType(stream);
				}
			}
			
			IMethod _m35;
			bool _g35;
			bool _b35;
			protected override void readUnitSpecialMoveType(BytesReadStream stream)
			{
				if(!_g35)
				{
					_m35=instance.Type.GetMethod("readUnitSpecialMoveType",1);
					_g35=true;
				}
				
				if(_m35!=null && !_b35)
				{
					_b35=true;
					_p1[0]=stream;
					appdomain.Invoke(_m35,instance,_p1);
					_p1[0]=null;
					_b35=false;
					
				}
				else
				{
					base.readUnitSpecialMoveType(stream);
				}
			}
			
			IMethod _m36;
			bool _g36;
			bool _b36;
			protected override void readLanguageType(BytesReadStream stream)
			{
				if(!_g36)
				{
					_m36=instance.Type.GetMethod("readLanguageType",1);
					_g36=true;
				}
				
				if(_m36!=null && !_b36)
				{
					_b36=true;
					_p1[0]=stream;
					appdomain.Invoke(_m36,instance,_p1);
					_p1[0]=null;
					_b36=false;
					
				}
				else
				{
					base.readLanguageType(stream);
				}
			}
			
			IMethod _m37;
			bool _g37;
			bool _b37;
			protected override void readRoleLevel(BytesReadStream stream)
			{
				if(!_g37)
				{
					_m37=instance.Type.GetMethod("readRoleLevel",1);
					_g37=true;
				}
				
				if(_m37!=null && !_b37)
				{
					_b37=true;
					_p1[0]=stream;
					appdomain.Invoke(_m37,instance,_p1);
					_p1[0]=null;
					_b37=false;
					
				}
				else
				{
					base.readRoleLevel(stream);
				}
			}
			
			IMethod _m38;
			bool _g38;
			bool _b38;
			protected override void readRandomName(BytesReadStream stream)
			{
				if(!_g38)
				{
					_m38=instance.Type.GetMethod("readRandomName",1);
					_g38=true;
				}
				
				if(_m38!=null && !_b38)
				{
					_b38=true;
					_p1[0]=stream;
					appdomain.Invoke(_m38,instance,_p1);
					_p1[0]=null;
					_b38=false;
					
				}
				else
				{
					base.readRandomName(stream);
				}
			}
			
			IMethod _m39;
			bool _g39;
			bool _b39;
			protected override void readSkillVar(BytesReadStream stream)
			{
				if(!_g39)
				{
					_m39=instance.Type.GetMethod("readSkillVar",1);
					_g39=true;
				}
				
				if(_m39!=null && !_b39)
				{
					_b39=true;
					_p1[0]=stream;
					appdomain.Invoke(_m39,instance,_p1);
					_p1[0]=null;
					_b39=false;
					
				}
				else
				{
					base.readSkillVar(stream);
				}
			}
			
			IMethod _m40;
			bool _g40;
			bool _b40;
			protected override void readSkill(BytesReadStream stream)
			{
				if(!_g40)
				{
					_m40=instance.Type.GetMethod("readSkill",1);
					_g40=true;
				}
				
				if(_m40!=null && !_b40)
				{
					_b40=true;
					_p1[0]=stream;
					appdomain.Invoke(_m40,instance,_p1);
					_p1[0]=null;
					_b40=false;
					
				}
				else
				{
					base.readSkill(stream);
				}
			}
			
			IMethod _m41;
			bool _g41;
			bool _b41;
			protected override void readAttribute(BytesReadStream stream)
			{
				if(!_g41)
				{
					_m41=instance.Type.GetMethod("readAttribute",1);
					_g41=true;
				}
				
				if(_m41!=null && !_b41)
				{
					_b41=true;
					_p1[0]=stream;
					appdomain.Invoke(_m41,instance,_p1);
					_p1[0]=null;
					_b41=false;
					
				}
				else
				{
					base.readAttribute(stream);
				}
			}
			
			IMethod _m42;
			bool _g42;
			bool _b42;
			protected override void readBuffLevel(BytesReadStream stream)
			{
				if(!_g42)
				{
					_m42=instance.Type.GetMethod("readBuffLevel",1);
					_g42=true;
				}
				
				if(_m42!=null && !_b42)
				{
					_b42=true;
					_p1[0]=stream;
					appdomain.Invoke(_m42,instance,_p1);
					_p1[0]=null;
					_b42=false;
					
				}
				else
				{
					base.readBuffLevel(stream);
				}
			}
			
			IMethod _m43;
			bool _g43;
			bool _b43;
			protected override void readPlatform(BytesReadStream stream)
			{
				if(!_g43)
				{
					_m43=instance.Type.GetMethod("readPlatform",1);
					_g43=true;
				}
				
				if(_m43!=null && !_b43)
				{
					_b43=true;
					_p1[0]=stream;
					appdomain.Invoke(_m43,instance,_p1);
					_p1[0]=null;
					_b43=false;
					
				}
				else
				{
					base.readPlatform(stream);
				}
			}
			
			IMethod _m44;
			bool _g44;
			bool _b44;
			protected override void readTask(BytesReadStream stream)
			{
				if(!_g44)
				{
					_m44=instance.Type.GetMethod("readTask",1);
					_g44=true;
				}
				
				if(_m44!=null && !_b44)
				{
					_b44=true;
					_p1[0]=stream;
					appdomain.Invoke(_m44,instance,_p1);
					_p1[0]=null;
					_b44=false;
					
				}
				else
				{
					base.readTask(stream);
				}
			}
			
			IMethod _m45;
			bool _g45;
			bool _b45;
			protected override void readUI(BytesReadStream stream)
			{
				if(!_g45)
				{
					_m45=instance.Type.GetMethod("readUI",1);
					_g45=true;
				}
				
				if(_m45!=null && !_b45)
				{
					_b45=true;
					_p1[0]=stream;
					appdomain.Invoke(_m45,instance,_p1);
					_p1[0]=null;
					_b45=false;
					
				}
				else
				{
					base.readUI(stream);
				}
			}
			
			IMethod _m46;
			bool _g46;
			bool _b46;
			protected override void readCDGroup(BytesReadStream stream)
			{
				if(!_g46)
				{
					_m46=instance.Type.GetMethod("readCDGroup",1);
					_g46=true;
				}
				
				if(_m46!=null && !_b46)
				{
					_b46=true;
					_p1[0]=stream;
					appdomain.Invoke(_m46,instance,_p1);
					_p1[0]=null;
					_b46=false;
					
				}
				else
				{
					base.readCDGroup(stream);
				}
			}
			
			IMethod _m47;
			bool _g47;
			bool _b47;
			protected override void readModel(BytesReadStream stream)
			{
				if(!_g47)
				{
					_m47=instance.Type.GetMethod("readModel",1);
					_g47=true;
				}
				
				if(_m47!=null && !_b47)
				{
					_b47=true;
					_p1[0]=stream;
					appdomain.Invoke(_m47,instance,_p1);
					_p1[0]=null;
					_b47=false;
					
				}
				else
				{
					base.readModel(stream);
				}
			}
			
			IMethod _m48;
			bool _g48;
			bool _b48;
			protected override void readMarkResource(BytesReadStream stream)
			{
				if(!_g48)
				{
					_m48=instance.Type.GetMethod("readMarkResource",1);
					_g48=true;
				}
				
				if(_m48!=null && !_b48)
				{
					_b48=true;
					_p1[0]=stream;
					appdomain.Invoke(_m48,instance,_p1);
					_p1[0]=null;
					_b48=false;
					
				}
				else
				{
					base.readMarkResource(stream);
				}
			}
			
			IMethod _m49;
			bool _g49;
			bool _b49;
			protected override void readSkillStep(BytesReadStream stream)
			{
				if(!_g49)
				{
					_m49=instance.Type.GetMethod("readSkillStep",1);
					_g49=true;
				}
				
				if(_m49!=null && !_b49)
				{
					_b49=true;
					_p1[0]=stream;
					appdomain.Invoke(_m49,instance,_p1);
					_p1[0]=null;
					_b49=false;
					
				}
				else
				{
					base.readSkillStep(stream);
				}
			}
			
			IMethod _m50;
			bool _g50;
			bool _b50;
			protected override void readSkillGroup(BytesReadStream stream)
			{
				if(!_g50)
				{
					_m50=instance.Type.GetMethod("readSkillGroup",1);
					_g50=true;
				}
				
				if(_m50!=null && !_b50)
				{
					_b50=true;
					_p1[0]=stream;
					appdomain.Invoke(_m50,instance,_p1);
					_p1[0]=null;
					_b50=false;
					
				}
				else
				{
					base.readSkillGroup(stream);
				}
			}
			
			IMethod _m51;
			bool _g51;
			bool _b51;
			protected override void readAttackGroup(BytesReadStream stream)
			{
				if(!_g51)
				{
					_m51=instance.Type.GetMethod("readAttackGroup",1);
					_g51=true;
				}
				
				if(_m51!=null && !_b51)
				{
					_b51=true;
					_p1[0]=stream;
					appdomain.Invoke(_m51,instance,_p1);
					_p1[0]=null;
					_b51=false;
					
				}
				else
				{
					base.readAttackGroup(stream);
				}
			}
			
			IMethod _m52;
			bool _g52;
			bool _b52;
			protected override void readModelMotion(BytesReadStream stream)
			{
				if(!_g52)
				{
					_m52=instance.Type.GetMethod("readModelMotion",1);
					_g52=true;
				}
				
				if(_m52!=null && !_b52)
				{
					_b52=true;
					_p1[0]=stream;
					appdomain.Invoke(_m52,instance,_p1);
					_p1[0]=null;
					_b52=false;
					
				}
				else
				{
					base.readModelMotion(stream);
				}
			}
			
			IMethod _m53;
			bool _g53;
			bool _b53;
			protected override void readBuffGroup(BytesReadStream stream)
			{
				if(!_g53)
				{
					_m53=instance.Type.GetMethod("readBuffGroup",1);
					_g53=true;
				}
				
				if(_m53!=null && !_b53)
				{
					_b53=true;
					_p1[0]=stream;
					appdomain.Invoke(_m53,instance,_p1);
					_p1[0]=null;
					_b53=false;
					
				}
				else
				{
					base.readBuffGroup(stream);
				}
			}
			
			IMethod _m54;
			bool _g54;
			bool _b54;
			protected override void readQuest(BytesReadStream stream)
			{
				if(!_g54)
				{
					_m54=instance.Type.GetMethod("readQuest",1);
					_g54=true;
				}
				
				if(_m54!=null && !_b54)
				{
					_b54=true;
					_p1[0]=stream;
					appdomain.Invoke(_m54,instance,_p1);
					_p1[0]=null;
					_b54=false;
					
				}
				else
				{
					base.readQuest(stream);
				}
			}
			
			IMethod _m55;
			bool _g55;
			bool _b55;
			protected override void readBullet(BytesReadStream stream)
			{
				if(!_g55)
				{
					_m55=instance.Type.GetMethod("readBullet",1);
					_g55=true;
				}
				
				if(_m55!=null && !_b55)
				{
					_b55=true;
					_p1[0]=stream;
					appdomain.Invoke(_m55,instance,_p1);
					_p1[0]=null;
					_b55=false;
					
				}
				else
				{
					base.readBullet(stream);
				}
			}
			
			IMethod _m56;
			bool _g56;
			bool _b56;
			protected override void readNPC(BytesReadStream stream)
			{
				if(!_g56)
				{
					_m56=instance.Type.GetMethod("readNPC",1);
					_g56=true;
				}
				
				if(_m56!=null && !_b56)
				{
					_b56=true;
					_p1[0]=stream;
					appdomain.Invoke(_m56,instance,_p1);
					_p1[0]=null;
					_b56=false;
					
				}
				else
				{
					base.readNPC(stream);
				}
			}
			
			IMethod _m57;
			bool _g57;
			bool _b57;
			protected override void readCurrency(BytesReadStream stream)
			{
				if(!_g57)
				{
					_m57=instance.Type.GetMethod("readCurrency",1);
					_g57=true;
				}
				
				if(_m57!=null && !_b57)
				{
					_b57=true;
					_p1[0]=stream;
					appdomain.Invoke(_m57,instance,_p1);
					_p1[0]=null;
					_b57=false;
					
				}
				else
				{
					base.readCurrency(stream);
				}
			}
			
			IMethod _m58;
			bool _g58;
			bool _b58;
			protected override void readSkillStepLevel(BytesReadStream stream)
			{
				if(!_g58)
				{
					_m58=instance.Type.GetMethod("readSkillStepLevel",1);
					_g58=true;
				}
				
				if(_m58!=null && !_b58)
				{
					_b58=true;
					_p1[0]=stream;
					appdomain.Invoke(_m58,instance,_p1);
					_p1[0]=null;
					_b58=false;
					
				}
				else
				{
					base.readSkillStepLevel(stream);
				}
			}
			
			IMethod _m59;
			bool _g59;
			bool _b59;
			protected override void readPuppet(BytesReadStream stream)
			{
				if(!_g59)
				{
					_m59=instance.Type.GetMethod("readPuppet",1);
					_g59=true;
				}
				
				if(_m59!=null && !_b59)
				{
					_b59=true;
					_p1[0]=stream;
					appdomain.Invoke(_m59,instance,_p1);
					_p1[0]=null;
					_b59=false;
					
				}
				else
				{
					base.readPuppet(stream);
				}
			}
			
			IMethod _m60;
			bool _g60;
			bool _b60;
			protected override void readPuppetLevel(BytesReadStream stream)
			{
				if(!_g60)
				{
					_m60=instance.Type.GetMethod("readPuppetLevel",1);
					_g60=true;
				}
				
				if(_m60!=null && !_b60)
				{
					_b60=true;
					_p1[0]=stream;
					appdomain.Invoke(_m60,instance,_p1);
					_p1[0]=null;
					_b60=false;
					
				}
				else
				{
					base.readPuppetLevel(stream);
				}
			}
			
			IMethod _m61;
			bool _g61;
			bool _b61;
			protected override void readAvatarPart(BytesReadStream stream)
			{
				if(!_g61)
				{
					_m61=instance.Type.GetMethod("readAvatarPart",1);
					_g61=true;
				}
				
				if(_m61!=null && !_b61)
				{
					_b61=true;
					_p1[0]=stream;
					appdomain.Invoke(_m61,instance,_p1);
					_p1[0]=null;
					_b61=false;
					
				}
				else
				{
					base.readAvatarPart(stream);
				}
			}
			
			IMethod _m62;
			bool _g62;
			bool _b62;
			protected override void readLanguage(BytesReadStream stream)
			{
				if(!_g62)
				{
					_m62=instance.Type.GetMethod("readLanguage",1);
					_g62=true;
				}
				
				if(_m62!=null && !_b62)
				{
					_b62=true;
					_p1[0]=stream;
					appdomain.Invoke(_m62,instance,_p1);
					_p1[0]=null;
					_b62=false;
					
				}
				else
				{
					base.readLanguage(stream);
				}
			}
			
			IMethod _m63;
			bool _g63;
			bool _b63;
			protected override void readFacade(BytesReadStream stream)
			{
				if(!_g63)
				{
					_m63=instance.Type.GetMethod("readFacade",1);
					_g63=true;
				}
				
				if(_m63!=null && !_b63)
				{
					_b63=true;
					_p1[0]=stream;
					appdomain.Invoke(_m63,instance,_p1);
					_p1[0]=null;
					_b63=false;
					
				}
				else
				{
					base.readFacade(stream);
				}
			}
			
			IMethod _m64;
			bool _g64;
			bool _b64;
			protected override void readPet(BytesReadStream stream)
			{
				if(!_g64)
				{
					_m64=instance.Type.GetMethod("readPet",1);
					_g64=true;
				}
				
				if(_m64!=null && !_b64)
				{
					_b64=true;
					_p1[0]=stream;
					appdomain.Invoke(_m64,instance,_p1);
					_p1[0]=null;
					_b64=false;
					
				}
				else
				{
					base.readPet(stream);
				}
			}
			
			IMethod _m65;
			bool _g65;
			bool _b65;
			protected override void readMotion(BytesReadStream stream)
			{
				if(!_g65)
				{
					_m65=instance.Type.GetMethod("readMotion",1);
					_g65=true;
				}
				
				if(_m65!=null && !_b65)
				{
					_b65=true;
					_p1[0]=stream;
					appdomain.Invoke(_m65,instance,_p1);
					_p1[0]=null;
					_b65=false;
					
				}
				else
				{
					base.readMotion(stream);
				}
			}
			
			IMethod _m66;
			bool _g66;
			bool _b66;
			protected override void readItem(BytesReadStream stream)
			{
				if(!_g66)
				{
					_m66=instance.Type.GetMethod("readItem",1);
					_g66=true;
				}
				
				if(_m66!=null && !_b66)
				{
					_b66=true;
					_p1[0]=stream;
					appdomain.Invoke(_m66,instance,_p1);
					_p1[0]=null;
					_b66=false;
					
				}
				else
				{
					base.readItem(stream);
				}
			}
			
			IMethod _m67;
			bool _g67;
			bool _b67;
			protected override void readBulletLevel(BytesReadStream stream)
			{
				if(!_g67)
				{
					_m67=instance.Type.GetMethod("readBulletLevel",1);
					_g67=true;
				}
				
				if(_m67!=null && !_b67)
				{
					_b67=true;
					_p1[0]=stream;
					appdomain.Invoke(_m67,instance,_p1);
					_p1[0]=null;
					_b67=false;
					
				}
				else
				{
					base.readBulletLevel(stream);
				}
			}
			
			IMethod _m68;
			bool _g68;
			bool _b68;
			protected override void readFightUnit(BytesReadStream stream)
			{
				if(!_g68)
				{
					_m68=instance.Type.GetMethod("readFightUnit",1);
					_g68=true;
				}
				
				if(_m68!=null && !_b68)
				{
					_b68=true;
					_p1[0]=stream;
					appdomain.Invoke(_m68,instance,_p1);
					_p1[0]=null;
					_b68=false;
					
				}
				else
				{
					base.readFightUnit(stream);
				}
			}
			
			IMethod _m69;
			bool _g69;
			bool _b69;
			protected override void readInfoCode(BytesReadStream stream)
			{
				if(!_g69)
				{
					_m69=instance.Type.GetMethod("readInfoCode",1);
					_g69=true;
				}
				
				if(_m69!=null && !_b69)
				{
					_b69=true;
					_p1[0]=stream;
					appdomain.Invoke(_m69,instance,_p1);
					_p1[0]=null;
					_b69=false;
					
				}
				else
				{
					base.readInfoCode(stream);
				}
			}
			
			IMethod _m70;
			bool _g70;
			bool _b70;
			protected override void readRedPoint(BytesReadStream stream)
			{
				if(!_g70)
				{
					_m70=instance.Type.GetMethod("readRedPoint",1);
					_g70=true;
				}
				
				if(_m70!=null && !_b70)
				{
					_b70=true;
					_p1[0]=stream;
					appdomain.Invoke(_m70,instance,_p1);
					_p1[0]=null;
					_b70=false;
					
				}
				else
				{
					base.readRedPoint(stream);
				}
			}
			
			IMethod _m71;
			bool _g71;
			bool _b71;
			protected override void readFightUnitLevel(BytesReadStream stream)
			{
				if(!_g71)
				{
					_m71=instance.Type.GetMethod("readFightUnitLevel",1);
					_g71=true;
				}
				
				if(_m71!=null && !_b71)
				{
					_b71=true;
					_p1[0]=stream;
					appdomain.Invoke(_m71,instance,_p1);
					_p1[0]=null;
					_b71=false;
					
				}
				else
				{
					base.readFightUnitLevel(stream);
				}
			}
			
			IMethod _m72;
			bool _g72;
			bool _b72;
			protected override void readInternationalResource(BytesReadStream stream)
			{
				if(!_g72)
				{
					_m72=instance.Type.GetMethod("readInternationalResource",1);
					_g72=true;
				}
				
				if(_m72!=null && !_b72)
				{
					_b72=true;
					_p1[0]=stream;
					appdomain.Invoke(_m72,instance,_p1);
					_p1[0]=null;
					_b72=false;
					
				}
				else
				{
					base.readInternationalResource(stream);
				}
			}
			
			IMethod _m73;
			bool _g73;
			bool _b73;
			protected override void readScenePlaceElement(BytesReadStream stream)
			{
				if(!_g73)
				{
					_m73=instance.Type.GetMethod("readScenePlaceElement",1);
					_g73=true;
				}
				
				if(_m73!=null && !_b73)
				{
					_b73=true;
					_p1[0]=stream;
					appdomain.Invoke(_m73,instance,_p1);
					_p1[0]=null;
					_b73=false;
					
				}
				else
				{
					base.readScenePlaceElement(stream);
				}
			}
			
			IMethod _m74;
			bool _g74;
			bool _b74;
			protected override void readCreateItem(BytesReadStream stream)
			{
				if(!_g74)
				{
					_m74=instance.Type.GetMethod("readCreateItem",1);
					_g74=true;
				}
				
				if(_m74!=null && !_b74)
				{
					_b74=true;
					_p1[0]=stream;
					appdomain.Invoke(_m74,instance,_p1);
					_p1[0]=null;
					_b74=false;
					
				}
				else
				{
					base.readCreateItem(stream);
				}
			}
			
			IMethod _m75;
			bool _g75;
			bool _b75;
			protected override void readScene(BytesReadStream stream)
			{
				if(!_g75)
				{
					_m75=instance.Type.GetMethod("readScene",1);
					_g75=true;
				}
				
				if(_m75!=null && !_b75)
				{
					_b75=true;
					_p1[0]=stream;
					appdomain.Invoke(_m75,instance,_p1);
					_p1[0]=null;
					_b75=false;
					
				}
				else
				{
					base.readScene(stream);
				}
			}
			
			IMethod _m76;
			bool _g76;
			bool _b76;
			protected override void readFunction(BytesReadStream stream)
			{
				if(!_g76)
				{
					_m76=instance.Type.GetMethod("readFunction",1);
					_g76=true;
				}
				
				if(_m76!=null && !_b76)
				{
					_b76=true;
					_p1[0]=stream;
					appdomain.Invoke(_m76,instance,_p1);
					_p1[0]=null;
					_b76=false;
					
				}
				else
				{
					base.readFunction(stream);
				}
			}
			
			IMethod _m77;
			bool _g77;
			bool _b77;
			protected override void readReward(BytesReadStream stream)
			{
				if(!_g77)
				{
					_m77=instance.Type.GetMethod("readReward",1);
					_g77=true;
				}
				
				if(_m77!=null && !_b77)
				{
					_b77=true;
					_p1[0]=stream;
					appdomain.Invoke(_m77,instance,_p1);
					_p1[0]=null;
					_b77=false;
					
				}
				else
				{
					base.readReward(stream);
				}
			}
			
			IMethod _m78;
			bool _g78;
			bool _b78;
			protected override void readGlobal(BytesReadStream stream)
			{
				if(!_g78)
				{
					_m78=instance.Type.GetMethod("readGlobal",1);
					_g78=true;
				}
				
				if(_m78!=null && !_b78)
				{
					_b78=true;
					_p1[0]=stream;
					appdomain.Invoke(_m78,instance,_p1);
					_p1[0]=null;
					_b78=false;
					
				}
				else
				{
					base.readGlobal(stream);
				}
			}
			
			IMethod _m79;
			bool _g79;
			bool _b79;
			protected override void readSensitiveWord(BytesReadStream stream)
			{
				if(!_g79)
				{
					_m79=instance.Type.GetMethod("readSensitiveWord",1);
					_g79=true;
				}
				
				if(_m79!=null && !_b79)
				{
					_b79=true;
					_p1[0]=stream;
					appdomain.Invoke(_m79,instance,_p1);
					_p1[0]=null;
					_b79=false;
					
				}
				else
				{
					base.readSensitiveWord(stream);
				}
			}
			
			IMethod _m80;
			bool _g80;
			bool _b80;
			protected override void readFont(BytesReadStream stream)
			{
				if(!_g80)
				{
					_m80=instance.Type.GetMethod("readFont",1);
					_g80=true;
				}
				
				if(_m80!=null && !_b80)
				{
					_b80=true;
					_p1[0]=stream;
					appdomain.Invoke(_m80,instance,_p1);
					_p1[0]=null;
					_b80=false;
					
				}
				else
				{
					base.readFont(stream);
				}
			}
			
			IMethod _m81;
			bool _g81;
			bool _b81;
			protected override void readCountryCode(BytesReadStream stream)
			{
				if(!_g81)
				{
					_m81=instance.Type.GetMethod("readCountryCode",1);
					_g81=true;
				}
				
				if(_m81!=null && !_b81)
				{
					_b81=true;
					_p1[0]=stream;
					appdomain.Invoke(_m81,instance,_p1);
					_p1[0]=null;
					_b81=false;
					
				}
				else
				{
					base.readCountryCode(stream);
				}
			}
			
			IMethod _m82;
			bool _g82;
			bool _b82;
			protected override void readGMType(BytesReadStream stream)
			{
				if(!_g82)
				{
					_m82=instance.Type.GetMethod("readGMType",1);
					_g82=true;
				}
				
				if(_m82!=null && !_b82)
				{
					_b82=true;
					_p1[0]=stream;
					appdomain.Invoke(_m82,instance,_p1);
					_p1[0]=null;
					_b82=false;
					
				}
				else
				{
					base.readGMType(stream);
				}
			}
			
			IMethod _m83;
			bool _g83;
			bool _b83;
			protected override void readCost(BytesReadStream stream)
			{
				if(!_g83)
				{
					_m83=instance.Type.GetMethod("readCost",1);
					_g83=true;
				}
				
				if(_m83!=null && !_b83)
				{
					_b83=true;
					_p1[0]=stream;
					appdomain.Invoke(_m83,instance,_p1);
					_p1[0]=null;
					_b83=false;
					
				}
				else
				{
					base.readCost(stream);
				}
			}
			
			IMethod _m84;
			bool _g84;
			bool _b84;
			protected override void readExchange(BytesReadStream stream)
			{
				if(!_g84)
				{
					_m84=instance.Type.GetMethod("readExchange",1);
					_g84=true;
				}
				
				if(_m84!=null && !_b84)
				{
					_b84=true;
					_p1[0]=stream;
					appdomain.Invoke(_m84,instance,_p1);
					_p1[0]=null;
					_b84=false;
					
				}
				else
				{
					base.readExchange(stream);
				}
			}
			
			IMethod _m85;
			bool _g85;
			bool _b85;
			protected override void readBigFloatRank(BytesReadStream stream)
			{
				if(!_g85)
				{
					_m85=instance.Type.GetMethod("readBigFloatRank",1);
					_g85=true;
				}
				
				if(_m85!=null && !_b85)
				{
					_b85=true;
					_p1[0]=stream;
					appdomain.Invoke(_m85,instance,_p1);
					_p1[0]=null;
					_b85=false;
					
				}
				else
				{
					base.readBigFloatRank(stream);
				}
			}
			
			IMethod _m86;
			bool _g86;
			bool _b86;
			protected override void readVocation(BytesReadStream stream)
			{
				if(!_g86)
				{
					_m86=instance.Type.GetMethod("readVocation",1);
					_g86=true;
				}
				
				if(_m86!=null && !_b86)
				{
					_b86=true;
					_p1[0]=stream;
					appdomain.Invoke(_m86,instance,_p1);
					_p1[0]=null;
					_b86=false;
					
				}
				else
				{
					base.readVocation(stream);
				}
			}
			
			IMethod _m87;
			bool _g87;
			bool _b87;
			protected override void readSceneMap(BytesReadStream stream)
			{
				if(!_g87)
				{
					_m87=instance.Type.GetMethod("readSceneMap",1);
					_g87=true;
				}
				
				if(_m87!=null && !_b87)
				{
					_b87=true;
					_p1[0]=stream;
					appdomain.Invoke(_m87,instance,_p1);
					_p1[0]=null;
					_b87=false;
					
				}
				else
				{
					base.readSceneMap(stream);
				}
			}
			
			IMethod _m88;
			bool _g88;
			bool _b88;
			protected override void readPushNotify(BytesReadStream stream)
			{
				if(!_g88)
				{
					_m88=instance.Type.GetMethod("readPushNotify",1);
					_g88=true;
				}
				
				if(_m88!=null && !_b88)
				{
					_b88=true;
					_p1[0]=stream;
					appdomain.Invoke(_m88,instance,_p1);
					_p1[0]=null;
					_b88=false;
					
				}
				else
				{
					base.readPushNotify(stream);
				}
			}
			
			IMethod _m89;
			bool _g89;
			bool _b89;
			protected override void readOperation(BytesReadStream stream)
			{
				if(!_g89)
				{
					_m89=instance.Type.GetMethod("readOperation",1);
					_g89=true;
				}
				
				if(_m89!=null && !_b89)
				{
					_b89=true;
					_p1[0]=stream;
					appdomain.Invoke(_m89,instance,_p1);
					_p1[0]=null;
					_b89=false;
					
				}
				else
				{
					base.readOperation(stream);
				}
			}
			
			IMethod _m90;
			bool _g90;
			bool _b90;
			protected override void readEquipSlotType(BytesReadStream stream)
			{
				if(!_g90)
				{
					_m90=instance.Type.GetMethod("readEquipSlotType",1);
					_g90=true;
				}
				
				if(_m90!=null && !_b90)
				{
					_b90=true;
					_p1[0]=stream;
					appdomain.Invoke(_m90,instance,_p1);
					_p1[0]=null;
					_b90=false;
					
				}
				else
				{
					base.readEquipSlotType(stream);
				}
			}
			
			IMethod _m91;
			bool _g91;
			bool _b91;
			protected override void readSceneRoleAttribute(BytesReadStream stream)
			{
				if(!_g91)
				{
					_m91=instance.Type.GetMethod("readSceneRoleAttribute",1);
					_g91=true;
				}
				
				if(_m91!=null && !_b91)
				{
					_b91=true;
					_p1[0]=stream;
					appdomain.Invoke(_m91,instance,_p1);
					_p1[0]=null;
					_b91=false;
					
				}
				else
				{
					base.readSceneRoleAttribute(stream);
				}
			}
			
			IMethod _m92;
			bool _g92;
			bool _b92;
			protected override void readBuildingLevel(BytesReadStream stream)
			{
				if(!_g92)
				{
					_m92=instance.Type.GetMethod("readBuildingLevel",1);
					_g92=true;
				}
				
				if(_m92!=null && !_b92)
				{
					_b92=true;
					_p1[0]=stream;
					appdomain.Invoke(_m92,instance,_p1);
					_p1[0]=null;
					_b92=false;
					
				}
				else
				{
					base.readBuildingLevel(stream);
				}
			}
			
			IMethod _m93;
			bool _g93;
			bool _b93;
			protected override void readSkillInfluenceType(BytesReadStream stream)
			{
				if(!_g93)
				{
					_m93=instance.Type.GetMethod("readSkillInfluenceType",1);
					_g93=true;
				}
				
				if(_m93!=null && !_b93)
				{
					_b93=true;
					_p1[0]=stream;
					appdomain.Invoke(_m93,instance,_p1);
					_p1[0]=null;
					_b93=false;
					
				}
				else
				{
					base.readSkillInfluenceType(stream);
				}
			}
			
			IMethod _m94;
			bool _g94;
			bool _b94;
			protected override void readBuilding(BytesReadStream stream)
			{
				if(!_g94)
				{
					_m94=instance.Type.GetMethod("readBuilding",1);
					_g94=true;
				}
				
				if(_m94!=null && !_b94)
				{
					_b94=true;
					_p1[0]=stream;
					appdomain.Invoke(_m94,instance,_p1);
					_p1[0]=null;
					_b94=false;
					
				}
				else
				{
					base.readBuilding(stream);
				}
			}
			
			IMethod _m95;
			bool _g95;
			bool _b95;
			protected override void readSkillBar(BytesReadStream stream)
			{
				if(!_g95)
				{
					_m95=instance.Type.GetMethod("readSkillBar",1);
					_g95=true;
				}
				
				if(_m95!=null && !_b95)
				{
					_b95=true;
					_p1[0]=stream;
					appdomain.Invoke(_m95,instance,_p1);
					_p1[0]=null;
					_b95=false;
					
				}
				else
				{
					base.readSkillBar(stream);
				}
			}
			
			IMethod _m96;
			bool _g96;
			bool _b96;
			protected override void readRoleGroup(BytesReadStream stream)
			{
				if(!_g96)
				{
					_m96=instance.Type.GetMethod("readRoleGroup",1);
					_g96=true;
				}
				
				if(_m96!=null && !_b96)
				{
					_b96=true;
					_p1[0]=stream;
					appdomain.Invoke(_m96,instance,_p1);
					_p1[0]=null;
					_b96=false;
					
				}
				else
				{
					base.readRoleGroup(stream);
				}
			}
			
			IMethod _m97;
			bool _g97;
			bool _b97;
			protected override void readRoleGroupLevel(BytesReadStream stream)
			{
				if(!_g97)
				{
					_m97=instance.Type.GetMethod("readRoleGroupLevel",1);
					_g97=true;
				}
				
				if(_m97!=null && !_b97)
				{
					_b97=true;
					_p1[0]=stream;
					appdomain.Invoke(_m97,instance,_p1);
					_p1[0]=null;
					_b97=false;
					
				}
				else
				{
					base.readRoleGroupLevel(stream);
				}
			}
			
			IMethod _m98;
			bool _g98;
			bool _b98;
			protected override void readRoleGroupTitle(BytesReadStream stream)
			{
				if(!_g98)
				{
					_m98=instance.Type.GetMethod("readRoleGroupTitle",1);
					_g98=true;
				}
				
				if(_m98!=null && !_b98)
				{
					_b98=true;
					_p1[0]=stream;
					appdomain.Invoke(_m98,instance,_p1);
					_p1[0]=null;
					_b98=false;
					
				}
				else
				{
					base.readRoleGroupTitle(stream);
				}
			}
			
			IMethod _m99;
			bool _g99;
			bool _b99;
			protected override void readInfoLog(BytesReadStream stream)
			{
				if(!_g99)
				{
					_m99=instance.Type.GetMethod("readInfoLog",1);
					_g99=true;
				}
				
				if(_m99!=null && !_b99)
				{
					_b99=true;
					_p1[0]=stream;
					appdomain.Invoke(_m99,instance,_p1);
					_p1[0]=null;
					_b99=false;
					
				}
				else
				{
					base.readInfoLog(stream);
				}
			}
			
			IMethod _m100;
			bool _g100;
			bool _b100;
			public override void addToConfigOne(int type)
			{
				if(!_g100)
				{
					_m100=instance.Type.GetMethod("addToConfigOne",1);
					_g100=true;
				}
				
				if(_m100!=null && !_b100)
				{
					_b100=true;
					_p1[0]=type;
					appdomain.Invoke(_m100,instance,_p1);
					_p1[0]=null;
					_b100=false;
					
				}
				else
				{
					base.addToConfigOne(type);
				}
			}
			
			IMethod _m101;
			bool _g101;
			bool _b101;
			protected override void readExchangeGroup(BytesReadStream stream)
			{
				if(!_g101)
				{
					_m101=instance.Type.GetMethod("readExchangeGroup",1);
					_g101=true;
				}
				
				if(_m101!=null && !_b101)
				{
					_b101=true;
					_p1[0]=stream;
					appdomain.Invoke(_m101,instance,_p1);
					_p1[0]=null;
					_b101=false;
					
				}
				else
				{
					base.readExchangeGroup(stream);
				}
			}
			
			IMethod _m102;
			bool _g102;
			bool _b102;
			protected override void readUILogic(BytesReadStream stream)
			{
				if(!_g102)
				{
					_m102=instance.Type.GetMethod("readUILogic",1);
					_g102=true;
				}
				
				if(_m102!=null && !_b102)
				{
					_b102=true;
					_p1[0]=stream;
					appdomain.Invoke(_m102,instance,_p1);
					_p1[0]=null;
					_b102=false;
					
				}
				else
				{
					base.readUILogic(stream);
				}
			}
			
			IMethod _m103;
			bool _g103;
			bool _b103;
			protected override void readMonsterLevel(BytesReadStream stream)
			{
				if(!_g103)
				{
					_m103=instance.Type.GetMethod("readMonsterLevel",1);
					_g103=true;
				}
				
				if(_m103!=null && !_b103)
				{
					_b103=true;
					_p1[0]=stream;
					appdomain.Invoke(_m103,instance,_p1);
					_p1[0]=null;
					_b103=false;
					
				}
				else
				{
					base.readMonsterLevel(stream);
				}
			}
			
			IMethod _m104;
			bool _g104;
			bool _b104;
			protected override void readUnitModelSlotType(BytesReadStream stream)
			{
				if(!_g104)
				{
					_m104=instance.Type.GetMethod("readUnitModelSlotType",1);
					_g104=true;
				}
				
				if(_m104!=null && !_b104)
				{
					_b104=true;
					_p1[0]=stream;
					appdomain.Invoke(_m104,instance,_p1);
					_p1[0]=null;
					_b104=false;
					
				}
				else
				{
					base.readUnitModelSlotType(stream);
				}
			}
			
			IMethod _m105;
			bool _g105;
			bool _b105;
			protected override void readRoleAttribute(BytesReadStream stream)
			{
				if(!_g105)
				{
					_m105=instance.Type.GetMethod("readRoleAttribute",1);
					_g105=true;
				}
				
				if(_m105!=null && !_b105)
				{
					_b105=true;
					_p1[0]=stream;
					appdomain.Invoke(_m105,instance,_p1);
					_p1[0]=null;
					_b105=false;
					
				}
				else
				{
					base.readRoleAttribute(stream);
				}
			}
			
			IMethod _m106;
			bool _g106;
			bool _b106;
			protected override void readChatChannel(BytesReadStream stream)
			{
				if(!_g106)
				{
					_m106=instance.Type.GetMethod("readChatChannel",1);
					_g106=true;
				}
				
				if(_m106!=null && !_b106)
				{
					_b106=true;
					_p1[0]=stream;
					appdomain.Invoke(_m106,instance,_p1);
					_p1[0]=null;
					_b106=false;
					
				}
				else
				{
					base.readChatChannel(stream);
				}
			}
			
			IMethod _m107;
			bool _g107;
			bool _b107;
			protected override void readRegion(BytesReadStream stream)
			{
				if(!_g107)
				{
					_m107=instance.Type.GetMethod("readRegion",1);
					_g107=true;
				}
				
				if(_m107!=null && !_b107)
				{
					_b107=true;
					_p1[0]=stream;
					appdomain.Invoke(_m107,instance,_p1);
					_p1[0]=null;
					_b107=false;
					
				}
				else
				{
					base.readRegion(stream);
				}
			}
			
			IMethod _m108;
			bool _g108;
			bool _b108;
			protected override void readClientPlatformType(BytesReadStream stream)
			{
				if(!_g108)
				{
					_m108=instance.Type.GetMethod("readClientPlatformType",1);
					_g108=true;
				}
				
				if(_m108!=null && !_b108)
				{
					_b108=true;
					_p1[0]=stream;
					appdomain.Invoke(_m108,instance,_p1);
					_p1[0]=null;
					_b108=false;
					
				}
				else
				{
					base.readClientPlatformType(stream);
				}
			}
			
			IMethod _m109;
			bool _g109;
			bool _b109;
			protected override void readMapBlockType(BytesReadStream stream)
			{
				if(!_g109)
				{
					_m109=instance.Type.GetMethod("readMapBlockType",1);
					_g109=true;
				}
				
				if(_m109!=null && !_b109)
				{
					_b109=true;
					_p1[0]=stream;
					appdomain.Invoke(_m109,instance,_p1);
					_p1[0]=null;
					_b109=false;
					
				}
				else
				{
					base.readMapBlockType(stream);
				}
			}
			
			IMethod _m110;
			bool _g110;
			bool _b110;
			protected override void readBuffActionType(BytesReadStream stream)
			{
				if(!_g110)
				{
					_m110=instance.Type.GetMethod("readBuffActionType",1);
					_g110=true;
				}
				
				if(_m110!=null && !_b110)
				{
					_b110=true;
					_p1[0]=stream;
					appdomain.Invoke(_m110,instance,_p1);
					_p1[0]=null;
					_b110=false;
					
				}
				else
				{
					base.readBuffActionType(stream);
				}
			}
			
			IMethod _m111;
			bool _g111;
			bool _b111;
			protected override void readSceneForceType(BytesReadStream stream)
			{
				if(!_g111)
				{
					_m111=instance.Type.GetMethod("readSceneForceType",1);
					_g111=true;
				}
				
				if(_m111!=null && !_b111)
				{
					_b111=true;
					_p1[0]=stream;
					appdomain.Invoke(_m111,instance,_p1);
					_p1[0]=null;
					_b111=false;
					
				}
				else
				{
					base.readSceneForceType(stream);
				}
			}
			
			IMethod _m112;
			bool _g112;
			bool _b112;
			protected override void readVehicle(BytesReadStream stream)
			{
				if(!_g112)
				{
					_m112=instance.Type.GetMethod("readVehicle",1);
					_g112=true;
				}
				
				if(_m112!=null && !_b112)
				{
					_b112=true;
					_p1[0]=stream;
					appdomain.Invoke(_m112,instance,_p1);
					_p1[0]=null;
					_b112=false;
					
				}
				else
				{
					base.readVehicle(stream);
				}
			}
			
			IMethod _m113;
			bool _g113;
			bool _b113;
			protected override void readMapMoveType(BytesReadStream stream)
			{
				if(!_g113)
				{
					_m113=instance.Type.GetMethod("readMapMoveType",1);
					_g113=true;
				}
				
				if(_m113!=null && !_b113)
				{
					_b113=true;
					_p1[0]=stream;
					appdomain.Invoke(_m113,instance,_p1);
					_p1[0]=null;
					_b113=false;
					
				}
				else
				{
					base.readMapMoveType(stream);
				}
			}
			
			IMethod _m114;
			bool _g114;
			bool _b114;
			protected override void readAuction(BytesReadStream stream)
			{
				if(!_g114)
				{
					_m114=instance.Type.GetMethod("readAuction",1);
					_g114=true;
				}
				
				if(_m114!=null && !_b114)
				{
					_b114=true;
					_p1[0]=stream;
					appdomain.Invoke(_m114,instance,_p1);
					_p1[0]=null;
					_b114=false;
					
				}
				else
				{
					base.readAuction(stream);
				}
			}
			
			IMethod _m115;
			bool _g115;
			bool _b115;
			protected override void readAuctionQueryConditionType(BytesReadStream stream)
			{
				if(!_g115)
				{
					_m115=instance.Type.GetMethod("readAuctionQueryConditionType",1);
					_g115=true;
				}
				
				if(_m115!=null && !_b115)
				{
					_b115=true;
					_p1[0]=stream;
					appdomain.Invoke(_m115,instance,_p1);
					_p1[0]=null;
					_b115=false;
					
				}
				else
				{
					base.readAuctionQueryConditionType(stream);
				}
			}
			
			IMethod _m116;
			bool _g116;
			bool _b116;
			protected override void readTeamTarget(BytesReadStream stream)
			{
				if(!_g116)
				{
					_m116=instance.Type.GetMethod("readTeamTarget",1);
					_g116=true;
				}
				
				if(_m116!=null && !_b116)
				{
					_b116=true;
					_p1[0]=stream;
					appdomain.Invoke(_m116,instance,_p1);
					_p1[0]=null;
					_b116=false;
					
				}
				else
				{
					base.readTeamTarget(stream);
				}
			}
			
			IMethod _m117;
			bool _g117;
			bool _b117;
			protected override void readSubsectionRank(BytesReadStream stream)
			{
				if(!_g117)
				{
					_m117=instance.Type.GetMethod("readSubsectionRank",1);
					_g117=true;
				}
				
				if(_m117!=null && !_b117)
				{
					_b117=true;
					_p1[0]=stream;
					appdomain.Invoke(_m117,instance,_p1);
					_p1[0]=null;
					_b117=false;
					
				}
				else
				{
					base.readSubsectionRank(stream);
				}
			}
			
			IMethod _m118;
			bool _g118;
			bool _b118;
			protected override void readInitCreate(BytesReadStream stream)
			{
				if(!_g118)
				{
					_m118=instance.Type.GetMethod("readInitCreate",1);
					_g118=true;
				}
				
				if(_m118!=null && !_b118)
				{
					_b118=true;
					_p1[0]=stream;
					appdomain.Invoke(_m118,instance,_p1);
					_p1[0]=null;
					_b118=false;
					
				}
				else
				{
					base.readInitCreate(stream);
				}
			}
			
			IMethod _m119;
			bool _g119;
			bool _b119;
			protected override void readPushTopicType(BytesReadStream stream)
			{
				if(!_g119)
				{
					_m119=instance.Type.GetMethod("readPushTopicType",1);
					_g119=true;
				}
				
				if(_m119!=null && !_b119)
				{
					_b119=true;
					_p1[0]=stream;
					appdomain.Invoke(_m119,instance,_p1);
					_p1[0]=null;
					_b119=false;
					
				}
				else
				{
					base.readPushTopicType(stream);
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
