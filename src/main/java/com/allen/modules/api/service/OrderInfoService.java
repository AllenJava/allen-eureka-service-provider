package com.allen.modules.api.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.allen.modules.api.bo.OrderInfo;
import com.allen.modules.api.dao.StockMapper;
import com.allen.modules.api.dao.StockOrderMapper;
import com.allen.modules.api.po.Stock;
import com.allen.modules.api.po.StockOrder;


@Service
@Transactional
public class OrderInfoService {
	
	@Resource
	private StockMapper stockMapper;
	
	@Resource
	private StockOrderMapper stockOrderMapper;
	
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
	
	/**
	 * 下单服务
	 */
	public Integer createOrder(Integer sid){
		//检查库存
		Stock stock=this.checkStock(sid);
		
		//扣减库存
		//this.saleStock(stock);
		
		//扣减库存（乐观锁）（防止超卖）
		this.saleStockByCAS(stock);

		//下单
		return this.newOrder(stock);
	}
	
	/**
	 * 检查库存
	 */
	public Stock checkStock(Integer sid){
		Stock stock=this.stockMapper.selectByPrimaryKey(sid);
		if(stock.getCount().compareTo(stock.getSale())<=0){
			throw new RuntimeException("库存不足!");
		}
		return stock;
	}
	
	/**
	 * 扣减库存
	 */
	public void saleStock(Stock stock){
		Stock updateEntity=new Stock();
		updateEntity.setSale(stock.getSale()+1);
		updateEntity.setId(stock.getId());
		this.stockMapper.updateByPrimaryKeySelective(updateEntity);
	}
	
	/**
	 * 扣减库存（乐观锁）
	 */
	public void saleStockByCAS(Stock stock){
		Stock updateEntity=new Stock();
		updateEntity.setSale(stock.getSale()+1);
		updateEntity.setId(stock.getId());
		updateEntity.setVersion(stock.getVersion());
		int count=this.stockMapper.updateByCAS(updateEntity);
		if(count==0)
			throw new RuntimeException("并发更新失败!");
	}
	
	/**
	 * 下单
	 */
	public Integer newOrder(Stock stock){
		StockOrder order=new StockOrder();
		order.setSid(stock.getId());
		order.setName(stock.getName());
		order.setCreateTime(new Date());
		return this.stockOrderMapper.insertSelective(order);
	}

}
