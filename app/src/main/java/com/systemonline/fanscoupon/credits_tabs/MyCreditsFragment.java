package com.systemonline.fanscoupon.credits_tabs;

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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.systemonline.fanscoupon.Base.BaseFragment;
import com.systemonline.fanscoupon.Helpers.Utility;
import com.systemonline.fanscoupon.Model.Credit;
import com.systemonline.fanscoupon.Model.CustomLoading;
import com.systemonline.fanscoupon.R;
import com.systemonline.fanscoupon.WebServices.JSONAsync;
import com.systemonline.fanscoupon.WebServices.JSONWebServices;
import com.systemonline.fanscoupon.WebServices.ParseData;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONTokener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MyCreditsFragment extends BaseFragment {

    public static Credit credit;
    static int brandSortState = 0, brandsLimit = 4;
    ListView lvAllBrands;
    CreditBrandsAdapter allBrandsAdapter;
    //    static ArrayList<CreditPerBrand> allBrands = new ArrayList<>();
    int brandPage = 0;
    LinearLayout lin_sort;
    TextView tv_no_brands, tv_no_customerTypes, tv_no_badges, totalPoints, usedPoints, availableCoupons;
    Button loadMore;
    boolean loadMoreFlag;
    JSONAsync call;
    Utility _utility;
    private int lastBrandsSize;
    private CustomLoading customLoading;
    private LinearLayout lin_customerTypes, lin_badges;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.cred_tab_my_credits, null);
        _utility = new Utility(getContext());
        customLoading = new CustomLoading(_utility.getCurrentActivity());
        tv_no_brands = (TextView) root.findViewById(R.id.tv_no_brands);
        tv_no_brands.setVisibility(View.GONE);

        tv_no_customerTypes = (TextView) root.findViewById(R.id.tv_no_customerTypes);
        tv_no_customerTypes.setVisibility(View.GONE);

        tv_no_badges = (TextView) root.findViewById(R.id.tv_no_badges);
        tv_no_badges.setVisibility(View.GONE);

        totalPoints = (TextView) root.findViewById(R.id.tv_total_credit_total_points);
        usedPoints = (TextView) root.findViewById(R.id.tv_total_credit_used_coupons);
        availableCoupons = (TextView) root.findViewById(R.id.tv_total_credit_available_coupons);


        lin_customerTypes = (LinearLayout) root.findViewById(R.id.cred_customerLinear);
        lin_badges = (LinearLayout) root.findViewById(R.id.badge_badgesLinear);
        lvAllBrands = (ListView) root.findViewById(R.id.lv_credit_brands);
        lin_sort = (LinearLayout) root.findViewById(R.id.lin_sort);

        getActivity().setTitle(getString(R.string.credit));


        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.allCredit_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);

//
                customLoading.showProgress(_utility.getCurrentActivity());
                brandsLimit = 4;
                requestAllCredit(brandsLimit, brandPage);//refresh
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        loadMore = (Button) root.findViewById(R.id.loadMore);
        loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadMoreFlag = true;
//
                customLoading.showProgress(_utility.getCurrentActivity());
                requestAllCredit(brandsLimit, brandPage);//loadMore
            }
        });


        lin_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new CreditBrandSortFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });


        getScreenData();

        return root;
    }


    private void getScreenData() {
        if (credit != null) {
            setScreenData();
//
            customLoading.hideProgress();
        } else {
//
            customLoading.showProgress(_utility.getCurrentActivity());
            loadMoreFlag = false;
            if (_utility.isConnectingToInternet_ping()) {
                Log.e("get from cloud", "credit");
                requestAllCredit(brandsLimit, brandPage);//on create
            } else {
//
                customLoading.hideProgress();
                _utility.showMessage(getResources().getString(R.string.no_net));
                noBrandsStatus();
            }
        }
    }

    private void setScreenData() {
        if (credit.getTotalCustomerTypes().isEmpty())
            tv_no_customerTypes.setVisibility(View.VISIBLE);
        else {
            _utility.createDynamicImageViews(credit.getTotalCustomerTypes(), lin_customerTypes);
        }

        if (credit.getTotalBadges().isEmpty())
            tv_no_badges.setVisibility(View.VISIBLE);
        else {
            _utility.createDynamicImageViews(credit.getTotalBadges(), lin_badges);
        }

        totalPoints.setText(_utility.formatNumber(Long.valueOf(credit.getTotalPoints())));
        usedPoints.setText(_utility.formatNumber(credit.getUsedCoupons()));
        availableCoupons.setText(_utility.formatNumber(credit.getAvailableCoupons()));

        sortAndShowBrands();

    }

    private void sortAndShowBrands() {
        Log.e("sort and show brands", "credit");
        if (credit.getBrands().isEmpty())
            noBrandsStatus();
        else {
            brandsLimit += 4;
            lastBrandsSize = credit.getBrands().size();
            tv_no_brands.setVisibility(View.GONE);
            lvAllBrands.setVisibility(View.VISIBLE);
            allBrandsAdapter = new CreditBrandsAdapter(getActivity(), credit.getBrands());
            lvAllBrands.setAdapter(allBrandsAdapter);
            _utility.setListViewHeightBasedOnChildren(lvAllBrands);
            lvAllBrands.setSelectionFromTop(credit.getBrands().size() - 4, 0);
            loadMore.setVisibility(View.VISIBLE);
        }
    }

    /**
     * make a request to get all Brands
     *
     * @param limit
     */
    public void requestAllCredit(int limit, int offset) {
        if (_utility.isConnectingToInternet_ping()) {

            Log.e("all -- request credit", limit + "");
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("index", String.valueOf(offset)));
            nameValuePairs.add(new BasicNameValuePair("limit", String.valueOf(limit)));

            switch (brandSortState) {
                case 1:
                    nameValuePairs.add(new BasicNameValuePair("sortBy", "total_points"));
                    nameValuePairs.add(new BasicNameValuePair("reverse", "true"));
                    break;
                case 2:
                    nameValuePairs.add(new BasicNameValuePair("sortBy", "created_at"));
                    nameValuePairs.add(new BasicNameValuePair("reverse", "true"));
                    break;
                case 3:
                    nameValuePairs.add(new BasicNameValuePair("sortBy", "brand_name"));
                    nameValuePairs.add(new BasicNameValuePair("reverse", "false"));
                    break;
            }

            JSONWebServices service = new JSONWebServices(MyCreditsFragment.this);
            call = service.getAllCredit(nameValuePairs);

        } else {
//
            customLoading.hideProgress();
            _utility.showMessage(getResources().getString(R.string.no_net));
        }
    }


    @Override
    public void PostBackExecutionJSON(JSONTokener Result) {
        try {
            Log.e("all credit response", "-------------");
            credit = ParseData.parseAllCredit(Result);
            if (credit != null) {
                if (loadMoreFlag) {
                    loadMoreFlag = false;
                    if (credit.getBrands().size() == lastBrandsSize)
                        _utility.showMessage(getResources().getString(R.string.no_more));
                    else
                        setScreenData();
                } else
                    setScreenData();
            } else {
                _utility.showMessage(getResources().getString(R.string.ws_err));
            }
        } catch (Exception e) {
            e.printStackTrace();
            _utility.showMessage(getResources().getString(R.string.ws_err));
        } finally {
//
            customLoading.hideProgress();
            call = null;

        }

    }

    private void noBrandsStatus() {
        loadMore.setVisibility(View.GONE);
        tv_no_brands.setVisibility(View.VISIBLE);
        lvAllBrands.setVisibility(View.GONE);
    }


    /**
     * sort all Brands according to its popularity .. the brand which have more "add to fav" action will be the most popular
     */


//    void sortMostPopular() {
//        Log.e("AllBrandsFragment", "sortMostPopular fn.");
//        ArrayList<Brand> tmp = new ArrayList<>();
//        Integer[] sortedCount = new Integer[allBrands.size()];
//        int allBrandsSize = allBrands.size();
//        for (int i = 0; i < allBrandsSize; i++) {
//            sortedCount[i] = allBrands.get(i).getBrandsavedByCount();
//        }
//        Arrays.sort(sortedCount, Collections.reverseOrder());
//        int sortCountedLength = sortedCount.length;
//        for (int j = 0; j < sortCountedLength; j++) {
//            for (int i = 0; i < allBrands.size(); i++) {
//                if (allBrands.get(i).getBrandsavedByCount() == sortedCount[j]) {
//                    tmp.add(allBrands.get(i));
//                    allBrands.remove(i);
//                    break;
//                }
//            }
//        }
//
//        allBrands = tmp;
//        tmp = null;
//    }


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


//}

}
