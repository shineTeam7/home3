namespace ShineEngine
{
	public class BytesWriteStreamPool:ObjectPool<BytesWriteStream>
	{
		private static BytesWriteStreamPool _instance=new BytesWriteStreamPool();

		public BytesWriteStreamPool():base(()=>new BytesWriteStream(),256)
		{

		}

		public override void back(BytesWriteStream obj)
		{
			if(obj.getBuf().Length>ShineSetting.bytesWriteStreamPoolKeepSize)
				return;


			base.back(obj);
		}

		/** 取一个 */
		public static BytesWriteStream create()
		{
			return _instance.getOne();
		}

		public static void release(BytesWriteStream stream)
		{
			_instance.back(stream);
		}
	}
}