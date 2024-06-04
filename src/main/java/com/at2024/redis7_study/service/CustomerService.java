package com.at2024.redis7_study.service;

import com.at2024.redis7_study.entities.Customer;
import com.at2024.redis7_study.mapper.CustomerMapper;
import com.at2024.redis7_study.utils.CheckBloomFilterUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;

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
    @Autowired
    private CheckBloomFilterUtils checkBloomFilterUtils;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    public void addCustomer(Customer customer) {
        int i = customerMapper.insertCustomerForSelective(customer);

        if (i > 0) {
            //同步数据到redis，mysql插入成功，需要重新查询一次将数据捞出来，写进Redis
            Customer result = customerMapper.selectCustomerById(customer.getId());
            //redis缓存key
            String key = CACHE_KEY_CUSTOMER + result.getId();
            redisTemplate.opsForHash().put("customer:", key, result);
        }
    }

    public Customer getCustomerById(Integer id) {
        Customer customer = null;
        //redis缓存key
        String key = CACHE_KEY_CUSTOMER + id;
        //先从redis中取，查看redis中是否存在
        customer = (Customer) redisTemplate.opsForHash().get("customer:", key);

        //redis不存在，去mysql中查找
        if (null == customer) {
            // 双端加锁策略
            synchronized (CustomerService.class) {
                customer = (Customer) redisTemplate.opsForHash().get("customer:", key);
                if (null == customer) {
                    //mysql中取
                    customer = customerMapper.selectCustomerById(id);
                    if (null == customer) {
                        //mysql中没有数据，设置一个空值到redis，避免缓存穿透
                        addCustomerWithDelayedDeletion(key, customer);
                    } else {
                        //mysql取到数据，写进redis
                        redisTemplate.opsForHash().put("customer:", key, customer);
                    }
                }
            }
        }
        return customer;
    }

    public Customer findCustomerByIdWithBloomFilter(Integer id) {
        Customer customer = null;
        //redis缓存key
        String key = CACHE_KEY_CUSTOMER + id;

        //================= 新加一层布隆过滤器的检查 =====================
        if (!checkBloomFilterUtils.checkWithBloomFilter("whitelistCustomer", key)) {
            log.info("白名单无此顾客，不可以访问，{}", key);
            return null;
        }
        //================= 新加一层布隆过滤器的检查 =====================

        //先从redis中取，查看redis中是否存在
        customer = (Customer) redisTemplate.opsForHash().get("customer:", key);

        //redis不存在，去mysql中查找
        if (null == customer) {
            // 双端加锁策略
            synchronized (CustomerService.class) {
                customer = (Customer) redisTemplate.opsForHash().get("customer:", key);
                if (null == customer) {
                    //mysql中取
                    customer = customerMapper.selectCustomerById(id);
                    if (null == customer) {
                        //mysql中没有数据，设置一个空值到redis，避免缓存穿透
                        addCustomerWithDelayedDeletion(key, customer);
                    } else {
                        //mysql取到数据，写进redis
                        redisTemplate.opsForHash().put("customer:", key, customer);
                    }
                }
            }
        }
        return customer;
    }

    public void addCustomerWithDelayedDeletion(String key, Customer customer) {
        // 创建一个新的线程来处理延迟删除
        Future<?> deletionFuture = executorService.submit(() -> {
            try {
                // 添加键值对到Redis
                redisTemplate.opsForHash().put("customer:", key, new Customer());
                log.info("新增一个customer到redis缓存，key:{}", key);

                // 等待一分钟
                Thread.sleep(60 * 1000);
                log.info("Deleting customer with ID {} from cache after 1 minute", key);

                // 删除键值对
                redisTemplate.opsForHash().delete("customer:", key);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Interrupted while waiting for deletion", e);
            }
        });
    }
}
