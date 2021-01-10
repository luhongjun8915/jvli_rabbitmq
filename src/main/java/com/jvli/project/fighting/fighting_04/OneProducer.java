package com.jvli.project.fighting.fighting_04;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class OneProducer {

	private static final String Exchange_Name = "rabbit:mq04:exchange:e01";
	
	public static void main(String[] args) {
		try {
			//创建连接工厂、设置服务地址
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("127.0.0.1");
			//创建连接
			Connection connection = factory.newConnection();
			//创建通道
			Channel channel = connection.createChannel();
			
			String message = "topicExchange-publish消息";
			
			channel.basicPublish(Exchange_Name, "rabbit:mq04:routing:key:r.orange", null, message.getBytes("UTF-8"));
			channel.basicPublish(Exchange_Name, "rabbit:mq04:routing:key:r.orange.apple", null, message.getBytes("UTF-8"));
			
			System.out.println("生产者已发送消息>>>>");
			channel.close();
			connection.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
