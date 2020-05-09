namespace Shine
{
	export class GamePoolControl
	{			
		/** 物品数据吃 */
		private _itemDataPool:Array<ObjectPool<ItemData>>;
		/** 任务目标数据池 */
		private _taskDataPool:Array<ObjectPool<TaskData>>;
		
		public init():void
		{
			this._itemDataPool=new Array< ObjectPool<ItemData>>(ItemType.size);

			for(var i=0;i<this._itemDataPool.length;++i)
			{
				this._itemDataPool[i]=this.createItemDataPool(i);
			}

			
			 var typeConfig:TaskTypeConfig;

			this._taskDataPool=new Array< ObjectPool<TaskData>>(QuestType.size);

			this._taskDataPool[0]=this.createTaskDataPool(0);

			for(var i:number=0;i<this._taskDataPool.length;++i)
			{
				if((typeConfig=TaskTypeConfig.get(i))!=null && typeConfig.needCustomTask)
				{
					this._taskDataPool[i]=this.createTaskDataPool(i);
				}
			}
		}

		private createItemDataPool(type:number):ObjectPool<ItemData> 
		{
			var re:ObjectPool<ItemData>=new ObjectPool<ItemData>(Func.create0(()=>
			{
				var data:ItemData=GameC.factory.createItemData();
				data.initIdentityByType(type);
				return data;
			}));

			return re;
		}

		private createTaskDataPool(type:number):ObjectPool<TaskData> 
		{
			var re:ObjectPool<TaskData>=new ObjectPool<TaskData>(Func.create0(()=>
			{
				return GameC.logic.createTaskData(type);
			})  );

			return re;
		}
		
		/** 创建物品数据 */
		public createItemData(type:number):ItemData
		{
			return this._itemDataPool[type].getOne();
		}

		/** 回收物品数据 */
		public releaseItemData(data:ItemData):void
		{
			this._itemDataPool[data.identity.type].back(data);
		}

		/** 创建任务数据 */
		public createTaskData(type:number):TaskData
		{
			if(TaskTypeConfig.get(type).needCustomTask)
			{
				return this._taskDataPool[type].getOne();
			}
			else
			{
				return this._taskDataPool[0].getOne();
			}
		}

		/** 回收任务目标数据 */
		public releaseTaskData(type:number,data:TaskData):void
		{
			if(TaskTypeConfig.get(type).needCustomTask)
			{
				this._taskDataPool[type].back(data);
			}
			else
			{
				this._taskDataPool[0].back(data);
			}
		}
    }
}