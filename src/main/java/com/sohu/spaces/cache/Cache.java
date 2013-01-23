package com.sohu.spaces.cache;

import java.io.Serializable;
import java.util.List;

public interface Cache {


    public void put(String key,Serializable value);

    public void put(String key,Serializable value,int ttl);

    public <T> T get(String key);

    public void remove(String key);

    public <T> List<T> getMulti(List<String> keys);


}
