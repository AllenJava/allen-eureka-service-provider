package com.allen.modules.api.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.allen.modules.api.bo.CreateOrder;
import com.allen.modules.api.bo.OrderInfo;
import com.allen.modules.api.dao.OrderTokenInfoMapper;
import com.allen.modules.api.dao.StockMapper;
import com.allen.modules.api.dao.StockOrderMapper;
import com.allen.modules.api.po.OrderTokenInfo;
import com.allen.modules.api.po.Stock;
import com.allen.modules.api.po.StockOrder;


@Service
@Transactional
public class OrderInfoService {
	
	@Resource
	private StockMapper stockMapper;
	
	@Resource
	private StockOrderMapper stockOrderMapper;
	
	@Resource
	private OrderTokenInfoMapper orderTokenInfoMapper;
	
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
	 * 生产下单凭证
	 */
	public String getOrderToken(){
	    //需优化（比如"用户通过抓包，可以无限获取orderToken,这样就可无限下单"）
	    String uuId=UUID.randomUUID().toString();
	    OrderTokenInfo orderToken=new OrderTokenInfo();
	    orderToken.setOrderToken(uuId);
	    orderToken.setOrderStatus(OrderTokenStaus.PLACEING.code);
	    orderToken.setCreateTime(new Date());
	    int result=this.orderTokenInfoMapper.insert(orderToken);
	    if(result==0){
	        throw new RuntimeException("获取下单凭证失败!");
	    }
	    return orderToken.getOrderToken();
	}
	
	/**
	 * 下单服务
	 */
	public Integer createOrder(CreateOrder createOrder){
	    if(StringUtils.isEmpty(createOrder.getOrderToken())){
	        throw new RuntimeException("下单凭证不能为空!");
	    }
	    if(createOrder.getSid()==null){
	        throw new RuntimeException("商品id不能为空!");
	    }
	    //检查下单凭证
	    OrderTokenInfo orderToken=this.orderTokenInfoMapper.selectByOrderToken(createOrder.getOrderToken());
	    if(orderToken==null){
	        throw new RuntimeException("下单凭证无效!");
	    }
	    if(orderToken.getOrderStatus()==OrderTokenStaus.FINISHED.code){
	        throw new RuntimeException("下单凭证已使用，即已失效!");
	    }
	    
	    //根据orderToken加单机锁（分布式环境则加分布式锁）
	    if(!SingleMechineLock.tryLock(orderToken.getOrderToken())){
	        throw new RuntimeException("该订单正在下单中，请勿重复操作!");
	    }
	    int result=0;
	    try {
	        //睡眠2s，模拟复杂的下单业务
	        try {
	            TimeUnit.SECONDS.sleep(2);
	        } catch (InterruptedException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        
	        //检查库存
	        Stock stock=this.checkStock(createOrder.getSid());
	        
	        //扣减库存（乐观锁）（防止超卖）
	        this.saleStockByCAS(stock);

	        //下单
	        result=this.newOrder(stock);
	        if(result==0){
	            throw new RuntimeException("下单失败!");
	        }
	        
	        //更新下单凭证状态
	        orderToken.setOrderStatus(OrderTokenStaus.FINISHED.code);
	        orderToken.setCreateTime(new Date());
	        this.orderTokenInfoMapper.updateByPrimaryKeySelective(orderToken);
        } finally {
            //释放单机锁
            SingleMechineLock.releaseLock(orderToken.getOrderToken());
        }
		return result;
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
	
	/**
	 * 
	* @ClassName: OrderTokenStaus
	* @Description: 下单凭证状态
	* @author chenliqiao
	* @date 2018年8月8日 下午4:24:45
	*
	 */
	public enum OrderTokenStaus{
	    PLACEING(0,"未使用"),
	    FINISHED(1,"已使用");
	    
	    private int code;
	    private String msg;
	    
	    private OrderTokenStaus(int code,String msg){
	        this.code=code;
	        this.msg=msg;
	    }
	    
        public int getCode() {
            return code;
        }
        public void setCode(int code) {
            this.code = code;
        }
        public String getMsg() {
            return msg;
        }
        public void setMsg(String msg) {
            this.msg = msg;
        }
	}	
	
	/**
	 * 
	* @ClassName: SingleMechineLock
	* @Description: 单机锁
	* @author chenliqiao
	* @date 2018年8月8日 下午11:41:43
	*
	 */
	public static class SingleMechineLock{
	    
	    private static final CopyOnWriteArrayList<Object> list=new CopyOnWriteArrayList<>();
	    
	    /**
	     * 尝试获取锁
	     */
	    public static boolean tryLock(Object object){
	        return list.addIfAbsent(object);
	    }
	    
	    /**
	     * 释放锁
	     */
	    public static void releaseLock(Object object){
	        list.remove(object);
	    }
	}

}
