package com.you.redis.javautil;

import java.nio.charset.Charset;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * 实现redis缓存对象时，使用fastjson自定义序列化和反序列化器来实现
 * @author Administrator
 * @date 2021.06.01
 * @param <T>
 */

public class FastJsonRedisSerializer<T> implements RedisSerializer<T>
{
    
    // 定义编码格式
    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    private Class<T> clazz;
    
    // static代码块
    static {
        //如果反序列化时，遇到autoType is not support提示错误， 这两句代码都可以解决反序列化问题
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
        //ParserConfig.getGlobalInstance().addAccept("com.you.redis.entity");
    }
    
    //显示构造函数
    public FastJsonRedisSerializer(Class<T> clazz) {
        super();
        this.clazz = clazz;
    }
    
    /**
     * 序列化方法
     */
    @Override
    public byte[] serialize(T t) throws SerializationException
    {
        if (t == null)
        {
            return new byte[0];
        }
        return JSON.toJSONString(t, SerializerFeature.WriteClassName).getBytes(DEFAULT_CHARSET);
    }

    /**
     * 反序列化方法
     */
    @Override
    public T deserialize(byte[] bytes) throws SerializationException
    {
        if (null == bytes || bytes.length <= 0){
            return null;
        }
        String str = new String(bytes,DEFAULT_CHARSET);
        return (T) JSON.parseObject(str,clazz);
    }

}
