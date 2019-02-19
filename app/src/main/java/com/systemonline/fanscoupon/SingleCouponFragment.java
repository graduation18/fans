package com.systemonline.fanscoupon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.google.android.gms.maps.model.LatLng;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.squareup.picasso.Picasso;
import com.systemonline.fanscoupon.Base.BaseFragment;
import com.systemonline.fanscoupon.Helpers.Const;
import com.systemonline.fanscoupon.Helpers.Utility;
import com.systemonline.fanscoupon.Model.CustomLoading;
import com.systemonline.fanscoupon.WebServices.JSONAsync;
import com.systemonline.fanscoupon.WebServices.JSONWebServices;
import com.systemonline.fanscoupon.WebServices.ParseData;
import com.systemonline.fanscoupon.coupon_tabs.CouponTab;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

public class SingleCouponFragment extends BaseFragment {

    Dialog likeDialog, shareDialog;
    Button btn_comment;
    String status = "";
    BranchesAdapter brAd;
    CommentsAdapter comAd;
    EventRateAdapter evRaAdapter;
    Boolean flagAvailability = true, flagCopType = true, flagEarn = true, flagOffersConditions = true, flagEarnedPoints = true, flagLoyalty = true;
    View line_availabilty, line_cop_type, line_cop_friends, line_alliance;
    Utility _utility;
    LinearLayout lin_cop_type, lin_earn, lin_add_fav_s, brandFriendsLinear, brandAlliancesLinear;//lin_earned_points_data
    RelativeLayout rel_offersConditions, rel_loyalty_for_user, relativeLayout6, rel_earned_points, rel_earn, rel_availability, rel_alliance, rel_cop_friends, rel_cop_type;
    ListView listView_branches, listView_comments, listView_loyalty, lv_earn_point;//listView_offersConditions
    ExpandableHeightListView exLvOffCond;
    ShareButton img_share_s;
    ImageView img_qr, arrow_offersCondition, arrow_loyalty, img_mark, arrow_earned_points, arrow_cop_type, arrow_earn, arrow_availability, imgv_brand, brand_1, brand_2, brand_3, brand_4, img_fav_cop_s, img_cop, s_fan_type;
    TextView tv_brand, tv_add_to_fav, tv_redemption, tv_due_days, tv_comments_count, tv_fav_count_s, no_friends,
            tv_branches_count, tv_date, tv_cop_name, tv_affiliate, tv_prog_name, tv_prog_type, tv_prog_users, no_alliance,
            tv_prog_details, tv_earned_points, tv_expire_days, tv_earned_money, tv_cop_type;//tv_lvl_1, tv_lvl_2,
    View rootView = null, line_8;
    EditText et_comment;
    SpannableStringBuilder builder, builderTemp;
    String strBuild;
    SpannableString strColor;
    TextView earn_1, earn_2, earn_3, earn_4;
    PurshaceEarnPointLevelAdapter pepla;
    private CallbackManager callbackManager;
    private String eventType = "";
    private String requestType;
    private HorizontalScrollView brandAlliancesScroll, brandFriendsScroll;
    private JSONAsync call;
    private CustomLoading customLoading;
    /**
     * facebook share callback (share process result)
     */
    private FacebookCallback<Sharer.Result> mCallback = new FacebookCallback<Sharer.Result>() {

        @Override
        public void onSuccess(Sharer.Result result) {
//            Log.e("share", "success");
            eventType = "Share Brand Page";
            if (_utility.isConnectingToInternet_ping()) {
                eventRequest(eventType);//Share Page on FB
            } else {
                _utility.showMessage(getResources().getString(R.string.no_net));
            }
            _utility.showMessage(getResources().getString(R.string.success));
        }

        @Override
        public void onCancel() {
            Log.e("share", "cancel");
            _utility.showMessage(getResources().getString(R.string.cancel));
        }

        @Override
        public void onError(FacebookException error) {
            Log.e("share", "error");
            error.printStackTrace();
            _utility.showMessage(getResources().getString(R.string.must_login_fb), true);
        }
    };

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
        _utility = new Utility(getContext());
        Log.e("Single selectCoupType", MainActivity.selectedCoupon.getCouponType() + "..");
        customLoading = new CustomLoading(_utility.getCurrentActivity());

        if (MainActivity.selectedCoupon.getCouponType().equals(getString(R.string.loyalty_programs))) {
            rootView = inflater.inflate(R.layout.loyalty_single_coupon_fragment, container, false);
            customLoading.showProgress(_utility.getCurrentActivity());

            rel_earned_points = (RelativeLayout) rootView.findViewById(R.id.rel_earned_points);
//
//            tv_lvl_1 = (TextView) rootView.findViewById(R.id.tv_lvl_1);
//            tv_lvl_2 = (TextView) rootView.findViewById(R.id.tv_lvl_2);

            lv_earn_point = (ListView) rootView.findViewById(R.id.lv_earn_point);
            lv_earn_point.setVisibility(View.GONE);

            earn_1 = (TextView) rootView.findViewById(R.id.earn_1);
            earn_2 = (TextView) rootView.findViewById(R.id.earn_2);
            earn_3 = (TextView) rootView.findViewById(R.id.earn_3);
            earn_4 = (TextView) rootView.findViewById(R.id.earn_4);

            rel_loyalty_for_user = (RelativeLayout) rootView.findViewById(R.id.rel_loyalty_for_user);

            listView_loyalty = (ListView) rootView.findViewById(R.id.listView_loyalty);
            listView_loyalty.setVisibility(View.GONE);

            rel_earn = (RelativeLayout) rootView.findViewById(R.id.rel_earn);
            arrow_earn = (ImageView) rootView.findViewById(R.id.arrow_earn);
            arrow_loyalty = (ImageView) rootView.findViewById(R.id.arrow_loyalty);
            arrow_earned_points = (ImageView) rootView.findViewById(R.id.arrow_earned_points);


        } else {
            rootView = inflater.inflate(R.layout.special_offer_single_coupon_fragment, container, false);
            customLoading.showProgress(_utility.getCurrentActivity());

            rel_offersConditions = (RelativeLayout) rootView.findViewById(R.id.rel_offers_conditions);
            exLvOffCond = (ExpandableHeightListView) rootView.findViewById(R.id.ex_lv_off_cond);
            exLvOffCond.setVisibility(View.GONE);
            arrow_offersCondition = (ImageView) rootView.findViewById(R.id.arrow_offers_conditions);

            tv_prog_name = (TextView) rootView.findViewById(R.id.prog_name);
            tv_prog_type = (TextView) rootView.findViewById(R.id.prog_type);
            tv_prog_users = (TextView) rootView.findViewById(R.id.prog_users);
            tv_prog_details = (TextView) rootView.findViewById(R.id.prog_details);
        }


        callbackManager = CallbackManager.Factory.create();
        img_qr = (ImageView) rootView.findViewById(R.id.img_qr);
        no_friends = (TextView) rootView.findViewById(R.id.no_friends);
        no_alliance = (TextView) rootView.findViewById(R.id.no_alliance);
        et_comment = (EditText) rootView.findViewById(R.id.et_comment);
        et_comment.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                if (v.getId() == R.id.et_comment) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });

        img_mark = (ImageView) rootView.findViewById(R.id.img_mark);
        btn_comment = (Button) rootView.findViewById(R.id.btn_comment);
        tv_affiliate = (TextView) rootView.findViewById(R.id.tv_affiliate);
        arrow_cop_type = (ImageView) rootView.findViewById(R.id.arrow_cop_type);

        lin_earn = (LinearLayout) rootView.findViewById(R.id.lin_earn);
        lin_earn.setVisibility(View.GONE);

        lin_cop_type = (LinearLayout) rootView.findViewById(R.id.lin_cop_type);
        lin_cop_type.setVisibility(View.GONE);

        line_availabilty = rootView.findViewById(R.id.line_availabilty);
        line_cop_friends = rootView.findViewById(R.id.line_cop_friends);
        line_alliance = rootView.findViewById(R.id.line_alliance);
        line_cop_type = rootView.findViewById(R.id.line_cop_type);
        s_fan_type = (ImageView) rootView.findViewById(R.id.s_fan_type);
        imgv_brand = (ImageView) rootView.findViewById(R.id.imgv_brand);
        img_cop = (ImageView) rootView.findViewById(R.id.img_cop);
        img_share_s = (ShareButton) rootView.findViewById(R.id.img_share_s);
        brand_1 = (ImageView) rootView.findViewById(R.id.brand_1);
        brand_2 = (ImageView) rootView.findViewById(R.id.brand_2);
        brand_3 = (ImageView) rootView.findViewById(R.id.brand_3);
        brand_4 = (ImageView) rootView.findViewById(R.id.brand_4);
        tv_fav_count_s = (TextView) rootView.findViewById(R.id.tv_fav_count_s);
        tv_brand = (TextView) rootView.findViewById(R.id.tv_brand);
        tv_date = (TextView) rootView.findViewById(R.id.tv_date);
        tv_cop_type = (TextView) rootView.findViewById(R.id.tv_cop_type);
        tv_cop_name = (TextView) rootView.findViewById(R.id.tv_cop_name);
        tv_comments_count = (TextView) rootView.findViewById(R.id.tv_comments_count);
        tv_branches_count = (TextView) rootView.findViewById(R.id.tv_branches_count);
        img_fav_cop_s = (ImageView) rootView.findViewById(R.id.img_fav_cop_s);
        listView_branches = (ListView) rootView.findViewById(R.id.listView_branches);
        listView_comments = (ListView) rootView.findViewById(R.id.listView_comments);
        rel_availability = (RelativeLayout) rootView.findViewById(R.id.rel_availability);
        rel_cop_friends = (RelativeLayout) rootView.findViewById(R.id.rel_cop_friends);
        rel_alliance = (RelativeLayout) rootView.findViewById(R.id.rel_alliance);
        rel_cop_type = (RelativeLayout) rootView.findViewById(R.id.rel_cop_type);
        arrow_availability = (ImageView) rootView.findViewById(R.id.arrow_availability);
        lin_add_fav_s = (LinearLayout) rootView.findViewById(R.id.lin_add_fav_s);
        relativeLayout6 = (RelativeLayout) rootView.findViewById(R.id.relativeLayout6);
        tv_add_to_fav = (TextView) rootView.findViewById(R.id.tv_add_to_fav);

        brandFriendsLinear = (LinearLayout) rootView.findViewById(R.id.brandFriendsLinear);
        brandAlliancesLinear = (LinearLayout) rootView.findViewById(R.id.brandAlliancesLinear);
        brandFriendsScroll = (HorizontalScrollView) rootView.findViewById(R.id.brandFriendsScroll);
        brandAlliancesScroll = (HorizontalScrollView) rootView.findViewById(R.id.brandAlliancesScroll);

        if (_utility.isConnectingToInternet_ping()) {
            requestSingleCoupon();
        } else {
            customLoading.hideProgress();
            _utility.showMessage(getResources().getString(R.string.no_net));
        }

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void PostBackExecutionJSON(JSONTokener Result) {
        try {
            call = null;
            Log.e("single coup response", requestType);
            switch (requestType) {
                case "eventRequest":
                case "commentRequest":
                    parseEventResponse(Result);
                    break;
                case "addToWallet":
                    CouponTab.couponQualification = ParseData.parseQualifiedFragment(Result);
                    CouponTab.addToWalletSelectedCoupName = MainActivity.selectedCoupon.getCouponName();
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
                    break;
                case "requestSingleCoupon":
                    Log.e("zz0zz", Result.toString() + "     ..");
                    if (ParseData.ParseGetSingleCoupon(Result)) {
                        parseSingleCouponResponse();
                    } else {
                        _utility.showMessage(getResources().getString(R.string.ws_err));
                    }
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
            _utility.showMessage(_utility.getCurrentActivity().getResources().getString(R.string.ws_err));
            _utility.getCurrentActivity().onBackPressed();
        } finally {
            customLoading.hideProgress();

        }

    }

    private void parseSingleCouponResponse() {
        try {
            if (MainActivity.selectedCoupon.getCouponType().equals(getString(R.string.loyalty_programs))) {
                rel_earned_points.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (flagEarnedPoints) {
                            lv_earn_point.setVisibility(View.VISIBLE);
                            arrow_earned_points.setImageResource(R.drawable.up);
                            flagEarnedPoints = false;
                        } else {
                            lv_earn_point.setVisibility(View.GONE);
                            arrow_earned_points.setImageResource(R.drawable.down);
                            flagEarnedPoints = true;
                        }
                    }
                });

                rel_loyalty_for_user.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (flagLoyalty) {
                            listView_loyalty.setVisibility(View.VISIBLE);
                            arrow_loyalty.setImageResource(R.drawable.up);
                            flagLoyalty = false;
                        } else {
                            listView_loyalty.setVisibility(View.GONE);
                            arrow_loyalty.setImageResource(R.drawable.down);
                            flagLoyalty = true;
                        }
                    }
                });

                rel_earn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (flagEarn) {
                            if (MainActivity.selectedCoupon.getCouponConditions() != null)
                                lin_earn.setVisibility(View.VISIBLE);
                            arrow_earn.setImageResource(R.drawable.up);
                            flagEarn = false;
                        } else {
                            lin_earn.setVisibility(View.GONE);
                            arrow_earn.setImageResource(R.drawable.down);
                            flagEarn = true;
                        }
                    }
                });

                if (MainActivity.selectedCoupon.getCouponConditions() != null) {
                    builderTemp = new SpannableStringBuilder();
                    stringBuilder(R.string.every, String.format("%s%s", MainActivity.selectedCoupon.getCouponConditions().getEarnedPoints(), getString(R.string.point)));
                    builderTemp.append(builder);
                    builderTemp.append(" ");
                    stringBuilder(R.string.equal, String.format("%s%s", MainActivity.selectedCoupon.getCouponConditions().getEarnedEquivalentCurrency(), getString(R.string.LE)));
                    builderTemp.append(builder);
                    earn_1.setText(builderTemp, TextView.BufferType.SPANNABLE);

                    builderTemp = new SpannableStringBuilder();
                    stringBuilder(R.string.earn_till, MainActivity.selectedCoupon.getCouponConditions().getPointsExpireDays());
                    builderTemp.append(builder);
                    builderTemp.append(" ");
                    stringBuilder(R.string.from_due, " ");
                    builderTemp.append(builder);
                    earn_2.setText(builderTemp, TextView.BufferType.SPANNABLE);

                    builderTemp = new SpannableStringBuilder();
                    stringBuilder(R.string.due_after, String.format("%s%s", MainActivity.selectedCoupon.getCouponConditions().getRedemptionThreshold(), getString(R.string.day)));
                    builderTemp.append(builder);
                    builderTemp.append(" ");
                    stringBuilder(R.string.from_earn_date, " ");
                    builderTemp.append(builder);
                    earn_3.setText(builderTemp, TextView.BufferType.SPANNABLE);

                    stringBuilder(R.string.least_amount, "100");
                    earn_4.setText(builder, TextView.BufferType.SPANNABLE);
                }

                if (MainActivity.selectedCoupon.getEventRates() != null) {
                    evRaAdapter = new EventRateAdapter(getContext(), MainActivity.selectedCoupon.getEventRates());
                    listView_loyalty.setAdapter(evRaAdapter);
                    _utility.setListViewHeightBasedOnChildren(listView_loyalty);
                }

                pepla = new PurshaceEarnPointLevelAdapter(getContext(), MainActivity.selectedCoupon.getEarnedPointsAverages());
                lv_earn_point.setAdapter(pepla);
                _utility.setListViewHeightBasedOnChildren(lv_earn_point);
            } else {
                rel_offersConditions.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (flagOffersConditions) {
                            exLvOffCond.setVisibility(View.VISIBLE);
                            arrow_offersCondition.setImageResource(R.drawable.up);
                            flagOffersConditions = false;
                        } else {
                            exLvOffCond.setVisibility(View.GONE);
                            arrow_offersCondition.setImageResource(R.drawable.down);
                            flagOffersConditions = true;
                        }
                    }
                });
                if (MainActivity.selectedCoupon.getOfferConditions() != null && MainActivity.selectedCoupon.getOfferConditions().isEmpty())
                    rel_offersConditions.setVisibility(View.GONE);

                stringBuilder(R.string.name_of_prog, MainActivity.selectedCoupon.getCouponProgram().getProgramName());
                tv_prog_name.setText(builder, TextView.BufferType.SPANNABLE);

                stringBuilder(R.string.prog_type, MainActivity.selectedCoupon.getCouponProgram().getProgramType());
                tv_prog_type.setText(builder, TextView.BufferType.SPANNABLE);

                stringBuilder(R.string.cust_type, MainActivity.selectedCoupon.getCouponProgram().getProgramUsers());
                tv_prog_users.setText(builder, TextView.BufferType.SPANNABLE);

                stringBuilder(R.string.details, MainActivity.selectedCoupon.getCouponProgram().getProgramDesc());
                tv_prog_details.setText(builder, TextView.BufferType.SPANNABLE);

                if (MainActivity.selectedCoupon.getOfferConditions() != null) {
                    CouponConditionsAdapter offerConditionsAdapter = new CouponConditionsAdapter(getContext(), MainActivity.selectedCoupon.getOfferConditions());
                    exLvOffCond.setAdapter(offerConditionsAdapter);
                    exLvOffCond.setExpanded(true);
                } else
                    rel_offersConditions.setVisibility(View.GONE);
            }

            if (MainActivity.selectedCoupon.getCouponChallenges() != null && !MainActivity.selectedCoupon.getCouponChallenges().getChallengeName().equals("")) {
                if (!MainActivity.selectedCoupon.getCouponChallenges().isMissionStatus())
                    tv_add_to_fav.setText(R.string.start_challenge);
            }

            Picasso.with(getContext()).load(Const.imagesURL + "coupons/320x172/" + MainActivity.selectedCoupon.getCouponImage()).placeholder(R.drawable.ph_coupon).into(img_cop);


            tv_fav_count_s.setText(String.valueOf(MainActivity.selectedCoupon.getCouponSavedByCount()));

            if (MainActivity.selectedCoupon.getHasSaved()) {
                img_fav_cop_s.setImageResource(R.drawable.filled_heart);
            } else {
                img_fav_cop_s.setImageResource(R.drawable.empty_heart);
            }

            TextView tv_qr = (TextView) rootView.findViewById(R.id.tv_qr);

            if (MainActivity.selectedCoupon.getHasSaved()) {

                String qr = Integer.toString(MainActivity.currentFan.getFanID() + 10, 32) + "&" +
                        Integer.toString(MainActivity.selectedCoupon.getCouponID() + 10, 32);
                Log.e("SingleCoupon qr value", qr);
                img_qr.setImageBitmap(encodeToQrCode(qr));
                tv_qr.setText(qr);
                lin_add_fav_s.setBackgroundColor(getResources().getColor(R.color.green));
                tv_add_to_fav.setText(getResources().getString(R.string.in_my_coupons));
            } else {
                img_qr.setVisibility(View.GONE);
                tv_qr.setVisibility(View.GONE);

            }

            if (MainActivity.currentFan != null) {
                _utility.showMessage(getContext().getResources().getString(R.string.plz_w8));

                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentTitle(MainActivity.selectedCoupon.getCouponName())
                        .setContentDescription(MainActivity.selectedCoupon.getCouponInfo())
                        .setContentUrl(Uri.parse(Const.webServiceURL + MainActivity.langResult + "/coupon/" + MainActivity.selectedCoupon.getCouponSlug()))
                        .setImageUrl(Uri.parse(Const.imagesURL + "coupons/320x172/" + MainActivity.selectedCoupon.getCouponImage()))
                        .build();


                img_share_s.setShareContent(linkContent);


                Log.e("publish:", "end");
            }
            img_share_s.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (MainActivity.currentFan != null) {
                        _utility.showMessage(getContext().getResources().getString(R.string.plz_w8));
                        Log.e("publish:", "end");
                    } else {
                        _utility.showMessage(getResources().getString(R.string.plz_relogin));
                    }
                }
            });


            lin_add_fav_s.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (MainActivity.currentFan != null) {
                        CouponTab.coupSlugTemp = MainActivity.selectedCoupon.getCouponSlug();
                        if (MainActivity.selectedCoupon.getHasSaved()) {
                            CouponTab.couponQualification = null;
                            Fragment fragment = new QualifiedFragment();
                            FragmentManager fragmentManager = ((FragmentActivity) _utility.getCurrentActivity()).getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.fragment_container, fragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();

                        } else if (tv_add_to_fav.getText().toString().equals(getResources().getString(R.string.start_challenge))) {
                            Fragment fragment = null;
                            Log.e("single coup challenge", MainActivity.selectedCoupon.getCouponChallenges().getChallengeName());
                            switch (MainActivity.selectedCoupon.getCouponChallenges().getChallengeTitle()) {
                                case "group-buying":
                                    fragment = new GroupBuyingFragment();
                                    break;
                                case "punch-card":
                                    fragment = new CollectAndWinFragment();
                                    break;
                                case "dare-to":
                                    fragment = new WhoDareFragment();
                                    break;
                                case "surveyor":
                                    fragment = new SurveyFragment();
                                    break;
                                case "contests":
                                    fragment = new ContestsFragment();
                                    break;
                                case "socializers":
                                    fragment = new InteractionsFragment();
                                    break;
                                case "buy":
                                    fragment = new PurchaseFragment();
                                    break;
                                case "early-birds":
                                    fragment = new AlphaFragment();
                                    break;
                            }
                            if (fragment != null) {
                                FragmentManager fragmentManager = ((FragmentActivity) _utility.getCurrentActivity()).getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.fragment_container, fragment);
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();
                            } else
                                _utility.showMessage("Wrong coupon");
                        } else if (tv_add_to_fav.getText().toString().equals(getResources().getString(R.string.add_to_cop))) {
                            customLoading.showProgress(_utility.getCurrentActivity());
                            eventType = "Add to Wallet,Coupon";
                            if (_utility.isConnectingToInternet_ping()) {
                                eventRequest(eventType);
                            } else {
                                customLoading.hideProgress();
                                _utility.showMessage(getContext().getResources().getString(R.string.no_net));
                            }
                        }
                    } else {
                        _utility.loginDialogBox();
                    }
                }
            });


            if (MainActivity.selectedCoupon.getAffiliate()) {
                tv_affiliate.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.right, 0);
            } else {
                tv_affiliate.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.wrong, 0);
            }


            tv_date.setText(MainActivity.selectedCoupon.getCouponEndDate());
            tv_cop_type.setText(MainActivity.selectedCoupon.getCouponType());
            tv_cop_name.setText(MainActivity.selectedCoupon.getCouponName());
            tv_comments_count.setText(String.valueOf(MainActivity.selectedCoupon.getCouponComments().size()));

            if (MainActivity.selectedCoupon.getCouponComments().size() > 0) {
                comAd = new CommentsAdapter(getActivity(), MainActivity.selectedCoupon.getCouponComments());
                listView_comments.setAdapter(comAd);
                _utility.setListViewHeightBasedOnChildren(listView_comments);
            }


            tv_branches_count.setText(String.format(getString(R.string.branches_count), MainActivity.selectedCoupon.getCouponBrand().getBrandBranches().size()));

            if (MainActivity.selectedCoupon.getCouponBrand().getBrandBranches().size() > 0) {
                arrow_availability.setImageResource(R.drawable.down);
                rel_availability.setClickable(true);
                brAd = new BranchesAdapter(getActivity(), MainActivity.selectedCoupon.getCouponBrand().getBrandBranches());
                listView_branches.setAdapter(brAd);
                listView_branches.setVisibility(View.GONE);
                _utility.setListViewHeightBasedOnChildren(listView_branches);
            } else {
                arrow_availability.setImageResource(0);
                rel_availability.setClickable(false);
            }

            listView_branches.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    MainActivity.onActivityResultType = 0;
//                    String message = "Text I want to share.";
//                    Intent share = new Intent(Intent.ACTION_SEND);
//                    share.setType("text/plain");
//                    share.putExtra(Intent.EXTRA_TEXT, message);
//
//                    startActivityForResult(Intent.createChooser(share, "Title of the dialog the system will open"),9999);
                    Log.e("branches list", "single coupon");
                    MainActivity.mapType = "singleBranch";

                    MainActivity.selectedBranchLatLng = new LatLng(
                            MainActivity.selectedCoupon.getCouponBrand().getBrandBranches().get(position).getBranchLatitude(),
                            MainActivity.selectedCoupon.getCouponBrand().getBrandBranches().get(position).getBranchLongitude());
                    MainActivity.onActivityResultType = 2;

                    if (_utility.checkGPS()) {
                        if (MainActivity.latitudeSrc != 0.0) {
                            if (_utility.isConnectingToInternet_ping()) {

                                Fragment fragment = new FragmentMap();
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.fragment_container, fragment);
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();
                            } else
                                _utility.showMessage(getResources().getString(R.string.no_net));
                        } else
                            _utility.showMessage(getString(R.string.cannot_get_location));
                    } else {
                        MainActivity.alertNoGPS.show();
                    }
                }
            });


            rel_cop_type.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (flagCopType) {/////////////////////////////////////////////////////////////////////
                        lin_cop_type.setVisibility(View.VISIBLE);
                        arrow_cop_type.setImageResource(R.drawable.up);
                        flagCopType = false;
                    } else {
                        arrow_cop_type.setImageResource(R.drawable.down);
                        lin_cop_type.setVisibility(View.GONE);
                        flagCopType = true;
                    }
                }
            });


            rel_availability.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (flagAvailability) {
                        listView_branches.setVisibility(View.VISIBLE);
                        arrow_availability.setImageResource(R.drawable.up);
                        flagAvailability = false;
                    } else {
                        arrow_availability.setImageResource(R.drawable.down);
                        listView_branches.setVisibility(View.GONE);
                        flagAvailability = true;
                    }
                }
            });

            tv_brand.setText(MainActivity.selectedCoupon.getCouponBrand().getBrandName());
            Picasso.with(getContext()).load(Const.imagesURL + "brands/50x50/" +
                    MainActivity.selectedCoupon.getCouponBrand().getBrandImage()).placeholder(R.drawable.ph_brand).into(imgv_brand);
            if (MainActivity.selectedCoupon.getCouponBrand().getBrandAlliances() != null) {
                if (MainActivity.selectedCoupon.getCouponBrand().getBrandAlliances().isEmpty()) {
                    brandAlliancesScroll.setVisibility(View.GONE);
                } else {
                    no_alliance.setVisibility(View.GONE);
                    _utility.createDynamicImageViews("Alliances", MainActivity.selectedCoupon.getCouponBrand().getBrandAlliances(), brandAlliancesLinear);
                }
            } else {
                brandAlliancesScroll.setVisibility(View.GONE);
            }

            if (MainActivity.selectedCoupon.getCouponSavedBy() != null) {
                if (MainActivity.selectedCoupon.getCouponSavedBy().isEmpty()) {
                    brandFriendsScroll.setVisibility(View.GONE);
                } else {
                    no_friends.setVisibility(View.GONE);
                    _utility.createDynamicImageViews("friends", MainActivity.selectedCoupon.getCouponSavedBy(), brandFriendsLinear);
                }
            } else {
                brandFriendsScroll.setVisibility(View.GONE);
            }

            btn_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("BtnC0mment", "clicked");

                    InputMethodManager imm = (InputMethodManager) _utility.getCurrentActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    if (et_comment.getText().toString().trim().length() != 0 && _utility.isConnectingToInternet_ping()) {
                        String commentText = et_comment.getText().toString();
                        et_comment.setText("");
                        commentRequest(commentText);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            _utility.showMessage(getContext().getResources().getString(R.string.error));
        }
    }


    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        Log.e("singleFragment", "onActivityResult call request code:" + requestCode + " result code:" + resultCode);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if ("com.facebook.platform.action.request.LIKE_DIALOG".equals(data.getStringExtra("com.facebook.platform.protocol.PROTOCOL_ACTION"))) {
            // get action results
            Bundle bundle = data.getExtras().getBundle("com.facebook.platform.protocol.RESULT_ARGS");
            if (bundle != null) {
                bundle.getBoolean("object_is_liked"); // liked/unliked
                Log.e("0fb_object_is_liked", "++ " + bundle.getBoolean("object_is_liked"));
                bundle.getInt("didComplete");
                Log.e("0fb_didComplete", "++ " + bundle.getInt("didComplete"));
                bundle.getInt("like_count"); // object like count
                Log.e("0fb_like_count", "++ " + bundle.getInt("like_count"));
                bundle.getString("like_count_string");
                Log.e("0fb_like_count_string", "++ " + bundle.getString("like_count_string"));
                bundle.getString("social_sentence");
                Log.e("0fb_social_sentence", "++ " + bundle.getString("social_sentence"));
                bundle.getString("completionGesture"); // liked/cancel/unliked
                Log.e("0fb_completionGesture", "++ " + bundle.getString("completionGesture"));
                if (bundle.getString("completionGesture").equals("like")) {
                    eventType = "Like Brand";
                    if (_utility.isConnectingToInternet_ping()) {
                        eventRequest(eventType);//Like Brand
                    } else
                        _utility.showMessage(getResources().getString(R.string.no_net));
                }
            }
        }
    }

    private void eventRequest(String eventType) {
        if (MainActivity.currentFan != null) {
            requestType = "eventRequest";
            JSONWebServices service = new JSONWebServices(SingleCouponFragment.this);
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            switch (eventType) {
                case "Add to Wallet,Coupon":
                    Log.e("00s_nodeId", MainActivity.selectedCoupon.getCouponID() + "");
                    status = "Coupon added successfully";
                    call = service.qualifiedToCouponRequestFragment(null, MainActivity.selectedCoupon.getCouponSlug());
                    requestType = "addToWallet";
                    return;
                case "Like Brand":
                    nameValuePairs.add(new BasicNameValuePair("user_id", MainActivity.currentFan.getFanID() + ""));
                    nameValuePairs.add(new BasicNameValuePair("event_type_id", "15"));
                    nameValuePairs.add(new BasicNameValuePair("node_id", MainActivity.selectedCoupon.getCouponBrand().getBrandID() + ""));
                    Log.e("00s_nodeId", MainActivity.selectedCoupon.getCouponBrand().getBrandID() + "");
                    status = "like";
                    break;
                default:
                    nameValuePairs.add(new BasicNameValuePair("user_id", MainActivity.currentFan.getFanID() + ""));
                    nameValuePairs.add(new BasicNameValuePair("event_type_id", "19"));
                    nameValuePairs.add(new BasicNameValuePair("node_id", MainActivity.selectedCoupon.getCouponBrand().getBrandID() + ""));
                    Log.e("00s_nodeId", MainActivity.selectedCoupon.getCouponBrand().getBrandID() + "");
                    status = "Page shared successfully";
                    break;
            }
            call = service.eventRequestFragment(nameValuePairs);
        } else {
            _utility.loginDialogBox();
        }
    }

    private void commentRequest(String comment) {
        if (MainActivity.currentFan != null) {
            customLoading.showProgress(_utility.getCurrentActivity());
            requestType = "commentRequest";
            JSONWebServices service = new JSONWebServices(SingleCouponFragment.this);
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("comment", comment));
            nameValuePairs.add(new BasicNameValuePair("node_type", "125"));
            status = "Comment added successfully";
            call = service.couponCommentRequest(nameValuePairs);
        } else {
            _utility.loginDialogBox();
        }
    }

    /**
     * process a request for a single coupon data
     */
    private void requestSingleCoupon() {
        requestType = "requestSingleCoupon";
        JSONWebServices service = new JSONWebServices(SingleCouponFragment.this);
        Log.e("coup id", MainActivity.selectedCoupon.getCouponID() + "---");

        if (MainActivity.selectedCoupon.getCouponID() != 0)
            call = service.getSingleCoupon(null, MainActivity.selectedCoupon.getCouponSlug());
        else {
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            if (MainActivity.currentFan != null) {
                nameValuePairs.add(new BasicNameValuePair("type", "campaign"));
                call = service.getSingleCoupon(nameValuePairs, MainActivity.selectedCoupon.getCouponSlug());
            } else {
                nameValuePairs.add(new BasicNameValuePair("type", "mobile"));
                call = service.getSingleCoupon(nameValuePairs, MainActivity.selectedCoupon.getCouponSlug());
            }
        }
    }

    private void parseEventResponse(JSONTokener Result) {
        ArrayList<String> result = ParseData.parseEventResponse(Result);
        if (result == null) {
            _utility.showMessage(getResources().getString(R.string.ws_err));
            return;
        }
        if (result.get(0).equals("success")) {

            if (!status.equals("")) {
                AlertDialog.Builder build = new AlertDialog.Builder(getContext());
                AlertDialog alert;
                switch (status) {
                    case "Comment added successfully":
                        build.setMessage(getResources().getString(R.string.comment_added))
                                .setCancelable(false)
                                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                        customLoading.showProgress(_utility.getCurrentActivity());
                                        requestSingleCoupon();
                                    }
                                });
                        alert = build.create();
                        alert.show();
                        break;
                    case "like":
                        eventType = "Add to Wallet,Coupon";
                        status = "Coupon added successfully";
                        if (_utility.isConnectingToInternet_ping()) {
                            eventRequest(eventType);//add to wallet after like auto
                        } else
                            _utility.showMessage(getResources().getString(R.string.no_net));
                        break;
                    case "Page shared successfully":
                        build.setMessage(getResources().getString(R.string.coupon_shared))
                                .setCancelable(false)
                                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                        alert = build.create();
                        alert.show();
                        break;
                    default:
                        CouponTab.couponQualification = ParseData.parseQualifiedFragment(Result);
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
                        break;
                }
            } else {
                Log.e("status fady", status + ".");
                AlertDialog.Builder build = new AlertDialog.Builder(getContext());
                build.setMessage(getResources().getString(R.string.cant_reach))
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                try {
                                    likeDialog.dismiss();
                                    shareDialog.dismiss();
                                } catch (Exception e) {
                                }
                            }
                        });
                AlertDialog alert = build.create();
                alert.show();
            }
        } else {
            if (status.equals("Coupon added successfully")) {
                CouponTab.addToWalletSelectedCouponConditions.clear();
                for (int i = 2; i < result.size(); i++) {
                    CouponTab.addToWalletSelectedCouponConditions.add(result.get(i));
                }

                Fragment fragment = new DisqualifiedFragment();
                FragmentManager fragmentManager = ((FragmentActivity) _utility.getCurrentActivity()).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            } else {
                AlertDialog.Builder build = new AlertDialog.Builder(getContext());
                build.setMessage(result.get(1) + "\n" + result.get(2))
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = build.create();
                alert.show();
            }

        }
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