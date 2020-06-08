package com.example.mqdemo.quickstart;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Consumer4topic {
    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        //1. 创建出链接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.0.134");
        factory.setPort(5672);
        factory.setUsername("root");
        factory.setPassword("123456");
        factory.setVirtualHost("/");

        //2. 通过链接工厂创建出链接对象
        Connection conn = factory.newConnection();

        //3. 通过链接对象创建channel
        Channel channel = conn.createChannel();


        // 声明队列
        /*
        * 第一个参数表示队列名称
        * 第二个参数表示是否持久化消息队列
        * 第三个参数表示消息队列是否被channel独占，false:表示这个消息队列可以被其它channel占有
        * 第四个参数表示是否自动删除，当队列没有绑定交换机就自动删除
        * 第五个参数是扩展参数
        * */
        String queueName = "test_queue";
        channel.queueDeclare(queueName,true,false,false,null);

        //声明交换机
        String exchangeName = "test_topic";
        String exchangeType = "topic";
        String routingKey = "test.#";
        /*
        * 第一个参数：交换机的名称
        * 第二个参数：交换机的类型
        * 第三个参数：是否持久化
        * 第四个参数：是否自动删除
        * 第五个参数：是否用于rabbitMQ内部
        * 第六个参数：扩展参数
        * */
        channel.exchangeDeclare(exchangeName,exchangeType,true,false,false,null);

        // 让交换机和消息队列绑定
        channel.queueBind(queueName,exchangeName,routingKey);

        //创建消费者
        QueueingConsumer consumer = new QueueingConsumer(channel);

        // 通过channel把消费者和消息队列进行关联
        /*
        * 第一个参数是消息队列名
        * 第二个参数表示ack消息是否自动签收,消费者把消息消费后告知服务器这个消息已经被处理了
        * 第三个是消费者对象
        * */
        channel.basicConsume(queueName,true,consumer);

        // 获取消息
        while(true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            String message = new String(delivery.getBody());
            System.out.println("消费端：" + message);
        }

    }
}
