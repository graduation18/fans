package com.systemonline.fanscoupon.coupon_tabs;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.systemonline.fanscoupon.Base.BaseFragment;
import com.systemonline.fanscoupon.FragmentMap;
import com.systemonline.fanscoupon.Helpers.Utility;
import com.systemonline.fanscoupon.MainActivity;
import com.systemonline.fanscoupon.Model.Coupon;
import com.systemonline.fanscoupon.Model.CustomLoading;
import com.systemonline.fanscoupon.R;
import com.systemonline.fanscoupon.SingleCouponFragment;
import com.systemonline.fanscoupon.WebServices.JSONAsync;
import com.systemonline.fanscoupon.WebServices.JSONWebServices;
import com.systemonline.fanscoupon.WebServices.ParseData;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.systemonline.fanscoupon.coupon_tabs.CouponTab.forYou2;

public class OnlyForYouCouponsFragment2 extends BaseFragment {

    ListView loyalty_ls, specialOffer_ls, challenges_ls, luckyHour_ls, specialOccasions_ls, forYou_ls;
    //    ForYouAdapter2 allCopAd;
    ImageView btn_map;
    LinearLayout lin_filter, lin_sort;
    //    HorizontalScrollView couponsTypesBar;
//    tv_loyalty, tv_specialoffer, tv_challenges, tv_happyOcc, tv_luckyHour, tv_forYou,
    //    Button loadMore;
    boolean loadMoreFlag;
    JSONAsync call;
    Utility _utility;
    RelativeLayout rel_loyalty, rel_specialOffer, rel_challenges, rel_luckyHour, rel_specialOccasions, rel_forYou;
    private CustomLoading customLoading;
    private Button loyalty_loadMore, specialOffer_loadMore, challenges_loadMore, luckyHour_loadMore,
            specialOccasions_loadMore, forYou_loadMore;
    private String couponsRequestType = "all";
    private String loadMoreType;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.cop_tab_only_for_you2, null);
        _utility = new Utility(getContext());
        customLoading = new CustomLoading(_utility.getCurrentActivity());
        Log.e("0pen", "only youCoupon\n-------" + MainActivity.langResult);
        MainActivity.langResult = MainActivity.sharedPreferences.getString("lang", "en");
//        tv_no_cop = (TextView) root.findViewById(R.id.tv_no_cop);
//        tv_no_cop.setVisibility(View.GONE);

        btn_map = (ImageView) root.findViewById(R.id.btn_map);
//        couponsTypesBar=(HorizontalScrollView)root.findViewById(R.id.horizontalScrollView);

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.allCoupons_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);

                customLoading.showProgress(_utility.getCurrentActivity());
                CouponTab.couponPageOnlyYou = 1;
                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPageOnlyYou, "all");//refresh
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (forYou2 == null || forYou2.isEmpty()) {
                    _utility.showMessage(_utility.getCurrentActivity().getResources().getString(R.string.no_branches));
                    return;
                }

                CouponTab.couponsBranches = new ArrayList<>();
                for (int k = 0; k < forYou2.size(); k++) {

                    for (int i = 0; i < forYou2.get(k).size(); i++) {
                        for (int j = 0; j < forYou2.get(k).get(i).getCouponBrand().getBrandBranches().size(); j++) {
                            forYou2.get(k).get(i).getCouponBrand().getBrandBranches().get(j).
                                    setOffName(forYou2.get(k).get(i).getCouponName());
                            CouponTab.couponsBranches.add(forYou2.get(k).get(i).getCouponBrand().getBrandBranches().get(j));
                        }
                    }
                }
                Log.e("couponsBranches", CouponTab.couponsBranches.size() + "--");
//                for (int i = 0; i < CouponTab.couponsBranches.size(); i++) {
//                    Log.e("only you couponsBranches", CouponTab.couponsBranches.get(i).getBranchName());
//
//                }
                MainActivity.mapType = "allBranches";
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
                    }
                } else {
                    MainActivity.alertNoGPS.show();
                }

            }
        });

        rel_loyalty = (RelativeLayout) root.findViewById(R.id.rel_loyalty);
        rel_specialOffer = (RelativeLayout) root.findViewById(R.id.rel_specialOffer);
        rel_challenges = (RelativeLayout) root.findViewById(R.id.rel_challenge);
        rel_luckyHour = (RelativeLayout) root.findViewById(R.id.rel_luckyHour);
        rel_specialOccasions = (RelativeLayout) root.findViewById(R.id.rel_specialOccasions);
        rel_forYou = (RelativeLayout) root.findViewById(R.id.rel_forYou);

        loyalty_ls = (ListView) root.findViewById(R.id.loyalty_ls);
        specialOffer_ls = (ListView) root.findViewById(R.id.specialOffer_ls);
        challenges_ls = (ListView) root.findViewById(R.id.challenges_ls);
        luckyHour_ls = (ListView) root.findViewById(R.id.luckyHour_ls);
        specialOccasions_ls = (ListView) root.findViewById(R.id.specialOccasions_ls);
        forYou_ls = (ListView) root.findViewById(R.id.forYou_ls);
//        tv_specialoffer = (TextView) root.findViewById(R.id.tv_specialoffer);
//        tv_loyalty = (TextView) root.findViewById(R.id.tv_loyalty);
//        tv_challenges = (TextView) root.findViewById(R.id.tv_challenges);
//        tv_happyOcc = (TextView) root.findViewById(R.id.tv_happyOccasion);
//        if (MainActivity.currentFan == null) {
//            tv_happyOcc.setVisibility(View.GONE);
//        }
//        tv_luckyHour = (TextView) root.findViewById(R.id.tv_luckyHour);
//        tv_forYou = (TextView) root.findViewById(R.id.tv_forYou);
//        tv_forYou.setVisibility(View.GONE);
        lin_filter = (LinearLayout) root.findViewById(R.id.lin_filter);
        lin_sort = (LinearLayout) root.findViewById(R.id.lin_sort);

        loyalty_loadMore = (Button) root.findViewById(R.id.loyalty_loadMore);
        specialOffer_loadMore = (Button) root.findViewById(R.id.specialOffer_loadMore);
        challenges_loadMore = (Button) root.findViewById(R.id.challenges_loadMore);
        luckyHour_loadMore = (Button) root.findViewById(R.id.luckyHour_loadMore);
        specialOccasions_loadMore = (Button) root.findViewById(R.id.specialOccasions_loadMore);
        forYou_loadMore = (Button) root.findViewById(R.id.forYou_loadMore);

        loyalty_loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMoreType = "loyalty";
                loadMoreFlag = true;
                customLoading.showProgress(_utility.getCurrentActivity());
                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPageOnlyYou, "loyalty");//loadMore loyalty
            }
        });
        specialOffer_loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMoreType = "specialOffers";
                loadMoreFlag = true;
                customLoading.showProgress(_utility.getCurrentActivity());
                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPageOnlyYou, "specialOffers");//loadMore specialOffers
            }
        });
        challenges_loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMoreType = "challenges";

                loadMoreFlag = true;
                customLoading.showProgress(_utility.getCurrentActivity());
                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPageOnlyYou, "challenges");//loadMore challenges
            }
        });
        luckyHour_loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMoreType = "luckyHour";

                loadMoreFlag = true;
                customLoading.showProgress(_utility.getCurrentActivity());
                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPageOnlyYou, "luckyHour");//loadMore luckyHour
            }
        });
        specialOccasions_loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMoreType = "specialOccasion";

                loadMoreFlag = true;
                customLoading.showProgress(_utility.getCurrentActivity());
                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPageOnlyYou, "specialOccasion");//loadMore specialOccasion
            }
        });
        forYou_loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMoreType = "forYou";

                loadMoreFlag = true;
                customLoading.showProgress(_utility.getCurrentActivity());
                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPageOnlyYou, "forYou");//loadMore forYou
            }
        });

// (Button) ((LayoutInflater) _utility.getCurrentActivity().
//                getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.lv_footer, null, false);
//        loyalty_ls.addFooterView(loyalty_loadMore);
//
//        specialOffer_loadMore = (Button) ((LayoutInflater) _utility.getCurrentActivity().
//                getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.lv_footer, null, false);
//        specialOffer_ls.addFooterView(specialOffer_loadMore);
//
//        challenges_loadMore = (Button) ((LayoutInflater) _utility.getCurrentActivity().
//                getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.lv_footer, null, false);
//        challenges_ls.addFooterView(challenges_loadMore);
//
//        luckyHour_loadMore = (Button) ((LayoutInflater) _utility.getCurrentActivity().
//                getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.lv_footer, null, false);
//        luckyHour_ls.addFooterView(luckyHour_loadMore);
//
//        specialOccasions_loadMore = (Button) ((LayoutInflater) _utility.getCurrentActivity().
//                getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.lv_footer, null, false);
//        specialOccasions_ls.addFooterView(specialOccasions_loadMore);
//
//        forYou_loadMore = (Button) ((LayoutInflater) _utility.getCurrentActivity().
//                getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.lv_footer, null, false);
//        forYou_ls.addFooterView(forYou_loadMore);


        lin_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new SortFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        lin_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new CouponsFilterFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });


        setCoupTypeBackground();

//        tv_loyalty.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                customLoading.showProgress(_utility.getCurrentActivity());
//                CouponTab.selectedCopTypeID = 95;
//                CouponTab.selectedCoupType = "loyalty";
//                CouponTab.couponPageO = 1;
//                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPageO, CouponTab.selectedCoupType);//loyalty
//                tv_forYou.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_loyalty.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_green));
//                tv_specialoffer.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_challenges.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_luckyHour.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_happyOcc.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//            }
//        });
//
//        tv_specialoffer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                customLoading.showProgress(_utility.getCurrentActivity());
//                CouponTab.selectedCopTypeID = 93;
//                CouponTab.selectedCoupType = "specialOffers";
//                CouponTab.couponPageO = 1;
//
//                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPageO, CouponTab.selectedCoupType);//special offer
//                tv_forYou.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_specialoffer.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_green));
//                tv_loyalty.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_challenges.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_luckyHour.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_happyOcc.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//            }
//        });
//        tv_challenges.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                customLoading.showProgress(_utility.getCurrentActivity());
//                CouponTab.selectedCopTypeID = 232;
//                CouponTab.selectedCoupType = "challenges";
//                CouponTab.couponPageO = 1;
//
//                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPageO, CouponTab.selectedCoupType);//challenge
//                tv_forYou.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_challenges.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_green));
//                tv_loyalty.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_specialoffer.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_luckyHour.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_happyOcc.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//            }
//        });
//        tv_luckyHour.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                customLoading.showProgress(_utility.getCurrentActivity());
//                CouponTab.selectedCopTypeID = 238;
//                CouponTab.selectedCoupType = "luckyHour";
//                CouponTab.couponPageO = 1;
//
//                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPageO, CouponTab.selectedCoupType);//lucky hour
//                tv_forYou.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_luckyHour.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_green));
//                tv_loyalty.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_challenges.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_specialoffer.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_happyOcc.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//            }
//        });
//        tv_happyOcc.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                customLoading.showProgress(_utility.getCurrentActivity());
//                CouponTab.selectedCopTypeID = 240;
//                CouponTab.selectedCoupType = "specialOccasion";
//                CouponTab.couponPageO = 1;
//
//                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPageO, CouponTab.selectedCoupType);//happy Occ
//                tv_forYou.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_happyOcc.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_green));
//                tv_loyalty.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_challenges.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_luckyHour.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_specialoffer.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//            }
//        });
//
//        tv_forYou.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                customLoading.showProgress(_utility.getCurrentActivity());
//                CouponTab.selectedCoupType = "forYou";
//                CouponTab.selectedCopTypeID = 242;
//                CouponTab.couponPageO = 1;
//
//                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPageO, CouponTab.selectedCoupType);//for you
//                tv_forYou.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_green));
//                tv_happyOcc.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_loyalty.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_challenges.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_luckyHour.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_specialoffer.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//            }
//        });

        ((MainActivity) getActivity()).setAllCouponsRefreshListener(new MainActivity.FragmentRefreshListener() {
            @Override
            public void onRefresh() {
                Log.e("allCoupon", "refresh");
                if (_utility.isConnectingToInternet_ping()) {
                    Log.e("get from cloud", "all coupons");
                    CouponTab.couponPageOnlyYou = 1;
                    requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPageOnlyYou, "all");//on create
                } else {
                    customLoading.hideProgress();
                    _utility.showMessage(getResources().getString(R.string.no_net));
                    noCouponsStatus();
                }
            }
        });
        processCoupons();

        return root;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            Log.e("00vis00", "ony youCop - visible");
        } else {
            Log.e("00vis00", "only youCop - notVisible");
        }
    }

    void setCoupTypeBackground() {
//        try {
//            switch (CouponTab.selectedCopTypeID) {
//                case 93:
//                    tv_specialoffer.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_green));
//                    break;
//                case 95:
//                    tv_loyalty.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_green));
//                    break;
//                case 232:
//                    tv_challenges.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_green));
//                    break;
//                case 238:
//                    tv_luckyHour.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_green));
//                    break;
//                case 240:
//                    tv_happyOcc.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_green));
//                    break;
//                case 242:
//                    tv_forYou.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_green));
//                    break;
//
//                default:
//                    break;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private void processCoupons() {
        setCoupTypeBackground();
        customLoading.showProgress(_utility.getCurrentActivity());
        loadMoreFlag = false;
        if (forYou2 != null && Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            Log.e("coupons,,,", "!=null");
            if (forYou2.size() > 0) {
                Log.e("get locally", "all coupons= " + forYou2.size());
                sortAndShowCoupons();
            } else {
                Log.e("coupons,,,", "empty");
//                loadMore.setVisibility(View.GONE);
//                tv_no_cop.setVisibility(View.VISIBLE);
            }
            customLoading.hideProgress();
        } else {
            Log.e("coupons,,,", "is null");
            if (_utility.isConnectingToInternet_ping()) {
                Log.e("get from cloud", "all coupons");
                CouponTab.couponPageOnlyYou = 1;
                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPageOnlyYou, "all");//on create
            } else {
                customLoading.hideProgress();
                _utility.showMessage(getResources().getString(R.string.no_net));
                noCouponsStatus();
            }
        }
    }

    private void sortAndShowCoupons() {
//        switch (CouponTab.sortState) {
//            case 1:
//                for (int i = 0; i < forYou2.size(); i++)
//                    Collections.sort(forYou2.get(i), Collections.reverseOrder());
//                break;
//            case 2:
//                for (int i = 0; i < forYou2.size(); i++)
//                    Collections.sort(forYou2.get(i));
//                break;
//            case 3:
//                sortMostPopular();
//                break;
//            default:
//                break;
//        }
//        tv_no_cop.setVisibility(View.GONE);
//        lvAllCoupons.setVisibility(View.VISIBLE);

        clearListViews();
        if (forYou2.get(0).isEmpty()) {
            loyalty_loadMore.setText(getString(R.string.no_coupons));
            loyalty_loadMore.setEnabled(false);
            if (couponsRequestType.equals("filter")) {
                loyalty_loadMore.setVisibility(View.GONE);
                rel_loyalty.setVisibility(View.GONE);
            }
        } else {
            ForYouAdapter2 loyalty_allCopAd = new ForYouAdapter2(getActivity(), forYou2.get(0), 95, "loyalty");
            loyalty_ls.setAdapter(loyalty_allCopAd);
            _utility.setListViewHeightBasedOnChildren(loyalty_ls);
//            loyalty_ls.setSelectionFromTop(forYou2.size() - 4, 0);
            loyalty_ls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        MainActivity.selectedCoupon = (Coupon) forYou2.get(0).get(position).clone();
                    } catch (CloneNotSupportedException e) {
                        Log.e("coupon class clone", "exception");
                        e.printStackTrace();
                    }
                    CouponTab.myCoupon = false;
                    Fragment fragment = new SingleCouponFragment();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });
            loyalty_loadMore.setText(getString(R.string.load_more));
            loyalty_loadMore.setEnabled(true);
            loyalty_loadMore.setVisibility(View.VISIBLE);

        }

        if (forYou2.get(1).isEmpty()) {
            specialOffer_loadMore.setText(getString(R.string.no_coupons));
            specialOffer_loadMore.setEnabled(false);
            if (couponsRequestType.equals("filter")) {
                specialOffer_loadMore.setVisibility(View.GONE);
                rel_specialOffer.setVisibility(View.GONE);
            }
        } else {
            ForYouAdapter2 specialOffer_allCopAd = new ForYouAdapter2(getActivity(), forYou2.get(1), 93, "specialOffer");
            specialOffer_ls.setAdapter(specialOffer_allCopAd);
            _utility.setListViewHeightBasedOnChildren(specialOffer_ls);
//            specialOffer_ls.setSelectionFromTop(forYou2.size() - 4, 0);
            specialOffer_ls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        MainActivity.selectedCoupon = (Coupon) forYou2.get(1).get(position).clone();
                    } catch (CloneNotSupportedException e) {
                        Log.e("coupon class clone", "exception");
                        e.printStackTrace();
                    }
                    CouponTab.myCoupon = false;
                    Fragment fragment = new SingleCouponFragment();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });
            specialOffer_loadMore.setText(getString(R.string.load_more));
            specialOffer_loadMore.setEnabled(true);
            specialOffer_loadMore.setVisibility(View.VISIBLE);

        }

        if (forYou2.get(2).isEmpty()) {
            challenges_loadMore.setText(getString(R.string.no_coupons));
            challenges_loadMore.setEnabled(false);
            if (couponsRequestType.equals("filter")) {
                challenges_loadMore.setVisibility(View.GONE);
                rel_challenges.setVisibility(View.GONE);
            }
        } else {
            ForYouAdapter2 challenges_allCopAd = new ForYouAdapter2(getActivity(), forYou2.get(2), 232, "challenges");
            challenges_ls.setAdapter(challenges_allCopAd);
            _utility.setListViewHeightBasedOnChildren(challenges_ls);
//            challenges_ls.setSelectionFromTop(forYou2.size() - 4, 0);
            challenges_ls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        MainActivity.selectedCoupon = (Coupon) forYou2.get(2).get(position).clone();
                    } catch (CloneNotSupportedException e) {
                        Log.e("coupon class clone", "exception");
                        e.printStackTrace();
                    }
                    CouponTab.myCoupon = false;
                    Fragment fragment = new SingleCouponFragment();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });
            challenges_loadMore.setText(getString(R.string.load_more));
            challenges_loadMore.setEnabled(true);
            challenges_loadMore.setVisibility(View.VISIBLE);

        }

        if (forYou2.get(3).isEmpty()) {
            luckyHour_loadMore.setText(getString(R.string.no_coupons));
            luckyHour_loadMore.setEnabled(false);
            if (couponsRequestType.equals("filter")) {
                luckyHour_loadMore.setVisibility(View.GONE);
                rel_luckyHour.setVisibility(View.GONE);
            }
        } else {
            ForYouAdapter2 luckyHour_allCopAd = new ForYouAdapter2(getActivity(), forYou2.get(3), 238, "luckyHour");
            luckyHour_ls.setAdapter(luckyHour_allCopAd);
            _utility.setListViewHeightBasedOnChildren(luckyHour_ls);
//            luckyHour_ls.setSelectionFromTop(forYou2.size() - 4, 0);
            luckyHour_ls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        MainActivity.selectedCoupon = (Coupon) forYou2.get(3).get(position).clone();
                    } catch (CloneNotSupportedException e) {
                        Log.e("coupon class clone", "exception");
                        e.printStackTrace();
                    }
                    CouponTab.myCoupon = false;
                    Fragment fragment = new SingleCouponFragment();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });
            luckyHour_loadMore.setText(getString(R.string.load_more));
            luckyHour_loadMore.setEnabled(true);
            luckyHour_loadMore.setVisibility(View.VISIBLE);

        }

        if (forYou2.get(4).isEmpty()) {
            specialOccasions_loadMore.setText(getString(R.string.no_coupons));
            specialOccasions_loadMore.setEnabled(false);
            if (couponsRequestType.equals("filter")) {
                specialOccasions_loadMore.setVisibility(View.GONE);
                rel_specialOccasions.setVisibility(View.GONE);
            }
        } else {
            ForYouAdapter2 specialOccasions_allCopAd = new ForYouAdapter2(getActivity(), forYou2.get(4), 240, "specialOccasions");
            specialOccasions_ls.setAdapter(specialOccasions_allCopAd);
            _utility.setListViewHeightBasedOnChildren(specialOccasions_ls);
//            specialOccasions_ls.setSelectionFromTop(forYou2.size() - 4, 0);
            specialOccasions_ls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        MainActivity.selectedCoupon = (Coupon) forYou2.get(4).get(position).clone();
                    } catch (CloneNotSupportedException e) {
                        Log.e("coupon class clone", "exception");
                        e.printStackTrace();
                    }
                    CouponTab.myCoupon = false;
                    Fragment fragment = new SingleCouponFragment();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });
            specialOccasions_loadMore.setText(getString(R.string.load_more));
            specialOccasions_loadMore.setEnabled(true);
            specialOccasions_loadMore.setVisibility(View.VISIBLE);

        }

        if (forYou2.get(5).isEmpty()) {
            forYou_loadMore.setText(getString(R.string.no_coupons));
            forYou_loadMore.setEnabled(false);
            if (couponsRequestType.equals("filter")) {
                forYou_loadMore.setVisibility(View.GONE);
                rel_forYou.setVisibility(View.GONE);
            }
        } else {
            ForYouAdapter2 forYou_allCopAd = new ForYouAdapter2(getActivity(), forYou2.get(5), 242, "forYou");
            forYou_ls.setAdapter(forYou_allCopAd);
            _utility.setListViewHeightBasedOnChildren(forYou_ls);
//            forYou_ls.setSelectionFromTop(forYou2.size() - 4, 0);
            forYou_ls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        MainActivity.selectedCoupon = (Coupon) forYou2.get(5).get(position).clone();
                    } catch (CloneNotSupportedException e) {
                        Log.e("coupon class clone", "exception");
                        e.printStackTrace();
                    }
                    CouponTab.myCoupon = false;
                    Fragment fragment = new SingleCouponFragment();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });
            forYou_loadMore.setText(getString(R.string.load_more));
            forYou_loadMore.setEnabled(true);
            forYou_loadMore.setVisibility(View.VISIBLE);

        }

    }

    /**
     * make a request to get all coupons
     *
     * @param limit
     */

    public void requestAllCoupons(int limit, int offset, String type) {

        if (MainActivity.currentFan == null) {
            customLoading.hideProgress();
            return;
        }

        couponsRequestType = type;
        if (_utility.isConnectingToInternet_ping()) {
            Log.e("onlyYou -- request " + type + " coupons", limit + "");
            try {

                ArrayList<String> allFilters = getFilters();
                JSONWebServices service = new JSONWebServices(OnlyForYouCouponsFragment2.this);
                List<NameValuePair> nameValuePairs = new ArrayList<>();
                JSONObject filters = new JSONObject();
                Log.e("customerTypes filter", allFilters.get(0) + "---");
                JSONArray clients = new JSONArray();
                String[] splitter = allFilters.get(0).split(",");
                if (!allFilters.get(0).isEmpty()) {
                    for (String aSplitter : splitter) {
                        Log.e("filters", aSplitter + "");
                        clients.put(aSplitter);
                    }
                }
                filters.put("clients", clients);
                Log.e("favoriteBrands filter", allFilters.get(1) + "---");
                JSONArray brands = new JSONArray();
                if (!allFilters.get(1).isEmpty()) {
                    splitter = allFilters.get(1).split(",");
                    for (String aSplitter : splitter) {
                        Log.e("filters", aSplitter + "");
                        brands.put(aSplitter);
                    }
                }
                filters.put("brands", brands);

                Log.e("favoriteCateg filter", allFilters.get(2) + "---");
                JSONArray categories = new JSONArray();
                if (!allFilters.get(2).isEmpty()) {
                    splitter = allFilters.get(2).split(",");
                    for (String aSplitter : splitter) {
                        Log.e("filters", aSplitter + "");
                        categories.put(Integer.valueOf(aSplitter));
                    }
                }
                filters.put("categories", categories);

                filters.put("all", false);
                filters.put("coupons", new JSONArray());
                filters.put("country", MainActivity.currentFan != null ? MainActivity.currentFan.getFanCountryID() : 36);

                JSONObject couponType = new JSONObject();
                couponType.put("sortBy", CouponTab.sortState);
                couponType.put("limit", limit);
                couponType.put("page", offset);

                JSONObject sections = new JSONObject();
                if (!type.equals("all"))
                    sections.put(type, couponType);
                else {
                    Log.e("coup Categ. filter", allFilters.get(3) + "---");

                    if (!allFilters.get(3).isEmpty()) {
                        couponsRequestType = "filter";
                        splitter = allFilters.get(3).split(",");
                        for (String aSplitter : splitter) {
                            switch (aSplitter) {
                                case "95":
                                    sections.put("loyalty", couponType);
                                    break;
                                case "93":
                                    sections.put("specialOffers", couponType);
                                    break;
                                case "232":
                                    sections.put("challenges", couponType);
                                    break;
                                case "238":
                                    sections.put("luckyHour", couponType);
                                    break;
                                case "240":
                                    sections.put("specialOccasion", couponType);
                                    break;
                                case "242":
                                    sections.put("forYou", couponType);
                                    break;

                            }
                        }
                    } else {
                        sections.put("loyalty", couponType);
                        sections.put("specialOffers", couponType);
                        sections.put("challenges", couponType);
                        sections.put("luckyHour", couponType);
                        sections.put("specialOccasion", couponType);
                        sections.put("forYou", couponType);
                    }
                }

                nameValuePairs.add(new BasicNameValuePair("sections", sections.toString()));
                nameValuePairs.add(new BasicNameValuePair("filters", filters.toString()));
                call = service.getAllCoupons(nameValuePairs);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            customLoading.hideProgress();
            loadMoreFlag = false;
            _utility.showMessage(getResources().getString(R.string.no_net));
        }
    }

    /**
     * make a request to get all filters
     *
     * @return
     */
    ArrayList<String> getFilters() {
        StringBuilder sb = new StringBuilder();
        ArrayList<String> filtersResult = new ArrayList<>();

        int verySpecialsCheckboxStatusesSize = CouponTab.verySpecialsCheckboxStatuses.size();
        for (int i = 0; i < verySpecialsCheckboxStatusesSize; i++) {
            if (CouponTab.verySpecialsCheckboxStatuses.get(i).getFilterChecked())
                sb.append(CouponTab.verySpecialsCheckboxStatuses.get(i).getFilterID()).append(",");
        }
        if (!sb.toString().isEmpty()) {
            sb.deleteCharAt(sb.lastIndexOf(","));
            filtersResult.add(sb.toString());
        } else
            filtersResult.add("");
        sb.delete(0, sb.capacity());
        int favouriteBrandCheckboxStatuses = CouponTab.favouriteBrandCheckboxStatuses.size();
        for (int i = 0; i < favouriteBrandCheckboxStatuses; i++) {
            if (CouponTab.favouriteBrandCheckboxStatuses.get(i).getFilterChecked())
                sb.append(CouponTab.favouriteBrandCheckboxStatuses.get(i).getFilterID()).append(",");
        }
        if (!sb.toString().isEmpty()) {
            sb.deleteCharAt(sb.lastIndexOf(","));
            filtersResult.add(sb.toString());
        } else
            filtersResult.add("");
        sb.delete(0, sb.capacity());
        int interestedINCheckboxStatuses = CouponTab.interestedINCheckboxStatuses.size();
        for (int i = 0; i < interestedINCheckboxStatuses; i++) {
            if (CouponTab.interestedINCheckboxStatuses.get(i).getFilterChecked())
                sb.append(CouponTab.interestedINCheckboxStatuses.get(i).getFilterID()).append(",");
        }
        if (!sb.toString().isEmpty()) {
            sb.deleteCharAt(sb.lastIndexOf(","));
            filtersResult.add(sb.toString());
        } else
            filtersResult.add("");

        sb.delete(0, sb.capacity());
        int couponCategoriesCheckboxStatuses = CouponTab.couponCategoriesCheckboxStatuses.size();
        for (int i = 0; i < couponCategoriesCheckboxStatuses; i++) {
            if (CouponTab.couponCategoriesCheckboxStatuses.get(i).getFilterChecked())
                sb.append(CouponTab.couponCategoriesCheckboxStatuses.get(i).getFilterID()).append(",");
        }
        if (!sb.toString().isEmpty()) {
            sb.deleteCharAt(sb.lastIndexOf(","));
            filtersResult.add(sb.toString());
        } else
            filtersResult.add("");
        sb.delete(0, sb.capacity());
        return filtersResult;
    }

    @Override
    public void PostBackExecutionJSON(JSONTokener Result) {
        try {
            Log.e("all coupons response", "-------------");
            if (loadMoreFlag) {
                loadMoreFlag = false;
                Boolean loadMoreOperation = false;

                switch (loadMoreType) {
                    case "loyalty":
                        loadMoreOperation = forYou2.get(0).addAll(ParseData.NEW_ParseGetCoupons2(Result, "").get(0));
                        Log.e(loadMoreOperation + " more coupons ", forYou2.size() + "   after request");
                        if (!loadMoreOperation) {
                            _utility.showMessage(getResources().getString(R.string.no_more));
                            loyalty_loadMore.setVisibility(View.GONE);
                            return;
                        }
                        break;
                    case "specialOffers":
                        loadMoreOperation = forYou2.get(1).addAll(ParseData.NEW_ParseGetCoupons2(Result, "").get(1));
                        Log.e(loadMoreOperation + " more coupons ", forYou2.size() + "   after request");
                        if (!loadMoreOperation) {
                            _utility.showMessage(getResources().getString(R.string.no_more));
                            specialOffer_loadMore.setVisibility(View.GONE);
                            return;
                        }
                        break;
                    case "challenges":
                        loadMoreOperation = forYou2.get(2).addAll(ParseData.NEW_ParseGetCoupons2(Result, "").get(2));
                        Log.e(loadMoreOperation + " more coupons ", forYou2.get(2).size() + "   after request");
                        if (!loadMoreOperation) {
                            _utility.showMessage(getResources().getString(R.string.no_more));
                            challenges_loadMore.setVisibility(View.GONE);
                            return;
                        }
                        break;
                    case "luckyHour":
                        loadMoreOperation = forYou2.get(3).addAll(ParseData.NEW_ParseGetCoupons2(Result, "").get(3));
                        Log.e(loadMoreOperation + " more coupons ", forYou2.get(3).size() + "   after request");
                        if (!loadMoreOperation) {
                            _utility.showMessage(getResources().getString(R.string.no_more));
                            luckyHour_loadMore.setVisibility(View.GONE);
                            return;
                        }
                        break;
                    case "specialOccasion":
                        loadMoreOperation = forYou2.get(4).addAll(ParseData.NEW_ParseGetCoupons2(Result, "").get(4));
                        Log.e(loadMoreOperation + " more coupons ", forYou2.get(4).size() + "   after request");
                        if (!loadMoreOperation) {
                            _utility.showMessage(getResources().getString(R.string.no_more));
                            specialOccasions_loadMore.setVisibility(View.GONE);
                            return;
                        }
                        break;
                    case "forYou":
                        loadMoreOperation = forYou2.get(5).addAll(ParseData.NEW_ParseGetCoupons2(Result, "").get(5));
                        Log.e(loadMoreOperation + " more coupons ", forYou2.get(5).size() + "   after request");
                        if (!loadMoreOperation) {
                            _utility.showMessage(getResources().getString(R.string.no_more));
                            forYou_loadMore.setVisibility(View.GONE);
                            return;
                        }
                        break;

                }


            } else
                forYou2 = ParseData.NEW_ParseGetCoupons2(Result, "");

            if (forYou2 != null) {
//                Log.e("all coupons ", forYou2.size() + "   after request");

                if (forYou2.size() != 0) {
                    CouponTab.couponPageOnlyYou++;
                    sortAndShowCoupons();
                } else {
                    noCouponsStatus();
                }
            } else {
//                allCopAd = new ForYouAdapter2(getActivity(), new ArrayList<Coupon>());
//                lvAllCoupons.setAdapter(allCopAd);
//                loadMore.setVisibility(View.GONE);
//                tv_no_cop.setVisibility(View.VISIBLE);
                _utility.showMessage(getResources().getString(R.string.ws_err));
                customLoading.hideProgress();
            }
        } catch (Exception e) {
            e.printStackTrace();
            _utility.showMessage(getResources().getString(R.string.ws_err));
        } finally {
            customLoading.hideProgress();
            call = null;

        }

    }

    private void noCouponsStatus() {
//        loadMore.setVisibility(View.GONE);
//        tv_no_cop.setVisibility(View.VISIBLE);
//        lvAllCoupons.setVisibility(View.GONE);
    }


    /**
     * sort all coupons according to its popularity .. the brand which have more "add to fav" action will be the most popular
     * //
     */
//    void sortMostPopular() {
//        Log.e("AllCouponsFragment", "sortMostPopular fn.");
//        ArrayList<Coupon> tmp;
//        for (int k = 0; k < forYou2.size(); k++) {
////            for (int y = 0; y < forYou2.get(k).size(); y++) {
//
//            tmp = new ArrayList<>();
//            Integer[] sortedCount = new Integer[forYou2.get(k).size()];
//            int allCouponsSize = forYou2.get(k).size();
//            for (int i = 0; i < allCouponsSize; i++) {
//                sortedCount[i] = forYou2.get(k).get(i).getCouponSavedByCount();
//            }
//            Arrays.sort(sortedCount, Collections.reverseOrder());
//            int sortCountedLength = sortedCount.length;
//            for (int j = 0; j < sortCountedLength; j++) {
//                for (int i = 0; i < forYou2.get(k).size(); i++) {
//                    if (forYou2.get(k).get(i).getCouponSavedByCount() == sortedCount[j]) {
//                        tmp.add(forYou2.get(k).get(i));
//                        forYou2.get(k).remove(i);
//                        break;
//                    }
//                }
//            }
//
//            forYou2.get(k).addAll(tmp);
//            tmp = null;
//
////            }
//        }
//

//
//
//
//    }

    void clearListViews() {
        loyalty_ls.setAdapter(null);
        _utility.setListViewHeightBasedOnChildren(loyalty_ls);

        specialOffer_ls.setAdapter(null);
        _utility.setListViewHeightBasedOnChildren(specialOffer_ls);

        challenges_ls.setAdapter(null);
        _utility.setListViewHeightBasedOnChildren(challenges_ls);

        luckyHour_ls.setAdapter(null);
        _utility.setListViewHeightBasedOnChildren(luckyHour_ls);

        specialOccasions_ls.setAdapter(null);
        _utility.setListViewHeightBasedOnChildren(specialOccasions_ls);

        forYou_ls.setAdapter(null);
        _utility.setListViewHeightBasedOnChildren(forYou_ls);
    }

    /*
        at the implementation of Fragment, you'll see that when moving to the detached state,
         it'll reset its internal state. However, it doesn't reset mChildFragmentManager
          (this is a bug in the current version of the support library).
           This causes it to not reattach the child fragment manager when the Fragment is reattached,
            causing the exception Activity has been destroyed
         */
    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


//    ListView lvAllCoupons;
//    ForYouAdapter2 forYouCopAd;
//    ImageView btn_map;
//    LinearLayout lin_filter, lin_sort;
//    TextView tv_loyalty, tv_specialoffer, tv_challenges, tv_happyOcc, tv_luckyHour, tv_forYou, tv_no_cop;
//    Button loadMore;
//    boolean loadMoreFlag;
//    Utility _utility;
//    private JSONAsync call;
//    private CustomLoading customLoading;
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View root = inflater.inflate(R.layout.cop_tab_only_for_you, null);
//        Log.e("0pen", "OnlyForYou\n-------");
//        _utility = new Utility(getContext());
//        customLoading = new CustomLoading(_utility.getCurrentActivity());
//        MainActivity.langResult = MainActivity.sharedPreferences.getString("lang", "en");
//        tv_specialoffer = (TextView) root.findViewById(R.id.tv_specialoffer2);
//        tv_loyalty = (TextView) root.findViewById(R.id.tv_loyalty2);
//        tv_challenges = (TextView) root.findViewById(R.id.tv_challenges2);
//        tv_happyOcc = (TextView) root.findViewById(R.id.tv_happyOccasion2);
//        tv_luckyHour = (TextView) root.findViewById(R.id.tv_luckyHour2);
//        tv_forYou = (TextView) root.findViewById(R.id.tv_forYou2);
//        tv_forYou.setVisibility(View.GONE);
//        tv_no_cop = (TextView) root.findViewById(R.id.tv_no_cop2);
//        tv_no_cop.setVisibility(View.GONE);
//
//        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.onlyYouCoupons_refresh_layout);
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                swipeRefreshLayout.setRefreshing(true);
//                CouponTab.couponPageOOnlyYou = 1;
//                customLoading.showProgress(_utility.getCurrentActivity());
//                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPageOOnlyYou, "forYou");//refresh
//                swipeRefreshLayout.setRefreshing(false);
//            }
//        });
//
//
//        btn_map = (ImageView) root.findViewById(R.id.btn_map2);
//
//        btn_map.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (CouponTab.allCoupons == null || CouponTab.allCoupons.isEmpty()) {
//                    _utility.showMessage(_utility.getCurrentActivity().getResources().getString(R.string.no_branches));
//                    return;
//                }
//                CouponTab.couponsBranches = new ArrayList<>();
//                for (int i = 0; i < CouponTab.forYou2.size(); i++) {
//                    for (int j = 0; j < CouponTab.forYou2.get(i).getCouponBrand().getBrandBranches().size(); j++) {
//                        CouponTab.forYou2.get(i).getCouponBrand().getBrandBranches().get(j).
//                                setOffName(CouponTab.forYou2.get(i).getCouponName());
//                        CouponTab.couponsBranches.add(CouponTab.forYou2.get(i).getCouponBrand().getBrandBranches().get(j));
//                    }
//                }
//                Log.e("couponsBranches", CouponTab.couponsBranches.size() + "--");
//                MainActivity.mapType = "allBranches";
//                MainActivity.onActivityResultType = 2;
//
//                if (_utility.checkGPS()) {
//                    if (MainActivity.latitudeSrc != 0.0) {
//                        if (_utility.isConnectingToInternet_ping()) {
//                            Fragment fragment = new FragmentMap();
//                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                            fragmentTransaction.replace(R.id.fragment_container, fragment);
//                            fragmentTransaction.addToBackStack(null);
//                            fragmentTransaction.commit();
//                        } else
//                            _utility.showMessage(getResources().getString(R.string.no_net));
//                    }
//                } else {
//                    MainActivity.alertNoGPS.show();
//                }
//            }
//        });
//
//        lvAllCoupons = (ListView) root.findViewById(R.id.lv_all_coupons2);
//        lin_filter = (LinearLayout) root.findViewById(R.id.lin_filter2);
//        lin_sort = (LinearLayout) root.findViewById(R.id.lin_sort2);
//        loadMore = (Button) ((LayoutInflater) _utility.getCurrentActivity().
//                getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.lv_footer, null, false);
//        lvAllCoupons.addFooterView(loadMore);
//
//        loadMore.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                loadMoreFlag = true;
//                customLoading.showProgress(_utility.getCurrentActivity());
//                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPageOOnlyYou, "forYou");//loadMore
//            }
//        });
//
//
//        lin_sort.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Fragment fragment = new SortFragment();
//                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.fragment_container, fragment);
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commit();
//            }
//        });
//
//        lin_filter.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Fragment fragment = new CouponsFilterFragment();
//                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.fragment_container, fragment);
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commit();
//            }
//        });
//        setCoupTypeBackground();
//
//        tv_loyalty.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                customLoading.showProgress(_utility.getCurrentActivity());
//                CouponTab.selectedCopTypeIDOnlyYou = 95;
//                CouponTab.selectedCoupTypeOnlyYou = "loyalty";
//                CouponTab.couponPageOOnlyYou = 1;
//                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPageOOnlyYou, CouponTab.selectedCoupTypeOnlyYou);//loyalty
//                tv_forYou.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_loyalty.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_green));
//                tv_specialoffer.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_challenges.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_luckyHour.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_happyOcc.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//            }
//        });
//
//        tv_specialoffer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                customLoading.showProgress(_utility.getCurrentActivity());
//                CouponTab.selectedCopTypeIDOnlyYou = 93;
//                CouponTab.selectedCoupTypeOnlyYou = "specialOffers";
//                CouponTab.couponPageOOnlyYou = 1;
//
//                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPageOOnlyYou, CouponTab.selectedCoupTypeOnlyYou);//special offer
//                tv_forYou.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_specialoffer.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_green));
//                tv_loyalty.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_challenges.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_luckyHour.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_happyOcc.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//            }
//        });
//        tv_challenges.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                customLoading.showProgress(_utility.getCurrentActivity());
//                CouponTab.selectedCopTypeIDOnlyYou = 232;
//                CouponTab.selectedCoupTypeOnlyYou = "challenges";
//                CouponTab.couponPageOOnlyYou = 1;
//
//                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPageOOnlyYou, CouponTab.selectedCoupTypeOnlyYou);//challenge
//                tv_forYou.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_challenges.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_green));
//                tv_loyalty.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_specialoffer.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_luckyHour.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_happyOcc.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//            }
//        });
//        tv_luckyHour.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                customLoading.showProgress(_utility.getCurrentActivity());
//                CouponTab.selectedCopTypeIDOnlyYou = 238;
//                CouponTab.selectedCoupTypeOnlyYou = "luckyHour";
//                CouponTab.couponPageOOnlyYou = 1;
//
//                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPageOOnlyYou, CouponTab.selectedCoupTypeOnlyYou);//lucky hour
//                tv_forYou.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_luckyHour.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_green));
//                tv_loyalty.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_challenges.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_specialoffer.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_happyOcc.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//            }
//        });
//        tv_happyOcc.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                customLoading.showProgress(_utility.getCurrentActivity());
//                CouponTab.selectedCopTypeIDOnlyYou = 240;
//                CouponTab.selectedCoupTypeOnlyYou = "specialOccasion";
//                CouponTab.couponPageOOnlyYou = 1;
//
//                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPageOOnlyYou, CouponTab.selectedCoupTypeOnlyYou);//happy Occ
//                tv_forYou.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_happyOcc.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_green));
//                tv_loyalty.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_challenges.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_luckyHour.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_specialoffer.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//            }
//        });
//
//        tv_forYou.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                customLoading.showProgress(_utility.getCurrentActivity());
//                CouponTab.selectedCoupTypeOnlyYou = "forYou";
//                CouponTab.selectedCopTypeIDOnlyYou = 242;
//                CouponTab.couponPageOOnlyYou = 1;
//
//                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPageOOnlyYou, CouponTab.selectedCoupTypeOnlyYou);//happy Occ
//                tv_forYou.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_green));
//                tv_happyOcc.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_loyalty.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_challenges.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_luckyHour.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//                tv_specialoffer.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_red));
//            }
//        });
//
//        ((MainActivity) getActivity()).setForYouCouponsRefreshListener(new MainActivity.FragmentRefreshListener() {
//            @Override
//            public void onRefresh() {
//                Log.e("forYouCoupon", "refresh");
//                if (_utility.isConnectingToInternet_ping()) {
//                    Log.e("get from cloud", "for  You");
//                    CouponTab.couponPageOOnlyYou = 1;
//                    requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPageOOnlyYou, "forYou");//on create
//                } else {
//
//                    customLoading.hideProgress();
//                    _utility.showMessage(getResources().getString(R.string.no_net));
//                    noCouponsStatus();
//                }
//            }
//        });
//        loadMoreFlag = false;
//        if (CouponTab.forYou2 != null) {
//            Log.e("coupons only for,,,", "!=null");
//            if (CouponTab.forYou2.size() > 0) {
//                Log.e("get locally", "for  You= " + CouponTab.forYou2.size());
//                sortAndShowCoupons();
//            } else {
//                Log.e("coupons only for,,,", "empty");
//                loadMore.setVisibility(View.GONE);
//                tv_no_cop.setVisibility(View.VISIBLE);
//            }
//
//            customLoading.hideProgress();
//
//        } else {
//            Log.e("coupons only for,,,", "is null");
//            if (_utility.isConnectingToInternet_ping()) {
//                Log.e("get from cloud", "for  You");
//                CouponTab.couponPageOOnlyYou = 1;
//                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPageOOnlyYou, "forYou");//on create
//            } else {
//
//                customLoading.hideProgress();
//                _utility.showMessage(getResources().getString(R.string.no_net));
//                noCouponsStatus();
//            }
//        }
//
//        return root;
//    }
//
//    private void sortAndShowCoupons() {
//        switch (CouponTab.sortState) {
//            case 1:
//                Collections.sort(CouponTab.forYou2, Collections.reverseOrder());
//                break;
//            case 2:
//                Collections.sort(CouponTab.forYou2);
//                break;
//            case 3:
//                sortMostPopular();
//                break;
//            default:
//                break;
//        }
//        tv_no_cop.setVisibility(View.GONE);
//        lvAllCoupons.setVisibility(View.VISIBLE);
//
//        forYouCopAd = new ForYouAdapter2(getActivity(), CouponTab.forYou2);
//        lvAllCoupons.setAdapter(forYouCopAd);
//        lvAllCoupons.setSelectionFromTop(CouponTab.forYou2.size() - 4, 0);
//
//        lvAllCoupons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                try {
//                    MainActivity.selectedCoupon = (Coupon) CouponTab.forYou2.get(position).clone();
//                } catch (CloneNotSupportedException e) {
//                    e.printStackTrace();
//                }
//                CouponTab.myCoupon = false;
//                Fragment fragment = new SingleCouponFragment();
//                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.fragment_container, fragment);
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commit();
//            }
//        });
//
//        loadMore.setVisibility(View.VISIBLE);
//    }
//
//    void setCoupTypeBackground() {
//        try {
//            switch (CouponTab.selectedCopTypeIDOnlyYou) {
//                case 93:
//                    tv_specialoffer.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_green));
//                    break;
//                case 95:
//                    tv_loyalty.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_green));
//                    break;
//                case 232:
//                    tv_challenges.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_green));
//                    break;
//                case 238:
//                    tv_luckyHour.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_green));
//                    break;
//                case 240:
//                    tv_happyOcc.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_green));
//                    break;
//                case 242:
//                    tv_forYou.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_tv_green));
//                    break;
//
//                default:
//                    break;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        if (isVisibleToUser) {
//            Log.e("00vis00", "onlyForU - visible");
//        } else {
//            Log.e("00vis00", "onlyForU - notVisible");
//        }
//    }
//
//    /**
//     * make a request to get all coupons
//     *
//     * @param limit
//     */
//    public void requestAllCoupons(int limit, int offset, String type) {
//        if (MainActivity.currentFan == null) {
//            noCouponsStatus();
//            customLoading.hideProgress();
//            return;
//        }
//
//        if (_utility.isConnectingToInternet_ping()) {
//
//            Log.e("all -- request " + type + " coupons", limit + "");
//            try {
//                ArrayList<String> allFilters = getFilters();
//                JSONWebServices service = new JSONWebServices(OnlyForYouCouponsFragment2.this);
//                List<NameValuePair> nameValuePairs = new ArrayList<>();
//                JSONObject filters = new JSONObject();
//                Log.e("customerTypes filter", allFilters.get(1) + "---");
//                JSONArray clients = new JSONArray();
//                String[] splitter = allFilters.get(1).split(",");
//                if (!allFilters.get(1).isEmpty()) {
//                    for (int i = 0; i < splitter.length; i++) {
//                        Log.e("filters", splitter[i] + "");
//                        clients.put(splitter[i]);
//                    }
//                }
//                filters.put("clients", clients);
//                Log.e("favoriteBrands filter", allFilters.get(2) + "---");
//                JSONArray brands = new JSONArray();
//                if (!allFilters.get(2).isEmpty()) {
//                    splitter = allFilters.get(2).split(",");
//                    for (int i = 0; i < splitter.length; i++) {
//                        Log.e("filters", splitter[i] + "");
//                        brands.put(splitter[i]);
//                    }
//                }
//                filters.put("brands", brands);
//                Log.e("favoriteCateg filter", allFilters.get(3) + "---");
//                JSONArray categories = new JSONArray();
//                if (!allFilters.get(3).isEmpty()) {
//                    splitter = allFilters.get(3).split(",");
//                    for (int i = 0; i < splitter.length; i++) {
//                        Log.e("filters", splitter[i] + "");
//                        categories.put(Integer.valueOf(splitter[i]));
//                    }
//                }
//                filters.put("categories", categories);
//                filters.put("all", false);
//                filters.put("coupons", new JSONArray());
//                filters.put("country", MainActivity.currentFan != null ? MainActivity.currentFan.getFanCountryID() : 36);
//
//                JSONObject couponType = new JSONObject();
//                couponType.put("sortBy", CouponTab.sortState);
//                couponType.put("limit", limit);
//                couponType.put("page", offset);
//
//                JSONObject sections = new JSONObject();
//                sections.put(CouponTab.selectedCoupTypeOnlyYou, couponType);
//
//                nameValuePairs.add(new BasicNameValuePair("sections", sections.toString()));
//                nameValuePairs.add(new BasicNameValuePair("filters", filters.toString()));
//
//                call = service.getAllCoupons(nameValuePairs);
//
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        } else {
//            customLoading.hideProgress();
//            _utility.showMessage(getResources().getString(R.string.no_net));
//        }
//
//    }
//
//    /**
//     * make a request to get all filters
//     *
//     * @return
//     */
//    ArrayList<String> getFilters() {
//        StringBuilder sb = new StringBuilder();
//        ArrayList<String> filtersResult = new ArrayList<>();
//
//        int verySpecialsCheckboxStatusesSize = CouponTab.verySpecialsCheckboxStatuses.size();
//        for (int i = 0; i < verySpecialsCheckboxStatusesSize; i++) {
//            if (CouponTab.verySpecialsCheckboxStatuses.get(i).getFilterChecked())
//                sb.append(CouponTab.verySpecialsCheckboxStatuses.get(i).getFilterID()).append(",");
//        }
//        if (!sb.toString().isEmpty()) {
//            sb.deleteCharAt(sb.lastIndexOf(","));
//            filtersResult.add(sb.toString());
//        } else
//            filtersResult.add("");
//        sb.delete(0, sb.capacity());
//        int favouriteBrandCheckboxStatuses = CouponTab.favouriteBrandCheckboxStatuses.size();
//        for (int i = 0; i < favouriteBrandCheckboxStatuses; i++) {
//            if (CouponTab.favouriteBrandCheckboxStatuses.get(i).getFilterChecked())
//                sb.append(CouponTab.favouriteBrandCheckboxStatuses.get(i).getFilterID()).append(",");
//        }
//        if (!sb.toString().isEmpty()) {
//            sb.deleteCharAt(sb.lastIndexOf(","));
//            filtersResult.add(sb.toString());
//        } else
//            filtersResult.add("");
//        sb.delete(0, sb.capacity());
//        int interestedINCheckboxStatuses = CouponTab.interestedINCheckboxStatuses.size();
//        for (int i = 0; i < interestedINCheckboxStatuses; i++) {
//            if (CouponTab.interestedINCheckboxStatuses.get(i).getFilterChecked())
//                sb.append(CouponTab.interestedINCheckboxStatuses.get(i).getFilterID()).append(",");
//        }
//        if (!sb.toString().isEmpty()) {
//            sb.deleteCharAt(sb.lastIndexOf(","));
//            filtersResult.add(sb.toString());
//        } else
//            filtersResult.add("");
//
//        sb.delete(0, sb.capacity());
//        int couponCategoriesCheckboxStatuses = CouponTab.couponCategoriesCheckboxStatuses.size();
//        for (int i = 0; i < couponCategoriesCheckboxStatuses; i++) {
//            if (CouponTab.couponCategoriesCheckboxStatuses.get(i).getFilterChecked())
//                sb.append(CouponTab.couponCategoriesCheckboxStatuses.get(i).getFilterID()).append(",");
//        }
//        if (!sb.toString().isEmpty()) {
//            sb.deleteCharAt(sb.lastIndexOf(","));
//            filtersResult.add(sb.toString());
//        } else
//            filtersResult.add("");
//        sb.delete(0, sb.capacity());
//        return filtersResult;
//    }
//
//    @Override
//    public void PostBackExecutionJSON(JSONTokener Result) {
//        try {
//            if (loadMoreFlag) {
//                loadMoreFlag = false;
//
//                Boolean loadMoreOperation = CouponTab.forYou2.addAll(ParseData.NEW_ParseGetCoupons2(Result, "forYou"));
//                Log.e(loadMoreOperation + " more coupons ", CouponTab.forYou2.size() + "   after request");
//                if (!loadMoreOperation)
//                    _utility.showMessage(getResources().getString(R.string.no_more));
//            } else
//                CouponTab.forYou2 = ParseData.NEW_ParseGetCoupons2(Result, "forYou");
//
//            if (CouponTab.forYou2 != null) {
//                Log.e("for  You ", CouponTab.forYou2.size() + "   after request");
//
//                if (CouponTab.forYou2.size() != 0) {
//                    CouponTab.couponPageOOnlyYou++;
//
//                    sortAndShowCoupons();
//                } else {
//                    noCouponsStatus();
//                }
//            } else {
//                forYouCopAd = new ForYouAdapter2(getActivity(), new ArrayList<Coupon>());
//                lvAllCoupons.setAdapter(forYouCopAd);
//                loadMore.setVisibility(View.GONE);
//                tv_no_cop.setVisibility(View.VISIBLE);
//                _utility.showMessage(getResources().getString(R.string.ws_err));
//                customLoading.hideProgress();
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            _utility.showMessage(getResources().getString(R.string.ws_err));
//
//        } finally {
//            customLoading.hideProgress();
//            call = null;
//        }
//
//    }
//
//    private void noCouponsStatus() {
//        loadMore.setVisibility(View.GONE);
//        tv_no_cop.setVisibility(View.VISIBLE);
//        lvAllCoupons.setVisibility(View.GONE);
//    }
//
////
////    /**
////     * sort all coupons according to its popularity .. the brand which have more "add to fav" action will be the most popular
////     */
////    void sortMostPopular() {
////        Log.e("forYouCouponsFragment", "sortMostPopular fn.");
////        ArrayList<Coupon> tmp = new ArrayList<>();
////        Integer[] sortedCount = new Integer[CouponTab.forYou2.size()];
////        int allCouponsSize = CouponTab.forYou2.size();
////        for (int i = 0; i < allCouponsSize; i++) {
////            sortedCount[i] = CouponTab.forYou2.get(i).getCouponSavedByCount();
////        }
////        Arrays.sort(sortedCount, Collections.reverseOrder());
////        int sortCountedLength = sortedCount.length;
////        for (int j = 0; j < sortCountedLength; j++) {
////            for (int i = 0; i < CouponTab.forYou2.size(); i++) {
////                if (CouponTab.forYou2.get(i).getCouponSavedByCount() == sortedCount[j]) {
////                    tmp.add(CouponTab.forYou2.get(i));
////                    CouponTab.forYou2.remove(i);
////                    break;
////                }
////            }
////        }
////
////        CouponTab.forYou2 = tmp;
////        tmp = null;
////    }
///*
//    at the implementation of Fragment, you'll see that when moving to the detached state,
//     it'll reset its internal state. However, it doesn't reset mChildFragmentManager
//      (this is a bug in the current version of the support library).
//       This causes it to not reattach the child fragment manager when the Fragment is reattached,
//        causing the exception Activity has been destroyed
//     */
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//
//        try {
//            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
//            childFragmentManager.setAccessible(true);
//            childFragmentManager.set(this, null);
//
//        } catch (NoSuchFieldException e) {
//            throw new RuntimeException(e);
//        } catch (IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
