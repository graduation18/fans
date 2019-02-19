package com.systemonline.fanscoupon;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.picasso.Picasso;
import com.systemonline.fanscoupon.Base.BaseFragment;
import com.systemonline.fanscoupon.Helpers.Const;
import com.systemonline.fanscoupon.Helpers.Utility;
import com.systemonline.fanscoupon.Model.CollectAndWin;
import com.systemonline.fanscoupon.Model.CustomLoading;
import com.systemonline.fanscoupon.WebServices.JSONAsync;
import com.systemonline.fanscoupon.WebServices.JSONWebServices;
import com.systemonline.fanscoupon.WebServices.ParseData;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

public class CollectAndWinFragment extends BaseFragment {


    TextView scannedQR, totalQRNumber, backToHome;
    Button scanButton;
    Utility _utility;
    TextView brandName, win_all, ch_all;
    JSONAsync call;
    private String requestType;
    private CollectAndWin collectAndWin;
    private ImageView brandImage;
    private CustomLoading customLoading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.collect_win, null);
        _utility = new Utility(getContext());
        customLoading = new CustomLoading(_utility.getCurrentActivity());

        brandImage = (ImageView) rootView.findViewById(R.id.imgv_brand);

        scannedQR = (TextView) rootView.findViewById(R.id.tv_scannedQR);
        totalQRNumber = (TextView) rootView.findViewById(R.id.tv_totalNumberQR);
        backToHome = (TextView) rootView.findViewById(R.id.tv_backToHome);

        ch_all = (TextView) rootView.findViewById(R.id.ch_all);
        win_all = (TextView) rootView.findViewById(R.id.win_all);

        brandName = (TextView) rootView.findViewById(R.id.tv_brand);
        backToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        scanButton = (Button) rootView.findViewById(R.id.btn_scan);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanFromFragment();
            }
        });
        if (_utility.isConnectingToInternet_ping())
            getCollectAndWinData();
        else
            _utility.showMessage(getActivity().getResources().getString(R.string.no_net));
        return rootView;
    }


    private void getCollectAndWinData() {
        requestType = "getRequest";

//        _utility.ShowDialog(getResources().getString(R.string.load), true);
        customLoading.showProgress(_utility.getCurrentActivity());
        JSONWebServices service = new JSONWebServices(CollectAndWinFragment.this);
        call = service.getCollectAndWinRequest(null);
    }

    private void sendCollectAndWinData(String scannedCode) {
        requestType = "sendRequest";

//        _utility.ShowDialog(getResources().getString(R.string.load), true);
        customLoading.showProgress(_utility.getCurrentActivity());
        JSONWebServices service = new JSONWebServices(CollectAndWinFragment.this);
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("punch_card_id", String.valueOf(collectAndWin.getPunchCardID())));
        nameValuePairs.add(new BasicNameValuePair("punch_card_code", scannedCode));
        call = service.sendCollectAndWinRequest(nameValuePairs);
    }

    public void scanFromFragment() {
        IntentIntegrator.forSupportFragment(this)
                .setPrompt(getResources().getString(R.string.scan_qr_code))
                .setCameraId(0)
                .setBeepEnabled(true)
                .initiateScan();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
//                toast = "Cancelled from fragment";
            } else {
                _utility.showMessage(result.getContents());
//                toast = "Scanned from fragment: " + result.getContents();
                sendCollectAndWinData(result.getContents());
            }

        }
    }

    @Override
    public void PostBackExecutionJSON(JSONTokener Result) {
        try {
            call = null;
            if (requestType.equals("sendRequest")) {
                Log.e("collect and win resp", Result.toString());
                if (ParseData.parseActionsResult(Result)) {
                    _utility.showMessage(getResources().getString(R.string.truee));
                    scannedQR.setText(String.valueOf(Integer.valueOf(scannedQR.getText().toString()) + 1));
                    if (Integer.parseInt(scannedQR.getText().toString()) == Integer.parseInt(totalQRNumber.getText().toString())) {
                        customLoading.hideProgress();
                        _utility.updateCoupons("mission");
                        getActivity().onBackPressed();
                        _utility.showHover("pass");
                    }
                } else
                    _utility.showMessage(getResources().getString(R.string.falsee));
            } else {
                collectAndWin = ParseData.parseCollectAndWin(Result);
                if (collectAndWin != null) {
                    Picasso.with(getContext()).load(Const.imagesURL + "brands/50x50/" + collectAndWin.getBrandLogo()).placeholder(R.drawable.ph_brand).into(brandImage);

                    scannedQR.setText(String.valueOf(collectAndWin.getScannedQRCodes()));
                    totalQRNumber.setText(String.valueOf(collectAndWin.getTotalQRCodes()));
                    brandName.setText(collectAndWin.getBrandName());

                    win_all.setText(_utility.colorString(R.string.win, R.color.red, collectAndWin.getCouponName(), R.color.black), TextView.BufferType.SPANNABLE);

                    ch_all.setText(_utility.colorString(R.string.challenge, R.color.red, collectAndWin.getCodeName(), R.color.black), TextView.BufferType.SPANNABLE);

                } else {
                    _utility.showMessage(getResources().getString(R.string.col_win_ws_err));
                    customLoading.hideProgress();
                    getActivity().onBackPressed();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(_utility.GetAppContext(), getString(R.string.ws_err), Toast.LENGTH_LONG).show();

        } finally {
            customLoading.hideProgress();
        }
    }
}