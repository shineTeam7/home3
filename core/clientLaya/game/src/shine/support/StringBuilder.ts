namespace Shine
{
    /**  */
    export class StringBuilder
    {
        private _str:string="";

        constructor()
        {
            
        }

        public clear():void
        {
            this._str="";
        }

        public append(obj:any):void
        {
            this._str+=obj;
        }

        public toString():string
        {
            return this._str;
        }
    }
}