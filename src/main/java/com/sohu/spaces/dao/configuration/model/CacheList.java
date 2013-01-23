package com.sohu.spaces.dao.configuration.model;

import java.util.Map;

import org.hibernate.type.Type;

public class CacheList {
    private String name;
    private String sqlString;
    private String countSqlString;
    private String key;
    private String param;
    private String cacheObjName ;
    private String returnType;
    private String[] params;
    private Map<String,Type> scalarMap;



    public Map<String, Type> getScalarMap() {
        return scalarMap;
    }
    public void setScalarMap(Map<String, Type> scalarMap) {
        this.scalarMap = scalarMap;
    }
    public String getParam() {
        return param;
    }
    public void setParam(String param) {
        this.param = param;
    }
    public String getReturnType() {
        return returnType;
    }
    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String[] getParams() {
        return params;
    }
    public void setParams(String[] params) {
        this.params = params;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSqlString() {
        return sqlString;
    }
    public void setSqlString(String sqlString) {
        this.sqlString = sqlString;
    }
    public String getCountSqlString() {
        return countSqlString;
    }
    public void setCountSqlString(String countSqlString) {
        this.countSqlString = countSqlString;
    }
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getCacheObjName() {
        return cacheObjName;
    }
    public void setCacheObjName(String cacheObjName) {
        this.cacheObjName = cacheObjName;
    }

}
