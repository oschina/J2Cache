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

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * 空的缓存Provider
 * @author Winter Lau(javayou@gmail.com)
 */
public class NullCache implements Cache {

	@Override
	public Serializable get(String key) {
		return null;
	}

	@Override
	public void put(String key, Serializable value) {

	}

	@Override
	public Collection<String> keys() {
		return null;
	}

	@Override
	public Map getAll(Collection<String> keys) {
		return null;
	}

	@Override
	public boolean exists(String key) {
		return false;
	}

	@Override
	public void putAll(Map<String, Serializable> elements)  {

	}

	@Override
	public void evict(String...keys) {

	}

	@Override
	public void clear() {

	}
}
