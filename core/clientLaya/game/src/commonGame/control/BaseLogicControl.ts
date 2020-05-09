namespace Shine
{
    /** 基础逻辑控制 */
    export class BaseLogicControl
    {
        constructor()
        {
            
        }

        public BaseLogicControl()
        {

        }

        /** 初始化 */
        public init():void
        {
            
        }


        /** 创建使用物品参数数据 */
        public createUseItemArgData(type:number):UseItemArgData
        {
            return GameC.logic.createUseItemArgData(type);
        }

        /** 创建任务数据 */
        public createTaskData(type:number):TaskData
        {
            return GameC.factory.createTaskData();
        }

        /** 初始化物品 */
        public initItem(data:ItemData,id:number):void
        {

        }

        /** 创建物品数据 */
        public createItemDInt(data:DIntData):ItemData
        {
            return this.createItem(data.key,data.value);
        }

        /** 创建物品数据(如id<=0则为空) */
        public createItem( id:number,num:number):ItemData
        {
            if(id<=0)
                return null;

            var config:ItemConfig=ItemConfig.get(id);

            var data:ItemData=GameC.pool.createItemData(config.type);
            data.initIdentityByType(config.type);
            data.id=id;
            data.num=num;
            data.config=config;
            //失效时间
            data.disableTime=config.enableTimeT.getNextTime();

            //额外初始化
            this.initItem(data,id);

            return data;
        }

        /** 通过创建物品配置创建物品(可能为空) */
        public createItemByCreateID( createID:number):ItemData
        {
            if(createID<=0)
                return null;

            var createConfig:CreateItemConfig=CreateItemConfig.get(createID);
            var data:ItemData=this.createItem(createConfig.itemID,1);
            data.isBind=createConfig.isBind;

            this.makeItemDataByCreateConfig(data,createConfig);

            return data;
        }

        /** 进一步构造物品 */
        protected  makeItemDataByCreateConfig( data:ItemData, config:CreateItemConfig):void
        {

        }

        public createItemIdentityByType(type:number):ItemIdentityData
        {
            switch(type)
            {
                case ItemType.Equip:
                {
                    return GameC.factory.createItemEquipData();
                }
                case ItemType.Stuff:
                case ItemType.Tool:
                {
                    return GameC.factory.createItemIdentityData();
                }
                default:
                {
                    return GameC.factory.createItemIdentityData();
                }
            }
        }

        /** 随机一个物品(可能为空) */
        public randomItem(randomItemConfigID:number,entity:ILogicEntity):ItemData
        {
            return RandomItemConfig.get(randomItemConfigID).randomOne(entity);
        }

        /** 随机一组物品 */
        public  randomItemList(list:SList<ItemData>, randItemListConfigID:number, entity:ILogicEntity):void
        {
            list.clear();
           // RandomItemListConfig.get(randItemListConfigID).randomList(list,entity);
        }

    }
}