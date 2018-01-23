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

import org.jgroups.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Properties;

/**
 * 使用 JGroups 组播进行集群内节点通讯
 * @author Winter Lau(javayou@gmail.com)
 */
public class JGroupsClusterPolicy extends ReceiverAdapter implements ClusterPolicy {

    private final static Logger log = LoggerFactory.getLogger(JGroupsClusterPolicy.class);

    private String configXml;
    private JChannel channel;
    private String name;

    static {
        System.setProperty("java.net.preferIPv4Stack", "true"); //Disable IPv6 in JVM
    }

    /**
     * 构造函数
     * @param name 组播频道名称
     * @param props 配置文件路径
     */
    public JGroupsClusterPolicy(String name, Properties props) {
        this.name = name;
        this.configXml = props.getProperty("configXml");
        if(configXml != null && configXml.trim().length() > 0)
            this.configXml = "/network.xml";
    }

    @Override
    public void connect(Properties props) {
        try{
            long ct = System.currentTimeMillis();

            URL xml = CacheChannel.class.getResource(configXml);
            if(xml == null)
                xml = getClass().getClassLoader().getParent().getResource(configXml);
            channel = new JChannel(xml);
            channel.setReceiver(this);
            channel.connect(name);

            this.sendMessage(Command.join());
            log.info("Connected to jgroups channel:" + name + ", time " + (System.currentTimeMillis()-ct) + " ms.");

        }catch(Exception e){
            throw new CacheException(e);
        }
    }

    @Override
    public void disconnect() {
        this.sendMessage(Command.quit());
        channel.close();
    }

    @Override
    public void receive(Message msg) {

        //不处理发送给自己的消息
        if(msg.getSrc().equals(channel.getAddress()))
            return ;

        //无效消息
        String msgJson = (String)msg.getObject();

        try{
            Command cmd = Command.parse(msgJson);

            if(cmd == null || cmd.isLocal())
                return;

            switch(cmd.getOperator()){
                case Command.OPT_JOIN:
                    log.info("Node-"+cmd.getSrc()+" joined to " + name);
                    break;
                case Command.OPT_EVICT_KEY:
                    this.evict(cmd.getRegion(),cmd.getKeys());
                    break;
                case Command.OPT_CLEAR_KEY:
                    this.clear(cmd.getRegion());
                    break;
                case Command.OPT_QUIT:
                    log.info("Node-"+cmd.getSrc()+" quit to " + name);
                    break;
                default:
                    log.warn("Unknown message type = " + cmd.getOperator());
            }
        }catch(Exception e){
            log.error("Failed to handle received msg" , e);
        }
    }

    /**
     * 发送清除缓存的广播命令
     *
     * @param region : Cache region name
     * @param keys    : cache key
     */
    @Override
    public void sendEvictCmd(String region, String...keys) {
        sendMessage(new Command(Command.OPT_EVICT_KEY, region, keys));
    }

    /**
     * 发送清除缓存的广播命令
     *
     * @param region: Cache region name
     */
    @Override
    public void sendClearCmd(String region) {
        sendMessage(new Command(Command.OPT_CLEAR_KEY, region, ""));
    }

    @Override
    public void viewAccepted(View view) {
        log.info(String.format("Group Members Changed, LIST: %s",
                String.join(",", view.getMembers().stream().map(a -> a.toString()).toArray(String[]::new)))
        );
    }

    private void sendMessage(Command cmd) {
        try {
            Message msg = new Message(null, cmd.json());
            channel.send(msg);
        } catch (Exception e) {
            log.error("Failed to send message to jgroups -> " + cmd.json(), e);
        }
    }

}
