package net.oschina.j2cache;

import java.io.Serializable;

/**
 * Cached object description
 * @author Winter Lau(javayou@gmail.com)
 */
public class CacheObject {

	private String region;
	private Serializable key;
	private Serializable value;
	private byte level;
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public Serializable getKey() {
		return key;
	}
	public void setKey(Serializable key) {
		this.key = key;
	}
	public Serializable getValue() {
		return value;
	}
	public void setValue(Serializable value) {
		this.value = value;
	}
	public byte getLevel() {
		return level;
	}
	public void setLevel(byte level) {
		this.level = level;
	}
	
}
