using UnityEngine;

namespace ShineEngine
{
	/** 层级类型 */
	public class LayerType
	{
		public static readonly int Default = LayerMask.NameToLayer("Default");
		public static readonly int UI = LayerMask.NameToLayer("UI");
		public static readonly int Water = LayerMask.NameToLayer("Water");

		public static readonly int Terrain = LayerMask.NameToLayer("Terrain");
		public static readonly int Navmesh = LayerMask.NameToLayer("Navmesh");
		public static readonly int Collider = LayerMask.NameToLayer("Collider");
		public static readonly int Unit = LayerMask.NameToLayer("Unit");
		public static readonly int Hero = LayerMask.NameToLayer("Hero");
		public static readonly int Effect = LayerMask.NameToLayer("Effect");
        public static readonly int Building = LayerMask.NameToLayer("Building");

        /** 地形mask(地形+行走面) */
        public static readonly int TerrainMask = getLayerMask(Terrain, Navmesh);
        /** 单位mask */
        public static readonly int UnitMask = getLayerMask(Unit);
        /** 移动碰撞组 */
        public static readonly int MoveMask = getLayerMask(Terrain, Navmesh,Collider);

		/** 某mask是否包含layer */
		public static bool containLayer(int mask,int layer)
		{
			return ((1 << layer) & mask) != 0;
		}

		public static int layerToMask(int layer)
		{
			return 1 << layer;
		}

		/** 获取一组layer的mask */
		public static int getLayerMask(params int[] layers)
		{
			int mask = 0;

			for (int i = 0; i < layers.Length; ++i)
			{
				mask |= (1 << layers[i]);
			}

			return mask;
		}
	}
}