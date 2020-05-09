namespace ShineEngine
{
	public class ResourceSaveData
	{
		/** 资源名(加载时用的资源名，从Assets/source/后面开始，不包含Assets/source/的资源路径) */
		public string name;
		/** 大小 */
		public int size;
		/** 资源版本 */
		public int version;
		/** 保存方式 */
		public int saveType;
		/** 当前状态(未处理) */
		public int state=ResourceSaveStateType.None;
		//temp

		/** 临时标记用 */
		public bool dided=false;

		private int _type=-1;

		public virtual void readBytes(BytesReadStream stream)
		{
			name=stream.readUTF();
			size=stream.readInt();
			version=stream.readInt();
			saveType=stream.readInt();
			state=stream.readInt();
		}

		public virtual void writeBytes(BytesWriteStream stream)
		{
			stream.writeUTF(name);
			stream.writeInt(size);
			stream.writeInt(version);
			stream.writeInt(saveType);
			stream.writeInt(state);
		}

		/** 获取资源类型 */
		public int getType()
		{
			if(_type>=0)
				return _type;

			return _type=ResourceType.getFileResourceType(name);
		}
	}
}