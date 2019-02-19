package com.systemonline.fanscoupon;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import java.io.InputStream;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;


@ReportsCrashes(
        formKey = "",
        mailTo = "ossama.m.adel@gmail.com",
        customReportContent = {
                ReportField.LOGCAT,
                ReportField.ANDROID_VERSION,
                ReportField.REPORT_ID,
                ReportField.STACK_TRACE
        },
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.app_crashed
)

public class MyApp extends Application {

    public static MyApp instance;
    private static Context mContext;
    Locale myLocale;


    public MyApp() {
        instance = this;
    }

    /**
     * return app context
     *
     * @return
     */


    public static Context getContext() {
        return mContext;
    }

    public static MyApp getInstance() {
        return instance;
    }

    public static InputStream loadCertAsInputStream() {
        return MyApp.mContext.getResources().openRawResource(
                R.raw.www_fanscoupon_com);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        FacebookSdk.sdkInitialize(getApplicationContext());

        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(
                        getResources().getString(R.string.CONSUMER_KEY),getResources().getString(R.string.CONSUMER_SECRET) ))
                .debug(true)
                .build();
        Twitter.initialize(config);


        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/Roboto-Medium.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        ACRA.init(this);
        mContext = getApplicationContext();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}