package com.at2024.redis_redlock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RedisRedLockApplication.class)
public class RedLockTest {
    //todo 连接不上部署在docker上的redis，后续学习完docker要继续排查
    @Test
    public void guavaWithBloomFilterTest() {
        // 创建配置对象
        Config config = new Config();
        // 指定使用单节点部署方式
        config.useSingleServer().setAddress("redis://192.168.0.132:6379");
        // 创建Redisson客户端实例
        RedissonClient redisson = Redisson.create(config);
        System.out.println("=========================");
        // 这里可以进行相关操作，比如...

        // 最后关闭Redisson客户端
        redisson.shutdown();
    }
}
