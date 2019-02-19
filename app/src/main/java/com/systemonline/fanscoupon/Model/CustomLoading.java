package com.systemonline.fanscoupon.Model;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;

import com.systemonline.fanscoupon.R;

public class CustomLoading {
    public static CustomLoading customLoading;
    public static Context m_Context;
    public Dialog m_Dialog;

    public CustomLoading(Context m_Context) {
        this.m_Context = m_Context;
    }

    public static CustomLoading getInstance() {
        if (customLoading == null) {
            customLoading = new CustomLoading(m_Context);
        }
        return customLoading;
    }

    public void showProgress(Context m_Context) {
        m_Dialog = new Dialog(m_Context);
        m_Dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        m_Dialog.setContentView(R.layout.custom_progress_layout);
        m_Dialog.findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
        m_Dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        m_Dialog.setCancelable(true);
        m_Dialog.setCanceledOnTouchOutside(true);
        m_Dialog.show();
    }

    public void hideProgress() {
        if (m_Dialog != null) {
            m_Dialog.dismiss();
            m_Dialog = null;
        }
    }
}