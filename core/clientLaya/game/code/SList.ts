namespace Shine
{
	/** List */
	export class SList<V> extends Array<V>
	{
	    constructor(capacity?:number)
        {
            super(capacity);
        }
	
	    /** 尺寸 */
	    public size():number
        {
            return this.length;
        }

        public clear():void
        {
            this.length=0;
        }
	
	    public isEmpty():boolean
        {
            return this.length==0;
        }
	
	    /** 添加元素 */
	    public add(obj:V):void
        {
            this.push(obj);
        }
	
	    /** 获取元素 */
	    public get(index:number):V
        {
            return this[index];
        }
	
	    /** 移除某位置的元素 */
	    public remove(index:number):V
        {
            var re=this[index];
            delete this[index];
            return re;
        }
	
	    /** 移除对象 */
	    public removeObj(obj:V):boolean
        {
            var index:number=this.indexOf(obj);

            if(index==-1)
                return false;
            
            delete this[index];
            return true;
        }
	
	    /** 是否包含 */
	    public contains(obj:V):boolean
        {
            return this.indexOf(obj)!=-1;
        }
	
	    /** 获取末尾 */
	    public getLast():V
        {
            return this.get(this.length-1);
        }
	
	    /** 插入 */
	    public insert(index:number,obj:V):void
        {
            this.splice(index,0,obj);
        }
	
	    public toArray():V[]
        {
            return this.slice(0,this.length);
        }
	
	    /** 确认容量 */
	    public ensureCapacity(capacity:number):void
        {

        }
	
	    /** 添加一组 */
	    public addAll(list:SList<V>):void
        {
            this.push.apply(this,list.toArray());
        }
	
	    /** 克隆 */
	    public clone():SList<V>
        {
            var re:SList<V>=new SList<V>();
            re.addAll(this);
            return re;
        }
	    
	    /** 遍历 */
	    public forEach(consumer:(value:V)=>void):void
        {
            for(var i=0;i<this.length;++i)
            {
                consumer(this[i]);
            }
        }
	}
}