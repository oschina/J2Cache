/**
 * Copyright (c) 2015-2018, Winter Lau (javayou@gmail.com).
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
package net.oschina.j2cache.tomcat;

import net.oschina.j2cache.*;
import org.apache.catalina.*;
import org.apache.catalina.session.ManagerBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import java.io.File;
import java.io.IOException;

/**
 * 实现通过 j2cache 管理 Tomcat 的 Session
 * @author Winter Lau (javayou@gmail.com)
 */
public class J2CacheSessionManager extends ManagerBase {

    private static final Log log = LogFactory.getLog(J2CacheSessionManager.class);

    private String config;
    private CacheChannel j2cache;

    public CacheChannel getJ2cache() {
        return j2cache;
    }

    @Override
    public void load() {}

    @Override
    public void unload() {}

    @Override
    protected void startInternal() throws LifecycleException {
        super.startInternal();
        setState(LifecycleState.STARTING);
        try {
            if(config == null || config.trim().equals(""))
                config = "j2cache.properties";
            File configDir = new File(System.getProperty("catalina.base"), "conf");
            File configFile = new File(configDir, config);
            J2CacheConfig j2CacheConfig = J2CacheConfig.initFromConfig(configFile);
            J2CacheBuilder builder = J2CacheBuilder.init(j2CacheConfig);
            j2cache = builder.getChannel();
        } catch (Exception e) {
            throw new LifecycleException("Failed to load j2cache configuration " + config, e);
        }
    }

    @Override
    protected void stopInternal() throws LifecycleException {
        setState(LifecycleState.STOPPING);
        if(j2cache != null)
            j2cache.close();
        super.stopInternal();
    }

    @Override
    public Session createSession(String sessionId) {
        return createSession(sessionId, true);
    }

    public Session createSession(String sessionId, boolean create) {
        J2CacheSession session = (J2CacheSession)super.createSession(sessionId);
        if(create) {
            j2cache.set(session.getId(), J2CacheSession.KEY_CREATE_TIME, String.valueOf(session.getCreationTime()));
            j2cache.set(session.getId(), J2CacheSession.KEY_AUTH_TYPE, session.getAuthType());
        }
        return session;
    }

    @Override
    public Session createEmptySession() {
        return new J2CacheSession(this);
    }

    @Override
    public void remove(Session session, boolean update) {
        j2cache.clear(session.getId());
        j2cache.removeRegion(session.getId());
        super.remove(session, update);
    }

    @Override
    public Session findSession(String id) throws IOException {
        Session session = super.findSession(id);
        if(session == null) {
            long createTime = j2cache.get(id, J2CacheSession.KEY_CREATE_TIME).asLong(-1L);
            if(createTime > 0) {
                session = createSession(id, false);
                session.setCreationTime(createTime);
                String authType = j2cache.get(id, J2CacheSession.KEY_AUTH_TYPE).asString();
                if(authType != null)
                    session.setAuthType(authType);
                session.setNew(false);
            }
        }
        else {

        }
        return session;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

}
