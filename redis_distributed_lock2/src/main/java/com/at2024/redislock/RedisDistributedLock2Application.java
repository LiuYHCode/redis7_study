package com.at2024.redislock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RedisDistributedLock2Application {

    private static Logger LOGGER = LoggerFactory.getLogger(RedisDistributedLock2Application.class);

    public static void main(String[] args) {
        try {
            SpringApplication.run(RedisDistributedLock2Application.class, args);
            LOGGER.info("****************************************redis分布式锁7777启动成功****************************************");
        } catch (Exception e) {
            LOGGER.error("redis分布式锁7777启动成功",e);
        }
    }

}
