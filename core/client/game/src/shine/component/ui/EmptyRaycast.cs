using UnityEngine.UI;

namespace ShineEngine
{
	/** 空渲染可点击Component */
	public class EmptyRaycast : MaskableGraphic
	{
		protected EmptyRaycast()
		{
			useLegacyMeshGeneration = false;
		}

		protected override void OnPopulateMesh(VertexHelper toFill)
		{
			toFill.Clear();
		}
	}

}