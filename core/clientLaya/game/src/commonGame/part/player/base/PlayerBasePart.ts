namespace Shine
{
	export abstract class PlayerBasePart extends BasePart
	{
		/** 角色自身 */
		public me:Player;
	
		public constructor()
		{
			super();
		}

		/** 设置主 */
		public setMe(player:Player ):void
		{
			this.me=player;
		}
	
		/** 功能开启 */
		public abstract onFunctionOpen(id:number):void;
	
		/** 功能关闭 */
		public abstract onFunctionClose(id:number):void;

		/** 登录前(beforeLogin在beforeEnter前) */
		public beforeLogin():void
		{
		
		}
	
		/** 活动开启 */
		public onActivityOpen(id:number,atTime:boolean):void
		{
	
		}
	
		/** 活动关闭 */
		public onActivityClose(id:number,atTime:boolean):void
		{
	
		}
	
		/** 活动重置 */
		public onActivityReset(id:number,atTime:boolean):void
		{
	
		}
	
		/** 升级 */
		public onLevelUp(oldLevel:number):void
		{
	
		}
	}
}