package com.systemonline.fanscoupon;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.systemonline.fanscoupon.Base.BaseFragment;
import com.systemonline.fanscoupon.Helpers.Utility;
import com.systemonline.fanscoupon.Model.CustomLoading;
import com.systemonline.fanscoupon.Model.Filter;
import com.systemonline.fanscoupon.WebServices.JSONAsync;
import com.systemonline.fanscoupon.WebServices.JSONWebServices;
import com.systemonline.fanscoupon.WebServices.ParseData;
import com.systemonline.fanscoupon.brands_tabs.BrandsTab;
import com.systemonline.fanscoupon.coupon_tabs.CouponTab;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends BaseFragment {
    public static ArrayList<ArrayList<Filter>> allFilters;
    Utility _utility;
    RadioGroup rad_grp_lang;
    RadioButton radBtn_eng, radBtn_ar;
    JSONAsync call;
    private String requestType;
    private ListView filter1LV, filter2LV;
    private TextView filterName1, filterName2, filterName3;
    private TextView saveFilter;
    private CustomLoading customLoading;
    private CheckedTextView stopMobNotif;
    //    Button btn_switch;
    private RelativeLayout rel_filter1, rel_filter2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, null);
        _utility = new Utility(getContext());
        customLoading = new CustomLoading(_utility.getCurrentActivity());
        rad_grp_lang = (RadioGroup) rootView.findViewById(R.id.rad_grp_lang);
        radBtn_eng = (RadioButton) rootView.findViewById(R.id.radBtn_eng);
        radBtn_ar = (RadioButton) rootView.findViewById(R.id.radBtn_ar);
        rel_filter1 = (RelativeLayout) rootView.findViewById(R.id.rel_filter1);
        rel_filter2 = (RelativeLayout) rootView.findViewById(R.id.rel_filter2);
        RelativeLayout rel_stopNotif = (RelativeLayout) rootView.findViewById(R.id.rel_stopNotif);
        getActivity().setTitle(getString(R.string.settings));

//        btn_switch=(Button)rootView.findViewById(R.id.btn_switch);
//        btn_switch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(Const.webServiceURL.contains("test")){
//                Const.webServiceURL=Const.webServiceURL.replace("test","www");}
//                else{
//                    Const.webServiceURL=Const.webServiceURL.replace("www","test");
//                }
////                logout
//            }
//        });

        if (MainActivity.sharedPreferences.getString("lang", "null").equals("en")) {
            radBtn_eng.setChecked(true);
        } else if (MainActivity.sharedPreferences.getString("lang", "null").equals("ar")) {
            radBtn_ar.setChecked(true);
        }
        filter1LV = (ListView) rootView.findViewById(R.id.lv_filter1);
        filter2LV = (ListView) rootView.findViewById(R.id.lv_filter2);
        filterName1 = (TextView) rootView.findViewById(R.id.filter1_name);
        filterName2 = (TextView) rootView.findViewById(R.id.filter2_name);
        filterName3 = (TextView) rootView.findViewById(R.id.filter3_name);
        stopMobNotif = (CheckedTextView) rootView.findViewById(R.id.stop_mob_noti);

        rel_stopNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stopMobNotif.isChecked()) {
                    stopMobNotif.setChecked(false);
                } else
                    stopMobNotif.setChecked(true);
            }
        });

        saveFilter = (TextView) rootView.findViewById(R.id.btn_save_filter);
        saveFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("filter save", "btn");
                if (MainActivity.currentFan != null)
                    try {
                        saveFilter.setEnabled(false);
                        sendSettingsData();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error settings", Toast.LENGTH_LONG).show();

                    } finally {
                        customLoading.hideProgress();
                    }
                else
                    changeLanguage();
            }
        });

        TextView cancelFilter = (TextView) rootView.findViewById(R.id.btn_cancel_filter);
        cancelFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        if (MainActivity.currentFan != null)
            getSettingsData();
        else {
            rel_filter1.setVisibility(View.GONE);
            rel_filter2.setVisibility(View.GONE);
        }

        return rootView;
    }

    private void changeLanguage() {
        if (rad_grp_lang.getCheckedRadioButtonId() == radBtn_eng.getId() &&
                MainActivity.sharedPreferences.getString("lang", "null").equals("ar")) {
            MainActivity.editor.putString("lang", "en");
            MainActivity.langResult = "en";
            MainActivity.editor.commit();
//            CouponTab.allCoupons = null;
            CouponTab.allCoupons2 = null;
//            CouponTab.forYou = null;
            CouponTab.forYou2 = null;
            CouponTab.myCoupons = null;
            BrandsTab.allBrands = null;
            BrandsTab.myBrands = null;
            CouponTab.couponPage = 1;
            CouponTab.couponPageOnlyYou = 1;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                getActivity().finishAffinity();
            } else {
                getActivity().finish();
            }

            Intent i = getActivity().getIntent();
            startActivity(i);
        } else if (rad_grp_lang.getCheckedRadioButtonId() == radBtn_ar.getId() &&
                MainActivity.sharedPreferences.getString("lang", "null").equals("en")) {
            MainActivity.editor.putString("lang", "ar");
            MainActivity.langResult = "ar";
            MainActivity.editor.commit();
//            CouponTab.allCoupons = null;
            CouponTab.allCoupons2 = null;
//            CouponTab.forYou = null;
            CouponTab.forYou2 = null;
            CouponTab.myCoupons = null;
            CouponTab.couponPage = 1;
            CouponTab.couponPageOnlyYou = 1;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                getActivity().finishAffinity();
            } else {
                getActivity().finish();
            }
            Intent i = getActivity().getIntent();
            startActivity(i);
        }
        saveFilter.setEnabled(true);
    }

    private void sendSettingsData() {
        if (allFilters == null)
            changeLanguage();
        if (allFilters.get(0).isEmpty() && allFilters.get(1).isEmpty())
            return;
        if (!_utility.isConnectingToInternet_ping()) {
            customLoading.hideProgress();
            MainActivity._utility.showMessage(getResources().getString(R.string.no_net));
            return;
        }
        requestType = "sendRequest";

        customLoading.showProgress(_utility.getCurrentActivity());
        JSONWebServices service = new JSONWebServices(SettingsFragment.this);
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        int i = 0;
        for (; i < allFilters.get(0).size(); i++) {
            if (allFilters.get(0).get(i).isChecked())
                nameValuePairs.add(new BasicNameValuePair("settings_ids[" + i + "]", allFilters.get(0).get(i).getFilterID() + ""));
            Log.e("fb id", allFilters.get(0).get(i).isChecked() + "");
        }

        for (int x = 0; x < allFilters.get(1).size(); x++, i++) {
            if (allFilters.get(1).get(x).isChecked())
                nameValuePairs.add(new BasicNameValuePair("settings_ids[" + i + "]", allFilters.get(1).get(x).getFilterID() + ""));
            Log.e("noti id", allFilters.get(1).get(x).isChecked() + "");
        }

        //stop mobile notification
        if (stopMobNotif.isChecked())
            nameValuePairs.add(new BasicNameValuePair("settings_ids[" + i + "]", allFilters.get(2).get(0).getFilterID() + ""));


        call = service.sendSettingsData(nameValuePairs);
    }

    private void getSettingsData() {
        if (!_utility.isConnectingToInternet_ping()) {
            customLoading.hideProgress();
            MainActivity._utility.showMessage(getResources().getString(R.string.no_net));
            return;
        }
        requestType = "getRequest";

        customLoading.showProgress(_utility.getCurrentActivity());
        JSONWebServices service = new JSONWebServices(SettingsFragment.this);
        call = service.getSettingsData(null);
    }

    @Override
    public void PostBackExecutionJSON(JSONTokener Result) {
        try {
            call = null;
            if (requestType.equals("sendRequest")) {
                Log.e("setting resp", Result.toString());
                if (ParseData.parseActionsResult(Result)) {
                    MainActivity.editor.putBoolean("stop_mobile_notification", stopMobNotif.isChecked()).commit();
                    _utility.showMessage(getString(R.string.setting_updated));
                } else
                    _utility.showMessage(getString(R.string.ws_err));
                changeLanguage();

            } else {
                allFilters = ParseData.parseSettings(Result);
                if (allFilters != null) {
                    filterName1.setText(getResources().getString(R.string.fb_set));
                    filterName2.setText(getResources().getString(R.string.notification_set));
                    filterName3.setText(getResources().getString(R.string.mob_set));


                    if (allFilters.get(0) != null && !allFilters.get(0).isEmpty()) {
                        SettingsRowAdapter settingsRowAdapter = new SettingsRowAdapter(getContext(), allFilters.get(0), "FB");
                        filter1LV.setAdapter(settingsRowAdapter);
                        _utility.setListViewHeightBasedOnChildren(filter1LV);
                    } else {
                        rel_filter1.setVisibility(View.GONE);
                    }

                    if (allFilters.get(0) != null && !allFilters.get(1).isEmpty()) {
                        SettingsRowAdapter settingsRowAdapter2 = new SettingsRowAdapter(getContext(), allFilters.get(1), "NOTI");
                        filter2LV.setAdapter(settingsRowAdapter2);
                        _utility.setListViewHeightBasedOnChildren(filter2LV);
                    } else {
                        rel_filter2.setVisibility(View.GONE);
                    }

                    stopMobNotif.setChecked(allFilters.get(2).get(0).isChecked());

                    filter1LV.setFocusable(false);
                    filter2LV.setFocusable(false);

                } else
                    _utility.showMessage(getString(R.string.ws_err));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), getString(R.string.ws_err), Toast.LENGTH_LONG).show();
        } finally {
            customLoading.hideProgress();
        }
    }
}