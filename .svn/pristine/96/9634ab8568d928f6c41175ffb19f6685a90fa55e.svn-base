package com.jvli.project.rabbitmqlistener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jvli.project.entity.UserLog;
import com.jvli.project.mapper.UserLogMapper;
import com.jvli.project.service.MailService;

@Component
public class CommonMqListener {
	
	private static final Logger logger = LoggerFactory.getLogger(CommonMqListener.class);
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private UserLogMapper userLogMapper;
	
	@Autowired
	private MailService mailService;
	
	/**
	 * 监听消费-用户日志
	 */
	@RabbitListener(queues = "${log.user.queue.name}",containerFactory = "singleListenerContainer")
	public void consumeUserLogQueue(@Payload Message message) {
		try {
			byte[] body = message.getBody();
			String msg = new String(body,"UTF-8");
			//UserLog userLog = objectMapper.readValue(message, UserLog.class);
			UserLog userLog = JSONObject.parseObject(msg, UserLog.class);
			logger.info("监听消费用户日志，监听到消息：{}",userLog);
			
			userLogMapper.insertSelective(userLog);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * 监听消费-邮件发送
	 */
	@RabbitListener(queues = "${mail.queue.name}",containerFactory = "singleListenerContainer")
	public void consumeMailQueue(@Payload Message message) {
		try {
			logger.info("监听消费邮件发送，监听到消息：{}",new String(message.getBody(),"UTF-8"));
			
			mailService.sendEmail();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 监听消费死信队列中的消息
	 */
	@RabbitListener(queues = "${simple.dead.real.queue.name}",containerFactory = "singleListenerContainer")
	public void consumeDeadQueue(@Payload Message message) {
		try {
			
			String msg = new String(message.getBody(),"UTF-8");
			logger.info("监听到死信队列消息：{}",msg);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
}
