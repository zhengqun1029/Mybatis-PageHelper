package com.github.pagehelper.component;

import com.github.pagehelper.annotation.Cache;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import org.apache.ibatis.mapping.MappedStatement;

/**
 * @author zhenghanbin
 * @description
 * @date Created in 18:19 2019/8/26
 */
public class CacheProcessor {

  private static CacheProcessor cacheProcessor;

  private String countSuffix = "_COUNT";

  private HashSet<String> autoCacheSet;

  public static CacheProcessor getInstance() {
    if (cacheProcessor == null) {
      synchronized (CacheProcessor.class) {
        if (cacheProcessor == null) {
          cacheProcessor = new CacheProcessor();
        }
      }
    }
    return cacheProcessor;
  }


  /**
   * 初始化方法 加载需要缓存的mapper方法
   */
  public synchronized void init(MappedStatement ms) {
    if (autoCacheSet == null) {
      autoCacheSet = new HashSet<String>();
      Collection<Class<?>> mappers = ms.getConfiguration().getMapperRegistry().getMappers();
      if (!mappers.isEmpty()) {
        for (Class mapper : mappers) {
          Method[] methods = mapper.getMethods();
          if (methods != null && methods.length > 0) {
            for (Method method : methods) {
              if (method.getAnnotation(Cache.class) != null) {
                autoCacheSet.add(mapper.getCanonicalName() + "." + method.getName() + countSuffix);
              }
            }
          }
        }
      }
    }
  }

  /**
   * 判断方法是否需要缓存总数
   */
  public boolean isCache(String id) {
    return autoCacheSet.contains(id);
  }
}
