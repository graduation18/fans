package com.systemonline.fanscoupon.credits_tabs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.systemonline.fanscoupon.Base.BaseFragment;
import com.systemonline.fanscoupon.Helpers.Utility;
import com.systemonline.fanscoupon.R;

public class CreditBrandSortFragment extends BaseFragment {

    RadioButton radbtn_fanCount, radbtn_starsRating, radbtn_couponsCount, radbtn_default;
    Utility _utility;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.credit_brand_sort_fragment, null);

        _utility = new Utility(getContext());

        radbtn_fanCount = (RadioButton) rootView.findViewById(R.id.radbtn_fanCount);
        radbtn_starsRating = (RadioButton) rootView.findViewById(R.id.radbtn_starRating);
        radbtn_couponsCount = (RadioButton) rootView.findViewById(R.id.radbtn_couponsCount);
        radbtn_default = (RadioButton) rootView.findViewById(R.id.radbtn_default);

        switch (MyCreditsFragment.brandSortState) {
            case 0:
                radbtn_default.setChecked(true);
                break;
            case 1:
                radbtn_fanCount.setChecked(true);
                break;
            case 2:
                radbtn_starsRating.setChecked(true);
                break;
            case 3:
                radbtn_couponsCount.setChecked(true);
                break;
            default:
                break;
        }


        radbtn_fanCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyCreditsFragment.brandSortState != 1) {
                    Log.e("fans count", "brand sort");
                    MyCreditsFragment.brandSortState = 1;
                    MyCreditsFragment.brandsLimit = 4;
                    MyCreditsFragment.credit = null;
                    getActivity().onBackPressed();
                }
            }
        });

        radbtn_starsRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyCreditsFragment.brandSortState != 2) {
                    Log.e("stars rating", "brand sort");
                    MyCreditsFragment.brandSortState = 2;
                    MyCreditsFragment.brandsLimit = 4;
                    MyCreditsFragment.credit = null;
                    getActivity().onBackPressed();
                }
            }
        });

        radbtn_couponsCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyCreditsFragment.brandSortState != 3) {
                    Log.e("coupons count", "brand sort");
                    MyCreditsFragment.brandSortState = 3;
                    MyCreditsFragment.brandsLimit = 4;
                    MyCreditsFragment.credit = null;
                    getActivity().onBackPressed();
                }
            }
        });
        radbtn_default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyCreditsFragment.brandSortState != 0) {
                    Log.e("default", "brand sort");
                    MyCreditsFragment.brandSortState = 0;
                    MyCreditsFragment.brandsLimit = 4;
                    MyCreditsFragment.credit = null;
                    getActivity().onBackPressed();
                }
            }
        });

        return rootView;
    }

}