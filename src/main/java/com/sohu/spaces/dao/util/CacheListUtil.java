package com.sohu.spaces.dao.util;

import java.util.Map;

import com.sohu.spaces.dao.configuration.CacheConfiguration;
import com.sohu.spaces.dao.configuration.model.CacheList;

public class CacheListUtil {
    public static String getSqlString(String name){
        CacheList cacheList = CacheConfiguration.getInstance().getListMap().get(name);
        if(cacheList!=null){
            return cacheList.getSqlString();
        }else {
            return null;
        }
    }

    public static String getCountSqlString(String name){
        CacheList cacheList = CacheConfiguration.getInstance().getListMap().get(name);
        if(cacheList!=null){
            return cacheList.getCountSqlString();
        }else {
            return null;
        }
    }

    public static Map getScalar(String name){
        CacheList cacheList = CacheConfiguration.getInstance().getListMap().get(name);
        if(cacheList!=null){
            return cacheList.getScalarMap();
        }else {
            return null;
        }
    }
}
