using System;

namespace ShineEngine
{
	/// <summary>
	/// 流程控制插件
	/// </summary>
	public class StepTool
	{
		/** 未开始 */
		private const int None=0;
		/** 执行中 */
		private const int Doing=1;
		/** 已完成 */
		private const int Complete=2;

		/** 登录逻辑步骤组 */

		private IntObjectMap<StepData> _loginSteps=new IntObjectMap<StepData>();

		private int _lastStep=-1;

		public StepTool()
		{

		}

		/** 添加步骤 */
		public void addStep(int id,Action func,params int[] preIds)
		{
			StepData data=new StepData();
			data.id=id;
			data.func=func;
			data.preIDs=preIds;
			data.state=None;

			_loginSteps.put(data.id,data);
			_lastStep=id;
		}

		/** 添加下一步(快捷方式) */
		public void addNext(int id,Action func)
		{
			if(_lastStep==-1)
			{
				addStep(id,func);
			}
			else
			{
				addStep(id,func,_lastStep);
			}
		}

		/** 还原完成状态 */
		public void clearStates()
		{
			foreach(StepData v in _loginSteps)
			{
				v.state=None;
			}
		}

		/** 步骤检查 */
		public void checkStep()
		{
			bool can;

			foreach(StepData data in _loginSteps)
			{
				if(data.state==None)
				{
					can=true;

					if(data.preIDs.Length>0)
					{
						foreach(int v in data.preIDs)
						{
							if(_loginSteps[v].state!=Complete)
							{
								can=false;
								break;
							}
						}
					}

					if(can)
					{
						data.state=Doing;
						data.func();
					}
				}
			}
		}

		/** 完成步骤 */
		public void completeStep(int id)
		{
			//不为1,就跳过
			if(_loginSteps[id].state!=Doing)
				return;

			_loginSteps[id].state=Complete;

			checkStep();
		}

		/** 完成步骤Abs(只有登录服务器c层可用) */
		public void completeStepAbs(int id)
		{
			_loginSteps[id].state=Complete;

			checkStep();
		}

		/** 看某步是否完成 */
		public bool isComplete(int id)
		{
			return _loginSteps[id].state==Complete;
		}

		/** 看某步是否执行中 */
		public bool isDoing(int id)
		{
			return _loginSteps[id].state==Doing;
		}

		/** 直接执行某步骤 */
		public void doStepAbs(int id)
		{
			completeStepPre(id);

			checkStep();
		}

		/** 完成该id的所有前置 */
		public void completeStepPre(int id)
		{
			completeStepPre(_loginSteps.get(id));
		}

		private void completeStepPre(StepData stepData)
		{
			if(stepData.preIDs!=null && stepData.preIDs.Length>0)
			{
				foreach(int v in stepData.preIDs)
				{
					StepData sData=_loginSteps.get(v);
					completeStepPre(sData);
					sData.state=Complete;
				}
			}
		}

		/** 设置当前执行(并且清空，添加前置) */
		public void setCurrentDoing(int id)
		{
			clearStates();
			completeStepPre(id);
			_loginSteps.get(id).state=Doing;
		}

		/** 登录步骤数据 */
		private class StepData
		{
			/** id */
			public int id;

			/** 前置ID组 */
			public int[] preIDs;

			/** 方法 */
			public Action func;

			/** 状态(0:未开始,1:执行中,2:已完成) */
			public int state=None;
		}
	}
}