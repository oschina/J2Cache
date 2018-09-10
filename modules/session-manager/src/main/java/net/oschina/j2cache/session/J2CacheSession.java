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
package net.oschina.j2cache.session;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.*;

/**
 * 实现对 Session 的自定义管理
 * @author Winter Lau (javayou@gmail.com)
 */
public class J2CacheSession implements HttpSession {

    private SessionObject session = new SessionObject();
    private int maxInactiveInterval;
    private boolean newSession = true;

    private final ServletContext servletContext;
    private String region;

    private volatile boolean invalid;

    public J2CacheSession(ServletContext servletContext, String region, String id) {
        this.servletContext = servletContext;
        this.region = region;

        this.session.setId(id);
        this.session.setCreated_at(System.currentTimeMillis());
    }

    @Override
    public long getCreationTime() {
        return session.getCreated_at();
    }

    @Override
    public String getId() {
        return session.getId();
    }

    @Override
    public long getLastAccessedTime() {
        return session.getLastAccess_at();
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        this.maxInactiveInterval = interval;
    }

    @Override
    public int getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        this.checkValid();
        return null;
    }

    @Override
    public Object getAttribute(String name) {
        checkValid();
        return null;
    }

    @Override
    public void setAttribute(String name, Object value) {
        this.checkValid();
    }

    @Override
    public void removeAttribute(String name) {
        this.checkValid();
    }

    @Override
    public void invalidate() {
        invalid = true;
        //TODO delete all attributes of this session

    }

    public boolean isNew() {
        return newSession;
    }

    public void setNew(boolean isNew) {
        this.newSession = isNew;
    }

    protected void checkValid() throws IllegalStateException {
        if (invalid) {
            throw new IllegalStateException("http session has invalidate");
        }
    }

    @Deprecated
    @Override
    public Object getValue(String name) {
        return getAttribute(name);
    }

    @Deprecated
    @Override
    public void removeValue(String name) {
        removeAttribute(name);
    }

    @Deprecated
    @Override
    public void putValue(String name, Object value) {
        this.setAttribute(name, value);
    }

    @Deprecated
    @Override
    public String[] getValueNames() {
        this.checkValid();
        return null;
    }

    @Deprecated
    public HttpSessionContext getSessionContext() {
        return null;
    }

}
