using System.Collections.Generic;
using UnityEngine;

namespace ShineEngine
{
	[System.Serializable]
	public struct ElementInfo
	{
		/** 属性名 */
		public string name;
		// /** 数组索引(默认-1) */
		// public int index;
		/** 元素 */
		public GameObject obj;
	}

	/** UI元素脚本 */
	public class UIElementComponent:MonoBehaviour
	{
		/** 导出组 */
		public ElementInfo[] exports;

	}
}