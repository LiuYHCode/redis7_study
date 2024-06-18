package com.at2024.redislock.service;

import com.at2024.redislock.mylock.DistributedLockFactory;
import com.at2024.redislock.mylock.RedisDistributedLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
public class InventoryService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Value("${server.port}")
    private String port;

    private Lock lock = new ReentrantLock();
    private Lock redisDistributedLock;

    public String sale1_0() {
        String resMessgae = "";
        lock.lock();
        try {
            //1.查询库存信息
            String result = stringRedisTemplate.opsForValue().get("inventory");
            //2.判断库存是否足够
            Integer inventoryNum = result == null ? 0 : Integer.parseInt(result);
            //3.扣减库存，每次减少一个库存
            if (inventoryNum > 0) {
                stringRedisTemplate.opsForValue().set("inventory", String.valueOf(--inventoryNum));
                resMessgae = "成功卖出一个商品，库存剩余：" + inventoryNum;
                log.info(resMessgae + "\t" + "，服务端口号：" + port);
            } else {
                resMessgae = "商品已售罄。";
                log.info(resMessgae + "\t" + "，服务端口号：" + port);
            }
        } finally {
            lock.unlock();
        }
        return resMessgae;
    }

    //由于redisDistributedLock比InventoryService更早执行，所以得延迟new，确保stringRedisTemplate已经注入
//    @PostConstruct
//    public void init() {
//        redisDistributedLock = new RedisDistributedLock(stringRedisTemplate, "lyhRedisLock");
//    }
    @Autowired
    private DistributedLockFactory distributedLockFactory;
    //v7.0 版本，先复原程序为无锁版本
    //使用自研的lock/unlock+LUA脚本自研的Redis分布式锁
    public String sale() {
        String resMessgae = "";
//        redisDistributedLock.lock();
        Lock redisLock = distributedLockFactory.getDistributedLock("REDIS", "lyhRedisLock");
        redisLock.lock();
        try {
            // 1 抢锁成功，查询库存信息
            String result = stringRedisTemplate.opsForValue().get("inventory");
            // 2 判断库存书否足够
            Integer inventoryNum = result == null ? 0 : Integer.parseInt(result);
            // 3 扣减库存，每次减少一个库存
            if (inventoryNum > 0) {
                stringRedisTemplate.opsForValue().set("inventory", String.valueOf(--inventoryNum));
                resMessgae = "成功卖出一个商品，库存剩余：" + inventoryNum + "\t" + "，服务端口号：" + port;
                log.info(resMessgae);
            } else {
                resMessgae = "商品已售罄。" + "\t" + "，服务端口号：" + port;
                log.info(resMessgae);
            }
        } finally {
            redisLock.unlock();
        }
        return resMessgae;
    }
}
