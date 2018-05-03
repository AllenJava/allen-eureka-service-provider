package com.allen.modules.api.vo;

import org.springframework.http.HttpStatus;

public class Result<T> {
	
	private int code;
	
	private String message;
	
	private T data;
	
	public Result(){};
	
	public Result(T data){
		this.code=HttpStatus.OK.value();
		this.message=HttpStatus.OK.getReasonPhrase();
		this.data=data;
	}
	
	public Result(int code,String message){
		this.code=code;
		this.message=message;
	};

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "Result [code=" + code + ", message=" + message + ", data=" + data + "]";
	}		

}
