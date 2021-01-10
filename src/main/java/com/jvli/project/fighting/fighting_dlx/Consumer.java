package com.jvli.project.fighting.fighting_dlx;

import java.io.IOException; 
import java.util.HashMap;
import java.util.Map;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class Consumer {

	public static final String PRODUCER_EXCHANGE = "producer_exchange";
	public static final String PRODUCER_ROUTING_KEY = "producer.#";
	
	public static final String DEAD_QUEUE = "dead_queue";
	public static final String DLX_PROPERTY = "abc";
	public static final String DLK_PROPERTY = "123";

	
	public static final String CONSUMER_QUEUE = "consumer_queue";


	public static void main(String[] args) {
		try {
			// 创建连接工厂,设置服务地址
			ConnectionFactory connectionFactory = new ConnectionFactory();
			connectionFactory.setHost("127.0.0.1");
			// 获取连接
			Connection connection = connectionFactory.newConnection();
			// 创建通道
			Channel channel = connection.createChannel();
			
			// 声明交换机、队列、绑定队列
			channel.exchangeDeclare(PRODUCER_EXCHANGE, BuiltinExchangeType.TOPIC, true, false, null);
			
			Map<String, Object> parmMap = new HashMap<>();
			parmMap.put("x-dead-letter-exchange", DLX_PROPERTY);
			parmMap.put("x-dead-letter-routing-key", DLK_PROPERTY);
			channel.queueDeclare(DEAD_QUEUE, true, false, false, parmMap);
			channel.queueBind(DEAD_QUEUE, PRODUCER_EXCHANGE, PRODUCER_ROUTING_KEY);
			
			// 声明死信队列的交换机、队列、routingKey并绑定
			channel.exchangeDeclare(DLX_PROPERTY, BuiltinExchangeType.TOPIC, true, false, null);
			channel.queueDeclare(CONSUMER_QUEUE, true, false, false, null);
			channel.queueBind(CONSUMER_QUEUE, DLX_PROPERTY, DLK_PROPERTY);
			// 消费
			channel.basicConsume(CONSUMER_QUEUE, true, new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties,
						byte[] body) throws IOException {
					System.out.println("-----------consume message----------");
					System.out.println("consumerTag: " + consumerTag);
					System.out.println("envelope: " + envelope);
					System.out.println("properties: " + properties);
					System.out.println("body: " + new String(body));
				}

			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
