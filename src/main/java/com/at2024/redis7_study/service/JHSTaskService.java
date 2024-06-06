package com.at2024.redis7_study.service;

import com.at2024.redis7_study.entities.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class JHSTaskService {

    public static final String JHS_KEY = "jhs";

    @Autowired
    private RedisTemplate redisTemplate;

    public List<Product> getProductsFromMysql() {
        List<Product> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Random random = new Random();
            int id = random.nextInt(10000);
            Product product = new Product(id, "product" + i, new BigDecimal(i), "detail");
            list.add(product);
        }
        return list;
    }

    @PostConstruct
    public void initJHS() {
        log.info("模拟定时任务从数据库中不断获取参加聚划算的商品");
        //1.用多线程模拟定时任务，将商品从数据库刷新到redis
        new Thread(() -> {
            //2.模拟从数据库查询数据
            List<Product> productList = getProductsFromMysql();
            //3.删除原来的数据
            redisTemplate.delete(JHS_KEY);
            //4.加入最新的数据给Redis参加活动
            redisTemplate.opsForList().leftPushAll(JHS_KEY, productList);
            //5暂停1分钟，模拟聚划算参加商品下架上新等操作
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t1").start();



    }
}
