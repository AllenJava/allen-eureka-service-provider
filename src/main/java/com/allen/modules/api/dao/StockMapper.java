package com.allen.modules.api.dao;

import com.allen.modules.api.po.Stock;

public interface StockMapper {
	
    int deleteByPrimaryKey(Integer id);

    int insert(Stock record);

    int insertSelective(Stock record);

    Stock selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Stock record);

    int updateByPrimaryKey(Stock record);
    
    /**
     * 根据乐观锁更新
     */
    int updateByCAS(Stock record);
}