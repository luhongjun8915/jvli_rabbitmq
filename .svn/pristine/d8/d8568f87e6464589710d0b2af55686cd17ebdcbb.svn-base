package com.jvli.project.fighting.fighting_03;

import java.io.IOException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class MultiConsumerTwo {
	private static final String Exchange_Name = "rabbit:mq03:exchange:e01";
	
	private static final String Queue_Name_02 = "rabbit:mq03:queue:q02";
	
	private static final String Routing_Key_02 = "rabbit:mq03:routing:key:k02";
	private static final String Routing_Key_03 = "rabbit:mq03:routing:key:k03";
	
	public static void main(String[] args) {
		try {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("127.0.0.1");
			Connection connection = factory.newConnection();//创建连接
			Channel channel = connection.createChannel();//创建通道
			
			channel.exchangeDeclare(Exchange_Name, BuiltinExchangeType.DIRECT);
			channel.queueDeclare(Queue_Name_02, true, false, false, null);
			channel.queueBind(Queue_Name_02, Exchange_Name, Routing_Key_02);
			channel.queueBind(Queue_Name_02, Exchange_Name, Routing_Key_03);
			
			Consumer consumer = new DefaultConsumer(channel) {

				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties,
						byte[] body) throws IOException {
					String message = new String(body,"UTF-8");
					System.out.println("MultiConsumerTwo消费消息>>>>>" + message);
				}
				
			};
			channel.basicConsume(Queue_Name_02, true,consumer);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
}
