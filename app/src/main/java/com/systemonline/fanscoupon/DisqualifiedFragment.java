package com.systemonline.fanscoupon;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.systemonline.fanscoupon.Base.BaseFragment;
import com.systemonline.fanscoupon.Helpers.Utility;
import com.systemonline.fanscoupon.coupon_tabs.CouponTab;

public class DisqualifiedFragment extends BaseFragment {


    Utility _utility;
    TextView win_all, agree;
    ListView exLvCopCond;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.disqualified_fragment, null);

        _utility = new Utility(getContext());
        win_all = (TextView) rootView.findViewById(R.id.win_all);

        win_all.setText(_utility.colorString(R.string.win, R.color.red, CouponTab.addToWalletSelectedCoupName, R.color.black), TextView.BufferType.SPANNABLE);

        exLvCopCond = (ListView) rootView.findViewById(R.id.listView_couponCondition);
        CouponConditionsAdapter couponConditionsAdapter = new CouponConditionsAdapter(getContext(), CouponTab.couponQualification.getDisqualificationReasons());
        exLvCopCond.setAdapter(couponConditionsAdapter);
        _utility.setListViewHeightBasedOnChildren(exLvCopCond);

//        exLvCopCond.setExpanded(true);

        agree = (TextView) rootView.findViewById(R.id.tv_agree);
        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        ImageView coupIcon = (ImageView) rootView.findViewById(R.id.icon_type);

        switch (CouponTab.selectedCopTypeID) {
            case 93:
                coupIcon.setImageResource(R.drawable.voucher_cyan);
                break;
            case 95:
                coupIcon.setImageResource(R.drawable.voucher_green);

                break;
            case 232:
                coupIcon.setImageResource(R.drawable.win);

                break;
            case 238:
                coupIcon.setImageResource(R.drawable.voucher_blue);
                break;
            case 240:
                coupIcon.setImageResource(R.drawable.voucher_pink);
                break;
            case 242:
                coupIcon.setImageResource(R.drawable.voucher_orange);
                break;

            default:
                break;
        }

        return rootView;
    }

//
//    @Override
//    public void PostBackExecutionJSON(JSONTokener Result) {
//        try {
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(_utility.GetAppContext(), "Error survey", Toast.LENGTH_LONG).show();
//
//        } finally {
//            _utility.HideDialog();
//        }
//    }


}