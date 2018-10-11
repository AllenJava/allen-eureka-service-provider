package com.allen.modules.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.allen.modules.api.bo.CreateOrder;
import com.allen.modules.api.bo.OrderInfo;
import com.allen.modules.api.service.OrderInfoService;
import com.allen.modules.api.vo.Result;

@RestController
public class OrderInfoController {
	
	@Autowired
	private OrderInfoService orderInfoService;
	
	@RequestMapping(value="/api/order/queryList",method=RequestMethod.POST)
	@ResponseBody
	public Result<List<OrderInfo>> queryList(){
		return new Result<List<OrderInfo>>(this.orderInfoService.findOrderList());
	}
	
	@RequestMapping(value="/api/order/create",method=RequestMethod.POST)
	@ResponseBody
	public Result<Object> createOrder(@RequestBody(required=true) CreateOrder request){
		return new Result<>(this.orderInfoService.createOrder(request));
	} 

	@RequestMapping(value="/api/order/getOrderToken")
    @ResponseBody
	public Result<String> getOrderToken(){
	    return new Result<String>(this.orderInfoService.getOrderToken());
	}

}
