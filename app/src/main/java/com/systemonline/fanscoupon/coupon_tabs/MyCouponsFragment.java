package com.systemonline.fanscoupon.coupon_tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.systemonline.fanscoupon.Base.BaseFragment;
import com.systemonline.fanscoupon.MainActivity;
import com.systemonline.fanscoupon.Model.Coupon;
import com.systemonline.fanscoupon.Model.CustomLoading;
import com.systemonline.fanscoupon.R;
import com.systemonline.fanscoupon.SingleCouponFragment;
import com.systemonline.fanscoupon.WebServices.JSONAsync;
import com.systemonline.fanscoupon.WebServices.JSONWebServices;
import com.systemonline.fanscoupon.WebServices.ParseData;

import org.json.JSONTokener;

import java.util.ArrayList;

public class MyCouponsFragment extends BaseFragment {

    MyCouponsAdapter myCopAd;
    ListView lv_my_coupons;
    Button loadMore;
    boolean loadMoreFlag;
    TextView tv_no_cop;
    JSONAsync call;
    private CustomLoading customLoading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.cop_tab_my_coupons, null);
        Log.e("0pen", "MyCoupon\n-------");
        MainActivity.langResult = MainActivity.sharedPreferences.getString("lang", "en");
        customLoading = new CustomLoading(getContext());
        tv_no_cop = (TextView) root.findViewById(R.id.tv_no_cop);
        tv_no_cop.setVisibility(View.GONE);
        lv_my_coupons = (ListView) root.findViewById(R.id.lv_my_coupons);
        loadMore = (Button) root.findViewById(R.id.loadMoreMyCoupons);
        loadMore.setVisibility(View.GONE);
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.myCoupons_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                customLoading.showProgress(MainActivity._utility.getCurrentActivity());
                requestMyCoupons();//refresh
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        processCoupons();
        return root;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            Log.e("00vis00", "myCop - visible");
        } else {
            Log.e("00vis00", "myCop - notVisible");
        }
    }

    private void processCoupons() {

        if (MainActivity.currentFan == null) {
            Log.e("MyCoupons", "current fan is null");
            noCoupons();
        } else {
            customLoading.showProgress(getContext());

            loadMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadMoreFlag = true;
                    customLoading.showProgress(getContext());
                    requestMyCoupons();

                }
            });
            loadMoreFlag = false;
            CouponTab.myCouponsOffset = 0;

            requestMyCoupons();

        }
    }

    /**
     * make a request to get user's coupons
     */
    private void requestMyCoupons() {
        if (MainActivity.currentFan == null)
            return;
        if (MainActivity._utility.isConnectingToInternet_ping()) {
            JSONWebServices service = new JSONWebServices(MyCouponsFragment.this);
            call = service.getMyCoupons(null);
        } else {
            customLoading.hideProgress();
            MainActivity._utility.showMessage(getResources().getString(R.string.no_net));
        }

    }

    @Override
    public void PostBackExecutionJSON(JSONTokener Result) {
        try {
            CouponTab.myCoupons = new ArrayList<>();
            CouponTab.myCoupons = ParseData.NEW_parseMyCoupons(Result);
            if (CouponTab.myCoupons != null) {
                if (CouponTab.myCoupons.size() != 0) {
                    CouponTab.myCouponsOffset = CouponTab.myCoupons.size();
                    showCoupons();
                } else {
                    noCoupons();
                }
            } else {
                myCopAd = new MyCouponsAdapter(getActivity(), new ArrayList<Coupon>());
                lv_my_coupons.setAdapter(myCopAd);
                loadMore.setVisibility(View.GONE);
                tv_no_cop.setVisibility(View.VISIBLE);
                MainActivity._utility.showMessage(getResources().getString(R.string.ws_err));
            }
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity._utility.showMessage(getResources().getString(R.string.ws_err));
        } finally {
            customLoading.hideProgress();
            call = null;
        }
    }

    private void noCoupons() {
        loadMore.setVisibility(View.GONE);
        tv_no_cop.setVisibility(View.VISIBLE);
        myCopAd = new MyCouponsAdapter(getActivity(), new ArrayList<Coupon>());
        lv_my_coupons.setAdapter(myCopAd);
    }

    private void showCoupons() {
        tv_no_cop.setVisibility(View.GONE);
        lv_my_coupons.setVisibility(View.VISIBLE);
        myCopAd = new MyCouponsAdapter(getActivity(), CouponTab.myCoupons);
        lv_my_coupons.setAdapter(myCopAd);
        MainActivity._utility.setListViewHeightBasedOnChildren(lv_my_coupons);

        lv_my_coupons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    MainActivity.selectedCoupon = (Coupon) CouponTab.myCoupons.get(position).clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
                CouponTab.myCoupon = true;
                Fragment fragment = new SingleCouponFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }


}
