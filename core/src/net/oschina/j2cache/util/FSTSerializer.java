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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import net.oschina.j2cache.CacheException;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

/**
 * 使用 FST 实现序列化
 *
 * @author Winter Lau(javayou@gmail.com)
 */
public class FSTSerializer implements Serializer {

	@Override
	public String name() {
		return "fst";
	}

	@Override
	public byte[] serialize(Object obj) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (FSTObjectOutput fOut = new FSTObjectOutput(out)) {
			fOut.writeObject(obj);
			fOut.flush();
			return out.toByteArray();
		}
	}

	@Override
	public Object deserialize(byte[] bytes) throws IOException {
		if(bytes == null || bytes.length == 0)
			return null;
		try (FSTObjectInput in = new FSTObjectInput(new ByteArrayInputStream(bytes))){
			return in.readObject();
		} catch (ClassNotFoundException e) {
			throw new CacheException(e);
		}
	}

}
