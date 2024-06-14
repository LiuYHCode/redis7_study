package com.at2024.redis7_study.controller;

import com.at2024.redis7_study.entities.Customer;
import com.at2024.redis7_study.service.CustomerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Random;

/**
 * @author lyh
 * @date 2024/4/24  14:09
 */
@RestController
@Api(tags = "客户Customer接口+布隆过滤器讲解")
@RequestMapping("/customer")
@Slf4j
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @ApiOperation("数据库初始化两条Customer记录")
    @GetMapping(value = "/add")
    public void addCustomer() {
        for (int i = 0; i < 2; i++) {
            Customer customer = new Customer();
            customer.setId(new Random().nextInt(10000) + 1);
            customer.setCname("customer" + i);
            customer.setAge(new Random().nextInt(30) + 1);
            customer.setPhone("138" + new Random().nextInt(100000000));
            customer.setSex((byte) new Random().nextInt(2));
            customer.setBirth(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));

            customerService.addCustomer(customer);
        }
    }

    @ApiOperation("根据ID获取Customer记录")
    @GetMapping(value = "/{id}")
    public Customer getCustomerById(@PathVariable int id) {
        return customerService.getCustomerById(id);
    }

    @ApiOperation("BloomFilter, 单个customer查询操作")
    @GetMapping(value = "/customerBloomFilter/{id}")
    public Customer findCustomerByIdWithBloomFilter(@PathVariable int id) {
        return customerService.findCustomerByIdWithBloomFilter(id);
    }
}
