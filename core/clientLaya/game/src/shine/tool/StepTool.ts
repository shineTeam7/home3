namespace Shine
{
	export class StepTool
	{
		/** 未开始 */
		public static readonly None:number=0;
		/** 执行中 */
		private readonly Doing:number=1;
		/** 已完成 */
		private readonly Complete:number=2;

		/** 登录逻辑步骤组 */
		private _loginSteps:SMap<number,StepData>=new SMap<number,StepData>();
		
		private _lastStep:number=-1;

		constructor()
        {

        }

		/** 添加步骤 */
		public addStep(id:number, func:Func,...preIds):void
		{
			var data:StepData=new StepData();
			data.id=id;
			data.func=func;
			data.preIDs=preIds;
			data.state=StepTool.None;

			this._loginSteps.put(data.id,data);
			this._lastStep=id;
		}

		/** 添加下一步(快捷方式) */
		public addNext(id:number,func:Func):void
		{
			if(this._lastStep==-1)
			{
				this.addStep(id,func);
			}
			else
			{
				this.addStep(id,func,this._lastStep);
			}
		}

		/** 还原完成状态 */
		public clearStates():void
		{
			this._loginSteps.forEachValue(v=>{ v.state=StepTool.None; });
		}

		/** 步骤检查 */
		public checkStep():void
		{
			var can:boolean;

			this._loginSteps.forEachValue(data=>
			{
				if(data.state==StepTool.None)
				{
					can=true;

					if(data.preIDs.length>0)
					{
						for(var v of data.preIDs)
						{
							if(this._loginSteps.get(v).state!=this.Complete)
							{
								can=false;
								break;
							}
						}
					}

					if(can)
					{
						data.state=this.Doing;
						data.func.invoke();
					}
				}
			});
		}

		/** 完成步骤 */
		public completeStep(id:number):void
		{
			//不为1,就跳过
			if(this._loginSteps.get(id).state!=this.Doing)
				return;

			this._loginSteps.get(id).state=this.Complete;

			this.checkStep();
		}

		/** 完成步骤Abs(只有登录服务器c层可用) */
		public completeStepAbs(id:number):void
		{
			this._loginSteps.get(id).state=this.Complete;

			this.checkStep();
		}

		/** 看某步是否完成 */
		public isComplete(id:number):boolean
		{
			return this._loginSteps.get(id).state==this.Complete;
		}

		/** 看某步是否执行中 */
		public  isDoing(id:number):boolean
		{
			return this._loginSteps.get(id).state==this.Doing;
		}

		/** 直接执行某步骤 */
		public doStepAbs(id:number):void
		{
			this.completeStepPreId(id);

			this.checkStep();
		}

		/** 完成该id的所有前置 */
		public completeStepPreId(id:number):void
		{
			
			this.completeStepPreData(this._loginSteps.get(id));
		}

		private completeStepPreData(stepData:StepData ):void
		{
			if(stepData.preIDs!=null && stepData.preIDs.length>0)
			{
				for(var v of stepData.preIDs)
				{
					var sData:StepData=this._loginSteps.get(v);
					this.completeStepPreData(sData);
					sData.state=this.Complete;
				}
			}
		}
		
		/** 设置当前执行(并且清空，添加前置) */
		public setCurrentDoing(id:number):void
		{
			this.clearStates();
			this.completeStepPreId(id);
			this._loginSteps.get(id).state=this.Doing;
		}
	}
}