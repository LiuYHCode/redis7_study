package com.at2024.redis7_study.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author lyh
 * @date 2024/4/23  17:24
 */
@Service
public class OrderService {
    public static final String ORDER_KEY = "ord:";

    @Autowired
    private RedisTemplate redisTemplate;

    public void addOrder(){
        int keyId = ThreadLocalRandom.current().nextInt(1000)+1;
        String serialNo = UUID.randomUUID().toString();
        String key = ORDER_KEY + keyId;
        String value = "京东订单" + serialNo;
        redisTemplate.opsForValue().set(key,value);
        System.out.println(key);
        System.out.println(value);
    }

    public String getOrderById(Integer keyId){
        Object o = redisTemplate.opsForValue().get(ORDER_KEY + keyId);
        return (String) o;
    }

}
