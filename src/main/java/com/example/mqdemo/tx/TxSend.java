package com.example.mqdemo.tx;

import com.example.mqdemo.util.ConnectionUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class TxSend {

    private static final String QUEUE_NAME= "test_queue_tx";

    public static void main(String[] args) throws IOException, TimeoutException {
        //获取连接
        Connection connection = ConnectionUtil.getConnection();

        //获取channel
        Channel channel = connection.createChannel();

        //声明队列
        channel.queueDeclare(QUEUE_NAME, false,false,false,null );

        String msg = "hello tx message";

        //发送消息
        try {
            channel.txSelect();
            channel.basicPublish("", QUEUE_NAME,null,msg.getBytes());
//            int xx = 1/0;
            channel.txCommit();
        } catch (IOException e) {
            channel.txRollback();
            System.out.println("Send message txRollback!");
        }
        channel.close();
        connection.close();
    }
}
