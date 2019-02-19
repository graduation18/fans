package com.systemonline.fanscoupon;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Picasso;
import com.systemonline.fanscoupon.Base.BaseActivity;
import com.systemonline.fanscoupon.GCM.CommonUtilities;
import com.systemonline.fanscoupon.GCM.ServerUtilities;
import com.systemonline.fanscoupon.Helpers.Const;
import com.systemonline.fanscoupon.Helpers.Utility;
import com.systemonline.fanscoupon.Model.Coupon;
import com.systemonline.fanscoupon.Model.DefaultExceptionHandler;
import com.systemonline.fanscoupon.Model.Fan;
import com.systemonline.fanscoupon.WebServices.JSONAsync;
import com.systemonline.fanscoupon.WebServices.JSONWebServices;
import com.systemonline.fanscoupon.WebServices.ParseData;
import com.systemonline.fanscoupon.brands_tabs.BrandsTab;
import com.systemonline.fanscoupon.coupon_tabs.CouponTab;
import com.systemonline.fanscoupon.credits_tabs.MyCreditsFragment;
import com.systemonline.fanscoupon.db.DatabaseHandler;
import com.systemonline.fanscoupon.experience_tabs.ExperienceTab;
import com.systemonline.fanscoupon.friends_tabs.MyFriendsFragment;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends BaseActivity {

    public static int locationTimeoutCtr = 0, onActivityResultType = 0;
    public static String WS_ACCESSTOKEN, selectedFBBrand, IMEI = "", brandPageLiked = "", mapType, langResult;
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;
    public static Coupon selectedCoupon;
    public static Fan myProfile;
    public static CallbackManager callbackManagerAdapter;
    public static Button logout;
    public static double latitudeSrc = 0.0, longitudeSrc = 0.0;
    public static LatLng selectedBranchLatLng;
    public static AlertDialog alertNoGPS;
    public static Utility _utility;
    public static JSONAsync jsonAsync;
    public static Fan currentFan;
    public static Handler handlerGPS;// = new Handler();
    public static EditText birthdate;
    //    public static FirebaseAuth mAuth;
    static AsyncTask<Void, Void, Void> mRegisterTask;
    static NavigationView navigationView;
    static ArrayList<Integer> settingsUpdate = new ArrayList<>();
    //    private String[] navMenuTitles;
    protected LocationRequest mLocationRequest;
    protected LocationSettingsRequest mLocationSettingsRequest;
    Fragment fragment = null;
    DrawerLayout drawer;
    AlertDialog.Builder builderNoGPS;
    //    ProfileTracker profileTracker;
//    AccessTokenTracker accessTokenTracker;
    Locale myLocale;
    JSONAsync call;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    boolean doubleBackToExitPressedOnce = false;
    private DatabaseHandler dbh;
    private Location mLastLocation;
    private ProgressDialog pdLocation;
    private GoogleApiClient mGoogleApiClient;
    private FragmentRefreshListener allCouponsRefreshListener, forYouCouponsRefreshListener;
    private String requestType = "";
    private AlertDialog alertDialog;
    /**
     * periodically check gps to get location
     */
    Runnable runnableGPS = new Runnable() {
        @Override
        public void run() {
            mLastLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                latitudeSrc = mLastLocation.getLatitude();
                longitudeSrc = mLastLocation.getLongitude();
                pdLocation.hide();
                handlerGPS.removeCallbacks(runnableGPS);
                goToMapFragment();
            } else {
                if (locationTimeoutCtr == 6) {
                    pdLocation.hide();
                    handlerGPS.removeCallbacks(runnableGPS);
                    alertDialog.setMessage(getString(R.string.no_loc));
                    alertDialog.show();

                    //couldn't get location
                    locationTimeoutCtr = 0;
                } else {
                    locationTimeoutCtr++;
                    handlerGPS.postDelayed(this, 5000);
                }
            }
        }
    };
    private FirebaseAnalytics mFirebaseAnalytics;


//    void sendRefferer(Context c, String referrer) {
//        Log.e("0main activity", "0fn - sendRefferer");
//
//        String urlJsonArry = "http://market-it-online.com/brands/brands.php?METHOD=SEND_REFERRER_DATA" +
//                "&USER_ACCOUNT=" + MainActivity.sharedPreferences.getString("user_account", "No Registered Account") +
//                "&REFERRER=" + referrer;
//
//        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, urlJsonArry
//                , null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//
//                try {
//                    if (response.getString("result").equals("FALSE")) {
//                        editor.putBoolean("referrer_status", true);
//                    } else {
//                        editor.putBoolean("referrer_status", false);
//                    }
//                    editor.commit();
//                } catch (JSONException e) {
////                    Toast.makeText(getActivity(),
////                            "volley exception",
////                            Toast.LENGTH_LONG).show();
//                    Log.e("volley exception ", e.toString());
//                }
//            }
//
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                error.printStackTrace();
//                //Log.e("Get data from MySQL ", error.getMessage());
//            }
//        });
//
//        ConnectionController.getInstance(c).add(req);
//    }

    static void setUserDataNavDrawer() {
        Log.e("Main activity", "set user data in drawer");
        View header = navigationView.getHeaderView(0);
        TextView username = (TextView) header.findViewById(R.id.drawer_user_name);
        username.setText(currentFan.getFanFirstName());
        ImageView userPicture = (ImageView) header.findViewById(R.id.drawer_user_image);
        Picasso.with(_utility.getCurrentActivity()).load(Const.imagesURL + "users/75x75/" + currentFan.getFanImage()).placeholder(R.drawable.ph_user).into(userPicture);

    }

    /**
     * register this mobile to GCM server
     */
    static void registerToGCM() {
        try {
            GCMRegistrar.checkDevice(_utility.getCurrentActivity());
            GCMRegistrar.checkManifest(_utility.getCurrentActivity());
            final String regId = GCMRegistrar.getRegistrationId(_utility.getCurrentActivity());
            if (regId.equals("")) {
                Log.e("Main act.", "GCM first time");
                GCMRegistrar.register(_utility.getCurrentActivity(), CommonUtilities.SENDER_ID);
            } else {
                if (!GCMRegistrar.isRegisteredOnServer(_utility.getCurrentActivity())) {
                    final Context context = _utility.getCurrentActivity();
                    mRegisterTask = new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            ServerUtilities.register(context, regId, MainActivity.currentFan.getFanID(), IMEI);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void result) {
                            mRegisterTask = null;
                        }
                    };
                    mRegisterTask.execute(null, null, null);
                } else
                    Log.e("Main act.", "GCM this device is registered");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public FragmentRefreshListener getAllCouponsRefreshListener() {
        return allCouponsRefreshListener;
    }

    public void setAllCouponsRefreshListener(FragmentRefreshListener fragmentRefreshListener) {
        this.allCouponsRefreshListener = fragmentRefreshListener;
    }

    public FragmentRefreshListener getForYouRefreshListener() {
        return forYouCouponsRefreshListener;
    }

    public void setForYouCouponsRefreshListener(FragmentRefreshListener fragmentRefreshListener) {
        this.forYouCouponsRefreshListener = fragmentRefreshListener;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _utility = new Utility(getBaseContext());
        Log.e("Main activity", "set current activity");
        _utility.SetCurrentActivity(this);
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        setLanguage();
        Log.e("Main activity", "lang set " + langResult);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                finishAffinity();
            } else {
                finish();
            }
            Intent i = new Intent(MainActivity.this, SplashScreen.class);
            startActivity(i);
        }


        //To Restart if exits with crash ******************

        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this));


//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    "com.systemonline.fanscoupon",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.e("KeyHash", "KeyHash:" + Base64.encodeToString(md.digest(),
//                        Base64.DEFAULT));
//                Toast.makeText(getApplicationContext(), Base64.encodeToString(md.digest(),
//                        Base64.DEFAULT), Toast.LENGTH_LONG).show();
//                Log.e("keyhash2", Base64.encodeToString(md.digest(),
//                        Base64.DEFAULT) + "");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
//        mAuth = FirebaseAuth.getInstance();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        logout = (Button) header.findViewById(R.id.drawer_logout);
        logout.setText(getResources().getString(R.string.login));
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Main activity", "logout button");
                if (logout.getText().toString().equals(getResources().getString(R.string.logout))) {
                    Log.e("Main activity", "logout button");

                    dbh.dropDatabase();
                    editor.clear();
                    editor.commit();
                    editor.putString("lang", langResult);
                    editor.commit();
                    currentFan = null;
                    stopService(new Intent(MainActivity.this, CampaignLocationsService.class));
                    LoginManager.getInstance().logOut();
                    ServerUtilities.unregister(_utility.getCurrentActivity());
//                    CouponTab.allCoupons = null;
                    CouponTab.allCoupons2 = null;

//                    CouponTab.forYou = null;
                    CouponTab.forYou2 = null;
                    unregisterGCM(IMEI);
                } else {
                    Log.e("Main activity", "login button");
//                    if (langResult.equals("ar")) {
//                        drawer.closeDrawer(GravityCompat.END);
//                    } else {
                    drawer.closeDrawer(GravityCompat.START);
//                    }
                    fragmentTransaction(new FbMainFragment());
                }
            }
        });
        dbh = new DatabaseHandler(_utility.getCurrentActivity());

        Log.e("db creation", "--" + sharedPreferences.getBoolean("db", false));

//        editor.putLong("camp_last_update", -1).commit();
//        dbh.dropDatabase();
//        dbh.createDB();


        if (!sharedPreferences.getBoolean("db", false)) {
            editor.putLong("camp_last_update", -1);
            editor.commit();
            dbh.createDB();
            MainActivity.editor.putBoolean("db", true);
            MainActivity.editor.commit();
        }

        try {
            TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            IMEI = mngr.getDeviceId();
        } catch (Exception e) {
            Log.e("Main act. onCreate", "Error getting IMEI");
            e.printStackTrace();
        }

        if (_utility.isConnectingToInternet()) {
            int referrer_status = sharedPreferences.getInt("referrer_status", -1);
            if (referrer_status != -1 && referrer_status == 0) {
                if (!sharedPreferences.getString("referrer", "").isEmpty())
                    sendReferrerID(sharedPreferences.getString("referrer", ""), IMEI);
            }
        }

        if (sharedPreferences.getInt("admin_id", -1) != -1) {
            currentFan = new Fan();
            currentFan.setFanFirstName(sharedPreferences.getString("admin_name", "Name not found!"));
            currentFan.setStopNotification(sharedPreferences.getBoolean("mobile_notification", false));
            currentFan.setFanEMAil(sharedPreferences.getString("admin_mail", "Mail not found!"));
            currentFan.setFanID(sharedPreferences.getInt("admin_id", -1));
            currentFan.setFanCountryID(sharedPreferences.getInt("country_id", 36));
            currentFan.setFanImage(sharedPreferences.getString("admin_picture", ""));
            Log.e("MainAct. current User", currentFan.getFanFirstName());
            Log.e("MainAct.stop UserNotify", currentFan.isStopNotification() + " <<");
            setUserDataNavDrawer();
            if (!sharedPreferences.getString("accessToken", "empty").equals("empty")
                    && sharedPreferences.getLong("accessTokenCreation", -1) != -1) {
                WS_ACCESSTOKEN = sharedPreferences.getString("accessToken", "empty");
//                Log.e("accessToken creation", sharedPreferences.getLong("accessTokenCreation", -1) + "");
//                Log.e("current time", (System.currentTimeMillis() / 1000) + "");
//                Log.e("accessto equation resul", (System.currentTimeMillis() / 1000) - sharedPreferences.getLong("accessTokenCreation", -1) + "");

//                if ((System.currentTimeMillis() / 1000) - sharedPreferences.getLong("accessTokenCreation", -1) > 4200000) {
//                    Log.e("accessToken", "expired and make request");
//                    Log.e("accessToken main act", "request");
//                    requestType = "requestNewAccessToken";
//                    Fan.requestNewAccessToken(sharedPreferences.getString("fb_access_token", "no value"), MainActivity.this);
////                    requestNewAccessToken(sharedPreferences.getString("fb_access_token", "no value"));
//                } else {
//                    Log.e("accessToken", "get from shared pref");
                if (_utility.isConnectingToInternet())
                    registerToGCM();
                fragment = new CouponTab();
                logout.setText(getResources().getString(R.string.logout));
//                }
            } else {
                Log.e("accessToken main act", "request");
                requestType = "requestNewAccessToken";
                call = Fan.requestNewAccessToken(sharedPreferences.getString("fb_access_token", "no value"), MainActivity.this,
                        sharedPreferences.getString("fb_account_id", "no value"));
//                       requestNewAccessToken(sharedPreferences.getString("fb_access_token", "no value"));
            }
            startService();
//            periodicStartService();

        } else {
//            fragment = new FbMainFragment();
            fragment = new CouponTab();
        }


/////////////////////////////////////////////////////////////////////
        Bundle extras = getIntent().getExtras();
        if (extras != null && (extras.containsKey("coupSlug") && extras.containsKey("coupType"))) {
            Log.e("intent data", ">> " + extras.getString("coupSlug"));
            Log.e("intent data", ">> " + extras.getString("coupType"));
            fragment = new SingleCouponFragment();
            selectedCoupon = new Coupon();
            selectedCoupon.setCouponID(0);
            selectedCoupon.setCouponType(extras.getString("coupType"));
            selectedCoupon.setCouponSlug(extras.getString("coupSlug"));
            notifyCampaignClicked(extras.getString("campaignID"));
        }


        if (getIntent() != null && getIntent().getAction() != null) {
            try {
//            String action = getIntent().getAction();
//            Log.e("Main activity action", action + "+");
                Uri data = getIntent().getData();
                Log.e("Main activity intent", "data " + data.getPath());
                if (!data.getPath().isEmpty()) {
//                    if (fragment != null) {
//                        fragmentTransaction(fragment);
//
//                    } else {
//                        Log.e("Main activity", "fragment variable equals null");
//                        Log.e("Main activity", "current activity " + _utility.getCurrentActivity());
//                        _utility.ShowDialog(getResources().getString(R.string.plz_w8), false);
//                    }
//                } else {
                    String[] splitter = data.getPath().split("/");
                    for (int i = 0; i < splitter.length; i++) {
                        Log.e("Main activity splitter", i + " " + splitter[i]);

                    }
//                    Log.e("Main activity splitter", "1 " + splitter[1]);
//                    Log.e("Main activity splitter", "2 " + splitter[2]);


                    if (splitter[2].equals("coupon")) {
                        fragment = null;
                        selectedCoupon = new Coupon();
                        selectedCoupon.setCouponID(0);
                        selectedCoupon.setCouponSlug(splitter[3]);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e("Main activity intent", "no intent");

        }

        if (fragment != null) {
            fragmentTransaction(fragment);
        } else {
            Log.e("Main activity", "fragment variable equals null");
            Log.e("Main activity", "current activity " + _utility.getCurrentActivity());
            _utility.ShowDialog(getResources().getString(R.string.plz_w8), false);
            getCoupType(selectedCoupon.getCouponSlug());
        }
//        }

//
//        accessTokenTracker = new AccessTokenTracker() {
//            @Override
//            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
//                Log.e("access token tracker", "access token changed");
////                try {
////                    Log.e("access token tracker", "current access token: " + AccessToken.getCurrentAccessToken().getToken());
////
////                } catch (Exception e) {
////                    e.printStackTrace();
////                }
//            }
//        };
//
//        profileTracker = new ProfileTracker() {
//            @Override
//            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
//                Log.e("profile tracker", "profile changed");
////                editor.clear();
////                editor.commit();
//                currentFan = null;
//            }
//        };
//        accessTokenTracker.startTracking();
//        profileTracker.startTracking();

        handlerGPS = new Handler();
        pdLocation = new ProgressDialog(this);
        pdLocation.setTitle(R.string.load);
        pdLocation.setMessage(getResources().getString(R.string.get_loc));
        pdLocation.setIndeterminate(false);
        pdLocation.setCancelable(false);

        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setIcon(R.drawable.wrong);
        alertDialog.setButton(getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        Log.e("0k pressed", "");

                    }
                });
        builderNoGPS = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        builderNoGPS.setMessage(getResources().getString(R.string.gps_closed))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        checkLocationSettings();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        alertNoGPS = builderNoGPS.create();

        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
        else
            Log.e("main google api client", "null");

        if (checkPlayServices()) {
            buildGoogleApiClient();
            createLocationRequest();
            buildLocationSettingsRequest();
        }


//        else {
//            _utility.showMessage(getResources().getString(R.string.couldnt), true);
//        }
    }


    private void notifyCampaignClicked(String campaignID) {
        requestType = "cloudCampClickedReport";
        JSONWebServices service = new JSONWebServices(MainActivity.this);
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("campaign_id", campaignID));
        call = service.cloudCampClickedReport(nameValuePairs);
    }

    /**
     * start promotions notifications service
     */
    private void startService() {
        Log.e("MainActivity", "0fn - startService");

//        try {
//            stopService(new Intent(MainActivity.this, CampaignLocationsService.class));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        String serviceState = sharedPreferences.getString("toggle", "no_serv");

//        if (serviceState.equals("no_serv")) {
        if (!isMyServiceRunning(CampaignLocationsService.class)) {
            startService(new Intent(this, CampaignLocationsService.class));
            Log.e("MainActivity", "0fn - startService yessssssssssssssssssssss");

//            editor.putString("toggle", "true");
//            editor.commit();
        }
    }

    private void periodicStartService() {
        // Start service using AlarmManager

        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.HOUR, 24);
        cal.add(Calendar.MILLISECOND, 15);

        Intent intent = new Intent(this, CampaignLocationsService.class);

        PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);

        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int i;
        i = 86400;//24 Hours
        i = 120;
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                i * 1000, pintent);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void setLanguage() {
        langResult = sharedPreferences.getString("lang", "NO_LANG");
        switch (langResult) {
            case "en":
                setLocale("en");
                CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("font/Roboto-Medium.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
                );
                break;
            case "ar":
                setLocale("ar");
                CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("font/DroidKufi-Regular.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
                );
                break;
            case "NO_LANG":
                if (Locale.getDefault().getDisplayLanguage().equals("العربية")) {
                    setLocale("ar");
                    editor.putString("lang", "ar");
                    langResult = "ar";
                    editor.commit();
                    CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                            .setDefaultFontPath("font/DroidKufi-Regular.ttf")
                            .setFontAttrId(R.attr.fontPath)
                            .build()
                    );
//                } else if (Locale.getDefault().getDisplayLanguage().equals("English")) {
                } else {
                    setLocale("en");
                    editor.putString("lang", "en");
                    langResult = "en";
                    editor.commit();
                    CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                            .setDefaultFontPath("font/Roboto-Medium.ttf")
                            .setFontAttrId(R.attr.fontPath)
                            .build()
                    );
                }
                break;
        }
    }

    public void fragmentTransaction(Fragment fragment) {
        try {
            Log.e("Main activity", "process fragment transaction");

            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        Log.e("Main act.", "on destroy");
        if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }
        try {
            GCMRegistrar.onDestroy(this);
        } catch (Exception e) {
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        cancelRequestIfPossible();
        Log.e("onBack", getSupportFragmentManager().getBackStackEntryCount() + "--");


        if (langResult.equals("ar")) {
            if (drawer.isDrawerOpen(GravityCompat.END)) {
                drawer.closeDrawer(GravityCompat.END);
                return;
            }
//            else {
//                super.onBackPressed();
//            }
        } else {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
                return;
            }
//            else {
//                super.onBackPressed();
//            }
        }


        Log.e("onBack", "back pressed");
        try {
            if (getSupportFragmentManager().getBackStackEntryCount() < 2) {

                if (doubleBackToExitPressedOnce) {
                    if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
//                        Log.e("onBack", "getFragments().get(0) == null");
                        Log.e("onBack", "last fragment");
                        finish();
                    }
//                    finish();
                } else {
                    this.doubleBackToExitPressedOnce = true;
                    _utility.showMessage(getString(R.string.press_back_again), true);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce = false;
                        }
                    }, 3000);
                }

            } else
                super.onBackPressed();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("onBack", "error= " + e.getMessage());
        }
    }

    private void cancelRequestIfPossible() {
        try {
            if (jsonAsync.cancel(true))
                _utility.showMessage(getString(R.string.request_cancelled));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if (!_utility.isConnectingToInternet_ping()) {
            _utility.showMessage(getString(R.string.no_net));
            return true;
        }
        // Handle navigation view item clicks here.
        cancelRequestIfPossible();
        int id = item.getItemId();

        switch (id) {
            case R.id.coupons:
                fragment = new CouponTab();
                break;

            case R.id.my_account:
                if (currentFan == null) {
                    _utility.showMessage(getString(R.string.not_login));
                    return true;
                }

                fragment = new MyAccountFragment();
                break;

            case R.id.brands:
                fragment = new BrandsTab();
                break;

            case R.id.credits:
                if (currentFan == null) {
                    _utility.showMessage(getString(R.string.not_login));
                    return true;
                }
                fragment = new MyCreditsFragment();
                break;

//            case R.id.leaderboard:
//                fragment = new LeaderboardTab();
//                break;

            case R.id.friends:
                if (currentFan == null) {
                    _utility.showMessage(getString(R.string.not_login));
                    return true;
                }
                fragment = new MyFriendsFragment();
                break;

            case R.id.experience:
                if (currentFan == null) {
                    _utility.showMessage(getString(R.string.not_login));
                    return true;
                }
                fragment = new ExperienceTab();
                break;

            case R.id.settings:
                fragment = new SettingsFragment();
                break;

//            case R.id.contact_us:
//                fragment = new ContactUsTab();
//                break;

            default:
                break;
        }


        fragmentTransaction(fragment);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.e("TAG", "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {
        startLocationUpdates();

    }

    /**
     * Starting the location updates
     */
    protected void startLocationUpdates() {

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
    }

    @Override
    public void onStop() {
        Log.e("Main act.", "on stop");
        super.onStop();

//        accessTokenTracker.stopTracking();
//        profileTracker.stopTracking();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Creating google api client object
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        Log.e("build google api", "branches");
    }

    /**
     * Method to verify google play services on the device
     */
    public boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        Const.PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                _utility.showMessage(getResources().getString(R.string.not_supported), true);
            }
            return false;
        }
        return true;
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
//            Log.e(" **********", location.getLatitude() + "," + location.getLongitude());

            mLastLocation = location;
            latitudeSrc = location.getLatitude();
            longitudeSrc = location.getLongitude();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * check location setting to show change location dialog using play service
     */
    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);
    }

    /**
     * Uses a {@link LocationSettingsRequest.Builder} to build
     * a {@link LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        mLocationSettingsRequest = builder.build();
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        mLocationRequest.setInterval(Const.UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setFastestInterval(Const.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        Log.e("tag", "Location settings are not satisfied. Show the user a dialog to" +
                "upgrade location settings ");
        try {
            status.startResolutionForResult(this, Const.REQUEST_CHECK_SETTINGS);
        } catch (IntentSender.SendIntentException e) {
            Log.e("tag", "PendingIntent unable to execute request.");
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("mainActivity", "onActivityResult call request code:" + requestCode + " result code:" + resultCode);

        Log.e("main onActResult", "  >> " + onActivityResultType);
        if (data == null)
            return;

        switch (onActivityResultType) {
            case 1:
                callbackManagerAdapter.onActivityResult(requestCode, resultCode, data);

                if ("com.facebook.platform.action.request.LIKE_DIALOG".equals(data.getStringExtra("com.facebook.platform.protocol.PROTOCOL_ACTION"))) {
                    // get action results
                    Bundle bundle = data.getExtras().getBundle("com.facebook.platform.protocol.RESULT_ARGS");
                    if (bundle != null) {
                        bundle.getBoolean("object_is_liked"); // liked/unliked
                        Log.e("0fb_object_is_liked", "++ " + bundle.getBoolean("object_is_liked"));
                        bundle.getInt("didComplete");
                        Log.e("0fb_didComplete", "++ " + bundle.getInt("didComplete"));
                        bundle.getInt("like_count"); // object like count
                        Log.e("0fb_like_count", "++ " + bundle.getInt("like_count"));
                        bundle.getString("like_count_string");
                        Log.e("0fb_like_count_string", "++ " + bundle.getString("like_count_string"));
                        bundle.getString("social_sentence");
                        Log.e("0fb_social_sentence", "++ " + bundle.getString("social_sentence"));
                        bundle.getString("completionGesture"); // liked/cancel/unliked
                        Log.e("0fb_completionGesture", bundle.getString("completionGesture"));
                        Log.e("bundle kolo", bundle.toString());
                        if (bundle.getString("completionGesture").equals("like")) {
                            likeRequestActivity("like");
                        } else if (bundle.getString("completionGesture").equals("unlike")) {
                            likeRequestActivity("dislike");
                        }
                    }
                }
                break;
            case 2:
                try {
                    switch (requestCode) {
                        case 0x1://REQUEST_CHECK_SETTINGS
                            switch (resultCode) {
                                case Activity.RESULT_OK:
                                    if (MainActivity.latitudeSrc != 0.0) {
                                        if (_utility.isConnectingToInternet_ping()) {
                                            goToMapFragment();
                                        } else
                                            _utility.showMessage(getResources().getString(R.string.no_net));
                                    } else
                                        getLocation();

                                    break;
                                case Activity.RESULT_CANCELED:

                                    break;


                            }
                            break;

                        case 140: {
                            Log.e("main act act result", "twitter");
                            EditAccountFragment.twitterLoginButton.onActivityResult(requestCode, resultCode, data);

                            break;
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

        }
        onActivityResultType = 0;

    }

    /**
     * trying to get location or wait for it
     */
    public void getLocation() {
//        Log.e("mainAct", "0fn - getLocation");
        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            Log.e("mainAct", "0fn - getLocation not null");

            latitudeSrc = mLastLocation.getLatitude();
            longitudeSrc = mLastLocation.getLongitude();

        } else {
            locationTimeoutCtr = 0;
            handlerGPS.postDelayed(runnableGPS, 5000);
            pdLocation.show();
        }
    }

    void goToMapFragment() {
        _utility.showMessage(getString(R.string.plz_w8), true);
        if (_utility.isConnectingToInternet_ping()) {
            Fragment fragment = new FragmentMap();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else
            _utility.showMessage(getResources().getString(R.string.no_net));
    }

    private void likeRequestActivity(String event) {
        _utility.ShowDialog(getResources().getString(R.string.plz_w8), true);
        requestType = "likeRequestActivity";
        JSONWebServices service = new JSONWebServices(MainActivity.this);
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("type", event));
        nameValuePairs.add(new BasicNameValuePair("pageUrl", brandPageLiked));
        call = service.likeRequestActivity(nameValuePairs);
    }

    private void getCoupType(String slug) {
//        _utility.ShowDialog(getResources().getString(R.string.plz_w8), true);
        requestType = "getCoupType";
        JSONWebServices service = new JSONWebServices(MainActivity.this);
        call = service.getCoupTypeActivity(null, slug);
    }

    private void unregisterGCM(String IMEI) {
        _utility.ShowDialog(getResources().getString(R.string.plz_w8), true);
        requestType = "unregisterGCM";
        JSONWebServices service = new JSONWebServices(MainActivity.this);
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("IMEI", IMEI));
        service.unRegisterGCM(nameValuePairs);
    }

    void sendReferrerID(String referrer, String IMEI) {
        requestType = "sendReferrerID";
        JSONWebServices service = new JSONWebServices(MainActivity.this);
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("refid", referrer));
        nameValuePairs.add(new BasicNameValuePair("imei", IMEI));
        service.sendReferrerActivity(nameValuePairs);
    }

    @Override
    public void PostBackExecutionJSON(JSONTokener Result) {
        if (requestType.equals("cloudCampClickedReport"))
            return;
        if (requestType.equals("sendReferrerID")) {
            if (ParseData.parseActionsResult(Result)) {
                editor.putInt("referrer_status", 1);
            } else {
                editor.putInt("referrer_status", 0);
            }
            editor.commit();
        } else {
            try {
                _utility.HideDialog();

                switch (requestType) {

                    case "getCoupType":
                        if (ParseData.parseCouponType(Result, _utility)) {

                            fragment = new SingleCouponFragment();
                        } else {
                            fragment = new CouponTab();
                            _utility.showMessage(getResources().getString(R.string.ws_err));
                        }
                        fragmentTransaction(fragment);
                        break;

                    case "likeRequestActivity":
                        if (ParseData.parseActionsResult(Result)) {
                            _utility.showMessage(getResources().getString(R.string.success));
                            updateCoupons(CouponTab.coupIdTemp);
                        } else {
                            _utility.showMessage(getResources().getString(R.string.ws_err));
                        }
                        break;

                    case "unregisterGCM":
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            finishAffinity();
                        } else {
                            finish();
                        }
                        Intent i = new Intent(MainActivity.this, SplashScreen.class);
                        startActivity(i);
                        break;

                    default:
                        if (Result != null) {
                            Log.e("accessToken", "response");
                            Log.e("register response", Result.toString());
                            MainActivity.currentFan = ParseData.parseFan(Result, _utility);
                            if (MainActivity.currentFan == null) {
                                _utility.showMessage(getResources().getString(R.string.error));
                                return;
                            }
                            MainActivity.WS_ACCESSTOKEN = MainActivity.currentFan.getFanAccessToken();
                            MainActivity.editor.putString("accessToken", MainActivity.currentFan.getFanAccessToken());
                            Log.e("accessToken from resp", MainActivity.currentFan.getFanAccessToken());
                            MainActivity.editor.putLong("accessTokenCreation", System.currentTimeMillis() / 1000);
                            MainActivity.editor.commit();
                            setUserDataNavDrawer();

                            fragment = new CouponTab();
                            logout.setText(getResources().getString(R.string.logout));
                            fragmentTransaction(fragment);
                            if (_utility.isConnectingToInternet())
                                registerToGCM();
                        } else
                            _utility.showMessage(getResources().getString(R.string.ws_err));
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                _utility.showMessage(getResources().getString(R.string.ws_err));
            } finally {
                _utility.HideDialog();
                call = null;
            }
        }
    }

    void updateCoupons(int coupID) {
        for (int i = 0; i < CouponTab.allCoupons2.size(); i++) {
            for (int j = 0; j < CouponTab.allCoupons2.get(i).size(); j++) {
                if (CouponTab.allCoupons2.get(i).get(j).getCouponID() == coupID) {
                    if (getAllCouponsRefreshListener() != null) {
                        getAllCouponsRefreshListener().onRefresh();
                    }
//                    return;
                }
            }
        }

        for (int i = 0; i < CouponTab.forYou2.size(); i++) {
            for (int j = 0; j < CouponTab.forYou2.get(i).size(); j++) {
                if (CouponTab.forYou2.get(i).get(j).getCouponID() == coupID) {
                    if (getAllCouponsRefreshListener() != null) {
                        getAllCouponsRefreshListener().onRefresh();
                    }
//                    return;
                }
            }
        }

    }

    public void setLocale(String language) {
        Log.e("0 - MainActivity", "0fn - setLocale");
        myLocale = new Locale(language);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    public interface FragmentRefreshListener {
        void onRefresh();
    }

}