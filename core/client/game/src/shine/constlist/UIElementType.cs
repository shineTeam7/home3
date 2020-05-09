using System;

namespace ShineEngine
{
	/// <summary>
	/// UI元素类型
	/// </summary>
	public class UIElementType
	{
		/** 空 */
		public const int None=0;

		/** 基础对象 */
		public const int Object=1;

		/** 容器(节点) */
		public const int Container=2;

		/** 原件 */
		public const int Element=3;

		/** 模型 */
		public const int Model=4;

		/** 按钮 */
		public const int Button=5;

		/** 图文混排文本框 */
		public const int TextField=6;

		/** 图片 */
		public const int Image=7;

		/** 裁切图片 */
		public const int RawImage=8;

		/** 文本框 */
		public const int Text=9;

		/** 血条 */
		public const int BloodBar=10;

		/** 滚动容器 */
		public const int SScrollView=11;

		/** 多帧图片容器 */
		public const int ImageFrameContainer=12;

		/** 输入文本框 */
		public const int InputField=13;

		/** 选择按钮 */
		public const int Toggle=14;

		/** 滑块条 */
		public const int Slider=15;

		/** 骨骼动画 */
		public const int SkeletonGraphic=16;

		/** 新手引导遮罩 */
		public const int GuideMask=17;

		/** 图片加载器 */
		public const int ImageLoader=18;

		/** 骨骼动画加载器 */
		public const int SkeletonGraphicLoader=19;

		/** 翻页容器 */
		public const int SPageView=20;

		/** 滚动条 */
		public const int ScrollBar=21;

		/** 下拉列表 */
		public const int Dropdown=22;
		
		/** 自定义滚动容器 */
		public const int SCustomScrollView=23;

		/** 国际化文本 */
		public const int I18NText=24;
		
		/** RawImage图片加载器 */
		public const int RawImageLoader=25;
	}
}