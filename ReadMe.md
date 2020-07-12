# rabbitMQ

RabbitMQ在中小企业中很受欢迎。

## 应用场景

1. 异步处理；
2. 应用解耦；
3. 流量削峰  --高并发；
4. 日志处理；


### 异步处理
~~~
串行： 注册 （50ms）----> email(50ms) ----> sms(50ms)
                         | ---->email(50ms)
并行:  注册 （50ms）----> | 
                         | ---->sms(50ms)

由于email和sms与业务不相关，次要业务，可以进行下面设计:

                         | ---->email(50ms)
注册(50ms) ----> MQ(5ms) | 
                         | ---->sms(50ms)
~~~

### 应用解耦
订单系统为例：有订单和库存。
~~~
order ------(rpc)-------> store

如果store挂掉，order就会失败

order ------> MQ(高可用) -------> store
~~~

### 流量削峰
~~~
秒杀用得比较多，秒杀时，前端有很多请求。

req ----> MQ ----->业务
~~~

### 日志处理
~~~
                                                                                                                      |流计算---->前端显示
前端(js) --(req)-->后台接口 ----> 写到文件(id,browser,ip,area)---->日志收集(logstash)---->消息中间件(rabbitMQ,kafka)---->|
                                                                                                                      |归档放到数据仓库（离线计算）--->mysql--->报表
~~~



## rabbitMQ

1. simple                简单队列
2. work queues           工作队列  公平分发 轮询分发
3. publish/subscribe     发布订阅
4. routing               路由选择 通配符模式
5. topic                 主题
6. 手动和自动确认消息
7. 队列的持久化和非持久化
8. rabbitMQ的延迟队列



### 简单队列

P ----> MQ ----> C

1. 耦合性高，生产者一一对应消费者，如果有多个消费者消费消息队列中的消息就会存在问题。
2. 队列名变更，这时候也要同时变更。

### WorkQueue 工作队列

一个生产者对应多个消费者；Simple队列是一一对应的，而且我们实际开发，生产者是毫不费力的，而消费者一般是要跟业务相结合的，消费者接收到消息之后就需要处理，可能需要花费时间。这时候队列就会积压很多消息。

#### 轮询分发(round-robin)和公平分发(fair dispatch)

轮询分发:默认情况下，多个消费者处理的消息数据量均分的。

公平分发：手动反馈一个处理完了的消息。必须关闭自动应答ack,改成手动，能者多劳。

### 消息应答与消息持久化

~~~java
    //自动应答
    boolean autoAck = false;
    channel.basicConsume(QUEUE_NAME,autoAck,defaultConsumer);
~~~

boolean autoAck = true; (自动确认模式) 这种模式下，RabbitMQ一旦把消息分发给消费者，就会从内存中删除，如果杀死的正在执行的消费者，就会丢失正在处理的消息。

boolean autoAck = false; (手动模式) 如果有一个消费者挂掉，则分发给另一个消费者。RabbitMQ支持消息应答，消费者发送一个消息应答，告诉RabbitMp这个消息我已经处理完成，你可以删了，然后rabbitMq就删除内存中的消息。


### rabbitMQ支持持久化

~~~java
channel.queueDeclare(QUEUE_NAME,false,false,false,null);
~~~

第二个参数durable = false;就是持久化。我们将程序中的false改为true是不可以的，尽管代码是正确的，但是它不会运行成功，因为我们已经定义了一个test_work_queue，这个queue是未持久化的。rabbitMQ不允许改变已生成的队列持久化性质。


### 订阅模式（一个消息被多个消费者消费)

~~~
              | ---->queue---->C1
P----> x ---->|
              | ---->queue---->C2
~~~

1. 一个生产者对应多个消费者；
2. 每个消费者都有自己的队列；
3. 生产者没有直接把消息发送到队列，而是发送到exchange;
4. 每个队列都要绑定到exchange；
5. 生产者发送的消息经过exchange到达队列就能实现一个消息被多个消费者消费。

发送到exchange，如果exchange没有绑定消息队列，则消息会丢失，因为交换机没有存储能力，在rabbitMQ里面只有队列有存储能力。



### RabbitMQ的消息确认机制(事务 + confirm) ---生产者

在rabbitMQ中，我们可以通过持久化数据解决rabbitMQ服务器异常丢失数据的问题；

问题：生产者将消息发送出去之后，消息到底有没有到达rabbitMQ服务器，默认的情况是不知道的。

两种方式:

1. AMQP：实现了事务机制(这种模式降低了rabbitMQ的吞吐量);
2. Confirm模式(建议使用)。

#### 事务机制
txSelect txCommit txRollback

* txSelect:用户将当前channel设置成为transation模式；
* txCommit:用于提交事务；
* txRollback:回滚事务。

#### confirm模式

生产者端confirm模式的实现原理。生产者将信道设置成为confirm模式，一旦信道进入confirm模式，所有在该信道上面发布的消息都会被指派一个唯一的ID(从1开始)，一旦消息被投递到所有匹配的队列之后，broker就会发送一个确认给生产者（包含消息的唯一ID），这就使得生产者知道消息已经正确到达目的队列了，如果消息和队列是持久化的，那么确认消息会将消息写入磁盘之后发出，broker回传给生产者的确认消息中deliver-tag域包含了确认消息的序列号，此外broker也可以设置basic.ack的multiple域，表示到这个序列号之前的所有消息都已经得到了处理。

**Confirm模式最大的好处在于他是异步的。**

**开启confirm模式**

channel.confirmSelect();

编程模式：
1. 普通 发一条 waitForConfirms();
2. 批量 发一批 waitForConfirms();
3. 异步confirm模式：提供一个回调方法。

**异步模式**

Channel对象提供的ConfirmListener回调方法只包含deliveryTag(当前Channel发出的消息序号)，我们需要自己为每一个Channel维护一个unconfirm的消息序号集合，每publish一条数据，集合中元素加1，每回调一次handleAck方法，unconfirm集合删掉响应的一条(multiple=false)或多条(multiple=true)记录。从程序运行效率上看，这个unconfirm集合最好采用有序集合SortedSet存储结构。







