package com.home.commonGame.part.player;
import com.home.commonBase.config.game.InfoCodeConfig;
import com.home.commonBase.config.game.InfoLogConfig;
import com.home.commonBase.constlist.system.PartCallPhaseType;
import com.home.commonBase.constlist.system.WorkType;
import com.home.commonBase.data.login.PlayerLoginToEachGameData;
import com.home.commonBase.data.login.PlayerSwitchGameData;
import com.home.commonBase.data.login.RePlayerLoginFromEachGameData;
import com.home.commonBase.data.system.AreaGlobalWorkData;
import com.home.commonBase.data.system.InfoLogData;
import com.home.commonBase.data.system.InfoLogWData;
import com.home.commonBase.data.system.PlayerToPlayerTCCWData;
import com.home.commonBase.data.system.PlayerWorkData;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.logic.LogicEntity;
import com.home.commonBase.part.player.clientData.AchievementClientPartData;
import com.home.commonBase.part.player.clientData.ActivityClientPartData;
import com.home.commonBase.part.player.clientData.BagClientPartData;
import com.home.commonBase.part.player.clientData.CharacterClientPartData;
import com.home.commonBase.part.player.clientData.EquipClientPartData;
import com.home.commonBase.part.player.clientData.FriendClientPartData;
import com.home.commonBase.part.player.clientData.FuncClientPartData;
import com.home.commonBase.part.player.clientData.GuideClientPartData;
import com.home.commonBase.part.player.clientData.MailClientPartData;
import com.home.commonBase.part.player.clientData.PetClientPartData;
import com.home.commonBase.part.player.clientData.QuestClientPartData;
import com.home.commonBase.part.player.clientData.RoleClientPartData;
import com.home.commonBase.part.player.clientData.SceneClientPartData;
import com.home.commonBase.part.player.clientData.SocialClientPartData;
import com.home.commonBase.part.player.clientData.SystemClientPartData;
import com.home.commonBase.part.player.clientData.TeamClientPartData;
import com.home.commonBase.part.player.clientData.UnionClientPartData;
import com.home.commonBase.part.player.data.AchievementPartData;
import com.home.commonBase.part.player.data.ActivityPartData;
import com.home.commonBase.part.player.data.BagPartData;
import com.home.commonBase.part.player.data.CharacterPartData;
import com.home.commonBase.part.player.data.EquipPartData;
import com.home.commonBase.part.player.data.FriendPartData;
import com.home.commonBase.part.player.data.FuncPartData;
import com.home.commonBase.part.player.data.GuidePartData;
import com.home.commonBase.part.player.data.MailPartData;
import com.home.commonBase.part.player.data.PetPartData;
import com.home.commonBase.part.player.data.QuestPartData;
import com.home.commonBase.part.player.data.RolePartData;
import com.home.commonBase.part.player.data.ScenePartData;
import com.home.commonBase.part.player.data.SocialPartData;
import com.home.commonBase.part.player.data.SystemPartData;
import com.home.commonBase.part.player.data.TeamPartData;
import com.home.commonBase.part.player.data.UnionPartData;
import com.home.commonBase.part.player.list.PlayerClientListData;
import com.home.commonBase.part.player.list.PlayerListData;
import com.home.commonBase.table.table.PlayerTable;
import com.home.commonGame.control.LogicExecutor;
import com.home.commonGame.global.GameC;
import com.home.commonGame.net.request.system.SendInfoCodeRequest;
import com.home.commonGame.net.request.system.SendInfoCodeWithArgsRequest;
import com.home.commonGame.net.request.system.SendInfoLogRequest;
import com.home.commonGame.net.request.system.SendWarningLogRequest;
import com.home.commonGame.part.player.base.PlayerBasePart;
import com.home.commonGame.part.player.part.AchievementPart;
import com.home.commonGame.part.player.part.ActivityPart;
import com.home.commonGame.part.player.part.BagPart;
import com.home.commonGame.part.player.part.CharacterPart;
import com.home.commonGame.part.player.part.EquipPart;
import com.home.commonGame.part.player.part.FriendPart;
import com.home.commonGame.part.player.part.FuncPart;
import com.home.commonGame.part.player.part.GuidePart;
import com.home.commonGame.part.player.part.MailPart;
import com.home.commonGame.part.player.part.PetPart;
import com.home.commonGame.part.player.part.QuestPart;
import com.home.commonGame.part.player.part.RolePart;
import com.home.commonGame.part.player.part.ScenePart;
import com.home.commonGame.part.player.part.SocialPart;
import com.home.commonGame.part.player.part.SystemPart;
import com.home.commonGame.part.player.part.TeamPart;
import com.home.commonGame.part.player.part.UnionPart;
import com.home.shine.constlist.SLogType;
import com.home.shine.control.DateControl;
import com.home.shine.control.ThreadControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.net.base.BaseRequest;
import com.home.shine.net.socket.BaseSocket;
import com.home.shine.support.collection.LongSet;
import com.home.shine.support.pool.StringBuilderPool;
import com.home.shine.utils.MathUtils;
import com.home.shine.utils.StringUtils;

/** 玩家数据主体(generated by shine) */
public class Player extends LogicEntity
{
	protected PlayerBasePart[] _list;
	
	/** 当前代码调用阶段 */
	private int _phase=PartCallPhaseType.None;
	
	private int _tenTimeTick=0;
	
	private int _secondTick=0;
	
	private int _minuteTick=0;
	
	/** 系统 */
	public SystemPart system;
	
	/** 通用功能 */
	public FuncPart func;
	
	/** 玩家 */
	public RolePart role;
	
	/** 场景 */
	public ScenePart scene;
	
	/** 角色 */
	public CharacterPart character;
	
	/** 背包 */
	public BagPart bag;
	
	/** 社交 */
	public SocialPart social;
	
	/** 邮件 */
	public MailPart mail;
	
	/** 任务 */
	public QuestPart quest;
	
	/** 装备 */
	public EquipPart equip;
	
	/** 好友 */
	public FriendPart friend;
	
	/** 组队 */
	public TeamPart team;
	
	/** 工会 */
	public UnionPart union;
	
	/** 成就 */
	public AchievementPart achievement;
	
	/** 活动 */
	public ActivityPart activity;
	
	/** 引导 */
	public GuidePart guide;
	
	/** 宠物 */
	public PetPart pet;
	
	/** 注册部件 */
	protected void registParts()
	{
		_list=new PlayerBasePart[17];
		int i=0;
		
		system=new SystemPart();
		system.setMe(this);
		_list[i++]=system;
		
		func=new FuncPart();
		func.setMe(this);
		_list[i++]=func;
		
		activity=new ActivityPart();
		activity.setMe(this);
		_list[i++]=activity;
		
		role=new RolePart();
		role.setMe(this);
		_list[i++]=role;
		
		scene=new ScenePart();
		scene.setMe(this);
		_list[i++]=scene;
		
		character=new CharacterPart();
		character.setMe(this);
		_list[i++]=character;
		
		social=new SocialPart();
		social.setMe(this);
		_list[i++]=social;
		
		bag=new BagPart();
		bag.setMe(this);
		_list[i++]=bag;
		
		mail=new MailPart();
		mail.setMe(this);
		_list[i++]=mail;
		
		quest=new QuestPart();
		quest.setMe(this);
		_list[i++]=quest;
		
		guide=new GuidePart();
		guide.setMe(this);
		_list[i++]=guide;
		
		friend=new FriendPart();
		friend.setMe(this);
		_list[i++]=friend;
		
		equip=new EquipPart();
		equip.setMe(this);
		_list[i++]=equip;
		
		team=new TeamPart();
		team.setMe(this);
		_list[i++]=team;
		
		union=new UnionPart();
		union.setMe(this);
		_list[i++]=union;
		
		achievement=new AchievementPart();
		achievement.setMe(this);
		_list[i++]=achievement;
		
		pet=new PetPart();
		pet.setMe(this);
		_list[i++]=pet;
		
	}
	
	/** 获取part列表 */
	public PlayerBasePart[] getPartList()
	{
		return _list;
	}
	
	/** 创建列表数据 */
	public PlayerListData createListData()
	{
		return new PlayerListData();
	}
	
	/** 构造客户端列表数据 */
	public PlayerClientListData makeClientListData()
	{
		PlayerClientListData listData=new PlayerClientListData();
		
		listData.system=(SystemClientPartData)this.system.makeClientPartData();
		listData.func=(FuncClientPartData)this.func.makeClientPartData();
		listData.activity=(ActivityClientPartData)this.activity.makeClientPartData();
		listData.role=(RoleClientPartData)this.role.makeClientPartData();
		listData.scene=(SceneClientPartData)this.scene.makeClientPartData();
		listData.character=(CharacterClientPartData)this.character.makeClientPartData();
		listData.social=(SocialClientPartData)this.social.makeClientPartData();
		listData.bag=(BagClientPartData)this.bag.makeClientPartData();
		listData.mail=(MailClientPartData)this.mail.makeClientPartData();
		listData.quest=(QuestClientPartData)this.quest.makeClientPartData();
		listData.guide=(GuideClientPartData)this.guide.makeClientPartData();
		listData.friend=(FriendClientPartData)this.friend.makeClientPartData();
		listData.equip=(EquipClientPartData)this.equip.makeClientPartData();
		listData.team=(TeamClientPartData)this.team.makeClientPartData();
		listData.union=(UnionClientPartData)this.union.makeClientPartData();
		listData.achievement=(AchievementClientPartData)this.achievement.makeClientPartData();
		listData.pet=(PetClientPartData)this.pet.makeClientPartData();
		
		return listData;
	}
	
	/** 推送完客户端列表数据后 */
	public void afterSendClientListData()
	{
		character.afterSendClientListData();
		role.afterSendClientListData();
		
		if(pet!=null)
		{
			pet.afterSendClientListData();
		}
	}
	
	/** 构造登陆其他逻辑服数据 */
	public PlayerLoginToEachGameData makeLoginEachGameData()
	{
		PlayerLoginToEachGameData data=BaseC.factory.createPlayerLoginToEachGameData();
		data.initDefault();
		
		PlayerBasePart[] list=_list;
		for(int vI=0,vLen=list.length;vI<vLen;++vI)
		{
			list[vI].makeLoginEachGameData(data);
		}
		
		return data;
	}
	
	/** 登陆前写入逻辑服返回数据(主线程) */
	public void beforeLoginForEachGame(RePlayerLoginFromEachGameData data)
	{
		PlayerBasePart[] list=_list;
		for(int vI=0,vLen=list.length;vI<vLen;++vI)
		{
			list[vI].beforeLoginForEachGame(data);
		}
	}
	
	/** 写切换数据 */
	public PlayerSwitchGameData writeSwitchGameData()
	{
		PlayerSwitchGameData data=BaseC.factory.createPlayerSwitchGameData();
		
		PlayerBasePart[] list=_list;
		for(int vI=0,vLen=list.length;vI<vLen;++vI)
		{
			list[vI].writeSwitchData(data);
		}
		
		return data;
	}
	
	/** 读切换数据(主线程) */
	public void readSwitchGameData(PlayerSwitchGameData data)
	{
		PlayerBasePart[] list=_list;
		for(int vI=0,vLen=list.length;vI<vLen;++vI)
		{
			list[vI].readSwitchData(data);
		}
	}
	
	/** 获取当前阶段 */
	public int getPhase()
	{
		return _phase;
	}
	
	/** 构造函数(只在new后调用一次,再次从池中取出不会调用) */
	public void construct()
	{
		_phase=PartCallPhaseType.Construct;
		
		registParts();
		
		PlayerBasePart[] list=_list;
		for(int vI=0,vLen=list.length;vI<vLen;++vI)
		{
			try
			{
				list[vI].construct();
			}
			catch(Exception e)
			{
				Ctrl.errorLog(e);
			}
		}
		
		_phase=PartCallPhaseType.None;
	}
	
	/** 初始化(创建后刚调用,与dispose成对) */
	public void init()
	{
		_phase=PartCallPhaseType.Init;
		
		PlayerBasePart[] list=_list;
		for(int vI=0,vLen=list.length;vI<vLen;++vI)
		{
			try
			{
				list[vI].init();
			}
			catch(Exception e)
			{
				Ctrl.errorLog(e);
			}
		}
		
		_phase=PartCallPhaseType.None;
	}
	
	/** 析构(回池前调用,与init成对) */
	public void dispose()
	{
		_phase=PartCallPhaseType.Dispose;
		
		PlayerBasePart[] list=_list;
		for(int vI=0,vLen=list.length;vI<vLen;++vI)
		{
			try
			{
				list[vI].dispose();
			}
			catch(Exception e)
			{
				Ctrl.errorLog(e);
			}
		}
		
		_phase=PartCallPhaseType.None;
	}
	
	/** 从库中读完数据后(做数据的补充解析)(onNewCreate后也会调用一次)(主线程) */
	public void afterReadData()
	{
		PlayerBasePart[] list=_list;
		for(int vI=0,vLen=list.length;vI<vLen;++vI)
		{
			list[vI].afterReadData();
		}
		
		for(int vI=0,vLen=list.length;vI<vLen;++vI)
		{
			list[vI].afterReadDataSecond();
		}
	}
	
	/** 每秒调用 */
	public void onSecond(int delay)
	{
		PlayerBasePart[] list=_list;
		for(int vI=0,vLen=list.length;vI<vLen;++vI)
		{
			list[vI].onSecond(delay);
		}
	}
	
	/** 每分调用 */
	public void onMinute()
	{
		PlayerBasePart[] list=_list;
		for(int vI=0,vLen=list.length;vI<vLen;++vI)
		{
			list[vI].onMinute();
		}
	}
	
	/** 每天调用(上线时如隔天也会调用,) */
	public void onDaily()
	{
		PlayerBasePart[] list=_list;
		for(int vI=0,vLen=list.length;vI<vLen;++vI)
		{
			list[vI].onDaily();
		}
	}
	
	/** 配置表更新后(配置替换) */
	public void onReloadConfig()
	{
		PlayerBasePart[] list=_list;
		for(int vI=0,vLen=list.length;vI<vLen;++vI)
		{
			list[vI].onReloadConfig();
		}
	}
	
	/** 新创建时(该主对象在服务器上第一次被创建时的动作(一生只一次),(只对数据赋值就好,自定义数据构造的部分写到afterReadData里,因为这个完事儿就会回调到)) */
	public void onNewCreate()
	{
		PlayerBasePart[] list=_list;
		for(int vI=0,vLen=list.length;vI<vLen;++vI)
		{
			list[vI].onNewCreate();
		}
	}
	
	/** 初次构造数据(只为new出Data,跟onCreate不是一回事) */
	public void newInitData()
	{
		PlayerBasePart[] list=_list;
		for(int vI=0,vLen=list.length;vI<vLen;++vI)
		{
			list[vI].newInitData();
		}
	}
	
	/** 功能开启(id:功能ID) */
	public void onFunctionOpen(int id)
	{
		PlayerBasePart[] list=_list;
		for(int vI=0,vLen=list.length;vI<vLen;++vI)
		{
			list[vI].onFunctionOpen(id);
		}
	}
	
	/** 功能关闭(id:功能ID) */
	public void onFunctionClose(int id)
	{
		PlayerBasePart[] list=_list;
		for(int vI=0,vLen=list.length;vI<vLen;++vI)
		{
			list[vI].onFunctionClose(id);
		}
	}
	
	/** 活动开启 */
	public void onActivityOpen(int id,boolean atTime)
	{
		PlayerBasePart[] list=_list;
		for(int vI=0,vLen=list.length;vI<vLen;++vI)
		{
			list[vI].onActivityOpen(id,atTime);
		}
	}
	
	/** 活动关闭 */
	public void onActivityClose(int id,boolean atTime)
	{
		PlayerBasePart[] list=_list;
		for(int vI=0,vLen=list.length;vI<vLen;++vI)
		{
			list[vI].onActivityClose(id,atTime);
		}
	}
	
	/** 活动重置 */
	public void onActivityReset(int id,boolean atTime)
	{
		PlayerBasePart[] list=_list;
		for(int vI=0,vLen=list.length;vI<vLen;++vI)
		{
			list[vI].onActivityReset(id,atTime);
		}
	}
	
	/** 登录前(每次角色即将登录时调用)(在主线程时刻,取全局数据用,beforeLoginOnMain在beforeEnterOnMain前) */
	public void beforeLoginOnMain()
	{
		PlayerBasePart[] list=_list;
		for(int vI=0,vLen=list.length;vI<vLen;++vI)
		{
			list[vI].beforeLoginOnMain();
		}
	}
	
	/** 登录前(每次角色即将登录时调用,beforeLogin在beforeEnter前) */
	public void beforeLogin()
	{
		PlayerBasePart[] list=_list;
		for(int vI=0,vLen=list.length;vI<vLen;++vI)
		{
			list[vI].beforeLogin();
		}
	}
	
	/** 登录 */
	public void login()
	{
		int len=_list.length;
		
		if(!system.getPartData().firstLogined)
		{
			system.getPartData().firstLogined=true;
			
			for(int i=0;i<len;++i)
			{
				_list[i].onFirstLogin();
			}
		}
		
		for(int i=0;i<len;++i)
		{
			_list[i].onLogin();
		}
	}
	
	/** 重连登录前 */
	public void beforeReconnectLogin()
	{
		PlayerBasePart[] list=_list;
		for(int vI=0,vLen=list.length;vI<vLen;++vI)
		{
			list[vI].beforeReconnectLogin();
		}
	}
	
	/** 重连登录 */
	public void onReconnectLogin()
	{
		PlayerBasePart[] list=_list;
		for(int vI=0,vLen=list.length;vI<vLen;++vI)
		{
			list[vI].onReconnectLogin();
		}
	}
	
	/** 进入前(登录和切到game都算)(在主线程时刻,取全局数据用) */
	public void beforeEnterOnMain()
	{
		PlayerBasePart[] list=_list;
		for(int vI=0,vLen=list.length;vI<vLen;++vI)
		{
			list[vI].beforeEnterOnMain();
		}
	}
	
	/** 进入前(登录和切到game都算) */
	public void beforeEnter()
	{
		PlayerBasePart[] list=_list;
		for(int vI=0,vLen=list.length;vI<vLen;++vI)
		{
			list[vI].beforeEnter();
		}
	}
	
	/** 进入时(登录和切到game都算) */
	public void onEnter()
	{
		PlayerBasePart[] list=_list;
		for(int vI=0,vLen=list.length;vI<vLen;++vI)
		{
			list[vI].onEnter();
		}
	}
	
	/** 登出(每次角色登出或切出时调用) */
	public void onLeave()
	{
		PlayerBasePart[] list=_list;
		for(int vI=0,vLen=list.length;vI<vLen;++vI)
		{
			list[vI].onLeave();
		}
	}
	
	/** 中心服下线(每次角色登出时调用) */
	public void onLogoutOnMain()
	{
		PlayerBasePart[] list=_list;
		for(int vI=0,vLen=list.length;vI<vLen;++vI)
		{
			list[vI].onLogoutOnMain();
		}
	}
	
	/** 登出时(逻辑线程/主线程) */
	public void onLogout()
	{
		//已登出过
		if(!system.getPartData().onLineMark)
			return;
		
		//记录离线时间
		system.recordLogoutDate();
		
		PlayerBasePart[] list=_list;
		for(int vI=0,vLen=list.length;vI<vLen;++vI)
		{
			list[vI].onLogout();
		}
	}
	
	/** 从列表数据读取 */
	public void readListData(PlayerListData listData)
	{
		this.system.setData(listData.system);
		this.func.setData(listData.func);
		this.activity.setData(listData.activity);
		this.role.setData(listData.role);
		this.scene.setData(listData.scene);
		this.character.setData(listData.character);
		this.social.setData(listData.social);
		this.bag.setData(listData.bag);
		this.mail.setData(listData.mail);
		this.quest.setData(listData.quest);
		this.guide.setData(listData.guide);
		this.friend.setData(listData.friend);
		this.equip.setData(listData.equip);
		this.team.setData(listData.team);
		this.union.setData(listData.union);
		this.achievement.setData(listData.achievement);
		this.pet.setData(listData.pet);
	}
	
	/** 写列表数据(深拷) */
	public void writeListData(PlayerListData listData)
	{
		listData.system=(SystemPartData)this.system.makePartData();
		listData.func=(FuncPartData)this.func.makePartData();
		listData.activity=(ActivityPartData)this.activity.makePartData();
		listData.role=(RolePartData)this.role.makePartData();
		listData.scene=(ScenePartData)this.scene.makePartData();
		listData.character=(CharacterPartData)this.character.makePartData();
		listData.social=(SocialPartData)this.social.makePartData();
		listData.bag=(BagPartData)this.bag.makePartData();
		listData.mail=(MailPartData)this.mail.makePartData();
		listData.quest=(QuestPartData)this.quest.makePartData();
		listData.guide=(GuidePartData)this.guide.makePartData();
		listData.friend=(FriendPartData)this.friend.makePartData();
		listData.equip=(EquipPartData)this.equip.makePartData();
		listData.team=(TeamPartData)this.team.makePartData();
		listData.union=(UnionPartData)this.union.makePartData();
		listData.achievement=(AchievementPartData)this.achievement.makePartData();
		listData.pet=(PetPartData)this.pet.makePartData();
	}
	
	/** 写列表数据(潜拷) */
	public void writeShadowListData(PlayerListData listData)
	{
		listData.system=(SystemPartData)this.system.makeShadowPartData();
		listData.func=(FuncPartData)this.func.makeShadowPartData();
		listData.activity=(ActivityPartData)this.activity.makeShadowPartData();
		listData.role=(RolePartData)this.role.makeShadowPartData();
		listData.scene=(ScenePartData)this.scene.makeShadowPartData();
		listData.character=(CharacterPartData)this.character.makeShadowPartData();
		listData.social=(SocialPartData)this.social.makeShadowPartData();
		listData.bag=(BagPartData)this.bag.makeShadowPartData();
		listData.mail=(MailPartData)this.mail.makeShadowPartData();
		listData.quest=(QuestPartData)this.quest.makeShadowPartData();
		listData.guide=(GuidePartData)this.guide.makeShadowPartData();
		listData.friend=(FriendPartData)this.friend.makeShadowPartData();
		listData.equip=(EquipPartData)this.equip.makeShadowPartData();
		listData.team=(TeamPartData)this.team.makeShadowPartData();
		listData.union=(UnionPartData)this.union.makeShadowPartData();
		listData.achievement=(AchievementPartData)this.achievement.makeShadowPartData();
		listData.pet=(PetPartData)this.pet.makeShadowPartData();
	}
	
	/** 检查新增模块 */
	public void checkNewAdd()
	{
		PlayerBasePart[] list=_list;
		for(int vI=0,vLen=list.length;vI<vLen;++vI)
		{
			list[vI].checkNewAdd();
		}
	}
	
	/** 删除角色  */
	public void onDeletePlayer()
	{
		PlayerBasePart[] list=_list;
		for(int vI=0,vLen=list.length;vI<vLen;++vI)
		{
			list[vI].onDeletePlayer();
		}
	}
	
	/** 写playerTable的其他部分(主键等) */
	public void writePlayerTableForOther(PlayerTable table)
	{
		role.writePlayerTableKey(table);
		system.writePlayerTable(table);
	}
	
	/** 读取player表的其他部分 */
	public void readPlayerTableForOther(PlayerTable table)
	{
		role.readPlayerTableKey(table);
	}
	
	/** 获取socket */
	public BaseSocket getSocket()
	{
		return system.socket;
	}
	
	/** 获取描述信息 */
	public String getInfo()
	{
		StringBuilder sb=StringBuilderPool.create();
		writeInfo(sb);
		return StringBuilderPool.releaseStr(sb);
	}
	
	/** 写描述信息 */
	@Override
	public void writeInfo(StringBuilder sb)
	{
		sb.append("uid:");
		sb.append(role.uid);
		sb.append(" playerID:");
		sb.append(role.playerID);
		sb.append(" 名字:");
		sb.append(role.name);
		sb.append(" 源服:");
		sb.append(role.sourceGameID);
		sb.append(" socketID:");
		sb.append(system.socket!=null ? system.socket.id : -1);
	}
	
	/** 每帧调用 */
	public void onFrame(int delay)
	{
		int time;
		
		if((time=(_tenTimeTick+=delay))>=ShineSetting.pieceTime)
		{
			_tenTimeTick=0;
			
			onPiece(time);
		}
		
		if((time=(_secondTick+=delay))>=1000)
		{
			_secondTick=0;
			
			onSecond(time);
			onSecondEx(time);
			
			if((++_minuteTick)>=60)
			{
				_minuteTick=0;
				
				onMinute();
			}
		}
	}
	
	/** 离线时间 */
	public void onOfflineTime(long delay)
	{
		PlayerBasePart[] list=_list;
		for(int vI=0,vLen=list.length;vI<vLen;++vI)
		{
			list[vI].onOfflineTime(delay);
		}
	}
	
	protected void onPiece(int delay)
	{
		role.onPiece(delay);
		character.onPiece(delay);
		bag.onPiece(delay);
	}
	
	protected void onSecondEx(int delay)
	{
		character.onSecondEx(delay);
	}
	
	/** 升级(升级多次也只调用一次) */
	public void onLevelUp(int oldLevel)
	{
		PlayerBasePart[] list=_list;
		for(int vI=0, vLen=list.length;vI<vLen;++vI)
		{
			list[vI].onLevelUp(oldLevel);
		}
	}
	
	/** 是否是当前游戏服 */
	public boolean isCurrentGame()
	{
		return role.sourceGameID==GameC.app.id;
	}
	
	/** 是否同一个源服 */
	public boolean isSameGame(long logicID)
	{
		return role.sourceGameID==GameC.main.getNowGameIDByLogicID(logicID);
	}
	
	/** 添加主线程执行(逻辑线程) */
	public void addMainFunc(Runnable func)
	{
		system.addMainFunc(func);
	}
	
	/** 添加待办事务(如是在主线程通过GetPlayer系列取出的Player然后addFunc,则一定会被执行) */
	public void addFunc(Runnable func)
	{
		system.addFunc(func);
	}
	
	/** 获取执行器 */
	public LogicExecutor getExecutor()
	{
		return system.getExecutor();
	}
	
	@Override
	protected void sendWarnLog(String str)
	{
		if(CommonSetting.needSendWarningLog)
		{
			send(SendWarningLogRequest.create(str));
		}
	}
	
	/** 推送消息 */
	public void send(BaseRequest request)
	{
		if(!system.getSocketReady())
			return;
		
		BaseSocket socket;
		
		if((socket=system.socket)==null)
			return;
		
		//离线中不发送
		if(CommonSetting.useOfflineGame && system.isClientOfflining())
			return;
		
		socket.send(request);
	}
	
	/** 推送信息码 */
	@Override
	public void sendInfoCode(int code)
	{
		send(SendInfoCodeRequest.create(code));
	}
	
	/** 推送信息码 */
	@Override
	public void sendInfoCode(int code,String...args)
	{
		send(SendInfoCodeWithArgsRequest.create(code,args));
	}
	
	/** 推送信息码到玩家(池线程) */
	public void sendInfoCodeToPlayer(long playerID,int code)
	{
		Player player;
		LogicExecutor executor;
		
		if((executor=getExecutor())!=null && (player=executor.getPlayer(playerID))!=null)
		{
			player.sendInfoCode(code);
			return;
		}
		
		addMainFunc(()->
		{
			GameC.main.sendInfoCodeToPlayer(playerID,code);
		});
	}
	
	/** 发送日志数据 */
	public void addInfoLog(InfoLogData data)
	{
		if(InfoLogConfig.get(data.id).needSave)
		{
			//存库
			system.recordInfoLog(data);
		}
		
		send(SendInfoLogRequest.create(data));
	}
	
	public void addSimpleInfoLog(int id)
	{
		InfoLogData data=new InfoLogData();
		data.id=id;
		addInfoLog(data);
	}
	
	/** 发送信息日志数据到角色 */
	public void addInfoLogToPlayer(long playerID,InfoLogData data)
	{
		if(InfoLogConfig.get(data.id).needSave)
		{
			InfoLogWData wData=new InfoLogWData();
			wData.data=data;
			addPlayerOfflineWork(playerID,wData);
			return;
		}
		else
		{
			SendInfoLogRequest.create(data).sendToPlayer(playerID);
		}
	}
	
	/** 添加角色事务 */
	private void addPlayerWork(int type,long playerID,PlayerWorkData data)
	{
		LogicExecutor executor=getExecutor();
		
		if(executor!=null)
		{
			Player player;
			//当前线程
			if((player=executor.getPlayer(playerID))!=null)
			{
				player.system.executeWork(data);
				return;
			}
		}
		
		GameC.main.addPlayerWork(type,playerID,data);
	}
	
	/** 添加角色事务组(池线程) */
	private void addPlayerWorkList(int type,LongSet list,PlayerWorkData data)
	{
		//TODO:可以补个在线的线程检测
		
		LogicExecutor executor=getExecutor();
		
		if(executor!=null)
		{
			list.forEachS(k->
			{
				Player player;
				//在当前线程
				if((player=executor.getPlayer(k))!=null)
				{
					player.system.executeWork(data);
					
					list.remove(k);
				}
			});
		}
		
		//还有
		if(!list.isEmpty())
		{
			ThreadControl.addMainFunc(()->
			{
				GameC.main.addPlayerWorkList(type,list,data);
			});
		}
	}
	
	/** 添加角色离线事务数据(池线程) */
	public void addPlayerOfflineWork(long playerID,PlayerWorkData data)
	{
		addPlayerWork(WorkType.PlayerOffline,playerID,data);
	}
	
	/** 添加一组角色离线事务数据(池线程) */
	public void addPlayerListOfflineWork(LongSet list,PlayerWorkData data)
	{
		addPlayerWorkList(WorkType.PlayerOffline,list,data);
	}
	
	/** 添加一个角色的在线事务数据(池线程)(只对在线角色有效,如目标离线则收不到) */
	public void addPlayerOnlineWork(long playerID,PlayerWorkData data)
	{
		addPlayerWork(WorkType.PlayerOnline,playerID,data);
	}
	
	/** 添加一组角色的在线事务数据(池线程)(只对在线角色有效,如目标离线则收不到) */
	public void addPlayerListOnlineWork(LongSet list,PlayerWorkData data)
	{
		addPlayerWorkList(WorkType.PlayerOnline,list,data);
	}
	
	/** 添加一个角色的立即事务数据(池线程) */
	public void addPlayerAbsWork(long playerID,PlayerWorkData data)
	{
		addPlayerWork(WorkType.PlayerAbs,playerID,data);
	}
	
	/** 添加一组角色的立即事务数据(池线程) */
	public void addPlayerListAbsWork(LongSet list,PlayerWorkData data)
	{
		addPlayerWorkList(WorkType.PlayerAbs,list,data);
	}
	
	/** 添加逻辑服事务(池线程) */
	public void addAreaGlobalWork(AreaGlobalWorkData data)
	{
		addMainFunc(()->
		{
			GameC.main.addAreaWork(data);
		});
	}
	
	/** 添加到玩家的tcc事务 */
	public void addPlayerTCCWork(long playerID,PlayerToPlayerTCCWData data)
	{
		data.sendPlayerID=role.playerID;
		addPlayerAbsWork(playerID,data);
	}
	
	@Override
	public long getTimeMillis()
	{
		if(CommonSetting.useOfflineGame && system.isClientOfflining())
		{
			return system.getPartData().lastClientOfflineTime;
		}

		return DateControl.getTimeMillis();
	}
	
	@Override
	public int randomInt(int range)
	{
		if(CommonSetting.needClientRandomSeeds && system.isClientOfflining())
		{
			return system.clientRandomInt(range);
		}
		
		return MathUtils.randomInt(range);
	}
	
	@Override
	public boolean randomProb(int prob,int max)
	{
		if(CommonSetting.needClientRandomSeeds && system.isClientOfflining())
		{
			return system.clientRandomProb(prob,max);
		}
		
		return MathUtils.randomProb(prob,max);
	}
	
	/** 角色战力变化 */
	protected void fightForceModified()
	{
		role.fightForceModified();
	}
	
	/** 添加流程步日志 */
	public void addFlowLog(int step)
	{
		system.addFlowLog(step);
	}
	
}
