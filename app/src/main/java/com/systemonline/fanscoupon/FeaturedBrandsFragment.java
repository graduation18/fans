package com.systemonline.fanscoupon;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.systemonline.fanscoupon.Base.BaseFragment;

public class FeaturedBrandsFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.featured_brands_fragment, null);
        return rootView;
    }

}
