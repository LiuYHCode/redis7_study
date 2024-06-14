package com.at2024.redis7_study.controller;

import com.at2024.redis7_study.service.GeoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.graph.entities.Point;

/**
 * @author lyh
 * @date 2024/4/28  17:32
 */
@RestController
@Api(tags = "酒店推送")
@RequestMapping("/geo")
public class GeoController {
    @Autowired
    private GeoService geoService;

    @ApiOperation("添加坐标geoAdd")
    @GetMapping(value = "/geoAdd")
    public String geoAdd(){
        return geoService.geoAdd();
    }

    @ApiOperation("获取经纬度坐标position")
    @GetMapping(value = "/position")
    public Point position(String member){
        return geoService.position(member);
    }

    @ApiOperation("获取经纬度生成的base32编码值hash")
    @GetMapping(value = "/hash")
    public String hash(String member){
        return geoService.hash(member);
    }

    @ApiOperation("获取两个给定位置间的距离")
    @GetMapping(value = "/distance")
    public Distance distance(String member1, String member2){
        return geoService.distance(member1,member2);
    }

    @ApiOperation("通过经度纬度查找xxx附近")
    @GetMapping(value = "/radiusByxy")
    public GeoResults radiusByxy(){
        return geoService.radiusByxy();
    }

    @ApiOperation("通过地方查找附近")
    @GetMapping(value = "/radiusByMember")
    public GeoResults radiusByMember(){
        return geoService.radiusByMember();
    }
}
