using ShineEngine;

/// <summary>
/// (generated by shine)
/// </summary>
public class SingleBagPart:BagPart
{
	/** 数据 */
	private SingleBagPartData _d;
	
	/** 主背包 */
	private BaseItemContainerTool _mainBag;
	
	public override void setData(BaseData data)
	{
		base.setData(data);
		
		_d=(SingleBagPartData)data;
	}
	
	/// <summary>
	/// 获取数据
	/// </summary>
	public SingleBagPartData getPartData()
	{
		return _d;
	}
	
	/// <summary>
	/// 构造函数(只在new后调用一次,再次从池中取出不会调用)
	/// </summary>
	public override void construct()
	{
		base.construct();

		if(CommonSetting.useDicBag)
		{
			_mainBag=new PlayerItemDicContainerTool(FunctionType.MainBag);
		}
		else
		{
			_mainBag=new PlayerItemContainerTool(FunctionType.MainBag);
		}

		_mainBag.setIsBag(true);

		me.func.registFuncTool((IPlayerFuncTool)_mainBag);
	}
	
	/// <summary>
	/// 构造数据前
	/// </summary>
	protected override void beforeMakeData()
	{
		base.beforeMakeData();
		
	}
	
	/// <summary>
	/// 初始化(创建后刚调用,与dispose成对)
	/// </summary>
	public override void init()
	{
		base.init();
		
	}
	
	/// <summary>
	/// 析构(回池前调用,与init成对)
	/// </summary>
	public override void dispose()
	{
		base.dispose();
		
	}
	
	/// <summary>
	/// 从库中读完数据后(做数据的补充解析)(onNewCreate后也会调用一次)(主线程)
	/// </summary>
	public override void afterReadData()
	{
		base.afterReadData();
		
	}
	
	/// <summary>
	/// 功能开启(id:功能ID)
	/// </summary>
	public override void onFunctionOpen(int id)
	{
		base.onFunctionOpen(id);
		
	}
	
	/// <summary>
	/// 功能关闭(id:功能ID)
	/// </summary>
	public override void onFunctionClose(int id)
	{
		base.onFunctionClose(id);
		
	}
	
	public override void onNewCreate()
	{
		base.onNewCreate();

		//有格背包
		if(!CommonSetting.useDicBag)
		{
			//初始设置上限
			((ItemContainerData)_mainBag.getData()).gridNum=Global.mainBagGridNum;
		}
	}
	
	/// <summary>
	/// 获取主容器
	/// </summary>
	public BaseItemContainerTool getContainer()
	{
		return _mainBag;
	}
	
	public override ItemData getItem(int index)
	{
		return _mainBag.getItem(index);
	}
	
	public override bool hasFreeGrid(int num)
	{
		return _mainBag.hasFreeGrid(num);
	}
	
	public override bool hasItemPlace(ItemData data)
	{
		return _mainBag.hasItemPlace(data);
	}
	
	public override bool hasItemPlace(int id,int num)
	{
		return _mainBag.hasItemPlace(id,num);
	}
	
	public override bool hasItemPlace(SList<ItemData> list)
	{
		return _mainBag.hasItemPlace(list);
	}
	
	public override bool hasItemPlace(DIntData[] dataArr)
	{
		return _mainBag.hasItemPlace(dataArr);
	}
	
	public override int getItemNum(int itemID)
	{
		return _mainBag.getItemNum(itemID);
	}
	
	protected override bool toAddItem(ItemData data,int way)
	{
		return _mainBag.addItem(data,way);
	}
	
	protected override bool toAddItem(int id,int num,int way)
	{
		return _mainBag.addItem(id,num,way);
	}
	
	protected override bool toAddItems(SList<ItemData> list,int way)
	{
		return _mainBag.addItems(list,way);
	}
	
	protected override bool toAddItems(DIntData[] list,int num,int way)
	{
		return _mainBag.addItems(list,num,way);
	}
	
	protected override bool toAddNewItemToIndex(int index,ItemData data,int way)
	{
		return _mainBag.addNewItemToIndex(index,data,way);
	}
	
	public override bool containsItem(int id,int num)
	{
		return _mainBag.containsItem(id,num);
	}
	
	public override bool removeItem(int id,int num,int way)
	{
		return _mainBag.removeItem(id,num,way);
	}
	
	public override bool removeItemByIndex(int index,int way)
	{
		return _mainBag.removeItemByIndex(index,way);
	}
	
	public override bool removeItemByIndex(int index,int num,int way)
	{
		return _mainBag.removeItemByIndex(index,num,way);
	}
	
	public override bool removeItems(DIntData[] items,int num,int way)
	{
		return _mainBag.removeItems(items,num,way);
	}
	
	public override void cleanUp()
	{
		if(!CommonSetting.useDicBag)
		{
			((PlayerItemContainerTool)_mainBag).cleanUp();
		}
	}
	
	public override void printBag()
	{
		_mainBag.printBag();
	}
	
	public override bool useItemByID(int id,UseItemArgData arg)
	{
		return _mainBag.useItemByID(id,arg);
	}
	
	public override bool useItemByIndex(int index,int num,UseItemArgData arg)
	{
		return _mainBag.useItemByIndex(index,num,arg);
	}
	
	public override int getRedPointCount()
	{
		return _mainBag.getRedPointCount();
	}
	
	public override void removeRedPoint(int index)
	{
		_mainBag.removeRedPoint(index);
	}
	
	protected override BaseData createPartData()
	{
		return new SingleBagPartData();
	}
	
}
