package com.systemonline.fanscoupon;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.systemonline.fanscoupon.Helpers.Const;
import com.systemonline.fanscoupon.Helpers.Utility;
import com.systemonline.fanscoupon.Interfaces.IServiceCallBack;
import com.systemonline.fanscoupon.Model.CampaignLocation;
import com.systemonline.fanscoupon.Model.LBC;
import com.systemonline.fanscoupon.WebServices.JSONAsync;
import com.systemonline.fanscoupon.WebServices.JSONWebServices;
import com.systemonline.fanscoupon.WebServices.ParseData;
import com.systemonline.fanscoupon.db.DatabaseHandler;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

//import com.systemonline.gm3yat.db.DatabaseHandler;

public class CampaignLocationsService extends Service implements IServiceCallBack {

    public static final String BROADCAST_ACTION = "notify test";
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    public LocationManager locationManager;
    public MyLocationListener listener;
    public Location previousBestLocation = null, last_loc;
    Intent intent;
    Utility utility;
    DatabaseHandler db;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    MediaPlayer mp;
    private int uniNum;
    private double acceptableMove = 15.0, totalDistanceSum = 0.0, currentUserMove = 0.0,
            updateDistance = 7000.0,//////////////////////////////////
            GPSError = 5.0;
    private JSONAsync call;
    private volatile LBC currentLBC, updatedLBC;
    private String type;
    private String accessToken;
    private SimpleDateFormat simpleDateFormat;
    private Date date;
    private boolean checkCampaignsFlag;

    @Override
    public void onCreate() {
        super.onCreate();

        //Log.e("0 LocationService", "0fn - onCreate");

        mp = MediaPlayer.create(this, R.raw.sounds);
        db = new DatabaseHandler(getApplicationContext());
        intent = new Intent(BROADCAST_ACTION);
        uniNum = 0;

    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.e("0 LocationService", "0fn - onStartCommand");
        try {
            sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
            accessToken = sharedPreferences.getString("accessToken", "no value");

//            acceptableMove = sharedPreferences.getInt("notify_distance_value", 100);
//        acceptableMove = sharedPreferences.getInt("notify_distance_value", 5);

            ////Log.e("n0tify distance ", acceptableMove + "");

//            radius = marginError + acceptableMove + (0.5 * (marginError + acceptableMove));
            ////Log.e("radius0", radius + "");

            last_loc = null;
            checkCampaignsFlag = true;
            updateDistance = sharedPreferences.getFloat("camp_update_interval", (float) 7000.0);
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            listener = new MyLocationListener();
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_perm_loc), Toast.LENGTH_SHORT).show();

            } else {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Service.START_STICKY;
    }

    private void getAllLBC() {
        ArrayList<String> campData = db.getLBCs();
        String[] splitter;
        currentLBC = new LBC();
        ArrayList<CampaignLocation> userCampaignLocations = new ArrayList<>();
        CampaignLocation campaignLocation;
        for (int i = 0; i < campData.size(); i++) {
            splitter = campData.get(i).split(",");
            campaignLocation = new CampaignLocation();
            campaignLocation.setBranchId(Integer.valueOf(splitter[0]));
            campaignLocation.setCampaignID(Integer.valueOf(splitter[1]));
            campaignLocation.setCampaignLong(Double.valueOf(splitter[2]));
            campaignLocation.setCampaignLat(Double.valueOf(splitter[3]));
            campaignLocation.setBranchNotifyRange(Integer.valueOf(splitter[4]));
            campaignLocation.setCampaignCouponId(Integer.valueOf(splitter[5]));
            campaignLocation.setCampaignCouponSlug(splitter[6]);
            campaignLocation.setCampaignCouponType(splitter[7]);
            campaignLocation.setCampaignCouponImg(splitter[8]);
            campaignLocation.setCampaignEndDate(splitter[9]);
            campaignLocation.setCampaignName(splitter[10]);
            campaignLocation.setCampaignTitle(splitter[11]);
            campaignLocation.setCampaignBody(splitter[12]);
            campaignLocation.setFrequency(Integer.valueOf(splitter[13]));
            campaignLocation.setFrequencyLimit(Integer.valueOf(splitter[14]));
            campaignLocation.setCampaignDeliveryTime(splitter[15]);
            campaignLocation.setTimeDifference(Integer.valueOf(splitter[16]));
            userCampaignLocations.add(campaignLocation);
        }
        currentLBC.setCampaignLocations(userCampaignLocations);
    }

    /**
     * a synchronous method to check nearby branches list
     *
     * @param loc current user location
     */
    synchronized void checkNearbyBranchesList(Location loc) {
//        //Log.e("0 - LocationService", "0fn - checkNearbyBranchesList");
//        //Log.e("0 - checkCampaignsFlag", "++++" + checkCampaignsFlag);
//        //Log.e("0 - currentLBC null", "++++" + (currentLBC == null));
        if (!checkCampaignsFlag || currentLBC == null)
            return;
        try {
            ////Log.e("notification list", "notify");
            for (int i = 0; i < currentLBC.getCampaignLocations().size(); i++) {
                //Log.e("range ", (float) currentLBC.getCampaignLocations().get(i).getBranchNotifyRange() + "---");
//                //Log.e("freq_limit", currentLBC.getCampaignLocations().get(i).getFrequencyLimit() + "---");
//                isValidCampTimeDifference(currentLBC.getCampaignLocations().get(i));

                if ((currentLBC.getCampaignLocations().get(i).getFrequency() < currentLBC.getCampaignLocations().get(i).getFrequencyLimit())
                        && isValidCampTimeDifference(currentLBC.getCampaignLocations().get(i)))
//                {
                    if (calculateDistanceLocally(loc.getLatitude(), loc.getLongitude(),
                            currentLBC.getCampaignLocations().get(i).getCampaignLat(), currentLBC.getCampaignLocations().get(i).getCampaignLong()) <=
                            (float) currentLBC.getCampaignLocations().get(i).getBranchNotifyRange()) {
                        checkCampaignsFlag = false;
                        showNotification(currentLBC.getCampaignLocations().get(i));
                    }
//                } else {
//                    currentLBC.getCampaignLocations().remove(i);
//                    //Log.e("campaign", currentLBC.getCampaignLocations().get(i).getCampaignName() + "-- removed -");
//                    break;
//                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isValidCampTimeDifference(CampaignLocation campaignLocation) throws ParseException {
        //Log.e("difference DeliveryTime", " --- " + campaignLocation.getCampaignDeliveryTime());

        if (campaignLocation.getCampaignDeliveryTime() == null || campaignLocation.getCampaignDeliveryTime().equals("null"))
            return true;

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        date = simpleDateFormat.parse("2017-07-08 01:00:00");
        date = simpleDateFormat.parse(campaignLocation.getCampaignDeliveryTime());


        //Log.e("difference time unit", " --- " + TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - date.getTime()));

        return TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - date.getTime()) >= campaignLocation.getTimeDifference();
    }

    /**
     * check if current location is better or the last one.
     *
     * @param location
     * @param currentBestLocation
     * @return true is better, else return false
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
//        //Log.e("0 - LocationService", "0fn - isBetterLocation");

        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        //Log.e("0 - LocationService", "0fn - isSameProvider");

        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    /**
     * show notification for a promotion
     *
     * @param campaignLocation
     */
    synchronized void showNotification(CampaignLocation campaignLocation) {
        //Log.e("0 - LocationService", "0fn - showNotification campID " + campaignLocation.getCampaignID() +
//                " brID" + campaignLocation.getBranchId());

        new sendNotification(this)
                .execute(String.valueOf(campaignLocation.getCampaignID()),
                        campaignLocation.getCampaignTitle(), campaignLocation.getCampaignBody(),
                        Const.imagesURL + "coupons/320x172/" + campaignLocation.getCampaignCouponImg(),
                        campaignLocation.getCampaignCouponSlug(), campaignLocation.getCampaignCouponType());
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        Notification notification;
//
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.putExtra("coupID", campaignLocation.getCampaignCouponId());
//        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
//
//        Bitmap fansCouponIcon = ((BitmapDrawable) getResources().getDrawable(R.drawable.fans_coupons)).getBitmap();
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
//            notification = new Notification.Builder(this)
//                    .setContentTitle(campaignLocation.getCampaignTitle())
//                    .setContentText(campaignLocation.getCampaignBody())
//                    .setSmallIcon(R.drawable.map_pin)
//                    .setLargeIcon(fansCouponIcon)
//                    .setAutoCancel(true)
//                    .setStyle(new Notification.BigPictureStyle()
//                            .bigPicture(getBitmapFromURL(Const.imagesURL + "coupons/320x172/" + campaignLocation.getCampaignCouponImg()))
//                            .setBigContentTitle("big title"))
//                    .build();
////                        notificationManager.notify(0, notif);
//
//        } else {
//            notification = new Notification.Builder(this)
//                    .setContentTitle(campaignLocation.getCampaignTitle())
//                    .setContentText(campaignLocation.getCampaignBody())
//                    .setSmallIcon(R.drawable.map_pin)
//                    .setContentIntent(pIntent)
//                    .setAutoCancel(true)
//                    .getNotification();
//        }
//        notificationManager.notify(++uniNum, notification);
//        notificationManager = null;
//        notification = null;
//        pIntent = null;
        //////////////////////////////////////////////////////////////////////////////////////////////////

//        mp.start();
//
//        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        // prepare intent which is triggered if the
//// notification is selected
//////Log.e("0n0tificati0n" , "sh0w");
//        Intent intent = new Intent(this, MainActivity.class);
//
//        intent.putExtra("offname", offName);
//        intent.putExtra("loc_lat", last_loc.getLatitude());
//        intent.putExtra("loc_lng", last_loc.getLongitude());
//
//        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
//
//
//// build notification
//        String[] splitter = place.split(" in ");
//        Notification n;
//
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
//            n = new Notification.Builder(this)
//                    .setContentTitle(splitter[1])
//                    .setContentText(splitter[0])
//                    .setSmallIcon(R.drawable.logo)
//                    .setContentIntent(pIntent)
//                    .setAutoCancel(true)
//                    .setStyle(new Notification.BigTextStyle().bigText(place))
//                    .build();
//        } else {
////            n = new Notification.Builder(this)
////                    .setContentTitle(splitter[1])
////                    .setContentText(splitter[0])
////                    .setSmallIcon(R.drawable.mark_gm3yat)
////                    .setContentIntent(pIntent)
////                    .setAutoCancel(true)
////                    .build();
//
//            n = new Notification.Builder(this)
//                    .setContentTitle(splitter[1])
//                    .setContentText(splitter[0])
//                    .setSmallIcon(R.drawable.logo)
//                    .setContentIntent(pIntent)
//                    .setAutoCancel(true)
//                    .getNotification();
//        }
//
//        notificationManager.notify(++uniNum, n);
    }

    Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    private void deliveryUpdate(int campID) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String Date = sdf.format(new Date());
        if (db.notificationDeliveryUpdate(campID, Date)) {
            for (int k = 0; k < currentLBC.getCampaignLocations().size(); k++) {
                if (currentLBC.getCampaignLocations().get(k).getCampaignID() == campID) {
                    currentLBC.getCampaignLocations().get(k).setFrequency(currentLBC.getCampaignLocations().get(k).getFrequency() + 1);
                    currentLBC.getCampaignLocations().get(k).setCampaignDeliveryTime(Date);
                }
            }
            cloudCampDeliveryReport(campID, last_loc.getLatitude(), last_loc.getLongitude());
        }
        sdf = null;
        Date = null;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        //Log.e("0 - LocationService", "0fn - onDestroy");

//        Toast.makeText(LocationService.this, "service stopped", Toast.LENGTH_LONG).show();
        ////Log.e("ST0P_SERVICE", "DONE");
        try {
            locationManager.removeUpdates(listener);
        } catch (Exception e) {

        }
    }

    float calculateDistanceLocally(double lat1, double lng1, double lat2, double lng2) {
        float[] result = new float[10];
        Location.distanceBetween(lat1, lng1, lat2, lng2, result);
        //Log.e("distance", lat1 + "---" + lng1 + "---" + lat2 + "---" + lng2 + "---" + result[0]);
        return result[0];
    }

    void getUserCampaigns(String requestType, double latitude, double longitude) {
        if (sharedPreferences.getInt("admin_id", -1) == -1)
            return;
        //Log.e("reqType", requestType);
        type = "getUserCampaigns";
        JSONWebServices service = new JSONWebServices(CampaignLocationsService.this);
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("type", requestType));
        nameValuePairs.add(new BasicNameValuePair("lat", String.valueOf(latitude)));
        nameValuePairs.add(new BasicNameValuePair("lon", String.valueOf(longitude)));
        call = service.getUserCampaigns(nameValuePairs, accessToken);
    }

    void cloudCampDeliveryReport(int campID, double latitude, double longitude) {
        type = "cloudCampDeliveryReport";
        JSONWebServices service = new JSONWebServices(CampaignLocationsService.this);
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("campaign_id", String.valueOf(campID)));
        nameValuePairs.add(new BasicNameValuePair("lat", String.valueOf(latitude)));
        nameValuePairs.add(new BasicNameValuePair("lon", String.valueOf(longitude)));
        call = service.cloudCampDeliveryReport(nameValuePairs, accessToken);
    }

    @Override
    public void PostBackExecutionJSON(JSONTokener Result) {
        try {
            if (type.equals("getUserCampaigns")) {
                updatedLBC = ParseData.parseGetUserCampaigns(Result);
                if (updatedLBC == null)
                    return;

                updateDistance = updatedLBC.getCampaignsUpdateInterval();
                editor.putFloat("camp_update_interval", (float) updateDistance);
                //Log.e("camp_update_interval", updateDistance + "---");

                //Log.e("updatedLBC", " - empty- " + updatedLBC.getCampaignLocations().isEmpty());
                editor.putLong("camp_last_update", System.currentTimeMillis());
                if (!updatedLBC.getCampaignLocations().isEmpty()) {
                    checkCampaignsFlag = false;
                    currentLBC = new LBC();
                    currentLBC.setCampaignLocations(
                            (ArrayList<CampaignLocation>) updatedLBC.getCampaignLocations().clone());
                    updatedLBC = null;
                    checkCampaignsFlag = true;
//                    for (int i = 0; i < currentLBC.getCampaignLocations().size(); i++) {
                    //Log.e(currentLBC.getCampaignLocations().get(i).getCampaignName(),
//                                currentLBC.getCampaignLocations().get(i).getCampaignLat() + " -- " + currentLBC.getCampaignLocations().get(i).getCampaignLong()
//                                        + " -- " + currentLBC.getCampaignLocations().get(i).getBranchNotifyRange());
//                    }
                    if (db.insertCampLocations(currentLBC.getCampaignLocations())) {
                        //Log.e("camp db insertion", "true");
                        Calendar c = Calendar.getInstance();
//                        //Log.e("Current time => ", c.getTime() + "--");
//
//                        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
//                        String formattedDate = df.format(c.getTime());
//                        //Log.e("Current time => ", formattedDate);
                        editor.putLong("camp_last_update", System.currentTimeMillis());
                    } else
                        editor.putLong("camp_last_update", -1);

                }
                editor.commit();

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            call = null;
        }
    }

    /**
     * create a location listener class
     */
    public class MyLocationListener implements LocationListener {

        public void onLocationChanged(final Location loc) {
            try {
                //Log.e(" *****s*****", loc.getLatitude() + "," + loc.getLongitude()
//                        + " -stop notif- " + sharedPreferences.getBoolean("stop_mobile_notification", false));
                if (sharedPreferences.getBoolean("stop_mobile_notification", false))
                    return;
                if (last_loc != null) {
                    float distance = calculateDistanceLocally(last_loc.getLatitude(), last_loc.getLongitude(),
                            loc.getLatitude(), loc.getLongitude());
                    if (distance >= GPSError) {// hena n3ml check 3la el campaigns elly 3andy kol ma ymshy ad eh??
                        currentUserMove += distance;
                        if (currentUserMove >= acceptableMove) {
                            checkNearbyBranchesList(loc);
                            currentUserMove = 0.0;
                        }
                        last_loc = loc;
                    }
                    totalDistanceSum += distance;
                    if (totalDistanceSum >= updateDistance) {
                        totalDistanceSum = 0.0;
                        //API call to update campaigns
                        getUserCampaigns("all", loc.getLatitude(), loc.getLongitude());
                    } else if (System.currentTimeMillis() - sharedPreferences.getLong("camp_last_update", 1000 * 3600) >= (1000 * 86400))//24 Hours
//                    } else if (System.currentTimeMillis() - sharedPreferences.getLong("camp_last_update", 1000 * 3600) >= (1000 * 120))
                        getUserCampaigns("update", loc.getLatitude(), loc.getLongitude());

                } else {
                    last_loc = loc;
                    //API call to update campaigns (first time) OR get it locally
                    if (sharedPreferences.getLong("camp_last_update", -1) == -1) {
                        getUserCampaigns("all", loc.getLatitude(), loc.getLongitude());
//                    } else if (System.currentTimeMillis() - sharedPreferences.getLong("camp_last_update", -1) >= (1000 * 120)) {
                    } else if (TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - sharedPreferences.getLong("camp_last_update", -1)) >= 24) {
                        getUserCampaigns("update", loc.getLatitude(), loc.getLongitude());
                    } else {
                        //Log.e("camp database", "exist");
                        getAllLBC();
//                        for (int i = 0; i < currentLBC.getCampaignLocations().size(); i++) {
//                            //Log.e(currentLBC.getCampaignLocations().get(i).getCampaignName(), currentLBC.getCampaignLocations().get(i).getBranchId() + " -- " + currentLBC.getCampaignLocations().get(i).getBranchNotifyRange());
//                        }
                    }

                }


                if (isBetterLocation(loc, previousBestLocation)) {
                    loc.getLatitude();
                    loc.getLongitude();
                    intent.putExtra("Latitude", loc.getLatitude());
                    intent.putExtra("Longitude", loc.getLongitude());
                    intent.putExtra("Provider", loc.getProvider());
                    sendBroadcast(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
//            Toast.makeText(getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
//            Toast.makeText(getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

    }

    private class sendNotification extends AsyncTask<String, Void, Bitmap> {

        Context ctx;
        String campID, campTitle, campBody, couponImg, couponSlug, couponType;

        sendNotification(Context context) {
            super();
            this.ctx = context;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            InputStream in;
            campID = params[0];
            campTitle = params[1];
            campBody = params[2];
//            coupID = params[3];
            couponImg = params[3];
            couponSlug = params[4];
            couponType = params[5];
            try {

                URL url = new URL(couponImg);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                in = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(in);
                return myBitmap;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {

            super.onPostExecute(result);
            try {

                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                Notification notification;

                Intent intent = new Intent(ctx, MainActivity.class);
                intent.putExtra("campaignID", campID);
                intent.putExtra("coupSlug", couponSlug);
                intent.putExtra("coupType", couponType);
                PendingIntent pIntent = PendingIntent.getActivity(ctx, (int) System.currentTimeMillis(), intent, 0);

                Bitmap fansCouponIcon = ((BitmapDrawable) getResources().getDrawable(R.drawable.fans_coupons)).getBitmap();
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    notification = new Notification.Builder(ctx)
                            .setContentTitle(campTitle)
                            .setContentText(campBody)
                            .setSmallIcon(R.drawable.map_pin)
                            .setLargeIcon(fansCouponIcon)
                            .setContentIntent(pIntent)
                            .setAutoCancel(true)
                            .setStyle(new Notification.BigPictureStyle()
                                    .bigPicture(result)
                                    .setBigContentTitle(campTitle))
                            .build();
//                        notificationManager.notify(0, notif);

                } else {
                    notification = new Notification.Builder(ctx)
                            .setContentTitle(campTitle)
                            .setContentText(campBody)
                            .setSmallIcon(R.drawable.fans_coupons)
                            .setContentIntent(pIntent)
                            .setAutoCancel(true)
                            .getNotification();
                }
                notificationManager.notify(++uniNum, notification);
                notificationManager = null;
                notification = null;
                pIntent = null;
                deliveryUpdate(Integer.valueOf(campID));
                checkCampaignsFlag = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


//    FOT TESTING ONLY
//    package com.systemonline.fanscoupon;
//
//import android.app.Notification;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.drawable.BitmapDrawable;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.media.MediaPlayer;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.os.IBinder;
//import android.support.v4.app.ActivityCompat;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.systemonline.fanscoupon.Helpers.Const;
//import com.systemonline.fanscoupon.Helpers.Utility;
//import com.systemonline.fanscoupon.Interfaces.IServiceCallBack;
//import com.systemonline.fanscoupon.Model.CampaignLocation;
//import com.systemonline.fanscoupon.Model.LBC;
//import com.systemonline.fanscoupon.WebServices.JSONAsync;
//import com.systemonline.fanscoupon.WebServices.JSONWebServices;
//import com.systemonline.fanscoupon.WebServices.ParseData;
//import com.systemonline.fanscoupon.db.DatabaseHandler;
//
//import org.apache.http.NameValuePair;
//import org.apache.http.message.BasicNameValuePair;
//import org.json.JSONTokener;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
////import com.systemonline.gm3yat.db.DatabaseHandler;
//
//    public class CampaignLocationsService extends Service implements IServiceCallBack {
//
//        public static final String BROADCAST_ACTION = "notify test";
//        private static final int TWO_MINUTES = 1000 * 60 * 2;
//        public LocationManager locationManager;
//        public MyLocationListener listener;
//        public Location previousBestLocation = null, last_loc;
//        Intent intent;
//        Utility utility;
//        DatabaseHandler db;
//        SharedPreferences sharedPreferences;
//        SharedPreferences.Editor editor;
//        MediaPlayer mp;
//        private int uniNum;
//        //    private double acceptableMove = 15.0, totalDistanceSum = 0.0, currentUserMove = 0.0,
////            updateDistance = 7000.0,//////////////////////////////////
////            GPSError = 5.0;
//        private double acceptableMove = 5.0, totalDistanceSum = 0.0, currentUserMove = 0.0,
//                updateDistance = 20.0,//////////////////////////////////
//                GPSError = 2.0;
//        private JSONAsync call;
//        private volatile LBC currentLBC, updatedLBC;
//        private String type;
//        private String accessToken;
//        private SimpleDateFormat simpleDateFormat;
//        private Date date;
//        private boolean checkCampaignsFlag;
//
//        @Override
//        public void onCreate() {
//            super.onCreate();
//
//            //Log.e("0 LocationService", "0fn - onCreate");
//
//            mp = MediaPlayer.create(this, R.raw.sounds);
//            db = new DatabaseHandler(getApplicationContext());
//            intent = new Intent(BROADCAST_ACTION);
//            uniNum = 0;
//
//        }
//
//
//        @Override
//        public IBinder onBind(Intent intent) {
//            return null;
//        }
//
//        @Override
//        public int onStartCommand(Intent intent, int flags, int startId) {
//            //Log.e("0 LocationService", "0fn - onStartCommand");
//            try {
//                sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
//                editor = sharedPreferences.edit();
//                accessToken = sharedPreferences.getString("accessToken", "no value");
//
////            acceptableMove = sharedPreferences.getInt("notify_distance_value", 100);
////        acceptableMove = sharedPreferences.getInt("notify_distance_value", 5);
//
//                ////Log.e("n0tify distance ", acceptableMove + "");
//
////            radius = marginError + acceptableMove + (0.5 * (marginError + acceptableMove));
//                ////Log.e("radius0", radius + "");
//
//                last_loc = null;
//                checkCampaignsFlag = true;
//                updateDistance = sharedPreferences.getFloat("camp_update_interval", (float) 7000.0);
//                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//                listener = new MyLocationListener();
//                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_perm_loc), Toast.LENGTH_SHORT).show();
//
//                } else {
//                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
//                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return Service.START_STICKY;
//        }
//
//        private void getAllLBC() {
//            ArrayList<String> campData = db.getLBCs();
//            String[] splitter;
//            currentLBC = new LBC();
//            ArrayList<CampaignLocation> userCampaignLocations = new ArrayList<>();
//            CampaignLocation campaignLocation;
//            for (int i = 0; i < campData.size(); i++) {
//                splitter = campData.get(i).split(",");
//                campaignLocation = new CampaignLocation();
//                campaignLocation.setBranchId(Integer.valueOf(splitter[0]));
//                campaignLocation.setCampaignID(Integer.valueOf(splitter[1]));
//                campaignLocation.setCampaignLong(Double.valueOf(splitter[2]));
//                campaignLocation.setCampaignLat(Double.valueOf(splitter[3]));
//                campaignLocation.setBranchNotifyRange(Integer.valueOf(splitter[4]));
//                campaignLocation.setCampaignCouponId(Integer.valueOf(splitter[5]));
//                campaignLocation.setCampaignCouponSlug(splitter[6]);
//                campaignLocation.setCampaignCouponType(splitter[7]);
//                campaignLocation.setCampaignCouponImg(splitter[8]);
//                campaignLocation.setCampaignEndDate(splitter[9]);
//                campaignLocation.setCampaignName(splitter[10]);
//                campaignLocation.setCampaignTitle(splitter[11]);
//                campaignLocation.setCampaignBody(splitter[12]);
//                campaignLocation.setFrequency(Integer.valueOf(splitter[13]));
//                campaignLocation.setFrequencyLimit(Integer.valueOf(splitter[14]));
//                campaignLocation.setCampaignDeliveryTime(splitter[15]);
//                campaignLocation.setTimeDifference(Integer.valueOf(splitter[16]));
//                userCampaignLocations.add(campaignLocation);
//            }
//            currentLBC.setCampaignLocations(userCampaignLocations);
//        }
//
//        /**
//         * a synchronous method to check nearby branches list
//         *
//         * @param loc current user location
//         */
//        synchronized void checkNearbyBranchesList(Location loc) {
//            Log.e("0 - LocationService", "0fn - checkNearbyBranchesList");
//            Log.e("0 - checkCampaignsFlag", "++++" + checkCampaignsFlag);
//            Log.e("0 - currentLBC null", "++++" + (currentLBC == null));
//            if (!checkCampaignsFlag || currentLBC == null)
//                return;
//            try {
//                Log.e("notification list", "notify");
//                for (int i = 0; i < currentLBC.getCampaignLocations().size(); i++) {
//                    Log.e("range ", (float) currentLBC.getCampaignLocations().get(i).getBranchNotifyRange() + "---");
//                    Log.e("freq_limit", currentLBC.getCampaignLocations().get(i).getFrequencyLimit() + "---");
////                isValidCampTimeDifference(currentLBC.getCampaignLocations().get(i));
//
//                    if ((currentLBC.getCampaignLocations().get(i).getFrequency() < currentLBC.getCampaignLocations().get(i).getFrequencyLimit())
//                            && isValidCampTimeDifference(currentLBC.getCampaignLocations().get(i)))
////                {
//                        if (calculateDistanceLocally(loc.getLatitude(), loc.getLongitude(),
//                                currentLBC.getCampaignLocations().get(i).getCampaignLat(), currentLBC.getCampaignLocations().get(i).getCampaignLong()) <=
//                                (float) currentLBC.getCampaignLocations().get(i).getBranchNotifyRange()) {
//                            checkCampaignsFlag = false;
//                            showNotification(currentLBC.getCampaignLocations().get(i));
//                        }
////                } else {
////                    currentLBC.getCampaignLocations().remove(i);
////                    //Log.e("campaign", currentLBC.getCampaignLocations().get(i).getCampaignName() + "-- removed -");
////                    break;
////                }
//
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        private boolean isValidCampTimeDifference(CampaignLocation campaignLocation) throws ParseException {
//            Log.e("difference DeliveryTime", " --- " + campaignLocation.getCampaignDeliveryTime());
//
//            if (campaignLocation.getCampaignDeliveryTime() == null || campaignLocation.getCampaignDeliveryTime().equals("null"))
//                return true;
//
//            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
////        date = simpleDateFormat.parse("2017-07-08 01:00:00");
//            date = simpleDateFormat.parse(campaignLocation.getCampaignDeliveryTime());
//
//
//            Log.e("difference time unit", " --- " + TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - date.getTime()));
//
//            return true;
////                TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - date.getTime()) >= campaignLocation.getTimeDifference();
//        }
//
//        /**
//         * check if current location is better or the last one.
//         *
//         * @param location
//         * @param currentBestLocation
//         * @return true is better, else return false
//         */
//        protected boolean isBetterLocation(Location location, Location currentBestLocation) {
////        //Log.e("0 - LocationService", "0fn - isBetterLocation");
//
//            if (currentBestLocation == null) {
//                // A new location is always better than no location
//                return true;
//            }
//
//            // Check whether the new location fix is newer or older
//            long timeDelta = location.getTime() - currentBestLocation.getTime();
//            boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
//            boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
//            boolean isNewer = timeDelta > 0;
//
//            // If it's been more than two minutes since the current location, use the new location
//            // because the user has likely moved
//            if (isSignificantlyNewer) {
//                return true;
//                // If the new location is more than two minutes older, it must be worse
//            } else if (isSignificantlyOlder) {
//                return false;
//            }
//
//            // Check whether the new location fix is more or less accurate
//            int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
//            boolean isLessAccurate = accuracyDelta > 0;
//            boolean isMoreAccurate = accuracyDelta < 0;
//            boolean isSignificantlyLessAccurate = accuracyDelta > 200;
//
//            // Check if the old and new location are from the same provider
//            boolean isFromSameProvider = isSameProvider(location.getProvider(),
//                    currentBestLocation.getProvider());
//
//            // Determine location quality using a combination of timeliness and accuracy
//            if (isMoreAccurate) {
//                return true;
//            } else if (isNewer && !isLessAccurate) {
//                return true;
//            } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
//                return true;
//            }
//            return false;
//        }
//
//        /**
//         * Checks whether two providers are the same
//         */
//        private boolean isSameProvider(String provider1, String provider2) {
//            //Log.e("0 - LocationService", "0fn - isSameProvider");
//
//            if (provider1 == null) {
//                return provider2 == null;
//            }
//            return provider1.equals(provider2);
//        }
//
//        /**
//         * show notification for a promotion
//         *
//         * @param campaignLocation
//         */
//        synchronized void showNotification(CampaignLocation campaignLocation) {
//            Log.e("0 - LocationService", "0fn - showNotification campID " + campaignLocation.getCampaignID() +
//                    " brID" + campaignLocation.getBranchId());
//
//            new sendNotification(this)
//                    .execute(String.valueOf(campaignLocation.getCampaignID()),
//                            campaignLocation.getCampaignTitle(), campaignLocation.getCampaignBody(),
//                            Const.imagesURL + "coupons/320x172/" + campaignLocation.getCampaignCouponImg(),
//                            campaignLocation.getCampaignCouponSlug(), campaignLocation.getCampaignCouponType());
////
////        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
////        Notification notification;
////
////        Intent intent = new Intent(this, MainActivity.class);
////        intent.putExtra("coupID", campaignLocation.getCampaignCouponId());
////        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
////
////        Bitmap fansCouponIcon = ((BitmapDrawable) getResources().getDrawable(R.drawable.fans_coupons)).getBitmap();
////        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
////            notification = new Notification.Builder(this)
////                    .setContentTitle(campaignLocation.getCampaignTitle())
////                    .setContentText(campaignLocation.getCampaignBody())
////                    .setSmallIcon(R.drawable.map_pin)
////                    .setLargeIcon(fansCouponIcon)
////                    .setAutoCancel(true)
////                    .setStyle(new Notification.BigPictureStyle()
////                            .bigPicture(getBitmapFromURL(Const.imagesURL + "coupons/320x172/" + campaignLocation.getCampaignCouponImg()))
////                            .setBigContentTitle("big title"))
////                    .build();
//////                        notificationManager.notify(0, notif);
////
////        } else {
////            notification = new Notification.Builder(this)
////                    .setContentTitle(campaignLocation.getCampaignTitle())
////                    .setContentText(campaignLocation.getCampaignBody())
////                    .setSmallIcon(R.drawable.map_pin)
////                    .setContentIntent(pIntent)
////                    .setAutoCancel(true)
////                    .getNotification();
////        }
////        notificationManager.notify(++uniNum, notification);
////        notificationManager = null;
////        notification = null;
////        pIntent = null;
//            //////////////////////////////////////////////////////////////////////////////////////////////////
//
////        mp.start();
////
////        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
////        // prepare intent which is triggered if the
////// notification is selected
////////Log.e("0n0tificati0n" , "sh0w");
////        Intent intent = new Intent(this, MainActivity.class);
////
////        intent.putExtra("offname", offName);
////        intent.putExtra("loc_lat", last_loc.getLatitude());
////        intent.putExtra("loc_lng", last_loc.getLongitude());
////
////        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
////
////
////// build notification
////        String[] splitter = place.split(" in ");
////        Notification n;
////
////        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
////            n = new Notification.Builder(this)
////                    .setContentTitle(splitter[1])
////                    .setContentText(splitter[0])
////                    .setSmallIcon(R.drawable.logo)
////                    .setContentIntent(pIntent)
////                    .setAutoCancel(true)
////                    .setStyle(new Notification.BigTextStyle().bigText(place))
////                    .build();
////        } else {
//////            n = new Notification.Builder(this)
//////                    .setContentTitle(splitter[1])
//////                    .setContentText(splitter[0])
//////                    .setSmallIcon(R.drawable.mark_gm3yat)
//////                    .setContentIntent(pIntent)
//////                    .setAutoCancel(true)
//////                    .build();
////
////            n = new Notification.Builder(this)
////                    .setContentTitle(splitter[1])
////                    .setContentText(splitter[0])
////                    .setSmallIcon(R.drawable.logo)
////                    .setContentIntent(pIntent)
////                    .setAutoCancel(true)
////                    .getNotification();
////        }
////
////        notificationManager.notify(++uniNum, n);
//        }
//
//        Bitmap getBitmapFromURL(String src) {
//            try {
//                URL url = new URL(src);
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                connection.setDoInput(true);
//                connection.connect();
//                InputStream input = connection.getInputStream();
//                Bitmap myBitmap = BitmapFactory.decodeStream(input);
//                return myBitmap;
//            } catch (IOException e) {
//                // Log exception
//                return null;
//            }
//        }
//
//        private void deliveryUpdate(int campID) {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            String Date = sdf.format(new Date());
//            if (db.notificationDeliveryUpdate(campID, Date)) {
//                for (int k = 0; k < currentLBC.getCampaignLocations().size(); k++) {
//                    if (currentLBC.getCampaignLocations().get(k).getCampaignID() == campID) {
//                        currentLBC.getCampaignLocations().get(k).setFrequency(currentLBC.getCampaignLocations().get(k).getFrequency() + 1);
//                        currentLBC.getCampaignLocations().get(k).setCampaignDeliveryTime(Date);
//                    }
//                }
//                cloudCampDeliveryReport(campID, last_loc.getLatitude(), last_loc.getLongitude());
//            }
//            sdf = null;
//            Date = null;
//        }
//
//        @Override
//        public void onDestroy() {
//
//            super.onDestroy();
//            //Log.e("0 - LocationService", "0fn - onDestroy");
//
////        Toast.makeText(LocationService.this, "service stopped", Toast.LENGTH_LONG).show();
//            ////Log.e("ST0P_SERVICE", "DONE");
//            try {
//                locationManager.removeUpdates(listener);
//            } catch (Exception e) {
//
//            }
//        }
//
//        float calculateDistanceLocally(double lat1, double lng1, double lat2, double lng2) {
//            float[] result = new float[10];
//            Location.distanceBetween(lat1, lng1, lat2, lng2, result);
//            Log.e("distance", lat1 + "---" + lng1 + "---" + lat2 + "---" + lng2 + "---" + result[0]);
//            return result[0];
//        }
//
//        void getUserCampaigns(String requestType, double latitude, double longitude) {
//            if (sharedPreferences.getInt("admin_id", -1) == -1)
//                return;
//            Log.e("reqType", requestType);
//            type = "getUserCampaigns";
//            JSONWebServices service = new JSONWebServices(CampaignLocationsService.this);
//            List<NameValuePair> nameValuePairs = new ArrayList<>();
//            nameValuePairs.add(new BasicNameValuePair("type", requestType));
//            nameValuePairs.add(new BasicNameValuePair("lat", String.valueOf(latitude)));
//            nameValuePairs.add(new BasicNameValuePair("lon", String.valueOf(longitude)));
//            call = service.getUserCampaigns(nameValuePairs, accessToken);
//        }
//
//        void cloudCampDeliveryReport(int campID, double latitude, double longitude) {
//            type = "cloudCampDeliveryReport";
//            JSONWebServices service = new JSONWebServices(CampaignLocationsService.this);
//            List<NameValuePair> nameValuePairs = new ArrayList<>();
//            nameValuePairs.add(new BasicNameValuePair("campaign_id", String.valueOf(campID)));
//            nameValuePairs.add(new BasicNameValuePair("lat", String.valueOf(latitude)));
//            nameValuePairs.add(new BasicNameValuePair("lon", String.valueOf(longitude)));
//            call = service.cloudCampDeliveryReport(nameValuePairs, accessToken);
//        }
//
//        @Override
//        public void PostBackExecutionJSON(JSONTokener Result) {
//            try {
//                if (type.equals("getUserCampaigns")) {
//                    updatedLBC = ParseData.parseGetUserCampaigns(Result);
//                    if (updatedLBC == null)
//                        return;
//
//                    updateDistance = updatedLBC.getCampaignsUpdateInterval();
//                    editor.putFloat("camp_update_interval", (float) updateDistance);
//                    Log.e("camp_update_interval", updateDistance + "---");
////
//                    Log.e("updatedLBC", " - empty- " + updatedLBC.getCampaignLocations().isEmpty());
//                    editor.putLong("camp_last_update", System.currentTimeMillis());
//                    if (!updatedLBC.getCampaignLocations().isEmpty()) {
//                        checkCampaignsFlag = false;
//                        currentLBC = new LBC();
//                        currentLBC.setCampaignLocations(
//                                (ArrayList<CampaignLocation>) updatedLBC.getCampaignLocations().clone());
//                        updatedLBC = null;
//                        checkCampaignsFlag = true;
//                        for (int i = 0; i < currentLBC.getCampaignLocations().size(); i++) {
//                            Log.e(currentLBC.getCampaignLocations().get(i).getCampaignName(),
//                                    currentLBC.getCampaignLocations().get(i).getCampaignLat() + " -- " + currentLBC.getCampaignLocations().get(i).getCampaignLong()
//                                            + " -- " + currentLBC.getCampaignLocations().get(i).getBranchNotifyRange());
//                        }
//                        if (db.insertCampLocations(currentLBC.getCampaignLocations())) {
//                            Log.e("camp db insertion", "true");
//                            Calendar c = Calendar.getInstance();
////                        //Log.e("Current time => ", c.getTime() + "--");
////
////                        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
////                        String formattedDate = df.format(c.getTime());
////                        //Log.e("Current time => ", formattedDate);
//                            editor.putLong("camp_last_update", System.currentTimeMillis());
//                        } else
//                            editor.putLong("camp_last_update", -1);
//
//                    }
//                    editor.commit();
//
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                call = null;
//            }
//        }
//
//        /**
//         * create a location listener class
//         */
//        public class MyLocationListener implements LocationListener {
//
//            public void onLocationChanged(final Location loc) {
//                try {
//                    //Log.e(" *****s*****", loc.getLatitude() + "," + loc.getLongitude()
////                        + " -stop notif- " + sharedPreferences.getBoolean("stop_mobile_notification", false));
//                    if (sharedPreferences.getBoolean("stop_mobile_notification", false))
//                        return;
//                    if (last_loc != null) {
//                        float distance = 5.0f;
////                            calculateDistanceLocally(last_loc.getLatitude(), last_loc.getLongitude(),
////                            loc.getLatitude(), loc.getLongitude());
//                        if (distance >= GPSError) {// hena n3ml check 3la el campaigns elly 3andy kol ma ymshy ad eh??
//                            currentUserMove += distance;
//                            if (currentUserMove >= acceptableMove) {
//                                checkNearbyBranchesList(loc);
//                                currentUserMove = 0.0;
//                            }
//                            last_loc = loc;
//                        }
//                        totalDistanceSum += distance;
//                        if (totalDistanceSum >= updateDistance) {
//                            totalDistanceSum = 0.0;
//                            //API call to update campaigns
//                            getUserCampaigns("all", loc.getLatitude(), loc.getLongitude());
//                        } else if (System.currentTimeMillis() - sharedPreferences.getLong("camp_last_update", 1000 * 3600) >= (1000 * 86400))//24 Hours
////                    } else if (System.currentTimeMillis() - sharedPreferences.getLong("camp_last_update", 1000 * 3600) >= (1000 * 120))
//                            getUserCampaigns("update", loc.getLatitude(), loc.getLongitude());
//
//                    } else {
//                        last_loc = loc;
//                        //API call to update campaigns (first time) OR get it locally
//                        if (sharedPreferences.getLong("camp_last_update", -1) == -1) {
//                            getUserCampaigns("all", loc.getLatitude(), loc.getLongitude());
////                    } else if (System.currentTimeMillis() - sharedPreferences.getLong("camp_last_update", -1) >= (1000 * 120)) {
//                        } else if (TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - sharedPreferences.getLong("camp_last_update", -1)) >= 24) {
//                            getUserCampaigns("update", loc.getLatitude(), loc.getLongitude());
//                        } else {
//                            //Log.e("camp database", "exist");
//                            getAllLBC();
////                        for (int i = 0; i < currentLBC.getCampaignLocations().size(); i++) {
////                            //Log.e(currentLBC.getCampaignLocations().get(i).getCampaignName(), currentLBC.getCampaignLocations().get(i).getBranchId() + " -- " + currentLBC.getCampaignLocations().get(i).getBranchNotifyRange());
////                        }
//                        }
//
//                    }
//
//
//                    if (isBetterLocation(loc, previousBestLocation)) {
//                        loc.getLatitude();
//                        loc.getLongitude();
//                        intent.putExtra("Latitude", loc.getLatitude());
//                        intent.putExtra("Longitude", loc.getLongitude());
//                        intent.putExtra("Provider", loc.getProvider());
//                        sendBroadcast(intent);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onProviderDisabled(String provider) {
////            Toast.makeText(getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onProviderEnabled(String provider) {
////            Toast.makeText(getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onStatusChanged(String provider, int status, Bundle extras) {
//
//            }
//
//        }
//
//        private class sendNotification extends AsyncTask<String, Void, Bitmap> {
//
//            Context ctx;
//            String campID, campTitle, campBody, couponImg, couponSlug, couponType;
//
//            sendNotification(Context context) {
//                super();
//                this.ctx = context;
//            }
//
//            @Override
//            protected Bitmap doInBackground(String... params) {
//
//                InputStream in;
//                campID = params[0];
//                campTitle = params[1];
//                campBody = params[2];
////            coupID = params[3];
//                couponImg = params[3];
//                couponSlug = params[4];
//                couponType = params[5];
//                try {
//
//                    URL url = new URL(couponImg);
//                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                    connection.setDoInput(true);
//                    connection.connect();
//                    in = connection.getInputStream();
//                    Bitmap myBitmap = BitmapFactory.decodeStream(in);
//                    return myBitmap;
//
//
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Bitmap result) {
//
//                super.onPostExecute(result);
//                try {
//
//                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//                    Notification notification;
//
//                    Intent intent = new Intent(ctx, MainActivity.class);
//                    intent.putExtra("campaignID", campID);
//                    intent.putExtra("coupSlug", couponSlug);
//                    intent.putExtra("coupType", couponType);
//                    PendingIntent pIntent = PendingIntent.getActivity(ctx, (int) System.currentTimeMillis(), intent, 0);
//
//                    Bitmap fansCouponIcon = ((BitmapDrawable) getResources().getDrawable(R.drawable.fans_coupons)).getBitmap();
//                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
//                        notification = new Notification.Builder(ctx)
//                                .setContentTitle(campTitle)
//                                .setContentText(campBody)
//                                .setSmallIcon(R.drawable.map_pin)
//                                .setLargeIcon(fansCouponIcon)
//                                .setContentIntent(pIntent)
//                                .setAutoCancel(true)
//                                .setStyle(new Notification.BigPictureStyle()
//                                        .bigPicture(result)
//                                        .setBigContentTitle(campTitle))
//                                .build();
////                        notificationManager.notify(0, notif);
//
//                    } else {
//                        notification = new Notification.Builder(ctx)
//                                .setContentTitle(campTitle)
//                                .setContentText(campBody)
//                                .setSmallIcon(R.drawable.fans_coupons)
//                                .setContentIntent(pIntent)
//                                .setAutoCancel(true)
//                                .getNotification();
//                    }
//                    notificationManager.notify(++uniNum, notification);
//                    notificationManager = null;
//                    notification = null;
//                    pIntent = null;
//                    deliveryUpdate(Integer.valueOf(campID));
//                    checkCampaignsFlag = true;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//
////    'hena timer
////    'y3ml update
////    'lel campaigns
////    'kol youm wyb2a hwa elly y3ml awel request 5ales awel mra w in case of not logined user yb2a el youm elly
////    'ba3do teshta3'al hia
//
//    }

//    'hena timer
//    'y3ml update
//    'lel campaigns
//    'kol youm wyb2a hwa elly y3ml awel request 5ales awel mra w in case of not logined user yb2a el youm elly
//    'ba3do teshta3'al hia

}
