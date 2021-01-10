package com.jvli.project.rabbitmqlistener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jvli.project.service.ConcurrencyService;

@Component
public class RobbingListener {
	
	private static final Logger logger = LoggerFactory.getLogger(RobbingListener.class);
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private ConcurrencyService concurrencyService;
	
	/**
	 * 监听抢单消息
	 */
	@RabbitListener(queues = "${product.robbing.mq.queue.name}",containerFactory = "singleListenerContainer")
	public void consumeMessage(@Payload Message message){
		try {
			System.out.println("正在监听消息>>>>>>>>>>>>>>>>" + message);
			byte[] body = message.getBody();
			String msg = new String(body,"UTF-8");
			logger.info("监听到抢单手机号：{}",msg);
			
			concurrencyService.manageRabbiting(String.valueOf(msg));
		} catch (Exception e) {
			logger.error("监听抢单消息发生异常：{}",e.fillInStackTrace());
		}
	}
}
