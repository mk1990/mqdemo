package com.example.mqdemo.topic;

import com.example.mqdemo.util.ConnectionUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Send {
    private static final String EXCHANGE_NAME= "test_exchange_topic";

    public static void main(String[] args) throws IOException, TimeoutException {
        //获取连接
        Connection connection = ConnectionUtil.getConnection();

        //获取channel
        Channel channel = connection.createChannel();

        //声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME,"topic");

        String msg = "商品.....";

        //发送消息
        String routingKey = "goods.update";
        channel.basicPublish(EXCHANGE_NAME, routingKey,null,msg.getBytes());

        System.out.println("Send: " + msg);

        channel.close();
        connection.close();
    }
}
