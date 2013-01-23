package com.sohu.spaces.dao.ds;

public class DdsContext {

    public static ThreadLocal<String> dsType = new ThreadLocal<String>();

    public static void setDataSource(String datasource) {
        dsType.set(datasource);
    }

    public static String getDataSource() {
        return dsType.get();
    }

    public static void clearDataSource() {
        dsType.remove();
    }
}
