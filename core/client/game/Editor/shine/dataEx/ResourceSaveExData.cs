using ShineEngine;

namespace ShineEditor
{
	/// <summary>
	/// 资源保存数据
	/// </summary>
	public class ResourceSaveExData:ResourceSaveData
	{
		/** md5信息 */
		public string md5="";
		//temp
		/** 是否执行过 */
		public bool dided=false;

		public override void readBytes(BytesReadStream stream)
		{
			base.readBytes(stream);

			md5=stream.readUTF();
		}

		public override void writeBytes(BytesWriteStream stream)
		{
			base.writeBytes(stream);

			stream.writeUTF(md5);
		}
	}
}