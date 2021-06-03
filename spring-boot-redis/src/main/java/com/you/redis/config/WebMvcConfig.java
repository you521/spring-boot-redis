package com.you.redis.config;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;

/**
 * @ClassName: WebMvcConfig  
 * @Description: spring mvc的拓展配置文件
 * @author Administrator
 * @date 2021.06.03
 */

@Configuration
public class WebMvcConfig implements WebMvcConfigurer
{
    
    private static final Logger logger = LoggerFactory.getLogger(WebMvcConfig.class);

    /**
      * 添加默认的消息转换器，删除项目自带的jackson消息转换器，添加fastjson消息转换器
     */
    
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // for循环遍历删除jackson转换器
        for(int i = converters.size() - 1; i >= 0; i--) {
            if (converters.get(i) instanceof MappingJackson2HttpMessageConverter){
              converters.remove(i);
            }
        }
        // 创建fastjson消息转换器
        FastJsonHttpMessageConverter fastJsonConverter = new FastJsonHttpMessageConverter();
        // 创建fastjson全局配置
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(
                //结果是否式化,默认为false
                SerializerFeature.PrettyFormat,
                //List字段如果为null,输出为 [ ],而非null
                SerializerFeature.WriteNullListAsEmpty,
                //字符类型字段如果为null,输出为 " " ,而非null
                SerializerFeature.WriteNullStringAsEmpty,
                //Boolean字段如果为null,输出为false,而非null
                SerializerFeature.WriteNullBooleanAsFalse,
                //消除对同一对象循环引用的问题，默认为false（如果不配置有可能会进入死循环）
                SerializerFeature.DisableCircularReferenceDetect,
                //将数值类型字段的空值输出为0
                SerializerFeature.WriteNullNumberAsZero,
                //是否输出值为null的字段,默认为false
                SerializerFeature.WriteMapNullValue
                );
        // fastjson 编码配置
        fastJsonConfig.setCharset(Charset.forName("UTF-8"));
        // 设置全局的日期格式
        //fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        //在convert中添加配置信息
        fastJsonConverter.setFastJsonConfig(fastJsonConfig);
        //处理中文乱码问题
        List<MediaType> fastMediaTypes = new ArrayList<MediaType>();
        //添加支持的MediaTypes;不添加时默认为*/*,也就是默认支持全部
        //但是MappingJackson2HttpMessageConverter里面支持的MediaTypes为application/json
        //参考它的做法, fastjson也只添加application/json的MediaType
        fastMediaTypes.add(MediaType.APPLICATION_JSON);
        fastJsonConverter.setSupportedMediaTypes(fastMediaTypes);
        //将fastjson添加到转换器列表内
        converters.add(fastJsonConverter);
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (HttpMessageConverter<?> messageConverter : converters) {
            logger.info("项目中配置的消息转换器列表为：{}",messageConverter);
        }
    }
    
}
