package com.systemonline.fanscoupon;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.systemonline.fanscoupon.Base.BaseFragment;
import com.systemonline.fanscoupon.Helpers.Const;
import com.systemonline.fanscoupon.Helpers.Utility;
import com.systemonline.fanscoupon.Model.CustomLoading;
import com.systemonline.fanscoupon.Model.Interaction;
import com.systemonline.fanscoupon.WebServices.JSONAsync;
import com.systemonline.fanscoupon.WebServices.JSONWebServices;
import com.systemonline.fanscoupon.WebServices.ParseData;
import com.systemonline.fanscoupon.coupon_tabs.CouponTab;

import org.json.JSONTokener;

import java.util.ArrayList;

public class InteractionsFragment extends BaseFragment {


    Utility _utility;
    ArrayList<ArrayList<Interaction>> interactions;
    ListView facebookInteractionsList, twitterInteractionsList, youtubeInteractionsList, instgramInteractionsList;
    TextView brandName, win_all, ch_all;
    JSONAsync call;
    RelativeLayout rel_facebook, rel_twitter, rel_youtube, rel_instgram;
    private ImageView brandImage;
    private CustomLoading customLoading;
    private TextView doneBtn;
    private String requestType = "", coupSlug;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.social_media_tasks, null);
        _utility = new Utility(getContext());


        customLoading = new CustomLoading(_utility.getCurrentActivity());
        rel_facebook = (RelativeLayout) rootView.findViewById(R.id.rel_facebook);
        rel_twitter = (RelativeLayout) rootView.findViewById(R.id.rel_twitter);
        rel_youtube = (RelativeLayout) rootView.findViewById(R.id.rel_youtube);
        rel_instgram = (RelativeLayout) rootView.findViewById(R.id.rel_instgram);

        brandImage = (ImageView) rootView.findViewById(R.id.imgv_brand);
        brandName = (TextView) rootView.findViewById(R.id.tv_brand);
        win_all = (TextView) rootView.findViewById(R.id.win_all);
        ch_all = (TextView) rootView.findViewById(R.id.ch_all);
        facebookInteractionsList = (ListView) rootView.findViewById(R.id.listView_socialActivities_facebook);
        twitterInteractionsList = (ListView) rootView.findViewById(R.id.listView_socialActivities_twitter);
        youtubeInteractionsList = (ListView) rootView.findViewById(R.id.listView_socialActivities_youtube);
        instgramInteractionsList = (ListView) rootView.findViewById(R.id.listView_socialActivities_instgram);
        doneBtn = (TextView) rootView.findViewById(R.id.tv_backToHome);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (doneBtn.getText().toString().equals(_utility.getCurrentActivity().getString(R.string.done)))
                    getInteractions();//done btn
                else
                    addToWallet();//done btn
            }
        });

        coupSlug = CouponTab.coupSlugTemp;
        getInteractions();// awl d5la
        return rootView;
    }


    private void getInteractions() {
        if (!_utility.isConnectingToInternet_ping()) {
            customLoading.hideProgress();
            MainActivity._utility.showMessage(getResources().getString(R.string.no_net));
            return;
        }
        customLoading.showProgress(_utility.getCurrentActivity());
        requestType = "getInteractions";
        JSONWebServices service = new JSONWebServices(InteractionsFragment.this);
        call = service.interactionsRequest(null, coupSlug);
    }

    private void addToWallet() {
        if (!_utility.isConnectingToInternet_ping()) {
            customLoading.hideProgress();
            MainActivity._utility.showMessage(getResources().getString(R.string.no_net));
            return;
        }
        customLoading.showProgress(_utility.getCurrentActivity());
        requestType = "sendRequest";
        JSONWebServices service = new JSONWebServices(InteractionsFragment.this);
        call = service.qualifiedToCouponRequestFragment(null, coupSlug);
    }

    @Override
    public void PostBackExecutionJSON(JSONTokener Result) {
        Log.e("interactions response", Result.toString());
        try {

            call = null;
            if (requestType.equals("sendRequest")) {
                CouponTab.couponQualification = ParseData.parseQualifiedFragment(Result);
//
                customLoading.hideProgress();
                if (CouponTab.couponQualification == null) {
                    _utility.showMessage(getContext().getResources().getString(R.string.ws_err));
                    return;
                }
                Fragment fragment;

                if (CouponTab.couponQualification.isQualifiedUser()) {
                    fragment = new QualifiedFragment();
                } else {
                    fragment = new DisqualifiedFragment();
                }

                FragmentManager fragmentManager = ((FragmentActivity) _utility.getCurrentActivity()).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            } else {
                interactions = ParseData.parseInteractions(Result);
                if (interactions != null) {
                    int availableEvents;
                    if (!interactions.get(0).isEmpty())
                        availableEvents = 0;
                    else if (!interactions.get(1).isEmpty())
                        availableEvents = 1;
                    else if (!interactions.get(2).isEmpty())
                        availableEvents = 2;
                    else
                        availableEvents = 3;

                    Picasso.with(getContext()).load(Const.imagesURL + "brands/50x50/" + interactions.get(availableEvents).get(0).getBrandLogo()).placeholder(R.drawable.ph_brand).into(brandImage);

                    brandName.setText(interactions.get(availableEvents).get(0).getBrandName());

                    win_all.setText(_utility.colorString(R.string.win, R.color.red, interactions.get(availableEvents).get(0).getCouponName(), R.color.black)
                            , TextView.BufferType.SPANNABLE);

                    ch_all.setText(_utility.colorString(R.string.challenge, R.color.red, interactions.get(availableEvents).get(0).getMissionName(), R.color.black)
                            , TextView.BufferType.SPANNABLE);


                    if (interactions.get(0).isEmpty())
                        rel_facebook.setVisibility(View.GONE);
                    else {
                        if (!interactions.get(0).get(0).isAllowed()) {
                            customLoading.hideProgress();
                            _utility.fbLoginDialogBox();
                            return;
                        }

                        InteractionsAdapter facebookInteractionsAdapter = new InteractionsAdapter(getContext(), interactions.get(0), 0);
                        facebookInteractionsList.setAdapter(facebookInteractionsAdapter);
                        _utility.setListViewHeightBasedOnChildren(facebookInteractionsList);
                    }

                    if (interactions.get(1).isEmpty())
                        rel_twitter.setVisibility(View.GONE);
                    else {
                        if (!interactions.get(1).get(0).isAllowed()) {
                            customLoading.hideProgress();
                            _utility.socialAccountDialogBox(_utility.getCurrentActivity().getString(R.string.add_twitter));
                            return;
                        }
                        InteractionsAdapter twitterInteractionsAdapter = new InteractionsAdapter(getContext(), interactions.get(1), 1);
                        twitterInteractionsList.setAdapter(twitterInteractionsAdapter);
                        _utility.setListViewHeightBasedOnChildren(twitterInteractionsList);
                    }

                    if (interactions.get(2).isEmpty())
                        rel_youtube.setVisibility(View.GONE);
                    else {
                        if (!interactions.get(2).get(0).isAllowed()) {
                            customLoading.hideProgress();
                            _utility.socialAccountDialogBox(_utility.getCurrentActivity().getString(R.string.add_google));
                            return;
                        }
                        InteractionsAdapter youtubeInteractionsAdapter = new InteractionsAdapter(getContext(), interactions.get(2), 2);
                        youtubeInteractionsList.setAdapter(youtubeInteractionsAdapter);
                        _utility.setListViewHeightBasedOnChildren(youtubeInteractionsList);
                    }

                    if (interactions.get(3).isEmpty())
                        rel_instgram.setVisibility(View.GONE);
                    else {
                        if (!interactions.get(3).get(0).isAllowed()) {
                            customLoading.hideProgress();
                            _utility.socialAccountDialogBox(_utility.getCurrentActivity().getString(R.string.add_instagram));
                            return;
                        }
                        InteractionsAdapter instgramInteractionsAdapter = new InteractionsAdapter(getContext(), interactions.get(3), 3);
                        instgramInteractionsList.setAdapter(instgramInteractionsAdapter);
                        _utility.setListViewHeightBasedOnChildren(instgramInteractionsList);
                    }

                    checkIfChallengeCompleted();
                } else {
                    _utility.showMessage(getResources().getString(R.string.interaction_ws_err));
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

    private void checkIfChallengeCompleted() {
        boolean finished = true;
        //fb
        for (int i = 0; i < interactions.get(0).size(); i++) {
            if (!interactions.get(0).get(i).isDone()) {
                finished = false;
                break;
            }
        }

        //twitter
        for (int i = 0; i < interactions.get(1).size(); i++) {
            if (!interactions.get(1).get(i).isDone()) {
                finished = false;
                break;
            }
        }

        //youtube
        for (int i = 0; i < interactions.get(2).size(); i++) {
            if (!interactions.get(2).get(i).isDone()) {
                finished = false;
                break;
            }
        }

        //instgram
        for (int i = 0; i < interactions.get(3).size(); i++) {
            if (!interactions.get(3).get(i).isDone()) {
                finished = false;
                break;
            }
        }

        if (finished)
            doneBtn.setText(_utility.getCurrentActivity().getString(R.string.add_to_cop));

    }

}