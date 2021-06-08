package com.you.redis.javautil;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 
    * @ClassName: RedisUtil  
    * @Description: redis工具类
    * @author you  
    * @date 2020年3月24日  
    *
 */

@Component
public class RedisUtil
{
    private static final Logger logger = LoggerFactory.getLogger(RedisUtil.class);
    
    /*
     * 引入采用的fastjson序列化策略的RedisTemplate
     */
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    //=============================common（公共方法）============================
    
    /**
     * 根据key指定缓存失效时间
     * @param key 键
     * @param time 时间(秒)
     * @return
     */
    public boolean expire(String key,long time){
        try 
        {
           if(time>0){
               redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            logger.error("根据某个key设置缓存时间失败，抛出异常：{}"+e);
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 根据key指定缓存失效时间
     * @param key 键
     * @param time 失效时间
     * @param unit 时间单位
     * @return
     */
    public boolean expire(String key,long time, TimeUnit unit){
        try 
        {
           if(time>0){
               redisTemplate.expire(key, time, unit);
            }
            return true;
        } catch (Exception e) {
            logger.error("根据某个key设置缓存时间失败，抛出异常：{}"+e);
            e.printStackTrace();
            return false;
        }
    }    
    
    /**
     * 移除指定key 的过期时间
     *
     * @param key
     * @return
     */
    public boolean persist(String key) {
        return redisTemplate.boundValueOps(key).persist();
    }

    /**
     * 根据key 获取过期时间
     * @param key 键 不能为null
     * @return 时间(秒) 返回-1代表为永久有效, 返回-2表示key值不存在
     */
    public long getExpire(String key) {
        long expire = 0;
        try
        {
            Object object = redisTemplate.getExpire(key,TimeUnit.SECONDS);
            // 三目运算法
            expire = object != null ? (long)object : 0;
        }
        catch (Exception e){
            logger.error("根据某个key获取过期时间失败，抛出异常：{}"+e);
            e.printStackTrace();
            return expire;
        }
        return expire;
    }
    
    /**
     * 判断key是否存在
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key){
        boolean has = false;
        try 
        {
            if (key != null)
            {
                Object object = redisTemplate.hasKey(key);
                has = object != null && (boolean) object;
            } else 
            {
                return has;
            }
        } catch (Exception e) {
            logger.error("判断key是否存在抛出异常：{}"+e);
            e.printStackTrace();
            return has;
        }
        return has;
    }
    
    /**
     * 删除缓存
     * @param key 可以传一个值 或多个
     */
    public void delete(String ... key){
        
    }
    
    /**
     * 修改 key 的名称
     * 
     * @param oldKey
     * @param newKey
     */
    public void rename(String oldKey, String newKey) {
        redisTemplate.rename(oldKey, newKey);
    }

    /**
     * 仅当 newkey 不存在时，将 oldKey 改名为 newkey
     * 
     * @param oldKey
     * @param newKey
     * @return
     */
    public Boolean renameIfAbsent(String oldKey, String newKey) {
        return redisTemplate.renameIfAbsent(oldKey, newKey);
    }

    /**
     * 返回 key 所储存的值的类型
     * 
     * @param key
     * @return
     */
    public DataType type(String key) {
        return redisTemplate.type(key);
    }
    
    // ============================String类型=============================
    //trings类型：一个String类型的value最大可以存储512M
    
    /**
     * 普通缓存获取数据
     * @param key 键
     * @return 值
     */
    public Object get(String key){
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }
    
    /**
     * 批量获取
     * 
     * @param keys
     * @return
     */
    public List<Object> multiGet(Collection<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }
    
    /**
     * 将给定 key 的值设为 value ，并返回 key 的旧值(old value)
     * 
     * @param key
     * @param value
     * @return
     */
    public Object getAndSet(String key, Object value) {
        return redisTemplate.opsForValue().getAndSet(key, value);
    }
    
    /**
     * 返回 key 中字符串值的子字符
     * @param key
     * @param start 子字符串的开始位置
     * @param end   子字符串的结束位置
     * @return
     */
    public Object getRange(String key, long start, long end) {
        return redisTemplate.opsForValue().get(key, start, end);
    }
    
    /**
     * 插入普通数据
     * @param key 数据的键
     * @param value 数据的值
     */
    public boolean set(String key, Object value){
        boolean setResult = false;
        try {
            if(key == null){
                logger.error("插入值出错: 键为空");
            }
            else if (value == null){
                logger.error("插入值出错: 值为空");
            }
            else {
                redisTemplate.opsForValue().set(key, value);
                setResult = true;
            }
        }
        catch (Exception e){
            logger.error("插入值出错:" +e);
            e.printStackTrace();
            return setResult;
        }
        return setResult;
    }
    
    /**
     * 只有在 key 不存在时设置 key 的值
     * 
     * @param key
     * @param value
     * @return 之前已经存在返回false,不存在返回true
     */
    public boolean setIfAbsent(String key, Object value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    /**
     * 只有在 key 不存在时设置 key 的值
     * 
     * @param key
     * @param value 
     * @param time 过期时间
     * @return 之前已经存在返回false,不存在返回true
     */
    public boolean setIfAbsent(String key, Object value, long time) {
        return redisTemplate.opsForValue().setIfAbsent(key, value, time, TimeUnit.SECONDS);
    }

    
    /**
     * 追加到末尾
     * 
     * @param key
     * @param value
     * @return
     */
    public Integer append(String key, String value) {
        return redisTemplate.opsForValue().append(key, value);
    }
    
    /**
     * 用 value 参数覆写给定 key 所储存的字符串值，从偏移量 offset 开始
     * 
     * @param key
     * @param value
     * @param offset
     *            从指定位置开始覆写
     */
    public void setRange(String key, Object value, long offset) {
        redisTemplate.opsForValue().set(key, value, offset);
    }

    
    /**
     * 普通缓存放入并设置时间
     * @param key 键
     * @param value 值
     * @param time 时间(秒) time要大于0 如果time小于等于0 将设置无限期
     *         时间单位:
     *            天:TimeUnit.DAYS 小时:TimeUnit.HOURS 分钟:TimeUnit.MINUTES
     *            秒:TimeUnit.SECONDS 毫秒:TimeUnit.MILLISECONDS
     * @return true成功 false 失败
     */
    public boolean set(String key, Object value, long time){
        boolean setResult = false;
        try {
            if(key == null){
                logger.error("插入值出错: 键为空");
            }
            else if (value == null){
                logger.error("插入值出错: 值为空");
            }
            else if (time <= 0){
                logger.error("插入值出错: 有效期小于0");
            }
            else {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
                setResult = true;
            }
        }
        catch (Exception e){
            logger.error("普通缓存放入并设置时间出错:" +e);
            e.printStackTrace();
            return setResult;
        }
        return setResult;
    }
    
    /**
     * 递增
     * @param key 键
     * @param by 要增加几(大于0)
     * @return
     */
    public long incr(String key, long delta){
        if(delta<0){
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     * @param key 键
     * @param by 要减少几(小于0)
     * @return
     */
    public long decr(String key, long delta){
        if(delta<0){
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }
    
    /**
     * 批量添加 key (重复的键会覆盖)
     *
     * @param keyAndValue
     */
    public void batchSet(Map<String, String> keyAndValue) {
        redisTemplate.opsForValue().multiSet(keyAndValue);
    }
 
    /**
     * 批量添加 key-value 只有在键不存在时,才添加
     * map 中只要有一个key存在,则全部不添加
     *
     * @param keyAndValue
     */
    public boolean batchSetIfAbsent(Map<String, String> keyAndValue) {
        boolean setResult = false;
        try {
            if(keyAndValue == null){
                logger.error("批量插入值出错: keyAndValue为空");
            }
            else {
                redisTemplate.opsForValue().multiSetIfAbsent(keyAndValue);
                setResult = true;
            }
        }
        catch (Exception e){
            logger.error("批量插入值出错:" +e);
            e.printStackTrace();
            return setResult;
        }
        return setResult;
    }
    
    /**
     * 获取字符串的长度
     * 
     * @param key
     * @return
     */
    public Long size(String key) {
        return redisTemplate.opsForValue().size(key);
    }

    
    //================================Map类型=================================
    //Redis中一个哈希存储一条数据，一个字段field则存储一条数据中的一个属性，字段值value是属性对应的值
    //每个哈希hash可存储2^32-1个键值对，约40多亿个。
    //Redis中的哈希散列类型与Java中的HashMap相似，都是一组键值对的集合，并且支持单独对其中一个键进行增删改查操作。
    //超时时间只能设置在键key上，单个域field不能设置过期时间。
    
    /**
     * 获取存储在哈希表中指定字段的值
     * @param key Redis的键
     * @param item 哈希的键
     * @return 值
     */
    public Object hget(String key,Object item){
        return redisTemplate.opsForHash().get(key, item);
    }
    
    /**
     * 获取hashKey对应的所有键值
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object,Object> hmget(String key){
        return redisTemplate.opsForHash().entries(key);
    }
    
    /**
     * 加入缓存
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public boolean hmset(String key, Map<Object, Object> map){
        boolean setResult = false;
        try {
            if(key == null){
                logger.error("存储哈希出错: 键为空");
            }
            else if(map == null){
                logger.error("存储哈希出错: 值为空");
            }
            else {
                this.redisTemplate.opsForHash().putAll(key, map);
                setResult = true;
            }
        }
        catch (Exception e){
            logger.error("存储哈希出错: Key:" + key, e);
        }
        return setResult;
    }
    
    /**
     * HashSet 并设置时间
     * @param key 键
     * @param map 对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public boolean hmset(String key, Map<String,Object> map, long time){
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if(time>0){
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 将哈希表key中的字段hashKey的值设置为value,若hashKey不存在则创建后赋值,若域hashKey已存在则不操作
     * @param key
     * @param hashKey
     * @param value
     * @return
     */
    public Boolean hPutIfAbsent(String key, Object hashKey, Object value) {
        return redisTemplate.opsForHash().putIfAbsent(key, hashKey, value);
    }
    
    /**
     * 将哈希表key中的字段hashKey的值设置为value,若hashKey不存在则创建后赋值,若域hashKey已存在则覆盖
     * @param key 键
     * @param hashKey 项
     * @param value 值
     * @return true 成功 false失败
     */
    public boolean hset(String key,Object hashKey,Object value) {
        try {
            redisTemplate.opsForHash().put(key, hashKey, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 向一张hash表中放入数据,如果不存在将创建，并设置时间
     * @param key 键
     * @param item 项
     * @param value 值
     * @param time 时间(秒)  注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public boolean hset(String key,Object item,Object value,long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if(time>0){
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除一个或多个哈希表字段
     * @param key 键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(String key, Object... item){
        redisTemplate.opsForHash().delete(key,item);
    }
    
    /**
     * 判断hash表中是否有该项的值
     * @param key 键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hExists(String key, Object item){
        return redisTemplate.opsForHash().hasKey(key, item);
    }
    
    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     * @param key 键
     * @param item 项
     * @param by 要增加几(大于0)
     * @return
     */
    public double hincr(String key, Object item,double by){
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递减
     * @param key 键
     * @param item 项
     * @param by 要减少记(小于0)
     * @return
     */
    public double hdecr(String key, Object item,double by){
        return redisTemplate.opsForHash().increment(key, item,-by);
    }
    
    /**
     * 获取所有哈希表中的字段
     * 
     * @param key
     * @return
     */
    public Set<Object> hKeys(String key) {
        return redisTemplate.opsForHash().keys(key);
    }

    /**
     * 获取哈希表中字段的数量
     * 
     * @param key
     * @return
     */
    public Long hSize(String key) {
        return redisTemplate.opsForHash().size(key);
    }

    /**
     * 获取哈希表中所有值
     * 
     * @param key
     * @return
     */
    public List<Object> hValues(String key) {
        return redisTemplate.opsForHash().values(key);
    }
    
    //============================set类型=============================
    //redis集合（set）类型和list列表类型类似，都可以用来存储多个字符串元素的集合。
    //但是和list不同的是set集合当中不允许重复的元素。而且set集合当中元素是没有顺序的，不存在元素下标。
    //redis的set类型是使用哈希表构造的，因此复杂度是O(1)，它支持集合内的增删改查，并且支持多个集合间的交集、并集、差集操作。
    //集合中最大的成员数为 232 - 1 ,每个集合可存储40多亿个成员。
    
    /**
     * 根据key获取Set中的所有值
     * @param key 键
     * @return
     */
    public Set<Object> sGet(String key){
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 随机获取集合中的一个元素
     *
     * @param key
     * @return
     */
    public Object sRandomMember(String key) {
        return redisTemplate.opsForSet().randomMember(key);
    }
    
    /**
     * 随机获取集合中count个元素
     *
     * @param key
     * @param count
     * @return
     */
    public List<Object> sRandomMembers(String key, long count) {
        return redisTemplate.opsForSet().randomMembers(key, count);
    }
    
    /**
     * 随机获取集合中count个元素并且去除重复的
     *
     * @param key
     * @param count
     * @return
     */
    public Set<Object> sDistinctRandomMembers(String key, long count) {
        return redisTemplate.opsForSet().distinctRandomMembers(key, count);
    }
    
    /**
     * 根据value从一个set中查询,是否存在
     * @param key 键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sHasKey(String key,Object value){
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 将数据放入set缓存
     * @param key 键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSet(String key, Object...values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 将set数据放入缓存，设置时间
     * @param key 键
     * @param time 时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSetAndTime(String key,long time,Object...values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if(time>0) 
            expire(key, time);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     * @param key 键
     * @return
     */
    public long sGetSetSize(String key){
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    /**
     * 移除值为value的
     * @param key 键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public long setRemove(String key, Object ...values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    /**
     * 移除集合中的一个随机元素，并将该随机元素返回
     *
     * @param key
     * @return
     */
    public Object sPop(String key) {
        return redisTemplate.opsForSet().pop(key);
    }
    
    /**
     * 移除集合中的多个随机元素，并将该多个随机元素返回
     *
     * @param key
     * @param count
     * @return
     */
    public Object sPop(String key,long count) {
        return redisTemplate.opsForSet().pop(key, count);
    }
    
    /**
     * 将元素value从一个集合移到另一个集合
     *
     * @param key
     * @param value
     * @param destKey
     * @return
     */
    public Boolean sMove(String key, Object value, String destKey) {
        return redisTemplate.opsForSet().move(key, value, destKey);
    }
    
    //===============================list类型=================================
    
    /**
     * 通过索引 获取list中的值
     * @param key 键
     * @param index 索引  index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public Object lGetIndex(String key,long index){
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取list缓存的内容
     * @param key 键
     * @param start 开始位置, 0是开始位置
     * @param end 结束位置, -1返回所有
     *        0 到 -1代表所有值
     * @return
     */
    public List<Object> lGet(String key,long start, long end){
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 获取list缓存的长度
     * @param key 键
     * @return
     */
    public long lGetListSize(String key){
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    /**
     * list放入缓存，存储在list尾部
     * @param key 键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * list放入缓存，存储在list尾部
     * @param key 键
     * @param value 值
     * @param time 时间(秒)
     * @return
     */
    public boolean lSet(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) expire(key, time);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * list批量放入缓存，存储在list尾部
     * @param key 键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, Collection<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list批量放入缓存，存储在list尾部
     * @param key 键
     * @param value 值
     * @param time 时间(秒)
     * @return
     */
    public boolean lSet(String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) expire(key, time);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 当key对应的list列表存在时，才添加value数据，如果list列表不存，则不添加数据
     * 
     * @param key
     * @param value
     * @return
     */
    public boolean lRightPushIfPresent(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPushIfPresent(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 当key对应的list列表存在时，才添加value数据，如果list列表不存，则不添加数据
     * 
     * @param key
     * @param value
     * @param time 时间(秒)
     * @return
     */
    public boolean lRightPushIfPresent(String key, Object value,long time) {
        try {
            redisTemplate.opsForList().rightPushIfPresent(key, value);
            if (time > 0) expire(key, time);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 在pivot元素的右边添加值
     * 
     * @param key
     * @param pivot
     * @param value
     * @return
     */
    public boolean lRightPush(String key, String pivot, String value) {
        try {
            redisTemplate.opsForList().rightPush(key, pivot, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 根据索引修改list中的某条数据
     * @param key 键
     * @param index 索引
     * @param value 值
     * @return
     */
    public boolean lUpdateIndex(String key, long index,Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 移除并获取列表最后一个元素
     * 
     * @param key
     * @return 删除的元素
     */
    public Object lRightPop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }
    
    /**
     * 移除N个值为value
     * @param key 键
     * @param count 移除多少个
     * @param value 值
     *            count=0, 删除所有值等于value的元素; count>0, 从头部开始删除第一个值等于value的元素;
     *            count<0, 从尾部开始删除第一个值等于value的元素;
     * @return 移除的个数
     */
    public long lRemove(String key,long count,Object value) {
        try {
            Long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
