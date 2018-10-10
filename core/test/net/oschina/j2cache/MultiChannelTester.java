package net.oschina.j2cache;

/**
 * 测试一个应用里多个 CacheChannel 实例
 */
public class MultiChannelTester {

    public static void main( String[] args )
    {
        try {

            J2CacheConfig config_admin = J2CacheConfig.initFromConfig("/j2cache.properties");
            J2CacheConfig config_service = J2CacheConfig.initFromConfig("/j2cache2.properties");

            config_admin.dump(System.out);

            System.out.println("---------------------------------------------");

            config_service.dump(System.out);

            CacheChannel channel_admin = J2CacheBuilder.init(config_admin).getChannel();
            CacheChannel channel_service = J2CacheBuilder.init(config_service).getChannel();

            channel_admin.set("test-admin", "test-key-admin", "test-value-admin");
            channel_service.set("test-service", "test-key-service", "test-value-service");

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.exit(0);
    }
}
