package com.at2024.redis7_study;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages = {"com.at2024.**.mapper"})
@ImportResource("classpath:/springconf/transaction.xml")
public class Redis7StudyApplication {

    private static Logger LOGGER = LoggerFactory.getLogger(Redis7StudyApplication.class);

    public static void main(String[] args) {
        try {
            SpringApplication.run(Redis7StudyApplication.class, args);
            LOGGER.info("****************************************Redis7学习项目启动成功****************************************");
        } catch (Exception e) {
            LOGGER.error("Redis7学习项目启动失败",e);
        }
    }

}
