package com.jvli.project.rabbitmqlistener;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.jvli.project.entity.UserOrder;
import com.jvli.project.mapper.UserOrderMapper;

@Component
public class UserOrderDeadListener {
	
	private static final Logger logger = LoggerFactory.getLogger(UserOrderDeadListener.class);

	@Autowired
	private UserOrderMapper userOrderMapper;
	
	@RabbitListener(queues = "${user.order.dead.real.queue.name}",containerFactory = "multiListenerContainer")
	private void consumeMessage(@Payload Integer id) {
		try {
			logger.info("死信队列-用户下单超时未支付监听消息：{}",id);
			
			UserOrder userOrder = userOrderMapper.selectByPrimaryKey(id);
			if(userOrder != null) {//说明用户未支付，取消订单，将状态改为3
				userOrder.setStatus(3);
				userOrder.setUpdateTime(new Date());
				userOrderMapper.updateByPrimaryKeySelective(userOrder);
			}else {
				//TODO: 已支付-可能需要异步  减库存；异步发送其他日志消息
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RabbitListener(queues = "${dynamic.dead.real.queue.name}",containerFactory = "multiListenerContainer")
	private void consumeDynamicTtlMsg(@Payload Integer id) {
		try {
			logger.info("死信队列-动态TTL-监听到消息：{}",id);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
