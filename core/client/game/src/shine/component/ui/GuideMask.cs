using UnityEngine;
using UnityEngine.UI;

namespace ShineEngine
{

	public class GuideMask:MaskableGraphic,ICanvasRaycastFilter
	{
		/** 掏空区域 */
		public RectTransform hollow;

		/** 掏空区域中心点 */
		public Vector2 center=Vector2.zero;

		/** 掏空区域位置 */
		public Vector2 size=new Vector2(100,100);

		public void DoUpdate()
		{
			// 当引导箭头位置或者大小改变后更新，注意：未处理拉伸模式
			if(hollow!=null && (center!=hollow.anchoredPosition || size!=hollow.sizeDelta))
			{
				center=hollow.anchoredPosition;
				size=hollow.sizeDelta;
				SetAllDirty();
			}
		}

		public bool IsRaycastLocationValid(Vector2 sp,Camera eventCamera)
		{
			// 点击在箭头框内部则无效，否则生效
			return !RectTransformUtility.RectangleContainsScreenPoint(hollow,sp,eventCamera);
		}

		protected override void OnPopulateMesh(VertexHelper vh)
		{
			Vector4 outer=new Vector4(-rectTransform.pivot.x * rectTransform.rect.width,-rectTransform.pivot.y * rectTransform.rect.height,(1 - rectTransform.pivot.x) * rectTransform.rect.width,(1 - rectTransform.pivot.y) * rectTransform.rect.height);

			Vector4 inner=new Vector4(center.x - size.x / 2,center.y - size.y / 2,center.x + size.x * 0.5f,center.y + size.y * 0.5f);

			vh.Clear();

			var vert=UIVertex.simpleVert;

			// left
			vert.position=new Vector2(outer.x,outer.y);
			vert.color=color;
			vh.AddVert(vert);

			vert.position=new Vector2(outer.x,outer.w);
			vert.color=color;
			vh.AddVert(vert);

			vert.position=new Vector2(inner.x,outer.w);
			vert.color=color;
			vh.AddVert(vert);

			vert.position=new Vector2(inner.x,outer.y);
			vert.color=color;
			vh.AddVert(vert);

			// top
			vert.position=new Vector2(inner.x,inner.w);
			vert.color=color;
			vh.AddVert(vert);

			vert.position=new Vector2(inner.x,outer.w);
			vert.color=color;
			vh.AddVert(vert);

			vert.position=new Vector2(inner.z,outer.w);
			vert.color=color;
			vh.AddVert(vert);

			vert.position=new Vector2(inner.z,inner.w);
			vert.color=color;
			vh.AddVert(vert);

			// right
			vert.position=new Vector2(inner.z,outer.y);
			vert.color=color;
			vh.AddVert(vert);

			vert.position=new Vector2(inner.z,outer.w);
			vert.color=color;
			vh.AddVert(vert);

			vert.position=new Vector2(outer.z,outer.w);
			vert.color=color;
			vh.AddVert(vert);

			vert.position=new Vector2(outer.z,outer.y);
			vert.color=color;
			vh.AddVert(vert);

			// bottom
			vert.position=new Vector2(inner.x,outer.y);
			vert.color=color;
			vh.AddVert(vert);

			vert.position=new Vector2(inner.x,inner.y);
			vert.color=color;
			vh.AddVert(vert);

			vert.position=new Vector2(inner.z,inner.y);
			vert.color=color;
			vh.AddVert(vert);

			vert.position=new Vector2(inner.z,outer.y);
			vert.color=color;
			vh.AddVert(vert);

			vh.AddTriangle(0,1,2);
			vh.AddTriangle(2,3,0);
			vh.AddTriangle(4,5,6);
			vh.AddTriangle(6,7,4);
			vh.AddTriangle(8,9,10);
			vh.AddTriangle(10,11,8);
			vh.AddTriangle(12,13,14);
			vh.AddTriangle(14,15,12);
		}

		private void Update()
		{
			DoUpdate();
		}
	}
}