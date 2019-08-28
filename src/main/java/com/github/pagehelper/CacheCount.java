package com.github.pagehelper;

import com.github.pagehelper.util.StringUtil;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhenghanbin
 * @description
 * @date Created in 18:18 2019/8/19
 */
public class CacheCount {

  private static Long time = 15000L;
  private static ConcurrentHashMap<Integer, CacheInfo> cacheMap = new ConcurrentHashMap();


  public static void setProperties(Properties properties) {
    String timeStr = properties.getProperty("count-cache-timeout");
    time = StringUtil.isNotEmpty(timeStr) ? Long.parseLong(timeStr) : time;
  }

  public static void setCacheMap(Integer key, Long count) {
    synchronized (key) {
      if (cacheMap.contains(key)) {
        if (isTimeOut(key)) {
          CacheInfo cacheInfo = cacheMap.get(key);
          cacheInfo.setCount(count);
          cacheInfo.setTime(System.currentTimeMillis());
        }
      } else {
          CacheInfo cacheInfo = new CacheInfo(count, System.currentTimeMillis());
          cacheMap.put(key, cacheInfo);
      }
    }
  }


  public static boolean isTimeOut(Integer key) {
    CacheInfo cacheInfo = cacheMap.get(key);
    if (System.currentTimeMillis() - cacheInfo.getTime() > time) {
      return true;
    }
    return false;
  }

  public static boolean contains(Integer key) {
    return cacheMap.containsKey(key);
  }


  public static Long getCount(Integer key) {
    return cacheMap.get(key).getCount();
  }

  static class CacheInfo {

    private Long count;
    private Long time;

    public CacheInfo() {
    }

    public CacheInfo(Long count, Long time) {
      this.count = count;
      this.time = time;
    }

    public Long getCount() {
      return count;
    }

    public void setCount(Long count) {
      this.count = count;
    }

    public Long getTime() {
      return time;
    }

    public void setTime(Long time) {
      this.time = time;
    }
  }
}
