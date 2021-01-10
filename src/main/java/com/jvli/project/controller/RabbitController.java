package com.jvli.project.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.sql.visitor.functions.Right;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.Deserializers.Base;
import com.jvli.project.dto.User;
import com.jvli.project.response.BaseResponse;
import com.jvli.project.response.StatusCode;

@RestController
@RequestMapping("/rabbit")
public class RabbitController {
	
	private static final Logger logger = LoggerFactory.getLogger(RabbitController.class);
	
	@Autowired
	private Environment env;
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	/**
	 * 发送简单消息
	 * @return 
	 */
	@RequestMapping(value = "/sendStr",method =RequestMethod.GET)
	public BaseResponse sendSimpleMessage(@RequestParam String message) {
		BaseResponse response = new BaseResponse<>(StatusCode.SUCCESS);
		try {
			logger.info("待发送的消息：{}",message);
			
			rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
			rabbitTemplate.setExchange(env.getProperty("basic.info.mq.exchange.name"));
			rabbitTemplate.setRoutingKey(env.getProperty("basic.info.mq.routing.key.name"));
			
			/*
			 * Message msg = MessageBuilder.withBody(message.getBytes("UTF-8")).build();
			 * rabbitTemplate.send(msg);
			 */
			
			Message msg = MessageBuilder.withBody(objectMapper.writeValueAsBytes(message)).build();
			rabbitTemplate.convertAndSend(msg);
		} catch (Exception e) {
			logger.error("发送简单的消息异常：{}",e.fillInStackTrace());
		}
		return response;
	}
	
	/*
	 * 发送对象消息
	 */
	@RequestMapping(value = "/sendObject",method =RequestMethod.POST)
	public BaseResponse sendObjectMsg(@RequestBody User user) {
		BaseResponse response = new BaseResponse<>(StatusCode.SUCCESS);
		try {
			logger.info("待发送的消息：{}",user);
			
			rabbitTemplate.setExchange(env.getProperty("basic.info.mq.exchange.name"));
			rabbitTemplate.setRoutingKey(env.getProperty("basic.info.mq.routing.key.name"));
			rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
			
			Message msg = MessageBuilder.withBody(objectMapper.writeValueAsBytes(user)).setDeliveryMode(MessageDeliveryMode.NON_PERSISTENT)
			.build();
			
			rabbitTemplate.convertAndSend(msg);
			
		} catch (Exception e) {
			logger.error("发送对象消息异常：{}",e.fillInStackTrace());
		}
		return response;
	}
	
	/**
	 * 发送多类字段消息
	 */
	@RequestMapping(value = "/sendMap",method =RequestMethod.GET)
	public BaseResponse sendMultiTypeMsg() {
		BaseResponse response = new BaseResponse<>(StatusCode.SUCCESS);
		try {
			Integer id = 120;
			String name = "阿修罗";
			Long userId = 12000L;
			Map<String, Object> dataMap = new HashMap<>();
			
			dataMap.put("id", id);
			dataMap.put("name", name);
			dataMap.put("userId", userId);
			logger.info("待发送的消息：{}",dataMap);
			
			rabbitTemplate.setExchange(env.getProperty("basic.info.mq.exchange.name"));
			rabbitTemplate.setRoutingKey(env.getProperty("basic.info.mq.routing.key.name"));
			rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
			
			Message msg = MessageBuilder.withBody(objectMapper.writeValueAsBytes(dataMap)).setDeliveryMode(MessageDeliveryMode.NON_PERSISTENT)
						  .build();
			rabbitTemplate.convertAndSend(msg);
		} catch (Exception e) {
			logger.error("发送多类型消息异常：{}",e.fillInStackTrace());
		}
		return response;
	}
}
