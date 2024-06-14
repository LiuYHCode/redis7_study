package com.at2024.redis7_study.controller;

import com.at2024.redis7_study.service.GuavaBloomFilterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lyh
 * @date 2024/6/5  23:09
 */
@RestController
@Api(tags = "google工具Guava处理布隆过滤器")
@Slf4j
public class GuavaBloomFilterController {

    @Autowired
    private GuavaBloomFilterService guavaBloomFilterService;

    @ApiOperation("guava布隆过滤器插入100万样本数据并额外10万测试是否存在")
    @GetMapping(value = "/guavafilter")
    public void guavaBloomFIlterService() {
        guavaBloomFilterService.guavaBloomFilterService();
    }
}
