using System;
using System.Text;
using ILRuntime.CLR.Method;
using ILRuntime.Runtime.Enviorment;
using ILRuntime.Runtime.Intepreter;
using UnityEngine;
using AppDomain = ILRuntime.Runtime.Enviorment.AppDomain;
using ShineEngine;

	public class GameFactoryControlAdapter : CrossBindingAdaptor
	{
		public override Type BaseCLRType
		{
			get
			{
				return typeof(GameFactoryControl);
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

		public class Adaptor : GameFactoryControl, CrossBindingAdaptorType
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

			

			IMethod _m0;
			bool _g0;
			bool _b0;
			public override GameDataRegister createDataRegister()
			{
				if(!_g0)
				{
					_m0=instance.Type.GetMethod("createDataRegister",0);
					_g0=true;
				}
				
				if(_m0!=null && !_b0)
				{
					_b0=true;
					GameDataRegister re=(GameDataRegister)appdomain.Invoke(_m0,instance,null);
					_b0=false;
					return re;
					
				}
				else
				{
					return base.createDataRegister();
				}
			}
			
			IMethod _m1;
			bool _g1;
			bool _b1;
			public override ConfigControl createConfigControl()
			{
				if(!_g1)
				{
					_m1=instance.Type.GetMethod("createConfigControl",0);
					_g1=true;
				}
				
				if(_m1!=null && !_b1)
				{
					_b1=true;
					ConfigControl re=(ConfigControl)appdomain.Invoke(_m1,instance,null);
					_b1=false;
					return re;
					
				}
				else
				{
					return base.createConfigControl();
				}
			}
			
			IMethod _m2;
			bool _g2;
			bool _b2;
			public override GameMainControl createGameMainControl()
			{
				if(!_g2)
				{
					_m2=instance.Type.GetMethod("createGameMainControl",0);
					_g2=true;
				}
				
				if(_m2!=null && !_b2)
				{
					_b2=true;
					GameMainControl re=(GameMainControl)appdomain.Invoke(_m2,instance,null);
					_b2=false;
					return re;
					
				}
				else
				{
					return base.createGameMainControl();
				}
			}
			
			IMethod _m3;
			bool _g3;
			bool _b3;
			public override GameServer createGameServer()
			{
				if(!_g3)
				{
					_m3=instance.Type.GetMethod("createGameServer",0);
					_g3=true;
				}
				
				if(_m3!=null && !_b3)
				{
					_b3=true;
					GameServer re=(GameServer)appdomain.Invoke(_m3,instance,null);
					_b3=false;
					return re;
					
				}
				else
				{
					return base.createGameServer();
				}
			}
			
			IMethod _m4;
			bool _g4;
			bool _b4;
			public override Player createPlayer()
			{
				if(!_g4)
				{
					_m4=instance.Type.GetMethod("createPlayer",0);
					_g4=true;
				}
				
				if(_m4!=null && !_b4)
				{
					_b4=true;
					Player re=(Player)appdomain.Invoke(_m4,instance,null);
					_b4=false;
					return re;
					
				}
				else
				{
					return base.createPlayer();
				}
			}
			
			IMethod _m5;
			bool _g5;
			bool _b5;
			public override GameUIControl createGameUIControl()
			{
				if(!_g5)
				{
					_m5=instance.Type.GetMethod("createGameUIControl",0);
					_g5=true;
				}
				
				if(_m5!=null && !_b5)
				{
					_b5=true;
					GameUIControl re=(GameUIControl)appdomain.Invoke(_m5,instance,null);
					_b5=false;
					return re;
					
				}
				else
				{
					return base.createGameUIControl();
				}
			}
			
			IMethod _m6;
			bool _g6;
			bool _b6;
			public override InfoControl createInfoControl()
			{
				if(!_g6)
				{
					_m6=instance.Type.GetMethod("createInfoControl",0);
					_g6=true;
				}
				
				if(_m6!=null && !_b6)
				{
					_b6=true;
					InfoControl re=(InfoControl)appdomain.Invoke(_m6,instance,null);
					_b6=false;
					return re;
					
				}
				else
				{
					return base.createInfoControl();
				}
			}
			
			IMethod _m7;
			bool _g7;
			bool _b7;
			public override KeyboardControl createKeyboardControl()
			{
				if(!_g7)
				{
					_m7=instance.Type.GetMethod("createKeyboardControl",0);
					_g7=true;
				}
				
				if(_m7!=null && !_b7)
				{
					_b7=true;
					KeyboardControl re=(KeyboardControl)appdomain.Invoke(_m7,instance,null);
					_b7=false;
					return re;
					
				}
				else
				{
					return base.createKeyboardControl();
				}
			}
			
			IMethod _m8;
			bool _g8;
			bool _b8;
			public override TouchControl createTouchControl()
			{
				if(!_g8)
				{
					_m8=instance.Type.GetMethod("createTouchControl",0);
					_g8=true;
				}
				
				if(_m8!=null && !_b8)
				{
					_b8=true;
					TouchControl re=(TouchControl)appdomain.Invoke(_m8,instance,null);
					_b8=false;
					return re;
					
				}
				else
				{
					return base.createTouchControl();
				}
			}
			
			IMethod _m9;
			bool _g9;
			bool _b9;
			public override RedPointControl createRedPointControl()
			{
				if(!_g9)
				{
					_m9=instance.Type.GetMethod("createRedPointControl",0);
					_g9=true;
				}
				
				if(_m9!=null && !_b9)
				{
					_b9=true;
					RedPointControl re=(RedPointControl)appdomain.Invoke(_m9,instance,null);
					_b9=false;
					return re;
					
				}
				else
				{
					return base.createRedPointControl();
				}
			}
			
			IMethod _m10;
			bool _g10;
			bool _b10;
			public override BaseLogicControl createBaseLogicControl()
			{
				if(!_g10)
				{
					_m10=instance.Type.GetMethod("createBaseLogicControl",0);
					_g10=true;
				}
				
				if(_m10!=null && !_b10)
				{
					_b10=true;
					BaseLogicControl re=(BaseLogicControl)appdomain.Invoke(_m10,instance,null);
					_b10=false;
					return re;
					
				}
				else
				{
					return base.createBaseLogicControl();
				}
			}
			
			IMethod _m11;
			bool _g11;
			bool _b11;
			public override BaseConstControl createBaseConstControl()
			{
				if(!_g11)
				{
					_m11=instance.Type.GetMethod("createBaseConstControl",0);
					_g11=true;
				}
				
				if(_m11!=null && !_b11)
				{
					_b11=true;
					BaseConstControl re=(BaseConstControl)appdomain.Invoke(_m11,instance,null);
					_b11=false;
					return re;
					
				}
				else
				{
					return base.createBaseConstControl();
				}
			}
			
			IMethod _m12;
			bool _g12;
			bool _b12;
			public override SceneControl createSceneControl()
			{
				if(!_g12)
				{
					_m12=instance.Type.GetMethod("createSceneControl",0);
					_g12=true;
				}
				
				if(_m12!=null && !_b12)
				{
					_b12=true;
					SceneControl re=(SceneControl)appdomain.Invoke(_m12,instance,null);
					_b12=false;
					return re;
					
				}
				else
				{
					return base.createSceneControl();
				}
			}
			
			IMethod _m13;
			bool _g13;
			bool _b13;
			public override GameOfflineControl createGameOfflineControl()
			{
				if(!_g13)
				{
					_m13=instance.Type.GetMethod("createGameOfflineControl",0);
					_g13=true;
				}
				
				if(_m13!=null && !_b13)
				{
					_b13=true;
					GameOfflineControl re=(GameOfflineControl)appdomain.Invoke(_m13,instance,null);
					_b13=false;
					return re;
					
				}
				else
				{
					return base.createGameOfflineControl();
				}
			}
			
			IMethod _m14;
			bool _g14;
			bool _b14;
			public override PlayerSaveControl createPlayerSaveControl()
			{
				if(!_g14)
				{
					_m14=instance.Type.GetMethod("createPlayerSaveControl",0);
					_g14=true;
				}
				
				if(_m14!=null && !_b14)
				{
					_b14=true;
					PlayerSaveControl re=(PlayerSaveControl)appdomain.Invoke(_m14,instance,null);
					_b14=false;
					return re;
					
				}
				else
				{
					return base.createPlayerSaveControl();
				}
			}
			
			IMethod _m15;
			bool _g15;
			bool _b15;
			public override GameLogControl createGameLogControl()
			{
				if(!_g15)
				{
					_m15=instance.Type.GetMethod("createGameLogControl",0);
					_g15=true;
				}
				
				if(_m15!=null && !_b15)
				{
					_b15=true;
					GameLogControl re=(GameLogControl)appdomain.Invoke(_m15,instance,null);
					_b15=false;
					return re;
					
				}
				else
				{
					return base.createGameLogControl();
				}
			}
			
			IMethod _m16;
			bool _g16;
			bool _b16;
			public override GamePoolControl createGamePoolControl()
			{
				if(!_g16)
				{
					_m16=instance.Type.GetMethod("createGamePoolControl",0);
					_g16=true;
				}
				
				if(_m16!=null && !_b16)
				{
					_b16=true;
					GamePoolControl re=(GamePoolControl)appdomain.Invoke(_m16,instance,null);
					_b16=false;
					return re;
					
				}
				else
				{
					return base.createGamePoolControl();
				}
			}
			
			IMethod _m17;
			bool _g17;
			bool _b17;
			public override ClientGMControl createClientGMControl()
			{
				if(!_g17)
				{
					_m17=instance.Type.GetMethod("createClientGMControl",0);
					_g17=true;
				}
				
				if(_m17!=null && !_b17)
				{
					_b17=true;
					ClientGMControl re=(ClientGMControl)appdomain.Invoke(_m17,instance,null);
					_b17=false;
					return re;
					
				}
				else
				{
					return base.createClientGMControl();
				}
			}
			
			IMethod _m18;
			bool _g18;
			bool _b18;
			public override TriggerControl createTriggerControl()
			{
				if(!_g18)
				{
					_m18=instance.Type.GetMethod("createTriggerControl",0);
					_g18=true;
				}
				
				if(_m18!=null && !_b18)
				{
					_b18=true;
					TriggerControl re=(TriggerControl)appdomain.Invoke(_m18,instance,null);
					_b18=false;
					return re;
					
				}
				else
				{
					return base.createTriggerControl();
				}
			}
			
			IMethod _m19;
			bool _g19;
			bool _b19;
			public override GuideControl createGuideControl()
			{
				if(!_g19)
				{
					_m19=instance.Type.GetMethod("createGuideControl",0);
					_g19=true;
				}
				
				if(_m19!=null && !_b19)
				{
					_b19=true;
					GuideControl re=(GuideControl)appdomain.Invoke(_m19,instance,null);
					_b19=false;
					return re;
					
				}
				else
				{
					return base.createGuideControl();
				}
			}
			
			IMethod _m20;
			bool _g20;
			bool _b20;
			public override GuideTriggerExecutor createGuideTriggerExecutor()
			{
				if(!_g20)
				{
					_m20=instance.Type.GetMethod("createGuideTriggerExecutor",0);
					_g20=true;
				}
				
				if(_m20!=null && !_b20)
				{
					_b20=true;
					GuideTriggerExecutor re=(GuideTriggerExecutor)appdomain.Invoke(_m20,instance,null);
					_b20=false;
					return re;
					
				}
				else
				{
					return base.createGuideTriggerExecutor();
				}
			}
			
			IMethod _m21;
			bool _g21;
			bool _b21;
			public override ConfigReadData createConfigReadData()
			{
				if(!_g21)
				{
					_m21=instance.Type.GetMethod("createConfigReadData",0);
					_g21=true;
				}
				
				if(_m21!=null && !_b21)
				{
					_b21=true;
					ConfigReadData re=(ConfigReadData)appdomain.Invoke(_m21,instance,null);
					_b21=false;
					return re;
					
				}
				else
				{
					return base.createConfigReadData();
				}
			}
			
			IMethod _m22;
			bool _g22;
			bool _b22;
			public override GlobalReadData createGlobalReadData()
			{
				if(!_g22)
				{
					_m22=instance.Type.GetMethod("createGlobalReadData",0);
					_g22=true;
				}
				
				if(_m22!=null && !_b22)
				{
					_b22=true;
					GlobalReadData re=(GlobalReadData)appdomain.Invoke(_m22,instance,null);
					_b22=false;
					return re;
					
				}
				else
				{
					return base.createGlobalReadData();
				}
			}
			
			IMethod _m23;
			bool _g23;
			bool _b23;
			public override RoleShowData createRoleShowData()
			{
				if(!_g23)
				{
					_m23=instance.Type.GetMethod("createRoleShowData",0);
					_g23=true;
				}
				
				if(_m23!=null && !_b23)
				{
					_b23=true;
					RoleShowData re=(RoleShowData)appdomain.Invoke(_m23,instance,null);
					_b23=false;
					return re;
					
				}
				else
				{
					return base.createRoleShowData();
				}
			}
			
			IMethod _m24;
			bool _g24;
			bool _b24;
			public override RoleSimpleShowData createRoleSimpleShowData()
			{
				if(!_g24)
				{
					_m24=instance.Type.GetMethod("createRoleSimpleShowData",0);
					_g24=true;
				}
				
				if(_m24!=null && !_b24)
				{
					_b24=true;
					RoleSimpleShowData re=(RoleSimpleShowData)appdomain.Invoke(_m24,instance,null);
					_b24=false;
					return re;
					
				}
				else
				{
					return base.createRoleSimpleShowData();
				}
			}
			
			IMethod _m25;
			bool _g25;
			bool _b25;
			public override UnitData createUnitData()
			{
				if(!_g25)
				{
					_m25=instance.Type.GetMethod("createUnitData",0);
					_g25=true;
				}
				
				if(_m25!=null && !_b25)
				{
					_b25=true;
					UnitData re=(UnitData)appdomain.Invoke(_m25,instance,null);
					_b25=false;
					return re;
					
				}
				else
				{
					return base.createUnitData();
				}
			}
			
			IMethod _m26;
			bool _g26;
			bool _b26;
			public override UnitSimpleData createUnitSimpleData()
			{
				if(!_g26)
				{
					_m26=instance.Type.GetMethod("createUnitSimpleData",0);
					_g26=true;
				}
				
				if(_m26!=null && !_b26)
				{
					_b26=true;
					UnitSimpleData re=(UnitSimpleData)appdomain.Invoke(_m26,instance,null);
					_b26=false;
					return re;
					
				}
				else
				{
					return base.createUnitSimpleData();
				}
			}
			
			IMethod _m27;
			bool _g27;
			bool _b27;
			public override CharacterIdentityData createCharacterIdentityData()
			{
				if(!_g27)
				{
					_m27=instance.Type.GetMethod("createCharacterIdentityData",0);
					_g27=true;
				}
				
				if(_m27!=null && !_b27)
				{
					_b27=true;
					CharacterIdentityData re=(CharacterIdentityData)appdomain.Invoke(_m27,instance,null);
					_b27=false;
					return re;
					
				}
				else
				{
					return base.createCharacterIdentityData();
				}
			}
			
			IMethod _m28;
			bool _g28;
			bool _b28;
			public override ItemData createItemData()
			{
				if(!_g28)
				{
					_m28=instance.Type.GetMethod("createItemData",0);
					_g28=true;
				}
				
				if(_m28!=null && !_b28)
				{
					_b28=true;
					ItemData re=(ItemData)appdomain.Invoke(_m28,instance,null);
					_b28=false;
					return re;
					
				}
				else
				{
					return base.createItemData();
				}
			}
			
			IMethod _m29;
			bool _g29;
			bool _b29;
			public override ItemIdentityData createItemIdentityData()
			{
				if(!_g29)
				{
					_m29=instance.Type.GetMethod("createItemIdentityData",0);
					_g29=true;
				}
				
				if(_m29!=null && !_b29)
				{
					_b29=true;
					ItemIdentityData re=(ItemIdentityData)appdomain.Invoke(_m29,instance,null);
					_b29=false;
					return re;
					
				}
				else
				{
					return base.createItemIdentityData();
				}
			}
			
			IMethod _m30;
			bool _g30;
			bool _b30;
			public override CharacterUseData createCharacterUseData()
			{
				if(!_g30)
				{
					_m30=instance.Type.GetMethod("createCharacterUseData",0);
					_g30=true;
				}
				
				if(_m30!=null && !_b30)
				{
					_b30=true;
					CharacterUseData re=(CharacterUseData)appdomain.Invoke(_m30,instance,null);
					_b30=false;
					return re;
					
				}
				else
				{
					return base.createCharacterUseData();
				}
			}
			
			IMethod _m31;
			bool _g31;
			bool _b31;
			public override ClientLoginData createClientLoginData()
			{
				if(!_g31)
				{
					_m31=instance.Type.GetMethod("createClientLoginData",0);
					_g31=true;
				}
				
				if(_m31!=null && !_b31)
				{
					_b31=true;
					ClientLoginData re=(ClientLoginData)appdomain.Invoke(_m31,instance,null);
					_b31=false;
					return re;
					
				}
				else
				{
					return base.createClientLoginData();
				}
			}
			
			IMethod _m32;
			bool _g32;
			bool _b32;
			public override RoleSocialData createRoleSocialData()
			{
				if(!_g32)
				{
					_m32=instance.Type.GetMethod("createRoleSocialData",0);
					_g32=true;
				}
				
				if(_m32!=null && !_b32)
				{
					_b32=true;
					RoleSocialData re=(RoleSocialData)appdomain.Invoke(_m32,instance,null);
					_b32=false;
					return re;
					
				}
				else
				{
					return base.createRoleSocialData();
				}
			}
			
			IMethod _m33;
			bool _g33;
			bool _b33;
			public override PlayerRankData createPlayerRankData()
			{
				if(!_g33)
				{
					_m33=instance.Type.GetMethod("createPlayerRankData",0);
					_g33=true;
				}
				
				if(_m33!=null && !_b33)
				{
					_b33=true;
					PlayerRankData re=(PlayerRankData)appdomain.Invoke(_m33,instance,null);
					_b33=false;
					return re;
					
				}
				else
				{
					return base.createPlayerRankData();
				}
			}
			
			IMethod _m34;
			bool _g34;
			bool _b34;
			public override PlayerMatchData createPlayerMatchData()
			{
				if(!_g34)
				{
					_m34=instance.Type.GetMethod("createPlayerMatchData",0);
					_g34=true;
				}
				
				if(_m34!=null && !_b34)
				{
					_b34=true;
					PlayerMatchData re=(PlayerMatchData)appdomain.Invoke(_m34,instance,null);
					_b34=false;
					return re;
					
				}
				else
				{
					return base.createPlayerMatchData();
				}
			}
			
			IMethod _m35;
			bool _g35;
			bool _b35;
			public override ItemEquipData createItemEquipData()
			{
				if(!_g35)
				{
					_m35=instance.Type.GetMethod("createItemEquipData",0);
					_g35=true;
				}
				
				if(_m35!=null && !_b35)
				{
					_b35=true;
					ItemEquipData re=(ItemEquipData)appdomain.Invoke(_m35,instance,null);
					_b35=false;
					return re;
					
				}
				else
				{
					return base.createItemEquipData();
				}
			}
			
			IMethod _m36;
			bool _g36;
			bool _b36;
			public override MailData createMailData()
			{
				if(!_g36)
				{
					_m36=instance.Type.GetMethod("createMailData",0);
					_g36=true;
				}
				
				if(_m36!=null && !_b36)
				{
					_b36=true;
					MailData re=(MailData)appdomain.Invoke(_m36,instance,null);
					_b36=false;
					return re;
					
				}
				else
				{
					return base.createMailData();
				}
			}
			
			IMethod _m37;
			bool _g37;
			bool _b37;
			public override FriendData createFriendData()
			{
				if(!_g37)
				{
					_m37=instance.Type.GetMethod("createFriendData",0);
					_g37=true;
				}
				
				if(_m37!=null && !_b37)
				{
					_b37=true;
					FriendData re=(FriendData)appdomain.Invoke(_m37,instance,null);
					_b37=false;
					return re;
					
				}
				else
				{
					return base.createFriendData();
				}
			}
			
			IMethod _m38;
			bool _g38;
			bool _b38;
			public override UnitFightDataLogic createUnitFightDataLogic()
			{
				if(!_g38)
				{
					_m38=instance.Type.GetMethod("createUnitFightDataLogic",0);
					_g38=true;
				}
				
				if(_m38!=null && !_b38)
				{
					_b38=true;
					UnitFightDataLogic re=(UnitFightDataLogic)appdomain.Invoke(_m38,instance,null);
					_b38=false;
					return re;
					
				}
				else
				{
					return base.createUnitFightDataLogic();
				}
			}
			
			IMethod _m39;
			bool _g39;
			bool _b39;
			public override BuffDataLogic createBuffDataLogic()
			{
				if(!_g39)
				{
					_m39=instance.Type.GetMethod("createBuffDataLogic",0);
					_g39=true;
				}
				
				if(_m39!=null && !_b39)
				{
					_b39=true;
					BuffDataLogic re=(BuffDataLogic)appdomain.Invoke(_m39,instance,null);
					_b39=false;
					return re;
					
				}
				else
				{
					return base.createBuffDataLogic();
				}
			}
			
			IMethod _m40;
			bool _g40;
			bool _b40;
			public override Scene createScene()
			{
				if(!_g40)
				{
					_m40=instance.Type.GetMethod("createScene",0);
					_g40=true;
				}
				
				if(_m40!=null && !_b40)
				{
					_b40=true;
					Scene re=(Scene)appdomain.Invoke(_m40,instance,null);
					_b40=false;
					return re;
					
				}
				else
				{
					return base.createScene();
				}
			}
			
			IMethod _m41;
			bool _g41;
			bool _b41;
			public override Unit createUnit()
			{
				if(!_g41)
				{
					_m41=instance.Type.GetMethod("createUnit",0);
					_g41=true;
				}
				
				if(_m41!=null && !_b41)
				{
					_b41=true;
					Unit re=(Unit)appdomain.Invoke(_m41,instance,null);
					_b41=false;
					return re;
					
				}
				else
				{
					return base.createUnit();
				}
			}
			
			IMethod _m42;
			bool _g42;
			bool _b42;
			public override Bullet createBullet()
			{
				if(!_g42)
				{
					_m42=instance.Type.GetMethod("createBullet",0);
					_g42=true;
				}
				
				if(_m42!=null && !_b42)
				{
					_b42=true;
					Bullet re=(Bullet)appdomain.Invoke(_m42,instance,null);
					_b42=false;
					return re;
					
				}
				else
				{
					return base.createBullet();
				}
			}
			
			IMethod _m43;
			bool _g43;
			bool _b43;
			public override UnitEffect createUnitEffect()
			{
				if(!_g43)
				{
					_m43=instance.Type.GetMethod("createUnitEffect",0);
					_g43=true;
				}
				
				if(_m43!=null && !_b43)
				{
					_b43=true;
					UnitEffect re=(UnitEffect)appdomain.Invoke(_m43,instance,null);
					_b43=false;
					return re;
					
				}
				else
				{
					return base.createUnitEffect();
				}
			}
			
			IMethod _m44;
			bool _g44;
			bool _b44;
			public override CharacterUseLogic createCharacterUseLogic()
			{
				if(!_g44)
				{
					_m44=instance.Type.GetMethod("createCharacterUseLogic",0);
					_g44=true;
				}
				
				if(_m44!=null && !_b44)
				{
					_b44=true;
					CharacterUseLogic re=(CharacterUseLogic)appdomain.Invoke(_m44,instance,null);
					_b44=false;
					return re;
					
				}
				else
				{
					return base.createCharacterUseLogic();
				}
			}
			
			IMethod _m45;
			bool _g45;
			bool _b45;
			public override ItemTipsReplaceTextTool createItemTipsReplaceTextTool()
			{
				if(!_g45)
				{
					_m45=instance.Type.GetMethod("createItemTipsReplaceTextTool",0);
					_g45=true;
				}
				
				if(_m45!=null && !_b45)
				{
					_b45=true;
					ItemTipsReplaceTextTool re=(ItemTipsReplaceTextTool)appdomain.Invoke(_m45,instance,null);
					_b45=false;
					return re;
					
				}
				else
				{
					return base.createItemTipsReplaceTextTool();
				}
			}
			
			IMethod _m46;
			bool _g46;
			bool _b46;
			public override TaskDescribeReplaceTextTool createTaskDescribeReplaceTextTool()
			{
				if(!_g46)
				{
					_m46=instance.Type.GetMethod("createTaskDescribeReplaceTextTool",0);
					_g46=true;
				}
				
				if(_m46!=null && !_b46)
				{
					_b46=true;
					TaskDescribeReplaceTextTool re=(TaskDescribeReplaceTextTool)appdomain.Invoke(_m46,instance,null);
					_b46=false;
					return re;
					
				}
				else
				{
					return base.createTaskDescribeReplaceTextTool();
				}
			}
			
			IMethod _m47;
			bool _g47;
			bool _b47;
			public override AchievementCompleteData createAchievementCompleteData()
			{
				if(!_g47)
				{
					_m47=instance.Type.GetMethod("createAchievementCompleteData",0);
					_g47=true;
				}
				
				if(_m47!=null && !_b47)
				{
					_b47=true;
					AchievementCompleteData re=(AchievementCompleteData)appdomain.Invoke(_m47,instance,null);
					_b47=false;
					return re;
					
				}
				else
				{
					return base.createAchievementCompleteData();
				}
			}
			
			IMethod _m48;
			bool _g48;
			bool _b48;
			public override AchievementData createAchievementData()
			{
				if(!_g48)
				{
					_m48=instance.Type.GetMethod("createAchievementData",0);
					_g48=true;
				}
				
				if(_m48!=null && !_b48)
				{
					_b48=true;
					AchievementData re=(AchievementData)appdomain.Invoke(_m48,instance,null);
					_b48=false;
					return re;
					
				}
				else
				{
					return base.createAchievementData();
				}
			}
			
			IMethod _m49;
			bool _g49;
			bool _b49;
			public override AchievementPart createAchievementPart()
			{
				if(!_g49)
				{
					_m49=instance.Type.GetMethod("createAchievementPart",0);
					_g49=true;
				}
				
				if(_m49!=null && !_b49)
				{
					_b49=true;
					AchievementPart re=(AchievementPart)appdomain.Invoke(_m49,instance,null);
					_b49=false;
					return re;
					
				}
				else
				{
					return base.createAchievementPart();
				}
			}
			
			IMethod _m50;
			bool _g50;
			bool _b50;
			public override AchievementSaveData createAchievementSaveData()
			{
				if(!_g50)
				{
					_m50=instance.Type.GetMethod("createAchievementSaveData",0);
					_g50=true;
				}
				
				if(_m50!=null && !_b50)
				{
					_b50=true;
					AchievementSaveData re=(AchievementSaveData)appdomain.Invoke(_m50,instance,null);
					_b50=false;
					return re;
					
				}
				else
				{
					return base.createAchievementSaveData();
				}
			}
			
			IMethod _m51;
			bool _g51;
			bool _b51;
			public override ActivityData createActivityData()
			{
				if(!_g51)
				{
					_m51=instance.Type.GetMethod("createActivityData",0);
					_g51=true;
				}
				
				if(_m51!=null && !_b51)
				{
					_b51=true;
					ActivityData re=(ActivityData)appdomain.Invoke(_m51,instance,null);
					_b51=false;
					return re;
					
				}
				else
				{
					return base.createActivityData();
				}
			}
			
			IMethod _m52;
			bool _g52;
			bool _b52;
			public override ActivityPart createActivityPart()
			{
				if(!_g52)
				{
					_m52=instance.Type.GetMethod("createActivityPart",0);
					_g52=true;
				}
				
				if(_m52!=null && !_b52)
				{
					_b52=true;
					ActivityPart re=(ActivityPart)appdomain.Invoke(_m52,instance,null);
					_b52=false;
					return re;
					
				}
				else
				{
					return base.createActivityPart();
				}
			}
			
			IMethod _m53;
			bool _g53;
			bool _b53;
			public override ApplyAddFriendData createApplyAddFriendData()
			{
				if(!_g53)
				{
					_m53=instance.Type.GetMethod("createApplyAddFriendData",0);
					_g53=true;
				}
				
				if(_m53!=null && !_b53)
				{
					_b53=true;
					ApplyAddFriendData re=(ApplyAddFriendData)appdomain.Invoke(_m53,instance,null);
					_b53=false;
					return re;
					
				}
				else
				{
					return base.createApplyAddFriendData();
				}
			}
			
			IMethod _m54;
			bool _g54;
			bool _b54;
			public override AreaClientData createAreaClientData()
			{
				if(!_g54)
				{
					_m54=instance.Type.GetMethod("createAreaClientData",0);
					_g54=true;
				}
				
				if(_m54!=null && !_b54)
				{
					_b54=true;
					AreaClientData re=(AreaClientData)appdomain.Invoke(_m54,instance,null);
					_b54=false;
					return re;
					
				}
				else
				{
					return base.createAreaClientData();
				}
			}
			
			IMethod _m55;
			bool _g55;
			bool _b55;
			public override AttackData createAttackData()
			{
				if(!_g55)
				{
					_m55=instance.Type.GetMethod("createAttackData",0);
					_g55=true;
				}
				
				if(_m55!=null && !_b55)
				{
					_b55=true;
					AttackData re=(AttackData)appdomain.Invoke(_m55,instance,null);
					_b55=false;
					return re;
					
				}
				else
				{
					return base.createAttackData();
				}
			}
			
			IMethod _m56;
			bool _g56;
			bool _b56;
			public override BagPart createBagPart()
			{
				if(!_g56)
				{
					_m56=instance.Type.GetMethod("createBagPart",0);
					_g56=true;
				}
				
				if(_m56!=null && !_b56)
				{
					_b56=true;
					BagPart re=(BagPart)appdomain.Invoke(_m56,instance,null);
					_b56=false;
					return re;
					
				}
				else
				{
					return base.createBagPart();
				}
			}
			
			IMethod _m57;
			bool _g57;
			bool _b57;
			public override BaseServer createBaseServer()
			{
				if(!_g57)
				{
					_m57=instance.Type.GetMethod("createBaseServer",0);
					_g57=true;
				}
				
				if(_m57!=null && !_b57)
				{
					_b57=true;
					BaseServer re=(BaseServer)appdomain.Invoke(_m57,instance,null);
					_b57=false;
					return re;
					
				}
				else
				{
					return base.createBaseServer();
				}
			}
			
			IMethod _m60;
			bool _g60;
			bool _b60;
			public override BattleSceneSyncPlayLogic createBattleSceneSyncPlayLogic()
			{
				if(!_g60)
				{
					_m60=instance.Type.GetMethod("createBattleSceneSyncPlayLogic",0);
					_g60=true;
				}
				
				if(_m60!=null && !_b60)
				{
					_b60=true;
					BattleSceneSyncPlayLogic re=(BattleSceneSyncPlayLogic)appdomain.Invoke(_m60,instance,null);
					_b60=false;
					return re;
					
				}
				else
				{
					return base.createBattleSceneSyncPlayLogic();
				}
			}
			
			IMethod _m61;
			bool _g61;
			bool _b61;
			public override BuffData createBuffData()
			{
				if(!_g61)
				{
					_m61=instance.Type.GetMethod("createBuffData",0);
					_g61=true;
				}
				
				if(_m61!=null && !_b61)
				{
					_b61=true;
					BuffData re=(BuffData)appdomain.Invoke(_m61,instance,null);
					_b61=false;
					return re;
					
				}
				else
				{
					return base.createBuffData();
				}
			}
			
			IMethod _m62;
			bool _g62;
			bool _b62;
			public override BuffIntervalActionData createBuffIntervalActionData()
			{
				if(!_g62)
				{
					_m62=instance.Type.GetMethod("createBuffIntervalActionData",0);
					_g62=true;
				}
				
				if(_m62!=null && !_b62)
				{
					_b62=true;
					BuffIntervalActionData re=(BuffIntervalActionData)appdomain.Invoke(_m62,instance,null);
					_b62=false;
					return re;
					
				}
				else
				{
					return base.createBuffIntervalActionData();
				}
			}
			
			IMethod _m63;
			bool _g63;
			bool _b63;
			public override BulletData createBulletData()
			{
				if(!_g63)
				{
					_m63=instance.Type.GetMethod("createBulletData",0);
					_g63=true;
				}
				
				if(_m63!=null && !_b63)
				{
					_b63=true;
					BulletData re=(BulletData)appdomain.Invoke(_m63,instance,null);
					_b63=false;
					return re;
					
				}
				else
				{
					return base.createBulletData();
				}
			}
			
			IMethod _m64;
			bool _g64;
			bool _b64;
			public override BulletLogicBase createBulletLogicBase()
			{
				if(!_g64)
				{
					_m64=instance.Type.GetMethod("createBulletLogicBase",0);
					_g64=true;
				}
				
				if(_m64!=null && !_b64)
				{
					_b64=true;
					BulletLogicBase re=(BulletLogicBase)appdomain.Invoke(_m64,instance,null);
					_b64=false;
					return re;
					
				}
				else
				{
					return base.createBulletLogicBase();
				}
			}
			
			IMethod _m65;
			bool _g65;
			bool _b65;
			public override BulletPosLogic createBulletPosLogic()
			{
				if(!_g65)
				{
					_m65=instance.Type.GetMethod("createBulletPosLogic",0);
					_g65=true;
				}
				
				if(_m65!=null && !_b65)
				{
					_b65=true;
					BulletPosLogic re=(BulletPosLogic)appdomain.Invoke(_m65,instance,null);
					_b65=false;
					return re;
					
				}
				else
				{
					return base.createBulletPosLogic();
				}
			}
			
			IMethod _m66;
			bool _g66;
			bool _b66;
			public override BulletShowLogic createBulletShowLogic()
			{
				if(!_g66)
				{
					_m66=instance.Type.GetMethod("createBulletShowLogic",0);
					_g66=true;
				}
				
				if(_m66!=null && !_b66)
				{
					_b66=true;
					BulletShowLogic re=(BulletShowLogic)appdomain.Invoke(_m66,instance,null);
					_b66=false;
					return re;
					
				}
				else
				{
					return base.createBulletShowLogic();
				}
			}
			
			IMethod _m67;
			bool _g67;
			bool _b67;
			public override CDData createCDData()
			{
				if(!_g67)
				{
					_m67=instance.Type.GetMethod("createCDData",0);
					_g67=true;
				}
				
				if(_m67!=null && !_b67)
				{
					_b67=true;
					CDData re=(CDData)appdomain.Invoke(_m67,instance,null);
					_b67=false;
					return re;
					
				}
				else
				{
					return base.createCDData();
				}
			}
			
			IMethod _m68;
			bool _g68;
			bool _b68;
			public override CharacterIdentityLogic createCharacterIdentityLogic()
			{
				if(!_g68)
				{
					_m68=instance.Type.GetMethod("createCharacterIdentityLogic",0);
					_g68=true;
				}
				
				if(_m68!=null && !_b68)
				{
					_b68=true;
					CharacterIdentityLogic re=(CharacterIdentityLogic)appdomain.Invoke(_m68,instance,null);
					_b68=false;
					return re;
					
				}
				else
				{
					return base.createCharacterIdentityLogic();
				}
			}
			
			IMethod _m69;
			bool _g69;
			bool _b69;
			public override CharacterPart createCharacterPart()
			{
				if(!_g69)
				{
					_m69=instance.Type.GetMethod("createCharacterPart",0);
					_g69=true;
				}
				
				if(_m69!=null && !_b69)
				{
					_b69=true;
					CharacterPart re=(CharacterPart)appdomain.Invoke(_m69,instance,null);
					_b69=false;
					return re;
					
				}
				else
				{
					return base.createCharacterPart();
				}
			}
			
			IMethod _m70;
			bool _g70;
			bool _b70;
			public override CharacterSaveData createCharacterSaveData()
			{
				if(!_g70)
				{
					_m70=instance.Type.GetMethod("createCharacterSaveData",0);
					_g70=true;
				}
				
				if(_m70!=null && !_b70)
				{
					_b70=true;
					CharacterSaveData re=(CharacterSaveData)appdomain.Invoke(_m70,instance,null);
					_b70=false;
					return re;
					
				}
				else
				{
					return base.createCharacterSaveData();
				}
			}
			
			IMethod _m71;
			bool _g71;
			bool _b71;
			public override ChatData createChatData()
			{
				if(!_g71)
				{
					_m71=instance.Type.GetMethod("createChatData",0);
					_g71=true;
				}
				
				if(_m71!=null && !_b71)
				{
					_b71=true;
					ChatData re=(ChatData)appdomain.Invoke(_m71,instance,null);
					_b71=false;
					return re;
					
				}
				else
				{
					return base.createChatData();
				}
			}
			
			IMethod _m72;
			bool _g72;
			bool _b72;
			public override ChatElementData createChatElementData()
			{
				if(!_g72)
				{
					_m72=instance.Type.GetMethod("createChatElementData",0);
					_g72=true;
				}
				
				if(_m72!=null && !_b72)
				{
					_b72=true;
					ChatElementData re=(ChatElementData)appdomain.Invoke(_m72,instance,null);
					_b72=false;
					return re;
					
				}
				else
				{
					return base.createChatElementData();
				}
			}
			
			IMethod _m73;
			bool _g73;
			bool _b73;
			public override ClientLoginExData createClientLoginExData()
			{
				if(!_g73)
				{
					_m73=instance.Type.GetMethod("createClientLoginExData",0);
					_g73=true;
				}
				
				if(_m73!=null && !_b73)
				{
					_b73=true;
					ClientLoginExData re=(ClientLoginExData)appdomain.Invoke(_m73,instance,null);
					_b73=false;
					return re;
					
				}
				else
				{
					return base.createClientLoginExData();
				}
			}
			
			IMethod _m74;
			bool _g74;
			bool _b74;
			public override ClientSimpleScene createClientSimpleScene()
			{
				if(!_g74)
				{
					_m74=instance.Type.GetMethod("createClientSimpleScene",0);
					_g74=true;
				}
				
				if(_m74!=null && !_b74)
				{
					_b74=true;
					ClientSimpleScene re=(ClientSimpleScene)appdomain.Invoke(_m74,instance,null);
					_b74=false;
					return re;
					
				}
				else
				{
					return base.createClientSimpleScene();
				}
			}
			
			IMethod _m75;
			bool _g75;
			bool _b75;
			public override ContactData createContactData()
			{
				if(!_g75)
				{
					_m75=instance.Type.GetMethod("createContactData",0);
					_g75=true;
				}
				
				if(_m75!=null && !_b75)
				{
					_b75=true;
					ContactData re=(ContactData)appdomain.Invoke(_m75,instance,null);
					_b75=false;
					return re;
					
				}
				else
				{
					return base.createContactData();
				}
			}
			
			IMethod _m76;
			bool _g76;
			bool _b76;
			public override CreatePlayerData createCreatePlayerData()
			{
				if(!_g76)
				{
					_m76=instance.Type.GetMethod("createCreatePlayerData",0);
					_g76=true;
				}
				
				if(_m76!=null && !_b76)
				{
					_b76=true;
					CreatePlayerData re=(CreatePlayerData)appdomain.Invoke(_m76,instance,null);
					_b76=false;
					return re;
					
				}
				else
				{
					return base.createCreatePlayerData();
				}
			}
			
			IMethod _m77;
			bool _g77;
			bool _b77;
			public override DamageOneData createDamageOneData()
			{
				if(!_g77)
				{
					_m77=instance.Type.GetMethod("createDamageOneData",0);
					_g77=true;
				}
				
				if(_m77!=null && !_b77)
				{
					_b77=true;
					DamageOneData re=(DamageOneData)appdomain.Invoke(_m77,instance,null);
					_b77=false;
					return re;
					
				}
				else
				{
					return base.createDamageOneData();
				}
			}
			
			IMethod _m78;
			bool _g78;
			bool _b78;
			public override DirData createDirData()
			{
				if(!_g78)
				{
					_m78=instance.Type.GetMethod("createDirData",0);
					_g78=true;
				}
				
				if(_m78!=null && !_b78)
				{
					_b78=true;
					DirData re=(DirData)appdomain.Invoke(_m78,instance,null);
					_b78=false;
					return re;
					
				}
				else
				{
					return base.createDirData();
				}
			}
			
			IMethod _m79;
			bool _g79;
			bool _b79;
			public override EquipPart createEquipPart()
			{
				if(!_g79)
				{
					_m79=instance.Type.GetMethod("createEquipPart",0);
					_g79=true;
				}
				
				if(_m79!=null && !_b79)
				{
					_b79=true;
					EquipPart re=(EquipPart)appdomain.Invoke(_m79,instance,null);
					_b79=false;
					return re;
					
				}
				else
				{
					return base.createEquipPart();
				}
			}
			
			IMethod _m80;
			bool _g80;
			bool _b80;
			public override FrameSyncCommandData createFrameSyncCommandData()
			{
				if(!_g80)
				{
					_m80=instance.Type.GetMethod("createFrameSyncCommandData",0);
					_g80=true;
				}
				
				if(_m80!=null && !_b80)
				{
					_b80=true;
					FrameSyncCommandData re=(FrameSyncCommandData)appdomain.Invoke(_m80,instance,null);
					_b80=false;
					return re;
					
				}
				else
				{
					return base.createFrameSyncCommandData();
				}
			}
			
			IMethod _m81;
			bool _g81;
			bool _b81;
			public override FrameSyncData createFrameSyncData()
			{
				if(!_g81)
				{
					_m81=instance.Type.GetMethod("createFrameSyncData",0);
					_g81=true;
				}
				
				if(_m81!=null && !_b81)
				{
					_b81=true;
					FrameSyncData re=(FrameSyncData)appdomain.Invoke(_m81,instance,null);
					_b81=false;
					return re;
					
				}
				else
				{
					return base.createFrameSyncData();
				}
			}
			
			IMethod _m82;
			bool _g82;
			bool _b82;
			public override FriendPart createFriendPart()
			{
				if(!_g82)
				{
					_m82=instance.Type.GetMethod("createFriendPart",0);
					_g82=true;
				}
				
				if(_m82!=null && !_b82)
				{
					_b82=true;
					FriendPart re=(FriendPart)appdomain.Invoke(_m82,instance,null);
					_b82=false;
					return re;
					
				}
				else
				{
					return base.createFriendPart();
				}
			}
			
			IMethod _m83;
			bool _g83;
			bool _b83;
			public override FuncPart createFuncPart()
			{
				if(!_g83)
				{
					_m83=instance.Type.GetMethod("createFuncPart",0);
					_g83=true;
				}
				
				if(_m83!=null && !_b83)
				{
					_b83=true;
					FuncPart re=(FuncPart)appdomain.Invoke(_m83,instance,null);
					_b83=false;
					return re;
					
				}
				else
				{
					return base.createFuncPart();
				}
			}
			
			IMethod _m84;
			bool _g84;
			bool _b84;
			public override FuncToolData createFuncToolData()
			{
				if(!_g84)
				{
					_m84=instance.Type.GetMethod("createFuncToolData",0);
					_g84=true;
				}
				
				if(_m84!=null && !_b84)
				{
					_b84=true;
					FuncToolData re=(FuncToolData)appdomain.Invoke(_m84,instance,null);
					_b84=false;
					return re;
					
				}
				else
				{
					return base.createFuncToolData();
				}
			}
			
			IMethod _m85;
			bool _g85;
			bool _b85;
			public override GameApp createGameApp()
			{
				if(!_g85)
				{
					_m85=instance.Type.GetMethod("createGameApp",0);
					_g85=true;
				}
				
				if(_m85!=null && !_b85)
				{
					_b85=true;
					GameApp re=(GameApp)appdomain.Invoke(_m85,instance,null);
					_b85=false;
					return re;
					
				}
				else
				{
					return base.createGameApp();
				}
			}
			
			IMethod _m86;
			bool _g86;
			bool _b86;
			public override GameDataRegister createGameDataRegister()
			{
				if(!_g86)
				{
					_m86=instance.Type.GetMethod("createGameDataRegister",0);
					_g86=true;
				}
				
				if(_m86!=null && !_b86)
				{
					_b86=true;
					GameDataRegister re=(GameDataRegister)appdomain.Invoke(_m86,instance,null);
					_b86=false;
					return re;
					
				}
				else
				{
					return base.createGameDataRegister();
				}
			}
			
			IMethod _m87;
			bool _g87;
			bool _b87;
			public override GameFactoryControl createGameFactoryControl()
			{
				if(!_g87)
				{
					_m87=instance.Type.GetMethod("createGameFactoryControl",0);
					_g87=true;
				}
				
				if(_m87!=null && !_b87)
				{
					_b87=true;
					GameFactoryControl re=(GameFactoryControl)appdomain.Invoke(_m87,instance,null);
					_b87=false;
					return re;
					
				}
				else
				{
					return base.createGameFactoryControl();
				}
			}
			
			IMethod _m88;
			bool _g88;
			bool _b88;
			public override GameServerClientSimpleData createGameServerClientSimpleData()
			{
				if(!_g88)
				{
					_m88=instance.Type.GetMethod("createGameServerClientSimpleData",0);
					_g88=true;
				}
				
				if(_m88!=null && !_b88)
				{
					_b88=true;
					GameServerClientSimpleData re=(GameServerClientSimpleData)appdomain.Invoke(_m88,instance,null);
					_b88=false;
					return re;
					
				}
				else
				{
					return base.createGameServerClientSimpleData();
				}
			}
			
			IMethod _m89;
			bool _g89;
			bool _b89;
			public override GameServerRunData createGameServerRunData()
			{
				if(!_g89)
				{
					_m89=instance.Type.GetMethod("createGameServerRunData",0);
					_g89=true;
				}
				
				if(_m89!=null && !_b89)
				{
					_b89=true;
					GameServerRunData re=(GameServerRunData)appdomain.Invoke(_m89,instance,null);
					_b89=false;
					return re;
					
				}
				else
				{
					return base.createGameServerRunData();
				}
			}
			
			IMethod _m90;
			bool _g90;
			bool _b90;
			public override GameServerSimpleInfoData createGameServerSimpleInfoData()
			{
				if(!_g90)
				{
					_m90=instance.Type.GetMethod("createGameServerSimpleInfoData",0);
					_g90=true;
				}
				
				if(_m90!=null && !_b90)
				{
					_b90=true;
					GameServerSimpleInfoData re=(GameServerSimpleInfoData)appdomain.Invoke(_m90,instance,null);
					_b90=false;
					return re;
					
				}
				else
				{
					return base.createGameServerSimpleInfoData();
				}
			}
			
			IMethod _m91;
			bool _g91;
			bool _b91;
			public override GameUIBase createGameUIBase()
			{
				if(!_g91)
				{
					_m91=instance.Type.GetMethod("createGameUIBase",0);
					_g91=true;
				}
				
				if(_m91!=null && !_b91)
				{
					_b91=true;
					GameUIBase re=(GameUIBase)appdomain.Invoke(_m91,instance,null);
					_b91=false;
					return re;
					
				}
				else
				{
					return base.createGameUIBase();
				}
			}
			
			IMethod _m92;
			bool _g92;
			bool _b92;
			public override GameUILogicBase createGameUILogicBase()
			{
				if(!_g92)
				{
					_m92=instance.Type.GetMethod("createGameUILogicBase",0);
					_g92=true;
				}
				
				if(_m92!=null && !_b92)
				{
					_b92=true;
					GameUILogicBase re=(GameUILogicBase)appdomain.Invoke(_m92,instance,null);
					_b92=false;
					return re;
					
				}
				else
				{
					return base.createGameUILogicBase();
				}
			}
			
			IMethod _m93;
			bool _g93;
			bool _b93;
			public override UnionPart createUnionPart()
			{
				if(!_g93)
				{
					_m93=instance.Type.GetMethod("createUnionPart",0);
					_g93=true;
				}
				
				if(_m93!=null && !_b93)
				{
					_b93=true;
					UnionPart re=(UnionPart)appdomain.Invoke(_m93,instance,null);
					_b93=false;
					return re;
					
				}
				else
				{
					return base.createUnionPart();
				}
			}
			
			IMethod _m94;
			bool _g94;
			bool _b94;
			public override ItemContainerData createItemContainerData()
			{
				if(!_g94)
				{
					_m94=instance.Type.GetMethod("createItemContainerData",0);
					_g94=true;
				}
				
				if(_m94!=null && !_b94)
				{
					_b94=true;
					ItemContainerData re=(ItemContainerData)appdomain.Invoke(_m94,instance,null);
					_b94=false;
					return re;
					
				}
				else
				{
					return base.createItemContainerData();
				}
			}
			
			IMethod _m95;
			bool _g95;
			bool _b95;
			public override ItemRecordData createItemRecordData()
			{
				if(!_g95)
				{
					_m95=instance.Type.GetMethod("createItemRecordData",0);
					_g95=true;
				}
				
				if(_m95!=null && !_b95)
				{
					_b95=true;
					ItemRecordData re=(ItemRecordData)appdomain.Invoke(_m95,instance,null);
					_b95=false;
					return re;
					
				}
				else
				{
					return base.createItemRecordData();
				}
			}
			
			IMethod _m96;
			bool _g96;
			bool _b96;
			public override KeepSaveData createKeepSaveData()
			{
				if(!_g96)
				{
					_m96=instance.Type.GetMethod("createKeepSaveData",0);
					_g96=true;
				}
				
				if(_m96!=null && !_b96)
				{
					_b96=true;
					KeepSaveData re=(KeepSaveData)appdomain.Invoke(_m96,instance,null);
					_b96=false;
					return re;
					
				}
				else
				{
					return base.createKeepSaveData();
				}
			}
			
			IMethod _m97;
			bool _g97;
			bool _b97;
			public override KeyData createKeyData()
			{
				if(!_g97)
				{
					_m97=instance.Type.GetMethod("createKeyData",0);
					_g97=true;
				}
				
				if(_m97!=null && !_b97)
				{
					_b97=true;
					KeyData re=(KeyData)appdomain.Invoke(_m97,instance,null);
					_b97=false;
					return re;
					
				}
				else
				{
					return base.createKeyData();
				}
			}
			
			IMethod _m98;
			bool _g98;
			bool _b98;
			public override MailPart createMailPart()
			{
				if(!_g98)
				{
					_m98=instance.Type.GetMethod("createMailPart",0);
					_g98=true;
				}
				
				if(_m98!=null && !_b98)
				{
					_b98=true;
					MailPart re=(MailPart)appdomain.Invoke(_m98,instance,null);
					_b98=false;
					return re;
					
				}
				else
				{
					return base.createMailPart();
				}
			}
			
			IMethod _m99;
			bool _g99;
			bool _b99;
			public override MonsterIdentityData createMonsterIdentityData()
			{
				if(!_g99)
				{
					_m99=instance.Type.GetMethod("createMonsterIdentityData",0);
					_g99=true;
				}
				
				if(_m99!=null && !_b99)
				{
					_b99=true;
					MonsterIdentityData re=(MonsterIdentityData)appdomain.Invoke(_m99,instance,null);
					_b99=false;
					return re;
					
				}
				else
				{
					return base.createMonsterIdentityData();
				}
			}
			
			IMethod _m100;
			bool _g100;
			bool _b100;
			public override MonsterIdentityLogic createMonsterIdentityLogic()
			{
				if(!_g100)
				{
					_m100=instance.Type.GetMethod("createMonsterIdentityLogic",0);
					_g100=true;
				}
				
				if(_m100!=null && !_b100)
				{
					_b100=true;
					MonsterIdentityLogic re=(MonsterIdentityLogic)appdomain.Invoke(_m100,instance,null);
					_b100=false;
					return re;
					
				}
				else
				{
					return base.createMonsterIdentityLogic();
				}
			}
			
			IMethod _m101;
			bool _g101;
			bool _b101;
			public override MUnitCacheData createMUnitCacheData()
			{
				if(!_g101)
				{
					_m101=instance.Type.GetMethod("createMUnitCacheData",0);
					_g101=true;
				}
				
				if(_m101!=null && !_b101)
				{
					_b101=true;
					MUnitCacheData re=(MUnitCacheData)appdomain.Invoke(_m101,instance,null);
					_b101=false;
					return re;
					
				}
				else
				{
					return base.createMUnitCacheData();
				}
			}
			
			IMethod _m102;
			bool _g102;
			bool _b102;
			public override MUnitFightDataLogic createMUnitFightDataLogic()
			{
				if(!_g102)
				{
					_m102=instance.Type.GetMethod("createMUnitFightDataLogic",0);
					_g102=true;
				}
				
				if(_m102!=null && !_b102)
				{
					_b102=true;
					MUnitFightDataLogic re=(MUnitFightDataLogic)appdomain.Invoke(_m102,instance,null);
					_b102=false;
					return re;
					
				}
				else
				{
					return base.createMUnitFightDataLogic();
				}
			}
			
			IMethod _m103;
			bool _g103;
			bool _b103;
			public override MUnitUseData createMUnitUseData()
			{
				if(!_g103)
				{
					_m103=instance.Type.GetMethod("createMUnitUseData",0);
					_g103=true;
				}
				
				if(_m103!=null && !_b103)
				{
					_b103=true;
					MUnitUseData re=(MUnitUseData)appdomain.Invoke(_m103,instance,null);
					_b103=false;
					return re;
					
				}
				else
				{
					return base.createMUnitUseData();
				}
			}
			
			IMethod _m104;
			bool _g104;
			bool _b104;
			public override MUnitUseLogic createMUnitUseLogic()
			{
				if(!_g104)
				{
					_m104=instance.Type.GetMethod("createMUnitUseLogic",0);
					_g104=true;
				}
				
				if(_m104!=null && !_b104)
				{
					_b104=true;
					MUnitUseLogic re=(MUnitUseLogic)appdomain.Invoke(_m104,instance,null);
					_b104=false;
					return re;
					
				}
				else
				{
					return base.createMUnitUseLogic();
				}
			}
			
			IMethod _m105;
			bool _g105;
			bool _b105;
			public override NPCIdentityData createNPCIdentityData()
			{
				if(!_g105)
				{
					_m105=instance.Type.GetMethod("createNPCIdentityData",0);
					_g105=true;
				}
				
				if(_m105!=null && !_b105)
				{
					_b105=true;
					NPCIdentityData re=(NPCIdentityData)appdomain.Invoke(_m105,instance,null);
					_b105=false;
					return re;
					
				}
				else
				{
					return base.createNPCIdentityData();
				}
			}
			
			IMethod _m106;
			bool _g106;
			bool _b106;
			public override PetIdentityData createPetIdentityData()
			{
				if(!_g106)
				{
					_m106=instance.Type.GetMethod("createPetIdentityData",0);
					_g106=true;
				}
				
				if(_m106!=null && !_b106)
				{
					_b106=true;
					PetIdentityData re=(PetIdentityData)appdomain.Invoke(_m106,instance,null);
					_b106=false;
					return re;
					
				}
				else
				{
					return base.createPetIdentityData();
				}
			}
			
			IMethod _m107;
			bool _g107;
			bool _b107;
			public override PetUseData createPetUseData()
			{
				if(!_g107)
				{
					_m107=instance.Type.GetMethod("createPetUseData",0);
					_g107=true;
				}
				
				if(_m107!=null && !_b107)
				{
					_b107=true;
					PetUseData re=(PetUseData)appdomain.Invoke(_m107,instance,null);
					_b107=false;
					return re;
					
				}
				else
				{
					return base.createPetUseData();
				}
			}
			
			IMethod _m108;
			bool _g108;
			bool _b108;
			public override PlayerLoginData createPlayerLoginData()
			{
				if(!_g108)
				{
					_m108=instance.Type.GetMethod("createPlayerLoginData",0);
					_g108=true;
				}
				
				if(_m108!=null && !_b108)
				{
					_b108=true;
					PlayerLoginData re=(PlayerLoginData)appdomain.Invoke(_m108,instance,null);
					_b108=false;
					return re;
					
				}
				else
				{
					return base.createPlayerLoginData();
				}
			}
			
			IMethod _m109;
			bool _g109;
			bool _b109;
			public override PlayerMailData createPlayerMailData()
			{
				if(!_g109)
				{
					_m109=instance.Type.GetMethod("createPlayerMailData",0);
					_g109=true;
				}
				
				if(_m109!=null && !_b109)
				{
					_b109=true;
					PlayerMailData re=(PlayerMailData)appdomain.Invoke(_m109,instance,null);
					_b109=false;
					return re;
					
				}
				else
				{
					return base.createPlayerMailData();
				}
			}
			
			IMethod _m110;
			bool _g110;
			bool _b110;
			public override PlayerPrimaryKeyData createPlayerPrimaryKeyData()
			{
				if(!_g110)
				{
					_m110=instance.Type.GetMethod("createPlayerPrimaryKeyData",0);
					_g110=true;
				}
				
				if(_m110!=null && !_b110)
				{
					_b110=true;
					PlayerPrimaryKeyData re=(PlayerPrimaryKeyData)appdomain.Invoke(_m110,instance,null);
					_b110=false;
					return re;
					
				}
				else
				{
					return base.createPlayerPrimaryKeyData();
				}
			}
			
			IMethod _m111;
			bool _g111;
			bool _b111;
			public override PlayerRankToolData createPlayerRankToolData()
			{
				if(!_g111)
				{
					_m111=instance.Type.GetMethod("createPlayerRankToolData",0);
					_g111=true;
				}
				
				if(_m111!=null && !_b111)
				{
					_b111=true;
					PlayerRankToolData re=(PlayerRankToolData)appdomain.Invoke(_m111,instance,null);
					_b111=false;
					return re;
					
				}
				else
				{
					return base.createPlayerRankToolData();
				}
			}
			
			IMethod _m112;
			bool _g112;
			bool _b112;
			public override PosData createPosData()
			{
				if(!_g112)
				{
					_m112=instance.Type.GetMethod("createPosData",0);
					_g112=true;
				}
				
				if(_m112!=null && !_b112)
				{
					_b112=true;
					PosData re=(PosData)appdomain.Invoke(_m112,instance,null);
					_b112=false;
					return re;
					
				}
				else
				{
					return base.createPosData();
				}
			}
			
			IMethod _m113;
			bool _g113;
			bool _b113;
			public override PosDirData createPosDirData()
			{
				if(!_g113)
				{
					_m113=instance.Type.GetMethod("createPosDirData",0);
					_g113=true;
				}
				
				if(_m113!=null && !_b113)
				{
					_b113=true;
					PosDirData re=(PosDirData)appdomain.Invoke(_m113,instance,null);
					_b113=false;
					return re;
					
				}
				else
				{
					return base.createPosDirData();
				}
			}
			
			IMethod _m114;
			bool _g114;
			bool _b114;
			public override PreBattleScenePlayLogic createPreBattleScenePlayLogic()
			{
				if(!_g114)
				{
					_m114=instance.Type.GetMethod("createPreBattleScenePlayLogic",0);
					_g114=true;
				}
				
				if(_m114!=null && !_b114)
				{
					_b114=true;
					PreBattleScenePlayLogic re=(PreBattleScenePlayLogic)appdomain.Invoke(_m114,instance,null);
					_b114=false;
					return re;
					
				}
				else
				{
					return base.createPreBattleScenePlayLogic();
				}
			}
			
			IMethod _m115;
			bool _g115;
			bool _b115;
			public override PuppetIdentityData createPuppetIdentityData()
			{
				if(!_g115)
				{
					_m115=instance.Type.GetMethod("createPuppetIdentityData",0);
					_g115=true;
				}
				
				if(_m115!=null && !_b115)
				{
					_b115=true;
					PuppetIdentityData re=(PuppetIdentityData)appdomain.Invoke(_m115,instance,null);
					_b115=false;
					return re;
					
				}
				else
				{
					return base.createPuppetIdentityData();
				}
			}
			
			IMethod _m116;
			bool _g116;
			bool _b116;
			public override PuppetIdentityLogic createPuppetIdentityLogic()
			{
				if(!_g116)
				{
					_m116=instance.Type.GetMethod("createPuppetIdentityLogic",0);
					_g116=true;
				}
				
				if(_m116!=null && !_b116)
				{
					_b116=true;
					PuppetIdentityLogic re=(PuppetIdentityLogic)appdomain.Invoke(_m116,instance,null);
					_b116=false;
					return re;
					
				}
				else
				{
					return base.createPuppetIdentityLogic();
				}
			}
			
			IMethod _m117;
			bool _g117;
			bool _b117;
			public override QuestCompleteData createQuestCompleteData()
			{
				if(!_g117)
				{
					_m117=instance.Type.GetMethod("createQuestCompleteData",0);
					_g117=true;
				}
				
				if(_m117!=null && !_b117)
				{
					_b117=true;
					QuestCompleteData re=(QuestCompleteData)appdomain.Invoke(_m117,instance,null);
					_b117=false;
					return re;
					
				}
				else
				{
					return base.createQuestCompleteData();
				}
			}
			
			IMethod _m118;
			bool _g118;
			bool _b118;
			public override QuestData createQuestData()
			{
				if(!_g118)
				{
					_m118=instance.Type.GetMethod("createQuestData",0);
					_g118=true;
				}
				
				if(_m118!=null && !_b118)
				{
					_b118=true;
					QuestData re=(QuestData)appdomain.Invoke(_m118,instance,null);
					_b118=false;
					return re;
					
				}
				else
				{
					return base.createQuestData();
				}
			}
			
			IMethod _m119;
			bool _g119;
			bool _b119;
			public override QuestPart createQuestPart()
			{
				if(!_g119)
				{
					_m119=instance.Type.GetMethod("createQuestPart",0);
					_g119=true;
				}
				
				if(_m119!=null && !_b119)
				{
					_b119=true;
					QuestPart re=(QuestPart)appdomain.Invoke(_m119,instance,null);
					_b119=false;
					return re;
					
				}
				else
				{
					return base.createQuestPart();
				}
			}
			
			IMethod _m120;
			bool _g120;
			bool _b120;
			public override RankData createRankData()
			{
				if(!_g120)
				{
					_m120=instance.Type.GetMethod("createRankData",0);
					_g120=true;
				}
				
				if(_m120!=null && !_b120)
				{
					_b120=true;
					RankData re=(RankData)appdomain.Invoke(_m120,instance,null);
					_b120=false;
					return re;
					
				}
				else
				{
					return base.createRankData();
				}
			}
			
			IMethod _m121;
			bool _g121;
			bool _b121;
			public override RankSimpleData createRankSimpleData()
			{
				if(!_g121)
				{
					_m121=instance.Type.GetMethod("createRankSimpleData",0);
					_g121=true;
				}
				
				if(_m121!=null && !_b121)
				{
					_b121=true;
					RankSimpleData re=(RankSimpleData)appdomain.Invoke(_m121,instance,null);
					_b121=false;
					return re;
					
				}
				else
				{
					return base.createRankSimpleData();
				}
			}
			
			IMethod _m122;
			bool _g122;
			bool _b122;
			public override RankToolData createRankToolData()
			{
				if(!_g122)
				{
					_m122=instance.Type.GetMethod("createRankToolData",0);
					_g122=true;
				}
				
				if(_m122!=null && !_b122)
				{
					_b122=true;
					RankToolData re=(RankToolData)appdomain.Invoke(_m122,instance,null);
					_b122=false;
					return re;
					
				}
				else
				{
					return base.createRankToolData();
				}
			}
			
			IMethod _m123;
			bool _g123;
			bool _b123;
			public override ReplaceTextTool createReplaceTextTool()
			{
				if(!_g123)
				{
					_m123=instance.Type.GetMethod("createReplaceTextTool",0);
					_g123=true;
				}
				
				if(_m123!=null && !_b123)
				{
					_b123=true;
					ReplaceTextTool re=(ReplaceTextTool)appdomain.Invoke(_m123,instance,null);
					_b123=false;
					return re;
					
				}
				else
				{
					return base.createReplaceTextTool();
				}
			}
			
			IMethod _m124;
			bool _g124;
			bool _b124;
			public override RewardShowData createRewardShowData()
			{
				if(!_g124)
				{
					_m124=instance.Type.GetMethod("createRewardShowData",0);
					_g124=true;
				}
				
				if(_m124!=null && !_b124)
				{
					_b124=true;
					RewardShowData re=(RewardShowData)appdomain.Invoke(_m124,instance,null);
					_b124=false;
					return re;
					
				}
				else
				{
					return base.createRewardShowData();
				}
			}
			
			IMethod _m125;
			bool _g125;
			bool _b125;
			public override RolePart createRolePart()
			{
				if(!_g125)
				{
					_m125=instance.Type.GetMethod("createRolePart",0);
					_g125=true;
				}
				
				if(_m125!=null && !_b125)
				{
					_b125=true;
					RolePart re=(RolePart)appdomain.Invoke(_m125,instance,null);
					_b125=false;
					return re;
					
				}
				else
				{
					return base.createRolePart();
				}
			}
			
			IMethod _m126;
			bool _g126;
			bool _b126;
			public override RoleShowChangeData createRoleShowChangeData()
			{
				if(!_g126)
				{
					_m126=instance.Type.GetMethod("createRoleShowChangeData",0);
					_g126=true;
				}
				
				if(_m126!=null && !_b126)
				{
					_b126=true;
					RoleShowChangeData re=(RoleShowChangeData)appdomain.Invoke(_m126,instance,null);
					_b126=false;
					return re;
					
				}
				else
				{
					return base.createRoleShowChangeData();
				}
			}
			
			IMethod _m127;
			bool _g127;
			bool _b127;
			public override SaveVersionData createSaveVersionData()
			{
				if(!_g127)
				{
					_m127=instance.Type.GetMethod("createSaveVersionData",0);
					_g127=true;
				}
				
				if(_m127!=null && !_b127)
				{
					_b127=true;
					SaveVersionData re=(SaveVersionData)appdomain.Invoke(_m127,instance,null);
					_b127=false;
					return re;
					
				}
				else
				{
					return base.createSaveVersionData();
				}
			}
			
			IMethod _m128;
			bool _g128;
			bool _b128;
			public override SceneCameraLogic createSceneCameraLogic()
			{
				if(!_g128)
				{
					_m128=instance.Type.GetMethod("createSceneCameraLogic",0);
					_g128=true;
				}
				
				if(_m128!=null && !_b128)
				{
					_b128=true;
					SceneCameraLogic re=(SceneCameraLogic)appdomain.Invoke(_m128,instance,null);
					_b128=false;
					return re;
					
				}
				else
				{
					return base.createSceneCameraLogic();
				}
			}
			
			IMethod _m129;
			bool _g129;
			bool _b129;
			public override SceneCameraLogic3DOne createSceneCameraLogic3D()
			{
				if(!_g129)
				{
					_m129=instance.Type.GetMethod("createSceneCameraLogic3D",0);
					_g129=true;
				}
				
				if(_m129!=null && !_b129)
				{
					_b129=true;
					SceneCameraLogic3DOne re=(SceneCameraLogic3DOne)appdomain.Invoke(_m129,instance,null);
					_b129=false;
					return re;
					
				}
				else
				{
					return base.createSceneCameraLogic3D();
				}
			}
			
			IMethod _m130;
			bool _g130;
			bool _b130;
			public override SceneEffectIdentityData createSceneEffectIdentityData()
			{
				if(!_g130)
				{
					_m130=instance.Type.GetMethod("createSceneEffectIdentityData",0);
					_g130=true;
				}
				
				if(_m130!=null && !_b130)
				{
					_b130=true;
					SceneEffectIdentityData re=(SceneEffectIdentityData)appdomain.Invoke(_m130,instance,null);
					_b130=false;
					return re;
					
				}
				else
				{
					return base.createSceneEffectIdentityData();
				}
			}
			
			IMethod _m131;
			bool _g131;
			bool _b131;
			public override SceneEnterArgData createSceneEnterArgData()
			{
				if(!_g131)
				{
					_m131=instance.Type.GetMethod("createSceneEnterArgData",0);
					_g131=true;
				}
				
				if(_m131!=null && !_b131)
				{
					_b131=true;
					SceneEnterArgData re=(SceneEnterArgData)appdomain.Invoke(_m131,instance,null);
					_b131=false;
					return re;
					
				}
				else
				{
					return base.createSceneEnterArgData();
				}
			}
			
			IMethod _m132;
			bool _g132;
			bool _b132;
			public override SceneEnterData createSceneEnterData()
			{
				if(!_g132)
				{
					_m132=instance.Type.GetMethod("createSceneEnterData",0);
					_g132=true;
				}
				
				if(_m132!=null && !_b132)
				{
					_b132=true;
					SceneEnterData re=(SceneEnterData)appdomain.Invoke(_m132,instance,null);
					_b132=false;
					return re;
					
				}
				else
				{
					return base.createSceneEnterData();
				}
			}
			
			IMethod _m133;
			bool _g133;
			bool _b133;
			public override SceneFightLogic createSceneFightLogic()
			{
				if(!_g133)
				{
					_m133=instance.Type.GetMethod("createSceneFightLogic",0);
					_g133=true;
				}
				
				if(_m133!=null && !_b133)
				{
					_b133=true;
					SceneFightLogic re=(SceneFightLogic)appdomain.Invoke(_m133,instance,null);
					_b133=false;
					return re;
					
				}
				else
				{
					return base.createSceneFightLogic();
				}
			}
			
			IMethod _m134;
			bool _g134;
			bool _b134;
			public override SceneInOutLogic createSceneInOutLogic()
			{
				if(!_g134)
				{
					_m134=instance.Type.GetMethod("createSceneInOutLogic",0);
					_g134=true;
				}
				
				if(_m134!=null && !_b134)
				{
					_b134=true;
					SceneInOutLogic re=(SceneInOutLogic)appdomain.Invoke(_m134,instance,null);
					_b134=false;
					return re;
					
				}
				else
				{
					return base.createSceneInOutLogic();
				}
			}
			
			IMethod _m135;
			bool _g135;
			bool _b135;
			public override SceneLoadLogic createSceneLoadLogic()
			{
				if(!_g135)
				{
					_m135=instance.Type.GetMethod("createSceneLoadLogic",0);
					_g135=true;
				}
				
				if(_m135!=null && !_b135)
				{
					_b135=true;
					SceneLoadLogic re=(SceneLoadLogic)appdomain.Invoke(_m135,instance,null);
					_b135=false;
					return re;
					
				}
				else
				{
					return base.createSceneLoadLogic();
				}
			}
			
			IMethod _m136;
			bool _g136;
			bool _b136;
			public override SceneObject createSceneObject()
			{
				if(!_g136)
				{
					_m136=instance.Type.GetMethod("createSceneObject",0);
					_g136=true;
				}
				
				if(_m136!=null && !_b136)
				{
					_b136=true;
					SceneObject re=(SceneObject)appdomain.Invoke(_m136,instance,null);
					_b136=false;
					return re;
					
				}
				else
				{
					return base.createSceneObject();
				}
			}
			
			IMethod _m137;
			bool _g137;
			bool _b137;
			public override SceneObjectLogicBase createSceneObjectLogicBase()
			{
				if(!_g137)
				{
					_m137=instance.Type.GetMethod("createSceneObjectLogicBase",0);
					_g137=true;
				}
				
				if(_m137!=null && !_b137)
				{
					_b137=true;
					SceneObjectLogicBase re=(SceneObjectLogicBase)appdomain.Invoke(_m137,instance,null);
					_b137=false;
					return re;
					
				}
				else
				{
					return base.createSceneObjectLogicBase();
				}
			}
			
			IMethod _m138;
			bool _g138;
			bool _b138;
			public override ScenePart createScenePart()
			{
				if(!_g138)
				{
					_m138=instance.Type.GetMethod("createScenePart",0);
					_g138=true;
				}
				
				if(_m138!=null && !_b138)
				{
					_b138=true;
					ScenePart re=(ScenePart)appdomain.Invoke(_m138,instance,null);
					_b138=false;
					return re;
					
				}
				else
				{
					return base.createScenePart();
				}
			}
			
			IMethod _m139;
			bool _g139;
			bool _b139;
			public override ScenePlaceConfig createScenePlaceConfig()
			{
				if(!_g139)
				{
					_m139=instance.Type.GetMethod("createScenePlaceConfig",0);
					_g139=true;
				}
				
				if(_m139!=null && !_b139)
				{
					_b139=true;
					ScenePlaceConfig re=(ScenePlaceConfig)appdomain.Invoke(_m139,instance,null);
					_b139=false;
					return re;
					
				}
				else
				{
					return base.createScenePlaceConfig();
				}
			}
			
			IMethod _m140;
			bool _g140;
			bool _b140;
			public override SceneMethodLogic createScenePlayLogic()
			{
				if(!_g140)
				{
					_m140=instance.Type.GetMethod("createScenePlayLogic",0);
					_g140=true;
				}
				
				if(_m140!=null && !_b140)
				{
					_b140=true;
					SceneMethodLogic re=(SceneMethodLogic)appdomain.Invoke(_m140,instance,null);
					_b140=false;
					return re;
					
				}
				else
				{
					return base.createScenePlayLogic();
				}
			}
			
			IMethod _m141;
			bool _g141;
			bool _b141;
			public override ScenePosLogic createScenePosLogic()
			{
				if(!_g141)
				{
					_m141=instance.Type.GetMethod("createScenePosLogic",0);
					_g141=true;
				}
				
				if(_m141!=null && !_b141)
				{
					_b141=true;
					ScenePosLogic re=(ScenePosLogic)appdomain.Invoke(_m141,instance,null);
					_b141=false;
					return re;
					
				}
				else
				{
					return base.createScenePosLogic();
				}
			}
			
			IMethod _m142;
			bool _g142;
			bool _b142;
			public override ScenePosLogic3DOne createScenePosLogic3D()
			{
				if(!_g142)
				{
					_m142=instance.Type.GetMethod("createScenePosLogic3D",0);
					_g142=true;
				}
				
				if(_m142!=null && !_b142)
				{
					_b142=true;
					ScenePosLogic3DOne re=(ScenePosLogic3DOne)appdomain.Invoke(_m142,instance,null);
					_b142=false;
					return re;
					
				}
				else
				{
					return base.createScenePosLogic3D();
				}
			}
			
			IMethod _m143;
			bool _g143;
			bool _b143;
			public override ScenePreInfoData createScenePreInfoData()
			{
				if(!_g143)
				{
					_m143=instance.Type.GetMethod("createScenePreInfoData",0);
					_g143=true;
				}
				
				if(_m143!=null && !_b143)
				{
					_b143=true;
					ScenePreInfoData re=(ScenePreInfoData)appdomain.Invoke(_m143,instance,null);
					_b143=false;
					return re;
					
				}
				else
				{
					return base.createScenePreInfoData();
				}
			}
			
			IMethod _m144;
			bool _g144;
			bool _b144;
			public override SceneShowLogic createSceneShowLogic()
			{
				if(!_g144)
				{
					_m144=instance.Type.GetMethod("createSceneShowLogic",0);
					_g144=true;
				}
				
				if(_m144!=null && !_b144)
				{
					_b144=true;
					SceneShowLogic re=(SceneShowLogic)appdomain.Invoke(_m144,instance,null);
					_b144=false;
					return re;
					
				}
				else
				{
					return base.createSceneShowLogic();
				}
			}
			
			IMethod _m145;
			bool _g145;
			bool _b145;
			public override SceneShowLogic3DOne createSceneShowLogic3D()
			{
				if(!_g145)
				{
					_m145=instance.Type.GetMethod("createSceneShowLogic3D",0);
					_g145=true;
				}
				
				if(_m145!=null && !_b145)
				{
					_b145=true;
					SceneShowLogic3DOne re=(SceneShowLogic3DOne)appdomain.Invoke(_m145,instance,null);
					_b145=false;
					return re;
					
				}
				else
				{
					return base.createSceneShowLogic3D();
				}
			}
			
			IMethod _m146;
			bool _g146;
			bool _b146;
			public override ServerSimpleInfoData createServerSimpleInfoData()
			{
				if(!_g146)
				{
					_m146=instance.Type.GetMethod("createServerSimpleInfoData",0);
					_g146=true;
				}
				
				if(_m146!=null && !_b146)
				{
					_b146=true;
					ServerSimpleInfoData re=(ServerSimpleInfoData)appdomain.Invoke(_m146,instance,null);
					_b146=false;
					return re;
					
				}
				else
				{
					return base.createServerSimpleInfoData();
				}
			}
			
			IMethod _m147;
			bool _g147;
			bool _b147;
			public override SingleBagPart createSingleBagPart()
			{
				if(!_g147)
				{
					_m147=instance.Type.GetMethod("createSingleBagPart",0);
					_g147=true;
				}
				
				if(_m147!=null && !_b147)
				{
					_b147=true;
					SingleBagPart re=(SingleBagPart)appdomain.Invoke(_m147,instance,null);
					_b147=false;
					return re;
					
				}
				else
				{
					return base.createSingleBagPart();
				}
			}
			
			IMethod _m148;
			bool _g148;
			bool _b148;
			public override SingleCharacterPart createSingleCharacterPart()
			{
				if(!_g148)
				{
					_m148=instance.Type.GetMethod("createSingleCharacterPart",0);
					_g148=true;
				}
				
				if(_m148!=null && !_b148)
				{
					_b148=true;
					SingleCharacterPart re=(SingleCharacterPart)appdomain.Invoke(_m148,instance,null);
					_b148=false;
					return re;
					
				}
				else
				{
					return base.createSingleCharacterPart();
				}
			}
			
			IMethod _m149;
			bool _g149;
			bool _b149;
			public override SkillData createSkillData()
			{
				if(!_g149)
				{
					_m149=instance.Type.GetMethod("createSkillData",0);
					_g149=true;
				}
				
				if(_m149!=null && !_b149)
				{
					_b149=true;
					SkillData re=(SkillData)appdomain.Invoke(_m149,instance,null);
					_b149=false;
					return re;
					
				}
				else
				{
					return base.createSkillData();
				}
			}
			
			IMethod _m150;
			bool _g150;
			bool _b150;
			public override SkillTargetData createSkillTargetData()
			{
				if(!_g150)
				{
					_m150=instance.Type.GetMethod("createSkillTargetData",0);
					_g150=true;
				}
				
				if(_m150!=null && !_b150)
				{
					_b150=true;
					SkillTargetData re=(SkillTargetData)appdomain.Invoke(_m150,instance,null);
					_b150=false;
					return re;
					
				}
				else
				{
					return base.createSkillTargetData();
				}
			}
			
			IMethod _m151;
			bool _g151;
			bool _b151;
			public override SocialPart createSocialPart()
			{
				if(!_g151)
				{
					_m151=instance.Type.GetMethod("createSocialPart",0);
					_g151=true;
				}
				
				if(_m151!=null && !_b151)
				{
					_b151=true;
					SocialPart re=(SocialPart)appdomain.Invoke(_m151,instance,null);
					_b151=false;
					return re;
					
				}
				else
				{
					return base.createSocialPart();
				}
			}
			
			IMethod _m152;
			bool _g152;
			bool _b152;
			public override SystemPart createSystemPart()
			{
				if(!_g152)
				{
					_m152=instance.Type.GetMethod("createSystemPart",0);
					_g152=true;
				}
				
				if(_m152!=null && !_b152)
				{
					_b152=true;
					SystemPart re=(SystemPart)appdomain.Invoke(_m152,instance,null);
					_b152=false;
					return re;
					
				}
				else
				{
					return base.createSystemPart();
				}
			}
			
			IMethod _m153;
			bool _g153;
			bool _b153;
			public override TaskData createTaskData()
			{
				if(!_g153)
				{
					_m153=instance.Type.GetMethod("createTaskData",0);
					_g153=true;
				}
				
				if(_m153!=null && !_b153)
				{
					_b153=true;
					TaskData re=(TaskData)appdomain.Invoke(_m153,instance,null);
					_b153=false;
					return re;
					
				}
				else
				{
					return base.createTaskData();
				}
			}
			
			IMethod _m154;
			bool _g154;
			bool _b154;
			public override TeamPart createTeamPart()
			{
				if(!_g154)
				{
					_m154=instance.Type.GetMethod("createTeamPart",0);
					_g154=true;
				}
				
				if(_m154!=null && !_b154)
				{
					_b154=true;
					TeamPart re=(TeamPart)appdomain.Invoke(_m154,instance,null);
					_b154=false;
					return re;
					
				}
				else
				{
					return base.createTeamPart();
				}
			}
			
			IMethod _m155;
			bool _g155;
			bool _b155;
			public override Test2Data createTest2Data()
			{
				if(!_g155)
				{
					_m155=instance.Type.GetMethod("createTest2Data",0);
					_g155=true;
				}
				
				if(_m155!=null && !_b155)
				{
					_b155=true;
					Test2Data re=(Test2Data)appdomain.Invoke(_m155,instance,null);
					_b155=false;
					return re;
					
				}
				else
				{
					return base.createTest2Data();
				}
			}
			
			IMethod _m156;
			bool _g156;
			bool _b156;
			public override TestData createTestData()
			{
				if(!_g156)
				{
					_m156=instance.Type.GetMethod("createTestData",0);
					_g156=true;
				}
				
				if(_m156!=null && !_b156)
				{
					_b156=true;
					TestData re=(TestData)appdomain.Invoke(_m156,instance,null);
					_b156=false;
					return re;
					
				}
				else
				{
					return base.createTestData();
				}
			}
			
			IMethod _m157;
			bool _g157;
			bool _b157;
			public override UIBase createUIBase()
			{
				if(!_g157)
				{
					_m157=instance.Type.GetMethod("createUIBase",0);
					_g157=true;
				}
				
				if(_m157!=null && !_b157)
				{
					_b157=true;
					UIBase re=(UIBase)appdomain.Invoke(_m157,instance,null);
					_b157=false;
					return re;
					
				}
				else
				{
					return base.createUIBase();
				}
			}
			
			IMethod _m158;
			bool _g158;
			bool _b158;
			public override UILogicBase createUILogicBase()
			{
				if(!_g158)
				{
					_m158=instance.Type.GetMethod("createUILogicBase",0);
					_g158=true;
				}
				
				if(_m158!=null && !_b158)
				{
					_b158=true;
					UILogicBase re=(UILogicBase)appdomain.Invoke(_m158,instance,null);
					_b158=false;
					return re;
					
				}
				else
				{
					return base.createUILogicBase();
				}
			}
			
			IMethod _m159;
			bool _g159;
			bool _b159;
			public override UnitAIData createUnitAIData()
			{
				if(!_g159)
				{
					_m159=instance.Type.GetMethod("createUnitAIData",0);
					_g159=true;
				}
				
				if(_m159!=null && !_b159)
				{
					_b159=true;
					UnitAIData re=(UnitAIData)appdomain.Invoke(_m159,instance,null);
					_b159=false;
					return re;
					
				}
				else
				{
					return base.createUnitAIData();
				}
			}
			
			IMethod _m160;
			bool _g160;
			bool _b160;
			public override UnitAILogic createUnitAILogic()
			{
				if(!_g160)
				{
					_m160=instance.Type.GetMethod("createUnitAILogic",0);
					_g160=true;
				}
				
				if(_m160!=null && !_b160)
				{
					_b160=true;
					UnitAILogic re=(UnitAILogic)appdomain.Invoke(_m160,instance,null);
					_b160=false;
					return re;
					
				}
				else
				{
					return base.createUnitAILogic();
				}
			}
			
			IMethod _m161;
			bool _g161;
			bool _b161;
			public override UnitAvatarData createUnitAvatarData()
			{
				if(!_g161)
				{
					_m161=instance.Type.GetMethod("createUnitAvatarData",0);
					_g161=true;
				}
				
				if(_m161!=null && !_b161)
				{
					_b161=true;
					UnitAvatarData re=(UnitAvatarData)appdomain.Invoke(_m161,instance,null);
					_b161=false;
					return re;
					
				}
				else
				{
					return base.createUnitAvatarData();
				}
			}
			
			IMethod _m162;
			bool _g162;
			bool _b162;
			public override UnitAvatarLogic createUnitAvatarLogic()
			{
				if(!_g162)
				{
					_m162=instance.Type.GetMethod("createUnitAvatarLogic",0);
					_g162=true;
				}
				
				if(_m162!=null && !_b162)
				{
					_b162=true;
					UnitAvatarLogic re=(UnitAvatarLogic)appdomain.Invoke(_m162,instance,null);
					_b162=false;
					return re;
					
				}
				else
				{
					return base.createUnitAvatarLogic();
				}
			}
			
			IMethod _m163;
			bool _g163;
			bool _b163;
			public override UnitFightData createUnitFightData()
			{
				if(!_g163)
				{
					_m163=instance.Type.GetMethod("createUnitFightData",0);
					_g163=true;
				}
				
				if(_m163!=null && !_b163)
				{
					_b163=true;
					UnitFightData re=(UnitFightData)appdomain.Invoke(_m163,instance,null);
					_b163=false;
					return re;
					
				}
				else
				{
					return base.createUnitFightData();
				}
			}
			
			IMethod _m164;
			bool _g164;
			bool _b164;
			public override UnitFightExData createUnitFightExData()
			{
				if(!_g164)
				{
					_m164=instance.Type.GetMethod("createUnitFightExData",0);
					_g164=true;
				}
				
				if(_m164!=null && !_b164)
				{
					_b164=true;
					UnitFightExData re=(UnitFightExData)appdomain.Invoke(_m164,instance,null);
					_b164=false;
					return re;
					
				}
				else
				{
					return base.createUnitFightExData();
				}
			}
			
			IMethod _m165;
			bool _g165;
			bool _b165;
			public override UnitFightLogic createUnitFightLogic()
			{
				if(!_g165)
				{
					_m165=instance.Type.GetMethod("createUnitFightLogic",0);
					_g165=true;
				}
				
				if(_m165!=null && !_b165)
				{
					_b165=true;
					UnitFightLogic re=(UnitFightLogic)appdomain.Invoke(_m165,instance,null);
					_b165=false;
					return re;
					
				}
				else
				{
					return base.createUnitFightLogic();
				}
			}
			
			IMethod _m166;
			bool _g166;
			bool _b166;
			public override UnitHeadLogic createUnitHeadLogic()
			{
				if(!_g166)
				{
					_m166=instance.Type.GetMethod("createUnitHeadLogic",0);
					_g166=true;
				}
				
				if(_m166!=null && !_b166)
				{
					_b166=true;
					UnitHeadLogic re=(UnitHeadLogic)appdomain.Invoke(_m166,instance,null);
					_b166=false;
					return re;
					
				}
				else
				{
					return base.createUnitHeadLogic();
				}
			}
			
			IMethod _m167;
			bool _g167;
			bool _b167;
			public override UnitIdentityData createUnitIdentityData()
			{
				if(!_g167)
				{
					_m167=instance.Type.GetMethod("createUnitIdentityData",0);
					_g167=true;
				}
				
				if(_m167!=null && !_b167)
				{
					_b167=true;
					UnitIdentityData re=(UnitIdentityData)appdomain.Invoke(_m167,instance,null);
					_b167=false;
					return re;
					
				}
				else
				{
					return base.createUnitIdentityData();
				}
			}
			
			IMethod _m168;
			bool _g168;
			bool _b168;
			public override UnitIdentityLogic createUnitIdentityLogic()
			{
				if(!_g168)
				{
					_m168=instance.Type.GetMethod("createUnitIdentityLogic",0);
					_g168=true;
				}
				
				if(_m168!=null && !_b168)
				{
					_b168=true;
					UnitIdentityLogic re=(UnitIdentityLogic)appdomain.Invoke(_m168,instance,null);
					_b168=false;
					return re;
					
				}
				else
				{
					return base.createUnitIdentityLogic();
				}
			}
			
			IMethod _m169;
			bool _g169;
			bool _b169;
			public override UnitInfoData createUnitInfoData()
			{
				if(!_g169)
				{
					_m169=instance.Type.GetMethod("createUnitInfoData",0);
					_g169=true;
				}
				
				if(_m169!=null && !_b169)
				{
					_b169=true;
					UnitInfoData re=(UnitInfoData)appdomain.Invoke(_m169,instance,null);
					_b169=false;
					return re;
					
				}
				else
				{
					return base.createUnitInfoData();
				}
			}
			
			IMethod _m170;
			bool _g170;
			bool _b170;
			public override UnitLogicBase createUnitLogicBase()
			{
				if(!_g170)
				{
					_m170=instance.Type.GetMethod("createUnitLogicBase",0);
					_g170=true;
				}
				
				if(_m170!=null && !_b170)
				{
					_b170=true;
					UnitLogicBase re=(UnitLogicBase)appdomain.Invoke(_m170,instance,null);
					_b170=false;
					return re;
					
				}
				else
				{
					return base.createUnitLogicBase();
				}
			}
			
			IMethod _m171;
			bool _g171;
			bool _b171;
			public override UnitPosData createUnitPosData()
			{
				if(!_g171)
				{
					_m171=instance.Type.GetMethod("createUnitPosData",0);
					_g171=true;
				}
				
				if(_m171!=null && !_b171)
				{
					_b171=true;
					UnitPosData re=(UnitPosData)appdomain.Invoke(_m171,instance,null);
					_b171=false;
					return re;
					
				}
				else
				{
					return base.createUnitPosData();
				}
			}
			
			IMethod _m172;
			bool _g172;
			bool _b172;
			public override UnitPosLogic createUnitPosLogic()
			{
				if(!_g172)
				{
					_m172=instance.Type.GetMethod("createUnitPosLogic",0);
					_g172=true;
				}
				
				if(_m172!=null && !_b172)
				{
					_b172=true;
					UnitPosLogic re=(UnitPosLogic)appdomain.Invoke(_m172,instance,null);
					_b172=false;
					return re;
					
				}
				else
				{
					return base.createUnitPosLogic();
				}
			}
			
			IMethod _m173;
			bool _g173;
			bool _b173;
			public override UnitShowLogic3DOne createUnitShowLogic3DBase()
			{
				if(!_g173)
				{
					_m173=instance.Type.GetMethod("createUnitShowLogic3DBase",0);
					_g173=true;
				}
				
				if(_m173!=null && !_b173)
				{
					_b173=true;
					UnitShowLogic3DOne re=(UnitShowLogic3DOne)appdomain.Invoke(_m173,instance,null);
					_b173=false;
					return re;
					
				}
				else
				{
					return base.createUnitShowLogic3DBase();
				}
			}
			
			IMethod _m174;
			bool _g174;
			bool _b174;
			public override UseItemArgData createUseItemArgData()
			{
				if(!_g174)
				{
					_m174=instance.Type.GetMethod("createUseItemArgData",0);
					_g174=true;
				}
				
				if(_m174!=null && !_b174)
				{
					_b174=true;
					UseItemArgData re=(UseItemArgData)appdomain.Invoke(_m174,instance,null);
					_b174=false;
					return re;
					
				}
				else
				{
					return base.createUseItemArgData();
				}
			}
			
			IMethod _m175;
			bool _g175;
			bool _b175;
			public override VehicleIdentityData createVehicleIdentityData()
			{
				if(!_g175)
				{
					_m175=instance.Type.GetMethod("createVehicleIdentityData",0);
					_g175=true;
				}
				
				if(_m175!=null && !_b175)
				{
					_b175=true;
					VehicleIdentityData re=(VehicleIdentityData)appdomain.Invoke(_m175,instance,null);
					_b175=false;
					return re;
					
				}
				else
				{
					return base.createVehicleIdentityData();
				}
			}
			
			IMethod _m176;
			bool _g176;
			bool _b176;
			public override UnitShowLogic createUnitShowLogic()
			{
				if(!_g176)
				{
					_m176=instance.Type.GetMethod("createUnitShowLogic",0);
					_g176=true;
				}
				
				if(_m176!=null && !_b176)
				{
					_b176=true;
					UnitShowLogic re=(UnitShowLogic)appdomain.Invoke(_m176,instance,null);
					_b176=false;
					return re;
					
				}
				else
				{
					return base.createUnitShowLogic();
				}
			}
			
			IMethod _m177;
			bool _g177;
			bool _b177;
			public override SEventRegister createSEventRegister()
			{
				if(!_g177)
				{
					_m177=instance.Type.GetMethod("createSEventRegister",0);
					_g177=true;
				}
				
				if(_m177!=null && !_b177)
				{
					_b177=true;
					SEventRegister re=(SEventRegister)appdomain.Invoke(_m177,instance,null);
					_b177=false;
					return re;
					
				}
				else
				{
					return base.createSEventRegister();
				}
			}
			
			IMethod _m178;
			bool _g178;
			bool _b178;
			public override ViewControl createViewControl()
			{
				if(!_g178)
				{
					_m178=instance.Type.GetMethod("createViewControl",0);
					_g178=true;
				}
				
				if(_m178!=null && !_b178)
				{
					_b178=true;
					ViewControl re=(ViewControl)appdomain.Invoke(_m178,instance,null);
					_b178=false;
					return re;
					
				}
				else
				{
					return base.createViewControl();
				}
			}
			
			IMethod _m179;
			bool _g179;
			bool _b179;
			public override ClientVersionData createClientVersionData()
			{
				if(!_g179)
				{
					_m179=instance.Type.GetMethod("createClientVersionData",0);
					_g179=true;
				}
				
				if(_m179!=null && !_b179)
				{
					_b179=true;
					ClientVersionData re=(ClientVersionData)appdomain.Invoke(_m179,instance,null);
					_b179=false;
					return re;
					
				}
				else
				{
					return base.createClientVersionData();
				}
			}
			
			IMethod _m180;
			bool _g180;
			bool _b180;
			public override ClientLoginCacheData createClientLoginCacheData()
			{
				if(!_g180)
				{
					_m180=instance.Type.GetMethod("createClientLoginCacheData",0);
					_g180=true;
				}
				
				if(_m180!=null && !_b180)
				{
					_b180=true;
					ClientLoginCacheData re=(ClientLoginCacheData)appdomain.Invoke(_m180,instance,null);
					_b180=false;
					return re;
					
				}
				else
				{
					return base.createClientLoginCacheData();
				}
			}
			
			IMethod _m181;
			bool _g181;
			bool _b181;
			public override ClientOfflineWorkData createClientOfflineWorkData()
			{
				if(!_g181)
				{
					_m181=instance.Type.GetMethod("createClientOfflineWorkData",0);
					_g181=true;
				}
				
				if(_m181!=null && !_b181)
				{
					_b181=true;
					ClientOfflineWorkData re=(ClientOfflineWorkData)appdomain.Invoke(_m181,instance,null);
					_b181=false;
					return re;
					
				}
				else
				{
					return base.createClientOfflineWorkData();
				}
			}
			
			IMethod _m182;
			bool _g182;
			bool _b182;
			public override ClientOfflineWorkListData createClientOfflineWorkListData()
			{
				if(!_g182)
				{
					_m182=instance.Type.GetMethod("createClientOfflineWorkListData",0);
					_g182=true;
				}
				
				if(_m182!=null && !_b182)
				{
					_b182=true;
					ClientOfflineWorkListData re=(ClientOfflineWorkListData)appdomain.Invoke(_m182,instance,null);
					_b182=false;
					return re;
					
				}
				else
				{
					return base.createClientOfflineWorkListData();
				}
			}
			
			IMethod _m183;
			bool _g183;
			bool _b183;
			public override PlayerOfflineCacheExData createPlayerOfflineCacheExData()
			{
				if(!_g183)
				{
					_m183=instance.Type.GetMethod("createPlayerOfflineCacheExData",0);
					_g183=true;
				}
				
				if(_m183!=null && !_b183)
				{
					_b183=true;
					PlayerOfflineCacheExData re=(PlayerOfflineCacheExData)appdomain.Invoke(_m183,instance,null);
					_b183=false;
					return re;
					
				}
				else
				{
					return base.createPlayerOfflineCacheExData();
				}
			}
			
			IMethod _m184;
			bool _g184;
			bool _b184;
			public override PlayerVersionControl createPlayerVersionControl()
			{
				if(!_g184)
				{
					_m184=instance.Type.GetMethod("createPlayerVersionControl",0);
					_g184=true;
				}
				
				if(_m184!=null && !_b184)
				{
					_b184=true;
					PlayerVersionControl re=(PlayerVersionControl)appdomain.Invoke(_m184,instance,null);
					_b184=false;
					return re;
					
				}
				else
				{
					return base.createPlayerVersionControl();
				}
			}
			
			IMethod _m185;
			bool _g185;
			bool _b185;
			public override ClientPlayerLocalCacheData createClientPlayerLocalCacheData()
			{
				if(!_g185)
				{
					_m185=instance.Type.GetMethod("createClientPlayerLocalCacheData",0);
					_g185=true;
				}
				
				if(_m185!=null && !_b185)
				{
					_b185=true;
					ClientPlayerLocalCacheData re=(ClientPlayerLocalCacheData)appdomain.Invoke(_m185,instance,null);
					_b185=false;
					return re;
					
				}
				else
				{
					return base.createClientPlayerLocalCacheData();
				}
			}
			
			IMethod _m186;
			bool _g186;
			bool _b186;
			public override PlayerUnionTool createPlayerUnionTool()
			{
				if(!_g186)
				{
					_m186=instance.Type.GetMethod("createPlayerUnionTool",0);
					_g186=true;
				}
				
				if(_m186!=null && !_b186)
				{
					_b186=true;
					PlayerUnionTool re=(PlayerUnionTool)appdomain.Invoke(_m186,instance,null);
					_b186=false;
					return re;
					
				}
				else
				{
					return base.createPlayerUnionTool();
				}
			}
			
			IMethod _m187;
			bool _g187;
			bool _b187;
			public override PlayerTeamTool createPlayerTeamTool()
			{
				if(!_g187)
				{
					_m187=instance.Type.GetMethod("createPlayerTeamTool",0);
					_g187=true;
				}
				
				if(_m187!=null && !_b187)
				{
					_b187=true;
					PlayerTeamTool re=(PlayerTeamTool)appdomain.Invoke(_m187,instance,null);
					_b187=false;
					return re;
					
				}
				else
				{
					return base.createPlayerTeamTool();
				}
			}
			
			IMethod _m188;
			bool _g188;
			bool _b188;
			public override Role createRole()
			{
				if(!_g188)
				{
					_m188=instance.Type.GetMethod("createRole",0);
					_g188=true;
				}
				
				if(_m188!=null && !_b188)
				{
					_b188=true;
					Role re=(Role)appdomain.Invoke(_m188,instance,null);
					_b188=false;
					return re;
					
				}
				else
				{
					return base.createRole();
				}
			}
			
			IMethod _m189;
			bool _g189;
			bool _b189;
			public override EquipContainerData createEquipContainerData()
			{
				if(!_g189)
				{
					_m189=instance.Type.GetMethod("createEquipContainerData",0);
					_g189=true;
				}
				
				if(_m189!=null && !_b189)
				{
					_b189=true;
					EquipContainerData re=(EquipContainerData)appdomain.Invoke(_m189,instance,null);
					_b189=false;
					return re;
					
				}
				else
				{
					return base.createEquipContainerData();
				}
			}
			
			IMethod _m190;
			bool _g190;
			bool _b190;
			public override AreaGlobalWorkCompleteData createAreaGlobalWorkCompleteData()
			{
				if(!_g190)
				{
					_m190=instance.Type.GetMethod("createAreaGlobalWorkCompleteData",0);
					_g190=true;
				}
				
				if(_m190!=null && !_b190)
				{
					_b190=true;
					AreaGlobalWorkCompleteData re=(AreaGlobalWorkCompleteData)appdomain.Invoke(_m190,instance,null);
					_b190=false;
					return re;
					
				}
				else
				{
					return base.createAreaGlobalWorkCompleteData();
				}
			}
			
			IMethod _m191;
			bool _g191;
			bool _b191;
			public override AreaServerData createAreaServerData()
			{
				if(!_g191)
				{
					_m191=instance.Type.GetMethod("createAreaServerData",0);
					_g191=true;
				}
				
				if(_m191!=null && !_b191)
				{
					_b191=true;
					AreaServerData re=(AreaServerData)appdomain.Invoke(_m191,instance,null);
					_b191=false;
					return re;
					
				}
				else
				{
					return base.createAreaServerData();
				}
			}
			
			IMethod _m192;
			bool _g192;
			bool _b192;
			public override AuctionBuyItemData createAuctionBuyItemData()
			{
				if(!_g192)
				{
					_m192=instance.Type.GetMethod("createAuctionBuyItemData",0);
					_g192=true;
				}
				
				if(_m192!=null && !_b192)
				{
					_b192=true;
					AuctionBuyItemData re=(AuctionBuyItemData)appdomain.Invoke(_m192,instance,null);
					_b192=false;
					return re;
					
				}
				else
				{
					return base.createAuctionBuyItemData();
				}
			}
			
			IMethod _m193;
			bool _g193;
			bool _b193;
			public override AuctionItemData createAuctionItemData()
			{
				if(!_g193)
				{
					_m193=instance.Type.GetMethod("createAuctionItemData",0);
					_g193=true;
				}
				
				if(_m193!=null && !_b193)
				{
					_b193=true;
					AuctionItemData re=(AuctionItemData)appdomain.Invoke(_m193,instance,null);
					_b193=false;
					return re;
					
				}
				else
				{
					return base.createAuctionItemData();
				}
			}
			
			IMethod _m194;
			bool _g194;
			bool _b194;
			public override AuctionItemRecordData createAuctionItemRecordData()
			{
				if(!_g194)
				{
					_m194=instance.Type.GetMethod("createAuctionItemRecordData",0);
					_g194=true;
				}
				
				if(_m194!=null && !_b194)
				{
					_b194=true;
					AuctionItemRecordData re=(AuctionItemRecordData)appdomain.Invoke(_m194,instance,null);
					_b194=false;
					return re;
					
				}
				else
				{
					return base.createAuctionItemRecordData();
				}
			}
			
			IMethod _m195;
			bool _g195;
			bool _b195;
			public override AuctionQueryConditionData createAuctionQueryConditionData()
			{
				if(!_g195)
				{
					_m195=instance.Type.GetMethod("createAuctionQueryConditionData",0);
					_g195=true;
				}
				
				if(_m195!=null && !_b195)
				{
					_b195=true;
					AuctionQueryConditionData re=(AuctionQueryConditionData)appdomain.Invoke(_m195,instance,null);
					_b195=false;
					return re;
					
				}
				else
				{
					return base.createAuctionQueryConditionData();
				}
			}
			
			IMethod _m196;
			bool _g196;
			bool _b196;
			public override AuctionSoldLogData createAuctionSoldLogData()
			{
				if(!_g196)
				{
					_m196=instance.Type.GetMethod("createAuctionSoldLogData",0);
					_g196=true;
				}
				
				if(_m196!=null && !_b196)
				{
					_b196=true;
					AuctionSoldLogData re=(AuctionSoldLogData)appdomain.Invoke(_m196,instance,null);
					_b196=false;
					return re;
					
				}
				else
				{
					return base.createAuctionSoldLogData();
				}
			}
			
			IMethod _m197;
			bool _g197;
			bool _b197;
			public override AuctionToolData createAuctionToolData()
			{
				if(!_g197)
				{
					_m197=instance.Type.GetMethod("createAuctionToolData",0);
					_g197=true;
				}
				
				if(_m197!=null && !_b197)
				{
					_b197=true;
					AuctionToolData re=(AuctionToolData)appdomain.Invoke(_m197,instance,null);
					_b197=false;
					return re;
					
				}
				else
				{
					return base.createAuctionToolData();
				}
			}
			
			IMethod _m198;
			bool _g198;
			bool _b198;
			public override BigFloatData createBigFloatData()
			{
				if(!_g198)
				{
					_m198=instance.Type.GetMethod("createBigFloatData",0);
					_g198=true;
				}
				
				if(_m198!=null && !_b198)
				{
					_b198=true;
					BigFloatData re=(BigFloatData)appdomain.Invoke(_m198,instance,null);
					_b198=false;
					return re;
					
				}
				else
				{
					return base.createBigFloatData();
				}
			}
			
			IMethod _m199;
			bool _g199;
			bool _b199;
			public override BuildingIdentityData createBuildingIdentityData()
			{
				if(!_g199)
				{
					_m199=instance.Type.GetMethod("createBuildingIdentityData",0);
					_g199=true;
				}
				
				if(_m199!=null && !_b199)
				{
					_b199=true;
					BuildingIdentityData re=(BuildingIdentityData)appdomain.Invoke(_m199,instance,null);
					_b199=false;
					return re;
					
				}
				else
				{
					return base.createBuildingIdentityData();
				}
			}
			
			IMethod _m200;
			bool _g200;
			bool _b200;
			public override CenterRoleGroupToolData createCenterRoleGroupToolData()
			{
				if(!_g200)
				{
					_m200=instance.Type.GetMethod("createCenterRoleGroupToolData",0);
					_g200=true;
				}
				
				if(_m200!=null && !_b200)
				{
					_b200=true;
					CenterRoleGroupToolData re=(CenterRoleGroupToolData)appdomain.Invoke(_m200,instance,null);
					_b200=false;
					return re;
					
				}
				else
				{
					return base.createCenterRoleGroupToolData();
				}
			}
			
			IMethod _m201;
			bool _g201;
			bool _b201;
			public override CharacterControlLogic createCharacterControlLogic()
			{
				if(!_g201)
				{
					_m201=instance.Type.GetMethod("createCharacterControlLogic",0);
					_g201=true;
				}
				
				if(_m201!=null && !_b201)
				{
					_b201=true;
					CharacterControlLogic re=(CharacterControlLogic)appdomain.Invoke(_m201,instance,null);
					_b201=false;
					return re;
					
				}
				else
				{
					return base.createCharacterControlLogic();
				}
			}
			
			IMethod _m202;
			bool _g202;
			bool _b202;
			public override ChatChannelData createChatChannelData()
			{
				if(!_g202)
				{
					_m202=instance.Type.GetMethod("createChatChannelData",0);
					_g202=true;
				}
				
				if(_m202!=null && !_b202)
				{
					_b202=true;
					ChatChannelData re=(ChatChannelData)appdomain.Invoke(_m202,instance,null);
					_b202=false;
					return re;
					
				}
				else
				{
					return base.createChatChannelData();
				}
			}
			
			IMethod _m203;
			bool _g203;
			bool _b203;
			public override ClientLoginResultData createClientLoginResultData()
			{
				if(!_g203)
				{
					_m203=instance.Type.GetMethod("createClientLoginResultData",0);
					_g203=true;
				}
				
				if(_m203!=null && !_b203)
				{
					_b203=true;
					ClientLoginResultData re=(ClientLoginResultData)appdomain.Invoke(_m203,instance,null);
					_b203=false;
					return re;
					
				}
				else
				{
					return base.createClientLoginResultData();
				}
			}
			
			IMethod _m204;
			bool _g204;
			bool _b204;
			public override ClientLoginServerInfoData createClientLoginServerInfoData()
			{
				if(!_g204)
				{
					_m204=instance.Type.GetMethod("createClientLoginServerInfoData",0);
					_g204=true;
				}
				
				if(_m204!=null && !_b204)
				{
					_b204=true;
					ClientLoginServerInfoData re=(ClientLoginServerInfoData)appdomain.Invoke(_m204,instance,null);
					_b204=false;
					return re;
					
				}
				else
				{
					return base.createClientLoginServerInfoData();
				}
			}
			
			IMethod _m205;
			bool _g205;
			bool _b205;
			public override CountData createCountData()
			{
				if(!_g205)
				{
					_m205=instance.Type.GetMethod("createCountData",0);
					_g205=true;
				}
				
				if(_m205!=null && !_b205)
				{
					_b205=true;
					CountData re=(CountData)appdomain.Invoke(_m205,instance,null);
					_b205=false;
					return re;
					
				}
				else
				{
					return base.createCountData();
				}
			}
			
			IMethod _m206;
			bool _g206;
			bool _b206;
			public override CreateRoleGroupData createCreateRoleGroupData()
			{
				if(!_g206)
				{
					_m206=instance.Type.GetMethod("createCreateRoleGroupData",0);
					_g206=true;
				}
				
				if(_m206!=null && !_b206)
				{
					_b206=true;
					CreateRoleGroupData re=(CreateRoleGroupData)appdomain.Invoke(_m206,instance,null);
					_b206=false;
					return re;
					
				}
				else
				{
					return base.createCreateRoleGroupData();
				}
			}
			
			IMethod _m207;
			bool _g207;
			bool _b207;
			public override CreateSceneData createCreateSceneData()
			{
				if(!_g207)
				{
					_m207=instance.Type.GetMethod("createCreateSceneData",0);
					_g207=true;
				}
				
				if(_m207!=null && !_b207)
				{
					_b207=true;
					CreateSceneData re=(CreateSceneData)appdomain.Invoke(_m207,instance,null);
					_b207=false;
					return re;
					
				}
				else
				{
					return base.createCreateSceneData();
				}
			}
			
			IMethod _m208;
			bool _g208;
			bool _b208;
			public override CreateTeamData createCreateTeamData()
			{
				if(!_g208)
				{
					_m208=instance.Type.GetMethod("createCreateTeamData",0);
					_g208=true;
				}
				
				if(_m208!=null && !_b208)
				{
					_b208=true;
					CreateTeamData re=(CreateTeamData)appdomain.Invoke(_m208,instance,null);
					_b208=false;
					return re;
					
				}
				else
				{
					return base.createCreateTeamData();
				}
			}
			
			IMethod _m209;
			bool _g209;
			bool _b209;
			public override CreateUnionData createCreateUnionData()
			{
				if(!_g209)
				{
					_m209=instance.Type.GetMethod("createCreateUnionData",0);
					_g209=true;
				}
				
				if(_m209!=null && !_b209)
				{
					_b209=true;
					CreateUnionData re=(CreateUnionData)appdomain.Invoke(_m209,instance,null);
					_b209=false;
					return re;
					
				}
				else
				{
					return base.createCreateUnionData();
				}
			}
			
			IMethod _m210;
			bool _g210;
			bool _b210;
			public override DriveData createDriveData()
			{
				if(!_g210)
				{
					_m210=instance.Type.GetMethod("createDriveData",0);
					_g210=true;
				}
				
				if(_m210!=null && !_b210)
				{
					_b210=true;
					DriveData re=(DriveData)appdomain.Invoke(_m210,instance,null);
					_b210=false;
					return re;
					
				}
				else
				{
					return base.createDriveData();
				}
			}
			
			IMethod _m211;
			bool _g211;
			bool _b211;
			public override FieldItemBagBindData createFieldItemBagBindData()
			{
				if(!_g211)
				{
					_m211=instance.Type.GetMethod("createFieldItemBagBindData",0);
					_g211=true;
				}
				
				if(_m211!=null && !_b211)
				{
					_b211=true;
					FieldItemBagBindData re=(FieldItemBagBindData)appdomain.Invoke(_m211,instance,null);
					_b211=false;
					return re;
					
				}
				else
				{
					return base.createFieldItemBagBindData();
				}
			}
			
			IMethod _m212;
			bool _g212;
			bool _b212;
			public override FieldItemBagIdentityData createFieldItemBagIdentityData()
			{
				if(!_g212)
				{
					_m212=instance.Type.GetMethod("createFieldItemBagIdentityData",0);
					_g212=true;
				}
				
				if(_m212!=null && !_b212)
				{
					_b212=true;
					FieldItemBagIdentityData re=(FieldItemBagIdentityData)appdomain.Invoke(_m212,instance,null);
					_b212=false;
					return re;
					
				}
				else
				{
					return base.createFieldItemBagIdentityData();
				}
			}
			
			IMethod _m213;
			bool _g213;
			bool _b213;
			public override FieldItemIdentityData createFieldItemIdentityData()
			{
				if(!_g213)
				{
					_m213=instance.Type.GetMethod("createFieldItemIdentityData",0);
					_g213=true;
				}
				
				if(_m213!=null && !_b213)
				{
					_b213=true;
					FieldItemIdentityData re=(FieldItemIdentityData)appdomain.Invoke(_m213,instance,null);
					_b213=false;
					return re;
					
				}
				else
				{
					return base.createFieldItemIdentityData();
				}
			}
			
			IMethod _m214;
			bool _g214;
			bool _b214;
			public override FightUnitIdentityData createFightUnitIdentityData()
			{
				if(!_g214)
				{
					_m214=instance.Type.GetMethod("createFightUnitIdentityData",0);
					_g214=true;
				}
				
				if(_m214!=null && !_b214)
				{
					_b214=true;
					FightUnitIdentityData re=(FightUnitIdentityData)appdomain.Invoke(_m214,instance,null);
					_b214=false;
					return re;
					
				}
				else
				{
					return base.createFightUnitIdentityData();
				}
			}
			
			IMethod _m215;
			bool _g215;
			bool _b215;
			public override FuncInfoLogData createFuncInfoLogData()
			{
				if(!_g215)
				{
					_m215=instance.Type.GetMethod("createFuncInfoLogData",0);
					_g215=true;
				}
				
				if(_m215!=null && !_b215)
				{
					_b215=true;
					FuncInfoLogData re=(FuncInfoLogData)appdomain.Invoke(_m215,instance,null);
					_b215=false;
					return re;
					
				}
				else
				{
					return base.createFuncInfoLogData();
				}
			}
			
			IMethod _m216;
			bool _g216;
			bool _b216;
			public override GameAuctionToolData createGameAuctionToolData()
			{
				if(!_g216)
				{
					_m216=instance.Type.GetMethod("createGameAuctionToolData",0);
					_g216=true;
				}
				
				if(_m216!=null && !_b216)
				{
					_b216=true;
					GameAuctionToolData re=(GameAuctionToolData)appdomain.Invoke(_m216,instance,null);
					_b216=false;
					return re;
					
				}
				else
				{
					return base.createGameAuctionToolData();
				}
			}
			
			IMethod _m217;
			bool _g217;
			bool _b217;
			public override GMCommandUI createGMCommandUI()
			{
				if(!_g217)
				{
					_m217=instance.Type.GetMethod("createGMCommandUI",0);
					_g217=true;
				}
				
				if(_m217!=null && !_b217)
				{
					_b217=true;
					GMCommandUI re=(GMCommandUI)appdomain.Invoke(_m217,instance,null);
					_b217=false;
					return re;
					
				}
				else
				{
					return base.createGMCommandUI();
				}
			}
			
			IMethod _m218;
			bool _g218;
			bool _b218;
			public override GuidePart createGuidePart()
			{
				if(!_g218)
				{
					_m218=instance.Type.GetMethod("createGuidePart",0);
					_g218=true;
				}
				
				if(_m218!=null && !_b218)
				{
					_b218=true;
					GuidePart re=(GuidePart)appdomain.Invoke(_m218,instance,null);
					_b218=false;
					return re;
					
				}
				else
				{
					return base.createGuidePart();
				}
			}
			
			IMethod _m219;
			bool _g219;
			bool _b219;
			public override InfoLogData createInfoLogData()
			{
				if(!_g219)
				{
					_m219=instance.Type.GetMethod("createInfoLogData",0);
					_g219=true;
				}
				
				if(_m219!=null && !_b219)
				{
					_b219=true;
					InfoLogData re=(InfoLogData)appdomain.Invoke(_m219,instance,null);
					_b219=false;
					return re;
					
				}
				else
				{
					return base.createInfoLogData();
				}
			}
			
			IMethod _m220;
			bool _g220;
			bool _b220;
			public override IntAuctionQueryConditionData createIntAuctionQueryConditionData()
			{
				if(!_g220)
				{
					_m220=instance.Type.GetMethod("createIntAuctionQueryConditionData",0);
					_g220=true;
				}
				
				if(_m220!=null && !_b220)
				{
					_b220=true;
					IntAuctionQueryConditionData re=(IntAuctionQueryConditionData)appdomain.Invoke(_m220,instance,null);
					_b220=false;
					return re;
					
				}
				else
				{
					return base.createIntAuctionQueryConditionData();
				}
			}
			
			IMethod _m221;
			bool _g221;
			bool _b221;
			public override InviteRoleGroupReceiveData createInviteRoleGroupReceiveData()
			{
				if(!_g221)
				{
					_m221=instance.Type.GetMethod("createInviteRoleGroupReceiveData",0);
					_g221=true;
				}
				
				if(_m221!=null && !_b221)
				{
					_b221=true;
					InviteRoleGroupReceiveData re=(InviteRoleGroupReceiveData)appdomain.Invoke(_m221,instance,null);
					_b221=false;
					return re;
					
				}
				else
				{
					return base.createInviteRoleGroupReceiveData();
				}
			}
			
			IMethod _m222;
			bool _g222;
			bool _b222;
			public override ItemChatElementData createItemChatElementData()
			{
				if(!_g222)
				{
					_m222=instance.Type.GetMethod("createItemChatElementData",0);
					_g222=true;
				}
				
				if(_m222!=null && !_b222)
				{
					_b222=true;
					ItemChatElementData re=(ItemChatElementData)appdomain.Invoke(_m222,instance,null);
					_b222=false;
					return re;
					
				}
				else
				{
					return base.createItemChatElementData();
				}
			}
			
			IMethod _m223;
			bool _g223;
			bool _b223;
			public override ItemDicContainerData createItemDicContainerData()
			{
				if(!_g223)
				{
					_m223=instance.Type.GetMethod("createItemDicContainerData",0);
					_g223=true;
				}
				
				if(_m223!=null && !_b223)
				{
					_b223=true;
					ItemDicContainerData re=(ItemDicContainerData)appdomain.Invoke(_m223,instance,null);
					_b223=false;
					return re;
					
				}
				else
				{
					return base.createItemDicContainerData();
				}
			}
			
			IMethod _m224;
			bool _g224;
			bool _b224;
			public override JoyDriveLogic createJoyDriveLogic()
			{
				if(!_g224)
				{
					_m224=instance.Type.GetMethod("createJoyDriveLogic",0);
					_g224=true;
				}
				
				if(_m224!=null && !_b224)
				{
					_b224=true;
					JoyDriveLogic re=(JoyDriveLogic)appdomain.Invoke(_m224,instance,null);
					_b224=false;
					return re;
					
				}
				else
				{
					return base.createJoyDriveLogic();
				}
			}
			
			IMethod _m225;
			bool _g225;
			bool _b225;
			public override JoystickLogic createJoystickLogic()
			{
				if(!_g225)
				{
					_m225=instance.Type.GetMethod("createJoystickLogic",0);
					_g225=true;
				}
				
				if(_m225!=null && !_b225)
				{
					_b225=true;
					JoystickLogic re=(JoystickLogic)appdomain.Invoke(_m225,instance,null);
					_b225=false;
					return re;
					
				}
				else
				{
					return base.createJoystickLogic();
				}
			}
			
			IMethod _m226;
			bool _g226;
			bool _b226;
			public override MUnitIdentityData createMUnitIdentityData()
			{
				if(!_g226)
				{
					_m226=instance.Type.GetMethod("createMUnitIdentityData",0);
					_g226=true;
				}
				
				if(_m226!=null && !_b226)
				{
					_b226=true;
					MUnitIdentityData re=(MUnitIdentityData)appdomain.Invoke(_m226,instance,null);
					_b226=false;
					return re;
					
				}
				else
				{
					return base.createMUnitIdentityData();
				}
			}
			
			IMethod _m227;
			bool _g227;
			bool _b227;
			public override MUnitSaveData createMUnitSaveData()
			{
				if(!_g227)
				{
					_m227=instance.Type.GetMethod("createMUnitSaveData",0);
					_g227=true;
				}
				
				if(_m227!=null && !_b227)
				{
					_b227=true;
					MUnitSaveData re=(MUnitSaveData)appdomain.Invoke(_m227,instance,null);
					_b227=false;
					return re;
					
				}
				else
				{
					return base.createMUnitSaveData();
				}
			}
			
			IMethod _m228;
			bool _g228;
			bool _b228;
			public override NatureUIBase createNatureUIBase()
			{
				if(!_g228)
				{
					_m228=instance.Type.GetMethod("createNatureUIBase",0);
					_g228=true;
				}
				
				if(_m228!=null && !_b228)
				{
					_b228=true;
					NatureUIBase re=(NatureUIBase)appdomain.Invoke(_m228,instance,null);
					_b228=false;
					return re;
					
				}
				else
				{
					return base.createNatureUIBase();
				}
			}
			
			IMethod _m229;
			bool _g229;
			bool _b229;
			public override OperationIdentityData createOperationIdentityData()
			{
				if(!_g229)
				{
					_m229=instance.Type.GetMethod("createOperationIdentityData",0);
					_g229=true;
				}
				
				if(_m229!=null && !_b229)
				{
					_b229=true;
					OperationIdentityData re=(OperationIdentityData)appdomain.Invoke(_m229,instance,null);
					_b229=false;
					return re;
					
				}
				else
				{
					return base.createOperationIdentityData();
				}
			}
			
			IMethod _m230;
			bool _g230;
			bool _b230;
			public override PetPart createPetPart()
			{
				if(!_g230)
				{
					_m230=instance.Type.GetMethod("createPetPart",0);
					_g230=true;
				}
				
				if(_m230!=null && !_b230)
				{
					_b230=true;
					PetPart re=(PetPart)appdomain.Invoke(_m230,instance,null);
					_b230=false;
					return re;
					
				}
				else
				{
					return base.createPetPart();
				}
			}
			
			IMethod _m231;
			bool _g231;
			bool _b231;
			public override PetSaveData createPetSaveData()
			{
				if(!_g231)
				{
					_m231=instance.Type.GetMethod("createPetSaveData",0);
					_g231=true;
				}
				
				if(_m231!=null && !_b231)
				{
					_b231=true;
					PetSaveData re=(PetSaveData)appdomain.Invoke(_m231,instance,null);
					_b231=false;
					return re;
					
				}
				else
				{
					return base.createPetSaveData();
				}
			}
			
			IMethod _m232;
			bool _g232;
			bool _b232;
			public override PetUseLogic createPetUseLogic()
			{
				if(!_g232)
				{
					_m232=instance.Type.GetMethod("createPetUseLogic",0);
					_g232=true;
				}
				
				if(_m232!=null && !_b232)
				{
					_b232=true;
					PetUseLogic re=(PetUseLogic)appdomain.Invoke(_m232,instance,null);
					_b232=false;
					return re;
					
				}
				else
				{
					return base.createPetUseLogic();
				}
			}
			
			IMethod _m233;
			bool _g233;
			bool _b233;
			public override PlayerApplyRoleGroupData createPlayerApplyRoleGroupData()
			{
				if(!_g233)
				{
					_m233=instance.Type.GetMethod("createPlayerApplyRoleGroupData",0);
					_g233=true;
				}
				
				if(_m233!=null && !_b233)
				{
					_b233=true;
					PlayerApplyRoleGroupData re=(PlayerApplyRoleGroupData)appdomain.Invoke(_m233,instance,null);
					_b233=false;
					return re;
					
				}
				else
				{
					return base.createPlayerApplyRoleGroupData();
				}
			}
			
			IMethod _m234;
			bool _g234;
			bool _b234;
			public override PlayerApplyRoleGroupSelfData createPlayerApplyRoleGroupSelfData()
			{
				if(!_g234)
				{
					_m234=instance.Type.GetMethod("createPlayerApplyRoleGroupSelfData",0);
					_g234=true;
				}
				
				if(_m234!=null && !_b234)
				{
					_b234=true;
					PlayerApplyRoleGroupSelfData re=(PlayerApplyRoleGroupSelfData)appdomain.Invoke(_m234,instance,null);
					_b234=false;
					return re;
					
				}
				else
				{
					return base.createPlayerApplyRoleGroupSelfData();
				}
			}
			
			IMethod _m235;
			bool _g235;
			bool _b235;
			public override PlayerAuctionToolData createPlayerAuctionToolData()
			{
				if(!_g235)
				{
					_m235=instance.Type.GetMethod("createPlayerAuctionToolData",0);
					_g235=true;
				}
				
				if(_m235!=null && !_b235)
				{
					_b235=true;
					PlayerAuctionToolData re=(PlayerAuctionToolData)appdomain.Invoke(_m235,instance,null);
					_b235=false;
					return re;
					
				}
				else
				{
					return base.createPlayerAuctionToolData();
				}
			}
			
			IMethod _m236;
			bool _g236;
			bool _b236;
			public override PlayerRoleGroup createPlayerRoleGroup()
			{
				if(!_g236)
				{
					_m236=instance.Type.GetMethod("createPlayerRoleGroup",0);
					_g236=true;
				}
				
				if(_m236!=null && !_b236)
				{
					_b236=true;
					PlayerRoleGroup re=(PlayerRoleGroup)appdomain.Invoke(_m236,instance,null);
					_b236=false;
					return re;
					
				}
				else
				{
					return base.createPlayerRoleGroup();
				}
			}
			
			IMethod _m237;
			bool _g237;
			bool _b237;
			public override PlayerRoleGroupClientToolData createPlayerRoleGroupClientToolData()
			{
				if(!_g237)
				{
					_m237=instance.Type.GetMethod("createPlayerRoleGroupClientToolData",0);
					_g237=true;
				}
				
				if(_m237!=null && !_b237)
				{
					_b237=true;
					PlayerRoleGroupClientToolData re=(PlayerRoleGroupClientToolData)appdomain.Invoke(_m237,instance,null);
					_b237=false;
					return re;
					
				}
				else
				{
					return base.createPlayerRoleGroupClientToolData();
				}
			}
			
			IMethod _m238;
			bool _g238;
			bool _b238;
			public override PlayerRoleGroupData createPlayerRoleGroupData()
			{
				if(!_g238)
				{
					_m238=instance.Type.GetMethod("createPlayerRoleGroupData",0);
					_g238=true;
				}
				
				if(_m238!=null && !_b238)
				{
					_b238=true;
					PlayerRoleGroupData re=(PlayerRoleGroupData)appdomain.Invoke(_m238,instance,null);
					_b238=false;
					return re;
					
				}
				else
				{
					return base.createPlayerRoleGroupData();
				}
			}
			
			IMethod _m239;
			bool _g239;
			bool _b239;
			public override PlayerRoleGroupExData createPlayerRoleGroupExData()
			{
				if(!_g239)
				{
					_m239=instance.Type.GetMethod("createPlayerRoleGroupExData",0);
					_g239=true;
				}
				
				if(_m239!=null && !_b239)
				{
					_b239=true;
					PlayerRoleGroupExData re=(PlayerRoleGroupExData)appdomain.Invoke(_m239,instance,null);
					_b239=false;
					return re;
					
				}
				else
				{
					return base.createPlayerRoleGroupExData();
				}
			}
			
			IMethod _m240;
			bool _g240;
			bool _b240;
			public override PlayerRoleGroupMemberData createPlayerRoleGroupMemberData()
			{
				if(!_g240)
				{
					_m240=instance.Type.GetMethod("createPlayerRoleGroupMemberData",0);
					_g240=true;
				}
				
				if(_m240!=null && !_b240)
				{
					_b240=true;
					PlayerRoleGroupMemberData re=(PlayerRoleGroupMemberData)appdomain.Invoke(_m240,instance,null);
					_b240=false;
					return re;
					
				}
				else
				{
					return base.createPlayerRoleGroupMemberData();
				}
			}
			
			IMethod _m241;
			bool _g241;
			bool _b241;
			public override PlayerRoleGroupSaveData createPlayerRoleGroupSaveData()
			{
				if(!_g241)
				{
					_m241=instance.Type.GetMethod("createPlayerRoleGroupSaveData",0);
					_g241=true;
				}
				
				if(_m241!=null && !_b241)
				{
					_b241=true;
					PlayerRoleGroupSaveData re=(PlayerRoleGroupSaveData)appdomain.Invoke(_m241,instance,null);
					_b241=false;
					return re;
					
				}
				else
				{
					return base.createPlayerRoleGroupSaveData();
				}
			}
			
			IMethod _m242;
			bool _g242;
			bool _b242;
			public override PlayerRoleGroupToolData createPlayerRoleGroupToolData()
			{
				if(!_g242)
				{
					_m242=instance.Type.GetMethod("createPlayerRoleGroupToolData",0);
					_g242=true;
				}
				
				if(_m242!=null && !_b242)
				{
					_b242=true;
					PlayerRoleGroupToolData re=(PlayerRoleGroupToolData)appdomain.Invoke(_m242,instance,null);
					_b242=false;
					return re;
					
				}
				else
				{
					return base.createPlayerRoleGroupToolData();
				}
			}
			
			IMethod _m243;
			bool _g243;
			bool _b243;
			public override PlayerSubsectionRankToolData createPlayerSubsectionRankToolData()
			{
				if(!_g243)
				{
					_m243=instance.Type.GetMethod("createPlayerSubsectionRankToolData",0);
					_g243=true;
				}
				
				if(_m243!=null && !_b243)
				{
					_b243=true;
					PlayerSubsectionRankToolData re=(PlayerSubsectionRankToolData)appdomain.Invoke(_m243,instance,null);
					_b243=false;
					return re;
					
				}
				else
				{
					return base.createPlayerSubsectionRankToolData();
				}
			}
			
			IMethod _m244;
			bool _g244;
			bool _b244;
			public override PlayerTeam createPlayerTeam()
			{
				if(!_g244)
				{
					_m244=instance.Type.GetMethod("createPlayerTeam",0);
					_g244=true;
				}
				
				if(_m244!=null && !_b244)
				{
					_b244=true;
					PlayerTeam re=(PlayerTeam)appdomain.Invoke(_m244,instance,null);
					_b244=false;
					return re;
					
				}
				else
				{
					return base.createPlayerTeam();
				}
			}
			
			IMethod _m245;
			bool _g245;
			bool _b245;
			public override PlayerTeamData createPlayerTeamData()
			{
				if(!_g245)
				{
					_m245=instance.Type.GetMethod("createPlayerTeamData",0);
					_g245=true;
				}
				
				if(_m245!=null && !_b245)
				{
					_b245=true;
					PlayerTeamData re=(PlayerTeamData)appdomain.Invoke(_m245,instance,null);
					_b245=false;
					return re;
					
				}
				else
				{
					return base.createPlayerTeamData();
				}
			}
			
			IMethod _m246;
			bool _g246;
			bool _b246;
			public override PlayerUnion createPlayerUnion()
			{
				if(!_g246)
				{
					_m246=instance.Type.GetMethod("createPlayerUnion",0);
					_g246=true;
				}
				
				if(_m246!=null && !_b246)
				{
					_b246=true;
					PlayerUnion re=(PlayerUnion)appdomain.Invoke(_m246,instance,null);
					_b246=false;
					return re;
					
				}
				else
				{
					return base.createPlayerUnion();
				}
			}
			
			IMethod _m247;
			bool _g247;
			bool _b247;
			public override PlayerUnionData createPlayerUnionData()
			{
				if(!_g247)
				{
					_m247=instance.Type.GetMethod("createPlayerUnionData",0);
					_g247=true;
				}
				
				if(_m247!=null && !_b247)
				{
					_b247=true;
					PlayerUnionData re=(PlayerUnionData)appdomain.Invoke(_m247,instance,null);
					_b247=false;
					return re;
					
				}
				else
				{
					return base.createPlayerUnionData();
				}
			}
			
			IMethod _m248;
			bool _g248;
			bool _b248;
			public override PlayerUnionSaveData createPlayerUnionSaveData()
			{
				if(!_g248)
				{
					_m248=instance.Type.GetMethod("createPlayerUnionSaveData",0);
					_g248=true;
				}
				
				if(_m248!=null && !_b248)
				{
					_b248=true;
					PlayerUnionSaveData re=(PlayerUnionSaveData)appdomain.Invoke(_m248,instance,null);
					_b248=false;
					return re;
					
				}
				else
				{
					return base.createPlayerUnionSaveData();
				}
			}
			
			IMethod _m249;
			bool _g249;
			bool _b249;
			public override PlayerWorkCompleteData createPlayerWorkCompleteData()
			{
				if(!_g249)
				{
					_m249=instance.Type.GetMethod("createPlayerWorkCompleteData",0);
					_g249=true;
				}
				
				if(_m249!=null && !_b249)
				{
					_b249=true;
					PlayerWorkCompleteData re=(PlayerWorkCompleteData)appdomain.Invoke(_m249,instance,null);
					_b249=false;
					return re;
					
				}
				else
				{
					return base.createPlayerWorkCompleteData();
				}
			}
			
			IMethod _m250;
			bool _g250;
			bool _b250;
			public override QueryPlayerResultData createQueryPlayerResultData()
			{
				if(!_g250)
				{
					_m250=instance.Type.GetMethod("createQueryPlayerResultData",0);
					_g250=true;
				}
				
				if(_m250!=null && !_b250)
				{
					_b250=true;
					QueryPlayerResultData re=(QueryPlayerResultData)appdomain.Invoke(_m250,instance,null);
					_b250=false;
					return re;
					
				}
				else
				{
					return base.createQueryPlayerResultData();
				}
			}
			
			IMethod _m251;
			bool _g251;
			bool _b251;
			public override RectData createRectData()
			{
				if(!_g251)
				{
					_m251=instance.Type.GetMethod("createRectData",0);
					_g251=true;
				}
				
				if(_m251!=null && !_b251)
				{
					_b251=true;
					RectData re=(RectData)appdomain.Invoke(_m251,instance,null);
					_b251=false;
					return re;
					
				}
				else
				{
					return base.createRectData();
				}
			}
			
			IMethod _m252;
			bool _g252;
			bool _b252;
			public override RegionData createRegionData()
			{
				if(!_g252)
				{
					_m252=instance.Type.GetMethod("createRegionData",0);
					_g252=true;
				}
				
				if(_m252!=null && !_b252)
				{
					_b252=true;
					RegionData re=(RegionData)appdomain.Invoke(_m252,instance,null);
					_b252=false;
					return re;
					
				}
				else
				{
					return base.createRegionData();
				}
			}
			
			IMethod _m253;
			bool _g253;
			bool _b253;
			public override RoleAttributeData createRoleAttributeData()
			{
				if(!_g253)
				{
					_m253=instance.Type.GetMethod("createRoleAttributeData",0);
					_g253=true;
				}
				
				if(_m253!=null && !_b253)
				{
					_b253=true;
					RoleAttributeData re=(RoleAttributeData)appdomain.Invoke(_m253,instance,null);
					_b253=false;
					return re;
					
				}
				else
				{
					return base.createRoleAttributeData();
				}
			}
			
			IMethod _m254;
			bool _g254;
			bool _b254;
			public override RoleAttributeDataLogic createRoleAttributeDataLogic()
			{
				if(!_g254)
				{
					_m254=instance.Type.GetMethod("createRoleAttributeDataLogic",0);
					_g254=true;
				}
				
				if(_m254!=null && !_b254)
				{
					_b254=true;
					RoleAttributeDataLogic re=(RoleAttributeDataLogic)appdomain.Invoke(_m254,instance,null);
					_b254=false;
					return re;
					
				}
				else
				{
					return base.createRoleAttributeDataLogic();
				}
			}
			
			IMethod _m255;
			bool _g255;
			bool _b255;
			public override RoleAttributeLogic createRoleAttributeLogic()
			{
				if(!_g255)
				{
					_m255=instance.Type.GetMethod("createRoleAttributeLogic",0);
					_g255=true;
				}
				
				if(_m255!=null && !_b255)
				{
					_b255=true;
					RoleAttributeLogic re=(RoleAttributeLogic)appdomain.Invoke(_m255,instance,null);
					_b255=false;
					return re;
					
				}
				else
				{
					return base.createRoleAttributeLogic();
				}
			}
			
			IMethod _m256;
			bool _g256;
			bool _b256;
			public override RoleBuildLogic createRoleBuildLogic()
			{
				if(!_g256)
				{
					_m256=instance.Type.GetMethod("createRoleBuildLogic",0);
					_g256=true;
				}
				
				if(_m256!=null && !_b256)
				{
					_b256=true;
					RoleBuildLogic re=(RoleBuildLogic)appdomain.Invoke(_m256,instance,null);
					_b256=false;
					return re;
					
				}
				else
				{
					return base.createRoleBuildLogic();
				}
			}
			
			IMethod _m257;
			bool _g257;
			bool _b257;
			public override RoleChatData createRoleChatData()
			{
				if(!_g257)
				{
					_m257=instance.Type.GetMethod("createRoleChatData",0);
					_g257=true;
				}
				
				if(_m257!=null && !_b257)
				{
					_b257=true;
					RoleChatData re=(RoleChatData)appdomain.Invoke(_m257,instance,null);
					_b257=false;
					return re;
					
				}
				else
				{
					return base.createRoleChatData();
				}
			}
			
			IMethod _m258;
			bool _g258;
			bool _b258;
			public override RoleForceData createRoleForceData()
			{
				if(!_g258)
				{
					_m258=instance.Type.GetMethod("createRoleForceData",0);
					_g258=true;
				}
				
				if(_m258!=null && !_b258)
				{
					_b258=true;
					RoleForceData re=(RoleForceData)appdomain.Invoke(_m258,instance,null);
					_b258=false;
					return re;
					
				}
				else
				{
					return base.createRoleForceData();
				}
			}
			
			IMethod _m259;
			bool _g259;
			bool _b259;
			public override RoleForceLogic createRoleForceLogic()
			{
				if(!_g259)
				{
					_m259=instance.Type.GetMethod("createRoleForceLogic",0);
					_g259=true;
				}
				
				if(_m259!=null && !_b259)
				{
					_b259=true;
					RoleForceLogic re=(RoleForceLogic)appdomain.Invoke(_m259,instance,null);
					_b259=false;
					return re;
					
				}
				else
				{
					return base.createRoleForceLogic();
				}
			}
			
			IMethod _m260;
			bool _g260;
			bool _b260;
			public override RoleGroupChangeData createRoleGroupChangeData()
			{
				if(!_g260)
				{
					_m260=instance.Type.GetMethod("createRoleGroupChangeData",0);
					_g260=true;
				}
				
				if(_m260!=null && !_b260)
				{
					_b260=true;
					RoleGroupChangeData re=(RoleGroupChangeData)appdomain.Invoke(_m260,instance,null);
					_b260=false;
					return re;
					
				}
				else
				{
					return base.createRoleGroupChangeData();
				}
			}
			
			IMethod _m261;
			bool _g261;
			bool _b261;
			public override RoleGroupCreateSceneData createRoleGroupCreateSceneData()
			{
				if(!_g261)
				{
					_m261=instance.Type.GetMethod("createRoleGroupCreateSceneData",0);
					_g261=true;
				}
				
				if(_m261!=null && !_b261)
				{
					_b261=true;
					RoleGroupCreateSceneData re=(RoleGroupCreateSceneData)appdomain.Invoke(_m261,instance,null);
					_b261=false;
					return re;
					
				}
				else
				{
					return base.createRoleGroupCreateSceneData();
				}
			}
			
			IMethod _m262;
			bool _g262;
			bool _b262;
			public override RoleGroupData createRoleGroupData()
			{
				if(!_g262)
				{
					_m262=instance.Type.GetMethod("createRoleGroupData",0);
					_g262=true;
				}
				
				if(_m262!=null && !_b262)
				{
					_b262=true;
					RoleGroupData re=(RoleGroupData)appdomain.Invoke(_m262,instance,null);
					_b262=false;
					return re;
					
				}
				else
				{
					return base.createRoleGroupData();
				}
			}
			
			IMethod _m263;
			bool _g263;
			bool _b263;
			public override RoleGroupMemberChangeData createRoleGroupMemberChangeData()
			{
				if(!_g263)
				{
					_m263=instance.Type.GetMethod("createRoleGroupMemberChangeData",0);
					_g263=true;
				}
				
				if(_m263!=null && !_b263)
				{
					_b263=true;
					RoleGroupMemberChangeData re=(RoleGroupMemberChangeData)appdomain.Invoke(_m263,instance,null);
					_b263=false;
					return re;
					
				}
				else
				{
					return base.createRoleGroupMemberChangeData();
				}
			}
			
			IMethod _m264;
			bool _g264;
			bool _b264;
			public override RoleGroupMemberData createRoleGroupMemberData()
			{
				if(!_g264)
				{
					_m264=instance.Type.GetMethod("createRoleGroupMemberData",0);
					_g264=true;
				}
				
				if(_m264!=null && !_b264)
				{
					_b264=true;
					RoleGroupMemberData re=(RoleGroupMemberData)appdomain.Invoke(_m264,instance,null);
					_b264=false;
					return re;
					
				}
				else
				{
					return base.createRoleGroupMemberData();
				}
			}
			
			IMethod _m265;
			bool _g265;
			bool _b265;
			public override RoleGroupRankData createRoleGroupRankData()
			{
				if(!_g265)
				{
					_m265=instance.Type.GetMethod("createRoleGroupRankData",0);
					_g265=true;
				}
				
				if(_m265!=null && !_b265)
				{
					_b265=true;
					RoleGroupRankData re=(RoleGroupRankData)appdomain.Invoke(_m265,instance,null);
					_b265=false;
					return re;
					
				}
				else
				{
					return base.createRoleGroupRankData();
				}
			}
			
			IMethod _m266;
			bool _g266;
			bool _b266;
			public override RoleGroupSimpleData createRoleGroupSimpleData()
			{
				if(!_g266)
				{
					_m266=instance.Type.GetMethod("createRoleGroupSimpleData",0);
					_g266=true;
				}
				
				if(_m266!=null && !_b266)
				{
					_b266=true;
					RoleGroupSimpleData re=(RoleGroupSimpleData)appdomain.Invoke(_m266,instance,null);
					_b266=false;
					return re;
					
				}
				else
				{
					return base.createRoleGroupSimpleData();
				}
			}
			
			IMethod _m267;
			bool _g267;
			bool _b267;
			public override RoleGroupToolData createRoleGroupToolData()
			{
				if(!_g267)
				{
					_m267=instance.Type.GetMethod("createRoleGroupToolData",0);
					_g267=true;
				}
				
				if(_m267!=null && !_b267)
				{
					_b267=true;
					RoleGroupToolData re=(RoleGroupToolData)appdomain.Invoke(_m267,instance,null);
					_b267=false;
					return re;
					
				}
				else
				{
					return base.createRoleGroupToolData();
				}
			}
			
			IMethod _m268;
			bool _g268;
			bool _b268;
			public override RoleLogicBase createRoleLogicBase()
			{
				if(!_g268)
				{
					_m268=instance.Type.GetMethod("createRoleLogicBase",0);
					_g268=true;
				}
				
				if(_m268!=null && !_b268)
				{
					_b268=true;
					RoleLogicBase re=(RoleLogicBase)appdomain.Invoke(_m268,instance,null);
					_b268=false;
					return re;
					
				}
				else
				{
					return base.createRoleLogicBase();
				}
			}
			
			IMethod _m269;
			bool _g269;
			bool _b269;
			public override RoleShowInfoLogData createRoleShowInfoLogData()
			{
				if(!_g269)
				{
					_m269=instance.Type.GetMethod("createRoleShowInfoLogData",0);
					_g269=true;
				}
				
				if(_m269!=null && !_b269)
				{
					_b269=true;
					RoleShowInfoLogData re=(RoleShowInfoLogData)appdomain.Invoke(_m269,instance,null);
					_b269=false;
					return re;
					
				}
				else
				{
					return base.createRoleShowInfoLogData();
				}
			}
			
			IMethod _m270;
			bool _g270;
			bool _b270;
			public override RoleShowLogData createRoleShowLogData()
			{
				if(!_g270)
				{
					_m270=instance.Type.GetMethod("createRoleShowLogData",0);
					_g270=true;
				}
				
				if(_m270!=null && !_b270)
				{
					_b270=true;
					RoleShowLogData re=(RoleShowLogData)appdomain.Invoke(_m270,instance,null);
					_b270=false;
					return re;
					
				}
				else
				{
					return base.createRoleShowLogData();
				}
			}
			
			IMethod _m271;
			bool _g271;
			bool _b271;
			public override RoleSocialPoolData createRoleSocialPoolData()
			{
				if(!_g271)
				{
					_m271=instance.Type.GetMethod("createRoleSocialPoolData",0);
					_g271=true;
				}
				
				if(_m271!=null && !_b271)
				{
					_b271=true;
					RoleSocialPoolData re=(RoleSocialPoolData)appdomain.Invoke(_m271,instance,null);
					_b271=false;
					return re;
					
				}
				else
				{
					return base.createRoleSocialPoolData();
				}
			}
			
			IMethod _m272;
			bool _g272;
			bool _b272;
			public override RoleSocialPoolToolData createRoleSocialPoolToolData()
			{
				if(!_g272)
				{
					_m272=instance.Type.GetMethod("createRoleSocialPoolToolData",0);
					_g272=true;
				}
				
				if(_m272!=null && !_b272)
				{
					_b272=true;
					RoleSocialPoolToolData re=(RoleSocialPoolToolData)appdomain.Invoke(_m272,instance,null);
					_b272=false;
					return re;
					
				}
				else
				{
					return base.createRoleSocialPoolToolData();
				}
			}
			
			IMethod _m273;
			bool _g273;
			bool _b273;
			public override SceneCameraFixedLogic createSceneCameraFixedLogic()
			{
				if(!_g273)
				{
					_m273=instance.Type.GetMethod("createSceneCameraFixedLogic",0);
					_g273=true;
				}
				
				if(_m273!=null && !_b273)
				{
					_b273=true;
					SceneCameraFixedLogic re=(SceneCameraFixedLogic)appdomain.Invoke(_m273,instance,null);
					_b273=false;
					return re;
					
				}
				else
				{
					return base.createSceneCameraFixedLogic();
				}
			}
			
			IMethod _m274;
			bool _g274;
			bool _b274;
			public override SceneCameraLogic3DOne createSceneCameraLogic3DOne()
			{
				if(!_g274)
				{
					_m274=instance.Type.GetMethod("createSceneCameraLogic3DOne",0);
					_g274=true;
				}
				
				if(_m274!=null && !_b274)
				{
					_b274=true;
					SceneCameraLogic3DOne re=(SceneCameraLogic3DOne)appdomain.Invoke(_m274,instance,null);
					_b274=false;
					return re;
					
				}
				else
				{
					return base.createSceneCameraLogic3DOne();
				}
			}
			
			IMethod _m275;
			bool _g275;
			bool _b275;
			public override SceneLocationData createSceneLocationData()
			{
				if(!_g275)
				{
					_m275=instance.Type.GetMethod("createSceneLocationData",0);
					_g275=true;
				}
				
				if(_m275!=null && !_b275)
				{
					_b275=true;
					SceneLocationData re=(SceneLocationData)appdomain.Invoke(_m275,instance,null);
					_b275=false;
					return re;
					
				}
				else
				{
					return base.createSceneLocationData();
				}
			}
			
			IMethod _m276;
			bool _g276;
			bool _b276;
			public override SceneLocationRoleShowChangeData createSceneLocationRoleShowChangeData()
			{
				if(!_g276)
				{
					_m276=instance.Type.GetMethod("createSceneLocationRoleShowChangeData",0);
					_g276=true;
				}
				
				if(_m276!=null && !_b276)
				{
					_b276=true;
					SceneLocationRoleShowChangeData re=(SceneLocationRoleShowChangeData)appdomain.Invoke(_m276,instance,null);
					_b276=false;
					return re;
					
				}
				else
				{
					return base.createSceneLocationRoleShowChangeData();
				}
			}
			
			IMethod _m277;
			bool _g277;
			bool _b277;
			public override ScenePosLogic3DOne createScenePosLogic3DOne()
			{
				if(!_g277)
				{
					_m277=instance.Type.GetMethod("createScenePosLogic3DOne",0);
					_g277=true;
				}
				
				if(_m277!=null && !_b277)
				{
					_b277=true;
					ScenePosLogic3DOne re=(ScenePosLogic3DOne)appdomain.Invoke(_m277,instance,null);
					_b277=false;
					return re;
					
				}
				else
				{
					return base.createScenePosLogic3DOne();
				}
			}
			
			IMethod _m278;
			bool _g278;
			bool _b278;
			public override SceneRoleData createSceneRoleData()
			{
				if(!_g278)
				{
					_m278=instance.Type.GetMethod("createSceneRoleData",0);
					_g278=true;
				}
				
				if(_m278!=null && !_b278)
				{
					_b278=true;
					SceneRoleData re=(SceneRoleData)appdomain.Invoke(_m278,instance,null);
					_b278=false;
					return re;
					
				}
				else
				{
					return base.createSceneRoleData();
				}
			}
			
			IMethod _m279;
			bool _g279;
			bool _b279;
			public override SceneRoleLogic createSceneRoleLogic()
			{
				if(!_g279)
				{
					_m279=instance.Type.GetMethod("createSceneRoleLogic",0);
					_g279=true;
				}
				
				if(_m279!=null && !_b279)
				{
					_b279=true;
					SceneRoleLogic re=(SceneRoleLogic)appdomain.Invoke(_m279,instance,null);
					_b279=false;
					return re;
					
				}
				else
				{
					return base.createSceneRoleLogic();
				}
			}
			
			IMethod _m280;
			bool _g280;
			bool _b280;
			public override SceneShowLogic3DOne createSceneShowLogic3DOne()
			{
				if(!_g280)
				{
					_m280=instance.Type.GetMethod("createSceneShowLogic3DOne",0);
					_g280=true;
				}
				
				if(_m280!=null && !_b280)
				{
					_b280=true;
					SceneShowLogic3DOne re=(SceneShowLogic3DOne)appdomain.Invoke(_m280,instance,null);
					_b280=false;
					return re;
					
				}
				else
				{
					return base.createSceneShowLogic3DOne();
				}
			}
			
			IMethod _m281;
			bool _g281;
			bool _b281;
			public override SceneUnitFactoryLogic createSceneUnitFactoryLogic()
			{
				if(!_g281)
				{
					_m281=instance.Type.GetMethod("createSceneUnitFactoryLogic",0);
					_g281=true;
				}
				
				if(_m281!=null && !_b281)
				{
					_b281=true;
					SceneUnitFactoryLogic re=(SceneUnitFactoryLogic)appdomain.Invoke(_m281,instance,null);
					_b281=false;
					return re;
					
				}
				else
				{
					return base.createSceneUnitFactoryLogic();
				}
			}
			
			IMethod _m282;
			bool _g282;
			bool _b282;
			public override SelectServerUI createSelectServerUI()
			{
				if(!_g282)
				{
					_m282=instance.Type.GetMethod("createSelectServerUI",0);
					_g282=true;
				}
				
				if(_m282!=null && !_b282)
				{
					_b282=true;
					SelectServerUI re=(SelectServerUI)appdomain.Invoke(_m282,instance,null);
					_b282=false;
					return re;
					
				}
				else
				{
					return base.createSelectServerUI();
				}
			}
			
			IMethod _m283;
			bool _g283;
			bool _b283;
			public override StatusDataLogic createStatusDataLogic()
			{
				if(!_g283)
				{
					_m283=instance.Type.GetMethod("createStatusDataLogic",0);
					_g283=true;
				}
				
				if(_m283!=null && !_b283)
				{
					_b283=true;
					StatusDataLogic re=(StatusDataLogic)appdomain.Invoke(_m283,instance,null);
					_b283=false;
					return re;
					
				}
				else
				{
					return base.createStatusDataLogic();
				}
			}
			
			IMethod _m284;
			bool _g284;
			bool _b284;
			public override SubsectionRankSimpleData createSubsectionRankSimpleData()
			{
				if(!_g284)
				{
					_m284=instance.Type.GetMethod("createSubsectionRankSimpleData",0);
					_g284=true;
				}
				
				if(_m284!=null && !_b284)
				{
					_b284=true;
					SubsectionRankSimpleData re=(SubsectionRankSimpleData)appdomain.Invoke(_m284,instance,null);
					_b284=false;
					return re;
					
				}
				else
				{
					return base.createSubsectionRankSimpleData();
				}
			}
			
			IMethod _m285;
			bool _g285;
			bool _b285;
			public override SubsectionRankToolData createSubsectionRankToolData()
			{
				if(!_g285)
				{
					_m285=instance.Type.GetMethod("createSubsectionRankToolData",0);
					_g285=true;
				}
				
				if(_m285!=null && !_b285)
				{
					_b285=true;
					SubsectionRankToolData re=(SubsectionRankToolData)appdomain.Invoke(_m285,instance,null);
					_b285=false;
					return re;
					
				}
				else
				{
					return base.createSubsectionRankToolData();
				}
			}
			
			IMethod _m286;
			bool _g286;
			bool _b286;
			public override TeamData createTeamData()
			{
				if(!_g286)
				{
					_m286=instance.Type.GetMethod("createTeamData",0);
					_g286=true;
				}
				
				if(_m286!=null && !_b286)
				{
					_b286=true;
					TeamData re=(TeamData)appdomain.Invoke(_m286,instance,null);
					_b286=false;
					return re;
					
				}
				else
				{
					return base.createTeamData();
				}
			}
			
			IMethod _m287;
			bool _g287;
			bool _b287;
			public override TeamMemberData createTeamMemberData()
			{
				if(!_g287)
				{
					_m287=instance.Type.GetMethod("createTeamMemberData",0);
					_g287=true;
				}
				
				if(_m287!=null && !_b287)
				{
					_b287=true;
					TeamMemberData re=(TeamMemberData)appdomain.Invoke(_m287,instance,null);
					_b287=false;
					return re;
					
				}
				else
				{
					return base.createTeamMemberData();
				}
			}
			
			IMethod _m288;
			bool _g288;
			bool _b288;
			public override TeamSimpleData createTeamSimpleData()
			{
				if(!_g288)
				{
					_m288=instance.Type.GetMethod("createTeamSimpleData",0);
					_g288=true;
				}
				
				if(_m288!=null && !_b288)
				{
					_b288=true;
					TeamSimpleData re=(TeamSimpleData)appdomain.Invoke(_m288,instance,null);
					_b288=false;
					return re;
					
				}
				else
				{
					return base.createTeamSimpleData();
				}
			}
			
			IMethod _m289;
			bool _g289;
			bool _b289;
			public override UII18NText createUII18NText()
			{
				if(!_g289)
				{
					_m289=instance.Type.GetMethod("createUII18NText",0);
					_g289=true;
				}
				
				if(_m289!=null && !_b289)
				{
					_b289=true;
					UII18NText re=(UII18NText)appdomain.Invoke(_m289,instance,null);
					_b289=false;
					return re;
					
				}
				else
				{
					return base.createUII18NText();
				}
			}
			
			IMethod _m290;
			bool _g290;
			bool _b290;
			public override UnionData createUnionData()
			{
				if(!_g290)
				{
					_m290=instance.Type.GetMethod("createUnionData",0);
					_g290=true;
				}
				
				if(_m290!=null && !_b290)
				{
					_b290=true;
					UnionData re=(UnionData)appdomain.Invoke(_m290,instance,null);
					_b290=false;
					return re;
					
				}
				else
				{
					return base.createUnionData();
				}
			}
			
			IMethod _m291;
			bool _g291;
			bool _b291;
			public override UnionMemberData createUnionMemberData()
			{
				if(!_g291)
				{
					_m291=instance.Type.GetMethod("createUnionMemberData",0);
					_g291=true;
				}
				
				if(_m291!=null && !_b291)
				{
					_b291=true;
					UnionMemberData re=(UnionMemberData)appdomain.Invoke(_m291,instance,null);
					_b291=false;
					return re;
					
				}
				else
				{
					return base.createUnionMemberData();
				}
			}
			
			IMethod _m292;
			bool _g292;
			bool _b292;
			public override UnionSimpleData createUnionSimpleData()
			{
				if(!_g292)
				{
					_m292=instance.Type.GetMethod("createUnionSimpleData",0);
					_g292=true;
				}
				
				if(_m292!=null && !_b292)
				{
					_b292=true;
					UnionSimpleData re=(UnionSimpleData)appdomain.Invoke(_m292,instance,null);
					_b292=false;
					return re;
					
				}
				else
				{
					return base.createUnionSimpleData();
				}
			}
			
			IMethod _m293;
			bool _g293;
			bool _b293;
			public override UnitAICommandLogic createUnitAICommandLogic()
			{
				if(!_g293)
				{
					_m293=instance.Type.GetMethod("createUnitAICommandLogic",0);
					_g293=true;
				}
				
				if(_m293!=null && !_b293)
				{
					_b293=true;
					UnitAICommandLogic re=(UnitAICommandLogic)appdomain.Invoke(_m293,instance,null);
					_b293=false;
					return re;
					
				}
				else
				{
					return base.createUnitAICommandLogic();
				}
			}
			
			IMethod _m294;
			bool _g294;
			bool _b294;
			public override UnitFuncData createUnitFuncData()
			{
				if(!_g294)
				{
					_m294=instance.Type.GetMethod("createUnitFuncData",0);
					_g294=true;
				}
				
				if(_m294!=null && !_b294)
				{
					_b294=true;
					UnitFuncData re=(UnitFuncData)appdomain.Invoke(_m294,instance,null);
					_b294=false;
					return re;
					
				}
				else
				{
					return base.createUnitFuncData();
				}
			}
			
			IMethod _m295;
			bool _g295;
			bool _b295;
			public override UnitHeadLogic3DOne createUnitHeadLogic3DOne()
			{
				if(!_g295)
				{
					_m295=instance.Type.GetMethod("createUnitHeadLogic3DOne",0);
					_g295=true;
				}
				
				if(_m295!=null && !_b295)
				{
					_b295=true;
					UnitHeadLogic3DOne re=(UnitHeadLogic3DOne)appdomain.Invoke(_m295,instance,null);
					_b295=false;
					return re;
					
				}
				else
				{
					return base.createUnitHeadLogic3DOne();
				}
			}
			
			IMethod _m296;
			bool _g296;
			bool _b296;
			public override UnitMoveData createUnitMoveData()
			{
				if(!_g296)
				{
					_m296=instance.Type.GetMethod("createUnitMoveData",0);
					_g296=true;
				}
				
				if(_m296!=null && !_b296)
				{
					_b296=true;
					UnitMoveData re=(UnitMoveData)appdomain.Invoke(_m296,instance,null);
					_b296=false;
					return re;
					
				}
				else
				{
					return base.createUnitMoveData();
				}
			}
			
			IMethod _m297;
			bool _g297;
			bool _b297;
			public override UnitMoveLogic createUnitMoveLogic()
			{
				if(!_g297)
				{
					_m297=instance.Type.GetMethod("createUnitMoveLogic",0);
					_g297=true;
				}
				
				if(_m297!=null && !_b297)
				{
					_b297=true;
					UnitMoveLogic re=(UnitMoveLogic)appdomain.Invoke(_m297,instance,null);
					_b297=false;
					return re;
					
				}
				else
				{
					return base.createUnitMoveLogic();
				}
			}
			
			IMethod _m298;
			bool _g298;
			bool _b298;
			public override UnitNormalData createUnitNormalData()
			{
				if(!_g298)
				{
					_m298=instance.Type.GetMethod("createUnitNormalData",0);
					_g298=true;
				}
				
				if(_m298!=null && !_b298)
				{
					_b298=true;
					UnitNormalData re=(UnitNormalData)appdomain.Invoke(_m298,instance,null);
					_b298=false;
					return re;
					
				}
				else
				{
					return base.createUnitNormalData();
				}
			}
			
			IMethod _m299;
			bool _g299;
			bool _b299;
			public override UnitShowLogic3DOne createUnitShowLogic3DOne()
			{
				if(!_g299)
				{
					_m299=instance.Type.GetMethod("createUnitShowLogic3DOne",0);
					_g299=true;
				}
				
				if(_m299!=null && !_b299)
				{
					_b299=true;
					UnitShowLogic3DOne re=(UnitShowLogic3DOne)appdomain.Invoke(_m299,instance,null);
					_b299=false;
					return re;
					
				}
				else
				{
					return base.createUnitShowLogic3DOne();
				}
			}
			
			IMethod _m300;
			bool _g300;
			bool _b300;
			public override VehicleIdentityLogic createVehicleIdentityLogic()
			{
				if(!_g300)
				{
					_m300=instance.Type.GetMethod("createVehicleIdentityLogic",0);
					_g300=true;
				}
				
				if(_m300!=null && !_b300)
				{
					_b300=true;
					VehicleIdentityLogic re=(VehicleIdentityLogic)appdomain.Invoke(_m300,instance,null);
					_b300=false;
					return re;
					
				}
				else
				{
					return base.createVehicleIdentityLogic();
				}
			}
			
			IMethod _m301;
			bool _g301;
			bool _b301;
			public override WorkCompleteData createWorkCompleteData()
			{
				if(!_g301)
				{
					_m301=instance.Type.GetMethod("createWorkCompleteData",0);
					_g301=true;
				}
				
				if(_m301!=null && !_b301)
				{
					_b301=true;
					WorkCompleteData re=(WorkCompleteData)appdomain.Invoke(_m301,instance,null);
					_b301=false;
					return re;
					
				}
				else
				{
					return base.createWorkCompleteData();
				}
			}
			
			IMethod _m302;
			bool _g302;
			bool _b302;
			public override WorkReceiverData createWorkReceiverData()
			{
				if(!_g302)
				{
					_m302=instance.Type.GetMethod("createWorkReceiverData",0);
					_g302=true;
				}
				
				if(_m302!=null && !_b302)
				{
					_b302=true;
					WorkReceiverData re=(WorkReceiverData)appdomain.Invoke(_m302,instance,null);
					_b302=false;
					return re;
					
				}
				else
				{
					return base.createWorkReceiverData();
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
