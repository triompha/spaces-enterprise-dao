package com.sohu.spaces.dao.util;

public class RegionUtil {
	
	public static final int regionSize = 30;
	
	public static int[] getRegion(int start , int size ){
		int startRegion = start/regionSize;
		int endRegion = (start+size-1)/regionSize;
		int[] regions = new int[endRegion-startRegion+1];
		for(int i=0 ; i<regions.length;i++){
			regions[i] = startRegion+i;
		}
		return regions;
	}
	
	public static void main(String[] args) {
		int[] rst = RegionUtil.getRegion(41, 20);
		System.out.println(rst);
	}

}
