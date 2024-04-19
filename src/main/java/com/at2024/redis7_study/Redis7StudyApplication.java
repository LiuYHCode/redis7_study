package com.at2024.redis7_study;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Redis7StudyApplication {

    private static Logger logger = LoggerFactory.getLogger(Redis7StudyApplication.class);

    public static void main(String[] args) {
        try {
            SpringApplication.run(Redis7StudyApplication.class, args);
            logger.info("****************************************Redis7学习项目启动成功****************************************");
        } catch (Exception e) {
            logger.error("Redis7学习项目启动失败",e);
        }

    }

}
