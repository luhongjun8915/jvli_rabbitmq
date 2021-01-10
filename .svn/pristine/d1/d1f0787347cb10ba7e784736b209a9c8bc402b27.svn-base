package com.jvli.project.rabbitmqlistener;

import org.slf4j.Logger; 
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;

@Component("simpleListener")
public class SimpleListener implements ChannelAwareMessageListener, MessageListener{

	private static final Logger logger = LoggerFactory.getLogger(SimpleListener.class);
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Override
	public void onMessage(Message message, Channel channel) throws Exception {
		long tag = message.getMessageProperties().getDeliveryTag();
		
		try {
			byte[] body = message.getBody();
	        //User user=objectMapper.readValue(body,User.class);
			String msg = new String(body,"UTF-8");
			logger.info("当前线程：{},简单消息监听确认机制监听到消息：{}",Thread.currentThread().getName(),msg);
			
			//int i=1/0;  测试抛出异常
			
			//手动确认
			channel.basicAck(tag, true);
		} catch (Exception e) {
			logger.error("简单消息监听确认机制发生异常：{}",e.fillInStackTrace());
			channel.basicReject(tag, false);
		}
		
	}

}
