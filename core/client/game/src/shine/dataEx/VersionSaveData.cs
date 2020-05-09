namespace ShineEngine
{
	/** 版本保存数据 */
	public class VersionSaveData
	{
		/** app版本 */
		public int appVersion;
		/** 资源版本 */
		public int resourceVersion;
		/** 显示版本 */
		public string version;
		/** 是否正式发包(为了让手机本地调试打包时不必再清空缓存) */
		public bool isRelease;
		/** 资源字典 */
		public SMap<string,ResourceSaveData> resourceDic=new SMap<string,ResourceSaveData>();

		protected virtual ResourceSaveData createBundleSaveData()
		{
			return new ResourceSaveData();
		}

		public virtual void readBytes(BytesReadStream stream)
		{
			appVersion=stream.readInt();
			// leastAppVersion=stream.readInt();
			resourceVersion=stream.readInt();
			// leastResourceVersion=stream.readInt();
			version=stream.readUTF();
			isRelease=stream.readBoolean();

			int len=stream.readLen();

			ResourceSaveData sData;

			for(int i=0;i<len;i++)
			{
				sData=createBundleSaveData();
				sData.readBytes(stream);
				resourceDic.put(sData.name,sData);
			}
		}

		public virtual void writeBytes(BytesWriteStream stream)
		{
			stream.writeInt(appVersion);
			// stream.writeInt(leastAppVersion);
			stream.writeInt(resourceVersion);
			// stream.writeInt(leastResourceVersion);
			stream.writeUTF(version);
			stream.writeBoolean(isRelease);

			stream.writeLen(resourceDic.size());

			resourceDic.forEachValue(v=>
			{
				v.writeBytes(stream);
			});
		}
	}
}