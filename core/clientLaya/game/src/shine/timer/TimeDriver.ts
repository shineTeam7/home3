namespace Shine
{
	/** 计时器驱动 */
	export class TimeDriver 
	{
	    private static _instance: TimeDriver;
	
	    /** 单例 */
	    public static get instance(): TimeDriver 
	    {
	        if( this._instance == null)
			{
				this._instance=new TimeDriver();
			}

			return this._instance 
	    }

	    //timeEx
	    /** 时间执行组 */
	    private _timeExDic:SMap<number,TimeExecuteData>= new SMap<number,TimeExecuteData>();
	    /** 计数 */
	    private _timeExIndexMaker:IndexMaker= new IndexMaker();
	
	    //frame
	    /** 每帧调用 */
	    private  _frameDic:SMap<number,Func>=new SMap<number,Func>();
		/** 每帧调用计数 */
		private _frameIndexMaker:IndexMaker= new IndexMaker();
	
		/** update */
		private _updateDic:SMap<number,Func>=new SMap<number,Func>();
	
		//callLater
		/** 延时调用 */
		private _callLaters:SList<Func>=new SList<Func>();
		/** callLater锁 */
		private _callLaterForEaching:boolean= false;
		/** 延时调用临时组 */
		private _callLaterTemps:SList<Func>=new SList<Func>();

		private removeList:SList<number>= new SList<number>();
	
		private TimeDriver()
	    {
	
	    }
	
		public tick(delay:number):void
	    {
			if(!this._timeExDic.isEmpty())
			{
				this.removeList.clear();
				this._timeExDic.forEachValue(tData=>
				{
						if (!tData.pause) 
	                    {
	                        tData.time += delay;
	
	                        if (tData.time >= tData.timeMax)
	                        {
	                            tData.intArg = tData.time;
	
	                            if (tData.isCut)
	                                tData.time = 0;
	                            else
	                                tData.time -= tData.timeMax;
	
	                            if (!tData.isGoOn)
	                            {

									this.removeList.add(tData.index);
	                            }
	
	                            this.executeTimeExFunc(tData);
	                        }
						}
				});

				this.removeList.forEach(key => 
				{
					 //移除
	                this._timeExDic.remove(key);
				});

				
	                               
					
			}

	       
	
	
	         //frame
	
	        var frameDic:SMap<number,Func>;
	
	        if (!(frameDic = this._frameDic).isEmpty()) 
	        {
	            var frameValues:Func[];
	            var v:Func;
	
	            for (var i:number= (frameValues = frameDic.getValues()).length - 1; i >= 0;--i)
	            {
	                if ((v = frameValues[i]) != null) {
	                    if (ShineSetting.needError) {
	                        v.invoke(delay);
	                    }
	                    else {
	                        try 
	                        {
	                            v.invoke(delay);
	                        }
	                        catch (err) 
	                        {
	                            Ctrl.throwError(err);
	                        }
	                    }
	
	                    if (v != frameValues[i]) {
	                        ++i;
	                    }
	                }
	            }
	        }
	
	        //callLater(放在最下面，用来在一次tick的最后执行逻辑)
	
	        var callLaters:SList<Func> ;
	
	        if (!(callLaters = this._callLaters).isEmpty()) 
	        {
	            this._callLaterForEaching = true;
				
				for(var i:number=0;i<callLaters.length;i++)
				{
					if (ShineSetting.needError) 
	                {
	                    callLaters[i].invoke();
	                }
	                else
	                {
	                    try 
	                    {
	                        callLaters[i].invoke();
	                    }
	                    catch (err) 
	                    {
	                        Ctrl.throwError(err);
	                    }
	                }
				}
	
	            callLaters.clear();
	
	            this._callLaterForEaching = false;
	
	            var callLaterTemps:SList<Func>;
	
	            if (!(callLaterTemps = this._callLaterTemps).isEmpty()) 
	            {
	                callLaters.addAll(callLaterTemps);
	
	                callLaterTemps.clear();
	            }
	        }
	    }
	
		/** update */
	    public update():void
	    {
	        var frameDic:SMap<number,Func>;
	
	        if (!(frameDic = this._updateDic).isEmpty()) 
	        {
	            var frameValues:Func[] ;
	            var v:Func;
	
	            for (var i:number= (frameValues = frameDic.getValues()).length - 1; i >= 0;--i)
	            {
	                if ((v = frameValues[i]) != null) 
	                {
	                    try 
	                    {
	                        v.invoke();
	                    }
	                    catch (err) 
	                    {
	                        Ctrl.throwError(err);
	                    }
	
	                    if (v != frameValues[i])
	                    {
	                        ++i;
	                    }
	                }
	            }
	        }
	    }
	    
		/** 析构 */
	    public dispose():void
	    {
	
	    }
	
		/** 添加timeEx */
		private addTimeEx(tData:TimeExecuteData):number
	    {
	        var index:number= this._timeExIndexMaker.get();
	
	        tData.index = index;
	
	        this._timeExDic.put(index, tData);
	
	        return index;
	    }
	
		/** 移除timeEx */
		private removeTimeEx(index:number):void
	    {
	        if (index <= 0) 
	        {
	            Ctrl.throwError("timeIndex不能<=0");
	
	            return;
	        }
	
	        var tData:TimeExecuteData= this._timeExDic.get(index);
	
	        if (tData == null)
	            return;
	
	        this._timeExDic.remove(index);
	    }
	
		private executeTimeExFunc(tData:TimeExecuteData):void
	    {
	        try {
	            if (tData.isCut) {
	                tData.func.invoke(tData.intArg);
	            }
	            else {
	                tData.func.invoke();
	            }
	        }
	        catch (err) 
	        {
	            Ctrl.throwError(err);
	        }
	    }
	
		/** 暂停时间ex */
		public pauseTimeEx(index:number,value:boolean):void
	    {
	        var tData:TimeExecuteData = this._timeExDic.get(index);
	
	        if (tData == null)
	            return;
	
	        tData.pause = value;
	    }
	
		/** 延迟delay毫秒,执行方法*/
		public setTimeOut(func:Func,delay:number):number
	    {
	        if (delay <= 0) 
	        {
	            Ctrl.throwError("传入的时间间隔<=0");
	            return -1;
	        }
	
	        var tData:TimeExecuteData= new TimeExecuteData();
	
	        tData.func = func;
	        tData.timeMax = delay;
	        tData.isGoOn = false;
	
	        return this.addTimeEx(tData);
	    }
	
		/**  */ 
		public clearTimeOut(index:number):void
	    {
	        this.removeTimeEx(index);
	    }
	
		/** 立即结束timeOut */
		public finishTimeOut(index:number):void
	    {
	        var tData:TimeExecuteData= this._timeExDic.get(index);
	
	        if (tData == null)
	            return;
	
	        this.clearTimeOut(index);
	
	        this.executeTimeExFunc(tData);
	    }
	
		/** 间隔delay毫秒，执行方法 */
		public  setIntervalFixed(func:Func,delay:number):number
	    {
	        if (delay <= 0) {
	            Ctrl.throwError("传入的时间间隔<=0");
	            return -1;
	        }
	
	        var tData:TimeExecuteData= new TimeExecuteData();
	
	        tData.func = func;
	        tData.timeMax = delay;
	        tData.isGoOn = true;
	        tData.isCut = false;
	
	        return this.addTimeEx(tData);
	    }
	
		/** 间隔delay毫秒,执行方法(如掉帧,只回调一次) */
		public setInterval(func:Func,delay:number):number
	    {
	        if (delay <= 0) 
	        {
	            Ctrl.throwError("传入的时间间隔<=0");
	            return -1;
	        }
	
	        var tData:TimeExecuteData= new TimeExecuteData();
	
	        tData.func = func;
	        tData.timeMax = delay;
	        tData.isGoOn = true;
	        tData.isCut = true;
			tData.time=0;
	
	        return this.addTimeEx(tData);
	    }
	
		/** 重置timeEx */
		public resetTimeEx(index:number,delay:number):void
	    {
	        var tData:TimeExecuteData= this._timeExDic.get(index);
	
	        if (tData == null)
	            return;
	
	        tData.time = 0;
	        tData.timeMax = delay;
	    }
	
		/** 取消间隔 */
		public clearInterval(index:number):void
	    {
	        this.removeTimeEx(index);
	    }
	
		//frame
	
		/** 设置每帧调用 */
	    public setFrame(func:Func):number
	    {
	        var index:number= this._frameIndexMaker.get();
	
	        this._frameDic.put(index, func);
	
	        return index;
	    }
	
	    /** 取消每帧调用 */
	    public clearFrame(index:number):void
	    {
	       this._frameDic.remove(index);
	    }
	
		//update
	
		/** 设置每帧调用(update) */
		public setUpdate(func:Func):number
	    {
	        var index:number= this._frameIndexMaker.get();
	
	       this._updateDic.put(index, func);
	
	        return index;
	    }
	
		/** 取消每帧调用(update) */
		public clearUpdate(index:number):void
	    {
	        this._updateDic.remove(index);
	    }
	
		//callLater
	    /** 延迟调用 */
		public callLater(func:Func):void
	    {
	        if (this._callLaterForEaching) 
	        {
	            this._callLaterTemps.add(func);
	        }
	        else 
	        {
	            this._callLaters.add(func);
	        }
	    }
	}
}