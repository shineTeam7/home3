namespace ShineEngine
{
	/// <summary>
	/// 包信息使用数据
	/// </summary>
	public class BundleInfoData
	{
		/** id */
		public int id;

		/** 依赖序号组 */
		public int[] depends;

		/** 资源序号组 */
		public int[] assets;

		// /** 资源尺寸 */、

		/** 从字节读取 */
		public void readBytes(BytesReadStream stream)
		{
			id=stream.readInt();

			int len=stream.readLen();
			depends=new int[len];

			for(int i=0;i<len;++i)
			{
				depends[i]=stream.readInt();
			}

			len=stream.readLen();

			assets=new int[len];

			for(int i=0;i<len;++i)
			{
				assets[i]=stream.readInt();
			}
		}

		/** 写入字节 */
		public void writeBytes(BytesWriteStream stream)
		{
			stream.writeInt(id);

			stream.writeLen(depends.Length);

			foreach(int v in depends)
			{
				stream.writeInt(v);
			}

			stream.writeLen(assets.Length);

			foreach(int v in assets)
			{
				stream.writeInt(v);
			}
		}
	}
}