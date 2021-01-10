package com.jvli.project.rabbitmqlistener;

import java.util.Map; 

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jvli.project.dto.User;

@Component
public class CommonListener {

	private static final Logger logger = LoggerFactory.getLogger(CommonListener.class);

	@Autowired
	private ObjectMapper objectMapper;

	/**
	 * 
	 * @Description: 监听消息
	 * @Date: 2021年1月1日 下午9:55:38
	 * @Author: luhongjun
	 */
	@RabbitListener(queues = "${basic.info.mq.queue.name}", containerFactory = "singleListenerContainer")
	public void consumerMessage(@Payload Message message) {
		try {
			System.out.println("正在监听消息>>>>>>>>>>>>>>>>" + message);
			byte[] body = message.getBody();
			String msg = new String(body,"UTF-8");
			logger.info("监听消费消息：{}",msg);
			// 接受String 消息
			/*
			 * String result = new String(message,"UTF-8");
			 * logger.info("接受到的消息：{}",message);
			 */
			// 接受对象
			/*
			 * User user = objectMapper.readValue(message, User.class);
			 * logger.info("接受到的消息：{}",user);
			 */
			// 接受map
			
			/*
			 * Map<String,Object> resultMap = objectMapper.readValue(message, Map.class);
			 * logger.info("接受到的消息：{}",resultMap);
			 */
			 
		} catch (Exception e) {
			logger.error("监听缴费异常：{}", e.fillInStackTrace());
		}

	}
}
