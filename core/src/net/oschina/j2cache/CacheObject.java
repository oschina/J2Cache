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

/**
 * Cached object description
 * @author Winter Lau(javayou@gmail.com)
 */
public class CacheObject<T> {

	private String region;
	private String key;
	private T value;
	private byte level;

	public CacheObject(String region, String key, byte level) {
		this(region, key, level, null);
	}

	public CacheObject(String region, String key, byte level, T value) {
		this.region =  region;
		this.key = key;
		this.level = level;
		this.value = value;
	}

	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}
	public byte getLevel() {
		return level;
	}
	public void setLevel(byte level) {
		this.level = level;
	}

}
