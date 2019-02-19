package com.systemonline.fanscoupon.brands_tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.systemonline.fanscoupon.Base.BaseFragment;
import com.systemonline.fanscoupon.Helpers.Utility;
import com.systemonline.fanscoupon.Model.BrandFilter;
import com.systemonline.fanscoupon.Model.CustomLoading;
import com.systemonline.fanscoupon.R;
import com.systemonline.fanscoupon.WebServices.JSONAsync;
import com.systemonline.fanscoupon.WebServices.JSONWebServices;
import com.systemonline.fanscoupon.WebServices.ParseData;

import org.json.JSONTokener;

import java.util.ArrayList;

public class MyBrandFilterFragment extends BaseFragment {

    Spinner myCategoriesSpinner, myCitiesSpinner;
    TextView saveFilter, cancelFilter;
    Utility _utility;
    JSONAsync call;
    private CustomLoading customLoading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.my_brand_filter_fragment, null);

        _utility = new Utility(getContext());
        customLoading = new CustomLoading(_utility.getCurrentActivity());

        myCategoriesSpinner = (Spinner) rootView.findViewById(R.id.myCategories_spinner);
        myCitiesSpinner = (Spinner) rootView.findViewById(R.id.myCities_spinner);
        saveFilter = (TextView) rootView.findViewById(R.id.my_btn_save_filter);
        saveFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BrandsTab.myBrandFilters = new ArrayList<>();
                BrandFilter brandFilter;
                if (myCategoriesSpinner.getSelectedItemPosition() != 0) {
                    brandFilter = new BrandFilter("category_id", String.valueOf(BrandsTab.myBrandAllFilters.get(0).get(myCategoriesSpinner.getSelectedItemPosition()).getFilterID() - 1));
                    BrandsTab.myBrandFilters.add(brandFilter);
                }
                if (myCitiesSpinner.getSelectedItemPosition() != 0) {
                    brandFilter = new BrandFilter("city_id", String.valueOf(BrandsTab.myBrandAllFilters.get(1).get(myCitiesSpinner.getSelectedItemPosition()).getFilterID() - 1));
                    BrandsTab.myBrandFilters.add(brandFilter);
                }
                BrandsTab.myBrands = null;
                getActivity().onBackPressed();
            }
        });
        cancelFilter = (TextView) rootView.findViewById(R.id.my_btn_cancel_filter);
        cancelFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        if (!_utility.isConnectingToInternet_ping()) {
            saveFilter.setEnabled(false);
        }


        if (BrandsTab.myBrandAllFilters.isEmpty()) {
            if (_utility.isConnectingToInternet_ping()) {
                customLoading.showProgress(_utility.getCurrentActivity());
                JSONWebServices service = new JSONWebServices(MyBrandFilterFragment.this);
                call = service.getBrandFilters();
            } else
                Toast.makeText(getContext(), getResources().getString(R.string.no_net), Toast.LENGTH_LONG).show();
        } else {
            loadSavedFilter();
        }
        return rootView;
    }

    private void loadSavedFilter() {
        String[] categoriesNames = new String[BrandsTab.myBrandAllFilters.get(0).size() + 1];
        categoriesNames[0] = getString(R.string.All);
        for (int i = 0; i < BrandsTab.myBrandAllFilters.get(0).size(); i++) {
            categoriesNames[i + 1] = BrandsTab.myBrandAllFilters.get(0).get(i).getFilterName();
        }
        _utility.spinnerProperties(categoriesNames, myCategoriesSpinner);

        String[] citiesNames = new String[BrandsTab.myBrandAllFilters.get(1).size() + 1];
        citiesNames[0] = getString(R.string.All);
        for (int i = 0; i < BrandsTab.myBrandAllFilters.get(1).size(); i++) {
            citiesNames[i + 1] = BrandsTab.myBrandAllFilters.get(1).get(i).getFilterName();
        }
        _utility.spinnerProperties(citiesNames, myCitiesSpinner);

//        setLastFilterChoice();
    }

    void setLastFilterChoice() {
        if (!BrandsTab.myBrandFilters.isEmpty()) {
            myCategoriesSpinner.setSelection(Integer.parseInt(BrandsTab.myBrandFilters.get(0).getValue()));
            if (BrandsTab.myBrandFilters.size() > 1)
                myCitiesSpinner.setSelection(Integer.parseInt(BrandsTab.myBrandFilters.get(1).getValue()));

        }
    }

    @Override
    public void PostBackExecutionJSON(JSONTokener Result) {
        try {
            BrandsTab.myBrandAllFilters = ParseData.parseBrandFilters(Result);
            if (BrandsTab.myBrandAllFilters != null) {
                loadSavedFilter();
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