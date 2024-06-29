package com.at2024.redis_redlock.controller;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class RediConn {

    public static void main(String[] args) {
        // 创建配置对象
        Config config = new Config();
        // 指定使用单节点部署方式
        config.useSingleServer().setAddress("redis://192.168.0.145:6381");
        // 创建Redisson客户端实例
        RedissonClient redisson = Redisson.create(config);
        System.out.println("=========================");
        // 这里可以进行相关操作，比如...

        // 最后关闭Redisson客户端
        redisson.shutdown();
    }
}
