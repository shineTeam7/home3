package com.home.shine.constlist;

/** UI元素类型 */
public class UIElementType
{
	/** 空 */
	public static final int None=0;
	/** 基础对象 */
	public static final int Object=1;
	/** 容器(节点) */
	public static final int Container=2;
	/** 原件 */
	public static final int Element=3;
	/** 模型 */
	public static final int Model=4;
	/** 按钮 */
	public static final int Button=5;
	/** 图文混排文本框 */
	public static final int TextField=6;
	/** 图片 */
	public static final int Image=7;
	/** 裁切图片 */
	public static final int RawImage=8;
	/** 文本框 */
	public static final int Text=9;
	/** 血条 */
	public static final int BloodBar=10;
	/** 滚动容器 */
	public static final int SScrollView=11;
	/** 多帧图片容器 */
	public static final int ImageFrameContainer=12;
	/** 输入文本框 */
	public static final int InputField=13;
	/** 选择按钮 */
	public static final int Toggle=14;
	/** 滑块条 */
	public static final int Slider=15;
	/** 骨骼动画 */
	public static final int SkeletonGraphic=16;
	/** 新手引导遮罩 */
	public static final int GuideMask=17;
	/** 图片加载器 */
	public static final int ImageLoader=18;
	/** 骨骼动画加载器 */
	public static final int SkeletonGraphicLoader=19;
	/** 翻页容器 */
	public static final int SPageView=20;
	/** 滚动条 */
	public static final int ScrollBar=21;
	/** 下拉列表 */
	public static final int Dropdown=22;
	///** 自定义滚动容器 */
	//public static final int SCustomScrollView=23;
	/** 国际化文本 */
	public static final int I18NText=24;
	/** RawImage图片加载器 */
	public static final int RawImageLoader=25;
	/** 伪3D滚动容器 */
	public static final int SScrollViewFake3D = 26;
	
	/** 是否有泛型 */
	public static boolean hasGenericity(int type)
	{
		switch(type)
		{
			case SScrollView:
			case SPageView:
			case SScrollViewFake3D:
				return true;
		}
		
		return false;
	}
}
