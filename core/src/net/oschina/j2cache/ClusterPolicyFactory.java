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

import net.oschina.j2cache.redis.RedisPubSubClusterPolicy;

import java.util.Properties;

/**
 * 集群策略工厂
 * @author Winter Lau(javayou@gmail.com)
 */
public class ClusterPolicyFactory {

    /**
     * 使用 Redis 订阅和发布机制，该方法只能调用一次
     * @param name  频道名称
     * @param props 框架配置
     * @return 返回 Redis 集群策略的实例
     */
    public final static ClusterPolicy redis(String name, Properties props) {
        RedisPubSubClusterPolicy policy = new RedisPubSubClusterPolicy(name, props);
        policy.connect(props);
        return policy;
    }

    /**
     * 使用 JGroups 组播机制
     * @param name  组播识别码
     * @param configPath 配置文件的名称
     * @param props 框架配置
     * @return 返回 JGroups 集群策略的实例
     */
    public final static ClusterPolicy jgroups(String name, String configPath, Properties props) {
        JGroupsClusterPolicy policy = new JGroupsClusterPolicy(name, configPath);
        policy.connect(props);
        return policy;
    }

    /**
     * 加载自定义的集群通知策略
     * @param classname
     * @param props
     * @return
     */
    public final static ClusterPolicy custom(String classname, Properties props) {
        try {
            ClusterPolicy policy = (ClusterPolicy)Class.forName(classname).newInstance();
            policy.connect(props);
            return policy;
        } catch (Exception e) {
            throw new CacheException("Failed in load custom cluster policy. class = " + classname, e);
        }
    }

}
