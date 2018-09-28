# Hibernate 5 支持模块

基于 Hibernate 5.2.17 开发，感谢 [@tandy](https://gitee.com/tandy)

Maven:

```xml
<dependency>
  <groupId>net.oschina.j2cache</groupId>
  <artifactId>j2cache-hibernate5</artifactId>
  <version>1.0.0-beta1</version>
</dependency>
```
使用说明:

1、XML文件配置

```xml
    <bean id="sessionFactory" class="org.springframework.orm.hibernate5.LocalSessionFactoryBean">
        <property name="hibernateProperties">
            <props>
                ...省略其他配置...
                <prop key="hibernate.current_session_context_class">org.springframework.orm.hibernate5.SpringSessionContext</prop>
                <prop key="hibernate.cache.region.factory_class">net.oschina.j2cache.hibernate5.J2CacheRegionFactory</prop>
                <prop key="hibernate.cache.use_second_level_cache">true</prop>
                <prop key="hibernate.cache.use_query_cache">true</prop>
            </props>
        </property>
        ...省略其他配置...
    </bean>
```

2、properties文件配置
```xml
    spring.jpa.properties.hibernate.current_session_context_class=org.springframework.orm.hibernate5.SpringSessionContext
    spring.jpa.properties.hibernate.cache.region.factory_class=net.oschina.j2cache.hibernate5.J2CacheRegionFactory
    spring.jpa.properties.hibernate.cache.use_second_level_cache=true
    spring.jpa.properties.hibernate.cache.use_query_cache=true
```

3、yml文件配置
```xml
    spring:
        jpa:
            properties:
                hibernate:
                    current_session_context_class: org.springframework.orm.hibernate5.SpringSessionContext
                    cache:
                        use_second_level_cache: true
                        use_query_cache: true
                        region:
                            factory_class: net.oschina.j2cache.hibernate5.J2CacheRegionFactory
```