namespace Shine
{
    /** 回调包装 */
    export class Func
    {
        private _thisArg:any;

        private _func:Function;

        constructor(thisArg:any,func:Function)
        {
            this._thisArg=thisArg;
            this._func=func;
        }

        public invoke(...args:any[]):any
        {
            var re=null;

            if(this._func!=null)
            {
                try
                {
                    re=this._func.apply(this._thisArg,args);
                }
                catch(err)
                {
                    Ctrl.errorLog(err);
                }
                
                return re;
            }

            return null;
        }

        public get length():number
        {
            return this._func.length;
        }
        
        public static create0(func:Function):Func
        {
            return new Func(null,func);
        }

        public static create(thisArg:any,func:Function):Func
        {
            return new Func(thisArg,func);
        }
    }
}