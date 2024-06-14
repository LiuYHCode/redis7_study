package com.at2024.redis7_study;

import com.at2024.redis7_study.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author lyh
 * @date 2024/4/22  10:20
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Redis7StudyApplication.class)
public class RedisTemplateApplicationTests {

    @Autowired
    public RedisTemplate<String, Object> redisTemplate;

    @Test
    public void setStringTest() {
        System.out.println(redisTemplate);
        redisTemplate.opsForValue().set("java2023", "java2023");
        System.out.println(redisTemplate.opsForValue().get("java2023"));
    }

    @Test
    public void setStringByHash() {
        User user = new User();
        user.setId("2024");
        user.setName("张三");
        user.setRemark("张三在学java");
        //对象存储到redis中以hash方式存入，这里的put放入的对象是user，对应的key和value就是id,整个user对象。
        redisTemplate.opsForHash().put("user", user.getId(), user);
        //按照对应id取出存储的user对象
        System.out.println(redisTemplate.opsForHash().get("user", user.getId()));
    }
}
