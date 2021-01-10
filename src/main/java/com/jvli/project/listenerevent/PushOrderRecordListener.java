package com.jvli.project.listenerevent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.jvli.project.entity.OrderRecord;
import com.jvli.project.mapper.OrderRecordMapper;

/**
 * 这就是监听器-跟RabbitMQ的Listener几乎是一个理念
 * @author Administrator
 *
 */
@Component
public class PushOrderRecordListener implements ApplicationListener<PushOrderRecordEvent>{
	
	private static final Logger log = LoggerFactory.getLogger(PushOrderRecordListener.class);
	
	@Autowired
	private OrderRecordMapper orderRecordMapper;
	
	@Override
	public void onApplicationEvent(PushOrderRecordEvent event) {
		// TODO spring事件驱动实现自己的业务
		log.info("监听到的下单记录：{}",event);
		try {
			if(event !=null) {
				OrderRecord orderRecord = new OrderRecord();
				BeanUtils.copyProperties(event, orderRecord);
				orderRecordMapper.insertSelective(orderRecord);
				
			}
		} catch (Exception e) {
			// TODO: handle exception
			log.error("监听下单记录发生异常：{},{}",event,e.fillInStackTrace());
			e.printStackTrace();
		}
	}

}
