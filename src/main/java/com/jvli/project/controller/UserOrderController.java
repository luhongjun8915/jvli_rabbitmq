package com.jvli.project.controller;

import java.awt.PageAttributes.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.AbstractJavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jvli.project.dto.LogDto;
import com.jvli.project.dto.UserOrderDto;
import com.jvli.project.entity.UserOrder;
import com.jvli.project.mapper.UserOrderMapper;
import com.jvli.project.response.BaseResponse;
import com.jvli.project.response.StatusCode;
import com.jvli.project.service.CommonLogService;

@RestController
@RequestMapping("/user/order")
public class UserOrderController {
	
	public static final Logger logger = LoggerFactory.getLogger(UserOrderController.class);
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private Environment env;
	
	@Autowired
	private CommonLogService logService;
	
	@Autowired
	private UserOrderMapper userOrdrerMapper;
	
	/**
	 * 用户商城下单
	 */
	@RequestMapping(value = "/push",method = RequestMethod.POST)
	public BaseResponse pushUserOrder(@RequestBody UserOrderDto dto) {
		BaseResponse<Object> response = new BaseResponse<>(StatusCode.SUCCESS);
		try {
			logger.info("接收到数据：{}",dto);
			
			//TODO: 用户下单记录-入库
			rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
			rabbitTemplate.setExchange(env.getProperty("user.order.exchange.name"));
			rabbitTemplate.setRoutingKey(env.getProperty("user.order.routing.key.name"));
			Message msg = MessageBuilder.withBody(objectMapper.writeValueAsBytes(dto)).setDeliveryMode(MessageDeliveryMode.PERSISTENT).build();
			rabbitTemplate.convertAndSend(msg);
		} catch (Exception e) {
			logger.error("用户商城下单异常：{}",e.fillInStackTrace());
			e.printStackTrace();
		}
		
		//TODO: 系统级别-日志记录-异步解耦
		try {
			rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
			rabbitTemplate.setExchange(env.getProperty("log.system.exchange.name"));
			rabbitTemplate.setRoutingKey(env.getProperty("log.system.routing.key.name"));
			
			LogDto logDto = new LogDto("pushUserOrder", objectMapper.writeValueAsString(dto));
			rabbitTemplate.convertAndSend(logDto,new MessagePostProcessor() {		
				@Override
				public Message postProcessMessage(Message message) throws AmqpException {
					MessageProperties properties = message.getMessageProperties();
					properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
					properties.setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME, LogDto.class);
					return message;
				}
			});
			
			//TODO: 还有很多业务逻辑...
			logger.info("主线程还是照样坦荡荡的往前走。。。。。。");
		} catch (Exception e) {
			logger.error("记录系统日志发生异常：{}",e.fillInStackTrace());
		}
		return response;
				
	}
	
	/**
	 * 用户商城下单
	 */
	@RequestMapping(value = "/pushbydeadqueue",method = RequestMethod.POST)
    public BaseResponse pushUserOrderV2(@RequestBody UserOrderDto dto){
        BaseResponse response=new BaseResponse(StatusCode.SUCCESS);
        UserOrder userOrder=new UserOrder();
        try {
            BeanUtils.copyProperties(dto,userOrder);
            userOrder.setStatus(1);
            userOrdrerMapper.insertSelective(userOrder);
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            Integer id=userOrder.getId();

            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
            rabbitTemplate.setExchange(env.getProperty("user.order.dead.produce.exchange.name"));
            rabbitTemplate.setRoutingKey(env.getProperty("user.order.dead.produce.routing.key.name"));
            
            rabbitTemplate.convertAndSend(id, new MessagePostProcessor() {
                @Override
                public Message postProcessMessage(Message message) throws AmqpException {
                    MessageProperties properties=message.getMessageProperties();
                    properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    properties.setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME,Integer.class);
                    
                    properties.setExpiration("10000");
                    return message;
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }

        return response;
    }
	
	/**
	 * 用户商城下单-动态TTL设置
	 */
	@RequestMapping(value = "/pushwithttl",method = RequestMethod.POST)
	public BaseResponse pushUserOrder3(@RequestBody UserOrderDto dto) {
		BaseResponse response = new BaseResponse<>(StatusCode.SUCCESS);
		UserOrder userOrder = new UserOrder();
		try {
			BeanUtils.copyProperties(dto, userOrder);
			userOrder.setStatus(1);
			userOrdrerMapper.insertSelective(userOrder);
			logger.info("用户商城下单成功！");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			Integer id = userOrder.getId();
			
			rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
			rabbitTemplate.setExchange(env.getProperty("dynamic.dead.produce.exchange.name"));
			rabbitTemplate.setRoutingKey(env.getProperty("dynamic.dead.produce.routing.key.name"));
			
			int ttl = 10000; //根据业务场景可以设置生成随机数，实现错峰消费
			
			rabbitTemplate.convertAndSend(id, new MessagePostProcessor() {
				
				@Override
				public Message postProcessMessage(Message message) throws AmqpException {
					MessageProperties properties = message.getMessageProperties();
					properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
					properties.setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME, Integer.class);
					
					properties.setExpiration(String.valueOf(ttl));
					return message;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}
	
	
	
	
	
}
