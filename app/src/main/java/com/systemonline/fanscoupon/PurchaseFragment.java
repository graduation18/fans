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
import com.systemonline.fanscoupon.Model.CustomLoading;
import com.systemonline.fanscoupon.Model.PurchaseCoupon;
import com.systemonline.fanscoupon.WebServices.JSONAsync;
import com.systemonline.fanscoupon.WebServices.JSONWebServices;
import com.systemonline.fanscoupon.WebServices.ParseData;
import com.systemonline.fanscoupon.coupon_tabs.CouponTab;

import org.json.JSONTokener;

public class PurchaseFragment extends BaseFragment {


    TextView moneyAmount, fromDate, toDate;
    Utility _utility;
    PurchaseCoupon purchaseCoupon;
    TextView brandName, couponName;
    JSONAsync call;
    private LinearLayout lin_add_fav_s;
    private ImageView brandImage;
    private String requestType;
    private CustomLoading customLoading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.purchases_screen, null);

        _utility = new Utility(getContext());
        customLoading = new CustomLoading(_utility.getCurrentActivity());
        moneyAmount = (TextView) rootView.findViewById(R.id.tv_money_amount);
        brandImage = (ImageView) rootView.findViewById(R.id.imgv_brand);
        fromDate = (TextView) rootView.findViewById(R.id.tv_from_date);
        toDate = (TextView) rootView.findViewById(R.id.tv_to_date);
        brandName = (TextView) rootView.findViewById(R.id.tv_brand);
        couponName = (TextView) rootView.findViewById(R.id.tv_cop_name);
        lin_add_fav_s = (LinearLayout) rootView.findViewById(R.id.lin_add_fav_s);
        lin_add_fav_s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToWallet();
            }
        });
        getPurchasesData();
        return rootView;
    }

    private void getPurchasesData() {
        if (!_utility.isConnectingToInternet_ping()) {
            customLoading.hideProgress();
            MainActivity._utility.showMessage(getResources().getString(R.string.no_net));
            return;
        }
        customLoading.showProgress(_utility.getCurrentActivity());
        requestType = "getRequest";
        JSONWebServices service = new JSONWebServices(PurchaseFragment.this);
        call = service.purchaseRequest(null);
    }

    private void addToWallet() {
        if (!_utility.isConnectingToInternet_ping()) {
            customLoading.hideProgress();
            MainActivity._utility.showMessage(getResources().getString(R.string.no_net));
            return;
        }
        customLoading.showProgress(_utility.getCurrentActivity());
        requestType = "sendRequest";
        JSONWebServices service = new JSONWebServices(PurchaseFragment.this);

        call = service.qualifiedToCouponRequestFragment(null, CouponTab.coupSlugTemp);
    }


    @Override
    public void PostBackExecutionJSON(JSONTokener Result) {
        try {
            call = null;
            if (requestType.equals("sendRequest")) {

                CouponTab.couponQualification = ParseData.parseQualifiedFragment(Result);
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
                purchaseCoupon = ParseData.parsePurchase(Result);
                if (purchaseCoupon != null) {
                    Picasso.with(getContext()).load(Const.imagesURL + "brands/50x50/" + purchaseCoupon.getBrandLogo()).placeholder(R.drawable.ph_brand).into(brandImage);

                    brandName.setText(purchaseCoupon.getBrandName());
                    couponName.setText(purchaseCoupon.getCouponName());
                    moneyAmount.setText(purchaseCoupon.getPurchaseAmountMoreThan());
                    toDate.setText(purchaseCoupon.getToDate());
                    fromDate.setText(purchaseCoupon.getFromDate());
                } else {
                    _utility.showMessage(getResources().getString(R.string.pur_ws_err));
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