package com.you.redis.Enum;

import java.time.Duration;

/**
 * 定义一个关于redis缓存配置的枚举类，有两个常量，分别是缓存空间名称和缓存过期时间
 * @author Administrator
 *
 */
public enum RedisCacheConfigEnum
{
    //定义缓存空间名称为user，缓存过期时间为一分钟
    Cache_Name_USER("user",Duration.ofMinutes(1)),
    //定义缓存空间名称为goods，缓存过期时间为五分钟
    Cache_Name_GOODS("goods",Duration.ofSeconds(300));
    
    
    //缓存空间名称
    private String cacheName;
    //缓存过期时间
    private Duration timeToLive;
    
    /**
     * 有参构造函数
     */
    RedisCacheConfigEnum(String cacheName,Duration timeToLive){
        this.cacheName=cacheName;
        this.timeToLive=timeToLive;
    }
    
    public String getCacheName()
    {
    
        return cacheName;
    }

    public void setCacheName(String cacheName)
    {
    
        this.cacheName = cacheName;
    }

    public Duration getTimeToLive()
    {
    
        return timeToLive;
    }

    public void setTimeToLive(Duration timeToLive)
    {
    
        this.timeToLive = timeToLive;
    }
}
