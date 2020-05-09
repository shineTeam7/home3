using System;

namespace ShineEngine
{
	public interface IGridContainer
	{
		/// <summary>
		/// 增加格子容器按钮点击回调
		/// </summary>
		/// <param name="obj"></param>
		/// <param name="gridIndex"></param>
		void setClickObject(UIObject obj,int gridIndex);

		/// <summary>
		/// 移除格子容器按钮点击回调
		/// </summary>
		/// <param name="obj"></param>
		void removeClickObject(UIObject obj);

		/// <summary>
		/// 清除所有格子容器按钮点击回调
		/// </summary>
		void clearClickObjects();

		/// <summary>
		/// 根据格子对象获取格子索引
		/// </summary>
		int getGridIndex(UIObject grid);
	}
}