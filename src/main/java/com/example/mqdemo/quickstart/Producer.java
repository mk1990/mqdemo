package com.example.mqdemo.quickstart;

/*
* 右上角是用户信息，Cluster:节点信息，MQ版本和Erlang版本号
*
* Overview:
* 概要信息，包含配置信息、数据文件目录、日志信息，端口号：5672---->客户端端口号，25672---->集群端口号 15672--->管控台
* 导入导出信息（管控台中配置的信息可以进行导入导出）。
*
* Connections:
* 客户端链接信息
*
* Channel:
* 通过Connection建立起来
*
* Exchange:
* 默认有8个交换机
*
* Queue:
* 消息队列
*
* Admin:
* 用户信息:创建用户，创建Virtual Hosts,管理权限
*
* state: idle,表示目前处于空闲状态。
* */






import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Producer {
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
            String body = "hello rabbitMq!";
            /*
            * 第一个参数是交换机的名称，为空表示默认交换机
            * 第二个参数表示路由键
            * 第三个参数表示消息属性
            * 第四个参数表示消息内容
            * */
            channel.basicPublish("","test001", null,body.getBytes());
        }



        //5. 释放资源
        channel.close();
        conn.close();
    }
}
