
## tomcat-session-manager

该模块是为了让 tomcat 支持用 J2Cache 管理 session。

使用方法：

1. 拷贝 j2cache-tomcat-session-mananger-xxxx.jar 到 {tomcat}/lib 目录
2. 拷贝 j2cache.properties 到 {tomcat}/conf 目录，并进行配置调整
3. 修复 server.xml 配置文件，增加如下信息： 
    ```
    <Context>
        ...
        <Manager       
                className="net.oschina.j2cache.tomcat.J2CacheSessionManager" 
                config="conf/j2cache.properties"
        />
        ...
    </Context>
    ```
4. 重启 tomcat 并检查 catalina.out 日志看是否启动正常