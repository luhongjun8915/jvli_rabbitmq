package com.jvli.project.controller;

import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jvli.project.listenerevent.PushOrderRecordEvent;
import com.jvli.project.response.BaseResponse;
import com.jvli.project.response.StatusCode;

@RestController
@RequestMapping("/event")
public class OrderRecordController {
	
	private static final Logger logger = LoggerFactory.getLogger(OrderRecordController.class);
	
	@Autowired
	private ApplicationEventPublisher publisher;//注入spring驱动事件的发布者
	
	/**
	 * 
	 * @Description: 下单
	 * @return BaseResponse 
	 * @Date: 2020年12月31日  下午4:52:30
	 * @Author: luhongjun
	 */
	@RequestMapping(value = "/push",method = RequestMethod.GET)
	private BaseResponse pushOrder() {
		try {
			PushOrderRecordEvent orderRecordEvent = new PushOrderRecordEvent(this, UUID.randomUUID().toString(),(new Random().nextInt(999999)+100000)+"_order");
			publisher.publishEvent(orderRecordEvent);
		} catch (Exception e) {
			logger.error("下单异常：{}",e.fillInStackTrace());
			e.printStackTrace();
		}
		return new BaseResponse<>(StatusCode.SUCCESS);
	}
}
