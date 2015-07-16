package com.lingmo.Utils;

public class LocConverter {
	public static double jzA = 6378245.0;
	public static double jzEE = 0.00669342162296594323;
	
	public static double RANGE_LON_MAX = 137.8347;
	public static double RANGE_LON_MIN = 72.004;
	public static double RANGE_LAT_MAX = 55.8271;
	public static double RANGE_LAT_MIN = 0.8293;
	
	public final static double LAT_OFFSET_0(double x, double y) {
		return -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
	}
	
	public final static double LAT_OFFSET_1(double x) {
		return (20.0 * Math.sin(6.0 * x * Math.PI) + 20.0 * Math.sin(2.0 * x * Math.PI)) * 2.0 / 3.0;
	}
	
	public final static double LAT_OFFSET_2(double y) {
		return (20.0 * Math.sin(y * Math.PI) + 40.0 * Math.sin(y / 3.0 * Math.PI)) * 2.0 / 3.0;
	}
	
	public final static double LAT_OFFSET_3(double y) {
		return (160.0 * Math.sin(y / 12.0 * Math.PI) + 320 * Math.sin(y * Math.PI / 30.0)) * 2.0 / 3.0;
	}
	
	public final static double LON_OFFSET_0(double x, double y) {
		return 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
	}
	
	public final static double LON_OFFSET_1(double x) {
		return (20.0 * Math.sin(6.0 * x * Math.PI) + 20.0 * Math.sin(2.0 * x * Math.PI)) * 2.0 / 3.0;
	}
	
	public final static double LON_OFFSET_2(double x) {
		return (20.0 * Math.sin(x * Math.PI) + 40.0 * Math.sin(x / 3.0 * Math.PI)) * 2.0 / 3.0;
	}
	
	public final static double LON_OFFSET_3(double x) {
		return (150.0 * Math.sin(x / 12.0 * Math.PI) + 300.0 * Math.sin(x / 30.0 * Math.PI)) * 2.0 / 3.0;
	}
	
	public static double transformLat(double x, double y)
	{
	    double ret = LAT_OFFSET_0(x, y);
	    ret += LAT_OFFSET_1(x);
	    ret += LAT_OFFSET_2(y);
	    ret += LAT_OFFSET_3(y);
	    return ret;
	}

	public static double transformLon(double x, double y)
	{
	    double ret = LON_OFFSET_0(x, y);
	    ret += LON_OFFSET_1(x);
	    ret += LON_OFFSET_2(x);
	    ret += LON_OFFSET_3(x);
	    return ret;
	}
	
	public static boolean outOfChina(double lat, double lon)
	{
	    if (lon < RANGE_LON_MIN || lon > RANGE_LON_MAX)
	        return true;
	    if (lat < RANGE_LAT_MIN || lat > RANGE_LAT_MAX)
	        return true;
	    return false;
	}
	
	public static Coordinate2D gcj02Encrypt(double ggLat, double ggLon)
	{
	    Coordinate2D resPoint = new Coordinate2D();
	    double mgLat;
	    double mgLon;
	    if (outOfChina(ggLat, ggLon)) {
	        resPoint.lat = ggLat;
	        resPoint.lng = ggLon;
	        return resPoint;
	    }
	    double dLat = transformLat(ggLon - 105.0, ggLat - 35.0);
	    double dLon = transformLon(ggLon - 105.0, ggLat - 35.0);
	    double radLat = ggLat / 180.0 * Math.PI;
	    double magic = Math.sin(radLat);
	    magic = 1 - jzEE * magic * magic;
	    double sqrtMagic = Math.sqrt(magic);
	    dLat = (dLat * 180.0) / ((jzA * (1 - jzEE)) / (magic * sqrtMagic) * Math.PI);
	    dLon = (dLon * 180.0) / (jzA / sqrtMagic * Math.cos(radLat) * Math.PI);
	    mgLat = ggLat + dLat;
	    mgLon = ggLon + dLon;
	    
	    resPoint.lat = mgLat;
	    resPoint.lng = mgLon;
	    return resPoint;
	}
	
	public static Coordinate2D gcj02Decrypt(double gjLat, double gjLon)
	{
	    Coordinate2D  gPt = gcj02Encrypt(gjLat, gjLon);
	    double dLon = gPt.lng - gjLon;
	    double dLat = gPt.lat - gjLat;
	    Coordinate2D pt = new Coordinate2D();
	    pt.lat = gjLat - dLat;
	    pt.lng = gjLon - dLon;
	    return pt;
	}
	
	public static Coordinate2D bd09Decrypt(double bdLat, double bdLon)
	{
	    Coordinate2D gcjPt = new Coordinate2D();
	    double x = bdLon - 0.0065, y = bdLat - 0.006;
	    double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * Math.PI);
	    double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * Math.PI);
	    gcjPt.lng = z * Math.cos(theta);
	    gcjPt.lat = z * Math.sin(theta);
	    return gcjPt;
	}
	
	public static Coordinate2D bd09ToGcj02(Coordinate2D location)
	{
	    return bd09Decrypt(location.lat, location.lng);
	}
	
	public static Coordinate2D bd09ToWgs84(Coordinate2D location)
	{
	    Coordinate2D gcj02 = bd09ToGcj02(location);
	    return gcj02Decrypt(gcj02.lat, gcj02.lng);
	}

}
