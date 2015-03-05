/**
 * 
 */
package net.oschina.j2cache.hibernate;

import java.util.Properties;

import junit.framework.TestCase;

import org.hibernate.cache.Cache;

/**
 * @author liao
 *
 */
@SuppressWarnings("deprecation")
public class J2CacheProviderTest extends TestCase {
    
    private J2CacheProvider provier = new J2CacheProvider();

    public void testBuildCache() {
        Properties properties = new Properties();
        Cache cache = provier.buildCache("user", properties);
        assertTrue(cache != null);
    }
}
