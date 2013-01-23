package com.sohu.spaces.dao.ds;

import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicDataSource extends AbstractRoutingDataSource {
    private static Logger logger = Logger.getLogger(DynamicDataSource.class);

    @Override
    protected Object determineCurrentLookupKey() {
        String datasourceName = DdsContext.getDataSource();
        logger.debug("current datasourceName is: " + datasourceName);
        return datasourceName;
    }

}
