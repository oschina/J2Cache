package net.oschina.j2cache;

/**
 * J2Cache exception
 *
 * @author Winter Lau (javayou@gmail.com)
 */
public class CacheException extends RuntimeException {

	public CacheException(String s) {
		super(s);
	}

	public CacheException(String s, Throwable e) {
		super(s, e);
	}

	public CacheException(Throwable e) {
		super(e);
	}
	
}
