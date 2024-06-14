package com.at2024.redis7_study.controller;

import com.at2024.redis7_study.service.BigKeyService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    /**
     * List删除：ltrim + hdel，先进行修剪，最后进行删除
     * @param host
     * @param port
     * @param password
     * @param bigListKey
     */
    @GetMapping(value = "/delBigList")
    public void delBigList(@RequestParam("host")String host, @RequestParam("port")int port, @RequestParam("password")String password, @RequestParam("bigListKey")String bigListKey) {
        bigKeyService.delBigList(host, port, password, bigListKey);
    }

    /**
     * Set删除：srem + hdel，先进行修剪，最后进行删除
     * @param host
     * @param port
     * @param password
     * @param bigSetKey
     */
    @GetMapping(value = "/delBigSet")
    public void delBigSet(@RequestParam("host")String host, @RequestParam("port")int port, @RequestParam("password")String password, @RequestParam("bigSetKey")String bigSetKey) {
        bigKeyService.delBigSet(host, port, password, bigSetKey);
    }

    /**
     * Zset删除：zrem + hdel，先进行修剪，最后进行删除
     * @param host
     * @param port
     * @param password
     * @param bigZsetKey
     */
    @GetMapping(value = "/delBigZset")
    public void delBigZset(@RequestParam("host")String host, @RequestParam("port")int port, @RequestParam("password")String password, @RequestParam("bigZsetKey")String bigZsetKey) {
        bigKeyService.delBigZset(host, port, password, bigZsetKey);
    }

}
