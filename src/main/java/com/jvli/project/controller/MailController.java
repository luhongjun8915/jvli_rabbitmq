package com.jvli.project.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.AbstractJavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jvli.project.response.BaseResponse;
import com.jvli.project.response.StatusCode;
import com.jvli.project.service.MailService;

@RestController
@RequestMapping("/mail")
public class MailController {
	
	private static final Logger logger = LoggerFactory.getLogger(MailController.class);
	
	@Autowired
	private MailService mailService;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Autowired
	private Environment env;
	
	@RequestMapping(value = "/send",method = RequestMethod.GET)
	public BaseResponse sendMail() {
		BaseResponse<Object> response = new BaseResponse<>(StatusCode.SUCCESS);
		try {
			rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
			rabbitTemplate.setExchange(env.getProperty("mail.exchange.name"));
			rabbitTemplate.setRoutingKey(env.getProperty("mail.routing.key.name"));
			
			Message message = MessageBuilder.withBody(objectMapper.writeValueAsBytes("2021todo")).setDeliveryMode(MessageDeliveryMode.PERSISTENT).build();
			message.getMessageProperties().setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME, MessageProperties.CONTENT_TYPE_JSON);//发送消息写法二
			rabbitTemplate.convertAndSend(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("邮件发送完毕。。。");
		return response;
	}
}
