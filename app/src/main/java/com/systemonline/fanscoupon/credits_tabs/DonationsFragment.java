package com.systemonline.fanscoupon.credits_tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.systemonline.fanscoupon.Base.BaseFragment;
import com.systemonline.fanscoupon.R;

public class DonationsFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.cred_tab_donation, null);
    }


}
