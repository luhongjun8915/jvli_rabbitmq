package com.jvli.project.rabbitmqlistener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.jvli.project.dto.LogDto;

@Component
public class LogSystemListener {
	
	private static final Logger logger = LoggerFactory.getLogger(LogSystemListener.class);
	
	@RabbitListener(queues = "${log.system.queue.name}",containerFactory = "multiListenerContainer")
	public void consumeLogInfo(@Payload LogDto logDto) {
		try {
			logger.info("系统日志监听器监听到消息：{}",logDto);
			
		} catch (Exception e) {
			logger.error("系统日志监听器监听发生异常：{}",e.fillInStackTrace());
		}
	}
}
