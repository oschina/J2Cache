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
package net.oschina.j2cache.session;

import org.nustaq.serialization.FSTConfiguration;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 使用 FST 实现序列化
 *
 * @author Winter Lau(javayou@gmail.com)
 */
public class FSTSerializer {

	private static final FSTConfiguration fstConfiguration ;

	static {
		fstConfiguration = FSTConfiguration.getDefaultConfiguration();
		fstConfiguration.setClassLoader(Thread.currentThread().getContextClassLoader());
	}

	public static byte[] write(Object obj) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (FSTObjectOutput fOut = new FSTObjectOutput(out, fstConfiguration)) {
			fOut.writeObject(obj);
			fOut.flush();
			return out.toByteArray();
		} catch (IOException e) {}
		return null;
	}

	public static Object read(byte[] bytes) throws IOException, ClassNotFoundException {
		if(bytes == null || bytes.length == 0)
			return null;
		try (FSTObjectInput in = new FSTObjectInput(new ByteArrayInputStream(bytes), fstConfiguration)){
			return in.readObject();
		}
	}

}
