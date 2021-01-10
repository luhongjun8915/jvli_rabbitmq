package com.jvli.project.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jvli.project.response.BaseResponse;
import com.jvli.project.response.StatusCode;
import com.jvli.project.service.InitService;

@RestController
@RequestMapping("/concurrenty")
public class ConcurrencyController {
	
	private static final Logger logger = LoggerFactory.getLogger(ConcurrencyController.class);
	
	@Autowired
	private InitService initService;
	
	@RequestMapping(value = "/robbing/thread",method = RequestMethod.GET)
	public BaseResponse robbingThread() {
		BaseResponse baseResponse = new BaseResponse<>(StatusCode.SUCCESS);
		try {
			initService.generateMultiThread();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return baseResponse;
	}
	
}
