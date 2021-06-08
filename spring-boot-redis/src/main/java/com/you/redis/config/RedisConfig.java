package com.you.redis.config;




import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import com.you.redis.javautil.FastJsonRedisSerializer;


/**
 * redis配置类
 * @author Administrator
 *
 */


@Configuration
public class RedisConfig
{
    private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);
    
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
        logger.info("<------------------------RedisTemplate加载开始--------------------------->");
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
        logger.info("<------------------------RedisTemplate加载结束--------------------------->");
        return redisTemplate;
    }
}
