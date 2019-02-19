package com.systemonline.fanscoupon;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.systemonline.fanscoupon.Base.BaseFragment;
import com.systemonline.fanscoupon.Helpers.Const;
import com.systemonline.fanscoupon.Helpers.Utility;
import com.systemonline.fanscoupon.Model.CustomLoading;
import com.systemonline.fanscoupon.Model.WhoDare;
import com.systemonline.fanscoupon.WebServices.JSONAsync;
import com.systemonline.fanscoupon.WebServices.JSONWebServices;
import com.systemonline.fanscoupon.WebServices.ParseData;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

public class WhoDareFragment extends BaseFragment {


    TextView missionDesc, back, reviewVideo;
    Utility _utility;
    EditText videoURL;
    TextView brandName, win_all, ch_all;
    JSONAsync call;
    private String requestType;
    private WhoDare whoDare;
    private ImageView brandImage;
    private CustomLoading customLoading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.who_dare, null);
        _utility = new Utility(getContext());
        customLoading = new CustomLoading(_utility.getCurrentActivity());
        brandImage = (ImageView) rootView.findViewById(R.id.imgv_brand);

        brandName = (TextView) rootView.findViewById(R.id.tv_brand);
        win_all = (TextView) rootView.findViewById(R.id.win_all);
        ch_all = (TextView) rootView.findViewById(R.id.ch_all);

        missionDesc = (TextView) rootView.findViewById(R.id.missionDesc);
        reviewVideo = (TextView) rootView.findViewById(R.id.reviewVideo);
        back = (TextView) rootView.findViewById(R.id.tv_review);
        videoURL = (EditText) rootView.findViewById(R.id.et_textAnswer);
        reviewVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!videoURL.getText().toString().isEmpty())
                    sendWhoDareData(videoURL.getText().toString());
                else
                    _utility.showMessage(getResources().getString(R.string.ins_vid_url));
            }
        });
        reviewVideo.setVisibility(View.GONE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!videoURL.getText().toString().isEmpty())
                    sendWhoDareData(videoURL.getText().toString());
                else
                    _utility.showMessage(getResources().getString(R.string.ins_vid_url));
            }
        });

        if (_utility.isConnectingToInternet_ping())
            getWhoDareData();
        else
            _utility.showMessage(getActivity().getResources().getString(R.string.no_net));
        return rootView;
    }


    private void getWhoDareData() {
        if (!_utility.isConnectingToInternet_ping()) {
            customLoading.hideProgress();
            MainActivity._utility.showMessage(getResources().getString(R.string.no_net));
            return;
        }
        requestType = "getRequest";

        customLoading.showProgress(_utility.getCurrentActivity());
        JSONWebServices service = new JSONWebServices(WhoDareFragment.this);
        call = service.getWhoDareRequest(null);
    }

    private void sendWhoDareData(String videoURL) {
        if (!_utility.isConnectingToInternet_ping()) {
            customLoading.hideProgress();
            MainActivity._utility.showMessage(getResources().getString(R.string.no_net));
            return;
        }

        requestType = "sendRequest";

        customLoading.showProgress(_utility.getCurrentActivity());
        JSONWebServices service = new JSONWebServices(WhoDareFragment.this);
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("who_dare_id", String.valueOf(whoDare.getMissionID())));
        nameValuePairs.add(new BasicNameValuePair("user_url", videoURL));
        call = service.sendWhoDareRequest(nameValuePairs);
    }


    @Override
    public void PostBackExecutionJSON(JSONTokener Result) {
        try {
            call = null;
            if (requestType.equals("sendRequest")) {
                Log.e("who dare resp", Result.toString());
                if (ParseData.parseActionsResult(Result)) {
                    _utility.showMessage(getResources().getString(R.string.success));
                } else
                    _utility.showMessage(getResources().getString(R.string.error));
            } else {
                whoDare = ParseData.parseWhoDare(Result);
                if (whoDare != null) {
                    Picasso.with(getContext()).load(Const.imagesURL + "brands/50x50/" + whoDare.getBrandLogo()).placeholder(R.drawable.ph_brand).into(brandImage);

                    brandName.setText(whoDare.getBrandName());

                    win_all.setText(_utility.colorString(R.string.win, R.color.red, whoDare.getCouponName(), R.color.black), TextView.BufferType.SPANNABLE);

                    ch_all.setText(_utility.colorString(R.string.challenge, R.color.red, whoDare.getMissionName(), R.color.black), TextView.BufferType.SPANNABLE);

                    missionDesc.setText(whoDare.getMissionDesc());
                } else {
                    _utility.showMessage(getResources().getString(R.string.col_win_ws_err));
                    customLoading.hideProgress();
                    getActivity().onBackPressed();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(_utility.GetAppContext(), "Error who dare", Toast.LENGTH_LONG).show();
        } finally {
            customLoading.hideProgress();
        }
    }
}