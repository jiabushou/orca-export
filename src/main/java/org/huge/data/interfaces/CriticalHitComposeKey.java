package org.huge.data.interfaces;

/**
 * 暴击RedisKey生成接口
 * @param <T> 暴击合成key的类型
 */
public interface CriticalHitComposeKey<T> {

    String getCriticalHitComposeKey(T t);
}
