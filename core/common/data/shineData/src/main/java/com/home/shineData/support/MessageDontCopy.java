package com.home.shineData.support;

/** 协议推送不复制(如果参数是临时的,用此注解可免除协议数据的深拷贝,提升性能)(含有自定义数据结构的协议才有意义) */
public @interface MessageDontCopy
{

}
