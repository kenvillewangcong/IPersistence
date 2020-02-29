package com.lagou.config;

import com.lagou.pojo.Configuration;
import com.lagou.pojo.MappedStatement;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;

public class XMLMapperBuilder {

    private Configuration configuration;

    public XMLMapperBuilder(Configuration configuration) {
        this.configuration = configuration;
    }

    public void parse(InputStream inputStream) throws DocumentException {
        Document document = new SAXReader().read(inputStream);

        Element rootElement = document.getRootElement();

        List<Element> selectList = rootElement.selectNodes("//select");
        setConfiguration(rootElement, "SELECT", selectList);

        List<Element> insertList = rootElement.selectNodes("//insert");
        setConfiguration(rootElement, "INSERT", insertList);

        List<Element> updateList = rootElement.selectNodes("//update");
        setConfiguration(rootElement, "UPDATE", updateList);

        List<Element> deleteList = rootElement.selectNodes("//delete");
        setConfiguration(rootElement, "DELETE", deleteList);

    }

    private void setConfiguration(Element rootElement, String sqlCommandType, List<Element> list) throws DocumentException {
        String namespace = rootElement.attributeValue("namespace");
        for (Element element : list) {
            String id = element.attributeValue("id");
            String resultType = element.attributeValue("resultType");
            String paramterType = element.attributeValue("paramterType");
            String sqlText = element.getTextTrim();
            MappedStatement mappedStatement = new MappedStatement();
            mappedStatement.setId(id);
            mappedStatement.setResultType(resultType);
            mappedStatement.setParamterType(paramterType);
            mappedStatement.setSql(sqlText);
            mappedStatement.setSqlCommandType(sqlCommandType);
            String key = namespace + "." + id;
            configuration.getMappedStatementMap().put(key, mappedStatement);
        }
    }


}
