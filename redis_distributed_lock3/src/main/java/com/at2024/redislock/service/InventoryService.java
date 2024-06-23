package com.at2024.redislock.service;

import com.at2024.redislock.mylock.DistributedLockFactory;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

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

    //v7.0 版本，先复原程序为无锁版本
    //使用自研的lock/unlock+LUA脚本自研的Redis分布式锁
    //由于redisDistributedLock比InventoryService更早执行，所以得延迟new，确保stringRedisTemplate已经注入
//    @PostConstruct
//    public void init() {
//        redisDistributedLock = new RedisDistributedLock(stringRedisTemplate, "lyhRedisLock");
//    }
    //v7.1我们将写死的分布式锁，改造为工厂模式，考虑扩展性
//    @Autowired
//    private DistributedLockFactory distributedLockFactory;
//    public String sale7_x() {
//        String resMessgae = "";
////        redisDistributedLock.lock();
//        Lock redisLock = distributedLockFactory.getDistributedLock("REDIS", "lyhRedisLock");
//        redisLock.lock();
//        try {
//            // 1 抢锁成功，查询库存信息
//            String result = stringRedisTemplate.opsForValue().get("inventory");
//            // 2 判断库存书否足够
//            Integer inventoryNum = result == null ? 0 : Integer.parseInt(result);
//            // 3 扣减库存，每次减少一个库存
//            if (inventoryNum > 0) {
//                stringRedisTemplate.opsForValue().set("inventory", String.valueOf(--inventoryNum));
//                resMessgae = "成功卖出一个商品，库存剩余：" + inventoryNum + "\t" + "，服务端口号：" + port;
//                log.info(resMessgae);
//                //测试可重入锁
//                testReEntry();
//            } else {
//                resMessgae = "商品已售罄。" + "\t" + "，服务端口号：" + port;
//                log.info(resMessgae);
//            }
//        } finally {
//            redisLock.unlock();
//        }
//        return resMessgae;
//    }

    @Autowired
    private DistributedLockFactory distributedLockFactory;
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
                //测试可重入锁
                testReEntry();
            } else {
                resMessgae = "商品已售罄。" + "\t" + "，服务端口号：" + port;
                log.info(resMessgae);
            }
        } finally {
            redisLock.unlock();
        }
        return resMessgae;
    }

    @Autowired
    private Redisson redisson;
    public String saleByRedisson()
    {
        String retMessage = "";

        RLock redissonLock = redisson.getLock("lyhRedisLock");
        redissonLock.lock();
        try {
            //1 查询库存信息
            String result = stringRedisTemplate.opsForValue().get("inventory");
            //2 判断库存是否足够
            Integer inventoryNumber = result == null ? 0 : Integer.parseInt(result);
            //3 扣减库存，每次减少一个
            if(inventoryNumber > 0) {
                stringRedisTemplate.opsForValue().set("inventory",String.valueOf(--inventoryNumber));
                retMessage = "成功卖出一个商品,库存剩余:"+inventoryNumber;
                log.info(retMessage+"\t"+"服务端口号"+port);
            } else {
                retMessage = "商品卖完了,o(╥﹏╥)o";
            }
        } finally {
            //改进点，只能删除属于自己的key，不能删除别人的
            if(redissonLock.isLocked() && redissonLock.isHeldByCurrentThread()) {
                redissonLock.unlock();
            }
        }
        return retMessage+"\t"+"服务端口号"+port;
    }

    private void testReEntry() {
        Lock redisLock = distributedLockFactory.getDistributedLock("REDIS", "lyhRedisLock");
        redisLock.lock();
        try {
            log.info("=================测试可重入锁=================");
        } finally {
            redisLock.unlock();
        }
    }
}
