package com.sohu.spaces.dao.impl;


import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;


import com.sohu.spaces.dao.BaseDao;
import com.sohu.spaces.dao.ds.DdsContext;
import com.sohu.spaces.entry.model.Entry;



public class HibernateDaoImpl extends HibernateDaoSupport implements BaseDao {


    String messageOut = "Too Many results !  return the first";
    //common logger
    public static final Logger logger =  LoggerFactory.getLogger(HibernateDaoImpl.class);

    //寻找更优秀的解决方案
    private void determineDs(){
        DdsContext.setDataSource("readDataSource");
    }
    @Override
    public void delete(Object entity) {
        getHibernateTemplate().delete(entity);
    }

    @Override
    public <T> void delete(Class<T> clazz, Serializable id) {
        getHibernateTemplate().bulkUpdate("delete from "+clazz.getSimpleName()+" where id = ?",id);
    }

    @Override
    public <T> void deleteAll(Collection<T> entities) {
        getHibernateTemplate().deleteAll(entities);
    }

    @Override
    public void execute(String sql, Object... values) {
        getHibernateTemplate().bulkUpdate(sql, values);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Class<T> clazz, Serializable id) {
        this.determineDs();
        return (T) getHibernateTemplate().get(clazz, id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String sql, Object... values) {
        this.determineDs();
        List<T> list = getHibernateTemplate().find(sql, values);
        if(list == null || list.size()==0 ){
            return null ;
        }else {
            if (list.size()>1) {
                logger.warn(messageOut+"(......in get function "+sql+"---"+values+")");
            }
            return list.get(0);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getByExample(T entity) {
        this.determineDs();
        List<T> list = getHibernateTemplate().findByExample(entity);
        if(list == null || list.size()==0 ){
            return null ;
        }else {
            if (list.size()>1) {
                logger.warn(messageOut+"(......in getByExample function "+entity+")");
            }
            return list.get(0);
        }
    }

    @Override
    public <T> long getCount(Class<T> clazz) {
        this.determineDs();
        String  sql = "select count(*) from "+ clazz.getSimpleName();
        return (Long) getHibernateTemplate().find(sql).get(0) ;
    }

    @Override
    public long getCount(String sql, Object... values) {
        this.determineDs();
        sql = "select count(*) from "+ StringUtils.substringAfter(sql, "from");
        return (Long) getHibernateTemplate().find(sql, values).get(0) ;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> list(Class<T> clazz) {
        this.determineDs();
        return getHibernateTemplate().find("from " + clazz.getSimpleName());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> list(String sql, Object... values) {
        this.determineDs();
        return getHibernateTemplate().find(sql,values);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> listLimit(final String sql, final int start, final int size, final Object... values) {
        this.determineDs();
        return (List<T>) getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(final Session session) throws HibernateException, SQLException {

                final Query query = session.createQuery(sql);
                if(values != null){
                    for(int i =0 ; i<values.length ;i++)
                        query.setParameter(i, values[i]);
                }
                query.setFirstResult(start);
                query.setMaxResults(size);
                return query.list();

            }
        });
    }


    @SuppressWarnings("unchecked")
    @Override
    public List getObjectList(final String sql,final Map scalar , final int start, final int size, final Object... values) {
        this.determineDs();
        List list = null;
        try {
            list = (List) getHibernateTemplate().execute(new HibernateCallback() {
                public Object doInHibernate(Session session) throws HibernateException, SQLException {
                    SQLQuery query = session.createSQLQuery(sql);
                    if (null !=values && values.length > 0) {
                        int i = 0;
                        for (Object id : values) {
                            if (null != id) {
                                query.setParameter(i++, id);
                            }
                        }
                    }
                    if(MapUtils.isNotEmpty(scalar)){
                        for(Object obj: scalar.keySet()){
                            if(obj!=null){
                                query.addScalar((String)obj,(Type)scalar.get(obj));
                            }
                        }
                    }
                    query.setFirstResult(start);
                    if (0 != size) {
                        query.setMaxResults(size);
                    }
                    return query.list();
                }
            });
            if (null == list) {
                list = new ArrayList();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }



    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> listByExample(T entity) {
        this.determineDs();
        return getHibernateTemplate().findByExample(entity);
    }


    @Override
    public Serializable save(Object entity) {
        return getHibernateTemplate().save(entity);
    }

    @Override
    public void saveOrUpdate(Object entity) {
        getHibernateTemplate().saveOrUpdate(entity);
    }

    @Override
    public <T> void saveOrUpdateAll(Collection<T> entities) {
        getHibernateTemplate().saveOrUpdateAll(entities);
    }

    @Override
    public void update(Object entity) {
        getHibernateTemplate().update(entity);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List query(final String sql, final Object... values) {
        this.determineDs();
        return  (List) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(final Session session) throws HibernateException, SQLException {
                final Query query = session.createQuery(sql);
                if(values != null){
                    for(int i =0 ; i<values.length ;i++)
                        query.setParameter(i, values[i]);
                }
                return query.list();
            }
        });
    }
    public static void main(String[] args) {
        ApplicationContext app = new ClassPathXmlApplicationContext(new String[]{
                "/context/applicationContext-dal.xml","/context/applicationContext-cache.xml"
        });
        BaseDao baseDao = (BaseDao) app.getBean("cachedDao");

        //        for(int i=0 ; i<20;i++){
        //            long s1 = System.nanoTime();
        //        Entry entry = baseDao.get(Entry.class,38219L);
        //            System.out.println(System.nanoTime()-s1);
        //        }
        long s1 = System.nanoTime();
        List<Entry> list = baseDao.listLimit("get_user_all_entry_list", 2, 30, 75941L);
        System.out.println(list.size());
        long s2= System.currentTimeMillis();
        //        Entry entry = list.get(0);
        //        entry.setStatus(-1);
        //        baseDao.update(entry);
        //        System.out.println(System.currentTimeMillis()-s2);
        long count = baseDao.getCount("get_user_all_entry_list", 75941L);
        System.out.println(count);

        System.out.println(list);
        //        System.out.println(entry);
    }

}
