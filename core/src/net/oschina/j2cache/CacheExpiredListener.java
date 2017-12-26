package net.oschina.j2cache;

import java.io.Serializable;

/**
 * When cached data expired in ehcache, this listener will be invoked.
 *
 * @author Winter Lau(javayou@gmail.com)
 */
public interface CacheExpiredListener {

	void notifyElementExpired(String region, Serializable key) ;

}
