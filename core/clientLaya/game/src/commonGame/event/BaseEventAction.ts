namespace Shine
{
	export class BaseEventAction<T>
	{
	        public func:Func;
	
	        public execute(data: T): void 
	        {
	                if (this.func != null)
	                {
	                        if (this.func.length == 0)
	                        {
					this.func.invoke();
	                        }
	                        else 
	                        {
	                        	this.func.invoke(data);
	                        }
	                }
	        }
	}
}