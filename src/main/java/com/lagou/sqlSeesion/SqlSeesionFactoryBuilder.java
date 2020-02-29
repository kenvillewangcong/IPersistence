package com.lagou.sqlSeesion;

import com.lagou.config.XMLConfigBuilder;
import com.lagou.pojo.Configuration;

import java.io.InputStream;

public class SqlSeesionFactoryBuilder {

    public SqlSeesionFactory build(InputStream inputStream) throws Exception {
        // 第一：用dom4j解析配置文件，将解析出来的内容封装到Configuration中
        XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder();
        Configuration configuration = xmlConfigBuilder.parseConfig(inputStream);

        // 第二：创建SqlSeesionFactory对象 工厂类  生产sqlSession 会话对象
        DefaultSqlSessionFactory defaultSqlSessionFactory = new DefaultSqlSessionFactory(configuration);

        return defaultSqlSessionFactory;
    }
}
