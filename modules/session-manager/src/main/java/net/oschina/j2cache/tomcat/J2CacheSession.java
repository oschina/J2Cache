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

import net.oschina.j2cache.CacheChannel;
import org.apache.catalina.session.StandardSession;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Tomcat Session 封装
 * @author Winter Lau (javayou@gmail.com)
 */
public class J2CacheSession extends StandardSession {

    public static final String KEY_CREATE_TIME = "__createTime__";
    public static final String KEY_AUTH_TYPE = "__authType__";

    private CacheChannel j2cache;

    public J2CacheSession(J2CacheSessionManager manager) {
        super(manager);
        this.j2cache = manager.getJ2cache();
    }

    @Override
    public Object getAttribute(String name) {
        return j2cache.get(this.getId(), name).getValue();
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(j2cache.keys(this.getId()));
    }

    @Override
    public void setAttribute(String key, Object value) {
        Object oldValue = getAttribute(key);
        super.setAttribute(key, value);

        if ((oldValue != null && !oldValue.equals(value)) || (value!=null && !value.equals(oldValue))) {
            j2cache.set(this.getId(), key, value);
        }
    }

    @Override
    public void removeAttribute(String name) {
        j2cache.evict(this.getId(), name);
        super.removeAttribute(name);
    }

    @Override
    public void readObjectData(ObjectInputStream stream) throws ClassNotFoundException, IOException {
        System.out.println("readObjectData");
        super.readObjectData(stream);
    }

    @Override
    public void writeObjectData(ObjectOutputStream stream) throws IOException {
        System.out.println("writeObjectData");
        super.writeObjectData(stream);
    }
}
