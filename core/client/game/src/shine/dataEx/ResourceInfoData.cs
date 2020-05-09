using System;
using ShineEngine;

namespace ShineEngine
{
	/// <summary>
	/// 资源信息(editor工程生成)
	/// </summary>
	public class ResourceInfoData
	{
		/** 资源索引 */
		public int id;

		/** 资源名(加载时用的资源名，从Assets/source/后面开始，不包含Assets/source/的资源路径) */
		public string name;

		/** 资源类型 */
		public int type;

		//temp

		/** unityBundle的名字(assets/xxx,并且全小写) */
		private string _uName;

		/** 从字节读取 */
		public void readBytes(BytesReadStream stream)
		{
			id=stream.readInt();
			name=stream.readUTF();
			type=stream.readByte();
		}

		/** 写入字节 */
		public void writeBytes(BytesWriteStream stream)
		{
			stream.writeInt(id);
			stream.writeUTF(name);
			stream.writeByte(type);
		}

		/** 创建(editor用) */
		public static ResourceInfoData create(string name,int type)
		{
			ResourceInfoData infoData=new ResourceInfoData();
			infoData.name=name;
			infoData.type=type;

			return infoData;
		}

		/** 获取unityBundle的名字(assets/xxx,并且全小写) */
		public string getUName()
		{
			if(_uName==null)
			{
				_uName=FileUtils.getBundleResourceUseName(name);
			}

			return _uName;
		}
	}
}