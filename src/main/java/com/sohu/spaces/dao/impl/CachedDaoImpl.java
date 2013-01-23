package com.sohu.spaces.dao.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;

import com.sohu.spaces.cache.Cache;
import com.sohu.spaces.cache.exception.CacheException;
import com.sohu.spaces.dao.BaseDao;
import com.sohu.spaces.dao.configuration.CacheConfiguration;
import com.sohu.spaces.dao.configuration.model.CacheList;
import com.sohu.spaces.dao.configuration.model.CacheObject;
import com.sohu.spaces.dao.util.CacheKeyUtil;
import com.sohu.spaces.dao.util.CacheListUtil;
import com.sohu.spaces.dao.util.InvokeUtil;
import com.sohu.spaces.dao.util.RegionUtil;

public class CachedDaoImpl implements BaseDao {

    public static final Logger logger = Logger.getLogger(CachedDaoImpl.class);
    private BaseDao baseDao ;
    private Cache cache;


    public void setBaseDao(BaseDao baseDao) {
        this.baseDao = baseDao;
    }

    public void setCache(Cache cache) {
        this.cache = cache;
    }

    @Override
    public void delete(Object entity) {
        if(entity==null){
            return ;
        }
        //删除缓存数据
        String cacheKey = CacheKeyUtil.getObjectCacheKey(entity);
        if(cacheKey!=null){
            cache.remove(cacheKey);
        }
        //删除数据库数据
        baseDao.delete(entity);
        //删除列表数据
        this.delete(entity);
    }

    private void deleteListCache(Object entity){
        CacheObject cacheObject = CacheConfiguration.getInstance().getObjMap().get(entity.getClass().getName());
        Map<String, CacheList> map = null;
        if(cacheObject!=null){
            map = cacheObject.getListMap();
        }
        if(map!=null){
            String listCcountKey = "";
            String listRegionKey = "";
            Object[] params = null;
            for(String name : map.keySet()){
                try {
                    params = InvokeUtil.getObjParams(name, entity);
                    listCcountKey = CacheKeyUtil.getListCountKey(name, params);
                    listRegionKey = CacheKeyUtil.getListRegionKey(name, params);
                    cache.remove(listCcountKey);
                    cache.remove(listRegionKey);
                } catch (CacheException e) {
                    logger.error("error when invoke delete(Object entity) if InvokeUtil.getObjParams(name, entity) ",e);
                }
            }
        }
    }

    @Override
    public <T> void delete(Class<T> clazz, Serializable id) {
        delete(get(clazz, id));
    }

    @Override
    public <T> void deleteAll(Collection<T> entities) {
        for(T t : entities){
            delete(t);
        }
    }

    @Override
    public void execute(String sql, Object... values) {
        baseDao.execute(sql, values);
    }

    /***
     * 获取单对象是最简单的逻辑
     * 直接从缓存里读取，如果读取不到则从数据库加载并放入缓存
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Class<T> clazz, Serializable id) {
        String cacheKey = CacheKeyUtil.getObjectCacheKey(clazz, id);
        T t = (T) cache.get(cacheKey);
        if(t==null){
            t = baseDao.get(clazz, id);
            if(t!=null){
                cache.put(cacheKey, (Serializable) t);
            }
        }
        return t;
    }

    @Override
    public <T> T get(String sql, Object... values) {
        return baseDao.get(sql, values);
    }

    @Override
    public <T> T getByExample(T entity) {
        return baseDao.getByExample(entity);
    }

    @Override
    public <T> long getCount(Class<T> clazz) {
        return baseDao.getCount(clazz);
    }

    @Override
    public long getCount(String sql, Object... values) {
        Map map = new HashMap();
        map.put("ctn", Hibernate.LONG);
        Long count = 0L;
        String countKey = CacheKeyUtil.getListCountKey(sql, values);
        String theSQL = CacheListUtil.getCountSqlString(sql);
        count = (Long) cache.get(countKey);
        if(count == null){
            List<?> list = getObjectList(theSQL ,map, 0, 1, values);
            if(list!=null && list.size()>0){
                count = (Long) list.get(0);
                cache.put(countKey, count);
            }
        }
        return count==null?0L:count;
    }

    @Override
    public <T> List<T> list(Class<T> clazz) {
        return baseDao.list(clazz);
    }

    @Override
    public <T> List<T> list(String sql, Object... values) {
        return baseDao.list(sql, values);
    }

    @Override
    public <T> List<T> listByExample(T entity) {
        return baseDao.listByExample(entity);
    }

    //0-29   共120条数据，应该是从91开始
    //
    private List<Long> loadIds(String sql,Map scalar,Object[] objs , int region ){
        List<Long> list =  getObjectList(sql,scalar, region*RegionUtil.regionSize, RegionUtil.regionSize, objs);
        return list;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> listLimit(String sql, int start, int size, Object... values) {
        String theSQL = CacheListUtil.getSqlString(sql);
        Map scalar = CacheListUtil.getScalar(sql);
        String regionKey = CacheKeyUtil.getListRegionKey(sql, values);
        String objectName = CacheConfiguration.getInstance().getListMap().get(sql).getCacheObjName();
        Class clazz = null;
        try {
            clazz = Class.forName(objectName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        int[] regions = RegionUtil.getRegion(start, size);
        List ids = new ArrayList();

        Map<Integer, List> region = (Map<Integer, List>) cache.get(regionKey);
        if(region==null) region = new HashMap<Integer, List>();
        boolean reloadFlag = false;
        for(int i : regions){
            if(region.get(i)==null){
                region.put(i, loadIds(theSQL,scalar, values, i));
                reloadFlag = true ;
            }
            ids.addAll(region.get(i));
        }
        if(reloadFlag)
            cache.put(regionKey, (Serializable) region);

        //考虑是否以多线程方式实现
        List realIds = ids.subList(start, (start+size)>ids.size()?ids.size():(start+size));
        List<String> realKeys = new ArrayList<String>();
        if(realIds!=null){
            for(Object id : realIds){
                realKeys.add(CacheKeyUtil.getObjectCacheKey(clazz, (Serializable) id));
            }
        }

        List<T> result =  cache.getMulti(realKeys);
        if(result==null){
            result = new ArrayList<T>(realIds.size());
            for(int i=0; i<realIds.size();i++){
                //看效率,之后可以考虑使用union
                result.add(i, (T) this.get(clazz, (Serializable) realIds.get(i)));
            }
        }else {
            for(int i=0; i<realIds.size();i++){
                T t = result.get(i);
                if(t==null){
                    result.set(i, (T) this.get(clazz, (Serializable) realIds.get(i)));
                }
            }
        }

        return result;
    }

    @Override
    public List<Object[]> query(String sql, Object... values) {
        return baseDao.query(sql, values);
    }

    @Override
    public Serializable save(Object entity) {
        Serializable rstId =  baseDao.save(entity);
        entity = this.get(entity.getClass(), rstId);
        if(rstId!=null){
            this.deleteListCache(entity);
        }
        return rstId;
    }

    @Override
    public void saveOrUpdate(Object entity) {
        Object id = null;
        try {
            id = InvokeUtil.invokeIdMethod(entity);
        } catch (CacheException e) {
            logger.error("error when invoke saveOrUpdate of InvokeUtil.invokeIdMethod(entity) entity:"+entity,e);
        }
        if(id==null){
            this.save(entity);
        }else {
            this.update(entity);
        }
    }

    @Override
    public <T> void saveOrUpdateAll(Collection<T> entities) {
        for(T t : entities){
            this.saveOrUpdate(t);
        }
    }

    @Override
    public void update(Object entity) {
        Object id = null;
        try {
            id = InvokeUtil.invokeIdMethod(entity);
        } catch (CacheException e) {
            logger.error("error when invoke update of InvokeUtil.invokeIdMethod(entity) entity:"+entity,e);
        }
        if( id==null )
            return ;
        Object oldEntity = this.get(entity.getClass(), (Serializable)id);
        //更新数据库数据
        baseDao.update(entity);

        //删除对象缓存
        cache.remove(CacheKeyUtil.getObjectCacheKey(entity));

        //删除原对象的列表
        this.deleteListCache(oldEntity);
        this.deleteListCache(entity);
    }

    @Override
    public List getObjectList(final String sql,final Map scalar , final int start, final int size, final Object... values){
        return baseDao.getObjectList(sql,scalar, start, size, values);
    }


}
