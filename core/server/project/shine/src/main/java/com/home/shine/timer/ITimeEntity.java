package com.home.shine.timer;

import com.home.shine.control.DateControl;

/** 时间主体接口 */
public interface ITimeEntity
{
  /** 获取当前毫秒时间戳 */
  default long getTimeMillis()
  {
    return DateControl.getTimeMillis();
  }

  /** 获取当前秒时间戳 */
  default long getTimeSeconds()
  {
    return getTimeMillis()/1000;
  }
}
