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

public class MultiConsumerOne {
	private static final String Exchange_Name = "rabbit:mq03:exchange:e01";
	
	private static final String Queue_Name_01 = "rabbit:mq03:queue:q01";
	private static final String Routing_Key_01 = "rabbit:mq03:routing:key:k01";
	
	public static void main(String[] args) {
		try {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("127.0.0.1");
			Connection connection = factory.newConnection();
			Channel channel = connection.createChannel();
			//声明交换机、声明队列；绑定交换机、队列及routingkey
			channel.exchangeDeclare(Exchange_Name, BuiltinExchangeType.DIRECT);
			channel.queueDeclare(Queue_Name_01,true,false,false,null);
			
			// 限流api测试，每次消费3条
			channel.basicQos(0, 3, false);
			
			channel.queueBind(Queue_Name_01, Exchange_Name, Routing_Key_01);
			
			Consumer consumer = new DefaultConsumer(channel) {

				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties,
						byte[] body) throws IOException {
					String message = new String(body,"UTF-8");
					System.out.println("MultiConsumerOne消费消息>>>>>" + message);
					
					//限流api测试，手动 ack，并且设置批量处理 ack 回应为 true
					channel.basicAck(envelope.getDeliveryTag(), true);
				}
				
			};
			//channel.basicConsume(Queue_Name_01,true,consumer);
			//限流api测试，手动ack
			channel.basicConsume(Queue_Name_01,true,consumer);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}
}
