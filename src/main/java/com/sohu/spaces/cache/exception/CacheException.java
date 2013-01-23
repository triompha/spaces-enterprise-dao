package com.sohu.spaces.cache.exception;


public class CacheException extends Exception{
    /**
     * cache的异常，
     * 这里暂时只做类型处理。
     * 
     */
    private static final long serialVersionUID = -7648265752111485942L;

    public CacheException(){
        super();
    }
    public CacheException(String message){
        super(message);
    }

}
