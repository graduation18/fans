package com.systemonline.fanscoupon.brands_tabs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;
import com.systemonline.fanscoupon.Base.BaseFragment;
import com.systemonline.fanscoupon.BranchesAdapter;
import com.systemonline.fanscoupon.CommentsAdapter;
import com.systemonline.fanscoupon.FragmentMap;
import com.systemonline.fanscoupon.Helpers.Const;
import com.systemonline.fanscoupon.Helpers.Utility;
import com.systemonline.fanscoupon.MainActivity;
import com.systemonline.fanscoupon.Model.Comment;
import com.systemonline.fanscoupon.Model.Coupon;
import com.systemonline.fanscoupon.Model.CustomLoading;
import com.systemonline.fanscoupon.R;
import com.systemonline.fanscoupon.SingleCouponFragment;
import com.systemonline.fanscoupon.WebServices.JSONAsync;
import com.systemonline.fanscoupon.WebServices.JSONWebServices;
import com.systemonline.fanscoupon.WebServices.ParseData;
import com.systemonline.fanscoupon.coupon_tabs.MyCouponsAdapter;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

public class SingleBrandFragment extends BaseFragment {

    ListView lv_loyalty, lv_specialOffers, lv_comments, listView_branches;
    RelativeLayout rel_availability;
    LinearLayout lin_loyaltyPrograms, lin_specialOffer, lin_customerTypes;
    TextView tv_brand_name, tv_brandDesc, tv_commentCount, tv_branchesCount, loyaltyNoCoupons, specialOfferNoCoupons;
    EditText et_comment;
    Button btn_comment, loadMore;
    Spinner rate;
    JSONAsync call;
    Utility _utility;
    boolean flagAvailability = true;
    int couponsLimit, couponsPage;
    private View root;
    private ImageView arrow_availability;
    private boolean loadMoreFlag;
    private int lastLoyaltySize, lastSpecialOffersSize;
    private String requestType;
    private CustomLoading customLoading;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.brand_tab_single_brand, null);
        _utility = new Utility(getContext());
        customLoading = new CustomLoading(_utility.getCurrentActivity());
        customLoading.showProgress(_utility.getCurrentActivity());
        couponsLimit = 4;
        couponsPage = 0;
        ImageView brandCover = (ImageView) root.findViewById(R.id.brandImg);
//        Picasso.with(con).load(Const.imagesURL + "brands/160x160/" + MainActivity.selectedBrand.getBrandCover()).into(brandCover);
        Picasso.with(_utility.getCurrentActivity()).load(Const.imagesURL + "brands/850x315/" + BrandsTab.selectedBrand.getBrandCover()).placeholder(R.drawable.ph_brand_cover).into(brandCover);
        ImageView brandLogo = (ImageView) root.findViewById(R.id.brandImgCircle);
        Picasso.with(_utility.getCurrentActivity()).load(Const.imagesURL + "brands/50x50/" + BrandsTab.selectedBrand.getBrandImage()).placeholder(R.drawable.ph_brand).into(brandLogo);

        LinearLayout brand_star = (LinearLayout) root.findViewById(R.id.brand_star);
        rate = (Spinner) root.findViewById(R.id.dropDown_rate);

        String[] rateValues = new String[]{"1", "2", "3", "4", "5"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(_utility.getCurrentActivity(),
                R.layout.rate_spinner, R.id.rate_number, rateValues);
        rate.setAdapter(adapter);

        if (!BrandsTab.selectedBrand.getBrandRatingStarsCount().equals("null")) {
            _utility.createDynamicStars(Double.parseDouble(BrandsTab.selectedBrand.getBrandRatingStarsCount()), brand_star);
        }

        ImageView hasLoyalty = (ImageView) root.findViewById(R.id.tv_hasLoyalty);
        TextView couponsCount = (TextView) root.findViewById(R.id.tv_couponsCount);
        TextView fansCount = (TextView) root.findViewById(R.id.tv_fansCount);

        hasLoyalty.setImageResource(BrandsTab.selectedBrand.isHasLoyalty() ? R.drawable.right : R.drawable.wrong);
        couponsCount.setText(String.valueOf(BrandsTab.selectedBrand.getBrandCouponsCount()));
        fansCount.setText(String.valueOf(BrandsTab.selectedBrand.getBrandFansCount()));

        lv_loyalty = (ListView) root.findViewById(R.id.lvLoyaltyPrograms);
        lv_specialOffers = (ListView) root.findViewById(R.id.lvSpecialOffer);

        loadMore = (Button) root.findViewById(R.id.loadMore);

        loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadMoreFlag = true;
//
                customLoading.showProgress(_utility.getCurrentActivity());
                requestSingleBrand(couponsLimit, couponsPage);//loadMore
            }
        });

        lv_comments = (ListView) root.findViewById(R.id.listView_comments);

        tv_brandDesc = (TextView) root.findViewById(R.id.tv_brandInfo);
        tv_brand_name = (TextView) root.findViewById(R.id.brandName);
        tv_branchesCount = (TextView) root.findViewById(R.id.tv_branches_count);
        tv_commentCount = (TextView) root.findViewById(R.id.tv_comments_count);
        loyaltyNoCoupons = (TextView) root.findViewById(R.id.no_loyaltyCoupons);
        specialOfferNoCoupons = (TextView) root.findViewById(R.id.no_specialOfferCoupons);


        btn_comment = (Button) root.findViewById(R.id.btn_comment);


        listView_branches = (ListView) root.findViewById(R.id.listView_branches);
        rel_availability = (RelativeLayout) root.findViewById(R.id.rel_availability);
        arrow_availability = (ImageView) root.findViewById(R.id.arrow_availability);
        loadMoreFlag = false;
        requestSingleBrand(couponsLimit, couponsPage);//onCreate

        return root;
    }


    /**
     * make a request to get all Brands
     */
    private void requestSingleBrand(int limit, int offset) {
        if (_utility.isConnectingToInternet_ping()) {
            requestType = "requestSingleBrand";

            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("index", String.valueOf(offset)));
            nameValuePairs.add(new BasicNameValuePair("limit", String.valueOf(limit)));

            JSONWebServices service = new JSONWebServices(SingleBrandFragment.this);
            call = service.getSingleBrand(nameValuePairs, BrandsTab.selectedBrand.getBrandSlug());

        } else {
            customLoading.hideProgress();
            _utility.showMessage(getResources().getString(R.string.no_net));
        }
    }

    /**
     * make a request to rate brand
     */

    private void commentRequest(Comment comment) {
        if (MainActivity.currentFan != null) {
            customLoading.showProgress(_utility.getCurrentActivity());
            requestType = "commentRequest";
            JSONWebServices service = new JSONWebServices(SingleBrandFragment.this);
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("comment", et_comment.getText().toString()));
            switch (rate.getSelectedItemPosition()) {
                case 0:
                    nameValuePairs.add(new BasicNameValuePair("rate", "1"));
                    break;
                case 1:
                    nameValuePairs.add(new BasicNameValuePair("rate", "2"));
                    break;
                case 2:
                    nameValuePairs.add(new BasicNameValuePair("rate", "3"));
                    break;
                case 3:
                    nameValuePairs.add(new BasicNameValuePair("rate", "4"));
                    break;
                case 4:
                    nameValuePairs.add(new BasicNameValuePair("rate", "5"));
                    break;
            }
            nameValuePairs.add(new BasicNameValuePair("node_type", "124"));
            if (comment != null) {
                nameValuePairs.add(new BasicNameValuePair("comment_id", String.valueOf(comment.getCommentID())));
                call = service.brandUpdateCommentRequest(nameValuePairs);
            } else
                call = service.brandCreateCommentRequest(nameValuePairs);
        } else {
            _utility.loginDialogBox();
        }
    }

    @Override
    public void PostBackExecutionJSON(JSONTokener Result) {
        try {
            Log.e("single Brand response", "-------------");
            if (requestType.equals("commentRequest")) {
                parseEventResponse(Result);
            } else {
                if (ParseData.parseSingleBrand(Result)) {
                    if (loadMoreFlag) {
                        loadMoreFlag = false;
                        if (BrandsTab.selectedBrand.getLoyaltyCoupons().size() == lastLoyaltySize &&
                                BrandsTab.selectedBrand.getSpecialOfferCoupons().size() == lastSpecialOffersSize)
                            _utility.showMessage(getResources().getString(R.string.no_more));
                        else
                            setBrandData();
                    } else
                        setBrandData();
                } else {
                    _utility.showMessage(getResources().getString(R.string.ws_err));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            _utility.showMessage(getResources().getString(R.string.ws_err));
        } finally {
            customLoading.hideProgress();
            call = null;

        }

    }

    void parseEventResponse(JSONTokener Result) {
        ArrayList<String> result = ParseData.parseEventResponse(Result);
        if (result == null) {
            _utility.showMessage(getResources().getString(R.string.ws_err));
            return;
        }
        if (result.get(0).equals("success")) {
            AlertDialog.Builder build = new AlertDialog.Builder(getContext());
            build.setMessage(getResources().getString(R.string.evaluation_added))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            customLoading.showProgress(_utility.getCurrentActivity());
                            requestSingleBrand(couponsLimit, couponsPage);
                        }
                    });
            AlertDialog alert = build.create();
            alert.show();
        }
    }

    private void setBrandData() {
        et_comment = (EditText) root.findViewById(R.id.et_commentBrand);
        et_comment.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                if (v.getId() == R.id.et_commentBrand) {
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
        if (MainActivity.currentFan != null && BrandsTab.selectedBrand.getCurrentFanComment() != null) {
            rate.setSelection(BrandsTab.selectedBrand.getCurrentFanComment().getRate() - 1);
            et_comment.setText(BrandsTab.selectedBrand.getCurrentFanComment().getCommentBody());
        }

        if (!BrandsTab.selectedBrand.getLoyaltyCoupons().isEmpty() &&
                !BrandsTab.selectedBrand.getSpecialOfferCoupons().isEmpty())
            couponsLimit += 4;
        lv_loyalty = (ListView) root.findViewById(R.id.lvLoyaltyPrograms);
        if (BrandsTab.selectedBrand.getLoyaltyCoupons().isEmpty()) {
            loyaltyNoCoupons.setVisibility(View.VISIBLE);
        } else {
            lastLoyaltySize = BrandsTab.selectedBrand.getLoyaltyCoupons().size();
            MyCouponsAdapter loyaltyAdapter = new MyCouponsAdapter(getActivity(), BrandsTab.selectedBrand.getLoyaltyCoupons());
            lv_loyalty.setAdapter(loyaltyAdapter);
            _utility.setListViewHeightBasedOnChildren(lv_loyalty);
            loyaltyNoCoupons.setVisibility(View.GONE);
            lv_loyalty.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        MainActivity.selectedCoupon = (Coupon) BrandsTab.selectedBrand.getLoyaltyCoupons().get(position).clone();
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                    Fragment fragment = new SingleCouponFragment();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });

        }

        lv_specialOffers = (ListView) root.findViewById(R.id.lvSpecialOffer);
        if (BrandsTab.selectedBrand.getSpecialOfferCoupons().isEmpty()) {
            specialOfferNoCoupons.setVisibility(View.VISIBLE);
        } else {
            lastSpecialOffersSize = BrandsTab.selectedBrand.getSpecialOfferCoupons().size();
            MyCouponsAdapter specialOfferAdapter = new MyCouponsAdapter(getActivity(), BrandsTab.selectedBrand.getSpecialOfferCoupons());
            lv_specialOffers.setAdapter(specialOfferAdapter);
            _utility.setListViewHeightBasedOnChildren(lv_specialOffers);
            specialOfferNoCoupons.setVisibility(View.GONE);
            lv_specialOffers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        MainActivity.selectedCoupon = (Coupon) BrandsTab.selectedBrand.getSpecialOfferCoupons().get(position).clone();
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                    Fragment fragment = new SingleCouponFragment();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });
        }

        lv_comments = (ListView) root.findViewById(R.id.listView_comments);

        if (BrandsTab.selectedBrand.getBrandEvaluations().size() > 0) {
            CommentsAdapter comAd = new CommentsAdapter(getActivity(), BrandsTab.selectedBrand.getBrandEvaluations());
            lv_comments.setAdapter(comAd);
            _utility.setListViewHeightBasedOnChildren(lv_comments);
        }
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

        tv_branchesCount.setText(String.format(getString(R.string.branches_count), BrandsTab.selectedBrand.getBrandBranches().size()));

        if (BrandsTab.selectedBrand.getBrandBranches().size() > 0) {
            arrow_availability.setImageResource(R.drawable.down);
            rel_availability.setClickable(true);
            BranchesAdapter brAd = new BranchesAdapter(getActivity(), BrandsTab.selectedBrand.getBrandBranches());
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
                Log.e("branches list", "single brand");
                MainActivity.mapType = "brandBranch";

                MainActivity.selectedBranchLatLng = new LatLng(
                        BrandsTab.selectedBrand.getBrandBranches().get(position).getBranchLatitude(),
                        BrandsTab.selectedBrand.getBrandBranches().get(position).getBranchLongitude());
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

        tv_brandDesc.setText(BrandsTab.selectedBrand.getBrandDesc());
        tv_brand_name.setText(BrandsTab.selectedBrand.getBrandName());
        tv_commentCount.setText(String.valueOf(BrandsTab.selectedBrand.getBrandEvaluations().size()));
        btn_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        lin_loyaltyPrograms = (LinearLayout) root.findViewById(R.id.lin_loyaltyPrograms);
        lin_specialOffer = (LinearLayout) root.findViewById(R.id.lin_specialOffer);
        lin_customerTypes = (LinearLayout) root.findViewById(R.id.brandBadgesLinear);
        _utility.createDynamicImageViews("customerTypes", BrandsTab.selectedBrand.getBrandCustomerTypes(), lin_customerTypes);

        btn_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("BtnC0mment", "clicked");

                InputMethodManager imm = (InputMethodManager) _utility.getCurrentActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                if (et_comment.getText().toString().trim().length() != 0 && _utility.isConnectingToInternet_ping()) {
                    commentRequest(BrandsTab.selectedBrand.getCurrentFanComment());
                }
            }
        });

    }


}
