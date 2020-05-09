using System;
using System.Text;
using ShineEngine;
using UnityEditor;
using UnityEngine;
using UnityEngine.AI;
using UnityEngine.UI;
using Object = UnityEngine.Object;

namespace ShineEditor
{
	/// <summary>
	/// 地形导出
	/// </summary>
	public class NavMeshExport
	{
		public const int RC_SPAN_HEIGHT_BITS = 16;
		/// Defines the maximum value for rcSpan::smin and rcSpan::smax.
		public const int RC_SPAN_MAX_HEIGHT = (1<<RC_SPAN_HEIGHT_BITS)-1;
		
		public const float ChunkSize = 512.0f;

		private static bool _needObjFile = true;

		private static bool _newBound = false;
		
		/** 将当前场景的mesh写出到路径 */
		public static void writeMesh(int currentMapID)
		{
			_needObjFile=CommonSetting.serverMapRecastNeedObjFile;

			MeshFilter[] mfs = Object.FindObjectsOfType<MeshFilter>();

			Terrain[] terrains = Object.FindObjectsOfType<Terrain>();

			if (terrains.Length > 1)
			{
				Ctrl.errorLog("地形数目非法");
				return;
			}

			NavMeshConfig config=new NavMeshConfig();
			SceneRecastData data=new SceneRecastData();
			
			Terrain terrain = terrains.Length > 0 ? terrains[0] : null;

			Bounds b=new Bounds();
			_newBound = true;
			
			if (terrain != null)
			{
				TerrainData terrainData = terrain.terrainData;
				Bounds terrainBounds = terrainData.bounds;
				terrainBounds.center = terrain.transform.TransformPoint(terrainBounds.center);
				
//				Bounds tBounds=new Bounds(new Vector3(terrainBounds.center.x, 0.0f, terrainBounds.center.z), new Vector3(ChunkSize, 1048576.0f, ChunkSize));
//
//				int sx = (int)Mathf.Floor(tBounds.center.x / ChunkSize);
//				int sz = (int) Mathf.Floor(tBounds.center.z / ChunkSize);
//				
//				var tilesPerChunks = (int)(ChunkSize / (config.tileSize * config.cellSize));
//				
//				data.sizeX = tilesPerChunks;
//				data.sizeZ = tilesPerChunks;
//				data.originX = sx * tilesPerChunks;
//				data.originZ = sz * tilesPerChunks;
//				data.tileSize = config.tileSize * config.cellSize;

				if (_newBound)
				{
					_newBound = false;
					b = terrainBounds;
				}
				else
				{
					b.Encapsulate(terrainBounds);
				}
				
				SceneRecastTerrainData tData=data.terrain=new SceneRecastTerrainData();
				tData.origin = terrainBounds.min;
				int rx=tData.resolutionX = terrainData.heightmapWidth;
				int rz=tData.resolutionZ = terrainData.heightmapHeight;
				Vector3 mapScale=terrainData.heightmapScale;
				
				tData.unit = mapScale;
				tData.unit.y /= 32768.0f;

				int startSize = data.triangles.size();
				//terrain
				
				short[] heightSamples=tData.heightSamples=new short[tData.resolutionX*tData.resolutionZ];
				float[,] rawHeights = terrainData.GetHeights(0, 0, terrainData.heightmapWidth, terrainData.heightmapHeight);

				int zm=rz - 1;
				int xm=rx - 1;

				for (int j = 0; j < rz; j++)
				{
					for (int i = 0; i < rx; i++)
					{
						float rawHeight=rawHeights[j,i];

						if (_needObjFile)
						{
							data.vertices.add(new Vector3(i*mapScale.x,rawHeight*mapScale.y,j*mapScale.z));
						}

						short sample = (short)(rawHeight * 32768.0f);
						if (sample < 0)
							sample = 32767;

						heightSamples[i + (j * rx)]=sample;

						if (_needObjFile)
						{
							if(j<zm && i<xm)
							{
								data.triangles.add(i + (j * rx));
								data.triangles.add(i + ((j+1) * rx));
								data.triangles.add((i+1) + ((j+1) * rx));
	
								data.triangles.add((i+1) + ((j+1) * rx));
								data.triangles.add((i+1) + (j * rx));
								data.triangles.add(i + (j * rx));
							}
						}
						
						
					}
				}

				if (_needObjFile)
				{
					if((data.triangles.size()-startSize)!=zm * xm * 6)
					{
						Ctrl.print("不对1");
					}
				}
	
				//trees
				foreach (TreeInstance tree in terrainData.treeInstances)
				{
					GameObject treeObj = (GameObject)PrefabUtility.InstantiatePrefab(terrainData.treePrototypes[tree.prototypeIndex].prefab);
					treeObj.transform.position = Vector3.Scale(tree.position, terrainData.size) + terrain.transform.position;
					treeObj.transform.localScale = new Vector3(tree.widthScale, tree.heightScale, tree.widthScale);
					treeObj.transform.rotation = Quaternion.AngleAxis(tree.rotation * Mathf.Rad2Deg, Vector3.up);

					MeshFilter[] treeMeshFilters = treeObj.GetComponentsInChildren<MeshFilter>();
							
					foreach (MeshFilter meshFilter in treeMeshFilters)
					{
						doOneMesh(data, meshFilter, ref b);
					}
				}
			}

			//meshes
			foreach (MeshFilter meshFilter in mfs)
			{
				doOneMesh(data, meshFilter, ref b);
			}

			int gx = (int)(b.size.x / config.cellSize + 0.5f);
			int gz = (int)(b.size.z / config.cellSize + 0.5f);
			int tz = (int) config.tileSize;
				
			data.sizeX = (gx + tz - 1) / tz;
			data.sizeZ= (gz + tz - 1) / tz;
				
//				data.sizeX = (int)(b.size.x / (config.tileSize * config.cellSize));
//				data.sizeZ = (int)(b.size.z / (config.tileSize * config.cellSize));
			
			int sx = (int) Mathf.Floor(b.min.x / data.sizeX);
			int sz = (int) Mathf.Floor(b.min.z / data.sizeZ);
			data.originX = sx*data.sizeX;
			data.originZ = sz*data.sizeZ;
			data.tileSize = config.tileSize * config.cellSize;

			data.min = b.min;
			data.min.y -= 1;
			data.max = b.max;
			data.max.y += 1;
			
			callNative(config,data,currentMapID);
		}

		private static void doOneMesh(SceneRecastData data,MeshFilter meshFilter,ref Bounds b)
		{
			Renderer renderer = meshFilter.GetComponent<Renderer>();
				
			if(renderer==null)
				return;

			Mesh mesh = meshFilter.sharedMesh;

			if (mesh == null)
				return;

			//需要Navigation
			if ((GameObjectUtility.GetStaticEditorFlags(meshFilter.gameObject) & StaticEditorFlags.NavigationStatic) != 0)
			{
				int area=GameObjectUtility.GetNavMeshArea(meshFilter.gameObject);

				int index;
				if (!data.meshRepeatDic.tryGetValue(mesh, out index))
				{
					SceneRecastMeshData mData=new SceneRecastMeshData();
					mData.triangles=mesh.triangles;
					mData.vertices=mesh.vertices;

//				Vector3[] vertices =meshVertices= mesh.vertices;
//				mData.vertices=new Vector3[vertices.Length];
//
//				for (int i = 0; i < vertices.Length; i++)
//				{
//					mData.vertices[i]=meshFilter.transform.TransformPoint(vertices[i]);
//				}
					
					Bounds mBounds = mesh.bounds;
					mData.min=mBounds.min;
					mData.max=mBounds.max;

//				mData.min = meshFilter.transform.TransformPoint(mBounds.min);
//				mData.max = meshFilter.transform.TransformPoint(mBounds.max);
					
					index = data.meshList.size();
					data.meshList.add(mData);
					data.meshRepeatDic.put(mesh,index);
				}

				Matrix4x4 matrix = meshFilter.gameObject.transform.localToWorldMatrix;

				SceneRecastObjData objData=new SceneRecastObjData();
				objData.meshIndex = index;
				objData.area=area;
				
				Ctrl.print("看area",area);
				
				objData.x = matrix.GetColumn(0);
				objData.y = matrix.GetColumn(1);
				objData.z = matrix.GetColumn(2);
				objData.w = matrix.GetColumn(3);
			
				Bounds rb = renderer.bounds;
				objData.min = rb.min;
				objData.max = rb.max;
				
				b.Encapsulate(rb);
				
				if (_newBound)
				{
					_newBound = false;
					b = rb;
				}
				else
				{
					b.Encapsulate(rb);
				}
				
				data.objList.add(objData);
				
				if (_needObjFile)
				{
					int vSize = data.vertices.size();
			
					Vector3[] meshVertices=mesh.vertices;
				
					foreach(Vector3 v in meshVertices)
					{
						data.vertices.add(meshFilter.transform.TransformPoint(v));
					}

					foreach (int v in mesh.triangles)
					{
						data.triangles.add(vSize+v);
					}
				}
				
			}
		}

		private static void callNative(NavMeshConfig config, SceneRecastData data,int currentMapID)
		{
			BytesWriteStream stream=new BytesWriteStream();
			config.write(stream);

			string cStr = ShineToolGlobal.mapInfoPath+"/navConfig.bin";
			string dStr = SceneEditorWindow.getMapFilePathFront(currentMapID)+"_navData.bin";
			string objStr = SceneEditorWindow.getMapFilePathFront(currentMapID)+"_navData.obj";

			FileUtils.writeFileForBytesWriteStream(cStr,stream);
			
			stream.clear();
			data.write(stream);
			FileUtils.writeFileForBytesWriteStream(dStr,stream);

			if (_needObjFile)
			{
				StringBuilder sb=new StringBuilder();

				foreach(Vector3 vec in data.vertices)
				{
					sb.Append('v');
					sb.Append(' ');
					sb.Append(vec.x);
					sb.Append(' ');
					sb.Append(vec.y);
					sb.Append(' ');
					sb.Append(vec.z);
					sb.Append('\n');
				}

				int[] navIndices=data.triangles.getValues();
				int len=data.triangles.size() / 3;

				for(int i=0;i<len;i++)
				{
					sb.Append('f');
					sb.Append(' ');
					sb.Append(navIndices[i*3]+1);
					sb.Append(' ');
					sb.Append(navIndices[i*3+1]+1);
					sb.Append(' ');
					sb.Append(navIndices[i*3+2]+1);
					sb.Append('\n');
				}

				FileUtils.writeFileForUTF(objStr,sb.ToString());
			}

			Ctrl.print("callJar");
			ToolFileUtils.executeServerTool("exportNav",currentMapID.ToString());
			
			Ctrl.print("OK");
		}
	}

	public class NavMeshConfig
	{
		//以下是unity参数(cellSize即voxel size)
		public float agentHeight = 2.0f;
		public float agentRadius = 0.5f;
		public float agentMaxClimb = 0.4f;
		public float agentMaxSlope = 45.0f;
		
		public float tileSize = 64;
		public float cellSize = 0.1f;

		//下面这些不用动
		public float regionMinSize = 8;
		public float edgeMaxError = 1.3f;

		/** 写入流 */
		public void write(BytesWriteStream stream)
		{
			stream.writeFloat(agentHeight);
			stream.writeFloat(agentRadius);
			stream.writeFloat(agentMaxClimb);
			stream.writeFloat(agentMaxSlope);
			
			stream.writeFloat(tileSize);
			stream.writeFloat(cellSize);

			stream.writeFloat(regionMinSize);
			stream.writeFloat(edgeMaxError);

		}
	}
	
	/// <summary>
	/// 场景寻路数据
	/// </summary>
	public class SceneRecastData
	{
		public int originX;
		public int originZ;
		public int sizeX;
		public int sizeZ;
		public float tileSize;

		public Vector3 min;
		public Vector3 max;

		public SceneRecastTerrainData terrain;

		public int meshIndex = 0;
		
		public SList<SceneRecastMeshData> meshList=new SList<SceneRecastMeshData>();
		
		public SMap<Mesh,int> meshRepeatDic=new SMap<Mesh, int>();
		
		public SList<SceneRecastObjData> objList=new SList<SceneRecastObjData>();

		//temp
		public IntList triangles=new IntList();
		public SList<Vector3> vertices=new SList<Vector3>();
	
		/** 写入流 */
		public void write(BytesWriteStream stream)
		{
			stream.writeInt(originX);
			stream.writeInt(originZ);
			stream.writeInt(sizeX);
			stream.writeInt(sizeZ);
			stream.writeFloat(tileSize);
			
			stream.writeVector3(min);
			stream.writeVector3(max);

			if (terrain != null)
			{
				stream.writeBoolean(true);
				terrain.write(stream);
			}
			else
			{
				stream.writeBoolean(false);
			}

			int len = meshList.size();
			SceneRecastMeshData[] values = meshList.getValues();
			stream.writeLen(len);

			for (int i = 0; i < len; i++)
			{
				values[i].write(stream);
			}

			len = objList.size();
			SceneRecastObjData[] values2 = objList.getValues();
			stream.writeLen(len);
			
			for (int i = 0; i < len; i++)
			{
				values2[i].write(stream);
			}
		}
	}

	public class SceneRecastTerrainData
	{
		public Vector3 origin;
		public Vector3 unit;

		public int resolutionX;
		public int resolutionZ;

		public short[] heightSamples;

		public void write(BytesWriteStream stream)
		{
			stream.writeVector3(origin);
			stream.writeVector3(unit);
			stream.writeInt(resolutionX);
			stream.writeInt(resolutionZ);

			int len = heightSamples.Length;
			stream.writeLen(len);
			
			for (int i = 0; i < len; i++)
			{
				stream.natureWriteShort(heightSamples[i]);
			}
		}
	}

	public class SceneRecastMeshData
	{
		public int[] triangles;
		public Vector3[] vertices;
		public Vector3 min;
		public Vector3 max;

		public void write(BytesWriteStream stream)
		{
			int len = triangles.Length;
			stream.writeLen(len);
			for (int i = 0; i < len; i++)
			{
				stream.writeInt(triangles[i]);
			}

			len = vertices.Length;
			stream.writeLen(len);
			for (int i = 0; i < len; i++)
			{
				stream.writeVector3(vertices[i]);
			}
			
			stream.writeVector3(min);
			stream.writeVector3(max);
		}
	}

	public class SceneRecastObjData
	{
		public int meshIndex;
		public int area;
		public Vector3 x;
		public Vector3 y;
		public Vector3 z;
		public Vector3 w;
		public Vector3 min;
		public Vector3 max;

		public void write(BytesWriteStream stream)
		{
			stream.writeInt(meshIndex);
			stream.writeInt(area);
			stream.writeVector3(x);
			stream.writeVector3(y);
			stream.writeVector3(z);
			stream.writeVector3(w);
			stream.writeVector3(min);
			stream.writeVector3(max);
		}
	}
}