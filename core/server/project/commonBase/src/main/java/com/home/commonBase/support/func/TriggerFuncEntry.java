package com.home.commonBase.support.func;

import com.home.commonBase.trigger.TriggerArg;
import com.home.commonBase.trigger.TriggerExecutor;
import com.home.shine.constlist.STriggerObjectType;
import com.home.shine.data.trigger.TriggerFuncData;

public class TriggerFuncEntry
{
	private int _type;
	public VoidTriggerFunc voidFunc;
	public BooleanTriggerFunc boolFunc;
	public FloatTriggerFunc floatFunc;
	public IntTriggerFunc intFunc;
	public LongTriggerFunc longFunc;
	public StringTriggerFunc stringFunc;
	public ObjectTriggerFunc objectFunc;
	
	public TriggerFuncEntry()
	{
	
	}
	
	public static TriggerFuncEntry createVoid(VoidTriggerFunc func)
	{
		TriggerFuncEntry re=new TriggerFuncEntry();
		re.voidFunc=func;
		re._type=STriggerObjectType.Void;
		return re;
	}
	
	public static TriggerFuncEntry createBoolean(BooleanTriggerFunc func)
	{
		TriggerFuncEntry re=new TriggerFuncEntry();
		re.boolFunc=func;
		re._type=STriggerObjectType.Boolean;
		return re;
	}
	
	public static TriggerFuncEntry createInt(IntTriggerFunc func)
	{
		TriggerFuncEntry re=new TriggerFuncEntry();
		re.intFunc=func;
		re._type=STriggerObjectType.Int;
		return re;
	}
	
	public static TriggerFuncEntry createLong(LongTriggerFunc func)
	{
		TriggerFuncEntry re=new TriggerFuncEntry();
		re.longFunc=func;
		re._type=STriggerObjectType.Long;
		return re;
	}
	
	public static TriggerFuncEntry createFloat(FloatTriggerFunc func)
	{
		TriggerFuncEntry re=new TriggerFuncEntry();
		re.floatFunc=func;
		re._type=STriggerObjectType.Float;
		return re;
	}
	
	public static TriggerFuncEntry createString(StringTriggerFunc func)
	{
		TriggerFuncEntry re=new TriggerFuncEntry();
		re.stringFunc=func;
		re._type=STriggerObjectType.String;
		return re;
	}
	
	public static TriggerFuncEntry createObject(ObjectTriggerFunc func)
	{
		TriggerFuncEntry re=new TriggerFuncEntry();
		re.objectFunc=func;
		re._type=STriggerObjectType.Object;
		return re;
	}
	
	public boolean doEver(TriggerExecutor e,TriggerFuncData func,TriggerArg arg)
	{
		switch(_type)
		{
			case STriggerObjectType.Void:
			{
				voidFunc.apply(e,func,arg);
				return true;
			}
			case STriggerObjectType.Boolean:
			{
				boolFunc.apply(e,func,arg);
				return true;
			}
			case STriggerObjectType.Int:
			{
				intFunc.apply(e,func,arg);
				return true;
			}
			case STriggerObjectType.Long:
			{
				longFunc.apply(e,func,arg);
				return true;
			}
			case STriggerObjectType.Float:
			{
				floatFunc.apply(e,func,arg);
				return true;
			}
			case STriggerObjectType.String:
			{
				stringFunc.apply(e,func,arg);
				return true;
			}
			case STriggerObjectType.Object:
			{
				objectFunc.apply(e,func,arg);
				return true;
			}
		}
		
		return false;
	}
}
