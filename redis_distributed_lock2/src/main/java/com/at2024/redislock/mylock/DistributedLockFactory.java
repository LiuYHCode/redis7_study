package com.at2024.redislock.mylock;

import cn.hutool.core.util.IdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;

@Component
public class DistributedLockFactory {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private String uuid;

    public DistributedLockFactory() {
        this.uuid = IdUtil.simpleUUID();
    }

    public Lock getDistributedLock(String lockType, String lockName) {
        if (lockType == null) {
            return null;
        }

        if ("REDIS".equalsIgnoreCase(lockType)) {
            //v7.2版本，测试可重入锁的时候发现uuid不一致，导致项目会存在bug，这里直接在工厂模式这里加上uuid，保证可重入锁的uuid是一致的
            //@Autowired创建的工厂类是一个单例的，在spring进行注入的时候已经初始化好了，所以所有线程产生的UUID都是一样的
            return new RedisDistributedLock(stringRedisTemplate, lockName, uuid);
        } else if ("ZOOKEEPER".equalsIgnoreCase(lockType)) {
            // 后面存在就返回对应的分布式锁
        } else if ("MYSQL".equalsIgnoreCase(lockType)) {
            // 后面存在就返回对应的分布式锁
        }

        return null;
    }
}