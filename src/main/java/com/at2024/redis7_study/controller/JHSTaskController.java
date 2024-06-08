package com.at2024.redis7_study.controller;

import com.at2024.redis7_study.entities.Product;
import com.at2024.redis7_study.service.JHSTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "模拟聚划算商品上下架")
@RestController
@Slf4j
public class JHSTaskController {

    public static final String JHS_KEY = "jhs";

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private JHSTaskService jhsService;

    @ApiOperation("聚划算案例，每次1页，每页5条数据")
    @GetMapping("/product/find")
    public List<Product> find(int page, int size) {
        //设置寻找记录的起始和结束位置
        int start = (page - 1) * size;
        int end = start + size -1;
        //1.先去缓存寻找
        List productList = redisTemplate.opsForList().range(JHS_KEY, start, end);
        //2.缓存如果存在就直接拿出，如果不存在就去数据库查找
        if (CollectionUtils.isEmpty(productList)) {
            productList = jhsService.getProductsFromMysql();
        }
        log.info("参加活动的商家: {}", productList);
        return productList;
    }
}
