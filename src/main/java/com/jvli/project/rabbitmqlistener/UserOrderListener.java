package com.jvli.project.rabbitmqlistener;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jvli.project.dto.UserOrderDto;
import com.jvli.project.entity.UserOrder;
import com.jvli.project.mapper.UserOrderMapper;
import com.rabbitmq.client.Channel;

//@Component("userOrderListener")
@Component
public class UserOrderListener implements ChannelAwareMessageListener{
	
	private static final Logger logger = LoggerFactory.getLogger(UserOrderListener.class);
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private UserOrderMapper userOrderMapper;

	@Override
	public void onMessage(Message message, Channel channel) throws Exception {
		long tag = message.getMessageProperties().getDeliveryTag();
		try {
			byte[] body=message.getBody();
			UserOrderDto entity = objectMapper.readValue(body, UserOrderDto.class);
			logger.info("用户商城下单监听到消息：{}",entity);
			
			UserOrder userOrder = new UserOrder();
			BeanUtils.copyProperties(entity, userOrder);
			userOrder.setStatus(1);
			userOrderMapper.insertSelective(userOrder);
			
			channel.basicAck(tag, true);
		} catch (Exception e) {
			logger.error("用户商城下单发生异常：{}",e.fillInStackTrace());
			channel.basicReject(tag, false);
		}
		
	}
	
	
}
