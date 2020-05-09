package com.home.commonData.data.system;

/** 用户事务(login服执行,要求事务本身幂等，因为login上目前没做事务序号存储) */
public class UserWorkDO extends WorkDO
{
	/** uid */
	String uid;
	/** userID */
	long userID;
}
