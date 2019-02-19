package com.systemonline.fanscoupon.coupon_tabs;//package com.systemonline.fanscoupon.coupon_tabs;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentTransaction;
//import android.support.v4.widget.SwipeRefreshLayout;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import com.systemonline.fanscoupon.Base.BaseFragment;
//import com.systemonline.fanscoupon.FragmentMap;
//import com.systemonline.fanscoupon.Helpers.Utility;
//import com.systemonline.fanscoupon.MainActivity;
//import com.systemonline.fanscoupon.Model.Coupon;
//import com.systemonline.fanscoupon.Model.CustomLoading;
//import com.systemonline.fanscoupon.R;
//import com.systemonline.fanscoupon.SingleCouponFragment;
//import com.systemonline.fanscoupon.WebServices.JSONAsync;
//import com.systemonline.fanscoupon.WebServices.JSONWebServices;
//import com.systemonline.fanscoupon.WebServices.ParseData;
//
//import org.apache.http.NameValuePair;
//import org.apache.http.message.BasicNameValuePair;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.json.JSONTokener;
//
//import java.lang.reflect.Field;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//
//
//public class AllCouponsFragment extends BaseFragment {
//    ListView lvAllCoupons;
//    AllCouponsAdapter allCopAd;
//    ImageView btn_map;
//    LinearLayout lin_filter, lin_sort;
//    //    HorizontalScrollView couponsTypesBar;
//    TextView tv_loyalty, tv_specialoffer, tv_challenges, tv_happyOcc, tv_luckyHour, tv_forYou, tv_no_cop;
//    Button loadMore;
//    boolean loadMoreFlag;
//    JSONAsync call;
//    Utility _utility;
//    private CustomLoading customLoading;
//
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//
//        View root = inflater.inflate(R.layout.cop_tab_all_coupons, null);
//        _utility = new Utility(getContext());
//        customLoading = new CustomLoading(_utility.getCurrentActivity());
//        Log.e("0pen", "AllCoupon\n-------" + MainActivity.langResult);
//        MainActivity.langResult = MainActivity.sharedPreferences.getString("lang", "en");
//        tv_no_cop = (TextView) root.findViewById(R.id.tv_no_cop);
//        tv_no_cop.setVisibility(View.GONE);
//
//        btn_map = (ImageView) root.findViewById(R.id.btn_map);
////        couponsTypesBar=(HorizontalScrollView)root.findViewById(R.id.horizontalScrollView);
//
//        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.allCoupons_refresh_layout);
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                swipeRefreshLayout.setRefreshing(true);
//
//                customLoading.showProgress(_utility.getCurrentActivity());
//                CouponTab.couponPage = 1;
//                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPage, CouponTab.selectedCoupType);//refresh
//                swipeRefreshLayout.setRefreshing(false);
//            }
//        });
//
//        btn_map.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (CouponTab.allCoupons == null || CouponTab.allCoupons.isEmpty()) {
//                    _utility.showMessage(_utility.getCurrentActivity().getResources().getString(R.string.no_branches));
//                    return;
//                }
//                CouponTab.couponsBranches = new ArrayList<>();
//                for (int i = 0; i < CouponTab.allCoupons.size(); i++) {
//                    for (int j = 0; j < CouponTab.allCoupons.get(i).getCouponBrand().getBrandBranches().size(); j++) {
//                        CouponTab.allCoupons.get(i).getCouponBrand().getBrandBranches().get(j).
//                                setOffName(CouponTab.allCoupons.get(i).getCouponName());
//                        CouponTab.couponsBranches.add(CouponTab.allCoupons.get(i).getCouponBrand().getBrandBranches().get(j));
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
//        lvAllCoupons = (ListView) root.findViewById(R.id.lv_all_coupons);
//        tv_specialoffer = (TextView) root.findViewById(R.id.tv_specialoffer);
//        tv_loyalty = (TextView) root.findViewById(R.id.tv_loyalty);
//        tv_challenges = (TextView) root.findViewById(R.id.tv_challenges);
//        tv_happyOcc = (TextView) root.findViewById(R.id.tv_happyOccasion);
//        if (MainActivity.currentFan == null) {
//            tv_happyOcc.setVisibility(View.GONE);
//        }
//        tv_luckyHour = (TextView) root.findViewById(R.id.tv_luckyHour);
//        tv_forYou = (TextView) root.findViewById(R.id.tv_forYou);
////        tv_forYou.setVisibility(View.GONE);
//        lin_filter = (LinearLayout) root.findViewById(R.id.lin_filter);
//        lin_sort = (LinearLayout) root.findViewById(R.id.lin_sort);
//
//        loadMore = (Button) ((LayoutInflater) _utility.getCurrentActivity().
//                getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.lv_footer, null, false);
//        lvAllCoupons.addFooterView(loadMore);
//
//        loadMore.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                loadMoreFlag = true;
//                customLoading.showProgress(_utility.getCurrentActivity());
//                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPage, CouponTab.selectedCoupType);//loadMore
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
//
//
//        setCoupTypeBackground();
//
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
//
//        ((MainActivity) getActivity()).setAllCouponsRefreshListener(new MainActivity.FragmentRefreshListener() {
//            @Override
//            public void onRefresh() {
//                Log.e("allCoupon", "refresh");
//                if (_utility.isConnectingToInternet_ping()) {
//                    Log.e("get from cloud", "all coupons");
//                    CouponTab.couponPage = 1;
//                    requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPage, CouponTab.selectedCoupType);//on create
//                } else {
//                    customLoading.hideProgress();
//                    _utility.showMessage(getResources().getString(R.string.no_net));
//                    noCouponsStatus();
//                }
//            }
//        });
//        processCoupons();
//
//        return root;
//    }
//
//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        if (isVisibleToUser) {
//            Log.e("00vis00", "allCop - visible");
//        } else {
//            Log.e("00vis00", "allCop - notVisible");
//        }
//    }
//
//    void setCoupTypeBackground() {
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
//    }
//
//    private void processCoupons() {
//        setCoupTypeBackground();
//        customLoading.showProgress(_utility.getCurrentActivity());
//        loadMoreFlag = false;
//        if (CouponTab.allCoupons != null) {
//            Log.e("coupons,,,", "!=null");
//            if (CouponTab.allCoupons.size() > 0) {
//                Log.e("get locally", "all coupons= " + CouponTab.allCoupons.size());
//                sortAndShowCoupons();
//            } else {
//                Log.e("coupons,,,", "empty");
//                loadMore.setVisibility(View.GONE);
//                tv_no_cop.setVisibility(View.VISIBLE);
//            }
//            customLoading.hideProgress();
//        } else {
//            Log.e("coupons,,,", "is null");
//            if (_utility.isConnectingToInternet_ping()) {
//                Log.e("get from cloud", "all coupons");
//                CouponTab.couponPage = 1;
//                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPage, CouponTab.selectedCoupType);//on create
//            } else {
//                customLoading.hideProgress();
//                _utility.showMessage(getResources().getString(R.string.no_net));
//                noCouponsStatus();
//            }
//        }
//    }
//
//    private void sortAndShowCoupons() {
//        switch (CouponTab.sortState) {
//            case 1:
//                Collections.sort(CouponTab.allCoupons, Collections.reverseOrder());
//                break;
//            case 2:
//                Collections.sort(CouponTab.allCoupons);
//                break;
//            case 3:
//                sortMostPopular();
//                break;
//            default:
//                break;
//        }
//        tv_no_cop.setVisibility(View.GONE);
//        lvAllCoupons.setVisibility(View.VISIBLE);
//        allCopAd = new AllCouponsAdapter(getActivity(), CouponTab.allCoupons);
//        lvAllCoupons.setAdapter(allCopAd);
//        lvAllCoupons.setSelectionFromTop(CouponTab.allCoupons.size() - 4, 0);
//        lvAllCoupons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                try {
//                    MainActivity.selectedCoupon = (Coupon) CouponTab.allCoupons.get(position).clone();
//                } catch (CloneNotSupportedException e) {
//                    Log.e("coupon class clone", "exception");
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
//    /**
//     * make a request to get all coupons
//     *
//     * @param limit
//     */
//
//    public void requestAllCoupons(int limit, int offset, String type) {
//        if (_utility.isConnectingToInternet_ping()) {
//
//            Log.e("all -- request " + type + " coupons", limit + "");
//            try {
//
//                ArrayList<String> allFilters = getFilters();
//                JSONWebServices service = new JSONWebServices(AllCouponsFragment.this);
//                List<NameValuePair> nameValuePairs = new ArrayList<>();
//                JSONObject filters = new JSONObject();
//                Log.e("customerTypes filter", allFilters.get(1) + "---");
//                JSONArray clients = new JSONArray();
//                String[] splitter = allFilters.get(1).split(",");
//                if (!allFilters.get(1).isEmpty()) {
//                    for (String aSplitter : splitter) {
//                        Log.e("filters", aSplitter + "");
//                        clients.put(aSplitter);
//                    }
//                }
//                filters.put("clients", clients);
//                Log.e("favoriteBrands filter", allFilters.get(2) + "---");
//                JSONArray brands = new JSONArray();
//                if (!allFilters.get(2).isEmpty()) {
//                    splitter = allFilters.get(2).split(",");
//                    for (String aSplitter : splitter) {
//                        Log.e("filters", aSplitter + "");
//                        brands.put(aSplitter);
//                    }
//                }
//                filters.put("brands", brands);
//
//                Log.e("favoriteCateg filter", allFilters.get(3) + "---");
//                JSONArray categories = new JSONArray();
//                if (!allFilters.get(3).isEmpty()) {
//                    splitter = allFilters.get(3).split(",");
//                    for (String aSplitter : splitter) {
//                        Log.e("filters", aSplitter + "");
//                        categories.put(Integer.valueOf(aSplitter));
//                    }
//                }
//                filters.put("categories", categories);
//                filters.put("all", true);
//                filters.put("coupons", new JSONArray());
//                filters.put("country", MainActivity.currentFan != null ? MainActivity.currentFan.getFanCountryID() : 36);
//
//                JSONObject couponType = new JSONObject();
//                couponType.put("sortBy", CouponTab.sortState);
//                couponType.put("limit", limit);
//                couponType.put("page", offset);
//
//                JSONObject sections = new JSONObject();
//                sections.put(CouponTab.selectedCoupType, couponType);
//                nameValuePairs.add(new BasicNameValuePair("sections", sections.toString()));
//                nameValuePairs.add(new BasicNameValuePair("filters", filters.toString()));
//                call = service.getAllCoupons(nameValuePairs);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        } else {
//            customLoading.hideProgress();
//            _utility.showMessage(getResources().getString(R.string.no_net));
//        }
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
//        int offerTypesCheckboxStatusesSize = CouponTab.offerTypesCheckboxStatuses.size();
//        for (int i = 0; i < offerTypesCheckboxStatusesSize; i++) {
//            if (CouponTab.offerTypesCheckboxStatuses.get(i).getFilterChecked())
//                sb.append(CouponTab.offerTypesCheckboxStatuses.get(i).getFilterID()).append(",");
//        }
//        if (!sb.toString().isEmpty()) {
//            sb.deleteCharAt(sb.lastIndexOf(","));
//            filtersResult.add(sb.toString());
//        } else
//            filtersResult.add("");
//        sb.delete(0, sb.capacity());
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
//        return filtersResult;
//    }
//
//    @Override
//    public void PostBackExecutionJSON(JSONTokener Result) {
//        try {
//            Log.e("all coupons response", "-------------");
//            if (loadMoreFlag) {
//                loadMoreFlag = false;
//                Boolean loadMoreOperation = CouponTab.allCoupons.addAll(ParseData.NEW_ParseGetCoupons(Result, ""));
//                Log.e(loadMoreOperation + " more coupons ", CouponTab.allCoupons.size() + "   after request");
//                if (!loadMoreOperation)
//                    _utility.showMessage(getResources().getString(R.string.no_more));
//            } else
//                CouponTab.allCoupons = ParseData.NEW_ParseGetCoupons(Result, "");
//
//            if (CouponTab.allCoupons != null) {
//                Log.e("all coupons ", CouponTab.allCoupons.size() + "   after request");
//
//                if (CouponTab.allCoupons.size() != 0) {
//                    CouponTab.couponPage++;
//                    sortAndShowCoupons();
//                } else {
//                    noCouponsStatus();
//                }
//            } else {
//                allCopAd = new AllCouponsAdapter(getActivity(), new ArrayList<Coupon>());
//                lvAllCoupons.setAdapter(allCopAd);
//                loadMore.setVisibility(View.GONE);
//                tv_no_cop.setVisibility(View.VISIBLE);
//                _utility.showMessage(getResources().getString(R.string.ws_err));
//                customLoading.hideProgress();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            _utility.showMessage(getResources().getString(R.string.ws_err));
//        } finally {
//            customLoading.hideProgress();
//            call = null;
//
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
//
//    /**
//     * sort all coupons according to its popularity .. the brand which have more "add to fav" action will be the most popular
//     */
//    void sortMostPopular() {
//        Log.e("AllCouponsFragment", "sortMostPopular fn.");
//        ArrayList<Coupon> tmp = new ArrayList<>();
//        Integer[] sortedCount = new Integer[CouponTab.allCoupons.size()];
//        int allCouponsSize = CouponTab.allCoupons.size();
//        for (int i = 0; i < allCouponsSize; i++) {
//            sortedCount[i] = CouponTab.allCoupons.get(i).getCouponSavedByCount();
//        }
//        Arrays.sort(sortedCount, Collections.reverseOrder());
//        int sortCountedLength = sortedCount.length;
//        for (int j = 0; j < sortCountedLength; j++) {
//            for (int i = 0; i < CouponTab.allCoupons.size(); i++) {
//                if (CouponTab.allCoupons.get(i).getCouponSavedByCount() == sortedCount[j]) {
//                    tmp.add(CouponTab.allCoupons.get(i));
//                    CouponTab.allCoupons.remove(i);
//                    break;
//                }
//            }
//        }
//
//        CouponTab.allCoupons = tmp;
//        tmp = null;
//    }
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
//
//
//}
