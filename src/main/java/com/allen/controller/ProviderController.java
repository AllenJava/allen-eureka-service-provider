package com.allen.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProviderController {
	
	@RequestMapping(value="/add",method=RequestMethod.GET)
	public Integer add(Integer a,Integer b){
		return a+b;
	}

}
