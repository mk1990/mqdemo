package com.example.mqdemo.confirm;

import com.example.mqdemo.util.ConnectionUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import sun.rmi.runtime.Log;

import java.io.IOException;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeoutException;

//异步模式
public class Send3 {
    private static final String QUEUE_NAME= "test_queue_confirm3";

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

        //未确认消息标识
        final SortedSet<Long> confirmSet = Collections.synchronizedSortedSet(new TreeSet<Long>());

        channel.addConfirmListener(new ConfirmListener(){
            //回执有问题
            @Override
            public void handleNack(long l, boolean b) throws IOException {
                if(b) {
                    System.out.println("------handleNack-------multiple");
                    confirmSet.headSet(l + 1).clear();
                }else {
                    System.out.println("------handleNack--------nultiple false");
                    confirmSet.remove(l);
                }
            }

            //没有问题的回执
            @Override
            public void handleAck(long l, boolean b) throws IOException {
                if (b) {
                    System.out.println("-------handleAck----------multiple");
                    confirmSet.headSet(l + 1).clear();
                }else {
                    System.out.println("-------handleAck----------multiple false");
                    confirmSet.remove(l);
                }
            }
        });

        String msgStr = "sssssss";

        while (true) {
            long seqNo = channel.getNextPublishSeqNo();
            channel.basicPublish("", QUEUE_NAME, null, msgStr.getBytes());
            confirmSet.add(seqNo);
        }
    }
}
