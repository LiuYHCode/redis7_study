package com.at2024.redis7_study.controller;

import com.at2024.redis7_study.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lyh
 * @date 2024/4/23  17:27
 */
@RestController
@Api(tags = "订单接口")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @ApiOperation("新增订单接口")
    @RequestMapping(value = "/order/add",method = RequestMethod.GET)
    public void addOrder(){
        orderService.addOrder();
    }

    @ApiOperation("根据keyID获取订单信息")
    @RequestMapping(value = "/order/{keyID}",method = RequestMethod.GET)
    public String getOrder(@PathVariable Integer keyID){
        return orderService.getOrderById(keyID);
    }
}
