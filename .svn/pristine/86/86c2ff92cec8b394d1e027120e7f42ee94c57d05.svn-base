package com.jvli.project.fighting.fighting_02;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class OneProducer {
	private static final String Exchange_Name = "rabbit:mq02:exchange:e01";
	
	public static void main(String[] args) {
		try {
			//创建连接工厂，设置服务地址
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("127.0.0.1");
			//创建连接
			Connection connection = factory.newConnection();
			//创建通道
			Channel channel = connection.createChannel();
			//声明交换机和交换机类型
			channel.exchangeDeclare(Exchange_Name, BuiltinExchangeType.FANOUT);
			String message = "OneProducer-publish消息";
			channel.basicPublish(Exchange_Name, "", null, message.getBytes("UTF-8"));		
			System.out.println("OneProducer消息已发送-->");
			channel.close();
			connection.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
