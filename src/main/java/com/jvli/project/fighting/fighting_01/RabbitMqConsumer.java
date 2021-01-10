package com.jvli.project.fighting.fighting_01;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;

public class RabbitMqConsumer {
	
	private static final String QUEUE_NAME = "first_queue";
	
	public static void main(String[] args) {
		try {
			// 创建连接工厂,设置连接地址
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("127.0.0.1");
			// 创建连接
			Connection connection = factory.newConnection();
			// 创建通道
			Channel channel = connection.createChannel();
			// 声明队列
			channel.queueDeclare(QUEUE_NAME, true,false,false, null);
			// 创建消费者
			Consumer consumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
						byte[] body) throws IOException {
					String message = new String(body,"UTF-8");
					System.out.println("RabbitMQ condumedr消费消息");
					System.out.println(message);
				}
			};
			// 消费消息
			channel.basicConsume(QUEUE_NAME, true,consumer);
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
