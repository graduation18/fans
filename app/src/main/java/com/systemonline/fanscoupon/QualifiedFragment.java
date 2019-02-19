package com.systemonline.fanscoupon;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.systemonline.fanscoupon.Base.BaseFragment;
import com.systemonline.fanscoupon.Helpers.Const;
import com.systemonline.fanscoupon.Helpers.Utility;
import com.systemonline.fanscoupon.Model.CustomLoading;
import com.systemonline.fanscoupon.WebServices.JSONAsync;
import com.systemonline.fanscoupon.WebServices.JSONWebServices;
import com.systemonline.fanscoupon.WebServices.ParseData;
import com.systemonline.fanscoupon.coupon_tabs.CouponTab;

import org.json.JSONTokener;

import java.util.ArrayList;

public class QualifiedFragment extends BaseFragment {

    SpannableStringBuilder builder, builderTemp;
    String strBuild;
    SpannableString strColor;


    Utility _utility;
    TextView tv_1, tv_2, tv_3, tv_4, win_all, qualifiedText, qrText, shouldRead;//coupValidTill,maxFans,maxTimesUsePerDay,maxTimesUse,
    LinearLayout addToFav;
    RelativeLayout rel_cop_type;
    JSONAsync call;
    private String status = "success";
    private boolean inWallet;
    private String requestType;
    private ListView branchesLV, otherInstructionsLv;
    private ImageView qrImage;
    private CustomLoading customLoading;

    /**
     * encode text to qr image
     *
     * @param text
     * @return
     */
    public static Bitmap encodeToQrCode(String text) {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = null;
        try {
            matrix = writer.encode(text, BarcodeFormat.QR_CODE, Const.qr_width, Const.qr_height);
        } catch (WriterException ex) {
            ex.printStackTrace();
        }

        Bitmap bmp = Bitmap.createBitmap(Const.qr_width, Const.qr_height, Bitmap.Config.RGB_565);
        for (int x = 0; x < Const.qr_width; x++) {
            for (int y = 0; y < Const.qr_height; y++) {
                bmp.setPixel(x, y, matrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bmp;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.qualified_fragment, null);
        _utility = new Utility(getContext());
        customLoading = new CustomLoading(_utility.getCurrentActivity());
        win_all = (TextView) rootView.findViewById(R.id.win_all);
//        coupValidTill = (TextView) rootView.findViewById(R.id.coupon_valid_till);
//        maxFans = (TextView) rootView.findViewById(R.id.max_fans_use_coupon);
//        maxTimesUse = (TextView) rootView.findViewById(R.id.max_times_use_coupon);
//        maxTimesUsePerDay = (TextView) rootView.findViewById(R.id.max_times_use_coupon_perDay);
        qualifiedText = (TextView) rootView.findViewById(R.id.qualifiedText);
        qrText = (TextView) rootView.findViewById(R.id.qrText);
        shouldRead = (TextView) rootView.findViewById(R.id.youShouldRead);
        branchesLV = (ListView) rootView.findViewById(R.id.listView_branches);
        otherInstructionsLv = (ListView) rootView.findViewById(R.id.listView_otherInstructions);
        ImageView heart = (ImageView) rootView.findViewById(R.id.img_add_to_fav_s);
        qrImage = (ImageView) rootView.findViewById(R.id.img_qr);
        TextView buttonText = (TextView) rootView.findViewById(R.id.tv_ok);
        rel_cop_type = (RelativeLayout) rootView.findViewById(R.id.rel_cop_type);
        tv_1 = (TextView) rootView.findViewById(R.id.tv_1);
        tv_2 = (TextView) rootView.findViewById(R.id.tv_2);
        tv_3 = (TextView) rootView.findViewById(R.id.tv_3);
        tv_4 = (TextView) rootView.findViewById(R.id.tv_4);

        if (CouponTab.couponQualification == null) {
            inWallet = true;
            getCouponInfo();
            heart.setVisibility(View.GONE);
            qualifiedText.setVisibility(View.GONE);
            shouldRead.setVisibility(View.GONE);
            buttonText.setText(getResources().getString(R.string.ok));
        } else {
            inWallet = false;
            setCouponData();
        }


        addToFav = (LinearLayout) rootView.findViewById(R.id.lin_ok);
        addToFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inWallet) {
                    getActivity().onBackPressed();
                } else {
                    if (_utility.isConnectingToInternet_ping()) {
                        addToWallet();
                    } else {
                        _utility.showMessage(getContext().getResources().getString(R.string.no_net));
                    }
                }

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

    private void setCouponData() {
        stringBuilder(R.string.cop_valid_till, CouponTab.couponQualification.getCouponEndDate());
        tv_1.setText(builder, TextView.BufferType.SPANNABLE);

        builderTemp = new SpannableStringBuilder();
        stringBuilder(R.string.cop_valid_for, String.format("%d", CouponTab.couponQualification.getMaxFansUse()));
        builderTemp.append(builder);
        builderTemp.append(" ");
        stringBuilder(R.string.fans, "");
        builderTemp.append(builder);
        tv_2.setText(builderTemp, TextView.BufferType.SPANNABLE);

        builderTemp = new SpannableStringBuilder();
        stringBuilder(R.string.use_cop, String.format("%d", CouponTab.couponQualification.getMaxUsePerFan()));
        builderTemp.append(builder);
        builderTemp.append(" ");
        stringBuilder(R.string.times, "");
        builderTemp.append(builder);
        tv_3.setText(builderTemp, TextView.BufferType.SPANNABLE);

        builderTemp = new SpannableStringBuilder();
        stringBuilder(R.string.use_cop, String.format("%d", CouponTab.couponQualification.getMaxUsePerFanPerDay()));
        builderTemp.append(builder);
        builderTemp.append(" ");
        stringBuilder(R.string.a_day, "");
        builderTemp.append(builder);
        tv_4.setText(builderTemp, TextView.BufferType.SPANNABLE);


        win_all.setText(_utility.colorString(R.string.win, R.color.red, CouponTab.couponQualification.getCouponName(), R.color.black), TextView.BufferType.SPANNABLE);

        BranchesAvailabilityAdapter branchesAvailabilityAdapter = new BranchesAvailabilityAdapter(getContext(), CouponTab.couponQualification.getCouponBrand().getBrandBranches());
        branchesLV.setAdapter(branchesAvailabilityAdapter);
        _utility.setListViewHeightBasedOnChildren(branchesLV);

        if (CouponTab.couponQualification.getOtherInstruction() != null && !CouponTab.couponQualification.getOtherInstruction().isEmpty()) {
            rel_cop_type.setVisibility(View.VISIBLE);
            CouponDaysAdapter couponDaysAdapter = new CouponDaysAdapter(getContext(), CouponTab.couponQualification.getOtherInstruction());
            otherInstructionsLv.setAdapter(couponDaysAdapter);
            _utility.setListViewHeightBasedOnChildren(otherInstructionsLv);
        } else {
            rel_cop_type.setVisibility(View.GONE);
        }
    }

    private void getCouponInfo() {
        if (!_utility.isConnectingToInternet_ping()) {
            customLoading.hideProgress();
            MainActivity._utility.showMessage(getResources().getString(R.string.no_net));
            return;
        }
        requestType = "getCouponInfo";

        customLoading.showProgress(_utility.getCurrentActivity());

        JSONWebServices service = new JSONWebServices(QualifiedFragment.this);
        call = service.myCouponInfoFragment(null, CouponTab.coupSlugTemp);
    }

    private void addToWallet() {
        if (!_utility.isConnectingToInternet_ping()) {
            customLoading.hideProgress();
            MainActivity._utility.showMessage(getResources().getString(R.string.no_net));
            return;
        }
        requestType = "addToWallet";
        customLoading.showProgress(_utility.getCurrentActivity());
        JSONWebServices service = new JSONWebServices(QualifiedFragment.this);
        call = service.addToWalletFragment(null, CouponTab.coupSlugTemp);
    }

    @Override
    public void PostBackExecutionJSON(JSONTokener Result) {
        try {
            call = null;
            if (requestType.equals("addToWallet")) {
                ArrayList<String> result = ParseData.parseEventResponse(Result);
                if (result != null) {
                    if (!result.get(0).equals("success")) {
                        status = "";
                    }
                    showDialog();
                } else {
                    customLoading.hideProgress();
                    _utility.showMessage(getContext().getResources().getString(R.string.ws_err));
                }
            } else {
                CouponTab.couponQualification = ParseData.parseQualifiedCouponInfo(Result);
                if (CouponTab.couponQualification == null) {
                    _utility.showMessage(getContext().getResources().getString(R.string.ws_err));
                    return;
                }
                setCouponData();

                String qr = Integer.toString(MainActivity.currentFan.getFanID() + 10, 32) + "&" +
                        Integer.toString(CouponTab.couponQualification.getCouponID() + 10, 32);
                Log.e("Qualified qr value", qr);
                qrImage.setImageBitmap(encodeToQrCode(qr));
                qrText.setVisibility(View.VISIBLE);
                qrText.setText(qr);
            }


        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(_utility.GetAppContext(), "Error Qualified fragment", Toast.LENGTH_LONG).show();

        } finally {
            customLoading.hideProgress();
        }
    }

    void showDialog() {
        Log.e("qualified show dialog", "begin transaction");
        customLoading.hideProgress();
        AlertDialog.Builder build = new AlertDialog.Builder(getContext());

        if (status.equals("")) {

            build.setMessage(getContext().getResources().getString(R.string.error))
                    .setCancelable(false)
                    .setPositiveButton(getContext().getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            getActivity().onBackPressed();
                        }
                    });
        } else {
            build.setMessage(getContext().getResources().getString(R.string.cop_added))
                    .setCancelable(false)
                    .setPositiveButton(getContext().getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            getActivity().onBackPressed();
                            _utility.updateCoupons("wallet");
                        }
                    });
        }
        AlertDialog alert = build.create();
        alert.show();
    }

    void stringBuilder(int strId, String strVar) {
        builder = new SpannableStringBuilder();
        strBuild = getResources().getString(strId);
        strColor = new SpannableString(strBuild);
        strColor.setSpan(new ForegroundColorSpan(Color.BLACK), 0, strBuild.length(), 0);
        builder.append(strColor);
        builder.append(" ");
        strBuild = strVar;
        strColor = new SpannableString(strBuild);
        strColor.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.red)), 0, strBuild.length(), 0);
        builder.append(strColor);
    }

}