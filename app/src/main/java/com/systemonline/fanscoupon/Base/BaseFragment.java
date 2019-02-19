package com.systemonline.fanscoupon.Base;


import android.support.v4.app.Fragment;

import com.systemonline.fanscoupon.Interfaces.IServiceCallBack;

import org.json.JSONTokener;

public abstract class BaseFragment extends Fragment implements IServiceCallBack {

    @Override
    public void PostBackExecutionJSON(JSONTokener Result) {

    }

}
