package com.sohu.spaces.dao.configuration.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class CacheObject {

    private String name;
    private Map<String, CacheList> listMap = new HashMap<String, CacheList>();

    public void  addListMap(CacheList cacheList){
        if (cacheList != null && StringUtils.isNotEmpty(cacheList.getName())) {
            if (!listMap.containsKey(cacheList.getName())) {
                cacheList.setCacheObjName(name);
                listMap.put(cacheList.getName(), cacheList);
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, CacheList> getListMap() {
        return listMap;
    }

    public void setListMap(Map<String, CacheList> listMap) {
        this.listMap = listMap;
    }



}
