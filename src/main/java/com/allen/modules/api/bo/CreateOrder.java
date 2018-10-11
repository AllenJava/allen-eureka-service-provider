package com.allen.modules.api.bo;

/**
 * 
* @ClassName: CreateOrder
* @Description: 下单请求参数类
* @author chenliqiao
* @date 2018年8月8日 下午3:15:19
*
 */
public class CreateOrder {
	
    /**商品id**/
	private Integer sid;
	
	/**下单凭证**/
	private String orderToken;

	public Integer getSid() {
		return sid;
	}

	public void setSid(Integer sid) {
		this.sid = sid;
	}

    public String getOrderToken() {
        return orderToken;
    }

    public void setOrderToken(String orderToken) {
        this.orderToken = orderToken;
    }
}
