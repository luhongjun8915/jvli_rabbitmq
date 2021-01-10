package com.jvli.project.fighting.fighting_01;

import com.rabbitmq.client.Channel; 
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMqProducer {
	
	private static final String QUEUE_NAME = "first_queue";
	
	public static void main(String[] args) {
		try {
			// 创建连接工厂
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("127.0.0.1");
			// 创建连接
			Connection connection = factory.newConnection();
			// 创建通道
			Channel channel = connection.createChannel();
			// 声明队列，发送消息
			channel.queueDeclare(QUEUE_NAME, true, false, false, null);
			String message = "hello world";
			channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
			
			System.out.println("生产者已发送消息>>>>>>>>>>>>>");
			System.out.println(message);
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
