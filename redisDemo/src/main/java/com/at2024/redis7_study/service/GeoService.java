package com.at2024.redis7_study.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.graph.entities.Point;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lyh
 * @date 2024/4/28  16:29
 */
@Service
public class GeoService {
    public static final String CITY = "city";

    @Autowired
    private RedisTemplate redisTemplate;

    public String geoAdd() {
        Map<String, Point> geoMap = new HashMap<>();
        geoMap.put("天安门", new Point(116.403963, 39.915119));
        geoMap.put("故宫", new Point(116.403414, 39.924091));
        geoMap.put("长城", new Point(116.403963, 39.915119));
        redisTemplate.opsForGeo().add(CITY, geoMap);
        return geoMap.toString();
    }

    public Point position(String member) {
        //获取经纬度坐标
        List<Point> list = redisTemplate.opsForGeo().position(CITY, member);
        //返回查询的值即第一个
        return list.get(0);
    }

    public String hash(String member) {
        //geohash算法生成的base32编码值
        List<String> list = redisTemplate.opsForGeo().hash(CITY, member);
        return list.get(0);
    }

    public Distance distance(String member1, String member2) {
        //获取两个给定位置之间的距离
        Distance distance = redisTemplate.opsForGeo().distance(CITY, member1, member2,
                RedisGeoCommands.DistanceUnit.KILOMETERS);
        return distance;
    }

    public GeoResults radiusByxy() {
        //通过经度，纬度查找附近的，北京王府井位置116.418017, 39.914402
        Circle circle = new Circle(116.418017, 39.914402, Metrics.KILOMETERS.getMultiplier());
        //返回50条记录
        RedisGeoCommands.GeoRadiusCommandArgs limit = RedisGeoCommands.GeoRadiusCommandArgs
                .newGeoRadiusArgs().includeDistance().includeCoordinates().sortDescending().limit(50);
        GeoResults<RedisGeoCommands.GeoLocation<String>> radius = redisTemplate.opsForGeo().radius(CITY, circle, limit);
        return radius;
    }

    public GeoResults radiusByMember() {
        //通过地方查找附近
        String member = "天安门";
        //返回50条
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs
                .newGeoRadiusArgs().includeDistance().includeCoordinates().sortAscending().limit(50) ;
        //半径10公里内
        Distance distance = new Distance(10, Metrics.KILOMETERS);
        GeoResults<RedisGeoCommands.GeoLocation<String>> geoResults = redisTemplate.opsForGeo().radius(CITY, member, distance, args);
        return geoResults;
    }
}
