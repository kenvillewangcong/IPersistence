package com.lagou.sqlSeesion;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public interface Sqlsession {

    // 查询所有
    <E> List<E> selectList(String statementId, Object... params) throws IllegalAccessException, IntrospectionException, InstantiationException, NoSuchFieldException, SQLException, InvocationTargetException, ClassNotFoundException;

    // 根据条件查询单个
    <T> T selectOne(String statementId, Object... params) throws IllegalAccessException, ClassNotFoundException, IntrospectionException, InstantiationException, SQLException, InvocationTargetException, NoSuchFieldException;

    // 为Dao接口生成代理实现类
    public <T> T getMapper(Class<?> mapperClass);

    /**
     * 新增
     * @param statementId
     * @param params
     * @return
     */
    int insert(String statementId, Object... params) throws ClassNotFoundException, SQLException, IllegalAccessException, NoSuchFieldException;

    /**
     * 更新
     * @param statementId
     * @param params
     * @return
     */
    int update(String statementId, Object... params) throws ClassNotFoundException, SQLException, IllegalAccessException, NoSuchFieldException;

    /**
     * 删除
     * @param statementId
     * @param params
     * @return
     */
    int delete(String statementId, Object... params) throws ClassNotFoundException, SQLException, IllegalAccessException, NoSuchFieldException;

}
