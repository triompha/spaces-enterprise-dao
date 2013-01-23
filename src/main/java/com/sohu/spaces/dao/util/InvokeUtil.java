package com.sohu.spaces.dao.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.persistence.Id;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import com.sohu.spaces.cache.exception.CacheException;
import com.sohu.spaces.dao.configuration.CacheConfiguration;
import com.sohu.spaces.dao.configuration.model.CacheList;
import com.sohu.spaces.entry.model.Entry;

public class InvokeUtil {

    public static final Logger logger = Logger.getLogger(InvokeUtil.class);

    /***
     **使用反射，获取注解@ID的JPA的ID的值
     * @param clazz
     * @return
     * @throws CacheException
     */
    public static Object invokeIdMethod(Object obj) throws CacheException{
        if(obj==null)
            return null;
        Method[] methods = obj.getClass().getDeclaredMethods();
        if(methods==null){
            return null;
        }
        for(Method m : methods){
            if(m.isAnnotationPresent(Id.class)){
                try {
                    return m.invoke(obj);
                } catch (IllegalArgumentException e) {
                    logger.error("invoke invokeIdMethod error1 " ,e);
                } catch (IllegalAccessException e) {
                    logger.error("invoke invokeIdMethod error2 " ,e);
                } catch (InvocationTargetException e) {
                    logger.error("invoke invokeIdMethod error3 " ,e);
                }
            }
        }
        return null;
    }



    public static Object[] getObjParams(String cacheKey , Object obj) throws CacheException{
        CacheList cacheList =  CacheConfiguration.getInstance().getListMap().get(cacheKey);
        if(cacheList==null)
            return null;
        String[] paramsPros = cacheList.getParams();
        if(paramsPros==null)
            return null;
        Object[] params = new Object[paramsPros.length];
        Object param = null;
        for(int i=0;i<paramsPros.length;i++){
            try {
                param = PropertyUtils.getProperty(obj,  paramsPros[i]);
            } catch (IllegalAccessException e) {
                logger.error("invoke getObjParams error1 " ,e);
            } catch (InvocationTargetException e) {
                logger.error("invoke getObjParams error2 " ,e);
            } catch (NoSuchMethodException e) {
                logger.error("invoke getObjParams error3 " ,e);
            }
            if(param==null)
                throw new CacheException("null obj param");

            params[i] = param;
        }

        return params;
    }

    public static void main(String[] args) throws CacheException {
        Entry entry = new Entry();
        entry.setEntryId(12121l);
        entry.setUserId(1212121212l);
        System.out.println(InvokeUtil.invokeIdMethod(entry));

        System.out.println(InvokeUtil.getObjParams("get_user_all_entry_list", entry));
    }
}
