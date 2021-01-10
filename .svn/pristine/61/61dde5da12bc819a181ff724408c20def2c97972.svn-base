package com.jvli.project.service;

import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class CommonMqService {
	
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CommonMqService.class);
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Autowired
	private Environment env;
	
	/**
	 * 发送抢单消息入队列
	 */
	public void sendRabbitingMsg(String mobile) {
		try {
			rabbitTemplate.setExchange(env.getProperty("product.robbing.mq.exchange.name"));
			rabbitTemplate.setRoutingKey(env.getProperty("product.robbing.mq.routing.key.name"));
			
			Message message = MessageBuilder.withBody(mobile.getBytes("UTF-8")).setDeliveryMode(MessageDeliveryMode.PERSISTENT)
						  .build();
			rabbitTemplate.send(message);
		} catch (Exception e) {
			logger.error("发送抢单消息入队列，发生异常：{}",mobile);
		}
	}
}
