package com.jvli.project.fighting.fighting_03;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class OneProducer {
	private static final String Exchange_Name = "rabbit:mq03:exchange:e01";
	
	private static final String Queue_Name_01 = "rabbit:mq03:queue:q01";
	private static final String Queue_Name_02 = "rabbit:mq03:queue:q02";
	
	private static final String Routing_Key_01 = "rabbit:mq03:routing:key:r01";
	private static final String Routing_Key_02 = "rabbit:mq03:routing:key:r02";
	private static final String Routing_Key_03 = "rabbit:ma03:routing:key:r03";
	
	public static void main(String[] args) {
		try {
			//创建连接工厂，设置服务地址
			ConnectionFactory connectionFactory = new ConnectionFactory();
			connectionFactory.setHost("127.0.0.1");
			//创建通道
			Connection connection = connectionFactory.newConnection();
			Channel channel =  connection.createChannel();
			//声明交换机
			channel.exchangeDeclare(Exchange_Name, BuiltinExchangeType.DIRECT);
			//声明队列;绑定交换机、队列、routing—key
			channel.queueDeclare(Queue_Name_01, true, false, false, null);
			channel.queueBind(Queue_Name_01, Exchange_Name, Routing_Key_01);
			
			channel.queueDeclare(Queue_Name_02, true, false, false, null);
			channel.queueBind(Queue_Name_02, Exchange_Name, Routing_Key_02);
			channel.queueBind(Queue_Name_02, Exchange_Name, Routing_Key_03);
			
			String message01 = "directExchange-publish-r01";
			String message02 = "directExchange-publish-r02";
			String message03 = "directExchange-publish-r03";
			
			channel.basicPublish(Exchange_Name, Routing_Key_01, null, message01.getBytes("UTF-8"));
			channel.basicPublish(Exchange_Name, Routing_Key_02, null, message02.getBytes("UTF-8"));
			channel.basicPublish(Exchange_Name, Routing_Key_03, null, message03.getBytes("UTF-8"));
			
			System.out.println("生产者已发送消息>>>>>>>>>>>>>>>");
			channel.close();
			connection.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	
}
