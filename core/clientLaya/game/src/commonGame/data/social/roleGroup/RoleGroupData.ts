namespace Shine
{
	/** 玩家群数据(单个群)(generated by shine) */
	export class RoleGroupData extends BaseData
	{
		/** 数据类型ID */
		public static dataID:number=BaseDataType.RoleGroup;
		
		/** 成员组 */
		public members:SMap<number,RoleGroupMemberData>;
		
		/** 群id */
		public groupID:number;
		
		/** 申请加入组 */
		public applyDic:SMap<number,PlayerApplyRoleGroupData>;
		
		/** 等级 */
		public level:number;
		
		/** 群名 */
		public name:string;
		
		/** 下一个0点时刻 */
		public nextDailyTime:number;
		
		/** 日志信息队列 */
		public logQueue:SList<InfoLogData>;
		
		/** 经验值 */
		public exp:number;
		
		/** 申请时是否可直接入群(无需同意) */
		public canApplyInAbs:boolean;
		
		/** 公告 */
		public notice:string;
		
		constructor()
		{
			super();
			this._dataID=BaseDataType.RoleGroup;
		}
		
		/** 读取字节流(简版) */
		protected toReadBytesSimple(stream:BytesReadStream):void
		{
			this.groupID=stream.readLong();
			
			this.level=stream.readInt();
			
			var membersLen:number=stream.readLen();
			if(this.members!=null)
			{
				this.members.clear();
				this.members.ensureCapacity(membersLen);
			}
			else
			{
				this.members=new SMap<number,RoleGroupMemberData>(membersLen);
			}
			
			var membersT:SMap<number,RoleGroupMemberData>=this.members;
			for(var membersI:number=membersLen-1;membersI>=0;--membersI)
			{
				var membersV:RoleGroupMemberData;
				membersV=stream.readDataSimpleNotNull() as RoleGroupMemberData;
				
				membersT.put(membersV.playerID,membersV);
			}
			
			this.name=stream.readUTF();
			
			this.notice=stream.readUTF();
			
			var applyDicLen:number=stream.readLen();
			if(this.applyDic!=null)
			{
				this.applyDic.clear();
				this.applyDic.ensureCapacity(applyDicLen);
			}
			else
			{
				this.applyDic=new SMap<number,PlayerApplyRoleGroupData>(applyDicLen);
			}
			
			var applyDicT:SMap<number,PlayerApplyRoleGroupData>=this.applyDic;
			for(var applyDicI:number=applyDicLen-1;applyDicI>=0;--applyDicI)
			{
				var applyDicK:number;
				var applyDicV:PlayerApplyRoleGroupData;
				applyDicK=stream.readLong();
				
				applyDicV=stream.readDataSimpleNotNull() as PlayerApplyRoleGroupData;
				
				applyDicT.put(applyDicK,applyDicV);
			}
			
			this.canApplyInAbs=stream.readBoolean();
			
			this.exp=stream.readLong();
			
			var logQueueLen:number=stream.readLen();
			if(this.logQueue!=null)
			{
				this.logQueue.clear();
				this.logQueue.ensureCapacity(logQueueLen);
			}
			else
			{
				this.logQueue=new SList<InfoLogData>();
			}
			
			var logQueueT:SList<InfoLogData>=this.logQueue;
			for(var logQueueI:number=logQueueLen-1;logQueueI>=0;--logQueueI)
			{
				var logQueueV:InfoLogData;
				logQueueV=stream.readDataSimpleNotNull() as InfoLogData;
				
				logQueueT.add(logQueueV);
			}
			
			this.nextDailyTime=stream.readLong();
			
		}
		
		/** 写入字节流(简版) */
		protected toWriteBytesSimple(stream:BytesWriteStream):void
		{
			stream.writeLong(this.groupID);
			
			stream.writeInt(this.level);
			
			stream.writeLen(this.members.size());
			if(!this.members.isEmpty())
			{
				for(var membersV of this.members.getValues())
				{
					stream.writeDataSimpleNotNull(membersV);
					
				}
			}
			
			stream.writeUTF(this.name);
			
			stream.writeUTF(this.notice);
			
			stream.writeLen(this.applyDic.size());
			if(!this.applyDic.isEmpty())
			{
				for(var applyDicK of this.applyDic.getKeys())
				{
					var applyDicV:PlayerApplyRoleGroupData=this.applyDic.get(applyDicK);
					stream.writeLong(applyDicK);
					
					stream.writeDataSimpleNotNull(applyDicV);
					
				}
			}
			
			stream.writeBoolean(this.canApplyInAbs);
			
			stream.writeLong(this.exp);
			
			stream.writeLen(this.logQueue.size());
			if(!this.logQueue.isEmpty())
			{
				for(var logQueueVI=0,logQueueVLen=this.logQueue.length;logQueueVI<logQueueVLen;++logQueueVI)
				{
					var logQueueV:InfoLogData=this.logQueue[logQueueVI];
					stream.writeDataSimpleNotNull(logQueueV);
					
				}
			}
			
			stream.writeLong(this.nextDailyTime);
			
		}
		
		/** 复制(潜拷贝) */
		protected toShadowCopy(data:BaseData):void
		{
			if(!(data instanceof RoleGroupData))
				return;
			
			var mData:RoleGroupData=data as RoleGroupData;
			
			this.groupID=mData.groupID;
			this.level=mData.level;
			this.members=mData.members;
			this.name=mData.name;
			this.notice=mData.notice;
			this.applyDic=mData.applyDic;
			this.canApplyInAbs=mData.canApplyInAbs;
			this.exp=mData.exp;
			this.logQueue=mData.logQueue;
			this.nextDailyTime=mData.nextDailyTime;
		}
		
		/** 复制(深拷贝) */
		protected toCopy(data:BaseData):void
		{
			if(!(data instanceof RoleGroupData))
				return;
			
			var mData:RoleGroupData=data as RoleGroupData;
			
			this.groupID=mData.groupID;
			
			this.level=mData.level;
			
			if(this.members!=null)
			{
				this.members.clear();
				this.members.ensureCapacity(mData.members.size());
			}
			else
			{
				this.members=new SMap<number,RoleGroupMemberData>(mData.members.size());
			}
			
			var membersT:SMap<number,RoleGroupMemberData>=this.members;
			if(!mData.members.isEmpty())
			{
				for(var membersV of mData.members.getValues())
				{
					var membersU:RoleGroupMemberData;
					membersU=membersV.clone() as RoleGroupMemberData;
					
					membersT.put(membersU.playerID,membersU);
				}
			}
			
			this.name=mData.name;
			
			this.notice=mData.notice;
			
			if(this.applyDic!=null)
			{
				this.applyDic.clear();
				this.applyDic.ensureCapacity(mData.applyDic.size());
			}
			else
			{
				this.applyDic=new SMap<number,PlayerApplyRoleGroupData>(mData.applyDic.size());
			}
			
			var applyDicT:SMap<number,PlayerApplyRoleGroupData>=this.applyDic;
			if(!mData.applyDic.isEmpty())
			{
				for(var applyDicK of mData.applyDic.getKeys())
				{
					var applyDicV:PlayerApplyRoleGroupData=mData.applyDic.get(applyDicK);
					var applyDicW:number;
					var applyDicU:PlayerApplyRoleGroupData;
					applyDicW=applyDicK;
					
					applyDicU=applyDicV.clone() as PlayerApplyRoleGroupData;
					
					applyDicT.put(applyDicW,applyDicU);
				}
			}
			
			this.canApplyInAbs=mData.canApplyInAbs;
			
			this.exp=mData.exp;
			
			if(this.logQueue!=null)
			{
				this.logQueue.clear();
				this.logQueue.ensureCapacity(mData.logQueue.size());
			}
			else
			{
				this.logQueue=new SList<InfoLogData>();
			}
			
			var logQueueT:SList<InfoLogData>=this.logQueue;
			if(!mData.logQueue.isEmpty())
			{
				for(var logQueueVI=0,logQueueVLen=mData.logQueue.length;logQueueVI<logQueueVLen;++logQueueVI)
				{
					var logQueueV:InfoLogData=mData.logQueue[logQueueVI];
					var logQueueU:InfoLogData;
					logQueueU=logQueueV.clone() as InfoLogData;
					
					logQueueT.add(logQueueU);
				}
			}
			
			this.nextDailyTime=mData.nextDailyTime;
			
		}
		
		/** 初始化初值 */
		public initDefault():void
		{
			this.members=new SMap<number,RoleGroupMemberData>();
			this.applyDic=new SMap<number,PlayerApplyRoleGroupData>();
			this.logQueue=new SList<InfoLogData>();
		}
		
	}
}
