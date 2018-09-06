
## tomcat-session-manager

该模块是为了让 tomcat 支持用 J2Cache 管理 session。

使用方法：

1. 拷贝 tomcat-j2cache-session-mananger-xxxx.jar 到 {tomcat}/lib 目录
2. 拷贝以下依赖包到 {tomcat}/lib 目录
   * j2cache-core-2.6.0-release.jar
   * caffeine-2.6.2.jar
   * commons-pool2-2.5.0.jar
   * fastjson-1.2.49.jar
   * fst-2.57.jar
   * jackson-core-2.9.5.jar
   * jedis-2.9.0.jar
   * objenesis-2.6.jar
   * slf4j-api-1.7.25.jar
   * slf4j-simple-1.7.25.jar
   
3. 拷贝 j2cache.properties 到 {tomcat}/conf 目录，并进行配置调整
4. 修复 server.xml 配置文件，增加如下信息： 
    ```
    <Context>
        ...
        <Manager       
                className="net.oschina.j2cache.tomcat.J2CacheSessionManager" 
                config="j2cache.properties"
        />
        ...
    </Context>
    ```
5. 启动 tomcat 并检查 catalina.out 日志看是否启动正常