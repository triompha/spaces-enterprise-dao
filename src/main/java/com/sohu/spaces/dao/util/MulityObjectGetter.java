//package com.sohu.spaces.dao.util;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.Callable;
//import java.util.concurrent.CompletionService;
//import java.util.concurrent.ExecutorCompletionService;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import org.apache.commons.lang.ArrayUtils;
//
//import com.sohu.spaces.cache.Cache;
//import com.sohu.spaces.dao.BaseDao;
//
//
//public class MulityObjectGetter {
//
//    private final static long TIME_OUT = 100;
//    private static final int BATCH_GET_NUMBER = 20;
//
//    private static final ExecutorService executors = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
//
//    public static MulityObjectGetter multiGetter;
//
//    public static Object[] get(final BaseDao dao, Long[] keys , final Class clazz) {
//        //初次设定一次最多取30
//        if (!ArrayUtils.isEmpty(keys) && keys.length > BATCH_GET_NUMBER) {
//            Object[] lObjects = new Object[0];
//            Map<String, Object[]> resMap = new HashMap<String, Object[]>();
//            try {
//                CompletionService completionService = new ExecutorCompletionService(executors);
//                int i = 0;
//                int index = 0;
//                // 启动线程
//                while (true) {
//                    final String[] section_keys = (String[]) ArrayUtils.subarray(keys, index, index += BATCH_GET_NUMBER);
//                    if (ArrayUtils.isEmpty(section_keys)) {
//                        break;
//                    } else {
//                        i++;
//                        final Integer queueNum = new Integer(i);
//                        completionService.submit(new Callable() {
//                            public Object call() {
//                            	return dao.get(clazz, id);
//                            }
//
//                        });
//                    }// else
//                }
//                for (int j = 0; j < i; j++) {
//                    Future future = completionService.poll(TIME_OUT, TimeUnit.SECONDS);
//                    if (future != null) {
//                        Map<String, Object[]> map = (Map<String, Object[]>) future.get();
//                        resMap.putAll(map);
//                    } else {
//                        throw new CacheException("poll timeout!");
//                    }
//                }
//                for (int j = 1; j <= i; j++) {
//                    lObjects = ArrayUtils.addAll(lObjects, resMap.get(j + ""));
//                }
//            } catch (Exception e) {
//                e.printStackTrace(System.out);
//            }
//            return lObjects;
//        } else {
//            return cache.get(keys);
//        }
//    }
//
//    public static void main(String[] args) {
//        System.out.println(Runtime.getRuntime().availableProcessors());
//    }
//}
