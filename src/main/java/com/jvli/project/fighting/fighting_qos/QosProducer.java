package com.jvli.project.fighting.fighting_qos;
import com.rabbitmq.client.BuiltinExchangeType; 
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class QosProducer {
    public static void main(String[] args) throws Exception {
        //1. 创建一个 ConnectionFactory 并进行设置
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");

        //2. 通过连接工厂来创建连接
        Connection connection = factory.newConnection();

        //3. 通过 Connection 来创建 Channel
        Channel channel = connection.createChannel();
        
        
        //4. 声明
        String exchangeName = "test_qos_exchange";
        String queueName = "test_qos_queue";
        String routingKey = "item.add";
        
       	//声明交换机
		channel.exchangeDeclare(exchangeName, BuiltinExchangeType.DIRECT);
		//声明队列;绑定交换机、队列、routing—key
		channel.queueDeclare(queueName, true, false, false, null);
		channel.queueBind(queueName, exchangeName, routingKey);
    	

        //5. 发送
        String msg = "hello world，keep coding";
        for (int i = 0; i < 10; i++) {
            String tem = msg + "-" + i;
            channel.basicPublish(exchangeName, routingKey, null, tem.getBytes());
            System.out.println("Send message : " + tem);
        }

        //6. 关闭连接
        channel.close();
        connection.close();
    }
}
