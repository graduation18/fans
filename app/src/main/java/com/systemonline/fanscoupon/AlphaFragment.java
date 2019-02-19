package com.systemonline.fanscoupon;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.systemonline.fanscoupon.Base.BaseFragment;
import com.systemonline.fanscoupon.Helpers.Const;
import com.systemonline.fanscoupon.Helpers.Utility;
import com.systemonline.fanscoupon.Model.Alpha;
import com.systemonline.fanscoupon.Model.CustomLoading;
import com.systemonline.fanscoupon.WebServices.JSONAsync;
import com.systemonline.fanscoupon.WebServices.JSONWebServices;
import com.systemonline.fanscoupon.WebServices.ParseData;
import com.systemonline.fanscoupon.coupon_tabs.CouponTab;

import org.json.JSONTokener;

public class AlphaFragment extends BaseFragment {

    Utility _utility;
    TextView brandName, win_all, ch_all;
    //    SpannableStringBuilder builder;
//    String strBuild;
//    SpannableString strColor;
    JSONAsync call;
    private String requestType;
    private ImageView brandImage;
    private CustomLoading customLoading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.alpha, null);

        _utility = new Utility(getContext());
        customLoading = new CustomLoading(_utility.getCurrentActivity());
        brandName = (TextView) rootView.findViewById(R.id.tv_brand);
        brandImage = (ImageView) rootView.findViewById(R.id.imgv_brand);
        ch_all = (TextView) rootView.findViewById(R.id.ch_all);
        win_all = (TextView) rootView.findViewById(R.id.win_all);

        ch_all.setText(_utility.colorString(R.string.challenge, R.color.red,
                getResources().getString(R.string.to_be_the_first_fan_used_this_coupon), R.color.black)
                , TextView.BufferType.SPANNABLE);

        LinearLayout lin_add_fav_s = (LinearLayout) rootView.findViewById(R.id.lin_add_fav_s);
        lin_add_fav_s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToWallet();
            }
        });
        getData();

        return rootView;
    }

    private void addToWallet() {
        if (!_utility.isConnectingToInternet_ping()) {
            customLoading.hideProgress();
            MainActivity._utility.showMessage(getResources().getString(R.string.no_net));
            return;
        }

//
        customLoading.showProgress(_utility.getCurrentActivity());
        requestType = "sendRequest";
        JSONWebServices service = new JSONWebServices(AlphaFragment.this);

        call = service.qualifiedToCouponRequestFragment(null, CouponTab.coupSlugTemp);
    }

    private void getData() {
        if (!_utility.isConnectingToInternet_ping()) {
//            MainActivity.
            customLoading.hideProgress();
            MainActivity._utility.showMessage(getResources().getString(R.string.no_net));
            return;
        }
        requestType = "getRequest";

//
        customLoading.showProgress(_utility.getCurrentActivity());
        JSONWebServices service = new JSONWebServices(AlphaFragment.this);
        call = service.alphaRequest(null);
    }

    @Override
    public void PostBackExecutionJSON(JSONTokener Result) {
        try {
            call = null;
            if (requestType.equals("sendRequest")) {
                CouponTab.couponQualification = ParseData.parseQualifiedFragment(Result);
//
                customLoading.hideProgress();
                if (CouponTab.couponQualification == null) {
                    _utility.showMessage(getContext().getResources().getString(R.string.ws_err));
                    return;
                }
                Fragment fragment;

                if (CouponTab.couponQualification.isQualifiedUser()) {
                    fragment = new QualifiedFragment();
                } else {
                    fragment = new DisqualifiedFragment();
                }

                FragmentManager fragmentManager = ((FragmentActivity) _utility.getCurrentActivity()).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            } else {
                Alpha alpha = ParseData.alphaRequest(Result);
                if (alpha != null) {
                    Picasso.with(getContext()).load(Const.imagesURL + "brands/50x50/" + alpha.getBrandLogo()).placeholder(R.drawable.ph_brand).into(brandImage);

                    brandName.setText(alpha.getBrandName());

                    win_all.setText(_utility.colorString(R.string.win, R.color.red, alpha.getCouponName(), R.color.black), TextView.BufferType.SPANNABLE);
                } else {
                    _utility.showMessage(getResources().getString(R.string.ws_err));
                    customLoading.hideProgress();
                    getActivity().onBackPressed();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(_utility.GetAppContext(), getString(R.string.ws_err), Toast.LENGTH_LONG).show();

        } finally {
            customLoading.hideProgress();
        }
    }

}