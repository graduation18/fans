package com.systemonline.fanscoupon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.systemonline.fanscoupon.Base.BaseActivity;
import com.systemonline.fanscoupon.Helpers.Utility;
import com.systemonline.fanscoupon.Model.Country;
import com.systemonline.fanscoupon.WebServices.JSONWebServices;
import com.systemonline.fanscoupon.WebServices.ParseData;

import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Locale;

import static com.systemonline.fanscoupon.MyApp.getContext;

/**
 * Created by Online 108 on 1/6/2016.
 */
public class SplashScreen extends BaseActivity {

    private AlertDialog alertDialog;
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;
    private Utility utility;
    private ArrayList<Country> activeCountries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        utility = new Utility(getBaseContext());
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        try {
            alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setIcon(R.drawable.wrong_icon);
            alertDialog.setCancelable(false);
            alertDialog.setButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            Log.e("0k pressed", "");
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                finishAffinity();
                            } else {
                                finish();
                            }
                        }
                    });


            ImageView logo = new ImageView(getApplicationContext());
            logo.setImageResource(R.drawable.splash_icon);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(500, 500);
            layoutParams.gravity = Gravity.CENTER;
            logo.setLayoutParams(layoutParams);
            final ViewAnimator simpleViewAnimator = (ViewAnimator) findViewById(R.id.anim);
            simpleViewAnimator.addView(logo);
            Animation in = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            in.setDuration(2500);
            simpleViewAnimator.setInAnimation(in);
            simpleViewAnimator.startAnimation(in);

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

                ActivityCompat.requestPermissions(SplashScreen.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.INTERNET,
                        android.Manifest.permission.READ_PHONE_STATE,
                        android.Manifest.permission.ACCESS_NETWORK_STATE}, 10);

//                alertDialog.setMessage(getResources().getString(R.string.no_prem));
//                alertDialog.show();

                return;
            } else {

                if (sharedPreferences.getString("country", "no").equals("no")) {
                    getActiveCountries();
                } else
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startMainActivity();
                        }
                    }, 5000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            startMainActivity();
        }

    }

    private void startMainActivity() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        } else {
            finish();
        }
        Intent i = new Intent(SplashScreen.this, MainActivity.class);
        startActivity(i);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 10: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length == 6
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[2] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[3] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[4] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[5] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(SplashScreen.this, grantResults.length + " Permissions granted ", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startMainActivity();
                        }
                    }, 5000);
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    alertDialog.setMessage(getResources().getString(R.string.no_prem));
                    alertDialog.show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
//                    Toast.makeText(SplashScreen.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    void showCountryDialog() {
        final Dialog country_dialog = new Dialog(SplashScreen.this);
        country_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        country_dialog.setContentView(R.layout.popup_country);
        country_dialog.setCancelable(false);
        country_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ListView countriesLV = (ListView) country_dialog.findViewById(R.id.listView_countries);

        countriesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("country", activeCountries.get(position).getCountryName());

                editor.putString("country", String.valueOf(activeCountries.get(position).getCountryID()))
                        .commit();

                startMainActivity();
            }
        });

        if (activeCountries != null) {
            CountriesAdapter countriesAdapter = new CountriesAdapter(getContext(), activeCountries);
            countriesLV.setAdapter(countriesAdapter);
            utility.setListViewHeightBasedOnChildren(countriesLV);
        }

        country_dialog.show();

    }


    void getActiveCountries() {
        JSONWebServices service = new JSONWebServices(SplashScreen.this);
        if (Locale.getDefault().getDisplayLanguage().equals("العربية"))
            service.getActiveCountries("ar");
        else
            service.getActiveCountries("en");

    }

    @Override
    public void PostBackExecutionJSON(JSONTokener Result) {
        try {
            activeCountries = ParseData.parseActiveCountries(Result);
            showCountryDialog();
//            utility.HideDialog();
        } catch (Exception e) {
            e.printStackTrace();
            utility.showMessage(getResources().getString(R.string.ws_err));
        } finally {
//            utility.HideDialog();
        }
    }

}
