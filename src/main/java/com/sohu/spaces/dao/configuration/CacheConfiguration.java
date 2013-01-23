package com.sohu.spaces.dao.configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.digester.Digester;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.sohu.spaces.cache.exception.CacheException;
import com.sohu.spaces.dao.configuration.model.CacheList;
import com.sohu.spaces.dao.configuration.model.CacheObject;



public class CacheConfiguration {
    public static final Logger logger = Logger.getLogger(CacheConfiguration.class);
    private final String defaultConfiguration = "/cache.xml";
    private static CacheConfiguration instance;
    private Map<String,CacheObject> objMap = new HashMap<String, CacheObject>();
    private Map<String,CacheList>   listMap = new HashMap<String, CacheList>();


    public Map<String, CacheObject> getObjMap() {
        return objMap;
    }

    public void setObjMap(Map<String, CacheObject> objMap) {
        this.objMap = objMap;
    }

    public Map<String, CacheList> getListMap() {
        return listMap;
    }

    public void setListMap(Map<String, CacheList> listMap) {
        this.listMap = listMap;
    }

    private CacheConfiguration(){
        init();
    }

    public static CacheConfiguration getInstance() {
        if (instance == null) {
            synchronized (CacheConfiguration.class) {
                if (instance == null) {
                    instance = new CacheConfiguration();
                }
            }
        }
        return instance;
    }

    @SuppressWarnings("unchecked")
    public void init(){
        Digester digester = new Digester();
        digester.setValidating(false);

        //对象的加载
        digester.addObjectCreate("cache", ArrayList.class);
        digester.addObjectCreate("cache/object", CacheObject.class);
        digester.addSetNext("cache/object", "add");
        digester.addSetProperties("cache/object");

        //对象的列表的加载
        digester.addObjectCreate("cache/object/list", CacheList.class);
        digester.addSetNext("cache/object/list", "addListMap");
        digester.addSetProperties("cache/object/list");



        try {
            Object objectList = digester.parse(CacheConfiguration.class.getResourceAsStream(defaultConfiguration));
            if (objectList != null && objectList instanceof ArrayList) {
                List<CacheObject> list = (List<CacheObject>) objectList;
                for (CacheObject objectItem : list) {
                    objMap.put(objectItem.getName(), objectItem);
                    try {
                        addCacheList(objectItem);
                    } catch (CacheException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("init error: " + e.getMessage());
        } catch (SAXException e) {
            logger.error("init parse fail : " + e.getMessage());
        }
    }
    public void addCacheList(CacheObject cacheObject) throws CacheException{
        if(cacheObject==null || MapUtils.isEmpty(cacheObject.getListMap())){
            return ;
        }
        for(CacheList cacheList : cacheObject.getListMap().values()){
            if(listMap.containsKey(cacheList.getName())){
                throw new CacheException("dublicate cachelist name ");
            }

            //设置count 的sql
            String sqlString = cacheList.getSqlString();
            if(StringUtils.isNotBlank(sqlString)){
                cacheList.setCountSqlString("select count(1) ctn from " + StringUtils.substringAfter(sqlString, "from"));

                if(StringUtils.isNotBlank(cacheList.getParam())){
                    cacheList.setParams(StringUtils.split(cacheList.getParam(), ","));
                }
                String returnType = cacheList.getReturnType();
                String[] returnTypes = null;
                if(StringUtils.isNotBlank(returnType)){
                    returnTypes = StringUtils.split(returnType,",");
                }
                Map map = cacheList.getScalarMap();
                if(!ArrayUtils.isEmpty(returnTypes)){
                    String[] returnNames = StringUtils.substringBetween(sqlString, "select", "from").split(",");
                    for(int i =0 ; i<returnNames.length;i++){
                        String[] tempReturn = StringUtils.split(returnNames[i], " ");
                        if(tempReturn.length>0){
                            returnNames[i] = tempReturn[tempReturn.length-1].replaceAll(" ", "");
                        }
                    }
                    if(map==null){
                        map = new HashMap();
                        cacheList.setScalarMap(map);
                    }
                    for(int j = 0 ; j< Math.min(returnTypes.length, returnNames.length); j++){
                        try {
                            map.put(returnNames[j], Class.forName(returnTypes[j]).newInstance());
                        } catch (ClassNotFoundException e) {
                            logger.error("configuration error of can't find Class", e);
                        } catch (InstantiationException e) {
                            logger.error("configuration error of can't find Class", e);
                        } catch (IllegalAccessException e) {
                            logger.error("configuration error of can't find Class", e);
                        }

                    }

                }

            }

            listMap.put(cacheList.getName(), cacheList);
        }

    }

    public static void main(String[] args) throws ClassNotFoundException {
        //        Type type = new LongType();
        //        CacheConfiguration configuration = CacheConfiguration.getInstance();
        //        Map<String, CacheObject> cacheMap = configuration.getObjMap();
        //        for(String key : cacheMap.keySet()){
        //            CacheObject obj = cacheMap.get(key);
        //            System.out.println(obj);
        //        }
        //        for(String key : configuration.getListMap().keySet()){
        //            CacheList list = configuration.getListMap().get(key);
        //            System.out.println(list);
        //        }
        //        String theReturn = "    aa  ";
        //        String[] returnS = StringUtils.split(theReturn, " ");
        //        System.out.println(returnS.length);


        //		Object[] objs = new Object[3];
        //		objs[0] = 1123123;
        //		objs[1] = "1sadfsdf";
        //		objs[2] = "true";
        //		System.out.println(StringUtils.join(objs, "_"));

    }


}
