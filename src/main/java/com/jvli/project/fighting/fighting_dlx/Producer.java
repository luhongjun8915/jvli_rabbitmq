package com.jvli.project.fighting.fighting_dlx;

import java.io.IOException; 
import java.util.concurrent.TimeoutException;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Producer {

	public static final String PRODUCER_EXCHANGE = "producer_exchange";
	public static final String PRODUCER_ROUTING_KEY = "producer.routing.key";

	public static void main(String[] args) throws IOException, TimeoutException {
		// 创建连接工厂
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("127.0.0.1");

		// 获取连接
		Connection connection = connectionFactory.newConnection();

		// 创建通道
		Channel channel = connection.createChannel();
		channel.exchangeDeclare(PRODUCER_EXCHANGE, BuiltinExchangeType.TOPIC, true, false, null);
		
		// 发布消息
		String msg = "dlx message tranfer";

		AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
				.deliveryMode(2)
				.contentEncoding("UTF-8")
				.expiration("10000")
				.build();
		channel.basicPublish(PRODUCER_EXCHANGE, PRODUCER_ROUTING_KEY, true, properties, msg.getBytes());
		System.out.println("生产者已发送消息>>>>>>>>>>>>");
		
		channel.close();
		connection.close();
	}
}
