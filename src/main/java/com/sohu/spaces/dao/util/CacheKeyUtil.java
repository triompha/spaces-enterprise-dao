package com.sohu.spaces.dao.util;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.sohu.spaces.cache.exception.CacheException;

public class CacheKeyUtil {
    public static final Logger logger = Logger.getLogger(CacheKeyUtil.class);
    public static  String getObjectCacheKey(Class<?> clazz , Serializable id){
        return clazz.getSimpleName()+"_"+String.valueOf(id);
    }
    public static String getObjectCacheKey(Object obj){
        Object id = null;
        try {
            id = InvokeUtil.invokeIdMethod(obj);
        } catch (CacheException e) {
            logger.error("error when getObjectCacheKey of InvokeUtil.invokeIdMethod(obj) ,obj is"+obj,e);
        }
        if(id==null){
            return null;
        }
        return getObjectCacheKey(obj.getClass(), (Serializable) id);
    }

    public static String getListCountKey(String sql , Object[] param){
        return sql + "@"+StringUtils.join(param, "_")+"@C";
    }

    public static String getListRegionKey(String sql , Object[] param){
        return sql + "@"+StringUtils.join(param, "_")+"@R";
    }

}
