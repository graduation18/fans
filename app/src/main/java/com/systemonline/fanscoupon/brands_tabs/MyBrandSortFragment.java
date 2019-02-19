package com.systemonline.fanscoupon.brands_tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.systemonline.fanscoupon.Base.BaseFragment;
import com.systemonline.fanscoupon.Helpers.Utility;
import com.systemonline.fanscoupon.R;

public class MyBrandSortFragment extends BaseFragment {

    RadioButton radbtn_fanCount, radbtn_starsRating, radbtn_couponsCount, radbtn_default;
    Utility _utility;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.my_brand_sort_fragment, null);

        _utility = new Utility(getContext());

        radbtn_fanCount = (RadioButton) rootView.findViewById(R.id.radbtn_fanCount);
        radbtn_starsRating = (RadioButton) rootView.findViewById(R.id.radbtn_starRating);
        radbtn_couponsCount = (RadioButton) rootView.findViewById(R.id.radbtn_couponsCount);
        radbtn_default = (RadioButton) rootView.findViewById(R.id.radbtn_default);

        switch (BrandsTab.myBrandSortState) {
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
                if (BrandsTab.myBrandSortState != 1) {
                    BrandsTab.myBrandSortState = 1;
                    BrandsTab.myBrands = null;
                    getActivity().onBackPressed();
                }
            }
        });

        radbtn_starsRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BrandsTab.myBrandSortState != 2) {
                    BrandsTab.myBrandSortState = 2;
                    BrandsTab.myBrands = null;
                    getActivity().onBackPressed();
                }
            }
        });

        radbtn_couponsCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BrandsTab.myBrandSortState != 3) {
                    BrandsTab.myBrandSortState = 3;
                    BrandsTab.myBrands = null;
                    getActivity().onBackPressed();
                }
            }
        });
        radbtn_default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BrandsTab.myBrandSortState != 0) {
                    BrandsTab.myBrandSortState = 0;
                    BrandsTab.myBrands = null;
                    getActivity().onBackPressed();
                }
            }
        });

        return rootView;
    }

}