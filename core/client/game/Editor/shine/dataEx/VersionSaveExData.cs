using ShineEngine;

namespace ShineEditor
{
	public class VersionSaveExData:VersionSaveData
	{
		/** 最低app版本 */
		public int leastAppVersion;
		/** 最低资源版本 */
		public int leastResourceVersion;

		public override void readBytes(BytesReadStream stream)
		{
			base.readBytes(stream);

			leastAppVersion=stream.readInt();
			leastResourceVersion=stream.readInt();
		}

		public override void writeBytes(BytesWriteStream stream)
		{
			base.writeBytes(stream);

			stream.writeInt(leastAppVersion);
			stream.writeInt(leastResourceVersion);
		}

		protected override ResourceSaveData createBundleSaveData()
		{
			return new ResourceSaveExData();
		}

		public ResourceSaveExData getBundleEx(string name)
		{
			return (ResourceSaveExData)resourceDic.get(name);
		}

		/** 创建原生数据 */
		public VersionSaveData createOriginalData()
		{
			VersionSaveData data=new VersionSaveData();
			data.appVersion=appVersion;
			data.resourceVersion=resourceVersion;
			data.version=version;
			data.isRelease=isRelease;

			resourceDic.forEachValue(v=>
			{
				ResourceSaveData dd=new ResourceSaveData();
				dd.name=v.name;
				dd.size=v.size;
				dd.saveType=v.saveType;
				dd.version=v.version;
				dd.state=ResourceSaveStateType.None;
				data.resourceDic.put(dd.name,dd);
			});

			return data;
		}

		/** 创建记录数据 */
		public VersionRecordData createRecordData()
		{
			VersionRecordData data=new VersionRecordData();
			data.appVersion=appVersion;
			data.leastAppVersion=leastAppVersion;
			data.resourceVersion=resourceVersion;
			data.leastResourceVersion=leastResourceVersion;
			data.version=version;
			data.isRelease=isRelease;

			return data;
		}
	}
}