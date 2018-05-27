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
package net.oschina.j2cache.util;

import org.nustaq.serialization.FSTConfiguration;

/**
 * 为了实现跨语言的支持，有必要支持 JSON 的序列化
 *
 * @author Winter Lau(javayou@gmail.com)
 */
public class JSONSerializer implements Serializer {

    private static final FSTConfiguration conf = FSTConfiguration.createJsonConfiguration();

    @Override
    public String name() {
        return "json";
    }

    @Override
    public byte[] serialize(Object obj) {
        return conf.asByteArray(obj);
    }

    @Override
    public Object deserialize(byte[] bytes) {
        return conf.asObject(bytes);
    }

}
