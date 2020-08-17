using System;
using System.Text;
using ILRuntime.CLR.Method;
using ILRuntime.Runtime.Enviorment;
using ILRuntime.Runtime.Intepreter;
using UnityEngine;
using AppDomain = ILRuntime.Runtime.Enviorment.AppDomain;
using ShineEngine;

	public class BaseLogicControlAdapter : CrossBindingAdaptor
	{
		public override Type BaseCLRType
		{
			get
			{
				return typeof(BaseLogicControl);
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

		public class Adaptor : BaseLogicControl, CrossBindingAdaptorType
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
			
			private object[] _p6=new object[6];
			
			

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
			public override UseItemArgData createUseItemArgData(int type)
			{
				if(!_g1)
				{
					_m1=instance.Type.GetMethod("createUseItemArgData",1);
					_g1=true;
				}
				
				if(_m1!=null && !_b1)
				{
					_b1=true;
					_p1[0]=type;
					UseItemArgData re=(UseItemArgData)appdomain.Invoke(_m1,instance,_p1);
					_p1[0]=null;
					_b1=false;
					return re;
					
				}
				else
				{
					return base.createUseItemArgData(type);
				}
			}
			
			IMethod _m2;
			bool _g2;
			bool _b2;
			public override ItemIdentityData createItemIdentityByType(int type)
			{
				if(!_g2)
				{
					_m2=instance.Type.GetMethod("createItemIdentityByType",1);
					_g2=true;
				}
				
				if(_m2!=null && !_b2)
				{
					_b2=true;
					_p1[0]=type;
					ItemIdentityData re=(ItemIdentityData)appdomain.Invoke(_m2,instance,_p1);
					_p1[0]=null;
					_b2=false;
					return re;
					
				}
				else
				{
					return base.createItemIdentityByType(type);
				}
			}
			
			IMethod _m3;
			bool _g3;
			bool _b3;
			public override int getUnitType(int unitDataID)
			{
				if(!_g3)
				{
					_m3=instance.Type.GetMethod("getUnitType",1);
					_g3=true;
				}
				
				if(_m3!=null && !_b3)
				{
					_b3=true;
					_p1[0]=unitDataID;
					int re=(int)appdomain.Invoke(_m3,instance,_p1);
					_p1[0]=null;
					_b3=false;
					return re;
					
				}
				else
				{
					return base.getUnitType(unitDataID);
				}
			}
			
			IMethod _m4;
			bool _g4;
			bool _b4;
			protected override void addColorFront(StringBuilder sb,string colorStr)
			{
				if(!_g4)
				{
					_m4=instance.Type.GetMethod("addColorFront",2);
					_g4=true;
				}
				
				if(_m4!=null && !_b4)
				{
					_b4=true;
					_p2[0]=sb;
					_p2[1]=colorStr;
					appdomain.Invoke(_m4,instance,_p2);
					_p2[0]=null;
					_p2[1]=null;
					_b4=false;
					
				}
				else
				{
					base.addColorFront(sb,colorStr);
				}
			}
			
			IMethod _m5;
			bool _g5;
			bool _b5;
			protected override void addColorEnd(StringBuilder sb)
			{
				if(!_g5)
				{
					_m5=instance.Type.GetMethod("addColorEnd",1);
					_g5=true;
				}
				
				if(_m5!=null && !_b5)
				{
					_b5=true;
					_p1[0]=sb;
					appdomain.Invoke(_m5,instance,_p1);
					_p1[0]=null;
					_b5=false;
					
				}
				else
				{
					base.addColorEnd(sb);
				}
			}
			
			IMethod _m6;
			bool _g6;
			bool _b6;
			public override int calculateAttribute(AttributeTool tool,int[] args)
			{
				if(!_g6)
				{
					_m6=instance.Type.GetMethod("calculateAttribute",2);
					_g6=true;
				}
				
				if(_m6!=null && !_b6)
				{
					_b6=true;
					_p2[0]=tool;
					_p2[1]=args;
					int re=(int)appdomain.Invoke(_m6,instance,_p2);
					_p2[0]=null;
					_p2[1]=null;
					_b6=false;
					return re;
					
				}
				else
				{
					return base.calculateAttribute(tool,args);
				}
			}
			
			IMethod _m7;
			bool _g7;
			bool _b7;
			public override int calculateSkillVar(int formulaType,int[][] args,UnitFightDataLogic self,UnitFightDataLogic target,int[] selfValues,int start)
			{
				if(!_g7)
				{
					_m7=instance.Type.GetMethod("calculateSkillVar",6);
					_g7=true;
				}
				
				if(_m7!=null && !_b7)
				{
					_b7=true;
					_p6[0]=formulaType;
					_p6[1]=args;
					_p6[2]=self;
					_p6[3]=target;
					_p6[4]=selfValues;
					_p6[5]=start;
					int re=(int)appdomain.Invoke(_m7,instance,_p6);
					_p6[0]=null;
					_p6[1]=null;
					_p6[2]=null;
					_p6[3]=null;
					_p6[4]=null;
					_p6[5]=null;
					_b7=false;
					return re;
					
				}
				else
				{
					return base.calculateSkillVar(formulaType,args,self,target,selfValues,start);
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
