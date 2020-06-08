package com.example.mqdemo.quickstart;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Producer4fanout {
    public static void main(String[] args) throws IOException, TimeoutException {
        //1. 创建出链接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.0.134");
        factory.setPort(5672);
        factory.setUsername("root");
        factory.setPassword("123456");
        factory.setVirtualHost("/");

        //2. 通过链接工厂创建出链接对象
        Connection  conn = factory.newConnection();

        //3. 通过链接对象创建channel
        Channel channel = conn.createChannel();

        //4. 通过channel发布消息
        for (int i = 0; i < 5 ; i++) {
            String body = "hello rabbitMq for fanout exchange!";
            /*
            * 第一个参数是交换机的名称，为空表示默认交换机
            * 第二个参数表示路由键
            * 第三个参数表示消息属性
            * 第四个参数表示消息内容
            * */
            String exchangeName = "test_fanout";
            String routingKey = "";
            channel.basicPublish(exchangeName,routingKey, null,body.getBytes());
        }

        //5. 释放资源
        channel.close();
        conn.close();
    }
}
