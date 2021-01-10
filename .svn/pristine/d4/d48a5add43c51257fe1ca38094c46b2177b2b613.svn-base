package com.jvli.project.fighting.fighting_04;

import java.io.IOException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class MultiConsumerOne {
	
	private static final String Exchange_Name = "rabbit:mq04:exchange:e01";
	
	private static final String Queue_Name_01 = "rabbit:mq04:queue:q01";
	private static final String Routing_Key_01 = "rabbit:mq04:routing:key:r.*";
	
	public static void main(String[] args) {
		try {
			ConnectionFactory factory = new ConnectionFactory();//创建连接工厂
			factory.setHost("127.0.0.1");//设置服务地址
			Connection connection = factory.newConnection();//创建连接
			Channel channel = connection.createChannel();//创建通道
			
			channel.exchangeDeclare(Exchange_Name, BuiltinExchangeType.TOPIC);//声明交换机名称、类型
			channel.queueDeclare(Queue_Name_01, true, false, false, null);
			channel.queueBind(Queue_Name_01, Exchange_Name, Routing_Key_01);
			
			Consumer consumer = new DefaultConsumer(channel) {

				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties,
						byte[] body) throws IOException {
					String message = new String (body, "UTF-8");
					System.out.println("MultiConsumerOne消费消息-->" + message);
				}
				
			};
			channel.basicConsume(Queue_Name_01,true,consumer);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
