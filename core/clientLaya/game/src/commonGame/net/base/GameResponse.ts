namespace Shine
{
	export abstract class GameResponse extends BaseResponse
	{
		public me:Player; 
		public constructor()
		{
			super();
			this.me = GameC.player;
		}
	}
}