namespace Shine
{
	export class BytesUtils
    {
        private static _lBytes:Laya.Byte=new Laya.Byte();

        // /** 二进制数组拷贝 */
        // public static arrayCopy(src: ArrayBuffer, srcPos: number, dest: ArrayBuffer, destPos: number, length: number): void
        // {
        //     try
        //     {
        //         let srcByteView = new Uint8Array(src, srcPos, length);
        //         let destByteView = new Uint8Array(dest);
        //         destByteView.set(srcByteView, destPos);
        //     }
        //     catch(error)
        //     {
        //         throw "arrayCopy failed";
        //     }
        // }

        /** 获取ArrayBuffer */
        public static getUTFBytes(str: string): ArrayBuffer
        {
            this._lBytes.clear();
            this._lBytes.writeUTFBytes(str);
            var re:ArrayBuffer=this._lBytes.buffer;
            this._lBytes.clear();
            return re;
        }

        /** 获取ArrayBuffer */
        public static getArrayBuff(str: string): ArrayBuffer
        {
            let re = new ArrayBuffer(str.length * 2);
            let u8d = new Uint8Array(re);
            let dv = new DataView(re);
            let pos = 0;
            for (let i=0, sz=str.length; i < sz; i++){
                let char = str.charCodeAt(i);
                if (char <= 0x7F){
                    dv.setInt8(pos++, char);
                }
                else if (char <= 0x7FF)
                {
                    u8d.set([0xC0 | (char >> 6), 0x80 | (char & 0x3F)], pos);
                    pos += 2;
                }
                else if (char <=0xFFFF)
                {
                    u8d.set([0xE0 | (char >> 12), 0x80 | ((char >> 6) & 0x3F), 0x80 | (char & 0x3F)], pos);
                    pos += 3;
                }
                else
                {
                    u8d.set([0xF0 | (char >> 18), 0x80 | ((char >> 12)& 0x3F), 0x80 | ((char >> 6)& 0x3F), 0x80 | (char & 0x3F)], pos);
                    pos += 4;
                }
            }
            return re;
        }

        /** 获取某一段的字节hash(short) */
        public static getHashCheck(buf: ArrayBuffer, pos: number, length: number): number
        {
            return 0;
        }
    }
}