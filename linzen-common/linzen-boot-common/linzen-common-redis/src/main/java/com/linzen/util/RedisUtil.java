package com.linzen.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Slf4j
@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedisUtil {

    /**
     *  默认缓存时间 60S
     */
    public final static int CAHCETIME = 60;
    /**
     *  默认缓存时间 1hr
     */
    public final static int CAHCEHOUR = 60 * 60;
    /**
     *  默认缓存时间 1Day
     */
    public final static int CAHCEDAY = 60 * 60 * 24;
    /**
     *  默认缓存时间 1week
     */
    public final static int CAHCEWEEK = 60 * 60 * 24 * 7;
    /**
     *  默认缓存时间 1month
     */
    public final static int CAHCEMONTH = 60 * 60 * 24 * 7 * 30;
    /**
     *  默认缓存时间 1年
     */
    public final static int CAHCEYEAR = 60 * 60 * 24 * 7 * 30 * 12;

    private static long expiresIn = TimeUnit.SECONDS.toSeconds(CAHCEHOUR * 8);

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private CacheKeyUtil cacheKeyUtil;

    // =============================common============================

    /**
     * 获取所有的key
     *
     * @return
     */
    public Set<String> getAllVisiualKeys() {
        Set<String> allKey = new HashSet<>(16);
        allKey.addAll(Objects.requireNonNull(redisTemplate.keys("*" + cacheKeyUtil.getAllUser() + "*")));
        allKey.addAll(Objects.requireNonNull(redisTemplate.keys("*" + cacheKeyUtil.getCompanySelect() + "*")));
        allKey.addAll(Objects.requireNonNull(redisTemplate.keys("*" + cacheKeyUtil.getDictionary() + "*")));
        allKey.addAll(Objects.requireNonNull(redisTemplate.keys("*" + cacheKeyUtil.getDynamic() + "*")));
        allKey.addAll(Objects.requireNonNull(redisTemplate.keys("*" + cacheKeyUtil.getOrganizeList() + "*")));
        allKey.addAll(Objects.requireNonNull(redisTemplate.keys("*" + cacheKeyUtil.getPositionList() + "*")));
        allKey.addAll(Objects.requireNonNull(redisTemplate.keys("*" + cacheKeyUtil.getVisiualData() + "*")));
        return allKey;
    }

    /**
     * 获取所有的key
     *
     * @return
     */
    public Set<String> getAllKeys() {
        Set<String> keys = redisTemplate.keys("*");
        if(CollectionUtils.isNotEmpty(keys)) {
            //排除ID生成器的缓存记录， 如果删除在集群情况下ID生成器的机器编号可能会重复导致生成的ID重复
            keys = keys.stream().filter(s -> !s.startsWith(CacheKeyUtil.IDGENERATOR)).collect(Collectors.toSet());
        }
        return keys;
    }

    /**
     * 返回 key 的剩余的过期时间
     *
     * @param key
     * @return
     */
    public Long getLiveTime(String key) {
        return redisTemplate.getExpire(key);
    }

    /**
     * 删除指定key
     *
     * @param key
     */
    public void remove(String key) {
        if (exists(key)) {
            redisTemplate.delete(key);
        }
    }

    /**
     * 删除全部redis
     */
    public void removeAll() {
        Set<String> keys = getAllKeys();
        if (CollectionUtils.isNotEmpty(keys)) {
            //兼容Redis集群， 不同的KEY在不同服务器上不允许同时联合操作
            keys.forEach(k->redisTemplate.delete(k));
        }
    }

    /**
     * 判断缓存中是否有对应的value
     *
     * @param key
     * @return
     */
    public boolean exists(final String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 指定缓存失效时间
     *
     * @param key
     * @param time
     * @return
     */
    public void expire(String key, long time) {
        if (time > 0) {
            redisTemplate.expire(key, time, TimeUnit.SECONDS);
        } else {
            redisTemplate.expire(key, expiresIn, TimeUnit.SECONDS);
        }
    }

    /**
     * 插入缓存(无时间)
     *
     * @param key
     * @param object
     */
    public void insert(String key, Object object) {
        insert(key, object, 0);
    }

    /**
     * 插入缓存(有时间)
     *
     * @param key
     * @param object
     */
    public void insert(String key, Object object, long time) {
        if (object instanceof Map) {
            redisTemplate.opsForHash().putAll(key, (Map<String, String>) object);
        } else if (object instanceof List) {
            redisTemplate.opsForList().rightPushAll(key, (List)object);
        } else if (object instanceof Set) {
            redisTemplate.opsForSet().add(key, ((Set<?>) object).toArray());
        } else {
            redisTemplate.opsForValue().set(key, toJson(object));
        }
        expire(key, time);
    }

    /**
     * Object转成JSON数据
     */
    private String toJson(Object object) {
        if (object instanceof Integer || object instanceof Long || object instanceof Float ||
                object instanceof Double || object instanceof Boolean || object instanceof String) {
            return String.valueOf(object);
        }
        return JsonUtil.createObjectToString(object);
    }

    /**
     * 修改key
     *
     * @param oldKey 旧的key
     * @param newKey 新的key
     */
    public void rename(String oldKey, String newKey) {
        redisTemplate.rename(oldKey, newKey);
    }

    /**
     * 返回key存储的类型
     *
     * @param key
     */
    public String getType(String key) {
        return redisTemplate.type(key).code();
    }

    // ============================String=============================

    /**
     * 获取redis的String值
     *
     * @param key
     * @return
     */
    public Object getString(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // ============================Map=============================

    /**
     * 判断hash表中是否有对应的value
     *
     * @param hashId
     * @param key
     * @return
     */
    public boolean hasKey(String hashId, String key) {
        return redisTemplate.opsForHash().hasKey(hashId, key);
    }

    /**
     * 获取hashKey对应的所有键
     *
     * @param hashId 键
     */
    public List<String> getHashKeys(String hashId) {
        List<String> list = new ArrayList<>();
        Map<Object, Object> map = this.getMap(hashId);
        for (Object object : map.keySet()) {
            if (object instanceof String) {
                list.add(String.valueOf(object));
            }
        }
        return list;
    }

    /**
     * 获取hashKey对应的所有值
     *
     * @param hashId 键
     */
    public List<String> getHashValues(String hashId) {
        List<String> list = new ArrayList<>();
        Map<Object, Object> map = this.getMap(hashId);
        for (Object object : map.keySet()) {
            if (map.get(object) instanceof String) {
                list.add(String.valueOf(map.get(object)));
            }
        }
        return list;
    }

    /**
     * 查询具体map的值
     *
     * @param hashId
     * @param key
     * @return
     */
    public String getHashValues(String hashId, String key) {
        Object object = redisTemplate.opsForHash().get(hashId, key);
        if (object != null) {
            return String.valueOf(object);
        } else {
            return null;
        }
    }

    /**
     * 删除指定map的key
     *
     * @param key
     */
    public void removeHash(String hashId, String key) {
        if (hasKey(hashId, key)) {
            redisTemplate.opsForHash().delete(hashId, key);
        }
    }

    /**
     * 获取所有的map缓存
     *
     * @param key
     * @return
     */
    public <K,V> Map<K, V> getMap(String key) {
        return (Map<K, V>) redisTemplate.opsForHash().entries(key);
    }

    /**
     * 插入map的值
     *
     * @param hashId 主键id
     * @param key    map的key
     * @param value  map的值
     */
    public void insertHash(String hashId, String key, String value) {
        redisTemplate.opsForHash().put(hashId, key, value);
    }

    // ============================set=============================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return
     */
    public Set<Object> getSet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error(key, e);
            return null;
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return
     */
    public long getSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            log.error(key, e);
            return 0;
        }
    }

    // ===============================list=================================

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始 0 是第一个元素
     * @param end   结束 -1代表所有值
     * @return
     * @取出来的元素 总数 end-start+1
     */
    public List<Object> get(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            log.error(key, e);
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return
     */
    public long getListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            log.error(key, e);
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public Object getIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            log.error(key, e);
            return null;
        }
    }



    /**
     * 添加 Key 缓存
     *
     * @param key   String key
     * @param value Object
     * @param <T>   Value Type
     */
    public <T> void setKey(String key, final T value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 添加 Key 缓存,并设置失效时间
     *
     * @param key   String key
     * @param value Object
     * @param time  Time
     * @param <T>   Value Type
     */
    public <T> void setKey(String key, final T value, long time) {
        redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
    }

    /**
     * 添加 Key 缓存,并设置失效时间
     *
     * @param key   String key
     * @param value Object
     * @param time  Time
     * @param unit  TimeUnit
     * @param <T>   Value Type
     */
    public <T> void setKey(String key, final T value, long time, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, time, unit);
    }


    /**
     * 批量添加 Key 缓存
     *
     * @param valuesMap Map String:Object
     * @param <T>       Value Type
     */
    public <T> void setKey(Map<String, T> valuesMap) {
        redisTemplate.opsForValue().multiSet(valuesMap);
    }

    /**
     * 批量添加 Key 缓存,并设置失效时间
     *
     * @param valueMap     Map String:Object
     * @param expireMillis Map String:Long
     * @param <T>          Value Type
     */
    public <T> void setKey(Map<String, T> valueMap, Map<String, Long> expireMillis) {
        redisTemplate.opsForValue().multiSet(valueMap);
        setExpire(expireMillis);
    }

    /**
     * 获取 Key 缓存
     *
     * @param key String key
     * @param <T> Value Type
     * @return T
     */
    public <T> T getKey(final String key) {
        ValueOperations<String, T> operations = redisTemplate.opsForValue();
        return operations.get(key);
    }

    /**
     * 批量获取 Key 缓存值
     *
     * @param keys String key array
     * @param <T>  Value Type
     * @return T Array
     */
    public <T> List<T> getKey(List<String> keys) {
        ValueOperations<String, T> operations = redisTemplate.opsForValue();
        return operations.multiGet(keys);
    }

    /**
     * 判断 Key 是否存在
     *
     * @param key String key
     * @return boolean
     */
    public boolean hasKey(String key) {
        Boolean hasKey = redisTemplate.hasKey(key);
        return Boolean.TRUE.equals(hasKey);
    }

    /**
     * 批量获取 Key 缓存
     *
     * @param pattern Key pattern
     * @return Key Set
     */
    public Set<String> getKeys(final String pattern) {
        return redisTemplate.keys(pattern);
    }

    /**
     * 删除 Key 缓存
     *
     * @param key Key
     */
    public void del(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 批量删除 Key 缓存
     *
     * @param keys Key Array
     */
    public void delKeys(List<String> keys) {
        redisTemplate.delete(keys);
    }

    /**
     * 指定键值失效时间
     *
     * @param key  String key
     * @param time Time
     * @param unit TimeUnit
     */
    public void setExpire(String key, long time, TimeUnit unit) {
        if (time > 0) {
            redisTemplate.expire(key, time, unit);
        }
    }

    /**
     * 指定键值失效时间
     *
     * @param key  String key
     * @param time Time
     */
    public void setExpire(String key, long time) {
        if (time > 0) {
            redisTemplate.expire(key, time, TimeUnit.SECONDS);
        }
    }

    /**
     * 批量指定键值失效时间
     *
     * @param expireMillis Map String:Long
     */
    public void setExpire(Map<String, Long> expireMillis) {
        if (null != expireMillis && !expireMillis.isEmpty()) {
            StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
            redisTemplate.execute((RedisCallback<Object>) connection -> {
                expireMillis.forEach((key, expire) -> {
                    byte[] serialize = stringRedisSerializer.serialize(key);
                    if (null != serialize) {
                        connection.pExpire(serialize, expire);
                    }
                });
                return null;
            });
        }
    }

    /**
     * 指定键值在指定时间失效
     *
     * @param key  String key
     * @param date Date
     */
    public void setExpireAt(String key, Date date) {
        Date current = new Date();
        if (date.getTime() >= current.getTime()) {
            redisTemplate.expireAt(key, date);
        }
    }

    /**
     * 获取 Key 失效时间
     *
     * @param key  String key
     * @param unit TimeUnit
     * @return 剩余失效时长
     */
    public long getExpire(String key, TimeUnit unit) {
        Long expire = redisTemplate.getExpire(key, unit);
        if (null != expire) {
            return expire;
        }
        return 0L;
    }

}

