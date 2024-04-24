package com.at2024.redis7_study.controller;

import com.at2024.redis7_study.service.BigKeyService;
import io.swagger.annotations.Api;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

import java.util.List;
import java.util.Map;

/**
 * @author lyh
 * @date 2024/4/24  14:09
 */
@RestController
@Api(tags = "大key接口")
@RequestMapping("/bigKey")
public class BigKeyController {
    private static Logger LOGGER = LoggerFactory.getLogger(BigKeyController.class);

    @Autowired
    private BigKeyService bigKeyService;

    /**
     * Hash删除：hscan + hdel，Hash由键值对构成，删除大key里面的部分键值对
     * @param host
     * @param port
     * @param password
     * @param bigHashKey
     */
    @GetMapping(value = "/delBigHash")
    public void delBigHash(@RequestParam("host")String host, @RequestParam("port")int port, @RequestParam("password")String password, @RequestParam("bigHashKey")String bigHashKey) {
        bigKeyService.delBigHash(host, port, password, bigHashKey);
    }

}
