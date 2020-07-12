package com.example.mqdemo.confirm;

import com.example.mqdemo.util.ConnectionUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

//批量数据确认
public class Send2 {
    private static final String QUEUE_NAME= "test_queue_confirm1";

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        //获取连接
        Connection connection = ConnectionUtil.getConnection();

        //获取channel
        Channel channel = connection.createChannel();

        //声明队列
        channel.queueDeclare(QUEUE_NAME, false,false,false,null );

        String msg = "hello confirm message";

        //将channel设置为confirm模式
        channel.confirmSelect();

        //批量发送
        for (int i = 0; i < 10; i++) {
            channel.basicPublish("", QUEUE_NAME,null,msg.getBytes());
        }

        if(!channel.waitForConfirms()) {
            System.out.println("message send failed!");
        } else {
            System.out.println("message send ok!");
        }

        channel.close();
        connection.close();
    }
}
