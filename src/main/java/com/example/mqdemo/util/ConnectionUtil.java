package com.example.mqdemo.util;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ConnectionUtil {

    public static Connection getConnection()  throws IOException, TimeoutException {

        //定义连接工厂
        ConnectionFactory factory = new ConnectionFactory();

        //设置服务地址
        factory.setHost("192.168.0.134");

        //AMQP 5672
        factory.setPort(5672);

        //vhost相当于数据库
        factory.setVirtualHost("/");

        //用户名
        factory.setUsername("root");

        //密码
        factory.setPassword("123456");

        return factory.newConnection();

    }
}
