package com.systemonline.fanscoupon.Helpers;


public class Const {
    public static final int qr_width = 500;
    public static final int qr_height = 500;
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "fansCoupon";
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    public static final int REQUEST_CHECK_SETTINGS = 0x1;
    public static String webServiceURL = "https://www.fanscoupon.com/";
    //    public static String webServiceURL = "http://test.fanscoupon.com/";
    //    public static String webServiceURL = "http://test.fanscoupon.com/";
//        public static String webServiceURL = "http://192.168.0.244:8000/";
    //    public static String localWebServiceURL = "http://192.168.0.10:8000/";
    public static String imagesURL = webServiceURL + "images/";
    //    public static String localImagesURL = localWebServiceURL + "images/";
    public static String couponsURL = "http://test.fanscoupon.com/en/coupon/";
}
