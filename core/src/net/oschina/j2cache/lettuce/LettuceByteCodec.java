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
package net.oschina.j2cache.lettuce;

import io.lettuce.core.codec.RedisCodec;

import java.nio.ByteBuffer;

/**
 * 使用字节编码
 * @author Winter Lau (javayou@gmail.com)
 */
public class LettuceByteCodec implements RedisCodec<String, byte[]> {

    private static final byte[] EMPTY = new byte[0];

    @Override
    public String decodeKey(ByteBuffer byteBuffer) {
        return new String(getBytes(byteBuffer));
    }

    @Override
    public byte[] decodeValue(ByteBuffer byteBuffer) {
        return getBytes(byteBuffer);
    }

    @Override
    public ByteBuffer encodeKey(String s) {
        return ByteBuffer.wrap(s.getBytes());
    }

    @Override
    public ByteBuffer encodeValue(byte[] bytes) {
        return ByteBuffer.wrap(bytes);
    }


    private static byte[] getBytes(ByteBuffer buffer) {
        int remaining = buffer.remaining();

        if (remaining == 0) {
            return EMPTY;
        }

        byte[] b = new byte[remaining];
        buffer.get(b);
        return b;
    }
}
