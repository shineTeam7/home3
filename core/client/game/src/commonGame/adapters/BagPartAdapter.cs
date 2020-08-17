using System;
using System.Text;
using ILRuntime.CLR.Method;
using ILRuntime.Runtime.Enviorment;
using ILRuntime.Runtime.Intepreter;
using UnityEngine;
using AppDomain = ILRuntime.Runtime.Enviorment.AppDomain;
using ShineEngine;

	public class BagPartAdapter : CrossBindingAdaptor
	{
		public override Type BaseCLRType
		{
			get
			{
				return typeof(BagPart);
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

		public class Adaptor : BagPart, CrossBindingAdaptorType
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
			
			private object[] _p4=new object[4];
			
			

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
			public override ItemData getItem(int index)
			{
				if(!_g20)
				{
					_m20=instance.Type.GetMethod("getItem",1);
					_g20=true;
				}
				
				if(_m20!=null && !_b20)
				{
					_b20=true;
					_p1[0]=index;
					ItemData re=(ItemData)appdomain.Invoke(_m20,instance,_p1);
					_p1[0]=null;
					_b20=false;
					return re;
					
				}
				else
				{
					return base.getItem(index);
				}
			}
			
			IMethod _m21;
			bool _g21;
			bool _b21;
			public override bool hasFreeGrid(int num)
			{
				if(!_g21)
				{
					_m21=instance.Type.GetMethod("hasFreeGrid",1);
					_g21=true;
				}
				
				if(_m21!=null && !_b21)
				{
					_b21=true;
					_p1[0]=num;
					bool re=(bool)appdomain.Invoke(_m21,instance,_p1);
					_p1[0]=null;
					_b21=false;
					return re;
					
				}
				else
				{
					return base.hasFreeGrid(num);
				}
			}
			
			IMethod _m22;
			bool _g22;
			bool _b22;
			public override bool hasItemPlace(ItemData data)
			{
				if(!_g22)
				{
					_m22=instance.Type.GetMethod("hasItemPlace",1);
					_g22=true;
				}
				
				if(_m22!=null && !_b22)
				{
					_b22=true;
					_p1[0]=data;
					bool re=(bool)appdomain.Invoke(_m22,instance,_p1);
					_p1[0]=null;
					_b22=false;
					return re;
					
				}
				else
				{
					return base.hasItemPlace(data);
				}
			}
			
			IMethod _m23;
			bool _g23;
			bool _b23;
			public override bool hasItemPlace(int id,int num)
			{
				if(!_g23)
				{
					_m23=instance.Type.GetMethod("hasItemPlace",2);
					_g23=true;
				}
				
				if(_m23!=null && !_b23)
				{
					_b23=true;
					_p2[0]=id;
					_p2[1]=num;
					bool re=(bool)appdomain.Invoke(_m23,instance,_p2);
					_p2[0]=null;
					_p2[1]=null;
					_b23=false;
					return re;
					
				}
				else
				{
					return base.hasItemPlace(id,num);
				}
			}
			
			IMethod _m24;
			bool _g24;
			bool _b24;
			public override bool hasItemPlace(SList<ItemData> list)
			{
				if(!_g24)
				{
					_m24=instance.Type.GetMethod("hasItemPlace",1);
					_g24=true;
				}
				
				if(_m24!=null && !_b24)
				{
					_b24=true;
					_p1[0]=list;
					bool re=(bool)appdomain.Invoke(_m24,instance,_p1);
					_p1[0]=null;
					_b24=false;
					return re;
					
				}
				else
				{
					return base.hasItemPlace(list);
				}
			}
			
			IMethod _m25;
			bool _g25;
			bool _b25;
			public override bool hasItemPlace(DIntData[] dataArr)
			{
				if(!_g25)
				{
					_m25=instance.Type.GetMethod("hasItemPlace",1);
					_g25=true;
				}
				
				if(_m25!=null && !_b25)
				{
					_b25=true;
					_p1[0]=dataArr;
					bool re=(bool)appdomain.Invoke(_m25,instance,_p1);
					_p1[0]=null;
					_b25=false;
					return re;
					
				}
				else
				{
					return base.hasItemPlace(dataArr);
				}
			}
			
			IMethod _m26;
			bool _g26;
			bool _b26;
			public override int getItemNum(int itemID)
			{
				if(!_g26)
				{
					_m26=instance.Type.GetMethod("getItemNum",1);
					_g26=true;
				}
				
				if(_m26!=null && !_b26)
				{
					_b26=true;
					_p1[0]=itemID;
					int re=(int)appdomain.Invoke(_m26,instance,_p1);
					_p1[0]=null;
					_b26=false;
					return re;
					
				}
				else
				{
					return base.getItemNum(itemID);
				}
			}
			
			IMethod _m27;
			bool _g27;
			bool _b27;
			protected override bool toAddItem(ItemData data,int way)
			{
				if(!_g27)
				{
					_m27=instance.Type.GetMethod("toAddItem",2);
					_g27=true;
				}
				
				if(_m27!=null && !_b27)
				{
					_b27=true;
					_p2[0]=data;
					_p2[1]=way;
					bool re=(bool)appdomain.Invoke(_m27,instance,_p2);
					_p2[0]=null;
					_p2[1]=null;
					_b27=false;
					return re;
					
				}
				else
				{
					return base.toAddItem(data,way);
				}
			}
			
			IMethod _m28;
			bool _g28;
			bool _b28;
			protected override bool toAddItem(int id,int num,int way)
			{
				if(!_g28)
				{
					_m28=instance.Type.GetMethod("toAddItem",3);
					_g28=true;
				}
				
				if(_m28!=null && !_b28)
				{
					_b28=true;
					_p3[0]=id;
					_p3[1]=num;
					_p3[2]=way;
					bool re=(bool)appdomain.Invoke(_m28,instance,_p3);
					_p3[0]=null;
					_p3[1]=null;
					_p3[2]=null;
					_b28=false;
					return re;
					
				}
				else
				{
					return base.toAddItem(id,num,way);
				}
			}
			
			IMethod _m29;
			bool _g29;
			bool _b29;
			protected override bool toAddNewItemToIndex(int index,ItemData data,int way)
			{
				if(!_g29)
				{
					_m29=instance.Type.GetMethod("toAddNewItemToIndex",3);
					_g29=true;
				}
				
				if(_m29!=null && !_b29)
				{
					_b29=true;
					_p3[0]=index;
					_p3[1]=data;
					_p3[2]=way;
					bool re=(bool)appdomain.Invoke(_m29,instance,_p3);
					_p3[0]=null;
					_p3[1]=null;
					_p3[2]=null;
					_b29=false;
					return re;
					
				}
				else
				{
					return base.toAddNewItemToIndex(index,data,way);
				}
			}
			
			IMethod _m30;
			bool _g30;
			bool _b30;
			protected override bool toAddItems(SList<ItemData> list,int way)
			{
				if(!_g30)
				{
					_m30=instance.Type.GetMethod("toAddItems",2);
					_g30=true;
				}
				
				if(_m30!=null && !_b30)
				{
					_b30=true;
					_p2[0]=list;
					_p2[1]=way;
					bool re=(bool)appdomain.Invoke(_m30,instance,_p2);
					_p2[0]=null;
					_p2[1]=null;
					_b30=false;
					return re;
					
				}
				else
				{
					return base.toAddItems(list,way);
				}
			}
			
			IMethod _m31;
			bool _g31;
			bool _b31;
			protected override bool toAddItems(DIntData[] list,int num,int way)
			{
				if(!_g31)
				{
					_m31=instance.Type.GetMethod("toAddItems",3);
					_g31=true;
				}
				
				if(_m31!=null && !_b31)
				{
					_b31=true;
					_p3[0]=list;
					_p3[1]=num;
					_p3[2]=way;
					bool re=(bool)appdomain.Invoke(_m31,instance,_p3);
					_p3[0]=null;
					_p3[1]=null;
					_p3[2]=null;
					_b31=false;
					return re;
					
				}
				else
				{
					return base.toAddItems(list,num,way);
				}
			}
			
			IMethod _m32;
			bool _g32;
			bool _b32;
			public override bool containsItem(int id,int num)
			{
				if(!_g32)
				{
					_m32=instance.Type.GetMethod("containsItem",2);
					_g32=true;
				}
				
				if(_m32!=null && !_b32)
				{
					_b32=true;
					_p2[0]=id;
					_p2[1]=num;
					bool re=(bool)appdomain.Invoke(_m32,instance,_p2);
					_p2[0]=null;
					_p2[1]=null;
					_b32=false;
					return re;
					
				}
				else
				{
					return base.containsItem(id,num);
				}
			}
			
			IMethod _m33;
			bool _g33;
			bool _b33;
			public override bool removeItem(int id,int num,int way)
			{
				if(!_g33)
				{
					_m33=instance.Type.GetMethod("removeItem",3);
					_g33=true;
				}
				
				if(_m33!=null && !_b33)
				{
					_b33=true;
					_p3[0]=id;
					_p3[1]=num;
					_p3[2]=way;
					bool re=(bool)appdomain.Invoke(_m33,instance,_p3);
					_p3[0]=null;
					_p3[1]=null;
					_p3[2]=null;
					_b33=false;
					return re;
					
				}
				else
				{
					return base.removeItem(id,num,way);
				}
			}
			
			IMethod _m34;
			bool _g34;
			bool _b34;
			public override bool removeItemByIndex(int index,int way)
			{
				if(!_g34)
				{
					_m34=instance.Type.GetMethod("removeItemByIndex",2);
					_g34=true;
				}
				
				if(_m34!=null && !_b34)
				{
					_b34=true;
					_p2[0]=index;
					_p2[1]=way;
					bool re=(bool)appdomain.Invoke(_m34,instance,_p2);
					_p2[0]=null;
					_p2[1]=null;
					_b34=false;
					return re;
					
				}
				else
				{
					return base.removeItemByIndex(index,way);
				}
			}
			
			IMethod _m35;
			bool _g35;
			bool _b35;
			public override bool removeItemByIndex(int index,int num,int way)
			{
				if(!_g35)
				{
					_m35=instance.Type.GetMethod("removeItemByIndex",3);
					_g35=true;
				}
				
				if(_m35!=null && !_b35)
				{
					_b35=true;
					_p3[0]=index;
					_p3[1]=num;
					_p3[2]=way;
					bool re=(bool)appdomain.Invoke(_m35,instance,_p3);
					_p3[0]=null;
					_p3[1]=null;
					_p3[2]=null;
					_b35=false;
					return re;
					
				}
				else
				{
					return base.removeItemByIndex(index,num,way);
				}
			}
			
			IMethod _m36;
			bool _g36;
			bool _b36;
			public override bool removeItems(DIntData[] items,int num,int way)
			{
				if(!_g36)
				{
					_m36=instance.Type.GetMethod("removeItems",3);
					_g36=true;
				}
				
				if(_m36!=null && !_b36)
				{
					_b36=true;
					_p3[0]=items;
					_p3[1]=num;
					_p3[2]=way;
					bool re=(bool)appdomain.Invoke(_m36,instance,_p3);
					_p3[0]=null;
					_p3[1]=null;
					_p3[2]=null;
					_b36=false;
					return re;
					
				}
				else
				{
					return base.removeItems(items,num,way);
				}
			}
			
			IMethod _m37;
			bool _g37;
			bool _b37;
			public override void cleanUp()
			{
				if(!_g37)
				{
					_m37=instance.Type.GetMethod("cleanUp",0);
					_g37=true;
				}
				
				if(_m37!=null && !_b37)
				{
					_b37=true;
					appdomain.Invoke(_m37,instance,null);
					_b37=false;
					
				}
				else
				{
					base.cleanUp();
				}
			}
			
			IMethod _m38;
			bool _g38;
			bool _b38;
			public override void printBag()
			{
				if(!_g38)
				{
					_m38=instance.Type.GetMethod("printBag",0);
					_g38=true;
				}
				
				if(_m38!=null && !_b38)
				{
					_b38=true;
					appdomain.Invoke(_m38,instance,null);
					_b38=false;
					
				}
				else
				{
					base.printBag();
				}
			}
			
			IMethod _m39;
			bool _g39;
			bool _b39;
			protected override void onItemUse(int id,int num,ItemData itemData,UseItemArgData arg)
			{
				if(!_g39)
				{
					_m39=instance.Type.GetMethod("onItemUse",4);
					_g39=true;
				}
				
				if(_m39!=null && !_b39)
				{
					_b39=true;
					_p4[0]=id;
					_p4[1]=num;
					_p4[2]=itemData;
					_p4[3]=arg;
					appdomain.Invoke(_m39,instance,_p4);
					_p4[0]=null;
					_p4[1]=null;
					_p4[2]=null;
					_p4[3]=null;
					_b39=false;
					
				}
				else
				{
					base.onItemUse(id,num,itemData,arg);
				}
			}
			
			IMethod _m40;
			bool _g40;
			bool _b40;
			protected override bool checkOneItemUseCondition(int[] args,bool needNotice)
			{
				if(!_g40)
				{
					_m40=instance.Type.GetMethod("checkOneItemUseCondition",2);
					_g40=true;
				}
				
				if(_m40!=null && !_b40)
				{
					_b40=true;
					_p2[0]=args;
					_p2[1]=needNotice;
					bool re=(bool)appdomain.Invoke(_m40,instance,_p2);
					_p2[0]=null;
					_p2[1]=null;
					_b40=false;
					return re;
					
				}
				else
				{
					return base.checkOneItemUseCondition(args,needNotice);
				}
			}
			
			IMethod _m41;
			bool _g41;
			bool _b41;
			protected override bool checkOneItemUseConditionForAction(int[] args,int num,UseItemArgData arg,bool needNotice)
			{
				if(!_g41)
				{
					_m41=instance.Type.GetMethod("checkOneItemUseConditionForAction",4);
					_g41=true;
				}
				
				if(_m41!=null && !_b41)
				{
					_b41=true;
					_p4[0]=args;
					_p4[1]=num;
					_p4[2]=arg;
					_p4[3]=needNotice;
					bool re=(bool)appdomain.Invoke(_m41,instance,_p4);
					_p4[0]=null;
					_p4[1]=null;
					_p4[2]=null;
					_p4[3]=null;
					_b41=false;
					return re;
					
				}
				else
				{
					return base.checkOneItemUseConditionForAction(args,num,arg,needNotice);
				}
			}
			
			IMethod _m42;
			bool _g42;
			bool _b42;
			public override bool useItemByID(int id,UseItemArgData arg)
			{
				if(!_g42)
				{
					_m42=instance.Type.GetMethod("useItemByID",2);
					_g42=true;
				}
				
				if(_m42!=null && !_b42)
				{
					_b42=true;
					_p2[0]=id;
					_p2[1]=arg;
					bool re=(bool)appdomain.Invoke(_m42,instance,_p2);
					_p2[0]=null;
					_p2[1]=null;
					_b42=false;
					return re;
					
				}
				else
				{
					return base.useItemByID(id,arg);
				}
			}
			
			IMethod _m43;
			bool _g43;
			bool _b43;
			public override bool useItemByIndex(int index,int num,UseItemArgData arg)
			{
				if(!_g43)
				{
					_m43=instance.Type.GetMethod("useItemByIndex",3);
					_g43=true;
				}
				
				if(_m43!=null && !_b43)
				{
					_b43=true;
					_p3[0]=index;
					_p3[1]=num;
					_p3[2]=arg;
					bool re=(bool)appdomain.Invoke(_m43,instance,_p3);
					_p3[0]=null;
					_p3[1]=null;
					_p3[2]=null;
					_b43=false;
					return re;
					
				}
				else
				{
					return base.useItemByIndex(index,num,arg);
				}
			}
			
			IMethod _m44;
			bool _g44;
			bool _b44;
			public override int getRedPointCount()
			{
				if(!_g44)
				{
					_m44=instance.Type.GetMethod("getRedPointCount",0);
					_g44=true;
				}
				
				if(_m44!=null && !_b44)
				{
					_b44=true;
					int re=(int)appdomain.Invoke(_m44,instance,null);
					_b44=false;
					return re;
					
				}
				else
				{
					return base.getRedPointCount();
				}
			}
			
			IMethod _m45;
			bool _g45;
			bool _b45;
			public override void removeRedPoint(int index)
			{
				if(!_g45)
				{
					_m45=instance.Type.GetMethod("removeRedPoint",1);
					_g45=true;
				}
				
				if(_m45!=null && !_b45)
				{
					_b45=true;
					_p1[0]=index;
					appdomain.Invoke(_m45,instance,_p1);
					_p1[0]=null;
					_b45=false;
					
				}
				else
				{
					base.removeRedPoint(index);
				}
			}
			
			IMethod _m46;
			bool _g46;
			bool _b46;
			public override void useItemResult(int id,int num,bool result)
			{
				if(!_g46)
				{
					_m46=instance.Type.GetMethod("useItemResult",3);
					_g46=true;
				}
				
				if(_m46!=null && !_b46)
				{
					_b46=true;
					_p3[0]=id;
					_p3[1]=num;
					_p3[2]=result;
					appdomain.Invoke(_m46,instance,_p3);
					_p3[0]=null;
					_p3[1]=null;
					_p3[2]=null;
					_b46=false;
					
				}
				else
				{
					base.useItemResult(id,num,result);
				}
			}
			
			IMethod _m47;
			bool _g47;
			bool _b47;
			public override void onAddReward(int way,int rewarID,int num,SList<ItemData> randomItemDatas)
			{
				if(!_g47)
				{
					_m47=instance.Type.GetMethod("onAddReward",4);
					_g47=true;
				}
				
				if(_m47!=null && !_b47)
				{
					_b47=true;
					_p4[0]=way;
					_p4[1]=rewarID;
					_p4[2]=num;
					_p4[3]=randomItemDatas;
					appdomain.Invoke(_m47,instance,_p4);
					_p4[0]=null;
					_p4[1]=null;
					_p4[2]=null;
					_p4[3]=null;
					_b47=false;
					
				}
				else
				{
					base.onAddReward(way,rewarID,num,randomItemDatas);
				}
			}
			
			IMethod _m48;
			bool _g48;
			bool _b48;
			protected override bool checkExchangeConditionEx(int id,int num)
			{
				if(!_g48)
				{
					_m48=instance.Type.GetMethod("checkExchangeConditionEx",2);
					_g48=true;
				}
				
				if(_m48!=null && !_b48)
				{
					_b48=true;
					_p2[0]=id;
					_p2[1]=num;
					bool re=(bool)appdomain.Invoke(_m48,instance,_p2);
					_p2[0]=null;
					_p2[1]=null;
					_b48=false;
					return re;
					
				}
				else
				{
					return base.checkExchangeConditionEx(id,num);
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
