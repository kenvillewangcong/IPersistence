package com.lagou.sqlSeesion;

import com.lagou.config.BoundSql;
import com.lagou.pojo.Configuration;
import com.lagou.pojo.MappedStatement;
import com.lagou.utils.GenericTokenParser;
import com.lagou.utils.ParameterMapping;
import com.lagou.utils.ParameterMappingTokenHandler;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SimpleExecutor implements Executor {


    public PreparedStatement buildPreparedStatement(Configuration configuration, MappedStatement mappedStatement, Object... params) throws SQLException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        // 1注册驱动，获取数据库链接
        Connection connection = configuration.getDataSource().getConnection();

        // 获取SQL语句
        String sql = mappedStatement.getSql();

        // 转换SQL语句
        BoundSql boundSql = getBoundSql(sql);

        // 获取预处理对象
        PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSqlText());

        // 设置参数

        String paramterType = mappedStatement.getParamterType();


        Class<?> paramterTypeClass = getClassType(paramterType);

        List<ParameterMapping> parameterMappingList = boundSql.getParameterMappingList();
        for (int i = 0; i < parameterMappingList.size(); i++) {
            ParameterMapping parameterMapping = parameterMappingList.get(i);
            String name = parameterMapping.getName();
            if(paramterTypeClass.equals(java.lang.Integer.class)) {
                preparedStatement.setObject(i+1, params[0]);
            }else{
                // 反射
                Field declaredField = paramterTypeClass.getDeclaredField(name);
                // 暴力访问
                declaredField.setAccessible(true);
                Object o = declaredField.get(params[0]);
                preparedStatement.setObject(i+1, o);
            }

        }

        return preparedStatement;
    }

    @Override
    public <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object... params) throws SQLException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException, IntrospectionException, InstantiationException, InvocationTargetException {

        String resultType = mappedStatement.getResultType();
        Class<?> resultTypeClass = getClassType(resultType);

        PreparedStatement preparedStatement = buildPreparedStatement(configuration, mappedStatement, params);

        // 执行SQL
        ResultSet resultSet = preparedStatement.executeQuery();

        ArrayList<Object> objects = new ArrayList<>();

        // 封装返回结果集
        while(resultSet.next()){
            Object o = resultTypeClass.newInstance();

            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                // 字段名
                String columnName = metaData.getColumnName(i);

                Object object = resultSet.getObject(columnName);

                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(columnName, resultTypeClass);
                Method writeMethod = propertyDescriptor.getWriteMethod();
                writeMethod.invoke(o, object);

            }

            objects.add(o);
        }

        return (List<E>) objects;
    }

    @Override
    public int update(Configuration configuration, MappedStatement mappedStatement, Object... params) throws SQLException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException {

        PreparedStatement preparedStatement = buildPreparedStatement(configuration, mappedStatement, params);

        // 执行SQL
        int resultSet = preparedStatement.executeUpdate();

        return resultSet;
    }

    private Class<?> getClassType(String paramterType) throws ClassNotFoundException {
        if(null != paramterType){
            return Class.forName(paramterType);
        }
        return null;
    }

    /**
     * 完成对#{}解析工作，将#{}用？代替，解析#{}的值进行存储
     *
     * @param sql
     * @return
     */
    private BoundSql getBoundSql(String sql) {

        // 标记处理类：配置标记解析器来完成对占位符的解析
        ParameterMappingTokenHandler parameterMappingTokenHandler = new ParameterMappingTokenHandler();
        GenericTokenParser genericTokenParser = new GenericTokenParser("#{","}", parameterMappingTokenHandler);
        String parseSql = genericTokenParser.parse(sql);

        List<ParameterMapping> parameterMappings = parameterMappingTokenHandler.getParameterMappings();

        BoundSql boundSql = new BoundSql(parseSql, parameterMappings);

        return boundSql;
    }
}
