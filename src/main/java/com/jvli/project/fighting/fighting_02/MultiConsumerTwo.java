package com.jvli.project.fighting.fighting_02;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;

public class MultiConsumerTwo {
	
	private static final String Exchange_Name = "rabbit:mq02:exchange:e01";
	private static final String Queue_Name_02 = "rabbit:mq02:queue:q02";
	
	public static void main(String[] args) {
		try {
			//创建连接工厂、设置服务地址
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("127.0.0.1");
			//创建连接
			Connection connection = factory.newConnection();
			//创建通道
			Channel channel = connection.createChannel();
			//声明交换机、声明队列、绑定队列
			channel.exchangeDeclare(Exchange_Name, BuiltinExchangeType.FANOUT);
			channel.queueDeclare(Queue_Name_02,true,false,false,null);
			channel.queueBind(Queue_Name_02, Exchange_Name, "");
			//创建消费者
			Consumer consumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, com.rabbitmq.client.Envelope envelope, com.rabbitmq.client.AMQP.BasicProperties properties, byte[] body) throws java.io.IOException {
				String message = new String(body,"UTF-8");
				System.out.println("MultiConsumerTwo消费消息：" + message);			
				}
			};
			//消费消息
			channel.basicConsume(Queue_Name_02,true,consumer);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
