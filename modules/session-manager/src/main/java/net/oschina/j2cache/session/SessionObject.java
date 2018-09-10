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

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Session 会话对象
 * @author Winter Lau (javayou@gmail.com)
 */
public class SessionObject implements Serializable {

    private String id;
    private long created_at;
    private long access_at;
    private ConcurrentHashMap<String, Object> attributes = new ConcurrentHashMap<>();

    public SessionObject(){}

    public Object get(Object key) {
        return attributes.get(key);
    }

    public boolean containsKey(Object key) {
        return attributes.containsKey(key);
    }

    public Object put(String key, Object value) {
        return attributes.put(key, value);
    }

    public Object remove(Object key) {
        return attributes.remove(key);
    }

    public void clear() {
        attributes.clear();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
        this.access_at = created_at;
    }

    public long getLastAccess_at() {
        return access_at;
    }

    public void setLastAccess_at(long access_at) {
        this.access_at = access_at;
    }

    public ConcurrentHashMap<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(HashMap<String, Object> attributes) {
        this.attributes.putAll(attributes);
    }
}
