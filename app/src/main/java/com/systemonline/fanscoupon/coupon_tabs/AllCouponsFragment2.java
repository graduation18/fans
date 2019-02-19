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

import static com.systemonline.fanscoupon.coupon_tabs.CouponTab.allCoupons2;


public class AllCouponsFragment2 extends BaseFragment {
    ListView loyalty_ls, specialOffer_ls, challenges_ls, luckyHour_ls, specialOccasions_ls, forYou_ls;
    //    AllCouponsAdapter2 allCopAd;
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

        View root = inflater.inflate(R.layout.cop_tab_all_coupons2, null);
        _utility = new Utility(getContext());
        customLoading = new CustomLoading(_utility.getCurrentActivity());
        Log.e("0pen", "AllCoupon\n-------" + MainActivity.langResult);
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
                CouponTab.couponPage = 1;
                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPage, "all");//refresh
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allCoupons2 == null || allCoupons2.isEmpty()) {
                    _utility.showMessage(_utility.getCurrentActivity().getResources().getString(R.string.no_branches));
                    return;
                }

                CouponTab.couponsBranches = new ArrayList<>();
                for (int k = 0; k < allCoupons2.size(); k++) {

                    for (int i = 0; i < allCoupons2.get(k).size(); i++) {
                        for (int j = 0; j < allCoupons2.get(k).get(i).getCouponBrand().getBrandBranches().size(); j++) {
                            allCoupons2.get(k).get(i).getCouponBrand().getBrandBranches().get(j).
                                    setOffName(allCoupons2.get(k).get(i).getCouponName());
                            CouponTab.couponsBranches.add(allCoupons2.get(k).get(i).getCouponBrand().getBrandBranches().get(j));
                        }
                    }
                }
                Log.e("couponsBranches", CouponTab.couponsBranches.size() + "--");
//                for (int i = 0; i < CouponTab.couponsBranches.size(); i++) {
//                    Log.e("all couponsBranches", CouponTab.couponsBranches.get(i).getBranchName());
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
                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPage, "loyalty");//loadMore loyalty
            }
        });
        specialOffer_loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMoreType = "specialOffers";
                loadMoreFlag = true;
                customLoading.showProgress(_utility.getCurrentActivity());
                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPage, "specialOffers");//loadMore specialOffers
            }
        });
        challenges_loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMoreType = "challenges";

                loadMoreFlag = true;
                customLoading.showProgress(_utility.getCurrentActivity());
                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPage, "challenges");//loadMore challenges
            }
        });
        luckyHour_loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMoreType = "luckyHour";

                loadMoreFlag = true;
                customLoading.showProgress(_utility.getCurrentActivity());
                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPage, "luckyHour");//loadMore luckyHour
            }
        });
        specialOccasions_loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMoreType = "specialOccasion";

                loadMoreFlag = true;
                customLoading.showProgress(_utility.getCurrentActivity());
                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPage, "specialOccasion");//loadMore specialOccasion
            }
        });
        forYou_loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMoreType = "forYou";

                loadMoreFlag = true;
                customLoading.showProgress(_utility.getCurrentActivity());
                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPage, "forYou");//loadMore forYou
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
//                CouponTab.couponPage = 1;
//                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPage, CouponTab.selectedCoupType);//loyalty
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
//                CouponTab.couponPage = 1;
//
//                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPage, CouponTab.selectedCoupType);//special offer
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
//                CouponTab.couponPage = 1;
//
//                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPage, CouponTab.selectedCoupType);//challenge
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
//                CouponTab.couponPage = 1;
//
//                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPage, CouponTab.selectedCoupType);//lucky hour
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
//                CouponTab.couponPage = 1;
//
//                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPage, CouponTab.selectedCoupType);//happy Occ
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
//                CouponTab.couponPage = 1;
//
//                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPage, CouponTab.selectedCoupType);//for you
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
                    CouponTab.couponPage = 1;
                    requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPage, "all");//on create
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
            Log.e("00vis00", "allCop - visible");
        } else {
            Log.e("00vis00", "allCop - notVisible");
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
        if (allCoupons2 != null && Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            Log.e("coupons,,,", "!=null");
            if (allCoupons2.size() > 0) {
                Log.e("get locally", "all coupons= " + allCoupons2.size());
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
                CouponTab.couponPage = 1;
                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPage, "all");//on create
            } else {
                customLoading.hideProgress();
                _utility.showMessage(getResources().getString(R.string.no_net));
                noCouponsStatus();
            }
        }
    }

    private void sortAndShowCoupons() {

        clearListViews();
        if (allCoupons2.get(0).isEmpty()) {
            loyalty_loadMore.setText(getString(R.string.no_coupons));
            loyalty_loadMore.setEnabled(false);
            if (couponsRequestType.equals("filter")) {
                loyalty_loadMore.setVisibility(View.GONE);
                rel_loyalty.setVisibility(View.GONE);
            }
        } else {
            AllCouponsAdapter2 loyalty_allCopAd = new AllCouponsAdapter2(getActivity(), allCoupons2.get(0), 95, "loyalty");
            loyalty_ls.setAdapter(loyalty_allCopAd);
            _utility.setListViewHeightBasedOnChildren(loyalty_ls);
//            loyalty_ls.setSelectionFromTop(allCoupons2.size() - 4, 0);
            loyalty_ls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        MainActivity.selectedCoupon = (Coupon) allCoupons2.get(0).get(position).clone();
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

        if (allCoupons2.get(1).isEmpty()) {
            specialOffer_loadMore.setText(getString(R.string.no_coupons));
            specialOffer_loadMore.setEnabled(false);
            if (couponsRequestType.equals("filter")) {
                specialOffer_loadMore.setVisibility(View.GONE);
                rel_specialOffer.setVisibility(View.GONE);
            }
        } else {
            AllCouponsAdapter2 specialOffer_allCopAd = new AllCouponsAdapter2(getActivity(), allCoupons2.get(1), 93, "specialOffer");
            specialOffer_ls.setAdapter(specialOffer_allCopAd);
            _utility.setListViewHeightBasedOnChildren(specialOffer_ls);
//            specialOffer_ls.setSelectionFromTop(allCoupons2.size() - 4, 0);
            specialOffer_ls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        MainActivity.selectedCoupon = (Coupon) allCoupons2.get(1).get(position).clone();
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

        if (allCoupons2.get(2).isEmpty()) {
            challenges_loadMore.setText(getString(R.string.no_coupons));
            challenges_loadMore.setEnabled(false);
            if (couponsRequestType.equals("filter")) {
                challenges_loadMore.setVisibility(View.GONE);
                rel_challenges.setVisibility(View.GONE);
            }
        } else {
            AllCouponsAdapter2 challenges_allCopAd = new AllCouponsAdapter2(getActivity(), allCoupons2.get(2), 232, "challenges");
            challenges_ls.setAdapter(challenges_allCopAd);
            _utility.setListViewHeightBasedOnChildren(challenges_ls);
//            challenges_ls.setSelectionFromTop(allCoupons2.size() - 4, 0);
            challenges_ls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        MainActivity.selectedCoupon = (Coupon) allCoupons2.get(2).get(position).clone();
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

        if (allCoupons2.get(3).isEmpty()) {
            luckyHour_loadMore.setText(getString(R.string.no_coupons));
            luckyHour_loadMore.setEnabled(false);
            if (couponsRequestType.equals("filter")) {
                luckyHour_loadMore.setVisibility(View.GONE);
                rel_luckyHour.setVisibility(View.GONE);
            }
        } else {
            AllCouponsAdapter2 luckyHour_allCopAd = new AllCouponsAdapter2(getActivity(), allCoupons2.get(3), 238, "luckyHour");
            luckyHour_ls.setAdapter(luckyHour_allCopAd);
            _utility.setListViewHeightBasedOnChildren(luckyHour_ls);
//            luckyHour_ls.setSelectionFromTop(allCoupons2.size() - 4, 0);
            luckyHour_ls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        MainActivity.selectedCoupon = (Coupon) allCoupons2.get(3).get(position).clone();
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

        if (allCoupons2.get(4).isEmpty()) {
            specialOccasions_loadMore.setText(getString(R.string.no_coupons));
            specialOccasions_loadMore.setEnabled(false);
            if (couponsRequestType.equals("filter")) {
                specialOccasions_loadMore.setVisibility(View.GONE);
                rel_specialOccasions.setVisibility(View.GONE);
            }
        } else {
            AllCouponsAdapter2 specialOccasions_allCopAd = new AllCouponsAdapter2(getActivity(), allCoupons2.get(4), 240, "specialOccasions");
            specialOccasions_ls.setAdapter(specialOccasions_allCopAd);
            _utility.setListViewHeightBasedOnChildren(specialOccasions_ls);
//            specialOccasions_ls.setSelectionFromTop(allCoupons2.size() - 4, 0);
            specialOccasions_ls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        MainActivity.selectedCoupon = (Coupon) allCoupons2.get(4).get(position).clone();
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

        if (allCoupons2.get(5).isEmpty()) {
            forYou_loadMore.setText(getString(R.string.no_coupons));
            forYou_loadMore.setEnabled(false);
            if (couponsRequestType.equals("filter")) {
                forYou_loadMore.setVisibility(View.GONE);
                rel_forYou.setVisibility(View.GONE);
            }
        } else {
            AllCouponsAdapter2 forYou_allCopAd = new AllCouponsAdapter2(getActivity(), allCoupons2.get(5), 242, "forYou");
            forYou_ls.setAdapter(forYou_allCopAd);
            _utility.setListViewHeightBasedOnChildren(forYou_ls);
//            forYou_ls.setSelectionFromTop(allCoupons2.size() - 4, 0);
            forYou_ls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        MainActivity.selectedCoupon = (Coupon) allCoupons2.get(5).get(position).clone();
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
        couponsRequestType = type;
        if (_utility.isConnectingToInternet_ping()) {
            Log.e("all -- request " + type + " coupons", limit + "");
            try {

                ArrayList<String> allFilters = getFilters();
                JSONWebServices service = new JSONWebServices(AllCouponsFragment2.this);
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

                filters.put("all", true);
                filters.put("coupons", new JSONArray());
                filters.put("country", MainActivity.sharedPreferences.getString("country", "36"));
//                filters.put("country", MainActivity.currentFan != null ? MainActivity.currentFan.getFanCountryID() : 36);

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
                        loadMoreOperation = allCoupons2.get(0).addAll(ParseData.NEW_ParseGetCoupons2(Result, "").get(0));
                        Log.e(loadMoreOperation + " more coupons ", allCoupons2.size() + "   after request");
                        if (!loadMoreOperation) {
                            _utility.showMessage(getResources().getString(R.string.no_more));
                            loyalty_loadMore.setVisibility(View.GONE);
                            return;
                        }
                        break;
                    case "specialOffers":
                        loadMoreOperation = allCoupons2.get(1).addAll(ParseData.NEW_ParseGetCoupons2(Result, "").get(1));
                        Log.e(loadMoreOperation + " more coupons ", allCoupons2.size() + "   after request");
                        if (!loadMoreOperation) {
                            _utility.showMessage(getResources().getString(R.string.no_more));
                            specialOffer_loadMore.setVisibility(View.GONE);
                            return;
                        }
                        break;
                    case "challenges":
                        loadMoreOperation = allCoupons2.get(2).addAll(ParseData.NEW_ParseGetCoupons2(Result, "").get(2));
                        Log.e(loadMoreOperation + " more coupons ", allCoupons2.get(2).size() + "   after request");
                        if (!loadMoreOperation) {
                            _utility.showMessage(getResources().getString(R.string.no_more));
                            challenges_loadMore.setVisibility(View.GONE);
                            return;
                        }

                        break;
                    case "luckyHour":
                        loadMoreOperation = allCoupons2.get(3).addAll(ParseData.NEW_ParseGetCoupons2(Result, "").get(3));
                        Log.e(loadMoreOperation + " more coupons ", allCoupons2.get(3).size() + "   after request");
                        if (!loadMoreOperation) {
                            _utility.showMessage(getResources().getString(R.string.no_more));
                            luckyHour_loadMore.setVisibility(View.GONE);
                            return;
                        }
                        break;
                    case "specialOccasion":
                        loadMoreOperation = allCoupons2.get(4).addAll(ParseData.NEW_ParseGetCoupons2(Result, "").get(4));
                        Log.e(loadMoreOperation + " more coupons ", allCoupons2.get(4).size() + "   after request");
                        if (!loadMoreOperation) {
                            _utility.showMessage(getResources().getString(R.string.no_more));
                            specialOccasions_loadMore.setVisibility(View.GONE);
                            return;
                        }
                        break;
                    case "forYou":
                        loadMoreOperation = allCoupons2.get(5).addAll(ParseData.NEW_ParseGetCoupons2(Result, "").get(5));
                        Log.e(loadMoreOperation + " more coupons ", allCoupons2.get(5).size() + "   after request");
                        if (!loadMoreOperation) {
                            _utility.showMessage(getResources().getString(R.string.no_more));
                            forYou_loadMore.setVisibility(View.GONE);
                            return;
                        }
                        break;

                }

            } else
                allCoupons2 = ParseData.NEW_ParseGetCoupons2(Result, "");

            if (allCoupons2 != null) {
//                Log.e("all coupons ", allCoupons2.size() + "   after request");

                if (allCoupons2.size() != 0) {
                    CouponTab.couponPage++;
                    sortAndShowCoupons();
                } else {
                    noCouponsStatus();
                }
            } else {
//                allCopAd = new AllCouponsAdapter2(getActivity(), new ArrayList<Coupon>());
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
//        for (int k = 0; k < allCoupons2.size(); k++) {
////            for (int y = 0; y < allCoupons2.get(k).size(); y++) {
//
//            tmp = new ArrayList<>();
//            Integer[] sortedCount = new Integer[allCoupons2.get(k).size()];
//            int allCouponsSize = allCoupons2.get(k).size();
//            for (int i = 0; i < allCouponsSize; i++) {
//                sortedCount[i] = allCoupons2.get(k).get(i).getCouponSavedByCount();
//            }
//            Arrays.sort(sortedCount, Collections.reverseOrder());
//            int sortCountedLength = sortedCount.length;
//            for (int j = 0; j < sortCountedLength; j++) {
//                for (int i = 0; i < allCoupons2.get(k).size(); i++) {
//                    if (allCoupons2.get(k).get(i).getCouponSavedByCount() == sortedCount[j]) {
//                        tmp.add(allCoupons2.get(k).get(i));
//                        allCoupons2.get(k).remove(i);
//                        break;
//                    }
//                }
//            }
//
//            allCoupons2.get(k).addAll(tmp);
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
        Log.e("allCoupons", "clear list views");
        loyalty_ls.setAdapter(null);
//        _utility.setListViewHeightBasedOnChildren(loyalty_ls);

        specialOffer_ls.setAdapter(null);
//        _utility.setListViewHeightBasedOnChildren(specialOffer_ls);

        challenges_ls.setAdapter(null);
//        _utility.setListViewHeightBasedOnChildren(challenges_ls);

        luckyHour_ls.setAdapter(null);
//        _utility.setListViewHeightBasedOnChildren(luckyHour_ls);

        specialOccasions_ls.setAdapter(null);
//        _utility.setListViewHeightBasedOnChildren(specialOccasions_ls);

        forYou_ls.setAdapter(null);
//        _utility.setListViewHeightBasedOnChildren(forYou_ls);
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


}
