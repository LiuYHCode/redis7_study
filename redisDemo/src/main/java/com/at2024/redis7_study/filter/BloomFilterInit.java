package com.at2024.redis7_study.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 布隆过滤器白名单初始化工具类，又开始就设置一部分数据为白名单所有
 * 白名单业务默认规定：布隆过滤器有，Redis是极大可能有
 * 白名单：whitelistCustomer
 */
@Component
@Slf4j
public class BloomFilterInit {

    @Autowired
    private RedisTemplate redisTemplate;

    //这里之所以不需要调用init方法，是由于SpringBoot启动的时候，创建这个Bean，然后看见PostConstruct注解，就会在对象创建并且依赖注入之后就马上执行标记的方法。
    @PostConstruct
    public void init() {
        //初始话白名单的数据到bitmap中，当作自研的布隆过滤器
        //1白名单用户加载到布隆过滤器
        String key = "customer:12";
        //2计算hashValue，由于存在负数的可能性，需要取绝对值
        int hashValue = Math.abs(key.hashCode());
        //3通过hashValue和2^32取余，活得对应的下标坑位
        long index = (long) (hashValue % Math.pow(2, 32));
        log.info(key + "对应的坑位index：{}", index);
        //4设置Redis 里面的bitmap对应白名单类型的坑位，并设置为1
        redisTemplate.opsForValue().setBit("whitelistCustomer", index, true);
    }
}
