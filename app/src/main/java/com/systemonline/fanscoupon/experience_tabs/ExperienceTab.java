package com.systemonline.fanscoupon.experience_tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.systemonline.fanscoupon.Base.BaseFragment;
import com.systemonline.fanscoupon.Helpers.Utility;
import com.systemonline.fanscoupon.Model.CustomLoading;
import com.systemonline.fanscoupon.Model.Experience;
import com.systemonline.fanscoupon.R;
import com.systemonline.fanscoupon.WebServices.JSONAsync;
import com.systemonline.fanscoupon.WebServices.JSONWebServices;
import com.systemonline.fanscoupon.WebServices.ParseData;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;


public class ExperienceTab extends BaseFragment {

    public static int int_items = 3;
    public static ArrayList<ArrayList<Experience>> allExperiences;
    public static int expLimit = 4, expIndex = 0;
    public static Experience experienceToEdit;
    private static Utility _utility;
    private static JSONAsync call;
    private static CustomLoading customLoading;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    /**
     * make a request to get all exp
     *
     * @param limit
     */
    public static void requestAllExp(int limit, int offset, int categoryID, BaseFragment baseFragment) {
        if (_utility.isConnectingToInternet_ping()) {

            Log.e("all -- request exp", limit + "");
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("index", String.valueOf(offset)));
            nameValuePairs.add(new BasicNameValuePair("limit", String.valueOf(limit)));

            if (categoryID != 0) {
                nameValuePairs.add(new BasicNameValuePair("filters[1][key]", "category_id"));
                nameValuePairs.add(new BasicNameValuePair("filters[1][value]", String.valueOf(categoryID)));
            }


            JSONWebServices service = new JSONWebServices(baseFragment);
            call = service.allExperiencesRequest(nameValuePairs);

        } else {
//
            customLoading.hideProgress();
            _utility.showMessage(baseFragment.getResources().getString(R.string.no_net));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         *Inflate tab_layout and setup Views.
         */
        View rootView = inflater.inflate(R.layout.exp_tabs_layout, null);
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        getActivity().setTitle(getString(R.string.my_experiences));
        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        _utility = new Utility(getContext());
        customLoading = new CustomLoading(_utility.getCurrentActivity());

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                experienceToEdit = null;
                Fragment fragment = new WriteExperienceFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        if (allExperiences == null) {
            customLoading.showProgress(_utility.getCurrentActivity());
            requestAllExp(expLimit, expIndex, 0, ExperienceTab.this);

        } else {
            Log.e("all good size", ">> " + allExperiences.get(0).size());
            Log.e("all bad size", ">> " + allExperiences.get(1).size());
            Log.e("all fun size", ">> " + allExperiences.get(2).size());
            customLoading.hideProgress();
            Log.e("experience hide", rootView.toString());
            showView();
        }

        /**
         *Set an Apater for the View Pager
         */
//        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));
//
//        /**
//         * Now , this is a workaround ,
//         * The setupWithViewPager dose't works without the runnable .
//         * Maybe a Support Library Bug .
//         */
//
//        tabLayout.post(new Runnable() {
//            @Override
//            public void run() {
//                tabLayout.setupWithViewPager(viewPager);
//            }
//        });

        return rootView;

    }

    @Override
    public void PostBackExecutionJSON(JSONTokener Result) {
        try {
            Log.e("all exp response", "-------------");

            allExperiences = ParseData.parseAllExperiences(Result);

            if (allExperiences == null) {
                customLoading.hideProgress();

                _utility.showMessage(getResources().getString(R.string.ws_err));
//
            } else {
                Log.e("all good size", ">> " + allExperiences.get(0).size());
                Log.e("all bad size", ">> " + allExperiences.get(1).size());
                Log.e("all fun size", ">> " + allExperiences.get(2).size());
                customLoading.hideProgress();
                showView();
            }
        } catch (Exception e) {
            e.printStackTrace();
            _utility.showMessage(getResources().getString(R.string.ws_err));
        } finally {
//
            customLoading.hideProgress();
            call = null;
        }

    }

    void showView() {
        /**
         *Set an Apater for the View Pager
         */
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));

        /**
         * Now , this is a workaround ,
         * The setupWithViewPager dose't works without the runnable .
         * Maybe a Support Library Bug .
         */

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });
    }

    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Return fragment with respect to Position .
         */

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new GoodExperienceFragment();
                case 1:
                    return new BadExperienceFragment();
                case 2:
                    return new FunExperienceFragment();
            }
            return null;
        }

        @Override
        public int getCount() {

            return int_items;

        }

        /**
         * This method returns the title of the tab according to the position.
         */

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return getResources().getString(R.string.good_exp);
                case 1:
                    return getResources().getString(R.string.bad_exp);
                case 2:
                    return getResources().getString(R.string.fun_exp);
            }
            return null;
        }
    }

}