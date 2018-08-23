/**
 * Copyright (c) 2015-2017, Winter Lau (javayou@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.oschina.j2cache;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

/**
 * 使用 RabbitMQ 实现集群内节点的数据通知（用于对数据一致性要求特别严格的场景）
 * @author Winter Lau (javayou@gmail.com)
 */
public class RabbitMQClusterPolicy implements ClusterPolicy, Consumer {

    private final static Logger log = LoggerFactory.getLogger(RabbitMQClusterPolicy.class);

    private ConnectionFactory factory;
    private Connection connection;
    private Channel channel;
    private String queue;
    private String exchange = "";

    /**
     * @param props RabbitMQ 配置信息
     */
    public RabbitMQClusterPolicy(Properties props){
        this.queue = (String)props.getOrDefault("queue_name", "j2cache");
        factory = new ConnectionFactory();
        factory.setHost((String)props.getOrDefault("host" , "127.0.0.1"));
        factory.setPort(Integer.valueOf((String)props.getOrDefault("port", "5672")));
        factory.setUsername((String)props.getOrDefault("username" , null));
        factory.setPassword((String)props.getOrDefault("password" , null));
        //TODO 更多的 RabbitMQ 配置
    }

    /**
     * 发布消息
     * @param data 消息数据
     * @throws IOException
     */
    private void publish(byte[] data) throws IOException {
        //失败重连
        if(!channel.isOpen() || !connection.isOpen()) {
            synchronized (RabbitMQClusterPolicy.class) {
                if(!channel.isOpen() || !connection.isOpen()) {
                    try {
                        connection = factory.newConnection();
                        channel = connection.createChannel();
                    } catch(TimeoutException e) {
                        throw new CacheException("Failed to connect to RabbitMQ!", e);
                    }
                }
            }
        }
        channel.basicPublish(this.exchange, this.queue, null, data);
    }

    @Override
    public void connect(Properties props) {
        try {
            long ct = System.currentTimeMillis();
            connection = factory.newConnection();
            channel = connection.createChannel();

            channel.queueDeclare(this.queue, false, false, false, null);
            publish(Command.join().json().getBytes());

            channel.basicConsume(this.queue, true, this);

            log.info("Connected to RabbitMQ:" + channel + ", time " + (System.currentTimeMillis()-ct) + " ms.");
        } catch (Exception e) {
            log.error(String.format("Failed to connect to RabbitMQ (%s:%d)", factory.getHost(), factory.getPort()), e);
        }
    }

    @Override
    public void sendEvictCmd(String region, String... keys) {
        Command cmd = new Command(Command.OPT_EVICT_KEY, region, keys);
        try {
            publish(cmd.json().getBytes());
        } catch ( IOException e ) {
            log.error("Failed to send EVICT cmd to RabbitMQ", e);
        }
    }

    @Override
    public void sendClearCmd(String region) {
        Command cmd = new Command(Command.OPT_CLEAR_KEY, region);
        try {
            publish(cmd.json().getBytes());
        } catch ( IOException e ) {
            log.error("Failed to send CLEAR cmd to RabbitMQ", e);
        }
    }

    @Override
    public void disconnect() {
        try {
            publish(Command.quit().json().getBytes());
        } catch ( IOException e ) {
            log.error("Failed to send QUIT cmd to RabbitMQ", e);
        } finally {
            try {
                channel.close();
                connection.close();
            } catch(Exception e){}
        }
    }

    @Override
    public void handleDelivery(String s, Envelope envelope, AMQP.BasicProperties basicProperties, byte[] bytes) {
        handleCommand(Command.parse(new String(bytes)));
    }

    @Override
    public void handleConsumeOk(String s) {

    }

    @Override
    public void handleCancelOk(String s) {

    }

    @Override
    public void handleCancel(String s) {

    }

    @Override
    public void handleShutdownSignal(String s, ShutdownSignalException e) {

    }

    @Override
    public void handleRecoverOk(String s) {

    }

}
