/*
 * Copyright (c) 2015-2020, whcrow (v@whcrow.com).
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
package net.oschina.j2cache.util;

import com.jfireframework.fse.ByteArray;
import com.jfireframework.fse.Fse;

/**
 * 使用 fse 实现序列化
 *
 * @author whcrow (v@whcrow.com)
 */
public class FseSerializer implements Serializer {

    @Override
    public String name() {
        return "fse";
    }

    @Override
    public byte[] serialize(Object obj) {
        ByteArray buf = ByteArray.allocate(100);
        new Fse().serialize(obj, buf);
        byte[] resultBytes = buf.toArray();
        buf.clear();
        return resultBytes;
    }

    @Override
    public Object deserialize(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        ByteArray buf = ByteArray.allocate(100);
        buf.put(bytes);
        Object result = new Fse().deSerialize(buf);
        buf.clear();
        return result;
    }

}
