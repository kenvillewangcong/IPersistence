package com.lagou.sqlSeesion;

import com.lagou.pojo.Configuration;

public class DefaultSqlSessionFactory implements SqlSeesionFactory{

    private Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Sqlsession openSession() {
        return new DefaultSqlSession(configuration);
    }
}
