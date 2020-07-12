package com.example.mqdemo.ps;

import com.example.mqdemo.util.ConnectionUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Sendps {

    private static final String EXCHANGE_NAME= "test_exchange_fanout";

    public static void main(String[] args) throws IOException, TimeoutException {
        //获取连接
        Connection connection = ConnectionUtil.getConnection();

        //获取channel
        Channel channel = connection.createChannel();

        //声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME,"fanout");

        String msg = "hello ps";

        //发送消息
        channel.basicPublish(EXCHANGE_NAME,"",null,msg.getBytes());

        System.out.println("Send: " + msg);

        channel.close();
        connection.close();
    }
}
