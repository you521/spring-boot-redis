package com.you.redis.config;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.you.redis.Enum.RedisCacheConfigEnum;
import com.you.redis.javautil.FastJsonRedisSerializer;

/**
 * redis缓存配置类，继承CachingConfigurerSupport,为了自定义生成KEY的策略,当然也可以不继承
 * @author Administrator
 *
 */

@Configuration
public class CacheConfig extends CachingConfigurerSupport
{
     private static final Logger logger = LoggerFactory.getLogger(CacheConfig.class);
     
   //缓存key的前缀
     private static final String redis_key_prefix = "redis_cache_test";
     
     //分隔符
     private static final String separator = "::";
     
     
     /**
      *  注解 @Primary, 指定默认使用的bean, 在配置多个相同类型bean的时候使用
         * @Title: cacheManager  
         * @Description: 自定义缓存管理器，注入RedisCacheManager，不使用springboot默认提供的缓存管理器
         * @param @param factory
         * @param @return    参数  
         * @return CacheManager    返回类型  
         * @throws
      */
     @Bean(name = "defaultCacheManager")
     @Primary
     public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
         logger.info("<----------------------CacheManager缓存管理器加载开始----------------------->");
         StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
         FastJsonRedisSerializer<Object> fastJsonRedisSerializer = new FastJsonRedisSerializer<>(Object.class);
         //生成一个默认配置，通过redisCacheConfig对象即可对缓存进行自定义配置
         //通过缓存源码可以看出，生成的默认配置中，key永不过期，允许key使用前缀，允许缓存null值
         //键值对的序列化和反序列化也是默认的，RedisCacheConfiguration默认就是使用StringRedisSerializer序列化key，JdkSerializationRedisSerializer序列化value
         RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
         //对默认的redisCacheConfig进行更改
         RedisCacheConfiguration redisCacheConfig = config
         //key采用String的序列化方式
         .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(stringRedisSerializer))
         //value序列化方式采用fastJson
         .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(fastJsonRedisSerializer))
         //不启用Redis的键前缀
         //.disableKeyPrefix()
         //设置key的前缀,注意：prefixKeysWith()方法在redis2.3的版本以后就被prefixCacheNameWith()或者computePrefixWith()代替了
         .prefixCacheNameWith(redis_key_prefix+separator)
         //使用java1.8的lambda表达式
         //.computePrefixWith(cacheName -> redis_key_prefix.concat("::").concat(cacheName).concat("::"))
         //不缓存空值
        .disableCachingNullValues();
         
         //设置一个初始化的缓存空间set集合
         Set<String> cacheNames =  new HashSet<>();
         //用户缓存空间
         cacheNames.add(RedisCacheConfigEnum.Cache_Name_USER.getCacheName());
         //商品缓存空间
         cacheNames.add(RedisCacheConfigEnum.Cache_Name_GOODS.getCacheName());
        
         //针对不同的缓存空间使用不同的配置
         Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
         //用户缓存空间
         configMap.put(RedisCacheConfigEnum.Cache_Name_USER.getCacheName(), redisCacheConfig.entryTtl(RedisCacheConfigEnum.Cache_Name_USER.getTimeToLive()));
         //商品缓存空间
         configMap.put(RedisCacheConfigEnum.Cache_Name_GOODS.getCacheName(), redisCacheConfig.entryTtl(RedisCacheConfigEnum.Cache_Name_GOODS.getTimeToLive()));
    
         //使用自定义的缓存配置初始化一个cacheManager
         RedisCacheManager cacheManager = RedisCacheManager.builder(redisConnectionFactory) 
                 .initialCacheNames(cacheNames)  // 注意这两句的调用顺序，一定要先调用该方法设置初始化的缓存名，再初始化相关的配置
                 //初始化自定义相关配置
                 .withInitialCacheConfigurations(configMap) 
                 //将缓存的操作纳入到事务管理中,即回滚事务会同步回滚缓存
                 .transactionAware()
                 //不允许添加除上述定义之外的缓存名称
                 .disableCreateOnMissingCache()
                 .build();
         logger.info("<----------------------CacheManager缓存管理器加载结束----------------------->");
         return cacheManager;
     }
     
     
     /**
      * 自定义缓存key的生成策略：包名+方法名+参数列表
      */
     @Bean
     public KeyGenerator keyGenerator() {
         return new KeyGenerator() {
             @Override
             public Object generate(Object target, Method method, Object... params) {
                 StringBuffer sb = new StringBuffer();
                 sb.append(target.getClass().getName());  // 类目
                 sb.append(method.getName());   // 方法名
                 for (Object obj : params) {
                     sb.append(obj.toString());  // 参数名
                 }
                 return sb.toString();
             }
         };
     }
}
