package com.jvli.project.fighting.fighting_02;

import java.io.IOException;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

public class MultiConsumerOne {
	
	private static final String Exchange_Name = "rabbit:mq02:exchange:e01";
	private static final String Queue_Name_01 = "rabbit:mq02:queueL:q01";
	
	public static void main(String[] args) {
		try {
			//创建连接工厂，设置服务地址
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("127.0.0.1");
			//创建连接、创建通道
			Connection connetion = factory.newConnection();
			Channel channel = connetion.createChannel();
			//声明交换机、声明队列、绑定队列
			channel.exchangeDeclare(Exchange_Name, BuiltinExchangeType.FANOUT);
			channel.queueDeclare(Queue_Name_01,true,false, false, null);
			channel.queueBind(Queue_Name_01, Exchange_Name, "");			
			//创建消费者
			Consumer  consumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties,
						byte[] body) throws IOException {
				    String message = new String(body, "UTF-8");
			        System.out.println("MultiConsumerOne"
			        		+ "消费消息---> "+message);
				}
			};	
			channel.basicConsume(Queue_Name_01, true, consumer);
			//消费消息
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
