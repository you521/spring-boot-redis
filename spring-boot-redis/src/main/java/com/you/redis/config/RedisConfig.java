package com.you.redis.config;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.you.redis.javautil.FastJsonRedisSerializer;


/**
 * redis配置类，包括redis作为缓存时，继承CachingConfigurerSupport,为了自定义生成KEY的策略,当然也可以不继承
 * @author Administrator
 *
 */


@Configuration
@EnableCaching   // 开启缓存
public class RedisConfig extends CachingConfigurerSupport
{
    //缓存过期时间
    private Duration timeToLive = Duration.ofDays(1);
    
    //缓存key的前缀
    private static final String redis_key_prefix = "test";
    
    /**
     * 
     * 当我们的数据存储到Redis的时候，我们的键（key）和值（value）都是通过Spring提供的Serializer序列化到数据库的
     * RedisTemplate默认使用的是JdkSerializationRedisSerializer，StringRedisTemplate默认使用的是StringRedisSerializer
     * Spring Data JPA为我们提供了下面的Serializer：
     * GenericToStringSerializer、Jackson2JsonRedisSerializer、JacksonJsonRedisSerializer、JdkSerializationRedisSerializer、OxmSerializer、StringRedisSerializer
     * 在此我们将自己配置RedisTemplate并定义Serializer
     *
     */
    @Bean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory){
        return generateRedisTemplate(connectionFactory);
    }
    
    /**
     * 
        * @Title: 自定义redisTemplate  
        * @Description: redisTemplate配置，redisTemplate模板提供给其他类对redis数据库进行操作  
        * @return RedisTemplate<String,Object>    返回类型  
        * @throws
     */
    private RedisTemplate<String, Object> generateRedisTemplate(RedisConnectionFactory redisConnectionFactory)
    {
        //定义redisTemplate对象
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        FastJsonRedisSerializer<Object> fastJsonRedisSerializer = new FastJsonRedisSerializer<>(Object.class);
        //设置Key的序列化采用StringRedisSerializer
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        //设置值的序列化采用FastJsonRedisSerializer
        redisTemplate.setValueSerializer(fastJsonRedisSerializer);
        redisTemplate.setHashValueSerializer(fastJsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
    
    /**
     *  注解 @Primary, 指定默认使用的bean, 在配置多个相同类型bean的时候使用
        * @Title: cacheManager  
        * @Description: 自定义默认缓存管理器  
        * @param @param factory
        * @param @return    参数  
        * @return CacheManager    返回类型  
        * @throws
     */
    @Bean(name = "defaultCacheManager")
    @Primary
    public CacheManager redisClusterCacheManager(RedisConnectionFactory redisConnectionFactory) {
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        FastJsonRedisSerializer<Object> fastJsonRedisSerializer = new FastJsonRedisSerializer<>(Object.class);
        //生成一个默认配置，通过redisCacheConfig对象即可对缓存进行自定义配置
        //通过缓存源码可以看出，生成的默认配置中，key永不过期，允许key使用前缀，允许缓存null值
        //键值对的序列化和反序列化也是默认的，RedisCacheConfiguration默认就是使用StringRedisSerializer序列化key，JdkSerializationRedisSerializer序列化value
        RedisCacheConfiguration redisCacheConfig = RedisCacheConfiguration.defaultCacheConfig();
        //对默认生成的redisCacheConfig配置进行修改
        redisCacheConfig.entryTtl(timeToLive)   //缓存时间一天
        //key采用String的序列化方式
        .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(stringRedisSerializer))
        //value序列化方式采用fastJson
       .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(fastJsonRedisSerializer))
        //不启用Redis的键前缀
        //.disableKeyPrefix()
        //设置key的前缀,注意：prefixKeysWith()方法在redis2.3的版本以后就被prefixCacheNameWith()或者computePrefixWith()代替了
        .prefixCacheNameWith(redis_key_prefix)
        //使用java1.8的lambda表达式
        //.computePrefixWith(cacheName -> "jiasy".concat("::").concat(cacheName).concat("::"))
        //不缓存空值
       .disableCachingNullValues();
        
        //设置一个初始化的缓存空间set集合
        Set<String> cacheNames =  new HashSet<>();
        //用户缓存空间
        cacheNames.add("user");
        //商品缓存空间
        cacheNames.add("goods");
       
        //针对 不同的缓存空间使用不同的配置
        Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
        //用户缓存空间
        configMap.put("user", redisCacheConfig);
        //商品缓存空间
        configMap.put("goods", redisCacheConfig.entryTtl(Duration.ofSeconds(120)));
   
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
