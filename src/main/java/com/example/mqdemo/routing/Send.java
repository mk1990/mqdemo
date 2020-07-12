package com.example.mqdemo.routing;

import com.example.mqdemo.util.ConnectionUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Send {
    private static final String EXCHANGE_NAME= "test_exchange_direct";

    public static void main(String[] args) throws IOException, TimeoutException {
        //获取连接
        Connection connection = ConnectionUtil.getConnection();

        //获取channel
        Channel channel = connection.createChannel();

        //声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME,"direct");

        String msg = "hello direct";

        //发送消息
        String routingKey = "info";
        channel.basicPublish(EXCHANGE_NAME, routingKey,null,msg.getBytes());

        System.out.println("Send: " + msg);

        channel.close();
        connection.close();
    }
}
