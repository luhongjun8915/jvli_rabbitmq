package com.jvli.project.controller;

import java.util.Date; 

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jvli.project.entity.User;
import com.jvli.project.entity.UserLog;
import com.jvli.project.mapper.UserLogMapper;
import com.jvli.project.mapper.UserMapper;
import com.jvli.project.response.BaseResponse;
import com.jvli.project.response.StatusCode;

@RestController
@RequestMapping("/user")
public class UserController {
	
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private UserLogMapper userLogMapper;
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Autowired
	private Environment env;
	
	@RequestMapping(value = "/login",method = RequestMethod.POST)
	public BaseResponse login(@RequestParam("userName") String userName,@RequestParam("password") String password) {
		BaseResponse response = new BaseResponse<>(StatusCode.SUCCESS);
		try {
			User user = userMapper.selectByUserNamePassword(userName, password);
			if(user!=null) {
				//TODO: 异步写日志
			    /*UserLog log=new UserLog(userName,"Login","login",objectMapper.writeValueAsString(user));
                userLogMapper.insertSelective(log);*/ //同步
				
				UserLog userLog = new UserLog(userName, "Login", "login", objectMapper.writeValueAsString(user));
				userLog.setCreateTime(new Date());
				rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
				rabbitTemplate.setExchange(env.getProperty("log.user.exchange.name"));
				rabbitTemplate.setRoutingKey(env.getProperty("log.user.routing.key.name"));
				
				/*MessageProperties properties=new MessageProperties();
                properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                properties.setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME, MessageProperties.CONTENT_TYPE_JSON);
                Message message=new Message(objectMapper.writeValueAsBytes(userLog),properties);*/ //发送消息写法一
			
				Message message = MessageBuilder.withBody(objectMapper.writeValueAsBytes(userLog)).setDeliveryMode(MessageDeliveryMode.PERSISTENT).build();
				message.getMessageProperties().setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME, MessageProperties.CONTENT_TYPE_JSON);//发送消息写法二
				
				rabbitTemplate.convertAndSend(message);
				
				
				// TODO: 塞权限数据-资源数据-视野数据
			}else {
				 response = new BaseResponse<>(StatusCode.FAIL);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}
	
	
}
