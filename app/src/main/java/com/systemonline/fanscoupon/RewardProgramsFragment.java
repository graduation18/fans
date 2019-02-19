package com.systemonline.fanscoupon;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.systemonline.fanscoupon.Base.BaseFragment;
import com.systemonline.fanscoupon.coupon_tabs.CouponsFilterFragment;

public class RewardProgramsFragment extends BaseFragment {

    Button btnFilter;

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.reward_programs_fragment, null);

        btnFilter = (Button) rootView.findViewById(R.id.btn_filter);

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragment = new CouponsFilterFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment).commit();
                fragmentManager.beginTransaction().remove(fragment);

            }
        });

        return rootView;
    }

}