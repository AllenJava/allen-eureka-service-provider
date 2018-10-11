package com.allen.modules.api.dao;

import com.allen.modules.api.po.OrderTokenInfo;

public interface OrderTokenInfoMapper {
    
    int deleteByPrimaryKey(Integer id);

    int insert(OrderTokenInfo record);

    int insertSelective(OrderTokenInfo record);

    OrderTokenInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderTokenInfo record);

    int updateByPrimaryKey(OrderTokenInfo record);
    
    /**
     * 根据orderToken查询
     */
    OrderTokenInfo selectByOrderToken(String orderToken);
}