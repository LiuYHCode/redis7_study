package com.at2024.redislock.service;

import cn.hutool.core.util.IdUtil;
import com.at2024.redislock.mylock.DistributedLockFactory;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
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

//    V2.0版本，使用nginx与jmeter压测过后，发现不满足高并发分布式锁的性能，出现超卖现象
//    public String sale() {
//        String resMessgae = "";
//        lock.lock();
//        try {
//            //1.查询库存信息
//            String result = stringRedisTemplate.opsForValue().get("inventory");
//            //2.判断库存是否足够
//            Integer inventoryNum = result == null ? 0 : Integer.parseInt(result);
//            //3.扣减库存，每次减少一个库存
//            if (inventoryNum > 0) {
//                stringRedisTemplate.opsForValue().set("inventory", String.valueOf(--inventoryNum));
//                resMessgae = "成功卖出一个商品，库存剩余：" + inventoryNum;
//                log.info(resMessgae + "\t" + "，服务端口号：" + port);
//            } else {
//                resMessgae = "商品已售罄。";
//                log.info(resMessgae + "\t" + "，服务端口号：" + port);
//            }
//        } finally {
//            lock.unlock();
//        }
//        return resMessgae;
//    }

    public String sale3_1() {
        String resMessgae = "";
        String key = "lyhRedisLock";
        String uuidValue = IdUtil.simpleUUID() + ":" + Thread.currentThread().getId();

        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, uuidValue);
        //抢不到的线程继续重试
        if (!flag) {
            //线程休眠20毫秒，进行递归重试
            try {
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sale3_1();
        } else {
            try {
                //1.抢锁成功，查询库存信息
                String result = stringRedisTemplate.opsForValue().get("inventory");
                //2.判断库存是否足够
                Integer inventoryNum = result == null ? 0 : Integer.parseInt(result);
                //3.扣减库存，每次减少一个库存
                if (inventoryNum > 0) {
                    stringRedisTemplate.opsForValue().set("inventory", String.valueOf(--inventoryNum));
                    resMessgae = "成功卖出一个商品，库存剩余：" + inventoryNum + "\t" + "，服务端口号：" + port;
                    log.info(resMessgae);
                } else {
                    resMessgae = "商品已售罄。" + "\t" + "，服务端口号：" + port;
                    log.info(resMessgae);
                }
            } finally {
                stringRedisTemplate.delete(key);
            }
        }
        return resMessgae;
    }

    public String sale3_2() {
        String resMessgae = "";
        String key = "lyhRedisLock";
        String uuidValue = IdUtil.simpleUUID() + ":" + Thread.currentThread().getId();

        //不用递归了，高并发容易出错，我们用自旋代替递归方法重试调用；也不用if，用while代替
        while (!stringRedisTemplate.opsForValue().setIfAbsent(key, uuidValue)) {
            //线程休眠20毫秒，进行递归重试
            try {
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            //1.抢锁成功，查询库存信息
            String result = stringRedisTemplate.opsForValue().get("inventory");
            //2.判断库存是否足够
            Integer inventoryNum = result == null ? 0 : Integer.parseInt(result);
            //3.扣减库存，每次减少一个库存
            if (inventoryNum > 0) {
                stringRedisTemplate.opsForValue().set("inventory", String.valueOf(--inventoryNum));
                resMessgae = "成功卖出一个商品，库存剩余：" + inventoryNum + "\t" + "，服务端口号：" + port;
                log.info(resMessgae);
            } else {
                resMessgae = "商品已售罄。" + "\t" + "，服务端口号：" + port;
                log.info(resMessgae);
            }
        } finally {
            stringRedisTemplate.delete(key);
        }
        return resMessgae;
    }

    public String sale4_1() {
        String resMessgae = "";
        String key = "lyhRedisLock";
        String uuidValue = IdUtil.simpleUUID() + ":" + Thread.currentThread().getId();

        //不用递归了，高并发容易出错，我们用自旋代替递归方法重试调用；也不用if，用while代替
        while (!stringRedisTemplate.opsForValue().setIfAbsent(key, uuidValue)) {
            //线程休眠20毫秒，进行递归重试
            try {
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //设置过期时间
            stringRedisTemplate.expire(key, 30L, TimeUnit.SECONDS);
        }
        try {
            //1.抢锁成功，查询库存信息
            String result = stringRedisTemplate.opsForValue().get("inventory");
            //2.判断库存是否足够
            Integer inventoryNum = result == null ? 0 : Integer.parseInt(result);
            //3.扣减库存，每次减少一个库存
            if (inventoryNum > 0) {
                stringRedisTemplate.opsForValue().set("inventory", String.valueOf(--inventoryNum));
                resMessgae = "成功卖出一个商品，库存剩余：" + inventoryNum + "\t" + "，服务端口号：" + port;
                log.info(resMessgae);
            } else {
                resMessgae = "商品已售罄。" + "\t" + "，服务端口号：" + port;
                log.info(resMessgae);
            }
        } finally {
            stringRedisTemplate.delete(key);
        }
        return resMessgae;
    }

    public String sale4_2() {
        String resMessgae = "";
        String key = "lyhRedisLock";
        String uuidValue = IdUtil.simpleUUID() + ":" + Thread.currentThread().getId();

        //设置的key+过期时间分开了，必须要合并成一行使其具备原子性
        while (!stringRedisTemplate.opsForValue().setIfAbsent(key, uuidValue, 30L, TimeUnit.SECONDS)) {
            //线程休眠20毫秒，进行递归重试
            try {
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            //1.抢锁成功，查询库存信息
            String result = stringRedisTemplate.opsForValue().get("inventory");
            //2.判断库存是否足够
            Integer inventoryNum = result == null ? 0 : Integer.parseInt(result);
            //3.扣减库存，每次减少一个库存
            if (inventoryNum > 0) {
                stringRedisTemplate.opsForValue().set("inventory", String.valueOf(--inventoryNum));
                resMessgae = "成功卖出一个商品，库存剩余：" + inventoryNum + "\t" + "，服务端口号：" + port;
                log.info(resMessgae);
            } else {
                resMessgae = "商品已售罄。" + "\t" + "，服务端口号：" + port;
                log.info(resMessgae);
            }
        } finally {
            stringRedisTemplate.delete(key);
        }
        return resMessgae;
    }

    //v5.0 版本
    public String sale5_0() {
        String resMessgae = "";
        String key = "lyhRedisLock";
        String uuidValue = IdUtil.simpleUUID() + ":" + Thread.currentThread().getId();

        //不用递归了，高并发容易出错，我们用自旋代替递归方法重试调用；也不用if，用while代替
        while (!stringRedisTemplate.opsForValue().setIfAbsent(key, uuidValue, 30L, TimeUnit.SECONDS)) {
            //线程休眠20毫秒，进行递归重试
            try {
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            //1.抢锁成功，查询库存信息
            String result = stringRedisTemplate.opsForValue().get("inventory");
            //2.判断库存是否足够
            Integer inventoryNum = result == null ? 0 : Integer.parseInt(result);
            //3.扣减库存，每次减少一个库存
            if (inventoryNum > 0) {
                stringRedisTemplate.opsForValue().set("inventory", String.valueOf(--inventoryNum));
                resMessgae = "成功卖出一个商品，库存剩余：" + inventoryNum + "\t" + "，服务端口号：" + port;
                log.info(resMessgae);
            } else {
                resMessgae = "商品已售罄。" + "\t" + "，服务端口号：" + port;
                log.info(resMessgae);
            }
        } finally {
            //v5.0改进点，判断加锁与解锁是不同客户端，自己只能删除自己的锁，不误删别人的锁
            if (stringRedisTemplate.opsForValue().get(key).equalsIgnoreCase(uuidValue)) {
                stringRedisTemplate.delete(key);
            }
        }
        return resMessgae;
    }

    //v6.0 版本
    public String sale6_0() {
        String resMessgae = "";
        String key = "lyhRedisLock";
        String uuidValue = IdUtil.simpleUUID() + ":" + Thread.currentThread().getId();

        //不用递归了，高并发容易出错，我们用自旋代替递归方法重试调用；也不用if，用while代替
        while (!stringRedisTemplate.opsForValue().setIfAbsent(key, uuidValue, 50L, TimeUnit.SECONDS)) {
            //线程休眠20毫秒，进行递归重试
            try {
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            //1.抢锁成功，查询库存信息
            String result = stringRedisTemplate.opsForValue().get("inventory");
            //2.判断库存是否足够
            Integer inventoryNum = result == null ? 0 : Integer.parseInt(result);
            //3.扣减库存，每次减少一个库存
            if (inventoryNum > 0) {
                stringRedisTemplate.opsForValue().set("inventory", String.valueOf(--inventoryNum));
                resMessgae = "成功卖出一个商品，库存剩余：" + inventoryNum + "\t" + "，服务端口号：" + port;
                log.info(resMessgae);
            } else {
                resMessgae = "商品已售罄。" + "\t" + "，服务端口号：" + port;
                log.info(resMessgae);
            }
        } finally {
            //v6.0改进点，修改为lua脚本的redis分布式锁调用，必须保证原子性，参考官网脚本案例
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                    "return redis.call('del', KEYS[1]) else return 0 end";
            stringRedisTemplate.execute(new DefaultRedisScript<>(script, Boolean.class), Arrays.asList(key), uuidValue);
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
