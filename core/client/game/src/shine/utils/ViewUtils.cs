using System;
using UnityEngine;
using UnityEngine.UI;

namespace ShineEngine
{
	/// <summary>
	/// 显示方法
	/// </summary>
	public static class ViewUtils
	{
		/// <summary>
		/// 获取根Canvas的Transform
		/// </summary>
		/// <param name="transform">当前transform</param>
		/// <returns>根Canvas的Transform</returns>
		public static Transform GetRootCanvasTransform(Transform transform)
		{
			if(transform == null)
				return null;

            CanvasScaler canvas = transform.GetComponent<CanvasScaler>();
			if(canvas != null)
				return transform;

			return GetRootCanvasTransform(transform.parent);
		}

		/// <summary>
		/// 检测UIObject是否有对应类型的父并将找到的父对象返回（递归向上找）
		/// </summary>
		/// <param name="obj">UIObject控件</param>
		/// <param name="types">要获取父的类型</param>
		public static UIObject getRootByType(UIObject obj,params int[] types)
		{
			UIObject tmpObj=obj;

			while(tmpObj.parent!=null)
			{
				tmpObj=tmpObj.parent;

				if(ObjectUtils.arrayIndexOf(types,tmpObj.type)!=-1)
				{
					return tmpObj;
				}
			}

			return null;
		}

		/// <summary>
		/// 获取父格子对象
		/// </summary>
		/// <param name="obj"></param>
		/// <returns></returns>
		public static UIObject getRootGrid(UIObject obj)
		{
			while(obj!=null)
			{
				if(obj.isGrid)
					return obj;

				obj=obj.parent;
			}

			return null;
		}

		/// <summary>
		/// 对显示对象的子孙做批量操作（递归查找所有子孙）
		/// </summary>
		/// <param name="tf">需要操作的显示对象</param>
		/// <param name="action">执行函数</param>
		/// <param name="hasSelf">是否包含自己</param>
		public static void doActionToChildren(Transform tf,Action<Transform> action,bool hasSelf=false)
		{
			if(action==null)
				return;

			if(hasSelf)
				action(tf);

			for(int i=0,len=tf.childCount;i<len;i++)
			{
				doActionToChildren(tf.GetChild(i),action,true);
			}
		}
	}
}