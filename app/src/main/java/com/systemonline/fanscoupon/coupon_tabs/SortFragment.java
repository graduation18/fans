package com.systemonline.fanscoupon.coupon_tabs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.systemonline.fanscoupon.Base.BaseFragment;
import com.systemonline.fanscoupon.Helpers.Utility;
import com.systemonline.fanscoupon.R;

public class SortFragment extends BaseFragment {

    RadioButton radbtn_newest, radbtn_oldest, radbtn_most_popular;
    Utility _utility;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.sort_fragment, null);

        _utility = new Utility(getContext());

        radbtn_newest = (RadioButton) rootView.findViewById(R.id.radbtn_newest);
        radbtn_oldest = (RadioButton) rootView.findViewById(R.id.radbtn_oldest);
        radbtn_most_popular = (RadioButton) rootView.findViewById(R.id.radbtn_most_popular);

        switch (CouponTab.sortState) {
            case 1:
                radbtn_newest.setChecked(true);
                break;
            case 2:
                radbtn_oldest.setChecked(true);
                break;
            case 3:
                radbtn_most_popular.setChecked(true);
                break;
            default:
                break;
        }


        radbtn_newest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CouponTab.sortState != 1) {
                    Log.e("new", "a");
//                    Collections.sort(MainActivity.allCoupons);

                    CouponTab.sortState = 1;
//                    CouponTab.allCoupons = null;
                    CouponTab.allCoupons2 = null;
//                    CouponTab.forYou = null;
                    CouponTab.forYou2 = null;
                    CouponTab.couponPage = 1;
                    CouponTab.couponPageOnlyYou = 1;
                    getActivity().onBackPressed();
                }
            }
        });

        radbtn_oldest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CouponTab.sortState != 2) {
//                    ArrayList<Coupon> sortedCop = new ArrayList<Coupon>(MainActivity.allCoupons.size());
//
////                    for (int k = 0; k < sortedCop.size(); k++) {
//                    for (int i = 0; i < MainActivity.allCoupons.size(); i++)
//                    {
//                        for (int j = 0; j < MainActivity.allCoupons.size(); j++)
//                        {
//                            if (_utility.ConvertToDate(MainActivity.allCoupons.get(i).getCouponStartDate())
//                                    .before(_utility.ConvertToDate(MainActivity.allCoupons.get(j).getCouponStartDate()))) {
//                                sortedCop.add(MainActivity.allCoupons.get(i));
//
//                            }
//                        }
//                    }
//                    }
                    Log.e("old", "a");

//                    Collections.sort(MainActivity.allCoupons , Collections.reverseOrder());


                    CouponTab.sortState = 2;
//                    CouponTab.allCoupons = null;
                    CouponTab.allCoupons2 = null;
//                    CouponTab.forYou = null;
                    CouponTab.forYou2 = null;
                    CouponTab.couponPage = 1;
                    CouponTab.couponPageOnlyYou = 1;
                    getActivity().onBackPressed();
                }
            }
        });

        radbtn_most_popular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CouponTab.sortState != 3) {
                    Log.e("most", "a");

                    CouponTab.sortState = 3;
//                    CouponTab.allCoupons = null;
                    CouponTab.allCoupons2 = null;
//                    CouponTab.forYou = null;
                    CouponTab.forYou2 = null;
                    CouponTab.couponPage = 1;
                    CouponTab.couponPageOnlyYou = 1;
                    getActivity().onBackPressed();
                }
            }
        });

        return rootView;
    }

}