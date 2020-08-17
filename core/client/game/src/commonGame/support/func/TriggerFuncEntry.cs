using System;
using ShineEngine;

/// <summary>
///
/// </summary>
public class TriggerFuncEntry
{
	private int _type;

	public Action<TriggerExecutor,TriggerFuncData,TriggerArg> voidFunc;
	public Func<TriggerExecutor,TriggerFuncData,TriggerArg,bool> boolFunc;
	public Func<TriggerExecutor,TriggerFuncData,TriggerArg,float> floatFunc;
	public Func<TriggerExecutor,TriggerFuncData,TriggerArg,int> intFunc;
	public Func<TriggerExecutor,TriggerFuncData,TriggerArg,long> longFunc;
	public Func<TriggerExecutor,TriggerFuncData,TriggerArg,string> stringFunc;
	public Func<TriggerExecutor,TriggerFuncData,TriggerArg,object> objectFunc;

	public TriggerFuncEntry()
	{

	}

	public static TriggerFuncEntry createVoid(Action<TriggerExecutor,TriggerFuncData,TriggerArg> func)
	{
		TriggerFuncEntry re=new TriggerFuncEntry();
		re.voidFunc=func;
		re._type=STriggerObjectType.Void;
		return re;
	}

	public static TriggerFuncEntry createBoolean(Func<TriggerExecutor,TriggerFuncData,TriggerArg,bool> func)
	{
		TriggerFuncEntry re=new TriggerFuncEntry();
		re.boolFunc=func;
		re._type=STriggerObjectType.Boolean;
		return re;
	}

	public static TriggerFuncEntry createInt(Func<TriggerExecutor,TriggerFuncData,TriggerArg,int> func)
	{
		TriggerFuncEntry re=new TriggerFuncEntry();
		re.intFunc=func;
		re._type=STriggerObjectType.Int;
		return re;
	}

	public static TriggerFuncEntry createLong(Func<TriggerExecutor,TriggerFuncData,TriggerArg,long> func)
	{
		TriggerFuncEntry re=new TriggerFuncEntry();
		re.longFunc=func;
		re._type=STriggerObjectType.Long;
		return re;
	}

	public static TriggerFuncEntry createFloat(Func<TriggerExecutor,TriggerFuncData,TriggerArg,float> func)
	{
		TriggerFuncEntry re=new TriggerFuncEntry();
		re.floatFunc=func;
		re._type=STriggerObjectType.Float;
		return re;
	}

	public static TriggerFuncEntry createString(Func<TriggerExecutor,TriggerFuncData,TriggerArg,string> func)
	{
		TriggerFuncEntry re=new TriggerFuncEntry();
		re.stringFunc=func;
		re._type=STriggerObjectType.String;
		return re;
	}

	public static TriggerFuncEntry createObject(Func<TriggerExecutor,TriggerFuncData,TriggerArg,object> func)
	{
		TriggerFuncEntry re=new TriggerFuncEntry();
		re.objectFunc=func;
		re._type=STriggerObjectType.Object;
		return re;
	}

	public bool doEver(TriggerExecutor e,TriggerFuncData func,TriggerArg arg)
	{
		switch(_type)
		{
			case STriggerObjectType.Void:
			{
				voidFunc(e,func,arg);
				return true;
			}
			case STriggerObjectType.Boolean:
			{
				boolFunc(e,func,arg);
				return true;
			}
			case STriggerObjectType.Int:
			{
				intFunc(e,func,arg);
				return true;
			}
			case STriggerObjectType.Long:
			{
				longFunc(e,func,arg);
				return true;
			}
			case STriggerObjectType.Float:
			{
				floatFunc(e,func,arg);
				return true;
			}
			case STriggerObjectType.String:
			{
				stringFunc(e,func,arg);
				return true;
			}
			case STriggerObjectType.Object:
			{
				objectFunc(e,func,arg);
				return true;
			}
		}

		return false;
	}
}