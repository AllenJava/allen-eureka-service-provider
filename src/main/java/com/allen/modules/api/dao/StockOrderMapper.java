package com.allen.modules.api.dao;

import com.allen.modules.api.po.StockOrder;

public interface StockOrderMapper {
	
    int deleteByPrimaryKey(Integer id);

    int insert(StockOrder record);

    int insertSelective(StockOrder record);

    StockOrder selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(StockOrder record);

    int updateByPrimaryKey(StockOrder record);
}