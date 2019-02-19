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
//public class OnlyForYouCouponsFragment extends BaseFragment {
//
//    ListView lvAllCoupons;
//    ForYouAdapter forYouCopAd;
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
//                CouponTab.couponPageOnlyYou = 1;
//                customLoading.showProgress(_utility.getCurrentActivity());
//                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPageOnlyYou, "forYou");//refresh
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
//                for (int i = 0; i < CouponTab.forYou.size(); i++) {
//                    for (int j = 0; j < CouponTab.forYou.get(i).getCouponBrand().getBrandBranches().size(); j++) {
//                        CouponTab.forYou.get(i).getCouponBrand().getBrandBranches().get(j).
//                                setOffName(CouponTab.forYou.get(i).getCouponName());
//                        CouponTab.couponsBranches.add(CouponTab.forYou.get(i).getCouponBrand().getBrandBranches().get(j));
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
//                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPageOnlyYou, "forYou");//loadMore
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
//                CouponTab.couponPageOnlyYou = 1;
//                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPageOnlyYou, CouponTab.selectedCoupTypeOnlyYou);//loyalty
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
//                CouponTab.couponPageOnlyYou = 1;
//
//                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPageOnlyYou, CouponTab.selectedCoupTypeOnlyYou);//special offer
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
//                CouponTab.couponPageOnlyYou = 1;
//
//                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPageOnlyYou, CouponTab.selectedCoupTypeOnlyYou);//challenge
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
//                CouponTab.couponPageOnlyYou = 1;
//
//                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPageOnlyYou, CouponTab.selectedCoupTypeOnlyYou);//lucky hour
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
//                CouponTab.couponPageOnlyYou = 1;
//
//                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPageOnlyYou, CouponTab.selectedCoupTypeOnlyYou);//happy Occ
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
//                CouponTab.couponPageOnlyYou = 1;
//
//                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPageOnlyYou, CouponTab.selectedCoupTypeOnlyYou);//happy Occ
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
//                    CouponTab.couponPageOnlyYou = 1;
//                    requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPageOnlyYou, "forYou");//on create
//                } else {
//
//                    customLoading.hideProgress();
//                    _utility.showMessage(getResources().getString(R.string.no_net));
//                    noCouponsStatus();
//                }
//            }
//        });
//        loadMoreFlag = false;
//        if (CouponTab.forYou != null) {
//            Log.e("coupons only for,,,", "!=null");
//            if (CouponTab.forYou.size() > 0) {
//                Log.e("get locally", "for  You= " + CouponTab.forYou.size());
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
//                CouponTab.couponPageOnlyYou = 1;
//                requestAllCoupons(CouponTab.couponsLimit, CouponTab.couponPageOnlyYou, "forYou");//on create
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
//                Collections.sort(CouponTab.forYou, Collections.reverseOrder());
//                break;
//            case 2:
//                Collections.sort(CouponTab.forYou);
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
//        forYouCopAd = new ForYouAdapter(getActivity(), CouponTab.forYou);
//        lvAllCoupons.setAdapter(forYouCopAd);
//        lvAllCoupons.setSelectionFromTop(CouponTab.forYou.size() - 4, 0);
//
//        lvAllCoupons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                try {
//                    MainActivity.selectedCoupon = (Coupon) CouponTab.forYou.get(position).clone();
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
////        if (_utility.isConnectingToInternet_ping()) {
////
////            Log.e("all -- request " + type + " coupons", limit + "");
////            try {
////                ArrayList<String> allFilters = getFilters();
////                JSONWebServices service = new JSONWebServices(OnlyForYouCouponsFragment.this);
////                List<NameValuePair> nameValuePairs = new ArrayList<>();
////                JSONObject filters = new JSONObject();
////                Log.e("customerTypes filter", allFilters.get(1) + "---");
////                JSONArray clients = new JSONArray();
////                String[] splitter = allFilters.get(1).split(",");
////                if (!allFilters.get(1).isEmpty()) {
////                    for (int i = 0; i < splitter.length; i++) {
////                        Log.e("filters", splitter[i] + "");
////                        clients.put(splitter[i]);
////                    }
////                }
////                filters.put("clients", clients);
////                Log.e("favoriteBrands filter", allFilters.get(2) + "---");
////                JSONArray brands = new JSONArray();
////                if (!allFilters.get(2).isEmpty()) {
////                    splitter = allFilters.get(2).split(",");
////                    for (int i = 0; i < splitter.length; i++) {
////                        Log.e("filters", splitter[i] + "");
////                        brands.put(splitter[i]);
////                    }
////                }
////                filters.put("brands", brands);
////                Log.e("favoriteCateg filter", allFilters.get(3) + "---");
////                JSONArray categories = new JSONArray();
////                if (!allFilters.get(3).isEmpty()) {
////                    splitter = allFilters.get(3).split(",");
////                    for (int i = 0; i < splitter.length; i++) {
////                        Log.e("filters", splitter[i] + "");
////                        categories.put(Integer.valueOf(splitter[i]));
////                    }
////                }
////                filters.put("categories", categories);
////                filters.put("all", false);
////                filters.put("coupons", new JSONArray());
////                filters.put("country", MainActivity.currentFan != null ? MainActivity.currentFan.getFanCountryID() : 36);
////
////                JSONObject couponType = new JSONObject();
////                couponType.put("sortBy", CouponTab.sortState);
////                couponType.put("limit", limit);
////                couponType.put("page", offset);
////
////                JSONObject sections = new JSONObject();
////                sections.put(CouponTab.selectedCoupTypeOnlyYou, couponType);
////
////                nameValuePairs.add(new BasicNameValuePair("sections", sections.toString()));
////                nameValuePairs.add(new BasicNameValuePair("filters", filters.toString()));
////
////                call = service.getAllCoupons(nameValuePairs);
////
////
////            } catch (JSONException e) {
////                e.printStackTrace();
////            }
////        } else {
////            customLoading.hideProgress();
////            _utility.showMessage(getResources().getString(R.string.no_net));
////        }
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
//                Boolean loadMoreOperation = CouponTab.forYou.addAll(ParseData.NEW_ParseGetCoupons(Result, "forYou"));
//                Log.e(loadMoreOperation + " more coupons ", CouponTab.forYou.size() + "   after request");
//                if (!loadMoreOperation)
//                    _utility.showMessage(getResources().getString(R.string.no_more));
//            } else
//                CouponTab.forYou = ParseData.NEW_ParseGetCoupons(Result, "forYou");
//
//            if (CouponTab.forYou != null) {
//                Log.e("for  You ", CouponTab.forYou.size() + "   after request");
//
//                if (CouponTab.forYou.size() != 0) {
//                    CouponTab.couponPageOnlyYou++;
//
//                    sortAndShowCoupons();
//                } else {
//                    noCouponsStatus();
//                }
//            } else {
//                forYouCopAd = new ForYouAdapter(getActivity(), new ArrayList<Coupon>());
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
//
//    /**
//     * sort all coupons according to its popularity .. the brand which have more "add to fav" action will be the most popular
//     */
//    void sortMostPopular() {
//        Log.e("forYouCouponsFragment", "sortMostPopular fn.");
//        ArrayList<Coupon> tmp = new ArrayList<>();
//        Integer[] sortedCount = new Integer[CouponTab.forYou.size()];
//        int allCouponsSize = CouponTab.forYou.size();
//        for (int i = 0; i < allCouponsSize; i++) {
//            sortedCount[i] = CouponTab.forYou.get(i).getCouponSavedByCount();
//        }
//        Arrays.sort(sortedCount, Collections.reverseOrder());
//        int sortCountedLength = sortedCount.length;
//        for (int j = 0; j < sortCountedLength; j++) {
//            for (int i = 0; i < CouponTab.forYou.size(); i++) {
//                if (CouponTab.forYou.get(i).getCouponSavedByCount() == sortedCount[j]) {
//                    tmp.add(CouponTab.forYou.get(i));
//                    CouponTab.forYou.remove(i);
//                    break;
//                }
//            }
//        }
//
//        CouponTab.forYou = tmp;
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
//}
