package com.at2024.redis7_study.service;

import com.at2024.redis7_study.entities.Customer;
import com.at2024.redis7_study.mapper.CustomerMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author lyh
 * @date 2024/5/28  23:27
 */
@Service
@Slf4j
public class CustomerService {
    public static final String CACHE_KEY_CUSTOMER = "customer:";

    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    public void addCustomer(Customer customer) {
        int i = customerMapper.insertCustomerForSelective(customer);

        if (i > 0) {
            //同步数据到redis，mysql插入成功，需要重新查询一次将数据捞出来，写进Redis
            Customer result = customerMapper.selectCustomerById(customer.getId());
            //redis缓存key
            String key = CACHE_KEY_CUSTOMER + result.getId();
            redisTemplate.opsForValue().set(key, result);
        }
    }

    public Customer getCustomerById(Integer id) {
        Customer customer = null;
        //redis缓存key
        String key = CACHE_KEY_CUSTOMER + id;
        //先从redis中取
        customer = (Customer) redisTemplate.opsForValue().get(key);
        if (null == customer) {
            //redis缓存中没有，从mysql中取
            customer = customerMapper.selectCustomerById(id);
            //mysql取到数据，写进redis
            redisTemplate.opsForValue().set(key, customer);
        }
        return customer;
    }
}
