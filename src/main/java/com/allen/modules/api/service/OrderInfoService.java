package com.allen.modules.api.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.allen.modules.api.bo.OrderInfo;

@Service
public class OrderInfoService {
	
	public List<OrderInfo> findOrderList(){
		List<OrderInfo> resultList=new ArrayList<>();
		for (int i=1;i<=10;i++) {
			OrderInfo orderInfo=new OrderInfo();
			orderInfo.setOrderNo("orderNo"+i);
			orderInfo.setName("orderName"+i);
			orderInfo.setCreateTime(new Date());
			orderInfo.setCreator("creator"+i);
			resultList.add(orderInfo);
		}
		return resultList;
	}

}
