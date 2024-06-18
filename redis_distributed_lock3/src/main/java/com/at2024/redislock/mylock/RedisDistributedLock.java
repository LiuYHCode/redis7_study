package com.at2024.redislock.mylock;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 自研的分布式锁，实现了Lock接口
 * @author lyh
 * @date 2024/6/17  23:13
 */
@Slf4j
public class RedisDistributedLock implements Lock {

    private StringRedisTemplate stringRedisTemplate;

    private String lockName; // KEYS[1]
    private String uuidValule; // ARGV[1]
    private long expireTime; // ARGV[2]

    public RedisDistributedLock(StringRedisTemplate stringRedisTemplate, String lockName) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.lockName = lockName;
        this.uuidValule = IdUtil.simpleUUID() + ":" + Thread.currentThread().getId();
        this.expireTime = 500L;
    }

    @Override
    public void lock() {
        tryLock();
    }

    @Override
    public boolean tryLock() {
        try {
            tryLock(-1L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        if (-1 == time) {
            String script =
                "if redis.call('exists', KEYS[1]) == 0 or redis.call('hexists', KEYS[1], ARGV[1]) == 1 then " +
                    "redis.call('hincrby', KEYS[1], ARGV[1], 1) " +
                    "redis.call('expire', KEYS[1], ARGV[2]) " +
                    "return 1 " +
                "else " +
                    "return 0 " +
                "end";
            log.info("lockName:" + lockName + "\t" + "uuidValue:" + uuidValule);

            while (!stringRedisTemplate.execute(new DefaultRedisScript<>(script, Boolean.class),
                    Arrays.asList(lockName),
                    uuidValule,
                    String.valueOf(expireTime))) {
                // 休眠60毫秒再来重试
                try {TimeUnit.MILLISECONDS.sleep(60);} catch (InterruptedException e) {e.printStackTrace();}
            }
            return true;
        }
        return false;
    }

    @Override
    public void unlock() {
        String script = "" +
                "if redis.call('hexists', KEYS[1], ARGV[1]) == 0 then " +
                "return nil " +
                "elseif redis.call('hincrby', KEYS[1], ARGV[1], -1) == 0 then " +
                "return redis.call('del', KEYS[1]) " +
                "else " +
                "return 0 " +
                "end";
        log.info("lockName:" + lockName + "\t" + "uuidValue:" + uuidValule);

        // LUA脚本由C语言编写，nil -> false; 0 -> false; 1 -> true;
        // 所以此处DefaultRedisScript构造函数返回值不能是Boolean，Boolean没有nil
        Long flag = stringRedisTemplate.execute(
                new DefaultRedisScript<>(script, Long.class),
                Arrays.asList(lockName),
                uuidValule);
        log.info("flag:" + flag);
        if (null == flag) {
            throw new RuntimeException("this lock does not exists.");
        }
    }

    //下面两个暂时用不到，不用重写
    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
