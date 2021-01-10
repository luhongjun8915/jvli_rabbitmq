package com.jvli.project.fighting.fighting_qos;

import com.rabbitmq.client.*; 
import java.io.IOException;

public class QosConsumer {
	public static void main(String[] args) throws Exception {
		// 1. 创建一个 ConnectionFactory 并进行设置
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("127.0.0.1");
		factory.setAutomaticRecoveryEnabled(true);
		factory.setNetworkRecoveryInterval(3000);

		// 2. 通过连接工厂来创建连接
		Connection connection = factory.newConnection();

		// 3. 通过 Connection 来创建 Channel
		Channel channel = connection.createChannel();

		// 4. 声明
		String exchangeName = "test_qos_exchange";
		String queueName = "test_qos_queue";
		String routingKey = "item.#";
		channel.exchangeDeclare(exchangeName, BuiltinExchangeType.DIRECT);
		channel.queueDeclare(queueName, true, false, false, null);
		channel.queueBind(queueName, exchangeName, routingKey);

		// 限流api测试，每次消费3条
		channel.basicQos(0, 3, false);

		// 5. 创建消费者并接收消息
		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				String message = new String(body, "UTF-8");
				System.out.println("消费消息： '" + message + "'");

				channel.basicAck(envelope.getDeliveryTag(), true);
			}
		};
		// 6. 设置 Channel 消费者绑定队列
		channel.basicConsume(queueName, false, consumer);
	}
}
