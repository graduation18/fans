package com.systemonline.fanscoupon.coupon_tabs;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.systemonline.fanscoupon.Base.BaseFragment;
import com.systemonline.fanscoupon.Helpers.Utility;
import com.systemonline.fanscoupon.Model.CustomLoading;
import com.systemonline.fanscoupon.Model.FilterCheckboxStatus;
import com.systemonline.fanscoupon.R;
import com.systemonline.fanscoupon.WebServices.JSONAsync;
import com.systemonline.fanscoupon.WebServices.JSONWebServices;
import com.systemonline.fanscoupon.WebServices.ParseData;

import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CouponsFilterFragment extends BaseFragment {

    FilterAdapter expAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader, specs;
    HashMap<String, List<String>> listDataChild;
    ArrayList<ArrayList<com.systemonline.fanscoupon.Model.Filter>> allFilters = new ArrayList<>();
    TextView saveFilter, cancelFilter;
    Utility _utility;
    JSONAsync call;
    private CustomLoading customLoading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.filter_fragment, null);

        _utility = new Utility(getContext());
        customLoading = new CustomLoading(_utility.getCurrentActivity());

        expListView = (ExpandableListView) rootView.findViewById(R.id.lvExp);
        saveFilter = (TextView) rootView.findViewById(R.id.btn_save_filter);
        saveFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CouponTab.verySpecialsCheckboxStatuses = new ArrayList<FilterCheckboxStatus>();
                CouponTab.favouriteBrandCheckboxStatuses = new ArrayList<FilterCheckboxStatus>();
                CouponTab.interestedINCheckboxStatuses = new ArrayList<FilterCheckboxStatus>();
                CouponTab.couponCategoriesCheckboxStatuses = new ArrayList<FilterCheckboxStatus>();


                for (int i = 0; i < FilterAdapter.ad_verySpecialsCheckboxStatuses.size(); i++) {
                    CouponTab.verySpecialsCheckboxStatuses.add(new FilterCheckboxStatus(FilterAdapter.ad_verySpecialsCheckboxStatuses.get(i).getFilterID(), FilterAdapter.ad_verySpecialsCheckboxStatuses.get(i).getFilterChecked(), FilterAdapter.ad_verySpecialsCheckboxStatuses.get(i).getFilterName()));
                }
                for (int i = 0; i < FilterAdapter.ad_favouriteBrandCheckboxStatuses.size(); i++) {
                    CouponTab.favouriteBrandCheckboxStatuses.add(new FilterCheckboxStatus(FilterAdapter.ad_favouriteBrandCheckboxStatuses.get(i).getFilterID(), FilterAdapter.ad_favouriteBrandCheckboxStatuses.get(i).getFilterChecked(), FilterAdapter.ad_favouriteBrandCheckboxStatuses.get(i).getFilterName()));
                }
                for (int i = 0; i < FilterAdapter.ad_interestedINCheckboxStatuses.size(); i++) {
                    CouponTab.interestedINCheckboxStatuses.add(new FilterCheckboxStatus(FilterAdapter.ad_interestedINCheckboxStatuses.get(i).getFilterID(), FilterAdapter.ad_interestedINCheckboxStatuses.get(i).getFilterChecked(), FilterAdapter.ad_interestedINCheckboxStatuses.get(i).getFilterName()));
                }

                for (int i = 0; i < FilterAdapter.ad_couponCategoriesCheckboxStatuses.size(); i++) {
                    CouponTab.couponCategoriesCheckboxStatuses.add(new FilterCheckboxStatus(FilterAdapter.ad_couponCategoriesCheckboxStatuses.get(i).getFilterID(), FilterAdapter.ad_couponCategoriesCheckboxStatuses.get(i).getFilterChecked(), FilterAdapter.ad_couponCategoriesCheckboxStatuses.get(i).getFilterName()));
                }

                FilterAdapter.ad_verySpecialsCheckboxStatuses = null;
                FilterAdapter.ad_favouriteBrandCheckboxStatuses = null;
                FilterAdapter.ad_interestedINCheckboxStatuses = null;
                FilterAdapter.ad_couponCategoriesCheckboxStatuses = null;
//                CouponTab.allCoupons = null;
                CouponTab.allCoupons2 = null;
                CouponTab.forYou2 = null;
                CouponTab.couponPage = 1;
                CouponTab.couponPageOnlyYou = 1;

                getActivity().onBackPressed();
            }
        });
        cancelFilter = (TextView) rootView.findViewById(R.id.btn_cancel_filter);
        cancelFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        if (!_utility.isConnectingToInternet_ping()) {
            saveFilter.setEnabled(false);
        }

        DisplayMetrics metrics;
        int width;
        metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;

        expListView.setIndicatorBounds(width - GetDipsFromPixel(50), width - GetDipsFromPixel(40));

        if (CouponTab.interestedINCheckboxStatuses.isEmpty()
//                || CouponTab.selectedCopTypeID != CouponTab.lastSelectedCouponType
                ) {
            if (_utility.isConnectingToInternet_ping()) {
                customLoading.showProgress(_utility.getCurrentActivity());
                CouponTab.verySpecialsCheckboxStatuses = new ArrayList<FilterCheckboxStatus>();
                CouponTab.favouriteBrandCheckboxStatuses = new ArrayList<FilterCheckboxStatus>();
                CouponTab.interestedINCheckboxStatuses = new ArrayList<FilterCheckboxStatus>();
                CouponTab.couponCategoriesCheckboxStatuses = new ArrayList<FilterCheckboxStatus>();
                JSONWebServices service = new JSONWebServices(CouponsFilterFragment.this);
                call = service.getAllFilters();
            } else
                Toast.makeText(getContext(), getResources().getString(R.string.no_net), Toast.LENGTH_LONG).show();

        } else {
            loadSavedFilter();
        }
        return rootView;
    }

    /**
     * load saved filter instead of get it from cloud
     */
    private void loadSavedFilter() {
        Log.e("filters", "load saved filters");
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        listDataHeader.add(getResources().getString(R.string.very_special));
        listDataHeader.add(getResources().getString(R.string.ur_fav_brand));
        listDataHeader.add(getResources().getString(R.string.interested_in));
        listDataHeader.add(getResources().getString(R.string.coupons_categ));


        specs = new ArrayList<>();
        for (int i = 0; i < CouponTab.verySpecialsCheckboxStatuses.size(); i++) {
            specs.add(CouponTab.verySpecialsCheckboxStatuses.get(i).getFilterName());
        }
        listDataChild.put(getResources().getString(R.string.very_special), specs);
        specs = new ArrayList<>();
        for (int i = 0; i < CouponTab.favouriteBrandCheckboxStatuses.size(); i++) {
            specs.add(CouponTab.favouriteBrandCheckboxStatuses.get(i).getFilterName());
        }
        listDataChild.put(getResources().getString(R.string.ur_fav_brand), specs);
        specs = new ArrayList<>();
        for (int i = 0; i < CouponTab.interestedINCheckboxStatuses.size(); i++) {
            specs.add(CouponTab.interestedINCheckboxStatuses.get(i).getFilterName());
        }
        listDataChild.put(getResources().getString(R.string.interested_in), specs);

        specs = new ArrayList<>();
        for (int i = 0; i < CouponTab.couponCategoriesCheckboxStatuses.size(); i++) {
            specs.add(CouponTab.couponCategoriesCheckboxStatuses.get(i).getFilterName());
        }
        listDataChild.put(getResources().getString(R.string.coupons_categ), specs);

        expAdapter = new FilterAdapter(getActivity(), listDataHeader, listDataChild);
        expListView.setAdapter(expAdapter);
    }

    /**
     * get screen width
     *
     * @param pixels
     * @return
     */
    public int GetDipsFromPixel(float pixels) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (pixels * scale + 0.5f);
    }

    @Override
    public void PostBackExecutionJSON(JSONTokener Result) {
        try {
            allFilters = ParseData.NEW_parseFilters(Result);

            if (allFilters != null) {
                listDataHeader = new ArrayList<>();
                listDataChild = new HashMap<>();

                listDataHeader.add(getResources().getString(R.string.very_special));
                listDataHeader.add(getResources().getString(R.string.ur_fav_brand));
                listDataHeader.add(getResources().getString(R.string.interested_in));
                listDataHeader.add(getResources().getString(R.string.coupons_categ));

                for (int i = 0; i < allFilters.size(); i++) {
                    specs = new ArrayList<>();
                    for (int j = 0; j < allFilters.get(i).size(); j++) {
                        if (i == 0) {
//                            if (allFilters.get(i).get(j).getFilterID() == CouponTab.selectedCopTypeID) {
//                                try {
//                                    if (allFilters.get(i).get(j).getFilterChildren().size() > 0)
//                                        for (int k = 0; k < allFilters.get(i).get(j).getFilterChildren().size(); k++) {
//                                            specs.add(allFilters.get(i).get(j).getFilterChildren().get(k).getFilterName());
//                                            CouponTab.offerTypesCheckboxStatuses.add(new FilterCheckboxStatus(allFilters.get(i).get(j).getFilterChildren().get(k).getFilterID(), false, allFilters.get(i).get(j).getFilterChildren().get(k).getFilterName()));
//                                        }
//                                } catch (Exception e) {
//                                }
//                                break;
//                            }
                        } else {
                            specs.add(allFilters.get(i).get(j).getFilterName());
                            switch (i) {
                                case 1:
                                    CouponTab.verySpecialsCheckboxStatuses.add(new FilterCheckboxStatus(allFilters.get(i).get(j).getFilterID(), false, allFilters.get(i).get(j).getFilterName()));
                                    break;
                                case 2:
                                    CouponTab.favouriteBrandCheckboxStatuses.add(new FilterCheckboxStatus(allFilters.get(i).get(j).getFilterID(), false, allFilters.get(i).get(j).getFilterName()));
                                    break;
                                case 3:
                                    CouponTab.interestedINCheckboxStatuses.add(new FilterCheckboxStatus(allFilters.get(i).get(j).getFilterID(), false, allFilters.get(i).get(j).getFilterName()));
                                    break;
                                case 4:
                                    CouponTab.couponCategoriesCheckboxStatuses.add(new FilterCheckboxStatus(allFilters.get(i).get(j).getFilterID(), false, allFilters.get(i).get(j).getFilterName()));
                                    break;
                            }

                        }
                    }
                    switch (i) {
                        case 0:
//                            listDataChild.put(getResources().getString(R.string.offers_type), specs);
                            break;
                        case 1:
                            listDataChild.put(getResources().getString(R.string.very_special), specs);
                            break;
                        case 2:
                            listDataChild.put(getResources().getString(R.string.ur_fav_brand), specs);
                            break;
                        case 3:
                            listDataChild.put(getResources().getString(R.string.interested_in), specs);
                            break;
                        case 4:
                            listDataChild.put(getResources().getString(R.string.coupons_categ), specs);
                            break;
                    }

                }
                expAdapter = new FilterAdapter(getActivity(), listDataHeader, listDataChild);
                expListView.setAdapter(expAdapter);
//                CouponTab.lastSelectedCouponType = CouponTab.selectedCopTypeID;
                customLoading.hideProgress();
            } else {
                Toast.makeText(_utility.GetAppContext(), getResources().getString(R.string.ws_err), Toast.LENGTH_LONG).show();
                customLoading.hideProgress();
                saveFilter.setEnabled(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(_utility.GetAppContext(), getResources().getString(R.string.ws_err), Toast.LENGTH_LONG).show();
            saveFilter.setEnabled(false);
        } finally {
            customLoading.hideProgress();
            call = null;
        }
    }

}