package com.at2024.redis_redlock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RedisRedLockApplication {

    private static Logger LOGGER = LoggerFactory.getLogger(RedisRedLockApplication.class);

    public static void main(String[] args) {
        try {
            SpringApplication.run(RedisRedLockApplication.class, args);
            LOGGER.info("****************************************redisson9090启动成功****************************************");
        } catch (Exception e) {
            LOGGER.error("redisson9090启动失败",e);
        }
    }
}
