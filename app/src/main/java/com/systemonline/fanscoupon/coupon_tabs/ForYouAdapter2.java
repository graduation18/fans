package com.systemonline.fanscoupon.coupon_tabs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.LikeView;
import com.squareup.picasso.Picasso;
import com.systemonline.fanscoupon.AlphaFragment;
import com.systemonline.fanscoupon.Base.BaseAdapter;
import com.systemonline.fanscoupon.CollectAndWinFragment;
import com.systemonline.fanscoupon.ContestsFragment;
import com.systemonline.fanscoupon.DisqualifiedFragment;
import com.systemonline.fanscoupon.GroupBuyingFragment;
import com.systemonline.fanscoupon.Helpers.Const;
import com.systemonline.fanscoupon.Helpers.Utility;
import com.systemonline.fanscoupon.InteractionsFragment;
import com.systemonline.fanscoupon.MainActivity;
import com.systemonline.fanscoupon.Model.Coupon;
import com.systemonline.fanscoupon.Model.CustomLoading;
import com.systemonline.fanscoupon.PurchaseFragment;
import com.systemonline.fanscoupon.QualifiedFragment;
import com.systemonline.fanscoupon.R;
import com.systemonline.fanscoupon.SurveyFragment;
import com.systemonline.fanscoupon.WebServices.JSONAsync;
import com.systemonline.fanscoupon.WebServices.JSONWebServices;
import com.systemonline.fanscoupon.WebServices.ParseData;
import com.systemonline.fanscoupon.WhoDareFragment;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

class ForYouAdapter2 extends BaseAdapter {
    private static String eventType;
    public ArrayList<Coupon> values;
    int selectedCopTypeID;
    String selectedCoupType;
    private Context con;
    private Coupon coupon;
    private Utility utility;
    private JSONAsync call;
    private CustomLoading customLoading;
    /**
     * Facebook share callback (share process result)
     */
    private FacebookCallback<Sharer.Result> mCallback = new FacebookCallback<Sharer.Result>() {
        @Override
        public void onSuccess(Sharer.Result result) {
            eventType = "Share Brand Page";
            customLoading.showProgress(utility.getCurrentActivity());
            if (utility.isConnectingToInternet_ping()) {
                eventRequestAdapter(eventType);
            } else {
                customLoading.hideProgress();
                utility.showMessage(getContext().getResources().getString(R.string.no_net), true);
            }
            utility.showMessage(getContext().getResources().getString(R.string.success));
        }

        @Override
        public void onCancel() {
            utility.showMessage(getContext().getResources().getString(R.string.cancel));
        }

        @Override
        public void onError(FacebookException error) {
            error.printStackTrace();
            utility.showMessage(getContext().getResources().getString(R.string.error) + error.toString());
        }
    };

    ForYouAdapter2(Context c, ArrayList<Coupon> values, int selectedCopTypeID, String selectedCoupType) {
        super(c, R.layout.cop_tab_all_coupons, values);
        this.con = c;
        this.values = values;
        utility = new Utility(c);
        customLoading = new CustomLoading(utility.getCurrentActivity());
        this.selectedCopTypeID = selectedCopTypeID;
        this.selectedCoupType = selectedCoupType;
    }


    @Override
    public View getView(int position, View row, ViewGroup parent) {


        if (row == null) {
            LayoutInflater inflater = ((Activity) con).getLayoutInflater();
            row = inflater.inflate(R.layout.cop_tab_foryou_coupons_row, parent, false);
        }
        coupon = values.get(position);
        MainActivity.callbackManagerAdapter = CallbackManager.Factory.create();
        RelativeLayout lin_challenge = (RelativeLayout) row.findViewById(R.id.lin_challenge);
        final TextView addToWallet = (TextView) row.findViewById(R.id.tv_addToWallet);
        addToWallet.setText(con.getResources().getString(R.string.add_to_cop));

//        SpannableStringBuilder builder;
//        String strBuild;
//        SpannableString strColor;

        TextView cop_type = (TextView) row.findViewById(R.id.cop_type);
        cop_type.setText(coupon.getCouponType());
        LinearLayout brandFriends = (LinearLayout) row.findViewById(R.id.couponsAdapterBrandFriends);
        LinearLayout brand_star = (LinearLayout) row.findViewById(R.id.brand_star);
        TextView win_all = (TextView) row.findViewById(R.id.win_all);
        TextView ch_all = (TextView) row.findViewById(R.id.ch_all);
        ImageView imgv_cop = (ImageView) row.findViewById(R.id.imgv_cop);
        ImageView imgv_brand = (ImageView) row.findViewById(R.id.imgv_brand);
        ImageView imgv_coup_customer1 = (ImageView) row.findViewById(R.id.img_type1);
        ImageView imgv_coup_customer2 = (ImageView) row.findViewById(R.id.img_type2);
        ImageView imgv_coup_customer3 = (ImageView) row.findViewById(R.id.img_type3);
        ImageView coupIcon = (ImageView) row.findViewById(R.id.imageView);
        ImageView imgv_heart = (ImageView) row.findViewById(R.id.img_fav_cop);
        ImageView img_add_to_fav = (ImageView) row.findViewById(R.id.img_add_to_fav);
        TextView tv_brand = (TextView) row.findViewById(R.id.tv_brand);
        TextView tv_fav_count = (TextView) row.findViewById(R.id.tv_fav_count);
        LinearLayout lin_add_to_fav = (LinearLayout) row.findViewById(R.id.lin_add_to_fav);
        LinearLayout lin_share_fb = (LinearLayout) row.findViewById(R.id.lin_share_fb);
        RelativeLayout hor_scv = (RelativeLayout) row.findViewById(R.id.hor_scv);

        final Integer couponID = coupon.getCouponID();

        String link = "";
        int brandSocialNetworksSize = coupon.getCouponBrand().getBrandSocialNetworks().size();
        for (int i = 0; i < brandSocialNetworksSize; i++) {
            if (coupon.getCouponBrand().getBrandSocialNetworks().get(i).getBrandAccountType().equals("13")) {
                link = coupon.getCouponBrand().getBrandSocialNetworks().get(i).getBrandAccountID();
                break;
            }
        }

        final String brandSocialPage = con.getResources().getString(R.string.facebookLink) + link;
        MainActivity.onActivityResultType = 1;
        final LikeView likeView = (LikeView) row.findViewById(R.id.FY_like_view);
        likeView.setObjectIdAndType(brandSocialPage,
                LikeView.ObjectType.UNKNOWN);
        ImageView transparent_img = (ImageView) row.findViewById(R.id.transparent_img);
        transparent_img.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                MainActivity.brandPageLiked = brandSocialPage;
                likeView.requestDisallowInterceptTouchEvent(true);
                CouponTab.coupIdTemp = couponID;
                return false;
            }
        });


        if (coupon.getCouponCustomerTypes().size() > 0) {
            Picasso.with(con).load(Const.imagesURL + "customer-types/50x50/" + coupon.getCouponCustomerTypes().get(0).getCustomerTypeImage()).placeholder(R.drawable.ph_badge).into(imgv_coup_customer1);

            if (coupon.getCouponCustomerTypes().size() > 1)
                Picasso.with(con).load(Const.imagesURL + "customer-types/50x50/" + coupon.getCouponCustomerTypes().get(1).getCustomerTypeImage()).placeholder(R.drawable.ph_badge).into(imgv_coup_customer2);

            else
                imgv_coup_customer2.setVisibility(View.GONE);
            if (coupon.getCouponCustomerTypes().size() > 2)
                Picasso.with(con).load(Const.imagesURL + "customer-types/50x50/" + coupon.getCouponCustomerTypes().get(2).getCustomerTypeImage()).placeholder(R.drawable.ph_badge).into(imgv_coup_customer3);

            else
                imgv_coup_customer3.setVisibility(View.GONE);
        }
        if (coupon.getCouponSavedBy() != null) {
            hor_scv.setVisibility(View.VISIBLE);
            utility.createDynamicImageViews("friends", coupon.getCouponSavedBy(), brandFriends);
        } else {
            hor_scv.setVisibility(View.GONE);
        }

        if (!coupon.getCouponBrand().getBrandRatingStarsCount().equals("null")) {
            utility.createDynamicStars(Double.parseDouble(coupon.getCouponBrand().getBrandRatingStarsCount()), brand_star);
        }
        Picasso.with(con).load(Const.imagesURL + "brands/50x50/" + coupon.getCouponBrand().getBrandImage()).placeholder(R.drawable.ph_brand).into(imgv_brand);
        Picasso.with(con).load(Const.imagesURL + "coupons/320x172/" + coupon.getCouponImage()).placeholder(R.drawable.ph_coupon).into(imgv_cop);

        if (coupon.getHasSaved()) {
            imgv_heart.setImageResource(R.drawable.filled_heart);
        } else {
            imgv_heart.setImageResource(R.drawable.empty_heart);
        }
        switch (selectedCopTypeID) {
            case 93:
                img_add_to_fav.setImageResource(R.drawable.heart_cyan);
                coupIcon.setImageResource(R.drawable.voucher_cyan);
                cop_type.setBackgroundColor(getContext().getResources().getColor(R.color.cyan));
                break;
            case 95:
                img_add_to_fav.setImageResource(R.drawable.heart_green);
                coupIcon.setImageResource(R.drawable.voucher_green);
                cop_type.setBackgroundColor(getContext().getResources().getColor(R.color.green));

                break;
            case 232:
                img_add_to_fav.setImageResource(R.drawable.heart_violet);
                coupIcon.setImageResource(R.drawable.win);
                cop_type.setBackgroundColor(getContext().getResources().getColor(R.color.burble));

                break;
            case 238:
                img_add_to_fav.setImageResource(R.drawable.heart_blue);
                coupIcon.setImageResource(R.drawable.voucher_blue);
                cop_type.setBackgroundColor(getContext().getResources().getColor(R.color.blue));
                break;
            case 240:
                img_add_to_fav.setImageResource(R.drawable.heart_pink);
                coupIcon.setImageResource(R.drawable.voucher_pink);
                cop_type.setBackgroundColor(getContext().getResources().getColor(R.color.pink));
                break;
            case 242:
                img_add_to_fav.setImageResource(R.drawable.heart_orange);
                coupIcon.setImageResource(R.drawable.voucher_orange);
                cop_type.setBackgroundColor(getContext().getResources().getColor(R.color.orange));
                break;
            default:
                break;
        }

        tv_brand.setText(coupon.getCouponBrand().getBrandName());
        utility.setTextViewLines(tv_brand, coupon.getCouponBrand().getBrandName());
        tv_fav_count.setText(String.valueOf(coupon.getCouponSavedByCount()));

        win_all.setText(utility.colorString(R.string.win, R.color.red, coupon.getCouponName(), R.color.black), TextView.BufferType.SPANNABLE);
        utility.setTextViewLines(win_all, utility.colorString(R.string.win, R.color.red, coupon.getCouponName(), R.color.black).toString());


        if (selectedCoupType.equals("challenges")) {
            lin_challenge.setVisibility(View.VISIBLE);
            if (!coupon.getCouponChallenges().isMissionStatus())
                addToWallet.setText(R.string.start_challenge);

            ch_all.setText(utility.colorString(R.string.challenge, R.color.red, coupon.getCouponChallenges().getChallengeName(), R.color.black), TextView.BufferType.SPANNABLE);
            utility.setTextViewLines(ch_all, utility.colorString(R.string.challenge, R.color.red, coupon.getCouponChallenges().getChallengeName(), R.color.red).toString());

        } else
            lin_challenge.setVisibility(View.GONE);


        if (coupon.getHasSaved())
            addToWallet.setText(R.string.in_my_coupons);

        final String coupSlug = coupon.getCouponSlug();
        final String coupName = coupon.getCouponName();
        final String coupDesc = coupon.getCouponInfo();
        final String challengeName = coupon.getCouponChallenges().getChallengeTitle();
        final String coupImage = coupon.getCouponImage();
        final int coupTypeID = coupon.getCouponTypeID();


        lin_share_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                utility.showMessage(getContext().getResources().getString(R.string.plz_w8));
                MainActivity.selectedFBBrand = coupSlug + "";
                CouponTab.coupSlugTemp = coupSlug;
                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentTitle(coupName)
                        .setContentDescription(coupDesc)
                        .setContentUrl(Uri.parse(Const.webServiceURL + MainActivity.langResult + "/coupon/" + coupSlug))
                        .setImageUrl(Uri.parse(Const.imagesURL + "coupons/320x172/" + coupImage))
                        .build();

                ShareApi.share(linkContent, mCallback);
            }
        });

        lin_add_to_fav.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("adapter addFav CoupId", couponID + "");

                        if (MainActivity.currentFan != null) {
                            CouponTab.coupIdTemp = couponID;
                            CouponTab.coupSlugTemp = coupSlug;
                            CouponTab.addToWalletSelectedCoupName = coupName;
                            CouponTab.selectedCopTypeID = coupTypeID;

                            if (addToWallet.getText().toString().equals(con.getResources().getString(R.string.in_my_coupons))) {
                                CouponTab.couponQualification = null;
                                Fragment fragment = new QualifiedFragment();
                                FragmentManager fragmentManager = ((FragmentActivity) utility.getCurrentActivity()).getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.fragment_container, fragment);
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();


                            } else if (addToWallet.getText().toString().equals(con.getResources().getString(R.string.start_challenge))) {

                                Fragment fragment = null;
                                Log.e("adapter challenge type", challengeName);
                                switch (challengeName) {
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
                                    FragmentManager fragmentManager = ((FragmentActivity) utility.getCurrentActivity()).getSupportFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.replace(R.id.fragment_container, fragment);
                                    fragmentTransaction.addToBackStack(null);
                                    fragmentTransaction.commit();
                                } else
                                    utility.showMessage("Wrong coupon");
                            } else if (addToWallet.getText().toString().equals(con.getResources().getString(R.string.add_to_cop))) {
                                customLoading.showProgress(utility.getCurrentActivity());
                                eventType = "Add to Wallet,Coupon";
                                if (utility.isConnectingToInternet_ping()) {
                                    eventRequestAdapter(eventType);
                                } else {
                                    customLoading.hideProgress();
                                    utility.showMessage(getContext().getResources().getString(R.string.no_net));
                                }
                            }
                        } else {
                            utility.loginDialogBox();
                        }
                    }
                }
        );
        return row;
    }
//
//    /**
//     * Update all coupons listview after making add to fav to specific coupon
//     *
//     * @param coupID
//     */
//    void updateCoupons(int coupID) {
//        Log.e("adapter update coupons", "");
//        int allCouponsSize = CouponTab.forYou.size();
//        for (int i = 0; i < allCouponsSize; i++) {
//            if (CouponTab.forYou.get(i).getCouponID() == coupID) {
//                CouponTab.forYou.get(i).getCouponBrand().setLikedByCurrentUser(true);
//                CouponTab.forYou.get(i).setCouponSavedByCount(CouponTab.forYou.get(i).getCouponSavedByCount() + 1);
//                CouponTab.forYou.get(i).setHasSaved(true);
//                break;
//            }
//        }
//        values = CouponTab.forYou;
//        this.notifyDataSetChanged();
//    }


    private void eventRequestAdapter(String event) {

        JSONWebServices service = new JSONWebServices(ForYouAdapter2.this);
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        if (event.equals("Share Brand Page")) {
            nameValuePairs.add(new BasicNameValuePair("event_type_id", "19"));
            nameValuePairs.add(new BasicNameValuePair("node_id", MainActivity.selectedFBBrand));
            call = service.eventRequestAdapter(nameValuePairs);

        } else {
            call = service.qualifiedToCouponRequestAdapter(null);
        }

    }

    @Override
    public void PostBackExecutionJSON(JSONTokener Result) {
        try {
            Log.e("forYouCoupon adapt resp", Result.toString());
            call = null;
            if (eventType.equals("Share Brand Page")) {
                ArrayList<String> result = ParseData.parseEventResponse(Result);
                if (result == null) {
                    utility.showMessage(getContext().getResources().getString(R.string.ws_err));
                    return;
                }
                if (result.get(0).equals("success")) {

                    AlertDialog.Builder build = new AlertDialog.Builder(getContext());
                    build.setMessage(getContext().getResources().getString(R.string.coupon_shared))
                            .setCancelable(false)
                            .setPositiveButton(getContext().getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alert = build.create();
                    alert.show();
                }
            } else {
                CouponTab.couponQualification = ParseData.parseQualifiedFragment(Result);
                customLoading.hideProgress();
                if (CouponTab.couponQualification == null) {
                    utility.showMessage(getContext().getResources().getString(R.string.ws_err));
                    return;
                }
                Fragment fragment;

                if (CouponTab.couponQualification.isQualifiedUser()) {
                    fragment = new QualifiedFragment();
                } else {
                    fragment = new DisqualifiedFragment();
                }
                FragmentManager fragmentManager = ((FragmentActivity) utility.getCurrentActivity()).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
            utility.showMessage(getContext().getResources().getString(R.string.ws_err));
        } finally {
            customLoading.hideProgress();
        }
    }

}