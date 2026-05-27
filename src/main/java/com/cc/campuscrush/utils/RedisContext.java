package com.cc.campuscrush.utils;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 【RedisContext】工具类
 * &lt;p&gt;核心功能：RedisTemplate操作封装工具，提供Value（存取/过期/批量删除）、List（左推/范围查询/裁剪）、Hash（读写/自增/键集）、Set（添加/移除/成员判断）等常用数据结构操作&lt;/p&gt;
 * &lt;p&gt;使用场景：供各Service层统一操作Redis缓存，封装底层RedisTemplate API简化调用，支持字符串、列表、哈希、集合四种数据结构，避免业务代码直接操作原生Redis命令&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Component
public class RedisContext {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    // ==================== Value操作 ====================

    /**
     * 设置Key-Value缓存（永不过期）
     * @param key   Redis Key
     * @param value 缓存值（通过RedisTemplate序列化器转JSON）
     */
    public void set(String key, Object value) {
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        operations.set(key, value);
    }

    /**
     * 设置Key-Value缓存并指定过期时间
     * @param key     Redis Key
     * @param value   缓存值
     * @param timeout 过期时长
     * @param unit    时间单位
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        operations.set(key, value, timeout, unit);
    }

    /**
     * 获取Key对应的缓存值
     * 异常场景：Key不存在返回null
     * @param key Redis Key
     * @return 缓存值，Key不存在返回null
     */
    public Object get(String key) {
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        return operations.get(key);
    }

    /**
     * 删除指定Key的缓存
     * @param key Redis Key
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 设置String类型缓存并指定过期秒数
     * @param key            Redis Key
     * @param value          String类型缓存值
     * @param timeoutSeconds 过期秒数
     */
    public void setEx(String key, String value, long timeoutSeconds) {
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        operations.set(key, value, timeoutSeconds, TimeUnit.SECONDS);
    }

    /**
     * 按通配符模式批量删除Key
     * 业务逻辑：扫描匹配pattern的所有Key → 非空时批量删除
     * 异常场景：无匹配Key时不执行删除，不会报错
     * @param pattern Key匹配模式（如"Campus-Crush:post:img:*"）
     */
    public void deletePattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    // ==================== List操作 ====================

    /**
     * 列表左侧插入单个元素
     * @param key   Redis List Key
     * @param value 待插入的值
     */
    public void leftPush(String key, Object value) {
        ListOperations<String, Object> operations = redisTemplate.opsForList();
        operations.leftPush(key, value);
    }

    /**
     * 列表左侧批量插入多个元素
     * @param key    Redis List Key
     * @param values 待插入的值列表
     */
    public void leftPushAll(String key, List<Object> values) {
        ListOperations<String, Object> operations = redisTemplate.opsForList();
        operations.leftPushAll(key, values);
    }

    /**
     * 查询列表指定范围的元素
     * @param key   Redis List Key
     * @param start 起始索引（包含，0为第一个）
     * @param end   结束索引（包含，-1表示最后一个）
     * @return 指定范围内的元素列表，Key不存在返回空列表
     */
    public List<Object> range(String key, long start, long end) {
        ListOperations<String, Object> operations = redisTemplate.opsForList();
        return operations.range(key, start, end);
    }

    /**
     * 裁剪列表，只保留指定范围的元素
     * @param key   Redis List Key
     * @param start 保留的起始索引
     * @param end   保留的结束索引
     */
    public void trim(String key, long start, long end) {
        ListOperations<String, Object> operations = redisTemplate.opsForList();
        operations.trim(key, start, end);
    }

    /**
     * 获取列表长度
     * @param key Redis List Key
     * @return 列表元素个数，Key不存在返回0
     */
    public long size(String key) {
        ListOperations<String, Object> operations = redisTemplate.opsForList();
        return operations.size(key);
    }

    // ==================== Hash操作 ====================

    /**
     * Hash数据结构插入单个Field-Value
     * @param key   Redis Hash Key
     * @param field Hash字段名
     * @param value Hash字段值
     */
    public void putHash(String key, String field, Object value) {
        HashOperations<String, String, Object> operations = redisTemplate.opsForHash();
        operations.put(key, field, value);
    }

    /**
     * 获取Hash中指定Field的值
     * @param key   Redis Hash Key
     * @param field Hash字段名
     * @return 字段值，Key或Field不存在返回null
     */
    public Object getHash(String key, String field) {
        HashOperations<String, String, Object> operations = redisTemplate.opsForHash();
        return operations.get(key, field);
    }

    /**
     * 获取Hash中所有Field和Value
     * @param key Redis Hash Key
     * @return HashMap，Key不存在返回空Map
     */
    public Map<String, Object> getHashAll(String key) {
        HashOperations<String, String, Object> operations = redisTemplate.opsForHash();
        return operations.entries(key);
    }

    /**
     * Hash字段原子自增（delta可为负数实现自减）
     * @param key   Redis Hash Key
     * @param field Hash字段名
     * @param delta 自增步长（负数表示自减）
     */
    public void incrementHash(String key, String field, long delta) {
        HashOperations<String, String, Object> operations = redisTemplate.opsForHash();
        operations.increment(key, field, delta);
    }

    /**
     * 删除Hash中一个或多个Field
     * @param key    Redis Hash Key
     * @param fields 要删除的字段名（可变参数）
     */
    public void deleteHash(String key, String... fields) {
        HashOperations<String, String, Object> operations = redisTemplate.opsForHash();
        operations.delete(key, (Object[]) fields);
    }

    /**
     * 获取Hash中所有Field名称集合
     * @param key Redis Hash Key
     * @return Field名称Set，Key不存在返回空Set
     */
    public Set<String> getHashKeys(String key) {
        HashOperations<String, String, Object> operations = redisTemplate.opsForHash();
        return operations.keys(key);
    }

    // ==================== Set操作 ====================

    /**
     * Set中添加成员
     * @param key   Redis Set Key
     * @param value 待添加的成员
     */
    public void addSet(String key, String value) {
        redisTemplate.opsForSet().add(key, value);
    }

    /**
     * Set中移除成员
     * @param key   Redis Set Key
     * @param value 待移除的成员
     */
    public void removeSet(String key, String value) {
        redisTemplate.opsForSet().remove(key, value);
    }

    /**
     * 判断成员是否存在于Set中
     * @param key   Redis Set Key
     * @param value 待判断的成员
     * @return true存在，false不存在或Key不存在
     */
    public boolean isMemberOfSet(String key, String value) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
    }
}
