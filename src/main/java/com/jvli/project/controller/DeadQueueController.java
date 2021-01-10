package com.jvli.project.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jvli.project.response.BaseResponse;
import com.jvli.project.response.StatusCode;

@RestController
@RequestMapping("/dead/queue")
public class DeadQueueController {
	
	private static final Logger logger = LoggerFactory.getLogger(DeadQueueController.class);
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private Environment env;
	@RequestMapping(value = "/send",method = RequestMethod.GET)
	public BaseResponse sendMsg() {
		BaseResponse response = new BaseResponse<>(StatusCode.SUCCESS);
		try {
			rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
			rabbitTemplate.setExchange(env.getProperty("simple.produce.exchange.name"));
			rabbitTemplate.setRoutingKey(env.getProperty("simple.produce.routing.key.name"));
			
			String str = "abc";
			Message message = MessageBuilder.withBody(objectMapper.writeValueAsBytes(str)).setDeliveryMode(MessageDeliveryMode.PERSISTENT).build();
			rabbitTemplate.convertAndSend(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}
}
