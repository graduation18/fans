package com.systemonline.fanscoupon.brands_tabs;

import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.systemonline.fanscoupon.Base.BaseFragment;
import com.systemonline.fanscoupon.Helpers.Utility;
import com.systemonline.fanscoupon.Model.Brand;
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

public class MyBrandsFragment extends BaseFragment {

    ListView lvMyBrands;
    MyBrandsAdapter myBrandsAdapter;
    LinearLayout lin_filter, lin_sort;
    TextView tv_no_brands;
    Button loadMore;
    boolean loadMoreFlag;
    JSONAsync call;
    Utility _utility;
    private CustomLoading customLoading;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.brand_tab_my_brands, null);
        _utility = new Utility(getContext());
        customLoading = new CustomLoading(_utility.getCurrentActivity());
        tv_no_brands = (TextView) root.findViewById(R.id.tv_no_brands);
        tv_no_brands.setVisibility(View.GONE);
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.allBrands_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                customLoading.showProgress(_utility.getCurrentActivity());
                BrandsTab.myBrandPage = 0;
                requestMyBrands(BrandsTab.myBrandsLimit, BrandsTab.myBrandPage);//refresh
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        lvMyBrands = (ListView) root.findViewById(R.id.lv_my_brands);
        lin_filter = (LinearLayout) root.findViewById(R.id.lin_filter);
        lin_sort = (LinearLayout) root.findViewById(R.id.lin_sort);

        loadMore = (Button) ((LayoutInflater) _utility.getCurrentActivity().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.lv_footer, null, false);
        lvMyBrands.addFooterView(loadMore);

        loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMoreFlag = true;
                customLoading.showProgress(_utility.getCurrentActivity());
                requestMyBrands(BrandsTab.myBrandsLimit, BrandsTab.myBrandPage);//loadMore
            }
        });


        lin_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new MyBrandSortFragment();
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
                Fragment fragment = new MyBrandFilterFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });


        processBrands();

        return root;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            Log.e("00vis00", "allBrands - visible");
        } else {
            Log.e("00vis00", "allBrands - notVisible");
        }
    }


    private void processBrands() {

        customLoading.showProgress(_utility.getCurrentActivity());
        loadMoreFlag = false;
        if (BrandsTab.myBrands != null) {
            Log.e("Brands,,,", "!=null");
            if (BrandsTab.myBrands.size() > 0) {
                Log.e("get locally", "my Brands= " + BrandsTab.myBrands.size());
                sortAndShowBrands();
            } else {
                Log.e("Brands,,,", "empty");
                loadMore.setVisibility(View.GONE);
                tv_no_brands.setVisibility(View.VISIBLE);
            }

            customLoading.hideProgress();
        } else {
            Log.e("Brands,,,", "is null");
            if (_utility.isConnectingToInternet_ping()) {
                Log.e("get from cloud", "my Brands");
                BrandsTab.myBrandPage = 0;
                requestMyBrands(BrandsTab.myBrandsLimit, BrandsTab.myBrandPage);//on create
            } else {
                customLoading.hideProgress();
                _utility.showMessage(getResources().getString(R.string.no_net));
                noBrandsStatus();
            }
        }
    }

    private void sortAndShowBrands() {
        Log.e("sort and show brands", "AlBrands");
        tv_no_brands.setVisibility(View.GONE);
        lvMyBrands.setVisibility(View.VISIBLE);
        myBrandsAdapter = new MyBrandsAdapter(getActivity(), BrandsTab.myBrands);
        lvMyBrands.setAdapter(myBrandsAdapter);
        lvMyBrands.setSelectionFromTop(BrandsTab.myBrands.size() - 4, 0);
        lvMyBrands.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    BrandsTab.selectedBrand = (Brand) BrandsTab.myBrands.get(position).clone();
                } catch (CloneNotSupportedException e) {
                    Log.e("brand class clone", "exception");
                    e.printStackTrace();
                }
                Fragment fragment = new SingleBrandFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        loadMore.setVisibility(View.VISIBLE);
    }

    /**
     * make a request to get all Brands
     *
     * @param limit
     */
    public void requestMyBrands(int limit, int offset) {
        if (_utility.isConnectingToInternet_ping()) {

            Log.e("my -- request Brands", limit + "");
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("param1", "1"));
            nameValuePairs.add(new BasicNameValuePair("index", String.valueOf(offset)));
            nameValuePairs.add(new BasicNameValuePair("limit", String.valueOf(limit)));
            switch (BrandsTab.myBrandSortState) {
                case 1:
                    nameValuePairs.add(new BasicNameValuePair("sortBy", "fansCount"));
                    nameValuePairs.add(new BasicNameValuePair("reverse", "true"));
                    break;
                case 2:
                    nameValuePairs.add(new BasicNameValuePair("sortBy", "evaluations"));
                    nameValuePairs.add(new BasicNameValuePair("reverse", "true"));
                    break;
                case 3:
                    nameValuePairs.add(new BasicNameValuePair("sortBy", "couponcount"));
                    nameValuePairs.add(new BasicNameValuePair("reverse", "true"));
                    break;
            }

            if (!BrandsTab.myBrandFilters.isEmpty()) {
                for (int i = 0; i < BrandsTab.myBrandFilters.size(); i++) {
                    nameValuePairs.add(new BasicNameValuePair("filters[" + i + "][key]", BrandsTab.myBrandFilters.get(i).getKey()));
                    nameValuePairs.add(new BasicNameValuePair("filters[" + i + "][value]", BrandsTab.myBrandFilters.get(i).getValue()));
                }
            }

            JSONWebServices service = new JSONWebServices(MyBrandsFragment.this);
            call = service.getAllBrands(nameValuePairs);

        } else {
            customLoading.hideProgress();
            _utility.showMessage(getResources().getString(R.string.no_net));
        }
    }

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

    @Override
    public void PostBackExecutionJSON(JSONTokener Result) {
        try {
            Log.e("all Brands response", "-------------");
            if (loadMoreFlag) {
                loadMoreFlag = false;
                Boolean loadMoreOperation = BrandsTab.myBrands.addAll(ParseData.parseGetBrands(Result));
                Log.e(loadMoreOperation + " more Brands ", BrandsTab.myBrands.size() + "   after request");
                if (!loadMoreOperation)
                    _utility.showMessage(getResources().getString(R.string.no_more));
            } else
                BrandsTab.myBrands = ParseData.parseGetBrands(Result);

            if (BrandsTab.myBrands != null) {
                Log.e("all Brands ", BrandsTab.myBrands.size() + "   after request");

                if (BrandsTab.myBrands.size() != 0) {
                    BrandsTab.myBrandPage = BrandsTab.myBrands.size();
                    sortAndShowBrands();
                } else {
                    noBrandsStatus();
                }
            } else {
                myBrandsAdapter = new MyBrandsAdapter(getActivity(), new ArrayList<Brand>());
                lvMyBrands.setAdapter(myBrandsAdapter);
                loadMore.setVisibility(View.GONE);
                tv_no_brands.setVisibility(View.VISIBLE);
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

    private void noBrandsStatus() {
        loadMore.setVisibility(View.GONE);
        tv_no_brands.setVisibility(View.VISIBLE);
        lvMyBrands.setVisibility(View.GONE);
    }


    /**
     * sort all Brands according to its popularity .. the brand which have more "add to fav" action will be the most popular
     */


//    void sortMostPopular() {
//        Log.e("AllBrandsFragment", "sortMostPopular fn.");
//        ArrayList<Brand> tmp = new ArrayList<>();
//        Integer[] sortedCount = new Integer[MainActivity.myBrands.size()];
//        int allBrandsSize = MainActivity.myBrands.size();
//        for (int i = 0; i < allBrandsSize; i++) {
//            sortedCount[i] = MainActivity.myBrands.get(i).getBrandsavedByCount();
//        }
//        Arrays.sort(sortedCount, Collections.reverseOrder());
//        int sortCountedLength = sortedCount.length;
//        for (int j = 0; j < sortCountedLength; j++) {
//            for (int i = 0; i < MainActivity.myBrands.size(); i++) {
//                if (MainActivity.myBrands.get(i).getBrandsavedByCount() == sortedCount[j]) {
//                    tmp.add(MainActivity.myBrands.get(i));
//                    MainActivity.myBrands.remove(i);
//                    break;
//                }
//            }
//        }
//
//        MainActivity.myBrands = tmp;
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

}
