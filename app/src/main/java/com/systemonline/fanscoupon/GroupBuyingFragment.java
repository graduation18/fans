package com.systemonline.fanscoupon;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
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
import com.systemonline.fanscoupon.Model.GroupBuyingCoupon;
import com.systemonline.fanscoupon.WebServices.JSONAsync;
import com.systemonline.fanscoupon.WebServices.JSONWebServices;
import com.systemonline.fanscoupon.WebServices.ParseData;
import com.systemonline.fanscoupon.coupon_tabs.CouponTab;

import org.json.JSONTokener;

public class GroupBuyingFragment extends BaseFragment {


    Utility _utility;
    GroupBuyingCoupon groupBuyingCoupon;
    LinearLayout lin_add_fav_s;
    TextView brandName, txt_win_all, txt_ch_all, txt_share_all;
    JSONAsync call;
    SpannableStringBuilder builder, builderTemp;
    String strBuild;
    SpannableString strColor;
    private String requestType;
    private ImageView brandImage;
    private CustomLoading customLoading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.group_buying, null);

        _utility = new Utility(getContext());
        customLoading = new CustomLoading(_utility.getCurrentActivity());
        brandImage = (ImageView) rootView.findViewById(R.id.imgv_brand);

        brandName = (TextView) rootView.findViewById(R.id.tv_brand);
        txt_win_all = (TextView) rootView.findViewById(R.id.txt_win_all);
        txt_ch_all = (TextView) rootView.findViewById(R.id.txt_ch_all);
        txt_share_all = (TextView) rootView.findViewById(R.id.txt_share_all);
        lin_add_fav_s = (LinearLayout) rootView.findViewById(R.id.lin_add_fav_s);
        lin_add_fav_s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToWallet();
            }
        });
        getData();
        return rootView;
    }

    private void getData() {
        if (!_utility.isConnectingToInternet_ping()) {
            customLoading.hideProgress();
            MainActivity._utility.showMessage(getResources().getString(R.string.no_net));
            return;
        }
        requestType = "getRequest";

        customLoading.showProgress(_utility.getCurrentActivity());
        JSONWebServices service = new JSONWebServices(GroupBuyingFragment.this);
        call = service.groupBuyingRequest(null);
    }

    private void addToWallet() {
        if (!_utility.isConnectingToInternet_ping()) {
            customLoading.hideProgress();
            MainActivity._utility.showMessage(getResources().getString(R.string.no_net));
            return;
        }
        customLoading.showProgress(_utility.getCurrentActivity());
        requestType = "sendRequest";
        JSONWebServices service = new JSONWebServices(GroupBuyingFragment.this);

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
                groupBuyingCoupon = ParseData.parseGroupBuying(Result);
                if (groupBuyingCoupon != null) {
                    Picasso.with(getContext()).load(Const.imagesURL + "brands/50x50/" + groupBuyingCoupon.getBrandLogo()).placeholder(R.drawable.ph_brand).into(brandImage);

                    stringBuilder(R.string.share_url, groupBuyingCoupon.getFrontEndCouponURL());
                    txt_share_all.setText(builder, TextView.BufferType.SPANNABLE);

                    brandName.setText(groupBuyingCoupon.getBrandName());

                    stringBuilder(R.string.win, groupBuyingCoupon.getCouponName());
                    txt_win_all.setText(builder, TextView.BufferType.SPANNABLE);

                    builderTemp = new SpannableStringBuilder();
                    stringBuilder(R.string.challenge, getResources().getString(R.string.fans_who_is_keeping));
                    builderTemp.append(builder);
                    builderTemp.append(" ");
                    builder = new SpannableStringBuilder();
                    strBuild = String.valueOf(groupBuyingCoupon.getMinNumberToActivate());
                    strColor = new SpannableString(strBuild);
                    strColor.setSpan(new ForegroundColorSpan(Color.RED), 0, strBuild.length(), 0);
                    builder.append(strColor);
                    builder.append(" ");
                    strBuild = getResources().getString(R.string.fan);
                    strColor = new SpannableString(strBuild);
                    strColor.setSpan(new ForegroundColorSpan(Color.BLACK), 0, strBuild.length(), 0);
                    builder.append(strColor);
//                    stringBuilder(R.string.fan,String.valueOf(groupBuyingCoupon.getMinNumberToActivate()));
                    builderTemp.append(builder);
                    txt_ch_all.setText(builderTemp, TextView.BufferType.SPANNABLE);

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

    void stringBuilder(int strId, String strVar) {
        builder = new SpannableStringBuilder();
        strBuild = getResources().getString(strId);
        strColor = new SpannableString(strBuild);
        strColor.setSpan(new ForegroundColorSpan(Color.RED), 0, strBuild.length(), 0);
        builder.append(strColor);
        builder.append(" ");
        strBuild = strVar;
        strColor = new SpannableString(strBuild);
        strColor.setSpan(new ForegroundColorSpan(Color.BLACK), 0, strBuild.length(), 0);
        builder.append(strColor);
    }
}