package com.at2024.redis7_study.redis7;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.protocol.RedisCommand;

/**
 * @author lyh
 * @date 2024/4/19  16:35
 */
public class Lettuce {
    public static void main(String[] args) {

        //1.定义URI
        RedisURI uri = RedisURI.builder()
                .withHost("192.168.56.106")
                .withPort(6379)
                .withAuthentication("default","aaa111")
                .build();

        //2.创建redis客户端
        RedisClient redisClient = RedisClient.create(uri);

        //3.获得连接操作
        StatefulRedisConnection connect = redisClient.connect();

        //4.创建RedisCommands-------使用同步的方式来操作redis数据库
        RedisCommands command = connect.sync();

        //========================业务start=========================//
        command.ping();
        System.out.println(command.keys("*"));

        //string
        command.set("scw","1");
        command.get("scw");

        //list
        command.lpush("week","Mon", "Tur", "Wen", "Thr", "Fri", "Sta", "Sun");
        command.lindex("week", 0);


        //========================业务end=========================//

        //关闭连接资源
        connect.close();
        redisClient.shutdown();
    }
}
