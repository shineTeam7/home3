namespace Shine
{
    /** 本地存储控制 */
	export class LocalSaveControl
	{
        private _data:KeepSaveData;

        private _dirty:boolean=false;

        private _stream:BytesWriteStream=new BytesWriteStream();

     
    }
}