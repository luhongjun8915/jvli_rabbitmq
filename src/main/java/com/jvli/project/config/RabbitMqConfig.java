package com.jvli.project.config;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;  
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import com.jvli.project.rabbitmqlistener.SimpleListener;
import com.jvli.project.rabbitmqlistener.UserOrderListener;

@Configuration
public class RabbitMqConfig {

	private static final Logger logger = LoggerFactory.getLogger(RabbitMqConfig.class);

	@Autowired
	private Environment env;

	@Autowired
	private CachingConnectionFactory connectionFactory;

	@Autowired
	private SimpleRabbitListenerContainerFactoryConfigurer factoryConfigurer;

	@Autowired
	private SimpleListener simpleListener;

	/**
	 * 
	 * @return
	 * @Description: 单一消费者
	 * @Date: 2021年1月1日 下午8:50:56
	 * @Author: luhongjun
	 */
	@Bean(name = "singleListenerContainer")
	public SimpleRabbitListenerContainerFactory listenerContainer() {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		factory.setMessageConverter(new Jackson2JsonMessageConverter());
		factory.setConcurrentConsumers(1);
		factory.setMaxConcurrentConsumers(1);
		factory.setPrefetchCount(1);
		factory.setTxSize(1);
		return factory;
	}

	/**
	 * 
	 * @return
	 * @Description: 创建多个消费者
	 * @Date: 2021年1月1日 下午8:57:36
	 * @Author: luhongjun
	 */
	@Bean(name = "multiListenerContainer")
	public SimpleRabbitListenerContainerFactory multiListenerContainer() {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factoryConfigurer.configure(factory, connectionFactory);
		factory.setMessageConverter(new Jackson2JsonMessageConverter());
		factory.setAcknowledgeMode(AcknowledgeMode.NONE);
		factory.setConcurrentConsumers(env.getProperty("spring.rabbitmq.listener.concurrency", int.class));
		factory.setMaxConcurrentConsumers(env.getProperty("spring.rabbitmq.listener.max-concurrency", int.class));
		factory.setPrefetchCount(env.getProperty("spring.rabbitmq.listener.prefetch", int.class));
		return factory;
	}

	@Bean
	public RabbitTemplate rabbitTemplate() {
		connectionFactory.setPublisherConfirms(true);
		connectionFactory.setPublisherReturns(true);
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMandatory(true);
		rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {

			@Override
			public void confirm(CorrelationData correlationData, boolean ack, String cause) {
				logger.info("消息发送成功：correlationData({}),ack({}),cause({})", correlationData, ack, cause);

			}
		});
		rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {

			@Override
			public void returnedMessage(Message message, int replyCode, String replyText, String exchange,
					String routingKey) {
				logger.info("消息丢失：exchange({}),route({}),replyCode({}),replyText({}),message:{}", exchange, routingKey,
						replyCode, replyText, message);
			}
		});
		return rabbitTemplate;
	}

	// TODO: 基本消息模型构建
	@Bean
	public DirectExchange basicExchange() {
		return new DirectExchange(env.getProperty("basic.info.mq.exchange.name"), true, false);
	}

	@Bean(name = "basicQueue")
	public Queue basicQueue() {
		return new Queue(env.getProperty("basic.info.mq.queue.name"), true);
	}

	@Bean
	public Binding basicBinding() {
		return BindingBuilder.bind(basicQueue()).to(basicExchange())
				.with(env.getProperty("basic.info.mq.routing.key.name"));
	}

	// TODO: 商品抢单消息模型创建
	@Bean
	public DirectExchange rabbitingExchange() {
		return new DirectExchange(env.getProperty("product.robbing.mq.exchange.name"), true, false);
	}

	@Bean(name = "robbingQueue")
	public Queue rabbitQueue() {
		return new Queue(env.getProperty("product.robbing.mq.queue.name"), true);
	}

	@Bean
	public Binding rabbitingBinding() {
		return BindingBuilder.bind(rabbitQueue()).to(rabbitingExchange())
				.with(env.getProperty("product.robbing.mq.routing.key.name"));
	}

	// TODO: 并发配置-消息手动确认机制
	// 1. 创建消息模型
	@Bean(name = "simpleQueue")
	public Queue simpleQueue() {
		return new Queue(env.getProperty("simple.mq.queue.name"), true); // true表示持久化
	}

	@Bean
	public TopicExchange simpleExchange() {
		return new TopicExchange(env.getProperty("simple.mq.exchange.name"), true, false); // true表示持久化，false表示不自动删除
	}

	@Bean
	public Binding simpleBinding() {
		return BindingBuilder.bind(simpleQueue()).to(simpleExchange())
				.with(env.getProperty("simple.mq.routing.key.name"));
	}
	
	// 2.创建并发、手动确认监听容器
	@Bean(name = "simpleContainer")
	public SimpleMessageListenerContainer simpleContainer(@Qualifier("simpleQueue") Queue simpleQueue) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		//container.setMessageConverter(new Jackson2JsonMessageConverter());
		// TODO：并发配置
		container.setConcurrentConsumers(env.getProperty("spring.rabbitmq.listener.concurrency", Integer.class));
		container.setMaxConcurrentConsumers(env.getProperty("spring.rabbitmq.listener.max-concurrency", Integer.class));
		container.setPrefetchCount(env.getProperty("spring.rabbitmq.listener.prefetch", Integer.class));

		// TODO：消息确认-确认机制种类
		container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
		container.setQueues(simpleQueue);
		container.setMessageListener(simpleListener);
		return container;
	}
	
	//TODO：用户商城下单实战
	//1.创建消息模型
	@Bean(name="userOrderQueue")
	public Queue userOrderQueue() {
		return new Queue(env.getProperty("user.order.queue.name"),true);
	}
	
	@Bean
	public TopicExchange userOrderExchange() {
		return new TopicExchange(env.getProperty("user.order.exchange.name"),true,false);
	}

	@Bean
	public Binding userOrderBinding() {
		return BindingBuilder.bind(userOrderQueue()).to(userOrderExchange()).with(env.getProperty("user.order.routing.key.name"));
	}
	
	//2.创建监听容器
	@Autowired
	private UserOrderListener userOrderListener;
	
	@Bean
	public SimpleMessageListenerContainer listenerContainerUserOrder(@Qualifier("userOrderQueue") Queue userOrderQueue) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		
		container.setQueues(userOrderQueue);
		container.setMessageListener(userOrderListener);
		
		container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
		return container;
	}
	
	//TODO： 日志消息模型
	
	//@Bean(name = "logSystenQueue")
	@Bean
	public Queue logSystemQueue() {
		return new Queue(env.getProperty("log.system.queue.name"),true);
	}
	
	@Bean
	public TopicExchange logSystemExchange() {
		return new TopicExchange(env.getProperty("log.system.exchange.name"),true,false);
	}
	
	@Bean
	public Binding logSystemBinding() {
		return BindingBuilder.bind(logSystemQueue()).to(logSystemExchange()).with(env.getProperty("log.system.routing.key.name"));
	}
	
	//TODO: 用户操作日志消息模型
	@Bean(name = "logUserQueue")
	public Queue LogUserQueue() {
		return new Queue(env.getProperty("log.user.queue.name"),true);
	}
	
	@Bean
	public DirectExchange LogUserExchange() {
		return new DirectExchange(env.getProperty("log.user.exchange.name"),true,false);
	}
	
	@Bean
	public Binding LogUserBinding() {
		return BindingBuilder.bind(LogUserQueue()).to(LogUserExchange()).with(env.getProperty("log.user.routing.key.name"));
	}
	
	//TODO: 发送邮件消息模型
	@Bean
	public Queue mailQueue() {
		return new Queue(env.getProperty("mail.queue.name"),true);
	}
	
	@Bean
	public DirectExchange mainExchange() {
		return new DirectExchange(env.getProperty("mail.exchange.name"),true,false);
	}
	
	@Bean
	public Binding mailBinding() {
		return BindingBuilder.bind(mailQueue()).to(mainExchange()).with(env.getProperty("mail.routing.key.name"));
	}
	
	
	//TODO: 死信队列消息模型
	// 创建死信队列
	@Bean
	public Queue simpleDeadQueue() {
		Map<String,Object> parmMap = new HashMap();
		parmMap.put("x-dead-letter-exchange", env.getProperty("simple.dead.exchange.name"));
		parmMap.put("x-dead-letter-routing-key", env.getProperty("simple.dead.routing.key.name"));
		parmMap.put("x-message-ttl", 10000);
		return new Queue(env.getProperty("simple.dead.queue.name"), true, false, false, parmMap);
	}
	
	//绑定死信队列-面向生产端
	@Bean
	public TopicExchange simpleDeadExchange() {
		return new TopicExchange(env.getProperty("simple.produce.exchange.name"),true,false);
	}
	
	@Bean
	public Binding simpleDeadBinding() {
		return BindingBuilder.bind(simpleDeadQueue()).to(simpleDeadExchange()).with(env.getProperty("simple.produce.routing.key.name"));
	}
	
	//创建并绑定实际监听消费队列
	@Bean
	public Queue simpleDeadRealQueue() {
		return new Queue(env.getProperty("simple.dead.real.queue.name"),true);
	}
	
	@Bean
	public TopicExchange simpleDeadRealExchange() {
		return new TopicExchange(env.getProperty("simple.dead.exchange.name"), true, false);
	}
	
	@Bean
	public Binding simpleDeadRealBinding() {
		return BindingBuilder.bind(simpleDeadRealQueue()).to(simpleDeadRealExchange()).with(env.getProperty("simple.dead.routing.key.name"));
	}
	
	// TODO: 用户下单超时未支付订单取消，死信队列消息模型
	@Bean
	public Queue userOrderDeadQueue() {
		Map<String, Object> parmMap = new HashMap<>();
		parmMap.put("x-dead-letter-exchange", env.getProperty("user.order.dead.exchange.name"));
		parmMap.put("x-dead-letter-routing-key", env.getProperty("user.order.dead.routing.key.name"));
		//parmMap.put("x-message", 10000);
		return new Queue(env.getProperty("user.order.dead.queue.name"),true,false,false,parmMap);
	}
	
	@Bean
	public TopicExchange userOrderDeadExchange() {
		return new TopicExchange(env.getProperty("user.order.dead.produce.exchange.name"), true, false);
	}
	
	@Bean
	public Binding userOrderDeadBinding() {
		return BindingBuilder.bind(userOrderDeadQueue()).to(userOrderDeadExchange()).with(env.getProperty("user.order.dead.produce.routing.key.name"));
	}
	
	//消费用户下单死信队列
	@Bean
	public Queue userOrderDeadRealQueue() {
		return new Queue(env.getProperty("user.order.dead.real.queue.name"),true);
	}
	
	@Bean
	public TopicExchange userOrderDeadRealExchange() {
		return new TopicExchange(env.getProperty("user.order.dead.exchange.name"));
	}
	
	@Bean
	public Binding userOrderDeadRealBinding() {
		return BindingBuilder.bind(userOrderDeadRealQueue()).to(userOrderDeadRealExchange()).with(env.getProperty("user.order.dead.routing.key.name"));
	}
	
	//TODO: 死信队列动态设置TTL消息模型
	@Bean
	public Queue dynamicDeadQueue() {
		Map<String,Object> parmMap = new HashMap();
		parmMap.put("x-dead-letter-exchange", env.getProperty("dynamic.dead.exchange.name"));
		parmMap.put("x-dead-letter-routing-key", env.getProperty("dynamic.dead.routing.key.name"));
		
		return new Queue(env.getProperty("dynamic.dead.queue.name"),true,false,false,parmMap);
	}
	
	@Bean
	public TopicExchange dynamicDeadExchange() {
		return new TopicExchange(env.getProperty("dynamic.dead.produce.exchange.name"), true, false);
	}
	
	@Bean
	public Binding dynamicDeadBinding() {
		return BindingBuilder.bind(dynamicDeadQueue()).to(dynamicDeadExchange()).with(env.getProperty("dynamic.dead.produce.routing.key.name"));
	}
	
	// 动态TTL死信队列消费、
	@Bean
	public Queue dynamicDeadRealQueue() {
		return new Queue(env.getProperty("dynamic.dead.real.queue.name"), true);
	}
	
	@Bean
	public TopicExchange dynamicDeadRealExchange() {
		return new TopicExchange(env.getProperty("dynamic.dead.exchange.name"));
	}
	
	@Bean
	public Binding dynamicDeadRealBinding() {
		return BindingBuilder.bind(dynamicDeadRealQueue()).to(dynamicDeadRealExchange()).with(env.getProperty("dynamic.dead.routing.key.name"));
	}
}
