package com.lagou.sqlSeesion;

import com.lagou.pojo.Configuration;
import com.lagou.pojo.MappedStatement;

import java.beans.IntrospectionException;
import java.lang.reflect.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

public class DefaultSqlSession implements Sqlsession {

    private Configuration configuration;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <E> List<E> selectList(String statementId, Object... params) throws IllegalAccessException, IntrospectionException, InstantiationException, NoSuchFieldException, SQLException, InvocationTargetException, ClassNotFoundException {

        // 对SimpleExecutor里的Query方法的调用
        SimpleExecutor simpleExecutor = new SimpleExecutor();
        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);
        List<Object> query = simpleExecutor.query(configuration, mappedStatement, params);

        return (List<E>) query;
    }

    @Override
    public <T> T selectOne(String statementId, Object... params) throws IllegalAccessException, ClassNotFoundException, IntrospectionException, InstantiationException, SQLException, InvocationTargetException, NoSuchFieldException {
        List<Object> objects = selectList(statementId, params);
        if(objects.size() == 1){
            return (T) objects.get(0);
        }else{
            throw new RuntimeException("查询结果为空或者返回结果过多");
        }
    }



    @Override
    public <T> T getMapper(Class<?> mapperClass) {
        // 使用JDK动态代理为Dao接口生成代理对象，并返回

        Object proxyInstance = Proxy.newProxyInstance(DefaultSqlSession.class.getClassLoader(), new Class[]{mapperClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // 方法名
                String methodName = method.getName();
                String className = method.getDeclaringClass().getName();
                String statementId = className + "." + methodName;

                // 查询条件参数args

                // 获取被调用方法返回类型
                Type genericReturnType = method.getGenericReturnType();


                Object o = new Object();
                // 判断调用
                MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);

                switch (mappedStatement.getSqlCommandType()){
                    case "INSERT":
                        o = insert(statementId, args);
                        break;
                    case "DELETE":
                        o =  delete(statementId, args);
                        break;
                    case "UPDATE":
                        o =  update(statementId, args);
                        break;
                    case "SELECT":
                        o =  select(genericReturnType, statementId, args);
                        break;
                }

                return o;
            }
        });


        return (T) proxyInstance;
    }

    public  <T> T select(Type genericReturnType, String statementId, Object[] args) throws IllegalAccessException, ClassNotFoundException, IntrospectionException, InstantiationException, SQLException, InvocationTargetException, NoSuchFieldException {
        if(genericReturnType instanceof ParameterizedType){
            List<Object> objects = selectList(statementId, args);
            return (T) objects;
        }

        return selectOne(statementId,args);
    }

    @Override
    public int insert(String statementId, Object... params) throws ClassNotFoundException, SQLException, IllegalAccessException, NoSuchFieldException {
        int insert = update(statementId, params);
        return insert;
    }

    @Override
    public int update(String statementId, Object... params) throws ClassNotFoundException, SQLException, IllegalAccessException, NoSuchFieldException {
        SimpleExecutor simpleExecutor = new SimpleExecutor();
        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);
        int update = simpleExecutor.update(configuration, mappedStatement, params);
        return update;
    }

    @Override
    public int delete(String statementId, Object... params) throws ClassNotFoundException, SQLException, IllegalAccessException, NoSuchFieldException {

        int delete = update(statementId, params);
        return delete;
    }
}
