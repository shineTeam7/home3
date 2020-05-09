using System;

namespace ShineEngine
{
	/// <summary>
	/// 消息类型
	/// </summary>
	public class EventType
	{
		/** 重置位置(ViewControl) */
		public const int Resize=1;

		/** UI初始化(UILogicBase) */
		public const int UIInit=21;

		/** UI呈现(UILogicBase) */
		public const int UIEnter=22;

		/** UI退出(UILogicBase) */
		public const int UIExit=23;

		/** UI显示完毕(UILogicBase) */
		public const int UIShowOver=25;

		/** UI隐藏完毕(UILogicBase) */
		public const int UIHideOver=26;
	}
}