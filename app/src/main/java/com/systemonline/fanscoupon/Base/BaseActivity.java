package com.systemonline.fanscoupon.Base;

import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationSettingsResult;
import com.systemonline.fanscoupon.Helpers.Utility;
import com.systemonline.fanscoupon.Interfaces.IServiceCallBack;

import org.json.JSONTokener;


public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        ResultCallback<LocationSettingsResult>, IServiceCallBack {

    public BaseActivity MyActivity;
    Utility _utility;


    @Override
    protected void onStart() {
        super.onStart();
        MyActivity = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Base Activity", "set current activity");
        _utility = new Utility(getApplicationContext());
        _utility.SetCurrentActivity(this);
    }


    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {

    }

    @Override
    public void PostBackExecutionJSON(JSONTokener Result) {

    }
}